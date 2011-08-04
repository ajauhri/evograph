package evograph;

import java.text.DecimalFormat;

import graph.Graph;
import graph.GraphInstance;
import graph.NodeInstance;

import algorithms.generationBased.ALPS;
import algorithms.generationBased.GeneticAlgorithm;
import algorithms.iterationBased.HillClimber;
import algorithms.iterationBased.SimulatedAnnealing;

public class JavascriptBridge extends EvoGraph {
	private static final long serialVersionUID = 1L;
	DecimalFormat df;
	int algorithmNumber = 1;
	
	public void init() {
		super.init();
		df = new DecimalFormat();
		df.applyPattern("0.000");
	}
	
	public void setCanvasDimensions(int width, int height) {
		canvasWidth = width;
		canvasHeight = height;
	}
	
	public String next(int ms) {
		clock.init();
		while(true) {
			algorithm.next();
			if (clock.diff() >= ms)
				break;
		}
		return data(algorithm.displayGraph());
	}
	
	public String data(GraphInstance graph) {
		String data = "{\"runs\":" + algorithm.getRuns();
		data += ", \"fitness\":" + df.format(graph.fitness);
		data += ", \"ec\":" + graph.numberOfEdgeCrossings;
		data += ", \"ef\":" + df.format(graph.edgeFitness);
		data += ", \"et\":" + df.format(graph.edgeTunneling);
		data += ", \"ns\":" + df.format(graph.nodeSeparation);
		data += ", \"ar\":" + df.format(graph.angularResolution);
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
	
	public String setGraph(String graphString) {
		String[] parts = graphString.split(";");
		rawGraph = new Graph(Integer.parseInt(parts[0]));
		for (int i = 1; i < parts.length; i++) {
			String[] halves = parts[i].split(":");
			String[] edges = halves[1].split(",");
			int nodeFromId = Integer.parseInt(halves[0]);
			for (int j = 0; j < edges.length; j++)
				rawGraph.createEdge(nodeFromId, Integer.parseInt(edges[j]));
		}
		setAlgorithm(algorithmNumber);
		calculateOptimalEdgeLength();
		GraphInstance worstGraph = new GraphInstance(rawGraph);
		for (NodeInstance ni : worstGraph.nodeInstances) {
			ni.setRealX(1);
			ni.setRealY(1);
		}
		worstGraph.calculateFitness();
		return data(worstGraph);
	}
	
	public void setAlgorithm(int n) {
		algorithmNumber = n;
		switch (n) {
			case 1:
				algorithm = new GeneticAlgorithm(rawGraph);
				break;
			case 2:
				algorithm = new ALPS(rawGraph);
				break;
			case 3:
				algorithm = new SimulatedAnnealing(rawGraph);
				break;
			case 4:
				algorithm = new HillClimber(rawGraph);
				break;		
		}
	}
	
	public String setNodeCoords(int id, int x, int y) {
		GraphInstance g = algorithm.displayGraph();
		g.nodeInstances[id].setRealX(100 * x);
		g.nodeInstances[id].setRealY(100 * y);
		g.calculateFitness();
		return data(g);
	}
	
	public void restartAlgorithm() {
		algorithm.restart();
	}
	
	public void setMultiplier(String which, double x) {
		if (which.equals("ar")) {
			angularResolutionMultiplier = x;
		} else if (which.equals("ef")) {
			edgeFitnessMultiplier = x;
		} else if (which.equals("ec")) {
			edgeCrossingsMultiplier = x;
		} else if (which.equals("et")) {
			edgeTunnelingMultiplier = x;
		} else if (which.equals("ns")) {
			nodeSeparationMultiplier = x;
		}
//		GraphInstance g = algorithm.displayGraph();
//		g.calculateFitness();
//		return data(g);
	}
}
