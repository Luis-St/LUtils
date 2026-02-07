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

package net.luis.utils.io.codec.constraint.config.collection;

import net.luis.utils.io.codec.constraint.config.SizeConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SetConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class SetConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new SetConstraintConfig<String>(
			null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new SetConstraintConfig<String>(
			Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullSize() {
		assertThrows(NullPointerException.class, () -> new SetConstraintConfig<String>(
			Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new SetConstraintConfig<String>(
			Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new SetConstraintConfig<String>(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> SetConstraintConfig.<String>unconstrained().withMinSize(-1));
	}
	
	@Test
	void constructWithNegativeMax() {
		assertThrows(IllegalArgumentException.class, () -> SetConstraintConfig.<String>unconstrained().withMaxSize(-1));
	}
	
	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> SetConstraintConfig.<String>unconstrained().withSizeBetween(10, 5));
	}
	
	@Test
	void unconstrained() {
		SetConstraintConfig<String> config = SetConstraintConfig.unconstrained();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.size().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertDoesNotThrow(() -> config.validate(Set.of("a", "b")));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(SetConstraintConfig.<String>unconstrained().isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withMinSize(1);
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		Set<String> set = Set.of("a", "b");
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withEqualTo(set);
		assertTrue(config.equalTo().isPresent());
		assertEquals(set, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> SetConstraintConfig.<String>unconstrained().withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		Set<String> set = Set.of("a", "b");
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withNotEqualTo(set);
		assertTrue(config.equalTo().isPresent());
		assertEquals(set, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> SetConstraintConfig.<String>unconstrained().withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		List<Set<String>> sets = List.of(Set.of("a"), Set.of("b"));
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withIn(sets);
		assertTrue(config.in().isPresent());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> SetConstraintConfig.<String>unconstrained().withIn(null));
	}
	
	@Test
	void withNotIn() {
		List<Set<String>> sets = List.of(Set.of("a"), Set.of("b"));
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withNotIn(sets);
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> SetConstraintConfig.<String>unconstrained().withNotIn(null));
	}
	
	@Test
	void withMinSize() {
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withMinSize(5);
		assertTrue(config.size().isPresent());
		assertTrue(config.size().get().min().isPresent());
		assertEquals(5, config.size().get().min().get().getFirst());
		assertTrue(config.size().get().min().get().getSecond());
	}
	
	@Test
	void withMaxSize() {
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withMaxSize(10);
		assertTrue(config.size().isPresent());
		assertTrue(config.size().get().max().isPresent());
		assertEquals(10, config.size().get().max().get().getFirst());
		assertTrue(config.size().get().max().get().getSecond());
	}
	
	@Test
	void withExactSize() {
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withExactSize(7);
		assertTrue(config.size().isPresent());
		assertTrue(config.size().get().min().isPresent());
		assertTrue(config.size().get().max().isPresent());
		assertEquals(7, config.size().get().min().get().getFirst());
		assertEquals(7, config.size().get().max().get().getFirst());
	}
	
	@Test
	void withSizeBetween() {
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withSizeBetween(3, 8);
		assertTrue(config.size().isPresent());
		assertTrue(config.size().get().min().isPresent());
		assertTrue(config.size().get().max().isPresent());
		assertEquals(3, config.size().get().min().get().getFirst());
		assertEquals(8, config.size().get().max().get().getFirst());
	}
	
	@Test
	void withSize() {
		SizeConstraintConfig sizeConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(2).withMaxSize(10);
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withSize(sizeConfig);
		assertTrue(config.size().isPresent());
		assertEquals(sizeConfig, config.size().get());
		assertTrue(config.size().get().min().isPresent());
		assertTrue(config.size().get().max().isPresent());
		assertEquals(2, config.size().get().min().get().getFirst());
		assertEquals(10, config.size().get().max().get().getFirst());
	}
	
	@Test
	void withSizeNull() {
		assertThrows(NullPointerException.class, () -> SetConstraintConfig.<String>unconstrained().withSize(null));
	}
	
	@Test
	void withCustom() {
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withCustom(set -> {
			if (set.isEmpty()) throw new ConstraintViolateException("Set must not be empty");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> SetConstraintConfig.<String>unconstrained().withCustom(null));
	}
	
	@Test
	void validateWithEqualTo() {
		Set<String> set = Set.of("a", "b");
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withEqualTo(set);
		assertDoesNotThrow(() -> config.validate(Set.of("a", "b")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Set.of("c", "d")));
	}
	
	@Test
	void validateWithNotEqualTo() {
		Set<String> set = Set.of("a", "b");
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withNotEqualTo(set);
		assertDoesNotThrow(() -> config.validate(Set.of("c", "d")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Set.of("a", "b")));
	}
	
	@Test
	void validateWithIn() {
		List<Set<String>> sets = List.of(Set.of("a"), Set.of("b"));
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withIn(sets);
		assertDoesNotThrow(() -> config.validate(Set.of("a")));
		assertDoesNotThrow(() -> config.validate(Set.of("b")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Set.of("c")));
	}
	
	@Test
	void validateWithNotIn() {
		List<Set<String>> sets = List.of(Set.of("a"), Set.of("b"));
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withNotIn(sets);
		assertDoesNotThrow(() -> config.validate(Set.of("c")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Set.of("a")));
	}
	
	@Test
	void validateWithMinSize() {
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withMinSize(2);
		assertDoesNotThrow(() -> config.validate(Set.of("a", "b")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Set.of("a")));
	}
	
	@Test
	void validateWithMaxSize() {
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withMaxSize(2);
		assertDoesNotThrow(() -> config.validate(Set.of("a", "b")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Set.of("a", "b", "c")));
	}
	
	@Test
	void validateWithExactSize() {
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withExactSize(2);
		assertDoesNotThrow(() -> config.validate(Set.of("a", "b")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Set.of("a")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Set.of("a", "b", "c")));
	}
	
	@Test
	void validateWithSize() {
		SizeConstraintConfig sizeConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(1).withMaxSize(3);
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained().withSize(sizeConfig);
		assertDoesNotThrow(() -> config.validate(Set.of("a")));
		assertDoesNotThrow(() -> config.validate(Set.of("a", "b", "c")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Set.of()));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Set.of("a", "b", "c", "d")));
	}
	
	@Test
	void validateWithMultipleConstraints() {
		SetConstraintConfig<String> config = SetConstraintConfig.<String>unconstrained()
			.withMinSize(1)
			.withMaxSize(3);
		
		assertDoesNotThrow(() -> config.validate(Set.of("a", "b")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Set.of()));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Set.of("a", "b", "c", "d")));
	}
	
	@Test
	void validateWithNullValue() {
		SetConstraintConfig<String> config = SetConstraintConfig.unconstrained();
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
