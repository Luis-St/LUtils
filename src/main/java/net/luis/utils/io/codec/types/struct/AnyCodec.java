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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A codec that attempts to encode or decode using multiple codecs in sequence.
 * <p>
 *     This codec tries each provided codec in order until one succeeds. If all codecs fail,
 *     an error is returned containing all the individual error messages. This is particularly
 *     useful for polymorphic types where multiple subtypes need to be handled dynamically.
 * </p>
 * <p>
 *     Example usage with payment methods:
 * <pre>{@code
 * Codec<PaymentMethod> paymentCodec = Codec.any(
 *     creditCardCodec,
 *     paypalCodec,
 *     bankTransferCodec
 * );
 *
 * // When decoding, tries each codec until one successfully decodes the payment method
 * Result<PaymentMethod> result = paymentCodec.decodeStart(provider, jsonData);
 * }</pre>
 * <p>
 *     During encoding, the codec tries each codec in order and uses the first one that
 *     successfully encodes the value. This allows for automatic type detection based on
 *     which codec can handle the specific instance.
 * </p>
 * <p>
 *     During decoding, the codec similarly tries each codec in order and returns the
 *     result from the first codec that successfully decodes the value.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The common supertype of values handled by this codec
 */
public class AnyCodec<C> extends AbstractCodec<C, Object> {
	
	/**
	 * The list of codecs to try in sequence.<br>
	 */
	private final List<Codec<C>> codecs;
	
	/**
	 * Constructs a new any codec with the specified array of codecs.
	 *
	 * @param codecs The array of codecs to try in sequence
	 * @throws NullPointerException if codecs is null or contains null elements
	 * @throws IllegalArgumentException if codecs is empty or contains only one codec
	 */
	@SafeVarargs
	public AnyCodec(@NonNull Codec<? extends C>... codecs) {
		this(Arrays.asList(codecs));
	}
	
	/**
	 * Constructs a new any codec with the specified list of codecs.
	 *
	 * @param codecs The list of codecs to try in sequence
	 * @throws NullPointerException if codecs is null or contains null elements
	 * @throws IllegalArgumentException if codecs is empty or contains only one codec
	 */
	@SuppressWarnings("unchecked")
	public AnyCodec(@NonNull List<? extends Codec<? extends C>> codecs) {
		Objects.requireNonNull(codecs, "Codecs must not be null");
		
		if (codecs.isEmpty()) {
			throw new IllegalArgumentException("Codecs must not be empty");
		}
		if (codecs.size() == 1) {
			throw new IllegalArgumentException("Any codec requires at least two codecs, use the single codec directly instead");
		}
		if (codecs.stream().anyMatch(Objects::isNull)) {
			throw new NullPointerException("Codecs must not contain null elements");
		}
		this.codecs = (List<Codec<C>>) List.copyOf(codecs);
	}
	
	@Override
	public @NonNull Class<C> getType() {
		return this.codecs.getFirst().getType();
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null value as any using '" + this + "'");
		}
		
		List<String> errors = new ArrayList<>();
		for (Codec<C> codec : this.codecs) {
			try {
				Result<R> result = codec.encodeStart(provider, current, value);
				
				if (result.isSuccess()) {
					return result;
				}
				
				errors.add(result.errorOrThrow());
			} catch (Exception e) {
				errors.add("Codec '" + codec + "' cannot handle value of type " + value.getClass().getName());
			}
		}
		return Result.error("Unable to encode value '" + value + "' as any using '" + this + "': All codecs failed:\n" + errors.stream().map(error -> "  - " + error).collect(Collectors.joining("\n")));
	}
	
	@Override
	public <R> @NonNull Result<C> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to decode null value as any using '" + this + "'");
		}
		
		List<String> errors = new ArrayList<>();
		for (Codec<C> codec : this.codecs) {
			try {
				Result<C> result = codec.decodeStart(provider, current, value);
				
				if (result.isSuccess()) {
					return result;
				}
				
				errors.add(result.errorOrThrow());
			} catch (Exception e) {
				errors.add("Codec '" + codec + "' cannot decode to expected type: " + e.getMessage());
			}
		}
		return Result.error("Unable to decode value as any using '" + this + "': All codecs failed:\n" + errors.stream().map(error -> "  - " + error).collect(Collectors.joining("\n")));
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AnyCodec<?> that)) return false;
		
		return this.codecs.equals(that.codecs);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codecs);
	}
	
	@Override
	public String toString() {
		return "AnyCodec[" + this.codecs.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
	}
	//endregion
}
