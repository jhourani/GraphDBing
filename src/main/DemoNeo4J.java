package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.server.WrappingNeoServerBootstrapper;

import org.openbel.framework.tools.PhaseOneApplication;
import org.openbel.framework.common.bel.parser.BELParser;
import org.openbel.framework.common.model.Document;
import org.openbel.framework.common.model.Term;
import org.openbel.framework.compiler.DefaultPhaseOne;
import org.openbel.framework.compiler.PhaseOneImpl;
import org.openbel.framework.compiler.DefaultPhaseOne.Stage1Output;

import static java.lang.System.out;
import java.lang.reflect.Field;

public class DemoNeo4J {
	
	private static GraphDatabaseService graphDb;
	private static AbstractGraphDatabase abstractDb;
	private static Node firstNode, secondNode;
	private static Relationship relation;
	
	private static BELParser bel_parse;
	private static PhaseOneApplication p1app;
	private static PhaseOneImpl p1;
	private static DefaultPhaseOne.Stage1Output output;
	private static Document doc;
	private static Term term;
	private static List<org.openbel.framework.common.model.Statement> statements;
	//private static JParser jp;
	
	
	public static void main(String[] args) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
	
		File file = new File("full_abstract1.bel");
		String[] tmp = new String[] {file.getName()};
		p1app = new PhaseOneApplication(tmp);
		
		Field field = p1app.getClass().getDeclaredField("p1");
		field.setAccessible(true);
		p1 = (PhaseOneImpl)field.get(p1app);
		
		output = p1.stage1BELValidation(file);
		doc = output.getDocument();
		statements = doc.getAllStatements();
		
		
		// Each line of the input file is one statement, consisting of two terms and a relationship.
		// statements = jp.getStatements();
		// out.println("Line  #'s : " +jp.getLineNumbers());
		// Instantiate db and register shutdown hook.
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "var/graphDb/full_abstract1" );
		registerShutdownHook(graphDb);
		

		Transaction tx = graphDb.beginTx();
		int count = 0;
		try {
			for (org.openbel.framework.common.model.Statement s : statements) {
				
				firstNode = graphDb.createNode();
				String str = s.getSubject().toBELShortForm();
				firstNode.setProperty("getSubject()", str);
				
				secondNode = graphDb.createNode();
				String str0 = s.getObject().toBELShortForm();
				secondNode.setProperty( "getObject()", str0);
				
				// have to convert the Relationship Type
				org.openbel.framework.common.enums.RelationshipType r = s.getRelationshipType();
				RelationshipType r_neo = makeNeoRType(r);
				relation = firstNode.createRelationshipTo(secondNode, r_neo);
				
				tx.success();
				out.println("# statements: " +count++);
			}
		}
		finally {
			tx.finish();
		}
		// Some debug code, to make sure I get all the nodes I expect.
		for (Node n : graphDb.getAllNodes()) {
			for (Relationship r : n.getRelationships()) {
				out.println("Node Id: " +n.getId());
				out.println("Relationship Type: " +r.getType());
			}
		}
		out.println("Done");
	}
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running example before it's completed)
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}
	
	private static RelationshipType makeNeoRType(org.openbel.framework.common.enums.RelationshipType r) {
		switch(r) {
		case INCREASES:	return RelTypes.INCREASES;	
		case DECREASES: return RelTypes.DECREASES;	
		case DIRECTLY_INCREASES:	return RelTypes.DIRECTLY_INCREASES;
		case DIRECTLY_DECREASES:	return RelTypes.DIRECTLY_DECREASES;
		case ACTS_IN:	return RelTypes.ACTS_IN;
		case ANALOGOUS:	return RelTypes.ANALOGOUS;
		case ASSOCIATION:	return RelTypes.ASSOCIATION;
		case REACTANT_IN:	return RelTypes.REACTANT_IN;
		case NEGATIVE_CORRELATION:	return RelTypes.NEGATIVE_CORRELATION;
		case POSITIVE_CORRELATION:	return RelTypes.POSITIVE_CORRELATION;
		case BIOMARKER_FOR:	return RelTypes.BIOMARKER_FOR;
		case HAS_COMPONENT:	return RelTypes.HAS_COMPONENT;
		case HAS_COMPONENTS:	return RelTypes.HAS_COMPONENTS;
		case HAS_MEMBER:	return RelTypes.HAS_MEMBER;
		case HAS_MEMBERS:	return RelTypes.HAS_MEMBERS;
		case HAS_MODIFICATION:	return RelTypes.HAS_MODIFICATION;
		case HAS_PRODUCT:	return RelTypes.HAS_PRODUCT;
		case HAS_VARIANT:	return RelTypes.HAS_VARIANT;
		case INCLUDES:	return RelTypes.INCLUDES;
		case IS_A:	return RelTypes.IS_A;
		case ORTHOLOGOUS:	return RelTypes.OTHOLOGOUS;
		case PROGNOSTIC_BIOMARKER_FOR:	return RelTypes.PROGNOSTIC_BIOMARKER_FOR;
		case RATE_LIMITING_STEP_OF:	return RelTypes.RATE_LIMITING_STEP_OF;
		case SUB_PROCESS_OF:	return RelTypes.SUB_PROCESS_OF;
		case TRANSCRIBED_TO:	return RelTypes.TRANSCRIBED_TO;
		case TRANSLATED_TO:	return RelTypes.TRANSLATED_TO;
		case TRANSLOCATES:	return RelTypes.TRANSLOCATES;
		default:
			break;
		}
		return RelTypes.CAUSES_NO_CHANGE;
	}
	
	public static enum RelTypes implements RelationshipType
	{
	    INCREASES,
	    DIRECTLY_INCREASES,
	    DECREASES,
	    DIRECTLY_DECREASES,
	    CAUSES_NO_CHANGE,
	    ACTS_IN,
	    ANALOGOUS,
	    ASSOCIATION,
	    REACTANT_IN,
	    NEGATIVE_CORRELATION,
	    POSITIVE_CORRELATION,
	    BIOMARKER_FOR,
	    HAS_COMPONENT,
	    HAS_COMPONENTS,
	    HAS_MEMBER,
	    HAS_MEMBERS,
	    HAS_MODIFICATION,
	    HAS_PRODUCT,
	    HAS_VARIANT,
	    INCLUDES,
	    IS_A,
	    OTHOLOGOUS,
	    PROGNOSTIC_BIOMARKER_FOR,
	    RATE_LIMITING_STEP_OF,
	    SUB_PROCESS_OF,
	    TRANSCRIBED_TO,
	    TRANSLATED_TO,
	    TRANSLOCATES
	}
}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		