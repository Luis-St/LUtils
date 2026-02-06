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
import net.luis.utils.io.codec.constraint.config.StringConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.StringConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Internal codec implementation for strings.<br>
 *
 * @author Luis-St
 */
public class StringCodec
	extends AbstractConstrainableCodec<String, StringConstraintConfig, StringCodec>
	implements StringConstraint<StringCodec> {
	
	/**
	 * Constructs a new string codec.<br>
	 */
	public StringCodec() {
		super(StringCodec::new, StringConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new string codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private StringCodec(@NonNull StringConstraintConfig config) {
		super(StringCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable String value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as string", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value));
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull String key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key);
	}
	
	@Override
	public <R> @NonNull String decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null as string", this);
		}
		
		return this.validateDecodeConstraints(provider.getString(value));
	}
	
	@Override
	public @NonNull String decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateDecodeConstraints(key);
	}
}
