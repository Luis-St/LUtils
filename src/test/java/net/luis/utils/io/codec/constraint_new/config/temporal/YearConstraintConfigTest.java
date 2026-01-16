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

package net.luis.utils.io.codec.constraint_new.config.temporal;

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YearConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class YearConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new YearConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new YearConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMin() {
		assertThrows(NullPointerException.class, () -> new YearConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMax() {
		assertThrows(NullPointerException.class, () -> new YearConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new YearConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new YearConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(Year.of(2024)).isSuccess());
	}
	
	@Test
	void withEqualTo() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withEqualTo(Year.of(2024));
		assertTrue(config.equalTo().isPresent());
		assertEquals(Year.of(2024), config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withNotEqualTo(Year.of(2024));
		assertTrue(config.equalTo().isPresent());
		assertEquals(Year.of(2024), config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withIn(List.of(Year.of(2020), Year.of(2024)));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(Year.of(2020), Year.of(2024)), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withNotIn(List.of(Year.of(2020), Year.of(2024)));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(Year.of(2020), Year.of(2024)), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withAfter() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withAfter(Year.of(2020));
		assertTrue(config.min().isPresent());
		assertEquals(Year.of(2020), config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
	}
	
	@Test
	void withAfterNull() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withAfter(null));
	}
	
	@Test
	void withAfterOrEqual() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withAfterOrEqual(Year.of(2020));
		assertTrue(config.min().isPresent());
		assertEquals(Year.of(2020), config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
	}
	
	@Test
	void withAfterOrEqualNull() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withAfterOrEqual(null));
	}
	
	@Test
	void withBefore() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withBefore(Year.of(2030));
		assertTrue(config.max().isPresent());
		assertEquals(Year.of(2030), config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withBeforeNull() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withBefore(null));
	}
	
	@Test
	void withBeforeOrEqual() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(Year.of(2030));
		assertTrue(config.max().isPresent());
		assertEquals(Year.of(2030), config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withBeforeOrEqualNull() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(null));
	}
	
	@Test
	void withBetween() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withBetween(Year.of(2020), Year.of(2030));
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(Year.of(2020), config.min().get().getFirst());
		assertEquals(Year.of(2030), config.max().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withBetweenNullMin() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withBetween(null, Year.of(2030)));
	}
	
	@Test
	void withBetweenNullMax() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withBetween(Year.of(2020), null));
	}
	
	@Test
	void withBetweenOrEqual() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(Year.of(2020), Year.of(2030));
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(Year.of(2020), config.min().get().getFirst());
		assertEquals(Year.of(2030), config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqualNullMin() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(null, Year.of(2030)));
	}
	
	@Test
	void withBetweenOrEqualNullMax() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(Year.of(2020), null));
	}
	
	@Test
	void withCustom() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withCustom(year -> year.isLeap() ? Result.success() : Result.error("Year must be a leap year"));
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> YearConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesWithEqualTo() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withEqualTo(Year.of(2024));
		assertTrue(config.matches(Year.of(2024)).isSuccess());
		assertTrue(config.matches(Year.of(2023)).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withNotEqualTo(Year.of(2024));
		assertTrue(config.matches(Year.of(2023)).isSuccess());
		assertTrue(config.matches(Year.of(2024)).isError());
	}
	
	@Test
	void matchesWithIn() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withIn(List.of(Year.of(2020), Year.of(2024)));
		assertTrue(config.matches(Year.of(2020)).isSuccess());
		assertTrue(config.matches(Year.of(2024)).isSuccess());
		assertTrue(config.matches(Year.of(2022)).isError());
	}
	
	@Test
	void matchesWithNotIn() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withNotIn(List.of(Year.of(2020), Year.of(2024)));
		assertTrue(config.matches(Year.of(2022)).isSuccess());
		assertTrue(config.matches(Year.of(2020)).isError());
		assertTrue(config.matches(Year.of(2024)).isError());
	}
	
	@Test
	void matchesWithAfter() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withAfter(Year.of(2020));
		assertTrue(config.matches(Year.of(2021)).isSuccess());
		assertTrue(config.matches(Year.of(2020)).isError());
		assertTrue(config.matches(Year.of(2019)).isError());
	}
	
	@Test
	void matchesWithAfterOrEqual() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withAfterOrEqual(Year.of(2020));
		assertTrue(config.matches(Year.of(2020)).isSuccess());
		assertTrue(config.matches(Year.of(2021)).isSuccess());
		assertTrue(config.matches(Year.of(2019)).isError());
	}
	
	@Test
	void matchesWithBefore() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withBefore(Year.of(2030));
		assertTrue(config.matches(Year.of(2029)).isSuccess());
		assertTrue(config.matches(Year.of(2030)).isError());
		assertTrue(config.matches(Year.of(2031)).isError());
	}
	
	@Test
	void matchesWithBeforeOrEqual() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(Year.of(2030));
		assertTrue(config.matches(Year.of(2030)).isSuccess());
		assertTrue(config.matches(Year.of(2029)).isSuccess());
		assertTrue(config.matches(Year.of(2031)).isError());
	}
	
	@Test
	void matchesWithBetween() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withBetween(Year.of(2020), Year.of(2030));
		assertTrue(config.matches(Year.of(2025)).isSuccess());
		assertTrue(config.matches(Year.of(2020)).isError());
		assertTrue(config.matches(Year.of(2030)).isError());
	}
	
	@Test
	void matchesWithBetweenOrEqual() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(Year.of(2020), Year.of(2030));
		assertTrue(config.matches(Year.of(2020)).isSuccess());
		assertTrue(config.matches(Year.of(2030)).isSuccess());
		assertTrue(config.matches(Year.of(2025)).isSuccess());
		assertTrue(config.matches(Year.of(2019)).isError());
		assertTrue(config.matches(Year.of(2031)).isError());
	}
	
	@Test
	void matchesWithCustomLeapYear() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withCustom(year -> year.isLeap() ? Result.success() : Result.error("Year must be a leap year"));
		assertTrue(config.matches(Year.of(2024)).isSuccess());
		assertTrue(config.matches(Year.of(2020)).isSuccess());
		assertTrue(config.matches(Year.of(2023)).isError());
		assertTrue(config.matches(Year.of(2025)).isError());
	}
	
	@Test
	void matchesWithMultipleConstraints() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED
			.withAfterOrEqual(Year.of(2000))
			.withBeforeOrEqual(Year.of(2100))
			.withNotIn(List.of(Year.of(2050)));
		
		assertTrue(config.matches(Year.of(2000)).isSuccess());
		assertTrue(config.matches(Year.of(2100)).isSuccess());
		assertTrue(config.matches(Year.of(2049)).isSuccess());
		assertTrue(config.matches(Year.of(2050)).isError());
		assertTrue(config.matches(Year.of(1999)).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
