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

import net.luis.utils.collection.util.SimpleEntry;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.KeyableCodec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public class MapCodec<K, V> implements Codec<Map<K, V>> {
	
	private final KeyableCodec<K> keyCodec;
	private final Codec<V> valueCodec;
	private final int minSize;
	private final int maxSize;
	
	public MapCodec(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec) {
		this(keyCodec, valueCodec, 0, Integer.MAX_VALUE);
	}
	
	public MapCodec(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec, int minSize, int maxSize) {
		this.keyCodec = keyCodec;
		this.valueCodec = valueCodec;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Map<K, V> value) {
		if (value == null) {
			return Result.error("Unable to encode null value as map using '" + this + "'");
		}
		List<Result<Map.Entry<String, R>>> encoded = value.entrySet().stream().map(entry -> this.encodeEntry(provider, entry)).toList();
		if (encoded.stream().anyMatch(Result::isError)) {
			return Result.error("Unable to encode some entries of map '" + value + "' using '" + this + "': \n" + encoded.stream().filter(Result::isError).map(Result::errorOrThrow).collect(Collectors.joining("\n")));
		}
		Result<R> emptyMap = provider.createMap();
		if (emptyMap.isError()) {
			return Result.error("Unable to create empty map: " + emptyMap.errorOrThrow());
		}
		R resultMap = emptyMap.orThrow();
		encoded.stream().map(Result::orThrow).filter(entry -> provider.getEmpty(entry.getValue()).isError()).forEach(entry -> {
			provider.set(resultMap, entry.getKey(), entry.getValue());
		});
		return provider.merge(current, resultMap);
	}
	
	private <R> @NotNull Result<Map.Entry<String, R>> encodeEntry(@NotNull TypeProvider<R> provider, @NotNull Map.Entry<K, V> entry) {
		Result<String> encodedKey = this.keyCodec.encodeKey(provider, entry.getKey());
		if (encodedKey.isError()) {
			return Result.error("Unable to encode key '" + entry.getKey() + "' of map entry '" + entry + "' using codec '" + this.keyCodec + "': \n" + encodedKey.errorOrThrow());
		}
		Result<R> encodedValue = this.valueCodec.encodeStart(provider, provider.empty(), entry.getValue());
		if (encodedValue.isError()) {
			return Result.error("Unable to encode value '" + entry.getValue() + "' of map entry '" + entry + "' using codec '" + this.valueCodec + "': \n" + encodedValue.errorOrThrow());
		}
		return Result.success(new SimpleEntry<>(encodedKey.orThrow(), encodedValue.orThrow()));
	}
	
	@Override
	public <R> @NotNull Result<Map<K, V>> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		if (value == null) {
			return Result.error("Unable to decode null value as map using '" + this + "'");
		}
		Result<Map<String, R>> decodedMap = provider.getMap(value);
		if (decodedMap.isError()) {
			return Result.error("Unable to decode map '" + value + "' using '" + this + "': \n" + decodedMap.errorOrThrow());
		}
		List<Result<Map.Entry<K, V>>> decoded = decodedMap.orThrow().entrySet().stream().map(entry -> this.decodeEntry(provider, entry)).toList();
		if (decoded.stream().anyMatch(Result::isError)) {
			return Result.error("Unable to decode some entries of map '" + value + "' using '" + this + "': \n" + decoded.stream().filter(Result::isError).map(Result::errorOrThrow).collect(Collectors.joining("\n")));
		}
		if (this.maxSize >= decoded.size() && decoded.size() >= this.minSize) {
			return Result.success(decoded.stream().map(Result::orThrow).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
		}
		return Result.error("List was decoded successfully but size '" + decoded.size() + "' is out of range: " + this.minSize + ".." + this.maxSize);
	}
	
	private <R> @NotNull Result<Map.Entry<K, V>> decodeEntry(@NotNull TypeProvider<R> provider, @NotNull Map.Entry<String, R> entry) {
		Result<K> decodedKey = this.keyCodec.decodeKey(provider, entry.getKey());
		if (decodedKey.isError()) {
			return Result.error("Unable to decode key '" + entry.getKey() + "' using codec '" + this.keyCodec + "': \n" + decodedKey.errorOrThrow());
		}
		Result<V> decodedValue = this.valueCodec.decodeStart(provider, entry.getValue());
		if (decodedValue.isError()) {
			return Result.error("Unable to decode value '" + entry.getValue() + "' using codec '" + this.valueCodec + "': \n" + decodedValue.errorOrThrow());
		}
		return Result.success(new SimpleEntry<>(decodedKey.orThrow(), decodedValue.orThrow()));
	}
	
	@Override
	public String toString() {
		return "MapCodec[" + this.keyCodec + ", " + this.valueCodec + "]";
	}
}