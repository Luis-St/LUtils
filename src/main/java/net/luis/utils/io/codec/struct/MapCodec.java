/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A codec for encoding and decoding maps of key-value pairs.<br>
 * This codec uses two other codecs to encode and decode the keys and values of the map.<br>
 * The key codec must be a {@link KeyableCodec} to encode and decode the keys of the map.<br>
 * <p>
 *     The map codec can be sized to only accept maps of a certain size range.<br>
 *     The minimum size and maximum size are inclusive.<br>
 *     If the map size is out of range, the codec will return an error during encoding or decoding.
 * </p>
 *
 * @author Luis-St
 *
 * @param <K> The type of keys in the map
 * @param <V> The type of values in the map
 */
public class MapCodec<K, V> implements Codec<Map<K, V>> {
	
	/**
	 * The codec used to encode and decode the keys of the map.<br>
	 */
	private final KeyableCodec<K> keyCodec;
	/**
	 * The codec used to encode and decode the values of the map.<br>
	 */
	private final Codec<V> valueCodec;
	/**
	 * The minimum size of the map (inclusive).<br>
	 */
	private final int minSize;
	/**
	 * The maximum size of the map (inclusive).<br>
	 */
	private final int maxSize;
	
	/**
	 * Constructs a new map codec using the given codecs for the keys and values.<br>
	 * The map codec will accept maps of any size.<br>
	 * Do not use this constructor directly, use any of the map factory methods in {@link Codec} instead.<br>
	 *
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @throws NullPointerException If the key or value codec is null
	 */
	@ApiStatus.Internal
	public MapCodec(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec) {
		this(keyCodec, valueCodec, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Constructs a new map codec using the given codecs for the keys and values and the size range of the map.<br>
	 * Do not use this constructor directly, use any of the map factory methods in {@link Codec} instead.<br>
	 *
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @param minSize The minimum size of the map (inclusive)
	 * @param maxSize The maximum size of the map (inclusive)
	 * @throws NullPointerException If the key or value codec is null
	 * @throws IllegalArgumentException If the minimum size is less than zero or greater than the maximum size
	 */
	@ApiStatus.Internal
	public MapCodec(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec, int minSize, int maxSize) {
		this.keyCodec = Objects.requireNonNull(keyCodec, "Key codec must not be null");
		this.valueCodec = Objects.requireNonNull(valueCodec, "Value codec must not be null");
		
		if (0 > minSize) {
			throw new IllegalArgumentException("Minimum size must be at least 0");
		}
		if (minSize > maxSize) {
			throw new IllegalArgumentException("Minimum size must be less than or equal to maximum size");
		}
		
		this.minSize = minSize;
		this.maxSize = maxSize;
	}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Map<K, V> value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null value as map using '" + this + "'");
		}
		if (value.size() > this.maxSize || this.minSize > value.size()) {
			return Result.error("Map size '" + value.size() + "' is out of range: " + this.minSize + ".." + this.maxSize);
		}
		
		List<Result<Map.Entry<String, R>>> encoded = value.entrySet().stream().map(entry -> this.encodeEntry(provider, entry)).toList();
		if (encoded.stream().anyMatch(Result::isError)) {
			return Result.error("Unable to encode some entries of map '" + value + "' using '" + this + "': \n" + encoded.stream().filter(Result::isError).map(Result::errorOrThrow).collect(Collectors.joining("\n")));
		}
		
		Result<R> emptyMap = provider.createMap();
		if (emptyMap.isError()) {
			return Result.error("Unable to create empty map: " + emptyMap.errorOrThrow());
		}
		
		R resultMap = emptyMap.resultOrThrow();
		encoded.stream().map(Result::resultOrThrow).filter(entry -> provider.getEmpty(entry.getValue()).isError()).forEach(entry -> {
			provider.set(resultMap, entry.getKey(), entry.getValue());
		});
		return provider.merge(current, resultMap);
	}
	
