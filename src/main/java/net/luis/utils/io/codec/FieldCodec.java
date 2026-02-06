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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * A codec for encoding and decoding a field of a data structure by name.<br>
 * This codec uses another codec to encode and decode the value.<br>
 * <p>
 *     The field codec is used to encode and decode components of data structures with a specific name.<br>
 *     The name is used to identify the value in the data structure.<br>
 *     Optionally, aliases can be configured to identify the value if the name is not present.
 * </p>
 * <p>
 *     The current value in {@link #encodeInternal(TypeProvider, Object, Object)} must be a data structure that can hold the named value.<br>
 *     If the current value is not a data structure, the codec will return an error.
 * </p>
 * <p>
 *     The value in {@link #decode(TypeProvider, Object, Object)} must be a data structure that contains the named value.<br>
 *     If the value is not a data structure or the named value is not present, the codec will return an error.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of the named component
 * @param <O> The type of the object containing the component
 */
public class FieldCodec<C, O> {
	
	/**
	 * The codec used to encode and decode the named value.<br>
	 */
	private final Codec<C> codec;
	/**
	 * The name of the value.<br>
	 */
	private final String name;
	/**
	 * The aliases of the value.<br>
	 */
	private final Set<String> aliases;
	/**
	 * The function used to retrieve the component from the object.<br>
	 */
	private final Function<O, C> getter;
	
	/**
	 * Constructs a new field codec.<br>
	 *
	 * @param codec The codec used to
	 * @param name The name of the value
	 * @param aliases The aliases of the value
	 * @param getter The getter for the component
	 * @throws NullPointerException If the codec, name, aliases or getter is null
	 */
	FieldCodec(@NonNull Codec<C> codec, @NonNull String name, Set<String> aliases, @NonNull Function<O, C> getter) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.aliases = Set.copyOf(Objects.requireNonNull(aliases, "Aliases must not be null"));
		this.getter = Objects.requireNonNull(getter, "Getter must not be null");
	}
	
	/**
	 * Encodes the component of the object using the given type provider and current value.<br>
	 * Internally, the component is retrieved from the object using the configured getter and then further encoded using the internal codec.<br>
	 *
	 * @param provider The type provider
	 * @param current The current value
	 * @param object The object
	 * @return The object as map with the encoded component
	 * @param <R> The type of the current value
	 * @throws NullPointerException If the type provider or current value is null
	 * @throws EncoderException If an error occurs during encoding
	 * @see #encodeInternal(TypeProvider, Object, Object)
	 */
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable O object) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (object == null) {
			throw new EncoderException("Unable to encode component because the component can not be retrieved from a null object", this.codec);
		}
		return this.encodeInternal(provider, current, this.getter.apply(object));
	}
	
	/**
	 * Encodes the value using the internal codec and the given provider and map.<br>
	 * The encoded value will be stored in the map using the name of this codec.<br>
	 *
	 * @param provider The type provider
	 * @param map The map to encode the value into
	 * @param value The value to encode
	 * @param <R> The type to encode into
	 * @return The object as map with the encoded component
	 * @throws NullPointerException If the provider or map is null
	 * @throws EncoderException If an error occurs during encoding
	 */
	private <R> @NonNull R encodeInternal(@NonNull TypeProvider<R> provider, @NonNull R map, @Nullable C value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(map, "Current value must not be null");
		
		try {
			R encodedValue = this.codec.encode(provider, provider.empty(), value);
			if (provider.isEmpty(encodedValue)) {
				return provider.empty();
			}
			
			provider.set(map, this.name, encodedValue);
			return map;
		} catch (EncoderException e) {
			throw new EncoderException("Unable to encode named '" + this.name + "' '" + value, this.codec, e);
		}
	}
	
	/**
	 * Decodes the value using the internal codec and the given provider and map.<br>
	 * The value is retrieved from the map using the name of this codec.<br>
	 *
	 * @param provider The type provider
	 * @param current The current value
	 * @param map The map to decode the value from
	 * @param <R> The type to decode from
	 * @return The decoded value
	 * @throws NullPointerException If the provider is null
	 * @throws DecoderException If an error occurs during decoding
	 */
	public <R> @NonNull C decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R map) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (map == null) {
			throw new DecoderException("Unable to decode named '" + this.name + "' null value", this.codec);
		}
		
		try {
			R value = provider.get(map, this.name);
			return this.codec.decode(provider, map, value);
		} catch (DecoderException e) {
			try {
				return this.decodeWithAlias(provider, map);
			} catch (DecoderException inner) {
				DecoderException exception = new DecoderException("Unable to decode named '" + this.name + "' from '" + map + "'", this.codec, inner);
				exception.addSuppressed(e);
				throw exception;
			}
		}
	}
	
	/**
	 * Decodes the value using the given provider and map.<br>
	 * This method will try to decode the value by the first alias which is present in the map.<br>
	 * If no alias is present, an error will be returned.<br>
	 *
	 * @param provider The type provider
	 * @param map The map to decode the value from
	 * @return The decoded value
	 * @param <R> The type to decode from
	 * @throws NullPointerException If the provider, map or error is null
	 */
	private <R> @NonNull C decodeWithAlias(@NonNull TypeProvider<R> provider, @NonNull R map) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(map, "Map must not be null");
		
		String name = this.getDecodeName(provider, map);
		R value = provider.get(map, name);
		return this.codec.decode(provider, map, value);
	}
	
	/**
	 * Gets the first alias which is present in the given map or an error.<br>
	 *
	 * @param provider The type provider
	 * @param map The map to decode the value from
	 * @return The alias name
	 * @param <R> The type to decode from
	 * @throws NullPointerException If the provider or map is null
	 * @throws DecoderException If an error occurs during alias checking
	 */
	private <R> @NonNull String getDecodeName(@NonNull TypeProvider<R> provider, @NonNull R map) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(map, "Map must not be null");
		if (this.aliases.isEmpty()) {
			throw new DecoderException("Name '" + this.name + "' not found in '" + map + "', no aliases configured", this.codec);
		}
		
		return this.aliases.stream().filter(alias -> {
			return provider.has(map, alias);
		}).findFirst().orElseThrow(() -> new DecoderException("Name and aliases '" + this.name + "' and '" + this.aliases + "' not found in '" + map + "'", this.codec));
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FieldCodec<?, ?> that)) return false;
		
		if (!this.codec.equals(that.codec)) return false;
		if (!this.name.equals(that.name)) return false;
		return this.aliases.equals(that.aliases);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec, this.name, this.aliases, this.getter);
	}
	
	@Override
	public String toString() {
		if (this.aliases.isEmpty()) {
			return "NamedCodec['" + this.name + "', " + this.codec + "]";
		}
		return "NamedCodec['" + this.name + "', " + this.aliases + ", " + this.codec + "]";
	}
	//endregion
}
