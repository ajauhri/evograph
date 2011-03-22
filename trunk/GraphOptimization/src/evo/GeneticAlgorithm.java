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
	static final int elitism = 10;
	public int generation = 0;
	Vector<Graph> population = new Vector<Graph>();
	Graph motherGraph;

	public GeneticAlgorithm() {
		createGraph();
		initializePopulation();
	}

	public void createGraph() {
		FileToGraph fileToGraph = new FileToGraph(fileName);
		motherGraph = fileToGraph.createGraph();
	}

	public void initializePopulation() {
		for (int i = 0; i < populationSize; i++) {
			Graph individual;
			try {
				individual = (Graph) motherGraph.copy();
				initializeIndividual(individual);
				individual.calculateFitness();
				population.add(individual);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//double individualFitness = individual.fitness;
		}
		sortPopulationByFitness();
		printPopulation();
	}
	
	public Graph getFittestIndividual() {
		return population.get(0);
	}
	
	public void nextGeneration() {
		generation++;
		//Vector<Graph> newPopulation = new Vector<Graph>();
		//for(int i = 0; i < elitism; i++)
		//	newPopulation.add(i, population.get(i));
		for(int i = elitism; i < populationSize; i++) {
			Graph eliteParent =  population.get((int) (Math.random() * elitism));
			Graph parent2 = population.get(i);
			//TODO: Assign probabilty to determine recombination/mutation
			recombine(eliteParent, parent2);
			mutate(parent2);
			parent2.calculateFitness();
		}
		sortPopulationByFitness();
		printPopulation();	
	}
	
	public void mutate(Graph g) {
		Node highestCrossovers = g.getNodeAt(0);
		for (int i = 1; i < g.getNumberOfNodes(); i++) {
			if (highestCrossovers.numberOfCrossovers <= g.getNodeAt(i).numberOfCrossovers)
				highestCrossovers = g.getNodeAt(i);
		}
		int x = highestCrossovers.getX();
		int y = highestCrossovers.getY(); 
		highestCrossovers.setXandY(randomInt(-50,50)+x, randomInt(-50,50)+y);
	}
	
	public void recombine(Graph parent1, Graph parent2) {
		int crossoverPoint = (int) (Math.random() * (parent1.getNumberOfNodes() - 1)) + 1;
		for(int i = 0; i < crossoverPoint; i++) {
			Node node = parent1.getNodeAt(i);
			parent2.getNodeAt(i).setXandY(node.getX(), node.getY());
		}
		calculateEdgeLengths(parent2);
	}
	
	public void sortPopulationByFitness() {
		Collections.sort(population, new FitnessComparator());
	}

	public void initializeIndividual(Graph individual) {
		int numberOfNodes = individual.getNumberOfNodes();
		for (int i = 0; i < numberOfNodes; i++) {
			individual.getNodeAt(i).setXandY((int) (Math.random() * 400), (int) (Math.random() * 400));
		}
		calculateEdgeLengths(individual);
	}
	
	public void calculateEdgeLengths(Graph graph) {
		int numberOfNodes = graph.getNumberOfNodes();
		for (int i = 0; i < numberOfNodes; i++) {
			Node node = graph.getNodeAt(i);
			Object[] edges = node.getEdges();
			for (Object e : edges) {
				((Edge) e).computeEdgeLength(node.getX(), node.getY());
			}
		}
	}
	
	public void printPopulation() {
		for (int i = 0; i < populationSize; i++)
			System.out.println("Generation " + generation + " Graph " + i + " fitness is " + population.get(i).getFitness());
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
	
	public static int randomInt(int min, int max) {
		 return (int)Math.random()*(max-min+1) + min;
	}
}
