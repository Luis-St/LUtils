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

import net.luis.utils.io.codec.types.UUIDCodec;
import net.luis.utils.io.codec.types.array.*;
import net.luis.utils.io.codec.types.i18n.CurrencyCodec;
import net.luis.utils.io.codec.types.i18n.LocaleCodec;
import net.luis.utils.io.codec.types.io.*;
import net.luis.utils.io.codec.types.network.InetAddressCodec;
import net.luis.utils.io.codec.types.network.InetSocketAddressCodec;
import net.luis.utils.io.codec.types.primitive.*;
import net.luis.utils.io.codec.types.primitive.numeric.*;
import net.luis.utils.io.codec.types.stream.*;
import net.luis.utils.io.codec.types.struct.*;
import net.luis.utils.io.codec.types.struct.collection.MapCodec;
import net.luis.utils.io.codec.types.time.*;
import net.luis.utils.io.codec.types.time.local.*;
import net.luis.utils.io.codec.types.time.offset.OffsetDateTimeCodec;
import net.luis.utils.io.codec.types.time.offset.OffsetTimeCodec;
import net.luis.utils.io.codec.types.time.zoned.*;
import net.luis.utils.util.Either;
import net.luis.utils.util.result.Result;
import net.luis.utils.util.result.ResultingFunction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
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
	 * A codec that encodes and decodes {@link Boolean boolean} values.<br>
	 */
	public static final BooleanCodec BOOLEAN = new BooleanCodec();
	/**
	 * A range codec that encodes and decodes {@link Byte byte} values.<br>
	 */
	public static final ByteCodec BYTE = new ByteCodec();
	/**
	 * A range codec that encodes and decodes {@link Short short} values.<br>
	 */
	public static final ShortCodec SHORT = new ShortCodec();
	/**
	 * A range codec that encodes and decodes {@link Integer integer} values.<br>
	 */
	public static final IntegerCodec INTEGER = new IntegerCodec();
	/**
	 * A range codec that encodes and decodes {@link Long long} values.<br>
	 */
	public static final LongCodec LONG = new LongCodec();
	/**
	 * A range codec that encodes and decodes {@link Float float} values.<br>
	 */
	public static final FloatCodec FLOAT = new FloatCodec();
	/**
	 * A range codec that encodes and decodes {@link Double double} values.<br>
	 */
	public static final DoubleCodec DOUBLE = new DoubleCodec();
	/**
	 * A codec that encodes and decodes {@link BigInteger big integer} values.<br>
	 */
	public static final BigIntegerCodec BIG_INTEGER = new BigIntegerCodec();
	/**
	 * A codec that encodes and decodes {@link BigDecimal big decimal} values.<br>
	 */
	public static final BigDecimalCodec BIG_DECIMAL = new BigDecimalCodec();
	/**
	 * A codec that encodes and decodes {@link String strings}.<br>
	 */
	public static final StringCodec STRING = new StringCodec();
	/**
	 * A keyable codec that encodes and decodes {@link Character characters}.<br>
	 */
	public static final CharacterCodec CHARACTER = new CharacterCodec();
	/**
	 * A codec that encodes and decodes boolean arrays.<br>
	 * The underlying boolean array is converted to and from a list of booleans.<br>
	 */
	public static final BooleanArrayCodec BOOLEAN_ARRAY = new BooleanArrayCodec();
	/**
	 * A codec that encodes and decodes byte arrays.<br>
	 * The underlying byte array is converted to and from a list of bytes.<br>
	 */
	public static final ByteArrayCodec BYTE_ARRAY = new ByteArrayCodec();
	/**
	 * A codec that encodes and decodes short arrays.<br>
	 * The underlying byte array is converted to and from a list of shorts.<br>
	 */
	public static final ShortArrayCodec SHORT_ARRAY = new ShortArrayCodec();
	/**
	 * A codec that encodes and decodes int arrays.<br>
	 * The underlying byte array is converted to and from a list of integers.<br>
	 */
	public static final IntegerArrayCodec INTEGER_ARRAY = new IntegerArrayCodec();
	/**
	 * A codec that encodes and decodes long arrays.<br>
	 * The underlying byte array is converted to and from a list of longs.<br>
	 */
	public static final LongArrayCodec LONG_ARRAY = new LongArrayCodec();
	/**
	 * A codec that encodes and decodes float arrays.<br>
	 * The underlying byte array is converted to and from a list of floats.<br>
	 */
	public static final FloatArrayCodec FLOAT_ARRAY = new FloatArrayCodec();
	/**
	 * A codec that encodes and decodes double arrays.<br>
	 * The underlying byte array is converted to and from a list of doubles.<br>
	 */
	public static final DoubleArrayCodec DOUBLE_ARRAY = new DoubleArrayCodec();
	/**
	 * A codec that encodes and decodes character arrays.<br>
	 * The underlying character array is converted to and from a list of characters.<br>
	 */
	public static final CharacterArrayCodec CHARACTER_ARRAY = new CharacterArrayCodec();
	/**
	 * A codec that encodes and decodes {@link IntStream int streams}.<br>
	 * The underlying int stream is converted to and from a {@link Stream} of integers.<br>
	 */
	public static final IntStreamCodec INT_STREAM = new IntStreamCodec();
	/**
	 * A codec that encodes and decodes {@link LongStream long streams}.<br>
	 * The underlying long stream is converted to and from a {@link Stream} of longs.<br>
	 */
	public static final LongStreamCodec LONG_STREAM = new LongStreamCodec();
	/**
	 * A codec that encodes and decodes {@link DoubleStream double streams}.<br>
	 * The underlying double stream is converted to and from a {@link Stream} of doubles.<br>
	 */
	public static final DoubleStreamCodec DOUBLE_STREAM = new DoubleStreamCodec();
	/**
	 * A keyable codec that encodes and decodes {@link UUID UUIDs}.<br>
	 * The underlying UUID is converted to and from a string.<br>
	 */
	public static final UUIDCodec UUID = new UUIDCodec();
	/**
	 * A codec that encodes and decodes {@link DayOfWeek day of week} values.<br>
	 * The underlying day of the week is converted to and from a string.<br>
	 */
	public static final DayOfWeekCodec DAY_OF_WEEK = new DayOfWeekCodec();
	/**
	 * A codec that encodes and decodes {@link Month month} values.<br>
	 * The underlying month is converted to and from a string.<br>
	 */
	public static final MonthCodec MONTH = new MonthCodec();
	/**
	 * A codec that encodes and decodes {@link Year year} values.<br>
	 * The underlying year is converted to and from an integer.<br>
	 */
	public static final YearCodec YEAR = new YearCodec();
	/**
	 * A codec that encodes and decodes {@link LocalTime local time} values.<br>
	 * The underlying local time is converted to and from a string.<br>
	 */
	public static final LocalTimeCodec LOCAL_TIME = new LocalTimeCodec();
	/**
	 * A codec that encodes and decodes {@link LocalDate local date} values.<br>
	 * The underlying local date is converted to and from a string.<br>
	 */
	public static final LocalDateCodec LOCAL_DATE = new LocalDateCodec();
	/**
	 * A codec that encodes and decodes {@link LocalDateTime local date time} values.<br>
	 * The underlying local date time is converted to and from a string.<br>
	 */
	public static final LocalDateTimeCodec LOCAL_DATE_TIME = new LocalDateTimeCodec();
	/**
	 * A codec that encodes and decodes {@link OffsetDateTime offset date time} values.<br>
	 * The underlying offset date time is converted to and from a string.<br>
	 */
	public static final OffsetTimeCodec OFFSET_TIME = new OffsetTimeCodec();
	/**
	 * A codec that encodes and decodes {@link OffsetDateTime offset date time} values.<br>
	 * The underlying offset date time is converted to and from a string.<br>
	 */
	public static final OffsetDateTimeCodec OFFSET_DATE_TIME = new OffsetDateTimeCodec();
	/**
	 * A codec that encodes and decodes {@link ZonedDateTime zoned date time} values.<br>
	 * The underlying zoned date time is converted to and from a string.<br>
	 */
	public static final ZonedDateTimeCodec ZONED_DATE_TIME = new ZonedDateTimeCodec();
	/**
	 * A codec that encodes and decodes {@link ZoneId zone ID} values.<br>
	 * The underlying zone ID is converted to and from a string.<br>
	 */
	public static final ZoneIdCodec ZONE_ID = new ZoneIdCodec();
	/**
	 * A codec that encodes and decodes {@link ZoneOffset zone offset} values.<br>
	 * The underlying zone offset is converted to and from a string.<br>
	 */
	public static final ZoneOffsetCodec ZONE_OFFSET = new ZoneOffsetCodec();
	/**
	 * A codec that encodes and decodes {@link Instant instant} values.<br>
	 * The underlying instant is converted to and from a string using ISO-8601 format.<br>
	 */
	public static final InstantCodec INSTANT = new InstantCodec();
	/**
	 * A codec that encodes and decodes {@link Duration duration} values.<br>
	 * The underlying duration is converted to and from a human-readable string format.<br>
	 */
	public static final DurationCodec DURATION = new DurationCodec();
	/**
	 * A codec that encodes and decodes {@link Period period} values.<br>
	 * The underlying period is converted to and from a human-readable string format.<br>
	 */
	public static final PeriodCodec PERIOD = new PeriodCodec();
	/**
	 * A codec that encodes and decodes {@link Charset charsets}.<br>
	 * The underlying charset is converted to and from a string.<br>
	 */
	public static final CharsetCodec CHARSET = new CharsetCodec();
	/**
	 * A codec that encodes and decodes {@link Path paths}.<br>
	 * The underlying path is converted to and from a string.<br>
	 */
	public static final PathCodec PATH = new PathCodec();
	/**
	 * A codec that encodes and decodes {@link java.net.URI URIs}.<br>
	 * The underlying URI is converted to and from a string.<br>
	 */
	public static final URICodec URI = new URICodec();
	/**
	 * A codec that encodes and decodes {@link InetAddress inet addresses}.<br>
	 * The underlying inet address is converted to and from a string.<br>
	 */
	public static final InetAddressCodec INET_ADDRESS = new InetAddressCodec();
	/**
	 * A codec that encodes and decodes {@link InetSocketAddress inet socket addresses}.<br>
	 * The underlying inet socket address is converted to and from a string.<br>
	 */
	public static final InetSocketAddressCodec INET_SOCKET_ADDRESS = new InetSocketAddressCodec();
	/**
	 * A codec that encodes and decodes {@link Locale locales}.<br>
	 * The underlying locale is converted to and from a string.<br>
	 */
	public static final LocaleCodec LOCALE = new LocaleCodec();
	/**
	 * A codec that encodes and decodes {@link Currency currencies}.<br>
	 * The underlying currency is converted to and from a string.<br>
	 */
	public static final CurrencyCodec CURRENCY = new CurrencyCodec();
	/**
	 * A codec that encodes and decodes byte arrays using Base64 encoding.<br>
	 * The underlying byte array is converted to and from a Base64-encoded string.<br>
	 */
	public static final Base64Codec BASE64 = new Base64Codec();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Codecs() {}
	
	/**
	 * Creates a new codec for the given enum class.<br>
	 * The enum value is encoded and decoded using its ordinal value.<br>
	 *
	 * @param clazz The enum class
	 * @param <E> The type of the enum
	 * @return A new keyable codec
	 * @throws NullPointerException If the enum class is null
	 */
	public static <E extends Enum<E>> @NonNull Codec<E> enumOrdinal(@NonNull Class<E> clazz) {
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
	 */
	public static <E extends Enum<E>> @NonNull Codec<E> enumName(@NonNull Class<E> clazz) {
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
	 */
	public static <E extends Enum<E>> @NonNull Codec<E> friendlyEnumName(@NonNull Function<E, String> friendlyEncoder, @NonNull Function<String, E> friendlyDecoder) {
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
	 */
	public static <E extends Enum<E>> @NonNull Codec<E> dynamicEnum(@NonNull Class<E> clazz) {
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
	 * Creates a new any codec that attempts to encode or decode using multiple codecs in sequence.<br>
	 * This codec tries each provided codec in order until one succeeds. If all codecs fail,
	 * an error is returned containing all the individual error messages.<br>
	 * <p>
	 * This is particularly useful for polymorphic types where multiple subtypes need to be handled dynamically,
	 * such as different payment method implementations or various message types.
	 * </p>
	 *
	 * @param codecs The list of codecs to try in sequence
	 * @param <C> The common supertype of values handled by this codec
	 * @return A new any codec
	 * @throws NullPointerException If codecs is null or contains null elements
	 * @throws IllegalArgumentException If codecs is empty or contains only one codec
	 * @see AnyCodec
	 */
	public static <C> @NonNull AnyCodec<C> any(@NonNull List<Codec<? extends C>> codecs) {
		return new AnyCodec<>(codecs);
	}
	
	/**
	 * Creates a new any codec that attempts to encode or decode using multiple codecs in sequence.<br>
	 * This codec tries each provided codec in order until one succeeds. If all codecs fail,
	 * an error is returned containing all the individual error messages.<br>
	 * <p>
	 * This is particularly useful for polymorphic types where multiple subtypes need to be handled dynamically,
	 * such as different payment method implementations or various message types.
	 * </p>
	 *
	 * @param codecs The array of codecs to try in sequence
	 * @param <C> The common supertype of values handled by this codec
	 * @return A new any codec
	 * @throws NullPointerException If codecs is null or contains null elements
	 * @throws IllegalArgumentException If codecs is empty or contains only one codec
	 * @see AnyCodec
	 */
	@SafeVarargs
	public static <C> @NonNull AnyCodec<C> any(Codec<? extends C> @NonNull ... codecs) {
		return new AnyCodec<>(codecs);
	}
	
	/**
	 * Creates a new map codec with {@link Codecs#STRING} as key codec and the given value codec.<br>
	 * The created map codec has no size restrictions.<br>
	 *
	 * @param valueCodec The value codec
	 * @param <C> The type of the map values that are encoded and decoded by the codec
	 * @return A new map codec
	 * @throws NullPointerException If the value codec is null
	 * @see MapCodec
	 */
	public static <C> @NonNull MapCodec<String, C> map(@NonNull Codec<C> valueCodec) {
		return map(STRING, valueCodec);
	}
	
	/**
	 * Creates a new map codec with the given key codec and value codec.<br>
	 * The created map codec has no size restrictions.<br>
	 *
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @param <K> The type of the map keys that are encoded and decoded by the codec
	 * @param <V> The type of the map values that are encoded and decoded by the codec
	 * @return A new map codec
	 * @throws NullPointerException If the key codec or value codec is null
	 * @see MapCodec
	 */
	public static <K, V> @NonNull MapCodec<K, V> map(@NonNull Codec<K> keyCodec, @NonNull Codec<V> valueCodec) {
		return new MapCodec<>(keyCodec, valueCodec);
	}
	
	/**
	 * Creates a new either codec for the given codecs.<br>
	 * The value which is encoded and decoded by this codec can be either of type {@code F} or {@code S}.<br>
	 * The codec will try to use the first codec to encode and decode the value,<br>
	 * if that fails, it will try to use the second codec.<br>
	 *
	 * @param firstCodec The first codec
	 * @param secondCodec The second codec
	 * @param <F> The type of the first value
	 * @param <S> The type of the second value
	 * @return A new either codec
	 * @throws NullPointerException If the first codec or second codec is null
	 * @see EitherCodec
	 */
	public static <F, S> @NonNull EitherCodec<F, S> either(@NonNull Codec<F> firstCodec, @NonNull Codec<S> secondCodec) {
		return new EitherCodec<>(firstCodec, secondCodec);
	}
	
	/**
	 * Creates a new discriminated codec that selects the appropriate codec based on a discriminator field.<br>
	 * This codec reads a discriminator field from the parent object to determine which codec to use
	 * for encoding or decoding the actual value.<br>
	 * <p>
	 *     This is particularly useful for polymorphic types where the specific subtype is determined
	 *     by a discriminator field value, such as payment method types, message types, or entity types.
	 * </p>
	 * <p>
	 *     The discriminator field is expected to be present in the same parent object as the field
	 *     being encoded/decoded. During both encoding and decoding, the codec will:
	 * </p>
	 * <ol>
	 *     <li>Read the discriminator field from the parent object</li>
	 *     <li>Decode the discriminator value using the discriminator codec</li>
	 *     <li>Query the provider to get the appropriate codec for that discriminator value</li>
	 *     <li>Use that codec to encode or decode the actual value</li>
	 * </ol>
	 *
	 * @param discriminatedField The name of the discriminator field in the parent object
	 * @param discriminatedCodec The codec to use for encoding/decoding the discriminator value
	 * @param provider The provider that maps discriminator values to their corresponding codecs
	 * @param <C> The base type of values handled by this codec
	 * @param <T> The type of the discriminator value
	 * @return A new discriminated codec
	 * @throws NullPointerException If discriminated field, discriminated codec, or provider is null
	 * @see DiscriminatedCodec
	 * @see DiscriminatedCodecProvider
	 */
	public static <C, T> @NonNull DiscriminatedCodec<C, T> discriminatedBy(@NonNull String discriminatedField, @NonNull Codec<T> discriminatedCodec, @NonNull DiscriminatedCodecProvider<C, T> provider) {
		return new DiscriminatedCodec<>(discriminatedField, discriminatedCodec, provider);
	}
	
	/**
	 * Creates a new unit codec for the given value.<br>
	 *
	 * @param value The value
	 * @param <C> The type of the value
	 * @return A new unit codec
	 * @see UnitCodec
	 */
	public static <C> @NonNull UnitCodec<C> unit(@Nullable C value) {
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
	public static <C> @NonNull UnitCodec<C> unit(@NonNull Supplier<C> supplier) {
		return new UnitCodec<>(supplier);
	}
	
	/**
	 * Creates a new recursive codec that supports encoding and decoding recursive data structures.<br>
	 * <p>
	 *     This method is used to create codecs for types that reference themselves, such as trees or linked lists.<br>
	 *     The factory function receives a reference to the codec being created and must return the actual codec implementation.<br>
	 *     This allows the codec to reference itself during construction.
	 * </p>
	 * <p>
	 *     Example usage for a binary tree:
	 * </p>
	 * <pre>{@code
	 * record TreeNode(int value, TreeNode left, TreeNode right) {}
	 *
	 * Codec<TreeNode> treeCodec = Codecs.recursive(self ->
	 *     CodecBuilder.of(
	 *         Codecs.INTEGER.fieldOf("value", TreeNode::value),
	 *         self.nullable().fieldOf("left", TreeNode::left),
	 *         self.nullable().fieldOf("right", TreeNode::right)
	 *     ).create(TreeNode::new)
	 * );
	 * }</pre>
	 *
	 * @param codecFactory The factory function that creates the codec using a self-reference
	 * @param <C> The type of the recursive value
	 * @return A new recursive codec
	 * @throws NullPointerException If the codec factory is null
	 * @see RecursiveCodec
	 */
	public static <C> @NonNull RecursiveCodec<C> recursive(@NonNull Function<Codec<C>, Codec<C>> codecFactory) {
		return new RecursiveCodec<>(codecFactory);
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
	public static <E> @NonNull Codec<E> stringResolver(@NonNull Function<E, String> stringEncoder, @NonNull Function<String, @Nullable E> stringDecoder) {
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
