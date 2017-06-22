package resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import sofia_kp.KPICore;
import sofia_kp.SIBResponse;
import sofia_kp.SSAP_sparql_response;


/**
 * 
 * @author Alfredo
 * Class to make a copy of a SIB in a rdf file
 */

public class SIBCopy {
	
	public String outputfile = "sib3.rdf";
	public String sib_host = "127.0.0.1";
	public String getOutputfile() {
		return outputfile;
	}

	public void setOutputfile(String outputfile) {
		this.outputfile = outputfile;
	}

	public String getSib_host() {
		return sib_host;
	}

	public void setSib_host(String sib_host) {
		this.sib_host = sib_host;
	}

	public int getSib_port() {
		return sib_port;
	}

	public void setSib_port(int sib_port) {
		this.sib_port = sib_port;
	}

	public String getSib_name() {
		return sib_name;
	}

	public void setSib_name(String sib_name) {
		this.sib_name = sib_name;
	}

	public int sib_port = 10111;
	public String sib_name  = "X";
	
	
	
	public static void main (String[] argv)
	{
		SIBCopy test = new SIBCopy();
		test.copySIB(null);
	}
	
	public SIBCopy()
	{
		
	}
	
	public void copySIB(String out_file)
	{
	if(out_file!=null)
	{
		this.setOutputfile(out_file);
	}
	
	Model model = ModelFactory.createDefaultModel();

	KPICore kp = new KPICore(sib_host,sib_port,sib_name);
	kp.join();
	SIBResponse resp = new SIBResponse();
	resp = kp.querySPARQL("SELECT ?a ?b ?c WHERE { ?a ?b ?c }");
	SSAP_sparql_response sparql_result = resp.sparqlquery_results;
	Statement st = null;  
	Vector<String[]> result = null;
	String subject ="";
	String predicate = "";
	String object = "";
	String object_type = "";
	
	while (sparql_result.hasNext())
	{
		sparql_result.next();
		result    = sparql_result.getRow();
		subject   = sparql_result.getValueForVarName("a");
		predicate = sparql_result.getValueForVarName("b");
		object    = sparql_result.getValueForVarName("c");
		object_type = sparql_result.getValueCategoryForVarName("c");
		
		if(object_type.equals(SSAP_sparql_response.LITERAL))
		{
		st = model.createLiteralStatement(model.createResource(subject), model.createProperty(predicate), object);
		}
		else if(object_type.equals(SSAP_sparql_response.URI))
		{
		st = model.createStatement(model.createResource(subject), model.createProperty(predicate), model.createResource(object));
		}
		
		model.add(st);
	}
	
	
	System.out.println("Writing knowledge base rdf file");
	File outFile = new File(getOutputfile());
	
	//boolean ok = false;
	
	if (!outFile.exists())
	{
		try {
		/*	ok =*/ outFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println ("Error while creating the output file");
			e.printStackTrace();
		}
	}
	
	try {
		 model.write(new FileOutputStream(outFile));
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		System.out.println ("Error while writing the model");
		e.printStackTrace();
	}
	kp.leave();
	System.out.println("Writing knowledge base rdf file");
	
	}

}
