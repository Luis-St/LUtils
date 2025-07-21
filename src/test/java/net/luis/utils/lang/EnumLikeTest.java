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

import com.google.common.collect.Lists;
import net.luis.utils.annotation.ReflectiveUsage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class EnumLikeTest {
	
	@Test
	void getPredefinedConstantsFailsWithNullClass() {
		assertThrows(NullPointerException.class, () -> EnumLike.getPredefinedConstants(null));
	}
	
	@Test
	void getPredefinedConstantsReturnsCorrectConstants() {
		List<EnumConstant<Example>> constants = EnumLike.getPredefinedConstants(Example.class);
		
		assertEquals(4, constants.size());
		assertEquals(new EnumConstant<>("EXAMPLE_0", 0, Example.EXAMPLE_0), constants.get(0));
		assertEquals(new EnumConstant<>("EXAMPLE_1", 1, Example.EXAMPLE_1), constants.get(1));
		assertEquals(new EnumConstant<>("EXAMPLE_2", 2, Example.EXAMPLE_2), constants.get(2));
		assertEquals(new EnumConstant<>("EXAMPLE_3", 3, Example.EXAMPLE_3), constants.get(3));
	}
	
	@Test
	void getPredefinedConstantsHandlesEmptyClass() {
		List<EnumConstant<EmptyExample>> constants = EnumLike.getPredefinedConstants(EmptyExample.class);
		assertTrue(constants.isEmpty());
	}
	
	@Test
	void valuesFailsWithNullClass() {
		assertThrows(NullPointerException.class, () -> EnumLike.values(null));
	}
	
	@Test
	void valuesReturnsAllInstances() {
		List<Example> values = EnumLike.values(Example.class);
		
		assertEquals(5, values.size());
		assertEquals(Example.EXAMPLE_0, values.get(0));
		assertEquals(Example.EXAMPLE_1, values.get(1));
		assertEquals(Example.EXAMPLE_2, values.get(2));
		assertEquals(Example.EXAMPLE_3, values.get(3));
		assertEquals(TestClass.EXAMPLE_4, values.get(4));
	}
	
	@Test
	void valuesHandlesEmptyClass() {
		List<EmptyExample> values = EnumLike.values(EmptyExample.class);
		assertTrue(values.isEmpty());
	}
	
	@Test
	void valueOfFailsWithNullClass() {
		assertThrows(NullPointerException.class, () -> EnumLike.valueOf(null, "Example 0"));
	}
	
	@Test
	void valueOfFailsWithNullName() {
		assertThrows(IllegalStateException.class, () -> EnumLike.valueOf(Example.class, null));
	}
	
	@Test
	void valueOfFailsWithNonExistentName() {
		assertThrows(IllegalStateException.class, () -> EnumLike.valueOf(Example.class, "NON_EXISTENT"));
	}
	
	@Test
	void valueOfFindsConstantsCaseInsensitive() {
		assertEquals(Example.EXAMPLE_0, EnumLike.valueOf(Example.class, "EXAMPLE 0"));
		assertEquals(Example.EXAMPLE_1, EnumLike.valueOf(Example.class, "EXAMPLE 1"));
		assertEquals(Example.EXAMPLE_2, EnumLike.valueOf(Example.class, "EXAMPLE 2"));
		assertEquals(Example.EXAMPLE_3, EnumLike.valueOf(Example.class, "EXAMPLE 3"));
		assertEquals(TestClass.EXAMPLE_4, EnumLike.valueOf(Example.class, "EXAMPLE 4"));
	}
	
	@Test
	void valueOfFindsConstantsLowerCase() {
		assertEquals(Example.EXAMPLE_0, EnumLike.valueOf(Example.class, "example 0"));
		assertEquals(Example.EXAMPLE_1, EnumLike.valueOf(Example.class, "example 1"));
		assertEquals(Example.EXAMPLE_2, EnumLike.valueOf(Example.class, "example 2"));
		assertEquals(Example.EXAMPLE_3, EnumLike.valueOf(Example.class, "example 3"));
		assertEquals(TestClass.EXAMPLE_4, EnumLike.valueOf(Example.class, "example 4"));
	}
	
	@Test
	void valueOfFindsConstantsMixedCase() {
		assertEquals(Example.EXAMPLE_0, EnumLike.valueOf(Example.class, "Example 0"));
		assertEquals(Example.EXAMPLE_1, EnumLike.valueOf(Example.class, "eXaMpLe 1"));
		assertEquals(Example.EXAMPLE_2, EnumLike.valueOf(Example.class, "EXAMPLE 2"));
	}
	
	@Test
	void nameReturnsCorrectNames() {
		assertEquals("Example 0", Example.EXAMPLE_0.name());
		assertEquals("Example 1", Example.EXAMPLE_1.name());
		assertEquals("Example 2", Example.EXAMPLE_2.name());
		assertEquals("Example 3", Example.EXAMPLE_3.name());
		assertEquals("Example 4", TestClass.EXAMPLE_4.name());
	}
	
	@Test
	void ordinalReturnsCorrectOrder() {
		assertEquals(0, Example.EXAMPLE_0.ordinal());
		assertEquals(1, Example.EXAMPLE_1.ordinal());
		assertEquals(2, Example.EXAMPLE_2.ordinal());
		assertEquals(3, Example.EXAMPLE_3.ordinal());
		assertEquals(4, TestClass.EXAMPLE_4.ordinal());
	}
	
	@Test
	void compareToFailsWithNull() {
		assertThrows(NullPointerException.class, () -> Example.EXAMPLE_2.compareTo(null));
	}
	
	@Test
	void compareToReturnsZeroForSameInstance() {
		assertEquals(0, Example.EXAMPLE_2.compareTo(Example.EXAMPLE_2));
	}
	
	@Test
	void compareToReturnsCorrectOrder() {
		assertTrue(Example.EXAMPLE_0.compareTo(Example.EXAMPLE_1) < 0);
		assertTrue(Example.EXAMPLE_1.compareTo(Example.EXAMPLE_0) > 0);
		assertTrue(Example.EXAMPLE_2.compareTo(Example.EXAMPLE_3) < 0);
		assertTrue(Example.EXAMPLE_3.compareTo(Example.EXAMPLE_2) > 0);
	}
	
	@Test
	void compareToWorksAcrossClasses() {
		assertTrue(Example.EXAMPLE_3.compareTo(TestClass.EXAMPLE_4) < 0);
		assertTrue(TestClass.EXAMPLE_4.compareTo(Example.EXAMPLE_3) > 0);
	}
	
	//region Static initializer
	static {
		Example.EXAMPLE_2.name();
		TestClass.EXAMPLE_4.name();
	}
	//endregion
	
	//region Internal
	private static record Example(String name) implements EnumLike<Example> {
		
		@ReflectiveUsage
		private static final List<Example> VALUES = Lists.newLinkedList();
		private static final Example EXAMPLE_0 = new Example("Example 0");
		protected static final Example EXAMPLE_1 = new Example("Example 1");
		public static final Example EXAMPLE_2 = new Example("Example 2");
		public static final Example EXAMPLE_3 = new Example("Example 3");
		
		private Example(String name) {
			this.name = name;
			VALUES.add(this);
		}
		
		@Override
		public @NotNull String name() {
			return this.name;
		}
	}
	
	private static record EmptyExample(String name) implements EnumLike<EmptyExample> {
		
		@ReflectiveUsage
		private static final List<EmptyExample> VALUES = Lists.newLinkedList();
		
		private EmptyExample(String name) {
			this.name = name;
			VALUES.add(this);
		}
		
		@Override
		public @NotNull String name() {
			return this.name;
		}
	}
	
	private static class TestClass {
		
		public static final Example EXAMPLE_4 = new Example("Example 4");
	}
	//endregion
}
