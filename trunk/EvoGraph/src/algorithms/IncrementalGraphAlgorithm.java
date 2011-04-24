package algorithms;

import graph.GraphInstance;

public interface IncrementalGraphAlgorithm {
	public void next();
	public GraphInstance displayGraph();
	public String displayText();
	public void updateGraph();
}
