
package algorithms.kgraph;

import evograph.EvoGraph;
import graph.Graph;
import graph.GraphInstance;
import graph.NodeInstance;

public class KGraphSA extends KGraphHeuristic {
	int iteration = 0;
	GraphInstance gi;
	int flippedSide;
	double mutationProbability = 1;
	
	public KGraphSA(Graph graph) {
		super(graph);
		if (nLayers % 2 == 1)
			flippedSide = 2;
		else if (nLayers % 2 == 2)
			flippedSide = 1;
		else
			flippedSide = -1;
	}

	@Override
	public GraphInstance displayGraph() {
		return gi;
	}
	
	@Override
	public void next() {
		if (iteration == 0) {
			setAnchorPoints();
			gi = randomIndividual();
			gi.calculateKFitness();
			distanceMutationFactor *= 0.3;
			iteration++;
		} else {
			nextIteration();
		}
	}

	@Override
	public int getRuns() {
		return iteration;
	}
	
	@Override
	public void restart() {
		iteration = 0;
	}

	public void nextIteration() {
		iteration++;
		for(int i = 1; i < nLayers; i++) {
			GraphInstance oldGraph = gi.copy();
			double oldFitness = oldGraph.fitness;
			for(int j = nLayers - 1; j >= i; j--) {
				if(Math.random() > mutationProbability)
					continue;
				int nodesInLayer = Math.min((j + 1) * 3, Graph.nNodes) - (j * 3);
				double newDeltaDistance = rand.nextGaussian() * distanceMutationFactor / (nLayers - j);
				double newDeltaAngle = rand.nextGaussian() * angleMutationFactor / (nLayers - j);
				for(int q = 0; q < nodesInLayer; q++) {
					int gindex = (j * 3) + q;
					gi.nodeInstances[gindex].distanceFromAnchor += newDeltaDistance;
					if (q % 3 == flippedSide)
						gi.nodeInstances[gindex].deltaAngle += newDeltaAngle * -1;
					else
						gi.nodeInstances[gindex].deltaAngle += newDeltaAngle;
					calculateCoordsFromDistanceAndAngle(gi.nodeInstances[gindex]);
				}
			}
			gi.calculateKFitness();
			double newFitness = gi.fitness;
			if(oldFitness < newFitness)
				gi = oldGraph;
		}
	}
	
	public GraphInstance randomIndividual() {
		GraphInstance individual = new GraphInstance(graph);
		individual.nodeInstances[0].setRealX(anchorPoints[0].x);
		individual.nodeInstances[0].setRealY(anchorPoints[0].y);
		individual.nodeInstances[1].setRealX(anchorPoints[1].x);
		individual.nodeInstances[1].setRealY(anchorPoints[1].y);
		individual.nodeInstances[2].setRealX(anchorPoints[2].x);
		individual.nodeInstances[2].setRealY(anchorPoints[2].y);
		double sign = 1;
		double last = 0;
		for (int i = 0; i < Math.ceil((double) Graph.nNodes / 3); i++) {
			double distanceFromAnchor = distanceToCenter * i / nLayers;
			double deltaAngle = EvoGraph.randomDouble(sign * maximumDeltaAngle / (nLayers - i), last);
			last = deltaAngle;
			sign *= -1;
			for(int j = i * 3; j < Math.min((i + 1) * 3, Graph.nNodes); j++) {
				NodeInstance n = individual.nodeInstances[j];
				n.distanceFromAnchor = distanceFromAnchor;
				if (j % 3 == flippedSide)
					n.deltaAngle = deltaAngle * -1;
				else
					n.deltaAngle = deltaAngle;
				n.age = this.getRuns();
				calculateCoordsFromDistanceAndAngle(n);
			}
		}
		return individual;
	}
	
}
