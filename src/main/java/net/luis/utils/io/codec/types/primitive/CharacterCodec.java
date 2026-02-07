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

package net.luis.utils.io.codec.types.primitive;

import net.luis.utils.io.codec.AbstractConstrainableCodec;
import net.luis.utils.io.codec.constraint.config.CharacterConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.CharacterConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Internal codec implementation for characters.<br>
 *
 * @author Luis-St
 */
public class CharacterCodec
	extends AbstractConstrainableCodec<Character, CharacterConstraintConfig, CharacterCodec>
	implements CharacterConstraint<CharacterCodec> {
	
	/**
	 * Constructs a new character codec.<br>
	 */
	public CharacterCodec() {
		super(CharacterCodec::new, CharacterConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new character codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private CharacterCodec(@NonNull CharacterConstraintConfig config) {
		super(CharacterCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Character value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as character", this);
		}
		
		this.validateEncodeConstraints(value);
		return provider.createString(String.valueOf(value), EncoderException::new);
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull Character key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return String.valueOf(this.validateEncodeConstraints(key));
	}
	
	@Override
	public <R> @NonNull Character decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null as character", this);
		}
		
		String string = provider.getString(value, DecoderException::new);
		if (string.length() != 1) {
			throw new DecoderException("Unable to decode value '" + string + "' as character: String must have exactly one character to decode as character", this);
		}
		return this.validateDecodeConstraints(string.charAt(0));
	}
	
	@Override
	public @NonNull Character decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		if (key.length() != 1) {
			throw new DecoderException("Unable to decode key '" + key + "' as character: Key must have exactly one character to decode as character", this);
		}
		
		return this.validateDecodeConstraints(key.charAt(0));
	}
}
