package evograph;

import graph.FileToGraph;
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

import algorithms.IncrementalGraphAlgorithm;
import algorithms.KGraphHeuristic;
import algorithms.generationBased.ALPS;
import algorithms.generationBased.GeneticAlgorithm;
import algorithms.iterationBased.HillClimber;
import algorithms.iterationBased.SimulatedAnnealing;

@SuppressWarnings("unused")
public class EvoGraph extends JApplet implements ActionListener {
	private static final long serialVersionUID = 1L;
	GraphCanvas canvas;
	JButton nextButton;
	JLabel statusBar;
	IncrementalGraphAlgorithm algorithm;
	public static double angularResolutionMultiplier = 1;
	public static double edgeFitnessMultiplier = 1;
	public static double edgeTunnelingMultiplier = 0;
	public static double edgeCrossingsMultiplier = 1000;
	public static double nodeSeparationMultiplier = 1;
	public static double orthogonalityMultiplier = 0;
	
	public static String rgf = "k20";
	public static double optimalFitness = 5.01;
	
	public static int[] cnLowerBounds = {0,0,0,0,0,1,3,9,19,36,62,102,153,229,324,447,603,
										798,1029,1318,1657,2055,2528,3077,3699,4430,5250,6180,
										7233,8419,9723,11207,12827,14626,16580,18776,21123,23759,
										26569,29661,32987,36632,40488,44744,49238,54117,59311,64933,
										70836,77268,84012}; //50
	
	public Graph rawGraph;
	public LinkedList<Double> readings;
	public int nRestarts = 0;
	public int queueLength = 25;
	public DataCollector dataCollector;
	public long startTime;
	public Clock clock;
	
	public boolean optimalFound = false;
	public boolean converged = false;
	
	public void init() {
		clock = new Clock();
		readings = new LinkedList<Double>();
		rawGraph = new FileToGraph(rgf + ".rgf").createGraph();
		algorithm = new KGraphHeuristic(rawGraph);
		//algorithm = new GeneticAlgorithm(rawGraph);
		//algorithm = new SimulatedAnnealing(new FileToGraph("david-fig11.rgf").createGraph());
		//algorithm = new HillClimber(new FileToGraph("complex-octo.rgf").createGraph());
		//algorithm = new ALPS(rawGraph);
		createGUI();
	}

	public void createGUI() {
		canvas = new GraphCanvas(this);
		nextButton = new JButton("Next");
		nextButton.addActionListener(this);
		statusBar = new JLabel(" ");
	    getContentPane().add(canvas, BorderLayout.CENTER);
	    getContentPane().add(nextButton, BorderLayout.SOUTH);
	    getContentPane().add(statusBar, BorderLayout.NORTH);
		this.resize(900, 750);
	}
	
	public void next() {
		canvas.setCanvasWidthAndHeight();
		canvas.calculateOptimalEdgeLength();
		
//		clock.init();
//		for (int i = 0; i < 5; i++)
//			algorithm.next();
//		System.out.println("total time for 5 runs = " + clock.diff() + " ms");
		
		//runAllAlgorithms(1);
		
		runKGraphs(20, 50, 50); //starting k, ending k, maximum # runs

//		for (int i = 0; i < 5; i++)
//			algorithm.next();
//		for (int i = 0; i < 1000; i++) {
//			algorithm.restart();
//			dataCollector = new DataCollector(algorithm.getClass().getSimpleName(), rgf, i + 1);
//			do {
//				algorithm.next();
//			} while (checkConverged(algorithm.displayGraph()));
//			takeReading(algorithm.displayGraph());
//			dataCollector.close();
//		}
	//System.out.println("total time for 10 runs = " + clock.diff() + " ms");

		canvas.drawGraph(algorithm.displayGraph());
		statusBar.setText(algorithm.displayText());
		//checkOptimalFound();
//		}
	}
	
	public void runKGraphs(int first, int last, int maxRuns) {
		int run;
		boolean foundOptimal;
		GraphInstance[] bestFound = new GraphInstance[last - first + 1];
		for (int i = first; i <= last; i++) {
			run = 0;
			algorithm = new KGraphHeuristic(new FileToGraph("k" + i + ".rgf").createGraph());
			KDataCollector dc = new KDataCollector("k" + i);
			while(run < maxRuns) {
				run++;
				readings.clear();
				queueLength = 30 - (i / 2);
				converged = false;
				GraphInstance graph;
				do {
					algorithm.next();
					graph = algorithm.displayGraph();
				} while (!konverged(graph, i));
				dc.writeLine("K" + i + " run " + run + " converged to " + graph.numberOfEdgeCrossings + " in " + (algorithm.getRuns() - readings.size() + 1) + " generations");
				if(bestFound[i - first] == null || graph.numberOfEdgeCrossings < bestFound[i - first].numberOfEdgeCrossings) {
					bestFound[i - first] = graph;
					if(graph.numberOfEdgeCrossings <= cnLowerBounds[i]) {
						dc.writeLine("Lower bound for K" + i + " found (" + graph.numberOfEdgeCrossings + ")");
						break;
					}
				}
				algorithm.restart();
			}
			dc.close();
			dc = new KDataCollector("best-k" + i);
			dc.writeLine(bestFound[i - first].printCoordinates(), false);
			dc.close();
		}
	}
	
	public void runAllAlgorithms(int nRuns) {
		//queueLength = 10;
		//algorithm = new GeneticAlgorithm(rawGraph);
		//runUntilOptimalFound(nRuns);	

//		queueLength = 100;
//		algorithm = new SimulatedAnnealing(rawGraph);
//		runUntilOptimalFound(nRuns);
//
//		queueLength = 100;
//		algorithm = new HillClimber(rawGraph);
//		runUntilOptimalFound(nRuns);

		queueLength = 10;
		algorithm = new ALPS(rawGraph);
		runUntilOptimalFound(nRuns);
	}
	
	public void runUntilOptimalFound(int nRuns) {
		System.out.println("Beginning runs for " + algorithm.getClass().getSimpleName());
		for (int i = 0; i < nRuns; i++) {
			startTime = System.currentTimeMillis();
			System.out.println("Run #" + (i + 1));
			readings.clear();
			algorithm.restart();
			dataCollector = new DataCollector(algorithm.getClass().getSimpleName(), rgf, i + 1);
			do {
				algorithm.next();
				if (algorithm.getRuns() % 1000 == 0)
					takeReading(algorithm.displayGraph());
				checkOptimalFound();
			} while(!optimalFound);
			dataCollector.close();
			nRestarts = 0;
		}
		System.out.println("Finished runs for " + algorithm.getClass().getSimpleName());
	}
	
	public void checkOptimalFound() {
		optimalFound = algorithm.displayGraph().fitness <= optimalFitness;
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
	

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == nextButton)
			next();
	}
	
	public void updateGraph() {
		algorithm.updateGraph();
		statusBar.setText(algorithm.displayText());
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
		//return Line2D.linesIntersect(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y);
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
}