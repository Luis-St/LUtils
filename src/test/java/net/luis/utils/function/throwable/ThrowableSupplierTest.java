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

package net.luis.utils.function.throwable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableSupplier}.<br>
 *
 * @author Luis-St
 */
class ThrowableSupplierTest {
	
	@Test
	void caught() {
		ThrowableSupplier<String, Exception> supplier = () -> {
			throw new Exception("Test");
		};
		assertThrows(RuntimeException.class, () -> ThrowableSupplier.caught(supplier).get());
		assertThrows(NullPointerException.class, () -> ThrowableSupplier.caught(null));
	}
	
	@Test
	void get() {
		ThrowableSupplier<String, Exception> noneThrowing = () -> "Hello World!";
		assertEquals("Hello World!", assertDoesNotThrow(noneThrowing::get));
		
		ThrowableSupplier<String, Exception> throwing = () -> {
			throw new Exception("Test");
		};
		assertThrows(Exception.class, throwing::get);
	}
}
