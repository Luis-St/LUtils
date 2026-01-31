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
import net.luis.utils.io.codec.constraint.config.io.IpNetworkConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.IpNetworkConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.io.network.address.IpNetwork;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

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
public class IpNetworkCodec extends AbstractCodec<IpNetwork<?, ?>, IpNetworkConstraintConfig> implements IpNetworkConstraint<IpNetworkCodec> {
	
	/**
	 * Constructs a new IP network codec.<br>
	 */
	public IpNetworkCodec() {}
	
	/**
	 * Constructs a new IP network codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private IpNetworkCodec(@NonNull IpNetworkConstraintConfig config) {
		super(config);
	}
	
	@Override
	public @NonNull IpNetworkCodec apply(@NonNull UnaryOperator<IpNetworkConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new IpNetworkCodec(
			configModifier.apply(this.getConstraintConfig().orElse(IpNetworkConstraintConfig.UNCONSTRAINED))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable IpNetwork<?, ?> value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as IP network using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createString(value.toCidrNotation());
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull IpNetwork<?, ?> key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Result<Void> constraintResult = this.checkConstraints(key);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(key.toCidrNotation());
	}
	
	@Override
	public <R> @NonNull Result<IpNetwork<?, ?>> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as IP network using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		Optional<? extends IpNetwork<?, ?>> optionalNetwork = IpAddresses.tryParseNetwork(string);
		if (optionalNetwork.isEmpty()) {
			return Result.error("Unable to decode IP network '" + string + "' using '" + this + "': Invalid CIDR format");
		}
		
		IpNetwork<?, ?> network = optionalNetwork.get();
		Result<Void> constraintResult = this.checkConstraints(network);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(network);
	}
	
	@Override
	public @NonNull Result<IpNetwork<?, ?>> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Optional<? extends IpNetwork<?, ?>> optionalNetwork = IpAddresses.tryParseNetwork(key);
		if (optionalNetwork.isEmpty()) {
			return Result.error("Unable to decode key '" + key + "' as IP network using '" + this + "': Invalid CIDR format");
		}
		
		IpNetwork<?, ?> network = optionalNetwork.get();
		Result<Void> constraintResult = this.checkConstraints(network);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(network);
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedIpNetworkCodec[constraints=" + config + "]";
		}).orElse("IpNetworkCodec");
	}
}
