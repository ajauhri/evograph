package graph;

import java.util.HashMap;

public class NodeInstance {
	public Node node;
	public int id;
	public int x;
	public int y;
	public double[] nodeDistances;
	public HashMap<Integer, Double> edgeAngles;

	public NodeInstance(Node node, int totalNodes) {
		this.node = node;
		this.id = node.id;
		nodeDistances = new double[totalNodes];
		edgeAngles = new HashMap<Integer, Double>();
	}
}