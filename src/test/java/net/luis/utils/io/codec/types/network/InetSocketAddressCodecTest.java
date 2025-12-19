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

package net.luis.utils.io.codec.types.network;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
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
	void encodeStartNullChecks() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);

		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), address));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, address));
	}

	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as inet socket address"));
	}

	@Test
	void encodeStartWithIPv4AndPort() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("192.168.1.1:8080"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithLocalhost() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 3000);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("127.0.0.1:3000"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithIPv6AndPort() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("2001:db8::1"), 8080);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("[2001:db8:0:0:0:0:0:1]:8080"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithIPv6Localhost() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("::1"), 9000);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("[0:0:0:0:0:0:0:1]:9000"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithZeroPort() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 0);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("192.168.1.1:0"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithMaxPort() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 65535);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("192.168.1.1:65535"), result.resultOrThrow());
	}

	@Test
	void encodeKeyNullChecks() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}

	@Test
	void encodeKeyWithIPv4() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);

		Result<String> result = codec.encodeKey(address);
		assertTrue(result.isSuccess());
		assertEquals("192.168.1.1:8080", result.resultOrThrow());
	}

	@Test
	void encodeKeyWithIPv6() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("2001:db8::1"), 8080);

		Result<String> result = codec.encodeKey(address);
		assertTrue(result.isSuccess());
		assertEquals("[2001:db8:0:0:0:0:0:1]:8080", result.resultOrThrow());
	}

	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080")));
	}

	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as inet socket address"));
	}

	@Test
	void decodeStartWithValidIPv4() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080"));
		assertTrue(result.isSuccess());
		assertEquals(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080), result.resultOrThrow());
	}

	@Test
	void decodeStartWithLocalhost() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("127.0.0.1:3000"));
		assertTrue(result.isSuccess());
		assertEquals(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 3000), result.resultOrThrow());
	}

	@Test
	void decodeStartWithValidIPv6() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("[2001:db8::1]:8080"));
		assertTrue(result.isSuccess());
		assertEquals(new InetSocketAddress(InetAddress.getByName("2001:db8::1"), 8080), result.resultOrThrow());
	}

	@Test
	void decodeStartWithIPv6Localhost() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("[::1]:9000"));
		assertTrue(result.isSuccess());
		assertEquals(new InetSocketAddress(InetAddress.getByName("::1"), 9000), result.resultOrThrow());
	}

	@Test
	void decodeStartWithZeroPort() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:0"));
		assertTrue(result.isSuccess());
		assertEquals(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 0), result.resultOrThrow());
	}

	@Test
	void decodeStartWithMaxPort() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:65535"));
		assertTrue(result.isSuccess());
		assertEquals(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 65535), result.resultOrThrow());
	}

	@Test
	void decodeStartWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Invalid socket address format"));
	}

	@Test
	void decodeStartWithInvalidIPv6Format() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("[2001:db8::1"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Invalid IPv6 socket address format"));
	}

	@Test
	void decodeStartWithInvalidPort() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:99999"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Port number out of range"));
	}

	@Test
	void decodeStartWithNegativePort() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:-1"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Port number out of range"));
	}

	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}

	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}

	@Test
	void decodeKeyWithValidIPv4() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeKey("192.168.1.1:8080");
		assertTrue(result.isSuccess());
		assertEquals(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080), result.resultOrThrow());
	}

	@Test
	void decodeKeyWithValidIPv6() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeKey("[2001:db8::1]:8080");
		assertTrue(result.isSuccess());
		assertEquals(new InetSocketAddress(InetAddress.getByName("2001:db8::1"), 8080), result.resultOrThrow());
	}

	@Test
	void decodeKeyWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeKey("192.168.1.1");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Invalid socket address format"));
	}

	@Test
	void decodeKeyWithInvalidPort() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec();

		Result<InetSocketAddress> result = codec.decodeKey("192.168.1.1:99999");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Port number out of range"));
	}

	@Test
	void toStringRepresentation() {
		InetSocketAddressCodec codec = new InetSocketAddressCodec();
		assertEquals("InetSocketAddressCodec", codec.toString());
	}
}
