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

import algorithms.IncrementalGraphAlgorithm;
import algorithms.generationBased.ALPS;
import algorithms.generationBased.GeneticAlgorithm;
import algorithms.iterationBased.HillClimber;
import algorithms.iterationBased.SimulatedAnnealing;


public class EvoGraph extends JApplet implements ActionListener {
	private static final long serialVersionUID = 1L;
	GraphCanvas canvas;
	JButton nextButton;
	JLabel statusBar;
	IncrementalGraphAlgorithm algorithm;
	public static double angularResolutionMultiplier = 1;
	public static double edgeFitnessMultiplier = 1;
	public static double edgeTunnelingMultiplier = 1;
	public static double edgeCrossingsMultiplier = 1;
	public static double nodeSeparationMultiplier = 1;
	public static double orthogonalityMultiplier = 0;
	
	public static String rgf = "grid16";
	public static double optimalFitness = 3.0;
	
	public Graph rawGraph;
	public LinkedList<Double> readings;
	public int nRestarts = 0;
	public int queueLength = 10;
	public DataCollector dataCollector;
	public long startTime;

	public void init() {
		readings = new LinkedList<Double>();
		rawGraph = new FileToGraph(rgf + ".rgf").createGraph();
		//algorithm = new GeneticAlgorithm(new FileToGraph("spider-web.rgf").createGraph());
		//algorithm = new SimulatedAnnealing(new FileToGraph("david-fig11.rgf").createGraph());
		//algorithm = new HillClimber(new FileToGraph("complex-octo.rgf").createGraph());
		algorithm = new ALPS(new FileToGraph("complex-octo	.rgf").createGraph());
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
		this.resize(500, 550);
	}
	
	public void next() {
		canvas.setCanvasWidthAndHeight();
		canvas.calculateOptimalEdgeLength();
		for (int i = 0; i < 10; i++)
			algorithm.next();
		
		//runAllAlgorithms(25);
		
		canvas.drawGraph(algorithm.displayGraph());
		statusBar.setText(algorithm.displayText());
	}
	
	public void runAllAlgorithms(int nRuns) {
		queueLength = 10;
		algorithm = new GeneticAlgorithm(rawGraph);
		runUntilOptimalFound(nRuns);	

		queueLength = 100;
		algorithm = new SimulatedAnnealing(rawGraph);
		runUntilOptimalFound(nRuns);

		queueLength = 100;
 		algorithm = new HillClimber(rawGraph);
		runUntilOptimalFound(nRuns);

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
			double fitness;
			do {
				algorithm.next();
				GraphInstance graph = algorithm.displayGraph();
				fitness = graph.fitness;
				if (algorithm.getRuns() % 1000 == 0)
					takeReading(graph);
			} while(fitness > optimalFitness);
			long elapsedTime = System.currentTimeMillis() - startTime;
			dataCollector.writeReading("Total time: "+ elapsedTime + " ms");
			dataCollector.close();
			nRestarts = 0;
		}
		System.out.println("Finished runs for " + algorithm.getClass().getSimpleName());
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
		if (readings.size() < queueLength) {
			readings.add(graph.fitness);
		} else {
			readings.removeFirst();
			readings.add(graph.fitness);
			if(readings.getLast() > readings.getFirst() * 0.995 && graph.fitness > optimalFitness * 1.1) {
				restartAlgorithm();
				dataCollector.writeReading("*");
			}
		}
		String reading = String.format("%.3f", graph.fitness) + 
		" " + graph.numberOfEdgeCrossings +
		" " + String.format("%.3f", graph.edgeFitness) +
		" " + String.format("%.3f", graph.angularResolution) +
		" " + String.format("%.3f", graph.nodeSeparation) +
		" " + String.format("%.3f", graph.edgeTunneling); 
		//System.out.println(reading);
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
	
	/**
	 * Check if the edge between a-b intersects with the edge between c-d
	 */
	public static boolean checkEdgeCrossing(NodeInstance a, NodeInstance b, NodeInstance c, NodeInstance d) {
		if (b == c || d == a || b == d)
			return false;
		return Line2D.linesIntersect(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y);
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