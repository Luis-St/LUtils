package net.luis.utils.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TriFunction}.<br>
 *
 * @author Luis-St
 */
class TriFunctionTest {
	
	@Test
	void apply() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> {
			assertEquals(1, a);
			assertEquals(2, b);
			assertEquals(3, c);
			return a + b + c;
		};
		assertEquals(6, function.apply(1, 2, 3));
		assertDoesNotThrow(() -> function.apply(1, 2, 3));
	}
	
	@Test
	void andThen() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> a + b + c;
		assertDoesNotThrow(() -> function.andThen(i -> i * 2));
		assertEquals(12, function.andThen(i -> i * 2).apply(1, 2, 3));
		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
}