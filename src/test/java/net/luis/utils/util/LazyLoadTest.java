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

package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LazyLoad}.<br>
 *
 * @author Luis-St
 */
class LazyLoadTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new LazyLoad<>(null));
		assertDoesNotThrow(() -> new LazyLoad<>(() -> "Hello World!"));
	}
	
	@Test
	void load() {
		LazyLoad<String> lazyLoad = new LazyLoad<>(() -> "Hello World!");
		assertDoesNotThrow(lazyLoad::load);
		assertEquals("Hello World!", lazyLoad.get());
	}
	
	@Test
	void isLoaded() {
		LazyLoad<String> lazyLoad = new LazyLoad<>(() -> "Hello World!");
		assertFalse(lazyLoad.isLoaded());
		lazyLoad.load();
		assertTrue(lazyLoad.isLoaded());
	}
	
	@Test
	void get() {
		assertEquals("Hello World!", new LazyLoad<>(() -> "Hello World!").get());
		assertNull(new LazyLoad<>(() -> null).get());
	}
}