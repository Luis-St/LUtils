package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-st
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
		assertEquals(Range.of(0, 9).getMin(), 0);
		assertNotEquals(Range.of(0, 9).getMin(), 1);
	}
	
	@Test
	void getMax() {
		assertEquals(Range.of(0, 9).getMax(), 9);
		assertNotEquals(Range.of(0, 9).getMax(), 8);
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