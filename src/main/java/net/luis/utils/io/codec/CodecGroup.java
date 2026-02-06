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

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A codec that groups multiple codecs together to encode and decode a single object.<br>
 * This codec uses a factory function to create the final object from the encoded components.<br>
 * It is used to combine multiple codecs into one, allowing for complex objects to be encoded and decoded easily.<br>
 *
 * @see CodecBuilder
 * @see CodecCreator
 *
 * @author Luis-St
 *
 * @param <O> The type of the object that this codec encodes and decodes
 */
public final class CodecGroup<O> implements Codec<O> {
	
	/**
	 * The list of codecs that are grouped together in this codec.<br>
	 * Each codec is responsible for encoding and decoding a specific component of the final object.<br>
	 */
	private final List<FieldCodec<?, O>> codecs;
	/**
	 * The factory function that creates the final object from the encoded components.<br>
	 * This function takes a list of decoded components and returns the final object.<br>
	 */
	private final ThrowableFunction<List<Object>, O, DecoderException> factory;
	
	/**
	 * Constructs a new codec group using the given list of codecs and factory function.<br>
	 * This constructor is intended for internal use only and should not be used directly.<br>
	 *
	 * @param codecs The list of codecs to group together
	 * @param factory The factory function to create the final object from the encoded components
	 * @throws NullPointerException If the codecs list or factory function is null
	 */
	public CodecGroup(@NonNull List<FieldCodec<?, O>> codecs, @NonNull ThrowableFunction<List<Object>, O, DecoderException> factory) {
		this.codecs = Objects.requireNonNull(codecs, "Codecs list must not be null");
		this.factory = Objects.requireNonNull(factory, "Factory function must not be null");
		
		for (int i = 0; i < this.codecs.size(); i++) {
			Objects.requireNonNull(this.codecs.get(i), "Codec of component " + i + " must not be null");
		}
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable O value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null value", this);
		}
		
		R map = provider.merge(current, provider.createMap());
		for (FieldCodec<?, O> codec : this.codecs) {
			try {
				codec.encode(provider, map, value);
			} catch (EncoderException e) {
				throw new EncoderException("Unable to encode component of '" + value + "': " + e.getMessage(), this, e);
			}
		}
		return map;
	}
	
	@Override
	public <R> @NonNull O decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value", this);
		}
		
		provider.getMap(value); // Validate that value is a map
		
		List<Object> components = new ArrayList<>(this.codecs.size());
		for (FieldCodec<?, O> codec : this.codecs) {
			try {
				Object decoded = codec.decode(provider, value, value);
				components.add(decoded);
			} catch (DecoderException e) {
				throw new DecoderException("Unable to decode component of '" + value + "': " + e.getMessage(), this, e);
			}
		}
		return this.factory.apply(components);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CodecGroup<?> that)) return false;
		
		if (!this.codecs.equals(that.codecs)) return false;
		return this.factory.equals(that.factory);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codecs, this.factory);
	}
	
	@Override
	public String toString() {
		return this.codecs.stream().map(FieldCodec::toString).collect(Collectors.joining(", ", "GroupCodec[", "]"));
	}
	//endregion
}
