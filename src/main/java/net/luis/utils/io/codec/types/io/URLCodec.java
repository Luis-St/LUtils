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

package net.luis.utils.io.codec.types.io;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.*;
import java.util.Objects;

/**
 * Internal codec implementation for URLs.<br>
 * Uses the URL string as an internal representation.<br>
 *
 * @author Luis-St
 */
public class URLCodec extends AbstractCodec<URL, Object> {
	
	/**
	 * Constructs a new URL codec.<br>
	 */
	public URLCodec() {}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable URL value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as URL using '" + this + "'");
		}
		
		try {
			return Codecs.URI.encodeStart(provider, current, value.toURI());
		} catch (URISyntaxException e) {
			return Result.error("Unable to convert URL to URI: " + e.getMessage());
		}
	}
	
	@Override
	public <R> @NotNull Result<URL> decodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as URL using '" + this + "'");
		}
		
		Result<URI> result = Codecs.URI.decodeStart(provider, current, value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		URI uri = result.resultOrThrow();
		try {
			return Result.success(uri.toURL());
		} catch (MalformedURLException | IllegalArgumentException e) {
			return Result.error("Unable to decode URL '" + uri + "' using '" + this + "': Unable to convert URI to URL: " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "URLCodec";
	}
}
