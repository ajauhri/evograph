package hc;

import ga.GGraph;
import graph.Graph;
import graph.GraphInstance;
import evograph.IncrementalGraphAlgorithm;
import evograph.Operators;

public class HillClimber extends Operators implements IncrementalGraphAlgorithm {
	int iterations = 0;
	int num_iterations_with_constant_fitness = 0;
	double previous_parent_fitness = 0.0;
	double mutationProbability = 0.05;

	public GGraph parentGraph;
	public GGraph childGraph;
	// double best_yet_fitness = 0.0;
	public static final double converged_iterations = 50000;

	public HillClimber(Graph graph) {
		 super(graph);
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
		return "Iteration " + iterations + fitnessString(fittest);
	}

	@Override
	public void updateGraph() {
		parentGraph.calculateFitness();
	}

	public void initializeIndividual() {
		parentGraph = randomIndividual();
		parentGraph.centerGraph();
		parentGraph.calculateFitness();
		childGraph = copyGGraph(parentGraph);
		childGraph.calculateFitness();

		// best_yet_fitness = parentGraph.fitness;
		iterations++;
	}

	public void nextIndividual() {
		iterations++;
		previous_parent_fitness = parentGraph.fitness;
		childGraph = copyGGraph(parentGraph);
		gaussianMutate(childGraph, mutationProbability);
		childGraph.centerGraph();
		childGraph.calculateFitness();
		
		if (childGraph.fitness < parentGraph.fitness)
			swapParentAndChildGraphs();
		
		if (num_iterations_with_constant_fitness > HillClimber.converged_iterations && mutationProbability <= 1.0){
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

}
