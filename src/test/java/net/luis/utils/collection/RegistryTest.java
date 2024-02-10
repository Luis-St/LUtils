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

package net.luis.utils.collection;

import com.google.common.collect.Lists;
import net.luis.utils.exception.ModificationException;
import net.luis.utils.exception.NoSuchItemException;
import net.luis.utils.util.Utils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Registry}.<br>
 *
 * @author Luis-St
 */
class RegistryTest {
	
	@Test
	void of() {
		assertNotNull(Registry.of());
		assertDoesNotThrow(() -> Registry.of(Lists.newArrayList()));
		assertThrows(NullPointerException.class, () -> Registry.of(null));
	}
	
	@Test
	void register() {
		Registry<Integer> registry = Registry.of();
		assertNotNull(registry.register(10));
		assertDoesNotThrow(() -> registry.register(10));
		assertThrows(NullPointerException.class, () -> registry.register((Integer) null));
		assertNotNull(registry.register(uniqueId -> 10));
		assertDoesNotThrow(() -> registry.register(uniqueId -> 10));
		assertThrows(NullPointerException.class, () -> registry.register((Function<UUID, Integer>) null));
	}
	
	@Test
	void remove() {
		Registry<Integer> registry = Registry.of();
		assertFalse(registry.remove(UUID.fromString("00000000-0000-0000-0000-000000000000")));
		UUID uniqueId = registry.register(10);
		assertTrue(registry.remove(uniqueId));
		assertDoesNotThrow(() -> registry.remove(null));
		assertFalse(registry.remove(null));
	}
	
	@Test
	void get() {
		Registry<Integer> registry = Registry.of();
		UUID uniqueId = registry.register(10);
		assertEquals(10, registry.get(uniqueId));
		assertThrows(NullPointerException.class, () -> registry.get(null));
	}
	
	@Test
	void getOrThrow() {
		Registry<Integer> registry = Registry.of();
		UUID uniqueId = registry.register(10);
		assertDoesNotThrow(() -> registry.getOrThrow(uniqueId));
		assertThrows(NullPointerException.class, () -> registry.getOrThrow(null));
		assertThrows(NoSuchItemException.class, () -> registry.getOrThrow(UUID.randomUUID()));
	}
	
	@Test
	void getUniqueId() {
		Registry<Integer> registry = Registry.of();
		UUID uniqueId = registry.register(10);
		assertEquals(uniqueId, registry.getUniqueId(10));
		assertNotNull(registry.getUniqueId(20));
		assertDoesNotThrow(() -> registry.getUniqueId(null));
		assertEquals(Utils.EMPTY_UUID, registry.getUniqueId(null));
	}
	
	@Test
	void containsKey() {
		Registry<Integer> registry = Registry.of();
		UUID uniqueId = registry.register(10);
		assertTrue(registry.contains(uniqueId));
		assertFalse(registry.contains(Utils.EMPTY_UUID));
		assertDoesNotThrow(() -> registry.contains((UUID) null));
	}
	
	@Test
	void containsValue() {
		Registry<Integer> registry = Registry.of();
		registry.register(10);
		assertTrue(registry.contains(10));
		assertFalse(registry.contains(20));
		assertDoesNotThrow(() -> registry.contains((Integer) null));
	}
	
	@Test
	void getKeys() {
		Registry<Integer> registry = Registry.of();
		UUID uniqueId = registry.register(10);
		registry.register(20);
		assertEquals(2, registry.getKeys().size());
		assertTrue(registry.getKeys().contains(uniqueId));
	}
	
	@Test
	void getItems() {
		Registry<Integer> registry = Registry.of();
		registry.register(10);
		registry.register(20);
		assertEquals(2, registry.getItems().size());
		assertTrue(registry.getItems().contains(10));
		assertTrue(registry.getItems().contains(20));
	}
	
	@Test
	void isEmpty() {
		Registry<Integer> registry = Registry.of();
		assertTrue(registry.isEmpty());
		registry.register(10);
		assertFalse(registry.isEmpty());
	}
	
	@Test
	void size() {
		Registry<Integer> registry = Registry.of();
		assertEquals(0, registry.size());
		registry.register(10);
		assertEquals(1, registry.size());
	}
	
	@Test
	void clear() {
		Registry<Integer> registry = Registry.of();
		registry.register(10);
		registry.clear();
		assertTrue(registry.isEmpty());
	}
	
	@Test
	void freeze() {
		Registry<Integer> registry = Registry.freezable();
		assertDoesNotThrow(() -> registry.register(10));
		registry.freeze();
		assertThrows(ModificationException.class, () -> registry.register(10));
	}
	
	@Test
	void isFrozen() {
		Registry<Integer> registry1 = Registry.freezable();
		assertFalse(registry1.isFrozen());
		registry1.freeze();
		assertTrue(registry1.isFrozen());
		Registry<Integer> registry2 = Registry.of();
		registry2.freeze();
		assertFalse(registry2.isFrozen());
	}
}