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
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * Internal codec implementation for double streams.<br>
 * Uses a stream of boxed doubles as an internal representation.<br>
 *
 * @author Luis-St
 */
public class DoubleStreamCodec extends AbstractCodec<DoubleStream> {
	
	/**
	 * The internal codec that handles the conversion between a stream of boxed doubles and the unboxed version.<br>
	 */
	private final Codec<Stream<Double>> internalCodec = Codecs.DOUBLE.stream();
	
	/**
	 * Constructs a new double stream codec.<br>
	 */
	public DoubleStreamCodec() {}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable DoubleStream value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as double stream", this);
		}
		
		return this.internalCodec.encode(provider, current, value.boxed());
	}
	
	@Override
	public <R> @NonNull DoubleStream decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as double stream", this);
		}
		
		return this.internalCodec.decode(provider, current, value).mapToDouble(Double::doubleValue);
	}
	
	@Override
	public String toString() {
		return "DoubleStreamCodec";
	}
}
