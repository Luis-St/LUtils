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

package net.luis.utils.io.codec.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public class ListCodec<C> implements Codec<List<C>> {
	
	private final Codec<C> codec;
	private final int minSize;
	private final int maxSize;
	
	public ListCodec(@NotNull Codec<C> codec) {
		this(codec, 0, Integer.MAX_VALUE);
	}
	
	public ListCodec(@NotNull Codec<C> codec, int minSize, int maxSize) {
		this.codec = codec;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable List<C> value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null value as list using '" + this + "'");
		}
		List<Result<R>> encoded = value.stream().map(v -> this.codec.encodeStart(provider, provider.empty(), v)).toList();
		if (encoded.stream().anyMatch(Result::isError)) {
			return Result.error("Unable to encode some elements of list '" + value + "' using '" + this + "': \n" + encoded.stream().filter(Result::isError).map(Result::errorOrThrow).collect(Collectors.joining("\n")));
		}
		List<R> results = encoded.stream().map(Result::orThrow).filter(result -> provider.getEmpty(result).isError()).toList();
		return provider.merge(current, provider.createList(results));
	}
	
	@Override
	public <R> @NotNull Result<List<C>> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as list using'" + this + "'");
		}
		Result<List<R>> decoded = provider.getList(value);
		if (decoded.isError()) {
			return Result.error("Unable to decode list using '" + this + "': " + decoded.errorOrThrow());
		}
		List<Result<C>> results = decoded.orThrow().stream().map(v -> this.codec.decodeStart(provider, v)).toList();
		if (results.stream().anyMatch(Result::isError)) {
			return Result.error("Unable to decode some elements of list using '" + this + "': \n" + results.stream().filter(Result::isError).map(Result::errorOrThrow).collect(Collectors.joining("\n")));
		}
		if (this.maxSize >= results.size() && results.size() >= this.minSize) {
			return Result.success(results.stream().map(Result::orThrow).toList());
		}
		return Result.error("List was decoded successfully but size '" + results.size() + "' is out of range: " + this.minSize + ".." + this.maxSize);
	}
	
	@Override
	public String toString() {
		return "ListCodec[" + this.codec + "]";
	}
}
