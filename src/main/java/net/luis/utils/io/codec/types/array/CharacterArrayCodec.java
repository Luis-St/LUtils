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
 * Internal codec implementation for character arrays.<br>
 * Uses a list of characters as an internal representation.<br>
 *
 * @author Luis-St
 */
public class CharacterArrayCodec
	extends AbstractConstrainableCodec<char[], PrimitiveArrayConstraintConfig<char[]>, CharacterArrayCodec>
	implements PrimitiveArrayConstraint<char[], CharacterArrayCodec> {
	
	/**
	 * The internal codec that handles the conversion between a list of characters and the array representation.<br>
	 */
	private final Codec<List<Character>> internalCodec = Codecs.CHARACTER.list();
	
	/**
	 * Constructs a new character array codec.<br>
	 */
	public CharacterArrayCodec() {
		super(CharacterArrayCodec::new, PrimitiveArrayConstraintConfig.charArray());
	}
	
	/**
	 * Constructs a new character array codec with the specified length constraint configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private CharacterArrayCodec(@NonNull PrimitiveArrayConstraintConfig<char[]> config) {
		super(CharacterArrayCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, char @Nullable [] value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as char array", this);
		}
		
		this.validateEncodeConstraints(value);
		return this.internalCodec.encode(provider, current, Arrays.asList(ArrayUtils.toObject(value)));
	}
	
	@Override
	public <R> char @NonNull [] decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as char array", this);
		}
		
		List<Character> list = this.internalCodec.decode(provider, current, value);
		char[] array = ArrayUtils.toPrimitive(list.toArray(Character[]::new));
		return this.validateDecodeConstraints(array);
	}
}
