package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class RangeTest {
	
	@Test
	void of() {
		assertNotNull(Range.of(9));
		assertNotNull(Range.of(0, 0));
		assertNotNull(Range.of(0, 9));
		assertThrows(RuntimeException.class, () -> Range.of(9, 0));
	}
	
	@Test
	void getMin() {
		assertEquals(0, Range.of(0, 9).getMin());
		assertNotEquals(1, Range.of(0, 9).getMin());
	}
	
	@Test
	void getMax() {
		assertEquals(9, Range.of(0, 9).getMax());
		assertNotEquals(8, Range.of(0, 9).getMax());
	}
	
	@Test
	void isInRange() {
		assertFalse(Range.of(0, 9).isInRange(-1));
		for (int i = 0; i < 10; i++) {
			assertTrue(Range.of(0, 9).isInRange(i));
		}
		assertFalse(Range.of(0, 9).isInRange(10));
	}
}