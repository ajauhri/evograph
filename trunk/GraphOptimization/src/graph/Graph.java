package graph;

import java.util.Vector;

public class Graph {
	
	Vector<Node> nodes = new Vector<Node>(); //ID of the node is the position in the vector
	
	public Graph(int numberOfNodes) {
		for (int i = 0; i < numberOfNodes; i++)
			nodes.add(new Node(i));
	}
	
	public void createEdge(int fromId, int toId) {
		nodes.get(fromId).createEdge(nodes.get(toId));
	}
	
	public void print() {
		int numNodes = nodes.size();
		for(int i = 0; i < numNodes; i++)
			nodes.get(i).print();
	}
	
	public int getNumberOfNodes() {
		return nodes.size();
	}
	
	public Node getNodeAt(int pos) {
		return nodes.get(pos);
	}
}
