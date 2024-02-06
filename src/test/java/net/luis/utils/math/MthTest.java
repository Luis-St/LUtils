package net.luis.utils.math;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Mth}.<br>
 *
 * @author Luis-St
 */
class MthTest {
	
	private static final Random RNG = new Random(0);
	
	@Test
	void sum() {
		assertEquals(10, Mth.sum(127));
		assertEquals(5, Mth.sum(-23));
		assertEquals(10, Mth.sum(127L));
		assertEquals(5, Mth.sum(-23L));
	}
	
	@Test
	void randomInt() {
		assertThrows(NullPointerException.class, () -> Mth.randomInt(null, 0, 10));
		assertThrows(IllegalArgumentException.class, () -> Mth.randomInt(RNG, 10, 0));
		assertThrows(IllegalArgumentException.class, () -> Mth.randomInt(RNG, 10, 10));
		for (int i = 0; i < 1000; i++) {
			int value = assertDoesNotThrow(() -> Mth.randomInt(RNG, 0, 10));
			assertTrue(9 >= value && value >= 0);
		}
	}
	
	@Test
	void randomExclusiveInt() {
		assertThrows(NullPointerException.class, () -> Mth.randomInt(null, 0, 10));
		assertThrows(IllegalArgumentException.class, () -> Mth.randomInt(RNG, 10, 0));
		assertThrows(IllegalArgumentException.class, () -> Mth.randomInt(RNG, 10, 10));
		for (int i = 0; i < 1000; i++) {
			int value = assertDoesNotThrow(() -> Mth.randomExclusiveInt(RNG, 0, 10));
			assertTrue(9 >= value && value >= 1);
		}
	}
	
	@Test
	void randomInclusiveInt() {
		assertThrows(NullPointerException.class, () -> Mth.randomInt(null, 0, 10));
		assertThrows(IllegalArgumentException.class, () -> Mth.randomInt(RNG, 10, 0));
		assertThrows(IllegalArgumentException.class, () -> Mth.randomInt(RNG, 10, 10));
		for (int i = 0; i < 1000; i++) {
			int value = assertDoesNotThrow(() -> Mth.randomInclusiveInt(RNG, 0, 10));
			assertTrue(10 >= value && value >= 0);
		}
	}
	
	@Test
	void roundTo() {
		assertEquals(10.1, Mth.roundTo(10.05, 1));
		assertEquals(10.05, Mth.roundTo(10.051, 2));
		assertEquals(10.051, Mth.roundTo(10.051, 3));
	}
	
	@Test
	void isInBounds() {
		assertTrue(Mth.isInBounds(1.0, 0.0, 2.0));
		assertTrue(Mth.isInBounds(1.0, 0.0, 1.0));
		assertTrue(Mth.isInBounds(1.0, 1.0, 2.0));
		assertTrue(Mth.isInBounds(1.0, 1.0, 1.0));
		assertFalse(Mth.isInBounds(3.0, 0.0, 2.0));
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
		assertEquals(0.5F, Mth.frac(1.5F));
		assertEquals(0.5, Mth.frac(1.5));
	}
	
	@Test
	void clamp() {
		assertEquals(0, Mth.clamp(-1, 0, 9));
		assertEquals(4, Mth.clamp(4, 0, 9));
		assertEquals(9, Mth.clamp(10, 0, 9));
		
		assertEquals(0L, Mth.clamp(-1L, 0L, 9L));
		assertEquals(4L, Mth.clamp(4L, 0L, 9L));
		assertEquals(9L, Mth.clamp(10L, 0L, 9L));
		
		assertEquals(0.0, Mth.clamp(-1.0, 0.0, 9.0));
		assertEquals(4.0, Mth.clamp(4.0, 0.0, 9.0));
		assertEquals(9.0, Mth.clamp(10.0, 0.0, 9.0));
		
		assertEquals(0.0F, Mth.clamp(-1.0F, 0.0F, 9.0F));
		assertEquals(4.0F, Mth.clamp(4.0F, 0.0F, 9.0F));
		assertEquals(9.0F, Mth.clamp(10.0F, 0.0F, 9.0F));
	}
	
	@Test
	void average() {
		assertTrue(Double.isNaN(Mth.average()));
		assertEquals(2.0, Mth.average(0, 1, 2, 3, 4));
		assertEquals(2.0, Mth.average(0L, 1L, 2L, 3L, 4L));
		assertEquals(2.0, Mth.average(0.0, 1.0, 2.0, 3.0, 4.0));
		assertEquals(2.0, Mth.average(0.0F, 1.0F, 2.0F, 3.0F, 4.0F));
	}
	
	@Test
	void isPowerOfTwo() {
		assertFalse(Mth.isPowerOfTwo(0));
		assertTrue(Mth.isPowerOfTwo(2));
		assertFalse(Mth.isPowerOfTwo(3));
		assertTrue(Mth.isPowerOfTwo(4));
	}
}