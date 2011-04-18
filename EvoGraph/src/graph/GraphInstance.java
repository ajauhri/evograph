package graph;

import java.awt.geom.Line2D;

import evograph.EvoGraph;
import evograph.GraphCanvas;

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
	
	public void centerGraph() {
		double averageX = 0;
		double averageY = 0;
		for (NodeInstance n : nodeInstances) {
			averageX += n.x;
			averageY += n.y;
		}
		averageX /= nodeInstances.length;
		averageY /= nodeInstances.length;
		int canvasWidth = GraphCanvas.canvasWidth;
		int canvasHeight = GraphCanvas.canvasHeight;
		int deltaX = (int) ((canvasWidth / 2) - averageX);
		int deltaY = (int) ((canvasHeight / 2) - averageY);
		for (NodeInstance n : nodeInstances) {
			n.x = EvoGraph.boundaryChecker(n.x + deltaX, canvasWidth);
			n.y = EvoGraph.boundaryChecker(n.y + deltaY, canvasHeight);
		}		
	}
	
	public void calculateNodeDistances() {
		for (int i = 0; i < nodeInstances.length; i++)
			nodeInstances[i].nodeDistances = new double[nodeInstances.length];
		for (int i = 0; i < nodeInstances.length - 1; i++) {
			for (int j = i + 1; j < nodeInstances.length; j++) {
				double distance = Graph.distanceFormula(nodeInstances[i].x, nodeInstances[i].y, nodeInstances[j].x, nodeInstances[j].y);
				nodeInstances[i].nodeDistances[j] = distance;
				nodeInstances[j].nodeDistances[i] = distance;
			}
		}
	}

	public void calculateEdgeAngles() {
		for (int i = 0; i < nodeInstances.length - 1; i++) {
			for (Node n : nodeInstances[i].node.connectedNodes.values()) {
				if(n.id < i)
					continue;
				double angle = calculateAngle(nodeInstances[n.id].x, nodeInstances[n.id].y, nodeInstances[i].x, nodeInstances[i].y);
				nodeInstances[i].edgeAngles.put(n.id, angle);
				nodeInstances[n.id].edgeAngles.put(i, flipAngle(angle));
			}
		}
	}
	
	public double flipAngle(double angle) {
		return angle > Math.PI ? angle - Math.PI : angle + Math.PI;
	}
	
	public double calculateAngle(int x1, int y1, int x2, int y2) {
		double angle;
		try {
			angle = Math.atan(((double) (y2 - y1))/((double) (x2 - x1)));
			if(x2 < x1)
				angle += Math.PI;
			angle += (Math.PI / 2);
		} catch (ArithmeticException exc) {
			if (y2 > y1)
				angle = (Math.PI / 2);
			else
				angle = 3 * (Math.PI / 2);
		}
        return angle;
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
