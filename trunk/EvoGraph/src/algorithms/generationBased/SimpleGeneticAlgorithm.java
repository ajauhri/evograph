package algorithms.generationBased;

import graph.Graph;
import graph.GraphInstance;
import algorithms.IncrementalGraphAlgorithm;

import java.util.Collections;
import java.util.Vector;

import utils.DeepCopy;

public class SimpleGeneticAlgorithm extends GenerationBasedAlgorithm implements IncrementalGraphAlgorithm {
	public Vector<GraphInstance> population;
	public Vector<GraphInstance> tempPopulation;

	public SimpleGeneticAlgorithm(Graph graph) {
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
	public GraphInstance displayGraph() {
		return population.get(0);
	}

	@Override
	public String displayText() {
		GraphInstance fittest = (GraphInstance) displayGraph();
		return "Generation " + generation + fitnessString(fittest);
	}

	@Override
	public void updateGraph() {
		((GraphInstance) displayGraph()).calculateFitness();
	}
	
	public void nextGeneration() {
		generation++;
		Vector<GraphInstance> individuals = new Vector<GraphInstance>();
		tempPopulation = new Vector<GraphInstance>();
		int elites = (int) (populationSize * elitism);
		copyEliteIndividuals(elites);
		
		for (int i = elites; i < populationSize; i++) {
			individuals = tour();
			GraphInstance child = recombine(individuals.get(0), individuals.get(1));
			simpleMutate(child, 0.01);
			gaussianMutate(child, 0.05);
			child.centerGraph();
			tempPopulation.add(child);
			child.calculateFitness();
		}
		population.removeAllElements();
		population = tempPopulation;
		sortPopulationByFitness();
	}
	
	public Vector<GraphInstance> tour() {
		Vector<GraphInstance> individuals = new Vector<GraphInstance>();
		for (int i = 0; i < tournamentSize; i++)
			individuals.add(population.get((int) (Math.random() * populationSize)));
		Collections.sort(individuals, new FitnessComparator());
		return individuals;
	}
	
	public void copyEliteIndividuals(int elite_count) {
		for (int i = 0; i < elite_count; i++)
			tempPopulation.add((GraphInstance) DeepCopy.copy(population.get(i)));
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
