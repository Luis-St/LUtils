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
import net.luis.utils.io.codec.constraint.config.io.IpAddressConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.IpAddressConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.io.network.address.exception.IpParseException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Internal codec implementation for IP addresses.<br>
 * Supports both IPv4 and IPv6 addresses using the {@link IpAddress} interface.<br>
 * Uses the string representation as an internal format.<br>
 * <p>
 *     Supports IpAddress constraints for validation.
 * </p>
 *
 * @author Luis-St
 */
public class IpAddressCodec
	extends AbstractConstrainableCodec<IpAddress<?>, IpAddressConstraintConfig, IpAddressCodec>
	implements IpAddressConstraint<IpAddressCodec> {
	
	/**
	 * Constructs a new IP address codec.<br>
	 */
	public IpAddressCodec() {
		super(IpAddressCodec::new, IpAddressConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new IP address codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private IpAddressCodec(@NonNull IpAddressConstraintConfig config) {
		super(IpAddressCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable IpAddress<?> value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as ip address", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).toString());
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull IpAddress<?> key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key).toString();
	}
	
	@Override
	public <R> @NonNull IpAddress<?> decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as ip address", this);
		}
		
		String string = provider.getString(value);
		try {
			IpAddress<?> address = IpAddresses.parse(string);
			return this.validateDecodeConstraints(address);
		} catch (IpParseException e) {
			throw new DecoderException("Unable to decode ip address '" + string + ": " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull IpAddress<?> decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			IpAddress<?> address = IpAddresses.parse(key);
			return this.validateDecodeConstraints(address);
		} catch (IpParseException e) {
			throw new DecoderException("Unable to decode ip address key '" + key + ": " + e.getMessage(), this, e);
		}
	}
}
