package graph;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Node implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	int x;
	int y;
	public int numberOfCrossovers;
	public float crossoversToEdgesRatio;
	HashMap<Integer, Edge> edgesOut = new HashMap<Integer, Edge>();
	HashMap<Integer, Edge> edgesIn = new HashMap<Integer, Edge>();

	public Node(int id) {
		this.id = id;
		this.numberOfCrossovers = 0;
	}

	public void createEdge(Node n) {
		Edge edge = new Edge(n);
		edgesOut.put(n.id, edge);
		n.edgesIn.put(this.id, edge);
	}

	public boolean hasEdgeOut(Node n) {
		return edgesOut.containsKey(n.id);
	}

	public boolean hasEdgeOut(int i) {
		return edgesOut.containsKey(i);
	}

	public boolean hasEdgeIn(Node n) {
		return edgesIn.containsKey(n.id);
	}

	public boolean hasEdgeIn(int i) {
		return edgesIn.containsKey(i);
	}
	
	public int numberOfEdgesInAndOut() {
		return edgesOut.size() + edgesIn.size();
	}

	public void calculateCrossoversToEdgesRatio() {
		crossoversToEdgesRatio = numberOfCrossovers / numberOfEdgesInAndOut();
	}
	
	public float getCrossoversToEdgesRatio() {
		return crossoversToEdgesRatio;
	}
	
	public void print() {
		System.out.print("Node " + this.id + " is connected to ");
		for (Map.Entry<Integer, Edge> entry : edgesOut.entrySet())
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

	public Object[] getEdgesOut() {
		return edgesOut.values().toArray();
	}

	public void setXandY(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
