package graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Node implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	int x;
	int y;
	double edgeFitness;
	double angularResolution; //angularFitness
	public double fitness;
	public int numberOfCrossovers;
	public float crossoversToEdgesRatio;
	HashMap<Integer, Edge> edgesOut = new HashMap<Integer, Edge>();
	HashMap<Integer, Edge> edgesIn = new HashMap<Integer, Edge>();
	public Graph graph;

	public Node(int id, Graph graph) {
		this.graph = graph;
		this.id = id;
		this.numberOfCrossovers = 0;
	}

	public Edge createEdge(Node n) {
		Edge edge = new Edge(n, this);
		edgesOut.put(n.id, edge);
		n.edgesIn.put(this.id, edge);
		return edge;
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

	public void calculateFitness() {
		//calculateCrossoversToEdgesRatio();
		calculateEdgeLengths();
		calculateAngularResolution();
//		double offsetA = 0.1;
//		double offsetB = 0.1;
//		fitness = (edgeFitness + offsetA) * (crossoversToEdgesRatio + offsetB) - (offsetA * offsetB);
//		return fitness;
	}
	
	public void calculateAngularResolution() {
		angularResolution = 0;
		ArrayList<Double> edgesByAngle = new ArrayList<Double>();
		Object[] edges = this.getEdgesOut();
		for (Object e : edges) {
			Edge edge = (Edge) e;
			edgesByAngle.add(edge.angle);
		}
		edges = this.getEdgesIn();
		for (Object e : edges) {
			Edge edge = (Edge) e;
			if (this.graph.edges[edge.to.id][edge.from.id] == null) {
				double angle = edge.angle > Math.PI ? edge.angle - Math.PI : edge.angle + Math.PI;
				edgesByAngle.add(angle);
			}
		}
		Collections.sort(edgesByAngle);
		double minimumAngle = ((Math.PI * 2 * 4) / this.numberOfEdgesInAndOut());
		for (int i = 0; i < edgesByAngle.size() - 1; i++) {
			double angle = edgesByAngle.get(i + 1) - edgesByAngle.get(i);
			if(angle < minimumAngle) {
				angularResolution += minimumAngle - angle;
			}
		}
	}
	
	public double toDegrees(double radians) {
		return (radians/(2*Math.PI))*360;
	}
	
	public void calculateEdgeAngles() {
		Object[] edges = this.getEdgesOut();
		for (Object e : edges) {
			calculateEdgeAngle((Edge) e);
		}
	}
	
	public void calculateEdgeAngle(Edge edge) {
		double angle;
		try {
			angle = Math.atan(((double) (edge.to.y - edge.from.y))/((double) (edge.to.x - edge.from.x)));
			if(edge.to.x < edge.from.x)
				angle += Math.PI;
			angle += (Math.PI / 2);
		} catch (ArithmeticException exc) {
			if (edge.to.y > this.y)
				angle = (Math.PI / 2);
			else
				angle = 3 * (Math.PI / 2);
		}
        edge.angle = angle;
	}
	
	public void calculateCrossoversToEdgesRatio() {
		//System.out.println("Numberofcrossover: " + numberOfCrossovers + " numberofEdgesInandOut: "+ numberOfEdgesInAndOut());
		crossoversToEdgesRatio = numberOfCrossovers / numberOfEdgesInAndOut();
	}
	
	public void calculateEdgeLengths() {
		edgeFitness = 0;
		Object[] edges = this.getEdgesOut();
		for (Object e : edges) {
			double edgeLength = ((Edge) e).computeEdgeLength(this.getX(), this.getY());
			edgeFitness += calculateEdgeFitness(edgeLength);
		}
		edges = this.getEdgesIn();
		for (Object e : edges) {
			double edgeLength = ((Edge) e).computeEdgeLength(this.getX(), this.getY());
			edgeFitness += calculateEdgeFitness(edgeLength);	
		}
		edgeFitness /= numberOfEdgesInAndOut();
	}

	public double calculateEdgeFitness(double edgeLength) {
		if (edgeLength < Graph.optimalEdgeLengthLower)
			return (Graph.optimalEdgeLengthLower - edgeLength);
		else if (edgeLength > Graph.optimalEdgeLengthUpper)
			return (edgeLength - Graph.optimalEdgeLengthUpper);
		return 0;
	}
	
	public void printConnections() {
		System.out.print("Node " + this.id + " is connected to ");
		for (Map.Entry<Integer, Edge> entry : edgesOut.entrySet())
			System.out.print(entry.getKey() + ", ");
		System.out.println();
	}
	
	public void printCoordinatesAndAngles() {
		System.out.println("*** this.id = " + this.id);
		Object[] edges = this.getEdgesOut();
		for (Object e : edges) {
			Edge edge = (Edge) e;
			System.out.println("from " + edge.from.id + " to " + edge.to.id + ", angle " + toDegrees(edge.angle));
			System.out.println(edge.from.x + "," + edge.from.y + " to " + edge.to.x + "," + edge.to.y);
		}
		edges = this.getEdgesIn();
		for (Object e : edges) {
			Edge edge = (Edge) e;
			if (this.graph.edges[edge.to.id][edge.from.id] == null) {
				double angle = edge.angle;// > Math.PI ? edge.angle - Math.PI : edge.angle + Math.PI;
				System.out.println("from " + edge.from.id + " to " + edge.to.id + " makes angle " + toDegrees(angle));
				System.out.println(edge.from.x + "," + edge.from.y + " to " + edge.to.x + "," + edge.to.y);
			} else {
				System.out.println("There was already an edge from " + edge.from.id + " to " + edge.to.id);
			}
		}
		System.out.println("angularResolution = " + angularResolution);
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

	public Object[] getEdgesIn() {
		return edgesIn.values().toArray();
	}

	public void setXandY(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
