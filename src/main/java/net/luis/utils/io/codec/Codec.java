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

package net.luis.utils.io.codec;

import com.google.common.collect.Lists;
import net.luis.utils.io.codec.decoder.Decoder;
import net.luis.utils.io.codec.decoder.KeyableDecoder;
import net.luis.utils.io.codec.encoder.Encoder;
import net.luis.utils.io.codec.encoder.KeyableEncoder;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.codec.struct.*;
import net.luis.utils.util.Either;
import net.luis.utils.util.Result;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.*;

import static net.luis.utils.io.codec.ResultMappingFunction.*;

/**
 *
 * @author Luis-St
 *
 */

public interface Codec<C> extends Encoder<C>, Decoder<C> {
	
	Codec<Boolean> BOOLEAN = new Codec<>() {
		
		@Override
		public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Boolean value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(current, "Current value must not be null");
			if (value == null) {
				return Result.error("Unable to encode null as boolean using '" + this + "'");
			}
			return provider.createBoolean(value);
		}
		
		@Override
		public <R> @NotNull Result<Boolean> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
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
	RangeCodec<Byte> BYTE = new RangeCodec<>("Byte", Byte.MIN_VALUE, Byte.MAX_VALUE, Number::byteValue, Byte::parseByte) {
		
		@Override
		protected @NotNull <R> Result<R> encodeNumber(@NotNull TypeProvider<R> provider, @NotNull Byte value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(value, "Value must not be null");
			return provider.createByte(value);
		}
		
		@Override
		protected @NotNull <R> Result<Byte> decodeNumber(@NotNull TypeProvider<R> provider, @NotNull R value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(value, "Value must not be null");
			return provider.getByte(value);
		}
	};
	RangeCodec<Short> SHORT = new RangeCodec<>("Short", Short.MIN_VALUE, Short.MAX_VALUE, Number::shortValue, Short::parseShort) {
		
		@Override
		protected @NotNull <R> Result<R> encodeNumber(@NotNull TypeProvider<R> provider, @NotNull Short value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(value, "Value must not be null");
			return provider.createShort(value);
		}
		
		@Override
		protected @NotNull <R> Result<Short> decodeNumber(@NotNull TypeProvider<R> provider, @NotNull R value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(value, "Value must not be null");
			return provider.getShort(value);
		}
	};
	RangeCodec<Integer> INTEGER = new RangeCodec<>("Integer", Integer.MIN_VALUE, Integer.MAX_VALUE, Number::intValue, Integer::parseInt) {
		
		@Override
		protected @NotNull <R> Result<R> encodeNumber(@NotNull TypeProvider<R> provider, @NotNull Integer value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(value, "Value must not be null");
			return provider.createInteger(value);
		}
		
		@Override
		protected @NotNull <R> Result<Integer> decodeNumber(@NotNull TypeProvider<R> provider, @NotNull R value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(value, "Value must not be null");
			return provider.getInteger(value);
		}
	};
	RangeCodec<Long> LONG = new RangeCodec<>("Long", Long.MIN_VALUE, Long.MAX_VALUE, Number::longValue, Long::parseLong) {
		
		@Override
		protected @NotNull <R> Result<R> encodeNumber(@NotNull TypeProvider<R> provider, @NotNull Long value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(value, "Value must not be null");
			return provider.createLong(value);
		}
		
		@Override
		protected @NotNull <R> Result<Long> decodeNumber(@NotNull TypeProvider<R> provider, @NotNull R value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(value, "Value must not be null");
			return provider.getLong(value);
		}
	};
	RangeCodec<Float> FLOAT = new RangeCodec<>("Float", -Float.MAX_VALUE, Float.MAX_VALUE, Number::floatValue, Float::parseFloat) {
		
		@Override
		protected @NotNull <R> Result<R> encodeNumber(@NotNull TypeProvider<R> provider, @NotNull Float value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(value, "Value must not be null");
			return provider.createFloat(value);
		}
		
		@Override
		protected @NotNull <R> Result<Float> decodeNumber(@NotNull TypeProvider<R> provider, @NotNull R value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(value, "Value must not be null");
			return provider.getFloat(value);
		}
	};
	RangeCodec<Double> DOUBLE = new RangeCodec<>("Double", -Double.MAX_VALUE, Double.MAX_VALUE, Number::doubleValue, Double::parseDouble) {
		
		@Override
		protected @NotNull <R> Result<R> encodeNumber(@NotNull TypeProvider<R> provider, @NotNull Double value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(value, "Value must not be null");
			return provider.createDouble(value);
		}
		
		@Override
		protected @NotNull <R> Result<Double> decodeNumber(@NotNull TypeProvider<R> provider, @NotNull R value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(value, "Value must not be null");
			return provider.getDouble(value);
		}
	};
	KeyableCodec<String> STRING = new KeyableCodec<>() {
		private final KeyableEncoder<String> encoder = KeyableEncoder.of(this, Function.identity());
		private final KeyableDecoder<String> decoder = KeyableDecoder.of(this, Function.identity());
		
		@Override
		public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable String value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(current, "Current value must not be null");
			if (value == null) {
				return Result.error("Unable to encode null as string using '" + this + "'");
			}
			return provider.createString(value);
		}
		
