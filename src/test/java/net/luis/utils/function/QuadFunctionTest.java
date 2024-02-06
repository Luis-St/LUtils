package net.luis.utils.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link QuadFunction}.<br>
 *
 * @author Luis-St
 */
class QuadFunctionTest {
	
	@Test
	void apply() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> {
			assertEquals(1, a);
			assertEquals(2, b);
			assertEquals(3, c);
			assertEquals(4, d);
			return a + b + c + d;
		};
		assertEquals(10, function.apply(1, 2, 3, 4));
		assertDoesNotThrow(() -> function.apply(1, 2, 3, 4));
	}
	
	@Test
	void andThen() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> a + b + c + d;
		assertDoesNotThrow(() -> function.andThen(i -> i * 2));
		assertEquals(20, function.andThen(i -> i * 2).apply(1, 2, 3, 4));
		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
}