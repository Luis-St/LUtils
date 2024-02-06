package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Range}.<br>
 *
 * @author Luis-St
 */
class RangeTest {
	
	@Test
	void of() {
		assertNotNull(Range.of(9.0));
		assertNotNull(Range.of(0.0, 0));
		assertNotNull(Range.of(0.0, 9));
		assertThrows(IllegalArgumentException.class, () -> Range.of(9.0, 0.0));
	}
	
	@Test
	void getMin() {
		assertEquals(0.0, Range.of(0.0, 9.0).getMin());
		assertNotEquals(1.0, Range.of(0.0, 9.0).getMin());
	}
	
	@Test
	void getMax() {
		assertEquals(9.0, Range.of(0.0, 9.0).getMax());
		assertNotEquals(8.0, Range.of(0.0, 9.0).getMax());
	}
	
	@Test
	void isInRange() {
		assertFalse(Range.of(0.0, 9.0).isInRange(-1.0));
		for (int i = 0; i < 10; i++) {
			assertTrue(Range.of(0.0, 9.0).isInRange(i));
		}
		assertFalse(Range.of(0.0, 9.0).isInRange(10.0));
	}
}