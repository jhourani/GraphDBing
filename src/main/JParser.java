package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;

public class JParser {

	private String term1;
	private String relation;
	private String term2;
	private int num_lines;
	private List<Statement> list;
	private Scanner scan;
	
	public JParser(File file) throws FileNotFoundException{
		try{
			list = new ArrayList<Statement>();
			scan = new Scanner(file);
			makeStatements();
		}
		catch(FileNotFoundException e){
			out.println(e.getMessage());
		}
	}
	
	public int getLineNumbers(){
		return num_lines;
	}
	
	public void makeStatements(){
		while (scan.hasNextLine()){
			if (!scan.hasNext())
				break;
			term1 = scan.next();
			relation = scan.next();
			term2 = scan.next();
			list.add(new Statement(term1, term2, relation));
			out.println("\nterm1:" +term1+ " term2:" +term2+ " relationship:" +relation);
			num_lines++;
		}
	}
	
	public List<Statement> getStatements(){
		return list;
	}
}
	
