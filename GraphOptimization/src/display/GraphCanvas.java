package display;

import graph.Edge;
import graph.Node;

import java.awt.Canvas;
import java.awt.Graphics;

@SuppressWarnings("serial")
public class GraphCanvas extends Canvas {
	GraphApplet applet;
	
	public GraphCanvas(GraphApplet applet) {
		this.applet = applet;
	}

	public void paint(Graphics g) {
		int numberOfNodes = applet.graph.getNumberOfNodes();
		for (int i = 0; i < numberOfNodes; i++)
			drawNode(g, applet.graph.getNodeAt(i));
		for (int i = 0; i < numberOfNodes; i++)
			drawNodeEdges(g, applet.graph.getNodeAt(i));
		applet.graph.calculateFitness();
	}
	
	public void drawNode(Graphics g, Node node) {
		int x = node.getX();
		int y = node.getY();
		g.drawOval(x, y, 10, 10); // get dimensions from node
		g.drawString("" + node.id, x, y);
	}
	
	public void drawNodeEdges(Graphics g, Node node) {
		int x = node.getX();
		int y = node.getY();
		Object[] edges = node.getEdges();
		for (Object e : edges) {
			g.drawLine(x, y, ((Edge) e).to.getX(), ((Edge) e).to.getY());
			((Edge) e).computeEdgeLength(x, y);
		}
	}
}
