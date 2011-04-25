package algorithms.iterationBased;

import evograph.EvoGraph;
import graph.Graph;


public class SimulatedAnnealing extends IterationBasedAlgorithm {
	double temperature = 0.0;
	public static final double initial_temperature = 50.0;
	public static final int iterations_per_temperature_change = 10000;
	public static final double temperature_factor = 0.20;

	public SimulatedAnnealing(Graph graph) {
		 super(graph);
	}

	@Override
	public void restart() {
		super.restart();
		temperature = SimulatedAnnealing.initial_temperature;
	}
	
	public void nextIndividual() {
		iterations++;
		previous_parent_fitness = parentGraph.fitness;
		childGraph = copyGraphInstance(parentGraph);
		gaussianMutate(childGraph, mutationProbability);
	
		childGraph.centerGraph();
		childGraph.calculateFitness();
	
		
		if (childGraph.fitness < parentGraph.fitness) {
			swapParentAndChildGraphs();
		}

		else {
			double delta_fitness = childGraph.fitness - parentGraph.fitness;
			if (EvoGraph.probability(Math.exp((-1.0 * delta_fitness) / temperature)))
				swapParentAndChildGraphs();
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
}
