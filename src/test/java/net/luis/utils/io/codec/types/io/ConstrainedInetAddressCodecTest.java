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
import net.luis.utils.io.codec.constraint.util.IpAddressType;
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
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
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetAddress expected = getAddress("192.168.1.1");
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), expected);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("192.168.1.1"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetAddress expected = getAddress("192.168.1.1");
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isSuccess());
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		InetAddress expected = getAddress("192.168.1.1");
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withEqualTo(expected));
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetAddress expected = getAddress("192.168.1.1");
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withEqualTo(expected));
		InetAddress different = getAddress("10.0.0.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), different);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetAddress expected = getAddress("192.168.1.1");
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetAddress excluded = getAddress("192.168.1.1");
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		InetAddress excluded = getAddress("192.168.1.1");
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetAddress> allowed = List.of(getAddress("192.168.1.1"), getAddress("192.168.1.2"));
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withIn(allowed));
		InetAddress notAllowed = getAddress("10.0.0.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetAddress> allowed = List.of(getAddress("192.168.1.1"), getAddress("192.168.1.2"));
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withIn(allowed));
		
		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetAddress> excluded = List.of(getAddress("192.168.1.1"), getAddress("192.168.1.2"));
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withNotIn(excluded));
		InetAddress excludedValue = getAddress("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excludedValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<InetAddress> excluded = List.of(getAddress("192.168.1.1"), getAddress("192.168.1.2"));
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withNotIn(excluded));
		
		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4)));
		InetAddress ipv6Address = getAddress("::1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), ipv6Address);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4)));
		
		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("::1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartIpVersionIPv6Success() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6)));
		InetAddress ipv6Address = getAddress("2001:db8::1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), ipv6Address);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartIpTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK)));
		InetAddress privateAddress = getAddress("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), privateAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartIpTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK)));
		
		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartIpTypeLoopbackSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK)));
		InetAddress loopbackAddress = getAddress("127.0.0.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), loopbackAddress);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartIpTypePrivateSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE)));
		InetAddress privateAddress = getAddress("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), privateAddress);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withInAnySubnet(Set.of("10.0.0.0/8")));
		InetAddress outsideSubnet = getAddress("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), outsideSubnet);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withInAnySubnet(Set.of("10.0.0.0/8")));
		
		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInAnySubnetSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withInAnySubnet(Set.of("10.0.0.0/8")));
		InetAddress inSubnet = getAddress("10.1.2.3");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), inSubnet);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withNotInAnySubnet(Set.of("10.0.0.0/8")));
		InetAddress inSubnet = getAddress("10.1.2.3");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), inSubnet);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withNotInAnySubnet(Set.of("10.0.0.0/8")));
		
		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10.1.2.3"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInAnySubnetSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withNotInAnySubnet(Set.of("10.0.0.0/8")));
		InetAddress outsideSubnet = getAddress("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), outsideSubnet);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		InetAddress address = getAddress("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		
		Result<InetAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<InetAddress> codec = new InetAddressCodec().apply(config -> config.withCustom(value -> Result.success()));
		InetAddress address = getAddress("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
	}
}
