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

package net.luis.utils.io.codec.types.io;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.io.network.address.mac.MacAddress;
import net.luis.utils.io.network.address.mac.MacAddresses;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MacAddressCodec}.<br>
 *
 * @author Luis-St
 */
class MacAddressCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		MacAddress address = MacAddresses.parse("00:1A:2B:3C:4D:5E");
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), address));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, address));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as MAC address"));
	}
	
	@Test
	void encodeStartWithValidMac() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		MacAddress address = MacAddresses.parse("00:1A:2B:3C:4D:5E");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("00:1a:2b:3c:4d:5e"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithBroadcast() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		MacAddress address = MacAddresses.broadcast();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("ff:ff:ff:ff:ff:ff"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		MacAddress address = MacAddresses.zero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("00:00:00:00:00:00"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		Codec<MacAddress> codec = new MacAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValidMac() {
		Codec<MacAddress> codec = new MacAddressCodec();
		MacAddress address = MacAddresses.parse("00:1A:2B:3C:4D:5E");
		
		Result<String> result = codec.encodeKey(address);
		assertTrue(result.isSuccess());
		assertEquals("00:1a:2b:3c:4d:5e", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("00:1A:2B:3C:4D:5E")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as MAC address"));
	}
	
	@Test
	void decodeStartWithColonFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1A:2B:3C:4D:5E"));
		assertTrue(result.isSuccess());
		assertEquals(MacAddresses.parse("00:1A:2B:3C:4D:5E"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithDashFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("00-1A-2B-3C-4D-5E"));
		assertTrue(result.isSuccess());
		assertEquals(MacAddresses.parse("00:1A:2B:3C:4D:5E"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithCiscoFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("001A.2B3C.4D5E"));
		assertTrue(result.isSuccess());
		assertEquals(MacAddresses.parse("00:1A:2B:3C:4D:5E"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithBareFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("001A2B3C4D5E"));
		assertTrue(result.isSuccess());
		assertEquals(MacAddresses.parse("00:1A:2B:3C:4D:5E"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithBroadcast() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("FF:FF:FF:FF:FF:FF"));
		assertTrue(result.isSuccess());
		assertEquals(MacAddresses.broadcast(), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-mac"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode MAC address"));
	}
	
	@Test
	void decodeStartWithInvalidLength() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1A:2B"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode MAC address"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		Codec<MacAddress> codec = new MacAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidMac() {
		Codec<MacAddress> codec = new MacAddressCodec();
		
		Result<MacAddress> result = codec.decodeKey("00:1A:2B:3C:4D:5E");
		assertTrue(result.isSuccess());
		assertEquals(MacAddresses.parse("00:1A:2B:3C:4D:5E"), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidFormat() {
		Codec<MacAddress> codec = new MacAddressCodec();
		
		Result<MacAddress> result = codec.decodeKey("not-a-mac");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'not-a-mac' as MAC address"));
	}
	
	@Test
	void toStringRepresentation() {
		MacAddressCodec codec = new MacAddressCodec();
		assertEquals("MacAddressCodec", codec.toString());
	}
}
