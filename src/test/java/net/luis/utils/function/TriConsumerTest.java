/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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