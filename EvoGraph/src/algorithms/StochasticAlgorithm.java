package algorithms;

import java.util.Comparator;
import java.util.Random;

import evograph.EvoGraph;
import evograph.GraphCanvas;

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
		int canvasWidth = GraphCanvas.canvasWidth;
		int canvasHeight = GraphCanvas.canvasHeight;
		for (int i = 0; i < graph.nodes.length; i++) {
			if (EvoGraph.probability(mutationProbability)) {
				individual.nodeInstances[i].x = EvoGraph.boundaryChecker(individual.nodeInstances[i].x + (int) (rand.nextGaussian() * (canvasWidth / graph.nodes.length)), canvasWidth);
				individual.nodeInstances[i].y = EvoGraph.boundaryChecker(individual.nodeInstances[i].y + (int) (rand.nextGaussian() * (canvasHeight / graph.nodes.length)), canvasHeight);
			}
		}
	}
	
	public void simpleMutate(GraphInstance individual, double mutationProbability) {
		int canvasWidth = GraphCanvas.canvasWidth;
		int canvasHeight = GraphCanvas.canvasHeight;
		for (int i = 0; i < graph.nodes.length; i++) {
			if (EvoGraph.probability(mutationProbability)) {
				individual.nodeInstances[i].x = (int) (Math.random() * canvasWidth);
				individual.nodeInstances[i].y = (int) (Math.random() * canvasHeight);
			}
		}
	}

	public GraphInstance copyGraphInstance(GraphInstance graphInstance) {
		GraphInstance newGraphInstance = new GraphInstance(graph);
		for (int i = 0; i < newGraphInstance.nodeInstances.length; i++) {
			newGraphInstance.nodeInstances[i].x = graphInstance.nodeInstances[i].x;
			newGraphInstance.nodeInstances[i].y = graphInstance.nodeInstances[i].y;
		}
		return newGraphInstance;
	}

	public GraphInstance randomIndividual() {
		GraphInstance individual = new GraphInstance(graph);
		for (NodeInstance n : individual.nodeInstances) {
			n.x = (int) (Math.random() * GraphCanvas.canvasWidth); // initialize 
			n.y = (int) (Math.random() * GraphCanvas.canvasHeight);
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
