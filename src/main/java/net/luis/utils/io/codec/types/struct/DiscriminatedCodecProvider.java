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

import net.luis.utils.io.codec.Codec;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A provider interface for discriminated codecs that maps discriminator values to their corresponding codecs.<br>
 * This provider is used by {@link DiscriminatedCodec} to select the appropriate codec based on a discriminator value.<br>
 * <p>
 *     The provider supports two creation patterns:
 * </p>
 * <ul>
 *     <li>Function-based: Uses a function to dynamically resolve codecs from discriminator values</li>
 *     <li>Map-based: Uses a predefined map to look up codecs by discriminator values</li>
 * </ul>
 * <p>
 *     Example usage with function-based provider:
 * </p>
 * <pre>{@code
 * // Define polymorphic payment method types
 * interface PaymentMethodDetails {}
 * record PayPal(String mail) implements PaymentMethodDetails {}
 * record CreditCard(String cardNumber, String cardHolder, LocalDate expiryDate, String cvv)
 *     implements PaymentMethodDetails {}
 *
 * // Create codecs for each payment method type
 * Codec<PayPal> payPalCodec = CodecBuilder.of(
 *     Codecs.STRING.fieldOf("mail", PayPal::mail)
 * ).create(PayPal::new);
 *
 * Codec<CreditCard> creditCardCodec = CodecBuilder.of(
 *     Codecs.STRING.fieldOf("cardNumber", CreditCard::cardNumber),
 *     Codecs.STRING.fieldOf("cardHolder", CreditCard::cardHolder),
 *     Codecs.LOCAL_DATE.fieldOf("expiryDate", CreditCard::expiryDate),
 *     Codecs.STRING.fieldOf("cvv", CreditCard::cvv)
 * ).create(CreditCard::new);
 *
 * // Create a discriminated codec provider using a function
 * DiscriminatedCodecProvider<PaymentMethodDetails, String> provider =
 *     DiscriminatedCodecProvider.create(PaymentMethodDetails.class, type -> switch (type) {
 *         case "paypal" -> payPalCodec;
 *         case "credit_card" -> creditCardCodec;
 *         default -> throw new IllegalArgumentException("Unknown payment method type: " + type);
 *     });
 * }</pre>
 * <p>
 *     Example usage with map-based provider:
 * </p>
 * <pre>{@code
 * Map<String, Codec<? extends PaymentMethodDetails>> codecMap = Map.of(
 *     "paypal", payPalCodec,
 *     "credit_card", creditCardCodec
 * );
 *
 * DiscriminatedCodecProvider<PaymentMethodDetails, String> provider =
 *     DiscriminatedCodecProvider.create(PaymentMethodDetails.class, codecMap);
 * }</pre>
 *
 * @author Luis-St
 *
 * @param <C> The base type of values handled by the codecs provided by this provider
 * @param <T> The type of the discriminator value used to select codecs
 */

public interface DiscriminatedCodecProvider<C, T> {
	
	/**
	 * Creates a new discriminated codec provider using a function to resolve codecs from discriminator values.<br>
	 * The function is called with the discriminator value and should return the appropriate codec for that value.<br>
	 * <p>
	 *     This factory method is useful when the codec selection logic is dynamic or requires complex conditional logic,
	 *     such as switch statements or if-else chains.
	 * </p>
	 *
	 * @param codecType The class representing the base type of values handled by the codecs
	 * @param codecProvider The function that maps discriminator values to their corresponding codecs
	 * @param <C> The base type of values handled by the codecs provided by this provider
	 * @param <T> The type of the discriminator value
	 * @return A new discriminated codec provider
	 * @throws NullPointerException If codec type or codec provider is null
	 */
	static <C, T> @NonNull DiscriminatedCodecProvider<C, T> create(@NonNull Class<C> codecType, @NonNull Function<T, Codec<? extends C>> codecProvider) {
		Objects.requireNonNull(codecType, "Codec type must not be null");
		Objects.requireNonNull(codecProvider, "Codec provider must not be null");
		
		return new DiscriminatedCodecProvider<C, T>() {
			@Override
			public @NonNull Class<C> getCodecType() {
				return codecType;
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public @NonNull Codec<C> getCodec(@NonNull T discriminator) {
				return (Codec<C>) codecProvider.apply(discriminator);
			}
		};
	}
	
	/**
	 * Creates a new discriminated codec provider using a map to look up codecs by discriminator values.<br>
	 * The map is used to directly retrieve the codec for a given discriminator value.<br>
	 * <p>
	 *     This factory method is useful when the set of possible discriminator values and their
	 *     corresponding codecs is known in advance and can be defined as a static mapping.
	 * </p>
	 *
	 * @param codecType The class representing the base type of values handled by the codecs
	 * @param codecProvider The map that associates discriminator values with their corresponding codecs
	 * @param <C> The base type of values handled by the codecs provided by this provider
	 * @param <T> The type of the discriminator value
	 * @return A new discriminated codec provider
	 * @throws NullPointerException If codec type or codec provider is null
	 */
	static <C, T> @NonNull DiscriminatedCodecProvider<C, T> create(@NonNull Class<C> codecType, @NonNull Map<T, Codec<? extends C>> codecProvider) {
		Objects.requireNonNull(codecType, "Codec type must not be null");
		Objects.requireNonNull(codecProvider, "Codec provider must not be null");
		
		return new DiscriminatedCodecProvider<C, T>() {
			@Override
			public @NonNull Class<C> getCodecType() {
				return codecType;
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public @NonNull Codec<C> getCodec(@NonNull T discriminator) {
				return (Codec<C>) codecProvider.get(discriminator);
			}
		};
	}
	
	/**
	 * Returns the base class type of values handled by the codecs provided by this provider.<br>
	 * This type information is used by the discriminated codec to determine its own type.
	 *
	 * @return The base class type of values handled by the codecs
	 */
	@NonNull Class<C> getCodecType();
	
	/**
	 * Returns the codec corresponding to the given discriminator value.<br>
	 * The returned codec will be used to encode or decode values when the discriminator matches.
	 *
	 * @param discriminator The discriminator value used to select the appropriate codec
	 * @return The codec for the given discriminator value
	 * @throws NullPointerException If the discriminator is null
	 */
	@UnknownNullability
	Codec<C> getCodec(@NonNull T discriminator);
}
