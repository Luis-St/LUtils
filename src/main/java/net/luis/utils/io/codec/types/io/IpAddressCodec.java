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
import net.luis.utils.io.codec.constraint.config.io.IpAddressConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.IpAddressConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

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
public class IpAddressCodec extends AbstractCodec<IpAddress<?>, IpAddressConstraintConfig> implements IpAddressConstraint<IpAddressCodec> {
	
	/**
	 * Constructs a new IP address codec.<br>
	 */
	public IpAddressCodec() {}
	
	/**
	 * Constructs a new IP address codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private IpAddressCodec(@NonNull IpAddressConstraintConfig config) {
		super(config);
	}
	
	@Override
	public @NonNull IpAddressCodec apply(@NonNull UnaryOperator<IpAddressConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new IpAddressCodec(
			configModifier.apply(this.getConstraintConfig().orElse(IpAddressConstraintConfig.UNCONSTRAINED))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable IpAddress<?> value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as IP address using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createString(value.toString());
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull IpAddress<?> key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Result<Void> constraintResult = this.checkConstraints(key);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(key.toString());
	}
	
	@Override
	public <R> @NonNull Result<IpAddress<?>> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as IP address using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		Optional<? extends IpAddress<?>> optionalAddress = IpAddresses.tryParse(string);
		if (optionalAddress.isEmpty()) {
			return Result.error("Unable to decode IP address '" + string + "' using '" + this + "': Invalid format");
		}
		
		IpAddress<?> address = optionalAddress.get();
		Result<Void> constraintResult = this.checkConstraints(address);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(address);
	}
	
	@Override
	public @NonNull Result<IpAddress<?>> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Optional<? extends IpAddress<?>> optionalAddress = IpAddresses.tryParse(key);
		if (optionalAddress.isEmpty()) {
			return Result.error("Unable to decode key '" + key + "' as IP address using '" + this + "': Invalid format");
		}
		
		IpAddress<?> address = optionalAddress.get();
		Result<Void> constraintResult = this.checkConstraints(address);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(address);
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedIpAddressCodec[constraints=" + config + "]";
		}).orElse("IpAddressCodec");
	}
}
