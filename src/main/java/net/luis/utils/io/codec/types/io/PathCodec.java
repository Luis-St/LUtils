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
import net.luis.utils.io.codec.constraint.config.io.FilePathConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.FilePathConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Internal codec implementation for file system paths.<br>
 * Uses the path string as an internal representation.<br>
 * <p>
 *     Supports file path constraints for validation.
 * </p>
 *
 * @author Luis-St
 */
public class PathCodec
	extends AbstractConstrainableCodec<Path, FilePathConstraintConfig, PathCodec>
	implements FilePathConstraint<Path, PathCodec> {
	
	/**
	 * Constructs a new path codec.<br>
	 */
	public PathCodec() {
		super(PathCodec::new, FilePathConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new path codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private PathCodec(@NonNull FilePathConstraintConfig config) {
		super(PathCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Path value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as path", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).toString());
	}
	
	@Override
	public <R> @NonNull Path decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null as path", this);
		}
		
		String string = provider.getString(value);
		try {
			Path path = Path.of(string);
			return this.validateDecodeConstraints(path);
		} catch (Exception e) {
			throw new DecoderException("Unable to decode path '" + string + ": Unable to create path: " + e.getMessage(), this);
		}
	}
}
