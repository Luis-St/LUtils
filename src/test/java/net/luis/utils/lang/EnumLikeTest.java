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
	void getPredefinedConstants() {
		assertThrows(NullPointerException.class, () -> EnumLike.getPredefinedConstants(null));
		assertEquals(4, EnumLike.getPredefinedConstants(Example.class).size());
		List<EnumConstant<Example>> list = EnumLike.getPredefinedConstants(Example.class);
		assertEquals(new EnumConstant<>("EXAMPLE_0", 0, Example.EXAMPLE_0), list.get(0));
		assertEquals(new EnumConstant<>("EXAMPLE_1", 1, Example.EXAMPLE_1), list.get(1));
		assertEquals(new EnumConstant<>("EXAMPLE_2", 2, Example.EXAMPLE_2), list.get(2));
		assertEquals(new EnumConstant<>("EXAMPLE_3", 3, Example.EXAMPLE_3), list.get(3));
	}
	
	@Test
	void values() {
		assertThrows(NullPointerException.class, () -> EnumLike.values(null));
		assertEquals(5, EnumLike.values(Example.class).size());
		List<Example> list = EnumLike.values(Example.class);
		assertEquals(Example.EXAMPLE_0, list.get(0));
		assertEquals(Example.EXAMPLE_1, list.get(1));
		assertEquals(Example.EXAMPLE_2, list.get(2));
		assertEquals(Example.EXAMPLE_3, list.get(3));
		assertEquals(TestClass.EXAMPLE_4, list.get(4));
	}
	
	@Test
	void valueOf() {
		assertThrows(NullPointerException.class, () -> EnumLike.valueOf(null, "Example 0"));
		assertThrows(IllegalStateException.class, () -> EnumLike.valueOf(Example.class, null));
		assertEquals(Example.EXAMPLE_0, EnumLike.valueOf(Example.class, "EXAMPLE 0"));
		assertEquals(Example.EXAMPLE_1, EnumLike.valueOf(Example.class, "EXAMPLE 1"));
		assertEquals(Example.EXAMPLE_2, EnumLike.valueOf(Example.class, "EXAMPLE 2"));
		assertEquals(Example.EXAMPLE_3, EnumLike.valueOf(Example.class, "EXAMPLE 3"));
		assertEquals(TestClass.EXAMPLE_4, EnumLike.valueOf(Example.class, "EXAMPLE 4"));
		assertEquals(Example.EXAMPLE_0, EnumLike.valueOf(Example.class, "example 0"));
		assertEquals(Example.EXAMPLE_1, EnumLike.valueOf(Example.class, "example 1"));
		assertEquals(Example.EXAMPLE_2, EnumLike.valueOf(Example.class, "example 2"));
		assertEquals(Example.EXAMPLE_3, EnumLike.valueOf(Example.class, "example 3"));
		assertEquals(TestClass.EXAMPLE_4, EnumLike.valueOf(Example.class, "example 4"));
	}
	
	@Test
	void name() {
		assertEquals("Example 0", Example.EXAMPLE_0.name());
		assertEquals("Example 1", Example.EXAMPLE_1.name());
		assertEquals("Example 2", Example.EXAMPLE_2.name());
		assertEquals("Example 3", Example.EXAMPLE_3.name());
		assertEquals("Example 4", TestClass.EXAMPLE_4.name());
	}
	
	@Test
	void ordinal() {
		assertEquals(0, Example.EXAMPLE_0.ordinal());
		assertEquals(1, Example.EXAMPLE_1.ordinal());
		assertEquals(2, Example.EXAMPLE_2.ordinal());
		assertEquals(3, Example.EXAMPLE_3.ordinal());
		assertEquals(4, TestClass.EXAMPLE_4.ordinal());
	}
	
	@Test
	void compareTo() {
		assertThrows(NullPointerException.class, () -> Example.EXAMPLE_2.compareTo(null));
		assertEquals(0, Example.EXAMPLE_2.compareTo(Example.EXAMPLE_2));
		assertEquals(-1, Example.EXAMPLE_2.compareTo(Example.EXAMPLE_3));
		assertEquals(1, Example.EXAMPLE_3.compareTo(Example.EXAMPLE_2));
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
		
		public Example(String name) {
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