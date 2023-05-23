package net.luis.utils.collection;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
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
		assertThrows(NullPointerException.class, () -> registry.remove(null));
	}
	
	@Test
	void get() {
		Registry<Integer> registry = Registry.of();
		UUID uniqueId = registry.register(10);
		assertEquals(10, registry.get(uniqueId));
		assertThrows(NullPointerException.class, () -> registry.get(null));
	}
	
	@Test
	void getUniqueId() {
		Registry<Integer> registry = Registry.of();
		UUID uniqueId = registry.register(10);
		assertEquals(uniqueId, registry.getUniqueId(10));
		assertNull(registry.getUniqueId(20));
		assertThrows(NullPointerException.class, () -> registry.getUniqueId(null));
	}
	
	@Test
	void contains() {
		Registry<Integer> registry = Registry.of();
		assertTrue(registry.contains(registry.register(10)));
		assertFalse(registry.contains(null));
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
		assertThrows(UnsupportedOperationException.class, () -> registry.register(10));
	}
	
	@Test
	void unfreeze() {
		Registry<Integer> registry = Registry.freezable();
		registry.freeze();
		assertThrows(UnsupportedOperationException.class, () -> registry.register(10));
		registry.unfreeze();
		assertDoesNotThrow(() -> registry.register(10));
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