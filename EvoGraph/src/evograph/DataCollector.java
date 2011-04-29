package evograph;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
public class DataCollector {
	
	protected String rootPath = "../../output/k-graphs/";
	protected String inputFilePath;
	protected int numTestCases;
	protected BufferedReader br;
	protected long startTime;
	protected String algoName;	protected String problemInstance;	protected int runNumber;
	BufferedWriter bw;
	
	public DataCollector(String algoName, String problemInstance, int runNumber) {
		this.algoName = algoName;		this.problemInstance = problemInstance;		//this.runNumber = runNumber;
		initialize();
	}
	
	public void initialize() {
		try {			new File(rootPath+algoName).mkdir();			//new File(rootPath + algoName+"/"+problemInstance).mkdir();
			FileWriter fwriter = new FileWriter(rootPath + algoName + "/" + problemInstance + ".txt", true); //"/" + runNumber + ".txt", true);
			bw = new BufferedWriter(fwriter);
		} catch (Exception e) {
	    	System.err.println("Error: " + e.getMessage());
	    }	
	}
		
	public void writeReading(String reading) {
		try {
			bw.write(reading + "\n");	
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