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

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link HostConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class HostConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> HostConstraintConfig.UNCONSTRAINED);
		assertDoesNotThrow(() -> new HostConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new HostConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new HostConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new HostConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new HostConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new HostConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new HostConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorIpAndDomainMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new HostConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.of(IpConstraintConfig.UNCONSTRAINED), Optional.of(DomainConstraintConfig.UNCONSTRAINED), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.ip().isEmpty());
		assertTrue(config.domain().isEmpty());
		assertTrue(config.custom().isEmpty());
	}
	
	@Test
	void withEqualTo() {
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		
		assertTrue(config.equalTo().isPresent());
		assertEquals("example.com", config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> HostConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withNotEqualTo("blocked.com");
		
		assertTrue(config.equalTo().isPresent());
		assertEquals("blocked.com", config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withIn(List.of("example.com", "192.168.1.1"));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> HostConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withNotIn(List.of("blocked.com"));
		
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withIp() {
		IpConstraintConfig ipConfig = IpConstraintConfig.UNCONSTRAINED.withStartsWith("192.");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withIp(ipConfig);
		
		assertTrue(config.ip().isPresent());
		assertTrue(config.domain().isEmpty());
	}
	
	@Test
	void withIpNull() {
		assertThrows(NullPointerException.class, () -> HostConstraintConfig.UNCONSTRAINED.withIp(null));
	}
	
	@Test
	void withIpClearsDomain() {
		DomainConstraintConfig domainConfig = DomainConstraintConfig.UNCONSTRAINED.withEndsWith(".com");
		HostConstraintConfig configWithDomain = HostConstraintConfig.UNCONSTRAINED.withDomain(domainConfig);
		
		IpConstraintConfig ipConfig = IpConstraintConfig.UNCONSTRAINED;
		HostConstraintConfig configWithIp = configWithDomain.withIp(ipConfig);
		
		assertTrue(configWithIp.ip().isPresent());
		assertTrue(configWithIp.domain().isEmpty());
	}
	
	@Test
	void withDomain() {
		DomainConstraintConfig domainConfig = DomainConstraintConfig.UNCONSTRAINED.withEndsWith(".com");
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withDomain(domainConfig);
		
		assertTrue(config.domain().isPresent());
		assertTrue(config.ip().isEmpty());
	}
	
	@Test
	void withDomainNull() {
		assertThrows(NullPointerException.class, () -> HostConstraintConfig.UNCONSTRAINED.withDomain(null));
	}
	
	@Test
	void withDomainClearsIp() {
		IpConstraintConfig ipConfig = IpConstraintConfig.UNCONSTRAINED.withStartsWith("192.");
		HostConstraintConfig configWithIp = HostConstraintConfig.UNCONSTRAINED.withIp(ipConfig);
		
		DomainConstraintConfig domainConfig = DomainConstraintConfig.UNCONSTRAINED;
		HostConstraintConfig configWithDomain = configWithIp.withDomain(domainConfig);
		
		assertTrue(configWithDomain.domain().isPresent());
		assertTrue(configWithDomain.ip().isEmpty());
	}
	
	@Test
	void withCustom() {
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withCustom(value -> Result.success());
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> HostConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesUnconstrained() {
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED;
		
		assertTrue(config.matches("example.com").isSuccess());
		assertTrue(config.matches("192.168.1.1").isSuccess());
		assertTrue(config.matches("any-string").isSuccess());
	}
	
	@Test
	void matchesWithNull() {
		assertThrows(NullPointerException.class, () -> HostConstraintConfig.UNCONSTRAINED.matches(null));
	}
	
	@Test
	void matchesEqualTo() {
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		
		assertTrue(config.matches("example.com").isSuccess());
		assertTrue(config.matches("other.com").isError());
	}
	
	@Test
	void matchesNotEqualTo() {
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withNotEqualTo("blocked.com");
		
		assertTrue(config.matches("example.com").isSuccess());
		assertTrue(config.matches("blocked.com").isError());
	}
	
	@Test
	void matchesIn() {
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withIn(List.of("example.com", "192.168.1.1"));
		
		assertTrue(config.matches("example.com").isSuccess());
		assertTrue(config.matches("192.168.1.1").isSuccess());
		assertTrue(config.matches("other.com").isError());
	}
	
	@Test
	void matchesNotIn() {
		HostConstraintConfig config = HostConstraintConfig.UNCONSTRAINED.withNotIn(List.of("blocked.com"));
		
		assertTrue(config.matches("example.com").isSuccess());
		assertTrue(config.matches("blocked.com").isError());
	}
}
