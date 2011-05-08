package graph;

import java.io.Serializable;

public class Graph implements Serializable { //Undirected graph
	private static final long serialVersionUID = 1L;
	public final Node[] nodes;
	public static int nNodes;
	public static int nEdges = 0;

	public Graph(int nNodes) {
		Graph.nNodes = nNodes;
		nodes = new Node[nNodes];
		for (int i = 0; i < nNodes; i++)
			nodes[i] = new Node(i);
	}
	
	public void createEdge(int nodeFromId, int nodeToId) {
		nEdges++;
		nodes[nodeFromId].connectedNodes.put(nodeToId, nodes[nodeToId]);
		nodes[nodeToId].connectedNodes.put(nodeFromId, nodes[nodeFromId]);
	}

	public static double distanceFormula(int x, int y, int toX, int toY) {
		return Math.sqrt(Math.pow((x - toX), 2) + Math.pow((y - toY), 2));
	}
	
	public static double distanceFormula(double x, double y, double toX, double toY) {
		return Math.sqrt(Math.pow((x - toX), 2) + Math.pow((y - toY), 2));
	}
}