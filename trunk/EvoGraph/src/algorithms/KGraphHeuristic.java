package algorithms;

import java.awt.Point;
import java.util.Arrays;
import java.util.Random;

import evograph.EvoGraph;
import evograph.GraphCanvas;

import algorithms.generationBased.GenerationBasedAlgorithm;

import graph.Graph;
import graph.GraphInstance;
import graph.NodeInstance;

public class KGraphHeuristic extends GenerationBasedAlgorithm implements IncrementalGraphAlgorithm {
	GraphInstance[] population;
	int nLayers;
	Point anchorPoints[];
	double distanceToCenter;
	double referenceAngles[] = {Math.PI / 3, Math.PI, 5 * Math.PI / 3};
	double maximumDeltaAngle = Math.PI / 18;
	double angleMutationFactor;
	double distanceMutationFactor;
	FitnessComparator fitnessComparator; 
	int elites;
	Random rand;
	
	public KGraphHeuristic(Graph graph) {
		super(graph);
		elites = (int) (populationSize * elitism);
		rand = new Random();
		anchorPoints = new Point[3];
		nLayers = (graph.nodes.length - 1) / 3 + 1;
		angleMutationFactor = 0.5 * maximumDeltaAngle;
		fitnessComparator = new FitnessComparator();
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
	public void updateGraph() {
		displayGraph().calculateFitness();
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
	
	public void setAnchorPoints() {
		double width, height, xOffset, yOffset;
		if ((double) GraphCanvas.canvasHeight >= (double) (GraphCanvas.canvasWidth * Math.sqrt(3) / 2)) {
			width = GraphCanvas.canvasWidth;
			height = GraphCanvas.canvasWidth * Math.sqrt(3) / 2;
			xOffset = 0;
			yOffset = (GraphCanvas.canvasHeight - height) / 2;
		} else {
			height = GraphCanvas.canvasHeight;
			width = GraphCanvas.canvasHeight * 2 / Math.sqrt(3);
			yOffset = 0;
			xOffset = (GraphCanvas.canvasWidth - width) / 2;
		}
		anchorPoints[0] = new Point((int) (100 * xOffset), (int) (100 * (height + yOffset)));
		anchorPoints[1] = new Point((int) (100 * (xOffset + (width / 2))), (int) (100 * yOffset));
		anchorPoints[2] = new Point((int) (100 * (xOffset + width)), (int) (100 * (height + yOffset)));
		distanceToCenter = 100 * width / Math.sqrt(3);
		distanceMutationFactor = 0.1 * (distanceToCenter / nLayers);
	}
	
	public GraphInstance randomIndividual() {
		GraphInstance individual = new GraphInstance(graph);
		individual.nodeInstances[0].setRealX(anchorPoints[0].x);
		individual.nodeInstances[0].setRealY(anchorPoints[0].y);
		individual.nodeInstances[1].setRealX(anchorPoints[1].x);
		individual.nodeInstances[1].setRealY(anchorPoints[1].y);
		individual.nodeInstances[2].setRealX(anchorPoints[2].x);
		individual.nodeInstances[2].setRealY(anchorPoints[2].y);
		if(Graph.nNodes % 3 == 0) {
			for (int i = 0; i < Graph.nNodes / 3; i++) {
				double distanceFromAnchor = distanceToCenter * i / nLayers;
				double deltaAngle = (Math.random() - 0.5) * 2 * maximumDeltaAngle;
				for(int j = i * 3; j < (i + 1) * 3; j++) {
					NodeInstance n = individual.nodeInstances[j];
					n.distanceFromAnchor = distanceFromAnchor;
					n.deltaAngle = deltaAngle;
					calculateCoordsFromDistanceAndAngle(n);
				}
			}
		} else {
			for (int i = 3; i < Graph.nNodes; i++) {
				NodeInstance n = individual.nodeInstances[i];
				n.distanceFromAnchor = distanceToCenter * (n.id / 3) / nLayers;
				n.deltaAngle = (Math.random() - 0.5) * 2 * maximumDeltaAngle;
				calculateCoordsFromDistanceAndAngle(n);
			}
		}
		return individual;
	}
	
	public void calculateCoordsFromDistanceAndAngle(NodeInstance n) {
		int anchor = n.id % 3;
		int coords[] = EvoGraph.calculateCoordinatesFromPointAngleDistance(anchorPoints[anchor].x, anchorPoints[anchor].y, referenceAngles[anchor] + n.deltaAngle, n.distanceFromAnchor);
		n.setRealX(coords[0]);
		n.setRealY(coords[1]);
	}
	
	public void nextGeneration() {
		generation++;
		for (int i = elites; i < populationSize; i++) {
			GraphInstance parent1 =  population[(int) (Math.random() * elites)];
			GraphInstance parent2 = population[i];
			recombineK(parent1, parent2);
			mutate(parent2, 0.1, 0.05);
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
				double probability = Math.random() < .5 ? 0.5 : parent1.fitness / (parent1.fitness + parent2.fitness);
				if (EvoGraph.probability(probability)) {
					for (int j = i * 3; j < (i + 1) * 3; j++) {
						parent2.nodeInstances[j].setRealX(parent1.nodeInstances[j].realX);
						parent2.nodeInstances[j].setRealY(parent1.nodeInstances[j].realY);
						parent2.nodeInstances[j].distanceFromAnchor = parent1.nodeInstances[j].distanceFromAnchor;
						parent2.nodeInstances[j].deltaAngle = parent1.nodeInstances[j].deltaAngle;
					}
				}
			}
		} else {
			for (int i = 3; i < graph.nodes.length; i++) {
				double probability = Math.random() < .5 ? 0.5 : parent1.fitness / (parent1.fitness + parent2.fitness);
				if (EvoGraph.probability(probability)) {
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
				if (Math.random() < distanceProbability) {
					for (int j = i * 3; j < (i + 1) * 3; j++) {
						individual.nodeInstances[j].distanceFromAnchor += (rand.nextGaussian() * distanceMutationFactor);
						mutated = true;
					}
				}
				if (Math.random() < angleProbability) {
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

	public void sortPopulationByFitness() {
		Arrays.sort(population, fitnessComparator);
		//Collections.sort(population, new FitnessComparator());
	}
}
