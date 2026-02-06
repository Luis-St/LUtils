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

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.constraint.config.collection.ArrayConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.collection.ArrayConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Either;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;

/**
 * A codec for encoding and decoding arrays of elements.<br>
 * This codec uses another codec to encode and decode the elements of the array.<br>
 * <p>
 *     Due to the nature of types and arrays in Java, primitives can not be used in generic types<br>
 *     therefore the primitive array codecs have be predefined in the {@link Codecs} class.
 * </p>
 * By nesting this class, you can create codecs for multidimensional arrays.<br>
 * <p>
 *     <strong>Note:</strong><br>
 *     The type ({@link ArrayCodec#type}) is required due to the nature of Java's type/array system.
 * </p>
 *
 * @param <C> The type of elements in the array
 *
 * @author Luis-St
 */
public class ArrayCodec<C>
	extends AbstractConstrainableCodec<C[], ArrayConstraintConfig<C>, ArrayCodec<C>>
	implements PartialCodec<ArrayCodec<C>>, ArrayConstraint<C, ArrayCodec<C>> {
	
	/**
	 * The type of the elements in the array.<br>
	 * <p>
	 *     If this is a nested array codec, this will be the type of the inner elements.<br>
	 *     For example, if this codec is a {@code Integer[][]}, the type will be {@code Integer[]}.
	 * </p>
	 */
	private final Class<C> type;
	/**
	 * The codec used to encode and decode the elements of the array.<br>
	 */
	private final Codec<C> codec;
	/**
	 * Whether this codec is partial.<br>
	 */
	private final boolean partial;
	
	/**
	 * Constructs a new array codec using the given codec for the elements.<br>
	 *
	 * @param type The type of the elements in the array
	 * @param codec The codec for the elements
	 * @throws NullPointerException If the type or codec is null
	 */
	public ArrayCodec(@NonNull Class<C> type, @NonNull Codec<C> codec) {
		this(type, codec, false, ArrayConstraintConfig.unconstrained());
	}
	
	/**
	 * Constructs a new array codec using the given codec for the elements and the specified length constraint configuration.<br>
	 *
	 * @param type The type of the elements in the array
	 * @param codec The codec for the elements
	 * @param config The constraint configuration
	 * @throws NullPointerException If the type, codec, or the config is null
	 */
	private ArrayCodec(@NonNull Class<C> type, @NonNull Codec<C> codec, @NonNull ArrayConstraintConfig<C> config) {
		this(type, codec, false, config);
	}
	
	/**
	 * Constructs a new array codec using the given codec for the elements, the given partial flag and the given constraint configuration.<br>
	 *
	 * @param type The type of the elements in the array
	 * @param codec The codec for the elements
	 * @param partial Whether this codec is partial
	 * @param config The constraint configuration
	 * @throws NullPointerException If the type, codec, or the config is null
	 */
	private ArrayCodec(@NonNull Class<C> type, @NonNull Codec<C> codec, boolean partial, @NonNull ArrayConstraintConfig<C> config) {
		super(newConfig -> new ArrayCodec<>(type, codec, partial, newConfig), config);
		this.type = Objects.requireNonNull(type, "Type must not be null");
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		this.partial = partial;
	}
	
	@Override
	public @NonNull ArrayCodec<C> partial() {
		return new ArrayCodec<>(this.type, this.codec, true, this.config);
	}
	
	@Override
	public @NonNull ArrayCodec<C> strict() {
		return new ArrayCodec<>(this.type, this.codec, false, this.config);
	}
	
	@Override
	public boolean isPartial() {
		return this.partial;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Class<C[]> getType() {
		return (Class<C[]>) Array.newInstance(this.type, 0).getClass();
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, C @Nullable [] value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null value as array", this);
		}
		
		List<Either<R, EncoderException>> partialElements = new ArrayList<>(value.length);
		for (C element : this.validateEncodeConstraints(value)) {
			try {
				R encodedElement = this.codec.encode(provider, current, element);
				
				if (!provider.isEmpty(encodedElement)) {
					partialElements.add(Either.left(encodedElement));
				}
			} catch (EncoderException e) {
				partialElements.add(Either.right(e));
			}
		}
		return provider.merge(current, provider.createList(this.encode(partialElements)));
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "DuplicatedCode" })
	public <R> C @NonNull [] decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as array", this);
		}
		
		List<Either<C, DecoderException>> partialElements = new ArrayList<>();
		for (R element : provider.getList(value)) {
			try {
				C decodedElement = this.codec.decode(provider, current, element);
				partialElements.add(Either.left(decodedElement));
			} catch (DecoderException e) {
				partialElements.add(Either.right(e));
			}
		}
		
		List<C> elements = this.decode(partialElements);
		Object array = Array.newInstance(this.type, elements.size());
		for (int i = 0; i < elements.size(); i++) {
			Array.set(array, i, this.type.cast(elements.get(i)));
		}
		return this.validateDecodeConstraints((C[]) array);
	}
}
