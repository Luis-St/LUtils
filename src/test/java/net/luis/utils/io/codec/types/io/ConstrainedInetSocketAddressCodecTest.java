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
import net.luis.utils.io.codec.constraint.config.io.InetAddressConstraintConfig;
import net.luis.utils.io.codec.constraint.config.io.PortConstraintConfig;
import net.luis.utils.io.codec.constraint.util.*;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.net.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InetSocketAddressCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedInetSocketAddressCodecTest {
	
	private static @NonNull InetSocketAddress createSocketAddress(@NonNull String host, int port) {
		try {
			return new InetSocketAddress(InetAddress.getByName(host), port);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetSocketAddress expected = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.equalTo(expected);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), expected);
		assertEquals(new JsonPrimitive("192.168.1.1:8080"), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetSocketAddress expected = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.equalTo(expected);
		
		InetSocketAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080"));
		assertEquals(expected, result);
	}
	
	@Test
	void toStringWithConstraints() {
		InetSocketAddress expected = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.equalTo(expected);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetSocketAddress expected = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.equalTo(expected);
		InetSocketAddress different = createSocketAddress("10.0.0.1", 9090);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), different));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetSocketAddress expected = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.1:9090")));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetSocketAddress excluded = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetSocketAddress excluded = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080")));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetSocketAddress> allowed = List.of(
			createSocketAddress("192.168.1.1", 8080),
			createSocketAddress("192.168.1.2", 8080)
		);
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.in(allowed);
		InetSocketAddress notAllowed = createSocketAddress("10.0.0.1", 9090);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notAllowed));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetSocketAddress> allowed = List.of(
			createSocketAddress("192.168.1.1", 8080),
			createSocketAddress("192.168.1.2", 8080)
		);
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.1:9090")));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetSocketAddress> excluded = List.of(
			createSocketAddress("192.168.1.1", 8080),
			createSocketAddress("192.168.1.2", 8080)
		);
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.notIn(excluded);
		InetSocketAddress excludedValue = createSocketAddress("192.168.1.1", 8080);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excludedValue));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetSocketAddress> excluded = List.of(
			createSocketAddress("192.168.1.1", 8080),
			createSocketAddress("192.168.1.2", 8080)
		);
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080")));
	}
	
	@Test
	void encodeAddressIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.address(InetAddressConstraintConfig.UNCONSTRAINED.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4)));
		InetSocketAddress ipv6Address = createSocketAddress("::1", 8080);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), ipv6Address));
	}
	
	@Test
	void decodeAddressIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.address(InetAddressConstraintConfig.UNCONSTRAINED.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4)));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("[::1]:8080")));
	}
	
	@Test
	void encodeAddressIpTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.address(InetAddressConstraintConfig.UNCONSTRAINED.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK)));
		InetSocketAddress publicAddress = createSocketAddress("8.8.8.8", 53);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), publicAddress));
	}
	
	@Test
	void decodeAddressIpTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.address(InetAddressConstraintConfig.UNCONSTRAINED.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK)));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("8.8.8.8:53")));
	}
	
	@Test
	void encodeAddressLoopbackSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.address(InetAddressConstraintConfig.UNCONSTRAINED.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK)));
		InetSocketAddress loopbackAddress = createSocketAddress("127.0.0.1", 8080);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), loopbackAddress));
	}
	
	@Test
	void encodePortConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.port(PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080));
		InetSocketAddress wrongPort = createSocketAddress("192.168.1.1", 9090);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), wrongPort));
	}
	
	@Test
	void decodePortConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.port(PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:9090")));
	}
	
	@Test
	void encodePortRangeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.port(PortConstraintConfig.UNCONSTRAINED.withInRange(1024, 65535));
		InetSocketAddress systemPort = createSocketAddress("192.168.1.1", 80);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), systemPort));
	}
	
	@Test
	void decodePortRangeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.port(PortConstraintConfig.UNCONSTRAINED.withInRange(1024, 65535));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:80")));
	}
	
	@Test
	void encodePortTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.port(PortConstraintConfig.UNCONSTRAINED.withType(EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.DYNAMIC)));
		InetSocketAddress registeredPort = createSocketAddress("192.168.1.1", 8080);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), registeredPort));
	}
	
	@Test
	void decodePortTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.port(PortConstraintConfig.UNCONSTRAINED.withType(EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.DYNAMIC)));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080")));
	}
	
	@Test
	void encodePortSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.port(PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080));
		InetSocketAddress correctPort = createSocketAddress("192.168.1.1", 8080);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), correctPort));
	}
	
	@Test
	void encodeResolvedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.resolved();
		InetSocketAddress unresolved = InetSocketAddress.createUnresolved("invalid.host.test", 8080);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), unresolved));
	}
	
	@Test
	void encodeResolvedSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.resolved();
		InetSocketAddress resolved = createSocketAddress("192.168.1.1", 8080);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), resolved));
	}
	
	@Test
	void encodeUnresolvedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.unresolved();
		InetSocketAddress resolved = createSocketAddress("192.168.1.1", 8080);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), resolved));
	}
	
	@Test
	void encodeUnresolvedSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.unresolved();
		InetSocketAddress unresolved = InetSocketAddress.createUnresolved("invalid.host.test", 8080);
		
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, typeProvider.empty(), unresolved));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		InetSocketAddress address = createSocketAddress("192.168.1.1", 8080);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), address));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080")));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = Codecs.INET_SOCKET_ADDRESS.custom(value -> {});
		InetSocketAddress address = createSocketAddress("192.168.1.1", 8080);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), address));
	}
}
