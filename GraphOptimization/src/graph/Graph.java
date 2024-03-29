package graph;

import java.awt.geom.Line2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import metadata.Constants;

public class Graph implements Serializable {
	private static final long serialVersionUID = 1L;
	// public Fitness fitness;
	public double fitness;
	public Edge[][] edges;
	public int numberOfEdgeCrossings = 0;
	public int numberOfNodes;
	public static double optimalEdgeLengthLower;
	public static double optimalEdgeLengthUpper;
	Vector<Node> nodes = new Vector<Node>(); // ID of the node is the position
												// in the vector

	public Graph(int numberOfNodes) {
		edges = new Edge[numberOfNodes][numberOfNodes];
		this.numberOfNodes = numberOfNodes;
		for (int i = 0; i < numberOfNodes; i++)
			nodes.add(new Node(i, this));
		//calculateOptimalEdgeLength();
	}

	public void calculateOptimalEdgeLength() {
		double canvasArea = Constants.CANVAS_WIDTH * Constants.CANVAS_HEIGHT;
		double optimalEdgeLength = Math.sqrt(canvasArea / this.numberOfNodes);
		optimalEdgeLengthLower = optimalEdgeLength * Constants.OPTIMAL_EDGE_LENGTH_LENIENCY;
		optimalEdgeLengthUpper = optimalEdgeLength * (1 + Constants.OPTIMAL_EDGE_LENGTH_LENIENCY);
		
	}
	
	public void createEdge(int fromId, int toId) {
		Edge edge = nodes.get(fromId).createEdge(nodes.get(toId));
		edges[fromId][toId] = edge;
	}

	public void printCoordinatesAndAngles() {
		int numNodes = nodes.size();
		for (int i = 0; i < numNodes; i++)
			nodes.get(i).printCoordinatesAndAngles();
	}
	
	public void printConnections() {
		int numNodes = nodes.size();
		for (int i = 0; i < numNodes; i++)
			nodes.get(i).printConnections();
	}
	
	public void printEdgeMatrix() {
		for(int i = 0; i < numberOfNodes; i++) {
			for(int j = 0; j < numberOfNodes; j++) {
				System.out.print(edges[i][j] == null ? "0 " : "1 ");
			}
			System.out.println();
		}
	}

	public Node getNodeAt(int pos) {
		return nodes.get(pos);
	}

	public void calculateFitness() {
		// double sumOfEdgeLengths = 0;
		// int totalNumberOfEdges = 0;
		fitness = 0;
		calculateNumberOfEdgeCrossings();
		//calculateCrossoversToEdgesRatios();
		for (int i = 0; i < numberOfNodes; i++) {
			this.getNodeAt(i).calculateEdgeAngles();
		}
		double angularResolutionSum = 0.0;
		double edgeFitnessSum = 0.0;
		
		
		for (int i = 0; i < numberOfNodes; i++) {
			Node node = this.getNodeAt(i);
			node.calculateFitness();
			angularResolutionSum += node.angularResolution;
			edgeFitnessSum += node.edgeFitness;
			
			//System.out.println("fitness = " + fitness);
		}
		fitness = (numberOfEdgeCrossings + 1)
				* ((angularResolutionSum * Constants.ANGULAR_RESOLUTION_MULTIPLIER) +
						(edgeFitnessSum * Constants.EDGE_FITNESS_MULTIPLIER));

		//fitness = edgeFitness * (numberOfEdgeCrossings + 1);
		// fitness = Math.abs((sumOfEdgeLengths / totalNumberOfEdges) -
		// optimalEdgeLength); //0 is optimal
	}
	
	public void calculateEdgeLengths() {
		for (int i = 0; i < numberOfNodes; i++) {
			Node node = this.getNodeAt(i);
			node.calculateEdgeLengths();
		}
	}
	
	public void calculateNumberOfEdgeCrossings() {
		numberOfEdgeCrossings = 0;
		for (int i = 0; i < numberOfNodes; i++)
			getNodeAt(i).numberOfCrossovers = 0;
		for (int i = 0; i < numberOfNodes - 1; i++) {
			Node nodei = getNodeAt(i);
			Object[] edgesi = nodei.getEdgesOut();
			for (int j = i + 1; j < numberOfNodes; j++) {
				Node nodej = getNodeAt(j);
				Object[] edgesj = nodej.getEdgesOut();
				for (Object edgei : edgesi) {
					for (Object edgej : edgesj) {
						if (checkEdgeCrossing(nodei, ((Edge) edgei).to, nodej, ((Edge) edgej).to)) {
							numberOfEdgeCrossings++;
						}
					}
				}
			}
		}
	}
	
	public void calculateCrossoversToEdgesRatios() {
		for (int i = 0; i < numberOfNodes - 1; i++) {
			getNodeAt(i).calculateCrossoversToEdgesRatio();
		}
	}
	public Node getNodeWithHighestCrossoverRatio() {
		Node nodeWithHighestCrossoverRatio = this.getNodeAt(0);
		for (int i = 1; i < this.numberOfNodes; i++) {
			if (nodeWithHighestCrossoverRatio.crossoversToEdgesRatio <= this.getNodeAt(i).crossoversToEdgesRatio)
				nodeWithHighestCrossoverRatio = this.getNodeAt(i);
		}
		return nodeWithHighestCrossoverRatio;
	}

	/**
	 * Check if the edge between a-b intersects with the edge between c-d
	 */
	public boolean checkEdgeCrossing(Node a, Node b, Node c, Node d) {
		if (b == c || d == a || b == d)
			return false;
		boolean crossing = Line2D.linesIntersect(a.x, a.y, b.x, b.y, c.x, c.y,
				d.x, d.y);
		if (crossing) {
			a.numberOfCrossovers++;
			b.numberOfCrossovers++;
			c.numberOfCrossovers++;
			d.numberOfCrossovers++;
		}
		return crossing;
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