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

package net.luis.utils.io.codec.constraint_new.config.matcher;

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TemporalMatchers}.<br>
 *
 * @author Luis-St
 */
class TemporalMatchersTest {
	
	@Test
	void matchDurationSignWithAllEmpty() {
		Result<Void> result = TemporalMatchers.matchDurationSign(Duration.ofHours(1), Optional.empty(), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchDurationSignWithPositive() {
		Result<Void> result = TemporalMatchers.matchDurationSign(Duration.ofHours(1), Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchDurationSign(Duration.ofHours(-1), Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be positive"));
	}
	
	@Test
	void matchDurationSignWithNegative() {
		Result<Void> result = TemporalMatchers.matchDurationSign(Duration.ofHours(-1), Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchDurationSign(Duration.ofHours(1), Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be negative"));
	}
	
	@Test
	void matchDurationSignWithZero() {
		Result<Void> result = TemporalMatchers.matchDurationSign(Duration.ZERO, Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchDurationSign(Duration.ofHours(1), Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be zero"));
	}
	
	@Test
	void matchDurationSignWithNonPositive() {
		Result<Void> result = TemporalMatchers.matchDurationSign(Duration.ofHours(-1), Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = TemporalMatchers.matchDurationSign(Duration.ZERO, Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchDurationSign(Duration.ofHours(1), Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-positive"));
	}
	
	@Test
	void matchDurationSignWithNonNegative() {
		Result<Void> result = TemporalMatchers.matchDurationSign(Duration.ofHours(1), Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = TemporalMatchers.matchDurationSign(Duration.ZERO, Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchDurationSign(Duration.ofHours(-1), Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-negative"));
	}
	
	@Test
	void matchDurationSignWithNonZero() {
		Result<Void> result = TemporalMatchers.matchDurationSign(Duration.ofHours(1), Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(result.isSuccess());
		Result<Void> negResult = TemporalMatchers.matchDurationSign(Duration.ofHours(-1), Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(negResult.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchDurationSign(Duration.ZERO, Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-zero"));
	}
	
	@Test
	void matchDurationSignWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchDurationSign(null, Optional.empty(), Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchDurationSign(Duration.ZERO, null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchDurationSign(Duration.ZERO, Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchDurationSign(Duration.ZERO, Optional.empty(), Optional.empty(), null));
	}
	
	@Test
	void matchDurationWithinLastWithEmptyOptional() {
		Result<Void> result = TemporalMatchers.matchDurationWithinLast(Duration.ofHours(-5), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchDurationWithinLastWithPass() {
		Result<Void> result = TemporalMatchers.matchDurationWithinLast(Duration.ofMinutes(-30), Optional.of(Duration.ofHours(1)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchDurationWithinLastWithFail() {
		Result<Void> result = TemporalMatchers.matchDurationWithinLast(Duration.ofHours(-2), Optional.of(Duration.ofHours(1)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be within last"));
	}
	
	@Test
	void matchDurationWithinLastWithBoundary() {
		Result<Void> result = TemporalMatchers.matchDurationWithinLast(Duration.ofHours(-1), Optional.of(Duration.ofHours(1)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchDurationWithinLastWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchDurationWithinLast(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchDurationWithinLast(Duration.ZERO, null));
	}
	
	@Test
	void matchDurationWithinNextWithEmptyOptional() {
		Result<Void> result = TemporalMatchers.matchDurationWithinNext(Duration.ofHours(5), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchDurationWithinNextWithPass() {
		Result<Void> result = TemporalMatchers.matchDurationWithinNext(Duration.ofMinutes(30), Optional.of(Duration.ofHours(1)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchDurationWithinNextWithFail() {
		Result<Void> result = TemporalMatchers.matchDurationWithinNext(Duration.ofHours(2), Optional.of(Duration.ofHours(1)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be within next"));
	}
	
	@Test
	void matchDurationWithinNextWithBoundary() {
		Result<Void> result = TemporalMatchers.matchDurationWithinNext(Duration.ofHours(1), Optional.of(Duration.ofHours(1)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchDurationWithinNextWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchDurationWithinNext(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchDurationWithinNext(Duration.ZERO, null));
	}
	
	@Test
	void matchPeriodSignWithAllEmpty() {
		Result<Void> result = TemporalMatchers.matchPeriodSign(Period.ofMonths(1), Optional.empty(), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPeriodSignWithPositive() {
		Result<Void> result = TemporalMatchers.matchPeriodSign(Period.ofMonths(1), Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchPeriodSign(Period.ofMonths(-1), Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be positive"));
	}
	
	@Test
	void matchPeriodSignWithNegative() {
		Result<Void> result = TemporalMatchers.matchPeriodSign(Period.ofMonths(-1), Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchPeriodSign(Period.ofMonths(1), Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be negative"));
	}
	
	@Test
	void matchPeriodSignWithZero() {
		Result<Void> result = TemporalMatchers.matchPeriodSign(Period.ZERO, Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchPeriodSign(Period.ofMonths(1), Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be zero"));
	}
	
	@Test
	void matchPeriodSignWithNonPositive() {
		Result<Void> result = TemporalMatchers.matchPeriodSign(Period.ofMonths(-1), Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = TemporalMatchers.matchPeriodSign(Period.ZERO, Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchPeriodSign(Period.ofMonths(1), Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-positive"));
	}
	
	@Test
	void matchPeriodSignWithNonNegative() {
		Result<Void> result = TemporalMatchers.matchPeriodSign(Period.ofMonths(1), Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = TemporalMatchers.matchPeriodSign(Period.ZERO, Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchPeriodSign(Period.ofMonths(-1), Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-negative"));
	}
	
	@Test
	void matchPeriodSignWithNonZero() {
		Result<Void> result = TemporalMatchers.matchPeriodSign(Period.ofMonths(1), Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(result.isSuccess());
		Result<Void> negResult = TemporalMatchers.matchPeriodSign(Period.ofMonths(-1), Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(negResult.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchPeriodSign(Period.ZERO, Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-zero"));
	}
	
	@Test
	void matchPeriodSignWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchPeriodSign(null, Optional.empty(), Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchPeriodSign(Period.ZERO, null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchPeriodSign(Period.ZERO, Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchPeriodSign(Period.ZERO, Optional.empty(), Optional.empty(), null));
	}
	
	@Test
	void matchPeriodRangeWithAllEmpty() {
		Result<Void> result = TemporalMatchers.matchPeriodRange(Period.ofMonths(6), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPeriodRangeWithMinOnly() {
		Result<Void> result = TemporalMatchers.matchPeriodRange(Period.ofMonths(6), Optional.of(Pair.of(Period.ofMonths(3), true)), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchPeriodRange(Period.ofMonths(2), Optional.of(Pair.of(Period.ofMonths(3), true)), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be greater than or equal to"));
	}
	
	@Test
	void matchPeriodRangeWithMaxOnly() {
		Result<Void> result = TemporalMatchers.matchPeriodRange(Period.ofMonths(6), Optional.empty(), Optional.of(Pair.of(Period.ofMonths(12), true)));
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchPeriodRange(Period.ofMonths(15), Optional.empty(), Optional.of(Pair.of(Period.ofMonths(12), true)));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be less than or equal to"));
	}
	
	@Test
	void matchPeriodRangeWithBoth() {
		Result<Void> result = TemporalMatchers.matchPeriodRange(Period.ofMonths(6), Optional.of(Pair.of(Period.ofMonths(3), true)), Optional.of(Pair.of(Period.ofMonths(12), true)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPeriodRangeWithInclusiveBounds() {
		Result<Void> minResult = TemporalMatchers.matchPeriodRange(Period.ofMonths(3), Optional.of(Pair.of(Period.ofMonths(3), true)), Optional.empty());
		assertTrue(minResult.isSuccess());
		Result<Void> maxResult = TemporalMatchers.matchPeriodRange(Period.ofMonths(12), Optional.empty(), Optional.of(Pair.of(Period.ofMonths(12), true)));
		assertTrue(maxResult.isSuccess());
	}
	
	@Test
	void matchPeriodRangeWithExclusiveBounds() {
		Result<Void> minResult = TemporalMatchers.matchPeriodRange(Period.ofMonths(3), Optional.of(Pair.of(Period.ofMonths(3), false)), Optional.empty());
		assertTrue(minResult.isError());
		assertTrue(minResult.errorOrThrow().contains("must be greater than"));
		Result<Void> maxResult = TemporalMatchers.matchPeriodRange(Period.ofMonths(12), Optional.empty(), Optional.of(Pair.of(Period.ofMonths(12), false)));
		assertTrue(maxResult.isError());
		assertTrue(maxResult.errorOrThrow().contains("must be less than"));
	}
	
	@Test
	void matchPeriodRangeWithDays() {
		Result<Void> result = TemporalMatchers.matchPeriodRange(Period.ofDays(15), Optional.of(Pair.of(Period.ofDays(10), true)), Optional.of(Pair.of(Period.ofDays(20), true)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPeriodRangeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchPeriodRange(null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchPeriodRange(Period.ZERO, null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchPeriodRange(Period.ZERO, Optional.empty(), null));
	}
	
	@Test
	void matchZoneOffsetSignWithAllEmpty() {
		Result<Void> result = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchZoneOffsetSignWithPositive() {
		Result<Void> result = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.ofHours(1), Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be positive"));
	}
	
	@Test
	void matchZoneOffsetSignWithNegative() {
		Result<Void> result = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be negative"));
	}
	
	@Test
	void matchZoneOffsetSignWithZero() {
		Result<Void> result = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(result.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be zero"));
	}
	
	@Test
	void matchZoneOffsetSignWithNonPositive() {
		Result<Void> result = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.UTC, Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.ofHours(1), Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-positive"));
	}
	
	@Test
	void matchZoneOffsetSignWithNonNegative() {
		Result<Void> result = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-negative"));
	}
	
	@Test
	void matchZoneOffsetSignWithNonZero() {
		Result<Void> result = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.ofHours(1), Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(result.isSuccess());
		Result<Void> negResult = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.ofHours(-1), Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(negResult.isSuccess());
		Result<Void> failResult = TemporalMatchers.matchZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-zero"));
	}
	
	@Test
	void matchZoneOffsetSignWithNullChecks() {
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchZoneOffsetSign(null, Optional.empty(), Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchZoneOffsetSign(ZoneOffset.UTC, null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> TemporalMatchers.matchZoneOffsetSign(ZoneOffset.UTC, Optional.empty(), Optional.empty(), null));
	}
}
