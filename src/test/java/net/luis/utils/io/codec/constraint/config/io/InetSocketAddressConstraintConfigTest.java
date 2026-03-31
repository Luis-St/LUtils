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
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.constraint.util.*;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.net.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InetSocketAddressConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class InetSocketAddressConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> InetSocketAddressConstraintConfig.UNCONSTRAINED);
		assertDoesNotThrow(() -> new InetSocketAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new InetSocketAddressConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new InetSocketAddressConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new InetSocketAddressConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new InetSocketAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new InetSocketAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new InetSocketAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new InetSocketAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new InetSocketAddressConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorResolvedUnresolvedMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new InetSocketAddressConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(Unit.INSTANCE), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.address().isEmpty());
		assertTrue(config.port().isEmpty());
		assertTrue(config.resolved().isEmpty());
		assertTrue(config.unresolved().isEmpty());
		assertTrue(config.custom().isEmpty());
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(InetSocketAddressConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withPort(PortConstraintConfig.UNCONSTRAINED.withInRange(1, 65535));
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() throws UnknownHostException {
		InetSocketAddress value = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withEqualTo(value);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> InetSocketAddressConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() throws UnknownHostException {
		InetSocketAddress value = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withNotEqualTo(value);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> InetSocketAddressConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}
	
	@Test
	void withIn() throws UnknownHostException {
		InetSocketAddress addr1 = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		InetSocketAddress addr2 = new InetSocketAddress(InetAddress.getByName("192.168.1.2"), 8081);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withIn(List.of(addr1, addr2));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> InetSocketAddressConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() throws UnknownHostException {
		InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withNotIn(List.of(addr));
		
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> InetSocketAddressConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withAddress() {
		InetAddressConstraintConfig addressConfig = InetAddressConstraintConfig.UNCONSTRAINED.withIpVersion(
			EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4)
		);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withAddress(addressConfig);
		
		assertTrue(config.address().isPresent());
	}
	
	@Test
	void withAddressNull() {
		assertThrows(NullPointerException.class, () -> InetSocketAddressConstraintConfig.UNCONSTRAINED.withAddress(null));
	}
	
	@Test
	void withPort() {
		PortConstraintConfig portConfig = PortConstraintConfig.UNCONSTRAINED.withInRange(1024, 65535);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withPort(portConfig);
		
		assertTrue(config.port().isPresent());
	}
	
	@Test
	void withPortNull() {
		assertThrows(NullPointerException.class, () -> InetSocketAddressConstraintConfig.UNCONSTRAINED.withPort(null));
	}
	
	@Test
	void withResolved() {
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withResolved();
		
		assertTrue(config.resolved().isPresent());
		assertTrue(config.unresolved().isEmpty());
	}
	
	@Test
	void withUnresolved() {
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withUnresolved();
		
		assertTrue(config.unresolved().isPresent());
		assertTrue(config.resolved().isEmpty());
	}
	
	@Test
	void withCustom() {
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withCustom(value -> {});
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> InetSocketAddressConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void validateUnconstrained() throws UnknownHostException {
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED;
		
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080)));
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("10.0.0.1"), 443)));
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 80)));
	}
	
	@Test
	void validateWithNull() {
		assertThrows(NullPointerException.class, () -> InetSocketAddressConstraintConfig.UNCONSTRAINED.validate(null));
	}
	
	@Test
	void validateEqualTo() throws UnknownHostException {
		InetSocketAddress expected = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withEqualTo(expected);
		
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8081)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.2"), 8080)));
	}
	
	@Test
	void validateNotEqualTo() throws UnknownHostException {
		InetSocketAddress excluded = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withNotEqualTo(excluded);
		
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.2"), 8080)));
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8081)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080)));
	}
	
	@Test
	void validateIn() throws UnknownHostException {
		InetSocketAddress addr1 = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		InetSocketAddress addr2 = new InetSocketAddress(InetAddress.getByName("192.168.1.2"), 8080);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withIn(List.of(addr1, addr2));
		
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080)));
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.2"), 8080)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.3"), 8080)));
	}
	
	@Test
	void validateNotIn() throws UnknownHostException {
		InetSocketAddress excluded = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withNotIn(List.of(excluded));
		
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.2"), 8080)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080)));
	}
	
	@Test
	void validateAddressConstraint() throws UnknownHostException {
		InetAddressConstraintConfig addressConfig = InetAddressConstraintConfig.UNCONSTRAINED.withIpType(
			EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PRIVATE)
		);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withAddress(addressConfig);
		
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080)));
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("10.0.0.1"), 8080)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 8080)));
	}
	
	@Test
	void validatePortConstraint() throws UnknownHostException {
		PortConstraintConfig portConfig = PortConstraintConfig.UNCONSTRAINED.withInRange(1024, 65535);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withPort(portConfig);
		
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080)));
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 65535)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 80)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 443)));
	}
	
	@Test
	void validateResolved() throws UnknownHostException {
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withResolved();
		
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080)));
		
		InetSocketAddress unresolved = InetSocketAddress.createUnresolved("nonexistent.invalid", 8080);
		assertThrows(ConstraintViolateException.class, () -> config.validate(unresolved));
	}
	
	@Test
	void validateUnresolved() throws UnknownHostException {
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withUnresolved();
		
		InetSocketAddress unresolved = InetSocketAddress.createUnresolved("nonexistent.invalid", 8080);
		assertDoesNotThrow(() -> config.validate(unresolved));
		
		assertThrows(ConstraintViolateException.class, () -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080)));
	}
	
	@Test
	void validateCustom() throws UnknownHostException {
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED.withCustom(value -> {
			if (value.getPort() != 8080) {
				throw new ConstraintViolateException("Port must be 8080");
			}
		});
		
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8081)));
	}
	
	@Test
	void validateCombinedConstraints() throws UnknownHostException {
		InetAddressConstraintConfig addressConfig = InetAddressConstraintConfig.UNCONSTRAINED.withInAnySubnet(List.of("192.168.0.0/16"));
		PortConstraintConfig portConfig = PortConstraintConfig.UNCONSTRAINED.withInRange(8000, 9000);
		InetSocketAddressConstraintConfig config = InetSocketAddressConstraintConfig.UNCONSTRAINED
			.withAddress(addressConfig)
			.withPort(portConfig)
			.withResolved();
		
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 8080)));
		assertDoesNotThrow(() -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.255.255"), 9000)));
		
		assertThrows(ConstraintViolateException.class, () -> config.validate(new InetSocketAddress(InetAddress.getByName("10.0.0.1"), 8080)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 80)));
	}
}
