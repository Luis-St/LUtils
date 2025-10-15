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

import net.luis.utils.io.codec.internal.UUIDCodec;
import net.luis.utils.io.codec.internal.array.*;
import net.luis.utils.io.codec.internal.io.*;
import net.luis.utils.io.codec.internal.primitiv.numeric.*;
import net.luis.utils.io.codec.internal.stream.*;
import net.luis.utils.io.codec.internal.struct.EitherCodec;
import net.luis.utils.io.codec.internal.struct.UnitCodec;
import net.luis.utils.io.codec.internal.time.*;
import net.luis.utils.util.Either;
import net.luis.utils.util.result.Result;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.*;

/**
 * A utility class that provides various predefined codecs.<br>
 * This class contains codecs for primitive types, collections, streams, and various other data types.<br>
 * It also provides methods to create custom codecs for enums and other types.<br>
 *
 * @author Luis-St
 */
public final class Codecs {
	
	/**
	 * A codec that encodes and decodes boolean values.<br>
	 */
	public static final Codec<Boolean> BOOLEAN = new Codec<>() {
		
		@Override
		public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @Nullable R current, @Nullable Boolean value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
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
	/**
	 * A range codec that encodes and decodes byte values.<br>
	 */
	public static final KeyableCodec<Byte> BYTE = new ByteCodec();
	/**
	 * A range codec that encodes and decodes short values.<br>
	 */
	public static final KeyableCodec<Short> SHORT = new ShortCodec();
	/**
	 * A range codec that encodes and decodes integer values.<br>
	 */
	public static final KeyableCodec<Integer> INTEGER = new IntegerCodec();
	/**
	 * A range codec that encodes and decodes long values.<br>
	 */
	public static final KeyableCodec<Long> LONG = new LongCodec();
	/**
	 * A range codec that encodes and decodes float values.<br>
	 */
	public static final KeyableCodec<Float> FLOAT = new FloatCodec();
	/**
	 * A range codec that encodes and decodes double values.<br>
	 */
	public static final KeyableCodec<Double> DOUBLE = new DoubleCodec();
	/**
	 * A codec that encodes and decodes strings.<br>
	 */
	public static final KeyableCodec<String> STRING = new KeyableCodec<>() {
		private final KeyableEncoder<String> encoder = KeyableEncoder.of(this, ResultingFunction.direct(Function.identity()));
		private final KeyableDecoder<String> decoder = KeyableDecoder.of(this, ResultingFunction.direct(Function.identity()));
		
		@Override
		public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @Nullable R current, @Nullable String value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
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
	/**
	 * A keyable codec that encodes and decodes characters.<br>
	 */
	public static final KeyableCodec<Character> CHARACTER = new KeyableCodec<>() {
		
		@Override
		public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Character value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			if (value == null) {
				return Result.error("Unable to encode null as character using '" + this + "'");
			}
			return provider.createString(String.valueOf(value));
		}
		
		@Override
		public @NotNull <R> Result<String> encodeKey(@NotNull TypeProvider<R> provider, @NotNull Character key) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(key, "Key must not be null");
			return Result.success(String.valueOf(key));
		}
		
		@Override
		public @NotNull <R> Result<Character> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			if (value == null) {
				return Result.error("Unable to decode null value as character using '" + this + "'");
			}
			Result<String> result = provider.getString(value);
			if (result.isError()) {
				return Result.error("Unable to decode value as character from a string value using '" + this + "': " + result.errorOrThrow());
			}
			String str = result.resultOrThrow();
			if (str.length() != 1) {
				return Result.error("String must have exactly one character to decode as character using '" + this + "'");
			}
			return Result.success(str.charAt(0));
		}
		
