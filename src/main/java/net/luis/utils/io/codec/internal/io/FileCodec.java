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

package net.luis.utils.io.codec.internal.io;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.*;

import java.io.File;
import java.util.Objects;

/**
 * Internal codec implementation for files.<br>
 * Uses the file path as an internal representation.<br>
 *
 * @author Luis-St
 */
@ApiStatus.Internal
public class FileCodec implements Codec<File> {
	
	/**
	 * Constructs a new file codec.<br>
	 */
	public FileCodec() {}
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable File value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as file using '" + this + "'");
		}
		return Codecs.STRING.encodeStart(provider, current, value.getPath());
	}
	
	@Override
	public @NotNull <R> Result<File> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as file using '" + this + "'");
		}
		
		Result<String> result = Codecs.STRING.decodeStart(provider, value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			return Result.success(new File(string));
		} catch (Exception e) {
			return Result.error("Unable to decode file '" + string + "' using '" + this + "': Unable to create file: " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "FileCodec";
	}
}
