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

package net.luis.utils.io.codec.types.array;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.constraint.config.collection.PrimitiveArrayConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.collection.PrimitiveArrayConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Internal codec implementation for long arrays.<br>
 * Uses a list of longs as an internal representation.<br>
 *
 * @author Luis-St
 */
public class LongArrayCodec
	extends AbstractConstrainableCodec<long[], PrimitiveArrayConstraintConfig<long[]>, LongArrayCodec>
	implements PrimitiveArrayConstraint<long[], LongArrayCodec> {
	
	/**
	 * The internal codec that handles the conversion between a list of longs and the array representation.<br>
	 */
	private final Codec<List<Long>> internalCodec = Codecs.LONG.list();
	
	/**
	 * Constructs a new long array codec.<br>
	 */
	public LongArrayCodec() {
		super(LongArrayCodec::new, PrimitiveArrayConstraintConfig.longArray());
	}
	
	/**
	 * Constructs a new long array codec with the specified length constraint configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private LongArrayCodec(@NonNull PrimitiveArrayConstraintConfig<long[]> config) {
		super(LongArrayCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, long @Nullable [] value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as long array", this);
		}
		
		this.validateEncodeConstraints(value);
		return this.internalCodec.encode(provider, current, Arrays.asList(ArrayUtils.toObject(value)));
	}
	
	@Override
	public <R> long @NonNull [] decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as long array", this);
		}
		
		List<Long> list = this.internalCodec.decode(provider, current, value);
		long[] array = ArrayUtils.toPrimitive(list.toArray(Long[]::new));
		return this.validateDecodeConstraints(array);
	}
}
