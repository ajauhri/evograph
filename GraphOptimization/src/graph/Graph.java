package graph;

import java.util.Vector;
import evo.Fitness;

public class Graph implements Cloneable {
	public Fitness fitness;
	Vector<Node> nodes = new Vector<Node>(); //ID of the node is the position in the vector
	
	public Graph(int numberOfNodes) {
		for (int i = 0; i < numberOfNodes; i++)
			nodes.add(new Node(i));
		fitness = new Fitness(this);
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
		fitness.calculate();
	}
	
	public static double distanceFormula(int x, int y, int toX, int toY) {
		return Math.sqrt(Math.pow((x - toX), 2) + Math.pow((y - toY), 2));
	}
	
	public Graph copy() {
		try {
			//for(int i=0; this.getNumberOfNodes(); i++) 
			return (Graph) this.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("CLONE NOT SUPPORTED");
			return null;
		}
	}
}
