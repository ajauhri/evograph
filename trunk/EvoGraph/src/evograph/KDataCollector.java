package evograph;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
public class KDataCollector {
	
	protected String rootPath = "../output/";
	protected String inputFilePath;
	protected int numTestCases;
	protected BufferedReader br;
	protected long startTime;	protected String filename;
	BufferedWriter bw;
	
	public KDataCollector(String filename) {
		this.filename = filename;
		initialize();
	}
	
	public void initialize() {
		try {
			new File(rootPath + filename + ".txt").delete();
			new File(rootPath + filename + ".txt").createNewFile();
			FileWriter fwriter = new FileWriter(rootPath + filename + ".txt", false);
			bw = new BufferedWriter(fwriter);
		} catch (Exception e) {
	    	System.err.println("Error: " + e.getMessage());
	    }	
	}
	
	public void writeLine(String line) {
		writeLine(line, true);
	}
	
	public void writeLine(String line, boolean verbose) {
		try {
			if(verbose)
				System.out.println(line);
			bw.write(line + "\n");	
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}	
	}
	
	public void close() {
		try {
			bw.close();	
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}	
	}}