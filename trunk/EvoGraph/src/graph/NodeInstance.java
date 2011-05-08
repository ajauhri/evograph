package graph;

import java.io.Serializable;
import java.util.HashMap;

public class NodeInstance implements Serializable {
	private static final long serialVersionUID = 1L;
	public Node node;
	public int id;
	public int x;
	public int y;
	public int realX;
	public int realY;
	public int age;
	public double distanceFromAnchor;
	public double deltaAngle;
	public double[] nodeDistances;
	public HashMap<Integer, Double> edgeAngles;

	public NodeInstance(Node node, int totalNodes) {
		this.node = node;
		this.id = node.id;
		nodeDistances = new double[totalNodes];
		edgeAngles = new HashMap<Integer, Double>();
	}
	
	public void setRealX(int newX) {
		realX = newX;
		x = newX / 100;
	}
	
	public void setRealY(int newY) {
		realY = newY;
		y = newY / 100;
	}
}