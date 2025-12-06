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
import net.luis.utils.io.codec.encoder.Encoder;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.codec.types.struct.*;
import net.luis.utils.util.result.ResultMappingFunction;
import net.luis.utils.util.result.ResultingFunction;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.luis.utils.util.result.ResultMappingFunction.*;

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
 */
public sealed interface Codec<C> extends Encoder<C>, Decoder<C> permits AbstractCodec, AnonymousCodec, CodecGroup {
	
	/**
	 * Creates a new codec from the given type, encoder, decoder, and name.<br>
	 *
	 * @param type The class type of the value that is encoded and decoded by the codec
	 * @param encoder The encoder that is used to encode values of the type {@code C}
	 * @param decoder The decoder that is used to decode values of the type {@code C}
	 * @param name The name of the codec
	 * @param <C> The type of the value that is encoded and decoded by the codec
	 * @return A new codec
	 * @throws NullPointerException If the encoder, decoder or name is null
	 * @see AnonymousCodec
	 * @see #of(Supplier, Encoder, Decoder, String)
	 */
	static <C> @NotNull Codec<C> of(@Nullable Class<C> type, @NotNull Encoder<C> encoder, @NotNull Decoder<C> decoder, @NotNull String name) {
		return new AnonymousCodec<>(() -> type, name, encoder, decoder);
	}
	
	/**
	 * Creates a new codec from the given type supplier, encoder, decoder, and name.<br>
	 *
	 * @param typeSupplier The supplier which providers the class type of the value that is encoded and decoded by the codec
	 * @param encoder The encoder that is used to encode values of the type {@code C}
	 * @param decoder The decoder that is used to decode values of the type {@code C}
	 * @param name The name of the codec
	 * @param <C> The type of the value that is encoded and decoded by the codec
	 * @return A new codec
	 * @throws NullPointerException If the type provider, encoder, decoder or name is null
	 * @see AnonymousCodec
	 */
	static <C> @NotNull Codec<C> of(@NotNull Supplier<Class<C>> typeSupplier, @NotNull Encoder<C> encoder, @NotNull Decoder<C> decoder, @NotNull String name) {
		return new AnonymousCodec<>(typeSupplier, name, encoder, decoder);
	}
	
	/**
	 * Returns the element type of this codec.<br>
	 * <p>
	 *     By default, this method tries to infer the type using reflection from the type parameter of the codec implementation.<br>
	 *     If the type cannot be inferred, an exception is thrown.
	 * </p>
	 * <p>
	 *     If the type cannot be inferred automatically, this method should be overridden in the codec implementation to return the correct type.
	 * </p>
	 *
	 * @return The inferred element type
	 * @throws IllegalStateException If the type cannot be inferred
	 */
	@SuppressWarnings("unchecked")
	default @NotNull Class<C> getType() {
		try {
			Type[] interfaces = this.getClass().getGenericInterfaces();
			for (Type iface : interfaces) {
				if (iface instanceof ParameterizedType paramType) {
					Type rawType = paramType.getRawType();
					if (rawType instanceof Class<?> rawClass && Codec.class.isAssignableFrom(rawClass)) {
						Type[] typeArgs = paramType.getActualTypeArguments();
						if (typeArgs.length > 0 && typeArgs[0] instanceof Class<?>) {
							return (Class<C>) typeArgs[0];
						}
					}
				}
			}
			
			Type superclass = this.getClass().getGenericSuperclass();
			if (superclass instanceof ParameterizedType paramType) {
				Type[] typeArgs = paramType.getActualTypeArguments();
				if (typeArgs.length > 0 && typeArgs[0] instanceof Class<?>) {
					return (Class<C>) typeArgs[0];
				}
			}
		} catch (Exception _) {}
		
		throw new IllegalStateException("""
			Cannot infer type for codec '{Codec}'.
			The codec must either have a concrete type parameter that can be determined via reflection or must override the getType() method.
			""".replace("{Codec}", this.toString()));
	}
	
