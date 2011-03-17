package graph;

import java.awt.geom.Line2D;
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
		fitness += getNumberOfEdgeCrossings();
		//fitness = Math.abs((sumOfEdgeLengths / totalNumberOfEdges) - optimalEdgeLength); //0 is optimal
	}
	
	public int getNumberOfEdgeCrossings() {
		int edgeCrossings = 0;
		int numberOfNodes = getNumberOfNodes();
		for (int i = 0; i < numberOfNodes - 1; i++) {
			Node nodei = getNodeAt(i);
			Object[] edgesi = nodei.getEdges();
			for (int j = i + 1; j < numberOfNodes; j++) {
				Node nodej = getNodeAt(j);
				Object[] edgesj = nodej.getEdges();
				for (Object edgei : edgesi) {
					for (Object edgej : edgesj) {
						if (checkEdgeCrossing(nodei, ((Edge) edgei).to, nodej, ((Edge) edgej).to)) {
							//System.out.println(nodei.id + " to " + ((Edge) edgei).to.id + " is intersecting with " + nodej.id + " to " + ((Edge) edgej).to.id); 
							edgeCrossings++;
						}
					}
				}
			}
		}
		return edgeCrossings;
	}
	
	/**
	 * Check if the edge between a-b intersects with the edge between c-d
	 */
	public boolean checkEdgeCrossing(Node a, Node b, Node c, Node d) {
		if (b == c || d == a || b == d)
			return false;
		return Line2D.linesIntersect(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y);
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
