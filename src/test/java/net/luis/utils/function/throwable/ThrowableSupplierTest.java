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

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableSupplier}.<br>
 *
 * @author Luis-St
 */
class ThrowableSupplierTest {
	
	@Test
	void caughtConvertsSuccessfulSupplier() {
		ThrowableSupplier<String, Exception> supplier = () -> "Hello World!";
		Supplier<String> caught = ThrowableSupplier.caught(supplier);
		
		assertEquals("Hello World!", caught.get());
		assertDoesNotThrow(caught::get);
	}
	
	@Test
	void caughtWrapsExceptionInRuntimeException() {
		ThrowableSupplier<String, Exception> supplier = () -> {
			throw new Exception("Test exception");
		};
		Supplier<String> caught = ThrowableSupplier.caught(supplier);
		
		RuntimeException exception = assertThrows(RuntimeException.class, caught::get);
		assertInstanceOf(Exception.class, exception.getCause());
		assertEquals("Test exception", exception.getCause().getMessage());
	}
	
	@Test
	void caughtWithNullSupplier() {
		assertThrows(NullPointerException.class, () -> ThrowableSupplier.caught(null));
	}
	
	@Test
	void caughtWithNullResult() {
		ThrowableSupplier<String, Exception> supplier = () -> null;
		Supplier<String> caught = ThrowableSupplier.caught(supplier);
		
		assertNull(caught.get());
	}
	
	@Test
	void caughtWithDifferentReturnTypes() {
		ThrowableSupplier<Integer, Exception> intSupplier = () -> 42;
		Supplier<Integer> caughtInt = ThrowableSupplier.caught(intSupplier);
		
		assertEquals(42, caughtInt.get());
		
		ThrowableSupplier<Boolean, Exception> boolSupplier = () -> true;
		Supplier<Boolean> caughtBool = ThrowableSupplier.caught(boolSupplier);
		
		assertTrue(caughtBool.get());
	}
	
	@Test
	void getWithSuccessfulExecution() {
		ThrowableSupplier<String, Exception> supplier = () -> "test result";
		
		assertEquals("test result", assertDoesNotThrow(supplier::get));
	}
	
	@Test
	void getWithException() {
		ThrowableSupplier<String, Exception> supplier = () -> {
			throw new Exception("Test exception");
		};
		
		Exception exception = assertThrows(Exception.class, supplier::get);
		assertEquals("Test exception", exception.getMessage());
	}
	
	@Test
	void getWithNullResult() {
		ThrowableSupplier<String, Exception> supplier = () -> null;
		
		assertNull(assertDoesNotThrow(supplier::get));
	}
	
	@Test
	void getWithDifferentReturnTypes() {
		ThrowableSupplier<Integer, Exception> intSupplier = () -> 123;
		assertEquals(123, assertDoesNotThrow(intSupplier::get));
		
		ThrowableSupplier<Boolean, Exception> boolSupplier = () -> false;
		assertFalse(assertDoesNotThrow(boolSupplier::get));
		
		ThrowableSupplier<Double, Exception> doubleSupplier = () -> 3.14;
		assertEquals(3.14, assertDoesNotThrow(doubleSupplier::get), 0.001);
	}
	
	@Test
	void getWithConditionalException() {
		ThrowableSupplier<String, Exception> supplier = () -> {
			if (System.currentTimeMillis() % 2 == 0) {
				return "even";
			} else {
				return "odd";
			}
		};
		
		String result = assertDoesNotThrow(supplier::get);
		assertTrue("even".equals(result) || "odd".equals(result));
	}
	
	@Test
	void getWithComplexObject() {
		ThrowableSupplier<StringBuilder, Exception> supplier = () -> new StringBuilder("test");
		
		StringBuilder result = assertDoesNotThrow(supplier::get);
		assertEquals("test", result.toString());
	}
	
	@Test
	void getWithExceptionBasedOnCondition() {
		ThrowableSupplier<String, Exception> supplier = () -> {
			throw new Exception("Always fails");
		};
		
		Exception exception = assertThrows(Exception.class, supplier::get);
		assertEquals("Always fails", exception.getMessage());
	}
}
