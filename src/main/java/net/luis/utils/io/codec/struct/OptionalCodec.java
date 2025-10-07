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

package net.luis.utils.io.codec.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.*;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A codec for encoding and decoding optional values.<br>
 * This codec uses another codec to encode and decode the optional value.<br>
 * <p>
 *     The optional codec can be set to provide a default value if the optional value is empty.<br>
 *     The default value is provided by a supplier.
 * </p>
 * <p>
 *     If the optional value is empty during encoding the current value is returned.<br>
 *     This means that the value will not be appended to a data structure.
 * </p>
 * <p>
 *     If the optional value is empty during decoding the default value is returned.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of the optional value
 */
public class OptionalCodec<C> implements Codec<Optional<C>> {
	
	/**
	 * The codec used to encode and decode the optional value.<br>
	 */
	private final Codec<C> codec;
	/**
	 * The supplier used to provide the default value if the optional value is empty.<br>
	 * Empty if no default value is provided.<br>
	 */
	private final Supplier<Optional<C>> defaultProvider;
	
	/**
	 * Constructs a new optional codec using the given codec.<br>
	 * Do not use this constructor directly, use the optional factory methods in {@link Codec} instead.<br>
	 *
	 * @param codec The codec for the optional value
	 * @throws NullPointerException If the codec is null
	 */
	@ApiStatus.Internal
	public OptionalCodec(@NotNull Codec<C> codec) {
		this(codec, Optional::empty);
	}
	
	/**
	 * Constructs a new optional codec using the given codec and a supplier for the default value.<br>
	 * Do not use this constructor directly, use the optional factory methods in {@link Codec} instead.<br>
	 *
	 * @param codec The codec for the optional value
	 * @param defaultProvider The supplier for the default value if the optional value is empty
	 * @throws NullPointerException If the codec or the default provider is null
	 */
	@ApiStatus.Internal
	public OptionalCodec(@NotNull Codec<C> codec, @NotNull Supplier<Optional<C>> defaultProvider) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		this.defaultProvider = Objects.requireNonNull(defaultProvider, "Default provider must not be null");
	}
	
	@Override
	@SuppressWarnings("OptionalAssignedToNull")
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Optional<C> value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.success(current);
		}
		return value.map(c -> this.codec.encodeStart(provider, current, c)).orElseGet(() -> Result.success(current));
	}
	
	@Override
	public <R> @NotNull Result<Optional<C>> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.success(this.defaultProvider.get());
		}
		
		Result<R> decoded = provider.getEmpty(value);
		if (decoded.isSuccess()) {
			return Result.success(this.defaultProvider.get());
		}
		
		Result<C> result = this.codec.decodeStart(provider, value);
		if (result.isError()) {
			return Result.success(this.defaultProvider.get());
		}
		return result.map(Optional::of);
	}
	
	@Override
	public @NotNull Codec<Optional<C>> withDefault(@Nullable Optional<C> defaultValue) {
		return this.withDefaultGet(() -> defaultValue);
	}
	
	@Override
	public @NotNull Codec<Optional<C>> withDefaultGet(@NotNull Supplier<Optional<C>> supplier) {
		return new OptionalCodec<>(this.codec, supplier);
	}
	
	/**
	 * Returns a new codec that is not optional.<br>
	 * The new codec will use the given default value if the optional value is empty.<br>
	 *
	 * @param defaultValue The default value
	 * @return The new codec
	 * @see #orElseGet(Supplier)
	 */
	public @NotNull Codec<C> orElse(@Nullable C defaultValue) {
		return this.orElseGet(() -> defaultValue);
	}
	
	/**
	 * Returns a new codec that is not optional.<br>
	 * The new codec will use the default value provided by the given supplier if the optional value is empty.<br>
	 *
	 * @param supplier The supplier for the default value
	 * @return The new codec
	 * @throws NullPointerException If the supplier is null
	 */
	public @NotNull Codec<C> orElseGet(@NotNull Supplier<C> supplier) {
		Objects.requireNonNull(supplier, "Supplier must not be null");
		return this.xmap(Optional::ofNullable, optional -> optional.orElseGet(supplier));
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof OptionalCodec<?> that)) return false;
		
		if (!this.codec.equals(that.codec)) return false;
		return Objects.equals(this.defaultProvider, that.defaultProvider);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec);
	}
	
	@Override
	public String toString() {
		return "OptionalCodec[" + this.codec + "]";
	}
	//endregion
}
