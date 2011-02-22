package display;

import graph.Graph;

import io.FileToGraph;

import java.applet.Applet;
import java.awt.BorderLayout;


@SuppressWarnings("serial")
public class GraphApplet extends Applet {
	GraphCanvas canvas;
	Graph graph;
	
	public GraphApplet() {
		
	}
	
	public void init() {
		createGraph();
		this.resize(500, 600);
		canvas = new GraphCanvas(this);
		
	    setLayout(new BorderLayout());
	    add("Center", canvas);
	}
	
	public void createGraph() {
		FileToGraph fileToGraph = new FileToGraph("test1.rgf");
		graph = fileToGraph.createGraph();
	}
}
