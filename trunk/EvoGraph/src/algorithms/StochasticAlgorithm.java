package algorithms;

import java.util.Comparator;
import java.util.Random;

import evograph.EvoGraph;

import graph.GraphInstance;
import graph.Graph;
import graph.NodeInstance;

public class StochasticAlgorithm {
	public Graph graph;
	
	public StochasticAlgorithm(Graph graph) {
		this.graph = graph;
	}

	public void gaussianMutate(GraphInstance individual, double mutationProbability) {
		Random rand = new Random();
		int canvasWidth = EvoGraph.canvasWidth;
		int canvasHeight = EvoGraph.canvasHeight;
		for (int i = 0; i < graph.nodes.length; i++) {
			if (EvoGraph.probability(mutationProbability)) {
				individual.nodeInstances[i].setRealX(EvoGraph.boundaryChecker(individual.nodeInstances[i].x + (int) (rand.nextGaussian() * (canvasWidth / graph.nodes.length)), canvasWidth) * 100);
				individual.nodeInstances[i].setRealY(EvoGraph.boundaryChecker(individual.nodeInstances[i].y + (int) (rand.nextGaussian() * (canvasHeight / graph.nodes.length)), canvasHeight) * 100);
			}
		}
	}
	
	public void simpleMutate(GraphInstance individual, double mutationProbability) {
		int canvasWidth = EvoGraph.canvasWidth;
		int canvasHeight = EvoGraph.canvasHeight;
		for (int i = 0; i < graph.nodes.length; i++) {
			if (EvoGraph.probability(mutationProbability)) {
				individual.nodeInstances[i].setRealX((int) (Math.random() * canvasWidth) * 100);
				individual.nodeInstances[i].setRealY((int) (Math.random() * canvasHeight) * 100);
			}
		}
	}

	public GraphInstance copyGraphInstance(GraphInstance graphInstance) {
		return graphInstance.copy();
	}

	public GraphInstance randomIndividual() {
		GraphInstance individual = new GraphInstance(graph);
		for (NodeInstance n : individual.nodeInstances) {
			n.setRealX((int) (Math.random() * EvoGraph.canvasWidth) * 100); // initialize 
			n.setRealY((int) (Math.random() * EvoGraph.canvasHeight) * 100);
		}
		return individual;
	}
	
	public String fitnessString(GraphInstance gGraph) {
		return "\t\tF: " + String.format("%.3f", gGraph.fitness) + 
		"\t\t#EC: " + gGraph.numberOfEdgeCrossings +
		"\t\tEF: " + String.format("%.2f", gGraph.edgeFitness) +
		"\t\tAR: " + String.format("%.2f", gGraph.angularResolution) +
		"\t\tNS: " + String.format("%.2f", gGraph.nodeSeparation) +
		"\t\tET: " + String.format("%.2f", gGraph.edgeTunneling); 
		//"\t\tOR: " + String.format("%.2f", gGraph.orthogonality);
	}

	public class FitnessComparator implements Comparator<GraphInstance> {
		public int compare(GraphInstance g1, GraphInstance g2) {
			if (g2.fitness == g1.fitness)
				return 0;
			else if (g2.fitness > g1.fitness)
				return -1;
			else
				return 1;
		}
	}
}
