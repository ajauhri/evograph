
package algorithms.kgraph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;



import evograph.EvoGraph;
import graph.GraphInstance;
import graph.Graph;

public class KGraphALPS extends KGraphHeuristic {
	public ArrayList<Vector<GraphInstance>> population;
	int populationSize = 100;
	double elitism = 0.1;
	final int age_gap_factor = 20;
	ArrayList<Integer> fibonacciList;
	public int layerCount;
	int generation;
	
	public KGraphALPS(Graph graph) {
		super(graph);
		restart();
	}
	
	

	@Override
	public void restart() {
		generation = 0;
		layerCount = 1;
		population = new ArrayList<Vector<GraphInstance>>();
		population.add(new Vector<GraphInstance>());
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
		GraphInstance fittest = (GraphInstance) displayGraph();
		return "Generation " + generation + fitnessString(fittest);
	}

	@Override
	public void updateGraph() {
		((GraphInstance) displayGraph()).calculateFitness();
	}
	
	public void initializeLayerZeroPopulation() {
		setAnchorPoints();
		Vector<GraphInstance> layerPopulation = new Vector<GraphInstance>();
		
		for (int i = 0; i < populationSize; i++) {
			GraphInstance individual = randomIndividual();
			individual.age = 0;
			individual.calculateKFitness();
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
				GraphInstance parent1 = population.get(layerNumber).get(iterator);
				GraphInstance parent2;
				
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
				recombineK(parent1, parent2);

				/*** mutation steps ***/
				mutate(parent2, 0.2, 0.4);
				
				/***other steps of sanity***/
				parent2.calculateKFitness();
			}
			sortPopulationByFitness(layerNumber);
		}
	
		//System.out.println("Average fitness: " + calculateAverageFitness());
	}
	
	public void upgradeIndividuals() {
		
		for (int layerNumber = layerCount - 1; layerNumber > 0; layerNumber--) {
			Vector<GraphInstance> layerPopulation = population.get(layerNumber);
			
			/***
			 * get age_limit based on the fibonacci series. For example, a
			 * fib series of {1, 2, 3 , 5, 8...} will have age-limits of {1g,
			 * 2g, 3g, 5g, 8g...} where g is the age_gap factor being a constant
			 ***/
			int fibonacciFactor = fibonacciList.get(layerNumber);
			
			int layer_age_limit = age_gap_factor * fibonacciFactor;
			
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
	
	
	public void recombineK(GraphInstance parent1, GraphInstance parent2) {
		if(Graph.nNodes % 3 == 0) {
			for (int i = 1; i < graph.nodes.length / 3; i++) {
				if (Math.random() < parent1.fitness / (parent1.fitness + parent2.fitness)) {
					for (int j = i * 3; j < (i + 1) * 3; j++) {
						parent2.nodeInstances[j].setRealX(parent1.nodeInstances[j].realX);
						parent2.nodeInstances[j].setRealY(parent1.nodeInstances[j].realY);
						parent2.nodeInstances[j].distanceFromAnchor = parent1.nodeInstances[j].distanceFromAnchor;
						parent2.nodeInstances[j].deltaAngle = parent1.nodeInstances[j].deltaAngle;
						parent2.nodeInstances[j].age = (parent1.age < parent2.age) ? parent2.age : parent1.age;
					}
				}
			}
		} else {
			for (int i = 3; i < graph.nodes.length; i++) {
				if (Math.random() < (parent1.fitness / (parent1.fitness + parent2.fitness))) {
					parent2.nodeInstances[i].setRealX(parent1.nodeInstances[i].realX);
					parent2.nodeInstances[i].setRealY(parent1.nodeInstances[i].realY);
					parent2.nodeInstances[i].distanceFromAnchor = parent1.nodeInstances[i].distanceFromAnchor;
					parent2.nodeInstances[i].deltaAngle = parent1.nodeInstances[i].deltaAngle;
					parent2.nodeInstances[i].age = (parent1.age < parent2.age) ? parent2.age : parent1.age;
				}
			}
		}
	}
	
	public void mutate(GraphInstance individual, double distanceProbability, double angleProbability) {
		if(Graph.nNodes % 3 == 0) {
			for (int i = 1; i < graph.nodes.length / 3; i++) {
				boolean mutated = false;
				if (Math.random() < distanceProbability) {
					for (int j = i * 3; j < (i + 1) * 3; j++) {
						individual.nodeInstances[j].distanceFromAnchor += (rand.nextGaussian() * distanceMutationFactor);
						mutated = true;
					}
				}
				if (Math.random() < angleProbability ) {
					for (int j = i * 3; j < (i + 1) * 3; j++) {
						individual.nodeInstances[j].deltaAngle += (rand.nextGaussian() * angleMutationFactor);
						mutated = true;
					}
				}
				if (mutated) {
					for (int j = i * 3; j < (i + 1) * 3; j++) {
						calculateCoordsFromDistanceAndAngle(individual.nodeInstances[j]);
					}
				}
			}
		} else {
			for (int i = 3; i < graph.nodes.length; i++) {
				boolean mutated = false;
				if (Math.random() < distanceProbability) {
					individual.nodeInstances[i].distanceFromAnchor += (rand.nextGaussian() * distanceMutationFactor);
					mutated = true;
				}
				if (Math.random() < angleProbability) {
					individual.nodeInstances[i].deltaAngle += (rand.nextGaussian() * angleMutationFactor);
					mutated = true;
				}
				if (mutated)
					calculateCoordsFromDistanceAndAngle(individual.nodeInstances[i]);
			}
		}
	}


	public void putIndividualInSucceedingLayer(GraphInstance individual, int layerNumber) {
		Vector<GraphInstance> layerPopulation = population.get(layerNumber);
		int layerSize = layerPopulation.size() - 1;
		if (layerSize < 0)
			layerPopulation.add(individual);
		else if (layerPopulation.get(layerSize).fitness > individual.fitness) {
			layerPopulation.set(layerSize, individual);
			sortPopulationByFitness(layerNumber);
		}
	}
		
	public void updateFibonacciList() {	
		fibonacciList.add(fibonacciList.get(layerCount - 1) + fibonacciList.get(layerCount - 2));		
	}

	public void incrementPopulationAge() {
		for (int layerNumber = 1; layerNumber <= layerCount; layerNumber++) {
		Vector<GraphInstance> layerPopulation = population.get(layerNumber);
		for (int iterator = 0; iterator < layerPopulation.size(); iterator++)
			layerPopulation.get(iterator).age += 1;
		}
	}
	
	
	public void sortPopulationByFitness(int layerNumber) {
		Collections.sort(population.get(layerNumber), new FitnessComparator());
	}
}
