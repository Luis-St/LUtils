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
import net.luis.utils.io.codec.constraint.config.collection.SetConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.collection.SetConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Either;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Internal codec implementation for sets.<br>
 * Uses a list representation internally and converts to/from a set.<br>
 *
 * @param <C> The element type of the set
 *
 * @author Luis-St
 */
public class SetCodec<C>
	extends AbstractConstrainableCodec<Set<C>, SetConstraintConfig<C>, SetCodec<C>>
	implements PartialCodec<SetCodec<C>>, SetConstraint<C, SetCodec<C>> {
	
	/**
	 * The codec used to encode and decode set elements.<br>
	 */
	private final Codec<C> codec;
	/**
	 * Whether this codec is partial.<br>
	 */
	private final boolean partial;
	
	/**
	 * Constructs a new set codec.<br>
	 *
	 * @param codec The codec for set elements
	 * @throws NullPointerException If the element codec is null
	 */
	public SetCodec(@NonNull Codec<C> codec) {
		this(codec, false, SetConstraintConfig.unconstrained());
	}
	
	/**
	 * Constructs a new set codec using the given codec for the elements and the given constraint configuration.<br>
	 *
	 * @param codec The codec for the elements
	 * @param config The constraint configuration
	 * @throws NullPointerException If the codec or the config is null
	 */
	private SetCodec(@NonNull Codec<C> codec, @NonNull SetConstraintConfig<C> config) {
		this(codec, false, config);
	}
	
	/**
	 * Constructs a new set codec using the given codec for the elements, the given partial flag and the given constraint configuration.<br>
	 *
	 * @param codec The codec for the elements
	 * @param partial Whether this codec is partial
	 * @param config The constraint configuration
	 * @throws NullPointerException If the codec or the config is null
	 */
	private SetCodec(@NonNull Codec<C> codec, boolean partial, @NonNull SetConstraintConfig<C> config) {
		super(newConfig -> new SetCodec<>(codec, newConfig), config);
		this.codec = Objects.requireNonNull(codec, "Element codec must not be null");
		this.partial = partial;
	}
	
	@Override
	public @NonNull SetCodec<C> partial() {
		return new SetCodec<>(this.codec, true, this.config);
	}
	
	@Override
	public @NonNull SetCodec<C> strict() {
		return new SetCodec<>(this.codec, false, this.config);
	}
	
	@Override
	public boolean isPartial() {
		return this.partial;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Class<Set<C>> getType() {
		return (Class<Set<C>>) (Class<?>) Set.class;
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Set<C> value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as set", this);
		}
		
		List<Either<R, EncoderException>> partialElements = new ArrayList<>(value.size());
		for (C element : this.validateEncodeConstraints(value)) {
			try {
				R encodedElement = this.codec.encode(provider, current, element);
				
				if (!provider.isEmpty(encodedElement, EncoderException::new)) {
					partialElements.add(Either.left(encodedElement));
				}
			} catch (EncoderException e) {
				partialElements.add(Either.right(e));
			}
		}
		return provider.merge(current, provider.createList(this.encode(partialElements), EncoderException::new), EncoderException::new);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <R> @NonNull Set<C> decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as set", this);
		}
		
		List<Either<C, DecoderException>> partialElements = new ArrayList<>();
		for (R element : provider.getList(value, DecoderException::new)) {
			try {
				C decodedElement = this.codec.decode(provider, current, element);
				partialElements.add(Either.left(decodedElement));
			} catch (DecoderException e) {
				partialElements.add(Either.right(e));
			}
		}
		return this.validateDecodeConstraints(new LinkedHashSet<>(this.decode(partialElements)));
	}
}
