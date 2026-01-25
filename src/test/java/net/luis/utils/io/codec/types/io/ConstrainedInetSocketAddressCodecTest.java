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
import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.config.io.*;
import net.luis.utils.io.codec.constraint.util.*;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
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
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetSocketAddress expected = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), expected);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("192.168.1.1:8080"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetSocketAddress expected = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080"));
		assertTrue(result.isSuccess());
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		InetSocketAddress expected = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withEqualTo(expected));
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetSocketAddress expected = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withEqualTo(expected));
		InetSocketAddress different = createSocketAddress("10.0.0.1", 9090);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), different);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetSocketAddress expected = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.1:9090"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetSocketAddress excluded = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetSocketAddress excluded = createSocketAddress("192.168.1.1", 8080);
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetSocketAddress> allowed = List.of(
			createSocketAddress("192.168.1.1", 8080),
			createSocketAddress("192.168.1.2", 8080)
		);
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withIn(allowed));
		InetSocketAddress notAllowed = createSocketAddress("10.0.0.1", 9090);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetSocketAddress> allowed = List.of(
			createSocketAddress("192.168.1.1", 8080),
			createSocketAddress("192.168.1.2", 8080)
		);
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withIn(allowed));
		
		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.1:9090"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetSocketAddress> excluded = List.of(
			createSocketAddress("192.168.1.1", 8080),
			createSocketAddress("192.168.1.2", 8080)
		);
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withNotIn(excluded));
		InetSocketAddress excludedValue = createSocketAddress("192.168.1.1", 8080);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excludedValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetSocketAddress> excluded = List.of(
			createSocketAddress("192.168.1.1", 8080),
			createSocketAddress("192.168.1.2", 8080)
		);
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withNotIn(excluded));
		
		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAddressIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withAddress(InetAddressConstraintConfig.UNCONSTRAINED.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4))));
		InetSocketAddress ipv6Address = createSocketAddress("::1", 8080);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), ipv6Address);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAddressIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withAddress(InetAddressConstraintConfig.UNCONSTRAINED.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4))));
		
		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("[::1]:8080"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAddressIpTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withAddress(InetAddressConstraintConfig.UNCONSTRAINED.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK))));
		InetSocketAddress publicAddress = createSocketAddress("8.8.8.8", 53);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), publicAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAddressIpTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withAddress(InetAddressConstraintConfig.UNCONSTRAINED.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK))));
		
		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("8.8.8.8:53"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAddressLoopbackSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withAddress(InetAddressConstraintConfig.UNCONSTRAINED.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK))));
		InetSocketAddress loopbackAddress = createSocketAddress("127.0.0.1", 8080);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), loopbackAddress);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPortConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withPort(PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080)));
		InetSocketAddress wrongPort = createSocketAddress("192.168.1.1", 9090);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), wrongPort);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPortConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withPort(PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080)));
		
		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:9090"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPortRangeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withPort(PortConstraintConfig.UNCONSTRAINED.withInRange(1024, 65535)));
		InetSocketAddress systemPort = createSocketAddress("192.168.1.1", 80);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), systemPort);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPortRangeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withPort(PortConstraintConfig.UNCONSTRAINED.withInRange(1024, 65535)));
		
		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:80"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPortTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withPort(PortConstraintConfig.UNCONSTRAINED.withType(EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.DYNAMIC))));
		InetSocketAddress registeredPort = createSocketAddress("192.168.1.1", 8080);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), registeredPort);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPortTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withPort(PortConstraintConfig.UNCONSTRAINED.withType(EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.DYNAMIC))));
		
		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPortSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withPort(PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080)));
		InetSocketAddress correctPort = createSocketAddress("192.168.1.1", 8080);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), correctPort);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartResolvedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(InetSocketAddressConstraintConfig::withResolved);
		InetSocketAddress unresolved = InetSocketAddress.createUnresolved("invalid.host.test", 8080);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), unresolved);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartResolvedSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(InetSocketAddressConstraintConfig::withResolved);
		InetSocketAddress resolved = createSocketAddress("192.168.1.1", 8080);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), resolved);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartUnresolvedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(InetSocketAddressConstraintConfig::withUnresolved);
		InetSocketAddress resolved = createSocketAddress("192.168.1.1", 8080);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), resolved);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartUnresolvedSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(InetSocketAddressConstraintConfig::withUnresolved);
		InetSocketAddress unresolved = InetSocketAddress.createUnresolved("invalid.host.test", 8080);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, typeProvider.empty(), unresolved));
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		InetSocketAddress address = createSocketAddress("192.168.1.1", 8080);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		
		Result<InetSocketAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1:8080"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetSocketAddress> codec = new InetSocketAddressCodec().apply(config -> config.withCustom(value -> Result.success()));
		InetSocketAddress address = createSocketAddress("192.168.1.1", 8080);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
	}
}
