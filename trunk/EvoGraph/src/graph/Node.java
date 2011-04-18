package graph;

import java.util.HashMap;

public class Node {
	public int id;
	public HashMap<Integer, Node> connectedNodes = new HashMap<Integer, Node>();
	
	public Node(int id) {
		this.id = id;
	}
}