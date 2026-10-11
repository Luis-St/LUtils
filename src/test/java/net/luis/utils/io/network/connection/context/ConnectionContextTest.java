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

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConnectionContext}.<br>
 *
 * @author Luis-St
 */
class ConnectionContextTest {
	
	private static final ContextKey<String> NAME = ContextKey.of("name", String.class);
	private static final ContextKey<TestSession> SESSION = ContextKey.of("session", TestSession.class);
	
	@Test
	void constructEmptyContext() {
		ConnectionContext context = new ConnectionContext();
		
		assertTrue(context.isEmpty());
		assertEquals(0, context.size());
		assertTrue(context.keys().isEmpty());
		assertTrue(context.asMap().isEmpty());
	}
	
	@Test
	void setWithNullKey() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(NullPointerException.class, () -> context.set((String) null, "v"));
		assertThrows(NullPointerException.class, () -> context.set((ContextKey<String>) null, "v"));
	}
	
	@Test
	void setWithNullValue() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(NullPointerException.class, () -> context.set("k", null));
		assertThrows(NullPointerException.class, () -> context.set(NAME, null));
	}
	
	@Test
	void getWithNullKey() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(NullPointerException.class, () -> context.get(null, String.class));
		assertThrows(NullPointerException.class, () -> context.get((ContextKey<String>) null));
	}
	
	@Test
	void getWithNullType() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(NullPointerException.class, () -> context.get("k", null));
	}
	
	@Test
	void getOrDefaultWithNullArguments() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(NullPointerException.class, () -> context.getOrDefault(null, String.class, "d"));
		assertThrows(NullPointerException.class, () -> context.getOrDefault("k", null, "d"));
		assertThrows(NullPointerException.class, () -> context.getOrDefault("k", String.class, null));
		assertThrows(NullPointerException.class, () -> context.getOrDefault(NAME, null));
		assertThrows(NullPointerException.class, () -> context.getOrDefault(null, "d"));
	}
	
	@Test
	void computeIfAbsentWithNullArguments() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(NullPointerException.class, () -> context.computeIfAbsent(null, String.class, () -> "v"));
		assertThrows(NullPointerException.class, () -> context.computeIfAbsent("k", null, () -> "v"));
		assertThrows(NullPointerException.class, () -> context.computeIfAbsent("k", String.class, null));
		assertThrows(NullPointerException.class, () -> context.computeIfAbsent(null, () -> "v"));
		assertThrows(NullPointerException.class, () -> context.computeIfAbsent(NAME, null));
	}
	
	@Test
	void computeIfAbsentWithFactoryReturningNull() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(NullPointerException.class, () -> context.computeIfAbsent("k", String.class, () -> null));
		assertFalse(context.contains("k"));
		assertTrue(context.isEmpty());
	}
	
	@Test
	void computeIfAbsentWithThrowingFactory() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(IllegalStateException.class, () -> context.computeIfAbsent("k", String.class, () -> {
			throw new IllegalStateException("boom");
		}));
		assertFalse(context.contains("k"));
		assertEquals(0, context.size());
		assertEquals("recovered", context.computeIfAbsent("k", String.class, () -> "recovered"));
	}
	
	@Test
	void removeWithNullKey() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(NullPointerException.class, () -> context.remove((String) null));
		assertThrows(NullPointerException.class, () -> context.remove((ContextKey<String>) null));
	}
	
	@Test
	void containsWithNullKey() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(NullPointerException.class, () -> context.contains((String) null));
		assertThrows(NullPointerException.class, () -> context.contains((ContextKey<?>) null));
	}
	
	@Test
	void shortcutGettersWithNullKey() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(NullPointerException.class, () -> context.getString(null));
		assertThrows(NullPointerException.class, () -> context.getInt(null));
		assertThrows(NullPointerException.class, () -> context.getLong(null));
		assertThrows(NullPointerException.class, () -> context.getDouble(null));
		assertThrows(NullPointerException.class, () -> context.getBoolean(null));
	}
	
	@Test
	void getStringWithNullFallback() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(NullPointerException.class, () -> context.getString("k", null));
	}
	
	@Test
	void shortcutGettersWithFallbackAndNullKey() {
		ConnectionContext context = new ConnectionContext();
		
		assertThrows(NullPointerException.class, () -> context.getString(null, "d"));
		assertThrows(NullPointerException.class, () -> context.getInt(null, 0));
		assertThrows(NullPointerException.class, () -> context.getLong(null, 0L));
		assertThrows(NullPointerException.class, () -> context.getDouble(null, 0.0));
		assertThrows(NullPointerException.class, () -> context.getBoolean(null, false));
	}
	
	@Test
	void getAbsentKeyReturnsEmpty() {
		ConnectionContext context = new ConnectionContext();
		
		assertTrue(context.get("missing", String.class).isEmpty());
		assertTrue(context.get(NAME).isEmpty());
	}
	
	@Test
	void getPresentValueReturnsValue() {
		ConnectionContext context = new ConnectionContext();
		context.set(NAME, "Luis");
		
		assertEquals(Optional.of("Luis"), context.get(NAME));
		assertEquals(Optional.of("Luis"), context.get("name", String.class));
	}
	
	@Test
	void getWithMismatchedTypeThrows() {
		ConnectionContext context = new ConnectionContext();
		context.set("name", 42);
		
		ClassCastException exception = assertThrows(ClassCastException.class, () -> context.get("name", String.class));
		assertTrue(exception.getMessage().contains("name"));
		assertTrue(exception.getMessage().contains("java.lang.Integer"));
		assertTrue(exception.getMessage().contains("java.lang.String"));
		assertFalse(exception.getMessage().contains("42"));
	}
	
	@Test
	void getWithMismatchedContextKeyThrows() {
		ConnectionContext context = new ConnectionContext();
		context.set(ContextKey.of("id", Integer.class), 1);
		
		assertThrows(ClassCastException.class, () -> context.get(ContextKey.of("id", String.class)));
	}
	
	@Test
	void getSubtypeValueThroughSupertypeKey() {
		ConnectionContext context = new ConnectionContext();
		ArrayList<String> stored = new ArrayList<>(List.of("a", "b"));
		context.set("items", stored);
		
		assertSame(stored, context.get("items", List.class).orElseThrow());
		assertSame(stored, context.get("items", Object.class).orElseThrow());
	}
	
	@Test
	void getOrDefaultReturnsFallbackWhenAbsent() {
		ConnectionContext context = new ConnectionContext();
		
		assertEquals("default", context.getOrDefault("missing", String.class, "default"));
		assertEquals("default", context.getOrDefault(NAME, "default"));
	}
	
	@Test
	void getOrDefaultReturnsStoredValue() {
		ConnectionContext context = new ConnectionContext();
		context.set(NAME, "Luis");
		
		assertEquals("Luis", context.getOrDefault("name", String.class, "default"));
		assertEquals("Luis", context.getOrDefault(NAME, "default"));
	}
	
	@Test
	void getOrDefaultWithMismatchedTypeThrows() {
		ConnectionContext context = new ConnectionContext();
		context.set("name", 42);
		
		assertThrows(ClassCastException.class, () -> context.getOrDefault("name", String.class, "default"));
		assertThrows(ClassCastException.class, () -> context.getOrDefault(NAME, "default"));
	}
	
	@Test
	void computeIfAbsentStoresCreatedValue() {
		ConnectionContext context = new ConnectionContext();
		TestSession created = context.computeIfAbsent(SESSION, () -> new TestSession("Luis"));
		
		assertEquals(new TestSession("Luis"), created);
		assertTrue(context.contains(SESSION));
		assertEquals(1, context.size());
		assertSame(created, context.get(SESSION).orElseThrow());
	}
	
	@Test
	void computeIfAbsentDoesNotInvokeFactoryWhenPresent() {
		ConnectionContext context = new ConnectionContext();
		TestSession existing = new TestSession("Luis");
		context.set(SESSION, existing);
		
		AtomicInteger callCount = new AtomicInteger(0);
		TestSession resolved = context.computeIfAbsent(SESSION, () -> {
			callCount.incrementAndGet();
			return new TestSession("other");
		});
		
		assertEquals(0, callCount.get());
		assertSame(existing, resolved);
	}
	
	@Test
	void computeIfAbsentWithMismatchedTypeThrows() {
		ConnectionContext context = new ConnectionContext();
		context.set("session", "not-a-session");
		
		AtomicInteger callCount = new AtomicInteger(0);
		assertThrows(ClassCastException.class, () -> context.computeIfAbsent(SESSION, () -> {
			callCount.incrementAndGet();
			return new TestSession("Luis");
		}));
		
		assertEquals(0, callCount.get());
		assertEquals("not-a-session", context.getString("session").orElseThrow());
	}
	
	@Test
	void removePresentKeyReturnsValue() {
		ConnectionContext context = new ConnectionContext();
		context.set(NAME, "Luis");
		context.set("other", 1);
		
		assertEquals(Optional.of("Luis"), context.remove("name"));
		assertFalse(context.contains("name"));
		assertEquals(1, context.size());
		
		context.set(NAME, "Luis");
		assertEquals(Optional.of("Luis"), context.remove(NAME));
		assertFalse(context.contains(NAME));
	}
	
	@Test
	void removeAbsentKeyReturnsEmpty() {
		ConnectionContext context = new ConnectionContext();
		
		assertTrue(context.remove("missing").isEmpty());
		assertTrue(context.remove(NAME).isEmpty());
		assertTrue(context.isEmpty());
	}
	
	@Test
	void removeWithMismatchedTypeThrows() {
		ConnectionContext context = new ConnectionContext();
		context.set("name", 42);
		
		assertThrows(ClassCastException.class, () -> context.remove(NAME));
		assertFalse(context.contains("name"));
		assertTrue(context.isEmpty());
	}
	
	@Test
	void containsPresentAndAbsentKey() {
		ConnectionContext context = new ConnectionContext();
		context.set(NAME, "Luis");
		
		assertTrue(context.contains("name"));
		assertTrue(context.contains(NAME));
		assertFalse(context.contains("missing"));
		assertFalse(context.contains(SESSION));
	}
	
	@Test
	void containsContextKeyIgnoresType() {
		ConnectionContext context = new ConnectionContext();
		context.set("name", 42);
		
		assertTrue(assertDoesNotThrow(() -> context.contains(NAME)));
	}
	
	@Test
	void getStringVariants() {
		ConnectionContext context = new ConnectionContext();
		
		assertTrue(context.getString("k").isEmpty());
		assertEquals("fallback", context.getString("k", "fallback"));
		
		context.set("k", "Luis");
		assertEquals(Optional.of("Luis"), context.getString("k"));
		assertEquals("Luis", context.getString("k", "fallback"));
	}
	
	@Test
	void getIntVariants() {
		ConnectionContext context = new ConnectionContext();
		
		assertEquals(OptionalInt.empty(), context.getInt("k"));
		assertEquals(-1, context.getInt("k", -1));
		
		context.set("k", 7);
		assertEquals(OptionalInt.of(7), context.getInt("k"));
		assertEquals(7, context.getInt("k", -1));
	}
	
	@Test
	void getLongVariants() {
		ConnectionContext context = new ConnectionContext();
		
		assertEquals(OptionalLong.empty(), context.getLong("k"));
		assertEquals(-1L, context.getLong("k", -1L));
		
		context.set("k", 7L);
		assertEquals(OptionalLong.of(7L), context.getLong("k"));
		assertEquals(7L, context.getLong("k", -1L));
	}
	
	@Test
	void getDoubleVariants() {
		ConnectionContext context = new ConnectionContext();
		
		assertEquals(OptionalDouble.empty(), context.getDouble("k"));
		assertEquals(-1.0, context.getDouble("k", -1.0));
		
		context.set("k", 7.5);
		assertEquals(OptionalDouble.of(7.5), context.getDouble("k"));
		assertEquals(7.5, context.getDouble("k", -1.0));
	}
	
	@Test
	void getBooleanVariants() {
		ConnectionContext context = new ConnectionContext();
		
		assertTrue(context.getBoolean("k").isEmpty());
		assertFalse(context.getBoolean("k", false));
		
		context.set("k", true);
		assertEquals(Optional.of(true), context.getBoolean("k"));
		assertTrue(context.getBoolean("k", false));
	}
	
	@Test
	void shortcutFallbacksDistinguishStoredFalsyValueFromAbsence() {
		ConnectionContext context = new ConnectionContext();
		context.set("flag", false);
		context.set("i", 0);
		context.set("l", 0L);
		context.set("d", 0.0);
		
		assertFalse(context.getBoolean("flag", true));
		assertEquals(0, context.getInt("i", -1));
		assertEquals(0L, context.getLong("l", -1L));
		assertEquals(0.0, context.getDouble("d", -1.0));
	}
	
	@Test
	void shortcutGettersWithMismatchedTypeThrow() {
		ConnectionContext context = new ConnectionContext();
		context.set("k", new Object());
		
		assertThrows(ClassCastException.class, () -> context.getString("k"));
		assertThrows(ClassCastException.class, () -> context.getInt("k"));
		assertThrows(ClassCastException.class, () -> context.getLong("k"));
		assertThrows(ClassCastException.class, () -> context.getDouble("k"));
		assertThrows(ClassCastException.class, () -> context.getBoolean("k"));
		assertThrows(ClassCastException.class, () -> context.getString("k", "fallback"));
		assertThrows(ClassCastException.class, () -> context.getInt("k", -1));
		assertThrows(ClassCastException.class, () -> context.getBoolean("k", false));
	}
	
	@Test
	void keysAndAsMapWhenEmpty() {
		ConnectionContext context = new ConnectionContext();
		
		assertTrue(context.keys().isEmpty());
		assertTrue(context.asMap().isEmpty());
		assertEquals(0, context.size());
		assertTrue(context.isEmpty());
	}
	
	@Test
	void keysAndAsMapWhenPopulated() {
		ConnectionContext context = new ConnectionContext();
		TestSession session = new TestSession("Luis");
		context.set(NAME, "Luis");
		context.set("retries", 3);
		context.set(SESSION, session);
		
		assertEquals(Set.of("name", "retries", "session"), context.keys());
		assertEquals("Luis", context.asMap().get("name"));
		assertEquals(3, context.asMap().get("retries"));
		assertSame(session, context.asMap().get("session"));
		assertEquals(3, context.size());
	}
	
	@Test
	void keysAndAsMapAreUnmodifiable() {
		ConnectionContext context = new ConnectionContext();
		context.set(NAME, "Luis");
		
		Set<String> keys = context.keys();
		Map<String, Object> values = context.asMap();
		
		assertThrows(UnsupportedOperationException.class, () -> keys.add("other"));
		assertThrows(UnsupportedOperationException.class, () -> keys.remove("name"));
		assertThrows(UnsupportedOperationException.class, () -> values.put("other", "v"));
		assertThrows(UnsupportedOperationException.class, values::clear);
	}
	
	@Test
	void keysAndAsMapAreLiveViews() {
		ConnectionContext context = new ConnectionContext();
		Set<String> keys = context.keys();
		Map<String, Object> values = context.asMap();
		
		context.set(NAME, "Luis");
		assertTrue(keys.contains("name"));
		assertEquals("Luis", values.get("name"));
		
		context.remove(NAME);
		assertFalse(keys.contains("name"));
		assertTrue(values.isEmpty());
	}
	
	@Test
	void isEmptyReflectsContent() {
		ConnectionContext context = new ConnectionContext();
		assertTrue(context.isEmpty());
		
		context.set(NAME, "Luis");
		assertFalse(context.isEmpty());
		
		context.remove(NAME);
		assertTrue(context.isEmpty());
	}
	
	@Test
	void clearRemovesAllValues() {
		ConnectionContext context = new ConnectionContext();
		context.set(NAME, "Luis");
		context.set("retries", 3);
		
		context.clear();
		
		assertTrue(context.isEmpty());
		assertEquals(0, context.size());
		assertFalse(context.contains("name"));
		assertFalse(context.contains("retries"));
	}
	
	@Test
	void clearOnEmptyContext() {
		ConnectionContext context = new ConnectionContext();
		
		assertDoesNotThrow(context::clear);
		assertTrue(context.isEmpty());
	}
	
	@Test
	void setOverwritesExistingValue() {
		ConnectionContext context = new ConnectionContext();
		context.set(NAME, "first");
		context.set(NAME, "second");
		
		assertEquals(Optional.of("second"), context.get(NAME));
		assertEquals(1, context.size());
	}
	
	@Test
	void setOverwritesValueOfDifferentType() {
		ConnectionContext context = new ConnectionContext();
		context.set("k", "text");
		context.set("k", 1);
		
		assertEquals(Optional.of(1), context.get("k", Integer.class));
		assertThrows(ClassCastException.class, () -> context.get("k", String.class));
		assertEquals(1, context.size());
	}
	
	@Test
	void toStringOnEmptyContext() {
		assertEquals("ConnectionContext{keys=[]}", new ConnectionContext().toString());
	}
	
	@Test
	void toStringListsKeysSortedWithoutValues() {
		ConnectionContext context = new ConnectionContext();
		context.set("zeta", "top-secret");
		context.set("alpha", "top-secret");
		context.set("mid", "top-secret");
		
		assertEquals("ConnectionContext{keys=[alpha, mid, zeta]}", context.toString());
		assertFalse(context.toString().contains("top-secret"));
	}
	
	@Test
	void storeAndReadNullableFriendlyTypes() {
		ConnectionContext context = new ConnectionContext();
		context.set("s", "text");
		context.set("i", 1);
		context.set("l", 2L);
		context.set("d", 3.5);
		context.set("b", true);
		
		assertEquals(Optional.of("text"), context.get("s", String.class));
		assertEquals(OptionalInt.of(1), context.getInt("i"));
		assertEquals(OptionalLong.of(2L), context.getLong("l"));
		assertEquals(OptionalDouble.of(3.5), context.getDouble("d"));
		assertEquals(Optional.of(true), context.get("b", Boolean.class));
	}
	
	@Test
	void stringAndContextKeyApisInterop() {
		ConnectionContext context = new ConnectionContext();
		TestSession session = new TestSession("Luis");
		context.set("name", "Luis");
		context.set(SESSION, session);
		
		assertEquals(Optional.of("Luis"), context.get(NAME));
		assertSame(session, context.get("session", TestSession.class).orElseThrow());
	}
	
	@Test
	void noNumericCoercionBetweenTypes() {
		ConnectionContext context = new ConnectionContext();
		
		context.set("n", 5L);
		assertThrows(ClassCastException.class, () -> context.getInt("n"));
		
		context.set("n", 5);
		assertThrows(ClassCastException.class, () -> context.getLong("n"));
		assertThrows(ClassCastException.class, () -> context.getDouble("n"));
	}
	
	@Test
	void multipleKeysOfDifferentTypesCoexist() {
		ConnectionContext context = new ConnectionContext();
		TestSession session = new TestSession("Luis");
		context.set(NAME, "Luis");
		context.set("retries", 3);
		context.set(SESSION, session);
		context.set("items", List.of("a", "b"));
		
		assertEquals(4, context.size());
		assertEquals(Optional.of("Luis"), context.get(NAME));
		assertEquals(OptionalInt.of(3), context.getInt("retries"));
		
		context.remove("retries");
		assertEquals(3, context.size());
		assertSame(session, context.get(SESSION).orElseThrow());
	}
	
	@Test
	void computeIfAbsentInvokesFactoryOnceUnderConcurrency() throws Exception {
		ConnectionContext context = new ConnectionContext();
		AtomicInteger callCount = new AtomicInteger(0);
		int threads = 16;
		CountDownLatch startGate = new CountDownLatch(1);
		
		try (ExecutorService executor = Executors.newFixedThreadPool(threads)) {
			List<Future<TestSession>> futures = new ArrayList<>();
			for (int i = 0; i < threads; i++) {
				futures.add(executor.submit(() -> {
					startGate.await();
					return context.computeIfAbsent(SESSION, () -> {
						callCount.incrementAndGet();
						return new TestSession("Luis");
					});
				}));
			}
			startGate.countDown();
			
			TestSession first = futures.getFirst().get(10, TimeUnit.SECONDS);
			for (Future<TestSession> future : futures) {
				assertSame(first, future.get(10, TimeUnit.SECONDS));
			}
		}
		assertEquals(1, callCount.get());
	}
	
	@Test
	void concurrentSetAndRemoveKeepContextConsistent() throws Exception {
		ConnectionContext context = new ConnectionContext();
		int threads = 8;
		CountDownLatch startGate = new CountDownLatch(1);
		
		try (ExecutorService executor = Executors.newFixedThreadPool(threads)) {
			List<Future<?>> futures = new ArrayList<>();
			for (int i = 0; i < threads; i++) {
				int index = i;
				futures.add(executor.submit(() -> {
					startGate.await();
					context.set("kept-" + index, index);
					context.set("dropped-" + index, index);
					context.get("kept-" + index, Integer.class);
					context.remove("dropped-" + index);
					return null;
				}));
			}
			startGate.countDown();
			
			for (Future<?> future : futures) {
				assertDoesNotThrow(() -> future.get(10, TimeUnit.SECONDS));
			}
		}
		assertEquals(threads, context.size());
	}
	
	@Test
	void contextStateSurvivesAcrossHandlerLikeCalls() {
		ConnectionContext context = new ConnectionContext();
		
		for (int i = 0; i < 10; i++) {
			context.set("count", context.getInt("count", 0) + 1);
		}
		
		assertEquals(OptionalInt.of(10), context.getInt("count"));
		assertEquals(1, context.size());
	}
	
	private record TestSession(String user) {}
}
