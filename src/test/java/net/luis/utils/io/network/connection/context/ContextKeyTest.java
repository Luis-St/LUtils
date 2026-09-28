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

package net.luis.utils.io.network.connection.context;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ContextKey}.<br>
 *
 * @author Luis-St
 */
class ContextKeyTest {
	
	@Test
	void constructWithValidNameAndType() {
		ContextKey<String> key = new ContextKey<>("session", String.class);
		
		assertEquals("session", key.name());
		assertSame(String.class, key.type());
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new ContextKey<>(null, String.class));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new ContextKey<String>("session", null));
	}
	
	@Test
	void constructWithEmptyName() {
		assertThrows(IllegalArgumentException.class, () -> new ContextKey<>("", String.class));
	}
	
	@Test
	void constructWithBlankName() {
		assertThrows(IllegalArgumentException.class, () -> new ContextKey<>("   ", String.class));
		assertThrows(IllegalArgumentException.class, () -> new ContextKey<>("\t", String.class));
		assertThrows(IllegalArgumentException.class, () -> new ContextKey<>("\n", String.class));
	}
	
	@Test
	void ofWithNullName() {
		assertThrows(NullPointerException.class, () -> ContextKey.of(null, String.class));
	}
	
	@Test
	void ofWithNullType() {
		assertThrows(NullPointerException.class, () -> ContextKey.of("session", null));
	}
	
	@Test
	void ofWithBlankName() {
		assertThrows(IllegalArgumentException.class, () -> ContextKey.of(" ", String.class));
	}
	
	@Test
	void constructWithNameContainingWhitespace() {
		ContextKey<String> key = new ContextKey<>(" session ", String.class);
		
		assertEquals(" session ", key.name());
		assertSame(String.class, key.type());
	}
	
	@Test
	void ofCreatesEqualKey() {
		ContextKey<String> created = ContextKey.of("k", String.class);
		ContextKey<String> constructed = new ContextKey<>("k", String.class);
		
		assertEquals(constructed, created);
		assertEquals(constructed.hashCode(), created.hashCode());
	}
	
	@Test
	void toStringFormat() {
		assertEquals("session: java.lang.String", ContextKey.of("session", String.class).toString());
	}
	
	@Test
	void accessorsReturnConstructorArguments() {
		ContextKey<byte[]> key = new ContextKey<>("payload", byte[].class);
		
		assertEquals("payload", key.name());
		assertSame(byte[].class, key.type());
	}
	
	@Test
	void keysWithSameNameAndDifferentTypeAreNotEqual() {
		ContextKey<String> stringKey = ContextKey.of("id", String.class);
		ContextKey<Integer> integerKey = ContextKey.of("id", Integer.class);
		
		assertNotEquals(stringKey, integerKey);
		assertEquals(stringKey.name(), integerKey.name());
	}
	
	@Test
	void keyWithPrimitiveWrapperAndBoxedType() {
		ContextKey<Integer> key = ContextKey.of("count", Integer.class);
		
		assertSame(Integer.class, key.type());
		assertNotEquals(int.class, key.type());
	}
	
	@Test
	void toStringWithNestedTypeUsesBinaryName() {
		String representation = ContextKey.of("value", TestValue.class).toString();
		
		assertTrue(representation.startsWith("value: "));
		assertTrue(representation.contains("$"));
		assertTrue(representation.endsWith(TestValue.class.getName()));
	}
	
	private record TestValue(String name) {}
}
