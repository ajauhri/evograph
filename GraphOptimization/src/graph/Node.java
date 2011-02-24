package graph;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Node implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	int x;
	int y;
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

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Object[] getEdges() {
		return edges.values().toArray();
	}

	public void setXandY(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
