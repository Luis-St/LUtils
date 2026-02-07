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

package net.luis.utils.io.codec.types.i18n;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CurrencyCodec}.<br>
 *
 * @author Luis-St
 */
class CurrencyCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		Currency currency = Currency.getInstance("USD");
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), currency));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, currency));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as currency"));
	}
	
	@Test
	void encodeWithUSD() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		Currency currency = Currency.getInstance("USD");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), currency);
		assertEquals(new JsonPrimitive("USD"), result);
	}
	
	@Test
	void encodeWithEUR() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		Currency currency = Currency.getInstance("EUR");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), currency);
		assertEquals(new JsonPrimitive("EUR"), result);
	}
	
	@Test
	void encodeWithGBP() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		Currency currency = Currency.getInstance("GBP");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), currency);
		assertEquals(new JsonPrimitive("GBP"), result);
	}
	
	@Test
	void encodeWithJPY() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		Currency currency = Currency.getInstance("JPY");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), currency);
		assertEquals(new JsonPrimitive("JPY"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithCurrency() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		Currency currency = Currency.getInstance("CHF");
		
		String result = codec.encodeKey(currency);
		assertEquals("CHF", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("USD")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as currency"));
	}
	
	@Test
	void decodeWithValidUSD() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Currency result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("USD"));
		assertEquals(Currency.getInstance("USD"), result);
	}
	
	@Test
	void decodeWithValidEUR() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Currency result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("EUR"));
		assertEquals(Currency.getInstance("EUR"), result);
	}
	
	@Test
	void decodeWithValidGBP() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Currency result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("GBP"));
		assertEquals(Currency.getInstance("GBP"), result);
	}
	
	@Test
	void decodeWithValidJPY() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Currency result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("JPY"));
		assertEquals(Currency.getInstance("JPY"), result);
	}
	
	@Test
	void decodeWithInvalidCurrency() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("12345")));
		assertTrue(exception.getMessage().contains("Unable to decode currency"));
	}
	
	@Test
	void decodeWithLowercaseCurrency() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("usd")));
		assertTrue(exception.getMessage().contains("Unable to decode currency"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidCurrency() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Currency result = codec.decodeKey("USD");
		assertEquals(Currency.getInstance("USD"), result);
	}
	
	@Test
	void decodeKeyWithEUR() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Currency result = codec.decodeKey("EUR");
		assertEquals(Currency.getInstance("EUR"), result);
	}
	
	@Test
	void decodeKeyWithInvalidCurrency() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("ABCDE"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'ABCDE' as currency"));
	}
	
	@Test
	void toStringRepresentation() {
		CurrencyCodec codec = new CurrencyCodec();
		assertEquals("CurrencyCodec", codec.toString());
	}
}
