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

package net.luis.utils.util.unsafe;

import org.junit.jupiter.api.*;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StackTraceUtilsTest {
	
	private static final int MAX = Integer.MAX_VALUE - 1000;
	private static final int MIN = Integer.MIN_VALUE + 1000;
	private static final Method METHOD;
	private static final Method METHOD_SAFE;
	
	//region Setup
	@BeforeAll
	static void setUpBefore() {
		System.setProperty("unsafe.offset.base", "2");
	}
	//endregion
	
	//region Cleanup
	@AfterAll
	static void cleanUpAfter() {
		System.setProperty("unsafe.offset.base", "3");
	}
	//endregion
	
	@Test
	void getCallingClass() {
		assertEquals(StackTraceUtilsTest.class, StackTraceUtils.getCallingClass());
		assertEquals(StackTraceUtilsTest.class, StackTraceUtils.getCallingClass(0));
		assertThrows(IndexOutOfBoundsException.class, () -> StackTraceUtils.getCallingClass(MAX));
		assertThrows(IllegalArgumentException.class, () -> StackTraceUtils.getCallingClass(MIN));
	}
	
	@Test
	void getCallingMethod() {
		assertEquals(METHOD, StackTraceUtils.getCallingMethod());
		assertEquals(METHOD, StackTraceUtils.getCallingMethod(0));
		assertThrows(IndexOutOfBoundsException.class, () -> StackTraceUtils.getCallingMethod(MAX));
		assertThrows(IllegalArgumentException.class, () -> StackTraceUtils.getCallingMethod(MIN));
	}
	
	@Test
	void getCallingMethodSafe() {
		assertEquals(Optional.of(METHOD_SAFE), StackTraceUtils.getCallingMethodSafe());
		assertEquals(Optional.of(METHOD_SAFE), StackTraceUtils.getCallingMethodSafe(0));
		assertEquals(Optional.empty(), StackTraceUtils.getCallingMethodSafe(MAX));
		assertEquals(Optional.empty(), StackTraceUtils.getCallingMethodSafe(MIN));
	}
	
	static {
		try {
			METHOD = StackTraceUtilsTest.class.getDeclaredMethod("getCallingMethod");
			METHOD_SAFE = StackTraceUtilsTest.class.getDeclaredMethod("getCallingMethodSafe");
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}