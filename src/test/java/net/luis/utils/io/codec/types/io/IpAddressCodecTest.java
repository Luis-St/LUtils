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
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpAddressCodec}.<br>
 *
 * @author Luis-St
 */
class IpAddressCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), address));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, address));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as IP address"));
	}
	
	@Test
	void encodeStartWithIPv4() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("192.168.1.1"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithLocalhost() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		IpAddress<?> address = IpAddresses.parseIpv4("127.0.0.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("127.0.0.1"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithIPv6() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		IpAddress<?> address = IpAddresses.parseIpv6("2001:db8::1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2001:db8:0:0:0:0:0:1"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithIPv6Localhost() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		IpAddress<?> address = IpAddresses.parseIpv6("::1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("0:0:0:0:0:0:0:1"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithIPv4() {
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<String> result = codec.encodeKey(address);
		assertTrue(result.isSuccess());
		assertEquals("192.168.1.1", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as IP address"));
	}
	
	@Test
	void decodeStartWithValidIPv4() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isSuccess());
		assertEquals(IpAddresses.parseIpv4("192.168.1.1"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithLocalhost() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("127.0.0.1"));
		assertTrue(result.isSuccess());
		assertEquals(IpAddresses.parseIpv4("127.0.0.1"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidIPv6() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2001:db8::1"));
		assertTrue(result.isSuccess());
		assertEquals(IpAddresses.parseIpv6("2001:db8::1"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithIPv6Localhost() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("::1"));
		assertTrue(result.isSuccess());
		assertEquals(IpAddresses.parseIpv6("::1"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidAddress() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("999.999.999.999"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode IP address"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidIPv4() {
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		Result<IpAddress<?>> result = codec.decodeKey("192.168.1.1");
		assertTrue(result.isSuccess());
		assertEquals(IpAddresses.parseIpv4("192.168.1.1"), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithLocalhost() {
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		Result<IpAddress<?>> result = codec.decodeKey("127.0.0.1");
		assertTrue(result.isSuccess());
		assertEquals(IpAddresses.parseIpv4("127.0.0.1"), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidAddress() {
		Codec<IpAddress<?>> codec = new IpAddressCodec();
		
		Result<IpAddress<?>> result = codec.decodeKey("999.999.999.999");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key '999.999.999.999' as IP address"));
	}
	
	@Test
	void toStringRepresentation() {
		IpAddressCodec codec = new IpAddressCodec();
		assertEquals("IpAddressCodec", codec.toString());
	}
}
