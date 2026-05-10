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
import java.util.function.Function;

/**
 * A codec for encoding and decoding polymorphic values using a discriminator field at the same object level.<br>
 * Unlike {@link NestedDiscriminatedCodec}, this codec reads the discriminator from the same map as the value fields,<br>
 * allowing the discriminated fields to be siblings of the discriminator field.<br>
 * <p>
 *     During encoding, this codec:
 * </p>
 * <ol>
 *     <li>Extracts the discriminator value from the Java object using the discriminator getter</li>
 *     <li>Queries the provider to get the appropriate codec for that discriminator value</li>
 *     <li>Delegates encoding to the selected codec which writes all fields (including the discriminator) into the map</li>
 * </ol>
 * <p>
 *     During decoding, this codec:
 * </p>
 * <ol>
 *     <li>Reads the discriminator field from the value map</li>
 *     <li>Decodes the discriminator value using the discriminator codec</li>
 *     <li>Queries the provider to get the appropriate codec for that discriminator value</li>
 *     <li>Delegates decoding to the selected codec which reads all fields from the same map</li>
 * </ol>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * // Define polymorphic types with a shared discriminator
 * sealed interface PaymentMethod {
 *     String type();
 * }
 * record PayPal(String type, String mail) implements PaymentMethod {}
 * record CreditCard(String type, String cardNumber, String cardHolder, LocalDate expiryDate, String cvv) implements PaymentMethod {}
 *
 * // Create codecs for each variant (including the discriminator field)
 * Codec<PayPal> payPalCodec = CodecBuilder.of(
 *     Codecs.STRING.fieldOf("type", PayPal::type),
 *     Codecs.STRING.fieldOf("mail", PayPal::mail)
 * ).create(PayPal::new);
 *
 * Codec<CreditCard> creditCardCodec = CodecBuilder.of(
 *     Codecs.STRING.fieldOf("type", CreditCard::type),
 *     Codecs.STRING.fieldOf("cardNumber", CreditCard::cardNumber),
 *     Codecs.STRING.fieldOf("cardHolder", CreditCard::cardHolder),
 *     Codecs.LOCAL_DATE.fieldOf("expiryDate", CreditCard::expiryDate),
 *     Codecs.STRING.fieldOf("cvv", CreditCard::cvv)
 * ).create(CreditCard::new);
 *
 * // Create flat discriminated codec
 * Codec<PaymentMethod> codec = Codecs.flatDiscriminatedBy(
 *     "type",
 *     Codecs.STRING,
 *     PaymentMethod::type,
 *     DiscriminatedCodecProvider.create(PaymentMethod.class, type -> switch (type) {
 *         case "paypal" -> payPalCodec;
 *         case "credit_card" -> creditCardCodec;
 *         default -> throw new IllegalArgumentException("Unknown type: " + type);
 *     })
 * );
 * }</pre>
 *
 * @see NestedDiscriminatedCodec
 *
 * @author Luis-St
 *
 * @param <C> The base type of values handled by this codec
 * @param <T> The type of the discriminator value
 */
public class FlatDiscriminatedCodec<C, T> extends AbstractCodec<C> {
	
	/**
	 * The name of the discriminator field in the value object.<br>
	 */
	private final String discriminatedField;
	/**
	 * The codec used to decode the discriminator value during decoding.<br>
	 */
	private final Codec<T> discriminatedCodec;
	/**
	 * The provider that maps discriminator values to their corresponding codecs.<br>
	 */
	private final DiscriminatedCodecProvider<C, T> provider;
	/**
	 * The function used to extract the discriminator value from the Java object during encoding.<br>
	 */
	private final Function<C, T> discriminatorGetter;
	
	/**
	 * Constructs a new flat discriminated codec using the given discriminator field, codec, provider, and getter.<br>
	 *
	 * @param discriminatedField The name of the discriminator field in the value object
	 * @param discriminatedCodec The codec to use for decoding the discriminator value
	 * @param provider The provider that maps discriminator values to their corresponding codecs
	 * @param discriminatorGetter The function to extract the discriminator value from the Java object
	 * @throws NullPointerException If any parameter is null
	 */
	public FlatDiscriminatedCodec(@NonNull String discriminatedField, @NonNull Codec<T> discriminatedCodec, @NonNull DiscriminatedCodecProvider<C, T> provider, @NonNull Function<C, T> discriminatorGetter) {
		this.discriminatedField = Objects.requireNonNull(discriminatedField, "Discriminated field must not be null");
		this.discriminatedCodec = Objects.requireNonNull(discriminatedCodec, "Discriminated codec must not be null");
		this.provider = Objects.requireNonNull(provider, "Provider must not be null");
		this.discriminatorGetter = Objects.requireNonNull(discriminatorGetter, "Discriminator getter must not be null");
	}
	
	@Override
	public @NonNull Class<C> getType() {
		return this.provider.getCodecType();
	}
	
	@Override
	public @NonNull <R> R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable C value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null value as flat discriminated", this);
		}
		
		T discriminator = this.discriminatorGetter.apply(value);
		if (discriminator == null) {
			throw new EncoderException("Unable to encode value as flat discriminated: Discriminator getter returned null", this);
		}
		
		Codec<C> codec = this.provider.getCodec(discriminator);
		if (codec == null) {
			throw new EncoderException("Unable to encode value as flat discriminated: No codec found for discriminator value '" + discriminator + "'", this);
		}
		return codec.encode(provider, current, value);
	}
	
	@Override
	public @NonNull <R> C decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as flat discriminated", this);
		}
		
		R discriminatorField;
		try {
			if (!provider.has(value, this.discriminatedField, DecoderException::new)) {
				throw new DecoderException("Discriminator field '" + this.discriminatedField + "' not found", this);
			}
			
			discriminatorField = provider.get(value, this.discriminatedField, DecoderException::new);
		} catch (DecoderException e) {
			throw new DecoderException("Unable to decode value as flat discriminated: Discriminator field '" + this.discriminatedField + "' not found", this);
		}
		
		T discriminator;
		try {
			discriminator = this.discriminatedCodec.decode(provider, value, discriminatorField);
		} catch (DecoderException e) {
			throw new DecoderException("Unable to decode value as flat discriminated: Failed to decode discriminator field", this, e);
		}
		
		Codec<C> codec = this.provider.getCodec(discriminator);
		if (codec == null) {
			throw new DecoderException("Unable to decode value as flat discriminated: No codec found for discriminator value '" + discriminator + "'", this);
		}
		return codec.decode(provider, current, value);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FlatDiscriminatedCodec<?, ?> that)) return false;
		
		if (!this.discriminatedField.equals(that.discriminatedField)) return false;
		if (!this.discriminatedCodec.equals(that.discriminatedCodec)) return false;
		if (!this.provider.equals(that.provider)) return false;
		return this.discriminatorGetter.equals(that.discriminatorGetter);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.discriminatedField, this.discriminatedCodec, this.provider, this.discriminatorGetter);
	}
	
	@Override
	public String toString() {
		return "FlatDiscriminatedCodec[" + this.discriminatedField + "," + this.discriminatedCodec + "," + this.provider + "]";
	}
	//endregion
}
