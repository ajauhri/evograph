package graph;

import java.util.HashMap;
import java.util.Map;

public class Node {
	public int id;
	HashMap<Integer, Edge> edges = new HashMap<Integer, Edge>();
	
	public Node(int id) {
		this.id = id;
	}
	
	public void createEdge(Node n) {
		edges.put(n.id, new Edge(n));
	}
	
	public boolean hasEdge(Node n) {
		return edges.containsKey(n.id);
	}
	
	public boolean hasEdge(int i) {
		return edges.containsKey(i);
	}
	
	public void print() {
		System.out.print("Node " + this.id + " is connected to ");
		for (Map.Entry<Integer, Edge> entry : edges.entrySet())
		    System.out.print(entry.getKey() + ", ");
		System.out.println();
	}
}
