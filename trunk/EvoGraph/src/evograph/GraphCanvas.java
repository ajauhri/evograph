package evograph;

import graph.Graph;
import graph.GraphInstance;
import graph.Node;
import graph.NodeInstance;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Point;

public class GraphCanvas extends Canvas {
	private static final long serialVersionUID = 1L;
	public static double optimalEdgeLength;
	public static int canvasWidth;
	public static int canvasHeight;
	public GraphInstance graph;
	public NodeInstance draggedNode;
	public EvoGraph applet;

	public GraphCanvas(EvoGraph applet) {
		super();
		this.applet = applet;
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
	
	public void calculateOptimalEdgeLength() {
		optimalEdgeLength = Math.sqrt((getWidth() * getHeight())/Graph.nNodes);
	}
	
	public void setCanvasWidthAndHeight() {
		canvasWidth = this.getWidth();
		canvasHeight = this.getHeight();
	}
	
	/** Mouse functions **/
	  
	  public boolean mouseDown(Event evt, int x, int y) {
		if (graph != null) {
			for(NodeInstance node : graph.nodeInstances) {
				if(x > node.x - 10 && x < node.x + 10 && y > node.y - 10 && y < node.y + 10) {
					draggedNode = node;
					break;
				}		
			}
		}
	    return true;
	  }

	  public boolean mouseDrag(Event evt, int x, int y) {
		if (draggedNode != null) {
			draggedNode.x = x;
			draggedNode.y = y;
		    repaint();
		}
	    return true;
	  }

	  public boolean mouseUp (Event evt, int x, int y) {
		draggedNode = null;
		applet.updateGraph();
		//TODO: calculate fitness and add to population
	    repaint();
	    return true;
	  }
}
