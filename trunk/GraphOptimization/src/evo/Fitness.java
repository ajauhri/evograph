package evo;

import graph.Edge;
import graph.Graph;
import graph.Node;

public class Fitness {
	Graph graph;
	static double optimalEdgeLength = 50.0; 
	public double value; //Fitness value
	
	public Fitness(Graph graph) {
		this.graph = graph;
	}
	
	public void calculate() {
		double sumOfEdgeLengths = 0;
		int totalNumberOfEdges = 0;
		int numberOfNodes = graph.getNumberOfNodes();
		for (int i = 0; i < numberOfNodes; i++){
			Node node = graph.getNodeAt(i);
			Object[] edges = node.getEdges();
			for (Object e : edges) {
				double edgeLength = ((Edge) e).edgeLength;
				//totalDeviationFromOptimal += Math.abs(edgeLength - optimalEdgeLength);
				sumOfEdgeLengths += edgeLength;
				totalNumberOfEdges++;
			}
		}
		value = Math.abs((sumOfEdgeLengths / totalNumberOfEdges) - optimalEdgeLength); //0 is optimal
	}
	
	public int getFitnessValue() {
		return 0; 
	}
}
