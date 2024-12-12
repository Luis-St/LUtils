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

import net.luis.utils.io.codec.decoder.Decoder;
import net.luis.utils.io.codec.decoder.KeyableDecoder;
import net.luis.utils.io.codec.encoder.Encoder;
import net.luis.utils.io.codec.encoder.KeyableEncoder;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.codec.struct.*;
import net.luis.utils.util.Either;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
				return Result.error("Unable to encode null as boolean using '" + this + "'");
			}
			return provider.createBoolean(value);
		}
		
		@Override
		public @NotNull <R> Result<Boolean> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as boolean using '" + this + "'");
			}
			return provider.getBoolean(value);
		}
		
		@Override
		public String toString() {
			return "BooleanCodec";
		}
	};
	RangeCodec<Byte> BYTE = new RangeCodec<>(Byte.MIN_VALUE, Byte.MAX_VALUE, Number::byteValue, Byte::parseByte) {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Byte value) {
			if (value == null) {
				return Result.error("Unable to encode null as byte using '" + this + "'");
			}
			return provider.createByte(value);
		}
		
		@Override
		public @NotNull <R> Result<Byte> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as byte using '" + this + "'");
			}
			return provider.getByte(value);
		}
		
		@Override
		public String toString() {
			return "ByteCodec";
		}
	};
	RangeCodec<Short> SHORT = new RangeCodec<>(Short.MIN_VALUE, Short.MAX_VALUE, Number::shortValue, Short::parseShort) {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Short value) {
			if (value == null) {
				return Result.error("Unable to encode null as short using '" + this + "'");
			}
			return provider.createShort(value);
		}
		
		@Override
		public @NotNull <R> Result<Short> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as short using '" + this + "'");
			}
			return provider.getShort(value);
		}
		
		@Override
		public String toString() {
			return "ShortCodec";
		}
	};
	RangeCodec<Integer> INTEGER = new RangeCodec<>(Integer.MIN_VALUE, Integer.MAX_VALUE, Number::intValue, Integer::parseInt) {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Integer value) {
			if (value == null) {
				return Result.error("Unable to encode null as integer using '" + this + "'");
			}
			return provider.createInteger(value);
		}
		
		@Override
		public @NotNull <R> Result<Integer> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as integer using '" + this + "'");
			}
			return provider.getInteger(value);
		}
		
		@Override
		public String toString() {
			return "IntegerCodec";
		}
	};
	RangeCodec<Long> LONG = new RangeCodec<>(Long.MIN_VALUE, Long.MAX_VALUE, Number::longValue, Long::parseLong) {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Long value) {
			if (value == null) {
				return Result.error("Unable to encode null as long using '" + this + "'");
			}
			return provider.createLong(value);
		}
		
		@Override
		public @NotNull <R> Result<Long> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as long using '" + this + "'");
			}
			return provider.getLong(value);
		}
		
		@Override
		public String toString() {
			return "LongCodec";
		}
	};
	RangeCodec<Float> FLOAT = new RangeCodec<>(Float.MIN_VALUE, Float.MAX_VALUE, Number::floatValue, Float::parseFloat) {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Float value) {
			if (value == null) {
				return Result.error("Unable to encode null as float using '" + this + "'");
			}
			return provider.createFloat(value);
		}
		
		@Override
		public @NotNull <R> Result<Float> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as float using '" + this + "'");
			}
			return provider.getFloat(value);
		}
		
		@Override
		public String toString() {
			return "FloatCodec";
		}
	};
	RangeCodec<Double> DOUBLE = new RangeCodec<>(Double.MIN_VALUE, Double.MAX_VALUE, Number::doubleValue, Double::parseDouble) {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Double value) {
			if (value == null) {
				return Result.error("Unable to encode null as double using '" + this + "'");
			}
			return provider.createDouble(value);
		}
		
		@Override
		public @NotNull <R> Result<Double> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as double using '" + this + "'");
			}
			return provider.getDouble(value);
		}
		
		@Override
		public String toString() {
			return "Double Codec";
		}
	};
	KeyableCodec<String> STRING = new KeyableCodec<>() {
		private final KeyableEncoder<String> encoder = KeyableEncoder.of(this, Function.identity());
		private final KeyableDecoder<String> decoder = KeyableDecoder.of(this, Function.identity());
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable String value) {
			if (value == null) {
				return Result.error("Unable to encode null as string using '" + this + "'");
			}
			return provider.createString(value);
		}
		
		@Override
		public @NotNull <R> Result<String> encodeKey(@NotNull TypeProvider<R> provider, @NotNull String value) {
			return this.encoder.encodeKey(provider, value);
		}
		
		@Override
		public @NotNull <R> Result<String> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			if (value == null) {
				return Result.error("Unable to decode null value as string using '" + this + "'");
			}
			return provider.getString(value);
		}
		
		@Override
		public @NotNull <R> Result<String> decodeKey(@NotNull TypeProvider<R> provider, @NotNull String value) {
			return this.decoder.decodeKey(provider, value);
		}
		
		@Override
		public String toString() {
			return "StringCodec";
		}
	};
	
	//region Factories
	static <C> @NotNull KeyableCodec<C> keyable(@NotNull Codec<C> codec, @NotNull Function<String, @Nullable C> fromKey) {
		return keyable(codec, C::toString, fromKey);
	}
	
	static <C> @NotNull KeyableCodec<C> keyable(@NotNull Codec<C> codec, @NotNull Function<C, String> toKey, @NotNull Function<String, @Nullable C> fromKey) {
		return keyable(codec, KeyableEncoder.of(codec, toKey), KeyableDecoder.of(codec, fromKey));
	}
	
	static <C> @NotNull KeyableCodec<C> keyable(@NotNull Codec<C> codec, @NotNull KeyableEncoder<C> encoder, @NotNull KeyableDecoder<C> decoder) {
		return new KeyableCodec<>() {
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
				return encoder.encodeStart(provider, current, value);
			}
			
			@Override
			public <R> @NotNull Result<String> encodeKey(@NotNull TypeProvider<R> provider, @NotNull C value) {
				return encoder.encodeKey(provider, value);
			}
			
			@Override
			public <R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
				return decoder.decodeStart(provider, value);
			}
			
			@Override
			public <R> @NotNull Result<C> decodeKey(@NotNull TypeProvider<R> provider, @NotNull String value) {
				return decoder.decodeKey(provider, value);
			}
			
			@Override
			public String toString() {
				return "KeyableCodec[" + codec + "]";
			}
		};
	}
	
	static <E extends Enum<E>> @NotNull KeyableCodec<E> enumOrdinal(@NotNull Class<E> clazz) {
		Map<Integer, E> ordinalLookup = Arrays.stream(clazz.getEnumConstants()).collect(Collectors.toMap(Enum::ordinal, Function.identity()));
		return Codec.keyable(Codec.INTEGER.mapDirect(Enum::ordinal, ordinalLookup::get), constant -> String.valueOf(constant.ordinal()), str -> {
			try {
				return ordinalLookup.get(Integer.parseInt(str));
			} catch (NumberFormatException e) {
				return null;
			}
		});
	}
	
	static <E extends Enum<E>> @NotNull KeyableCodec<E> enumName(@NotNull Class<E> clazz) {
		Map<String, E> lookup = Arrays.stream(clazz.getEnumConstants()).collect(Collectors.toMap(Enum::name, Function.identity()));
		return Codec.keyable(Codec.STRING.mapDirect(Enum::name, lookup::get), Enum::name, lookup::get);
	}
	
	static <E extends Enum<E>> @NotNull KeyableCodec<E> dynamicEnum(@NotNull Class<E> clazz) {
		E[] constants = clazz.getEnumConstants();
		Map<Integer, E> ordinalLookup = Arrays.stream(constants).collect(Collectors.toMap(Enum::ordinal, Function.identity()));
		Map<String, E> nameLookup = Arrays.stream(constants).collect(Collectors.toMap(Enum::name, Function.identity()));
		return Codec.keyable(Codec.either(Codec.INTEGER, Codec.STRING).mapDirect(
			constant -> Either.right(constant.name()),
			either -> either.mapTo(ordinalLookup::get, nameLookup::get)
		), Enum::name, nameLookup::get);
	}
	
	static <E extends Enum<E>> @NotNull KeyableCodec<E> friendlyEnumName(@NotNull Class<E> clazz, @NotNull Function<E, String> toFriendly, @NotNull Function<String, E> fromFriendly) {
		return Codec.keyable(Codec.STRING.mapDirect(toFriendly, fromFriendly), toFriendly, fromFriendly);
	}
	
	static @NotNull Codec<String> string(int length) {
		return string(length, length);
	}
	
	static @NotNull Codec<String> string(int minLength, int maxLength) {
		return STRING.map(String::valueOf, result -> {
			if (result.isSuccess()) {
				String value = result.orThrow();
				if (value.length() < minLength) {
					return Result.error("String was decoded successfully but has less than " + minLength + " characters");
				}
				if (value.length() > maxLength) {
					return Result.error("String was decoded successfully but has more than " + maxLength + " characters");
				}
				return Result.success(value);
			}
			return result;
		});
	}
	
	static @NotNull Codec<String> noneEmptyString() {
		return STRING.map(String::valueOf, result -> {
			if (result.isSuccess()) {
				String value = result.orThrow();
				if (value.isEmpty()) {
					return Result.error("String was decoded successfully but is empty");
				}
				return Result.success(value);
			}
			return result;
		});
	}
	
	static <C> @NotNull Codec<C> unit(@NotNull C value) {
		return unit(() -> value);
	}
	
	static <C> @NotNull Codec<C> unit(@NotNull Supplier<C> supplier) {
		return new UnitCodec<>(supplier);
	}
	
	static <C> @NotNull Codec<List<C>> list(@NotNull Codec<C> codec) {
		return new ListCodec<>(codec);
	}
	
	static <C> @NotNull Codec<List<C>> list(@NotNull Codec<C> codec, int maxSize) {
		return list(codec, 0, maxSize);
	}
	
	static <C> @NotNull Codec<List<C>> list(@NotNull Codec<C> codec, int minSize, int maxSize) {
		return new ListCodec<>(codec, minSize, maxSize);
	}
	
	static <C> @NotNull Codec<List<C>> noneEmptyList(@NotNull Codec<C> codec) {
		return list(codec, 1);
	}
	
	static <C> @NotNull Codec<Optional<C>> optional(@NotNull Codec<C> codec) {
		return new OptionalCodec<>(codec);
	}
	
	static <C> @NotNull Codec<Map<String, C>> map(@NotNull Codec<C> codec) {
		return map(STRING, codec);
	}
	
	static <K, V> @NotNull Codec<Map<K, V>> map(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec) {
		return new MapCodec<>(keyCodec, valueCodec);
	}
	
	static <K, V> @NotNull Codec<Map<K, V>> map(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec, int maxSize) {
		return map(keyCodec, valueCodec, 0, maxSize);
	}
	
	static <K, V> @NotNull Codec<Map<K, V>> map(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec, int minSize, int maxSize) {
		return new MapCodec<>(keyCodec, valueCodec);
	}
	
	static <K, V> @NotNull Codec<Map<K, V>> noneEmptyMap(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec) {
		return map(keyCodec, valueCodec, 1);
	}
	
	static <F, S> @NotNull Codec<Either<F, S>> either(@NotNull Codec<F> firstCodec, @NotNull Codec<S> secondCodec) {
		return new EitherCodec<>(firstCodec, secondCodec);
	}
	
	static <C> @NotNull Codec<C> withAlternative(@NotNull Codec<C> codec, @NotNull Codec<? extends C> alternative) {
		return either(codec, alternative).mapDirect(
			Either::left,
			either -> either.mapTo(Function.identity(), Function.identity())
		);
	}
	
	static <E> @NotNull Codec<E> stringResolver(@NotNull Function<E, String> toString, @NotNull  Function<String, E> fromString) {
		return Codec.STRING.map(
			toString,
			result -> {
				if (result.isSuccess()) {
					return Optional.ofNullable(fromString.apply(result.orThrow())).map(Result::success).orElseGet(() -> Result.error("Unknown element name: " + result.orThrow()));
				}
				return Result.error("Unable to resolve element name: " + result.errorOrThrow());
			}
		);
	}
	//endregion
	
	default @NotNull KeyableCodec<C> keyable(@NotNull Function<C, String> toKey, @NotNull Function<String, @Nullable C> fromKey) {
		return keyable(this, toKey, fromKey);
	}
	
	default @NotNull Codec<List<C>> list() {
		return list(this);
	}
	
	default @NotNull Codec<List<C>> list(int maxSize) {
		return list(this, maxSize);
	}
	
	default @NotNull Codec<List<C>> list(int minSize, int maxSize) {
		return list(this, minSize, maxSize);
	}
	
	default @NotNull Codec<List<C>> noneEmptyList() {
		return noneEmptyList(this);
	}
	
	default @NotNull Codec<Optional<C>> optional() {
		return optional(this);
	}
	
	default @NotNull Codec<C> withAlternative(@NotNull Codec<? extends C> alternative) {
		return withAlternative(this, alternative);
	}
	
	default <O> @NotNull Codec<O> mapDirect(@NotNull Function<O, C> to, @NotNull Function<C, O> from) {
		return this.map(to, result -> result.map(from));
	}
	
	default <O> @NotNull Codec<O> map(@NotNull Function<O, C> to, @NotNull Function<Result<C>, Result<O>> from) {
		return new Codec<>() {
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable O value) {
				return Codec.this.encodeStart(provider, current, to.apply(value));
			}
			
			@Override
			public <R> @NotNull Result<O> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
				return from.apply(Codec.this.decodeStart(provider, value));
			}
			
			@Override
			public String toString() {
				return "MappedCodec[" + Codec.this + "]";
			}
		};
	}
	
	default @NotNull Codec<C> validate(@NotNull Function<C, Result<C>> validator) {
		return this.map(Function.identity(), result -> {
			if (result.isSuccess()) {
				return validator.apply(result.orThrow());
			}
			return result;
		});
	}
	
	default @NotNull Codec<C> orElse(@NotNull C defaultValue) {
		return this.orElseGet(() -> defaultValue);
	}
	
	default @NotNull Codec<C> orElseGet(@NotNull Supplier<C> supplier) {
		return new Codec<C>() {
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
				return Codec.this.encodeStart(provider, current, value);
			}
			
			@Override
			public <R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
				Result<C> result = Codec.this.decodeStart(provider, value);
				if (result.isError()) {
					return Result.success(supplier.get());
				}
				return result;
			}
			
			@Override
			public String toString() {
				return "OrElseCodec[" + Codec.this + "]";
			}
		};
	}
	
	default <O> @NotNull ConfigurableCodec<C, O> bind(@NotNull CodecBuilder<O> builder) {
		return new ConfigurableCodec<>(builder, this);
	}
}
