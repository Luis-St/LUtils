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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.io.network.address.IpNetwork;
import net.luis.utils.util.result.Result;
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
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), expected);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("192.168.1.0/24"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		
		Result<String> result = codec.encodeKey(expected);
		assertTrue(result.isSuccess());
		assertEquals("192.168.1.0/24", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/24"));
		assertTrue(result.isSuccess());
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		
		Result<IpNetwork<?, ?>> result = codec.decodeKey("192.168.1.0/24");
		assertTrue(result.isSuccess());
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		IpNetwork<?, ?> different = IpAddresses.parseNetwork("10.0.0.0/8");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), different);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		IpNetwork<?, ?> different = IpAddresses.parseNetwork("10.0.0.0/8");
		
		Result<String> result = codec.encodeKey(different);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.0/8"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.equalTo(expected);
		
		Result<IpNetwork<?, ?>> result = codec.decodeKey("10.0.0.0/8");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpNetwork<?, ?> excluded = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notEqualTo(excluded);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		IpNetwork<?, ?> excluded = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notEqualTo(excluded);
		
		Result<String> result = codec.encodeKey(excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		IpNetwork<?, ?> excluded = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notEqualTo(excluded);
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/24"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		IpNetwork<?, ?> excluded = IpAddresses.parseNetwork("192.168.1.0/24");
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notEqualTo(excluded);
		
		Result<IpNetwork<?, ?>> result = codec.decodeKey("192.168.1.0/24");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpNetwork<?, ?>> allowed = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.in(allowed);
		IpNetwork<?, ?> notAllowed = IpAddresses.parseNetwork("10.0.0.0/8");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		List<IpNetwork<?, ?>> allowed = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.in(allowed);
		IpNetwork<?, ?> notAllowed = IpAddresses.parseNetwork("10.0.0.0/8");
		
		Result<String> result = codec.encodeKey(notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpNetwork<?, ?>> allowed = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.in(allowed);
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.0/8"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		List<IpNetwork<?, ?>> allowed = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.in(allowed);
		
		Result<IpNetwork<?, ?>> result = codec.decodeKey("10.0.0.0/8");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpNetwork<?, ?>> excluded = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notIn(excluded);
		IpNetwork<?, ?> excludedValue = IpAddresses.parseNetwork("192.168.1.0/24");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excludedValue);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		List<IpNetwork<?, ?>> excluded = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notIn(excluded);
		IpNetwork<?, ?> excludedValue = IpAddresses.parseNetwork("192.168.1.0/24");
		
		Result<String> result = codec.encodeKey(excludedValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<IpNetwork<?, ?>> excluded = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notIn(excluded);
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/24"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		List<IpNetwork<?, ?>> excluded = List.of(IpAddresses.parseNetwork("192.168.1.0/24"), IpAddresses.parseNetwork("192.168.2.0/24"));
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.notIn(excluded);
		
		Result<IpNetwork<?, ?>> result = codec.decodeKey("192.168.1.0/24");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		IpNetwork<?, ?> ipv6Network = IpAddresses.parseNetwork("2001:db8::/32");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), ipv6Network);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyIpVersionConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		IpNetwork<?, ?> ipv6Network = IpAddresses.parseNetwork("2001:db8::/32");
		
		Result<String> result = codec.encodeKey(ipv6Network);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartIpVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2001:db8::/32"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyIpVersionConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		
		Result<IpNetwork<?, ?>> result = codec.decodeKey("2001:db8::/32");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartIpVersionIPv6Success() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.ipVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6));
		IpNetwork<?, ?> ipv6Network = IpAddresses.parseNetwork("2001:db8::/32");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), ipv6Network);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPrefixLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.prefixLength(builder -> builder.equalTo(24));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("10.0.0.0/8");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), network);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPrefixLengthConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.prefixLength(builder -> builder.equalTo(24));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("10.0.0.0/8");
		
		Result<String> result = codec.encodeKey(network);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPrefixLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.prefixLength(builder -> builder.equalTo(24));
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10.0.0.0/8"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPrefixLengthConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.prefixLength(builder -> builder.equalTo(24));
		
		Result<IpNetwork<?, ?>> result = codec.decodeKey("10.0.0.0/8");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPrefixLengthMinMaxConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.prefixLength(builder -> builder.lengthBetween(16, 24));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("10.0.0.0/8");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), network);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPrefixLengthSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.prefixLength(builder -> builder.equalTo(24));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("192.168.1.0/24");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), network);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCanonicalSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.canonical();
		IpNetwork<?, ?> canonical = IpAddresses.parseNetwork("192.168.1.0/24");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), canonical);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCanonicalSuccess() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.canonical();
		IpNetwork<?, ?> canonical = IpAddresses.parseNetwork("10.0.0.0/8");
		
		Result<String> result = codec.encodeKey(canonical);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCanonicalSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.canonical();
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/24"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCanonicalSuccess() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.canonical();
		
		Result<IpNetwork<?, ?>> result = codec.decodeKey("172.16.0.0/12");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.string(builder -> builder.contains("10."));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("192.168.1.0/24");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), network);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyStringConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.string(builder -> builder.contains("10."));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("192.168.1.0/24");
		
		Result<String> result = codec.encodeKey(network);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.string(builder -> builder.contains("10."));
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/24"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyStringConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.string(builder -> builder.contains("10."));
		
		Result<IpNetwork<?, ?>> result = codec.decodeKey("192.168.1.0/24");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.custom(value -> Result.error("Custom validation failed"));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("192.168.1.0/24");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), network);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.custom(value -> Result.error("Custom validation failed"));
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("192.168.1.0/24");
		
		Result<String> result = codec.encodeKey(network);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.custom(value -> Result.error("Custom validation failed"));
		
		Result<IpNetwork<?, ?>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("192.168.1.0/24"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.custom(value -> Result.error("Custom validation failed"));
		
		Result<IpNetwork<?, ?>> result = codec.decodeKey("192.168.1.0/24");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IpNetwork<?, ?>> codec = Codecs.IP_NETWORK.custom(value -> Result.success());
		IpNetwork<?, ?> network = IpAddresses.parseNetwork("192.168.1.0/24");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), network);
		assertTrue(result.isSuccess());
	}
}
