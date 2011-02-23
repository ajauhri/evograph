package display;

import evo.GeneticAlgorithm;
import graph.Graph;

import java.applet.Applet;
import java.awt.BorderLayout;


@SuppressWarnings("serial")
public class GraphApplet extends Applet {
	GraphCanvas canvas;
	public Graph graph;
	
	public GraphApplet() {
		
	}
	
	public void init() {
		GeneticAlgorithm ga = new GeneticAlgorithm();
		graph = ga.getFittestIndividual();
		
		this.resize(500, 600);
		canvas = new GraphCanvas(this);
		
	    setLayout(new BorderLayout());
	    add("Center", canvas);
	}
}
