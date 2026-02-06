/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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
 *     If the optional value is empty during encoding, the current value is returned.<br>
 *     This means that the value will not be appended to a data structure.
 * </p>
 * <p>
 *     If the optional value is empty during decoding, the default value is returned.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of the optional value
 */
public class OptionalCodec<C> extends AbstractCodec<Optional<C>> {
	
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
	 *
	 * @param codec The codec for the optional value
	 * @throws NullPointerException If the codec is null
	 */
	public OptionalCodec(@NonNull Codec<C> codec) {
		this(codec, Optional::empty);
	}
	
	/**
	 * Constructs a new optional codec using the given codec and a supplier for the default value.<br>
	 *
	 * @param codec The codec for the optional value
	 * @param defaultProvider The supplier for the default value if the optional value is empty
	 * @throws NullPointerException If the codec or the default provider is null
	 */
	public OptionalCodec(@NonNull Codec<C> codec, @NonNull Supplier<Optional<C>> defaultProvider) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		this.defaultProvider = Objects.requireNonNull(defaultProvider, "Default provider must not be null");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Class<Optional<C>> getType() {
		return (Class<Optional<C>>) (Class<?>) Optional.class;
	}
	
	@Override
	@SuppressWarnings("OptionalAssignedToNull")
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Optional<C> value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return current;
		}
		
		if (value.isEmpty()) {
			return current;
		} else {
			return this.codec.encode(provider, current, value.get());
		}
	}
	
	@Override
	public <R> @NonNull Optional<C> decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return this.defaultProvider.get();
		}
		
		if (provider.isEmpty(value)) {
			return this.defaultProvider.get();
		}
		
		try {
			return Optional.of(this.codec.decode(provider, current, value));
		} catch (DecoderException e) {
			return this.defaultProvider.get();
		}
	}
	
	@Override
	public @NonNull Codec<Optional<C>> orElse(@Nullable Optional<C> defaultValue) {
		return this.orElseGet(() -> defaultValue);
	}
	
	@Override
	public @NonNull Codec<Optional<C>> orElseGet(@NonNull Supplier<Optional<C>> supplier) {
		return new OptionalCodec<>(this.codec, supplier);
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
