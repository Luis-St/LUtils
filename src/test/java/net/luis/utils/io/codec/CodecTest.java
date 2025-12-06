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

import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.codec.types.struct.AnyCodec;
import net.luis.utils.io.codec.types.struct.OptionalCodec;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Codec}.<br>
 *
 * @author Luis-St
 */
class CodecTest {
	

	

	

	

	

	

	

	@Test
	void anyCodecs() {
		assertThrows(NullPointerException.class, () -> Codecs.any((Codec<String>[]) null));
		assertThrows(IllegalArgumentException.class, () -> Codecs.any(List.of()));
		assertThrows(IllegalArgumentException.class, () -> Codecs.any(List.of(STRING)));
		assertInstanceOf(AnyCodec.class, Codecs.any(STRING.union("a", "b"), STRING.union("c", "d")));

		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

		// Create codecs for different payment methods (simulated with strings)
		Codec<String> creditCard = STRING.union("cc:visa", "cc:mastercard", "cc:amex");
		Codec<String> paypal = STRING.union("paypal:user@example.com", "paypal:another@example.com");

		Codec<String> paymentCodec = Codecs.any(creditCard, paypal);

		// Test credit card payment
		assertTrue(paymentCodec.encodeStart(provider, provider.empty(), "cc:visa").isSuccess());
		assertTrue(paymentCodec.decodeStart(provider, provider.empty(), new JsonPrimitive("cc:visa")).isSuccess());

		// Test PayPal payment
		assertTrue(paymentCodec.encodeStart(provider, provider.empty(), "paypal:user@example.com").isSuccess());
		assertTrue(paymentCodec.decodeStart(provider, provider.empty(), new JsonPrimitive("paypal:user@example.com")).isSuccess());

		// Test invalid payment
		assertTrue(paymentCodec.encodeStart(provider, provider.empty(), "invalid").isError());
		assertTrue(paymentCodec.decodeStart(provider, provider.empty(), new JsonPrimitive("invalid")).isError());
	}

	@Test
	void optionalCodecs() {
		assertInstanceOf(OptionalCodec.class, INTEGER.optional());

		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> optionalCodec = INTEGER.optional();

		JsonElement encodedPresent = optionalCodec.encode(provider, Optional.of(42));
		assertEquals(new JsonPrimitive(42), encodedPresent);
		Optional<Integer> decodedPresent = optionalCodec.decode(provider, encodedPresent);
		assertTrue(decodedPresent.isPresent());
		assertEquals(42, decodedPresent.get());

		JsonElement encodedEmpty = optionalCodec.encode(provider, Optional.empty());
		assertFalse(encodedEmpty.isJsonNull());
		assertFalse(encodedEmpty.isJsonPrimitive());
		assertFalse(encodedEmpty.isJsonArray());
		assertFalse(encodedEmpty.isJsonObject());
		Optional<Integer> decodedEmpty = optionalCodec.decode(provider, encodedEmpty);
		assertTrue(decodedEmpty.isEmpty());
	}
	

	

	

	

	

	

	

	

	

	

	

	
	private record TestObject(@NotNull String name) {}
}
