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

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.constraint.config.io.InetAddressConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.InetAddressConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;
import java.util.function.UnaryOperator;

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
public class InetAddressCodec extends AbstractCodec<InetAddress, InetAddressConstraintConfig> implements InetAddressConstraint<InetAddressCodec> {
	
	/**
	 * Constructs a new network address codec.<br>
	 */
	public InetAddressCodec() {}
	
	/**
	 * Constructs a new network address codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private InetAddressCodec(@NonNull InetAddressConstraintConfig config) {
		super(config);
	}
	
	@Override
	public @NonNull InetAddressCodec apply(@NonNull UnaryOperator<InetAddressConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new InetAddressCodec(
			configModifier.apply(this.getConstraintConfig().orElse(InetAddressConstraintConfig.UNCONSTRAINED))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable InetAddress value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as network address using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createString(value.getHostAddress());
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull InetAddress key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Result<Void> constraintResult = this.checkConstraints(key);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(key.getHostAddress());
	}
	
	@Override
	public <R> @NonNull Result<InetAddress> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as network address using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			InetAddress address = InetAddress.getByName(string);
			
			Result<Void> constraintResult = this.checkConstraints(address);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(address);
		} catch (IOException e) {
			return Result.error("Unable to decode network address '" + string + "' using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public @NonNull Result<InetAddress> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			InetAddress address = InetAddress.getByName(key);
			
			Result<Void> constraintResult = this.checkConstraints(address);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(address);
		} catch (IOException e) {
			return Result.error("Unable to decode key '" + key + "' as network address using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedInetAddressCodec[constraints=" + config + "]";
		}).orElse("InetAddressCodec");
	}
}
