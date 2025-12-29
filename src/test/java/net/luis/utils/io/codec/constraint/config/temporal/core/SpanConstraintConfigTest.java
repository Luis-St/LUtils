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

package net.luis.utils.io.codec.constraint.config.temporal.core;

import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SpanConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class SpanConstraintConfigTest {

	@Test
	void constructor() {
		assertDoesNotThrow(() -> new SpanConstraintConfig(Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> new SpanConstraintConfig(Optional.of(Duration.ofDays(7)), Optional.empty()));
		assertDoesNotThrow(() -> new SpanConstraintConfig(Optional.empty(), Optional.of(Duration.ofDays(30))));
		assertDoesNotThrow(() -> new SpanConstraintConfig(Optional.of(Duration.ofHours(2)), Optional.of(Duration.ofHours(4))));
	}

	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new SpanConstraintConfig(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> new SpanConstraintConfig(Optional.empty(), null));
		assertThrows(NullPointerException.class, () -> new SpanConstraintConfig(null, null));
	}

	@Test
	void unconstrainedConstant() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.withinLast().isEmpty());
		assertTrue(config.withinNext().isEmpty());
	}

	@Test
	void isUnconstrained() {
		assertTrue(SpanConstraintConfig.UNCONSTRAINED.isUnconstrained());
		assertTrue(new SpanConstraintConfig(Optional.empty(), Optional.empty()).isUnconstrained());
		assertFalse(SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7)).isUnconstrained());
		assertFalse(SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(30)).isUnconstrained());
	}

	@Test
	void withWithinLast() {
		Duration duration = Duration.ofDays(7);
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(duration);

		assertTrue(config.withinLast().isPresent());
		assertEquals(duration, config.withinLast().get());
		assertTrue(config.withinNext().isEmpty());
	}

	@Test
	void withWithinLastPreservesWithinNext() {
		Duration withinNext = Duration.ofDays(30);
		SpanConstraintConfig initial = new SpanConstraintConfig(Optional.empty(), Optional.of(withinNext));
		SpanConstraintConfig config = initial.withWithinLast(Duration.ofDays(7));

		assertTrue(config.withinLast().isPresent());
		assertEquals(Duration.ofDays(7), config.withinLast().get());
		assertTrue(config.withinNext().isPresent());
		assertEquals(withinNext, config.withinNext().get());
	}

	@Test
	void withWithinNext() {
		Duration duration = Duration.ofDays(30);
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(duration);

		assertTrue(config.withinLast().isEmpty());
		assertTrue(config.withinNext().isPresent());
		assertEquals(duration, config.withinNext().get());
	}

	@Test
	void withWithinNextPreservesWithinLast() {
		Duration withinLast = Duration.ofDays(7);
		SpanConstraintConfig initial = new SpanConstraintConfig(Optional.of(withinLast), Optional.empty());
		SpanConstraintConfig config = initial.withWithinNext(Duration.ofDays(30));

		assertTrue(config.withinLast().isPresent());
		assertEquals(withinLast, config.withinLast().get());
		assertTrue(config.withinNext().isPresent());
		assertEquals(Duration.ofDays(30), config.withinNext().get());
	}

	// LocalDate matches() tests

	@Test
	void matchesLocalDateUnconstrained() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED;

		assertTrue(config.matches(LocalDate.of(2020, 1, 1), () -> LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 15), () -> LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2030, 12, 31), () -> LocalDate.of(2024, 6, 15)).isSuccess());
	}

	@Test
	void matchesLocalDateWithinLastSuccess() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		LocalDate now = LocalDate.of(2024, 6, 15);

		assertTrue(config.matches(LocalDate.of(2024, 6, 15), () -> now).isSuccess());  // Exact match
		assertTrue(config.matches(LocalDate.of(2024, 6, 10), () -> now).isSuccess());  // 5 days ago
		assertTrue(config.matches(LocalDate.of(2024, 6, 8), () -> now).isSuccess());   // 7 days ago
	}

	@Test
	void matchesLocalDateWithinLastFailure() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		LocalDate now = LocalDate.of(2024, 6, 15);

		Result<Void> result = config.matches(LocalDate.of(2024, 6, 7), () -> now);  // 8 days ago
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated within-last constraint"));
	}

	@Test
	void matchesLocalDateWithinNextSuccess() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(30));
		LocalDate now = LocalDate.of(2024, 6, 15);

		assertTrue(config.matches(LocalDate.of(2024, 6, 15), () -> now).isSuccess());  // Exact match
		assertTrue(config.matches(LocalDate.of(2024, 6, 30), () -> now).isSuccess());  // 15 days future
		assertTrue(config.matches(LocalDate.of(2024, 7, 15), () -> now).isSuccess());  // 30 days future
	}

	@Test
	void matchesLocalDateWithinNextFailure() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(30));
		LocalDate now = LocalDate.of(2024, 6, 15);

		Result<Void> result = config.matches(LocalDate.of(2024, 7, 16), () -> now);  // 31 days future
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated within-next constraint"));
	}

	@Test
	void matchesLocalDateBothConstraintsSuccess() {
		SpanConstraintConfig config = new SpanConstraintConfig(
			Optional.of(Duration.ofDays(7)),
			Optional.of(Duration.ofDays(7))
		);
		LocalDate now = LocalDate.of(2024, 6, 15);

		assertTrue(config.matches(LocalDate.of(2024, 6, 15), () -> now).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 10), () -> now).isSuccess());  // 5 days ago
		assertTrue(config.matches(LocalDate.of(2024, 6, 20), () -> now).isSuccess());  // 5 days future
	}

	@Test
	void matchesLocalDateBothConstraintsFailurePast() {
		SpanConstraintConfig config = new SpanConstraintConfig(
			Optional.of(Duration.ofDays(7)),
			Optional.of(Duration.ofDays(7))
		);
		LocalDate now = LocalDate.of(2024, 6, 15);

		Result<Void> result = config.matches(LocalDate.of(2024, 6, 7), () -> now);  // 8 days ago
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("within-last"));
	}

	@Test
	void matchesLocalDateBothConstraintsFailureFuture() {
		SpanConstraintConfig config = new SpanConstraintConfig(
			Optional.of(Duration.ofDays(7)),
			Optional.of(Duration.ofDays(7))
		);
		LocalDate now = LocalDate.of(2024, 6, 15);

		Result<Void> result = config.matches(LocalDate.of(2024, 6, 23), () -> now);  // 8 days future
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("within-next"));
	}

	// LocalTime matches() tests

	@Test
	void matchesLocalTimeUnconstrained() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED;

		assertTrue(config.matches(LocalTime.MIDNIGHT, () -> LocalTime.NOON).isSuccess());
		assertTrue(config.matches(LocalTime.NOON, () -> LocalTime.NOON).isSuccess());
		assertTrue(config.matches(LocalTime.of(23, 59), () -> LocalTime.NOON).isSuccess());
	}

	@Test
	void matchesLocalTimeWithinLastSuccess() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(2));
		LocalTime now = LocalTime.of(14, 0);  // 2:00 PM

		assertTrue(config.matches(LocalTime.of(14, 0), () -> now).isSuccess());  // Exact match
		assertTrue(config.matches(LocalTime.of(13, 0), () -> now).isSuccess());  // 1 hour ago
		assertTrue(config.matches(LocalTime.of(12, 0), () -> now).isSuccess());  // 2 hours ago
	}

	@Test
	void matchesLocalTimeWithinLastFailure() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(2));
		LocalTime now = LocalTime.of(14, 0);

		Result<Void> result = config.matches(LocalTime.of(11, 59), () -> now);  // Just over 2 hours ago
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated within-last constraint"));
	}

	@Test
	void matchesLocalTimeWithinNextSuccess() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(2));
		LocalTime now = LocalTime.of(14, 0);

		assertTrue(config.matches(LocalTime.of(14, 0), () -> now).isSuccess());  // Exact match
		assertTrue(config.matches(LocalTime.of(15, 0), () -> now).isSuccess());  // 1 hour future
		assertTrue(config.matches(LocalTime.of(16, 0), () -> now).isSuccess());  // 2 hours future
	}

	@Test
	void matchesLocalTimeWithinNextFailure() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(2));
		LocalTime now = LocalTime.of(14, 0);

		Result<Void> result = config.matches(LocalTime.of(16, 1), () -> now);  // Just over 2 hours future
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated within-next constraint"));
	}

	// LocalDateTime matches() tests

	@Test
	void matchesLocalDateTimeUnconstrained() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED;

		assertTrue(config.matches(LocalDateTime.of(2020, 1, 1, 0, 0), () -> LocalDateTime.of(2024, 6, 15, 12, 0)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 12, 0), () -> LocalDateTime.of(2024, 6, 15, 12, 0)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2030, 12, 31, 23, 59), () -> LocalDateTime.of(2024, 6, 15, 12, 0)).isSuccess());
	}

	@Test
	void matchesLocalDateTimeWithinLastSuccess() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(24));
		LocalDateTime now = LocalDateTime.of(2024, 6, 15, 14, 0);

		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 14, 0), () -> now).isSuccess());  // Exact match
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 2, 0), () -> now).isSuccess());   // 12 hours ago
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 14, 14, 0), () -> now).isSuccess());  // 24 hours ago
	}

	@Test
	void matchesLocalDateTimeWithinLastFailure() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(24));
		LocalDateTime now = LocalDateTime.of(2024, 6, 15, 14, 0);

		Result<Void> result = config.matches(LocalDateTime.of(2024, 6, 14, 13, 59), () -> now);  // Just over 24 hours ago
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated within-last constraint"));
	}

	@Test
	void matchesLocalDateTimeWithinNextSuccess() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(24));
		LocalDateTime now = LocalDateTime.of(2024, 6, 15, 14, 0);

		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 14, 0), () -> now).isSuccess());  // Exact match
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 16, 2, 0), () -> now).isSuccess());   // 12 hours future
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 16, 14, 0), () -> now).isSuccess());  // 24 hours future
	}

	@Test
	void matchesLocalDateTimeWithinNextFailure() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(24));
		LocalDateTime now = LocalDateTime.of(2024, 6, 15, 14, 0);

		Result<Void> result = config.matches(LocalDateTime.of(2024, 6, 16, 14, 1), () -> now);  // Just over 24 hours future
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated within-next constraint"));
	}

	// OffsetTime matches() tests

	@Test
	void matchesOffsetTimeUnconstrained() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED;

		assertTrue(config.matches(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC), () -> OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC)).isSuccess());
		assertTrue(config.matches(OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC), () -> OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC)).isSuccess());
	}

	@Test
	void matchesOffsetTimeWithinLastSuccess() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(2));
		OffsetTime now = OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC);

		assertTrue(config.matches(OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC), () -> now).isSuccess());  // Exact match
		assertTrue(config.matches(OffsetTime.of(13, 0, 0, 0, ZoneOffset.UTC), () -> now).isSuccess());  // 1 hour ago
		assertTrue(config.matches(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC), () -> now).isSuccess());  // 2 hours ago
	}

	@Test
	void matchesOffsetTimeWithinLastFailure() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(2));
		OffsetTime now = OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC);

		Result<Void> result = config.matches(OffsetTime.of(11, 59, 0, 0, ZoneOffset.UTC), () -> now);  // Just over 2 hours ago
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated within-last constraint"));
	}

	@Test
	void matchesOffsetTimeWithinNextSuccess() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(2));
		OffsetTime now = OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC);

		assertTrue(config.matches(OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC), () -> now).isSuccess());  // Exact match
		assertTrue(config.matches(OffsetTime.of(15, 0, 0, 0, ZoneOffset.UTC), () -> now).isSuccess());  // 1 hour future
		assertTrue(config.matches(OffsetTime.of(16, 0, 0, 0, ZoneOffset.UTC), () -> now).isSuccess());  // 2 hours future
	}

	@Test
	void matchesOffsetTimeWithinNextFailure() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(2));
		OffsetTime now = OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC);

		Result<Void> result = config.matches(OffsetTime.of(16, 1, 0, 0, ZoneOffset.UTC), () -> now);  // Just over 2 hours future
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated within-next constraint"));
	}

	// OffsetDateTime matches() tests

	@Test
	void matchesOffsetDateTimeUnconstrained() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED;

		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC), () -> OffsetDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC), () -> OffsetDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC)).isSuccess());
	}

	@Test
	void matchesOffsetDateTimeWithinLastSuccess() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(24));
		OffsetDateTime now = OffsetDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC);

		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC), () -> now).isSuccess());  // Exact match
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 2, 0, 0, 0, ZoneOffset.UTC), () -> now).isSuccess());   // 12 hours ago
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 14, 14, 0, 0, 0, ZoneOffset.UTC), () -> now).isSuccess());  // 24 hours ago
	}

	@Test
	void matchesOffsetDateTimeWithinLastFailure() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(24));
		OffsetDateTime now = OffsetDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC);

		Result<Void> result = config.matches(OffsetDateTime.of(2024, 6, 14, 13, 59, 0, 0, ZoneOffset.UTC), () -> now);  // Just over 24 hours ago
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated within-last constraint"));
	}

	@Test
	void matchesOffsetDateTimeWithinNextSuccess() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(24));
		OffsetDateTime now = OffsetDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC);

		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC), () -> now).isSuccess());  // Exact match
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 16, 2, 0, 0, 0, ZoneOffset.UTC), () -> now).isSuccess());   // 12 hours future
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 16, 14, 0, 0, 0, ZoneOffset.UTC), () -> now).isSuccess());  // 24 hours future
	}

	@Test
	void matchesOffsetDateTimeWithinNextFailure() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(24));
		OffsetDateTime now = OffsetDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC);

		Result<Void> result = config.matches(OffsetDateTime.of(2024, 6, 16, 14, 1, 0, 0, ZoneOffset.UTC), () -> now);  // Just over 24 hours future
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated within-next constraint"));
	}

	// ZonedDateTime matches() tests

	@Test
	void matchesZonedDateTimeUnconstrained() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED;

		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneId.systemDefault()), () -> ZonedDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneId.systemDefault())).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneId.systemDefault()), () -> ZonedDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneId.systemDefault())).isSuccess());
	}

	@Test
	void matchesZonedDateTimeWithinLastSuccess() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(24));
		ZonedDateTime now = ZonedDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneId.systemDefault());

		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneId.systemDefault()), () -> now).isSuccess());  // Exact match
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 2, 0, 0, 0, ZoneId.systemDefault()), () -> now).isSuccess());   // 12 hours ago
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 14, 14, 0, 0, 0, ZoneId.systemDefault()), () -> now).isSuccess());  // 24 hours ago
	}

	@Test
	void matchesZonedDateTimeWithinLastFailure() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(24));
		ZonedDateTime now = ZonedDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneId.systemDefault());

		Result<Void> result = config.matches(ZonedDateTime.of(2024, 6, 14, 13, 59, 0, 0, ZoneId.systemDefault()), () -> now);  // Just over 24 hours ago
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated within-last constraint"));
	}

	@Test
	void matchesZonedDateTimeWithinNextSuccess() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(24));
		ZonedDateTime now = ZonedDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneId.systemDefault());

		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneId.systemDefault()), () -> now).isSuccess());  // Exact match
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 16, 2, 0, 0, 0, ZoneId.systemDefault()), () -> now).isSuccess());   // 12 hours future
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 16, 14, 0, 0, 0, ZoneId.systemDefault()), () -> now).isSuccess());  // 24 hours future
	}

	@Test
	void matchesZonedDateTimeWithinNextFailure() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(24));
		ZonedDateTime now = ZonedDateTime.of(2024, 6, 15, 14, 0, 0, 0, ZoneId.systemDefault());

		Result<Void> result = config.matches(ZonedDateTime.of(2024, 6, 16, 14, 1, 0, 0, ZoneId.systemDefault()), () -> now);  // Just over 24 hours future
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated within-next constraint"));
	}

	// Null checks for matches methods

	@Test
	void matchesNullChecks() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));

		assertThrows(NullPointerException.class, () -> config.matches((LocalDate) null, () -> LocalDate.of(2024, 6, 15)));
		assertThrows(NullPointerException.class, () -> config.matches((LocalTime) null, () -> LocalTime.NOON));
		assertThrows(NullPointerException.class, () -> config.matches((LocalDateTime) null, () -> LocalDateTime.of(2024, 6, 15, 12, 0)));
		assertThrows(NullPointerException.class, () -> config.matches((OffsetTime) null, () -> OffsetTime.now()));
		assertThrows(NullPointerException.class, () -> config.matches((OffsetDateTime) null, () -> OffsetDateTime.now()));
		assertThrows(NullPointerException.class, () -> config.matches((ZonedDateTime) null, () -> ZonedDateTime.now()));
	}

	// toString tests

	@Test
	void toStringUnconstrained() {
		String str = SpanConstraintConfig.UNCONSTRAINED.toString();
		assertEquals("SpanConstraintConfig[unconstrained]", str);
	}

	@Test
	void toStringWithinLast() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		String str = config.toString();
		assertTrue(str.contains("SpanConstraintConfig["));
		assertTrue(str.contains("withinLast=PT168H"));
	}

	@Test
	void toStringWithinNext() {
		SpanConstraintConfig config = SpanConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(30));
		String str = config.toString();
		assertTrue(str.contains("withinNext=PT720H"));
	}

	@Test
	void toStringBothConstraints() {
		SpanConstraintConfig config = new SpanConstraintConfig(
			Optional.of(Duration.ofDays(7)),
			Optional.of(Duration.ofDays(30))
		);
		String str = config.toString();
		assertTrue(str.contains("withinLast=PT168H"));
		assertTrue(str.contains("withinNext=PT720H"));
	}

	// Equality tests

	@Test
	void equality() {
		SpanConstraintConfig config1 = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		SpanConstraintConfig config2 = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		SpanConstraintConfig config3 = SpanConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(14));

		assertEquals(config1, config2);
		assertNotEquals(config1, config3);
	}

	@Test
	void hashCodeConsistency() {
		SpanConstraintConfig config1 = new SpanConstraintConfig(
			Optional.of(Duration.ofDays(7)),
			Optional.of(Duration.ofDays(30))
		);
		SpanConstraintConfig config2 = new SpanConstraintConfig(
			Optional.of(Duration.ofDays(7)),
			Optional.of(Duration.ofDays(30))
		);

		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
