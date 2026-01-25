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
import net.luis.utils.io.codec.constraint.config.StringConstraintConfig;
import net.luis.utils.io.codec.constraint.util.IpAddressType;
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.util.result.Result;
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
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), expected);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("192.168.1.1"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<String> result = codec.encodeKey(expected);
		assertTrue(result.isSuccess());
		assertEquals("192.168.1.1", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isSuccess());
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<IpAddress<?>> result = codec.decodeKey("192.168.1.1");
		assertTrue(result.isSuccess());
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withEqualTo(expected));
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withEqualTo(expected));
		IpAddress<?> different = IpAddresses.parseIpv4("10.0.0.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), different);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withEqualTo(expected));
		IpAddress<?> different = IpAddresses.parseIpv4("10.0.0.1");
		
		Result<String> result = codec.encodeKey(different);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		IpAddress<?> expected = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<IpAddress<?>> result = codec.decodeKey("10.0.0.1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpAddress<?> excluded = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		IpAddress<?> excluded = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<String> result = codec.encodeKey(excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpAddress<?> excluded = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		IpAddress<?> excluded = IpAddresses.parseIpv4("192.168.1.1");
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<IpAddress<?>> result = codec.decodeKey("192.168.1.1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpAddress<?>> allowed = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIn(allowed));
		IpAddress<?> notAllowed = IpAddresses.parseIpv4("10.0.0.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		List<IpAddress<?>> allowed = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIn(allowed));
		IpAddress<?> notAllowed = IpAddresses.parseIpv4("10.0.0.1");
		
		Result<String> result = codec.encodeKey(notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpAddress<?>> allowed = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIn(allowed));
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		List<IpAddress<?>> allowed = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIn(allowed));
		
		Result<IpAddress<?>> result = codec.decodeKey("10.0.0.1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpAddress<?>> excluded = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withNotIn(excluded));
		IpAddress<?> excludedValue = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excludedValue);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		List<IpAddress<?>> excluded = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withNotIn(excluded));
		IpAddress<?> excludedValue = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<String> result = codec.encodeKey(excludedValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpAddress<?>> excluded = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withNotIn(excluded));
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		List<IpAddress<?>> excluded = List.of(IpAddresses.parseIpv4("192.168.1.1"), IpAddresses.parseIpv4("192.168.1.2"));
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withNotIn(excluded));
		
		Result<IpAddress<?>> result = codec.decodeKey("192.168.1.1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4)));
		IpAddress<?> ipv6Address = IpAddresses.parseIpv6("::1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), ipv6Address);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyIpVersionConstraintViolation() {
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4)));
		IpAddress<?> ipv6Address = IpAddresses.parseIpv6("::1");
		
		Result<String> result = codec.encodeKey(ipv6Address);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4)));
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("::1"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyIpVersionConstraintViolation() {
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4)));
		
		Result<IpAddress<?>> result = codec.decodeKey("::1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartIpVersionIPv6Success() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6)));
		IpAddress<?> ipv6Address = IpAddresses.parseIpv6("2001:db8::1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), ipv6Address);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartIpTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK)));
		IpAddress<?> privateAddress = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), privateAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyIpTypeConstraintViolation() {
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK)));
		IpAddress<?> privateAddress = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<String> result = codec.encodeKey(privateAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartIpTypeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK)));
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyIpTypeConstraintViolation() {
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK)));
		
		Result<IpAddress<?>> result = codec.decodeKey("192.168.1.1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartIpTypeLoopbackSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK)));
		IpAddress<?> loopbackAddress = IpAddresses.parseIpv4("127.0.0.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), loopbackAddress);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withInAnySubnet(Set.of("10.0.0.0/8")));
		IpAddress<?> outsideSubnet = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), outsideSubnet);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInAnySubnetConstraintViolation() {
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withInAnySubnet(Set.of("10.0.0.0/8")));
		IpAddress<?> outsideSubnet = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<String> result = codec.encodeKey(outsideSubnet);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withInAnySubnet(Set.of("10.0.0.0/8")));
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInAnySubnetConstraintViolation() {
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withInAnySubnet(Set.of("10.0.0.0/8")));
		
		Result<IpAddress<?>> result = codec.decodeKey("192.168.1.1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInAnySubnetSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withInAnySubnet(Set.of("10.0.0.0/8")));
		IpAddress<?> inSubnet = IpAddresses.parseIpv4("10.1.2.3");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), inSubnet);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withNotInAnySubnet(Set.of("10.0.0.0/8")));
		IpAddress<?> inSubnet = IpAddresses.parseIpv4("10.1.2.3");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), inSubnet);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInAnySubnetConstraintViolation() {
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withNotInAnySubnet(Set.of("10.0.0.0/8")));
		IpAddress<?> inSubnet = IpAddresses.parseIpv4("10.1.2.3");
		
		Result<String> result = codec.encodeKey(inSubnet);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInAnySubnetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withNotInAnySubnet(Set.of("10.0.0.0/8")));
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10.1.2.3"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInAnySubnetConstraintViolation() {
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withNotInAnySubnet(Set.of("10.0.0.0/8")));
		
		Result<IpAddress<?>> result = codec.decodeKey("10.1.2.3");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withStringConstraint(StringConstraintConfig.UNCONSTRAINED.withStartsWith("10.")));
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyStringConstraintViolation() {
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withStringConstraint(StringConstraintConfig.UNCONSTRAINED.withStartsWith("10.")));
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<String> result = codec.encodeKey(address);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withStringConstraint(StringConstraintConfig.UNCONSTRAINED.withStartsWith("10.")));
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyStringConstraintViolation() {
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withStringConstraint(StringConstraintConfig.UNCONSTRAINED.withStartsWith("10.")));
		
		Result<IpAddress<?>> result = codec.decodeKey("192.168.1.1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<String> result = codec.encodeKey(address);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		
		Result<IpAddress<?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		
		Result<IpAddress<?>> result = codec.decodeKey("192.168.1.1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpAddress<?>> codec = new IpAddressCodec().apply(config -> config.withCustom(value -> Result.success()));
		IpAddress<?> address = IpAddresses.parseIpv4("192.168.1.1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
	}
}
