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

package net.luis.utils.io.codec.mapping;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CodecComponent}.<br>
 *
 * @author Luis-St
 */
class CodecComponentTest {
	
	@Test
	void constructorWithNullRecordComponentThrowsException() {
		assertThrows(NullPointerException.class, () -> new CodecComponent((RecordComponent) null));
	}
	
	@Test
	void constructorWithNullFieldThrowsException() {
		assertThrows(NullPointerException.class, () -> new CodecComponent((Field) null));
	}
	
	@Test
	void recordComponentOperations() {
		record TestRecord(String name, int age, double height) {}
		
		RecordComponent[] components = TestRecord.class.getRecordComponents();
		assertNotNull(components);
		assertEquals(3, components.length);
		
		CodecComponent nameComponent = new CodecComponent(components[0]);
		assertEquals("name", nameComponent.getName());
		assertEquals(String.class, nameComponent.getType());
		
		CodecComponent ageComponent = new CodecComponent(components[1]);
		assertEquals("age", ageComponent.getName());
		assertEquals(int.class, ageComponent.getType());
		
		CodecComponent heightComponent = new CodecComponent(components[2]);
		assertEquals("height", heightComponent.getName());
		assertEquals(double.class, heightComponent.getType());
	}
	
	@Test
	void fieldOperations() throws NoSuchFieldException {
		TestClass testInstance = new TestClass("John", 25, 1.80);
		
		Field nameField = TestClass.class.getDeclaredField("name");
		Field ageField = TestClass.class.getDeclaredField("age");
		Field heightField = TestClass.class.getDeclaredField("height");
		
		CodecComponent nameComponent = new CodecComponent(nameField);
		assertEquals("name", nameComponent.getName());
		assertEquals(String.class, nameComponent.getType());
		
		CodecComponent ageComponent = new CodecComponent(ageField);
		assertEquals("age", ageComponent.getName());
		assertEquals(int.class, ageComponent.getType());
		
		CodecComponent heightComponent = new CodecComponent(heightField);
		assertEquals("height", heightComponent.getName());
		assertEquals(double.class, heightComponent.getType());
	}
	
	@Test
	void recordComponentValueAccess() {
		record TestRecord(String name, int age) {}
		
		RecordComponent[] components = TestRecord.class.getRecordComponents();
		CodecComponent nameComponent = new CodecComponent(components[0]);
		CodecComponent ageComponent = new CodecComponent(components[1]);
		
		TestRecord instance = new TestRecord("Alice", 30);
		
		Optional<Object> nameValue = nameComponent.accessValue(instance);
		assertTrue(nameValue.isPresent());
		assertEquals("Alice", nameValue.get());
		
		Optional<Object> ageValue = ageComponent.accessValue(instance);
		assertTrue(ageValue.isPresent());
		assertEquals(30, ageValue.get());
	}
	
	@Test
	void fieldValueAccess() throws NoSuchFieldException {
		TestClass instance = new TestClass("Bob", 35, 1.75);
		
		Field nameField = TestClass.class.getDeclaredField("name");
		Field ageField = TestClass.class.getDeclaredField("age");
		
		CodecComponent nameComponent = new CodecComponent(nameField);
		CodecComponent ageComponent = new CodecComponent(ageField);
		
		Optional<Object> nameValue = nameComponent.accessValue(instance);
		assertTrue(nameValue.isPresent());
		assertEquals("Bob", nameValue.get());
		
		Optional<Object> ageValue = ageComponent.accessValue(instance);
		assertTrue(ageValue.isPresent());
		assertEquals(35, ageValue.get());
	}
	
	@Test
	void accessValueWithNullInstanceThrowsException() throws NoSuchFieldException {
		Field nameField = TestClass.class.getDeclaredField("name");
		CodecComponent component = new CodecComponent(nameField);
		
		assertThrows(NullPointerException.class, () -> component.accessValue(null));
	}
	
