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

package net.luis.utils.io.codec.types.temporal.zoned;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ZoneOffsetCodec}.<br>
 *
 * @author Luis-St
 */
class ZoneOffsetCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.of("+02:00");
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), offset));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, offset));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as zone offset"));
	}
	
	@Test
	void encodeWithPositiveOffset() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.of("+02:00");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), offset);
		assertEquals(new JsonPrimitive("+02:00"), result);
	}
	
	@Test
	void encodeWithNegativeOffset() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.of("-05:00");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), offset);
		assertEquals(new JsonPrimitive("-05:00"), result);
	}
	
	@Test
	void encodeWithUTC() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.UTC;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), offset);
		assertEquals(new JsonPrimitive("Z"), result);
	}
	
	@Test
	void encodeWithMaxOffset() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.MAX;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), offset);
		assertEquals(new JsonPrimitive("+18:00"), result);
	}
	
	@Test
	void encodeWithMinOffset() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.MIN;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), offset);
		assertEquals(new JsonPrimitive("-18:00"), result);
	}
	
	@Test
	void encodeWithOffsetWithMinutes() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.of("+05:30");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), offset);
		assertEquals(new JsonPrimitive("+05:30"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithOffset() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.of("+02:00");
		
		String result = codec.encodeKey(offset);
		assertEquals("+02:00", result);
	}
	
	@Test
	void encodeKeyWithUTC() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		ZoneOffset offset = ZoneOffset.UTC;
		
		String result = codec.encodeKey(offset);
		assertEquals("Z", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("+02:00")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as zone offset"));
	}
	
	@Test
	void decodeWithValidPositiveOffset() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		ZoneOffset result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+02:00"));
		assertEquals(ZoneOffset.of("+02:00"), result);
	}
	
	@Test
	void decodeWithValidNegativeOffset() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		ZoneOffset result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-05:00"));
		assertEquals(ZoneOffset.of("-05:00"), result);
	}
	
	@Test
	void decodeWithUTC() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		ZoneOffset result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Z"));
		assertEquals(ZoneOffset.UTC, result);
	}
	
	@Test
	void decodeWithOffsetWithMinutes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		ZoneOffset result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:30"));
		assertEquals(ZoneOffset.of("+05:30"), result);
	}
	
	@Test
	void decodeWithInvalidOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid")));
		assertTrue(exception.getMessage().contains("Unable to decode zone offset"));
	}
	
	@Test
	void decodeWithOutOfRangeOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+20:00")));
		assertTrue(exception.getMessage().contains("Unable to decode zone offset"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidOffset() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		ZoneOffset result = codec.decodeKey("+02:00");
		assertEquals(ZoneOffset.of("+02:00"), result);
	}
	
	@Test
	void decodeKeyWithUTC() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		ZoneOffset result = codec.decodeKey("Z");
		assertEquals(ZoneOffset.UTC, result);
	}
	
	@Test
	void decodeKeyWithInvalidOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = new ZoneOffsetCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("invalid"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'invalid' as zone offset"));
	}
	
	@Test
	void toStringRepresentation() {
		ZoneOffsetCodec codec = new ZoneOffsetCodec();
		assertEquals("ZoneOffsetCodec", codec.toString());
	}
}
