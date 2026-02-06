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

package net.luis.utils.io.codec.types.struct.collection;

import net.luis.utils.collection.util.SimpleEntry;
import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.constraint.config.collection.MapConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.collection.MapConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Either;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * A codec for encoding and decoding maps of key-value pairs.<br>
 * This codec uses two other codecs to encode and decode the entries of the map.<br>
 *
 * @param <K> The type of keys in the map
 * @param <V> The type of values in the map
 *
 * @author Luis-St
 */
public class MapCodec<K, V>
	extends AbstractConstrainableCodec<Map<K, V>, MapConstraintConfig<K, V>, MapCodec<K, V>>
	implements PartialCodec<MapCodec<K, V>>, MapConstraint<K, V, MapCodec<K, V>> {
	
	/**
	 * The codec used to encode and decode the keys of the map.<br>
	 */
	private final Codec<K> keyCodec;
	/**
	 * The codec used to encode and decode the values of the map.<br>
	 */
	private final Codec<V> valueCodec;
	/**
	 * Whether this codec is partial.<br>
	 */
	private final boolean partial;
	
	/**
	 * Constructs a new map codec using the given codecs for the keys and values.<br>
	 *
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @throws NullPointerException If the key or value codec is null
	 */
	public MapCodec(@NonNull Codec<K> keyCodec, @NonNull Codec<V> valueCodec) {
		this(keyCodec, valueCodec, false, MapConstraintConfig.unconstrained());
	}
	
	/**
	 * Constructs a new map codec using the given codecs for the keys and values and the given configuration.<br>
	 *
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @param config The constraint configuration
	 * @throws NullPointerException If the key codec, value codec or the config is null
	 */
	private MapCodec(@NonNull Codec<K> keyCodec, @NonNull Codec<V> valueCodec, @NonNull MapConstraintConfig<K, V> config) {
		this(keyCodec, valueCodec, false, config);
	}
	
	/**
	 * Constructs a new map codec using the given codecs for the keys and values, the given partial flag and the given configuration.<br>
	 *
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @param partial Whether this codec is partial
	 * @param config The constraint configuration
	 * @throws NullPointerException If the key codec, value codec or the config is null
	 */
	private MapCodec(@NonNull Codec<K> keyCodec, @NonNull Codec<V> valueCodec, boolean partial, @NonNull MapConstraintConfig<K, V> config) {
		super(newConfig -> new MapCodec<>(keyCodec, valueCodec, partial, newConfig), config);
		this.keyCodec = Objects.requireNonNull(keyCodec, "Key codec must not be null");
		this.valueCodec = Objects.requireNonNull(valueCodec, "Value codec must not be null");
		this.partial = partial;
	}
	
	@Override
	public @NonNull MapCodec<K, V> partial() {
		return new MapCodec<>(this.keyCodec, this.valueCodec, true, this.config);
	}
	
	@Override
	public @NonNull MapCodec<K, V> strict() {
		return new MapCodec<>(this.keyCodec, this.valueCodec, false, this.config);
	}
	
	@Override
	public boolean isPartial() {
		return this.partial;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Class<Map<K, V>> getType() {
		return (Class<Map<K, V>>) (Class<?>) Map.class;
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Map<K, V> value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null value as map", this);
		}
		
		List<Either<Map.Entry<String, R>, EncoderException>> partialEntries = new ArrayList<>(value.size());
		for (Map.Entry<K, V> entry : this.validateEncodeConstraints(value).entrySet()) {
			try {
				Map.Entry<String, R> encodedEntry = this.encodeEntry(provider, entry);
				
				if (!provider.isEmpty(encodedEntry.getValue())) {
					partialEntries.add(Either.left(encodedEntry));
				}
			} catch (EncoderException e) {
				partialEntries.add(Either.right(e));
			}
		}
		
		R map = provider.createMap();
		for (Map.Entry<String, R> entry : this.encode(partialEntries)) {
			provider.set(map, entry.getKey(), entry.getValue());
		}
		return provider.merge(current, map);
	}
	
	/**
	 * Encodes the given map entry using the key and value codecs.<br>
	 * The result contains the encoded entry or an error message.<br>
	 *
	 * @param provider The type provider
	 * @param entry The map entry
	 * @param <R> The type of the encoded value
	 * @return The result value of the encoding process
	 * @throws NullPointerException If the type provider or map entry is null
	 * @throws EncoderException If the key or value of the entry cannot be encoded
	 */
	private <R> Map.@NonNull Entry<String, R> encodeEntry(@NonNull TypeProvider<R> provider, Map.@NonNull Entry<K, V> entry) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(entry, "Map entry must not be null");
		
		String encodedKey = this.keyCodec.encodeKey(entry.getKey());
		R encodedValue = this.valueCodec.encode(provider, provider.empty(), entry.getValue());
		return new SimpleEntry<>(encodedKey, encodedValue);
	}
	
	@Override
	public <R> @NonNull Map<K, V> decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as map", this);
		}
		
		List<Either<Map.Entry<K, V>, DecoderException>> partialEntries = new ArrayList<>();
		for (Map.Entry<String, R> entry : provider.getMap(value).entrySet()) {
			try {
				Map.Entry<K, V> decodedEntry = this.decodeEntry(provider, value, entry);
				partialEntries.add(Either.left(decodedEntry));
			} catch (DecoderException e) {
				partialEntries.add(Either.right(e));
			}
		}
		
		Map<K, V> entries = new HashMap<>();
		for (Map.Entry<K, V> entry : this.decode(partialEntries)) {
			entries.put(entry.getKey(), entry.getValue());
		}
		return this.validateDecodeConstraints(entries);
	}
	
	/**
	 * Decodes the given map entry using the key and value codecs.<br>
	 * The result contains the decoded entry or an error message.<br>
	 *
	 * @param provider The type provider
	 * @param current The current map in which the entry will be decoded
	 * @param entry The map entry
	 * @param <R> The type of the decoded value
	 * @return The result value of the decoding process
	 * @throws NullPointerException If the type provider or map entry is null
	 * @throws DecoderException If the key or value of the entry cannot be decoded
	 */
	private <R> Map.@NonNull Entry<K, V> decodeEntry(@NonNull TypeProvider<R> provider, @NonNull R current, Map.@NonNull Entry<String, R> entry) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(entry, "Map entry must not be null");
		
		K decodedKey = this.keyCodec.decodeKey(entry.getKey());
		V decodedValue = this.valueCodec.decode(provider, current, entry.getValue());
		return new SimpleEntry<>(decodedKey, decodedValue);
	}
}
