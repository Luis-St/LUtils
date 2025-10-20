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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * A codec for encoding and decoding named values.<br>
 * This codec uses another codec to encode and decode the value.<br>
 * <p>
 *     The named codec is used to encode and decode values with a specific name.<br>
 *     The name is used to identify the value in a data structure.<br>
 *     Optionally, aliases can be configured to identify the value if the name is not present.
 * </p>
 * <p>
 *     The current value in {@link #encodeStart(TypeProvider, Object, Object)} must be a data structure that can hold the named value.<br>
 *     If the current value is not a data structure, the codec will return an error.
 * </p>
 * <p>
 *     The value in {@link #decodeStart(TypeProvider, Object)} must be a data structure that contains the named value.<br>
 *     If the value is not a data structure or the named value is not present, the codec will return an error.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of the named value
 */
public class NamedCodec<C> implements Codec<C> {
	
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
	 * Constructs a new named codec using the given codec and name for the value.<br>
	 *
	 * @param codec The codec used to
	 * @param name The name of the value
	 * @param aliases The aliases of the value
	 * @throws NullPointerException If the codec or name is null
	 */
	NamedCodec(@NotNull Codec<C> codec, @NotNull String name, String @NotNull ... aliases) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.aliases = Set.of(Objects.requireNonNull(aliases, "Aliases must not be null"));
	}
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R map, @Nullable C value) {
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
	
	@Override
	public @NotNull <R> Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R map) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (map == null) {
			return Result.error("Unable to decode named '" + this.name + "' null value using '" + this + "'");
		}
		
		Result<R> value = provider.get(map, this.name);
		if (value.isError()) {
			return this.decodeStartWithAlias(provider, map, value.errorOrThrow());
		}
		
		Result<C> result = this.codec.decodeStart(provider, value.resultOrThrow());
		if (result.isError()) { // Required, since TypeProvider#get can return null as success
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
	private @NotNull <R> Result<C> decodeStartWithAlias(@NotNull TypeProvider<R> provider, @NotNull R map, @NotNull String error) {
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
		return this.codec.decodeStart(provider, value.resultOrThrow());
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
	private <R> @NotNull Result<String> getDecodeName(@NotNull TypeProvider<R> provider, @NotNull R map) {
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
		if (!(o instanceof NamedCodec<?> that)) return false;
		
		if (!this.codec.equals(that.codec)) return false;
		return this.name.equals(that.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec, this.name);
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
