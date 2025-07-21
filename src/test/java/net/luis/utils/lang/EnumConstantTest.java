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

package net.luis.utils.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EnumConstant}.
 *
 * @author Luis-St
 */
class EnumConstantTest {
	
	@Test
	void constructorWithValidParameters() {
		EnumConstant<String> constant = new EnumConstant<>("CONSTANT", 5, "test_value");
		
		assertEquals("CONSTANT", constant.name());
		assertEquals(5, constant.ordinal());
		assertEquals("test_value", constant.value());
	}
	
	@Test
	void constructorWithZeroOrdinal() {
		EnumConstant<Integer> constant = new EnumConstant<>("ZERO", 0, 42);
		
		assertEquals("ZERO", constant.name());
		assertEquals(0, constant.ordinal());
		assertEquals(42, constant.value());
	}
	
	@Test
	void constructorWithLargeOrdinal() {
		EnumConstant<Double> constant = new EnumConstant<>("LARGE", Integer.MAX_VALUE, 3.14);
		
		assertEquals("LARGE", constant.name());
		assertEquals(Integer.MAX_VALUE, constant.ordinal());
		assertEquals(3.14, constant.value());
	}
	
	@Test
	void constructorFailsWithNullName() {
		assertThrows(NullPointerException.class, () -> new EnumConstant<>(null, 0, "value"));
	}
	
	@Test
	void constructorFailsWithNullValue() {
		assertThrows(NullPointerException.class, () -> new EnumConstant<>("NAME", 0, null));
	}
	
	@Test
	void constructorFailsWithNegativeOrdinal() {
		assertThrows(IllegalArgumentException.class, () -> new EnumConstant<>("NAME", -1, "value"));
		
		assertThrows(IllegalArgumentException.class, () -> new EnumConstant<>("NAME", Integer.MIN_VALUE, "value"));
	}
	
	@Test
	void constructorAcceptsBothNullNameAndValue() {
		assertThrows(NullPointerException.class, () -> new EnumConstant<>(null, 0, null));
	}
	
	@Test
	void nameReturnsCorrectValue() {
		EnumConstant<String> constant1 = new EnumConstant<>("FIRST", 0, "value1");
		EnumConstant<String> constant2 = new EnumConstant<>("SECOND", 1, "value2");
		
		assertEquals("FIRST", constant1.name());
		assertEquals("SECOND", constant2.name());
		assertNotEquals(constant1.name(), constant2.name());
	}
	
	@Test
	void ordinalReturnsCorrectValue() {
		EnumConstant<String> constant1 = new EnumConstant<>("NAME", 10, "value1");
		EnumConstant<String> constant2 = new EnumConstant<>("NAME", 20, "value2");
		
		assertEquals(10, constant1.ordinal());
		assertEquals(20, constant2.ordinal());
		assertNotEquals(constant1.ordinal(), constant2.ordinal());
	}
	
	@Test
	void valueReturnsCorrectValue() {
		EnumConstant<String> stringConstant = new EnumConstant<>("NAME", 0, "string_value");
		EnumConstant<Integer> intConstant = new EnumConstant<>("NAME", 0, 123);
		
		assertEquals("string_value", stringConstant.value());
		assertEquals(123, intConstant.value());
		
		EnumConstant<String> another = new EnumConstant<>("NAME", 0, "different");
		assertNotEquals(stringConstant.value(), another.value());
	}
	
	@Test
	void constantsWithSameDataAreEqual() {
		EnumConstant<String> constant1 = new EnumConstant<>("TEST", 5, "value");
		EnumConstant<String> constant2 = new EnumConstant<>("TEST", 5, "value");
		
		assertEquals(constant1, constant2);
		assertEquals(constant1.hashCode(), constant2.hashCode());
	}
	
	@Test
	void constantsWithDifferentDataAreNotEqual() {
		EnumConstant<String> constant1 = new EnumConstant<>("TEST1", 5, "value");
		EnumConstant<String> constant2 = new EnumConstant<>("TEST2", 5, "value");
		EnumConstant<String> constant3 = new EnumConstant<>("TEST1", 6, "value");
		EnumConstant<String> constant4 = new EnumConstant<>("TEST1", 5, "different");
		
		assertNotEquals(constant1, constant2);
		assertNotEquals(constant1, constant3);
		assertNotEquals(constant1, constant4);
	}
}
