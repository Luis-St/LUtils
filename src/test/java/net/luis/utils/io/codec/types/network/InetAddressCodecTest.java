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
	void encodeStartNullChecks() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		InetAddress address = InetAddress.getByName("192.168.1.1");

		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), address));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, address));
	}

	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as inet address"));
	}

	@Test
	void encodeStartWithIPv4() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		InetAddress address = InetAddress.getByName("192.168.1.1");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("192.168.1.1"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithLocalhost() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		InetAddress address = InetAddress.getByName("127.0.0.1");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("127.0.0.1"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithIPv6() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		InetAddress address = InetAddress.getByName("2001:db8::1");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2001:db8:0:0:0:0:0:1"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithIPv6Localhost() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		InetAddress address = InetAddress.getByName("::1");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("0:0:0:0:0:0:0:1"), result.resultOrThrow());
	}

	@Test
	void encodeKeyNullChecks() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}

	@Test
	void encodeKeyWithIPv4() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();
		InetAddress address = InetAddress.getByName("192.168.1.1");

		Result<String> result = codec.encodeKey(address);
		assertTrue(result.isSuccess());
		assertEquals("192.168.1.1", result.resultOrThrow());
	}

	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}

	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as inet address"));
	}

	@Test
	void decodeStartWithValidIPv4() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isSuccess());
		assertEquals(InetAddress.getByName("192.168.1.1"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithLocalhost() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("127.0.0.1"));
		assertTrue(result.isSuccess());
		assertEquals(InetAddress.getByName("127.0.0.1"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithValidIPv6() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2001:db8::1"));
		assertTrue(result.isSuccess());
		assertEquals(InetAddress.getByName("2001:db8::1"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithIPv6Localhost() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("::1"));
		assertTrue(result.isSuccess());
		assertEquals(InetAddress.getByName("::1"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithInvalidAddress() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("999.999.999.999"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode inet address"));
	}

	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}

	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}

	@Test
	void decodeKeyWithValidIPv4() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		Result<InetAddress> result = codec.decodeKey("192.168.1.1");
		assertTrue(result.isSuccess());
		assertEquals(InetAddress.getByName("192.168.1.1"), result.resultOrThrow());
	}

	@Test
	void decodeKeyWithLocalhost() throws UnknownHostException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		Result<InetAddress> result = codec.decodeKey("127.0.0.1");
		assertTrue(result.isSuccess());
		assertEquals(InetAddress.getByName("127.0.0.1"), result.resultOrThrow());
	}

	@Test
	void decodeKeyWithInvalidAddress() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec();

		Result<InetAddress> result = codec.decodeKey("999.999.999.999");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key '999.999.999.999' as inet address"));
	}

	@Test
	void toStringRepresentation() {
		InetAddressCodec codec = new InetAddressCodec();
		assertEquals("InetAddressCodec", codec.toString());
	}
}
