package evograph;

import graph.GraphInstance;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JLabel;

import utils.DataCollector;
import utils.FileToGraph;

import algorithms.generationBased.ALPS;
import algorithms.generationBased.GeneticAlgorithm;
import algorithms.kgraph.KGraphGA;

public class Standalone extends EvoGraph implements ActionListener {
	private static final long serialVersionUID = 1L;
	GraphCanvas canvas;
	JButton nextButton;
	JLabel statusBar;
	public static String rgf = "complex-octo";
	public static double optimalFitness = 5.01;
	public boolean optimalFound = false;
	
	public void init() {
		super.init();
		createGUI();
		readings = new LinkedList<Double>();
		rawGraph = new FileToGraph(rgf + ".rgf").createGraph();
		algorithm = new GeneticAlgorithm(rawGraph);
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
		calculateOptimalEdgeLength();
		
		clock.init();
//		for (int i = 0; i < 5; i++)
		//algorithm.next();
		//canvas.drawGraph(algorithm.displayGraph());
		//statusBar.setText(algorithm.displayText());
//		System.out.println("total time for 5 runs = " + clock.diff() + " ms");
		
		//runAllAlgorithms(1);
		
		//runKGraphs(17, 17, 20); //starting k, ending k, maximum # runs

		for (int i = 0; i < 10; i++)
			algorithm.next();
//		for (int i = 0; i < 1000; i++) {
//			algorithm.restart();
//			dataCollector = new DataCollector(algorithm.getClass().getSimpleName(), rgf, i + 1);
//			do {
//				algorithm.next();
//			} while (checkConverged(algorithm.displayGraph()));
//			takeReading(algorithm.displayGraph());
//			dataCollector.close();
//		}
		System.out.println("Time elapsed = " + clock.diff() + " ms");

		canvas.drawGraph(algorithm.displayGraph());
		statusBar.setText(algorithm.displayText() + "\t" + clock.diff() + " ms");
		//checkOptimalFound();
//		}
	}
	

	
	public void runKGraphs(int first, int last, int maxRuns) {
		int run;
		//boolean foundOptimal;
		GraphInstance[] bestFound = new GraphInstance[last - first + 1];
		for (int i = first; i <= last; i++) {
			run = 0;
			//algorithm = new GeneticAlgorithm(new FileToGraph("k" + i + ".rgf").createGraph());
			algorithm = new KGraphGA(new FileToGraph("k" + i + ".rgf").createGraph());
			//algorithm = new KGraphSA(new FileToGraph("k" + i + ".rgf").createGraph());
			//KDataCollector dc = new KDataCollector("k" + i);
			while(run < maxRuns) {
				long startTime = System.currentTimeMillis();
				run++;
				readings.clear();
				queueLength = 30 - (i / 2);
				//queueLength = 200 - (i * 3);
				converged = false;
				GraphInstance graph;
				do {
					algorithm.next();
					graph = algorithm.displayGraph();
				} while (!konverged(graph, i));
				//dc.writeLine("K" + i + " run " + run + " converged to " + graph.numberOfEdgeCrossings + " in " + (algorithm.getRuns() - readings.size() + 1) + " generations");
				if(bestFound[i - first] == null || graph.numberOfEdgeCrossings < bestFound[i - first].numberOfEdgeCrossings) {
					bestFound[i - first] = graph;
					if(graph.numberOfEdgeCrossings <= cnLowerBounds[i]) {
						//dc.writeLine("Lower bound for K" + i + " found (" + graph.numberOfEdgeCrossings + ")");
						break;
					} else {
						//dc.writeLine("New best for K" + i + " found (" + graph.numberOfEdgeCrossings + ")");
					}
				}
				algorithm.restart();
				System.out.println("Run took " + (System.currentTimeMillis() - startTime) + " ms");
			}
//			dc.close();
//			dc = new KDataCollector("best-k" + i);
//			dc.writeLine(bestFound[i - first].printCoordinates(), false);
//			dc.writeLine(bestFound[i - first].printOrientations(), false);
//			dc.close();
			canvas.drawGraph(bestFound[i - first]);
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
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == nextButton)
			next();
	}
	
	public void updateGraph() {
		algorithm.updateGraph();
		statusBar.setText(algorithm.displayText());
	}
}
