package evograph;

import graph.GraphInstance;
import graph.Node;
import graph.NodeInstance;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class GraphCanvas extends Canvas {
	private static final long serialVersionUID = 1L;
	public static double optimalEdgeLength;
	public GraphInstance graph;

	public GraphCanvas() {
		super();
		this.setBackground(Color.WHITE);
	}
	
	public void drawGraph(GraphInstance graph) {
		this.graph = graph;
		this.repaint();
	}
	
	public void paint(Graphics g) {
		if (graph == null)
			return;
		drawEdges(g);
		drawNodes(g);
		calculateOptimalEdgeLength(graph.graph.nodes.length);
	}
	
	public void drawNodes(Graphics g) {
		for (int i = 0; i < graph.nodeInstances.length; i++) {
			NodeInstance node = graph.nodeInstances[i];
			g.setColor(Color.BLACK);
			g.fillOval(node.x - 10, node.y - 10, 20, 20);
			g.setColor(Color.WHITE);
			g.drawString("" + node.id, node.x - 8, node.y + 5);
		}
	}
	
	public void drawEdges(Graphics g) {
		g.setColor(Color.GRAY);
		for (int i = 0; i < graph.nodeInstances.length; i++) {
			NodeInstance node1 = graph.nodeInstances[i];
			for (Node n : node1.node.connectedNodes.values()) {
				NodeInstance node2 = graph.nodeInstances[n.id];
				g.drawLine(node1.x, node1.y, node2.x, node2.y);
			}
		}
	}
	
	public void calculateOptimalEdgeLength(int numberOfNodes) {
		optimalEdgeLength = Math.sqrt((getWidth() * getHeight())/numberOfNodes);
	}
}
