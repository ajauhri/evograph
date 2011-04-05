package evo;

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
		//printPopulation();
	}
	
	public Graph getFittestIndividual() {
		System.out.print(" Best individual: "+ population.get(0).fitness);
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
			parent2.calculateFitness(); //Needs to calculate fitness in order to have an effective heuristic
			mutate(parent2);
			parent2.calculateFitness();
		}
		sortPopulationByFitness();
		System.out.println("Average fitness: " + calculateAverageFitness());
		//printPopulation();
	}
	
	public void mutate(Graph g) {
		Node nodeWithHighestCrossoverRatio = g.getNodeWithHighestCrossoverRatio();
		int x = nodeWithHighestCrossoverRatio.getX();
		int y = nodeWithHighestCrossoverRatio.getY(); 
		nodeWithHighestCrossoverRatio.setXandY(randomInt(-50,50)+x, randomInt(-50,50)+y);
	}
	
	public void recombine(Graph parent1, Graph parent2) {
		int numberOfNodes = parent1.numberOfNodes;
		for (int i = 0; i < numberOfNodes; i++) {
			Node node1 = parent1.getNodeAt(i);
			Node node2 = parent2.getNodeAt(i);
			Node node = selectFittestNode(node1, node2);
			parent2.getNodeAt(i).setXandY(node.getX(), node.getY());
		}
//		int crossoverPoint = (int) (Math.random() * (parent1.numberOfNodes - 1)) + 1;
//		for(int i = 0; i < crossoverPoint; i++) {
//			Node node = parent1.getNodeAt(i);
//			parent2.getNodeAt(i).setXandY(node.getX(), node.getY());
//		}
	}
	
	public Node selectFittestNode(Node node1, Node node2) {
		double random = Math.random() * (node1.fitness + node2.fitness);
		if (random < node1.fitness)
			return node2;
		else
			return node1;
	}
	
	public void sortPopulationByFitness() {
		Collections.sort(population, new FitnessComparator());
	}
	
	public double calculateAverageFitness() {
		double sumOfFitnesses = 0;
		for(int i = 0; i < populationSize; i++) {
			sumOfFitnesses += population.get(i).fitness;
		}
		return sumOfFitnesses / populationSize;
	}

	public void initializeIndividual(Graph individual) {
		int numberOfNodes = individual.numberOfNodes;
		for (int i = 0; i < numberOfNodes; i++) {
			individual.getNodeAt(i).setXandY((int) (Math.random() * 400), (int) (Math.random() * 400));
		}
		individual.calculateEdgeLengths();
	}
	
	public void printPopulation() {
		for (int i = 0; i < populationSize; i++)
			System.out.println("Generation " + generation + " Graph " + i + " fitness is " + population.get(i).fitness);
	}
	
	class FitnessComparator implements Comparator<Graph> {
		public int compare(Graph g1, Graph g2) {
			if (g2.fitness == g1.fitness)
				return 0;
			else if (g2.fitness > g1.fitness)
				return -1;
			else
				return 1;
		}
	}
	
	public static int randomInt(int min, int max) {
		 return (int)Math.random()*(max-min+1) + min;
	}
}
