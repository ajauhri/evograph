package evo;

import graph.Edge;
import graph.Graph;
import graph.Node;

public class Fitness {
	Graph graph;
	static double optimalEdgeLength = 50.0; 

	
	public Fitness(Graph graph) {
		this.graph = graph;
	}
	
	public void calculate() {
		double sumOfEdgeLengths = sumEdgeLengths();
	}
	
	public double sumEdgeLengths() {
		double sum = 0;
		int numberOfNodes = graph.getNumberOfNodes();
		for (int i = 0; i < numberOfNodes; i++){
			Node node = graph.getNodeAt(i);
			Object[] edges = node.getEdges();
			for (Object e : edges) {
				sum += ((Edge) e).edgeLength;
			}
		}
		return sum;
	}
	
	public int getFitnessValue() {
		return 0; 
	}
}
