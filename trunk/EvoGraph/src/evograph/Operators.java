package evograph;

import java.util.Random;

import ga.GGraph;
import graph.Graph;
import graph.NodeInstance;

public class Operators {

	public Graph graph;
	
	public Operators(Graph graph) {
		this.graph = graph;
	}

	public void gaussianMutate(GGraph individual, double mutationProbability) {
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
	
	public void simpleMutate(GGraph individual, double mutationProbability) {
		int canvasWidth = GraphCanvas.canvasWidth;
		int canvasHeight = GraphCanvas.canvasHeight;
		for (int i = 0; i < graph.nodes.length; i++) {
			if (EvoGraph.probability(mutationProbability)) {
				individual.nodeInstances[i].x = (int) (Math.random() * canvasWidth);
				individual.nodeInstances[i].y = (int) (Math.random() * canvasHeight);
			}
		}
	}

	public GGraph copyGGraph(GGraph gGraph) {
		GGraph newGGraph = new GGraph(graph);
		for (int i = 0; i < newGGraph.nodeInstances.length; i++) {
			newGGraph.nodeInstances[i].x = gGraph.nodeInstances[i].x;
			newGGraph.nodeInstances[i].y = gGraph.nodeInstances[i].y;
		}
		return newGGraph;
	}

	public GGraph randomIndividual() {
		GGraph individual = new GGraph(graph);
		for (NodeInstance n : individual.nodeInstances) {
			n.x = (int) (Math.random() * GraphCanvas.canvasWidth); // initialize 
			n.y = (int) (Math.random() * GraphCanvas.canvasHeight);
		}
		return individual;
	}
	
	public String fitnessString(GGraph gGraph) {
		return "\t\tF: " + String.format("%.2f", gGraph.fitness) + 
		"\t\t#EC: " + gGraph.numberOfEdgeCrossings +
		"\t\tEF: " + String.format("%.2f", gGraph.edgeFitness) +
		"\t\tAR: " + String.format("%.2f", gGraph.angularResolution) +
		"\t\tNS: " + String.format("%.2f", gGraph.nodeSeparation) +
		"\t\tET: " + String.format("%.2f", gGraph.edgeTunneling) + 
		"\t\tOR: " + String.format("%.2f", gGraph.orthogonality);
	}

}
