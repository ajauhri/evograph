package graph;

import java.io.Serializable;

public class Edge implements Serializable {
	private static final long serialVersionUID = 1L;
	public Node to;
	public double edgeLength;
	
	public Edge(Node to) {
		this.to = to;
	}	
	
	public double computeEdgeLength(int x, int y) {
		edgeLength = Graph.distanceFormula(x, y, to.x, to.y);
		return edgeLength;
	}
}