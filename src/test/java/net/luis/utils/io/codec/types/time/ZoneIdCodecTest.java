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

package net.luis.utils.io.codec.types.time;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
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
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.of("Europe/Berlin");
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), zoneId));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, zoneId));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as zone id"));
	}
	
	@Test
	void encodeStartWithEuropeanZone() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.of("Europe/Berlin");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), zoneId);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("Europe/Berlin"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithAmericanZone() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.of("America/New_York");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), zoneId);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("America/New_York"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithUTC() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.of("UTC");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), zoneId);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("UTC"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSystemDefault() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.systemDefault();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), zoneId);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(zoneId.getId()), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithOffsetZone() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.of("+02:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), zoneId);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("+02:00"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithZone() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		ZoneId zoneId = ZoneId.of("Europe/Berlin");
		
		Result<String> result = codec.encodeKey(zoneId);
		assertTrue(result.isSuccess());
		assertEquals("Europe/Berlin", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("Europe/Berlin")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as zone id"));
	}
	
	@Test
	void decodeStartWithValidEuropeanZone() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Europe/Berlin"));
		assertTrue(result.isSuccess());
		assertEquals(ZoneId.of("Europe/Berlin"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidAmericanZone() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("America/New_York"));
		assertTrue(result.isSuccess());
		assertEquals(ZoneId.of("America/New_York"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithUTC() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("UTC"));
		assertTrue(result.isSuccess());
		assertEquals(ZoneId.of("UTC"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithOffsetZone() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+02:00"));
		assertTrue(result.isSuccess());
		assertEquals(ZoneId.of("+02:00"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidZone() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Invalid/Zone"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode zone id"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidZone() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		Result<ZoneId> result = codec.decodeKey("Europe/Berlin");
		assertTrue(result.isSuccess());
		assertEquals(ZoneId.of("Europe/Berlin"), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithUTC() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		Result<ZoneId> result = codec.decodeKey("UTC");
		assertTrue(result.isSuccess());
		assertEquals(ZoneId.of("UTC"), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidZone() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = new ZoneIdCodec();
		
		Result<ZoneId> result = codec.decodeKey("Invalid/Zone");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'Invalid/Zone' as zone id"));
	}
	
	@Test
	void toStringRepresentation() {
		ZoneIdCodec codec = new ZoneIdCodec();
		assertEquals("ZoneIdCodec", codec.toString());
	}
}
