package graph;

import java.awt.geom.Line2D;

public class GraphInstance {
	public Graph graph;
	public NodeInstance[] nodeInstances;
	public int numberOfEdgeCrossings;
	
	public GraphInstance(Graph graph) {
		this.graph = graph;
		nodeInstances = new NodeInstance[graph.nodes.length];
		for(int i = 0; i < graph.nodes.length; i++)
			nodeInstances[i] = new NodeInstance(graph.nodes[i], graph.nodes.length);
	}
	
	public void calculateNodeDistances() {
		for (int i = 0; i < nodeInstances.length; i++)
			nodeInstances[i].resetNodeDistances();
		for (int i = 0; i < nodeInstances.length - 1; i++) {
			for (int j = i + 1; j < nodeInstances.length; j++) {
				double distance = Graph.distanceFormula(nodeInstances[i].x, nodeInstances[i].y, nodeInstances[j].x, nodeInstances[j].y);
				nodeInstances[i].nodeDistances[j] = distance;
				nodeInstances[j].nodeDistances[i] = distance;
			}
		}
	}
	
	public void calculateNumberOfEdgeCrossings() {
		numberOfEdgeCrossings = 0;
		for (int i = 0; i < nodeInstances.length - 1; i++) {
			NodeInstance nodei = nodeInstances[i];
			for (int j = i + 1; j < nodeInstances.length; j++) {
				NodeInstance nodej = nodeInstances[j];
				for (Node cni : nodei.node.connectedNodes.values()) {
					if (cni.id < nodei.id)
						continue;
					NodeInstance nodei2 = nodeInstances[cni.id];
					for (Node cnj : nodej.node.connectedNodes.values()) {
						if (cnj.id < nodej.id)
							continue;
						NodeInstance nodej2 = nodeInstances[cnj.id];
						if (checkEdgeCrossing(nodei, nodei2, nodej, nodej2)) {
							numberOfEdgeCrossings++;
						}
					}
				}
			}
		}
	}

	/**
	 * Check if the edge between a-b intersects with the edge between c-d
	 */
	public boolean checkEdgeCrossing(NodeInstance a, NodeInstance b, NodeInstance c, NodeInstance d) {
		if (b == c || d == a || b == d)
			return false;
		return Line2D.linesIntersect(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y);
	}
}
