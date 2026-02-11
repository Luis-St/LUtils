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

package net.luis.utils.io.database.key;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Interface representing a SQL foreign key relationship.<br>
 *
 * @author Luis-St
 *
 * @param <K> The type of the key
 * @param <V> The type of the referenced value
 */
public interface SqlForeignKey<K, V> {
	
	/**
	 * Creates a foreign key with the given key and no loaded value.<br>
	 *
	 * @param key The key value
	 * @param <K> The type of the key
	 * @param <V> The type of the referenced value
	 * @return The foreign key
	 */
	static <K, V> @NonNull SqlForeignKey<K, V> of(@NonNull K key) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a foreign key with the given key and pre-loaded value.<br>
	 *
	 * @param key The key value
	 * @param value The pre-loaded referenced value
	 * @param <K> The type of the key
	 * @param <V> The type of the referenced value
	 * @return The foreign key
	 */
	static <K, V> @NonNull SqlForeignKey<K, V> of(@NonNull K key, @Nullable V value) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a foreign key with the given key and a custom loader.<br>
	 *
	 * @param key The key value
	 * @param loader The loader used to fetch the referenced value
	 * @param <K> The type of the key
	 * @param <V> The type of the referenced value
	 * @return The foreign key
	 */
	static <K, V> @NonNull SqlForeignKey<K, V> withLoader(@NonNull K key, @NonNull SqlForeignKeyLoader<K, V> loader) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Loads all referenced values for the given foreign keys in a batch {@code SELECT} query.<br>
	 *
	 * @param keys The foreign keys to load
	 * @param <K> The type of the key
	 * @param <V> The type of the referenced value
	 */
	static <K, V> void loadAll(@NonNull Collection<SqlForeignKey<K, V>> keys) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Asynchronously loads all referenced values for the given foreign keys in a batch {@code SELECT} query.<br>
	 *
	 * @param keys The foreign keys to load
	 * @param <K> The type of the key
	 * @param <V> The type of the referenced value
	 * @return A future that completes when all values are loaded
	 */
	static <K, V> @NonNull CompletableFuture<Void> loadAllAsync(@NonNull Collection<SqlForeignKey<K, V>> keys) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the key value of this foreign key.<br>
	 * @return The key value
	 */
	@NonNull K key();
	
	/**
	 * Returns whether the referenced value has been loaded.<br>
	 * @return Whether the value is loaded
	 */
	boolean isLoaded();
	
	/**
	 * Returns the loaded value of this foreign key.<br>
	 * @return The referenced value
	 */
	@NonNull V value();
	
	/**
	 * Returns the loaded value, throwing an exception if not loaded.<br>
	 * @return The referenced value
	 */
	@NonNull V requireValue();
	
	/**
	 * Returns the loaded value as an optional.<br>
	 * @return An optional containing the referenced value, or empty if not loaded
	 */
	@NonNull Optional<V> valueOptional();
	
	/**
	 * Loads the referenced value using a {@code SELECT} query.<br>
	 * @return The loaded value
	 */
	@NonNull V load();
	
	/**
	 * Asynchronously loads the referenced value using a {@code SELECT} query.<br>
	 * @return A future containing the loaded value
	 */
	@NonNull CompletableFuture<V> loadAsync();
	
	/**
	 * Loads the referenced value using a {@code SELECT} query with the given options.<br>
	 *
	 * @param options The load options
	 * @return The loaded value
	 */
	@NonNull V load(@NonNull SqlLoadOptions options);
	
	/**
	 * Asynchronously loads the referenced value using a {@code SELECT} query with the given options.<br>
	 *
	 * @param options The load options
	 * @return A future containing the loaded value
	 */
	@NonNull CompletableFuture<V> loadAsync(@NonNull SqlLoadOptions options);
	
	/**
	 * Reloads the referenced value, discarding any cached value.<br>
	 * @return The reloaded value
	 */
	@NonNull V reload();
	
	/**
	 * Asynchronously reloads the referenced value, discarding any cached value.<br>
	 * @return A future containing the reloaded value
	 */
	@NonNull CompletableFuture<V> reloadAsync();
	
	/**
	 * Loads the referenced value only if it has not been loaded yet.<br>
	 * @return The loaded value
	 */
	@NonNull V loadIfAbsent();
	
	/**
	 * Returns a new foreign key with the given value set.<br>
	 *
	 * @param value The value to set
	 * @return A new foreign key with the given value
	 */
	@NonNull SqlForeignKey<K, V> withValue(@Nullable V value);
}
