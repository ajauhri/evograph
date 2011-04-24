package ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import evograph.EvoGraph;
import evograph.IncrementalGraphAlgorithm;
import evograph.Operators;
import graph.Graph;
import graph.GraphInstance;

public class ALPS extends Operators implements IncrementalGraphAlgorithm {
	
	public ArrayList<Vector<GGraph>> population;
	static final int population_per_layer = 100;
	static final int age_gap_factor = 20;
	static final double elitism = 0.1;
	public int generation = 0;
	static List<Integer> fibonacciList;
	public static int layerCount = 1;
	
	public ALPS(Graph graph) {
		super(graph);
		population = new ArrayList<Vector<GGraph>>();
		population.add(new Vector<GGraph>());
		fibonacciList = new ArrayList<Integer>();
		fibonacciList.add(0, 1);
		fibonacciList.add(1, 1);
	}

	@Override
	public void next() {
		if (generation == 0)
			initializeLayerZeroPopulation();
		else if (generation % (age_gap_factor * fibonacciList.get(layerCount)) == 0) {
			initializeLayerZeroPopulation();
			layerCount += 1;
			/*** add fibonacci number fib(n+1) = fib(n) + fib(n-1) ***/
			updateFibonacciList();
			upgradeIndividuals();
			nextGeneration();
		} else {
			upgradeIndividuals();
			nextGeneration();
		}
		incrementPopulationAge();
		generation++;
	}

	@Override
	public GraphInstance displayGraph() {
		return population.get(layerCount).firstElement();
	}

	@Override
	public String displayText() {
		GGraph fittest = (GGraph) displayGraph();
		return "Generation " + generation + fitnessString(fittest);
	}

	@Override
	public void updateGraph() {
		((GGraph) displayGraph()).calculateFitness();
	}
	
	public void initializeLayerZeroPopulation() {
		Vector<GGraph> layerPopulation = new Vector<GGraph>();
		
		for (int i = 0; i < population_per_layer; i++) {
			GGraph individual = randomIndividual();
			individual.age = 0;
			individual.calculateFitness();
			layerPopulation.add(individual);
		}
		
		/*** put a new layer. All other layers will automatically >> by 1 (ArrayList) ***/
		population.add(1, layerPopulation);
		
		sortPopulationByFitness(1);
	}

	
	public void nextGeneration() {
		for (int layerNumber = layerCount; layerNumber > 0; layerNumber--) {
			int elites = (int) (elitism * population.get(layerNumber).size());
			/*** perform recombinations and mutations for the non-elite population ***/
			for (int iterator = elites ; iterator < population.get(layerNumber).size() - elitism; iterator++) {
				GGraph parent1 = population.get(layerNumber).get(iterator);
				GGraph parent2, child;
				
				/*** recombination step ***/
				/*** choose an elite parent either from nth layer, or (n-1)th layer with either probability of 0.5 ***/
				if (EvoGraph.probability(0.5)) 
					parent2 = population.get(layerNumber).get((int) (Math.random() * elites));
				else {
					try {
						parent2 = population.get(layerNumber - 1).get((int) (Math.random() * elites));
					} catch (Exception e) {
						parent2 = population.get(layerNumber).get((int) (Math.random() * elites));
					}
				}
				child = recombine(parent1, parent2);
				
				/*** mutation steps ***/
				simpleMutate(child, 0.01);
				gaussianMutate(child, 0.05);
				
				/***other steps of sanity***/
				child.centerGraph();
				child.calculateFitness();
				population.get(layerNumber).set(iterator, child);
			}
			sortPopulationByFitness(layerNumber);
		}
	
		//System.out.println("Average fitness: " + calculateAverageFitness());
	}
	
	public void upgradeIndividuals() {
		
		for (int layerNumber = layerCount - 1; layerNumber > 0; layerNumber--) {
			Vector<GGraph> layerPopulation = population.get(layerNumber);
			
			/***
			 * get age_limit based on the fibonacci series. For example, a
			 * fib series of {1, 2, 3 , 5, 8...} will have age-limits of {1k,
			 * 2k, 3k, 5k, 8k...} where k is the age_gap factor being a constant
			 ***/
			int fibonacciFactor = fibonacciList.get(layerNumber);
			
			int layer_age_limit = ALPS.age_gap_factor * fibonacciFactor;
			
			for (int iterator = 0; iterator < layerPopulation.size(); iterator++) {
				if (layerPopulation.get(iterator).age > layer_age_limit) {
					/***
					 * check whether it can replace the worse individual in a
					 * layer above it
					 ***/
					putIndividualInSucceedingLayer(layerPopulation.get(iterator), layerNumber + 1);
					layerPopulation.remove(iterator);
						/***
						 * ArrayList automatically shifts indexes of elements to the left
						 * by 1, hence the increment operand of the `for`
						 * needs to be reduced by 1 to avoid it from skipping an
						 * index
						 **/
					iterator--;
					
				}
			}
		}
	}


	public void putIndividualInSucceedingLayer(GGraph individual, int layerNumber) {
		Vector<GGraph> layerPopulation = population.get(layerNumber);
		int layerSize = layerPopulation.size() - 1;
		if (layerSize < 0)
			layerPopulation.add(individual);
		else if (layerPopulation.get(layerSize).fitness > individual.fitness) {
			layerPopulation.set(layerSize, individual);
			sortPopulationByFitness(layerNumber);
		}
	}
	
	public GGraph recombine(GGraph parent1, GGraph parent2) {
		GGraph child = new GGraph(graph);
		/*** offspring inherits the age of of oldest parent ***/
		child.age = (parent1.age < parent2.age) ? parent2.age : parent1.age;
		for (int i = 0; i < graph.nodes.length; i++) {
			double probability = EvoGraph.probability(.5) ? 0.5 : parent1.fitness / (parent1.fitness + parent2.fitness);
			if (EvoGraph.probability(probability)) {
				child.nodeInstances[i].x = parent1.nodeInstances[i].x;
				child.nodeInstances[i].y = parent1.nodeInstances[i].y;
			} else {
				child.nodeInstances[i].x = parent2.nodeInstances[i].x;
				child.nodeInstances[i].y = parent2.nodeInstances[i].y;	
			}
		}
		return child;
	}

		
	public static void updateFibonacciList() {
		fibonacciList.add(fibonacciList.get(layerCount - 1) + fibonacciList.get(layerCount - 2));		
	}

	public void incrementPopulationAge() {
		for (int layerNumber = 1; layerNumber <= layerCount; layerNumber++) {
		Vector<GGraph> layerPopulation = population.get(layerNumber);
		for (int iterator = 0; iterator < layerPopulation.size(); iterator++)
			layerPopulation.get(iterator).age += 1;
		}
	}
	
	
	public void sortPopulationByFitness(int layerNumber) {
		Collections.sort(population.get(layerNumber), new FitnessComparator());
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