		@Override
		public <R> @NotNull Result<String> encodeKey(@NotNull TypeProvider<R> provider, @NotNull String key) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(key, "Key must not be null");
			return this.encoder.encodeKey(provider, key);
		}
		
		@Override
		public <R> @NotNull Result<String> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			if (value == null) {
				return Result.error("Unable to decode null value as string using '" + this + "'");
			}
			return provider.getString(value);
		}
		
		@Override
		public <R> @NotNull Result<String> decodeKey(@NotNull TypeProvider<R> provider, @NotNull String key) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(key, "Key must not be null");
			return this.decoder.decodeKey(provider, key);
		}
		
		@Override
		public String toString() {
			return "StringCodec";
		}
	};
	
	Codec<byte[]> BYTE_ARRAY = BYTE.list().xmap(array -> Lists.newArrayList(ArrayUtils.toObject(array)), list -> ArrayUtils.toPrimitive(list.toArray(Byte[]::new))).codec("ByteArrayCodec");
	Codec<IntStream> INT_STREAM = INTEGER.stream().xmap(IntStream::boxed, stream -> stream.mapToInt(Integer::intValue)).codec("IntStreamCodec");
	Codec<LongStream> LONG_STREAM = LONG.stream().xmap(LongStream::boxed, stream -> stream.mapToLong(Long::longValue)).codec("LongStreamCodec");
	Codec<DoubleStream> DOUBLE_STREAM = DOUBLE.stream().xmap(DoubleStream::boxed, stream -> stream.mapToDouble(Double::doubleValue)).codec("DoubleStreamCodec");
	
	KeyableCodec<java.util.UUID> UUID = keyable(STRING.flatMap(java.util.UUID::toString, throwable(java.util.UUID::fromString)).codec("UUIDCodec"), java.util.UUID::fromString);
	
	Codec<LocalTime> LOCAL_TIME = STRING.flatMap(LocalTime::toString, throwable(LocalTime::parse)).codec("LocalTimeCodec");
	Codec<LocalDate> LOCAL_DATE = STRING.flatMap(LocalDate::toString, throwable(LocalDate::parse)).codec("LocalDateCodec");
	Codec<LocalDateTime> LOCAL_DATE_TIME = STRING.flatMap(LocalDateTime::toString, throwable(LocalDateTime::parse)).codec("LocalDateTimeCodec");
	Codec<ZonedDateTime> ZONED_DATE_TIME = STRING.flatMap(ZonedDateTime::toString, throwable(ZonedDateTime::parse)).codec("ZonedDateTimeCodec");
	
	Codec<Charset> CHARSET = STRING.flatMap(Charset::name, throwable(Charset::forName));
	Codec<File> FILE = STRING.flatMap(File::getPath, throwable(File::new)).codec("FileCodec");
	Codec<Path> PATH = STRING.flatMap(Path::toString, throwable(Path::of)).codec("PathCodec");
	Codec<java.net.URI> URI = STRING.flatMap(java.net.URI::toString, throwable(java.net.URI::new)).codec("URICodec");
	Codec<java.net.URL> URL = URI.map(ResultingFunction.throwable(java.net.URL::toURI), throwable(java.net.URI::toURL)).codec("URLCodec");
	
	//region Factories
	static <C> @NotNull Codec<C> of(@NotNull Encoder<C> encoder, @NotNull Decoder<C> decoder, @NotNull String name) {
		Objects.requireNonNull(encoder, "Encoder must not be null");
		Objects.requireNonNull(decoder, "Decoder must not be null");
		Objects.requireNonNull(name, "Codec name must not be null");
		return new Codec<>() {
			@Override
			public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
				return encoder.encodeStart(provider, current, value);
			}
			
			@Override
			public @NotNull <R> Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
				return decoder.decodeStart(provider, value);
			}
			
			@Override
			public String toString() {
				return name;
			}
		};
	}
	
	static <C> @NotNull KeyableCodec<C> keyable(@NotNull Codec<C> codec, @NotNull Function<String, @Nullable C> keyDecoder) {
		Objects.requireNonNull(codec, "Base codec must not be null");
		Objects.requireNonNull(keyDecoder, "Key decoder must not be null");
		return keyable(codec, C::toString, keyDecoder);
	}
	
	static <C> @NotNull KeyableCodec<C> keyable(@NotNull Codec<C> codec, @NotNull Function<C, String> keyEncoder, @NotNull Function<String, @Nullable C> keyDecoder) {
		Objects.requireNonNull(codec, "Base codec must not be null");
		Objects.requireNonNull(keyEncoder, "Key encoder must not be null");
		Objects.requireNonNull(keyDecoder, "Key decoder must not be null");
		return keyable(codec, KeyableEncoder.of(codec, keyEncoder), KeyableDecoder.of(codec, keyDecoder));
	}
	
	static <C> @NotNull KeyableCodec<C> keyable(@NotNull Codec<C> codec, @NotNull KeyableEncoder<C> encoder, @NotNull KeyableDecoder<C> decoder) {
		Objects.requireNonNull(codec, "Base codec must not be null");
		Objects.requireNonNull(encoder, "Key encoder must not be null");
		Objects.requireNonNull(decoder, "Key decoder must not be null");
		return new KeyableCodec<>() {
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
				return encoder.encodeStart(provider, current, value);
			}
			
			@Override
			public <R> @NotNull Result<String> encodeKey(@NotNull TypeProvider<R> provider, @NotNull C key) {
				return encoder.encodeKey(provider, key);
			}
			
			@Override
			public <R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
				return decoder.decodeStart(provider, value);
			}
			
			@Override
			public <R> @NotNull Result<C> decodeKey(@NotNull TypeProvider<R> provider, @NotNull String key) {
				return decoder.decodeKey(provider, key);
			}
			
			@Override
			public String toString() {
				return "KeyableCodec[" + codec + "]";
			}
		};
	}
	
	static <E extends Enum<E>> @NotNull KeyableCodec<E> enumOrdinal(@NotNull Class<E> clazz) {
		Objects.requireNonNull(clazz, "Enum class must not be null");
		Map<Integer, E> ordinalLookup = Arrays.stream(clazz.getEnumConstants()).collect(Collectors.toMap(Enum::ordinal, Function.identity()));
		return keyable(INTEGER.xmap(Enum::ordinal, ordinalLookup::get), constant -> String.valueOf(constant.ordinal()), str -> {
			try {
				return ordinalLookup.get(Integer.parseInt(str));
			} catch (NumberFormatException e) {
				return null;
			}
		});
	}
	
	static <E extends Enum<E>> @NotNull KeyableCodec<E> enumName(@NotNull Class<E> clazz) {
		Objects.requireNonNull(clazz, "Enum class must not be null");
		Map<String, E> lookup = Arrays.stream(clazz.getEnumConstants()).collect(Collectors.toMap(Enum::name, Function.identity()));
		return keyable(STRING.xmap(Enum::name, lookup::get), Enum::name, lookup::get);
	}
	
	static <E extends Enum<E>> @NotNull KeyableCodec<E> friendlyEnumName(@NotNull Function<E, String> toFriendly, @NotNull Function<String, E> fromFriendly) {
		Objects.requireNonNull(toFriendly, "Friendly name encoder must not be null");
		Objects.requireNonNull(fromFriendly, "Friendly name decoder must not be null");
		return keyable(STRING.xmap(toFriendly, fromFriendly), toFriendly, fromFriendly);
	}
	
	static <E extends Enum<E>> @NotNull KeyableCodec<E> dynamicEnum(@NotNull Class<E> clazz) {
		Objects.requireNonNull(clazz, "Enum class must not be null");
		E[] constants = clazz.getEnumConstants();
		Map<Integer, E> ordinalLookup = Arrays.stream(constants).collect(Collectors.toMap(Enum::ordinal, Function.identity()));
		Map<String, E> nameLookup = Arrays.stream(constants).collect(Collectors.toMap(Enum::name, Function.identity()));
		return keyable(either(INTEGER, STRING).xmap(
			constant -> Either.right(constant.name()),
			either -> either.mapTo(ordinalLookup::get, nameLookup::get)
		), Enum::name, nameLookup::get);
	}
	
	static @NotNull Codec<String> string(int length) {
		if (0 > length) {
			throw new IllegalArgumentException("Length must be at least 0");
		}
		return string(0, length);
	}
	
	static @NotNull Codec<String> string(int minLength, int maxLength) {
		if (0 > minLength) {
			throw new IllegalArgumentException("Minimum length must be at least 0");
		}
		if (minLength > maxLength) {
			throw new IllegalArgumentException("Minimum length must be less than or equal to maximum length");
		}
		return STRING.map(str -> {
			if (str.length() < minLength) {
				return Result.error("String must have at least " + minLength + " characters");
			}
			if (str.length() > maxLength) {
				return Result.error("String must have at most " + maxLength + " characters");
			}
			return Result.success(str);
		}, result -> {
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
		return STRING.map(str -> {
			if (str.isEmpty()) {
				return Result.error("String must not be empty");
			}
			return Result.success(str);
		}, result -> {
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
		Objects.requireNonNull(value, "Unit value must not be null");
		return unit(() -> value);
	}
	
	static <C> @NotNull Codec<C> unit(@NotNull Supplier<C> supplier) {
		Objects.requireNonNull(supplier, "Unit value supplier must not be null");
		return new UnitCodec<>(supplier);
	}
	
	static <C> @NotNull Codec<Optional<C>> optional(@NotNull Codec<C> codec) {
		return new OptionalCodec<>(Objects.requireNonNull(codec, "Codec must not be null"));
	}
	
	static <C> @NotNull Codec<List<C>> list(@NotNull Codec<C> codec) {
		return new ListCodec<>(Objects.requireNonNull(codec, "Codec must not be null"));
	}
	
	static <C> @NotNull Codec<List<C>> list(@NotNull Codec<C> codec, int maxSize) {
		Objects.requireNonNull(codec, "Codec must not be null");
		if (0 > maxSize) {
			throw new IllegalArgumentException("Maximum size must be at least 0");
		}
		return list(codec, 0, maxSize);
	}
	
	static <C> @NotNull Codec<List<C>> list(@NotNull Codec<C> codec, int minSize, int maxSize) {
		Objects.requireNonNull(codec, "Codec must not be null");
		if (0 > minSize) {
			throw new IllegalArgumentException("Minimum size must be at least 0");
		}
		if (minSize > maxSize) {
			throw new IllegalArgumentException("Minimum size must be less than or equal to maximum size");
		}
		return new ListCodec<>(codec, minSize, maxSize);
	}
	
	static <C> @NotNull Codec<List<C>> noneEmptyList(@NotNull Codec<C> codec) {
		return list(codec, 1);
	}
	
	static <C> @NotNull Codec<Stream<C>> stream(@NotNull Codec<C> codec) {
		Objects.requireNonNull(codec, "Codec must not be null");
		return codec.list().xmap(Stream::toList, List::stream).codec("StreamCodec[" + codec + "]");
	}
	
	static <C> @NotNull Codec<Map<String, C>> map(@NotNull Codec<C> valueCodec) {
		return map(STRING, Objects.requireNonNull(valueCodec, "Value codec must not be null"));
	}
	
	static <K, V> @NotNull Codec<Map<K, V>> map(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec) {
		Objects.requireNonNull(keyCodec, "Key codec must not be null");
		Objects.requireNonNull(valueCodec, "Value codec must not be null");
		return new MapCodec<>(keyCodec, valueCodec);
	}
	
	static <K, V> @NotNull Codec<Map<K, V>> map(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec, int maxSize) {
		Objects.requireNonNull(keyCodec, "Key codec must not be null");
		Objects.requireNonNull(valueCodec, "Value codec must not be null");
		if (0 > maxSize) {
			throw new IllegalArgumentException("Maximum size must be at least 0");
		}
		return map(keyCodec, valueCodec, 0, maxSize);
	}
	
	static <K, V> @NotNull Codec<Map<K, V>> map(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec, int minSize, int maxSize) {
		Objects.requireNonNull(keyCodec, "Key codec must not be null");
		Objects.requireNonNull(valueCodec, "Value codec must not be null");
		if (0 > minSize) {
			throw new IllegalArgumentException("Minimum size must be at least 0");
		}
		if (minSize > maxSize) {
			throw new IllegalArgumentException("Minimum size must be less than or equal to maximum size");
		}
		return new MapCodec<>(keyCodec, valueCodec, minSize, maxSize);
	}
	
	static <K, V> @NotNull Codec<Map<K, V>> noneEmptyMap(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec) {
		Objects.requireNonNull(keyCodec, "Key codec must not be null");
		Objects.requireNonNull(valueCodec, "Value codec must not be null");
		return map(keyCodec, valueCodec, 1);
	}
	
	static <F, S> @NotNull Codec<Either<F, S>> either(@NotNull Codec<F> firstCodec, @NotNull Codec<S> secondCodec) {
		Objects.requireNonNull(firstCodec, "First codec must not be null");
		Objects.requireNonNull(secondCodec, "Second codec must not be null");
		return new EitherCodec<>(firstCodec, secondCodec);
	}
	
	static <C> @NotNull Codec<C> withAlternative(@NotNull Codec<C> main, @NotNull Codec<? extends C> alternative) {
		Objects.requireNonNull(main, "Main codec must not be null");
		Objects.requireNonNull(alternative, "Alternative codec must not be null");
		return either(main, alternative).xmap(
			Either::left,
			either -> either.mapTo(Function.identity(), Function.identity())
		);
	}
	
	static <E> @NotNull Codec<E> stringResolver(@NotNull Function<E, String> toString, @NotNull  Function<String, E> fromString) {
		Objects.requireNonNull(toString, "Element string encoder must not be null");
		Objects.requireNonNull(fromString, "Element string decoder must not be null");
		return STRING.flatMap(
			toString,
			result -> {
				if (result.isSuccess()) {
					E value;
					try {
						value = fromString.apply(result.orThrow());
					} catch (Exception e) {
						return Result.error("Unable to resolve element: " + e.getMessage());
					}
					return Optional.ofNullable(value).map(Result::success).orElseGet(() -> Result.error("Unknown element: " + result.orThrow()));
				}
				return Result.error("Unable to resolve element: " + result.errorOrThrow());
			}
		);
	}
	//endregion
	
	private @NotNull Codec<C> codec(@NotNull String name) {
		Objects.requireNonNull(name, "Codec name must not be null");
		return of(this, this, name);
	}
	
	default @NotNull KeyableCodec<C> keyable(@NotNull Function<C, String> keyEncoder, @NotNull Function<String, @Nullable C> keyDecoder) {
		Objects.requireNonNull(keyEncoder, "Key encoder must not be null");
		Objects.requireNonNull(keyDecoder, "Key decoder must not be null");
		return keyable(this, keyEncoder, keyDecoder);
	}
	
	default @NotNull Codec<Optional<C>> optional() {
		return optional(this);
	}
	
	default @NotNull Codec<List<C>> list() {
		return list(this);
	}
	
	default @NotNull Codec<List<C>> list(int maxSize) {
		if (0 > maxSize) {
			throw new IllegalArgumentException("Maximum size must be at least 0");
		}
		return list(this, maxSize);
	}
	
	default @NotNull Codec<List<C>> list(int minSize, int maxSize) {
		if (0 > minSize) {
			throw new IllegalArgumentException("Minimum size must be at least 0");
		}
		if (minSize > maxSize) {
			throw new IllegalArgumentException("Minimum size must be less than or equal to maximum size");
		}
		return list(this, minSize, maxSize);
	}
	
	default @NotNull Codec<List<C>> noneEmptyList() {
		return noneEmptyList(this);
	}
	
	default @NotNull Codec<Stream<C>> stream() {
		return stream(this);
	}
	
	default @NotNull Codec<C> withAlternative(@NotNull Codec<? extends C> alternative) {
		Objects.requireNonNull(alternative, "Alternative codec must not be null");
		return withAlternative(this, alternative);
	}
	
	default <O> @NotNull Codec<O> xmap(@NotNull Function<O, C> to, @NotNull Function<C, O> from) {
		Objects.requireNonNull(to, "Encoding mapping function must not be null");
		Objects.requireNonNull(from, "Decoding mapping function must not be null");
		return this.map(ResultingFunction.direct(to), direct(from));
	}
	
	default <O> @NotNull Codec<O> flatMap(@NotNull Function<O, C> to, @NotNull ResultMappingFunction<C, O> from) {
		Objects.requireNonNull(to, "Encoding mapping function must not be null");
		Objects.requireNonNull(from, "Decoding mapping function must not be null");
		return this.map(ResultingFunction.direct(to), from);
	}
	
	default <O> @NotNull Codec<O> map(@NotNull ResultingFunction<O, C> to, @NotNull ResultMappingFunction<C, O> from) {
		Objects.requireNonNull(to, "Encoding mapping function must not be null");
		Objects.requireNonNull(from, "Decoding mapping function must not be null");
		return of(this.mapEncoder(to), this.mapDecoder(from), "MappedCodec[" + this + "]");
	}
	
	default @NotNull Codec<C> validate(@NotNull Function<C, Result<C>> validator) {
		Objects.requireNonNull(validator, "Validator function must not be null");
		return this.flatMap(Function.identity(), result -> result.flatMap(validator));
	}
	
	default @NotNull Codec<C> orElse(@Nullable C defaultValue) {
		return this.orElseGet(() -> defaultValue);
	}
	
	default @NotNull Codec<C> orElseGet(@NotNull Supplier<C> supplier) {
		Objects.requireNonNull(supplier, "Default value supplier must not be null");
		return of(this, this.mapDecoder(result -> Result.success(result.orElseGet(supplier))), "OrElseCodec[" + this + "]");
	}
	
	default @NotNull Codec<C> named(@NotNull String name) {
		return new NamedCodec<>(this, Objects.requireNonNull(name, "Name must not be null"));
	}
	
	default <O> @NotNull ConfiguredCodec<C, O> getter(@NotNull Function<O, C> getter) {
		return new ConfiguredCodec<>(this, getter);
	}
	
	default <O> @NotNull ConfiguredCodec<C, O> configure(@NotNull String name, @NotNull Function<O, C> getter) {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(getter, "Getter must not be null");
		return new ConfiguredCodec<>(this.named(name), getter);
	}
}
