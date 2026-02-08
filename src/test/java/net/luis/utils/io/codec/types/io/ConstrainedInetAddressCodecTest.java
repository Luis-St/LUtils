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
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.util.IpAddressType;
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InetAddressCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedInetAddressCodecTest {
	
	private static @NonNull InetAddress getAddress(@NonNull String host) {
		try {
			return InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetAddress expected = getAddress("192.168.1.1");
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.equalTo(expected);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), expected);
		assertEquals(new JsonPrimitive("192.168.1.1"), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetAddress expected = getAddress("192.168.1.1");
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.equalTo(expected);
		
		InetAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertEquals(expected, result);
	}
	
	@Test
	void toStringWithConstraints() {
		InetAddress expected = getAddress("192.168.1.1");
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.equalTo(expected);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetAddress expected = getAddress("192.168.1.1");
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.equalTo(expected);
		InetAddress different = getAddress("10.0.0.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), different));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetAddress expected = getAddress("192.168.1.1");
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.1")));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetAddress excluded = getAddress("192.168.1.1");
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetAddress excluded = getAddress("192.168.1.1");
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetAddress> allowed = List.of(getAddress("192.168.1.1"), getAddress("192.168.1.2"));
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.in(allowed);
		InetAddress notAllowed = getAddress("10.0.0.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notAllowed));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetAddress> allowed = List.of(getAddress("192.168.1.1"), getAddress("192.168.1.2"));
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.1")));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetAddress> excluded = List.of(getAddress("192.168.1.1"), getAddress("192.168.1.2"));
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.notIn(excluded);
		InetAddress excludedValue = getAddress("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excludedValue));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetAddress> excluded = List.of(getAddress("192.168.1.1"), getAddress("192.168.1.2"));
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void encodeIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		InetAddress ipv6Address = getAddress("::1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), ipv6Address));
	}
	
	@Test
	void decodeIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("::1")));
	}
	
	@Test
	void encodeIpVersionIPv6Success() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6));
		InetAddress ipv6Address = getAddress("2001:db8::1");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), ipv6Address));
	}
	
	@Test
	void encodeIpTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK));
		InetAddress privateAddress = getAddress("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), privateAddress));
	}
	
	@Test
	void decodeIpTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void encodeIpTypeLoopbackSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK));
		InetAddress loopbackAddress = getAddress("127.0.0.1");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), loopbackAddress));
	}
	
	@Test
	void encodeIpTypePrivateSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE));
		InetAddress privateAddress = getAddress("192.168.1.1");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), privateAddress));
	}
	
	@Test
	void encodeInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.inAnySubnet(Set.of("10.0.0.0/8"));
		InetAddress outsideSubnet = getAddress("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), outsideSubnet));
	}
	
	@Test
	void decodeInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.inAnySubnet(Set.of("10.0.0.0/8"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void encodeInAnySubnetSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.inAnySubnet(Set.of("10.0.0.0/8"));
		InetAddress inSubnet = getAddress("10.1.2.3");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), inSubnet));
	}
	
	@Test
	void encodeNotInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.notInAnySubnet(Set.of("10.0.0.0/8"));
		InetAddress inSubnet = getAddress("10.1.2.3");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), inSubnet));
	}
	
	@Test
	void decodeNotInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.notInAnySubnet(Set.of("10.0.0.0/8"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10.1.2.3")));
	}
	
	@Test
	void encodeNotInAnySubnetSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.notInAnySubnet(Set.of("10.0.0.0/8"));
		InetAddress outsideSubnet = getAddress("192.168.1.1");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), outsideSubnet));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		InetAddress address = getAddress("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), address));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = Codecs.INET_ADDRESS.custom(value -> {});
		InetAddress address = getAddress("192.168.1.1");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), address));
	}
}
