package graph;

import static org.junit.Assert.*;

import org.junit.Test;


public class TestMathFunctions {
	
	@Test
	public void calculateAngleTest() {
		GraphInstance gi = new GraphInstance(null);
		assertEquals(gi.calculateAngle(0, 0, 0, 0), Double.NaN, 0);
	}

	@Test
	public void calculatePointToLineDistanceTest() {
		GraphInstance gi = new GraphInstance(null);
		assertEquals(gi.distanceToSegment(0, 0, 0, 1, 1, 0), Math.sqrt(0.5), 0.001);
		assertEquals(gi.distanceToSegment(0, 0, 0, 1, 1, 1), 1, 0.001);
		assertEquals(gi.distanceToSegment(0, 0, 1, 1, 1, 0), 1, 0.001);
		assertEquals(gi.distanceToSegment(0, 0, 1, 1, 1, 2), Math.sqrt(2), 0.001);
		assertEquals(gi.distanceToSegment(0, 0, 1, 1, 2, 2), Math.sqrt(2), 0.001);
		assertEquals(gi.distanceToSegment(0, 0, 1, 1, 2, 1), Math.sqrt(2), 0.001);
		assertEquals(gi.distanceToSegment(-1, 1, 0, 1, 1, 0), 1, 0.001);

	}
}
