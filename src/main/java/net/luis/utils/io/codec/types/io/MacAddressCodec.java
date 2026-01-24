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
import net.luis.utils.io.codec.constraint.config.io.MacAddressConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.MacAddressConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.network.address.mac.MacAddress;
import net.luis.utils.io.network.address.mac.MacAddresses;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for MAC addresses.<br>
 * Uses the colon-delimited string representation as the default format (e.g., "00:1a:2b:3c:4d:5e").<br>
 * <p>
 *     Supports MacAddress constraints for validation.
 * </p>
 *
 * @author Luis-St
 */
public class MacAddressCodec extends AbstractCodec<MacAddress, MacAddressConstraintConfig> implements MacAddressConstraint<MacAddressCodec> {
	
	/**
	 * Constructs a new MAC address codec.<br>
	 */
	public MacAddressCodec() {}
	
	/**
	 * Constructs a new MAC address codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private MacAddressCodec(@NonNull MacAddressConstraintConfig config) {
		super(config);
	}
	
	@Override
	public @NonNull MacAddressCodec apply(@NonNull UnaryOperator<MacAddressConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new MacAddressCodec(
			configModifier.apply(this.getConstraintConfig().orElse(MacAddressConstraintConfig.UNCONSTRAINED))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable MacAddress value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as MAC address using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createString(value.toColonString());
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull MacAddress key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Result<Void> constraintResult = this.checkConstraints(key);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(key.toColonString());
	}
	
	@Override
	public <R> @NonNull Result<MacAddress> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as MAC address using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		Optional<MacAddress> optionalAddress = MacAddresses.tryParse(string);
		if (optionalAddress.isEmpty()) {
			return Result.error("Unable to decode MAC address '" + string + "' using '" + this + "': Invalid format");
		}
		
		MacAddress address = optionalAddress.get();
		Result<Void> constraintResult = this.checkConstraints(address);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(address);
	}
	
	@Override
	public @NonNull Result<MacAddress> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Optional<MacAddress> optionalAddress = MacAddresses.tryParse(key);
		if (optionalAddress.isEmpty()) {
			return Result.error("Unable to decode key '" + key + "' as MAC address using '" + this + "': Invalid format");
		}
		
		MacAddress address = optionalAddress.get();
		Result<Void> constraintResult = this.checkConstraints(address);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(address);
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedMacAddressCodec[constraints=" + config + "]";
		}).orElse("MacAddressCodec");
	}
}
