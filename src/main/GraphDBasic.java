package main;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import static java.lang.System.out;

@SuppressWarnings("unused") 
public class GraphDBasic {
	
	private static GraphDatabaseService graphDb;
	private static Node firstNode, secondNode;
	private static Relationship relation;
	
	public GraphDBasic(){
		
		// Instantiate the DB. Note that this instance can be shared across multiple
		// threads, so re-use it as much as possible.
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "var/graphDb" );
		registerShutdownHook(graphDb);
		
	};
	
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
	
	public static enum RelTypes implements RelationshipType
	{
	    KNOWS,
	    INCREASES,
	    DECREASES,
	    EATS,
	    LOVES,
	    HATES,
	}
	
	public static void main(String[] args){
		
		GraphDBasic gdb = new GraphDBasic();
		String greeting;
		
		// ALL mutating transactions must be performed within a transaction block.
		Transaction tx = graphDb.beginTx();
		try{
			firstNode = graphDb.createNode();
			firstNode.setProperty("message", "Hello ");
			secondNode = graphDb.createNode();
			secondNode.setProperty( "message", " world!" );
			
			relation = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
			relation.setProperty("message", "crazy cruel");
			
			/**************************************************************************************
			 Deleting a node which still has relationships when the transaction commits will FAIL!
			 Delete the relationship first.
			 *************************************************************************************/
			//firstNode.getSingleRelationship( RelTypes.KNOWS, Direction.OUTGOING ).delete();
			//firstNode.delete();
			//secondNode.delete();
			tx.success();
		}
		finally{
			tx.finish();
		}
		
		//getProperty() returns an Object, so this cast is necessary and by design.
		greeting = ((String)firstNode.getProperty("message"))
				+ (relation.getProperty("message"))
				+ (secondNode.getProperty("message"));
		
		out.print(greeting);
		
		graphDb.shutdown();
	}

}
