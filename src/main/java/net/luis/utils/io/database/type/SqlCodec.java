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
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.database.type.value.SqlValue;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public sealed interface SqlCodec<C> extends Codec<C> permits WrappedSqlCodec, DefaultSqlCodec {
	
	@NonNull String name();
	
	@Override
	@SuppressWarnings("unchecked")
	default <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable C value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null value as " + this.name());
		}
		
		if (provider instanceof SqlTypeProvider sqlProvider) {
			return (R) this.encodeSql(sqlProvider, value);
		}
		throw new EncoderException(StringUtils.capitalize(this.name()) + " codec requires " + SqlTypeProvider.class.getName() + ", got " + provider.getClass().getName());
	}
	
	@NonNull SqlValue encodeSql(@NonNull SqlTypeProvider provider, @NonNull C value) throws EncoderException;
	
	@Override
	default <R> @NonNull C decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as " + this.name());
		}
		
		if (!(value instanceof SqlValue sqlValue)) {
			throw new DecoderException("Unable to decode value of type " + value.getClass().getName() + " as " + this.name() + " codec requires " + SqlValue.class.getName());
		}
		if (provider instanceof SqlTypeProvider sqlProvider) {
			return this.decodeSql(sqlProvider, sqlValue);
		}
		throw new DecoderException(StringUtils.capitalize(this.name()) + " codec requires " + SqlTypeProvider.class.getName() + ", got " + provider.getClass().getName());
	}
	
	@NonNull C decodeSql(@NonNull SqlTypeProvider provider, @NonNull SqlValue value) throws DecoderException;
}
