package evo;

import graph.Edge;
import graph.Graph;
import graph.Node;

import io.FileToGraph;

import java.util.Collections;
import java.util.Comparator;
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
		for (int i = 0; i < populationSize; i++) {
			Graph individual;
			try {
				individual = (Graph) graph.copy();
				initializeIndividual(individual);
				individual.calculateFitness();
				population.add(individual);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//double individualFitness = individual.fitness;
		}
		sortPopulationByFitness();
		
		
		for (int i = 0; i < populationSize; i++) {
			System.out.println("Graph " + i + " fitness is " + population.get(i).getFitness());
		}
	}
	
	public Graph getFittestIndividual() {
		return population.get(0);
	}
	
	public void sortPopulationByFitness() {
		Collections.sort(population, new FitnessComparator());
	}

	public void initializeIndividual(Graph individual) {
		int numberOfNodes = individual.getNumberOfNodes();
		for (int i = 0; i < numberOfNodes; i++) {
			individual.getNodeAt(i).setXandY((int) (Math.random() * 400), (int) (Math.random() * 400));
		}
		for (int i = 0; i < numberOfNodes; i++) {
			Node node = individual.getNodeAt(i);
			Object[] edges = node.getEdges();
			for (Object e : edges) {
				((Edge) e).computeEdgeLength(node.getX(), node.getY());
			}
		}
	}
	
	class FitnessComparator implements Comparator<Graph> {
		public int compare(Graph g1, Graph g2) {
			if (g2.getFitness() == g1.getFitness())
				return 0;
			else if (g2.getFitness() > g1.getFitness())
				return -1;
			else
				return 1;
		}
	}
}
