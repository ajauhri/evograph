package ga;

import java.util.Collections;
import java.util.Vector;

import evograph.GraphCanvas;
import graph.Graph;
import graph.GraphInstance;
import graph.Node;

public class GGraph extends GraphInstance {
	public double fitness;
	public double edgeFitness;
	public double angularResolution;
	public double nodeSeparation;
	public double edgeTunneling;
	
	public GGraph(Graph graph) {
		super(graph);
	}

	public void calculateFitness() {
		calculateNumberOfEdgeCrossings();
		calculateAngularResolution();
		calculateEdgeFitness();
		calculateNodeSeparation();
		calculateEdgeTunneling();
		fitness = (double) (numberOfEdgeCrossings + 1) * (edgeFitness + angularResolution + nodeSeparation + edgeTunneling + 1);
	}

	public void calculateAngularResolution() {
		calculateEdgeAngles();
		angularResolution = 0;
		for (int i = 0; i < nodeInstances.length; i++) {
			int numberOfConnectedNodes = nodeInstances[i].edgeAngles.size();
			double optimalAngle = (2 * Math.PI) / numberOfConnectedNodes;
			Vector<Double> angles = new Vector<Double>();
			angles.addAll(nodeInstances[i].edgeAngles.values());
			Collections.sort(angles);
			double nodeResolution = 0;
			for (int j = 0; j < numberOfConnectedNodes; j++) {
				double angle1 = angles.get(j);
				double angle2 = j == numberOfConnectedNodes - 1 ? angles.get(0) + (2 * Math.PI) : angles.get(j + 1); 
				nodeResolution += Math.pow(Math.abs(optimalAngle - (angle2 - angle1)), 2);
			}
			angularResolution += nodeResolution;
		}
	}
	
	public void calculateNodeSeparation() {
		nodeSeparation = 0;
		for (int i = 0; i < nodeInstances.length - 1; i++) {
			for (int j = i + 1; j < nodeInstances[i].nodeDistances.length; j++) {
				if (!nodeInstances[i].node.connectedNodes.containsKey(j) && nodeInstances[i].nodeDistances[j] < GraphCanvas.optimalEdgeLength)
					nodeSeparation += Math.pow(GraphCanvas.optimalEdgeLength - nodeInstances[i].nodeDistances[j], 2);
			}
		}
		nodeSeparation /= (graph.nodes.length * GraphCanvas.optimalEdgeLength);
	}
	
	public void calculateEdgeTunneling() {
		//System.out.println();
		//System.out.println("=================");
		edgeTunneling = 0;
		for (int i = 0; i < nodeInstances.length; i++) {
			for (int j = 0; j < nodeInstances.length; j++) {
				if (i == j)
					continue;
				for(Node n : nodeInstances[j].node.connectedNodes.values()) {
					if (n.id == i || n.id < j)
						continue;
					double distance = distanceToSegment(nodeInstances[i].x, nodeInstances[i].y, nodeInstances[j].x, nodeInstances[j].y, nodeInstances[n.id].x, nodeInstances[n.id].y);
					//System.out.println("Node " + i + " is distance " + distance + " from edge (" + j + ", " + n.id + ")");
					edgeTunneling += edgeTunnelingPenalty(distance);
				}
				
			}
		}
	}
	
	public void calculateEdgeFitness() {
		calculateNodeDistances();
		edgeFitness = 0;
		for (int i = 0; i < nodeInstances.length; i++) {
			for (Node n : nodeInstances[i].node.connectedNodes.values()) {
				if (n.id > i)
					edgeFitness += edgePenalty(nodeInstances[i].nodeDistances[n.id]);
			}
		}
		edgeFitness /= (graph.nodes.length * GraphCanvas.optimalEdgeLength);
	}
	
	public double edgePenalty(double edgeLength) {
		return Math.pow(Math.abs(GraphCanvas.optimalEdgeLength - edgeLength), 2);
	}
	
	public double edgeTunnelingPenalty(double distance) {
		double minNT = 20;
		if (distance > minNT)
			return 0;
		else
			return Math.pow(minNT - distance, 2);
	}
}
