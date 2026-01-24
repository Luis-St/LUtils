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
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.constraint.config.io.FilePathConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.FilePathConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for file system paths.<br>
 * Uses the path string as an internal representation.<br>
 * <p>
 *     Supports file path constraints for validation.
 * </p>
 *
 * @author Luis-St
 */
public class PathCodec extends AbstractCodec<Path, FilePathConstraintConfig> implements FilePathConstraint<Path, PathCodec> {
	
	/**
	 * Constructs a new path codec.<br>
	 */
	public PathCodec() {}
	
	/**
	 * Constructs a new path codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private PathCodec(@NonNull FilePathConstraintConfig config) {
		super(config);
	}
	
	@Override
	public @NonNull PathCodec apply(@NonNull UnaryOperator<FilePathConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new PathCodec(
			configModifier.apply(this.getConstraintConfig().orElse(FilePathConstraintConfig.UNCONSTRAINED))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Path value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as path using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Codecs.STRING.encodeStart(provider, current, value.toString());
	}
	
	@Override
	public <R> @NonNull Result<Path> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as path using '" + this + "'");
		}
		
		Result<String> result = Codecs.STRING.decodeStart(provider, current, value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			Path path = Path.of(string);
			
			Result<Void> constraintResult = this.checkConstraints(path);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(path);
		} catch (Exception e) {
			return Result.error("Unable to decode path '" + string + "' using '" + this + "': Unable to create path: " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedPathCodec[constraints=" + config + "]";
		}).orElse("PathCodec");
	}
}
