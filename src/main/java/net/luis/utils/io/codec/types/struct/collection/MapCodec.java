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

package net.luis.utils.io.codec.types.struct.collection;

import net.luis.utils.collection.util.SimpleEntry;
import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.SizeConstraint;
import net.luis.utils.io.codec.constraint.config.SizeConstraintConfig;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * A codec for encoding and decoding maps of key-value pairs.<br>
 * This codec uses two other codecs to encode and decode the entries of the map.<br>
 *
 * @author Luis-St
 *
 * @param <K> The type of keys in the map
 * @param <V> The type of values in the map
 */
public class MapCodec<K, V> extends AbstractCodec<Map<K, V>, SizeConstraintConfig> implements SizeConstraint<Map<K, V>, MapCodec<K, V>> {
	
	/**
	 * The codec used to encode and decode the keys of the map.<br>
	 */
	private final Codec<K> keyCodec;
	/**
	 * The codec used to encode and decode the values of the map.<br>
	 */
	private final Codec<V> valueCodec;
	
	/**
	 * Constructs a new map codec using the given codecs for the keys and values.<br>
	 *
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @throws NullPointerException If the key or value codec is null
	 */
	public MapCodec(@NonNull Codec<K> keyCodec, @NonNull Codec<V> valueCodec) {
		this.keyCodec = Objects.requireNonNull(keyCodec, "Key codec must not be null");
		this.valueCodec = Objects.requireNonNull(valueCodec, "Value codec must not be null");
	}
	
