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

package net.luis.utils.util.unsafe.classpath;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ClassPathUtils}.<br>
 *
 * @author Luis-St
 */
class ClassPathUtilsTest {
	
	@Test
	void getAllClassesReturnsNonEmptyList() {
		List<Class<?>> classes = ClassPathUtils.getAllClasses();
		assertNotNull(classes);
		assertFalse(classes.isEmpty());
	}
	
	@Test
	void getProjectClassesReturnsNonEmptyList() {
		List<Class<?>> classes = ClassPathUtils.getProjectClasses();
		assertNotNull(classes);
		assertFalse(classes.isEmpty());
	}
	
	@Test
	void getClassesWithNullPackageReturnsAllClasses() {
		List<Class<?>> allClasses = ClassPathUtils.getClasses(null);
		assertNotNull(allClasses);
		assertFalse(allClasses.isEmpty());
	}
	
	@Test
	void getClassesWithPackageFiltersCorrectly() {
		List<Class<?>> classes = ClassPathUtils.getClasses("net.luis.utils.util");
		assertNotNull(classes);
		assertTrue(classes.stream().allMatch(c -> c.getName().startsWith("net.luis.utils.util")));
	}
	
	@Test
	void getClassesWithExcludeModeFiltersCorrectly() {
		List<Class<?>> classes = ClassPathUtils.getClasses("java.", ClassPathUtils.Mode.EXCLUDE);
		assertNotNull(classes);
		assertTrue(classes.stream().noneMatch(c -> c.getName().startsWith("java.")));
	}
	
	@Test
	void getAnnotatedClassesNullThrows() {
		assertThrows(NullPointerException.class, () -> ClassPathUtils.getAnnotatedClasses(null));
	}
	
	@Test
	void getAnnotatedClassesFindsAnnotatedClasses() {
		List<Class<?>> functionalInterfaces = ClassPathUtils.getAnnotatedClasses(FunctionalInterface.class);
		assertNotNull(functionalInterfaces);
		assertTrue(functionalInterfaces.stream().allMatch(c -> c.isAnnotationPresent(FunctionalInterface.class)));
	}
	
	@Test
	void getAnnotatedClassesWithPackageNullThrows() {
		assertThrows(NullPointerException.class, () -> ClassPathUtils.getAnnotatedClasses("net.luis", null));
	}
	
	@Test
	void getAnnotatedClassesWithPackageFiltersCorrectly() {
		List<Class<?>> classes = ClassPathUtils.getAnnotatedClasses("net.luis.utils", FunctionalInterface.class);
		assertNotNull(classes);
		assertTrue(classes.stream().allMatch(c -> c.getName().startsWith("net.luis.utils")));
		assertTrue(classes.stream().allMatch(c -> c.isAnnotationPresent(FunctionalInterface.class)));
	}
	
	@Test
	void getAnnotatedMethodsNullThrows() {
		assertThrows(NullPointerException.class, () -> ClassPathUtils.getAnnotatedMethods(null));
	}
	
	@Test
	void getAnnotatedMethodsFindsAnnotatedMethods() {
		List<Method> testMethods = ClassPathUtils.getAnnotatedMethods(Test.class);
		assertNotNull(testMethods);
		assertFalse(testMethods.isEmpty());
		assertTrue(testMethods.stream().allMatch(m -> m.isAnnotationPresent(Test.class)));
	}
	
	@Test
	void getAnnotatedMethodsWithPackageNullThrows() {
		assertThrows(NullPointerException.class, () -> ClassPathUtils.getAnnotatedMethods("net.luis", null));
	}
	
	@Test
	void getAnnotatedMethodsWithPackageFiltersCorrectly() {
		List<Method> methods = ClassPathUtils.getAnnotatedMethods("net.luis.utils.util.unsafe.classpath", Test.class);
		assertNotNull(methods);
		assertTrue(methods.stream().allMatch(m -> m.getDeclaringClass().getName().startsWith("net.luis.utils.util.unsafe.classpath")));
		assertTrue(methods.stream().allMatch(m -> m.isAnnotationPresent(Test.class)));
	}
	
	@Test
	void getAnnotatedFieldsNullThrows() {
		assertThrows(NullPointerException.class, () -> ClassPathUtils.getAnnotatedFields(null));
	}
	
	@Test
	void getAnnotatedFieldsFindsAnnotatedFields() {
		List<Field> deprecatedFields = ClassPathUtils.getAnnotatedFields(Deprecated.class);
		assertNotNull(deprecatedFields);
		assertTrue(deprecatedFields.stream().allMatch(f -> f.isAnnotationPresent(Deprecated.class)));
	}
	
	@Test
	void getAnnotatedFieldsWithPackageNullThrows() {
		assertThrows(NullPointerException.class, () -> ClassPathUtils.getAnnotatedFields("net.luis", null));
	}
	
	@Test
	void modeEnumValues() {
		assertEquals(2, ClassPathUtils.Mode.values().length);
		assertNotNull(ClassPathUtils.Mode.valueOf("INCLUDE"));
		assertNotNull(ClassPathUtils.Mode.valueOf("EXCLUDE"));
	}
	
	@Test
	void getAnnotatedMethodsScansEntireClasspathWithoutLinkageError() {
		List<Method> methods = assertDoesNotThrow(() -> ClassPathUtils.getAnnotatedMethods(Override.class));
		assertNotNull(methods);
		assertTrue(methods.stream().allMatch(m -> m.isAnnotationPresent(Override.class)));
	}
	
	@Test
	void getAnnotatedFieldsScansEntireClasspathWithoutLinkageError() {
		List<Field> fields = assertDoesNotThrow(() -> ClassPathUtils.getAnnotatedFields(Deprecated.class));
		assertNotNull(fields);
		assertTrue(fields.stream().allMatch(f -> f.isAnnotationPresent(Deprecated.class)));
	}
	
	@Test
	void getAnnotatedFieldsWithPackageFiltersCorrectly() {
		List<Field> fields = ClassPathUtils.getAnnotatedFields("net.luis.utils", Deprecated.class);
		assertNotNull(fields);
		assertTrue(fields.stream().allMatch(f -> f.getDeclaringClass().getName().startsWith("net.luis.utils")));
		assertTrue(fields.stream().allMatch(f -> f.isAnnotationPresent(Deprecated.class)));
	}
	
	@Test
	void getClassesWithNullModeIncludesClasses() {
		List<Class<?>> classes = ClassPathUtils.getClasses("net.luis.utils.util", null);
		assertNotNull(classes);
		assertTrue(classes.stream().allMatch(c -> c.getName().startsWith("net.luis.utils.util")));
	}
	
	@Test
	void emptyArraysAreEmpty() {
		assertEquals(0, ClassPathUtils.EMPTY_METHOD_ARRAY.length);
		assertEquals(0, ClassPathUtils.EMPTY_FIELD_ARRAY.length);
	}
}
