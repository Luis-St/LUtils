/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.codec.struct.*;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public interface Codec<C> extends Encoder<C>, Decoder<C> {
	
	Codec<Boolean> BOOLEAN = new Codec<>() {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Boolean value) {
			if (value == null) {
				return Result.error("Unable to encode null as boolean");
			}
			return provider.createBoolean(value);
		}
		
		@Override
		public @NotNull <R> Result<Boolean> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as boolean");
			}
			return provider.getBoolean(value);
		}
	};
	Codec<Byte> BYTE = new Codec<>() {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Byte value) {
			if (value == null) {
				return Result.error("Unable to encode null as byte");
			}
			return provider.createByte(value);
		}
		
		@Override
		public @NotNull <R> Result<Byte> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as byte");
			}
			return provider.getByte(value);
		}
	};
	Codec<Short> SHORT = new Codec<>() {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Short value) {
			if (value == null) {
				return Result.error("Unable to encode null as short");
			}
			return provider.createShort(value);
		}
		
		@Override
		public @NotNull <R> Result<Short> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as short");
			}
			return provider.getShort(value);
		}
	};
	Codec<Integer> INTEGER = new Codec<>() {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Integer value) {
			if (value == null) {
				return Result.error("Unable to encode null as integer");
			}
			return provider.createInteger(value);
		}
		
		@Override
		public @NotNull <R> Result<Integer> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as integer");
			}
			return provider.getInteger(value);
		}
	};
	Codec<Long> LONG = new Codec<>() {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Long value) {
			if (value == null) {
				return Result.error("Unable to encode null as long");
			}
			return provider.createLong(value);
		}
		
		@Override
		public @NotNull <R> Result<Long> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as long");
			}
			return provider.getLong(value);
		}
	};
	Codec<Float> FLOAT = new Codec<>() {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Float value) {
			if (value == null) {
				return Result.error("Unable to encode null as float");
			}
			return provider.createFloat(value);
		}
		
		@Override
		public @NotNull <R> Result<Float> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as float");
			}
			return provider.getFloat(value);
		}
	};
	Codec<Double> DOUBLE = new Codec<>() {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Double value) {
			if (value == null) {
				return Result.error("Unable to encode null as double");
			}
			return provider.createDouble(value);
		}
		
		@Override
		public @NotNull <R> Result<Double> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as double");
			}
			return provider.getDouble(value);
		}
	};
	Codec<String> STRING = new Codec<>() {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable String value) {
			if (value == null) {
				return Result.error("Unable to encode null as string");
			}
			return provider.createString(value);
		}
		
		@Override
		public @NotNull <R> Result<String> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as string");
			}
			return provider.getString(value);
		}
	};
	
	//region Factories
	static <C> @NotNull Codec<List<C>> list(@NotNull Codec<C> codec) {
		return new ListCodec<>(codec);
	}
	
	static <C> @NotNull Codec<List<C>> list(@NotNull Codec<C> codec, int minSize, int maxSize) {
		return new ListCodec<>(codec, minSize, maxSize);
	}
	
	static <C> @NotNull Codec<Optional<C>> optional(@NotNull Codec<C> codec) {
		return new OptionalCodec<>(codec);
	}
	
	static <C> @NotNull MapCodec<C> map(@NotNull Codec<C> codec) {
		return new MapCodec<>(STRING, codec);
	}
	
	static <C> @NotNull MapCodec<C> map(@NotNull Codec<String> keyCodec, @NotNull Codec<C> valueCodec) {
		return new MapCodec<>(keyCodec, valueCodec);
	}
	
	static <F, S> @NotNull EitherCodec<F, S> either(@NotNull Codec<F> firstCodec, @NotNull Codec<S> secondCodec) {
		return new EitherCodec<>(firstCodec, secondCodec);
	}
	//endregion
	
	default @NotNull Codec<List<C>> list() {
		return list(this);
	}
	
	default @NotNull Codec<Optional<C>> optional() {
		return optional(this);
	}
	
	default <O> @NotNull ConfigurableCodec<C, O> bind(@NotNull CodecBuilder<O> builder) {
		return new ConfigurableCodec<>(builder, this);
	}
}