	@Override
	<R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value);
	
	@Override
	default @NotNull Result<String> encodeKey(@NotNull C key) {
		return Encoder.super.encodeKey(key);
	}
	
	@Override
	<R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable R value);
	
	@Override
	default @NotNull Result<C> decodeKey(@NotNull String key) {
		return Decoder.super.decodeKey(key);
	}
	
	/**
	 * Creates a supplier that provides the type of the current codec.<br>
	 * This supplier can be used to create child codecs that delegate the type to the current codec.<br>
	 *
	 * @param name The name of the new codec, can be null
	 * @return A supplier that provides the type of the current codec
	 */
	private @NotNull Supplier<Class<C>> createDelegateTypeSupplier(@Nullable String name) {
		return () -> {
			try {
				return this.getType();
			} catch (IllegalStateException e) {
				if (name != null) {
					throw new IllegalStateException("Failed to infer type for child codec '" + this + "' of codec '" + name + "'", e);
				}
				throw new IllegalStateException("Failed to infer type for child codec '" + this + "'", e);
			}
		};
	}
	
	/**
	 * Wraps the current codec into a new codec with the given codec name.<br>
	 *
	 * @param name The name of the codec
	 * @return A new codec
	 * @throws NullPointerException If the codec name is null
	 * @throws IllegalStateException If the type cannot be inferred
	 * @see #of(Supplier, Encoder, Decoder, String)
	 * @see #getType()
	 */
	private @NotNull Codec<C> codec(@NotNull String name) {
		return of(this.createDelegateTypeSupplier(name), this, this, name);
	}
	
	/**
	 * Overwrites the type of the current codec with the given class type.<br>
	 * <p>
	 *     This method is useful if the codec type cannot be inferred automatically.<br>
	 *     The {@link Codec#getType()} method will return the given class type.
	 * </p>
	 * <p>
	 *     The returned typed codec will delegate all encoding and decoding operations to the current codec.
	 * </p>
	 *
	 * @param type The class type
	 * @return A new typed codec
	 * @throws NullPointerException If the type is null
	 */
	default @NotNull Codec<C> typed(@NotNull Class<C> type) {
		return Codec.of(type, this, this, "TypedCodec[" + this + "]");
	}
	
	/**
	 * Implements the keyable functionality for the current codec using the given key decoder.<br>
	 * The encoder is automatically generated using the {@code toString()} method of the key type C.<br>
	 * <p>
	 *     By default, the keyable functionality is not supported by codecs.<br>
	 *     If the underlying codec already supports keyable functionality, this method will override the existing key decoder.
	 * </p>
	 * <p>
	 *     The key encoder is defined as a function that converts keys of the type C to strings.
	 * </p>
	 *
	 * @param keyDecoder The key decoder
	 * @return A new keyable codec
	 * @throws NullPointerException If the key decoder is null
	 */
	default @NotNull Codec<C> keyable(@NotNull ResultingFunction<String, C> keyDecoder) {
		return this.keyable(ResultingFunction.direct(String::valueOf), keyDecoder);
	}
	
	/**
	 * Implements the keyable functionality for the current codec using the given key encoder and key decoder.<br>
	 * <p>
	 *     By default, the keyable functionality is not supported by codecs.<br>
	 *     If the underlying codec already supports keyable functionality, this method will override the existing key encoder and key decoder.
	 * </p>
	 * <p>
	 *     The key encoder is defined as a function that converts keys of the type C to strings.<br>
	 *     The key decoder is defined as a function that converts keys of the type C from strings.
	 * </p>
	 *
	 * @param keyEncoder The key encoder
	 * @param keyDecoder The key decoder
	 * @return A new keyable codec
	 * @throws NullPointerException If the key encoder or key decoder is null
	 * @see Encoder#encodeKey(Object)
	 * @see Decoder#decodeKey(String)
	 */
	default @NotNull Codec<C> keyable(@NotNull ResultingFunction<C, String> keyEncoder, @NotNull ResultingFunction<String, C> keyDecoder) {
		Objects.requireNonNull(keyEncoder, "Key encoder must not be null");
		Objects.requireNonNull(keyDecoder, "Key decoder must not be null");
		
		Encoder<C> encoder = new Encoder<>() {
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
				return Codec.this.encodeStart(provider, current, value);
			}
			
			@Override
			public @NotNull Result<String> encodeKey(@NotNull C key) {
				Objects.requireNonNull(key, "Key to encode must not be null");
				return keyEncoder.apply(key);
			}
		};
		Decoder<C> decoder = new Decoder<>() {
			@Override
			public <R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable R value) {
				return Codec.this.decodeStart(provider, current, value);
			}
			
			@Override
			public @NotNull Result<C> decodeKey(@NotNull String key) {
				Objects.requireNonNull(key, "Key to decode must not be null");
				return keyDecoder.apply(key);
			}
		};
		
		String name = "KeyableCodec[" + this + "]";
		return Codec.of(this.createDelegateTypeSupplier(name), encoder, decoder, name);
	}
	
	/**
	 * Wraps the current codec into a new nullable codec.<br>
	 *
	 * @return A new nullable codec
	 * @see NullableCodec
	 */
	default @NotNull NullableCodec<C> nullable() {
		return new NullableCodec<>(this);
	}
	
	/**
	 * Wraps the current codec into a new union codec that constrains values to a specific set of allowed values.<br>
	 * This codec validates that encoded and decoded values are within the predefined set of valid values.<br>
	 * Any value outside this set will cause encoding or decoding to fail with an error.<br>
	 *
	 * @param validValues The collection of valid values
	 * @return A new union codec for the current codec
	 * @throws NullPointerException If the valid values is null
	 * @throws IllegalArgumentException If the valid values collection is empty
	 * @see UnionCodec
	 */
	default @NotNull UnionCodec<C> union(@NotNull Collection<C> validValues) {
		return new UnionCodec<>(this, validValues);
	}
	
	/**
	 * Wraps the current codec into a new union codec that constrains values to a specific set of allowed values.<br>
	 * This codec validates that encoded and decoded values are within the predefined set of valid values.<br>
	 * Any value outside this set will cause encoding or decoding to fail with an error.<br>
	 *
	 * @param validValues The array of valid values
	 * @return A new union codec for the current codec
	 * @throws NullPointerException If the valid values is null
	 * @throws IllegalArgumentException If the valid values array is empty
	 * @see UnionCodec
	 */
	@SuppressWarnings("unchecked")
	default @NotNull UnionCodec<C> union(C @NotNull ... validValues) {
		return new UnionCodec<>(this, Arrays.asList(validValues));
	}
	
	/**
	 * Wraps the current codec into a new optional codec.<br>
	 *
	 * @return A new optional codec for the current codec
	 * @see OptionalCodec
	 */
	default @NotNull OptionalCodec<C> optional() {
		return new OptionalCodec<>(this);
	}
	
	/**
	 * Wraps the current codec into a new optional codec that provides a default value if the optional value is empty.<br>
	 * The default value is used if the optional value is empty during decoding.<br>
	 *
	 * @param defaultValue The default value
	 * @return A new optional codec for the current codec
	 * @see OptionalCodec
	 */
	default @NotNull OptionalCodec<C> optional(@Nullable C defaultValue) {
		return new OptionalCodec<>(this, () -> Optional.ofNullable(defaultValue));
	}
	
	/**
	 * Wraps the current codec into a new optional codec that provides a default value from the given supplier if the optional value is empty.<br>
	 * The default value is get if the optional value is empty during decoding.<br>
	 *
	 * @param defaultSupplier The supplier for the default value
	 * @return A new optional codec for the current codec
	 * @throws NullPointerException If the default supplier is null
	 * @see OptionalCodec
	 */
	default @NotNull Codec<Optional<C>> optional(@NotNull Supplier<C> defaultSupplier) {
		Objects.requireNonNull(defaultSupplier, "Default value supplier must not be null");
		return new OptionalCodec<>(this, () -> Optional.ofNullable(defaultSupplier.get()));
	}
	
	/**
	 * Creates a new array codec uses the current codec as element codec for the array codec.<br>
	 *
	 * @return A new array codec for the current codec
	 * @throws UnsupportedOperationException If the type cannot be inferred
	 * @see ArrayCodec
	 */
	default @NotNull ArrayCodec<C> array() {
		return new ArrayCodec<>(this.getType(), this);
	}
	
	/**
	 * Creates a new list codec uses the current codec as element codec for the list codec.<br>
	 *
	 * @return A new list codec for the current codec
	 * @see ListCodec
	 */
	default @NotNull ListCodec<C> list() {
		return new ListCodec<>(this);
	}
	
	/**
	 * Creates a new stream codec uses the current codec as element codec for the stream codec.<br>
	 *
	 * @return A new stream codec for the current codec
	 */
	default @NotNull Codec<Stream<C>> stream() {
		return this.list().xmap(Stream::toList, List::stream).codec("StreamCodec[" + this + "]");
	}
	
	/**
	 * Creates a new codec that uses the current codec as the main codec and the given codec as fallback codec.<br>
	 * If the main codec fails to encode or decode a value, the fallback codec is used.<br>
	 *
	 * @param fallback The fallback codec
	 * @return A new codec
	 * @throws NullPointerException If the fallback codec is null
	 */
	default @NotNull Codec<C> withFallback(@NotNull Codec<? extends C> fallback) {
		return Codecs.any(this, fallback);
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
		return of((Class<O>) null, this.mapEncoder(encoder), this.mapDecoder(decoder), "MappedCodec[" + this + "]");
	}
	
	/**
	 * Creates a new codec that will validate the result of the encoding and decoding process using the given validator function.<br>
	 * The validator function is applied to the value and must return a result that contains the validated value or an error message.<br>
	 *
	 * @param validator The validator function
	 * @return A new codec
	 * @throws NullPointerException If the validator function is null
	 */
	default @NotNull Codec<C> validate(@NotNull ResultingFunction<C, C> validator) {
		Objects.requireNonNull(validator, "Validator function must not be null");
		return this.map(validator, result -> result.flatMap(validator));
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
		
		String name = "OrElseCodec[" + this + "]";
		return of(this.createDelegateTypeSupplier(name), this, this.mapDecoder(result -> Result.success(result.orElseGet(supplier))), name);
	}
	
	/**
	 * Creates a new field codec using the current codec with the given name, aliases, and getter function.<br>
	 * The field codec is used in the codec builder to create codecs for complex data structures.<br>
	 * The field codec encodes and decodes components of the data structure using the given getter function.<br>
	 *
	 * @param name The name of the field
	 * @param getter The getter function to get the field value from the object
	 * @param <O> The type of the object which contains the component
	 * @return A new field codec
	 * @throws NullPointerException If the field name or getter function is null
	 * @see #fieldOf(String, String, Function)
	 * @see #fieldOf(String, Set, Function)
	 * @see FieldCodec
	 */
	default <O> @NotNull FieldCodec<C, O> fieldOf(@NotNull String name, @NotNull Function<O, C> getter) {
		return this.fieldOf(name, Set.of(), getter);
	}
	
	/**
	 * Creates a new field codec using the current codec with the given name, alias, and getter function.<br>
	 * The field codec is used in the codec builder to create codecs for complex data structures.<br>
	 * The field codec encodes and decodes components of the data structure using the given getter function.<br>
	 *
	 * @param name The name of the field
	 * @param alias The alias of the field
	 * @param getter The getter function to get the field value from the object
	 * @param <O> The type of the object which contains the component
	 * @return A new field codec
	 * @throws NullPointerException If the field name, alias or getter function is null
	 * @see #fieldOf(String, Function)
	 * @see #fieldOf(String, Set, Function)
	 * @see FieldCodec
	 */
	default <O> @NotNull FieldCodec<C, O> fieldOf(@NotNull String name, @NotNull String alias, @NotNull Function<O, C> getter) {
		return this.fieldOf(name, Set.of(alias), getter);
	}
	
	/**
	 * Creates a new field codec using the current codec with the given name, aliases, and getter function.<br>
	 * The field codec is used in the codec builder to create codecs for complex data structures.<br>
	 * The field codec encodes and decodes components of the data structure using the given getter function.<br>
	 *
	 * @param name The name of the field
	 * @param aliases The aliases of the field
	 * @param getter The getter function to get the field value from the object
	 * @param <O> The type of the object which contains the component
	 * @return A new field codec
	 * @throws NullPointerException If the field name, aliases or getter function is null
	 * @see #fieldOf(String, Function)
	 * @see #fieldOf(String, String, Function)
	 * @see FieldCodec
	 */
	default <O> @NotNull FieldCodec<C, O> fieldOf(@NotNull String name, @NotNull Set<String> aliases, @NotNull Function<O, C> getter) {
		return new FieldCodec<>(this, name, aliases, getter);
	}
	
	/**
	 * Creates a new optional field codec using the current codec with the given name, aliases, default value and getter function.<br>
	 * The optional field codec is used in the codec builder to create codecs for complex data structures.<br>
	 * The optional field codec encodes and decodes components of the data structure using the given getter function.<br>
	 * If the field is not provided during decoding, the default value is used.<br>
	 *
	 * @param name The name of the field
	 * @param defaultValue The default value
	 * @param getter The getter function to get the field value from the object
	 * @param <O> The type of the object which contains the component
	 * @return A new optional field codec
	 * @throws NullPointerException If the field name or getter function is null
	 * @see #optionalFieldOf(String, String, Object, Function)
	 * @see #optionalFieldOf(String, Set, Object, Function)
	 * @see FieldCodec
	 */
	default <O> @NotNull FieldCodec<C, O> optionalFieldOf(@NotNull String name, @Nullable C defaultValue, @NotNull Function<O, C> getter) {
		return this.optionalFieldOf(name, Set.of(), () -> defaultValue, getter);
	}
	
	/**
	 * Creates a new optional field codec using the current codec with the given name, aliases, default supplier and getter function.<br>
	 * The optional field codec is used in the codec builder to create codecs for complex data structures.<br>
	 * The optional field codec encodes and decodes components of the data structure using the given getter function.<br>
	 * If the field is not provided during decoding, the default value from the supplier is used.<br>
	 *
	 * @param name The name of the field
	 * @param defaultSupplier The supplier which provides the default value
	 * @param getter The getter function to get the field value from the object
	 * @param <O> The type of the object which contains the component
	 * @return A new optional field codec
	 * @throws NullPointerException If the field name or getter function is null
	 * @see #optionalFieldOf(String, String, Supplier, Function)
	 * @see #optionalFieldOf(String, Set, Supplier, Function)
	 * @see FieldCodec
	 */
	default <O> @NotNull FieldCodec<C, O> optionalFieldOf(@NotNull String name, @NotNull Supplier<C> defaultSupplier, @NotNull Function<O, C> getter) {
		return this.optionalFieldOf(name, Set.of(), defaultSupplier, getter);
	}
	
	/**
	 * Creates a new optional field codec using the current codec with the given name, alias, default value and getter function.<br>
	 * The optional field codec is used in the codec builder to create codecs for complex data structures.<br>
	 * The optional field codec encodes and decodes components of the data structure using the given getter function.<br>
	 * If the field is not provided during decoding, the default value is used.<br>
	 *
	 * @param name The name of the field
	 * @param alias The alias of the field
	 * @param defaultValue The default value
	 * @param getter The getter function to get the field value from the object
	 * @param <O> The type of the object which contains the component
	 * @return A new optional field codec
	 * @throws NullPointerException If the field name, alias or getter function is null
	 * @see #optionalFieldOf(String, Object, Function)
	 * @see #optionalFieldOf(String, Set, Object, Function)
	 * @see FieldCodec
	 */
	default <O> @NotNull FieldCodec<C, O> optionalFieldOf(@NotNull String name, @NotNull String alias, @Nullable C defaultValue, @NotNull Function<O, C> getter) {
		return this.optionalFieldOf(name, Set.of(alias), () -> defaultValue, getter);
	}
	
	/**
	 * Creates a new optional field codec using the current codec with the given name, alias, default supplier and getter function.<br>
	 * The optional field codec is used in the codec builder to create codecs for complex data structures.<br>
	 * The optional field codec encodes and decodes components of the data structure using the given getter function.<br>
	 * If the field is not provided during decoding, the default value from the supplier is used.<br>
	 *
	 * @param name The name of the field
	 * @param alias The alias of the field
	 * @param defaultSupplier The supplier which provides the default value
	 * @param getter The getter function to get the field value from the object
	 * @param <O> The type of the object which contains the component
	 * @return A new optional field codec
	 * @throws NullPointerException If the field name, alias, default supplier or getter function is null
	 * @see #optionalFieldOf(String, Supplier, Function)
	 * @see #optionalFieldOf(String, Set, Supplier, Function)
	 * @see FieldCodec
	 */
	default <O> @NotNull FieldCodec<C, O> optionalFieldOf(@NotNull String name, @NotNull String alias, @NotNull Supplier<C> defaultSupplier, @NotNull Function<O, C> getter) {
		return this.optionalFieldOf(name, Set.of(alias), defaultSupplier, getter);
	}
	
	/**
	 * Creates a new optional field codec using the current codec with the given name, aliases, default value and getter function.<br>
	 * The optional field codec is used in the codec builder to create codecs for complex data structures.<br>
	 * The optional field codec encodes and decodes components of the data structure using the given getter function.<br>
	 * If the field is not provided during decoding, the default value is used.<br>
	 *
	 * @param name The name of the field
	 * @param aliases The aliases of the field
	 * @param defaultValue The default value
	 * @param getter The getter function to get the field value from the object
	 * @param <O> The type of the object which contains the component
	 * @return A new optional field codec
	 * @throws NullPointerException If the field name, aliases or getter function is null
	 * @see #optionalFieldOf(String, Object, Function)
	 * @see #optionalFieldOf(String, String, Object, Function)
	 * @see FieldCodec
	 */
	default <O> @NotNull FieldCodec<C, O> optionalFieldOf(@NotNull String name, @NotNull Set<String> aliases, @Nullable C defaultValue, @NotNull Function<O, C> getter) {
		return this.optionalFieldOf(name, aliases, () -> defaultValue, getter);
	}
	
	/**
	 * Creates a new optional field codec using the current codec with the given name, aliases, default supplier and getter function.<br>
	 * The optional field codec is used in the codec builder to create codecs for complex data structures.<br>
	 * The optional field codec encodes and decodes components of the data structure using the given getter function.<br>
	 * If the field is not provided during decoding, the default value from the supplier is used.<br>
	 *
	 * @param name The name of the field
	 * @param aliases The aliases of the field
	 * @param defaultSupplier The supplier which provides the default value
	 * @param getter The getter function to get the field value from the object
	 * @param <O> The type of the object which contains the component
	 * @return A new optional field codec
	 * @throws NullPointerException If the field name, aliases, default supplier or getter function is null
	 * @see #optionalFieldOf(String, Supplier, Function)
	 * @see #optionalFieldOf(String, String, Supplier, Function)
	 * @see FieldCodec
	 */
	default <O> @NotNull FieldCodec<C, O> optionalFieldOf(@NotNull String name, @NotNull Set<String> aliases, @NotNull Supplier<C> defaultSupplier, @NotNull Function<O, C> getter) {
		Objects.requireNonNull(defaultSupplier, "Default value supplier must not be null");
		return new FieldCodec<>(
			this.optional().xmap(Optional::ofNullable, optional -> optional.orElseGet(defaultSupplier)).codec("OptionalFieldCodec[" + this + "]"),
			name,
			aliases,
			getter
		);
	}
}
