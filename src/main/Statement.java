package main;

import org.neo4j.graphdb.RelationshipType;

import main.GraphDBasic.RelTypes;

public class Statement {
	
	private String term1;
	private String term2;
	String r;
	
	public Statement(String t1, String t2, String r){
		this.term1 = t1;
		this.term2 = t2;
		this.r = r;
	}
	
	public String getFirstTerm(){
		return term1;
	}
	
	public String getSecondTerm(){
		return term2;
	}
	
	public RelationshipType getRelationshipType(){
		if (r.equals("INCREASES"))
			return RelTypes.INCREASES;
		if (r.equals("DECREASES"))
			return RelTypes.DECREASES;
		if (r.equals("EATS"))
			return RelTypes.EATS;
		if (r.equals("HATES"))
			return RelTypes.HATES;
		else if (r.equals("LOVES"))
			return RelTypes.LOVES;
		else
			return null;
	}
	
	public String toString() {
		return term1 + " " + r + " " +term2;
	}
}
