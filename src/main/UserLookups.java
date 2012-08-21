package main;


import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import static java.lang.System.out;

@SuppressWarnings("unused")

public class UserLookups {
	
	private static GraphDatabaseService graphDb;
	private static final String USERNAME_KEY = "username";
	private static Index<Node> nodeIndex;
	
	private static enum RelTypes implements RelationshipType{
		USERS_REF,
		USER
	}
	
	private static String idToUserName(final int id){
		return "user" + id + "@selventa.org";
	}
	
	private static Node createAndIndexUser( final String username )
	{
	    Node node = graphDb.createNode();
	    node.setProperty( USERNAME_KEY, username );
	    nodeIndex.add( node, USERNAME_KEY, username );
	    return node;
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
	
	public static void main(String[] args){
		GraphDatabaseService gdbs = new GraphDatabaseFactory().newEmbeddedDatabase("var/db_tmp");
		//nodeIndex = gdbs.
	}
}
