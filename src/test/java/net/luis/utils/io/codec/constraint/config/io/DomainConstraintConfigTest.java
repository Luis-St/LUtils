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

import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DomainConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class DomainConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> DomainConstraintConfig.UNCONSTRAINED);
		assertDoesNotThrow(() -> new DomainConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new DomainConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new DomainConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new DomainConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructorMinMaxLengthValidation() {
		// LengthConstraintConfig validates this internally
		assertThrows(IllegalArgumentException.class, () -> new LengthConstraintConfig(
			Optional.empty(), Optional.empty(),
			Optional.of(Pair.of(20, true)), Optional.of(Pair.of(10, true)),
			Optional.empty()
		));
	}

	@Test
	void constructorRootAndSubDomainMutuallyExclusive() {
		assertThrows(IllegalArgumentException.class, () -> new DomainConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.of(Unit.INSTANCE), Optional.of(Unit.INSTANCE), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.rootDomain().isEmpty());
		assertTrue(config.subDomain().isEmpty());
		assertTrue(config.custom().isEmpty());
	}
	
	@Test
	void withEqualTo() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		
		assertTrue(config.equalTo().isPresent());
		assertEquals("example.com", config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> DomainConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withNotEqualTo("blocked.com");
		
		assertTrue(config.equalTo().isPresent());
		assertEquals("blocked.com", config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withIn(List.of("example.com", "test.com"));
		
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withNotIn(List.of("blocked.com"));
		
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withMinLength() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withMinLength(5);

		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertEquals(5, config.length().get().min().get().getFirst());
		assertTrue(config.length().get().min().get().getSecond());
	}

	@Test
	void withMaxLength() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withMaxLength(253);

		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(253, config.length().get().max().get().getFirst());
		assertTrue(config.length().get().max().get().getSecond());
	}

	@Test
	void withExactLength() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withExactLength(11);

		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(11, config.length().get().min().get().getFirst());
		assertEquals(11, config.length().get().max().get().getFirst());
	}

	@Test
	void withLengthBetween() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withLengthBetween(5, 50);

		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(5, config.length().get().min().get().getFirst());
		assertEquals(50, config.length().get().max().get().getFirst());
	}

	@Test
	void withLength() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(5).withMaxLength(50);
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withLength(lengthConfig);

		assertTrue(config.length().isPresent());
		assertEquals(lengthConfig, config.length().get());
	}
	
	@Test
	void withStartsWith() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withStartsWith("www.");
		
		assertTrue(config.startsWith().isPresent());
		assertEquals("www.", config.startsWith().get().getFirst());
		assertFalse(config.startsWith().get().getSecond());
	}
	
	@Test
	void withNotStartsWith() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withNotStartsWith("test.");
		
		assertTrue(config.startsWith().isPresent());
		assertTrue(config.startsWith().get().getSecond());
	}
	
	@Test
	void withStartsWithAny() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withStartsWithAny(List.of("www.", "api."));
		
		assertTrue(config.startsWithAny().isPresent());
		assertEquals(2, config.startsWithAny().get().getFirst().size());
		assertFalse(config.startsWithAny().get().getSecond());
	}
	
	@Test
	void withContains() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withContains("example");
		
		assertTrue(config.contains().isPresent());
		assertEquals("example", config.contains().get().getFirst());
	}
	
	@Test
	void withEndsWith() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withEndsWith(".com");
		
		assertTrue(config.endsWith().isPresent());
		assertEquals(".com", config.endsWith().get().getFirst());
		assertFalse(config.endsWith().get().getSecond());
	}
	
	@Test
	void withNotEndsWith() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withNotEndsWith(".xyz");
		
		assertTrue(config.endsWith().isPresent());
		assertTrue(config.endsWith().get().getSecond());
	}
	
	@Test
	void withMatchesRegex() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withMatches("^[a-z]+\\.[a-z]+$");
		
		assertTrue(config.matches().isPresent());
		assertFalse(config.matches().get().getSecond());
	}
	
	@Test
	void withMatchesPattern() {
		Pattern pattern = Pattern.compile("^[a-z]+\\.[a-z]+$");
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withMatches(pattern);
		
		assertTrue(config.matches().isPresent());
	}
	
	@Test
	void withRootDomain() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withRootDomain();
		
		assertTrue(config.rootDomain().isPresent());
		assertTrue(config.subDomain().isEmpty());
	}
	
	@Test
	void withSubDomain() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withSubDomain();
		
		assertTrue(config.subDomain().isPresent());
		assertTrue(config.rootDomain().isEmpty());
	}
	
	@Test
	void withCustom() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withCustom(value -> Result.success());
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> DomainConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesUnconstrained() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED;
		
		assertTrue(config.matches("example.com").isSuccess());
		assertTrue(config.matches("sub.example.com").isSuccess());
		assertTrue(config.matches("any-string").isSuccess());
	}
	
	@Test
	void matchesWithNull() {
		assertThrows(NullPointerException.class, () -> DomainConstraintConfig.UNCONSTRAINED.matches(null));
	}
	
	@Test
	void matchesEqualTo() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withEqualTo("example.com");
		
		assertTrue(config.matches("example.com").isSuccess());
		assertTrue(config.matches("other.com").isError());
	}
	
	@Test
	void matchesNotEqualTo() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withNotEqualTo("blocked.com");
		
		assertTrue(config.matches("example.com").isSuccess());
		assertTrue(config.matches("blocked.com").isError());
	}
	
	@Test
	void matchesIn() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withIn(List.of("example.com", "test.com"));
		
		assertTrue(config.matches("example.com").isSuccess());
		assertTrue(config.matches("test.com").isSuccess());
		assertTrue(config.matches("other.com").isError());
	}
	
	@Test
	void matchesLengthConstraint() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withLengthBetween(5, 15);
		
		assertTrue(config.matches("test.com").isSuccess());
		assertTrue(config.matches("example.com").isSuccess());
		assertTrue(config.matches("a.io").isError());
	}
	
	@Test
	void matchesStartsWith() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withStartsWith("www.");
		
		assertTrue(config.matches("www.example.com").isSuccess());
		assertTrue(config.matches("example.com").isError());
	}
	
	@Test
	void matchesEndsWith() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withEndsWith(".com");
		
		assertTrue(config.matches("example.com").isSuccess());
		assertTrue(config.matches("example.org").isError());
	}
	
	@Test
	void matchesContains() {
		DomainConstraintConfig config = DomainConstraintConfig.UNCONSTRAINED.withContains("example");
		
		assertTrue(config.matches("example.com").isSuccess());
		assertTrue(config.matches("sub.example.org").isSuccess());
		assertTrue(config.matches("test.com").isError());
	}
}
