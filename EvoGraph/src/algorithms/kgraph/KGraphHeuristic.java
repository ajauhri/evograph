package algorithms.kgraph;

import java.awt.Point;
import java.util.Random;

import algorithms.IncrementalGraphAlgorithm;
import algorithms.StochasticAlgorithm;

import evograph.EvoGraph;
import evograph.GraphCanvas;

import graph.Graph;
import graph.GraphInstance;
import graph.NodeInstance;

public class KGraphHeuristic extends StochasticAlgorithm implements IncrementalGraphAlgorithm {
	
	int nLayers;
	Point anchorPoints[];
	double distanceToCenter;
	double referenceAngles[] = {Math.PI / 3, Math.PI, 5 * Math.PI / 3};
	double maximumDeltaAngle = Math.PI / 18;
	double angleMutationFactor;
	double distanceMutationFactor;
	double ageScaleFactor = 0.1;
	FitnessComparator fitnessComparator; 
	Random rand;
	
	public KGraphHeuristic(Graph graph) {
		super(graph);
		rand = new Random();
		anchorPoints = new Point[3];
		nLayers = (graph.nodes.length - 1) / 3 + 1;
		angleMutationFactor = 1 * maximumDeltaAngle;
		fitnessComparator = new FitnessComparator();
	}

	@Override
	public void next() {
	}

	@Override
	public GraphInstance displayGraph() {
		return null;
	}

	@Override
	public String displayText() {
		return fitnessString(displayGraph());
	}

	@Override
	public void updateGraph() {
		displayGraph().calculateKFitness();
	}

	@Override
	public void restart() {
	}

	@Override
	public int getRuns() {
		return 0;
	}
	
	public void setAnchorPoints() {
		double width, height, xOffset, yOffset;
		if ((double) GraphCanvas.canvasHeight >= (double) (GraphCanvas.canvasWidth * Math.sqrt(3) / 2)) {
			width = GraphCanvas.canvasWidth;
			height = GraphCanvas.canvasWidth * Math.sqrt(3) / 2;
			xOffset = 0;
			yOffset = (GraphCanvas.canvasHeight - height) / 2;
		} else {
			height = GraphCanvas.canvasHeight;
			width = GraphCanvas.canvasHeight * 2 / Math.sqrt(3);
			yOffset = 0;
			xOffset = (GraphCanvas.canvasWidth - width) / 2;
		}
		anchorPoints[0] = new Point((int) (100 * xOffset), (int) (100 * (height + yOffset)));
		anchorPoints[1] = new Point((int) (100 * (xOffset + (width / 2))), (int) (100 * yOffset));
		anchorPoints[2] = new Point((int) (100 * (xOffset + width)), (int) (100 * (height + yOffset)));
		distanceToCenter = 100 * width / Math.sqrt(3);
		distanceMutationFactor = 0.2 * (distanceToCenter / nLayers);
	}
	
	public GraphInstance randomIndividual() {
		GraphInstance individual = new GraphInstance(graph);
		individual.nodeInstances[0].setRealX(anchorPoints[0].x);
		individual.nodeInstances[0].setRealY(anchorPoints[0].y);
		individual.nodeInstances[1].setRealX(anchorPoints[1].x);
		individual.nodeInstances[1].setRealY(anchorPoints[1].y);
		individual.nodeInstances[2].setRealX(anchorPoints[2].x);
		individual.nodeInstances[2].setRealY(anchorPoints[2].y);
		if(Graph.nNodes % 3 == 0) {
			for (int i = 0; i < Graph.nNodes / 3; i++) {
				double distanceFromAnchor = distanceToCenter * i / nLayers;
				double deltaAngle = (Math.random() - 0.5) * 2 * maximumDeltaAngle / (nLayers - i);
				for(int j = i * 3; j < (i + 1) * 3; j++) {
					NodeInstance n = individual.nodeInstances[j];
					n.distanceFromAnchor = distanceFromAnchor;
					n.deltaAngle = deltaAngle;
					n.age = this.getRuns();
					calculateCoordsFromDistanceAndAngle(n);
				}
			}
		} else {
			for (int i = 3; i < Graph.nNodes; i++) {
				NodeInstance n = individual.nodeInstances[i];
				n.distanceFromAnchor = distanceToCenter * (n.id / 3) / nLayers;
				n.deltaAngle = (Math.random() - 0.5) * 2 * maximumDeltaAngle / (nLayers - ((n.id % 3) + 1));
				calculateCoordsFromDistanceAndAngle(n);
			}
		}
		return individual;
	}
	
	public void calculateCoordsFromDistanceAndAngle(NodeInstance n) {
		int anchor = n.id % 3;
		int coords[] = EvoGraph.calculateCoordinatesFromPointAngleDistance(anchorPoints[anchor].x, anchorPoints[anchor].y, referenceAngles[anchor] + n.deltaAngle, n.distanceFromAnchor);
		n.setRealX(coords[0]);
		n.setRealY(coords[1]);
	}
}
