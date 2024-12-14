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

package net.luis.utils.util.unsafe.reflection;

import net.luis.utils.exception.ReflectionException;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionHelperTest {
	
	private static final Constructor<TestClass> NO_ARGS_CONSTRUCTOR;
	private static final Constructor<TestClass> ARGS_CONSTRUCTOR;
	private static final Field FIELD;
	private static final Field STATIC_FIELD;
	private static final Method GET_FIELD;
	private static final Method SET_FIELD;
	private static final Method GET_STATIC_FIELD;
	private static final Method SET_STATIC_FIELD;
	
	//region Setup
	@BeforeAll
	static void setUpBefore() {
		System.setProperty("reflection.exceptions.throw", "true");
	}
	//endregion
	
	//region Cleanup
	@AfterAll
	static void cleanUpAfter() {
		System.setProperty("reflection.exceptions.throw", "false");
	}
	//endregion
	
	@Test
	void getClassForName() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getClassForName(null));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getClassForName("some.example.Class"));
		assertEquals(String.class, ReflectionHelper.getClassForName("java.lang.String"));
	}
	
	@Test
	void hasInterface() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasInterface(null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasInterface(ArrayList.class, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasInterface(null, RandomAccess.class));
		assertFalse(ReflectionHelper.hasInterface(ArrayList.class, Set.class));
		assertTrue(ReflectionHelper.hasInterface(ArrayList.class, List.class));
		assertTrue(ReflectionHelper.hasInterface(ArrayList.class, RandomAccess.class));
	}
	
	@Test
	void getConstructor() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getConstructor(null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getConstructor(null, String.class));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getConstructor(TestClass.class, (Class<?>) null));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getConstructor(TestClass.class, Integer.class));
		// No args constructor
		assertDoesNotThrow(() -> ReflectionHelper.getConstructor(TestClass.class, (Class<?>[]) null));
		assertTrue(ReflectionHelper.getConstructor(TestClass.class, (Class<?>[]) null).isPresent());
		assertEquals(NO_ARGS_CONSTRUCTOR, assertDoesNotThrow(() -> ReflectionHelper.getConstructor(TestClass.class, (Class<?>[]) null).orElseThrow()));
		// Args constructor
		assertDoesNotThrow(() -> ReflectionHelper.getConstructor(TestClass.class, String.class));
		assertTrue(ReflectionHelper.getConstructor(TestClass.class, String.class).isPresent());
		assertEquals(ARGS_CONSTRUCTOR, assertDoesNotThrow(() -> ReflectionHelper.getConstructor(TestClass.class, String.class).orElseThrow()));
	}
	
	@Test
	void hasConstructor() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasConstructor(null, (Predicate<Constructor<TestClass>>) null, (Class<?>[]) null));
		assertFalse(ReflectionHelper.hasConstructor(TestClass.class, (c) -> true, Integer.class));
		// No args constructor
		assertDoesNotThrow(() -> ReflectionHelper.hasConstructor(TestClass.class, (Predicate<Constructor<TestClass>>) null, String.class));
		assertDoesNotThrow(() -> ReflectionHelper.hasConstructor(TestClass.class, (c) -> true, String.class));
		assertFalse(ReflectionHelper.hasConstructor(TestClass.class, (c) -> false, String.class));
		assertTrue(ReflectionHelper.hasConstructor(TestClass.class, (c) -> true, String.class));
		// Args constructor
		assertDoesNotThrow(() -> ReflectionHelper.hasConstructor(TestClass.class, null, (Class<?>[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.hasConstructor(TestClass.class, (c) -> true, (Class<?>[]) null));
		assertFalse(ReflectionHelper.hasConstructor(TestClass.class, (c) -> false, (Class<?>[]) null));
		assertTrue(ReflectionHelper.hasConstructor(TestClass.class, (c) -> true, (Class<?>[]) null));
	}
	
	@Test
	void newInstance() {
		// No args constructor
		assertThrows(NullPointerException.class, () -> ReflectionHelper.newInstance((Constructor<TestClass>) null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.newInstance((Class<TestClass>) null, (Object[]) null));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.newInstance(NO_ARGS_CONSTRUCTOR, ""));
		assertDoesNotThrow(() -> ReflectionHelper.newInstance(NO_ARGS_CONSTRUCTOR, (Object[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.newInstance(TestClass.class, (Object[]) null));
		assertTrue(ReflectionHelper.newInstance(NO_ARGS_CONSTRUCTOR, (Object[]) null).isPresent());
		assertTrue(ReflectionHelper.newInstance(TestClass.class, (Object[]) null).isPresent());
		assertEquals(new TestClass(), assertDoesNotThrow(() -> ReflectionHelper.newInstance(NO_ARGS_CONSTRUCTOR, (Object[]) null).orElseThrow()));
		assertEquals(new TestClass(), assertDoesNotThrow(() -> ReflectionHelper.newInstance(TestClass.class, (Object[]) null).orElseThrow()));
		// Args constructor
		assertThrows(NullPointerException.class, () -> ReflectionHelper.newInstance((Constructor<TestClass>) null, "constructed"));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.newInstance((Class<TestClass>) null, "constructed"));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.newInstance(ARGS_CONSTRUCTOR, (Object[]) null));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.newInstance(ARGS_CONSTRUCTOR, 0));
		assertDoesNotThrow(() -> ReflectionHelper.newInstance(ARGS_CONSTRUCTOR, "constructed"));
		assertDoesNotThrow(() -> ReflectionHelper.newInstance(TestClass.class, "constructed"));
		assertTrue(ReflectionHelper.newInstance(ARGS_CONSTRUCTOR, "constructed").isPresent());
		assertTrue(ReflectionHelper.newInstance(TestClass.class, "constructed").isPresent());
		assertEquals(new TestClass("constructed"), assertDoesNotThrow(() -> ReflectionHelper.newInstance(ARGS_CONSTRUCTOR, "constructed").orElseThrow()));
		assertEquals(new TestClass("constructed"), assertDoesNotThrow(() -> ReflectionHelper.newInstance(TestClass.class, "constructed").orElseThrow()));
	}
	
	//region Tests for getMethod
	@Test
	void getGetterMethod() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getMethod(null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getMethod(null, "getField"));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getMethod(TestClass.class, null));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getMethod(TestClass.class, "getField", Integer.class));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getMethod(TestClass.class, "testMethod", (Class<?>[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.getMethod(TestClass.class, "getField", (Class<?>[]) null));
		assertTrue(ReflectionHelper.getMethod(TestClass.class, "getField", (Class<?>[]) null).isPresent());
		assertEquals(GET_FIELD, assertDoesNotThrow(() -> ReflectionHelper.getMethod(TestClass.class, "getField", (Class<?>[]) null).orElseThrow()));
	}
	
	@Test
	void getSetterMethod() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getMethod(null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getMethod(null, "setField"));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getMethod(TestClass.class, null));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getMethod(TestClass.class, "setField", (Class<?>[]) null));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getMethod(TestClass.class, "setField", Integer.class));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getMethod(TestClass.class, "testMethod", String.class));
		assertDoesNotThrow(() -> ReflectionHelper.getMethod(TestClass.class, "setField", String.class));
		assertTrue(ReflectionHelper.getMethod(TestClass.class, "setField", String.class).isPresent());
		assertEquals(SET_FIELD, assertDoesNotThrow(() -> ReflectionHelper.getMethod(TestClass.class, "setField", String.class).orElseThrow()));
	}
	
	@Test
	void getStaticGetterMethod() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getMethod(null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getMethod(null, "getStaticField"));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getMethod(TestClass.class, null));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getMethod(TestClass.class, "getStaticField", Integer.class));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getMethod(TestClass.class, "getStaticMethod", String.class));
		assertDoesNotThrow(() -> ReflectionHelper.getMethod(TestClass.class, "getStaticField", (Class<?>[]) null));
		assertTrue(ReflectionHelper.getMethod(TestClass.class, "getStaticField", (Class<?>[]) null).isPresent());
		assertEquals(GET_STATIC_FIELD, assertDoesNotThrow(() -> ReflectionHelper.getMethod(TestClass.class, "getStaticField").orElseThrow()));
	}
	
	@Test
	void getStaticSetterMethod() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getMethod(null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getMethod(null, "setStaticField"));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getMethod(TestClass.class, null));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getMethod(TestClass.class, "setStaticField", (Class<?>[]) null));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getMethod(TestClass.class, "setStaticField", Integer.class));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getMethod(TestClass.class, "testStaticField", String.class));
		assertDoesNotThrow(() -> ReflectionHelper.getMethod(TestClass.class, "setStaticField", String.class));
		assertTrue(ReflectionHelper.getMethod(TestClass.class, "setStaticField", String.class).isPresent());
		assertEquals(SET_STATIC_FIELD, assertDoesNotThrow(() -> ReflectionHelper.getMethod(TestClass.class, "setStaticField", String.class).orElseThrow()));
	}
	//endregion
	
	//region Tests for hasMethod
	@Test
	void hasGetterMethod() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasMethod(null, null, null, (Class<?>[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasMethod(null, "getField", null, (Class<?>[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasMethod(TestClass.class, null, null, (Class<?>[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.hasMethod(TestClass.class, "getField", null, (Class<?>[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.hasMethod(TestClass.class, "getField", (Predicate<Method>) null, String.class));
		assertDoesNotThrow(() -> ReflectionHelper.hasMethod(TestClass.class, "getField", (m) -> true, (Class<?>[]) null));
		assertFalse(ReflectionHelper.hasMethod(TestClass.class, "testMethod", (m) -> true, (Class<?>[]) null));
		assertFalse(ReflectionHelper.hasMethod(TestClass.class, "getField", (m) -> true, String.class));
		assertFalse(ReflectionHelper.hasMethod(TestClass.class, "getField", (m) -> false, (Class<?>[]) null));
		assertTrue(ReflectionHelper.hasMethod(TestClass.class, "getField", (m) -> true, (Class<?>[]) null));
	}
	
	@Test
	void hasSetterMethod() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasMethod(null, null, null, (Class<?>[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasMethod(null, "setField", null, (Class<?>[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasMethod(TestClass.class, null, null, (Class<?>[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.hasMethod(TestClass.class, "setField", null, (Class<?>[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.hasMethod(TestClass.class, "setField", (Predicate<Method>) null, String.class));
		assertDoesNotThrow(() -> ReflectionHelper.hasMethod(TestClass.class, "setField", (m) -> true, (Class<?>[]) null));
		assertFalse(ReflectionHelper.hasMethod(TestClass.class, "testMethod", (m) -> true, String.class));
		assertFalse(ReflectionHelper.hasMethod(TestClass.class, "setField", (m) -> true, (Class<?>[]) null));
		assertFalse(ReflectionHelper.hasMethod(TestClass.class, "setField", (m) -> false, String.class));
		assertTrue(ReflectionHelper.hasMethod(TestClass.class, "setField", (m) -> true, String.class));
	}
	
	@Test
	void hasStaticGetterMethod() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasMethod(null, null, null, (Class<?>[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasMethod(null, "getStaticField", null, (Class<?>[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasMethod(TestClass.class, null, null, (Class<?>[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.hasMethod(TestClass.class, "getStaticField", null, (Class<?>[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.hasMethod(TestClass.class, "getStaticField", (Predicate<Method>) null, String.class));
		assertDoesNotThrow(() -> ReflectionHelper.hasMethod(TestClass.class, "getStaticField", (m) -> true, (Class<?>[]) null));
		assertFalse(ReflectionHelper.hasMethod(TestClass.class, "testStaticMethod", (m) -> true, (Class<?>[]) null));
		assertFalse(ReflectionHelper.hasMethod(TestClass.class, "getStaticField", (m) -> true, String.class));
		assertFalse(ReflectionHelper.hasMethod(TestClass.class, "getStaticField", (m) -> false, (Class<?>[]) null));
		assertTrue(ReflectionHelper.hasMethod(TestClass.class, "getStaticField", (m) -> true, (Class<?>[]) null));
	}
	
	@Test
	void hasStaticSetterMethod() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasMethod(null, null, null, (Class<?>[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasMethod(null, "setStaticField", null, (Class<?>[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasMethod(TestClass.class, null, null, (Class<?>[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.hasMethod(TestClass.class, "setStaticField", null, (Class<?>[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.hasMethod(TestClass.class, "setStaticField", (Predicate<Method>) null, String.class));
		assertDoesNotThrow(() -> ReflectionHelper.hasMethod(TestClass.class, "setStaticField", (m) -> true, (Class<?>[]) null));
		assertFalse(ReflectionHelper.hasMethod(TestClass.class, "testStaticMethod", (m) -> true, String.class));
		assertFalse(ReflectionHelper.hasMethod(TestClass.class, "setStaticField", (m) -> true, (Class<?>[]) null));
		assertFalse(ReflectionHelper.hasMethod(TestClass.class, "setStaticField", (m) -> false, String.class));
		assertTrue(ReflectionHelper.hasMethod(TestClass.class, "setStaticField", (m) -> true, String.class));
	}
	//endregion
	
	//region Tests for invoke
	@Test
	void invokeGetter() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(null, null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(null, null, null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(null, "", null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(TestClass.class, null, null, (Object[]) null));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.invoke(TestClass.class, "testMethod", null, (Object[]) null));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.invoke(TestClass.class, "testMethod", new TestClass(), (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(GET_FIELD, null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(TestClass.class, "getField", null, (Object[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.invoke(GET_FIELD, new TestClass(), (Object[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.invoke(TestClass.class, "getField", new TestClass(), (Object[]) null));
		assertTrue(ReflectionHelper.invoke(GET_FIELD, new TestClass(), (Object[]) null).isPresent());
		assertTrue(ReflectionHelper.invoke(TestClass.class, "getField", new TestClass(), (Object[]) null).isPresent());
		assertEquals("field", assertDoesNotThrow(() -> ReflectionHelper.invoke(GET_FIELD, new TestClass(), (Object[]) null).orElseThrow()));
		assertEquals("field", assertDoesNotThrow(() -> ReflectionHelper.invoke(TestClass.class, "getField", new TestClass(), (Object[]) null).orElseThrow()));
	}
	
	@Test
	void invokeSetter() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(null, null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(null, null, null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(null, "", null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(TestClass.class, null, null, (Object[]) null));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.invoke(TestClass.class, "testMethod", null, "invoked"));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.invoke(TestClass.class, "testMethod", new TestClass(), "invoked"));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(SET_FIELD, null, "invoked"));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(TestClass.class, "setField", null, "invoked"));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.invoke(SET_FIELD, new TestClass(), 0));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.invoke(TestClass.class, "setField", new TestClass(), 0));
		assertDoesNotThrow(() -> ReflectionHelper.invoke(SET_FIELD, new TestClass(), "invoked"));
		assertDoesNotThrow(() -> ReflectionHelper.invoke(TestClass.class, "setField", new TestClass(), "invoked"));
		assertTrue(ReflectionHelper.invoke(SET_FIELD, new TestClass(), "invoked").isEmpty());
		assertTrue(ReflectionHelper.invoke(TestClass.class, "setField", new TestClass(), "invoked").isEmpty());
	}
	
	@Test
	void invokeStaticGetter() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(null, null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(null, null, null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(null, "", null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(TestClass.class, null, null, (Object[]) null));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.invoke(TestClass.class, "staticTestMethod", null, (Object[]) null));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.invoke(TestClass.class, "staticTestMethod", new TestClass(), (Object[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.invoke(GET_STATIC_FIELD, null, (Object[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.invoke(TestClass.class, "getStaticField", null, (Object[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.invoke(GET_STATIC_FIELD, new TestClass(), (Object[]) null));
		assertDoesNotThrow(() -> ReflectionHelper.invoke(TestClass.class, "getStaticField", new TestClass(), (Object[]) null));
		assertTrue(ReflectionHelper.invoke(GET_STATIC_FIELD, null, (Object[]) null).isPresent());
		assertTrue(ReflectionHelper.invoke(TestClass.class, "getStaticField", null, (Object[]) null).isPresent());
		assertTrue(ReflectionHelper.invoke(GET_STATIC_FIELD, new TestClass(), (Object[]) null).isPresent());
		assertTrue(ReflectionHelper.invoke(TestClass.class, "getStaticField", new TestClass(), (Object[]) null).isPresent());
		assertEquals("staticField", assertDoesNotThrow(() -> ReflectionHelper.invoke(GET_STATIC_FIELD, null, (Object[]) null).orElseThrow()));
		assertEquals("staticField", assertDoesNotThrow(() -> ReflectionHelper.invoke(TestClass.class, "getStaticField", null, (Object[]) null).orElseThrow()));
		assertEquals("staticField", assertDoesNotThrow(() -> ReflectionHelper.invoke(GET_STATIC_FIELD, new TestClass(), (Object[]) null).orElseThrow()));
		assertEquals("staticField", assertDoesNotThrow(() -> ReflectionHelper.invoke(TestClass.class, "getStaticField", new TestClass(), (Object[]) null).orElseThrow()));
	}
	
	@Test
	void invokeStaticSetter() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(null, null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(null, null, null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(null, "", null, (Object[]) null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.invoke(TestClass.class, null, null, (Object[]) null));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.invoke(TestClass.class, "staticTestMethod", null, "invoked"));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.invoke(TestClass.class, "staticTestMethod", new TestClass(), "invoked"));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.invoke(SET_STATIC_FIELD, new TestClass(), 0));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.invoke(TestClass.class, "setStaticField", new TestClass(), 0));
		assertDoesNotThrow(() -> ReflectionHelper.invoke(SET_STATIC_FIELD, null, "invoked"));
		assertDoesNotThrow(() -> ReflectionHelper.invoke(TestClass.class, "setStaticField", null, "invoked"));
		assertDoesNotThrow(() -> ReflectionHelper.invoke(SET_STATIC_FIELD, new TestClass(), "invoked"));
		assertDoesNotThrow(() -> ReflectionHelper.invoke(TestClass.class, "setStaticField", new TestClass(), "invoked"));
		assertTrue(ReflectionHelper.invoke(SET_STATIC_FIELD, null, "invoked").isEmpty());
		assertTrue(ReflectionHelper.invoke(TestClass.class, "setStaticField", null, "invoked").isEmpty());
		assertTrue(ReflectionHelper.invoke(SET_STATIC_FIELD, new TestClass(), "invoked").isEmpty());
		assertTrue(ReflectionHelper.invoke(TestClass.class, "setStaticField", new TestClass(), "invoked").isEmpty());
	}
	//endregion
	
	//region Tests for getField
	@Test
	void getField() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getField(null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getField(null, "field"));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getField(TestClass.class, null));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getField(TestClass.class, "testField"));
		assertDoesNotThrow(() -> ReflectionHelper.getField(TestClass.class, "field"));
		assertTrue(ReflectionHelper.getField(TestClass.class, "field").isPresent());
		assertEquals(FIELD, assertDoesNotThrow(() -> ReflectionHelper.getField(TestClass.class, "field").orElseThrow()));
	}
	
	@Test
	void getStaticField() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getField(null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getField(null, "staticField"));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.getField(TestClass.class, null));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.getField(TestClass.class, "staticTestField"));
		assertDoesNotThrow(() -> ReflectionHelper.getField(TestClass.class, "staticField"));
		assertTrue(ReflectionHelper.getField(TestClass.class, "staticField").isPresent());
		assertEquals(STATIC_FIELD, assertDoesNotThrow(() -> ReflectionHelper.getField(TestClass.class, "staticField").orElseThrow()));
	}
	//endregion
	
	//region Tests for hasField
	@Test
	void hasField() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasField(null, null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasField(null, "field", null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasField(TestClass.class, null, null));
		assertDoesNotThrow(() -> ReflectionHelper.hasField(TestClass.class, "field", null));
		assertDoesNotThrow(() -> ReflectionHelper.hasField(TestClass.class, "field", null));
		assertDoesNotThrow(() -> ReflectionHelper.hasField(TestClass.class, "field", (f) -> true));
		assertFalse(ReflectionHelper.hasField(TestClass.class, "testField", (f) -> true));
		assertFalse(ReflectionHelper.hasField(TestClass.class, "field", (f) -> false));
		assertTrue(ReflectionHelper.hasField(TestClass.class, "field", (f) -> true));
	}
	
	@Test
	void hasStaticField() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasField(null, null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasField(null, "staticField", null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.hasField(TestClass.class, null, null));
		assertDoesNotThrow(() -> ReflectionHelper.hasField(TestClass.class, "staticField", null));
		assertDoesNotThrow(() -> ReflectionHelper.hasField(TestClass.class, "staticField", null));
		assertDoesNotThrow(() -> ReflectionHelper.hasField(TestClass.class, "staticField", (f) -> true));
		assertFalse(ReflectionHelper.hasField(TestClass.class, "staticTestField", (f) -> true));
		assertFalse(ReflectionHelper.hasField(TestClass.class, "staticField", (f) -> false));
		assertTrue(ReflectionHelper.hasField(TestClass.class, "staticField", (f) -> true));
	}
	//endregion
	
	//region Tests for get
	@Test
	void get() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.get(null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.get(null, null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.get(null, "field", null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.get(TestClass.class, null, null));
		// Above the null checks, below the functional tests
		assertThrows(NullPointerException.class, () -> ReflectionHelper.get(FIELD, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.get(TestClass.class, "field", null));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.get(TestClass.class, "testField", null));
		assertDoesNotThrow(() -> ReflectionHelper.get(FIELD, new TestClass()));
		assertDoesNotThrow(() -> ReflectionHelper.get(TestClass.class, "field", new TestClass()));
		assertTrue(ReflectionHelper.get(FIELD, new TestClass()).isPresent());
		assertTrue(ReflectionHelper.get(TestClass.class, "field", new TestClass()).isPresent());
		assertEquals("field", assertDoesNotThrow(() -> ReflectionHelper.get(FIELD, new TestClass()).orElseThrow()));
		assertEquals("field", assertDoesNotThrow(() -> ReflectionHelper.get(TestClass.class, "field", new TestClass()).orElseThrow()));
	}
	
	@Test
	void getStatic() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.get(null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.get(null, null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.get(null, "field", null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.get(TestClass.class, null, null));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.get(TestClass.class, "testStaticField", null));
		assertDoesNotThrow(() -> ReflectionHelper.get(STATIC_FIELD, null));
		assertDoesNotThrow(() -> ReflectionHelper.get(STATIC_FIELD, new TestClass()));
		assertDoesNotThrow(() -> ReflectionHelper.get(TestClass.class, "staticField", null));
		assertDoesNotThrow(() -> ReflectionHelper.get(TestClass.class, "staticField", new TestClass()));
		assertTrue(ReflectionHelper.get(STATIC_FIELD, null).isPresent());
		assertTrue(ReflectionHelper.get(TestClass.class, "staticField", null).isPresent());
		assertTrue(ReflectionHelper.get(STATIC_FIELD, new TestClass()).isPresent());
		assertTrue(ReflectionHelper.get(TestClass.class, "staticField", new TestClass()).isPresent());
		assertEquals("invoked", assertDoesNotThrow(() -> ReflectionHelper.get(STATIC_FIELD, null).orElseThrow()));
		assertEquals("invoked", assertDoesNotThrow(() -> ReflectionHelper.get(TestClass.class, "staticField", null).orElseThrow()));
		assertEquals("invoked", assertDoesNotThrow(() -> ReflectionHelper.get(STATIC_FIELD, new TestClass()).orElseThrow()));
		assertEquals("invoked", assertDoesNotThrow(() -> ReflectionHelper.get(TestClass.class, "staticField", new TestClass()).orElseThrow()));
	}
	//endregion
	
	//region Tests for set
	@Test
	void set() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.set(null, null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.set(null, null, null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.set(null, "field", null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.set(TestClass.class, null, null, null));
		// Above the null checks, below the functional tests
		assertThrows(NullPointerException.class, () -> ReflectionHelper.set(FIELD, null, "invoked"));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.set(TestClass.class, "field", null, "invoked"));
		assertThrows(ReflectionException.class, () -> ReflectionHelper.set(TestClass.class, "testField", null, "invoked"));
		assertDoesNotThrow(() -> ReflectionHelper.set(FIELD, new TestClass(), "invoked"));
		assertDoesNotThrow(() -> ReflectionHelper.set(TestClass.class, "field", new TestClass(), "invoked"));
	}
	
	@Test
	void setStatic() {
		assertThrows(NullPointerException.class, () -> ReflectionHelper.set(null, null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.set(null, null, null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.set(null, "field", null, null));
		assertThrows(NullPointerException.class, () -> ReflectionHelper.set(TestClass.class, null, null, null));
		// Above the null checks, below the functional tests
		assertThrows(ReflectionException.class, () -> ReflectionHelper.set(TestClass.class, "testStaticField", null, "invoked"));
		assertDoesNotThrow(() -> ReflectionHelper.set(STATIC_FIELD, null, "invoked"));
		assertDoesNotThrow(() -> ReflectionHelper.set(STATIC_FIELD, new TestClass(), "invoked"));
		assertDoesNotThrow(() -> ReflectionHelper.set(TestClass.class, "staticField", null, "invoked"));
		assertDoesNotThrow(() -> ReflectionHelper.set(TestClass.class, "staticField", new TestClass(), "invoked"));
	}
	//endregion
	
	//region Static initializer
	static {
		try {
			NO_ARGS_CONSTRUCTOR = TestClass.class.getDeclaredConstructor();
			ARGS_CONSTRUCTOR = TestClass.class.getDeclaredConstructor(String.class);
			FIELD = TestClass.class.getDeclaredField("field");
			STATIC_FIELD = TestClass.class.getDeclaredField("staticField");
			GET_FIELD = TestClass.class.getDeclaredMethod("getField");
			SET_FIELD = TestClass.class.getDeclaredMethod("setField", String.class);
			GET_STATIC_FIELD = TestClass.class.getDeclaredMethod("getStaticField");
			SET_STATIC_FIELD = TestClass.class.getDeclaredMethod("setStaticField", String.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	//endregion
	
	//region Internal reflection test class
	@SuppressWarnings("EqualsAndHashcode")
	private static final class TestClass {
		
		private static String staticField = "staticField";
		
		private String field = "field";
		
		private TestClass() {}
		
		private TestClass(String field) {
			this.field = field;
		}
		
		private static String getStaticField() {
			return staticField;
		}
		
		private static void setStaticField(String field) {
			staticField = field;
		}
		
		private @Nullable String getField() {
			return this.field;
		}
		
		private void setField(@Nullable String field) {
			this.field = field;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof TestClass testClass)) return false;
			
			return Objects.equals(this.field, testClass.field);
		}
	}
	//endregion
}
