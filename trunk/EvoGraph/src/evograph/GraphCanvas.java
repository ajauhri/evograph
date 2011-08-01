package evograph;

import graph.GraphInstance;
import graph.Node;
import graph.NodeInstance;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;

public class GraphCanvas extends Canvas {
	private static final long serialVersionUID = 1L;
	public GraphInstance graph;
	public NodeInstance draggedNode;
	public int dragOffsetX, dragOffsetY;
	public Standalone applet;
	public final int padding = 15;

	public GraphCanvas(Standalone applet) {
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
			g.fillOval(node.x - 10 + padding, node.y - 10 + padding, 20, 20);
			g.setColor(Color.WHITE);
			g.drawString("" + node.id, node.x - 8 + padding, node.y + 5 + padding);
		}
	}
	
	public void drawEdges(Graphics g) {
		g.setColor(Color.GRAY);
		for (int i = 0; i < graph.nodeInstances.length; i++) {
			NodeInstance node1 = graph.nodeInstances[i];
			for (Node n : node1.node.connectedNodes.values()) {
				NodeInstance node2 = graph.nodeInstances[n.id];
				g.drawLine(node1.x + padding, node1.y + padding, node2.x + padding, node2.y + padding);
			}
		}
	}
	
	public void setCanvasWidthAndHeight() {
		EvoGraph.canvasWidth = this.getWidth() - (padding * 2);
		EvoGraph.canvasHeight = this.getHeight() - (padding * 2);
	}
	
	/** Mouse functions **/
	  
	  public boolean mouseDown(Event evt, int x, int y) {
		if (graph != null) {
			for(NodeInstance node : graph.nodeInstances) {
				if(x > node.x - 10 + padding && x < node.x + 10 + padding && y > node.y - 10 + padding && y < node.y + 10 + padding) {
					draggedNode = node;
					dragOffsetX = node.x - x;
					dragOffsetY = node.y - y;
					break;
				}		
			}
		}
	    return true;
	  }

	  public boolean mouseDrag(Event evt, int x, int y) {
		if (draggedNode != null) {
			draggedNode.setRealX((x + dragOffsetX) * 100);
			draggedNode.setRealY((y + dragOffsetY) * 100);
		    repaint();
		}
	    return true;
	  }

	  public boolean mouseUp (Event evt, int x, int y) {
		draggedNode = null;
		applet.updateGraph();
	    repaint();
	    return true;
	  }
}
