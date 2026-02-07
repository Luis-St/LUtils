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

package net.luis.utils.io.codec.constraint.config.validator;

import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TemporalValidators}.<br>
 *
 * @author Luis-St
 */
class TemporalValidatorsTest {
	
	@Test
	void validateDurationSignWithAllEmpty() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void validateDurationSignWithPositive() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.of(false), Optional.empty(), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateDurationSign(Duration.ofHours(-1), Optional.of(false), Optional.empty(), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be positive"));
	}
	
	@Test
	void validateDurationSignWithNegative() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationSign(Duration.ofHours(-1), Optional.empty(), Optional.of(false), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.empty(), Optional.of(false), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be negative"));
	}
	
	@Test
	void validateDurationSignWithZero() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationSign(Duration.ZERO, Optional.empty(), Optional.empty(), Optional.of(false)));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.empty(), Optional.empty(), Optional.of(false)));
		assertTrue(exception.getMessage().contains("must be zero"));
	}
	
	@Test
	void validateDurationSignWithNonPositive() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationSign(Duration.ofHours(-1), Optional.of(true), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> TemporalValidators.validateDurationSign(Duration.ZERO, Optional.of(true), Optional.empty(), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.of(true), Optional.empty(), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be non-positive"));
	}
	
	@Test
	void validateDurationSignWithNonNegative() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.empty(), Optional.of(true), Optional.empty()));
		assertDoesNotThrow(() -> TemporalValidators.validateDurationSign(Duration.ZERO, Optional.empty(), Optional.of(true), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateDurationSign(Duration.ofHours(-1), Optional.empty(), Optional.of(true), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be non-negative"));
	}
	
	@Test
	void validateDurationSignWithNonZero() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.empty(), Optional.empty(), Optional.of(true)));
		assertDoesNotThrow(() -> TemporalValidators.validateDurationSign(Duration.ofHours(-1), Optional.empty(), Optional.empty(), Optional.of(true)));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateDurationSign(Duration.ZERO, Optional.empty(), Optional.empty(), Optional.of(true)));
		assertTrue(exception.getMessage().contains("must be non-zero"));
	}
	
	@Test
	void validateDurationSignWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateDurationSign(null, Optional.empty(), Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateDurationSign(Duration.ZERO, null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateDurationSign(Duration.ZERO, Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateDurationSign(Duration.ZERO, Optional.empty(), Optional.empty(), null));
	}
	
	@Test
	void validateDurationWithinLastWithEmptyOptional() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationWithinLast(Duration.ofHours(-5), Optional.empty()));
	}
	
	@Test
	void validateDurationWithinLastWithPass() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationWithinLast(Duration.ofMinutes(-30), Optional.of(Duration.ofHours(1))));
	}
	
	@Test
	void validateDurationWithinLastWithFail() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateDurationWithinLast(Duration.ofHours(-2), Optional.of(Duration.ofHours(1))));
		assertTrue(exception.getMessage().contains("must be within last"));
	}
	
	@Test
	void validateDurationWithinLastWithBoundary() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationWithinLast(Duration.ofHours(-1), Optional.of(Duration.ofHours(1))));
	}
	
	@Test
	void validateDurationWithinLastWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateDurationWithinLast(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateDurationWithinLast(Duration.ZERO, null));
	}
	
	@Test
	void validateDurationWithinNextWithEmptyOptional() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationWithinNext(Duration.ofHours(5), Optional.empty()));
	}
	
	@Test
	void validateDurationWithinNextWithPass() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationWithinNext(Duration.ofMinutes(30), Optional.of(Duration.ofHours(1))));
	}
	
	@Test
	void validateDurationWithinNextWithFail() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateDurationWithinNext(Duration.ofHours(2), Optional.of(Duration.ofHours(1))));
		assertTrue(exception.getMessage().contains("must be within next"));
	}
	
	@Test
	void validateDurationWithinNextWithBoundary() {
		assertDoesNotThrow(() -> TemporalValidators.validateDurationWithinNext(Duration.ofHours(1), Optional.of(Duration.ofHours(1))));
	}
	
	@Test
	void validateDurationWithinNextWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateDurationWithinNext(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateDurationWithinNext(Duration.ZERO, null));
	}
	
	@Test
	void validatePeriodSignWithAllEmpty() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void validatePeriodSignWithPositive() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.of(false), Optional.empty(), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validatePeriodSign(Period.ofMonths(-1), Optional.of(false), Optional.empty(), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be positive"));
	}
	
	@Test
	void validatePeriodSignWithNegative() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodSign(Period.ofMonths(-1), Optional.empty(), Optional.of(false), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.empty(), Optional.of(false), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be negative"));
	}
	
	@Test
	void validatePeriodSignWithZero() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodSign(Period.ZERO, Optional.empty(), Optional.empty(), Optional.of(false)));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.empty(), Optional.empty(), Optional.of(false)));
		assertTrue(exception.getMessage().contains("must be zero"));
	}
	
	@Test
	void validatePeriodSignWithNonPositive() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodSign(Period.ofMonths(-1), Optional.of(true), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodSign(Period.ZERO, Optional.of(true), Optional.empty(), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.of(true), Optional.empty(), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be non-positive"));
	}
	
	@Test
	void validatePeriodSignWithNonNegative() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.empty(), Optional.of(true), Optional.empty()));
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodSign(Period.ZERO, Optional.empty(), Optional.of(true), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validatePeriodSign(Period.ofMonths(-1), Optional.empty(), Optional.of(true), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be non-negative"));
	}
	
	@Test
	void validatePeriodSignWithNonZero() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.empty(), Optional.empty(), Optional.of(true)));
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodSign(Period.ofMonths(-1), Optional.empty(), Optional.empty(), Optional.of(true)));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validatePeriodSign(Period.ZERO, Optional.empty(), Optional.empty(), Optional.of(true)));
		assertTrue(exception.getMessage().contains("must be non-zero"));
	}
	
	@Test
	void validatePeriodSignWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalValidators.validatePeriodSign(null, Optional.empty(), Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validatePeriodSign(Period.ZERO, null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validatePeriodSign(Period.ZERO, Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validatePeriodSign(Period.ZERO, Optional.empty(), Optional.empty(), null));
	}
	
	@Test
	void validatePeriodRangeWithAllEmpty() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodRange(Period.ofMonths(6), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void validatePeriodRangeWithMinOnly() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodRange(Period.ofMonths(6), Optional.of(Pair.of(Period.ofMonths(3), true)), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validatePeriodRange(Period.ofMonths(2), Optional.of(Pair.of(Period.ofMonths(3), true)), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be greater than or equal to"));
	}
	
	@Test
	void validatePeriodRangeWithMaxOnly() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodRange(Period.ofMonths(6), Optional.empty(), Optional.of(Pair.of(Period.ofMonths(12), true))));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validatePeriodRange(Period.ofMonths(15), Optional.empty(), Optional.of(Pair.of(Period.ofMonths(12), true))));
		assertTrue(exception.getMessage().contains("must be less than or equal to"));
	}
	
	@Test
	void validatePeriodRangeWithBoth() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodRange(Period.ofMonths(6), Optional.of(Pair.of(Period.ofMonths(3), true)), Optional.of(Pair.of(Period.ofMonths(12), true))));
	}
	
	@Test
	void validatePeriodRangeWithInclusiveBounds() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodRange(Period.ofMonths(3), Optional.of(Pair.of(Period.ofMonths(3), true)), Optional.empty()));
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodRange(Period.ofMonths(12), Optional.empty(), Optional.of(Pair.of(Period.ofMonths(12), true))));
	}
	
	@Test
	void validatePeriodRangeWithExclusiveBounds() {
		ConstraintViolateException minException = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validatePeriodRange(Period.ofMonths(3), Optional.of(Pair.of(Period.ofMonths(3), false)), Optional.empty()));
		assertTrue(minException.getMessage().contains("must be greater than"));
		ConstraintViolateException maxException = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validatePeriodRange(Period.ofMonths(12), Optional.empty(), Optional.of(Pair.of(Period.ofMonths(12), false))));
		assertTrue(maxException.getMessage().contains("must be less than"));
	}
	
	@Test
	void validatePeriodRangeWithDays() {
		assertDoesNotThrow(() -> TemporalValidators.validatePeriodRange(Period.ofDays(15), Optional.of(Pair.of(Period.ofDays(10), true)), Optional.of(Pair.of(Period.ofDays(20), true))));
	}
	
	@Test
	void validatePeriodRangeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalValidators.validatePeriodRange(null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validatePeriodRange(Period.ZERO, null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validatePeriodRange(Period.ZERO, Optional.empty(), null));
	}
	
	@Test
	void validateZoneOffsetSignWithAllEmpty() {
		assertDoesNotThrow(() -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void validateZoneOffsetSignWithPositive() {
		assertDoesNotThrow(() -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.of(false), Optional.empty(), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.of(false), Optional.empty(), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be positive"));
	}
	
	@Test
	void validateZoneOffsetSignWithNegative() {
		assertDoesNotThrow(() -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.empty(), Optional.of(false), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.of(false), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be negative"));
	}
	
	@Test
	void validateZoneOffsetSignWithZero() {
		assertDoesNotThrow(() -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), Optional.empty(), Optional.of(false)));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.empty(), Optional.of(false)));
		assertTrue(exception.getMessage().contains("must be zero"));
	}
	
	@Test
	void validateZoneOffsetSignWithNonPositive() {
		assertDoesNotThrow(() -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.of(true), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, Optional.of(true), Optional.empty(), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.of(true), Optional.empty(), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be non-positive"));
	}
	
	@Test
	void validateZoneOffsetSignWithNonNegative() {
		assertDoesNotThrow(() -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.of(true), Optional.empty()));
		assertDoesNotThrow(() -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), Optional.of(true), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.empty(), Optional.of(true), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be non-negative"));
	}
	
	@Test
	void validateZoneOffsetSignWithNonZero() {
		assertDoesNotThrow(() -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.empty(), Optional.of(true)));
		assertDoesNotThrow(() -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.empty(), Optional.empty(), Optional.of(true)));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), Optional.empty(), Optional.of(true)));
		assertTrue(exception.getMessage().contains("must be non-zero"));
	}
	
	@Test
	void validateZoneOffsetSignWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateZoneOffsetSign(null, Optional.empty(), Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), Optional.empty(), null));
	}
}
