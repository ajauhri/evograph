package graph;

import java.io.Serializable;
import java.util.HashMap;

public class Node implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public HashMap<Integer, Node> connectedNodes = new HashMap<Integer, Node>();
	
	public Node(int id) {
		this.id = id;
	}
}