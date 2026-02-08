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
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.IpAddresses;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpAddressCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedIpAddressCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.equalTo(expected);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), expected);
		assertEquals(new JsonPrimitive("192.168.1.1"), result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.equalTo(expected);
		
		String result = codec.encodeKey(expected);
		assertEquals("192.168.1.1", result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.equalTo(expected);
		
		IpAddress<?> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertEquals(expected, result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.equalTo(expected);
		
		IpAddress<?> result = codec.decodeKey("192.168.1.1");
		assertEquals(expected, result);
	}
	
	@Test
	void toStringWithConstraints() {
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.equalTo(expected);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.equalTo(expected);
		IpAddress<?> different = IpAddresses.parseIpv4("10.0.0.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), different));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.equalTo(expected);
		IpAddress<?> different = IpAddresses.parseIpv4("10.0.0.1");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(different));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.1")));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10.0.0.1"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpAddress<?> excluded = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		IpAddress<?> excluded = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(excluded));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpAddress<?> excluded = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		IpAddress<?> excluded = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.1"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpAddress<?>> allowed = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.in(allowed);
		IpAddress<?> notAllowed = IpAddresses.parseIpv4("10.0.0.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notAllowed));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		List<IpAddress<?>> allowed = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.in(allowed);
		IpAddress<?> notAllowed = IpAddresses.parseIpv4("10.0.0.1");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(notAllowed));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpAddress<?>> allowed = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.1")));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		List<IpAddress<?>> allowed = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10.0.0.1"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpAddress<?>> excluded = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.notIn(excluded);
		IpAddress<?> excludedValue = IpAddresses.parseIpv4("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excludedValue));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		List<IpAddress<?>> excluded = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.notIn(excluded);
		IpAddress<?> excludedValue = IpAddresses.parseIpv4("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(excludedValue));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpAddress<?>> excluded = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		List<IpAddress<?>> excluded = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.1"));
	}
	
	@Test
	void encodeIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		IpAddress<?> ipv6Address = IpAddresses.parseIpv6("::1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), ipv6Address));
	}
	
	@Test
	void encodeKeyIpVersionConstraintViolation() {
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		IpAddress<?> ipv6Address = IpAddresses.parseIpv6("::1");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(ipv6Address));
	}
	
	@Test
	void decodeIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("::1")));
	}
	
	@Test
	void decodeKeyIpVersionConstraintViolation() {
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("::1"));
	}
	
	@Test
	void encodeIpVersionIPv6Success() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6));
		IpAddress<?> ipv6Address = IpAddresses.parseIpv6("2001:db8::1");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), ipv6Address));
	}
	
	@Test
	void encodeIpTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK));
		IpAddress<?> privateAddress = IpAddresses.parseIpv4("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), privateAddress));
	}
	
	@Test
	void encodeKeyIpTypeConstraintViolation() {
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK));
		IpAddress<?> privateAddress = IpAddresses.parseIpv4("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(privateAddress));
	}
	
	@Test
	void decodeIpTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void decodeKeyIpTypeConstraintViolation() {
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.1"));
	}
	
	@Test
	void encodeIpTypeLoopbackSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.ipType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK));
		IpAddress<?> loopbackAddress = IpAddresses.parseIpv4("127.0.0.1");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), loopbackAddress));
	}
	
	@Test
	void encodeInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.inAnySubnet(Set.of("10.0.0.0/8"));
		IpAddress<?> outsideSubnet = IpAddresses.parseIpv4("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), outsideSubnet));
	}
	
	@Test
	void encodeKeyInAnySubnetConstraintViolation() {
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.inAnySubnet(Set.of("10.0.0.0/8"));
		IpAddress<?> outsideSubnet = IpAddresses.parseIpv4("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(outsideSubnet));
	}
	
	@Test
	void decodeInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.inAnySubnet(Set.of("10.0.0.0/8"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void decodeKeyInAnySubnetConstraintViolation() {
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.inAnySubnet(Set.of("10.0.0.0/8"));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.1"));
	}
	
	@Test
	void encodeInAnySubnetSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.inAnySubnet(Set.of("10.0.0.0/8"));
		IpAddress<?> inSubnet = IpAddresses.parseIpv4("10.1.2.3");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), inSubnet));
	}
	
	@Test
	void encodeNotInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.notInAnySubnet(Set.of("10.0.0.0/8"));
		IpAddress<?> inSubnet = IpAddresses.parseIpv4("10.1.2.3");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), inSubnet));
	}
	
	@Test
	void encodeKeyNotInAnySubnetConstraintViolation() {
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.notInAnySubnet(Set.of("10.0.0.0/8"));
		IpAddress<?> inSubnet = IpAddresses.parseIpv4("10.1.2.3");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(inSubnet));
	}
	
	@Test
	void decodeNotInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.notInAnySubnet(Set.of("10.0.0.0/8"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10.1.2.3")));
	}
	
	@Test
	void decodeKeyNotInAnySubnetConstraintViolation() {
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.notInAnySubnet(Set.of("10.0.0.0/8"));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10.1.2.3"));
	}
	
	@Test
	void encodeStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.string(builder -> builder.startsWith("10."));
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), address));
	}
	
	@Test
	void encodeKeyStringConstraintViolation() {
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.string(builder -> builder.startsWith("10."));
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(address));
	}
	
	@Test
	void decodeStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.string(builder -> builder.startsWith("10."));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void decodeKeyStringConstraintViolation() {
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.string(builder -> builder.startsWith("10."));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.1"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), address));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(address));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1")));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.1"));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = Codecs.IP_ADDRESS.custom(value -> {});
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), address));
	}
}
