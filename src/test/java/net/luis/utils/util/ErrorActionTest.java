/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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
 * Test class for {@link ErrorAction}.<br>
 *
 * @author Luis-St
 */
class ErrorActionTest {
	
	@Test
	void ignoreHandlesAnyException() {
		assertDoesNotThrow(() -> ErrorAction.IGNORE.handle(new RuntimeException("test")));
		assertDoesNotThrow(() -> ErrorAction.IGNORE.handle(new IllegalArgumentException()));
		assertDoesNotThrow(() -> ErrorAction.IGNORE.handle(new NullPointerException()));
		assertDoesNotThrow(() -> ErrorAction.IGNORE.handle(new Exception("checked exception")));
	}
	
	@Test
	void ignoreHandlesNullException() {
		assertDoesNotThrow(() -> ErrorAction.IGNORE.handle(null));
	}
	
	@Test
	void throwRethrowsException() {
		RuntimeException runtimeException = new RuntimeException("test message");
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> ErrorAction.THROW.handle(runtimeException));
		assertSame(runtimeException, thrown);
		assertEquals("test message", thrown.getMessage());
	}
	
	@Test
	void throwHandlesDifferentExceptionTypes() {
		IllegalArgumentException illegalArg = new IllegalArgumentException("illegal");
		IllegalArgumentException thrownIllegal = assertThrows(IllegalArgumentException.class, () -> ErrorAction.THROW.handle(illegalArg));
		assertSame(illegalArg, thrownIllegal);
		
		NullPointerException nullPointer = new NullPointerException("null");
		NullPointerException thrownNull = assertThrows(NullPointerException.class, () -> ErrorAction.THROW.handle(nullPointer));
		assertSame(nullPointer, thrownNull);
	}
	
	@Test
	void throwHandlesNullException() {
		assertThrows(NullPointerException.class, () -> ErrorAction.THROW.handle(null));
	}
	
	@Test
	void enumValues() {
		ErrorAction[] values = ErrorAction.values();
		assertEquals(2, values.length);
		assertSame(ErrorAction.IGNORE, values[0]);
		assertSame(ErrorAction.THROW, values[1]);
	}
	
	@Test
	void enumValueOf() {
		assertSame(ErrorAction.IGNORE, ErrorAction.valueOf("IGNORE"));
		assertSame(ErrorAction.THROW, ErrorAction.valueOf("THROW"));
		assertThrows(IllegalArgumentException.class, () -> ErrorAction.valueOf("INVALID"));
	}
}
