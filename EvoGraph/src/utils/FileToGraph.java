package utils;

import graph.Graph;

import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
//import java.io.FileWriter;
import java.io.InputStreamReader;

public class FileToGraph {
	protected String rootPath = "../src/metadata/";
	protected String inputFilePath;
	
	public FileToGraph(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public Graph createGraph() {
		try {
			FileInputStream fstream = new FileInputStream(rootPath + inputFilePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			int numberOfNodes = Integer.parseInt(br.readLine());
			Graph graph = new Graph(numberOfNodes);
			for(int i = 0; i < numberOfNodes; i++) {
				String line = br.readLine();
				String[] connectedNodes = line.split(" ");
				for(int j = 1; j < connectedNodes.length; j++)
					graph.createEdge(Integer.parseInt(connectedNodes[0]), Integer.parseInt(connectedNodes[j]));
			}
		    in.close();
			return graph;
	    } catch (Exception e) {
	    	//System.out.println("error");
	    	e.printStackTrace();
	    	return null;
	    }	
	}
}
