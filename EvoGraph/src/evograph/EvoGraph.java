package evograph;

import graph.Graph;
import graph.GraphInstance;
import graph.NodeInstance;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.LinkedList;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;

import utils.Clock;
import utils.DataCollector;
import utils.FileToGraph;

import algorithms.IncrementalGraphAlgorithm;
import algorithms.generationBased.ALPS;
import algorithms.generationBased.GeneticAlgorithm;
import algorithms.iterationBased.HillClimber;
import algorithms.iterationBased.SimulatedAnnealing;
import algorithms.kgraph.KGraphGA;
import algorithms.kgraph.KGraphHeuristic;
import algorithms.kgraph.KGraphSA;

@SuppressWarnings("unused")
public class EvoGraph extends JApplet {
	private static final long serialVersionUID = 1L;
	
	IncrementalGraphAlgorithm algorithm;
	public static double angularResolutionMultiplier = 1;
	public static double edgeFitnessMultiplier = 1;
	public static double edgeTunnelingMultiplier = 1;
	public static double edgeCrossingsMultiplier = 1;
	public static double nodeSeparationMultiplier = 1;
	public static double orthogonalityMultiplier = 0;
	public static double optimalEdgeLength;
	public static int canvasWidth;
	public static int canvasHeight;
	
	public boolean converged = false;
	
	public static int[] cnLowerBounds = {0,0,0,0,0,1,3,9,19,36,62,102,153,229,324,447,603,
										798,1029,1318,1657,2055,2528,3077,3699,4430,5250,6180,
										7233,8419,9723,11207,12827,14626,16580,18776,21123,23759,
										26569,29661,32987,36632,40488,44744,49238,54117,59311,64933,
										70836,77268,84012}; //50
	public LinkedList<Double> readings;
	public int nRestarts = 0;
	public int queueLength = 25;
	public DataCollector dataCollector;
	public long startTime;
	public Clock clock;
	
	
	public void init() {
		clock = new Clock();
	}

	public Graph testGraph() { //complex-octo
		Graph g = new Graph(13);
		g.createEdge(0, 1);
		g.createEdge(1, 2);
		g.createEdge(2, 3);
		g.createEdge(3, 4);
		g.createEdge(4, 5);
		g.createEdge(5, 6);
		g.createEdge(6, 7);
		g.createEdge(7, 0);
		g.createEdge(8, 9);
		g.createEdge(8, 10);
		g.createEdge(8, 11);
		g.createEdge(8, 12);
		g.createEdge(9, 0);
		g.createEdge(9, 1);
		g.createEdge(10, 2);
		g.createEdge(10, 3);
		g.createEdge(11, 4);
		g.createEdge(11, 5);
		g.createEdge(12, 6);
		g.createEdge(12, 7);
		return g;
	}
	
	public void calculateOptimalEdgeLength() {
		optimalEdgeLength = Math.sqrt(canvasWidth * canvasHeight / Graph.nNodes);
	}
	
	public boolean konverged(GraphInstance graph, int k) {
		if (readings.size() < queueLength) {
			readings.add(graph.fitness);
			if(graph.numberOfEdgeCrossings <= cnLowerBounds[k]) {
				return true;
			}
		} else {
			readings.removeFirst();
			readings.add(graph.fitness);
			if(readings.getLast() - readings.getFirst() == 0 || graph.numberOfEdgeCrossings <= cnLowerBounds[k])
				return true;
		}
		return false;
	}
	
	public boolean checkConverged(GraphInstance graph) {
		if (readings.size() < queueLength) {
			readings.add(graph.fitness);
			return true;
		} else {
			readings.removeFirst();
			readings.add(graph.fitness);
			if(readings.getLast() < readings.getFirst() * 0.995) 
				return true;
		}
		return false;
	}
	
	public void restartAlgorithm() {
		readings.clear();
		nRestarts++;
		System.out.println("Restart #" + nRestarts + " after " + algorithm.getRuns() + " runs");
		algorithm.restart();
	}
	
	public void takeReading(GraphInstance graph) {
		String reading = String.format("%.3f", graph.fitness) + 
		" " + graph.numberOfEdgeCrossings +
		" " + String.format("%.3f", graph.edgeFitness) +
		" " + String.format("%.3f", graph.angularResolution) +
		" " + String.format("%.3f", graph.nodeSeparation) +
		" " + String.format("%.3f", graph.edgeTunneling); 
		System.out.println(reading);
		dataCollector.writeReading(reading);
	}

	/** Static Helper functions **/
	
	public static int boundaryChecker(int coordinate, int maximum) {
		if(coordinate > maximum)
			return maximum;
		else if(coordinate < 0)
			return 0;
		else
			return coordinate;
	}
	
	public static boolean probability(double chance) {
		return Math.random() < chance;
	}

	public static int randomInt(int min, int max) {
		 return (int) Math.random() * (max - min + 1) + min;
	}
	
	public static double toDegrees(double radians) {
		return (radians / (2 * Math.PI)) * 360;
	}
	

	public static double flipAngle(double angle) {
		return angle > Math.PI ? angle - Math.PI : angle + Math.PI;
	}
	
	public static double calculateAngle(int x1, int y1, int x2, int y2) {
		if (x1 == x2 && y1 == y2)
			return 0;
		double angle;
		angle = Math.atan(((double) (y2 - y1))/((double) (x2 - x1)));
		if(x2 < x1)
			angle += Math.PI;
		angle += (Math.PI / 2);
        return angle;
	}
	
	public static int[] calculateCoordinatesFromPointAngleDistance(int x, int y, double angle, double distance) {
		int[] coords = new int[2];
		angle -= (Math.PI / 2);
		coords[0] = x + (int) (distance * Math.cos(angle));
		coords[1] = y + (int) (distance * Math.sin(angle));
		return coords;
	}
	
	/**
	 * Check if the edge between a-b intersects with the edge between c-d
	 */
	public static boolean checkEdgeCrossing(NodeInstance a, NodeInstance b, NodeInstance c, NodeInstance d) {
		if (b == c || d == a || b == d)
			return false;
		return Line2D.linesIntersect(a.realX, a.realY, b.realX, b.realY, c.realX, c.realY, d.realX, d.realY);
	}
	
	/**
	 * Calculates the distance from point (x, y) to the edge from (x1, y1) to (x2, y2)
	 */
    public static double distanceToSegment(double x, double y, double x1, double y1, double x2, double y2) {
    	double xDelta = x2 - x1;
    	double yDelta = y2 - y1;
    	if ((xDelta == 0) && (yDelta == 0))
        	return Graph.distanceFormula(x, y, x1, y1);
    	double u = ((x - x1) * xDelta + (y - y1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);
    	double closestPointX, closestPointY;
    	if (u < 0) {
    	    closestPointX = x1;
    	    closestPointY = y1;
    	} else if (u > 1) {
    	    closestPointX = x2;
    	    closestPointY = y2;
    	} else {
    	    closestPointX = x1 + u * xDelta;
    	    closestPointY = y1 + u * yDelta;
    	}
    	return Graph.distanceFormula(x, y, closestPointX, closestPointY);
    }
    
    public static int orient(int x1, int y1, int x2, int y2, int x3, int y3) {
		return ((x1 * y2) + (y1 * x3) + (x2 * y3)) - ((y1 * x2) + (x1 * y3) + (y2 * x3));
    }
    
    public static double randomDouble(double min, double max) {
    	return Math.random() * Math.abs(max - min) + Math.min(max, min);
    }
}