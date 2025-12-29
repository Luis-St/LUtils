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
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InstantConstraintConfigTest {

	// Constructor Tests

	@Test
	void constructorNullConfigThrows() {
		assertThrows(NullPointerException.class, () -> new InstantConstraintConfig(null));
	}

	@Test
	void constructorValidConfig() {
		InstantConstraintConfig config = new InstantConstraintConfig(TemporalConstraintConfig.unconstrained());
		assertNotNull(config);
		assertNotNull(config.config());
	}

	// UNCONSTRAINED Tests

	@Test
	void unconstrainedConstant() {
		assertNotNull(InstantConstraintConfig.UNCONSTRAINED);
		assertTrue(InstantConstraintConfig.UNCONSTRAINED.config().isUnconstrained());
	}

	// isUnconstrained Tests

	@Test
	void isUnconstrainedForConstant() {
		assertTrue(InstantConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedForNew() {
		InstantConstraintConfig config = new InstantConstraintConfig(TemporalConstraintConfig.unconstrained());
		assertTrue(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithConstraint() {
		Instant now = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withMin(now, true);
		assertFalse(config.isUnconstrained());
	}

	// Builder Method Tests - withEquals

	@Test
	void withEquals() {
		Instant value = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withEquals(value, false);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withEqualsNegated() {
		Instant value = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withEquals(value, true);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Builder Method Tests - withMin

	@Test
	void withMinInclusive() {
		Instant min = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withMin(min, true);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMinExclusive() {
		Instant min = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withMin(min, false);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Builder Method Tests - withMax

	@Test
	void withMaxInclusive() {
		Instant max = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withMax(max, true);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMaxExclusive() {
		Instant max = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withMax(max, false);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Builder Method Tests - withRange

	@Test
	void withRangeInclusive() {
		Instant min = Instant.parse("2024-06-15T10:30:00Z");
		Instant max = Instant.parse("2024-06-20T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withRange(min, max, true);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withRangeExclusive() {
		Instant min = Instant.parse("2024-06-15T10:30:00Z");
		Instant max = Instant.parse("2024-06-20T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withRange(min, max, false);

		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Unsupported Operations Tests

	@Test
	void withWithinLastThrows() {
		assertThrows(UnsupportedOperationException.class, () ->
			InstantConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7))
		);
	}

	@Test
	void withWithinNextThrows() {
		assertThrows(UnsupportedOperationException.class, () ->
			InstantConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(7))
		);
	}

	// matches Tests

	@Test
	void matchesUnconstrained() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED;
		assertTrue(config.matches(Instant.parse("2024-06-15T10:30:00Z")).isSuccess());
		assertTrue(config.matches(Instant.parse("2020-01-01T00:00:00Z")).isSuccess());
		assertTrue(config.matches(Instant.parse("2030-12-31T23:59:59Z")).isSuccess());
	}

	@Test
	void matchesEqualsSuccess() {
		Instant value = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withEquals(value, false);

		assertTrue(config.matches(value).isSuccess());
	}

	@Test
	void matchesEqualsFailure() {
		Instant value = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withEquals(value, false);

		Result<Void> result = config.matches(Instant.parse("2024-06-15T10:31:00Z"));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinInclusiveSuccess() {
		Instant min = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withMin(min, true);

		assertTrue(config.matches(min).isSuccess());
		assertTrue(config.matches(Instant.parse("2024-06-15T10:31:00Z")).isSuccess());
		assertTrue(config.matches(Instant.parse("2024-06-20T00:00:00Z")).isSuccess());
	}

	@Test
	void matchesMinInclusiveFailure() {
		Instant min = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withMin(min, true);

		Result<Void> result = config.matches(Instant.parse("2024-06-15T10:29:00Z"));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinExclusiveSuccess() {
		Instant min = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withMin(min, false);

		assertTrue(config.matches(Instant.parse("2024-06-15T10:31:00Z")).isSuccess());
		assertTrue(config.matches(Instant.parse("2024-06-20T00:00:00Z")).isSuccess());
	}

	@Test
	void matchesMinExclusiveFailure() {
		Instant min = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withMin(min, false);

		Result<Void> result = config.matches(min);
		assertTrue(result.isError());
	}

	@Test
	void matchesMaxInclusiveSuccess() {
		Instant max = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withMax(max, true);

		assertTrue(config.matches(max).isSuccess());
		assertTrue(config.matches(Instant.parse("2024-06-15T10:29:00Z")).isSuccess());
		assertTrue(config.matches(Instant.parse("2024-06-10T00:00:00Z")).isSuccess());
	}

	@Test
	void matchesMaxInclusiveFailure() {
		Instant max = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withMax(max, true);

		Result<Void> result = config.matches(Instant.parse("2024-06-15T10:31:00Z"));
		assertTrue(result.isError());
	}

	@Test
	void matchesRangeSuccess() {
		Instant min = Instant.parse("2024-06-15T10:30:00Z");
		Instant max = Instant.parse("2024-06-20T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withRange(min, max, true);

		assertTrue(config.matches(min).isSuccess());
		assertTrue(config.matches(Instant.parse("2024-06-17T12:00:00Z")).isSuccess());
		assertTrue(config.matches(max).isSuccess());
	}

	@Test
	void matchesRangeFailure() {
		Instant min = Instant.parse("2024-06-15T10:30:00Z");
		Instant max = Instant.parse("2024-06-20T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withRange(min, max, true);

		Result<Void> result = config.matches(Instant.parse("2024-06-25T00:00:00Z"));
		assertTrue(result.isError());
	}

	// toString Tests

	@Test
	void toStringUnconstrained() {
		assertEquals("InstantConstraintConfig[unconstrained]", InstantConstraintConfig.UNCONSTRAINED.toString());
	}

	@Test
	void toStringWithConstraint() {
		Instant value = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		String str = config.toString();
		assertTrue(str.contains("InstantConstraintConfig"));
		assertTrue(str.contains("equals"));
	}

	// Equality Tests

	@Test
	void equalsSameInstance() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED;
		assertEquals(config, config);
	}

	@Test
	void equalsSameValues() {
		Instant value = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config1 = InstantConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		InstantConstraintConfig config2 = InstantConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1, config2);
	}

	@Test
	void notEqualsDifferentConstraint() {
		Instant value = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config1 = InstantConstraintConfig.UNCONSTRAINED.withMin(value, true);
		InstantConstraintConfig config2 = InstantConstraintConfig.UNCONSTRAINED.withMax(value, true);
		assertNotEquals(config1, config2);
	}

	@Test
	void hashCodeConsistency() {
		Instant value = Instant.parse("2024-06-15T10:30:00Z");
		InstantConstraintConfig config1 = InstantConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		InstantConstraintConfig config2 = InstantConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
