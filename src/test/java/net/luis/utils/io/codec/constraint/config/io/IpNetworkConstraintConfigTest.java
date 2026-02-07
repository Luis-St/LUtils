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
import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.io.network.address.IpNetwork;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpNetworkConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class IpNetworkConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> IpNetworkConstraintConfig.UNCONSTRAINED);
		assertDoesNotThrow(() -> new IpNetworkConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new IpNetworkConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpNetworkConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpNetworkConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpNetworkConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpNetworkConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpNetworkConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpNetworkConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new IpNetworkConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.ipVersion().isEmpty());
		assertTrue(config.prefixLength().isEmpty());
		assertTrue(config.canonical().isEmpty());
		assertTrue(config.stringConstraint().isEmpty());
		assertTrue(config.custom().isEmpty());
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(IpNetworkConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withIpVersion(EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4));
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		IpNetwork<?, ?> value = IpAddresses.parseNetwork("192.168.0.0/16");
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withEqualTo(value);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> IpNetworkConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		IpNetwork<?, ?> value = IpAddresses.parseNetwork("192.168.0.0/16");
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withNotEqualTo(value);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(value, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> IpNetworkConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		IpNetwork<?, ?> net1 = IpAddresses.parseNetwork("192.168.0.0/16");
		IpNetwork<?, ?> net2 = IpAddresses.parseNetwork("10.0.0.0/8");
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withIn(List.of(net1, net2));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> IpNetworkConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		IpNetwork<?, ?> net = IpAddresses.parseNetwork("192.168.0.0/16");
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withNotIn(List.of(net));
		
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> IpNetworkConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withIpVersion() {
		EnumConstraintConfig<IpVersion> versionConfig = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withIpVersion(versionConfig);
		
		assertTrue(config.ipVersion().isPresent());
	}
	
	@Test
	void withIpVersionNull() {
		assertThrows(NullPointerException.class, () -> IpNetworkConstraintConfig.UNCONSTRAINED.withIpVersion(null));
	}
	
	@Test
	void withPrefixLength() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(8);
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withPrefixLength(lengthConfig);
		
		assertTrue(config.prefixLength().isPresent());
	}
	
	@Test
	void withPrefixLengthNull() {
		assertThrows(NullPointerException.class, () -> IpNetworkConstraintConfig.UNCONSTRAINED.withPrefixLength(null));
	}
	
	@Test
	void withCanonical() {
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withCanonical();
		
		assertTrue(config.canonical().isPresent());
	}
	
	@Test
	void withCustom() {
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withCustom(value -> {});
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> IpNetworkConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void validateUnconstrained() {
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED;
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("10.0.0.0/8")));
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("2001:db8::/32")));
	}
	
	@Test
	void validateWithNull() {
		assertThrows(NullPointerException.class, () -> IpNetworkConstraintConfig.UNCONSTRAINED.validate(null));
	}
	
	@Test
	void validateEqualTo() {
		IpNetwork<?, ?> expected = IpAddresses.parseNetwork("192.168.0.0/16");
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withEqualTo(expected);
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(IpAddresses.parseNetwork("192.168.0.0/24")));
	}
	
	@Test
	void validateNotEqualTo() {
		IpNetwork<?, ?> excluded = IpAddresses.parseNetwork("192.168.0.0/16");
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withNotEqualTo(excluded);
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("10.0.0.0/8")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
	}
	
	@Test
	void validateIn() {
		IpNetwork<?, ?> net1 = IpAddresses.parseNetwork("192.168.0.0/16");
		IpNetwork<?, ?> net2 = IpAddresses.parseNetwork("10.0.0.0/8");
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withIn(List.of(net1, net2));
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("10.0.0.0/8")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(IpAddresses.parseNetwork("172.16.0.0/12")));
	}
	
	@Test
	void validateNotIn() {
		IpNetwork<?, ?> excluded = IpAddresses.parseNetwork("192.168.0.0/16");
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withNotIn(List.of(excluded));
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("10.0.0.0/8")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
	}
	
	@Test
	void validateIpVersionIpv4() {
		EnumConstraintConfig<IpVersion> versionConfig = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withIpVersion(versionConfig);
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(IpAddresses.parseNetwork("2001:db8::/32")));
	}
	
	@Test
	void validateIpVersionIpv6() {
		EnumConstraintConfig<IpVersion> versionConfig = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV6);
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withIpVersion(versionConfig);
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("2001:db8::/32")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
	}
	
	@Test
	void validatePrefixLengthMin() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(16);
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withPrefixLength(lengthConfig);
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("192.168.1.0/24")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(IpAddresses.parseNetwork("10.0.0.0/8")));
	}
	
	@Test
	void validatePrefixLengthMax() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMaxLength(16);
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withPrefixLength(lengthConfig);
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("10.0.0.0/8")));
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(IpAddresses.parseNetwork("192.168.1.0/24")));
	}
	
	@Test
	void validatePrefixLengthExact() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withExactLength(16);
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withPrefixLength(lengthConfig);
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(IpAddresses.parseNetwork("10.0.0.0/8")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(IpAddresses.parseNetwork("192.168.1.0/24")));
	}
	
	@Test
	void validatePrefixLengthBetween() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(8).withMaxLength(16);
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withPrefixLength(lengthConfig);
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("10.0.0.0/8")));
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(IpAddresses.parseNetwork("192.168.1.0/24")));
	}
	
	@Test
	void validateCanonical() {
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withCanonical();
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("10.0.0.0/8")));
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("2001:db8::/32")));
	}
	
	@Test
	void validateCustom() {
		IpNetworkConstraintConfig config = IpNetworkConstraintConfig.UNCONSTRAINED.withCustom(
			value -> {
				if (value.prefixLength() != 16) throw new ConstraintViolateException("Prefix length must be 16");
			}
		);
		
		assertDoesNotThrow(() -> config.validate(IpAddresses.parseNetwork("192.168.0.0/16")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(IpAddresses.parseNetwork("10.0.0.0/8")));
	}
}
