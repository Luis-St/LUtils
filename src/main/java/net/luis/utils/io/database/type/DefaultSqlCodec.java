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

import net.luis.utils.function.throwable.ThrowableTriFunction;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.database.type.value.SqlValue;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

record DefaultSqlCodec<C>(
	@NonNull String name,
	@NonNull ThrowableTriFunction<SqlTypeProvider, SqlValue, Function<String, DecoderException>, C, DecoderException> getter,
	@NonNull ThrowableTriFunction<SqlTypeProvider, C, Function<String, EncoderException>, SqlValue, EncoderException> setter
) implements SqlCodec<C> {
	
	DefaultSqlCodec {
		Objects.requireNonNull(name, "Codec name must not be null");
		Objects.requireNonNull(getter, "Codec getter function must not be null");
		Objects.requireNonNull(setter, "Codec setter function must not be null");
	}
	
	@Override
	public @NonNull SqlValue encodeSql(@NonNull SqlTypeProvider provider, @NonNull C value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(value, "Value to encode must not be null");
		
		
		return this.setter.apply(provider, value, EncoderException::new);
	}
	
	@Override
	public @NonNull C decodeSql(@NonNull SqlTypeProvider provider, @NonNull SqlValue value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(value, "Value to decode must not be null");
		
		
		return this.getter.apply(provider, value, DecoderException::new);
	}
}
