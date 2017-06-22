/**
 * This class works with jena libraries correctly imported into namespace. Libraries are not directly included
 * for reasosns of dimension and because of restrictions on directly including Jena liraries into external projects.
 * Jena libraries can be downloaded from the official Jena libraries website
 * @author Alfredo D'Elia
 *
 */

package resources;

import java.nio.file.Paths;
import java.util.Vector;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import sofia_kp.KPICore;
import sofia_kp.SIBResponse;
import sofia_kp.SSAP_XMLTools;



public class JenaBasedOntologyLoader {
	
//	String SIB_host = "mml.arces.unibo.it";
//	int SIBPort = 7701;
	String SIB_host = "127.0.0.1";
    int SIBPort = 10111;
	String SIBName = "X";
	KPICore kp ;
	SIBResponse resp = new SIBResponse();
	java.nio.file.Path input = Paths.get("C:\\Users\\Alfredo D\'Elia\\git\\tesiantoniazzi\\KPI_Management", "test.owl");
	private String ontology_path = "Cidoc_CRRM_CORE_5.0.1.owl";//path to the ontology file
	Vector<Vector<String>> literalTriples = new Vector<Vector<String>>();
	Vector<Vector<String>> ObjectTriples = new Vector<Vector<String>>();


	public static void main(String[] argv)
	{
		JenaBasedOntologyLoader loader = new JenaBasedOntologyLoader();
		loader.LoadOntologyIntoSIB( );
	}
	
	
	public void LoadOntologyIntoSIB (String OntologyPath) 
	{
		
		OntModel model = ModelFactory.createOntologyModel();
		model.read(input.toUri().toString(), "RDF/XML");
		StmtIterator it = model.listStatements();
		//System.out.println("***" + it.toList().size());
		Statement st;
		while (it.hasNext())
		{
			st = it.next();
			if(st.getObject().isLiteral())
			{
				addLiteralTriple(st);
			}
			else
			{
				addObjectTriple(st);
			}
		}

		boolean ok = bufferedInsert();
//		String ontString = "";
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		model.write(baos);
//		
//		kp = new KPICore(SIB_host, SIBPort, SIBName);
//		kp.join();
//		ontString = baos.toString();
//		System.out.println("*****************" + ontString);
//		resp = kp.insert_rdf_xml(ontString);//.replace("\n", "").replace("\r", ""));
		if(ok)
		{
			System.out.println("Ontology correctly inserted");
		}
		else
		{
			System.out.println("Ontology not inserted");

		}
		
		// TODO Auto-generated constructor stub
	}
	
	public void LoadOntologyIntoSIB ()
	{
		this.LoadOntologyIntoSIB(ontology_path);
	}
	
	public void addLiteralTriple(Statement st)
	{
		Vector<String> temp  =new Vector<String>();
		SSAP_XMLTools ssap = new SSAP_XMLTools();
		temp = ssap.newTriple(st.getSubject().getURI(), st.getPredicate().getURI(), st.getObject().asLiteral().getString(), "uri", "literal");
	literalTriples.add(temp);
	}
	public void addObjectTriple(Statement st)
	{
		Vector<String> temp  =new Vector<String>();
		SSAP_XMLTools ssap = new SSAP_XMLTools();
		temp = ssap.newTriple(st.getSubject().getURI(), st.getPredicate().getURI(), st.getObject().asResource().getURI(), "uri", "uri");
	    ObjectTriples.add(temp);
	}
	
	public boolean bufferedInsert()
	{
		Vector<Vector<String>> triples = new Vector<Vector<String>>();
		kp = new KPICore(SIB_host, SIBPort, SIBName);
		kp.join();
		while((ObjectTriples.size()>0) || (literalTriples.size()>0)   )
		{
			triples =  new Vector<Vector<String>>();
			while(triples.size()<100 && ( (ObjectTriples.size()>0) || (literalTriples.size()>0)  ) )
			{
				if(ObjectTriples.size()>0)
				{
					triples.add(ObjectTriples.firstElement());
					ObjectTriples.remove(0);
				}
				if(literalTriples.size()>0)
				{
					triples.add(literalTriples.firstElement());
					literalTriples.remove(0);
				}
			}
			resp = kp.insert(triples);
			if(!resp.isConfirmed())
			{
				return false;
			}
			
		}
		return true;
			
	}

}
