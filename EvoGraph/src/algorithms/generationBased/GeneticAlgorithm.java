package algorithms.generationBased;

import java.util.Collections;
import java.util.Vector;

import algorithms.IncrementalGraphAlgorithm;
import evograph.GraphCanvas;
import graph.GraphInstance;
import graph.Graph;
import graph.NodeInstance;

public class GeneticAlgorithm extends GenerationBasedAlgorithm implements IncrementalGraphAlgorithm {
	public Vector<GraphInstance> population;

	public GeneticAlgorithm(Graph graph) {
		super(graph);
		population = new Vector<GraphInstance>();
	}

	@Override
	public void restart() {
		generation = 0;
		population.removeAllElements();
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
		GraphInstance fittest = (GraphInstance) displayGraph();
		return "Generation " + generation + fitnessString(fittest);
	}

	@Override
	public void updateGraph() {
		//((GGraph) graph).calculateFitness();
		((GraphInstance) displayGraph()).calculateFitness();
	}
	
	public void nextGeneration() {
		generation++;
		int elites = (int) (populationSize * elitism);
		for (int i = elites; i < populationSize; i++) {
			GraphInstance parent1 =  population.get((int) (Math.random() * elites));
			GraphInstance parent2 = population.get(i);
			GraphInstance child = recombine(parent1, parent2);
			simpleMutate(child, 0.01);
			gaussianMutate(child, 0.05);
			child.centerGraph();
			population.set(i, child);
			//child.calculateFitness();
			child.calculateKFitness();
		}
		sortPopulationByFitness();
		//System.out.println("Average fitness: " + calculateAverageFitness());
	}
	
	public void initializePopulation() {
		for (int i = 0; i < populationSize; i++) {
			GraphInstance individual = randomIndividual();
			individual.calculateFitness();
			population.add(individual);
		}
		sortPopulationByFitness();
		generation++;
	}
	
	public GraphInstance randomIndividual() {
		GraphInstance individual = new GraphInstance(graph);
		for (NodeInstance n : individual.nodeInstances) {
			n.setRealX((int) (100 * Math.random() * GraphCanvas.canvasWidth)); //initialize with random x, y
			n.setRealY((int) (100 * Math.random() * GraphCanvas.canvasHeight));
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
}
