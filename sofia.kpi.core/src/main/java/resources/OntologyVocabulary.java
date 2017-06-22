package resources;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyVocabulary {
	String namespace = "http://www.semanticweb.org/2013/Energy_Aware_Smart_Building#";
	/*public static String rdf_ns = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static String rdf_schema_ns = "http://www.w3.org/2000/01/rdf-schema#";
    public static String owl_ns="";//????????????????????????????
*/    
   /* public Hashtable<Stringname, Stringvalue> classTable;
    public Hashtable<Stringname, Stringvalue> objectPropertyTable;
    public Hashtable<Stringname, Stringvalue> dataPropertyTable;
    public Hashtable<Stringname, Stringvalue> instanceTable;*/

    
    public OntologyVocabulary(String ontPath, String destination, String packageName){
    	
    	writeClass(ontPath, destination, packageName);
		/*this.namespace=namespace;
		OntModel model = ModelFactory.createOntologyModel();
		//model.read(new InputStream() 
			model.listClasses();
			model.listObjectProperties();
			model.listDatatypeProperties();
			model.listIndividuals();
			
			@Override
			public int read() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}
		})
		
		//Sono queste le classi???
		ResIterator resIterator = model.listSubjects();
		StmtIterator stmtIterator;
		Statement currentStatement;
		
		while (resIterator.hasNext()){
			
			Resource resource = resIterator.next();
			classTable.put(resource.getLocalName(), namespace+resource.getLocalName());
			
		//Proprietà
			stmtIterator = resource.listProperties();
			while (stmtIterator.hasNext()){
				
				currentStatement = stmtIterator.next();
				Property currentProperty = currentStatement.getPredicate();
				if (currentProperty.isLiteral())
				{
					//Data Property
					dataPropertyTable.put(currentProperty.getLocalName(), namespace+currentProperty.getLocalName());
				}
				else
				{
					//Object Property
					objectPropertyTable.put(currentProperty.getLocalName(), namespace+currentProperty.getLocalName());
				}
				
				//Instance
				RDFNode rDFNode = currentStatement.getObject();
				instanceTable.put(rDFNode.toString(), namespace+ rDFNode.toString());
				
				writeClass();
			}*/
			
		
	}
    
    void writeClass(String fileInput, String fileOutput, String packageName)
	{
		OntModel model = ModelFactory.createOntologyModel();
		
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(fileInput);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model.read(inputStream, null);	
		
		try {
			inputStream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (fileOutput==null)
			fileOutput="OntologyReference.java";
		
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(fileOutput);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Scrittura del file OntologyReference
		//Nome package
		printWriter.println("package " + packageName + ";");
		printWriter.println();
		
		//  Per dare un'altro nome alla classe: String className = fileOutput.substring(0, fileOutput.length()-5);//Toglie i 5 caratteri finali del fileOutput .java
		printWriter.println("public class OntologyReference {");
		
		//Namespaces
		printWriter.println("\tpublic static String ns = \"http://www.semanticweb.org/2013/Energy_Aware_Smart_Building#\";");
		printWriter.println("\tpublic static String rdf_ns = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\";");
		printWriter.println("\tpublic static String rdf_schema_ns = \"http://www.w3.org/2000/01/rdf-schema#\";");
		printWriter.println("\tpublic static String owl_ns=\"http://www.w3.org/2002/07/owl#\";");	
	
		//Classes
		printWriter.println();
		printWriter.println("\t//Classes");
		
		ExtendedIterator<OntClass> extendedIteratorClasses = model.listClasses();	
		
		//int numClasses=0;
		Vector<String> classNames = new Vector<String>() ; 
		while (extendedIteratorClasses.hasNext())
		{
			OntClass ontClass = extendedIteratorClasses.next();
			if (ontClass.getLocalName()!=null){
				printWriter.println("\tpublic static String " + ontClass.getLocalName()+" = \"" + namespace + ontClass.getLocalName() +"\";");
				
				classNames.add(ontClass.getLocalName());
				//numClasses++;
				}
		}
		
		//Stampa di prova
		/*for (int i=0; i<classNames.size();i++)
			{
			System.out.println(classNames.get(i));
			}*/
		
		//Object Properties
		printWriter.println();
		printWriter.println("\t//Object Properties");
		
		ExtendedIterator<ObjectProperty> extendedIteratorObjectProperties = model.listObjectProperties();
		while (extendedIteratorObjectProperties.hasNext())
		{
			ObjectProperty objectProperty = extendedIteratorObjectProperties.next();
			printWriter.println("\tpublic static String " + objectProperty.getLocalName()+" = \"" + namespace + objectProperty.getLocalName() +"\";");
		}
		
		//Data Properties
		printWriter.println();
		printWriter.println("\t//Data Properties");
		
		ExtendedIterator<DatatypeProperty> extendedIteratorDatatypeProperties = model.listDatatypeProperties();
		while (extendedIteratorDatatypeProperties.hasNext())
		{
			DatatypeProperty datatypeProperty = extendedIteratorDatatypeProperties.next();
			printWriter.println("\tpublic static String " + datatypeProperty.getLocalName()+" = \"" + namespace + datatypeProperty.getLocalName() +"\";");
		}
		
		//Instances
		printWriter.println();
		printWriter.println("\t//Instances");
		
		ExtendedIterator<Individual> extendedIteratorIndividuals =	model.listIndividuals();
		Integer[] individualCounter= new Integer[classNames.size()];
		
		for(int i=0; i<individualCounter.length;i++)
		{
			individualCounter[i]=0;
		}
		
		//int index=0;
		String indClassName="";
		String variableName="";
		while(extendedIteratorIndividuals.hasNext())
		{	//Devo prendere il localname e sostituire al carattere - il carattere _	
			Individual individual = extendedIteratorIndividuals.next();
			indClassName=individual.getOntClass().getLocalName();
			//index=individualCounter[classNames.indexOf(indClassName)];
			//variableName = indClassName+"_"+index;
			variableName = individual.getLocalName().replace('-', '_');
			//System.out.println("Classe: "+indClassName+"\tIndice: "+index);
			
			printWriter.println("\tpublic static String " + variableName +" = \"" + individual.getURI() +"\";");
			
			individualCounter[classNames.indexOf(indClassName)]= individualCounter[classNames.indexOf(indClassName)]+1;
		}
		
		//Final }
		printWriter.println();
		printWriter.println("}");
		
		printWriter.close();
	}
	
	
}
