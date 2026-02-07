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
import net.luis.utils.io.codec.constraint.config.io.MacAddressConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.MacAddressConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.network.address.exception.IpParseException;
import net.luis.utils.io.network.address.mac.MacAddress;
import net.luis.utils.io.network.address.mac.MacAddresses;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Internal codec implementation for mac addresses.<br>
 * Uses the colon-delimited string representation as the default format (e.g., "00:1a:2b:3c:4d:5e").<br>
 * <p>
 *     Supports MacAddress constraints for validation.
 * </p>
 *
 * @author Luis-St
 */
public class MacAddressCodec
	extends AbstractConstrainableCodec<MacAddress, MacAddressConstraintConfig, MacAddressCodec>
	implements MacAddressConstraint<MacAddressCodec> {
	
	/**
	 * Constructs a new mac address codec.<br>
	 */
	public MacAddressCodec() {
		super(MacAddressCodec::new, MacAddressConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new mac address codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private MacAddressCodec(@NonNull MacAddressConstraintConfig config) {
		super(MacAddressCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable MacAddress value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as mac address", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).toColonString(), EncoderException::new);
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull MacAddress key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key).toColonString();
	}
	
	@Override
	public <R> @NonNull MacAddress decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as mac address", this);
		}
		
		String string = provider.getString(value, DecoderException::new);
		try {
			MacAddress address = MacAddresses.parse(string);
			return this.validateDecodeConstraints(address);
		} catch (IpParseException e) {
			throw new DecoderException("Unable to decode mac address '" + string + "': " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull MacAddress decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			MacAddress address = MacAddresses.parse(key);
			return this.validateDecodeConstraints(address);
		} catch (IpParseException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as mac address: " + e.getMessage(), this, e);
		}
	}
}
