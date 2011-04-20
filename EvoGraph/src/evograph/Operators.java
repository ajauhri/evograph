package evograph;

import java.util.Random;

import ga.GGraph;
import graph.Graph;
import graph.GraphInstance;
import graph.NodeInstance;

public class Operators implements IncrementalGraphAlgorithm {

	public Graph graph;
	
	public Operators(Graph graph) {
		this.graph = graph;
	}

	@Override
	public void next() {
		// TODO Auto-generated method stub

	}

	@Override
	public GraphInstance displayGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String displayText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateGraph() {
		// TODO Auto-generated method stub

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

	public GGraph copyParent(GGraph parentGraph) {
		GGraph childGraph = new GGraph(graph);
		for (int i = 0; i < childGraph.nodeInstances.length; i++) {
			childGraph.nodeInstances[i].x = parentGraph.nodeInstances[i].x;
			childGraph.nodeInstances[i].y = parentGraph.nodeInstances[i].y;
		}
		return childGraph;
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
	

	public void swapParentAndChildGraphs(GGraph parentGraph, GGraph childGraph) {
		GGraph temp = parentGraph;
		parentGraph = childGraph;
		childGraph = temp;
	}


}
