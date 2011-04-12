package graph;

import java.io.Serializable;

public class Edge implements Serializable {
	private static final long serialVersionUID = 1L;
	public Node to;
	public Node from;
	public double edgeLength;
	public double angle;
	
	public Edge(Node to, Node from) {
		this.to = to;
		this.from = from;
	}	
	
	public double computeEdgeLength(int x, int y) {
		edgeLength = Graph.distanceFormula(x, y, to.x, to.y);
		return edgeLength;
	}
}