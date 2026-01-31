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
import net.luis.utils.io.codec.constraint.util.IpAddressType;
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class IpConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> IpConstraintConfig.UNCONSTRAINED);
		assertDoesNotThrow(() -> new IpConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new IpConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new IpConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new IpConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorMinMaxLengthValidation() {
		// LengthConstraintConfig validates this internally
		assertThrows(IllegalArgumentException.class, () -> new LengthConstraintConfig(
			Optional.empty(), Optional.empty(),
			Optional.of(Pair.of(10, true)), Optional.of(Pair.of(5, true)),
			Optional.empty()
		));
		assertThrows(IllegalArgumentException.class, () -> new LengthConstraintConfig(
			Optional.empty(), Optional.empty(),
			Optional.of(Pair.of(5, false)), Optional.of(Pair.of(5, true)),
			Optional.empty()
		));
	}

	@Test
	void constructorEmptySubnetSet() {
		assertThrows(IllegalArgumentException.class, () -> new IpConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertTrue(config.custom().isEmpty());
	}
	
	@Test
	void withEqualTo() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withEqualTo("192.168.1.1");
		
		assertTrue(config.equalTo().isPresent());
		assertEquals("192.168.1.1", config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> IpConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withNotEqualTo("10.0.0.1");
		
		assertTrue(config.equalTo().isPresent());
		assertEquals("10.0.0.1", config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withIn(List.of("192.168.1.1", "10.0.0.1"));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withNotIn(List.of("192.168.1.1", "10.0.0.1"));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withMinLength() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withMinLength(7);

		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertEquals(7, config.length().get().min().get().getFirst());
		assertTrue(config.length().get().min().get().getSecond());
	}

	@Test
	void withMaxLength() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withMaxLength(15);

		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(15, config.length().get().max().get().getFirst());
		assertTrue(config.length().get().max().get().getSecond());
	}

	@Test
	void withExactLength() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withExactLength(11);

		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(11, config.length().get().min().get().getFirst());
		assertEquals(11, config.length().get().max().get().getFirst());
	}

	@Test
	void withLengthBetween() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withLengthBetween(7, 15);

		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(7, config.length().get().min().get().getFirst());
		assertEquals(15, config.length().get().max().get().getFirst());
	}

	@Test
	void withLength() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(7).withMaxLength(15);
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withLength(lengthConfig);

		assertTrue(config.length().isPresent());
		assertEquals(lengthConfig, config.length().get());
	}
	
	@Test
	void withStartsWith() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withStartsWith("192.");
		
		assertTrue(config.startsWith().isPresent());
		assertEquals("192.", config.startsWith().get().getFirst());
		assertFalse(config.startsWith().get().getSecond());
	}
	
	@Test
	void withNotStartsWith() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withNotStartsWith("10.");
		
		assertTrue(config.startsWith().isPresent());
		assertEquals("10.", config.startsWith().get().getFirst());
		assertTrue(config.startsWith().get().getSecond());
	}
	
	@Test
	void withStartsWithAny() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withStartsWithAny(List.of("192.", "10."));
		
		assertTrue(config.startsWithAny().isPresent());
		assertEquals(2, config.startsWithAny().get().getFirst().size());
		assertFalse(config.startsWithAny().get().getSecond());
	}
	
	@Test
	void withContains() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withContains("168");
		
		assertTrue(config.contains().isPresent());
		assertEquals("168", config.contains().get().getFirst());
		assertFalse(config.contains().get().getSecond());
	}
	
	@Test
	void withNotContains() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withNotContains("0.0");
		
		assertTrue(config.contains().isPresent());
		assertEquals("0.0", config.contains().get().getFirst());
		assertTrue(config.contains().get().getSecond());
	}
	
	@Test
	void withEndsWith() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withEndsWith(".1");
		
		assertTrue(config.endsWith().isPresent());
		assertEquals(".1", config.endsWith().get().getFirst());
		assertFalse(config.endsWith().get().getSecond());
	}
	
	@Test
	void withMatchesRegex() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withMatches("\\d+\\.\\d+\\.\\d+\\.\\d+");
		
		assertTrue(config.matches().isPresent());
		assertFalse(config.matches().get().getSecond());
	}
	
	@Test
	void withMatchesPattern() {
		Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withMatches(pattern);
		
		assertTrue(config.matches().isPresent());
		assertFalse(config.matches().get().getSecond());
	}
	
	@Test
	void withIpVersion() {
		EnumConstraintConfig<IpVersion> versionConfig = EnumConstraintConfig.<IpVersion>unconstrained().withEqualTo(IpVersion.IPV4);
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withIpVersion(versionConfig);
		
		assertTrue(config.ipVersion().isPresent());
	}
	
	@Test
	void withIpType() {
		EnumConstraintConfig<IpAddressType> typeConfig = EnumConstraintConfig.<IpAddressType>unconstrained().withEqualTo(IpAddressType.PUBLIC);
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withIpType(typeConfig);
		
		assertTrue(config.ipType().isPresent());
	}
	
	@Test
	void withInAnySubnet() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withInAnySubnet(List.of("192.168.0.0/16", "10.0.0.0/8"));
		
		assertTrue(config.inAnySubnet().isPresent());
		assertEquals(2, config.inAnySubnet().get().getFirst().size());
		assertFalse(config.inAnySubnet().get().getSecond());
	}
	
	@Test
	void withNotInAnySubnet() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withNotInAnySubnet(List.of("192.168.0.0/16"));
		
		assertTrue(config.inAnySubnet().isPresent());
		assertTrue(config.inAnySubnet().get().getSecond());
	}
	
	@Test
	void withCustom() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withCustom(value -> Result.success());
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> IpConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesUnconstrained() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED;
		
		assertTrue(config.matches("192.168.1.1").isSuccess());
		assertTrue(config.matches("10.0.0.1").isSuccess());
		assertTrue(config.matches("any-string").isSuccess());
	}
	
	@Test
	void matchesWithNull() {
		assertThrows(NullPointerException.class, () -> IpConstraintConfig.UNCONSTRAINED.matches(null));
	}
	
	@Test
	void matchesEqualTo() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withEqualTo("192.168.1.1");
		
		assertTrue(config.matches("192.168.1.1").isSuccess());
		assertTrue(config.matches("10.0.0.1").isError());
	}
	
	@Test
	void matchesNotEqualTo() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withNotEqualTo("192.168.1.1");
		
		assertTrue(config.matches("10.0.0.1").isSuccess());
		assertTrue(config.matches("192.168.1.1").isError());
	}
	
	@Test
	void matchesIn() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withIn(List.of("192.168.1.1", "10.0.0.1"));
		
		assertTrue(config.matches("192.168.1.1").isSuccess());
		assertTrue(config.matches("10.0.0.1").isSuccess());
		assertTrue(config.matches("172.16.0.1").isError());
	}
	
	@Test
	void matchesLengthConstraint() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withLengthBetween(7, 15);
		
		assertTrue(config.matches("1.1.1.1").isSuccess());
		assertTrue(config.matches("192.168.1.1").isSuccess());
		assertTrue(config.matches("192.168.100.100").isSuccess());
		assertTrue(config.matches("1.1.1").isError());
	}
	
	@Test
	void matchesStartsWith() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withStartsWith("192.");
		
		assertTrue(config.matches("192.168.1.1").isSuccess());
		assertTrue(config.matches("10.0.0.1").isError());
	}
	
	@Test
	void matchesContains() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withContains("168");
		
		assertTrue(config.matches("192.168.1.1").isSuccess());
		assertTrue(config.matches("10.0.0.1").isError());
	}
	
	@Test
	void matchesEndsWith() {
		IpConstraintConfig config = IpConstraintConfig.UNCONSTRAINED.withEndsWith(".1");
		
		assertTrue(config.matches("192.168.1.1").isSuccess());
		assertTrue(config.matches("192.168.1.2").isError());
	}
}
