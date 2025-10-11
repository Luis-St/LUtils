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

import net.luis.utils.io.codec.decoder.Decoder;
import net.luis.utils.io.codec.decoder.KeyableDecoder;
import net.luis.utils.io.codec.encoder.Encoder;
import net.luis.utils.io.codec.encoder.KeyableEncoder;
import net.luis.utils.io.codec.internal.struct.*;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Either;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.luis.utils.io.codec.Codecs.*;
import static net.luis.utils.io.codec.ResultMappingFunction.*;

/**
 * A codec is a combination of an encoder and a decoder.<br>
 * It is used to encode and decode values of a specific type.<br>
 * Take a look at the {@link Codecs} class for some predefined codecs.<br>
 *
 * @see Encoder
 * @see Decoder
 * @see Codecs
 *
 * @author Luis-St
 *
 * @param <C> The type of the value that is encoded and decoded by this codec
 *
 */
public interface Codec<C> extends Encoder<C>, Decoder<C> {
	
	/**
	 * Creates a new codec from the given encoder and decoder with the given name.<br>
	 *
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
	 *
	 * @param codec The base codec that is used to encode and decode values of the type {@code C}
	 * @param keyDecoder The key decoder
	 * @param <C> The type of the value that is encoded and decoded by the codec
	 * @return A new keyable codec
	 * @throws NullPointerException If the codec or key decoder is null
	 * @see #keyable(Codec, KeyableEncoder, KeyableDecoder)
	 * @see KeyableCodec
	 */
	static <C> @NotNull KeyableCodec<C> keyable(@NotNull Codec<C> codec, @NotNull ResultingFunction<String, C> keyDecoder) {
		return keyable(codec, ResultingFunction.direct(C::toString), keyDecoder);
	}
	
	/**
	 * Creates a new keyable codec for the given codec using the given key encoder and key decoder.<br>
	 * The key encoder and key decoder are defined as functions that convert keys of the type {@code C} to and from strings.<br>
	 *
	 * @param codec The base codec that is used to encode and decode values of the type {@code C}
	 * @param keyEncoder The key encoder
	 * @param keyDecoder The key decoder
	 * @param <C> The type of the value that is encoded and decoded by the codec
	 * @return A new keyable codec
	 * @throws NullPointerException If the codec, key encoder or key decoder is null
	 * @see #keyable(Codec, KeyableEncoder, KeyableDecoder)
	 * @see KeyableCodec
	 */
	static <C> @NotNull KeyableCodec<C> keyable(
		@NotNull Codec<C> codec, @NotNull ResultingFunction<C, String> keyEncoder, @NotNull ResultingFunction<String, @Nullable C> keyDecoder
	) {
		return keyable(codec, KeyableEncoder.of(codec, keyEncoder), KeyableDecoder.of(codec, keyDecoder));
	}
	
	/**
	 * Creates a new keyable codec for the given codec using the given key encoder and key decoder.<br>
	 *
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
	 * Creates a new array codec uses the current codec as element codec for the array codec.<br>
	 * The created array codec has no length restrictions.<br>
	 *
	 * @param type The type of the array elements
	 * @param codec The codec for the elements of the array
	 * @return A new array codec for the given codec
	 * @param <C> The type of the array elements that are encoded and decoded by the codec
	 * @throws NullPointerException If the type or codec is null
	 * @see ArrayCodec
	 */
	static <C> @NotNull Codec<C[]> array(@NotNull Class<C> type, @NotNull Codec<C> codec) {
		return new ArrayCodec<>(type, codec);
	}
	
