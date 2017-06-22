package resources;



public class OntloaderTest {
	
	public static void main(String[]  argv)
	{
//SimpleOntologyLoader loader = new SimpleOntologyLoader();
//loader.simpleLoad("ioe-ontology_v1.6.3.owl");

JenaBasedOntologyLoader loader2 = new JenaBasedOntologyLoader();
//loader2.LoadOntologyIntoSIB("ioe-ontology_v1.6.3.owl" );
loader2.LoadOntologyIntoSIB("SimCity_100x50.owl" );
	}

	public OntloaderTest()
	{
		// TODO Auto-generated constructor stub


	

	}

}