	/**
	 * Encodes the given map entry using the key and value codecs.<br>
	 * The result contains the encoded entry or an error message.<br>
	 *
	 * @param provider The type provider
	 * @param entry The map entry
	 * @return The result of the encoding process
	 * @param <R> The type of the encoded value
	 * @throws NullPointerException If the type provider or map entry is null
	 */
	private <R> @NotNull Result<Map.Entry<String, R>> encodeEntry(@NotNull TypeProvider<R> provider, @NotNull Map.Entry<K, V> entry) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(entry, "Map entry must not be null");
		
		Result<String> encodedKey = this.keyCodec.encodeKey(provider, entry.getKey());
		if (encodedKey.isError()) {
			return Result.error("Unable to encode key '" + entry.getKey() + "' of map entry '" + entry + "' using codec '" + this.keyCodec + "': \n" + encodedKey.errorOrThrow());
		}
		
		Result<R> encodedValue = this.valueCodec.encodeStart(provider, provider.empty(), entry.getValue());
		if (encodedValue.isError()) {
			return Result.error("Unable to encode value '" + entry.getValue() + "' of map entry '" + entry + "' using codec '" + this.valueCodec + "': \n" + encodedValue.errorOrThrow());
		}
		return Result.success(new SimpleEntry<>(encodedKey.resultOrThrow(), encodedValue.resultOrThrow()));
	}
	
	@Override
	public <R> @NotNull Result<Map<K, V>> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as map using '" + this + "'");
		}
		
		Result<Map<String, R>> decodedMap = provider.getMap(value);
		if (decodedMap.isError()) {
			return Result.error("Unable to decode map '" + value + "' using '" + this + "': \n" + decodedMap.errorOrThrow());
		}
		
		List<Result<Map.Entry<K, V>>> decoded = decodedMap.resultOrThrow().entrySet().stream().map(entry -> this.decodeEntry(provider, entry)).toList();
		if (decoded.stream().anyMatch(Result::isError)) {
			return Result.error("Unable to decode some entries of map '" + value + "' using '" + this + "': \n" + decoded.stream().filter(Result::isError).map(Result::errorOrThrow).collect(Collectors.joining("\n")));
		}
		
		if (this.maxSize >= decoded.size() && decoded.size() >= this.minSize) {
			return Result.success(decoded.stream().map(Result::resultOrThrow).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
		}
		return Result.error("Map was decoded successfully but size '" + decoded.size() + "' is out of range: " + this.minSize + ".." + this.maxSize);
	}
	
	/**
	 * Decodes the given map entry using the key and value codecs.<br>
	 * The result contains the decoded entry or an error message.<br>
	 *
	 * @param provider The type provider
	 * @param entry The map entry
	 * @return The result of the decoding process
	 * @param <R> The type of the decoded value
	 * @throws NullPointerException If the type provider or map entry is null
	 */
	private <R> @NotNull Result<Map.Entry<K, V>> decodeEntry(@NotNull TypeProvider<R> provider, @NotNull Map.Entry<String, R> entry) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(entry, "Map entry must not be null");
		
		Result<K> decodedKey = this.keyCodec.decodeKey(provider, entry.getKey());
		if (decodedKey.isError()) {
			return Result.error("Unable to decode key '" + entry.getKey() + "' using codec '" + this.keyCodec + "': \n" + decodedKey.errorOrThrow());
		}
		
		Result<V> decodedValue = this.valueCodec.decodeStart(provider, entry.getValue());
		if (decodedValue.isError()) {
			return Result.error("Unable to decode value '" + entry.getValue() + "' using codec '" + this.valueCodec + "': \n" + decodedValue.errorOrThrow());
		}
		return Result.success(new SimpleEntry<>(decodedKey.resultOrThrow(), decodedValue.resultOrThrow()));
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MapCodec<?, ?> mapCodec)) return false;
		
		if (this.minSize != mapCodec.minSize) return false;
		if (this.maxSize != mapCodec.maxSize) return false;
		if (!this.keyCodec.equals(mapCodec.keyCodec)) return false;
		return this.valueCodec.equals(mapCodec.valueCodec);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.keyCodec, this.valueCodec, this.minSize, this.maxSize);
	}
	
	@Override
	public String toString() {
		return "MapCodec[" + this.keyCodec + ", " + this.valueCodec + "]";
	}
	//endregion
}
