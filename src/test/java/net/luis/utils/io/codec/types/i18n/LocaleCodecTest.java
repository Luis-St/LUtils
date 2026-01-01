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

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocaleCodec}.<br>
 *
 * @author Luis-St
 */
class LocaleCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.US;
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), locale));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, locale));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as locale"));
	}
	
	@Test
	void encodeStartWithUS() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.US;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), locale);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("en-US"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithUK() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.UK;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), locale);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("en-GB"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithGermany() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.GERMANY;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), locale);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("de-DE"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithFrance() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.FRANCE;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), locale);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("fr-FR"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithRoot() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.ROOT;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), locale);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("und"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithLocale() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.JAPAN;
		
		Result<String> result = codec.encodeKey(locale);
		assertTrue(result.isSuccess());
		assertEquals("ja-JP", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("en-US")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Result<Locale> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as locale"));
	}
	
	@Test
	void decodeStartWithValidUS() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Result<Locale> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("en-US"));
		assertTrue(result.isSuccess());
		assertEquals(Locale.US, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidUK() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Result<Locale> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("en-GB"));
		assertTrue(result.isSuccess());
		assertEquals(Locale.UK, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidGermany() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Result<Locale> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("de-DE"));
		assertTrue(result.isSuccess());
		assertEquals(Locale.GERMANY, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidFrance() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Result<Locale> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("fr-FR"));
		assertTrue(result.isSuccess());
		assertEquals(Locale.FRANCE, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidJapan() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Result<Locale> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("ja-JP"));
		assertTrue(result.isSuccess());
		assertEquals(Locale.JAPAN, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithRoot() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Result<Locale> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("und"));
		assertTrue(result.isSuccess());
		assertEquals(Locale.ROOT, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Result<Locale> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidLocale() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Result<Locale> result = codec.decodeKey("en-US");
		assertTrue(result.isSuccess());
		assertEquals(Locale.US, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithGermany() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Result<Locale> result = codec.decodeKey("de-DE");
		assertTrue(result.isSuccess());
		assertEquals(Locale.GERMANY, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithJapan() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Result<Locale> result = codec.decodeKey("ja-JP");
		assertTrue(result.isSuccess());
		assertEquals(Locale.JAPAN, result.resultOrThrow());
	}
	
	@Test
	void toStringRepresentation() {
		LocaleCodec codec = new LocaleCodec();
		assertEquals("LocaleCodec", codec.toString());
	}
}
