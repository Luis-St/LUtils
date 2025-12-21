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
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * An anonymous implementation of the {@link Codec} interface.<br>
 * This record allows the creation of codecs without having to extend the {@link AnonymousCodec}.<br>
 * The codec is created from an existing encoder and decoder.<br>
 *
 * @author Luis-St
 *
 * @param typeSupplier The supplier which provides the type of the codec
 * @param name The name of the codec
 * @param encoder The encoder of the codec
 * @param decoder The decoder of the codec
 * @param <C> The type the codec is for
 */
record AnonymousCodec<C>(
	@NonNull Supplier<Class<C>> typeSupplier,
	@NonNull String name,
	@NonNull Encoder<C> encoder,
	@NonNull Decoder<C> decoder
) implements Codec<C> {
	
	/**
	 * Constructs an anonymous codec.<br>
	 *
	 * @param typeSupplier The supplier which provides the type of the codec
	 * @param name The name of the codec
	 * @param encoder The encoder of the codec
	 * @param decoder The decoder of the codec
	 * @throws NullPointerException If the type supplier, name, encoder or decoder is null
	 * @throws IllegalArgumentException If the name is empty or blank
	 */
	AnonymousCodec {
		Objects.requireNonNull(typeSupplier, "Type supplier must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(encoder, "Encoder must not be null");
		Objects.requireNonNull(decoder, "Decoder must not be null");
		
		if (name.isBlank()) {
			throw new IllegalArgumentException("Name must not be empty or blank");
		}
	}
	
	@Override
	public @NonNull Class<C> getType() {
		Class<C> type = this.typeSupplier.get();
		if (type == null) {
			try {
				return Codec.super.getType();
			} catch (IllegalStateException e) {
				throw new IllegalStateException("Failed to get type from inference and type supplier returned null", e);
			}
		}
		return type;
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable C value) {
		return this.encoder.encodeStart(provider, current, value);
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull C key) {
		return this.encoder.encodeKey(key);
	}
	
	@Override
	public <R> @NonNull Result<C> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		return this.decoder.decodeStart(provider, current, value);
	}
	
	//region Object overrides
	@Override
	public @NonNull Result<C> decodeKey(@NonNull String key) {
		return this.decoder.decodeKey(key);
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AnonymousCodec<?>(Supplier<?> typeProvider, String name, Encoder<?> encoder, Decoder<?> decoder))) return false;
		
		if (!this.name.equals(name)) return false;
		if (!this.typeSupplier.equals(typeProvider)) return false;
		if (!this.encoder.equals(encoder)) return false;
		return this.decoder.equals(decoder);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.typeSupplier, this.name, this.encoder, this.decoder);
	}
	
	@Override
	public @NonNull String toString() {
		return this.name;
	}
	//endregion
}
