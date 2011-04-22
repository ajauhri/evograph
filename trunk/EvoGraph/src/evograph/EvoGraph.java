	package evograph;

import graph.FileToGraph;
import hc.HillClimber;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;

public class EvoGraph extends JApplet implements ActionListener {
	private static final long serialVersionUID = 1L;
	GraphCanvas canvas;
	JButton nextButton;
	JLabel statusBar;
	IncrementalGraphAlgorithm algorithm;

	public void init() {
		createGUI();
		//algorithm = new GeneticAlgorithm(new FileToGraph("complex-octo.rgf").createGraph());
		//algorithm = new SimulatedAnnealing(new FileToGraph("complex-octo.rgf").createGraph());
		algorithm = new HillClimber(new FileToGraph("complex-octo.rgf").createGraph());
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
		//double fitness;
		//do {
		for(int i = 0; i < 10; i++)
			algorithm.next();
		//	fitness = ((GGraph) algorithm.displayGraph()).fitness;
		//} while(fitness > 52 || fitness == Double.NaN);
		canvas.drawGraph(algorithm.displayGraph());
		statusBar.setText(algorithm.displayText());
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
}