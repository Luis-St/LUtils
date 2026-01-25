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
import net.luis.utils.io.codec.constraint.config.StringConstraintConfig;
import net.luis.utils.io.codec.constraint.config.io.MacAddressConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.io.network.address.mac.MacAddress;
import net.luis.utils.io.network.address.mac.MacAddresses;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MacAddressCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedMacAddressCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), expected);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("00:1a:2b:3c:4d:5e"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<String> result = codec.encodeKey(expected);
		assertTrue(result.isSuccess());
		assertEquals("00:1a:2b:3c:4d:5e", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e"));
		assertTrue(result.isSuccess());
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<MacAddress> result = codec.decodeKey("00:1a:2b:3c:4d:5e");
		assertTrue(result.isSuccess());
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withEqualTo(expected));
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withEqualTo(expected));
		MacAddress different = MacAddresses.parse("aa:bb:cc:dd:ee:ff");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), different);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withEqualTo(expected));
		MacAddress different = MacAddresses.parse("aa:bb:cc:dd:ee:ff");
		
		Result<String> result = codec.encodeKey(different);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("aa:bb:cc:dd:ee:ff"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withEqualTo(expected));
		
		Result<MacAddress> result = codec.decodeKey("aa:bb:cc:dd:ee:ff");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		MacAddress excluded = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		MacAddress excluded = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<String> result = codec.encodeKey(excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		MacAddress excluded = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		MacAddress excluded = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<MacAddress> result = codec.decodeKey("00:1a:2b:3c:4d:5e");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<MacAddress> allowed = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withIn(allowed));
		MacAddress notAllowed = MacAddresses.parse("aa:bb:cc:dd:ee:ff");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		List<MacAddress> allowed = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withIn(allowed));
		MacAddress notAllowed = MacAddresses.parse("aa:bb:cc:dd:ee:ff");
		
		Result<String> result = codec.encodeKey(notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<MacAddress> allowed = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withIn(allowed));
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("aa:bb:cc:dd:ee:ff"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		List<MacAddress> allowed = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withIn(allowed));
		
		Result<MacAddress> result = codec.decodeKey("aa:bb:cc:dd:ee:ff");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<MacAddress> excluded = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withNotIn(excluded));
		MacAddress excludedValue = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excludedValue);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		List<MacAddress> excluded = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withNotIn(excluded));
		MacAddress excludedValue = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<String> result = codec.encodeKey(excludedValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<MacAddress> excluded = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withNotIn(excluded));
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		List<MacAddress> excluded = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withNotIn(excluded));
		
		Result<MacAddress> result = codec.decodeKey("00:1a:2b:3c:4d:5e");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartUnicastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withUnicast);
		MacAddress multicastAddress = MacAddresses.parse("01:00:5e:00:00:01");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), multicastAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyUnicastConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withUnicast);
		MacAddress multicastAddress = MacAddresses.parse("01:00:5e:00:00:01");
		
		Result<String> result = codec.encodeKey(multicastAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartUnicastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withUnicast);
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("01:00:5e:00:00:01"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyUnicastConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withUnicast);
		
		Result<MacAddress> result = codec.decodeKey("01:00:5e:00:00:01");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartUnicastSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withUnicast);
		MacAddress unicastAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), unicastAddress);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMulticastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withMulticast);
		MacAddress unicastAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), unicastAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyMulticastConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withMulticast);
		MacAddress unicastAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<String> result = codec.encodeKey(unicastAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMulticastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withMulticast);
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyMulticastConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withMulticast);
		
		Result<MacAddress> result = codec.decodeKey("00:1a:2b:3c:4d:5e");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMulticastSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withMulticast);
		MacAddress multicastAddress = MacAddresses.parse("01:00:5e:00:00:01");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), multicastAddress);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartUniversalConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withUniversal);
		MacAddress localAddress = MacAddresses.parse("02:1a:2b:3c:4d:5e");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), localAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyUniversalConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withUniversal);
		MacAddress localAddress = MacAddresses.parse("02:1a:2b:3c:4d:5e");
		
		Result<String> result = codec.encodeKey(localAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartUniversalConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withUniversal);
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("02:1a:2b:3c:4d:5e"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyUniversalConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withUniversal);
		
		Result<MacAddress> result = codec.decodeKey("02:1a:2b:3c:4d:5e");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartUniversalSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withUniversal);
		MacAddress universalAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), universalAddress);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLocalConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withLocal);
		MacAddress universalAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), universalAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLocalConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withLocal);
		MacAddress universalAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<String> result = codec.encodeKey(universalAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLocalConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withLocal);
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLocalConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withLocal);
		
		Result<MacAddress> result = codec.decodeKey("00:1a:2b:3c:4d:5e");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLocalSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withLocal);
		MacAddress localAddress = MacAddresses.parse("02:1a:2b:3c:4d:5e");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), localAddress);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBroadcastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withBroadcast);
		MacAddress notBroadcast = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notBroadcast);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBroadcastConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withBroadcast);
		MacAddress notBroadcast = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<String> result = codec.encodeKey(notBroadcast);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBroadcastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withBroadcast);
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBroadcastConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withBroadcast);
		
		Result<MacAddress> result = codec.decodeKey("00:1a:2b:3c:4d:5e");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBroadcastSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withBroadcast);
		MacAddress broadcastAddress = MacAddresses.parse("ff:ff:ff:ff:ff:ff");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), broadcastAddress);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotBroadcastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withNotBroadcast);
		MacAddress broadcastAddress = MacAddresses.parse("ff:ff:ff:ff:ff:ff");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), broadcastAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotBroadcastConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withNotBroadcast);
		MacAddress broadcastAddress = MacAddresses.parse("ff:ff:ff:ff:ff:ff");
		
		Result<String> result = codec.encodeKey(broadcastAddress);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotBroadcastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withNotBroadcast);
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("ff:ff:ff:ff:ff:ff"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotBroadcastConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withNotBroadcast);
		
		Result<MacAddress> result = codec.decodeKey("ff:ff:ff:ff:ff:ff");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotBroadcastSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(MacAddressConstraintConfig::withNotBroadcast);
		MacAddress normalAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), normalAddress);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withStringConstraint(StringConstraintConfig.UNCONSTRAINED.withStartsWith("aa:")));
		MacAddress address = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyStringConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withStringConstraint(StringConstraintConfig.UNCONSTRAINED.withStartsWith("aa:")));
		MacAddress address = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<String> result = codec.encodeKey(address);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withStringConstraint(StringConstraintConfig.UNCONSTRAINED.withStartsWith("aa:")));
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyStringConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withStringConstraint(StringConstraintConfig.UNCONSTRAINED.withStartsWith("aa:")));
		
		Result<MacAddress> result = codec.decodeKey("00:1a:2b:3c:4d:5e");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		MacAddress address = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		MacAddress address = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<String> result = codec.encodeKey(address);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		
		Result<MacAddress> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withCustom(value -> Result.error("Custom validation failed")));
		
		Result<MacAddress> result = codec.decodeKey("00:1a:2b:3c:4d:5e");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = new MacAddressCodec().apply(config -> config.withCustom(value -> Result.success()));
		MacAddress address = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), address);
		assertTrue(result.isSuccess());
	}
}
