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

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocaleCodec}.<br>
 *
 * @author Luis-St
 */
class LocaleCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.US;
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), locale));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, locale));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as locale"));
	}
	
	@Test
	void encodeWithUS() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.US;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), locale);
		assertEquals(new JsonPrimitive("en-US"), result);
	}
	
	@Test
	void encodeWithUK() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.UK;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), locale);
		assertEquals(new JsonPrimitive("en-GB"), result);
	}
	
	@Test
	void encodeWithGermany() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.GERMANY;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), locale);
		assertEquals(new JsonPrimitive("de-DE"), result);
	}
	
	@Test
	void encodeWithFrance() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.FRANCE;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), locale);
		assertEquals(new JsonPrimitive("fr-FR"), result);
	}
	
	@Test
	void encodeWithRoot() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.ROOT;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), locale);
		assertEquals(new JsonPrimitive("und"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithLocale() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		Locale locale = Locale.JAPAN;
		
		String result = codec.encodeKey(locale);
		assertEquals("ja-JP", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("en-US")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as locale"));
	}
	
	@Test
	void decodeWithValidUS() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Locale result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("en-US"));
		assertEquals(Locale.US, result);
	}
	
	@Test
	void decodeWithValidUK() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Locale result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("en-GB"));
		assertEquals(Locale.UK, result);
	}
	
	@Test
	void decodeWithValidGermany() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Locale result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("de-DE"));
		assertEquals(Locale.GERMANY, result);
	}
	
	@Test
	void decodeWithValidFrance() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Locale result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("fr-FR"));
		assertEquals(Locale.FRANCE, result);
	}
	
	@Test
	void decodeWithValidJapan() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Locale result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("ja-JP"));
		assertEquals(Locale.JAPAN, result);
	}
	
	@Test
	void decodeWithRoot() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Locale result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("und"));
		assertEquals(Locale.ROOT, result);
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidLocale() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Locale result = codec.decodeKey("en-US");
		assertEquals(Locale.US, result);
	}
	
	@Test
	void decodeKeyWithGermany() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Locale result = codec.decodeKey("de-DE");
		assertEquals(Locale.GERMANY, result);
	}
	
	@Test
	void decodeKeyWithJapan() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Locale> codec = new LocaleCodec();
		
		Locale result = codec.decodeKey("ja-JP");
		assertEquals(Locale.JAPAN, result);
	}
	
	@Test
	void toStringRepresentation() {
		LocaleCodec codec = new LocaleCodec();
		assertEquals("LocaleCodec", codec.toString());
	}
}
