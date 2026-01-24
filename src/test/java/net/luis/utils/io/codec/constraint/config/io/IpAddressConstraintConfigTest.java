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

import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.util.IpAddressType;
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpAddressConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class IpAddressConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> IpAddressConstraintConfig.UNCONSTRAINED);
		assertDoesNotThrow(() -> new IpAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new IpAddressConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpAddressConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpAddressConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new IpAddressConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorEmptySubnetSet() {
		assertThrows(IllegalArgumentException.class, () -> new IpAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.ipVersion().isEmpty());
		assertTrue(config.ipType().isEmpty());
		assertTrue(config.inAnySubnet().isEmpty());
		assertTrue(config.stringConstraint().isEmpty());
		assertTrue(config.custom().isEmpty());
	}
	
	@Test
	void withEqualTo() {
		IpAddress<?> value = IpAddresses.parse("192.168.1.1");
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withEqualTo(value);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> IpAddressConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		IpAddress<?> value = IpAddresses.parse("192.168.1.1");
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withNotEqualTo(value);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> IpAddressConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		IpAddress<?> addr1 = IpAddresses.parse("192.168.1.1");
		IpAddress<?> addr2 = IpAddresses.parse("192.168.1.2");
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withIn(List.of(addr1, addr2));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> IpAddressConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		IpAddress<?> addr = IpAddresses.parse("192.168.1.1");
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withNotIn(List.of(addr));
		
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> IpAddressConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withIpVersion() {
		EnumConstraintConfig<IpVersion> versionConfig = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withIpVersion(versionConfig);
		
		assertTrue(config.ipVersion().isPresent());
	}
	
	@Test
	void withIpVersionNull() {
		assertThrows(NullPointerException.class, () -> IpAddressConstraintConfig.UNCONSTRAINED.withIpVersion(null));
	}
	
	@Test
	void withIpType() {
		EnumConstraintConfig<IpAddressType> typeConfig = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE);
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withIpType(typeConfig);
		
		assertTrue(config.ipType().isPresent());
	}
	
	@Test
	void withIpTypeNull() {
		assertThrows(NullPointerException.class, () -> IpAddressConstraintConfig.UNCONSTRAINED.withIpType(null));
	}
	
	@Test
	void withInAnySubnet() {
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withInAnySubnet(List.of("192.168.0.0/16"));
		
		assertTrue(config.inAnySubnet().isPresent());
		assertFalse(config.inAnySubnet().get().getSecond());
	}
	
	@Test
	void withInAnySubnetNull() {
		assertThrows(NullPointerException.class, () -> IpAddressConstraintConfig.UNCONSTRAINED.withInAnySubnet(null));
	}
	
	@Test
	void withNotInAnySubnet() {
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withNotInAnySubnet(List.of("10.0.0.0/8"));
		
		assertTrue(config.inAnySubnet().isPresent());
		assertTrue(config.inAnySubnet().get().getSecond());
	}
	
	@Test
	void withNotInAnySubnetNull() {
		assertThrows(NullPointerException.class, () -> IpAddressConstraintConfig.UNCONSTRAINED.withNotInAnySubnet(null));
	}
	
	@Test
	void withCustom() {
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withCustom(value -> Result.success());
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> IpAddressConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesUnconstrained() {
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED;
		
		assertTrue(config.matches(IpAddresses.parse("192.168.1.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("10.0.0.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("127.0.0.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("::1")).isSuccess());
	}
	
	@Test
	void matchesWithNull() {
		assertThrows(NullPointerException.class, () -> IpAddressConstraintConfig.UNCONSTRAINED.matches(null));
	}
	
	@Test
	void matchesEqualTo() {
		IpAddress<?> expected = IpAddresses.parse("192.168.1.1");
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withEqualTo(expected);
		
		assertTrue(config.matches(IpAddresses.parse("192.168.1.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("192.168.1.2")).isError());
	}
	
	@Test
	void matchesNotEqualTo() {
		IpAddress<?> excluded = IpAddresses.parse("192.168.1.1");
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withNotEqualTo(excluded);
		
		assertTrue(config.matches(IpAddresses.parse("192.168.1.2")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("192.168.1.1")).isError());
	}
	
	@Test
	void matchesIn() {
		IpAddress<?> addr1 = IpAddresses.parse("192.168.1.1");
		IpAddress<?> addr2 = IpAddresses.parse("192.168.1.2");
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withIn(List.of(addr1, addr2));
		
		assertTrue(config.matches(IpAddresses.parse("192.168.1.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("192.168.1.2")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("192.168.1.3")).isError());
	}
	
	@Test
	void matchesNotIn() {
		IpAddress<?> excluded = IpAddresses.parse("192.168.1.1");
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withNotIn(List.of(excluded));
		
		assertTrue(config.matches(IpAddresses.parse("192.168.1.2")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("192.168.1.1")).isError());
	}
	
	@Test
	void matchesIpVersionIpv4() {
		EnumConstraintConfig<IpVersion> versionConfig = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withIpVersion(versionConfig);
		
		assertTrue(config.matches(IpAddresses.parse("192.168.1.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("::1")).isError());
	}
	
	@Test
	void matchesIpVersionIpv6() {
		EnumConstraintConfig<IpVersion> versionConfig = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withIpVersion(versionConfig);
		
		assertTrue(config.matches(IpAddresses.parse("::1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("2001:db8::1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("192.168.1.1")).isError());
	}
	
	@Test
	void matchesIpTypePrivate() {
		EnumConstraintConfig<IpAddressType> typeConfig = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE);
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withIpType(typeConfig);
		
		assertTrue(config.matches(IpAddresses.parse("192.168.1.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("10.0.0.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("172.16.0.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("127.0.0.1")).isError());
	}
	
	@Test
	void matchesIpTypeLoopback() {
		EnumConstraintConfig<IpAddressType> typeConfig = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withIpType(typeConfig);
		
		assertTrue(config.matches(IpAddresses.parse("127.0.0.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("::1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("192.168.1.1")).isError());
	}
	
	@Test
	void matchesInAnySubnet() {
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withInAnySubnet(List.of("192.168.0.0/16"));
		
		assertTrue(config.matches(IpAddresses.parse("192.168.1.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("192.168.255.255")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("10.0.0.1")).isError());
	}
	
	@Test
	void matchesNotInAnySubnet() {
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withNotInAnySubnet(List.of("192.168.0.0/16"));
		
		assertTrue(config.matches(IpAddresses.parse("10.0.0.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("172.16.0.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("192.168.1.1")).isError());
	}
	
	@Test
	void matchesCustom() {
		IpAddressConstraintConfig config = IpAddressConstraintConfig.UNCONSTRAINED.withCustom(
			value -> value.toString().startsWith("192.") ? Result.success() : Result.error("Address must start with 192.")
		);
		
		assertTrue(config.matches(IpAddresses.parse("192.168.1.1")).isSuccess());
		assertTrue(config.matches(IpAddresses.parse("10.0.0.1")).isError());
	}
}
