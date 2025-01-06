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

package net.luis.utils.collection.registry;

import com.google.common.collect.Lists;
import net.luis.utils.annotation.type.MockObject;
import net.luis.utils.collection.registry.key.KeyGenerator;
import net.luis.utils.collection.registry.key.RegistryKey;
import net.luis.utils.exception.ModificationException;
import net.luis.utils.exception.NoSuchItemException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Registry}.<br>
 *
 * @author Luis-St
 */
class RegistryTest {
	
	private static final KeyGenerator<MockedRegistryKey> KEY_GENERATOR = MockedRegistryKey.createKeyGenerator();
	
	@Test
	void of() {
		assertThrows(NullPointerException.class, () -> Registry.of(null));
		assertNotNull(Registry.of(KEY_GENERATOR));
		assertDoesNotThrow(() -> Registry.of(KEY_GENERATOR));
		
		assertThrows(NullPointerException.class, () -> Registry.of(null, Lists.newArrayList()));
		assertThrows(NullPointerException.class, () -> Registry.of(KEY_GENERATOR, null));
		assertNotNull(Registry.of(KEY_GENERATOR, Lists.newArrayList()));
		assertDoesNotThrow(() -> Registry.of(KEY_GENERATOR, Lists.newArrayList()));
	}
	
	@Test
	void register() {
		Registry<MockedRegistryKey, Integer> registry = Registry.of(KEY_GENERATOR);
		assertNotNull(registry.register(10));
		assertDoesNotThrow(() -> registry.register(10));
		assertThrows(NullPointerException.class, () -> registry.register((Integer) null));
		assertNotNull(registry.register(registryKey -> 10));
		assertDoesNotThrow(() -> registry.register(uniqueId -> 10));
		assertThrows(NullPointerException.class, () -> registry.register((Function<MockedRegistryKey, Integer>) null));
	}
	
	@Test
	void remove() {
		Registry<MockedRegistryKey, Integer> registry = Registry.of(KEY_GENERATOR);
		assertFalse(registry.remove(new MockedRegistryKey(10)));
		assertTrue(registry.remove(registry.register(10)));
		assertDoesNotThrow(() -> registry.remove(null));
		assertFalse(registry.remove(null));
	}
	
	@Test
	void get() {
		Registry<MockedRegistryKey, Integer> registry = Registry.of(KEY_GENERATOR);
		MockedRegistryKey registryKey = registry.register(10);
		assertEquals(10, registry.get(registryKey));
		assertThrows(NullPointerException.class, () -> registry.get(null));
	}
	
	@Test
	void getOrThrow() {
		Registry<MockedRegistryKey, Integer> registry = Registry.of(KEY_GENERATOR);
		MockedRegistryKey registryKey = registry.register(10);
		
		assertDoesNotThrow(() -> registry.getOrThrow(registryKey));
		assertThrows(NullPointerException.class, () -> registry.getOrThrow(null));
		assertThrows(NoSuchItemException.class, () -> registry.getOrThrow(new MockedRegistryKey(100)));
	}
	
	@Test
	void getKey() {
		Registry<MockedRegistryKey, Integer> registry = Registry.of(KEY_GENERATOR);
		MockedRegistryKey registryKey = registry.register(10);
		
		assertEquals(registryKey, registry.getKey(10));
		assertNull(registry.getKey(20));
		assertDoesNotThrow(() -> registry.getKey(null));
		assertNull(registry.getKey(null));
	}
	
	@Test
	void containsKey() {
		Registry<MockedRegistryKey, Integer> registry = Registry.of(KEY_GENERATOR);
		MockedRegistryKey registryKey = registry.register(10);
		
		assertTrue(registry.contains(registryKey));
		assertFalse(registry.contains(new MockedRegistryKey(200)));
		assertDoesNotThrow(() -> registry.contains((MockedRegistryKey) null));
	}
	
	@Test
	void containsValue() {
		Registry<MockedRegistryKey, Integer> registry = Registry.of(KEY_GENERATOR);
		
		registry.register(10);
		assertTrue(registry.contains(10));
		assertFalse(registry.contains(20));
		assertDoesNotThrow(() -> registry.contains((Integer) null));
	}
	
	@Test
	void getKeys() {
		Registry<MockedRegistryKey, Integer> registry = Registry.of(KEY_GENERATOR);
		MockedRegistryKey registryKey = registry.register(10);
		
		registry.register(20);
		assertEquals(2, registry.getKeys().size());
		assertTrue(registry.getKeys().contains(registryKey));
	}
	
	@Test
	void getItems() {
		Registry<MockedRegistryKey, Integer> registry = Registry.of(KEY_GENERATOR);
		registry.register(10);
		registry.register(20);
		assertEquals(2, registry.getItems().size());
		assertTrue(registry.getItems().contains(10));
		assertTrue(registry.getItems().contains(20));
	}
	
	@Test
	void isEmpty() {
		Registry<MockedRegistryKey, Integer> registry = Registry.of(KEY_GENERATOR);
		assertTrue(registry.isEmpty());
		registry.register(10);
		assertFalse(registry.isEmpty());
	}
	
	@Test
	void size() {
		Registry<MockedRegistryKey, Integer> registry = Registry.of(KEY_GENERATOR);
		assertEquals(0, registry.size());
		registry.register(10);
		assertEquals(1, registry.size());
	}
	
	@Test
	void clear() {
		Registry<MockedRegistryKey, Integer> registry = Registry.of(KEY_GENERATOR);
		registry.register(10);
		registry.clear();
		assertTrue(registry.isEmpty());
	}
	
	@Test
	void freeze() {
		Registry<MockedRegistryKey, Integer> registry = Registry.freezable(KEY_GENERATOR);
		assertDoesNotThrow(() -> registry.register(10));
		registry.freeze();
		assertThrows(ModificationException.class, () -> registry.register(10));
	}
	
	@Test
	void isFrozen() {
		Registry<MockedRegistryKey, Integer> registry1 = Registry.freezable(KEY_GENERATOR);
		assertFalse(registry1.isFrozen());
		registry1.freeze();
		assertTrue(registry1.isFrozen());
		Registry<MockedRegistryKey, Integer> registry2 = Registry.of(KEY_GENERATOR);
		registry2.freeze();
		assertFalse(registry2.isFrozen());
	}
	
	//region Mocked classes
	@MockObject(RegistryKey.class)
	private static class MockedRegistryKey implements RegistryKey<Integer> {
		
		private final int key;
		
		private MockedRegistryKey(int key) {
			this.key = key;
		}
		
		private static @NotNull KeyGenerator<MockedRegistryKey> createKeyGenerator() {
			return new KeyGenerator<>() {
				private int currentKey = 0;
				
				@Override
				public @NotNull MockedRegistryKey generateKey() {
					return new MockedRegistryKey(this.currentKey++);
				}
			};
		}
		
		@Override
		public @NotNull Integer getKey() {
			return this.key;
		}
		
		@Override
		public int compareTo(@NotNull RegistryKey<Integer> otherKey) {
			return Integer.compare(this.key, otherKey.getKey());
		}
		
		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (!(object instanceof MockedRegistryKey that)) return false;
			
			return this.key == that.key;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.key);
		}
	}
	//endregion
}