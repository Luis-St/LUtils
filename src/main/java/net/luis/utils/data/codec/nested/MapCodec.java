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

package net.luis.utils.data.codec.nested;

import com.google.common.collect.Maps;
import net.luis.utils.data.codec.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class MapCodec<K, V> implements NestedCodec<Map<K, V>> {
	
	private final Codec<K> keyCodec;
	private final Codec<V> valueCodec;
	
	public MapCodec(@NotNull Codec<K> keyCodec, @NotNull Codec<V> valueCodec) {
		this.keyCodec = Objects.requireNonNull(keyCodec, "Key codec must not be null");
		this.valueCodec = Objects.requireNonNull(valueCodec, "Value codec must not be null");
	}
	
	@Override
	public <X> @NotNull ParserCache<X> isValid(@Nullable String value) {
		return null;
	}
	
	@Override
	public @NotNull <X> Map<K, V> decode(@NotNull ParserCache<X> cache) {
		return null;
	}
	
	@Override
	public @NotNull String encode(@NotNull Map<K, V> map) {
		Objects.requireNonNull(map, "Map must not be null");
		StringBuilder builder = new StringBuilder("{");
		for (Map.Entry<K, V> entry : map.entrySet()) {
			builder.append(this.keyCodec.encode(entry.getKey())).append("=").append(this.valueCodec.encode(entry.getValue())).append(",");
		}
		builder.deleteCharAt(builder.length() - 1).append("}");
		return builder.toString();
	}
	
	@Override
	public @NotNull Map<K, V> getDefaultValue() {
		return Maps.newHashMap();
	}
	
	@Override
	public boolean isNested() {
		return true;
	}
}
