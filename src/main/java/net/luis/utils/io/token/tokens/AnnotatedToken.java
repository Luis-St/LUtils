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

package net.luis.utils.io.token.tokens;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.type.TokenType;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Token implementation that wraps another token with additional metadata.<br>
 * This token preserves all properties of the wrapped token while adding metadata support.<br>
 * The metadata can be used to store additional information about the token during processing.<br>
 *
 * @author Luis-St
 *
 * @param token The wrapped token
 * @param metadata The metadata associated with the token
 */
public record AnnotatedToken(
	@NonNull Token token,
	@NonNull Map<String, Object> metadata
) implements Token {
	
	/**
	 * Constructs a new annotated token with the given token and metadata.<br>
	 *
	 * @param token The wrapped token
	 * @param metadata The metadata associated with the token
	 * @throws NullPointerException If the token or metadata map is null
	 */
	public AnnotatedToken {
		Objects.requireNonNull(token, "Token must not be null");
		Objects.requireNonNull(metadata, "Metadata map must not be null");
		metadata = Map.copyOf(metadata);
	}
	
	/**
	 * Creates an annotated token with a single metadata entry.<br>
	 *
	 * @param token The wrapped token
	 * @param key The metadata key
	 * @param value The metadata value
	 * @return A new annotated token
	 * @throws NullPointerException If the token or key is null
	 */
	public static @NonNull AnnotatedToken of(@NonNull Token token, @NonNull String key, Object value) {
		Objects.requireNonNull(key, "Metadata key must not be null");
		return new AnnotatedToken(token, Map.of(key, value));
	}
	
	/**
	 * Creates an annotated token with empty metadata.<br>
	 *
	 * @param token The wrapped token
	 * @return A new annotated token with empty metadata
	 * @throws NullPointerException If the token is null
	 */
	public static @NonNull AnnotatedToken empty(@NonNull Token token) {
		return new AnnotatedToken(token, Map.of());
	}
	
	@Override
	public @NonNull String value() {
		return this.token.value();
	}
	
	@Override
	public @NonNull TokenPosition position() {
		return this.token.position();
	}
	
	@Override
	public @NonNull Set<TokenType> types() {
		return this.token.types();
	}
	
	/**
	 * Gets a metadata value by key.<br>
	 *
	 * @param key The metadata key
	 * @return The metadata value, or null if not present
	 * @throws NullPointerException If the key is null
	 */
	public @UnknownNullability Object getMetadata(@NonNull String key) {
		Objects.requireNonNull(key, "Metadata key must not be null");
		return this.metadata.get(key);
	}
	
	/**
	 * Gets a metadata value by key with a default value.<br>
	 *
	 * @param key The metadata key
	 * @param defaultValue The default value if key is not present
	 * @param <T> The type of the metadata value
	 * @return The metadata value, or the default value if not present
	 * @throws NullPointerException If the key is null
	 */
	@SuppressWarnings("unchecked")
	public <T> @UnknownNullability T getMetadata(@NonNull String key, T defaultValue) {
		Objects.requireNonNull(key, "Metadata key must not be null");
		return (T) this.metadata.getOrDefault(key, defaultValue);
	}
	
	/**
	 * Checks if the metadata contains the given key.<br>
	 *
	 * @param key The metadata key to check
	 * @return True if the metadata contains the key, false otherwise
	 * @throws NullPointerException If the key is null
	 */
	public boolean hasMetadata(@NonNull String key) {
		Objects.requireNonNull(key, "Metadata key must not be null");
		return this.metadata.containsKey(key);
	}
	
	@Override
	public @NonNull Token annotate(@Nullable Map<String, Object> annotations) {
		return this; // Already an annotated token
	}
}
