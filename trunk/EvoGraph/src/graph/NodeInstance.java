package graph;

public class NodeInstance {
	public Node node;
	public int id;
	public int x;
	public int y;
	public double[] nodeDistances;

	public NodeInstance(Node node, int totalNodes) {
		this.node = node;
		this.id = node.id;
		nodeDistances = new double[totalNodes];
	}
	
	public void resetNodeDistances() {
		for (int i = 0; i < nodeDistances.length; i++)
			nodeDistances[i] = 0;
	}
}