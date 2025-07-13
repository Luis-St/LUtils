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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.*;

import static net.luis.utils.io.codec.ResultMappingFunction.*;

/**
 * A codec is a combination of an encoder and a decoder.<br>
 * It is used to encode and decode values of a specific type.<br>
 *
 * @see Encoder
 * @see Decoder
 *
 * @author Luis-St
 *
 * @param <C> The type of the value that is encoded and decoded by this codec
 *
 */
public interface Codec<C> extends Encoder<C>, Decoder<C> {
	
	/**
	 * A codec that encodes and decodes boolean values.<br>
	 */
	Codec<Boolean> BOOLEAN = new Codec<>() {
		
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
	/**
	 * A range codec that encodes and decodes short values.<br>
	 */
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
	/**
	 * A range codec that encodes and decodes integer values.<br>
	 */
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
	/**
	 * A range codec that encodes and decodes long values.<br>
	 */
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
	/**
	 * A range codec that encodes and decodes float values.<br>
	 */
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
	/**
	 * A range codec that encodes and decodes double values.<br>
	 */
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
	/**
	 * A codec that encodes and decodes strings.<br>
	 */
	KeyableCodec<String> STRING = new KeyableCodec<>() {
		private final KeyableEncoder<String> encoder = KeyableEncoder.of(this, Function.identity());
		private final KeyableDecoder<String> decoder = KeyableDecoder.of(this, Function.identity());
		
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
	KeyableCodec<Character> CHARACTER = new KeyableCodec<>() {
		
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
			String str = result.orThrow();
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
	 * A codec that encodes and decodes byte arrays.<br>
	 * The underlying byte array is converted to and from a list of bytes.<br>
	 */
	Codec<byte[]> BYTE_ARRAY = BYTE.list().xmap(array -> Lists.newArrayList(ArrayUtils.toObject(array)), list -> ArrayUtils.toPrimitive(list.toArray(Byte[]::new))).codec("ByteArrayCodec");
	/**
	 * A codec that encodes and decodes {@link IntStream int streams}.<br>
	 * The underlying int stream is converted to and from a {@link Stream} of integers.<br>
	 */
	Codec<IntStream> INT_STREAM = INTEGER.stream().xmap(IntStream::boxed, stream -> stream.mapToInt(Integer::intValue)).codec("IntStreamCodec");
	/**
	 * A codec that encodes and decodes {@link LongStream long streams}.<br>
	 * The underlying long stream is converted to and from a {@link Stream} of longs.<br>
	 */
	Codec<LongStream> LONG_STREAM = LONG.stream().xmap(LongStream::boxed, stream -> stream.mapToLong(Long::longValue)).codec("LongStreamCodec");
	/**
	 * A codec that encodes and decodes {@link DoubleStream double streams}.<br>
	 * The underlying double stream is converted to and from a {@link Stream} of doubles.<br>
	 */
	Codec<DoubleStream> DOUBLE_STREAM = DOUBLE.stream().xmap(DoubleStream::boxed, stream -> stream.mapToDouble(Double::doubleValue)).codec("DoubleStreamCodec");
	
	/**
	 * A keyable codec that encodes and decodes {@link UUID UUIDs}.<br>
	 * The underlying UUID is converted to and from a string.<br>
	 */
	KeyableCodec<java.util.UUID> UUID = keyable(STRING.mapFlat(java.util.UUID::toString, throwable(java.util.UUID::fromString)).codec("UUIDCodec"), java.util.UUID::fromString);
	
	/**
	 * A codec that encodes and decodes {@link LocalTime local time} values.<br>
	 * The underlying local time is converted to and from a string.<br>
	 */
	Codec<LocalTime> LOCAL_TIME = STRING.mapFlat(LocalTime::toString, throwable(LocalTime::parse)).codec("LocalTimeCodec");
	/**
	 * A codec that encodes and decodes {@link LocalDate local date} values.<br>
	 * The underlying local date is converted to and from a string.<br>
	 */
	Codec<LocalDate> LOCAL_DATE = STRING.mapFlat(LocalDate::toString, throwable(LocalDate::parse)).codec("LocalDateCodec");
	/**
	 * A codec that encodes and decodes {@link LocalDateTime local date time} values.<br>
	 * The underlying local date time is converted to and from a string.<br>
	 */
	Codec<LocalDateTime> LOCAL_DATE_TIME = STRING.mapFlat(LocalDateTime::toString, throwable(LocalDateTime::parse)).codec("LocalDateTimeCodec");
	/**
	 * A codec that encodes and decodes {@link ZonedDateTime zoned date time} values.<br>
	 * The underlying zoned date time is converted to and from a string.<br>
	 */
	Codec<ZonedDateTime> ZONED_DATE_TIME = STRING.mapFlat(ZonedDateTime::toString, throwable(ZonedDateTime::parse)).codec("ZonedDateTimeCodec");
	/**
	 * A codec that encodes and decodes {@link Instant instant} values.<br>
	 * The underlying instant is converted to and from a string using ISO-8601 format.<br>
	 */
	Codec<Instant> INSTANT = STRING.mapFlat(Instant::toString, throwable(Instant::parse)).codec("InstantCodec");
	/**
	 * A codec that encodes and decodes {@link Duration duration} values.<br>
	 * The underlying duration is converted to and from a human-readable string format.<br>
	 */
	Codec<Duration> DURATION = new Codec<>() {
		@Override
		public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @Nullable R current, @Nullable Duration value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			if (value == null) {
				return Result.error("Unable to encode null as duration using '" + this + "'");
			}
			
			long totalSeconds = value.getSeconds();
			long days = totalSeconds / 86400;
			long hours = (totalSeconds % 86400) / 3600;
			long minutes = (totalSeconds % 3600) / 60;
			long seconds = totalSeconds % 60;
			long milliseconds = value.toMillis() % 1000;
			long nanos = value.toNanos() % 1_000_000;
			
			StringBuilder builder = new StringBuilder();
			if (days > 0) {
				builder.append(days).append("d ");
			}
			if (hours > 0) {
				builder.append(hours).append("h ");
			}
			if (minutes > 0) {
				builder.append(minutes).append("m ");
			}
			if (seconds > 0) {
				builder.append(seconds).append("s ");
			}
			if (milliseconds > 0) {
				builder.append(milliseconds).append("ms ");
			}
			if (nanos > 0) {
				builder.append(nanos).append("ns");
			}
			
			String encoded = builder.toString().trim();
			if (encoded.isEmpty()) {
				encoded = "0s";
			}
			
			return provider.createString(encoded);
		}
		
		@Override
		public <R> @NotNull Result<Duration> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			if (value == null) {
				return Result.error("Unable to decode null value as duration using '" + this + "'");
			}
			
			Result<String> stringResult = provider.getString(value);
			if (stringResult.isError()) {
				return Result.error("Unable to decode duration from non-string value using '" + this + "': " + stringResult.errorOrThrow());
			}
			
			String str = stringResult.orThrow();
			try {
				String[] parts = str.toLowerCase().split("\\s+");
				long totalSeconds = 0;
				long nanos = 0;
				
				Pattern pattern = Pattern.compile("([+-]?\\d+)([a-z]{1,2})", Pattern.CASE_INSENSITIVE);
				for (String part : parts) {
					if (part.isEmpty()) {
						continue;
					}
					
					Matcher matcher = pattern.matcher(part);
					if (!matcher.matches()) {
						return Result.error("Invalid duration format, expected format like '1y 2mo 3w 4d 5h 6m 7s 800ms 900ns' but got '" + part + "'");
					}
					
					long partValue = Long.parseLong(matcher.group(1));
					String unit = matcher.group(2).toLowerCase();
					switch (unit) {
						case "y" -> totalSeconds += partValue * 86400 * 365;
						case "mo" -> totalSeconds += partValue * 86400 * 30;
						case "w" -> totalSeconds += partValue * 86400 * 7;
						case "d" -> totalSeconds += partValue * 86400;
						case "h" -> totalSeconds += partValue * 3600;
						case "m" -> totalSeconds += partValue * 60;
						case "s" -> totalSeconds += partValue;
						case "ms" -> nanos += partValue * 1_000_000;
						case "ns" -> nanos += partValue;
						default -> {
							return Result.error("Unknown time unit, expected one of 'y', 'mo', 'w', 'd', 'h', 'm', 's', 'ms', or 'ns' but got '" + unit + "'");
						}
					}
				}
				return Result.success(Duration.ofSeconds(totalSeconds, nanos));
			} catch (Exception e) {
				return Result.error("Failed to parse duration '" + str + "': " + e.getMessage());
			}
		}
		
		@Override
		public String toString() {
			return "DurationCodec";
		}
	};
	/**
	 * A codec that encodes and decodes {@link Period period} values.<br>
	 * The underlying period is converted to and from a human-readable string format.<br>
	 */
	Codec<Period> PERIOD = new Codec<>() {
		@Override
		public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @Nullable R current, @Nullable Period value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			if (value == null) {
				return Result.error("Unable to encode null as period using '" + this + "'");
			}
			
			if (value.isZero()) {
				return provider.createString("0d");
			}
			
			List<String> parts = Lists.newArrayList();
			if (value.getYears() != 0) {
				parts.add(value.getYears() + "y");
			}
			if (value.getMonths() != 0) {
				parts.add(value.getMonths() + "m");
			}
			if (value.getDays() != 0) {
				parts.add(value.getDays() + "d");
			}
			
			return provider.createString(String.join(" ", parts));
		}
		
		@Override
		public <R> @NotNull Result<Period> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
			Objects.requireNonNull(provider, "Type provider must not be null");
			if (value == null) {
				return Result.error("Unable to decode null value as period using '" + this + "'");
			}
			
			Result<String> result = provider.getString(value);
			if (result.isError()) {
				return Result.error("Unable to decode period from non-string value using '" + this + "': " + result.errorOrThrow());
			}
			
			String str = result.orThrow();
			try {
				if ("0d".equalsIgnoreCase(str)) {
					return Result.success(Period.ZERO);
				}
				
				String[] parts = str.split("\\s+");
				int years = 0;
				int months = 0;
				int days = 0;
				
				Pattern pattern = Pattern.compile("([+-]?\\d+)([a-z]{1,2})", Pattern.CASE_INSENSITIVE);
				for (String part : parts) {
					if (part.isEmpty()) {
						continue;
					}
					
					Matcher matcher = pattern.matcher(part);
					if (!matcher.matches()) {
						return Result.error("Invalid period format, expected format like '1y 2mo 3d' but got '" + part + "'");
					}
					
					int partValue = Integer.parseInt(matcher.group(1));
					String unit = matcher.group(2).toLowerCase();
					
					switch (unit) {
						case "y" -> years += partValue;
						case "mo" -> months += partValue;
						case "d" -> days += partValue;
						default -> {
							return Result.error("Unknown time unit, expected one of 'y', 'mo', or 'd' but got '" + unit + "'");
						}
					}
				}
				return Result.success(Period.of(years, months, days));
			} catch (Exception e) {
				return Result.error("Failed to parse period '" + str + "': " + e.getMessage());
			}
		}
		
