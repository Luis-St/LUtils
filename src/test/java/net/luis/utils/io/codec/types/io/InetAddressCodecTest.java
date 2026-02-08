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
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InetAddressCodec}.<br>
 *
 * @author Luis-St
 */
class InetAddressCodecTest {
	
	@Test
	void encodeNullChecks() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		InetAddress address = InetAddress.getByName("192.168.1.1");
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), address));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, address));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as network address"));
	}
	
	@Test
	void encodeWithIPv4() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		InetAddress address = InetAddress.getByName("192.168.1.1");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("192.168.1.1"), result);
	}
	
	@Test
	void encodeWithLocalhost() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		InetAddress address = InetAddress.getByName("127.0.0.1");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("127.0.0.1"), result);
	}
	
	@Test
	void encodeWithIPv6() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		InetAddress address = InetAddress.getByName("2001:db8::1");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("2001:db8:0:0:0:0:0:1"), result);
	}
	
	@Test
	void encodeWithIPv6Localhost() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		InetAddress address = InetAddress.getByName("::1");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("0:0:0:0:0:0:0:1"), result);
	}
	
	@Test
	void encodeKeyNullChecks() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithIPv4() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		InetAddress address = InetAddress.getByName("192.168.1.1");
		
		String result = codec.encodeKey(address);
		assertEquals("192.168.1.1", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as network address"));
	}
	
	@Test
	void decodeWithValidIPv4() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		InetAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertEquals(InetAddress.getByName("192.168.1.1"), result);
	}
	
	@Test
	void decodeWithLocalhost() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		InetAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("127.0.0.1"));
		assertEquals(InetAddress.getByName("127.0.0.1"), result);
	}
	
	@Test
	void decodeWithValidIPv6() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		InetAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2001:db8::1"));
		assertEquals(InetAddress.getByName("2001:db8::1"), result);
	}
	
	@Test
	void decodeWithIPv6Localhost() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		InetAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("::1"));
		assertEquals(InetAddress.getByName("::1"), result);
	}
	
	@Test
	void decodeWithInvalidAddress() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("999.999.999.999")));
		assertTrue(exception.getMessage().contains("Unable to decode network address"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidIPv4() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		InetAddress result = codec.decodeKey("192.168.1.1");
		assertEquals(InetAddress.getByName("192.168.1.1"), result);
	}
	
	@Test
	void decodeKeyWithLocalhost() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		InetAddress result = codec.decodeKey("127.0.0.1");
		assertEquals(InetAddress.getByName("127.0.0.1"), result);
	}
	
	@Test
	void decodeKeyWithInvalidAddress() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("999.999.999.999"));
		assertTrue(exception.getMessage().contains("Unable to decode key '999.999.999.999' as network address"));
	}
	
	@Test
	void toStringRepresentation() {
		InetAddressCodec codec = new InetAddressCodec();
		assertEquals("InetAddressCodec", codec.toString());
	}
}
