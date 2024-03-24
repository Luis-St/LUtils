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

package net.luis.utils.data.key;

import com.google.common.collect.Lists;
import net.luis.utils.lang.EnumLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public interface Key {
	
	@SuppressWarnings("unchecked")
	public static <T extends Key> @NotNull T create(@Nullable String key) {
		Objects.requireNonNull(key, "Key must not be null");
		List<KeyType<?>> types = Lists.newArrayList(EnumLike.values(KeyType.class));
		types.sort(Collections.reverseOrder(KeyType::compareTo));
		for (KeyType<?> type : types) {
			if (type.isValid(key)) {
				return (T) type.createKey(key);
			}
		}
		throw new IllegalArgumentException("No valid key type found for key: '" + key + "'");
	}
	
	public static <T extends Key> @NotNull Optional<T> createSafe(@Nullable String key) {
		try {
			return Optional.of(create(key));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}
	
	@NotNull KeyType<?> getType();
	
	@NotNull String getRawKey();
	
	default @NotNull String getKey(Object @Nullable ... objects) {
		return this.getRawKey();
	}
	
	String @NotNull [] getKeyParts();
	
	default boolean matches(@Nullable String key) {
		return this.getRawKey().equals(key);
	}
}
