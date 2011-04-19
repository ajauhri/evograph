package sa;

import evograph.EvoGraph;
import evograph.GraphCanvas;
import evograph.IncrementalGraphAlgorithm;
import ga.GGraph;
import graph.Graph;
import graph.GraphInstance;
import graph.NodeInstance;
import java.util.Random;

public class SimulatedAnnealing implements IncrementalGraphAlgorithm {
	Graph graph;
	double temperature = 0.0;
	int iterations = 0;
	int num_iterations_with_constant_fitness = 0;
	double previous_parent_fitness = 0.0;
	double mutationProbability = 0.001;

	public GGraph parentGraph;
	public GGraph childGraph;
	// double best_yet_fitness = 0.0;
	public static final double initial_temperature = 250.0;
	public static final int iterations_per_temperature_change = 10000;
	public static final double temperature_factor = 0.98;
	public static final double converged_iterations = 50000;

	public SimulatedAnnealing(Graph graph) {
		this.graph = graph;
	}

	@Override
	public void next() {
		if (iterations == 0)
			initializeIndividual();
		else
			nextIndividual();
	}

	@Override
	public GraphInstance displayGraph() {
		return parentGraph;
	}

	@Override
	public String displayText() {
		GGraph fittest = (GGraph) displayGraph();
		return "Iteration " + iterations + "\t\tF: "
				+ String.format("%.2f", fittest.fitness) + "\t\t#EC: "
				+ fittest.numberOfEdgeCrossings + "\t\tEF: "
				+ String.format("%.2f", fittest.edgeFitness) + "\t\tAR: "
				+ String.format("%.2f", fittest.angularResolution) + "\t\tNT: "
				+ String.format("%.2f", fittest.nodeTunneling);
	}

	public void initializeIndividual() {
		parentGraph = randomIndividual();
		parentGraph.centerGraph();
		parentGraph.calculateFitness();
		copyParent();
		childGraph.calculateFitness();

		temperature = SimulatedAnnealing.initial_temperature;
		// best_yet_fitness = parentGraph.fitness;
		iterations++;
	}

	public void nextIndividual() {
		iterations++;
		previous_parent_fitness = parentGraph.fitness;
		mutate(childGraph);
		childGraph.centerGraph();
		childGraph.calculateFitness();
		
		if (childGraph.fitness < parentGraph.fitness) {
			System.out.println("mutation worked in Iteration " + iterations);	
			swapParentAndChildGraphs();
		}

		else {
			double delta_fitness = childGraph.fitness - parentGraph.fitness;
			if (EvoGraph.probability(Math.exp((-1.0 * delta_fitness) 
					/ temperature))) {
				System.out.println("IT HAPPENED in Iteration " + iterations);
				System.out.println("Mutation probability = " + mutationProbability);
				swapParentAndChildGraphs();
			}
		}

		if (iterations % SimulatedAnnealing.iterations_per_temperature_change == 0)
			temperature = temperature * SimulatedAnnealing.temperature_factor;

		if (num_iterations_with_constant_fitness > SimulatedAnnealing.converged_iterations && mutationProbability <= 1.0){
			mutationProbability = 1.5 * mutationProbability;
		}

		if (parentGraph.fitness == previous_parent_fitness)
			num_iterations_with_constant_fitness++;
		else
			num_iterations_with_constant_fitness = 0;

	}

	public void swapParentAndChildGraphs() {
		GGraph temp = parentGraph;
		parentGraph = childGraph;
		childGraph = temp;
	}

	public void mutate(GGraph individual) {
		Random rand = new Random();
		int canvasWidth = GraphCanvas.canvasWidth;
		int canvasHeight = GraphCanvas.canvasHeight;
		for (int i = 0; i < graph.nodes.length; i++) {
			if (EvoGraph.probability(mutationProbability)) {
				individual.nodeInstances[i].x = EvoGraph.boundaryChecker(individual.nodeInstances[i].x + (int) (rand.nextGaussian() * (canvasWidth / graph.nodes.length)), canvasWidth);
				individual.nodeInstances[i].y = EvoGraph.boundaryChecker(individual.nodeInstances[i].y + (int) (rand.nextGaussian() * (canvasHeight/ graph.nodes.length)), canvasHeight);
				
			}
		}
	}
	
	public void copyParent() {
		childGraph = new GGraph(graph);
		for (int i = 0; i < childGraph.nodeInstances.length; i++) {
			childGraph.nodeInstances[i].x = parentGraph.nodeInstances[i].x;
			childGraph.nodeInstances[i].y = parentGraph.nodeInstances[i].y;
		}
	}

	public GGraph randomIndividual() {
		GGraph individual = new GGraph(graph);
		for (NodeInstance n : individual.nodeInstances) {
			n.x = (int) (Math.random() * GraphCanvas.canvasWidth); // initialize
																	// with
																	// random x,
																	// y
			n.y = (int) (Math.random() * GraphCanvas.canvasHeight);
		}
		return individual;
	}

}
