package evograph;

import ga.GeneticAlgorithm;
import graph.FileToGraph;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;

public class EvoGraph extends JApplet implements ActionListener {
	private static final long serialVersionUID = 1L;
	GraphCanvas canvas;
	JButton nextButton;
	JLabel statusBar;
	IncrementalGraphAlgorithm algorithm;

	public void init() {
		createGUI();
		algorithm = new GeneticAlgorithm(this, new FileToGraph("grid9.rgf").createGraph());
	}

	public void createGUI() {
		canvas = new GraphCanvas();
		nextButton = new JButton("Next");
		nextButton.addActionListener(this);
		statusBar = new JLabel(" ");
	    getContentPane().add(canvas, BorderLayout.CENTER);
	    getContentPane().add(nextButton, BorderLayout.SOUTH);
	    getContentPane().add(statusBar, BorderLayout.NORTH);
		this.resize(500, 550);
	}
	
	public void next() {
		algorithm.next();
		canvas.drawGraph(algorithm.displayGraph());
		statusBar.setText(algorithm.displayText());
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == nextButton)
			next();
	}
	
	public int getCanvasWidth() {
		return canvas.getWidth();
	}
	
	public int getCanvasHeight() {
		return canvas.getHeight();
	}
	
	public static boolean probability(double chance) {
		return Math.random() < chance;
	}

	public static int randomInt(int min, int max) {
		 return (int) Math.random() * (max - min + 1) + min;
	}
}