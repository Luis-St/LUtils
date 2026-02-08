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
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.io.network.address.IpNetwork;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpNetworkCodec}.<br>
 *
 * @author Luis-St
 */
class IpNetworkCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		IpNetwork<?, ?> network = IpAddresses.parseIpv4Network("192.168.0.0/24");
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), network));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, network));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as ip network"));
	}
	
	@Test
	void encodeWithIPv4Network() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		IpNetwork<?, ?> network = IpAddresses.parseIpv4Network("192.168.0.0/24");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), network);
		assertEquals(new JsonPrimitive("192.168.0.0/24"), result);
	}
	
	@Test
	void encodeWithIPv4PrivateClassA() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		IpNetwork<?, ?> network = IpAddresses.parseIpv4Network("10.0.0.0/8");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), network);
		assertEquals(new JsonPrimitive("10.0.0.0/8"), result);
	}
	
	@Test
	void encodeWithIPv6Network() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		IpNetwork<?, ?> network = IpAddresses.parseIpv6Network("2001:db8::/32");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), network);
		assertEquals(new JsonPrimitive("2001:db8::/32"), result);
	}
	
	@Test
	void encodeWithIPv6LinkLocal() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		IpNetwork<?, ?> network = IpAddresses.parseIpv6Network("fe80::/10");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), network);
		assertEquals(new JsonPrimitive("fe80::/10"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithIPv4Network() throws EncoderException {
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		IpNetwork<?, ?> network = IpAddresses.parseIpv4Network("192.168.0.0/24");
		
		String result = codec.encodeKey(network);
		assertEquals("192.168.0.0/24", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("192.168.0.0/24")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as ip network"));
	}
	
	@Test
	void decodeWithValidIPv4Network() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		IpNetwork<?, ?> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.0.0/24"));
		assertEquals(IpAddresses.parseIpv4Network("192.168.0.0/24"), result);
	}
	
	@Test
	void decodeWithIPv4PrivateClassC() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		IpNetwork<?, ?> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.0.0/16"));
		assertEquals(IpAddresses.parseIpv4Network("192.168.0.0/16"), result);
	}
	
	@Test
	void decodeWithValidIPv6Network() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		IpNetwork<?, ?> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2001:db8::/32"));
		assertEquals(IpAddresses.parseIpv6Network("2001:db8::/32"), result);
	}
	
	@Test
	void decodeWithIPv6Documentation() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		IpNetwork<?, ?> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2001:db8::/32"));
		assertEquals(IpAddresses.IPV6_DOCUMENTATION_NETWORK, result);
	}
	
	@Test
	void decodeWithInvalidCidr() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0")));
		assertTrue(exception.getMessage().contains("Unable to decode ip network"));
	}
	
	@Test
	void decodeWithInvalidPrefix() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/99")));
		assertTrue(exception.getMessage().contains("Unable to decode ip network"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidIPv4Network() throws DecoderException {
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		IpNetwork<?, ?> result = codec.decodeKey("192.168.0.0/24");
		assertEquals(IpAddresses.parseIpv4Network("192.168.0.0/24"), result);
	}
	
	@Test
	void decodeKeyWithInvalidCidr() {
		Codec<IpNetwork<?, ?>> codec = new IpNetworkCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.0"));
		assertTrue(exception.getMessage().contains("Unable to decode key '192.168.1.0' as ip network"));
	}
	
	@Test
	void toStringRepresentation() {
		IpNetworkCodec codec = new IpNetworkCodec();
		assertEquals("IpNetworkCodec", codec.toString());
	}
}
