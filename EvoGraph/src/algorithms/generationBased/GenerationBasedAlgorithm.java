package algorithms.generationBased;

import evograph.EvoGraph;
import graph.Graph;
import graph.GraphInstance;
import algorithms.StochasticAlgorithm;

public class GenerationBasedAlgorithm extends StochasticAlgorithm {
	protected static final int populationSize = 100;
	protected static final double elitism = 0.1;
	public int generation = 0;
	
	public GenerationBasedAlgorithm(Graph graph) {
		super(graph);
	}

	public GraphInstance recombine(GraphInstance parent1, GraphInstance parent2) {
		GraphInstance child = new GraphInstance(graph);
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
	
	public int getRuns() {
		// TODO Auto-generated method stub
		return generation * populationSize;
	}
}