	/**
	 * Creates a new array codec uses the current codec as element codec for the array codec with the given maximum length.<br>
	 * The array must have at most the maximum length after encoding and decoding.<br>
	 *
	 * @param type The type of the array elements
	 * @param codec The codec for the elements of the array
	 * @param maxLength The maximum length of the array (inclusive)
	 * @return A new array codec for the given codec
	 * @param <C> The type of the array elements that are encoded and decoded by the codec
	 * @throws NullPointerException If the type or codec is null
	 * @throws IllegalArgumentException If the maximum length is less than 0
	 * @see #array(Class, Codec, int, int)
	 * @see ArrayCodec
	 */
	static <C> @NotNull Codec<C[]> array(@NotNull Class<C> type, @NotNull Codec<C> codec, int maxLength) {
		return array(type, codec, 0, maxLength);
	}
	
	/**
	 * Creates a new array codec uses the current codec as element codec for the array codec with the given minimum and maximum length.<br>
	 * The array must have at least the minimum length and at most the maximum length after encoding and decoding.<br>
	 *
	 * @param type The type of the array elements
	 * @param codec The codec for the elements of the array
	 * @param minLength The minimum length of the array (inclusive)
	 * @param maxLength The maximum length of the array (inclusive)
	 * @return A new array codec for the given codec
	 * @param <C> The type of the array elements that are encoded and decoded by the codec
	 * @throws NullPointerException If the type or codec is null
	 * @throws IllegalArgumentException If the minimum length is less than zero or greater than the maximum length
	 * @see ArrayCodec
	 */
	static <C> @NotNull Codec<C[]> array(@NotNull Class<C> type, @NotNull Codec<C> codec, int minLength, int maxLength) {
		return new ArrayCodec<>(type, codec, minLength, maxLength);
	}
	
