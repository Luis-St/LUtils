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

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ZoneIdCodec}.<br>
 *
 * @author Luis-St
 */
class ZoneIdCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.of("Europe/Berlin");
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), zoneId));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, zoneId));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as zone id"));
	}
	
	@Test
	void encodeWithEuropeanZone() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.of("Europe/Berlin");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), zoneId);
		assertEquals(new JsonPrimitive("Europe/Berlin"), result);
	}
	
	@Test
	void encodeWithAmericanZone() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.of("America/New_York");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), zoneId);
		assertEquals(new JsonPrimitive("America/New_York"), result);
	}
	
	@Test
	void encodeWithUTC() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.of("UTC");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), zoneId);
		assertEquals(new JsonPrimitive("UTC"), result);
	}
	
	@Test
	void encodeWithSystemDefault() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.systemDefault();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), zoneId);
		assertEquals(new JsonPrimitive(zoneId.getId()), result);
	}
	
	@Test
	void encodeWithOffsetZone() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.of("+02:00");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), zoneId);
		assertEquals(new JsonPrimitive("+02:00"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithZone() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.of("Europe/Berlin");
		
		String result = codec.encodeKey(zoneId);
		assertEquals("Europe/Berlin", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("Europe/Berlin")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as zone id"));
	}
	
	@Test
	void decodeWithValidEuropeanZone() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		ZoneId result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Europe/Berlin"));
		assertEquals(ZoneId.of("Europe/Berlin"), result);
	}
	
	@Test
	void decodeWithValidAmericanZone() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		ZoneId result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("America/New_York"));
		assertEquals(ZoneId.of("America/New_York"), result);
	}
	
	@Test
	void decodeWithUTC() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		ZoneId result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("UTC"));
		assertEquals(ZoneId.of("UTC"), result);
	}
	
	@Test
	void decodeWithOffsetZone() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		ZoneId result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+02:00"));
		assertEquals(ZoneId.of("+02:00"), result);
	}
	
	@Test
	void decodeWithInvalidZone() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Invalid/Zone")));
		assertTrue(exception.getMessage().contains("Unable to decode zone id"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidZone() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		ZoneId result = codec.decodeKey("Europe/Berlin");
		assertEquals(ZoneId.of("Europe/Berlin"), result);
	}
	
	@Test
	void decodeKeyWithUTC() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		ZoneId result = codec.decodeKey("UTC");
		assertEquals(ZoneId.of("UTC"), result);
	}
	
	@Test
	void decodeKeyWithInvalidZone() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("Invalid/Zone"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'Invalid/Zone' as zone id"));
	}
	
	@Test
	void toStringRepresentation() {
		ZoneIdCodec codec = new ZoneIdCodec();
		assertEquals("ZoneIdCodec", codec.toString());
	}
}
