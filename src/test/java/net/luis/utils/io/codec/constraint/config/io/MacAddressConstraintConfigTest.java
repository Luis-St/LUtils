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

package net.luis.utils.io.codec.constraint.config.io;

import net.luis.utils.io.network.address.mac.MacAddress;
import net.luis.utils.io.network.address.mac.MacAddresses;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MacAddressConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class MacAddressConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> MacAddressConstraintConfig.UNCONSTRAINED);
		assertDoesNotThrow(() -> new MacAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new MacAddressConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new MacAddressConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new MacAddressConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new MacAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new MacAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new MacAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new MacAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new MacAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new MacAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new MacAddressConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorMutuallyExclusiveUnicastMulticast() {
		assertThrows(IllegalArgumentException.class, () -> new MacAddressConstraintConfig(
			Optional.empty(), Optional.empty(),
			Optional.of(net.luis.utils.io.codec.constraint.util.Unit.INSTANCE), // unicast
			Optional.of(net.luis.utils.io.codec.constraint.util.Unit.INSTANCE), // multicast (both present)
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorMutuallyExclusiveUniversalLocal() {
		assertThrows(IllegalArgumentException.class, () -> new MacAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.of(net.luis.utils.io.codec.constraint.util.Unit.INSTANCE), // universal
			Optional.of(net.luis.utils.io.codec.constraint.util.Unit.INSTANCE), // local (both present)
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.unicast().isEmpty());
		assertTrue(config.multicast().isEmpty());
		assertTrue(config.universal().isEmpty());
		assertTrue(config.local().isEmpty());
		assertTrue(config.broadcast().isEmpty());
		assertTrue(config.stringConstraint().isEmpty());
		assertTrue(config.custom().isEmpty());
	}
	
	@Test
	void withEqualTo() {
		MacAddress value = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withEqualTo(value);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> MacAddressConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		MacAddress value = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withNotEqualTo(value);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> MacAddressConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		MacAddress addr1 = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		MacAddress addr2 = MacAddresses.parse("00:1a:2b:3c:4d:5f");
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withIn(List.of(addr1, addr2));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> MacAddressConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		MacAddress addr = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withNotIn(List.of(addr));
		
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> MacAddressConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withUnicast() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withUnicast();
		
		assertTrue(config.unicast().isPresent());
		assertTrue(config.multicast().isEmpty());
	}
	
	@Test
	void withMulticast() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withMulticast();
		
		assertTrue(config.multicast().isPresent());
		assertTrue(config.unicast().isEmpty());
	}
	
	@Test
	void withUniversal() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withUniversal();
		
		assertTrue(config.universal().isPresent());
		assertTrue(config.local().isEmpty());
	}
	
	@Test
	void withLocal() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withLocal();
		
		assertTrue(config.local().isPresent());
		assertTrue(config.universal().isEmpty());
	}
	
	@Test
	void withBroadcast() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withBroadcast();
		
		assertTrue(config.broadcast().isPresent());
		assertFalse(config.broadcast().get().getSecond());
	}
	
	@Test
	void withNotBroadcast() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withNotBroadcast();
		
		assertTrue(config.broadcast().isPresent());
		assertTrue(config.broadcast().get().getSecond());
	}
	
	@Test
	void withCustom() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withCustom(value -> Result.success());
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> MacAddressConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesUnconstrained() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED;
		
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5e")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("ff:ff:ff:ff:ff:ff")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("01:00:5e:00:00:01")).isSuccess());
	}
	
	@Test
	void matchesWithNull() {
		assertThrows(NullPointerException.class, () -> MacAddressConstraintConfig.UNCONSTRAINED.matches(null));
	}
	
	@Test
	void matchesEqualTo() {
		MacAddress expected = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withEqualTo(expected);
		
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5e")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5f")).isError());
	}
	
	@Test
	void matchesNotEqualTo() {
		MacAddress excluded = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withNotEqualTo(excluded);
		
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5f")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5e")).isError());
	}
	
	@Test
	void matchesIn() {
		MacAddress addr1 = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		MacAddress addr2 = MacAddresses.parse("00:1a:2b:3c:4d:5f");
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withIn(List.of(addr1, addr2));
		
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5e")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5f")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:60")).isError());
	}
	
	@Test
	void matchesNotIn() {
		MacAddress excluded = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withNotIn(List.of(excluded));
		
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5f")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5e")).isError());
	}
	
	@Test
	void matchesUnicast() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withUnicast();
		
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5e")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("01:00:5e:00:00:01")).isError());
	}
	
	@Test
	void matchesMulticast() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withMulticast();
		
		assertTrue(config.matches(MacAddresses.parse("01:00:5e:00:00:01")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5e")).isError());
	}
	
	@Test
	void matchesUniversal() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withUniversal();
		
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5e")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("02:1a:2b:3c:4d:5e")).isError());
	}
	
	@Test
	void matchesLocal() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withLocal();
		
		assertTrue(config.matches(MacAddresses.parse("02:1a:2b:3c:4d:5e")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5e")).isError());
	}
	
	@Test
	void matchesBroadcast() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withBroadcast();
		
		assertTrue(config.matches(MacAddresses.parse("ff:ff:ff:ff:ff:ff")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5e")).isError());
	}
	
	@Test
	void matchesNotBroadcast() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withNotBroadcast();
		
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5e")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("ff:ff:ff:ff:ff:ff")).isError());
	}
	
	@Test
	void matchesCustom() {
		MacAddressConstraintConfig config = MacAddressConstraintConfig.UNCONSTRAINED.withCustom(
			value -> value.toColonString().startsWith("00:") ? Result.success() : Result.error("Address must start with 00:")
		);
		
		assertTrue(config.matches(MacAddresses.parse("00:1a:2b:3c:4d:5e")).isSuccess());
		assertTrue(config.matches(MacAddresses.parse("01:1a:2b:3c:4d:5e")).isError());
	}
}