	/**
	 * Creates a new array codec uses the current codec as element codec for the array codec for non-empty arrays.<br>
	 * The array must not be empty after encoding and decoding.<br>
	 *
	 * @param type The type of the array elements
	 * @param codec The codec for the elements of the array
	 * @return A new array codec for the given codec
	 * @param <C> The type of the array elements that are encoded and decoded by the codec
	 * @throws NullPointerException If the type or codec is null
	 * @see #array(Class, Codec, int, int)
	 * @see ArrayCodec
	 */
	static <C> @NotNull Codec<C[]> noneEmptyArray(@NotNull Class<C> type, @NotNull Codec<C> codec) {
		return array(type, codec, 1, Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a new list codec for the given codec.<br>
	 * The created list codec has no size restrictions.<br>
	 *
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
	 *
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
	 *
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
	 *
	 * @param codec The base codec
	 * @param <C> The type of the list elements that are encoded and decoded by the codec
	 * @return A new list codec
	 * @throws NullPointerException If the codec is null
	 * @see ListCodec
	 */
	static <C> @NotNull Codec<List<C>> noneEmptyList(@NotNull Codec<C> codec) {
		return list(codec, 1, Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a new stream codec for the given codec.<br>
	 *
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
	 * Creates a new map codec with {@link Codecs#STRING} as key codec and the given value codec.<br>
	 * The created map codec has no size restrictions.<br>
	 *
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
	 *
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
	 *
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
	 *
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
	 *
	 * @param keyCodec The key codec
	 * @param valueCodec The value codec
	 * @param <K> The type of the map keys that are encoded and decoded by the codec
	 * @param <V> The type of the map values that are encoded and decoded by the codec
	 * @return A new map codec
	 * @throws NullPointerException If the key codec or value codec is null
	 * @see MapCodec
	 */
	static <K, V> @NotNull Codec<Map<K, V>> noneEmptyMap(@NotNull KeyableCodec<K> keyCodec, @NotNull Codec<V> valueCodec) {
		return map(keyCodec, valueCodec, 1, Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a new codec that uses the given codec as the main codec and the given codec as alternative codec.<br>
	 * If the main codec fails to encode or decode a value, the alternative codec is used.<br>
	 *
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
	 *
	 * @param keyEncoder The key encoder
	 * @param keyDecoder The key decoder
	 * @return A new keyable codec
	 * @throws NullPointerException If the key encoder or key decoder is null
	 * @see #keyable(Codec, ResultingFunction, ResultingFunction)
	 * @see KeyableCodec
	 */
	default @NotNull KeyableCodec<C> keyable(
		@NotNull ResultingFunction<C, String> keyEncoder, @NotNull ResultingFunction<String, C> keyDecoder
	) {
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
	 * Creates a new array codec uses the current codec as element codec for the array codec.<br>
	 * The created array codec has no length restrictions.<br>
	 *
	 * @param type The type of the array elements
	 * @return A new array codec for the current codec
	 * @throws NullPointerException If the type is null
	 * @see #array(Class, Codec)
	 * @see ArrayCodec
	 */
	default @NotNull Codec<C[]> array(@NotNull Class<C> type) {
		return array(type, this);
	}
	
	/**
	 * Creates a new array codec uses the current codec as element codec for the array codec with the given maximum length.<br>
	 * The array must have at most the maximum length after encoding and decoding.<br>
	 *
	 * @param type The type of the array elements
	 * @param maxSize The maximum length of the array (inclusive)
	 * @return A new array codec for the current codec
	 * @throws NullPointerException If the type is null
	 * @throws IllegalArgumentException If the maximum length is less than 0
	 * @see #array(Class, Codec, int)
	 * @see ArrayCodec
	 */
	default @NotNull Codec<C[]> array(@NotNull Class<C> type, int maxSize) {
		return array(type, this, maxSize);
	}
	
	/**
	 * Creates a new array codec uses the current codec as element codec for the array codec with the given minimum and maximum length.<br>
	 * The array must have at least the minimum length and at most the maximum length after encoding and decoding.<br>
	 *
	 * @param type The type of the array elements
	 * @param minSize The minimum length of the array (inclusive)
	 * @param maxSize The maximum length of the array (inclusive)
	 * @return A new array codec for the current codec
	 * @throws NullPointerException If the type is null
	 * @throws IllegalArgumentException If the minimum length is less than zero or greater than the maximum length
	 * @see #array(Class, Codec, int, int)
	 * @see ArrayCodec
	 */
	default @NotNull Codec<C[]> array(@NotNull Class<C> type, int minSize, int maxSize) {
		return array(type, this, minSize, maxSize);
	}
	
	/**
	 * Creates a new array codec uses the current codec as element codec for the array codec for non-empty arrays.<br>
	 * The array must not be empty after encoding and decoding.<br>
	 *
	 * @param type The type of the array elements
	 * @return A new array codec for the current codec
	 * @throws NullPointerException If the type is null
	 * @see #noneEmptyArray(Class, Codec)
	 * @see ArrayCodec
	 */
	default @NotNull Codec<C[]> noneEmptyArray(@NotNull Class<C> type) {
		return noneEmptyArray(type, this);
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
	 * @param encoder The encoding mapping function
	 * @param decoder The decoding mapping function
	 * @param <O> The type of the mapped value
	 * @return A new mapped codec
	 * @throws NullPointerException If the encoding mapping function or decoding mapping function is null
	 * @see #xmap(Function, Function)
	 * @see #mapFlat(Function, ResultMappingFunction)
	 */
	default <O> @NotNull Codec<O> map(@NotNull ResultingFunction<O, C> encoder, @NotNull ResultMappingFunction<C, O> decoder) {
		return of(this.mapEncoder(encoder), this.mapDecoder(decoder), "MappedCodec[" + this + "]");
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
	 * @see #withDefaultGet(Supplier)
	 */
	default @NotNull Codec<C> withDefault(@Nullable C defaultValue) {
		return this.withDefaultGet(() -> defaultValue);
	}
	
	/**
	 * Creates a new codec that will return the value provided by the given supplier in an error case during decoding.<br>
	 *
	 * @param supplier The default value supplier
	 * @return A new codec
	 * @throws NullPointerException If the default value supplier is null
	 */
	default @NotNull Codec<C> withDefaultGet(@NotNull Supplier<C> supplier) {
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
