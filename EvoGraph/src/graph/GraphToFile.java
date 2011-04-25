package graph;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
public class GraphToFile {
	
	protected String rootPath = "C:/Users/Phil/workspace/CodeJam/src/";
	protected String inputFilePath;
	protected int numTestCases;
	protected BufferedReader br;
	protected long startTime;
	protected String algoName;
	
	public GraphToFile(String algoName, String problemInstance, int runNumber) {
		this.algoName = algoName;
	}
	public void writeReading(String reading) {
		//startTime = System.currentTimeMillis();
		try {
			}
			FileWriter fwriter = new FileWriter(rootPath + algoName + problemInstance + runNumber + ".out", true);
			BufferedWriter bw = new BufferedWriter(fwriter);
			for(int i = 1; i <= numTestCases; i++) {
				bw.write(reading + "\n");
				//System.out.println("Case #" + i + " complete");
			}
			bw.close();
		} catch (Exception e) {
	    	System.err.println("Error: " + e.getMessage());
	    }	
	}
