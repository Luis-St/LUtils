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
import net.luis.utils.io.codec.constraint.config.io.URIConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.URIConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Internal codec implementation for URIs.<br>
 * Uses the URI string as an internal representation.<br>
 * <p>
 *     Supports URI constraints for validation.
 * </p>
 *
 * @author Luis-St
 */
public class URICodec
	extends AbstractConstrainableCodec<URI, URIConstraintConfig, URICodec>
	implements URIConstraint<URICodec> {
	
	/**
	 * Constructs a new URI codec.<br>
	 */
	public URICodec() {
		super(URICodec::new, URIConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new URI codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private URICodec(@NonNull URIConstraintConfig config) {
		super(URICodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable URI value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as uri", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).toString(), EncoderException::new);
	}
	
	@Override
	public <R> @NonNull URI decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null as uri", this);
		}
		
		String string = provider.getString(value, DecoderException::new);
		try {
			URI uri = new URI(string);
			return this.validateDecodeConstraints(uri);
		} catch (URISyntaxException e) {
			throw new DecoderException("Unable to decode uri '" + string + "': Unable to parse uri: " + e.getMessage(), this);
		}
	}
}
