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

package net.luis.utils.io.codec.types.stream;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Internal codec implementation for long streams.<br>
 * Uses a stream of boxed longs as an internal representation.<br>
 *
 * @author Luis-St
 */
public class LongStreamCodec extends AbstractCodec<LongStream> {
	
	/**
	 * The internal codec that handles the conversion between a stream of boxed longs and the unboxed version.<br>
	 */
	private final Codec<Stream<Long>> internalCodec = Codecs.LONG.stream();
	
	/**
	 * Constructs a new long stream codec.<br>
	 */
	public LongStreamCodec() {}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable LongStream value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as long stream", this);
		}
		
		return this.internalCodec.encode(provider, current, value.boxed());
	}
	
	@Override
	public <R> @NonNull LongStream decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as long stream", this);
		}
		
		return this.internalCodec.decode(provider, current, value).mapToLong(Long::longValue);
	}
	
	@Override
	public String toString() {
		return "LongStreamCodec";
	}
}
