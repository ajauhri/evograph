package ga;

import evograph.GraphCanvas;
import graph.Graph;
import graph.GraphInstance;
import graph.Node;

public class GGraph extends GraphInstance {
	public double fitness;
	public double edgeFitness;

	public GGraph(Graph graph) {
		super(graph);
	}

	public void calculateFitness() {
		calculateNumberOfEdgeCrossings();
		calculateNodeDistances();
		calculateEdgeFitness();
		fitness = (double) (numberOfEdgeCrossings + 1) * (edgeFitness + 1);
	}
	
	public void calculateEdgeFitness() {
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
