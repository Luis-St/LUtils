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
import net.luis.utils.io.codec.constraint.config.io.IpNetworkConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.IpNetworkConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.io.network.address.IpNetwork;
import net.luis.utils.io.network.address.exception.IpParseException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Internal codec implementation for IP networks.<br>
 * Supports both IPv4 and IPv6 networks using the {@link IpNetwork} interface.<br>
 * Uses the CIDR notation string representation as an internal format.<br>
 * <p>
 *     Supports IpNetwork constraints for validation.
 * </p>
 *
 * @author Luis-St
 */
public class IpNetworkCodec
	extends AbstractConstrainableCodec<IpNetwork<?, ?>, IpNetworkConstraintConfig, IpNetworkCodec>
	implements IpNetworkConstraint<IpNetworkCodec> {
	
	/**
	 * Constructs a new IP network codec.<br>
	 */
	public IpNetworkCodec() {
		super(IpNetworkCodec::new, IpNetworkConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new IP network codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private IpNetworkCodec(@NonNull IpNetworkConstraintConfig config) {
		super(IpNetworkCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable IpNetwork<?, ?> value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as ip network", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).toCidrNotation(), EncoderException::new);
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull IpNetwork<?, ?> key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key).toCidrNotation();
	}
	
	@Override
	public <R> @NonNull IpNetwork<?, ?> decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as ip network", this);
		}
		
		String string = provider.getString(value, DecoderException::new);
		try {
			IpNetwork<?, ?> network = IpAddresses.parseNetwork(string);
			return this.validateDecodeConstraints(network);
		} catch (IpParseException e) {
			throw new DecoderException("Unable to decode ip network '" + string + "': " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull IpNetwork<?, ?> decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			IpNetwork<?, ?> network = IpAddresses.parseNetwork(key);
			return this.validateDecodeConstraints(network);
		} catch (IpParseException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as ip network: " + e.getMessage(), this, e);
		}
	}
}
