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
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.database.type.value.SqlValue;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public record SqlCodec<C>(
	@NonNull String name,
	@NonNull ThrowableTriFunction<SqlTypeProvider, SqlValue, Function<String, DecoderException>, C, DecoderException> getter
) implements Codec<C> {
	
	public SqlCodec {
		Objects.requireNonNull(name, "Codec name must not be null");
		Objects.requireNonNull(getter, "Codec getter function must not be null");
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable C value) throws EncoderException {
		throw new EncoderException(StringUtils.capitalize(this.name) + " codec is read-only and does not support encoding");
	}
	
	@Override
	public <R> @NonNull C decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as " + this.name);
		}
		
		if (!(value instanceof SqlValue sqlValue)) {
			throw new DecoderException("Unable to decode value of type " + value.getClass().getSimpleName() + " as " + this.name + " codec requires " + SqlValue.class.getSimpleName());
		}
		if (provider instanceof SqlTypeProvider sqlProvider) {
			return this.getter.apply(sqlProvider, sqlValue, DecoderException::new);
		}
		throw new DecoderException(StringUtils.capitalize(this.name) + " codec requires " + SqlTypeProvider.class.getSimpleName() + ", got " + provider.getClass().getSimpleName());
	}
}
