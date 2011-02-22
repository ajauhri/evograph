package evo;

import graph.Edge;
import graph.Graph;
import graph.Node;

import io.FileToGraph;

import java.util.Vector;

public class GeneticAlgorithm {
	static final String fileName = "test1.rgf";
	static final int populationSize = 100;
	Vector<Graph> population = new Vector<Graph>();
	Graph graph;

	public GeneticAlgorithm() {
		createGraph();
		initializePopulation();
	}

	public void createGraph() {
		FileToGraph fileToGraph = new FileToGraph(fileName);
		graph = fileToGraph.createGraph();
	}

	public void initializePopulation() {
		// population = new Population(populationSize);
		for (int i = 0; i < populationSize; i++) {
			Graph individual = (Graph) graph.copy();
			initializeIndividual(individual);
			individual.calculateFitness();
			population.add(individual);
			
		}
	}

	public void initializeIndividual(Graph individual) {
		int numberOfNodes = graph.getNumberOfNodes();

		for (int i = 0; i < numberOfNodes; i++) {
			individual.getNodeAt(i).setXandY((int) (Math.random() * 400),
					(int) (Math.random() * 400));
			}
		for (int i = 0; i < numberOfNodes; i++) {
			Node node = graph.getNodeAt(i);
			Object[] edges = node.getEdges();
			for (Object e : edges) {
				((Edge) e).computeEdgeLength(node.getX(), node.getY());
			}
		}
		

	}
	
}
