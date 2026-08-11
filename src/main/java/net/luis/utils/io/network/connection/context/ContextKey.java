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

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A typed key for values stored in a {@link ConnectionContext}.<br>
 * The key bundles the name a value is stored under with the type it is expected to have,<br>
 * so that values can be read back without a cast and without passing the type at every call site.<br>
 * <p>
 *     Keys are meant to be declared once as constants and reused, so that all accesses to a value agree on its type:
 * </p>
 * <pre>{@code
 * public static final ContextKey<Session> SESSION = ContextKey.of("session", Session.class);
 *
 * connection.context().set(SESSION, new Session(user));
 * Optional<Session> session = connection.context().get(SESSION);
 * }</pre>
 * <p>
 *     Two keys with the same name but different types refer to the same stored value,<br>
 *     which makes reading through the second key fail with a {@link ClassCastException}.
 * </p>
 *
 * @see ConnectionContext
 *
 * @author Luis-St
 *
 * @param name The name the value is stored under
 * @param type The type of the value
 * @param <T> The type of the value
 */
public record ContextKey<T>(@NonNull String name, @NonNull Class<T> type) {
	
	/**
	 * Constructs a new context key with the specified name and type.<br>
	 *
	 * @param name The name the value is stored under (must not be blank)
	 * @param type The type of the value
	 * @throws NullPointerException If name or type is null
	 * @throws IllegalArgumentException If name is blank
	 */
	public ContextKey {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(type, "Type must not be null");
		
		if (name.isBlank()) {
			throw new IllegalArgumentException("Name must not be blank");
		}
	}
	
	/**
	 * Creates a new context key with the specified name and type.<br>
	 * This method is equivalent to the canonical constructor and exists for readability at declaration sites.<br>
	 *
	 * @param name The name the value is stored under (must not be blank)
	 * @param type The type of the value
	 * @return The created context key
	 * @throws NullPointerException If name or type is null
	 * @throws IllegalArgumentException If name is blank
	 * @param <T> The type of the value
	 */
	public static <T> @NonNull ContextKey<T> of(@NonNull String name, @NonNull Class<T> type) {
		return new ContextKey<>(name, type);
	}
	
	/**
	 * Returns a string representation of this key.<br>
	 * The format is {@code name: type} (e.g., "session: com.example.Session").<br>
	 *
	 * @return The string representation of this key
	 */
	@Override
	public @NonNull String toString() {
		return this.name + ": " + this.type.getName();
	}
}
