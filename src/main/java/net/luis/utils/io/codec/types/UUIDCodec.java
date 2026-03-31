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

package net.luis.utils.io.codec.types;

import net.luis.utils.io.codec.AbstractConstrainableCodec;
import net.luis.utils.io.codec.constraint.config.UUIDConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.UUIDConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Internal codec implementation for UUIDs.<br>
 * Uses the standard string representation as an internal representation.<br>
 *
 * @author Luis-St
 */
public class UUIDCodec
	extends AbstractConstrainableCodec<UUID, UUIDConstraintConfig, UUIDCodec>
	implements UUIDConstraint<UUIDCodec> {
	
	/**
	 * Constructs a new UUID codec.<br>
	 */
	public UUIDCodec() {
		super(UUIDCodec::new, UUIDConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new UUID codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private UUIDCodec(@NonNull UUIDConstraintConfig config) {
		super(UUIDCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable UUID value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as uuid", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).toString(), EncoderException::new);
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull UUID key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key).toString();
	}
	
	@Override
	public <R> @NonNull UUID decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as uuid", this);
		}
		
		String string = provider.getString(value, DecoderException::new);
		try {
			UUID uuid = UUID.fromString(string);
			return this.validateDecodeConstraints(uuid);
		} catch (IllegalArgumentException e) {
			throw new DecoderException("Unable to decode uuid '" + string + ": Unable to parse UUID: " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull UUID decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			UUID uuid = UUID.fromString(key);
			return this.validateDecodeConstraints(uuid);
		} catch (IllegalArgumentException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as uuid: Unable to parse UUID: " + e.getMessage(), this, e);
		}
	}
}
