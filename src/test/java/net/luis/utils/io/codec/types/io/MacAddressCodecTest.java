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
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.io.network.address.mac.MacAddress;
import net.luis.utils.io.network.address.mac.MacAddresses;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MacAddressCodec}.<br>
 *
 * @author Luis-St
 */
class MacAddressCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		MacAddress address = MacAddresses.parse("00:1A:2B:3C:4D:5E");
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), address));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, address));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as MAC address"));
	}
	
	@Test
	void encodeWithValidMac() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		MacAddress address = MacAddresses.parse("00:1A:2B:3C:4D:5E");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("00:1a:2b:3c:4d:5e"), result);
	}
	
	@Test
	void encodeWithBroadcast() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		MacAddress address = MacAddresses.broadcast();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("ff:ff:ff:ff:ff:ff"), result);
	}
	
	@Test
	void encodeWithZero() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		MacAddress address = MacAddresses.zero();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("00:00:00:00:00:00"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		Codec<MacAddress> codec = new MacAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValidMac() throws EncoderException {
		Codec<MacAddress> codec = new MacAddressCodec();
		MacAddress address = MacAddresses.parse("00:1A:2B:3C:4D:5E");
		
		String result = codec.encodeKey(address);
		assertEquals("00:1a:2b:3c:4d:5e", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("00:1A:2B:3C:4D:5E")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as MAC address"));
	}
	
	@Test
	void decodeWithColonFormat() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		MacAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1A:2B:3C:4D:5E"));
		assertEquals(MacAddresses.parse("00:1A:2B:3C:4D:5E"), result);
	}
	
	@Test
	void decodeWithDashFormat() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		MacAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00-1A-2B-3C-4D-5E"));
		assertEquals(MacAddresses.parse("00:1A:2B:3C:4D:5E"), result);
	}
	
	@Test
	void decodeWithCiscoFormat() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		MacAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("001A.2B3C.4D5E"));
		assertEquals(MacAddresses.parse("00:1A:2B:3C:4D:5E"), result);
	}
	
	@Test
	void decodeWithBareFormat() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		MacAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("001A2B3C4D5E"));
		assertEquals(MacAddresses.parse("00:1A:2B:3C:4D:5E"), result);
	}
	
	@Test
	void decodeWithBroadcast() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		MacAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("FF:FF:FF:FF:FF:FF"));
		assertEquals(MacAddresses.broadcast(), result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-mac")));
		assertTrue(exception.getMessage().contains("Unable to decode MAC address"));
	}
	
	@Test
	void decodeWithInvalidLength() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1A:2B")));
		assertTrue(exception.getMessage().contains("Unable to decode MAC address"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		Codec<MacAddress> codec = new MacAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidMac() throws DecoderException {
		Codec<MacAddress> codec = new MacAddressCodec();
		
		MacAddress result = codec.decodeKey("00:1A:2B:3C:4D:5E");
		assertEquals(MacAddresses.parse("00:1A:2B:3C:4D:5E"), result);
	}
	
	@Test
	void decodeKeyWithInvalidFormat() {
		Codec<MacAddress> codec = new MacAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("not-a-mac"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'not-a-mac' as MAC address"));
	}
	
	@Test
	void toStringRepresentation() {
		MacAddressCodec codec = new MacAddressCodec();
		assertEquals("MacAddressCodec", codec.toString());
	}
}
