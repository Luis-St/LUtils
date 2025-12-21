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
import java.util.Objects;

/**
 * Internal codec implementation for inet addresses.<br>
 * Supports IPv4, IPv6, and domain names.<br>
 * Uses the string representation as an internal format.<br>
 *
 * @author Luis-St
 */
public class InetAddressCodec extends AbstractCodec<InetAddress, Object> {
	
	/**
	 * Constructs a new inet address codec.<br>
	 */
	public InetAddressCodec() {}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable InetAddress value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as inet address using '" + this + "'");
		}
		return provider.createString(value.getHostAddress());
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull InetAddress key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key.getHostAddress());
	}
	
	@Override
	public <R> @NonNull Result<InetAddress> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as inet address using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			return Result.success(InetAddress.getByName(string));
		} catch (IOException e) {
			return Result.error("Unable to decode inet address '" + string + "' using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public @NonNull Result<InetAddress> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Result.success(InetAddress.getByName(key));
		} catch (IOException e) {
			return Result.error("Unable to decode key '" + key + "' as inet address using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "InetAddressCodec";
	}
}
