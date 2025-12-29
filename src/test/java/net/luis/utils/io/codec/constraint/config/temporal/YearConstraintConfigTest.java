/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.codec.constraint.config.temporal;

import net.luis.utils.io.codec.constraint.config.temporal.core.TemporalConstraintConfig;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class YearConstraintConfigTest {

	// Constructor Tests

	@Test
	void constructorNullConfigThrows() {
		assertThrows(NullPointerException.class, () -> new YearConstraintConfig(null));
	}

	@Test
	void constructorValidConfig() {
		YearConstraintConfig config = new YearConstraintConfig(TemporalConstraintConfig.unconstrained());
		assertNotNull(config);
		assertNotNull(config.config());
	}

	// UNCONSTRAINED Tests

	@Test
	void unconstrainedConstant() {
		assertNotNull(YearConstraintConfig.UNCONSTRAINED);
		assertTrue(YearConstraintConfig.UNCONSTRAINED.config().isUnconstrained());
	}

	// isUnconstrained Tests

	@Test
	void isUnconstrainedForConstant() {
		assertTrue(YearConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedForNew() {
		YearConstraintConfig config = new YearConstraintConfig(TemporalConstraintConfig.unconstrained());
		assertTrue(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithConstraint() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withMin(Year.of(2020), true);
		assertFalse(config.isUnconstrained());
	}

	// Builder Method Tests - withEquals

	@Test
	void withEquals() {
		Year value = Year.of(2024);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withEquals(value, false);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withEqualsNegated() {
		Year value = Year.of(2024);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withEquals(value, true);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Builder Method Tests - withMin

	@Test
	void withMinInclusive() {
		Year min = Year.of(2020);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withMin(min, true);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMinExclusive() {
		Year min = Year.of(2020);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withMin(min, false);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Builder Method Tests - withMax

	@Test
	void withMaxInclusive() {
		Year max = Year.of(2030);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withMax(max, true);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMaxExclusive() {
		Year max = Year.of(2030);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withMax(max, false);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Builder Method Tests - withRange

	@Test
	void withRangeInclusive() {
		Year min = Year.of(2020);
		Year max = Year.of(2030);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withRange(min, max, true);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withRangeExclusive() {
		Year min = Year.of(2020);
		Year max = Year.of(2030);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withRange(min, max, false);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Unsupported Operations Tests

	@Test
	void withWithinLastThrows() {
		assertThrows(UnsupportedOperationException.class, () ->
			YearConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(365))
		);
	}

	@Test
	void withWithinNextThrows() {
		assertThrows(UnsupportedOperationException.class, () ->
			YearConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(365))
		);
	}

	// matches Tests

	@Test
	void matchesUnconstrained() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED;
		assertTrue(config.matches(Year.of(2024)).isSuccess());
		assertTrue(config.matches(Year.of(2000)).isSuccess());
		assertTrue(config.matches(Year.of(2100)).isSuccess());
	}

	@Test
	void matchesEqualsSuccess() {
		Year value = Year.of(2024);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withEquals(value, false);

		assertTrue(config.matches(value).isSuccess());
	}

	@Test
	void matchesEqualsFailure() {
		Year value = Year.of(2024);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withEquals(value, false);

		Result<Void> result = config.matches(Year.of(2025));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinInclusiveSuccess() {
		Year min = Year.of(2020);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withMin(min, true);

		assertTrue(config.matches(min).isSuccess());
		assertTrue(config.matches(Year.of(2021)).isSuccess());
		assertTrue(config.matches(Year.of(2030)).isSuccess());
	}

	@Test
	void matchesMinInclusiveFailure() {
		Year min = Year.of(2020);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withMin(min, true);

		Result<Void> result = config.matches(Year.of(2019));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinExclusiveSuccess() {
		Year min = Year.of(2020);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withMin(min, false);

		assertTrue(config.matches(Year.of(2021)).isSuccess());
		assertTrue(config.matches(Year.of(2030)).isSuccess());
	}

	@Test
	void matchesMinExclusiveFailure() {
		Year min = Year.of(2020);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withMin(min, false);

		Result<Void> result = config.matches(min);
		assertTrue(result.isError());
	}

	@Test
	void matchesMaxInclusiveSuccess() {
		Year max = Year.of(2030);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withMax(max, true);

		assertTrue(config.matches(max).isSuccess());
		assertTrue(config.matches(Year.of(2029)).isSuccess());
		assertTrue(config.matches(Year.of(2020)).isSuccess());
	}

	@Test
	void matchesMaxInclusiveFailure() {
		Year max = Year.of(2030);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withMax(max, true);

		Result<Void> result = config.matches(Year.of(2031));
		assertTrue(result.isError());
	}

	@Test
	void matchesRangeSuccess() {
		Year min = Year.of(2020);
		Year max = Year.of(2030);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withRange(min, max, true);

		assertTrue(config.matches(min).isSuccess());
		assertTrue(config.matches(Year.of(2025)).isSuccess());
		assertTrue(config.matches(max).isSuccess());
	}

	@Test
	void matchesRangeFailure() {
		Year min = Year.of(2020);
		Year max = Year.of(2030);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withRange(min, max, true);

		Result<Void> result = config.matches(Year.of(2035));
		assertTrue(result.isError());
	}

	// toString Tests

	@Test
	void toStringUnconstrained() {
		assertEquals("YearConstraintConfig[unconstrained]", YearConstraintConfig.UNCONSTRAINED.toString());
	}

	@Test
	void toStringWithConstraint() {
		Year value = Year.of(2024);
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		String str = config.toString();
		assertTrue(str.contains("YearConstraintConfig"));
		assertTrue(str.contains("equals"));
	}

	// Equality Tests

	@Test
	void equalsSameInstance() {
		YearConstraintConfig config = YearConstraintConfig.UNCONSTRAINED;
		assertEquals(config, config);
	}

	@Test
	void equalsSameValues() {
		Year value = Year.of(2024);
		YearConstraintConfig config1 = YearConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		YearConstraintConfig config2 = YearConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1, config2);
	}

	@Test
	void notEqualsDifferentConstraint() {
		Year value = Year.of(2024);
		YearConstraintConfig config1 = YearConstraintConfig.UNCONSTRAINED.withMin(value, true);
		YearConstraintConfig config2 = YearConstraintConfig.UNCONSTRAINED.withMax(value, true);
		assertNotEquals(config1, config2);
	}

	@Test
	void hashCodeConsistency() {
		Year value = Year.of(2024);
		YearConstraintConfig config1 = YearConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		YearConstraintConfig config2 = YearConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
