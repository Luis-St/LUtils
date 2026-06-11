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

package net.luis.utils.io.database;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.conditions.comparison.*;
import net.luis.utils.io.database.condition.conditions.numeric.*;
import net.luis.utils.io.database.condition.conditions.string.*;
import net.luis.utils.io.database.condition.conditions.temporal.*;
import net.luis.utils.io.database.exception.client.SqlTypeNotFoundException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.SqlAggregateFunction;
import net.luis.utils.io.database.function.functions.aggregate.*;
import net.luis.utils.io.database.function.functions.generic.*;
import net.luis.utils.io.database.function.functions.numeric.*;
import net.luis.utils.io.database.function.functions.numeric.bitwise.*;
import net.luis.utils.io.database.function.functions.numeric.trigonometric.*;
import net.luis.utils.io.database.function.functions.string.*;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.function.functions.window.*;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Sql}.<br>
 *
 * @author Luis-St
 */
class SqlTest {
	
	private static final SqlType<Instant> INSTANT_TYPE = SqlTypes.INSTANT.configure(SqlParameter.fractional(0));
	private static final SqlType<LocalTime> LOCAL_TIME_TYPE = SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(0));
	private static final SqlType<LocalDateTime> LOCAL_DATE_TIME_TYPE = SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(0));
	
	//region Fixtures
	private static @NonNull SqlExpression<Integer> intExpr() {
		return SqlTestFixtures.integerExpression();
	}
	
	private static @NonNull SqlExpression<String> strExpr() {
		return SqlTestFixtures.stringExpression();
	}
	
	private static @NonNull SqlExpression<LocalDate> dateExpr() {
		return Sql.of(LocalDate.of(2026, 1, 1), SqlTypes.LOCAL_DATE);
	}
	
	private static @NonNull SqlExpression<Duration> durationExpr() {
		return Sql.of(Duration.ofSeconds(1), SqlTypes.DURATION);
	}
	
	private static @NonNull SqlWindowClause window() {
		return SqlWindowClause.of();
	}
	
	private static @NonNull SqlAggregateFunction<Integer> aggregate() {
		return new SqlSumFunction<>(intExpr());
	}
	
	private static @NonNull SqlSelectQuery<Integer> intSubquery() {
		return new SqlSelectQuery<>(SqlTable.create(Integer.class, "int_table"), SqlTestFixtures.DIALECT, SqlTestFixtures.SOURCE, SqlTestFixtures.TIMEOUT, resultSet -> null);
	}
	//endregion
	
	//region Tier 2 - Exceptions (explicit guards / branch-bearing methods)
	@Test
	void ofWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.of(null));
	}
	
	@Test
	void ofWithUnregisteredTypeThrows() {
		assertThrows(SqlTypeNotFoundException.class, () -> Sql.of(new Object()));
	}
	
	@Test
	void ofWithTypeNullArgumentsThrow() {
		assertThrows(NullPointerException.class, () -> Sql.of(null, SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.of(0, null));
	}
	
	@Test
	void castWithNullArgumentsThrow() {
		assertThrows(NullPointerException.class, () -> Sql.cast(null, SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.cast(intExpr(), null));
	}
	
	@Test
	void inVarargsWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> Sql.in(intExpr(), (SqlExpression<Integer>[]) null));
	}
	
	@Test
	void inVarargsWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.in(intExpr(), intExpr(), null));
	}
	
	@Test
	void coalesceWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> Sql.coalesce((SqlExpression<Integer>[]) null));
	}
	
	@Test
	void coalesceWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.coalesce(intExpr(), null));
	}
	
	@Test
	void greatestWithNullArgumentsThrow() {
		assertThrows(NullPointerException.class, () -> Sql.greatest(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.greatest(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.greatest(intExpr(), intExpr(), (SqlExpression<Integer>[]) null));
		assertThrows(NullPointerException.class, () -> Sql.greatest(intExpr(), intExpr(), intExpr(), null));
	}
	
	@Test
	void leastWithNullArgumentsThrow() {
		assertThrows(NullPointerException.class, () -> Sql.least(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.least(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.least(intExpr(), intExpr(), (SqlExpression<Integer>[]) null));
		assertThrows(NullPointerException.class, () -> Sql.least(intExpr(), intExpr(), intExpr(), null));
	}
	
	@Test
	void concatWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> Sql.concat((SqlExpression<String>[]) null));
	}
	
	@Test
	void concatWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.concat(strExpr(), null));
	}
	
	@Test
	void concatWithSeparatorWithNullArgumentsThrow() {
		assertThrows(NullPointerException.class, () -> Sql.concatWithSeparator(null, strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.concatWithSeparator("-", (SqlExpression<String>[]) null));
		assertThrows(NullPointerException.class, () -> Sql.concatWithSeparator("-", strExpr(), null));
	}
	
	@Test
	void concatDistinctWithSeparatorWithNullArgumentsThrow() {
		assertThrows(NullPointerException.class, () -> Sql.concatDistinctWithSeparator(null, strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.concatDistinctWithSeparator("-", (SqlExpression<String>[]) null));
		assertThrows(NullPointerException.class, () -> Sql.concatDistinctWithSeparator("-", strExpr(), null));
	}
	
	@Test
	void concatOrderedWithSeparatorWithNullArgumentsThrow() {
		assertThrows(NullPointerException.class, () -> Sql.concatOrderedWithSeparator(null, strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.concatOrderedWithSeparator("-", (SqlExpression<String>[]) null));
		assertThrows(NullPointerException.class, () -> Sql.concatOrderedWithSeparator("-", strExpr(), null));
	}
	
	@Test
	@SuppressWarnings("deprecation")
	void ofUnsafeWithNullArgumentsThrow() {
		assertThrows(NullPointerException.class, () -> Sql.ofUnsafe(null, SqlTestFixtures.INTEGER_TYPE, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.ofUnsafe("fn", null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.ofUnsafe("fn", SqlTestFixtures.INTEGER_TYPE, (SqlExpression<?>[]) null));
		assertThrows(NullPointerException.class, () -> Sql.ofUnsafe("fn", SqlTestFixtures.INTEGER_TYPE, intExpr(), null));
	}
	//endregion
	
	//region Tier 2 - Exceptions (delegated null guards by category)
	@Test
	void comparisonConditionsRejectNullArguments() {
		assertThrows(NullPointerException.class, () -> Sql.equalTo(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.equalTo(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.in(null, intSubquery()));
		assertThrows(NullPointerException.class, () -> Sql.in(intExpr(), (SqlSelectQuery<Integer>) null));
		assertThrows(NullPointerException.class, () -> Sql.isDistinctFrom(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.isDistinctFrom(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.isNull(null));
		assertThrows(NullPointerException.class, () -> Sql.greaterThan(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.greaterThan(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.greaterThanOrEqualTo(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.greaterThanOrEqualTo(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.lessThan(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.lessThan(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.lessThanOrEqualTo(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.lessThanOrEqualTo(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.between(null, intExpr(), intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.between(intExpr(), null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.between(intExpr(), intExpr(), null));
	}
	
	@Test
	void numericConditionsRejectNullArguments() {
		assertThrows(NullPointerException.class, () -> Sql.isPositive(null));
		assertThrows(NullPointerException.class, () -> Sql.isNegative(null));
		assertThrows(NullPointerException.class, () -> Sql.isZero(null));
		assertThrows(NullPointerException.class, () -> Sql.modEquals(null, intExpr(), intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.modEquals(intExpr(), null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.modEquals(intExpr(), intExpr(), null));
	}
	
	@Test
	void genericFunctionsRejectNullArguments() {
		assertThrows(NullPointerException.class, () -> Sql.count(null, true));
		assertThrows(NullPointerException.class, () -> Sql.nullIf(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.nullIf(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.caseWhen(null, intExpr(), intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.caseWhen(SqlTestFixtures.alwaysCondition(), null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.min(null));
		assertThrows(NullPointerException.class, () -> Sql.max(null));
	}
	
	@Test
	void numericFunctionsRejectNullArguments() {
		assertThrows(NullPointerException.class, () -> Sql.add(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.add(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.subtract(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.subtract(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.multiply(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.multiply(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.divide(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.divide(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.negate(null));
		assertThrows(NullPointerException.class, () -> Sql.sum(null));
		assertThrows(NullPointerException.class, () -> Sql.average(null));
		assertThrows(NullPointerException.class, () -> Sql.abs(null));
		assertThrows(NullPointerException.class, () -> Sql.round(null));
		assertThrows(NullPointerException.class, () -> Sql.round(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.ceil(null));
		assertThrows(NullPointerException.class, () -> Sql.floor(null));
		assertThrows(NullPointerException.class, () -> Sql.truncate(null));
		assertThrows(NullPointerException.class, () -> Sql.mod(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.mod(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.pow(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.pow(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.sqrt(null));
		assertThrows(NullPointerException.class, () -> Sql.sign(null));
		assertThrows(NullPointerException.class, () -> Sql.exp(null));
		assertThrows(NullPointerException.class, () -> Sql.log2(null));
		assertThrows(NullPointerException.class, () -> Sql.ln(null));
		assertThrows(NullPointerException.class, () -> Sql.log10(null));
	}
	
	@Test
	void trigonometricFunctionsRejectNullArguments() {
		assertThrows(NullPointerException.class, () -> Sql.sin(null));
		assertThrows(NullPointerException.class, () -> Sql.cos(null));
		assertThrows(NullPointerException.class, () -> Sql.tan(null));
		assertThrows(NullPointerException.class, () -> Sql.asin(null));
		assertThrows(NullPointerException.class, () -> Sql.acos(null));
		assertThrows(NullPointerException.class, () -> Sql.atan(null));
		assertThrows(NullPointerException.class, () -> Sql.atan2(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.atan2(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.radians(null));
		assertThrows(NullPointerException.class, () -> Sql.degrees(null));
	}
	
	@Test
	void bitwiseFunctionsRejectNullArguments() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(intExpr(), intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(intExpr(), intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(intExpr(), intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.bitwiseNot(null));
		assertThrows(NullPointerException.class, () -> Sql.bitwiseNot(intExpr(), null));
	}
	
	@Test
	void stringConditionsRejectNullArguments() {
		assertThrows(NullPointerException.class, () -> Sql.startsWith(null, strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.startsWith(strExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.contains(null, strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.contains(strExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.endsWith(null, strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.endsWith(strExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.like(null, strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.like(strExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.equalsIgnoreCase(null, strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.equalsIgnoreCase(strExpr(), null));
	}
	
	@Test
	void stringFunctionsRejectNullArguments() {
		assertThrows(NullPointerException.class, () -> Sql.lower(null));
		assertThrows(NullPointerException.class, () -> Sql.upper(null));
		assertThrows(NullPointerException.class, () -> Sql.trim(null));
		assertThrows(NullPointerException.class, () -> Sql.leftTrim(null));
		assertThrows(NullPointerException.class, () -> Sql.rightTrim(null));
		assertThrows(NullPointerException.class, () -> Sql.trimChars(null, strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.trimChars(strExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.length(null));
		assertThrows(NullPointerException.class, () -> Sql.length(null, SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.length(strExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.substring(null, intExpr(), intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.substring(strExpr(), null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.replace(null, strExpr(), strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.replace(strExpr(), null, strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.replace(strExpr(), strExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.position(null, strExpr(), SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.position(strExpr(), null, SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.position(strExpr(), strExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.left(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.left(strExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.right(null, intExpr()));
		assertThrows(NullPointerException.class, () -> Sql.right(strExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.leftPad(null, intExpr(), strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.leftPad(strExpr(), null, strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.leftPad(strExpr(), intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.rightPad(null, intExpr(), strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.rightPad(strExpr(), null, strExpr()));
		assertThrows(NullPointerException.class, () -> Sql.rightPad(strExpr(), intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.hex(null));
		assertThrows(NullPointerException.class, () -> Sql.hex(null, SqlTestFixtures.STRING_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.hex(strExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.unhex(null));
		assertThrows(NullPointerException.class, () -> Sql.unhex(null, SqlTypes.LARGE_BYTES));
		assertThrows(NullPointerException.class, () -> Sql.unhex(strExpr(), null));
	}
	
	@Test
	void temporalConditionsRejectNullArguments() {
		assertThrows(NullPointerException.class, () -> Sql.withinLast(null, durationExpr()));
		assertThrows(NullPointerException.class, () -> Sql.withinLast(dateExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.withinNext(null, durationExpr()));
		assertThrows(NullPointerException.class, () -> Sql.withinNext(dateExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.before(null, dateExpr()));
		assertThrows(NullPointerException.class, () -> Sql.before(dateExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.after(null, dateExpr()));
		assertThrows(NullPointerException.class, () -> Sql.after(dateExpr(), null));
	}
	
	@Test
	void temporalFunctionsRejectNullArguments() {
		assertThrows(NullPointerException.class, () -> Sql.now(null));
		assertThrows(NullPointerException.class, () -> Sql.currentDate(null));
		assertThrows(NullPointerException.class, () -> Sql.currentTime(null));
		assertThrows(NullPointerException.class, () -> Sql.currentTimestamp(null));
		assertThrows(NullPointerException.class, () -> Sql.fromEpoch(null, LOCAL_DATE_TIME_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.fromEpoch(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.makeDate(null, intExpr(), intExpr(), SqlTypes.LOCAL_DATE));
		assertThrows(NullPointerException.class, () -> Sql.makeDate(intExpr(), null, intExpr(), SqlTypes.LOCAL_DATE));
		assertThrows(NullPointerException.class, () -> Sql.makeDate(intExpr(), intExpr(), null, SqlTypes.LOCAL_DATE));
		assertThrows(NullPointerException.class, () -> Sql.makeDate(intExpr(), intExpr(), intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.makeTime(null, intExpr(), intExpr(), LOCAL_TIME_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.makeTime(intExpr(), null, intExpr(), LOCAL_TIME_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.makeTime(intExpr(), intExpr(), null, LOCAL_TIME_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.makeTime(intExpr(), intExpr(), intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.extract(null, SqlTemporalPart.YEAR));
		assertThrows(NullPointerException.class, () -> Sql.extract(dateExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.extract(null, SqlTemporalPart.YEAR, SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.extract(dateExpr(), null, SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.extract(dateExpr(), SqlTemporalPart.YEAR, null));
		assertThrows(NullPointerException.class, () -> Sql.truncate(null, SqlTemporalPart.DAY, SqlTypes.LOCAL_DATE));
		assertThrows(NullPointerException.class, () -> Sql.truncate(dateExpr(), null, SqlTypes.LOCAL_DATE));
		assertThrows(NullPointerException.class, () -> Sql.truncate(dateExpr(), SqlTemporalPart.DAY, null));
		assertThrows(NullPointerException.class, () -> Sql.add(null, SqlTemporalPart.DAY, intExpr(), SqlTypes.LOCAL_DATE));
		assertThrows(NullPointerException.class, () -> Sql.add(dateExpr(), null, intExpr(), SqlTypes.LOCAL_DATE));
		assertThrows(NullPointerException.class, () -> Sql.add(dateExpr(), SqlTemporalPart.DAY, null, SqlTypes.LOCAL_DATE));
		assertThrows(NullPointerException.class, () -> Sql.add(dateExpr(), SqlTemporalPart.DAY, intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.subtract(null, SqlTemporalPart.DAY, intExpr(), SqlTypes.LOCAL_DATE));
		assertThrows(NullPointerException.class, () -> Sql.subtract(dateExpr(), null, intExpr(), SqlTypes.LOCAL_DATE));
		assertThrows(NullPointerException.class, () -> Sql.subtract(dateExpr(), SqlTemporalPart.DAY, null, SqlTypes.LOCAL_DATE));
		assertThrows(NullPointerException.class, () -> Sql.subtract(dateExpr(), SqlTemporalPart.DAY, intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.toEpoch(null, SqlTypes.LONG));
		assertThrows(NullPointerException.class, () -> Sql.toEpoch(dateExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.toDate(null, SqlTypes.LOCAL_DATE));
		assertThrows(NullPointerException.class, () -> Sql.toDate(dateExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.toTime(null, LOCAL_TIME_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.toTime(dateExpr(), null));
	}
	
	@Test
	void windowFunctionsRejectNullArguments() {
		assertThrows(NullPointerException.class, () -> Sql.over(null, window()));
		assertThrows(NullPointerException.class, () -> Sql.over(aggregate(), null));
		assertThrows(NullPointerException.class, () -> Sql.rowNumber(null));
		assertThrows(NullPointerException.class, () -> Sql.rowNumber(null, SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.rowNumber(window(), null));
		assertThrows(NullPointerException.class, () -> Sql.rank(null));
		assertThrows(NullPointerException.class, () -> Sql.rank(null, SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.rank(window(), null));
		assertThrows(NullPointerException.class, () -> Sql.denseRank(null));
		assertThrows(NullPointerException.class, () -> Sql.denseRank(null, SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.denseRank(window(), null));
		assertThrows(NullPointerException.class, () -> Sql.tileBucket(4, null));
		assertThrows(NullPointerException.class, () -> Sql.tileBucket(4, null, SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.tileBucket(4, window(), null));
		assertThrows(NullPointerException.class, () -> Sql.lag(null, window()));
		assertThrows(NullPointerException.class, () -> Sql.lag(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.lag(null, 1, window()));
		assertThrows(NullPointerException.class, () -> Sql.lag(intExpr(), 1, null));
		assertThrows(NullPointerException.class, () -> Sql.lag(intExpr(), 1, null, SqlTestFixtures.INTEGER_TYPE, window()));
		assertThrows(NullPointerException.class, () -> Sql.lag(intExpr(), 1, 0, null, window()));
		assertThrows(NullPointerException.class, () -> Sql.lag(intExpr(), 1, 0, SqlTestFixtures.INTEGER_TYPE, null));
		assertThrows(NullPointerException.class, () -> Sql.lead(null, window()));
		assertThrows(NullPointerException.class, () -> Sql.lead(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.lead(null, 1, window()));
		assertThrows(NullPointerException.class, () -> Sql.lead(intExpr(), 1, null));
		assertThrows(NullPointerException.class, () -> Sql.lead(intExpr(), 1, null, SqlTestFixtures.INTEGER_TYPE, window()));
		assertThrows(NullPointerException.class, () -> Sql.lead(intExpr(), 1, 0, null, window()));
		assertThrows(NullPointerException.class, () -> Sql.lead(intExpr(), 1, 0, SqlTestFixtures.INTEGER_TYPE, null));
		assertThrows(NullPointerException.class, () -> Sql.percentRank(null));
		assertThrows(NullPointerException.class, () -> Sql.percentRank(null, SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.percentRank(window(), null));
		assertThrows(NullPointerException.class, () -> Sql.cumulativeDistribution(null));
		assertThrows(NullPointerException.class, () -> Sql.cumulativeDistribution(null, SqlTestFixtures.INTEGER_TYPE));
		assertThrows(NullPointerException.class, () -> Sql.cumulativeDistribution(window(), null));
		assertThrows(NullPointerException.class, () -> Sql.firstValue(null, window()));
		assertThrows(NullPointerException.class, () -> Sql.firstValue(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.lastValue(null, window()));
		assertThrows(NullPointerException.class, () -> Sql.lastValue(intExpr(), null));
		assertThrows(NullPointerException.class, () -> Sql.valueAt(null, 0, window()));
		assertThrows(NullPointerException.class, () -> Sql.valueAt(intExpr(), 0, null));
	}
	//endregion
	
	//region Tier 3 - Branch coverage
	@Test
	void countDistinctReturnsCountDistinctFunction() {
		assertInstanceOf(SqlCountDistinctFunction.class, Sql.count(intExpr(), true));
	}
	
	@Test
	void countNonDistinctReturnsCountFunction() {
		assertInstanceOf(SqlCountFunction.class, Sql.count(intExpr(), false));
	}
	
	@Test
	void inVarargsWithEmptyOptions() {
		assertThrows(IllegalArgumentException.class, () -> Sql.in(intExpr()));
	}
	
	@Test
	void inVarargsWithMultipleOptions() {
		assertInstanceOf(SqlInListCondition.class, Sql.in(intExpr(), intExpr(), intExpr()));
	}
	
	@Test
	void coalesceWithEmptyValues() {
		assertThrows(IllegalArgumentException.class, () -> Sql.<Integer>coalesce());
	}
	
	@Test
	void coalesceWithMultipleValues() {
		assertInstanceOf(SqlCoalesceFunction.class, Sql.coalesce(intExpr(), intExpr()));
	}
	
	@Test
	void greatestWithNoExtraValues() {
		assertInstanceOf(SqlGreatestFunction.class, Sql.greatest(intExpr(), intExpr()));
	}
	
	@Test
	void greatestWithExtraValues() {
		assertInstanceOf(SqlGreatestFunction.class, Sql.greatest(intExpr(), intExpr(), intExpr()));
	}
	
	@Test
	void leastWithNoExtraValues() {
		assertInstanceOf(SqlLeastFunction.class, Sql.least(intExpr(), intExpr()));
	}
	
	@Test
	void leastWithExtraValues() {
		assertInstanceOf(SqlLeastFunction.class, Sql.least(intExpr(), intExpr(), intExpr()));
	}
	
	@Test
	void concatWithEmptyValues() {
		assertThrows(IllegalArgumentException.class, Sql::concat);
	}
	
	@Test
	void concatWithMultipleValues() {
		assertInstanceOf(SqlConcatFunction.class, Sql.concat(strExpr(), strExpr()));
	}
	
	@Test
	void concatWithSeparatorBuildsSeparatedConcat() {
		assertInstanceOf(SqlConcatFunction.class, Sql.concatWithSeparator("-", strExpr(), strExpr()));
	}
	
	@Test
	void concatDistinctWithSeparatorBuildsDistinctConcat() {
		assertInstanceOf(SqlConcatFunction.class, Sql.concatDistinctWithSeparator("-", strExpr(), strExpr()));
	}
	
	@Test
	void concatOrderedWithSeparatorBuildsOrderedConcat() {
		assertInstanceOf(SqlConcatFunction.class, Sql.concatOrderedWithSeparator("-", strExpr(), strExpr()));
	}
	
	@Test
	@SuppressWarnings("deprecation")
	void ofUnsafeWithNoArgs() {
		assertInstanceOf(SqlUnsafeFunction.class, Sql.ofUnsafe("fn", SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	@SuppressWarnings("deprecation")
	void ofUnsafeWithArgs() {
		assertInstanceOf(SqlUnsafeFunction.class, Sql.ofUnsafe("fn", SqlTestFixtures.INTEGER_TYPE, intExpr(), intExpr()));
	}
	//endregion
	
	//region Tier 4 - Simple inputs (happy path per method/overload)
	@Test
	void ofWithTypeReturnsValueExpression() {
		assertInstanceOf(SqlValueExpression.class, Sql.of(1, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void ofInfersTypeReturnsValueExpression() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlValueExpression.class, Sql.of(1));
	}
	
	@Test
	void castReturnsCastFunction() {
		assertInstanceOf(SqlCastFunction.class, Sql.cast(intExpr(), SqlTestFixtures.STRING_TYPE));
	}
	
	@Test
	void equalToReturnsEqualToCondition() {
		assertInstanceOf(SqlEqualToCondition.class, Sql.equalTo(intExpr(), intExpr()));
	}
	
	@Test
	void inListReturnsInListCondition() {
		assertInstanceOf(SqlInListCondition.class, Sql.in(intExpr(), intExpr()));
	}
	
	@Test
	void inSubqueryReturnsInQueryCondition() {
		assertInstanceOf(SqlInQueryCondition.class, Sql.in(intExpr(), intSubquery()));
	}
	
	@Test
	void isDistinctFromReturnsCondition() {
		assertInstanceOf(SqlIsDistinctFromCondition.class, Sql.isDistinctFrom(intExpr(), intExpr()));
	}
	
	@Test
	void isNullReturnsCondition() {
		assertInstanceOf(SqlIsNullCondition.class, Sql.isNull(intExpr()));
	}
	
	@Test
	void greaterThanReturnsCondition() {
		assertInstanceOf(SqlGreaterThanCondition.class, Sql.greaterThan(intExpr(), intExpr()));
	}
	
	@Test
	void greaterThanOrEqualToReturnsCondition() {
		assertInstanceOf(SqlGreaterThanCondition.class, Sql.greaterThanOrEqualTo(intExpr(), intExpr()));
	}
	
	@Test
	void lessThanReturnsCondition() {
		assertInstanceOf(SqlLessThanCondition.class, Sql.lessThan(intExpr(), intExpr()));
	}
	
	@Test
	void lessThanOrEqualToReturnsCondition() {
		assertInstanceOf(SqlLessThanCondition.class, Sql.lessThanOrEqualTo(intExpr(), intExpr()));
	}
	
	@Test
	void betweenReturnsCondition() {
		assertInstanceOf(SqlBetweenCondition.class, Sql.between(intExpr(), intExpr(), intExpr()));
	}
	
	@Test
	void nullIfReturnsNullIfFunction() {
		assertInstanceOf(SqlNullIfFunction.class, Sql.nullIf(intExpr(), intExpr()));
	}
	
	@Test
	void caseWhenReturnsCaseWhenFunction() {
		assertInstanceOf(SqlCaseWhenFunction.class, Sql.caseWhen(SqlTestFixtures.alwaysCondition(), intExpr(), intExpr()));
	}
	
	@Test
	void minReturnsMinFunction() {
		assertInstanceOf(SqlMinFunction.class, Sql.min(intExpr()));
	}
	
	@Test
	void maxReturnsMaxFunction() {
		assertInstanceOf(SqlMaxFunction.class, Sql.max(intExpr()));
	}
	
	@Test
	void isPositiveReturnsCondition() {
		assertInstanceOf(SqlIsPositiveCondition.class, Sql.isPositive(intExpr()));
	}
	
	@Test
	void isNegativeReturnsCondition() {
		assertInstanceOf(SqlIsNegativeCondition.class, Sql.isNegative(intExpr()));
	}
	
	@Test
	void isZeroReturnsCondition() {
		assertInstanceOf(SqlIsZeroCondition.class, Sql.isZero(intExpr()));
	}
	
	@Test
	void modEqualsReturnsCondition() {
		assertInstanceOf(SqlModEqualsCondition.class, Sql.modEquals(intExpr(), intExpr(), intExpr()));
	}
	
	@Test
	void randomReturnsRandomFunction() {
		assertInstanceOf(SqlRandomFunction.class, Sql.random());
	}
	
	@Test
	void piReturnsPiFunction() {
		assertInstanceOf(SqlPiFunction.class, Sql.pi());
	}
	
	@Test
	void addReturnsAddFunction() {
		assertInstanceOf(SqlNumericAddFunction.class, Sql.add(intExpr(), intExpr()));
	}
	
	@Test
	void subtractReturnsSubtractFunction() {
		assertInstanceOf(SqlNumericSubtractFunction.class, Sql.subtract(intExpr(), intExpr()));
	}
	
	@Test
	void multiplyReturnsMultiplyFunction() {
		assertInstanceOf(SqlNumericMultiplyFunction.class, Sql.multiply(intExpr(), intExpr()));
	}
	
	@Test
	void divideReturnsDivideFunction() {
		assertInstanceOf(SqlNumericDivideFunction.class, Sql.divide(intExpr(), intExpr()));
	}
	
	@Test
	void negateReturnsNegateFunction() {
		assertInstanceOf(SqlNegateFunction.class, Sql.negate(intExpr()));
	}
	
	@Test
	void sumReturnsSumFunction() {
		assertInstanceOf(SqlSumFunction.class, Sql.sum(intExpr()));
	}
	
	@Test
	void averageReturnsAverageFunction() {
		assertInstanceOf(SqlAverageFunction.class, Sql.average(intExpr()));
	}
	
	@Test
	void absReturnsAbsFunction() {
		assertInstanceOf(SqlAbsFunction.class, Sql.abs(intExpr()));
	}
	
	@Test
	void roundWithoutPrecisionReturnsRoundFunction() {
		assertInstanceOf(SqlRoundFunction.class, Sql.round(intExpr()));
	}
	
	@Test
	void roundWithPrecisionReturnsRoundFunction() {
		assertInstanceOf(SqlRoundFunction.class, Sql.round(intExpr(), intExpr()));
	}
	
	@Test
	void ceilReturnsCeilFunction() {
		assertInstanceOf(SqlCeilFunction.class, Sql.ceil(intExpr()));
	}
	
	@Test
	void floorReturnsFloorFunction() {
		assertInstanceOf(SqlFloorFunction.class, Sql.floor(intExpr()));
	}
	
	@Test
	void truncateNumericReturnsTruncateFunction() {
		assertInstanceOf(SqlNumericTruncateFunction.class, Sql.truncate(intExpr()));
	}
	
	@Test
	void modReturnsModFunction() {
		assertInstanceOf(SqlModFunction.class, Sql.mod(intExpr(), intExpr()));
	}
	
	@Test
	void powReturnsPowFunction() {
		assertInstanceOf(SqlPowFunction.class, Sql.pow(intExpr(), intExpr()));
	}
	
	@Test
	void sqrtReturnsSqrtFunction() {
		assertInstanceOf(SqlSqrtFunction.class, Sql.sqrt(intExpr()));
	}
	
	@Test
	void signReturnsSignFunction() {
		assertInstanceOf(SqlSignFunction.class, Sql.sign(intExpr()));
	}
	
	@Test
	void expReturnsExpFunction() {
		assertInstanceOf(SqlExpFunction.class, Sql.exp(intExpr()));
	}
	
	@Test
	void log2ReturnsLogFunction() {
		assertInstanceOf(SqlLogFunction.class, Sql.log2(intExpr()));
	}
	
	@Test
	void lnReturnsLogFunction() {
		assertInstanceOf(SqlLogFunction.class, Sql.ln(intExpr()));
	}
	
	@Test
	void log10ReturnsLogFunction() {
		assertInstanceOf(SqlLogFunction.class, Sql.log10(intExpr()));
	}
	
	@Test
	void sinReturnsExpectedFunction() {
		assertInstanceOf(SqlSinFunction.class, Sql.sin(intExpr()));
	}
	
	@Test
	void cosReturnsExpectedFunction() {
		assertInstanceOf(SqlCosFunction.class, Sql.cos(intExpr()));
	}
	
	@Test
	void tanReturnsExpectedFunction() {
		assertInstanceOf(SqlTanFunction.class, Sql.tan(intExpr()));
	}
	
	@Test
	void asinReturnsExpectedFunction() {
		assertInstanceOf(SqlAsinFunction.class, Sql.asin(intExpr()));
	}
	
	@Test
	void acosReturnsExpectedFunction() {
		assertInstanceOf(SqlAcosFunction.class, Sql.acos(intExpr()));
	}
	
	@Test
	void atanReturnsExpectedFunction() {
		assertInstanceOf(SqlAtanFunction.class, Sql.atan(intExpr()));
	}
	
	@Test
	void atan2ReturnsExpectedFunction() {
		assertInstanceOf(SqlAtan2Function.class, Sql.atan2(intExpr(), intExpr()));
	}
	
	@Test
	void radiansReturnsExpectedFunction() {
		assertInstanceOf(SqlRadiansFunction.class, Sql.radians(intExpr()));
	}
	
	@Test
	void degreesReturnsExpectedFunction() {
		assertInstanceOf(SqlDegreesFunction.class, Sql.degrees(intExpr()));
	}
	
	@Test
	void bitwiseAndReturnsFunction() {
		assertInstanceOf(SqlBitwiseAndFunction.class, Sql.bitwiseAnd(intExpr(), intExpr()));
	}
	
	@Test
	void bitwiseAndWithTypeReturnsFunction() {
		assertInstanceOf(SqlBitwiseAndFunction.class, Sql.bitwiseAnd(intExpr(), intExpr(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void bitwiseOrReturnsFunction() {
		assertInstanceOf(SqlBitwiseOrFunction.class, Sql.bitwiseOr(intExpr(), intExpr()));
	}
	
	@Test
	void bitwiseOrWithTypeReturnsFunction() {
		assertInstanceOf(SqlBitwiseOrFunction.class, Sql.bitwiseOr(intExpr(), intExpr(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void bitwiseXorReturnsFunction() {
		assertInstanceOf(SqlBitwiseXorFunction.class, Sql.bitwiseXor(intExpr(), intExpr()));
	}
	
	@Test
	void bitwiseXorWithTypeReturnsFunction() {
		assertInstanceOf(SqlBitwiseXorFunction.class, Sql.bitwiseXor(intExpr(), intExpr(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void bitwiseNotReturnsFunction() {
		assertInstanceOf(SqlBitwiseNotFunction.class, Sql.bitwiseNot(intExpr()));
	}
	
	@Test
	void bitwiseNotWithTypeReturnsFunction() {
		assertInstanceOf(SqlBitwiseNotFunction.class, Sql.bitwiseNot(intExpr(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void startsWithReturnsCondition() {
		assertInstanceOf(SqlStartsWithCondition.class, Sql.startsWith(strExpr(), strExpr()));
	}
	
	@Test
	void containsReturnsCondition() {
		assertInstanceOf(SqlContainsCondition.class, Sql.contains(strExpr(), strExpr()));
	}
	
	@Test
	void endsWithReturnsCondition() {
		assertInstanceOf(SqlEndsWithCondition.class, Sql.endsWith(strExpr(), strExpr()));
	}
	
	@Test
	void likeReturnsCondition() {
		assertInstanceOf(SqlLikeCondition.class, Sql.like(strExpr(), strExpr()));
	}
	
	@Test
	void equalsIgnoreCaseReturnsCondition() {
		assertInstanceOf(SqlEqualsIgnoreCaseCondition.class, Sql.equalsIgnoreCase(strExpr(), strExpr()));
	}
	
	@Test
	void lowerReturnsExpectedFunction() {
		assertInstanceOf(SqlLowerFunction.class, Sql.lower(strExpr()));
	}
	
	@Test
	void upperReturnsExpectedFunction() {
		assertInstanceOf(SqlUpperFunction.class, Sql.upper(strExpr()));
	}
	
	@Test
	void trimReturnsExpectedFunction() {
		assertInstanceOf(SqlTrimFunction.class, Sql.trim(strExpr()));
	}
	
	@Test
	void leftTrimReturnsExpectedFunction() {
		assertInstanceOf(SqlLeftTrimFunction.class, Sql.leftTrim(strExpr()));
	}
	
	@Test
	void rightTrimReturnsExpectedFunction() {
		assertInstanceOf(SqlRightTrimFunction.class, Sql.rightTrim(strExpr()));
	}
	
	@Test
	void trimCharsReturnsExpectedFunction() {
		assertInstanceOf(SqlTrimCharsFunction.class, Sql.trimChars(strExpr(), strExpr()));
	}
	
	@Test
	void lengthDefaultTypeReturnsLengthFunction() {
		assertInstanceOf(SqlLengthFunction.class, Sql.length(strExpr()));
	}
	
	@Test
	void lengthWithTypeReturnsLengthFunction() {
		assertInstanceOf(SqlLengthFunction.class, Sql.length(strExpr(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void substringReturnsExpectedFunction() {
		assertInstanceOf(SqlSubstringFunction.class, Sql.substring(strExpr(), intExpr(), intExpr()));
	}
	
	@Test
	void replaceReturnsExpectedFunction() {
		assertInstanceOf(SqlReplaceFunction.class, Sql.replace(strExpr(), strExpr(), strExpr()));
	}
	
	@Test
	void positionReturnsExpectedFunction() {
		assertInstanceOf(SqlPositionFunction.class, Sql.position(strExpr(), strExpr(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void leftReturnsExpectedFunction() {
		assertInstanceOf(SqlLeftFunction.class, Sql.left(strExpr(), intExpr()));
	}
	
	@Test
	void rightReturnsExpectedFunction() {
		assertInstanceOf(SqlRightFunction.class, Sql.right(strExpr(), intExpr()));
	}
	
	@Test
	void leftPadReturnsExpectedFunction() {
		assertInstanceOf(SqlLeftPadFunction.class, Sql.leftPad(strExpr(), intExpr(), strExpr()));
	}
	
	@Test
	void rightPadReturnsExpectedFunction() {
		assertInstanceOf(SqlRightPadFunction.class, Sql.rightPad(strExpr(), intExpr(), strExpr()));
	}
	
	@Test
	void hexDefaultReturnsHexFunction() {
		assertInstanceOf(SqlHexFunction.class, Sql.hex(strExpr()));
	}
	
	@Test
	void hexWithTypeReturnsHexFunction() {
		assertInstanceOf(SqlHexFunction.class, Sql.hex(strExpr(), SqlTestFixtures.STRING_TYPE));
	}
	
	@Test
	void unhexDefaultReturnsUnhexFunction() {
		assertInstanceOf(SqlUnhexFunction.class, Sql.unhex(strExpr()));
	}
	
	@Test
	void unhexWithTypeReturnsUnhexFunction() {
		assertInstanceOf(SqlUnhexFunction.class, Sql.unhex(strExpr(), SqlTypes.LARGE_BYTES));
	}
	
	@Test
	void withinLastReturnsCondition() {
		assertInstanceOf(SqlWithinLastCondition.class, Sql.withinLast(dateExpr(), durationExpr()));
	}
	
	@Test
	void withinNextReturnsCondition() {
		assertInstanceOf(SqlWithinNextCondition.class, Sql.withinNext(dateExpr(), durationExpr()));
	}
	
	@Test
	void beforeReturnsCondition() {
		assertInstanceOf(SqlBeforeCondition.class, Sql.before(dateExpr(), dateExpr()));
	}
	
	@Test
	void afterReturnsCondition() {
		assertInstanceOf(SqlAfterCondition.class, Sql.after(dateExpr(), dateExpr()));
	}
	
	@Test
	void nowDefaultReturnsNowFunction() {
		assertInstanceOf(SqlNowFunction.class, Sql.now());
	}
	
	@Test
	void nowWithTypeReturnsNowFunction() {
		assertInstanceOf(SqlNowFunction.class, Sql.now(INSTANT_TYPE));
	}
	
	@Test
	void currentDateDefaultReturnsFunction() {
		assertInstanceOf(SqlCurrentDateFunction.class, Sql.currentDate());
	}
	
	@Test
	void currentDateWithTypeReturnsFunction() {
		assertInstanceOf(SqlCurrentDateFunction.class, Sql.currentDate(SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void currentTimeDefaultReturnsFunction() {
		assertInstanceOf(SqlCurrentTimeFunction.class, Sql.currentTime());
	}
	
	@Test
	void currentTimeWithTypeReturnsFunction() {
		assertInstanceOf(SqlCurrentTimeFunction.class, Sql.currentTime(LOCAL_TIME_TYPE));
	}
	
	@Test
	void currentTimestampDefaultReturnsFunction() {
		assertInstanceOf(SqlCurrentTimestampFunction.class, Sql.currentTimestamp());
	}
	
	@Test
	void currentTimestampWithTypeReturnsFunction() {
		assertInstanceOf(SqlCurrentTimestampFunction.class, Sql.currentTimestamp(INSTANT_TYPE));
	}
	
	@Test
	void fromEpochReturnsFunction() {
		assertInstanceOf(SqlFromEpochFunction.class, Sql.fromEpoch(intExpr(), LOCAL_DATE_TIME_TYPE));
	}
	
	@Test
	void makeDateReturnsFunction() {
		assertInstanceOf(SqlMakeDateFunction.class, Sql.makeDate(intExpr(), intExpr(), intExpr(), SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void makeTimeReturnsFunction() {
		assertInstanceOf(SqlMakeTimeFunction.class, Sql.makeTime(intExpr(), intExpr(), intExpr(), LOCAL_TIME_TYPE));
	}
	
	@Test
	void extractDefaultTypeReturnsFunction() {
		assertInstanceOf(SqlExtractFunction.class, Sql.extract(dateExpr(), SqlTemporalPart.YEAR));
	}
	
	@Test
	void extractWithTypeReturnsFunction() {
		assertInstanceOf(SqlExtractFunction.class, Sql.extract(dateExpr(), SqlTemporalPart.YEAR, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void truncateTemporalReturnsFunction() {
		assertInstanceOf(SqlTemporalTruncateFunction.class, Sql.truncate(dateExpr(), SqlTemporalPart.DAY, SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void addTemporalReturnsFunction() {
		assertInstanceOf(SqlTemporalAddFunction.class, Sql.add(dateExpr(), SqlTemporalPart.DAY, intExpr(), SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void subtractTemporalReturnsFunction() {
		assertInstanceOf(SqlTemporalSubtractFunction.class, Sql.subtract(dateExpr(), SqlTemporalPart.DAY, intExpr(), SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void toEpochReturnsFunction() {
		assertInstanceOf(SqlToEpochFunction.class, Sql.toEpoch(dateExpr(), SqlTypes.LONG));
	}
	
	@Test
	void toDateReturnsFunction() {
		assertInstanceOf(SqlToDateFunction.class, Sql.toDate(dateExpr(), SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void toTimeReturnsFunction() {
		assertInstanceOf(SqlToTimeFunction.class, Sql.toTime(dateExpr(), LOCAL_TIME_TYPE));
	}
	
	@Test
	void overReturnsWindowedAggregate() {
		assertInstanceOf(SqlWindowedAggregate.class, Sql.over(aggregate(), window()));
	}
	
	@Test
	void rowNumberDefaultReturnsFunction() {
		assertInstanceOf(SqlRowNumberFunction.class, Sql.rowNumber(window()));
	}
	
	@Test
	void rowNumberWithTypeReturnsFunction() {
		assertInstanceOf(SqlRowNumberFunction.class, Sql.rowNumber(window(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void rankDefaultReturnsFunction() {
		assertInstanceOf(SqlRankFunction.class, Sql.rank(window()));
	}
	
	@Test
	void rankWithTypeReturnsFunction() {
		assertInstanceOf(SqlRankFunction.class, Sql.rank(window(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void denseRankDefaultReturnsFunction() {
		assertInstanceOf(SqlDenseRankFunction.class, Sql.denseRank(window()));
	}
	
	@Test
	void denseRankWithTypeReturnsFunction() {
		assertInstanceOf(SqlDenseRankFunction.class, Sql.denseRank(window(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void tileBucketReturnsFunction() {
		assertInstanceOf(SqlTileBucketFunction.class, Sql.tileBucket(4, window()));
	}
	
	@Test
	void tileBucketWithTypeReturnsFunction() {
		assertInstanceOf(SqlTileBucketFunction.class, Sql.tileBucket(4, window(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void lagSimpleReturnsFunction() {
		assertInstanceOf(SqlLagFunction.class, Sql.lag(intExpr(), window()));
	}
	
	@Test
	void lagWithOffsetReturnsFunction() {
		assertInstanceOf(SqlLagFunction.class, Sql.lag(intExpr(), 1, window()));
	}
	
	@Test
	void lagWithOffsetAndDefaultReturnsFunction() {
		assertInstanceOf(SqlLagFunction.class, Sql.lag(intExpr(), 1, 0, SqlTestFixtures.INTEGER_TYPE, window()));
	}
	
	@Test
	void leadSimpleReturnsFunction() {
		assertInstanceOf(SqlLeadFunction.class, Sql.lead(intExpr(), window()));
	}
	
	@Test
	void leadWithOffsetReturnsFunction() {
		assertInstanceOf(SqlLeadFunction.class, Sql.lead(intExpr(), 1, window()));
	}
	
	@Test
	void leadWithOffsetAndDefaultReturnsFunction() {
		assertInstanceOf(SqlLeadFunction.class, Sql.lead(intExpr(), 1, 0, SqlTestFixtures.INTEGER_TYPE, window()));
	}
	
	@Test
	void percentRankDefaultReturnsFunction() {
		assertInstanceOf(SqlPercentRankFunction.class, Sql.percentRank(window()));
	}
	
	@Test
	void percentRankWithTypeReturnsFunction() {
		assertInstanceOf(SqlPercentRankFunction.class, Sql.percentRank(window(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void cumulativeDistributionDefaultReturnsFunction() {
		assertInstanceOf(SqlCumulativeDistributionFunction.class, Sql.cumulativeDistribution(window()));
	}
	
	@Test
	void cumulativeDistributionWithTypeReturnsFunction() {
		assertInstanceOf(SqlCumulativeDistributionFunction.class, Sql.cumulativeDistribution(window(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void firstValueReturnsFunction() {
		assertInstanceOf(SqlFirstValueFunction.class, Sql.firstValue(intExpr(), window()));
	}
	
	@Test
	void lastValueReturnsFunction() {
		assertInstanceOf(SqlLastValueFunction.class, Sql.lastValue(intExpr(), window()));
	}
	
	@Test
	void valueAtReturnsFunction() {
		assertInstanceOf(SqlValueAtFunction.class, Sql.valueAt(intExpr(), 0, window()));
	}
	//endregion
	
	//region Tier 5 - Complex inputs
	@Test
	void greaterThanAndOrEqualUseInclusiveFlagConsistently() {
		SqlCondition exclusive = Sql.greaterThan(intExpr(), intExpr());
		SqlCondition inclusive = Sql.greaterThanOrEqualTo(intExpr(), intExpr());
		assertInstanceOf(SqlGreaterThanCondition.class, exclusive);
		assertInstanceOf(SqlGreaterThanCondition.class, inclusive);
		assertNotEquals(exclusive, inclusive);
	}
	
	@Test
	void lessThanAndOrEqualUseInclusiveFlagConsistently() {
		SqlCondition exclusive = Sql.lessThan(intExpr(), intExpr());
		SqlCondition inclusive = Sql.lessThanOrEqualTo(intExpr(), intExpr());
		assertInstanceOf(SqlLessThanCondition.class, exclusive);
		assertInstanceOf(SqlLessThanCondition.class, inclusive);
		assertNotEquals(exclusive, inclusive);
	}
	
	@Test
	void defaultTypeOverloadsMatchExplicitDefault() {
		assertEquals(SqlTypes.INTEGER, Sql.length(strExpr()).type());
		assertEquals(SqlTypes.LONG, Sql.rowNumber(window()).type());
		assertEquals(SqlTypes.DOUBLE, Sql.percentRank(window()).type());
		assertEquals(SqlTypes.INTEGER, Sql.extract(dateExpr(), SqlTemporalPart.YEAR).type());
	}
	
	@Test
	void nestedExpressionComposition() {
		SqlCondition condition = Sql.equalTo(Sql.abs(intExpr()), Sql.of(0, SqlTestFixtures.INTEGER_TYPE));
		assertNotNull(condition);
		assertInstanceOf(SqlWindowedAggregate.class, Sql.over(aggregate(), window()));
	}
	
	@Test
	void caseWhenComposesConditionAndBranches() {
		SqlExpression<Integer> result = Sql.caseWhen(Sql.isNull(intExpr()), intExpr(), intExpr());
		assertInstanceOf(SqlCaseWhenFunction.class, result);
	}
	//endregion
}
