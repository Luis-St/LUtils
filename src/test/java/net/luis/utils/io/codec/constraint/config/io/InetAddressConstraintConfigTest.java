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
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InetAddressConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class InetAddressConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> InetAddressConstraintConfig.UNCONSTRAINED);
		assertDoesNotThrow(() -> new InetAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new InetAddressConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new InetAddressConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new InetAddressConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new InetAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new InetAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new InetAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new InetAddressConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorEmptySubnetSet() {
		assertThrows(IllegalArgumentException.class, () -> new InetAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.ipVersion().isEmpty());
		assertTrue(config.ipType().isEmpty());
		assertTrue(config.inAnySubnet().isEmpty());
		assertTrue(config.custom().isEmpty());
	}

	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(InetAddressConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithConstraint() {
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpType(EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withEqualTo() throws UnknownHostException {
		InetAddress value = InetAddress.getByName("192.168.1.1");
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withEqualTo(value);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> InetAddressConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() throws UnknownHostException {
		InetAddress value = InetAddress.getByName("192.168.1.1");
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withNotEqualTo(value);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> InetAddressConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}
	
	@Test
	void withIn() throws UnknownHostException {
		InetAddress addr1 = InetAddress.getByName("192.168.1.1");
		InetAddress addr2 = InetAddress.getByName("192.168.1.2");
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIn(List.of(addr1, addr2));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> InetAddressConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() throws UnknownHostException {
		InetAddress addr = InetAddress.getByName("192.168.1.1");
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withNotIn(List.of(addr));
		
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> InetAddressConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withIpVersion() {
		EnumConstraintConfig<IpVersion> versionConfig = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpVersion(versionConfig);
		
		assertTrue(config.ipVersion().isPresent());
	}
	
	@Test
	void withIpVersionNull() {
		assertThrows(NullPointerException.class, () -> InetAddressConstraintConfig.UNCONSTRAINED.withIpVersion(null));
	}
	
	@Test
	void withIpType() {
		EnumConstraintConfig<IpAddressType> typeConfig = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpType(typeConfig);
		
		assertTrue(config.ipType().isPresent());
	}
	
	@Test
	void withIpTypeNull() {
		assertThrows(NullPointerException.class, () -> InetAddressConstraintConfig.UNCONSTRAINED.withIpType(null));
	}
	
	@Test
	void withInAnySubnet() {
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withInAnySubnet(List.of("192.168.0.0/16"));
		
		assertTrue(config.inAnySubnet().isPresent());
		assertFalse(config.inAnySubnet().get().getSecond());
	}
	
	@Test
	void withInAnySubnetNull() {
		assertThrows(NullPointerException.class, () -> InetAddressConstraintConfig.UNCONSTRAINED.withInAnySubnet(null));
	}
	
	@Test
	void withNotInAnySubnet() {
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withNotInAnySubnet(List.of("10.0.0.0/8"));
		
		assertTrue(config.inAnySubnet().isPresent());
		assertTrue(config.inAnySubnet().get().getSecond());
	}
	
	@Test
	void withNotInAnySubnetNull() {
		assertThrows(NullPointerException.class, () -> InetAddressConstraintConfig.UNCONSTRAINED.withNotInAnySubnet(null));
	}
	
	@Test
	void withCustom() {
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withCustom(value -> Result.success());
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> InetAddressConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesUnconstrained() throws UnknownHostException {
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED;
		
		assertTrue(config.matches(InetAddress.getByName("192.168.1.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("10.0.0.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("127.0.0.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("::1")).isSuccess());
	}
	
	@Test
	void matchesWithNull() {
		assertThrows(NullPointerException.class, () -> InetAddressConstraintConfig.UNCONSTRAINED.matches(null));
	}
	
	@Test
	void matchesEqualTo() throws UnknownHostException {
		InetAddress expected = InetAddress.getByName("192.168.1.1");
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withEqualTo(expected);
		
		assertTrue(config.matches(InetAddress.getByName("192.168.1.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("192.168.1.2")).isError());
	}
	
	@Test
	void matchesNotEqualTo() throws UnknownHostException {
		InetAddress excluded = InetAddress.getByName("192.168.1.1");
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withNotEqualTo(excluded);
		
		assertTrue(config.matches(InetAddress.getByName("192.168.1.2")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("192.168.1.1")).isError());
	}
	
	@Test
	void matchesIn() throws UnknownHostException {
		InetAddress addr1 = InetAddress.getByName("192.168.1.1");
		InetAddress addr2 = InetAddress.getByName("192.168.1.2");
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIn(List.of(addr1, addr2));
		
		assertTrue(config.matches(InetAddress.getByName("192.168.1.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("192.168.1.2")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("192.168.1.3")).isError());
	}
	
	@Test
	void matchesNotIn() throws UnknownHostException {
		InetAddress excluded = InetAddress.getByName("192.168.1.1");
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withNotIn(List.of(excluded));
		
		assertTrue(config.matches(InetAddress.getByName("192.168.1.2")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("192.168.1.1")).isError());
	}
	
	@Test
	void matchesIpVersionIpv4() throws UnknownHostException {
		EnumConstraintConfig<IpVersion> versionConfig = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpVersion(versionConfig);
		
		assertTrue(config.matches(InetAddress.getByName("192.168.1.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("::1")).isError());
	}
	
	@Test
	void matchesIpVersionIpv6() throws UnknownHostException {
		EnumConstraintConfig<IpVersion> versionConfig = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpVersion(versionConfig);
		
		assertTrue(config.matches(InetAddress.getByName("::1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("2001:db8::1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("192.168.1.1")).isError());
	}
	
	@Test
	void matchesIpTypePrivate() throws UnknownHostException {
		EnumConstraintConfig<IpAddressType> typeConfig = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpType(typeConfig);
		
		assertTrue(config.matches(InetAddress.getByName("192.168.1.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("10.0.0.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("172.16.0.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("127.0.0.1")).isError());
	}
	
	@Test
	void matchesIpTypeLoopback() throws UnknownHostException {
		EnumConstraintConfig<IpAddressType> typeConfig = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.LOOPBACK);
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withIpType(typeConfig);
		
		assertTrue(config.matches(InetAddress.getByName("127.0.0.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("::1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("192.168.1.1")).isError());
	}
	
	@Test
	void matchesInAnySubnet() throws UnknownHostException {
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withInAnySubnet(List.of("192.168.0.0/16"));
		
		assertTrue(config.matches(InetAddress.getByName("192.168.1.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("192.168.255.255")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("10.0.0.1")).isError());
	}
	
	@Test
	void matchesNotInAnySubnet() throws UnknownHostException {
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withNotInAnySubnet(List.of("192.168.0.0/16"));
		
		assertTrue(config.matches(InetAddress.getByName("10.0.0.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("172.16.0.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("192.168.1.1")).isError());
	}
	
	@Test
	void matchesCustom() throws UnknownHostException {
		InetAddressConstraintConfig config = InetAddressConstraintConfig.UNCONSTRAINED.withCustom(
			value -> value.getHostAddress().startsWith("192.") ? Result.success() : Result.error("Address must start with 192.")
		);
		
		assertTrue(config.matches(InetAddress.getByName("192.168.1.1")).isSuccess());
		assertTrue(config.matches(InetAddress.getByName("10.0.0.1")).isError());
	}
}
