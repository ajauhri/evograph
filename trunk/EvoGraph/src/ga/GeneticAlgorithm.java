package ga;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import evograph.EvoGraph;
import evograph.GraphCanvas;
import evograph.IncrementalGraphAlgorithm;
import graph.Graph;
import graph.GraphInstance;
import graph.NodeInstance;

public class GeneticAlgorithm implements IncrementalGraphAlgorithm {
	Graph graph;
	static final int populationSize = 100;
	static final int elitism = 10;
	public int generation = 0;
	public Vector<GGraph> population;

	public GeneticAlgorithm(Graph graph) {
		this.graph = graph;
		population = new Vector<GGraph>();
	}
	
	@Override
	public void next() {
		if(generation == 0)
			initializePopulation();
		else
			nextGeneration();
	}

	@Override
	public GraphInstance displayGraph() { //Return fittest individual
		return population.get(0);
	}

	@Override
	public String displayText() {
		GGraph fittest = (GGraph) displayGraph();
		return "Generation " + generation +
				"\t\tF: " + String.format("%.2f", fittest.fitness) + 
				"\t\t#EC: " + fittest.numberOfEdgeCrossings +
				"\t\tEF: " + String.format("%.2f", fittest.edgeFitness) +
				"\t\tAR: " + String.format("%.2f", fittest.angularResolution) +
				"\t\tNT: " + String.format("%.2f", fittest.nodeTunneling);
	}

	@Override
	public void updateGraph() {
		//((GGraph) graph).calculateFitness();
		((GGraph) displayGraph()).calculateFitness();
	}
	
	public void nextGeneration() {
		generation++;
		for (int i = elitism; i < populationSize - elitism; i++) {
			GGraph parent1 =  population.get((int) (Math.random() * elitism));
			GGraph parent2 = population.get(i);
			GGraph child = mutate(recombine(parent1, parent2));
			child.centerGraph();
			population.set(i, child);
			child.calculateFitness();
		}
		for (int i = populationSize - elitism; i < populationSize; i++) {
			GGraph child = randomIndividual();
			child.centerGraph();
			population.set(i, child);
			child.calculateFitness();
		}
		sortPopulationByFitness();
		//System.out.println("Average fitness: " + calculateAverageFitness());
	}
	
	public GGraph recombine(GGraph parent1, GGraph parent2) {
		GGraph child = new GGraph(graph);
		for (int i = 0; i < graph.nodes.length; i++) {
			if (EvoGraph.probability(parent1.fitness / (parent1.fitness + parent2.fitness))) {
				child.nodeInstances[i].x = parent1.nodeInstances[i].x;
				child.nodeInstances[i].y = parent1.nodeInstances[i].y;
			} else {
				child.nodeInstances[i].x = parent2.nodeInstances[i].x;
				child.nodeInstances[i].y = parent2.nodeInstances[i].y;	
			}
		}
		return child;
	}
	
	public GGraph mutate(GGraph individual) {
		int canvasWidth = GraphCanvas.canvasWidth;
		int canvasHeight = GraphCanvas.canvasHeight;
		for (int i = 0; i < graph.nodes.length; i++) {
			if (EvoGraph.probability(.01)) {
				individual.nodeInstances[i].x = (int) (Math.random() * canvasWidth);
				individual.nodeInstances[i].y = (int) (Math.random() * canvasHeight);
			}
		}
		return individual;
	}
	
	public void initializePopulation() {
		for (int i = 0; i < populationSize; i++) {
			GGraph individual = randomIndividual();
			individual.calculateFitness();
			population.add(individual);
		}
		sortPopulationByFitness();
		generation++;
	}
	
	public GGraph randomIndividual() {
		GGraph individual = new GGraph(graph);
		for (NodeInstance n : individual.nodeInstances) {
			n.x = (int) (Math.random() * GraphCanvas.canvasWidth); //initialize with random x, y
			n.y = (int) (Math.random() * GraphCanvas.canvasHeight);
		}
		return individual;
	}
	
	public double calculateAverageFitness() {
		double sumOfFitnesses = 0;
		for (int i = 0; i < populationSize; i++) {
			sumOfFitnesses += population.get(i).fitness;
		}
		return sumOfFitnesses / populationSize;
	}
	
	public void sortPopulationByFitness() {
		Collections.sort(population, new FitnessComparator());
	}

	class FitnessComparator implements Comparator<GGraph> {
		public int compare(GGraph g1, GGraph g2) {
			if (g2.fitness == g1.fitness)
				return 0;
			else if (g2.fitness > g1.fitness)
				return -1;
			else
				return 1;
		}
	}
}
