
package algorithms.kgraph;

import java.util.Arrays;

import graph.Graph;
import graph.GraphInstance;

public class KGraphGA extends KGraphHeuristic {
	GraphInstance[] population;
	int generation = 0;
	int populationSize = 100;
	int elites = 10;
	
	public KGraphGA(Graph graph) {
		super(graph);
		population = new GraphInstance[populationSize];
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
		return population[0];
	}

	@Override
	public String displayText() {
		return "Generation " + generation + fitnessString(displayGraph());
	}

	@Override
	public void restart() {
		generation = 0;
	}

	public int getRuns() {
		return generation;
	}

	public void initializePopulation() {
		setAnchorPoints();
		for (int i = 0; i < populationSize; i++) {
			GraphInstance individual = randomIndividual();
			individual.calculateKFitness();
			population[i] = individual;
		}
		sortPopulationByFitness();
		generation++;
	}
	
	public void nextGeneration() {
		generation++;
		for (int i = elites; i < populationSize; i++) {
			GraphInstance parent1 =  population[(int) (Math.random() * elites)];
			GraphInstance parent2 = population[i];
			recombineK(parent1, parent2);
			mutate(parent2, 0.2, 0.4);
			//simpleMutate(child, 0.01);
			//gaussianMutate(child, 0.05);
			//child.centerGraph();
			//population.set(i, child);
			parent2.calculateKFitness();
		}
		sortPopulationByFitness();
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
						parent2.nodeInstances[j].age = this.generation;
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
				}
			}
		}
	}
	
	public void mutate(GraphInstance individual, double distanceProbability, double angleProbability) {
		if(Graph.nNodes % 3 == 0) {
			for (int i = 1; i < graph.nodes.length / 3; i++) {
				boolean mutated = false;
				if (Math.random() < distanceProbability / ((nLayers - i) * ageScaleFactor * (this.generation - individual.nodeInstances[i * 3].age))) {
					for (int j = i * 3; j < (i + 1) * 3; j++) {
						individual.nodeInstances[j].distanceFromAnchor += (rand.nextGaussian() * distanceMutationFactor / (nLayers - i));
						individual.nodeInstances[j].age = this.generation;
						mutated = true;
					}
				}
				if (Math.random() < angleProbability / ((nLayers - i) * ageScaleFactor * (this.generation - individual.nodeInstances[i * 3].age))) {
					for (int j = i * 3; j < (i + 1) * 3; j++) {
						individual.nodeInstances[j].deltaAngle += (rand.nextGaussian() * angleMutationFactor / (nLayers - i));
						individual.nodeInstances[j].age = this.generation;
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

	public void sortPopulationByFitness() {
		Arrays.sort(population, fitnessComparator);
		//Collections.sort(population, new FitnessComparator());
	}
}
