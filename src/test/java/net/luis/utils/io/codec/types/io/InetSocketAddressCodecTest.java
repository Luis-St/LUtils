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

import java.net.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InetSocketAddressCodec}.<br>
 *
 * @author Luis-St
 */
class InetSocketAddressCodecTest {
	
	@Test
	void encodeNullChecks() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), address));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, address));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as network socket address"));
	}
	
	@Test
	void encodeWithIPv4AndPort() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("192.168.1.1:8080"), result);
	}
	
	@Test
	void encodeWithLocalhost() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 3000);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("127.0.0.1:3000"), result);
	}
	
	@Test
	void encodeWithIPv6AndPort() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("2001:db8::1"), 8080);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("[2001:db8:0:0:0:0:0:1]:8080"), result);
	}
	
	@Test
	void encodeWithIPv6Localhost() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("::1"), 9000);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("[0:0:0:0:0:0:0:1]:9000"), result);
	}
	
	@Test
	void encodeWithZeroPort() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 0);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("192.168.1.1:0"), result);
	}
	
	@Test
	void encodeWithMaxPort() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 65535);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), address);
		assertEquals(new JsonPrimitive("192.168.1.1:65535"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithIPv4() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		
		String result = codec.encodeKey(address);
		assertEquals("192.168.1.1:8080", result);
	}
	
	@Test
	void encodeKeyWithIPv6() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("2001:db8::1"), 8080);
		
		String result = codec.encodeKey(address);
		assertEquals("[2001:db8:0:0:0:0:0:1]:8080", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as network socket address"));
	}
	
	@Test
	void decodeWithValidIPv4() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		InetSocketAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080"));
		assertEquals(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080), result);
	}
	
	@Test
	void decodeWithLocalhost() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		InetSocketAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("127.0.0.1:3000"));
		assertEquals(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 3000), result);
	}
	
	@Test
	void decodeWithValidIPv6() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		InetSocketAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("[2001:db8::1]:8080"));
		assertEquals(new InetSocketAddress(InetAddress.getByName("2001:db8::1"), 8080), result);
	}
	
	@Test
	void decodeWithIPv6Localhost() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		InetSocketAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("[::1]:9000"));
		assertEquals(new InetSocketAddress(InetAddress.getByName("::1"), 9000), result);
	}
	
	@Test
	void decodeWithZeroPort() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		InetSocketAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:0"));
		assertEquals(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 0), result);
	}
	
	@Test
	void decodeWithMaxPort() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		InetSocketAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:65535"));
		assertEquals(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 65535), result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
		assertTrue(exception.getMessage().contains("Invalid network socket address format"));
	}
	
	@Test
	void decodeWithInvalidIPv6Format() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("[2001:db8::1")));
		assertTrue(exception.getMessage().contains("Invalid IPv6 socket address format"));
	}
	
	@Test
	void decodeWithInvalidPort() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:99999")));
		assertTrue(exception.getMessage().contains("Port number out of range"));
	}
	
	@Test
	void decodeWithNegativePort() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:-1")));
		assertTrue(exception.getMessage().contains("Port number out of range"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidIPv4() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		InetSocketAddress result = codec.decodeKey("192.168.1.1:8080");
		assertEquals(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080), result);
	}
	
	@Test
	void decodeKeyWithValidIPv6() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		InetSocketAddress result = codec.decodeKey("[2001:db8::1]:8080");
		assertEquals(new InetSocketAddress(InetAddress.getByName("2001:db8::1"), 8080), result);
	}
	
	@Test
	void decodeKeyWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.1"));
		assertTrue(exception.getMessage().contains("Invalid network socket address format"));
	}
	
	@Test
	void decodeKeyWithInvalidPort() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.1:99999"));
		assertTrue(exception.getMessage().contains("Port number out of range"));
	}
	
	@Test
	void toStringRepresentation() {
		InetSocketAddressCodec codec = new InetSocketAddressCodec();
		assertEquals("InetSocketAddressCodec", codec.toString());
	}
}
