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

package net.luis.utils.util.unsafe.reflection;

import com.google.common.collect.Lists;
import net.luis.utils.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilsTest {
	
	private static final Object[] EMPTY = {};
	private static final Method GET_STRING;
	private static final Method SET_STRING;
	private static final Method SET_STRING_OLD_ACTION;
	private static final Method HAS_STRING;
	private static final Method DO_PRINT;
	
	@Test
	void getRawName() {
		assertThrows(NullPointerException.class, () -> ReflectionUtils.getRawName(null));
		assertThrows(NullPointerException.class, () -> ReflectionUtils.getRawName(null, "get"));
		assertDoesNotThrow(() -> ReflectionUtils.getRawName(GET_STRING, (String) null));
		assertDoesNotThrow(() -> ReflectionUtils.getRawName(GET_STRING, (String[]) null));
		assertEquals("String", ReflectionUtils.getRawName(GET_STRING));
		assertEquals("String", ReflectionUtils.getRawName(SET_STRING));
		assertEquals("String", ReflectionUtils.getRawName(HAS_STRING));
		assertEquals("doPrint", ReflectionUtils.getRawName(DO_PRINT));
		assertEquals("Print", ReflectionUtils.getRawName(DO_PRINT, "do"));
	}
	
	@Test
	void getMethodsForName() {
		assertThrows(NullPointerException.class, () -> ReflectionUtils.getMethodsForName(null, null));
		assertThrows(NullPointerException.class, () -> ReflectionUtils.getMethodsForName(null, "setString"));
		assertDoesNotThrow(() -> ReflectionUtils.getMethodsForName(TestClass.class, null));
		assertEquals(5, ReflectionUtils.getMethodsForName(TestClass.class, null).size());
		assertEquals(Lists.newArrayList(GET_STRING), ReflectionUtils.getMethodsForName(TestClass.class, "getString"));
		assertEquals(Lists.newArrayList(SET_STRING, SET_STRING_OLD_ACTION), ReflectionUtils.getMethodsForName(TestClass.class, "setString"));
		assertEquals(Lists.newArrayList(HAS_STRING), ReflectionUtils.getMethodsForName(TestClass.class, "hasString"));
		assertEquals(Lists.newArrayList(DO_PRINT), ReflectionUtils.getMethodsForName(TestClass.class, "doPrint"));
	}
	
	@Test
	void findParameters() {
		Consumer<String> action = (str) -> {};
		assertThrows(NullPointerException.class, () -> ReflectionUtils.findParameters(null));
		assertThrows(NullPointerException.class, () -> ReflectionUtils.findParameters(GET_STRING, (Object) null));
		assertDoesNotThrow(() -> ReflectionUtils.findParameters(GET_STRING, (Object[]) null));
		assertDoesNotThrow(() -> ReflectionUtils.findParameters(GET_STRING, (List<Pair<Object, String>>) null));
		assertArrayEquals(EMPTY, ReflectionUtils.findParameters(GET_STRING));
		assertArrayEquals(new Object[] { "" }, ReflectionUtils.findParameters(SET_STRING, ""));
		assertArrayEquals(new Object[] { "" }, ReflectionUtils.findParameters(SET_STRING, 10, "", new Object()));
		assertArrayEquals(new Object[] { "", action }, ReflectionUtils.findParameters(SET_STRING_OLD_ACTION, action, ""));
		assertArrayEquals(new Object[] { "", action }, ReflectionUtils.findParameters(SET_STRING_OLD_ACTION, "", action));
		
		List<Pair<Object, String>> parameters = Lists.newArrayList(Pair.of(action, "oldAction"), Pair.of("", "str"));
		assertArrayEquals(new Object[] { "" }, ReflectionUtils.findParameters(SET_STRING, Lists.newArrayList(Pair.of("", "str"))));
		assertArrayEquals(new Object[] { "" }, ReflectionUtils.findParameters(SET_STRING, parameters));
		assertArrayEquals(new Object[] { "", action }, ReflectionUtils.findParameters(SET_STRING_OLD_ACTION, parameters));
		assertArrayEquals(new Object[] { "", action }, ReflectionUtils.findParameters(SET_STRING_OLD_ACTION, "", action));
	}
	
	//region Static initializer
	static {
		try {
			GET_STRING = TestClass.class.getDeclaredMethod("getString");
			SET_STRING = TestClass.class.getDeclaredMethod("setString", String.class);
			SET_STRING_OLD_ACTION = TestClass.class.getDeclaredMethod("setString", String.class, Consumer.class);
			HAS_STRING = TestClass.class.getDeclaredMethod("hasString");
			DO_PRINT = TestClass.class.getDeclaredMethod("doPrint");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	//endregion
	
	//region Internal reflection test class
	private static class TestClass {
		
		private String str;
		
		public @Nullable String getString() {
			return this.str;
		}
		
		public void setString(@Nullable String str) {
			this.str = str;
		}
		
		public void setString(@Nullable String str, @NotNull Consumer<String> oldAction) {
			oldAction.accept(this.str);
			this.str = str;
		}
		
		public boolean hasString() {
			return this.str != null;
		}
		
		public void doPrint() {
			System.out.println(this.str);
		}
	}
	//endregion
}