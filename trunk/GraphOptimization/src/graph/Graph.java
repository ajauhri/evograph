package graph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class Graph implements Serializable {
	private static final long serialVersionUID = 1L;
	//public Fitness fitness;
	double fitness;
	static double optimalEdgeLength = 50.0; 

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
	
	public void calculateFitness() {
		//double sumOfEdgeLengths = 0;
		//int totalNumberOfEdges = 0;
		fitness = 0;
		int numberOfNodes = getNumberOfNodes();
		for (int i = 0; i < numberOfNodes; i++) {
			Node node = getNodeAt(i);
			Object[] edges = node.getEdges();
			for (Object e : edges) {
				double edgeLength = ((Edge) e).edgeLength;
				fitness += Math.abs(edgeLength - optimalEdgeLength);
				//sumOfEdgeLengths += edgeLength;
				//totalNumberOfEdges++;
			}
		}
		//fitness = Math.abs((sumOfEdgeLengths / totalNumberOfEdges) - optimalEdgeLength); //0 is optimal
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public static double distanceFormula(int x, int y, int toX, int toY) {
		return Math.sqrt(Math.pow((x - toX), 2) + Math.pow((y - toY), 2));
	}
	
	public Graph copy() throws Exception { // AWESOME DEEP-COPY
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(this);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		return (Graph) ois.readObject();
	}
}
