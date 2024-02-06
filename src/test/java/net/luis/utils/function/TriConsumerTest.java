package net.luis.utils.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TriConsumer}.<br>
 *
 * @author Luis-St
 */
class TriConsumerTest {
	
	@Test
	void accept() {
		TriConsumer<String, String, String> consumer = (a, b, c) -> {
			assertEquals("a", a);
			assertEquals("b", b);
			assertEquals("c", c);
		};
		assertDoesNotThrow(() -> consumer.accept("a", "b", "c"));
	}
	
	@Test
	void andThen() {
		TriConsumer<String, String, String> consumer = (a, b, c) -> {};
		assertDoesNotThrow(() -> consumer.andThen((a, b, c) -> {}));
		assertThrows(NullPointerException.class, () -> consumer.andThen(null));
	}
}