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
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.io.network.address.IpNetwork;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpNetworkCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedIpNetworkCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), expected);
		assertEquals(new JsonPrimitive("192.168.1.0/24"), result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		
		String result = codec.encodeKey(expected);
		assertEquals("192.168.1.0/24", result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		
		IpNetwork<?, ?> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/24"));
		assertEquals(expected, result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		
		IpNetwork<?, ?> result = codec.decodeKey("192.168.1.0/24");
		assertEquals(expected, result);
	}
	
	@Test
	void toStringWithConstraints() {
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		IpNetwork<?, ?> different = IpAddresses.parseNetwork("10.0.0.0/8");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), different));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		IpNetwork<?, ?> different = IpAddresses.parseNetwork("10.0.0.0/8");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(different));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.0/8")));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10.0.0.0/8"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpNetwork<?, ?> excluded = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		IpNetwork<?, ?> excluded = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(excluded));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpNetwork<?, ?> excluded = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/24")));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		IpNetwork<?, ?> excluded = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.0/24"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpNetwork<?, ?>> allowed = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.in(allowed);
		IpNetwork<?, ?> notAllowed = IpAddresses.parseNetwork("10.0.0.0/8");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notAllowed));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		List<IpNetwork<?, ?>> allowed = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.in(allowed);
		IpNetwork<?, ?> notAllowed = IpAddresses.parseNetwork("10.0.0.0/8");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(notAllowed));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpNetwork<?, ?>> allowed = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.0/8")));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		List<IpNetwork<?, ?>> allowed = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10.0.0.0/8"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpNetwork<?, ?>> excluded = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notIn(excluded);
		IpNetwork<?, ?> excludedValue = IpAddresses.parseNetwork("192.168.1.0/24");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excludedValue));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		List<IpNetwork<?, ?>> excluded = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notIn(excluded);
		IpNetwork<?, ?> excludedValue = IpAddresses.parseNetwork("192.168.1.0/24");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(excludedValue));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpNetwork<?, ?>> excluded = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/24")));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		List<IpNetwork<?, ?>> excluded = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.0/24"));
	}
	
	@Test
	void encodeIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		IpNetwork<?, ?> ipv6Network = IpAddresses.parseNetwork("2001:db8::/32");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), ipv6Network));
	}
	
	@Test
	void encodeKeyIpVersionConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		IpNetwork<?, ?> ipv6Network = IpAddresses.parseNetwork("2001:db8::/32");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(ipv6Network));
	}
	
	@Test
	void decodeIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2001:db8::/32")));
	}
	
	@Test
	void decodeKeyIpVersionConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2001:db8::/32"));
	}
	
	@Test
	void encodeIpVersionIPv6Success() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6));
		IpNetwork<?, ?> ipv6Network = IpAddresses.parseNetwork("2001:db8::/32");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), ipv6Network));
	}
	
	@Test
	void encodePrefixLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.prefixLength(builder -> builder.equalTo(24));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("10.0.0.0/8");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), network));
	}
	
	@Test
	void encodeKeyPrefixLengthConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.prefixLength(builder -> builder.equalTo(24));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("10.0.0.0/8");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(network));
	}
	
	@Test
	void decodePrefixLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.prefixLength(builder -> builder.equalTo(24));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.0/8")));
	}
	
	@Test
	void decodeKeyPrefixLengthConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.prefixLength(builder -> builder.equalTo(24));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10.0.0.0/8"));
	}
	
	@Test
	void encodePrefixLengthMinMaxConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.prefixLength(builder -> builder.lengthBetween(16, 24));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("10.0.0.0/8");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), network));
	}
	
	@Test
	void encodePrefixLengthSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.prefixLength(builder -> builder.equalTo(24));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("192.168.1.0/24");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), network));
	}
	
	@Test
	void encodeCanonicalSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.canonical();
		IpNetwork<?, ?> canonical = IpAddresses.parseNetwork("192.168.1.0/24");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), canonical));
	}
	
	@Test
	void encodeKeyCanonicalSuccess() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.canonical();
		IpNetwork<?, ?> canonical = IpAddresses.parseNetwork("10.0.0.0/8");
		
		assertDoesNotThrow(() -> codec.encodeKey(canonical));
	}
	
	@Test
	void decodeCanonicalSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.canonical();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/24")));
	}
	
	@Test
	void decodeKeyCanonicalSuccess() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.canonical();
		
		assertDoesNotThrow(() -> codec.decodeKey("172.16.0.0/12"));
	}
	
	@Test
	void encodeStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.string(builder -> builder.contains("10."));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("192.168.1.0/24");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), network));
	}
	
	@Test
	void encodeKeyStringConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.string(builder -> builder.contains("10."));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("192.168.1.0/24");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(network));
	}
	
	@Test
	void decodeStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.string(builder -> builder.contains("10."));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/24")));
	}
	
	@Test
	void decodeKeyStringConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.string(builder -> builder.contains("10."));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.0/24"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("192.168.1.0/24");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), network));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("192.168.1.0/24");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(network));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/24")));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("192.168.1.0/24"));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.custom(value -> {});
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("192.168.1.0/24");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), network));
	}
}
