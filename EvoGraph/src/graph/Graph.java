package graph;

public class Graph { //Undirected graph
	public Node[] nodes;

	public Graph(int nNodes) {
		nodes = new Node[nNodes];
		for (int i = 0; i < nNodes; i++)
			nodes[i] = new Node(i);
	}
	
	public void createEdge(int nodeFromId, int nodeToId) {
		nodes[nodeFromId].connectedNodes.put(nodeToId, nodes[nodeToId]);
		nodes[nodeToId].connectedNodes.put(nodeFromId, nodes[nodeFromId]);
	}
	
	public static double distanceFormula(int x, int y, int toX, int toY) {
		return Math.sqrt(Math.pow((x - toX), 2) + Math.pow((y - toY), 2));
	}
}