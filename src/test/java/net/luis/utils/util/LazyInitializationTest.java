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

package net.luis.utils.util;

import net.luis.utils.exception.AlreadyInitializedException;
import net.luis.utils.exception.NotInitializedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LazyInitialization}.<br>
 *
 * @author Luis-St
 */
class LazyInitializationTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new LazyInitialization<>());
		assertDoesNotThrow(() -> new LazyInitialization<>(10));
		assertDoesNotThrow(() -> new LazyInitialization<>((Object) null));
		assertDoesNotThrow(() -> new LazyInitialization<>((v) -> {}));
		assertThrows(NullPointerException.class, () -> new LazyInitialization<>(null));
		assertDoesNotThrow(() -> new LazyInitialization<>(10, (v) -> {}));
		assertDoesNotThrow(() -> new LazyInitialization<>(null, (v) -> {}));
		assertThrows(NullPointerException.class, () -> new LazyInitialization<>(null, null));
	}
	
	@Test
	void get() {
		assertThrows(NotInitializedException.class, new LazyInitialization<>()::get);
		assertDoesNotThrow(new LazyInitialization<>(10)::get);
		assertEquals(10, new LazyInitialization<>(10).get());
		assertNull(new LazyInitialization<>((Object) null).get());
	}
	
	@Test
	void set() {
		LazyInitialization<Integer> lazy = new LazyInitialization<>();
		assertDoesNotThrow(() -> lazy.set(10));
		assertThrows(AlreadyInitializedException.class, () -> lazy.set(10));
	}
	
	@Test
	void isInstantiated() {
		LazyInitialization<Integer> lazy = new LazyInitialization<>();
		assertFalse(lazy.isInstantiated());
		lazy.set(10);
		assertTrue(lazy.isInstantiated());
	}
}