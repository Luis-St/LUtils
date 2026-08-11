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

import net.luis.utils.io.network.connection.Connection;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * A mutable store for user data attached to a {@link Connection}.<br>
 * The context is a map of string keys to arbitrary values, which allows to keep per-connection state<br>
 * such as a session, an authenticated user or a partially received message across event handler invocations.<br>
 * <p>
 *     Values can be accessed either through a {@link ContextKey}, which carries the expected type,<br>
 *     or through a plain string key together with the expected type:
 * </p>
 * <pre>{@code
 * private static final ContextKey<Session> SESSION = ContextKey.of("session", Session.class);
 *
 * // In the message handler
 * Session session = connection.context().computeIfAbsent(SESSION, () -> new Session(connection.remoteEndpoint()));
 * session.messageReceived();
 *
 * // Shortcuts for the common types
 * connection.context().set("retries", 0);
 * int retries = connection.context().getInt("retries", 0);
 * }</pre>
 * <p>
 *     An empty {@link Optional} always means that no value is stored under the key, never that a stored value<br>
 *     had a different type. Reading a value through a key whose type does not match the stored value is a<br>
 *     programming error and fails with a {@link ClassCastException}, including on the overloads that take a<br>
 *     fallback value, which only applies when no value is stored at all.<br>
 *     No conversion is performed, so a value stored as a {@link Long} cannot be read with {@link #getInt(String)}.
 * </p>
 * <p>
 *     This class is thread-safe, which is required because a connection is typically touched by the thread that<br>
 *     accepted it, the thread that handles its messages and any thread that broadcasts to it.<br>
 *     Null values are not supported, the absence of a key is the only way to express a missing value.
 * </p>
 *
 * @see Connection
 * @see ContextKey
 *
 * @author Luis-St
 */
public final class ConnectionContext {
	
	/**
	 * The values stored in this context, keyed by their name.<br>
	 */
	private final ConcurrentMap<String, Object> values = new ConcurrentHashMap<>();
	
	/**
	 * Constructs a new empty connection context.<br>
	 */
	public ConnectionContext() {}
	
	/**
	 * Stores the given value under the given key.<br>
	 * An already stored value is overwritten, regardless of its type.<br>
	 *
	 * @param key The key to store the value under
	 * @param value The value to store
	 * @throws NullPointerException If key or value is null
	 */
	public void set(@NonNull String key, @NonNull Object value) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		
		this.values.put(key, value);
	}
	
	/**
	 * Stores the given value under the given typed key.<br>
	 * An already stored value is overwritten, regardless of its type.<br>
	 *
	 * @param key The typed key to store the value under
	 * @param value The value to store
	 * @throws NullPointerException If key or value is null
	 * @param <T> The type of the value
	 */
	public <T> void set(@NonNull ContextKey<T> key, @NonNull T value) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		
		this.values.put(key.name(), value);
	}
	
	/**
	 * Returns the value stored under the given key.<br>
	 *
	 * @param key The key of the value
	 * @param type The expected type of the value
	 * @return An optional containing the value, or an empty optional if no value is stored under the key
	 * @throws NullPointerException If key or type is null
	 * @throws ClassCastException If a value is stored under the key but is not of the expected type
	 * @param <T> The expected type of the value
	 */
	public <T> @NonNull Optional<T> get(@NonNull String key, @NonNull Class<T> type) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(type, "Type must not be null");
		
		return Optional.ofNullable(this.cast(key, type, this.values.get(key)));
	}
	
	/**
	 * Returns the value stored under the given typed key.<br>
	 *
	 * @param key The typed key of the value
	 * @return An optional containing the value, or an empty optional if no value is stored under the key
	 * @throws NullPointerException If key is null
	 * @throws ClassCastException If a value is stored under the key but is not of the expected type
	 * @param <T> The type of the value
	 */
	public <T> @NonNull Optional<T> get(@NonNull ContextKey<T> key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.get(key.name(), key.type());
	}
	
	/**
	 * Returns the value stored under the given key, or the given fallback if no value is stored.<br>
	 * The fallback is not used when a value of a different type is stored, this fails instead.<br>
	 *
	 * @param key The key of the value
	 * @param type The expected type of the value
	 * @param fallback The value to return if no value is stored under the key
	 * @return The stored value, or the fallback if no value is stored under the key
	 * @throws NullPointerException If key, type or fallback is null
	 * @throws ClassCastException If a value is stored under the key but is not of the expected type
	 * @param <T> The expected type of the value
	 */
	public <T> @NonNull T getOrDefault(@NonNull String key, @NonNull Class<T> type, @NonNull T fallback) {
		Objects.requireNonNull(fallback, "Fallback must not be null");
		return this.get(key, type).orElse(fallback);
	}
	
	/**
	 * Returns the value stored under the given typed key, or the given fallback if no value is stored.<br>
	 * The fallback is not used when a value of a different type is stored, this fails instead.<br>
	 *
	 * @param key The typed key of the value
	 * @param fallback The value to return if no value is stored under the key
	 * @return The stored value, or the fallback if no value is stored under the key
	 * @throws NullPointerException If key or fallback is null
	 * @throws ClassCastException If a value is stored under the key but is not of the expected type
	 * @param <T> The type of the value
	 */
	public <T> @NonNull T getOrDefault(@NonNull ContextKey<T> key, @NonNull T fallback) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.getOrDefault(key.name(), key.type(), fallback);
	}
	
	/**
	 * Returns the value stored under the given key, storing and returning the value created by the given factory<br>
	 * if no value is stored yet.<br>
	 * <p>
	 *     The factory is only invoked when no value is stored under the key, and it is invoked at most once even<br>
	 *     if multiple threads call this method at the same time.<br>
	 *     A value of a different type is not replaced, this fails instead.
	 * </p>
	 *
	 * @param key The key of the value
	 * @param type The expected type of the value
	 * @param factory The factory creating the value if none is stored yet
	 * @return The stored value, or the newly created value
	 * @throws NullPointerException If key, type or factory is null, or if the factory returned null
	 * @throws ClassCastException If a value is stored under the key but is not of the expected type
	 * @param <T> The expected type of the value
	 */
	public <T> @NonNull T computeIfAbsent(@NonNull String key, @NonNull Class<T> type, @NonNull Supplier<? extends T> factory) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(factory, "Factory must not be null");
		
		Object value = this.values.computeIfAbsent(key, _ -> factory.get());
		if (value == null) {
			throw new NullPointerException("Factory for context key '" + key + "' must not return null");
		}
		return this.cast(key, type, value);
	}
	
	/**
	 * Returns the value stored under the given typed key, storing and returning the value created by the given factory<br>
	 * if no value is stored yet.<br>
	 * <p>
	 *     The factory is only invoked when no value is stored under the key, and it is invoked at most once even<br>
	 *     if multiple threads call this method at the same time.<br>
	 *     A value of a different type is not replaced, this fails instead.
	 * </p>
	 *
	 * @param key The typed key of the value
	 * @param factory The factory creating the value if none is stored yet
	 * @return The stored value, or the newly created value
	 * @throws NullPointerException If key or factory is null, or if the factory returned null
	 * @throws ClassCastException If a value is stored under the key but is not of the expected type
	 * @param <T> The type of the value
	 */
	public <T> @NonNull T computeIfAbsent(@NonNull ContextKey<T> key, @NonNull Supplier<? extends T> factory) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.computeIfAbsent(key.name(), key.type(), factory);
	}
	
	/**
	 * Removes the value stored under the given key.<br>
	 *
	 * @param key The key of the value to remove
	 * @return An optional containing the removed value, or an empty optional if no value was stored under the key
	 * @throws NullPointerException If key is null
	 */
	public @NonNull Optional<Object> remove(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Optional.ofNullable(this.values.remove(key));
	}
	
	/**
	 * Removes the value stored under the given typed key.<br>
	 * The value is removed even if it is not of the expected type, only the returned value fails in that case.<br>
	 *
	 * @param key The typed key of the value to remove
	 * @return An optional containing the removed value, or an empty optional if no value was stored under the key
	 * @throws NullPointerException If key is null
	 * @throws ClassCastException If a value was stored under the key but is not of the expected type
	 * @param <T> The type of the value
	 */
	public <T> @NonNull Optional<T> remove(@NonNull ContextKey<T> key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Optional.ofNullable(this.cast(key.name(), key.type(), this.values.remove(key.name())));
	}
	
	/**
	 * Returns whether a value is stored under the given key.<br>
	 *
	 * @param key The key to check
	 * @return True if a value is stored under the key
	 * @throws NullPointerException If key is null
	 */
	public boolean contains(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.values.containsKey(key);
	}
	
	/**
	 * Returns whether a value is stored under the name of the given typed key.<br>
	 * The type of the key is not taken into account, so this method returns true even if the stored value<br>
	 * is not of the expected type.<br>
	 *
	 * @param key The typed key to check
	 * @return True if a value is stored under the name of the key
	 * @throws NullPointerException If key is null
	 */
	public boolean contains(@NonNull ContextKey<?> key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.values.containsKey(key.name());
	}
	
	/**
	 * Returns the keys of all values stored in this context.<br>
	 * The returned set is an unmodifiable live view, so it reflects later modifications of this context.<br>
	 *
	 * @return The keys of all stored values
	 */
	public @NonNull Set<String> keys() {
		return Collections.unmodifiableSet(this.values.keySet());
	}
	
	/**
	 * Returns all values stored in this context, keyed by their name.<br>
	 * The returned map is an unmodifiable live view, so it reflects later modifications of this context.<br>
	 *
	 * @return All stored values
	 */
	public @NonNull Map<String, Object> asMap() {
		return Collections.unmodifiableMap(this.values);
	}
	
	/**
	 * Returns the number of values stored in this context.<br>
	 * @return The number of stored values
	 */
	public int size() {
		return this.values.size();
	}
	
	/**
	 * Returns whether this context stores no values.<br>
	 * @return True if no values are stored
	 */
	public boolean isEmpty() {
		return this.values.isEmpty();
	}
	
	/**
	 * Removes all values stored in this context.<br>
	 * Values holding resources are not closed, releasing them is the responsibility of the caller.<br>
	 */
	public void clear() {
		this.values.clear();
	}
	
	/**
	 * Returns the string value stored under the given key.<br>
	 *
	 * @param key The key of the value
	 * @return An optional containing the value, or an empty optional if no value is stored under the key
	 * @throws NullPointerException If key is null
	 * @throws ClassCastException If a value is stored under the key but is not a string
	 */
	public @NonNull Optional<String> getString(@NonNull String key) {
		return this.get(key, String.class);
	}
	
	/**
	 * Returns the string value stored under the given key, or the given fallback if no value is stored.<br>
	 * The fallback is not used when a value of a different type is stored, this fails instead.<br>
	 *
	 * @param key The key of the value
	 * @param fallback The value to return if no value is stored under the key
	 * @return The stored value, or the fallback if no value is stored under the key
	 * @throws NullPointerException If key or fallback is null
	 * @throws ClassCastException If a value is stored under the key but is not a string
	 */
	public @NonNull String getString(@NonNull String key, @NonNull String fallback) {
		return this.getOrDefault(key, String.class, fallback);
	}
	
	/**
	 * Returns the integer value stored under the given key.<br>
	 *
	 * @param key The key of the value
	 * @return An optional containing the value, or an empty optional if no value is stored under the key
	 * @throws NullPointerException If key is null
	 * @throws ClassCastException If a value is stored under the key but is not an integer
	 */
	public @NonNull OptionalInt getInt(@NonNull String key) {
		return this.get(key, Integer.class).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}
	
	/**
	 * Returns the integer value stored under the given key, or the given fallback if no value is stored.<br>
	 * The fallback is not used when a value of a different type is stored, this fails instead.<br>
	 *
	 * @param key The key of the value
	 * @param fallback The value to return if no value is stored under the key
	 * @return The stored value, or the fallback if no value is stored under the key
	 * @throws NullPointerException If key is null
	 * @throws ClassCastException If a value is stored under the key but is not an integer
	 */
	public int getInt(@NonNull String key, int fallback) {
		return this.getOrDefault(key, Integer.class, fallback);
	}
	
	/**
	 * Returns the long value stored under the given key.<br>
	 *
	 * @param key The key of the value
	 * @return An optional containing the value, or an empty optional if no value is stored under the key
	 * @throws NullPointerException If key is null
	 * @throws ClassCastException If a value is stored under the key but is not a long
	 */
	public @NonNull OptionalLong getLong(@NonNull String key) {
		return this.get(key, Long.class).map(OptionalLong::of).orElseGet(OptionalLong::empty);
	}
	
	/**
	 * Returns the long value stored under the given key, or the given fallback if no value is stored.<br>
	 * The fallback is not used when a value of a different type is stored, this fails instead.<br>
	 *
	 * @param key The key of the value
	 * @param fallback The value to return if no value is stored under the key
	 * @return The stored value, or the fallback if no value is stored under the key
	 * @throws NullPointerException If key is null
	 * @throws ClassCastException If a value is stored under the key but is not a long
	 */
	public long getLong(@NonNull String key, long fallback) {
		return this.getOrDefault(key, Long.class, fallback);
	}
	
	/**
	 * Returns the double value stored under the given key.<br>
	 *
	 * @param key The key of the value
	 * @return An optional containing the value, or an empty optional if no value is stored under the key
	 * @throws NullPointerException If key is null
	 * @throws ClassCastException If a value is stored under the key but is not a double
	 */
	public @NonNull OptionalDouble getDouble(@NonNull String key) {
		return this.get(key, Double.class).map(OptionalDouble::of).orElseGet(OptionalDouble::empty);
	}
	
	/**
	 * Returns the double value stored under the given key, or the given fallback if no value is stored.<br>
	 * The fallback is not used when a value of a different type is stored, this fails instead.<br>
	 *
	 * @param key The key of the value
	 * @param fallback The value to return if no value is stored under the key
	 * @return The stored value, or the fallback if no value is stored under the key
	 * @throws NullPointerException If key is null
	 * @throws ClassCastException If a value is stored under the key but is not a double
	 */
	public double getDouble(@NonNull String key, double fallback) {
		return this.getOrDefault(key, Double.class, fallback);
	}
	
	/**
	 * Returns the boolean value stored under the given key.<br>
	 *
	 * @param key The key of the value
	 * @return An optional containing the value, or an empty optional if no value is stored under the key
	 * @throws NullPointerException If key is null
	 * @throws ClassCastException If a value is stored under the key but is not a boolean
	 */
	public @NonNull Optional<Boolean> getBoolean(@NonNull String key) {
		return this.get(key, Boolean.class);
	}
	
	/**
	 * Returns the boolean value stored under the given key, or the given fallback if no value is stored.<br>
	 * The fallback is not used when a value of a different type is stored, this fails instead.<br>
	 *
	 * @param key The key of the value
	 * @param fallback The value to return if no value is stored under the key
	 * @return The stored value, or the fallback if no value is stored under the key
	 * @throws NullPointerException If key is null
	 * @throws ClassCastException If a value is stored under the key but is not a boolean
	 */
	public boolean getBoolean(@NonNull String key, boolean fallback) {
		return this.getOrDefault(key, Boolean.class, fallback);
	}
	
	//region Helper methods
	
	/**
	 * Casts the given value to the given type.<br>
	 * The value of the stored object is not included in the failure message, because a context commonly holds<br>
	 * sensitive data such as sessions or credentials.<br>
	 *
	 * @param key The key the value is stored under, used for the failure message
	 * @param type The expected type of the value
	 * @param value The value to cast, or null if no value is stored under the key
	 * @return The cast value, or null if the given value is null
	 * @throws ClassCastException If the value is not of the expected type
	 * @param <T> The expected type of the value
	 */
	private <T> @Nullable T cast(@NonNull String key, @NonNull Class<T> type, @Nullable Object value) {
		if (value == null) {
			return null;
		}
		if (!type.isInstance(value)) {
			throw new ClassCastException("Context value for key '" + key + "' is of type " + value.getClass().getName() + ", expected " + type.getName());
		}
		return type.cast(value);
	}
	//endregion
	
	//region Object overrides
	@Override
	public String toString() {
		return "ConnectionContext{keys=" + new TreeSet<>(this.values.keySet()) + "}";
	}
	//endregion
}
