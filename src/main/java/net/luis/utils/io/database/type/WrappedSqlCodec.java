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

package net.luis.utils.io.database.type;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.database.type.value.SqlValue;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

record WrappedSqlCodec<C>(
	@NonNull String name,
	@NonNull Codec<C> wrappedCodec
) implements SqlCodec<C> {
	
	WrappedSqlCodec {
		Objects.requireNonNull(name, "Codec name must not be null");
		Objects.requireNonNull(wrappedCodec, "Wrapped codec must not be null");
	}
	
	@Override
	public @NonNull SqlValue encodeSql(@NonNull SqlTypeProvider provider, @NonNull C value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(value, "Value to encode must not be null");
		
		return this.wrappedCodec.encode(provider, provider.empty(), value);
	}
	
	@Override
	public @NonNull C decodeSql(@NonNull SqlTypeProvider provider, @NonNull SqlValue value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(value, "Value to decode must not be null");
		
		return this.wrappedCodec.decode(provider, provider.empty(), value);
	}
}