		@Override
		public String toString() {
			return "PeriodCodec";
		}
	};
	
	/**
	 * A codec that encodes and decodes {@link Charset charsets}.<br>
	 * The underlying charset is converted to and from a string.<br>
	 */
	Codec<Charset> CHARSET = STRING.mapFlat(Charset::name, throwable(Charset::forName));
	/**
	 * A codec that encodes and decodes {@link File files}.<br>
	 * The underlying file is converted to and from a string.<br>
	 */
	Codec<File> FILE = STRING.mapFlat(File::getPath, throwable(File::new)).codec("FileCodec");
	/**
	 * A codec that encodes and decodes {@link Path paths}.<br>
	 * The underlying path is converted to and from a string.<br>
	 */
	Codec<Path> PATH = STRING.mapFlat(Path::toString, throwable(Path::of)).codec("PathCodec");
	/**
	 * A codec that encodes and decodes {@link java.net.URI URIs}.<br>
	 * The underlying URI is converted to and from a string.<br>
	 */
	Codec<java.net.URI> URI = STRING.mapFlat(java.net.URI::toString, throwable(java.net.URI::new)).codec("URICodec");
	/**
	 * A codec that encodes and decodes {@link java.net.URL URLs}.<br>
	 * The underlying URL is converted to and from a string.<br>
	 */
	Codec<java.net.URL> URL = URI.map(ResultingFunction.throwable(java.net.URL::toURI), throwable(java.net.URI::toURL)).codec("URLCodec");
	
	//region Factories
	
	/**
	 * Creates a new codec from the given encoder and decoder with the given name.<br>
	 * @param encoder The encoder that is used to encode values of the type {@code C}
	 * @param decoder The decoder that is used to decode values of the type {@code C}
	 * @param name The name of the codec
	 * @param <C> The type of the value that is encoded and decoded by the codec
	 * @return A new codec
	 * @throws NullPointerException If the encoder, decoder or name is null
	 */
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
	
	/**
	 * Creates a new keyable codec for the given codec using the given key encoder and key decoder.<br>
	 * The key encoder is the java built-in {@code toString()} method.<br>
	 * The key decoder is defined as function that converts keys from strings to values of the type {@code C}.<br>
	 * If the key decoder is unable to decode a key, it should return null.<br>
	 * @param codec The base codec that is used to encode and decode values of the type {@code C}
	 * @param keyDecoder The key decoder
	 * @param <C> The type of the value that is encoded and decoded by the codec
	 * @return A new keyable codec
	 * @throws NullPointerException If the codec or key decoder is null
	 * @see #keyable(Codec, KeyableEncoder, KeyableDecoder)
	 * @see KeyableCodec
	 */
	static <C> @NotNull KeyableCodec<C> keyable(@NotNull Codec<C> codec, @NotNull Function<String, @Nullable C> keyDecoder) {
		return keyable(codec, C::toString, keyDecoder);
	}
	
	/**
	 * Creates a new keyable codec for the given codec using the given key encoder and key decoder.<br>
	 * The key encoder and key decoder are defined as functions that convert keys of the type {@code C} to and from strings.<br>
	 * If the key decoder is unable to decode a key, it should return null.<br>
	 * @param codec The base codec that is used to encode and decode values of the type {@code C}
	 * @param keyEncoder The key encoder
	 * @param keyDecoder The key decoder
	 * @param <C> The type of the value that is encoded and decoded by the codec
	 * @return A new keyable codec
	 * @throws NullPointerException If the codec, key encoder or key decoder is null
	 * @see #keyable(Codec, KeyableEncoder, KeyableDecoder)
	 * @see KeyableCodec
	 */
	static <C> @NotNull KeyableCodec<C> keyable(@NotNull Codec<C> codec, @NotNull Function<C, String> keyEncoder, @NotNull Function<String, @Nullable C> keyDecoder) {
		return keyable(codec, KeyableEncoder.of(codec, keyEncoder), KeyableDecoder.of(codec, keyDecoder));
	}
	
	/**
	 * Creates a new keyable codec for the given codec using the given key encoder and key decoder.<br>
	 * @param codec The base codec that is used to encode and decode values of the type {@code C}
	 * @param encoder The key encoder that is used to encode keys of the type {@code C}
	 * @param decoder The key decoder that is used to decode keys of the type {@code C}
	 * @param <C> The type of the value that is encoded and decoded by the codec
	 * @return A new keyable codec
	 * @throws NullPointerException If the codec, key encoder or key decoder is null
	 * @see KeyableCodec
	 */
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
	
	/**
	 * Creates a new keyable codec for the given enum class.<br>
	 * The enum value is encoded and decoded using its ordinal value.<br>
	 * @param clazz The enum class
	 * @param <E> The type of the enum
	 * @return A new keyable codec
	 * @throws NullPointerException If the enum class is null
	 * @see KeyableCodec
	 */
	static <E extends Enum<E>> @NotNull KeyableCodec<E> enumOrdinal(@NotNull Class<E> clazz) {
		Objects.requireNonNull(clazz, "Enum class must not be null");
		Map<Integer, E> ordinalLookup = Arrays.stream(clazz.getEnumConstants()).collect(Collectors.toMap(Enum::ordinal, Function.identity()));
		return INTEGER.xmap(Enum::ordinal, ordinalLookup::get).keyable(constant -> String.valueOf(constant.ordinal()), str -> {
			try {
				return ordinalLookup.get(Integer.parseInt(str));
			} catch (NumberFormatException e) {
				return null;
			}
		});
	}
	
	/**
	 * Creates a new keyable codec for the given enum class.<br>
	 * The enum value is encoded and decoded using its name.<br>
	 * @param clazz The enum class
	 * @param <E> The type of the enum
	 * @return A new keyable codec
	 * @throws NullPointerException If the enum class is null
	 * @see KeyableCodec
	 */
	static <E extends Enum<E>> @NotNull KeyableCodec<E> enumName(@NotNull Class<E> clazz) {
		Objects.requireNonNull(clazz, "Enum class must not be null");
		Map<String, E> lookup = Arrays.stream(clazz.getEnumConstants()).collect(Collectors.toMap(Enum::name, Function.identity()));
		return STRING.xmap(Enum::name, lookup::get).keyable(Enum::name, lookup::get);
	}
	
	/**
	 * Creates a new keyable codec for an enum class.<br>
	 * The enum value is encoded and decoded using a friendly name.<br>
	 * @param friendlyEncoder The function that converts an enum value to a friendly name
	 * @param friendlyDecoder The function that converts a friendly name to an enum value
	 * @param <E> The type of the enum
	 * @return A new keyable codec
	 * @throws NullPointerException If the friendly name encoder or decoder is null
	 * @see KeyableCodec
	 */
	static <E extends Enum<E>> @NotNull KeyableCodec<E> friendlyEnumName(@NotNull Function<E, String> friendlyEncoder, @NotNull Function<String, E> friendlyDecoder) {
		Objects.requireNonNull(friendlyEncoder, "Friendly name encoder must not be null");
		Objects.requireNonNull(friendlyDecoder, "Friendly name decoder must not be null");
		return STRING.xmap(friendlyEncoder, friendlyDecoder).keyable(friendlyEncoder, friendlyDecoder);
	}
	
	/**
	 * Creates a new keyable codec for an enum class.<br>
	 * The enum is encoded using the name of the enum value.<br>
	 * The decoder supports both the name and the ordinal value of the enum.<br>
	 * @param clazz The enum class
	 * @param <E> The type of the enum
	 * @return A new keyable codec
	 * @throws NullPointerException If the enum class is null
	 * @see KeyableCodec
	 */
	static <E extends Enum<E>> @NotNull KeyableCodec<E> dynamicEnum(@NotNull Class<E> clazz) {
		Objects.requireNonNull(clazz, "Enum class must not be null");
		E[] constants = clazz.getEnumConstants();
		Map<Integer, E> ordinalLookup = Arrays.stream(constants).collect(Collectors.toMap(Enum::ordinal, Function.identity()));
		Map<String, E> nameLookup = Arrays.stream(constants).collect(Collectors.toMap(Enum::name, Function.identity()));
		return either(INTEGER, STRING).xmap(
			constant -> Either.right(constant.name()),
			either -> either.mapTo(ordinalLookup::get, nameLookup::get)
		).keyable(Enum::name, nameLookup::get);
	}
	
	/**
	 * Creates a new keyable string codec for the given maximum length.<br>
	 * @param length The maximum length of the string (inclusive)
	 * @return A new keyable string codec
	 * @throws IllegalArgumentException If the length is less than 0
	 * @see KeyableCodec
	 */
	static @NotNull KeyableCodec<String> string(int length) {
		if (0 > length) {
			throw new IllegalArgumentException("Length must be at least 0");
		}
		return string(0, length);
	}
	
	/**
	 * Creates a new keyable string codec for the given minimum and maximum length.<br>
	 * @param minLength The minimum length of the string (inclusive)
	 * @param maxLength The maximum length of the string (inclusive)
	 * @return A new keyable string codec
	 * @throws IllegalArgumentException If the minimum length is less than zero or greater than the maximum length
	 * @see KeyableCodec
	 */
	static @NotNull KeyableCodec<String> string(int minLength, int maxLength) {
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
		}).keyable(Function.identity(), Function.identity());
	}
	
	/**
	 * Creates a new keyable string codec for non-empty strings.<br>
	 * The string must not be empty after encoding and decoding.<br>
	 * @return A new keyable string codec
	 * @see KeyableCodec
	 */
	static @NotNull KeyableCodec<String> noneEmptyString() {
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
		}).keyable(Function.identity(), Function.identity());
	}
	
	/**
	 * Creates a new unit codec for the given value.<br>
	 * @param value The value
	 * @param <C> The type of the value
	 * @return A new unit codec
	 * @see UnitCodec
	 */
	static <C> @NotNull Codec<C> unit(@Nullable C value) {
		return unit(() -> value);
	}
	
	/**
	 * Creates a new unit codec for the given supplier.<br>
	 * @param supplier The supplier that provides the value
	 * @param <C> The type of the value
	 * @return A new unit codec
	 * @throws NullPointerException If the supplier is null
	 * @see UnitCodec
	 */
	static <C> @NotNull Codec<C> unit(@NotNull Supplier<C> supplier) {
		return new UnitCodec<>(supplier);
	}
	
	/**
	 * Creates a new optional codec for the given codec.<br>
	 * @param codec The base codec
	 * @param <C> The type of the value that is encoded and decoded by the codec
	 * @return A new optional codec
	 * @throws NullPointerException If the codec is null
	 * @see OptionalCodec
	 */
	static <C> @NotNull Codec<Optional<C>> optional(@NotNull Codec<C> codec) {
		return new OptionalCodec<>(codec);
	}
	
	/**
	 * Creates a new list codec for the given codec.<br>
	 * The created list codec has no size restrictions.<br>
	 * @param codec The base codec
	 * @param <C> The type of the list elements that are encoded and decoded by the codec
	 * @return A new list codec
	 * @throws NullPointerException If the codec is null
	 * @see ListCodec
	 */
	static <C> @NotNull Codec<List<C>> list(@NotNull Codec<C> codec) {
		return new ListCodec<>(codec);
	}
	
	/**
	 * Creates a new list codec for the given codec and maximum size.<br>
	 * The list must have at most the maximum size after encoding and decoding.<br>
	 * @param codec The base codec
	 * @param maxSize The maximum size of the list (inclusive)
	 * @param <C> The type of the list elements that are encoded and decoded by the codec
	 * @return A new list codec
	 * @throws NullPointerException If the codec is null
	 * @throws IllegalArgumentException If the maximum size is less than 0
	 * @see ListCodec
	 */
	static <C> @NotNull Codec<List<C>> list(@NotNull Codec<C> codec, int maxSize) {
		return list(codec, 0, maxSize);
	}
	
	/**
	 * Creates a new list codec for the given codec and minimum and maximum size.<br>
	 * The list must have at least the minimum size and at most the maximum size after encoding and decoding.<br>
	 * @param codec The base codec
	 * @param minSize The minimum size of the list (inclusive)
	 * @param maxSize The maximum size of the list (inclusive)
	 * @param <C> The type of the list elements that are encoded and decoded by the codec
	 * @return A new list codec
	 * @throws NullPointerException If the codec is null
	 * @throws IllegalArgumentException If the minimum size is less than zero or greater than the maximum size
	 * @see ListCodec
	 */
	static <C> @NotNull Codec<List<C>> list(@NotNull Codec<C> codec, int minSize, int maxSize) {
		return new ListCodec<>(codec, minSize, maxSize);
	}
	
	/**
	 * Creates a new list codec for the given codec for non-empty lists.<br>
	 * The list must not be empty after encoding and decoding.<br>
	 * @param codec The base codec
	 * @param <C> The type of the list elements that are encoded and decoded by the codec
	 * @return A new list codec
	 * @throws NullPointerException If the codec is null
	 * @see ListCodec
	 */
	static <C> @NotNull Codec<List<C>> noneEmptyList(@NotNull Codec<C> codec) {
		return list(codec, 1);
	}
	
	/**
	 * Creates a new stream codec for the given codec.<br>
	 * @param codec The base codec
	 * @param <C> The type of the stream elements that are encoded and decoded by the codec
	 * @return A new stream codec
	 * @throws NullPointerException If the codec is null
	 */
	static <C> @NotNull Codec<Stream<C>> stream(@NotNull Codec<C> codec) {
		Objects.requireNonNull(codec, "Codec must not be null");
		return codec.list().xmap(Stream::toList, List::stream).codec("StreamCodec[" + codec + "]");
	}
	
	/**
	 * Creates a new map codec with {@link #STRING} as key codec and the given value codec.<br>
	 * The created map codec has no size restrictions.<br>
	 * @param valueCodec The value codec
	 * @param <C> The type of the map values that are encoded and decoded by the codec
	 * @return A new map codec
	 * @throws NullPointerException If the value codec is null
	 * @see MapCodec
	 */
	static <C> @NotNull Codec<Map<String, C>> map(@NotNull Codec<C> valueCodec) {
		return map(STRING, valueCodec);
	}
	
	/**
	 * Creates a new map codec with the given key codec and value codec.<br>
	 * The created map codec has no size restrictions.<br>
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @param <K> The type of the map keys that are encoded and decoded by the codec
	 * @param <V> The type of the map values that are encoded and decoded by the codec
	 * @return A new map codec
	 * @throws NullPointerException If the key codec or value codec is null
	 * @see MapCodec
	 */
	static <K, V> @NotNull Codec<Map<K, V>> map(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec) {
		return new MapCodec<>(keyCodec, valueCodec);
	}
	
	/**
	 * Creates a new map codec with the given key codec and value codec and maximum size.<br>
	 * The map must have at most the maximum size after encoding and decoding.<br>
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @param maxSize The maximum size of the map (inclusive)
	 * @param <K> The type of the map keys that are encoded and decoded by the codec
	 * @param <V> The type of the map values that are encoded and decoded by the codec
	 * @return A new map codec
	 * @throws NullPointerException If the key codec or value codec is null
	 * @throws IllegalArgumentException If the maximum size is less than 0
	 * @see MapCodec
	 */
	static <K, V> @NotNull Codec<Map<K, V>> map(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec, int maxSize) {
		return map(keyCodec, valueCodec, 0, maxSize);
	}
	
	/**
	 * Creates a new map codec with the given key codec and value codec and minimum and maximum size.<br>
	 * The map must have at least the minimum size and at most the maximum size after encoding and decoding.<br>
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @param minSize The minimum size of the map (inclusive)
	 * @param maxSize The maximum size of the map (inclusive)
	 * @param <K> The type of the map keys that are encoded and decoded by the codec
	 * @param <V> The type of the map values that are encoded and decoded by the codec
	 * @return A new map codec
	 * @throws NullPointerException If the key codec or value codec is null
	 * @throws IllegalArgumentException If the minimum size is less than zero or greater than the maximum size
	 * @see MapCodec
	 */
	static <K, V> @NotNull Codec<Map<K, V>> map(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec, int minSize, int maxSize) {
		return new MapCodec<>(keyCodec, valueCodec, minSize, maxSize);
	}
	
	/**
	 * Creates a new map codec with the given key codec and value codec for non-empty maps.<br>
	 * The map must not be empty after encoding and decoding.<br>
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @param <K> The type of the map keys that are encoded and decoded by the codec
	 * @param <V> The type of the map values that are encoded and decoded by the codec
	 * @return A new map codec
	 * @throws NullPointerException If the key codec or value codec is null
	 * @see MapCodec
	 */
	static <K, V> @NotNull Codec<Map<K, V>> noneEmptyMap(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec) {
		return map(keyCodec, valueCodec, 1);
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
	 * @param firstCodec The first codec
	 * @param secondCodec The second codec
	 * @param <F> The type of the first value
	 * @param <S> The type of the second value
	 * @return A new either codec
	 * @throws NullPointerException If the first codec or second codec is null
	 * @see EitherCodec
	 */
	static <F, S> @NotNull Codec<Either<F, S>> either(@NotNull Codec<F> firstCodec, @NotNull Codec<S> secondCodec) {
		return new EitherCodec<>(firstCodec, secondCodec);
	}
	
	/**
	 * Creates a new codec that uses the given codec as the main codec and the given codec as alternative codec.<br>
	 * If the main codec fails to encode or decode a value, the alternative codec is used.<br>
	 * @param main The main codec
	 * @param alternative The alternative codec
	 * @param <C> The type of the value that is encoded and decoded by the codec
	 * @return A new codec
	 * @throws NullPointerException If the main codec or alternative codec is null
	 */
	static <C> @NotNull Codec<C> withAlternative(@NotNull Codec<C> main, @NotNull Codec<? extends C> alternative) {
		return either(main, alternative).xmap(
			Either::left,
			either -> either.mapTo(Function.identity(), Function.identity())
		);
	}
	
	/**
	 * Creates a new codec that encodes and decodes values of the type {@code C} to and from strings.<br>
	 * The string encoder and decoder are defined as functions that convert values of the type {@code C} to and from strings.<br>
	 * If the decoder is unable to decode a string, it should return null.<br>
	 * @param stringEncoder The encoder function
	 * @param stringDecoder The decoder function
	 * @param <E> The type of the value that is encoded and decoded by the codec
	 * @return A new codec
	 * @throws NullPointerException If the string encoder or decoder is null
	 */
	static <E> @NotNull Codec<E> stringResolver(@NotNull Function<E, String> stringEncoder, @NotNull Function<String, @Nullable E> stringDecoder) {
		Objects.requireNonNull(stringDecoder, "Element string decoder must not be null");
		return STRING.mapFlat(
			stringEncoder,
			result -> {
				if (result.isSuccess()) {
					E value;
					try {
						value = stringDecoder.apply(result.orThrow());
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
	
	/**
	 * Wraps the current codec into a new codec with the given codec name.<br>
	 *
	 * @param name The name of the codec
	 * @return A new codec
	 * @throws NullPointerException If the codec name is null
	 * @see #of(Encoder, Decoder, String)
	 */
	private @NotNull Codec<C> codec(@NotNull String name) {
		return of(this, this, name);
	}
	
	/**
	 * Wraps the current codec into a new keyable codec using the given key encoder and key decoder.<br>
	 * The key encoder and key decoder are defined as functions that convert keys of the type C to and from strings.<br>
	 * If the key decoder is unable to decode a key, it should return null.<br>
	 *
	 * @param keyEncoder The key encoder
	 * @param keyDecoder The key decoder
	 * @return A new keyable codec
	 * @throws NullPointerException If the key encoder or key decoder is null
	 * @see #keyable(Codec, Function, Function)
	 * @see KeyableCodec
	 */
	default @NotNull KeyableCodec<C> keyable(@NotNull Function<C, String> keyEncoder, @NotNull Function<String, @Nullable C> keyDecoder) {
		return keyable(this, keyEncoder, keyDecoder);
	}
	
	/**
	 * Wraps the current codec into a new optional codec.<br>
	 *
	 * @return A new optional codec for the current codec
	 * @see #optional(Codec)
	 * @see OptionalCodec
	 */
	default @NotNull Codec<Optional<C>> optional() {
		return optional(this);
	}
	
	/**
	 * Creates a new list codec uses the current codec as element codec for the list codec.<br>
	 * The created list codec has no size restrictions.<br>
	 *
	 * @return A new list codec for the current codec
	 * @see #list(Codec)
	 * @see ListCodec
	 */
	default @NotNull Codec<List<C>> list() {
		return list(this);
	}
	
	/**
	 * Creates a new list codec uses the current codec as element codec for the list codec with the given maximum size.<br>
	 * The list must have at most the maximum size after encoding and decoding.<br>
	 *
	 * @param maxSize The maximum size of the list (inclusive)
	 * @return A new list codec for the current codec
	 * @throws IllegalArgumentException If the maximum size is less than 0
	 * @see #list(Codec, int)
	 * @see ListCodec
	 */
	default @NotNull Codec<List<C>> list(int maxSize) {
		return list(this, maxSize);
	}
	
	/**
	 * Creates a new list codec uses the current codec as element codec for the list codec with the given minimum and maximum size.<br>
	 * The list must have at least the minimum size and at most the maximum size after encoding and decoding.<br>
	 *
	 * @param minSize The minimum size of the list (inclusive)
	 * @param maxSize The maximum size of the list (inclusive)
	 * @return A new list codec for the current codec
	 * @throws IllegalArgumentException If the minimum size is less than zero or greater than the maximum size
	 * @see #list(Codec, int, int)
	 * @see ListCodec
	 */
	default @NotNull Codec<List<C>> list(int minSize, int maxSize) {
		return list(this, minSize, maxSize);
	}
	
	/**
	 * Creates a new list codec uses the current codec as element codec for the list codec for non-empty lists.<br>
	 * The list must not be empty after encoding and decoding.<br>
	 *
	 * @return A new list codec for the current codec
	 * @see #noneEmptyList(Codec)
	 * @see ListCodec
	 */
	default @NotNull Codec<List<C>> noneEmptyList() {
		return noneEmptyList(this);
	}
	
	/**
	 * Creates a new stream codec uses the current codec as element codec for the stream codec.<br>
	 *
	 * @return A new stream codec for the current codec
	 * @see #stream(Codec)
	 */
	default @NotNull Codec<Stream<C>> stream() {
		return stream(this);
	}
	
	/**
	 * Creates a new codec that uses the current codec as the main codec and the given codec as alternative codec.<br>
	 * If the main codec fails to encode or decode a value, the alternative codec is used.<br>
	 *
	 * @param alternative The alternative codec
	 * @return A new codec
	 * @throws NullPointerException If the alternative codec is null
	 * @see #withAlternative(Codec, Codec)
	 */
	default @NotNull Codec<C> withAlternative(@NotNull Codec<? extends C> alternative) {
		return withAlternative(this, alternative);
	}
	
	/**
	 * Creates a new mapped codec of type {@code O} from the current codec.<br>
	 * <p>
	 *     The mapped codec maps the raw input and output values using the given functions.<br>
	 *     The functions are applied before encoding and after decoding the base codec, on the raw values.<br>
	 *     Any errors that occur during mapping must be self-contained and should not affect the base codec.
	 * </p>
	 *
	 * @param to The encoding mapping function
	 * @param from The decoding mapping function
	 * @param <O> The type of the mapped value
	 * @return A new mapped codec
	 * @throws NullPointerException If the encoding mapping function or decoding mapping function is null
	 * @see #mapFlat(Function, ResultMappingFunction)
	 * @see #map(ResultingFunction, ResultMappingFunction)
	 */
	default <O> @NotNull Codec<O> xmap(@NotNull Function<O, C> to, @NotNull Function<C, O> from) {
		return this.map(ResultingFunction.direct(to), direct(from));
	}
	
	/**
	 * Creates a new mapped codec of type {@code O} from the current codec.<br>
	 * <p>
	 *     The mapped codec maps the raw input value using the given function.<br>
	 *     The function is applied before encoding the base codec.
	 * </p>
	 * <p>
	 *     This mapping functions allows the handling of errors that occur during decode-mapping.<br>
	 *     Therefor the mapping function is applied to the result of the base codec.<br>
	 *     The mapping function must return a new result that contains the mapped value or an error message.
	 * </p>
	 *
	 * @param to The encoding mapping function
	 * @param from The decoding mapping function
	 * @param <O> The type of the mapped value
	 * @return A new mapped codec
	 * @throws NullPointerException If the encoding mapping function or decoding mapping function is null
	 * @see #xmap(Function, Function)
	 * @see #map(ResultingFunction, ResultMappingFunction)
	 */
	default <O> @NotNull Codec<O> mapFlat(@NotNull Function<O, C> to, @NotNull ResultMappingFunction<C, O> from) {
		return this.map(ResultingFunction.direct(to), from);
	}
	
	/**
	 * Creates a new mapped codec of type {@code O} from the current codec.<br>
	 * This mapping functions allows the handling of errors that occur during mapping.<br>
	 * <p>
	 *     The encode-mapping function is applied before encoding the base codec.<br>
	 *     The function can return a new result that contains the mapped value or an error message.
	 * </p>
	 * <p>
	 *     The decode-mapping function is applied to the result of the base codec.<br>
	 *     The function can return a new result that contains the mapped value or an error message.
	 * </p>
	 *
	 * @param to The encoding mapping function
	 * @param from The decoding mapping function
	 * @param <O> The type of the mapped value
	 * @return A new mapped codec
	 * @throws NullPointerException If the encoding mapping function or decoding mapping function is null
	 * @see #xmap(Function, Function)
	 * @see #mapFlat(Function, ResultMappingFunction)
	 */
	default <O> @NotNull Codec<O> map(@NotNull ResultingFunction<O, C> to, @NotNull ResultMappingFunction<C, O> from) {
		return of(this.mapEncoder(to), this.mapDecoder(from), "MappedCodec[" + this + "]");
	}
	
	/**
	 * Creates a new codec that will validate the result of the decoding process using the given validator function.<br>
	 * The validator function is applied to the decoded value and must return a result that contains the validated value or an error message.<br>
	 *
	 * @param validator The validator function
	 * @return A new codec
	 * @throws NullPointerException If the validator function is null
	 */
	default @NotNull Codec<C> validate(@NotNull Function<C, Result<C>> validator) {
		Objects.requireNonNull(validator, "Validator function must not be null");
		return this.mapFlat(Function.identity(), result -> result.flatMap(validator));
	}
	
	/**
	 * Creates a new codec that will return the given default value in an error case during decoding.<br>
	 *
	 * @param defaultValue The default value
	 * @return A new codec
	 * @see #orElseGet(Supplier)
	 */
	default @NotNull Codec<C> orElse(@Nullable C defaultValue) {
		return this.orElseGet(() -> defaultValue);
	}
	
	/**
	 * Creates a new codec that will return the value provided by the given supplier in an error case during decoding.<br>
	 *
	 * @param supplier The default value supplier
	 * @return A new codec
	 * @throws NullPointerException If the default value supplier is null
	 */
	default @NotNull Codec<C> orElseGet(@NotNull Supplier<C> supplier) {
		Objects.requireNonNull(supplier, "Default value supplier must not be null");
		return of(this, this.mapDecoder(result -> Result.success(result.orElseGet(supplier))), "OrElseCodec[" + this + "]");
	}
	
	/**
	 * Creates a new named codec with the current codec using the given name and aliases.<br>
	 *
	 * @param name The name of the codec
	 * @param aliases The aliases of the codec
	 * @return A new named codec
	 * @throws NullPointerException If the codec name is null
	 * @see NamedCodec
	 */
	default @NotNull Codec<C> named(@NotNull String name, String @NotNull ... aliases) {
		return new NamedCodec<>(this, name, aliases);
	}
	
	/**
	 * Creates a new configured codec.<br>
	 * The configured codec is used in the codec builder to create codecs for complex data structures.<br>
	 * The configured codec encodes and decodes components of the data structure using the given getter function.<br>
	 * It is expected that this is only called on named codecs.<br>
	 *
	 * @param getter The getter function
	 * @param <O> The type of the object which contains the component
	 * @return A new configured codec
	 * @throws NullPointerException If the getter function is null
	 * @see #named(String, String...)
	 * @see #configure(String, Function)
	 * @see ConfiguredCodec
	 */
	default <O> @NotNull ConfiguredCodec<C, O> getter(@NotNull Function<O, C> getter) {
		return new ConfiguredCodec<>(this, getter);
	}
	
	/**
	 * Creates a new configured codec.<br>
	 * This method combines the {@link #named(String, String...)} and {@link #getter(Function)} methods.<br>
	 * <p>
	 *     The configured codec is used in the codec builder to create codecs for complex data structures.<br>
	 *     The configured codec encodes and decodes components of the data structure using the given getter function.
	 * </p>
	 *
	 * @param name The name of the codec
	 * @param getter The getter function
	 * @param <O> The type of the object which contains the component
	 * @return A new configured codec
	 * @throws NullPointerException If the codec name or getter function is null
	 * @see #named(String, String...)
	 * @see #getter(Function)
	 */
	default <O> @NotNull ConfiguredCodec<C, O> configure(@NotNull String name, @NotNull Function<O, C> getter) {
		return new ConfiguredCodec<>(this.named(name), getter);
	}
}