	/**
	 * Constructs a new map codec using the given codecs for the keys and values and the given size constraint configuration.<br>
	 *
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @param constraintConfig The size constraint configuration
	 * @throws NullPointerException If the key or value codec is null
	 */
	public MapCodec(@NonNull Codec<K> keyCodec, @NonNull Codec<V> valueCodec, @NonNull SizeConstraintConfig constraintConfig) {
		this.keyCodec = Objects.requireNonNull(keyCodec, "Key codec must not be null");
		this.valueCodec = Objects.requireNonNull(valueCodec, "Value codec must not be null");
		super(constraintConfig);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Class<Map<K, V>> getType() {
		return (Class<Map<K, V>>) (Class<?>) Map.class;
	}
	
	@Override
	public @NonNull MapCodec<K, V> applyConstraint(@NonNull UnaryOperator<SizeConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new MapCodec<>(this.keyCodec, this.valueCodec, configModifier.apply(
			this.getConstraintConfig().orElse(SizeConstraintConfig.UNCONSTRAINED)
		));
	}
	
	@Override
	protected @NonNull Result<Void> checkConstraints(@NonNull Map<K, V> value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(value.size())).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("Map " + value + " does not meet constraints: " + constraintResult.errorOrThrow());
		}
		return Result.success();
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Map<K, V> value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null value as map using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error("Unable to encode map using '" + this + "': " + constraintResult.errorOrThrow());
		}
		
		Result<R> emptyMap = provider.createMap();
		if (emptyMap.isError()) {
			return Result.error("Unable to create empty map: " + emptyMap.errorOrThrow());
		}
		R resultMap = emptyMap.resultOrThrow();
		
		int successCount = 0;
		List<String> errors = new ArrayList<>();
		for (Map.Entry<K, V> entry : value.entrySet()) {
			Result<Map.Entry<String, R>> result = this.encodeEntry(provider, entry);
			
			if (result.hasValue()) {
				Map.Entry<String, R> encodedEntry = result.resultOrThrow();
				if (provider.getEmpty(encodedEntry.getValue()).isError()) {
					provider.set(resultMap, encodedEntry.getKey(), encodedEntry.getValue());
					successCount++;
				}
			}
			if (result.hasError()) {
				errors.add("Key '" + entry.getKey() + "': " + result.errorOrThrow());
			}
		}
		
		Result<R> merged = provider.merge(current, resultMap);
		if (merged.isError()) {
			return Result.error(merged.errorOrThrow());
		}
		if (errors.isEmpty()) {
			return merged;
		}
		return Result.partial(merged.resultOrThrow(), "Encoded " + successCount + " of " + value.size() + " entries successfully:", errors);
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
	private <R> @NonNull Result<Map.Entry<String, R>> encodeEntry(@NonNull TypeProvider<R> provider, Map.@NonNull Entry<K, V> entry) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(entry, "Map entry must not be null");
		
		Result<String> encodedKey = this.keyCodec.encodeKey(entry.getKey());
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
	public <R> @NonNull Result<Map<K, V>> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as map using '" + this + "'");
		}
		
		Result<Map<String, R>> decodedMap = provider.getMap(value);
		if (decodedMap.isError()) {
			return Result.error("Unable to decode map '" + value + "' using '" + this + "': \n" + decodedMap.errorOrThrow());
		}
		Map<String, R> map = decodedMap.resultOrThrow();
		
		Map<K, V> entries = new HashMap<>();
		List<String> errors = new ArrayList<>();
		for (Map.Entry<String, R> entry : map.entrySet()) {
			Result<Map.Entry<K, V>> result = this.decodeEntry(provider, value, entry);
			if (result.hasValue()) {
				Map.Entry<K, V> decodedEntry = result.resultOrThrow();
				entries.put(decodedEntry.getKey(), decodedEntry.getValue());
			}
			if (result.hasError()) {
				errors.add("Key '" + entry.getKey() + "': " + result.errorOrThrow());
			}
		}
		
		if (entries.isEmpty() && !errors.isEmpty()) {
			return Result.error("Unable to decode any entries of the map using '" + this + "': " + String.join("\n - ", errors));
		}
		Result<Void> constraintResult = this.checkConstraints(entries);
		if (constraintResult.isError()) {
			return Result.error("Unable to decode map using '" + this + "': " + constraintResult.errorOrThrow());
		}
		
		if (errors.isEmpty()) {
			return Result.success(entries);
		}
		return Result.partial(entries, "Decoded " + entries.size() + " of " + map.size() + " entries successfully:", errors);
	}
	
	/**
	 * Decodes the given map entry using the key and value codecs.<br>
	 * The result contains the decoded entry or an error message.<br>
	 *
	 * @param provider The type provider
	 * @param current The current map in which the entry will be decoded
	 * @param entry The map entry
	 * @return The result of the decoding process
	 * @param <R> The type of the decoded value
	 * @throws NullPointerException If the type provider or map entry is null
	 */
	private <R> @NonNull Result<Map.Entry<K, V>> decodeEntry(@NonNull TypeProvider<R> provider, @NonNull R current, Map.@NonNull Entry<String, R> entry) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(entry, "Map entry must not be null");
		
		Result<K> decodedKey = this.keyCodec.decodeKey(entry.getKey());
		if (decodedKey.isError()) {
			return Result.error("Unable to decode key '" + entry.getKey() + "' using codec '" + this.keyCodec + "': \n" + decodedKey.errorOrThrow());
		}
		
		Result<V> decodedValue = this.valueCodec.decodeStart(provider, current, entry.getValue());
		if (decodedValue.isError()) {
			return Result.error("Unable to decode value '" + entry.getValue() + "' using codec '" + this.valueCodec + "': \n" + decodedValue.errorOrThrow());
		}
		return Result.success(new SimpleEntry<>(decodedKey.resultOrThrow(), decodedValue.resultOrThrow()));
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MapCodec<?, ?> mapCodec)) return false;
		
		if (!this.keyCodec.equals(mapCodec.keyCodec)) return false;
		return this.valueCodec.equals(mapCodec.valueCodec);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.keyCodec, this.valueCodec);
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(sizeConstraintConfig -> {
			return "ConstrainedMapCodec[" + this.keyCodec + ", " + this.valueCodec + ",constraints=" + sizeConstraintConfig + "]";
		}).orElse("MapCodec[" + this.keyCodec + ", " + this.valueCodec + "]");
	}
	//endregion
}
