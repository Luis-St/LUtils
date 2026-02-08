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

	static <K, V> @NonNull SqlForeignKey<K, V> of(@NonNull K key) {
		throw new UnsupportedOperationException();
	}

	static <K, V> @NonNull SqlForeignKey<K, V> of(@NonNull K key, @Nullable V value) {
		throw new UnsupportedOperationException();
	}

	static <K, V> @NonNull SqlForeignKey<K, V> withLoader(@NonNull K key, @NonNull SqlForeignKeyLoader<K, V> loader) {
		throw new UnsupportedOperationException();
	}

	static <K, V> void loadAll(@NonNull Collection<SqlForeignKey<K, V>> keys) {
		throw new UnsupportedOperationException();
	}

	static <K, V> @NonNull CompletableFuture<Void> loadAllAsync(@NonNull Collection<SqlForeignKey<K, V>> keys) {
		throw new UnsupportedOperationException();
	}

	@NonNull K key();

	boolean isLoaded();

	@NonNull V value();

	@NonNull V requireValue();

	@NonNull Optional<V> valueOptional();

	@NonNull V load();

	@NonNull CompletableFuture<V> loadAsync();
	
	@NonNull V load(@NonNull SqlLoadOptions options);
	
	@NonNull CompletableFuture<V> loadAsync(@NonNull SqlLoadOptions options);
	
	@NonNull V reload();
	
	@NonNull CompletableFuture<V> reloadAsync();

	@NonNull V loadIfAbsent();

	@NonNull SqlForeignKey<K, V> withValue(@Nullable V value);
}
