package io;

import graph.Graph;

import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
//import java.io.FileWriter;
import java.io.InputStreamReader;

public class FileToGraph {// implements Runnable {
	
	protected String rootPath = "../src/metadata/";
	protected String inputFilePath;
	protected int numberOfNodes;
	protected BufferedReader br;
	protected long startTime;
	public Graph graph;
	
	public FileToGraph(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public Graph createGraph() {
		startTime = System.currentTimeMillis();
		try {
			FileInputStream fstream = new FileInputStream(rootPath + inputFilePath);
			DataInputStream in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			numberOfNodes = Integer.parseInt(br.readLine());
			graph = new Graph(numberOfNodes);
			for(int i = 0; i < numberOfNodes; i++) {
				String line = br.readLine();
				String[] connectedNodes = line.split(" ");
				for(int j = 1; j < connectedNodes.length; j++)
					graph.createEdge(Integer.parseInt(connectedNodes[0]), Integer.parseInt(connectedNodes[j]));
			}
			graph.printEdgeMatrix();
			graph.calculateOptimalEdgeLength();
		    in.close();
			long elapsedTime = System.currentTimeMillis() - startTime;
			System.out.println("Completed in " + elapsedTime + " ms");
			graph.printConnections();
			return graph;
	    } catch (Exception e) {
	    	System.err.println("Error: " + e.getMessage());
	    	return null;
	    }	
	}
}
