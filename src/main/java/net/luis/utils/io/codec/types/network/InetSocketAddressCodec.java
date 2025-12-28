/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.codec.types.network;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * Internal codec implementation for inet socket addresses.<br>
 * Uses the format "address:port" as an internal representation.<br>
 * Supports IPv4, IPv6, and domain names.<br>
 *
 * @author Luis-St
 */
public class InetSocketAddressCodec extends AbstractCodec<InetSocketAddress, Object> {
	
	/**
	 * Constructs a new inet socket address codec.<br>
	 */
	public InetSocketAddressCodec() {}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable InetSocketAddress value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as network socket address using '" + this + "'");
		}
		
		String address = value.getAddress().getHostAddress();
		int port = value.getPort();
		
		String formatted;
		if (address.contains(":")) {
			formatted = "[" + address + "]:" + port;
		} else {
			formatted = address + ":" + port;
		}
		return provider.createString(formatted);
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull InetSocketAddress key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		String address = key.getAddress().getHostAddress();
		int port = key.getPort();
		
		if (address.contains(":")) {
			return Result.success("[" + address + "]:" + port);
		}
		return Result.success(address + ":" + port);
	}
	
	@Override
	public <R> @NonNull Result<InetSocketAddress> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as network socket address using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		return this.parseSocketAddress(string);
	}
	
	@Override
	public @NonNull Result<InetSocketAddress> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.parseSocketAddress(key);
	}
	
	/**
	 * Parses a socket address from a string.<br>
	 * Supports formats: "address:port" and "[ipv6]:port"<br>
	 *
	 * @param string The string to parse
	 * @return The result containing the socket address or an error
	 */
	private @NonNull Result<InetSocketAddress> parseSocketAddress(@NonNull String string) {
		try {
			String address;
			int port;
			
			if (string.startsWith("[")) {
				int closeBracket = string.indexOf(']');
				if (closeBracket == -1 || closeBracket + 2 >= string.length() || string.charAt(closeBracket + 1) != ':') {
					return Result.error("Invalid IPv6 socket address format '" + string + "', expected '[ipv6]:port'");
				}
				
				address = string.substring(1, closeBracket);
				port = Integer.parseInt(string.substring(closeBracket + 2));
			} else {
				int lastColon = string.lastIndexOf(':');
				if (lastColon == -1) {
					return Result.error("Invalid network socket address format '" + string + "', expected 'address:port'");
				}
				
				address = string.substring(0, lastColon);
				port = Integer.parseInt(string.substring(lastColon + 1));
			}
			
			if (port < 0 || port > 65535) {
				return Result.error("Port number out of range: " + port);
			}
			
			InetAddress inetAddress = InetAddress.getByName(address);
			return Result.success(new InetSocketAddress(inetAddress, port));
		} catch (IOException e) {
			return Result.error("Unable to decode network socket address '" + string + "' using '" + this + "': " + e.getMessage());
		} catch (NumberFormatException e) {
			return Result.error("Invalid port number in '" + string + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "InetSocketAddressCodec";
	}
}
