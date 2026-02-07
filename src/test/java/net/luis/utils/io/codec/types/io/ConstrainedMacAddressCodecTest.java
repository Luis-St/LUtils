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
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.io.network.address.mac.MacAddress;
import net.luis.utils.io.network.address.mac.MacAddresses;
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
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.equalTo(expected);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), expected);
		assertEquals(new JsonPrimitive("00:1a:2b:3c:4d:5e"), result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.equalTo(expected);
		
		String result = codec.encodeKey(expected);
		assertEquals("00:1a:2b:3c:4d:5e", result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.equalTo(expected);
		
		MacAddress result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e"));
		assertEquals(expected, result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.equalTo(expected);
		
		MacAddress result = codec.decodeKey("00:1a:2b:3c:4d:5e");
		assertEquals(expected, result);
	}
	
	@Test
	void toStringWithConstraints() {
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.equalTo(expected);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.equalTo(expected);
		MacAddress different = MacAddresses.parse("aa:bb:cc:dd:ee:ff");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), different));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.equalTo(expected);
		MacAddress different = MacAddresses.parse("aa:bb:cc:dd:ee:ff");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(different));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("aa:bb:cc:dd:ee:ff")));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("aa:bb:cc:dd:ee:ff"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		MacAddress excluded = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		MacAddress excluded = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(excluded));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		MacAddress excluded = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e")));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		MacAddress excluded = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("00:1a:2b:3c:4d:5e"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<MacAddress> allowed = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.in(allowed);
		MacAddress notAllowed = MacAddresses.parse("aa:bb:cc:dd:ee:ff");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notAllowed));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		List<MacAddress> allowed = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.in(allowed);
		MacAddress notAllowed = MacAddresses.parse("aa:bb:cc:dd:ee:ff");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(notAllowed));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<MacAddress> allowed = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("aa:bb:cc:dd:ee:ff")));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		List<MacAddress> allowed = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("aa:bb:cc:dd:ee:ff"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<MacAddress> excluded = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notIn(excluded);
		MacAddress excludedValue = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excludedValue));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		List<MacAddress> excluded = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notIn(excluded);
		MacAddress excludedValue = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(excludedValue));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<MacAddress> excluded = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e")));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		List<MacAddress> excluded = List.of(MacAddresses.parse("00:1a:2b:3c:4d:5e"), MacAddresses.parse("00:1a:2b:3c:4d:5f"));
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("00:1a:2b:3c:4d:5e"));
	}
	
	@Test
	void encodeUnicastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.unicast();
		MacAddress multicastAddress = MacAddresses.parse("01:00:5e:00:00:01");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), multicastAddress));
	}
	
	@Test
	void encodeKeyUnicastConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.unicast();
		MacAddress multicastAddress = MacAddresses.parse("01:00:5e:00:00:01");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(multicastAddress));
	}
	
	@Test
	void decodeUnicastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.unicast();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("01:00:5e:00:00:01")));
	}
	
	@Test
	void decodeKeyUnicastConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.unicast();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("01:00:5e:00:00:01"));
	}
	
	@Test
	void encodeUnicastSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.unicast();
		MacAddress unicastAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), unicastAddress));
	}
	
	@Test
	void encodeMulticastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.multicast();
		MacAddress unicastAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), unicastAddress));
	}
	
	@Test
	void encodeKeyMulticastConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.multicast();
		MacAddress unicastAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(unicastAddress));
	}
	
	@Test
	void decodeMulticastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.multicast();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e")));
	}
	
	@Test
	void decodeKeyMulticastConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.multicast();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("00:1a:2b:3c:4d:5e"));
	}
	
	@Test
	void encodeMulticastSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.multicast();
		MacAddress multicastAddress = MacAddresses.parse("01:00:5e:00:00:01");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), multicastAddress));
	}
	
	@Test
	void encodeUniversalConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.universal();
		MacAddress localAddress = MacAddresses.parse("02:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), localAddress));
	}
	
	@Test
	void encodeKeyUniversalConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.universal();
		MacAddress localAddress = MacAddresses.parse("02:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(localAddress));
	}
	
	@Test
	void decodeUniversalConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.universal();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("02:1a:2b:3c:4d:5e")));
	}
	
	@Test
	void decodeKeyUniversalConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.universal();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("02:1a:2b:3c:4d:5e"));
	}
	
	@Test
	void encodeUniversalSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.universal();
		MacAddress universalAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), universalAddress));
	}
	
	@Test
	void encodeLocalConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.local();
		MacAddress universalAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), universalAddress));
	}
	
	@Test
	void encodeKeyLocalConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.local();
		MacAddress universalAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(universalAddress));
	}
	
	@Test
	void decodeLocalConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.local();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e")));
	}
	
	@Test
	void decodeKeyLocalConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.local();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("00:1a:2b:3c:4d:5e"));
	}
	
	@Test
	void encodeLocalSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.local();
		MacAddress localAddress = MacAddresses.parse("02:1a:2b:3c:4d:5e");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), localAddress));
	}
	
	@Test
	void encodeBroadcastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.broadcast();
		MacAddress notBroadcast = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notBroadcast));
	}
	
	@Test
	void encodeKeyBroadcastConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.broadcast();
		MacAddress notBroadcast = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(notBroadcast));
	}
	
	@Test
	void decodeBroadcastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.broadcast();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e")));
	}
	
	@Test
	void decodeKeyBroadcastConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.broadcast();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("00:1a:2b:3c:4d:5e"));
	}
	
	@Test
	void encodeBroadcastSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.broadcast();
		MacAddress broadcastAddress = MacAddresses.parse("ff:ff:ff:ff:ff:ff");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), broadcastAddress));
	}
	
	@Test
	void encodeNotBroadcastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notBroadcast();
		MacAddress broadcastAddress = MacAddresses.parse("ff:ff:ff:ff:ff:ff");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), broadcastAddress));
	}
	
	@Test
	void encodeKeyNotBroadcastConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notBroadcast();
		MacAddress broadcastAddress = MacAddresses.parse("ff:ff:ff:ff:ff:ff");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(broadcastAddress));
	}
	
	@Test
	void decodeNotBroadcastConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notBroadcast();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("ff:ff:ff:ff:ff:ff")));
	}
	
	@Test
	void decodeKeyNotBroadcastConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notBroadcast();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("ff:ff:ff:ff:ff:ff"));
	}
	
	@Test
	void encodeNotBroadcastSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.notBroadcast();
		MacAddress normalAddress = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), normalAddress));
	}
	
	@Test
	void encodeStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.string(builder -> builder.startsWith("aa:"));
		MacAddress address = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), address));
	}
	
	@Test
	void encodeKeyStringConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.string(builder -> builder.startsWith("aa:"));
		MacAddress address = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(address));
	}
	
	@Test
	void decodeStringConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.string(builder -> builder.startsWith("aa:"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e")));
	}
	
	@Test
	void decodeKeyStringConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.string(builder -> builder.startsWith("aa:"));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("00:1a:2b:3c:4d:5e"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		MacAddress address = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), address));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		MacAddress address = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(address));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00:1a:2b:3c:4d:5e")));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("00:1a:2b:3c:4d:5e"));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<MacAddress> codec = Codecs.MAC_ADDRESS.custom(value -> {});
		MacAddress address = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), address));
	}
}
