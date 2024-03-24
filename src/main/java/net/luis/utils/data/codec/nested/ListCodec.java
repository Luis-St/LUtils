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

import com.google.common.collect.Lists;
import net.luis.utils.data.codec.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class ListCodec<T> implements NestedCodec<List<T>> {
	
	private final Codec<T> innerCodec;
	
	public ListCodec(@NotNull Codec<T> innerCodec) {
		this.innerCodec = Objects.requireNonNull(innerCodec, "Inner codec must not be null");
	}
	
	@Override
	public <X> @NotNull ParserCache<X> isValid(@Nullable String value) {
		return null;
	}
	
	@Override
	public @NotNull <X> List<T> decode(@NotNull ParserCache<X> cache) {
		return null;
	}
	
	@Override
	public @NotNull String encode(@NotNull List<T> list) {
		Objects.requireNonNull(list, "List must not be null");
		StringBuilder builder = new StringBuilder("[");
		for (T element : list) {
			builder.append(this.innerCodec.encode(element)).append(",");
		}
		builder.deleteCharAt(builder.length() - 1).append("]");
		return builder.toString();
	}
	
	@Override
	public @NotNull List<T> getDefaultValue() {
		return Lists.newArrayList();
	}
	
	@Override
	public boolean isNested() {
		return true;
	}
}
