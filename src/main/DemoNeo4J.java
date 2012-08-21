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
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.server.*;

import static java.lang.System.out;

public class DemoNeo4J {
	
	private static GraphDatabaseService graphDb;
	private static Node firstNode, secondNode;
	private static Relationship relation;
	private static JParser jp;
	private static List<Statement> statements;
	
	public static void main(String[] args){
		
		File file = new File("facts.txt");
		try{
			jp = new JParser(file);
		}
		catch (FileNotFoundException e){
			out.println(e.getMessage());
			out.println(e.getStackTrace());
		}
		
		// Each line of the input file is one statement, consisting of two terms and a relationship.
		statements = jp.getStatements();
		
		// Instantiate db and register shutdown hook.
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "var/graphDb" );
		registerShutdownHook(graphDb);

		WrappingNeoServerBootstrapper srv = new WrappingNeoServerBootstrapper( graphDb );
		srv.start();
		
		for (Statement s : statements){

			Transaction tx = graphDb.beginTx();
			try{
				firstNode = graphDb.createNode();
				firstNode.setProperty("message", s.getFirstTerm());
				secondNode = graphDb.createNode();
				secondNode.setProperty( "message", s.getSecondTerm());
			
				relation = firstNode.createRelationshipTo(secondNode, s.getRelationshipType());
				//relation.setProperty("message", "crazy cruel");
				tx.success();
			}
			finally{
				tx.finish();
			}
		}
		srv.stop();
		
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
}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		