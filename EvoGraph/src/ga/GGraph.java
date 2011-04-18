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
	
	public GGraph(Graph graph) {
		super(graph);
	}

	public void calculateFitness() {
		calculateNumberOfEdgeCrossings();
		calculateAngularResolution();
		calculateEdgeFitness();
		fitness = (double) (numberOfEdgeCrossings + 1) * (edgeFitness + angularResolution + 1);
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
	
	public void calculateEdgeFitness() {
		calculateNodeDistances();
		edgeFitness = 0;
		for (int i = 0; i < nodeInstances.length; i++) {
			for(Node n : nodeInstances[i].node.connectedNodes.values()) {
				if (n.id > i)
					edgeFitness += edgePenalty(nodeInstances[i].nodeDistances[n.id]);
			}
		}
		edgeFitness /= (graph.nodes.length * 100);
	}
	
	public double edgePenalty(double edgeLength) {
		return Math.pow(Math.abs(GraphCanvas.optimalEdgeLength - edgeLength), 2);
	}
}
