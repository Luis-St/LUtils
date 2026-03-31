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

package net.luis.utils.io.codec.types.io;

import net.luis.utils.io.codec.AbstractConstrainableCodec;
import net.luis.utils.io.codec.constraint.config.io.InetAddressConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.InetAddressConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;

/**
 * Internal codec implementation for network addresses.<br>
 * Supports IPv4, IPv6, and domain names.<br>
 * Uses the string representation as an internal format.<br>
 * <p>
 *     Supports InetAddress constraints for validation.
 * </p>
 *
 * @author Luis-St
 */
public class InetAddressCodec
	extends AbstractConstrainableCodec<InetAddress, InetAddressConstraintConfig, InetAddressCodec>
	implements InetAddressConstraint<InetAddressCodec> {
	
	/**
	 * Constructs a new network address codec.<br>
	 */
	public InetAddressCodec() {
		super(InetAddressCodec::new, InetAddressConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new network address codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private InetAddressCodec(@NonNull InetAddressConstraintConfig config) {
		super(InetAddressCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable InetAddress value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as network address", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).getHostAddress(), EncoderException::new);
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull InetAddress key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key).getHostAddress();
	}
	
	@Override
	public <R> @NonNull InetAddress decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as network address", this);
		}
		
		String string = provider.getString(value, DecoderException::new);
		try {
			InetAddress address = InetAddress.getByName(string);
			return this.validateDecodeConstraints(address);
		} catch (IOException e) {
			throw new DecoderException("Unable to decode network address '" + string + ": " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull InetAddress decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			InetAddress address = InetAddress.getByName(key);
			return this.validateDecodeConstraints(address);
		} catch (IOException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as network address: " + e.getMessage(), this, e);
		}
	}
}
