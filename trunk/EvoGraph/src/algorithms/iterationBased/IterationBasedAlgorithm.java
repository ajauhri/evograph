package algorithms.iterationBased;

import graph.Graph;
import graph.GraphInstance;
import algorithms.IncrementalGraphAlgorithm;
import algorithms.StochasticAlgorithm;

public class IterationBasedAlgorithm extends StochasticAlgorithm implements IncrementalGraphAlgorithm {
	int iterations = 0;
	int num_iterations_with_constant_fitness = 0;
	double previous_parent_fitness = 0.0;
	double mutationProbability = 0.05;
	public static final double converged_iterations = 50000;
	public GraphInstance parentGraph;
	public GraphInstance childGraph;
	
	public IterationBasedAlgorithm(Graph graph) {
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
		GraphInstance fittest = (GraphInstance) displayGraph();
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
		iterations++;
	}

	public void nextIndividual() {} //Override
	
	public void swapParentAndChildGraphs() {
		GraphInstance temp = parentGraph;
		parentGraph = childGraph;
		childGraph = temp;
	}
}
