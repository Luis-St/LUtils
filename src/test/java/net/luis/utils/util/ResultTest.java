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

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Result}
 *
 * @author Luis-St
 */
class ResultTest {
	
	@Test
	void createSuccess() {
		assertNotNull(Result.success(100));
		assertDoesNotThrow(() -> Result.success(100));
		assertDoesNotThrow(() -> Result.success(null));
	}
	
	@Test
	void createError() {
		assertNotNull(Result.error("Error"));
		assertDoesNotThrow(() -> Result.error("Error"));
		assertThrows(NullPointerException.class, () -> Result.error(null));
	}
	
	@Test
	void get() {
		assertEquals(Result.success(100).get(), Either.left(100));
		assertEquals(Result.error("Error").get(), Either.right("Error"));
	}
	
	@Test
	void isSuccess() {
		assertTrue(Result.success(100).isSuccess());
		assertFalse(Result.error("Error").isSuccess());
	}
	
	@Test
	void result() {
		assertEquals(Optional.of(100), Result.success(100).result());
		assertEquals(Optional.empty(), Result.error("Error").result());
	}
	
	@Test
	void orThrow() {
		assertDoesNotThrow(() -> Result.success(100).orThrow());
		assertDoesNotThrow(() -> Result.success(100).orThrow(RuntimeException::new));
		assertThrows(IllegalStateException.class, () -> Result.error("Error").orThrow());
		assertThrows(NullPointerException.class, () -> Result.error("Error").orThrow(null));
		assertThrows(NoSuchElementException.class, () -> Result.error("Error").orThrow(NoSuchElementException::new));
	}
	
	@Test
	void isError() {
		assertFalse(Result.success(100).isError());
		assertTrue(Result.error("Error").isError());
	}
	
	@Test
	void error() {
		assertEquals(Optional.empty(), Result.success(100).error());
		assertEquals(Optional.of("Error"), Result.error("Error").error());
	}
	
	@Test
	void errorOrThrow() {
		assertThrows(IllegalStateException.class, () -> Result.success(100).errorOrThrow());
		assertEquals("Error", Result.error("Error").errorOrThrow());
	}
	
	@Test
	void map() {
		assertThrows(NullPointerException.class, () -> Result.success(100).map(null));
		assertEquals(Result.success(200), Result.success(100).map(i -> i * 2));
		assertEquals(Result.error("Error"), Result.error("Error").map(i -> 10));
	}
	
	@Test
	void flatMap() {
		assertThrows(NullPointerException.class, () -> Result.success(100).flatMap(null));
		assertEquals(Result.success(200), Result.success(100).flatMap(i -> Result.success(i * 2)));
		assertEquals(Result.error("Error"), Result.error("Error").flatMap(i -> Result.success(10)));
	}
	
	@Test
	void orElse() {
		assertThrows(NullPointerException.class, () -> Result.success(100).orElse(null));
		assertEquals(100, Result.success(100).orElse(200));
		assertEquals(200, Result.error("Error").orElse(200));
	}
	
	@Test
	void orElseGet() {
		assertThrows(NullPointerException.class, () -> Result.success(100).orElseGet(null));
		assertEquals(100, Result.success(100).orElseGet(() -> 200));
		assertEquals(200, Result.error("Error").orElseGet(() -> 200));
	}
}
