package net.luis.utils.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link QuadConsumer}.<br>
 *
 * @author Luis-St
 */
class QuadConsumerTest {
	
	@Test
	void accept() {
		QuadConsumer<String, String, String, String> consumer = (a, b, c, d) -> {
			assertEquals("a", a);
			assertEquals("b", b);
			assertEquals("c", c);
			assertEquals("d", d);
		};
		assertDoesNotThrow(() -> consumer.accept("a", "b", "c", "d"));
	}
	
	@Test
	void andThen() {
		QuadConsumer<String, String, String, String> consumer = (a, b, c, d) -> {};
		assertDoesNotThrow(() -> consumer.andThen((a, b, c, d) -> {}));
		assertThrows(NullPointerException.class, () -> consumer.andThen(null));
	}
}