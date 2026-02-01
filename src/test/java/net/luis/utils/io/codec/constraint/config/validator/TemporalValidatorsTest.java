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
import net.luis.utils.util.result.Result;
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
		Result<Void> result = TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.empty(), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateDurationSignWithPositive() {
		Result<Void> result = TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalValidators.validateDurationSign(Duration.ofHours(-1), Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be positive"));
	}
	
	@Test
	void validateDurationSignWithNegative() {
		Result<Void> result = TemporalValidators.validateDurationSign(Duration.ofHours(-1), Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be negative"));
	}
	
	@Test
	void validateDurationSignWithZero() {
		Result<Void> result = TemporalValidators.validateDurationSign(Duration.ZERO, Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be zero"));
	}
	
	@Test
	void validateDurationSignWithNonPositive() {
		Result<Void> result = TemporalValidators.validateDurationSign(Duration.ofHours(-1), Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = TemporalValidators.validateDurationSign(Duration.ZERO, Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-positive"));
	}
	
	@Test
	void validateDurationSignWithNonNegative() {
		Result<Void> result = TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = TemporalValidators.validateDurationSign(Duration.ZERO, Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = TemporalValidators.validateDurationSign(Duration.ofHours(-1), Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-negative"));
	}
	
	@Test
	void validateDurationSignWithNonZero() {
		Result<Void> result = TemporalValidators.validateDurationSign(Duration.ofHours(1), Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(result.isSuccess());
		Result<Void> negResult = TemporalValidators.validateDurationSign(Duration.ofHours(-1), Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(negResult.isSuccess());
		Result<Void> failResult = TemporalValidators.validateDurationSign(Duration.ZERO, Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-zero"));
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
		Result<Void> result = TemporalValidators.validateDurationWithinLast(Duration.ofHours(-5), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateDurationWithinLastWithPass() {
		Result<Void> result = TemporalValidators.validateDurationWithinLast(Duration.ofMinutes(-30), Optional.of(Duration.ofHours(1)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateDurationWithinLastWithFail() {
		Result<Void> result = TemporalValidators.validateDurationWithinLast(Duration.ofHours(-2), Optional.of(Duration.ofHours(1)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be within last"));
	}
	
	@Test
	void validateDurationWithinLastWithBoundary() {
		Result<Void> result = TemporalValidators.validateDurationWithinLast(Duration.ofHours(-1), Optional.of(Duration.ofHours(1)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateDurationWithinLastWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateDurationWithinLast(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateDurationWithinLast(Duration.ZERO, null));
	}
	
	@Test
	void validateDurationWithinNextWithEmptyOptional() {
		Result<Void> result = TemporalValidators.validateDurationWithinNext(Duration.ofHours(5), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateDurationWithinNextWithPass() {
		Result<Void> result = TemporalValidators.validateDurationWithinNext(Duration.ofMinutes(30), Optional.of(Duration.ofHours(1)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateDurationWithinNextWithFail() {
		Result<Void> result = TemporalValidators.validateDurationWithinNext(Duration.ofHours(2), Optional.of(Duration.ofHours(1)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be within next"));
	}
	
	@Test
	void validateDurationWithinNextWithBoundary() {
		Result<Void> result = TemporalValidators.validateDurationWithinNext(Duration.ofHours(1), Optional.of(Duration.ofHours(1)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateDurationWithinNextWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateDurationWithinNext(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateDurationWithinNext(Duration.ZERO, null));
	}
	
	@Test
	void validatePeriodSignWithAllEmpty() {
		Result<Void> result = TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.empty(), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePeriodSignWithPositive() {
		Result<Void> result = TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalValidators.validatePeriodSign(Period.ofMonths(-1), Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be positive"));
	}
	
	@Test
	void validatePeriodSignWithNegative() {
		Result<Void> result = TemporalValidators.validatePeriodSign(Period.ofMonths(-1), Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be negative"));
	}
	
	@Test
	void validatePeriodSignWithZero() {
		Result<Void> result = TemporalValidators.validatePeriodSign(Period.ZERO, Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be zero"));
	}
	
	@Test
	void validatePeriodSignWithNonPositive() {
		Result<Void> result = TemporalValidators.validatePeriodSign(Period.ofMonths(-1), Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = TemporalValidators.validatePeriodSign(Period.ZERO, Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-positive"));
	}
	
	@Test
	void validatePeriodSignWithNonNegative() {
		Result<Void> result = TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = TemporalValidators.validatePeriodSign(Period.ZERO, Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = TemporalValidators.validatePeriodSign(Period.ofMonths(-1), Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-negative"));
	}
	
	@Test
	void validatePeriodSignWithNonZero() {
		Result<Void> result = TemporalValidators.validatePeriodSign(Period.ofMonths(1), Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(result.isSuccess());
		Result<Void> negResult = TemporalValidators.validatePeriodSign(Period.ofMonths(-1), Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(negResult.isSuccess());
		Result<Void> failResult = TemporalValidators.validatePeriodSign(Period.ZERO, Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-zero"));
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
		Result<Void> result = TemporalValidators.validatePeriodRange(Period.ofMonths(6), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePeriodRangeWithMinOnly() {
		Result<Void> result = TemporalValidators.validatePeriodRange(Period.ofMonths(6), Optional.of(Pair.of(Period.ofMonths(3), true)), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalValidators.validatePeriodRange(Period.ofMonths(2), Optional.of(Pair.of(Period.ofMonths(3), true)), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be greater than or equal to"));
	}
	
	@Test
	void validatePeriodRangeWithMaxOnly() {
		Result<Void> result = TemporalValidators.validatePeriodRange(Period.ofMonths(6), Optional.empty(), Optional.of(Pair.of(Period.ofMonths(12), true)));
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalValidators.validatePeriodRange(Period.ofMonths(15), Optional.empty(), Optional.of(Pair.of(Period.ofMonths(12), true)));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be less than or equal to"));
	}
	
	@Test
	void validatePeriodRangeWithBoth() {
		Result<Void> result = TemporalValidators.validatePeriodRange(Period.ofMonths(6), Optional.of(Pair.of(Period.ofMonths(3), true)), Optional.of(Pair.of(Period.ofMonths(12), true)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePeriodRangeWithInclusiveBounds() {
		Result<Void> minResult = TemporalValidators.validatePeriodRange(Period.ofMonths(3), Optional.of(Pair.of(Period.ofMonths(3), true)), Optional.empty());
		assertTrue(minResult.isSuccess());
		Result<Void> maxResult = TemporalValidators.validatePeriodRange(Period.ofMonths(12), Optional.empty(), Optional.of(Pair.of(Period.ofMonths(12), true)));
		assertTrue(maxResult.isSuccess());
	}
	
	@Test
	void validatePeriodRangeWithExclusiveBounds() {
		Result<Void> minResult = TemporalValidators.validatePeriodRange(Period.ofMonths(3), Optional.of(Pair.of(Period.ofMonths(3), false)), Optional.empty());
		assertTrue(minResult.isError());
		assertTrue(minResult.errorOrThrow().contains("must be greater than"));
		Result<Void> maxResult = TemporalValidators.validatePeriodRange(Period.ofMonths(12), Optional.empty(), Optional.of(Pair.of(Period.ofMonths(12), false)));
		assertTrue(maxResult.isError());
		assertTrue(maxResult.errorOrThrow().contains("must be less than"));
	}
	
	@Test
	void validatePeriodRangeWithDays() {
		Result<Void> result = TemporalValidators.validatePeriodRange(Period.ofDays(15), Optional.of(Pair.of(Period.ofDays(10), true)), Optional.of(Pair.of(Period.ofDays(20), true)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validatePeriodRangeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalValidators.validatePeriodRange(null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validatePeriodRange(Period.ZERO, null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validatePeriodRange(Period.ZERO, Optional.empty(), null));
	}
	
	@Test
	void validateZoneOffsetSignWithAllEmpty() {
		Result<Void> result = TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateZoneOffsetSignWithPositive() {
		Result<Void> result = TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be positive"));
	}
	
	@Test
	void validateZoneOffsetSignWithNegative() {
		Result<Void> result = TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be negative"));
	}
	
	@Test
	void validateZoneOffsetSignWithZero() {
		Result<Void> result = TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be zero"));
	}
	
	@Test
	void validateZoneOffsetSignWithNonPositive() {
		Result<Void> result = TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-positive"));
	}
	
	@Test
	void validateZoneOffsetSignWithNonNegative() {
		Result<Void> result = TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-negative"));
	}
	
	@Test
	void validateZoneOffsetSignWithNonZero() {
		Result<Void> result = TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(result.isSuccess());
		Result<Void> negResult = TemporalValidators.validateZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(negResult.isSuccess());
		Result<Void> failResult = TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-zero"));
	}
	
	@Test
	void validateZoneOffsetSignWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateZoneOffsetSign(null, Optional.empty(), Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalValidators.validateZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), Optional.empty(), null));
	}
}