	@Test
	void accessValueReturnsEmptyWhenAccessFails() throws NoSuchFieldException {
		Field privateField = TestClassWithPrivateField.class.getDeclaredField("privateField");
		CodecComponent component = new CodecComponent(privateField);
		
		TestClassWithPrivateField instance = new TestClassWithPrivateField();
		Optional<Object> value = component.accessValue(instance);
		assertTrue(value.isPresent());
		assertEquals("private", value.get());
	}
	
	@Test
	void annotationAccess() throws NoSuchFieldException {
		Field annotatedField = AnnotatedTestClass.class.getDeclaredField("annotatedField");
		Field nonAnnotatedField = AnnotatedTestClass.class.getDeclaredField("nonAnnotatedField");
		
		CodecComponent annotatedComponent = new CodecComponent(annotatedField);
		CodecComponent nonAnnotatedComponent = new CodecComponent(nonAnnotatedField);
		
		assertNotNull(annotatedComponent.getAnnotation(CodecField.class));
		assertNull(nonAnnotatedComponent.getAnnotation(CodecField.class));
		
		assertNull(annotatedComponent.getAnnotation(Deprecated.class));
		assertNull(nonAnnotatedComponent.getAnnotation(Deprecated.class));
	}
	
	@Test
	void recordComponentAnnotationAccess() {
		record AnnotatedRecord(@GenericInfo(String.class) String annotatedField, String nonAnnotatedField) {}
		
		RecordComponent[] components = AnnotatedRecord.class.getRecordComponents();
		CodecComponent annotatedComponent = new CodecComponent(components[0]);
		CodecComponent nonAnnotatedComponent = new CodecComponent(components[1]);
		
		assertNotNull(annotatedComponent.getAnnotation(GenericInfo.class));
		assertNull(nonAnnotatedComponent.getAnnotation(GenericInfo.class));
		
		assertEquals(String.class, annotatedComponent.getAnnotation(GenericInfo.class).value()[0]);
	}
	
	@Test
	void getAnnotationWithNullClassThrowsException() throws NoSuchFieldException {
		Field field = TestClass.class.getDeclaredField("name");
		CodecComponent component = new CodecComponent(field);
		
		assertThrows(NullPointerException.class, () -> component.getAnnotation(null));
	}
	
	@Test
	void equalityAndHashCode() throws NoSuchFieldException {
		record TestRecord(String name) {}
		
		RecordComponent component = TestRecord.class.getRecordComponents()[0];
		CodecComponent codecComponent1 = new CodecComponent(component);
		CodecComponent codecComponent2 = new CodecComponent(component);
		
		assertEquals(codecComponent1, codecComponent2);
		assertEquals(codecComponent1.hashCode(), codecComponent2.hashCode());
		
		Field field = TestClass.class.getDeclaredField("name");
		CodecComponent fieldComponent1 = new CodecComponent(field);
		CodecComponent fieldComponent2 = new CodecComponent(field);
		
		assertEquals(fieldComponent1, fieldComponent2);
		assertEquals(fieldComponent1.hashCode(), fieldComponent2.hashCode());
		
		assertNotEquals(codecComponent1, fieldComponent1);
		
		assertNotEquals(codecComponent1, null);
		assertNotEquals(codecComponent1, "string");
	}
	
	@Test
	void differentComponentsAreNotEqual() throws NoSuchFieldException {
		record TestRecord(String name, int age) {}
		
		RecordComponent[] components = TestRecord.class.getRecordComponents();
		CodecComponent nameComponent = new CodecComponent(components[0]);
		CodecComponent ageComponent = new CodecComponent(components[1]);
		
		assertNotEquals(nameComponent, ageComponent);
	}
	
	//region Test Classes
	private static class TestClass {
		private final String name;
		private final int age;
		private final double height;
		
		private TestClass(String name, int age, double height) {
			this.name = name;
			this.age = age;
			this.height = height;
		}
	}
	
	private static class TestClassWithPrivateField {
		private String privateField = "private";
	}
	
	private static class AnnotatedTestClass {
		@CodecField
		private final String annotatedField = "annotated";
		
		private final String nonAnnotatedField = "nonAnnotated";
	}
	//endregion
}
