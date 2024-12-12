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
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public class MapCodec<C> implements Codec<Map<String, C>> {
	
	private final Codec<String> keyCodec;
	private final Codec<C> valueCodec;
	
	public MapCodec(@NotNull Codec<String> keyCodec, @NotNull Codec<C> valueCodec) {
		this.keyCodec = keyCodec;
		this.valueCodec = valueCodec;
	}
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Map<String, C> value) {
		if (value == null) {
			return Result.error("Unable to encode null value as map of '" + this.keyCodec + "' and '" + this.valueCodec + "'");
			return Result.error("Unable to encode null value as map using '" + this + "'");
		}
		Map<String, Result<R>> encoded = Utils.mapValue(value, v -> this.valueCodec.encodeStart(provider, provider.empty(), v));
		Collection<Result<R>> encodedValues = encoded.values();
		if (encodedValues.stream().anyMatch(Result::isError)) {
			return Result.error("Unable to encode some elements of map '" + value + "' of '" + this.keyCodec + "' and '" + this.valueCodec + "': \n" +
				encodedValues.stream().filter(Result::isError).map(Result::errorOrThrow).collect(Collectors.joining("\n")));
		}
		return provider.merge(current, provider.createMap(Utils.mapValue(encoded, Result::orThrow)));
	}
	
	@Override
	public @NotNull <R> Result<Map<String, C>> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		if (value == null) {
			return Result.error("Unable to decode null value as map using '" + this + "'");
		}
		Result<Map<String, R>> decodedMap = provider.getMap(value);
		if (decodedMap.isError()) {
			return Result.error("Unable to decode map '" + value + "' using '" + this + "': \n" + decodedMap.errorOrThrow());
		}
		Result<Map<String, R>> decoded = provider.getMap(value);
		if (decoded.isError()) {
			return Result.error("Unable to decode map '" + value + "' of '" + this.keyCodec + "' and '" + this.valueCodec + "': \n" + decoded.errorOrThrow());
		}
		Map<String, Result<C>> decodedMap = Utils.mapValue(decoded.orThrow(), v -> this.valueCodec.decodeStart(provider, v));
		Collection<Result<C>> decodedValues = decodedMap.values();
		if (decodedValues.stream().anyMatch(Result::isError)) {
			return Result.error("Unable to decode some elements of map '" + value + "' of '" + this.keyCodec + "' and '" + this.valueCodec + "': \n" +
				decodedValues.stream().filter(Result::isError).map(Result::errorOrThrow).collect(Collectors.joining("\n")));
		}
		return Result.success(Utils.mapValue(decodedMap, Result::orThrow));
	
	@Override
	public String toString() {
		return "MapCodec[" + this.keyCodec + ", " + this.valueCodec + "]";
	}
}
