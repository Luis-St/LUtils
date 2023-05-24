package net.luis.utils.math;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class MthTest {
	
	static final Random RNG = new Random();
	
	@Test
	void sum() {
		assertEquals(Mth.sum(127), 10);
		assertEquals(Mth.sum(127L), 10);
	}
	
	@Test
	void randomInt() {
		List<Integer> values = new ArrayList<>();
		for (int i = 0; i < 1000000; i++) {
			values.add(Mth.randomInt(RNG, 0, 10));
		}
		assertEquals(Collections.min(values), 0);
		assertEquals(Collections.max(values), 9);
	}
	
	@Test
	void randomExclusiveInt() {
		List<Integer> values = new ArrayList<>();
		for (int i = 0; i < 1000000; i++) {
			values.add(Mth.randomExclusiveInt(RNG, 0, 10));
		}
		assertEquals(Collections.min(values), 1);
		assertEquals(Collections.max(values), 9);
	}
	
	@Test
	void randomInclusiveInt() {
		List<Integer> values = new ArrayList<>();
		for (int i = 0; i < 1000000; i++) {
			values.add(Mth.randomInclusiveInt(RNG, 0, 10));
		}
		assertEquals(Collections.min(values), 0);
		assertEquals(Collections.max(values), 10);
	}
	
	@Test
	void roundTo() {
		assertEquals(Mth.roundTo(10.051, 100), 10.05);
	}
	
	@Test
	void isInBounds() {
		assertTrue(Mth.isInBounds(1, 0, 2));
		assertTrue(Mth.isInBounds(1, 0, 1));
		assertTrue(Mth.isInBounds(1, 1, 2));
		assertTrue(Mth.isInBounds(1, 1, 1));
		assertFalse(Mth.isInBounds(3, 0, 2));
	}
	
	@Test
	void sameValues() {
		assertFalse(Mth.sameValues());
		assertTrue(Mth.sameValues(0));
		assertTrue(Mth.sameValues(1, 1.0, 1));
		assertFalse(Mth.sameValues(0, 1.0, 0.5));
	}
	
	@Test
	void frac() {
		assertEquals(Mth.frac(1.5F), 0.5F);
		assertEquals(Mth.frac(1.5), 0.5);
	}
	
	@Test
	void clamp() {
		assertEquals(Mth.clamp(-1, 0, 9), 0);
		assertEquals(Mth.clamp(4, 0, 9), 4);
		assertEquals(Mth.clamp(10, 0, 9), 9);
		
		assertEquals(Mth.clamp(-1L, 0L, 9L), 0L);
		assertEquals(Mth.clamp(4L, 0L, 9L), 4L);
		assertEquals(Mth.clamp(10L, 0L, 9L), 9L);
		
		assertEquals(Mth.clamp(-1.0, 0.0, 9.0), 0.0);
		assertEquals(Mth.clamp(4.0, 0.0, 9.0), 4.0);
		assertEquals(Mth.clamp(10.0, 0.0, 9.0), 9.0);
		
		assertEquals(Mth.clamp(-1.0F, 0.0F, 9.0F), 0.0F);
		assertEquals(Mth.clamp(4.0F, 0.0F, 9.0F), 4.0F);
		assertEquals(Mth.clamp(10.0F, 0.0F, 9.0F), 9.0F);
	}
	
	@Test
	void average() {
		assertTrue(Double.isNaN(Mth.average()));
		assertEquals(Mth.average(0, 1, 2, 3, 4), 2.0);
		assertEquals(Mth.average(0L, 1L, 2L, 3L, 4L), 2.0);
		assertEquals(Mth.average(0.0, 1.0, 2.0, 3.0, 4.0), 2.0);
		assertEquals(Mth.average(0F, 1F, 2F, 3F, 4F), 2.0);
	}
	
	@Test
	void isPowerOfTwo() {
		assertFalse(Mth.isPowerOfTwo(0));
		assertTrue(Mth.isPowerOfTwo(2));
		assertFalse(Mth.isPowerOfTwo(3));
		assertTrue(Mth.isPowerOfTwo(4));
	}
}