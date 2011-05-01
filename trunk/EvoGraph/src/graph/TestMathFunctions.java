package graph;

import static org.junit.Assert.*;

import org.junit.Test;

import evograph.EvoGraph;


public class TestMathFunctions {

	@Test
	public void testPointAngleDistance() {
		int[] coords = EvoGraph.calculateCoordinatesFromPointAngleDistance(100, 100, Math.PI/ 3, 50);
		System.out.println("Coords = " + coords[0] + ", " + coords[1]);
		coords = EvoGraph.calculateCoordinatesFromPointAngleDistance(100, 100, Math.PI, 50);
		System.out.println("Coords = " + coords[0] + ", " + coords[1]);
		coords = EvoGraph.calculateCoordinatesFromPointAngleDistance(100, 100, 5 * Math.PI/ 3, 50);
		System.out.println("Coords = " + coords[0] + ", " + coords[1]);
	}
	
	@Test
	public void calculateAngleTest() {
//		System.out.println(EvoGraph.calculateAngle(0,0,0,5));
//		System.out.println(EvoGraph.calculateAngle(0,100,(int) (10*Math.sqrt(3)),90));
//		System.out.println(EvoGraph.calculateAngle(100,100,100 - (int) (10*Math.sqrt(3)),90));
		assertEquals(EvoGraph.calculateAngle(5, 32, 5, 32), Double.NaN, 0);
		assertEquals(EvoGraph.calculateAngle(5, 32, 11, 32), Math.PI / 2, 0);
		assertEquals(EvoGraph.calculateAngle(5, 32, 5, 34), Math.PI, 0);
	}

	@Test
	public void calculatePointToLineDistanceTest() {
		assertEquals(EvoGraph.distanceToSegment(0, 0, 0, 1, 1, 0), Math.sqrt(0.5), 0.001);
		assertEquals(EvoGraph.distanceToSegment(0, 0, 0, 1, 1, 1), 1, 0.001);
		assertEquals(EvoGraph.distanceToSegment(0, 0, 1, 1, 1, 0), 1, 0.001);
		assertEquals(EvoGraph.distanceToSegment(0, 0, 1, 1, 1, 2), Math.sqrt(2), 0.001);
		assertEquals(EvoGraph.distanceToSegment(0, 0, 1, 1, 2, 2), Math.sqrt(2), 0.001);
		assertEquals(EvoGraph.distanceToSegment(0, 0, 1, 1, 2, 1), Math.sqrt(2), 0.001);
		assertEquals(EvoGraph.distanceToSegment(-1, 1, 0, 1, 1, 0), 1, 0.001);

	}
	
	@Test
	public void testOrthogonalityPenalty() {
		assertEquals(GraphInstance.orthogonalityPenalty(0), 0, 0);
		assertEquals(GraphInstance.orthogonalityPenalty(Math.PI / 4), 1, 0);
		assertEquals(GraphInstance.orthogonalityPenalty(Math.PI / 2), 0, 0);
		assertEquals(GraphInstance.orthogonalityPenalty(3 * Math.PI / 4), 1, 0);
		assertEquals(GraphInstance.orthogonalityPenalty(Math.PI), 0, 0);
		assertEquals(GraphInstance.orthogonalityPenalty(5 * Math.PI / 4), 1, 0);
		assertEquals(GraphInstance.orthogonalityPenalty(3 * Math.PI / 2), 0, 0);
		assertEquals(GraphInstance.orthogonalityPenalty(7 * Math.PI / 4), 1, 0);
		assertEquals(GraphInstance.orthogonalityPenalty(2 * Math.PI), 0, 0);
		assertTrue(GraphInstance.orthogonalityPenalty(0.1) > 1 -GraphInstance.orthogonalityPenalty((Math.PI / 4) - 0.1));
	}
}
