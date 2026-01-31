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
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.io.network.address.IpNetwork;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpNetworkCodec}.<br>
 *
 * @author Luis-St
 */
class IpNetworkCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		IpNetwork<?, ?> network = IpAddresses.parseIpv4Network("192.168.0.0/24");
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), network));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, network));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as IP network"));
	}
	
	@Test
	void encodeStartWithIPv4Network() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		IpNetwork<?, ?> network = IpAddresses.parseIpv4Network("192.168.0.0/24");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), network);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("192.168.0.0/24"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithIPv4PrivateClassA() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		IpNetwork<?, ?> network = IpAddresses.parseIpv4Network("10.0.0.0/8");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), network);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("10.0.0.0/8"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithIPv6Network() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		IpNetwork<?, ?> network = IpAddresses.parseIpv6Network("2001:db8::/32");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), network);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2001:db8::/32"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithIPv6LinkLocal() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		IpNetwork<?, ?> network = IpAddresses.parseIpv6Network("fe80::/10");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), network);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("fe80::/10"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithIPv4Network() {
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		IpNetwork<?, ?> network = IpAddresses.parseIpv4Network("192.168.0.0/24");
		
		Result<String> result = codec.encodeKey(network);
		assertTrue(result.isSuccess());
		assertEquals("192.168.0.0/24", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("192.168.0.0/24")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as IP network"));
	}
	
	@Test
	void decodeStartWithValidIPv4Network() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.0.0/24"));
		assertTrue(result.isSuccess());
		assertEquals(IpAddresses.parseIpv4Network("192.168.0.0/24"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithIPv4PrivateClassC() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.0.0/16"));
		assertTrue(result.isSuccess());
		assertEquals(IpAddresses.parseIpv4Network("192.168.0.0/16"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidIPv6Network() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2001:db8::/32"));
		assertTrue(result.isSuccess());
		assertEquals(IpAddresses.parseIpv6Network("2001:db8::/32"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithIPv6Documentation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2001:db8::/32"));
		assertTrue(result.isSuccess());
		assertEquals(IpAddresses.IPV6_DOCUMENTATION_NETWORK, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidCidr() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode IP network"));
	}
	
	@Test
	void decodeStartWithInvalidPrefix() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/99"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode IP network"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidIPv4Network() {
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		Result<IpNetwork<?, ?>> result = codec.decodeKey("192.168.0.0/24");
		assertTrue(result.isSuccess());
		assertEquals(IpAddresses.parseIpv4Network("192.168.0.0/24"), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidCidr() {
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		Result<IpNetwork<?, ?>> result = codec.decodeKey("192.168.1.0");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key '192.168.1.0' as IP network"));
	}
	
	@Test
	void toStringRepresentation() {
		IpNetworkCodec codec = new IpNetworkCodec();
		assertEquals("IpNetworkCodec", codec.toString());
	}
}
