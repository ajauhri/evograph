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
		distanceMutationFactor = 0.5 * (distanceToCenter / nLayers);
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
		int width, height, xOffset, yOffset;
		if ((double) GraphCanvas.canvasHeight >= (double) (GraphCanvas.canvasWidth * Math.sqrt(3) / 2)) {
			width = GraphCanvas.canvasWidth;
			height = (int) (GraphCanvas.canvasWidth * Math.sqrt(3) / 2);
			xOffset = 0;
			yOffset = (GraphCanvas.canvasHeight - height) / 2;
		} else {
			height = GraphCanvas.canvasHeight;
			width = (int) (GraphCanvas.canvasHeight * 2 / Math.sqrt(3));
			yOffset = 0;
			xOffset = (GraphCanvas.canvasWidth - width) / 2;
		}
		anchorPoints[0] = new Point(xOffset, height + yOffset);
		anchorPoints[1] = new Point(xOffset + (width / 2), yOffset);
		anchorPoints[2] = new Point(xOffset + width, height + yOffset);
		distanceToCenter = width / Math.sqrt(3);
	}
	
	public GraphInstance randomIndividual() {
		GraphInstance individual = new GraphInstance(graph);
		individual.nodeInstances[0].x = anchorPoints[0].x;
		individual.nodeInstances[0].y = anchorPoints[0].y;
		
		individual.nodeInstances[1].x = anchorPoints[1].x;
		individual.nodeInstances[1].y = anchorPoints[1].y;
		
		individual.nodeInstances[2].x = anchorPoints[2].x;
		individual.nodeInstances[2].y = anchorPoints[2].y;
		
		for (int i = 0; i < Graph.nNodes; i++) {
			NodeInstance n = individual.nodeInstances[i];
			n.distanceFromAnchor = distanceToCenter * (n.id / 3) / nLayers;
			n.deltaAngle = (Math.random() * 2 * maximumDeltaAngle) - maximumDeltaAngle;
			calculateCoordsFromDistanceAndAngle(n);
		}
		return individual;
	}
	
	public void calculateCoordsFromDistanceAndAngle(NodeInstance n) {
		int anchor = n.id % 3;
		int coords[] = EvoGraph.calculateCoordinatesFromPointAngleDistance(anchorPoints[anchor].x, anchorPoints[anchor].y, referenceAngles[anchor] + n.deltaAngle, n.distanceFromAnchor);
		n.x = coords[0];
		n.y = coords[1];
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
		for (int i = 0; i < graph.nodes.length; i++) {
			double probability = Math.random() < .5 ? 0.5 : parent1.fitness / (parent1.fitness + parent2.fitness);
			if (EvoGraph.probability(probability)) {
				parent2.nodeInstances[i].x = parent1.nodeInstances[i].x;
				parent2.nodeInstances[i].y = parent1.nodeInstances[i].y;
				parent2.nodeInstances[i].distanceFromAnchor = parent1.nodeInstances[i].distanceFromAnchor;
				parent2.nodeInstances[i].deltaAngle = parent1.nodeInstances[i].deltaAngle;
			}
		}
	}
	
	public void mutate(GraphInstance individual, double distanceProbability, double angleProbability) {
		for (int i = 0; i < graph.nodes.length; i++) {
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

	public void sortPopulationByFitness() {
		Arrays.sort(population, fitnessComparator);
		//Collections.sort(population, new FitnessComparator());
	}
}
