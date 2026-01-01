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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
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
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		Currency currency = Currency.getInstance("USD");
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), currency));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, currency));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as currency"));
	}
	
	@Test
	void encodeStartWithUSD() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		Currency currency = Currency.getInstance("USD");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), currency);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("USD"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithEUR() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		Currency currency = Currency.getInstance("EUR");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), currency);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("EUR"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithGBP() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		Currency currency = Currency.getInstance("GBP");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), currency);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("GBP"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithJPY() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		Currency currency = Currency.getInstance("JPY");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), currency);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("JPY"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithCurrency() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		Currency currency = Currency.getInstance("CHF");
		
		Result<String> result = codec.encodeKey(currency);
		assertTrue(result.isSuccess());
		assertEquals("CHF", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("USD")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Result<Currency> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as currency"));
	}
	
	@Test
	void decodeStartWithValidUSD() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Result<Currency> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("USD"));
		assertTrue(result.isSuccess());
		assertEquals(Currency.getInstance("USD"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidEUR() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Result<Currency> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("EUR"));
		assertTrue(result.isSuccess());
		assertEquals(Currency.getInstance("EUR"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidGBP() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Result<Currency> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("GBP"));
		assertTrue(result.isSuccess());
		assertEquals(Currency.getInstance("GBP"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidJPY() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Result<Currency> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("JPY"));
		assertTrue(result.isSuccess());
		assertEquals(Currency.getInstance("JPY"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidCurrency() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Result<Currency> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("12345"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode currency"));
	}
	
	@Test
	void decodeStartWithLowercaseCurrency() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Result<Currency> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("usd"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode currency"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Result<Currency> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidCurrency() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Result<Currency> result = codec.decodeKey("USD");
		assertTrue(result.isSuccess());
		assertEquals(Currency.getInstance("USD"), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithEUR() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Result<Currency> result = codec.decodeKey("EUR");
		assertTrue(result.isSuccess());
		assertEquals(Currency.getInstance("EUR"), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidCurrency() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Currency> codec = new CurrencyCodec();
		
		Result<Currency> result = codec.decodeKey("ABCDE");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'ABCDE' as currency"));
	}
	
	@Test
	void toStringRepresentation() {
		CurrencyCodec codec = new CurrencyCodec();
		assertEquals("CurrencyCodec", codec.toString());
	}
}
