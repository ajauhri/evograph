package graph;

import java.util.Collections;
import java.util.Vector;

import evograph.EvoGraph;
import evograph.GraphCanvas;

public class GraphInstance {
	public double fitness;
	public double edgeFitness;
	public double angularResolution;
	public double nodeSeparation;
	public double edgeTunneling;
	public double orthogonality;
	public Graph graph;
	public NodeInstance[] nodeInstances;
	public int numberOfEdgeCrossings;
	public int age = 0;
	
	public GraphInstance(Graph graph) {
		this.graph = graph;
		if(graph != null) {
			nodeInstances = new NodeInstance[graph.nodes.length];
			for(int i = 0; i < graph.nodes.length; i++)
				nodeInstances[i] = new NodeInstance(graph.nodes[i], graph.nodes.length);
		}
	}
	
	public String printCoordinates() {
		String printString = "" + nodeInstances.length;
		for (int i = 0; i < nodeInstances.length; i++)
			printString += "\n" + nodeInstances[i].realX + " " + nodeInstances[i].realY;
		return printString;
	}
	
	public String printOrientations() {
		String printString = "\n***** Orientations \n";
		for (int i = 6; i < nodeInstances.length; i += 3) {
			printString += orientBit(nodeInstances[i - 6], nodeInstances[i - 3], nodeInstances[i]);
		}
		printString += "\n";
		for (int i = 7; i < nodeInstances.length; i += 3) {
			printString += orientBit(nodeInstances[i - 6], nodeInstances[i - 3], nodeInstances[i]);
		}
		printString += "\n";
		for (int i = 8; i < nodeInstances.length; i += 3) {
			printString += orientBit(nodeInstances[i - 6], nodeInstances[i - 3], nodeInstances[i]);
		}
		printString += "\n";
		return printString;
	}
	
	public int orientBit(NodeInstance n1, NodeInstance n2, NodeInstance n3) {
    	return EvoGraph.orient(n1.realX, n1.realY, n2.realX, n2.realY, n3.realX, n3.realY) > 0 ? 1 : 0;
    }
	
	public void centerGraph() {
		double averageX = 0;
		double averageY = 0;
		for (NodeInstance n : nodeInstances) {
			averageX += n.x;
			averageY += n.y;
		}
		averageX /= nodeInstances.length;
		averageY /= nodeInstances.length;
		int canvasWidth = GraphCanvas.canvasWidth;
		int canvasHeight = GraphCanvas.canvasHeight;
		int deltaX = (int) ((canvasWidth / 2) - averageX);
		int deltaY = (int) ((canvasHeight / 2) - averageY);
		for (NodeInstance n : nodeInstances) {
			n.x = EvoGraph.boundaryChecker(n.x + deltaX, canvasWidth);
			n.y = EvoGraph.boundaryChecker(n.y + deltaY, canvasHeight);
		}		
	}
	
	public void calculateNodeDistances() {
		for (int i = 0; i < nodeInstances.length; i++)
			nodeInstances[i].nodeDistances = new double[nodeInstances.length];
		for (int i = 0; i < nodeInstances.length - 1; i++) {
			for (int j = i + 1; j < nodeInstances.length; j++) {
				double distance = Graph.distanceFormula(nodeInstances[i].x, nodeInstances[i].y, nodeInstances[j].x, nodeInstances[j].y);
				nodeInstances[i].nodeDistances[j] = distance;
				nodeInstances[j].nodeDistances[i] = distance;
			}
		}
	}

	public void calculateEdgeAngles() {
		for (int i = 0; i < nodeInstances.length - 1; i++) {
			for (Node n : nodeInstances[i].node.connectedNodes.values()) {
				if(n.id < i)
					continue;
				double angle = EvoGraph.calculateAngle(nodeInstances[n.id].x, nodeInstances[n.id].y, nodeInstances[i].x, nodeInstances[i].y);
				nodeInstances[i].edgeAngles.put(n.id, angle);
				nodeInstances[n.id].edgeAngles.put(i, EvoGraph.flipAngle(angle));
			}
		}
	}
	
	public void calculateNumberOfEdgeCrossings() {
		numberOfEdgeCrossings = 0;
		for (int i = 0; i < nodeInstances.length - 1; i++) {
			NodeInstance nodei = nodeInstances[i];
			for (int j = i + 1; j < nodeInstances.length; j++) {
				NodeInstance nodej = nodeInstances[j];
				for (Node cni : nodei.node.connectedNodes.values()) {
					if (cni.id < nodei.id)
						continue;
					NodeInstance nodei2 = nodeInstances[cni.id];
					for (Node cnj : nodej.node.connectedNodes.values()) {
						if (cnj.id < nodej.id)
							continue;
						NodeInstance nodej2 = nodeInstances[cnj.id];
						if (EvoGraph.checkEdgeCrossing(nodei, nodei2, nodej, nodej2)) {
							numberOfEdgeCrossings++;
						}
					}
				}
			}
		}
	}
	
	public void calculateKFitness() {
		calculateNumberOfEdgeCrossings();
		fitness = numberOfEdgeCrossings;
	}

