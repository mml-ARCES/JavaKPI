package resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import sofia_kp.KPICore;
import sofia_kp.SIBResponse;

public class SimpleOntologyLoader {


	String SIB_host = "127.0.0.1";
	int SIBPort = 10111;
	String SIBName = "X";
	KPICore kp ;
	SIBResponse resp = new SIBResponse();


	public void simpleLoad (String OntologyPath) 
	{
		String ontString = "";
		StringBuilder builder = new StringBuilder();
		try
		{
			ontString =   new String(Files.readAllBytes(Paths.get(OntologyPath)));
			ontString = ontString.substring(21);

		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		kp = new KPICore(SIB_host, SIBPort, SIBName);
		System.out.println("*****************" + ontString);
		resp = kp.insert_rdf_xml(ontString.replace("\n", "").replace("\r", ""));
		if(resp.isConfirmed())
		{
			System.out.println("Ontology correctly inserted");
		}
		else
		{
			System.out.println("Ontology not inserted");
		}

	}



	public SimpleOntologyLoader() {
		// TODO Auto-generated constructor stub







	}

}
