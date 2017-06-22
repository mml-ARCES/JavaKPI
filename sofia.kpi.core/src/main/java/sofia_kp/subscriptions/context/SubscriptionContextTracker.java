package sofia_kp.subscriptions.context;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import sofia_kp.SIBResponse;
import sofia_kp.SSAP_sparql_response;
import sofia_kp.iKPIC_subscribeHandler2;

public class SubscriptionContextTracker implements iKPIC_subscribeHandler2 {

	private iKPIC_subscribeHandler2 userHandler;
	private SubscriptionContext subscriptionContext;
	
	public SubscriptionContextTracker(iKPIC_subscribeHandler2 handler,SIBResponse startContext) {
		this.userHandler = handler;
		if("sparql".equals(startContext.queryType)){
			subscriptionContext = createSparqlCtx(startContext);
		}else{
			subscriptionContext = createRDFSubCtx(startContext);
		}
		
	}

	@Override
	public void kpic_RDFEventHandler(Vector<Vector<String>> newTriples, Vector<Vector<String>> oldTriples,
			String indSequence, String subID) {
		userHandler.kpic_RDFEventHandler(newTriples, oldTriples, indSequence, subID);
		
		for(Vector<String> v : newTriples){
			ContextEntry rdfEntry = buildRDFentry(v);
			subscriptionContext.add(rdfEntry);
		}
		
		for(Vector<String> v : oldTriples){
			ContextEntry rdfEntry = buildRDFentry(v);
			subscriptionContext.remove(rdfEntry);
		}
	}

	private ContextEntry buildRDFentry(Vector<String> v) {
		String [] triple = new String[4];
		for(int i =0;i<4;i++){
			triple[i] = v.get(i);
		}
		ContextEntry rdfEntry = new ContextEntry(triple);
		return rdfEntry;
	}

	@Override
	public void kpic_SPARQLEventHandler(SSAP_sparql_response newResults, SSAP_sparql_response oldResults,
			String indSequence, String subID) {
		userHandler.kpic_SPARQLEventHandler(newResults, oldResults, indSequence, subID);
		
		if (newResults != null) {
			for(Vector<String[]> v : newResults.getResults()){
				ContextEntry sparqlEntry = buildSparqlEntry(v);
				subscriptionContext.add(sparqlEntry);
			}
		}
		
		if (oldResults != null) {
			for(Vector<String[]> v : oldResults.getResults()){
				ContextEntry sparqlEntry = buildSparqlEntry(v);
				subscriptionContext.remove(sparqlEntry);
			}
		}
	}

	private ContextEntry buildSparqlEntry(Vector<String[]> v) {
		String [] data = new String[v.size()*3];
		//Every entry is [ varname , type , data ]
		for(int i = 0;i<v.size();i++){
			data[i*3] = v.elementAt(i)[0];
			data[i*3+1] = v.elementAt(i)[1];
			data[i*3+2] = v.elementAt(i)[2];
		}
		ContextEntry sparqlEntry = new ContextEntry(data);
		return sparqlEntry;
	}

	@Override
	public void kpic_UnsubscribeEventHandler(String sub_ID) {
		userHandler.kpic_UnsubscribeEventHandler(sub_ID);
		
	}

	@Override
	public void kpic_ExceptionEventHandler(Throwable SocketException) {
		userHandler.kpic_ExceptionEventHandler(SocketException);
	}
	
	public void handleReSubscribeRDF(SIBResponse newState){
		SubscriptionContext newSubCtx = createRDFSubCtx(newState);
		
		DiffResult diff = subscriptionContext.diff(newSubCtx);
		
		Vector<Vector<String>> newEntries = buildNewRDFTriples(diff);
		
		Vector<Vector<String>> oldEntries = buildOldRDFTriples(diff);
		
		userHandler.kpic_RDFEventHandler(newEntries, oldEntries, "", newState.subscription_id);
	}

	public void handleReSubscribeSPARQL(SIBResponse newState){
		SubscriptionContext newCtx = createSparqlCtx(newState);
		
		DiffResult diff = subscriptionContext.diff(newCtx);
		
		Vector<Vector<String[]>> newEntries = buildNewSparqlResults(diff);
		
		Vector<Vector<String[]>> oldEntries = buildOldSparqlResults(diff);
		
		SSAP_sparql_response newResults = new SSAP_sparql_response();
		SSAP_sparql_response oldResults = new SSAP_sparql_response();
		newResults.initFromResults(newEntries);
		oldResults.initFromResults(oldEntries);
		
		userHandler.kpic_SPARQLEventHandler(newResults, oldResults, "", newState.subscription_id);
	}
	private SubscriptionContext createRDFSubCtx(SIBResponse newState) {
		SubscriptionContext newSubCtx = new SubscriptionContext();
		
		for(Vector<String> v : newState.query_results){
			ContextEntry entry = buildRDFentry(v);
			newSubCtx.add(entry);
		}
		return newSubCtx;
	}
	
	private SubscriptionContext createSparqlCtx(SIBResponse newState) {
		SubscriptionContext newSubCtx = new SubscriptionContext();
		
		Vector<Vector<String[]>> results = newState.sparqlquery_results.getResults();
		for(Vector<String[]> v : results){
			ContextEntry sparqlEntry = buildSparqlEntry(v);
			newSubCtx.add(sparqlEntry);
		}
		return newSubCtx;
	}

	private Vector<Vector<String>> buildOldRDFTriples(DiffResult diff) {
		Vector<Vector<String>> oldEntries = new Vector<>();
		for(ContextEntry e : diff.getOldEntries()){
			Vector<String> data = new Vector<>(4);
			data.addAll(Arrays.asList(e.getData()));
			oldEntries.addElement(data);
		}
		return oldEntries;
	}

	private Vector<Vector<String>> buildNewRDFTriples(DiffResult diff) {
		Vector<Vector<String>> newEntries = new Vector<>();
		for(ContextEntry e : diff.getNewEntries()){
			Vector<String> data = new Vector<>(4);
			data.addAll(Arrays.asList(e.getData()));
			newEntries.addElement(data);
		}
		return newEntries;
	}
	
	private Vector<Vector<String[]>> buildOldSparqlResults(DiffResult diff) {
		Vector<Vector<String[]>> oldEntries = new Vector<>();
		for(ContextEntry e : diff.getOldEntries()){
			buildSparqlEntries(oldEntries, e);
		}
		return oldEntries;
	}

	private Vector<Vector<String[]>> buildNewSparqlResults(DiffResult diff) {
		Vector<Vector<String[]>> newEntries = new Vector<>();
		for(ContextEntry e : diff.getNewEntries()){
			buildSparqlEntries(newEntries, e);
		}
		return newEntries;
	}

	private void buildSparqlEntries(Vector<Vector<String[]>> newEntries, ContextEntry e) {
		Vector<String[]> data = new Vector<>();
		String[] entryData = e.getData();
		for(int i =0 ;i < entryData.length;i++){
			String [] rawdata = new String[3];
			//Every entry is [ varname , type , data ]
			rawdata[0] = entryData[i];
			i++;
			rawdata[1] = entryData[i];
			i++;
			rawdata[2] = entryData[i];
			
			data.add(rawdata);
		}
		newEntries.addElement(data);
	}
	
	
}