	public void calculateFitness() {
		calculateNodeDistances();
		calculateEdgeAngles();
		
		calculateNumberOfEdgeCrossings();
		calculateAngularResolution();
		calculateEdgeFitness();
		calculateNodeSeparation();
		calculateEdgeTunneling();
		//fitness = (double) (numberOfEdgeCrossings + 1) * (edgeFitness + angularResolution + nodeSeparation + edgeTunneling + orthogonality + 1);
		fitness = ((numberOfEdgeCrossings * EvoGraph.edgeCrossingsMultiplier) + 1) *
					(edgeFitness * EvoGraph.edgeFitnessMultiplier + 
					angularResolution * EvoGraph.angularResolutionMultiplier + 
					nodeSeparation * EvoGraph.nodeSeparationMultiplier + 
					edgeTunneling * EvoGraph.edgeTunnelingMultiplier +
					orthogonality * EvoGraph.orthogonalityMultiplier + 1);
	}

	public void calculateAngularResolution() {
		angularResolution = 0;
		for (int i = 0; i < nodeInstances.length; i++) {
			int nEdges = nodeInstances[i].edgeAngles.size();
			double optimalAngle = (2 * Math.PI) / nEdges;
			Vector<Double> angles = new Vector<Double>();
			angles.addAll(nodeInstances[i].edgeAngles.values());
			Collections.sort(angles);
			double nodeResolution = 0;
			for (int j = 0; j < nEdges; j++) {
				double angle1 = angles.get(j);
				double angle2 = j == nEdges - 1 ? angles.get(0) + (2 * Math.PI) : angles.get(j + 1); 
				nodeResolution += angularResolutionPenalty(optimalAngle - (angle2 - angle1), nEdges);
			}
			angularResolution += nodeResolution;
		}
	}

	public  static double angularResolutionPenalty(double angle, int nEdges) {
		return Math.pow(angle, 2) / (nEdges * Math.pow(2 * Math.PI / nEdges, 2));
	}
	
	public void calculateNodeSeparation() {
		nodeSeparation = 0;
		for (int i = 0; i < nodeInstances.length - 1; i++) {
			for (int j = i + 1; j < nodeInstances[i].nodeDistances.length; j++) {
				if (!nodeInstances[i].node.connectedNodes.containsKey(j) && nodeInstances[i].nodeDistances[j] < GraphCanvas.optimalEdgeLength)
					nodeSeparation += nodeSeparationPenalty(nodeInstances[i].nodeDistances[j]);
			}
		}
	}
	
	public  static double nodeSeparationPenalty(double nodeDistance) {
		return Math.pow(GraphCanvas.optimalEdgeLength - nodeDistance, 2) / Math.pow(GraphCanvas.optimalEdgeLength, 2);
	}
	
	public void calculateEdgeTunneling() {
		//System.out.println();
		//System.out.println("=================");
		edgeTunneling = 0;
		for (int i = 0; i < nodeInstances.length; i++) {
			for (int j = 0; j < nodeInstances.length; j++) {
				if (i == j)
					continue;
				for(Node n : nodeInstances[j].node.connectedNodes.values()) {
					if (n.id == i || n.id < j)
						continue;
					double distance = EvoGraph.distanceToSegment(nodeInstances[i].x, nodeInstances[i].y, nodeInstances[j].x, nodeInstances[j].y, nodeInstances[n.id].x, nodeInstances[n.id].y);
					//System.out.println("Node " + i + " is distance " + distance + " from edge (" + j + ", " + n.id + ")");
					edgeTunneling += edgeTunnelingPenalty(distance);
				}
				
			}
		}
	}
	
	public static double edgeTunnelingPenalty(double distance) {
		double minNT = 30;
		if (distance > minNT)
			return 0;
		else
			return Math.pow(minNT - distance, 2) / Math.pow(minNT, 2);
	}
	
	public void calculateEdgeFitness() {
		edgeFitness = 0;
		orthogonality = 0;
		for (int i = 0; i < nodeInstances.length; i++) {
			for (Node n : nodeInstances[i].node.connectedNodes.values()) {
				if (n.id > i) {
					edgeFitness += edgeFitnessPenalty(nodeInstances[i].nodeDistances[n.id]);
					orthogonality += 0.1 * orthogonalityPenalty(nodeInstances[i].edgeAngles.get(n.id));
				}
			}
		}
	}
	
	public static double edgeFitnessPenalty(double edgeLength) {
		double maxLength = Math.sqrt(Math.pow(GraphCanvas.canvasHeight, 2) + Math.pow(GraphCanvas.canvasWidth, 2));  
		double unscaledPenalty = Math.pow(GraphCanvas.optimalEdgeLength - edgeLength, 2);
		if (edgeLength > GraphCanvas.optimalEdgeLength)
			return unscaledPenalty / Math.pow(maxLength, 2);
		else
			return unscaledPenalty / Math.pow(GraphCanvas.optimalEdgeLength, 2);
	}
	
	public static double orthogonalityPenalty(double angle) {
		if (angle < Math.PI / 4) {
			return 1 - Math.pow(1 - (angle / (Math.PI / 4)), 2);
		} else if (angle < 3 * Math.PI / 4) {
			return 1 - Math.pow(1 - (Math.abs(angle - (Math.PI / 2))) / (Math.PI / 4), 2);
		} else if (angle < 5 * Math.PI / 4) {
			return 1 - Math.pow(1 - (Math.abs(angle - Math.PI)) / (Math.PI / 4), 2);
		} else if (angle < 7 * Math.PI / 4) {
			return 1 - Math.pow(1 - (Math.abs(angle - (3 * (Math.PI / 2)))) / (Math.PI / 4), 2);
		} else {
			return 1 - Math.pow(1 - (2 * Math.PI - angle) / (Math.PI / 4), 2);
		}
	}
}
