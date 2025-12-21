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

import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * A codec for encoding and decoding a field of a data structure by name.<br>
 * This codec uses another codec to encode and decode the value.<br>
 * <p>
 *     The field codec is used to encode and decode components of data structures with a specific name.<br>
 *     The name is used to identify the value in the data structure.<br>
 *     Optionally, aliases can be configured to identify the value if the name is not present.
 * </p>
 * <p>
 *     The current value in {@link #encodeInternal(TypeProvider, Object, Object)} must be a data structure that can hold the named value.<br>
 *     If the current value is not a data structure, the codec will return an error.
 * </p>
 * <p>
 *     The value in {@link #decodeStart(TypeProvider, Object, Object)} must be a data structure that contains the named value.<br>
 *     If the value is not a data structure or the named value is not present, the codec will return an error.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of the named component
 * @param <O> The type of the object containing the component
 */
public class FieldCodec<C, O> {
	
	/**
	 * The codec used to encode and decode the named value.<br>
	 */
	private final Codec<C> codec;
	/**
	 * The name of the value.<br>
	 */
	private final String name;
	/**
	 * The aliases of the value.<br>
	 */
	private final Set<String> aliases;
	/**
	 * The function used to retrieve the component from the object.<br>
	 */
	private final Function<O, C> getter;
	
	/**
	 * Constructs a new field codec.<br>
	 *
	 * @param codec The codec used to
	 * @param name The name of the value
	 * @param aliases The aliases of the value
	 * @param getter The getter for the component
	 * @throws NullPointerException If the codec, name, aliases or getter is null
	 */
	FieldCodec(@NonNull Codec<C> codec, @NonNull String name, Set<String> aliases, @NonNull Function<O, C> getter) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.aliases = Set.copyOf(Objects.requireNonNull(aliases, "Aliases must not be null"));
		this.getter = Objects.requireNonNull(getter, "Getter must not be null");
	}
	
	/**
	 * Encodes the component of the object using the given type provider and current value.<br>
	 * Internally, the component is retrieved from the object using the configured getter and then further encoded using the internal codec.<br>
	 *
	 * @param provider The type provider
	 * @param current The current value
	 * @param object The object
	 * @return The result of the encoding operation
	 * @param <R> The type of the current value
	 * @throws NullPointerException If the type provider or current value is null
	 * @see #encodeInternal(TypeProvider, Object, Object)
	 */
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable O object) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (object == null) {
			return Result.error("Unable to encode component because the component can not be retrieved from a null object");
		}
		return this.encodeInternal(provider, current, this.getter.apply(object));
	}
	
	/**
	 * Encodes the value using the internal codec and the given provider and map.<br>
	 * The encoded value will be stored in the map using the name of this codec.<br>
	 *
	 * @param provider The type provider
	 * @param map The map to encode the value into
	 * @param value The value to encode
	 * @param <R> The type to encode into
	 * @return The result
	 * @throws NullPointerException If the provider or map is null
	 */
	private <R> @NonNull Result<R> encodeInternal(@NonNull TypeProvider<R> provider, @NonNull R map, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(map, "Current value must not be null");
		
		Result<R> encodedResult = this.codec.encodeStart(provider, provider.empty(), value);
		if (encodedResult.isError()) {
			return Result.error("Unable to encode named '" + this.name + "' '" + value + "' with '" + this + "': " + encodedResult.errorOrThrow());
		}
		
		R encoded = encodedResult.resultOrThrow();
		if (provider.getEmpty(encoded).isSuccess()) {
			return Result.success(provider.empty());
		}
		
		Result<R> result = provider.set(map, this.name, encoded);
		if (result.isError()) {
			return Result.error("Unable to encode named '" + this.name + "' '" + value + "' with '" + this + "': " + result.errorOrThrow());
		}
		return Result.success(map);
	}
	
	/**
	 * Decodes the value using the internal codec and the given provider and map.<br>
	 * The value is retrieved from the map using the name of this codec.<br>
	 *
	 * @param provider The type provider
	 * @param map The map to decode the value from
	 * @param <R> The type to decode from
	 * @return The result
	 * @throws NullPointerException If the provider is null
	 */
	public <R> @NonNull Result<C> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R map) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (map == null) {
			return Result.error("Unable to decode named '" + this.name + "' null value using '" + this + "'");
		}
		
		Result<R> value = provider.get(map, this.name);
		if (value.isError()) {
			return this.decodeStartWithAlias(provider, map, value.errorOrThrow());
		}
		
		Result<C> result = this.codec.decodeStart(provider, map, value.resultOrThrow());
		if (result.isError()) {
			return this.decodeStartWithAlias(provider, map, result.errorOrThrow());
		}
		return result;
	}
	
	/**
	 * Decodes the value using the given provider and map.<br>
	 * This method will try to decode the value by the first alias which is present in the map.<br>
	 * If no alias is present, an error will be returned.<br>
	 * The result will contain the decoded value or an error message.<br>
	 *
	 * @param provider The type provider
	 * @param map The map to decode the value from
	 * @param error The actual error message which occurred during decoding
	 * @return The result
	 * @param <R> The type to decode from
	 * @throws NullPointerException If the provider, map or error is null
	 */
	private <R> @NonNull Result<C> decodeStartWithAlias(@NonNull TypeProvider<R> provider, @NonNull R map, @NonNull String error) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(map, "Map must not be null");
		Objects.requireNonNull(error, "Error message must not be null");
		
		Result<String> name = this.getDecodeName(provider, map);
		if (name.isError()) {
			return Result.error("Unable to decode named '" + this.name + "' of '" + map + "' with '" + this + "': " + name.errorOrThrow() + "\nSuppresses actual error: " + error);
		}
		
		Result<R> value = provider.get(map, name.resultOrThrow());
		if (value.isError()) {
			return Result.error("Unable to decode named '" + name.resultOrThrow() + "' of '" + map + "' with '" + this + "': " + value.errorOrThrow() + "\nSuppresses actual error: " + error);
		}
		return this.codec.decodeStart(provider, map, value.resultOrThrow());
	}
	
	/**
	 * Gets the first alias which is present in the given map or an error.<br>
	 * The result contains the name or an error message.<br>
	 *
	 * @param provider The type provider
	 * @param map The map to decode the value from
	 * @return The result
	 * @param <R> The type to decode from
	 * @throws NullPointerException If the provider or map is null
	 */
	private <R> @NonNull Result<String> getDecodeName(@NonNull TypeProvider<R> provider, @NonNull R map) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(map, "Map must not be null");
		if (this.aliases.isEmpty()) {
			return Result.error("Name '" + this.name + "' not found in '" + map + "', no aliases configured");
		}
		
		return this.aliases.stream().filter(alias -> {
			Result<Boolean> hasAlias = provider.has(map, alias);
			return hasAlias.isSuccess() && hasAlias.resultOrThrow();
		}).findFirst().map(Result::success).orElse(Result.error("Name and aliases '" + this.name + "' and '" + this.aliases + "' not found in '" + map + "'"));
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FieldCodec<?, ?> that)) return false;
		
		if (!this.codec.equals(that.codec)) return false;
		if (!this.name.equals(that.name)) return false;
		return this.aliases.equals(that.aliases);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec, this.name, this.aliases, this.getter);
	}
	
	@Override
	public String toString() {
		if (this.aliases.isEmpty()) {
			return "NamedCodec['" + this.name + "', " + this.codec + "]";
		}
		return "NamedCodec['" + this.name + "', " + this.aliases + ", " + this.codec + "]";
	}
	//endregion
}
