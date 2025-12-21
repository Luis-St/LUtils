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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A codec for encoding and decoding polymorphic values using a discriminator field.<br>
 * This codec selects the appropriate codec based on a discriminator field value in the parent object.<br>
 * <p>
 *     The discriminator field is read from the parent object to determine which codec
 *     to use for encoding or decoding the actual value. This enables handling polymorphic
 *     types where the specific type is determined by a field value.
 * </p>
 * <p>
 *     During both encoding and decoding, this codec:
 * </p>
 * <ol>
 *     <li>Reads the discriminator field from the parent object</li>
 *     <li>Decodes the discriminator value using the discriminator codec</li>
 *     <li>Queries the provider to get the appropriate codec for that discriminator value</li>
 *     <li>Uses that codec to encode or decode the actual value</li>
 * </ol>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * // Define polymorphic payment method details
 * interface PaymentMethodDetails {}
 * record PayPal(String mail) implements PaymentMethodDetails {}
 * record CreditCard(String cardNumber, String cardHolder, LocalDate expiryDate, String cvv)
 *     implements PaymentMethodDetails {}
 *
 * // Define container with discriminator field
 * record PaymentMethod(String type, PaymentMethodDetails details) {}
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
 * // Create discriminated codec that uses "type" field to choose codec
 * Codec<PaymentMethod> codec = CodecBuilder.of(
 *     Codecs.STRING.fieldOf("type", PaymentMethod::type),
 *     Codecs.discriminatedBy(
 *         "type",  // discriminator field name
 *         Codecs.STRING,  // codec for discriminator value
 *         DiscriminatedCodecProvider.create(PaymentMethodDetails.class, type -> switch (type) {
 *             case "paypal" -> payPalCodec;
 *             case "credit_card" -> creditCardCodec;
 *             default -> throw new IllegalArgumentException("Unknown type: " + type);
 *         })
 *     ).fieldOf("details", PaymentMethod::details)
 * ).create(PaymentMethod::new);
 * }</pre>
 *
 * @author Luis-St
 *
 * @param <C> The base type of values handled by this codec
 * @param <T> The type of the discriminator value
 */

public class DiscriminatedCodec<C, T> extends AbstractCodec<C, Object> {
	
	/**
	 * The name of the discriminator field in the parent object.<br>
	 */
	private final String discriminatedField;
	/**
	 * The codec used to encode and decode the discriminator value.<br>
	 */
	private final Codec<T> discriminatedCodec;
	/**
	 * The provider that maps discriminator values to their corresponding codecs.<br>
	 */
	private final DiscriminatedCodecProvider<C, T> provider;
	
	/**
	 * Constructs a new discriminated codec using the given discriminator field, codec, and provider.<br>
	 *
	 * @param discriminatedField The name of the discriminator field in the parent object
	 * @param discriminatedCodec The codec to use for encoding/decoding the discriminator value
	 * @param provider The provider that maps discriminator values to their corresponding codecs
	 * @throws NullPointerException If discriminated field, discriminated codec, or provider is null
	 */
	public DiscriminatedCodec(@NotNull String discriminatedField, @NotNull Codec<T> discriminatedCodec, @NotNull DiscriminatedCodecProvider<C, T> provider) {
		this.discriminatedField = Objects.requireNonNull(discriminatedField, "Discriminated field must not be null");
		this.discriminatedCodec = Objects.requireNonNull(discriminatedCodec, "Discriminated codec must not be null");
		this.provider = Objects.requireNonNull(provider, "Provider must not be null");
	}
	
	@Override
	public @NotNull Class<C> getType() {
		return this.provider.getCodecType();
	}
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null value as discriminated using '" + this + "'");
		}
		
		Result<R> discriminatorFieldResult = provider.get(current, this.discriminatedField);
		if (discriminatorFieldResult.isError() || discriminatorFieldResult.resultOrThrow() == null) {
			return Result.error("Unable to encode value as discriminated using '" + this + "': Discriminator field '" + this.discriminatedField + "' not found");
		}
		
		Result<T> discriminatorResult = this.discriminatedCodec.decodeStart(provider, current, discriminatorFieldResult.resultOrThrow());
		if (discriminatorResult.isError()) {
			return Result.error("Unable to encode value as discriminated using '" + this + "': Failed to decode discriminator field: " + discriminatorResult.errorOrThrow());
		}
		
		T discriminatorValue = discriminatorResult.resultOrThrow();
		Codec<C> codec = this.provider.getCodec(discriminatorValue);
		if (codec == null) {
			return Result.error("Unable to encode value as discriminated using '" + this + "': No codec found for discriminator value '" + discriminatorValue + "'");
		}
		return codec.encodeStart(provider, current, value);
	}
	
	@Override
	public @NotNull <R> Result<C> decodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as discriminated using '" + this + "'");
		}
		
		Result<R> discriminatorFieldResult = provider.get(current, this.discriminatedField);
		if (discriminatorFieldResult.isError() || discriminatorFieldResult.resultOrThrow() == null) {
			return Result.error("Unable to decode value as discriminated using '" + this + "': Discriminator field '" + this.discriminatedField + "' not found");
		}
		
		Result<T> discriminatorResult = this.discriminatedCodec.decodeStart(provider, current, discriminatorFieldResult.resultOrThrow());
		if (discriminatorResult.isError()) {
			return Result.error("Unable to decode value as discriminated using '" + this + "': Failed to decode discriminator field: " + discriminatorResult.errorOrThrow());
		}
		
		T discriminatorValue = discriminatorResult.resultOrThrow();
		Codec<C> codec = this.provider.getCodec(discriminatorValue);
		if (codec == null) {
			return Result.error("Unable to decode value as discriminated using '" + this + "': No codec found for discriminator value '" + discriminatorValue + "'");
		}
		return codec.decodeStart(provider, current, value);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DiscriminatedCodec<?, ?> that)) return false;
		
		if (!this.discriminatedField.equals(that.discriminatedField)) return false;
		if (!this.discriminatedCodec.equals(that.discriminatedCodec)) return false;
		return this.provider.equals(that.provider);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.discriminatedField, this.discriminatedCodec, this.provider);
	}
	
	@Override
	public String toString() {
		return "DiscriminatedCodec[" + this.discriminatedField + "," + this.discriminatedCodec + "," + this.provider + "]";
	}
	//endregion
}
