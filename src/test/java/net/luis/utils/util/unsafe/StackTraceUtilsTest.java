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