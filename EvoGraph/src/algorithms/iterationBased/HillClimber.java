package algorithms.iterationBased;

import graph.Graph;

public class HillClimber extends IterationBasedAlgorithm {

	public HillClimber(Graph graph) {
		 super(graph);
	}

	public void nextIndividual() {
		iterations++;
		previous_parent_fitness = parentGraph.fitness;
		childGraph = copyGraphInstance(parentGraph);
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
}
