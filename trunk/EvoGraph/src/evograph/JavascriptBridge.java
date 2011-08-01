package evograph;

import graph.Graph;
import graph.GraphInstance;

import algorithms.generationBased.GeneticAlgorithm;

public class JavascriptBridge extends EvoGraph {
	private static final long serialVersionUID = 1L;
	
	public void init() {
		setGraph("7;0:1,2,3,4,5,6;1:2,6;2:3;3:4;4:5;5:6");
	}
	
	public void setCanvasDimensions(int width, int height) {
		canvasWidth = width;
		canvasHeight = height;
	}
	
	public String next() {
		for(int i = 0; i < 10; i++)
			algorithm.next();
		GraphInstance graph = algorithm.displayGraph();
		String data = "{\"runs\":" + algorithm.getRuns();
		data += ", \"fitness\":" + graph.fitness;
		data += ", \"ec\":" + graph.numberOfEdgeCrossings;
		data += ", \"ef\":" + graph.edgeFitness;
		data += ", \"et\":" + graph.edgeTunneling;
		data += ", \"ns\":" + graph.nodeSeparation;
		data += ", \"ar\":" + graph.angularResolution;
		data += ", \"coords\":" + coords(graph) + "}";
		return data;
	}
	
	public String coords(GraphInstance gi) {
		String json = "[";
		for (int i = 0; i < gi.nodeInstances.length; i++) {
			json += "{\"id\":" + gi.nodeInstances[i].id + ", \"x\":" + gi.nodeInstances[i].x + ", \"y\":" + gi.nodeInstances[i].y + "}";
			if (i != gi.nodeInstances.length - 1)
				json += ", ";
		}
		json += "]";
		return json;
	}
	
	public void setGraph(String graphString) {
		String[] parts = graphString.split(";");
		Graph g = new Graph(Integer.parseInt(parts[0]));
		for (int i = 1; i < parts.length; i++) {
			String[] halves = parts[i].split(":");
			String[] edges = halves[1].split(",");
			int nodeFromId = Integer.parseInt(halves[0]);
			for (int j = 0; j < edges.length; j++)
				g.createEdge(nodeFromId, Integer.parseInt(edges[j]));
		}
		algorithm = new GeneticAlgorithm(g);
		calculateOptimalEdgeLength();
	}
}
