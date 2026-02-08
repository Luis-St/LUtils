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
import net.luis.utils.io.codec.constraint.config.io.InetSocketAddressConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.InetSocketAddressConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.io.network.address.exception.IpParseException;
import net.luis.utils.io.network.address.format.IpParseOptions;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * Internal codec implementation for network socket addresses.<br>
 * Uses the format "address:port" as an internal representation.<br>
 * Supports IPv4, IPv6, and domain names.<br>
 * <p>
 *     Supports InetSocketAddress constraints for validation.
 * </p>
 *
 * @author Luis-St
 */
public class InetSocketAddressCodec
	extends AbstractConstrainableCodec<InetSocketAddress, InetSocketAddressConstraintConfig, InetSocketAddressCodec>
	implements InetSocketAddressConstraint<InetSocketAddressCodec> {
	
	/**
	 * Constructs a new network socket address codec.<br>
	 */
	public InetSocketAddressCodec() {
		super(InetSocketAddressCodec::new, InetSocketAddressConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new network socket address codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private InetSocketAddressCodec(@NonNull InetSocketAddressConstraintConfig config) {
		super(InetSocketAddressCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable InetSocketAddress value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as network socket address", this);
		}
		
		String address = this.validateEncodeConstraints(value).getAddress().getHostAddress();
		int port = value.getPort();
		
		String formatted;
		if (address.contains(":")) {
			formatted = "[" + address + "]:" + port;
		} else {
			formatted = address + ":" + port;
		}
		return provider.createString(formatted, EncoderException::new);
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull InetSocketAddress key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		String address = this.validateEncodeConstraints(key).getAddress().getHostAddress();
		int port = key.getPort();
		
		if (address.contains(":")) {
			return "[" + address + "]:" + port;
		}
		return address + ":" + port;
	}
	
	@Override
	public <R> @NonNull InetSocketAddress decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as network socket address", this);
		}
		
		try {
			InetSocketAddress socketAddress = this.parseSocketAddress(provider.getString(value, DecoderException::new));
			return this.validateDecodeConstraints(socketAddress);
		} catch (IpParseException e) {
			throw new DecoderException("Unable to decode value as network socket address: " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull InetSocketAddress decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			InetSocketAddress socketAddress = this.parseSocketAddress(key);
			return this.validateDecodeConstraints(socketAddress);
		} catch (IpParseException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as network socket address: " + e.getMessage(), this, e);
		}
	}
	
	/**
	 * Parses a socket address from a string.<br>
	 * Supports formats: "address:port" and "[ipv6]:port"<br>
	 *
	 * @param string The string to parse
	 * @return The socket address
	 * @throws NullPointerException If the string is null
	 * @throws DecoderException If the string could not be parsed
	 */
	private @NonNull InetSocketAddress parseSocketAddress(@NonNull String string) throws DecoderException {
		String addressPart;
		int port;
		try {
			if (string.startsWith("[")) {
				int closeBracket = string.indexOf(']');
				if (closeBracket == -1 || closeBracket + 2 >= string.length() || string.charAt(closeBracket + 1) != ':') {
					throw new DecoderException("Invalid IPv6 socket address format '" + string + "', expected '[ipv6]:port'", this);
				}
				
				addressPart = string.substring(1, closeBracket);
				port = Integer.parseInt(string.substring(closeBracket + 2));
			} else {
				int lastColon = string.lastIndexOf(':');
				if (lastColon == -1) {
					throw new DecoderException("Invalid network socket address format '" + string + "', expected 'ipv4:port'", this);
				}
				
				addressPart = string.substring(0, lastColon);
				port = Integer.parseInt(string.substring(lastColon + 1));
			}
		} catch (NumberFormatException e) {
			throw new DecoderException("Invalid port number in '" + string + "': " + e.getMessage(), this, e);
		}
		
		if (port < 0 || port > 65535) {
			throw new DecoderException("Port number out of range: " + port, this);
		}
		IpAddress<?> address = IpAddresses.parse(addressPart, IpParseOptions.LENIENT);
		return new InetSocketAddress(address.toInetAddress(), port);
	}
}
