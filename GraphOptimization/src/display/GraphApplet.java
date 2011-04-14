package display;

import evo.GeneticAlgorithm;
import graph.Graph;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Event;
import java.awt.Label;


@SuppressWarnings("serial")
public class GraphApplet extends Applet {
	GeneticAlgorithm ga;
	public Graph graph;
	
	//Display components
	GraphCanvas canvas;
	Label fitnessLabel;
	Label generationLabel;
	Button nextGenerationButton;
	
	public void init() {
		nextGenerationButton = new Button("Next Generation");
		fitnessLabel = new Label("");
		generationLabel = new Label("Generation 0000");
		ga = new GeneticAlgorithm();

		graph = ga.getFittestIndividual();
		setFitnessLabel(graph.fitness);

		this.resize(800, 560);
		canvas = new GraphCanvas(this);

		setLayout(new BorderLayout());
		add("North", fitnessLabel);

		add("Center", canvas);
		add("West", generationLabel);
		add("South", nextGenerationButton);
	}
	
	public void nextGeneration() {
		//for(int i = 0; i < 10; i++) {
			ga.nextGeneration();
			setGenerationLabel(ga.generation);
		//}
		graph = ga.getFittestIndividual();
		graph.printCoordinatesAndAngles();
		setFitnessLabel(graph.fitness);
		canvas.drawGraph();
	}
	
	public void setFitnessLabel(double fitness) {
		fitnessLabel.setText("Fitness value: " + fitness);
	}
	
	public void setGenerationLabel(int generation) {
		generationLabel.setText("Generation " + generation);
	}

	public boolean action(Event evt, Object arg){
		if (evt.target == nextGenerationButton) {
			nextGeneration();
			return true;
		}
		return false;
	}
}