		@Override
		public @NotNull <R> Result<Character> decodeKey(@NotNull TypeProvider<R> provider, @NotNull String key) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			Objects.requireNonNull(key, "Key must not be null");
			if (key.length() != 1) {
				return Result.error("Key must have exactly one character to decode as character using '" + this + "'");
			}
			return Result.success(key.charAt(0));
		}
		
		@Override
		public String toString() {
			return "CharacterCodec";
		}
	};
	/**
	 * A codec that encodes and decodes boolean arrays.<br>
	 * The underlying boolean array is converted to and from a list of booleans.<br>
	 */
	public static final Codec<boolean[]> BOOLEAN_ARRAY = new BooleanArrayCodec();
	/**
	 * A codec that encodes and decodes byte arrays.<br>
	 * The underlying byte array is converted to and from a list of bytes.<br>
	 */
	public static final Codec<byte[]> BYTE_ARRAY = new ByteArrayCodec();
	/**
	 * A codec that encodes and decodes short arrays.<br>
	 * The underlying byte array is converted to and from a list of shorts.<br>
	 */
	public static final Codec<short[]> SHORT_ARRAY = new ShortArrayCodec();
	/**
	 * A codec that encodes and decodes int arrays.<br>
	 * The underlying byte array is converted to and from a list of integers.<br>
	 */
	public static final Codec<int[]> INTEGER_ARRAY = new IntegerArrayCodec();
	/**
	 * A codec that encodes and decodes long arrays.<br>
	 * The underlying byte array is converted to and from a list of longs.<br>
	 */
	public static final Codec<long[]> LONG_ARRAY = new LongArrayCodec();
	/**
	 * A codec that encodes and decodes float arrays.<br>
	 * The underlying byte array is converted to and from a list of floats.<br>
	 */
	public static final Codec<float[]> FLOAT_ARRAY = new FloatArrayCodec();
	/**
	 * A codec that encodes and decodes double arrays.<br>
	 * The underlying byte array is converted to and from a list of doubles.<br>
	 */
	public static final Codec<double[]> DOUBLE_ARRAY = new DoubleArrayCodec();
	/**
	 * A codec that encodes and decodes character arrays.<br>
	 * The underlying character array is converted to and from a list of characters.<br>
	 */
	public static final Codec<char[]> CHARACTER_ARRAY = new CharacterArrayCodec();
	/**
	 * A codec that encodes and decodes {@link IntStream int streams}.<br>
	 * The underlying int stream is converted to and from a {@link Stream} of integers.<br>
	 */
	public static final Codec<IntStream> INT_STREAM = new IntStreamCodec();
	/**
	 * A codec that encodes and decodes {@link LongStream long streams}.<br>
	 * The underlying long stream is converted to and from a {@link Stream} of longs.<br>
	 */
	public static final Codec<LongStream> LONG_STREAM = new LongStreamCodec();
	/**
	 * A codec that encodes and decodes {@link DoubleStream double streams}.<br>
	 * The underlying double stream is converted to and from a {@link Stream} of doubles.<br>
	 */
	public static final Codec<DoubleStream> DOUBLE_STREAM = new DoubleStreamCodec();
	/**
	 * A keyable codec that encodes and decodes {@link UUID UUIDs}.<br>
	 * The underlying UUID is converted to and from a string.<br>
	 */
	public static final KeyableCodec<java.util.UUID> UUID = new UUIDCodec();
	/**
	 * A codec that encodes and decodes {@link LocalTime local time} values.<br>
	 * The underlying local time is converted to and from a string.<br>
	 */
	public static final Codec<LocalTime> LOCAL_TIME = new LocalTimeCodec();
	/**
	 * A codec that encodes and decodes {@link LocalDate local date} values.<br>
	 * The underlying local date is converted to and from a string.<br>
	 */
	public static final Codec<LocalDate> LOCAL_DATE = new LocalDateCodec();
	/**
	 * A codec that encodes and decodes {@link LocalDateTime local date time} values.<br>
	 * The underlying local date time is converted to and from a string.<br>
	 */
	public static final Codec<LocalDateTime> LOCAL_DATE_TIME = new LocalDateTimeCodec();
	/**
	 * A codec that encodes and decodes {@link ZonedDateTime zoned date time} values.<br>
	 * The underlying zoned date time is converted to and from a string.<br>
	 */
	public static final Codec<ZonedDateTime> ZONED_DATE_TIME = new ZonedDateTimeCodec();
	/**
	 * A codec that encodes and decodes {@link Instant instant} values.<br>
	 * The underlying instant is converted to and from a string using ISO-8601 format.<br>
	 */
	public static final Codec<Instant> INSTANT = new InstantCodec();
	/**
	 * A codec that encodes and decodes {@link Duration duration} values.<br>
	 * The underlying duration is converted to and from a human-readable string format.<br>
	 */
	public static final Codec<Duration> DURATION = new DurationCodec();
	/**
	 * A codec that encodes and decodes {@link Period period} values.<br>
	 * The underlying period is converted to and from a human-readable string format.<br>
	 */
	public static final Codec<Period> PERIOD = new PeriodCodec();
	/**
	 * A codec that encodes and decodes {@link Charset charsets}.<br>
	 * The underlying charset is converted to and from a string.<br>
	 */
	public static final Codec<Charset> CHARSET = new CharsetCodec();
	/**
	 * A codec that encodes and decodes {@link File files}.<br>
	 * The underlying file is converted to and from a string.<br>
	 */
	public static final Codec<File> FILE = new FileCodec();
	/**
	 * A codec that encodes and decodes {@link Path paths}.<br>
	 * The underlying path is converted to and from a string.<br>
	 */
	public static final Codec<Path> PATH = new PathCodec();
	/**
	 * A codec that encodes and decodes {@link java.net.URI URIs}.<br>
	 * The underlying URI is converted to and from a string.<br>
	 */
	public static final Codec<java.net.URI> URI = new URICodec();
	/**
	 * A codec that encodes and decodes {@link java.net.URL URLs}.<br>
	 * The underlying URL is converted to and from a string.<br>
	 */
	public static final Codec<java.net.URL> URL = new URLCodec();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Codecs() {}
	
	/**
	 * Creates a new keyable codec for the given enum class.<br>
	 * The enum value is encoded and decoded using its ordinal value.<br>
	 *
	 * @param clazz The enum class
	 * @param <E> The type of the enum
	 * @return A new keyable codec
	 * @throws NullPointerException If the enum class is null
	 * @see KeyableCodec
	 */
	public static <E extends Enum<E>> @NotNull KeyableCodec<E> enumOrdinal(@NotNull Class<E> clazz) {
		Objects.requireNonNull(clazz, "Enum class must not be null");
		Map<Integer, E> ordinalLookup = Arrays.stream(clazz.getEnumConstants()).collect(Collectors.toMap(Enum::ordinal, Function.identity()));
		return INTEGER.xmap(Enum::ordinal, ordinalLookup::get).keyable(constant -> Result.success(String.valueOf(constant.ordinal())), str -> {
			try {
				return Result.success(ordinalLookup.get(Integer.parseInt(str)));
			} catch (NumberFormatException e) {
				return Result.error("Unable to decode enum ordinal from string '" + str + "': " + e.getMessage());
			}
		});
	}
	
	/**
	 * Creates a new keyable codec for the given enum class.<br>
	 * The enum value is encoded and decoded using its name.<br>
	 *
	 * @param clazz The enum class
	 * @param <E> The type of the enum
	 * @return A new keyable codec
	 * @throws NullPointerException If the enum class is null
	 * @see KeyableCodec
	 */
	public static <E extends Enum<E>> @NotNull KeyableCodec<E> enumName(@NotNull Class<E> clazz) {
		Objects.requireNonNull(clazz, "Enum class must not be null");
		Map<String, E> lookup = Arrays.stream(clazz.getEnumConstants()).collect(Collectors.toMap(Enum::name, Function.identity()));
		return STRING.xmap(Enum::name, lookup::get).keyable(ResultingFunction.direct(Enum::name), key -> {
			E value = lookup.get(key);
			if (value == null) {
				return Result.error("Unable to decode enum value from name '" + key + "': No enum constant found");
			}
			return Result.success(value);
		});
	}
	
	/**
	 * Creates a new keyable codec for an enum class.<br>
	 * The enum value is encoded and decoded using a friendly name.<br>
	 *
	 * @param friendlyEncoder The function that converts an enum value to a friendly name
	 * @param friendlyDecoder The function that converts a friendly name to an enum value
	 * @param <E> The type of the enum
	 * @return A new keyable codec
	 * @throws NullPointerException If the friendly name encoder or decoder is null
	 * @see KeyableCodec
	 */
	public static <E extends Enum<E>> @NotNull KeyableCodec<E> friendlyEnumName(@NotNull Function<E, String> friendlyEncoder, @NotNull Function<String, E> friendlyDecoder) {
		Objects.requireNonNull(friendlyEncoder, "Friendly name encoder must not be null");
		Objects.requireNonNull(friendlyDecoder, "Friendly name decoder must not be null");
		return STRING.xmap(friendlyEncoder, friendlyDecoder).keyable(ResultingFunction.direct(friendlyEncoder), ResultingFunction.direct(friendlyDecoder));
	}
	
	/**
	 * Creates a new keyable codec for an enum class.<br>
	 * The enum is encoded using the name of the enum value.<br>
	 * The decoder supports both the name and the ordinal value of the enum.<br>
	 *
	 * @param clazz The enum class
	 * @param <E> The type of the enum
	 * @return A new keyable codec
	 * @throws NullPointerException If the enum class is null
	 * @see KeyableCodec
	 */
	public static <E extends Enum<E>> @NotNull KeyableCodec<E> dynamicEnum(@NotNull Class<E> clazz) {
		Objects.requireNonNull(clazz, "Enum class must not be null");
		E[] constants = clazz.getEnumConstants();
		Map<Integer, E> ordinalLookup = Arrays.stream(constants).collect(Collectors.toMap(Enum::ordinal, Function.identity()));
		Map<String, E> nameLookup = Arrays.stream(constants).collect(Collectors.toMap(Enum::name, Function.identity()));
		return either(INTEGER, STRING).xmap(
			constant -> Either.right(constant.name()),
			either -> either.mapTo(ordinalLookup::get, nameLookup::get)
		).keyable(ResultingFunction.direct(Enum::name), key -> {
			try {
				int ordinal = Integer.parseInt(key);
				E value = ordinalLookup.get(ordinal);
				if (value != null) {
					return Result.success(value);
				}
			} catch (NumberFormatException _) {}
			
			E value = nameLookup.get(key);
			if (value != null) {
				return Result.success(value);
			}
			return Result.error("Unable to decode enum value from key '" + key + "': No enum constant found");
		});
	}
	
	/**
	 * Creates a new either codec for the given codecs.<br>
	 * The value which is encoded and decoded by this codec can be either of type {@code F} or {@code S}.<br>
	 * The codec will try to use the first codec to encode and decode the value,<br>
	 * if that fails, it will try to use the second codec.<br>
	 * <p>
	 *     <strong>Note</strong>: If the first codec is a string codec, it will always succeed,<br>
	 *     so the second codec will never be used.<br>
	 * </p>
	 *
	 * @param firstCodec The first codec
	 * @param secondCodec The second codec
	 * @param <F> The type of the first value
	 * @param <S> The type of the second value
	 * @return A new either codec
	 * @throws NullPointerException If the first codec or second codec is null
	 * @see EitherCodec
	 */
	public static <F, S> @NotNull Codec<Either<F, S>> either(@NotNull Codec<F> firstCodec, @NotNull Codec<S> secondCodec) {
		return new EitherCodec<>(firstCodec, secondCodec);
	}
	
	/**
	 * Creates a new unit codec for the given value.<br>
	 *
	 * @param value The value
	 * @param <C> The type of the value
	 * @return A new unit codec
	 * @see UnitCodec
	 */
	public static <C> @NotNull Codec<C> unit(@Nullable C value) {
		return unit(() -> value);
	}
	
	/**
	 * Creates a new unit codec for the given supplier.<br>
	 *
	 * @param supplier The supplier that provides the value
	 * @param <C> The type of the value
	 * @return A new unit codec
	 * @throws NullPointerException If the supplier is null
	 * @see UnitCodec
	 */
	public static <C> @NotNull Codec<C> unit(@NotNull Supplier<C> supplier) {
		return new UnitCodec<>(supplier);
	}
	
	/**
	 * Creates a new keyable string codec for the given maximum length.<br>
	 *
	 * @param length The maximum length of the string (inclusive)
	 * @return A new keyable string codec
	 * @throws IllegalArgumentException If the length is less than 0
	 * @see KeyableCodec
	 */
	public static @NotNull KeyableCodec<String> string(int length) {
		if (0 > length) {
			throw new IllegalArgumentException("Length must be at least 0");
		}
		return string(0, length);
	}
	
	/**
	 * Creates a new keyable string codec for the given minimum and maximum length.<br>
	 *
	 * @param minLength The minimum length of the string (inclusive)
	 * @param maxLength The maximum length of the string (inclusive)
	 * @return A new keyable string codec
	 * @throws IllegalArgumentException If the minimum length is less than zero or greater than the maximum length
	 * @see KeyableCodec
	 */
	public static @NotNull KeyableCodec<String> string(int minLength, int maxLength) {
		if (0 > minLength) {
			throw new IllegalArgumentException("Minimum length must be at least 0");
		}
		if (minLength > maxLength) {
			throw new IllegalArgumentException("Minimum length must be less than or equal to maximum length");
		}
		
		ResultingFunction<String, String> encoder = str -> {
			if (str.length() < minLength) {
				return Result.error("String '" + str + "' must have at least " + minLength + " characters");
			}
			if (str.length() > maxLength) {
				return Result.error("String '" + str + "' must have at most " + maxLength + " characters");
			}
			return Result.success(str);
		};
		ResultingFunction<String, String> decoder = str -> {
			if (str.length() < minLength) {
				return Result.error("String '" + str + "' was decoded successfully but has less than " + minLength + " characters");
			}
			if (str.length() > maxLength) {
				return Result.error("String '" + str + "' was decoded successfully but has more than " + maxLength + " characters");
			}
			return Result.success(str);
		};
		
		return STRING.map(encoder, result -> {
			if (result.isError()) {
				return result;
			}
			return decoder.apply(result.resultOrThrow());
		}).keyable(encoder, decoder);
	}
	
	/**
	 * Creates a new keyable string codec for non-empty strings.<br>
	 * The string must not be empty after encoding and decoding.<br>
	 *
	 * @return A new keyable string codec
	 * @see KeyableCodec
	 */
	public static @NotNull KeyableCodec<String> noneEmptyString() {
		ResultingFunction<String, String> encoder = str -> {
			if (str.isEmpty()) {
				return Result.error("String must not be empty");
			}
			return Result.success(str);
		};
		ResultingFunction<String, String> decoder = str -> {
			if (str.isEmpty()) {
				return Result.error("String '" + str + "' must not be empty");
			}
			return Result.success(str);
		};
		
		return STRING.map(encoder, result -> {
			if (result.isError()) {
				return result;
			}
			return decoder.apply(result.resultOrThrow());
		}).keyable(encoder, decoder);
	}
	
	/**
	 * Creates a new keyable string codec that validates strings against the given regular expression.<br>
	 * The string must match the regex pattern during both encoding and decoding.<br>
	 *
	 * @param regex The regular expression pattern as a string
	 * @return A new keyable string codec that validates against the regex
	 * @throws NullPointerException If the regex is null
	 * @throws java.util.regex.PatternSyntaxException If the regex pattern is invalid
	 * @see KeyableCodec
	 * @see Pattern
	 */
	public static @NotNull KeyableCodec<String> formattedString(@NotNull @Language("RegExp") String regex) {
		Objects.requireNonNull(regex, "Format must not be null");
		return formattedString(Pattern.compile(regex));
	}
	
	/**
	 * Creates a new keyable string codec that validates strings against the given pattern.<br>
	 * The string must match the pattern during both encoding and decoding.<br>
	 *
	 * @param regex The compiled regular expression pattern
	 * @return A new keyable string codec that validates against the pattern
	 * @throws NullPointerException If the pattern is null
	 * @see KeyableCodec
	 * @see Pattern
	 */
	public static @NotNull KeyableCodec<String> formattedString(@NotNull Pattern regex) {
		Objects.requireNonNull(regex, "Format must not be null");
		
		ResultingFunction<String, String> encoder = str -> {
			if (regex.matcher(str).matches()) {
				return Result.success(str);
			}
			return Result.error("String '" + str + "' does not match the expected format '" + regex + "'");
		};
		ResultingFunction<String, String> decoder = str -> {
			if (regex.matcher(str).matches()) {
				return Result.success(str);
			}
			return Result.error("String '" + str + "' was decoded successfully but does not match expected format '" + regex + "'");
		};
		
		return STRING.map(encoder, result -> {
			if (result.isError()) {
				return result;
			}
			return decoder.apply(result.resultOrThrow());
		}).keyable(encoder, decoder);
	}
	
	/**
	 * Creates a new codec that encodes and decodes values of the type {@code C} to and from strings.<br>
	 * The string encoder and decoder are defined as functions that convert values of the type {@code C} to and from strings.<br>
	 * If the decoder is unable to decode a string, it should return null.<br>
	 *
	 * @param stringEncoder The encoder function
	 * @param stringDecoder The decoder function
	 * @param <E> The type of the value that is encoded and decoded by the codec
	 * @return A new codec
	 * @throws NullPointerException If the string encoder or decoder is null
	 */
	public static <E> @NotNull Codec<E> stringResolver(@NotNull Function<E, String> stringEncoder, @NotNull Function<String, @Nullable E> stringDecoder) {
		Objects.requireNonNull(stringDecoder, "Element string decoder must not be null");
		return STRING.mapFlat(
			stringEncoder,
			result -> {
				if (result.isSuccess()) {
					E value;
					try {
						value = stringDecoder.apply(result.resultOrThrow());
					} catch (Exception e) {
						return Result.error("Unable to resolve element: " + e.getMessage());
					}
					return Optional.ofNullable(value).map(Result::success).orElseGet(() -> Result.error("Unknown element: " + result.resultOrThrow()));
				}
				return Result.error("Unable to resolve element: " + result.errorOrThrow());
			}
		);
	}
}
