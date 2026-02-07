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
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.IpAddresses;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpAddressCodec}.<br>
 *
 * @author Luis-St
 */
class IpAddressCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), address));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, address));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as ip address"));
	}
	
	@Test
	void encodeWithIPv4() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("192.168.1.1"), result);
	}
	
	@Test
	void encodeWithLocalhost() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		IpAddress<?> address = IpAddresses.parseIpv4("127.0.0.1");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("127.0.0.1"), result);
	}
	
	@Test
	void encodeWithIPv6() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		IpAddress<?> address = IpAddresses.parseIpv6("2001:db8::1");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("2001:db8:0:0:0:0:0:1"), result);
	}
	
	@Test
	void encodeWithIPv6Localhost() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		IpAddress<?> address = IpAddresses.parseIpv6("::1");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("0:0:0:0:0:0:0:1"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithIPv4() throws EncoderException {
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		String result = codec.encodeKey(address);
		assertEquals("192.168.1.1", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as ip address"));
	}
	
	@Test
	void decodeWithValidIPv4() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		IpAddress<?> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertEquals(IpAddresses.parseIpv4("192.168.1.1"), result);
	}
	
	@Test
	void decodeWithLocalhost() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		IpAddress<?> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("127.0.0.1"));
		assertEquals(IpAddresses.parseIpv4("127.0.0.1"), result);
	}
	
	@Test
	void decodeWithValidIPv6() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		IpAddress<?> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2001:db8::1"));
		assertEquals(IpAddresses.parseIpv6("2001:db8::1"), result);
	}
	
	@Test
	void decodeWithIPv6Localhost() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		IpAddress<?> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("::1"));
		assertEquals(IpAddresses.parseIpv6("::1"), result);
	}
	
	@Test
	void decodeWithInvalidAddress() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("999.999.999.999")));
		assertTrue(exception.getMessage().contains("Unable to decode ip address"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidIPv4() throws DecoderException {
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		IpAddress<?> result = codec.decodeKey("192.168.1.1");
		assertEquals(IpAddresses.parseIpv4("192.168.1.1"), result);
	}
	
	@Test
	void decodeKeyWithLocalhost() throws DecoderException {
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		IpAddress<?> result = codec.decodeKey("127.0.0.1");
		assertEquals(IpAddresses.parseIpv4("127.0.0.1"), result);
	}
	
	@Test
	void decodeKeyWithInvalidAddress() {
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("999.999.999.999"));
		assertTrue(exception.getMessage().contains("Unable to decode key '999.999.999.999' as ip address"));
	}
	
	@Test
	void toStringRepresentation() {
		IpAddressCodec codec = new IpAddressCodec();
		assertEquals("IpAddressCodec", codec.toString());
	}
}
