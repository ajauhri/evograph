package graph;

public class Edge {
	public Node to;
	public double edgeLength;
	
	public Edge(Node to) {
		this.to = to;
	}	
	
	public void computeEdgeLength(int x, int y) {
		edgeLength = Graph.distanceFormula(x, y, to.x, to.y);
	}
}