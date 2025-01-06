/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.function.throwable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableTriConsumer}.<br>
 *
 * @author Luis-St
 */
class ThrowableTriConsumerTest {
	
	@Test
	void caught() {
		ThrowableTriConsumer<String, String, String, Exception> consumer = (a, b, c) -> {
			assertEquals("a", a);
			assertEquals("b", b);
			assertEquals("c", c);
			throw new Exception();
		};
		assertThrows(RuntimeException.class, () -> ThrowableTriConsumer.caught(consumer).accept("a", "b", "c"));
		assertThrows(NullPointerException.class, () -> ThrowableTriConsumer.caught(null));
	}
	
	@Test
	void accept() {
		ThrowableTriConsumer<String, String, String, Exception> consumer = (a, b, c) -> {
			assertEquals("a", a);
			assertEquals("b", b);
			assertEquals("c", c);
			throw new Exception();
		};
		assertThrows(Exception.class, () -> consumer.accept("a", "b", "c"));
	}
	
	@Test
	void andThen() {
		ThrowableTriConsumer<String, String, String, Exception> consumer = (a, b, c) -> {
			assertEquals("a", a);
			assertEquals("b", b);
			assertEquals("c", c);
		};
		ThrowableTriConsumer<String, String, String, Exception> after = (a, b, c) -> {
			assertEquals("a", a);
			assertEquals("b", b);
			assertEquals("c", c);
		};
		assertDoesNotThrow(() -> consumer.andThen(after).accept("a", "b", "c"));
		assertThrows(NullPointerException.class, () -> consumer.andThen(null));
	}
}
