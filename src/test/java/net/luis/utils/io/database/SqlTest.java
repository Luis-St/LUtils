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

import net.luis.utils.io.database.condition.conditions.comparison.*;
import net.luis.utils.io.database.condition.conditions.numeric.*;
import net.luis.utils.io.database.condition.conditions.string.*;
import net.luis.utils.io.database.condition.conditions.temporal.*;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.client.SqlTypeNotFoundException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.SqlAggregateFunction;
import net.luis.utils.io.database.function.functions.aggregate.*;
import net.luis.utils.io.database.function.functions.generic.*;
import net.luis.utils.io.database.function.functions.numeric.*;
import net.luis.utils.io.database.function.functions.numeric.bitwise.*;
import net.luis.utils.io.database.function.functions.numeric.trigonometric.SqlAtan2Function;
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
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.Temporal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Sql}.<br>
 *
 * @author Luis-St
 */
class SqlTest {
	
	private static final SqlType<String> STRING_TYPE = SqlTypes.STRING.configure(SqlParameter.length(255));
	private static final SqlType<Instant> INSTANT_TYPE = SqlTypes.INSTANT.configure(SqlParameter.fractional(6));
	private static final SqlType<LocalTime> LOCAL_TIME_TYPE = SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(0));
	private static final SqlExpression<Integer> INT = Sql.of(5, SqlTypes.INTEGER);
	private static final SqlExpression<Integer> INT2 = Sql.of(7, SqlTypes.INTEGER);
	private static final SqlExpression<String> STR = Sql.of("a", STRING_TYPE);
	private static final SqlExpression<String> STR2 = Sql.of("b", STRING_TYPE);
	private static final SqlExpression<Instant> TS = Sql.of(Instant.EPOCH, INSTANT_TYPE);
	private static final SqlExpression<Duration> DUR = Sql.of(Duration.ofDays(1), SqlTypes.DURATION);
	private static final SqlWindowClause OVER = SqlWindowClause.of();
	
	private static SqlAggregateFunction<Integer> aggregate() {
		return new SqlSumFunction<>(INT);
	}
	
	private static SqlSelectQuery<Integer> intSelectQuery() {
		SqlTable<Integer> table = SqlTable.create(Integer.class, "nums");
		table.column("id", SqlTypes.INTEGER, i -> 0);
		SqlConnectionSource source = () -> null;
		return new SqlSelectQuery<>(table, SqlDialects.DEFAULT, source, Duration.ofSeconds(30), resultSet -> null);
	}
	
	private static Object valueOf(SqlExpression<?> expression) {
		return ((SqlValueExpression<?>) expression).value();
	}
	
	@Test
	void ofNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.of(null));
	}
	
	@Test
	void ofWithTypeNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.of(null, SqlTypes.INTEGER));
	}
	
	@Test
	void ofWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.of(5, null));
	}
	
	@Test
	void ofUnknownTypeThrows() {
		assertThrows(SqlTypeNotFoundException.class, () -> Sql.of(new Object()));
	}
	
	@Test
	void equalToWithNullFirstExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.equalTo((SqlExpression<Integer>) null, INT));
	}
	
	@Test
	void equalToWithNullSecondExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.equalTo(INT, (SqlExpression<Integer>) null));
	}
	
	@Test
	void equalToValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.equalTo(null, 5));
	}
	
	@Test
	void equalToValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.equalTo(INT, (Integer) null));
	}
	
	@Test
	void isDistinctFromWithNullFirstExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.isDistinctFrom((SqlExpression<Integer>) null, INT));
	}
	
	@Test
	void isDistinctFromWithNullSecondExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.isDistinctFrom(INT, (SqlExpression<Integer>) null));
	}
	
	@Test
	void isDistinctFromValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.isDistinctFrom(null, 5));
	}
	
	@Test
	void isDistinctFromValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.isDistinctFrom(INT, (Integer) null));
	}
	
	@Test
	void greaterThanWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.greaterThan((SqlExpression<Integer>) null, INT));
	}
	
	@Test
	void greaterThanWithNullOtherThrows() {
		assertThrows(NullPointerException.class, () -> Sql.greaterThan(INT, (SqlExpression<Integer>) null));
	}
	
	@Test
	void greaterThanValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.greaterThan(null, 5));
	}
	
	@Test
	void greaterThanValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.greaterThan(INT, (Integer) null));
	}
	
	@Test
	void greaterThanOrEqualToWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.greaterThanOrEqualTo((SqlExpression<Integer>) null, INT));
	}
	
	@Test
	void greaterThanOrEqualToWithNullOtherThrows() {
		assertThrows(NullPointerException.class, () -> Sql.greaterThanOrEqualTo(INT, (SqlExpression<Integer>) null));
	}
	
	@Test
	void greaterThanOrEqualToValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.greaterThanOrEqualTo(null, 5));
	}
	
	@Test
	void greaterThanOrEqualToValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.greaterThanOrEqualTo(INT, (Integer) null));
	}
	
	@Test
	void lessThanWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lessThan((SqlExpression<Integer>) null, INT));
	}
	
	@Test
	void lessThanWithNullOtherThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lessThan(INT, (SqlExpression<Integer>) null));
	}
	
	@Test
	void lessThanValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lessThan(null, 5));
	}
	
	@Test
	void lessThanValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lessThan(INT, (Integer) null));
	}
	
	@Test
	void lessThanOrEqualToWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lessThanOrEqualTo((SqlExpression<Integer>) null, INT));
	}
	
	@Test
	void lessThanOrEqualToWithNullOtherThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lessThanOrEqualTo(INT, (SqlExpression<Integer>) null));
	}
	
	@Test
	void lessThanOrEqualToValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lessThanOrEqualTo(null, 5));
	}
	
	@Test
	void lessThanOrEqualToValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lessThanOrEqualTo(INT, (Integer) null));
	}
	
	@Test
	void betweenWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.between((SqlExpression<Integer>) null, INT, INT2));
	}
	
	@Test
	void betweenWithNullStartThrows() {
		assertThrows(NullPointerException.class, () -> Sql.between(INT, null, INT2));
	}
	
	@Test
	void betweenWithNullEndThrows() {
		assertThrows(NullPointerException.class, () -> Sql.between(INT, INT2, null));
	}
	
	@Test
	void betweenValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.between(null, 1, 2));
	}
	
	@Test
	void betweenValueWithNullStartThrows() {
		assertThrows(NullPointerException.class, () -> Sql.between(INT, null, 2));
	}
	
	@Test
	void betweenValueWithNullEndThrows() {
		assertThrows(NullPointerException.class, () -> Sql.between(INT, 1, null));
	}
	
	@Test
	void equalsIgnoreCaseWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.equalsIgnoreCase(null, STR2));
	}
	
	@Test
	void equalsIgnoreCaseWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.equalsIgnoreCase(STR, (SqlExpression<String>) null));
	}
	
	@Test
	void equalsIgnoreCaseStringWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.equalsIgnoreCase(null, "b"));
	}
	
	@Test
	void equalsIgnoreCaseStringWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.equalsIgnoreCase(STR, (String) null));
	}
	
	@Test
	void isNullWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.isNull(null));
	}
	
	@Test
	void inExpressionsWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.in((SqlExpression<Integer>) null, INT2));
	}
	
	@Test
	void inValuesWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.in(null, 1));
	}
	
	@Test
	void inExpressionsWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> Sql.in(INT, (SqlExpression<Integer>[]) null));
	}
	
	@Test
	void inExpressionsWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.in(INT, INT2, null));
	}
	
	@Test
	void inExpressionsEmptyThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.in(INT, new SqlExpression[0]));
	}
	
	@Test
	void inValuesWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> Sql.in(INT, (Integer[]) null));
	}
	
	@Test
	void inValuesWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.in(INT, 1, null));
	}
	
	@Test
	void inValuesEmptyThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.in(INT, new Integer[0]));
	}
	
	@Test
	void inSubqueryWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.in(null, intSelectQuery()));
	}
	
	@Test
	void inSubqueryWithNullSubqueryThrows() {
		assertThrows(NullPointerException.class, () -> Sql.in(INT, (SqlSelectQuery<Integer>) null));
	}
	
	@Test
	void countDistinctWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.count(null, true));
	}
	
	@Test
	void castWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.cast(null, STRING_TYPE));
	}
	
	@Test
	void castWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.cast(INT, null));
	}
	
	@Test
	void coalesceWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> Sql.coalesce((SqlExpression<Integer>[]) null));
	}
	
	@Test
	void coalesceWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.coalesce(INT, null));
	}
	
	@Test
	void coalesceEmptyThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.coalesce(new SqlExpression[0]));
	}
	
	@Test
	void coalesceTypeMismatchThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.coalesce(INT, (SqlExpression) STR));
	}
	
	@Test
	void nullIfWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.nullIf((SqlExpression<Integer>) null, INT2));
	}
	
	@Test
	void nullIfWithNullCompareValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.nullIf(INT, (SqlExpression<Integer>) null));
	}
	
	@Test
	void nullIfValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.nullIf(null, 5));
	}
	
	@Test
	void nullIfValueWithNullCompareValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.nullIf(INT, (Integer) null));
	}
	
	@Test
	void caseWhenWithNullConditionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.caseWhen(null, STR, STR2));
	}
	
	@Test
	void caseWhenWithNullThenThrows() {
		assertThrows(NullPointerException.class, () -> Sql.caseWhen(Sql.equalTo(INT, INT2), null, STR2));
	}
	
	@Test
	void caseWhenAllowsNullElse() {
		SqlExpression<String> result = assertDoesNotThrow(() -> Sql.caseWhen(Sql.equalTo(INT, INT2), STR, null));
		assertNull(((SqlCaseWhenFunction<?>) result).elseValue());
	}
	
	@Test
	void caseWhenValuesWithNullConditionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.caseWhen(null, 1, 2));
	}
	
	@Test
	void ofUnsafeWithNullNameThrows() {
		assertThrows(NullPointerException.class, () -> Sql.ofUnsafe(null, SqlTypes.INTEGER));
	}
	
	@Test
	void ofUnsafeWithBlankNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.ofUnsafe("  ", SqlTypes.INTEGER));
	}
	
	@Test
	void ofUnsafeWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.ofUnsafe("F", null));
	}
	
	@Test
	void ofUnsafeWithNullArgsArrayThrows() {
		assertThrows(NullPointerException.class, () -> Sql.ofUnsafe("F", SqlTypes.INTEGER, (SqlExpression<?>[]) null));
	}
	
	@Test
	void ofUnsafeWithNullArgElementThrows() { // List.of(args) rejects null elements with NPE before the delegate's IAE guard (Sql.java:159)
		assertThrows(NullPointerException.class, () -> Sql.ofUnsafe("F", SqlTypes.INTEGER, INT, null));
	}
	
	@Test
	void minWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.min(null));
	}
	
	@Test
	void maxWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.max(null));
	}
	
	@Test
	void greatestWithNullFirstExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.greatest(null, INT2));
	}
	
	@Test
	void greatestWithNullSecondExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.greatest(INT, null));
	}
	
	@Test
	void greatestWithNullOthersArrayThrows() {
		assertThrows(NullPointerException.class, () -> Sql.greatest(INT, INT2, (SqlExpression<Integer>[]) null));
	}
	
	@Test
	void greatestWithNullOtherElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.greatest(INT, INT2, INT, null));
	}
	
	@Test
	void leastWithNullFirstExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.least(null, INT2));
	}
	
	@Test
	void leastWithNullSecondExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.least(INT, null));
	}
	
	@Test
	void leastWithNullOthersArrayThrows() {
		assertThrows(NullPointerException.class, () -> Sql.least(INT, INT2, (SqlExpression<Integer>[]) null));
	}
	
	@Test
	void leastWithNullOtherElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.least(INT, INT2, INT, null));
	}
	
	@Test
	void isPositiveWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.isPositive(null));
	}
	
	@Test
	void isNegativeWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.isNegative(null));
	}
	
	@Test
	void isZeroWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.isZero(null));
	}
	
	@Test
	void modEqualsWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.modEquals(null, INT, INT2));
	}
	
	@Test
	void modEqualsWithNullDivisorThrows() {
		assertThrows(NullPointerException.class, () -> Sql.modEquals(INT, null, INT2));
	}
	
	@Test
	void modEqualsWithNullRemainderThrows() {
		assertThrows(NullPointerException.class, () -> Sql.modEquals(INT, INT2, null));
	}
	
	@Test
	void modEqualsValuesWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.modEquals(null, 2, 1));
	}
	
	@Test
	void modEqualsValuesWithNullDivisorThrows() {
		assertThrows(NullPointerException.class, () -> Sql.modEquals(INT, null, 1));
	}
	
	@Test
	void modEqualsValuesWithNullRemainderThrows() {
		assertThrows(NullPointerException.class, () -> Sql.modEquals(INT, 2, null));
	}
	
	@Test
	void addWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.add((SqlExpression<Integer>) null, INT2));
	}
	
	@Test
	void addWithNullOperandThrows() {
		assertThrows(NullPointerException.class, () -> Sql.add(INT, (SqlExpression<? extends Number>) null));
	}
	
	@Test
	void addNumberWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.add((SqlExpression<Integer>) null, 3));
	}
	
	@Test
	void addNumberWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.add(INT, (Number) null));
	}
	
	@Test
	void subtractWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.subtract((SqlExpression<Integer>) null, INT2));
	}
	
	@Test
	void subtractWithNullOperandThrows() {
		assertThrows(NullPointerException.class, () -> Sql.subtract(INT, (SqlExpression<? extends Number>) null));
	}
	
	@Test
	void subtractNumberWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.subtract((SqlExpression<Integer>) null, 3));
	}
	
	@Test
	void subtractNumberWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.subtract(INT, (Number) null));
	}
	
	@Test
	void multiplyWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.multiply((SqlExpression<Integer>) null, INT2));
	}
	
	@Test
	void multiplyWithNullOperandThrows() {
		assertThrows(NullPointerException.class, () -> Sql.multiply(INT, (SqlExpression<? extends Number>) null));
	}
	
	@Test
	void multiplyNumberWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.multiply((SqlExpression<Integer>) null, 3));
	}
	
	@Test
	void multiplyNumberWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.multiply(INT, (Number) null));
	}
	
	@Test
	void divideWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.divide((SqlExpression<Integer>) null, INT2));
	}
	
	@Test
	void divideWithNullOperandThrows() {
		assertThrows(NullPointerException.class, () -> Sql.divide(INT, (SqlExpression<? extends Number>) null));
	}
	
	@Test
	void divideNumberWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.divide((SqlExpression<Integer>) null, 3));
	}
	
	@Test
	void divideNumberWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.divide(INT, (Number) null));
	}
	
	@Test
	void modWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.mod((SqlExpression<Integer>) null, INT2));
	}
	
	@Test
	void modWithNullOperandThrows() {
		assertThrows(NullPointerException.class, () -> Sql.mod(INT, (SqlExpression<? extends Number>) null));
	}
	
	@Test
	void modNumberWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.mod((SqlExpression<Integer>) null, 3));
	}
	
	@Test
	void modNumberWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.mod(INT, (Number) null));
	}
	
	@Test
	void powWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.pow((SqlExpression<Integer>) null, INT2));
	}
	
	@Test
	void powWithNullOperandThrows() {
		assertThrows(NullPointerException.class, () -> Sql.pow(INT, (SqlExpression<? extends Number>) null));
	}
	
	@Test
	void powNumberWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.pow((SqlExpression<Integer>) null, 3));
	}
	
	@Test
	void powNumberWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.pow(INT, (Number) null));
	}
	
	@Test
	void negateWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.negate((SqlExpression<Integer>) null));
	}
	
	@Test
	void sumWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.sum((SqlExpression<Integer>) null));
	}
	
	@Test
	void averageWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.average((SqlExpression<Integer>) null));
	}
	
	@Test
	void absWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.abs((SqlExpression<Integer>) null));
	}
	
	@Test
	void ceilWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.ceil((SqlExpression<Integer>) null));
	}
	
	@Test
	void floorWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.floor((SqlExpression<Integer>) null));
	}
	
	@Test
	void truncateNumericWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.truncate((SqlExpression<Integer>) null));
	}
	
	@Test
	void sqrtWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.sqrt(null));
	}
	
	@Test
	void signWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.sign(null));
	}
	
	@Test
	void expWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.exp(null));
	}
	
	@Test
	void log2WithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.log2(null));
	}
	
	@Test
	void lnWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.ln(null));
	}
	
	@Test
	void log10WithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.log10(null));
	}
	
	@Test
	void sinWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.sin(null));
	}
	
	@Test
	void cosWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.cos(null));
	}
	
	@Test
	void tanWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.tan(null));
	}
	
	@Test
	void asinWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.asin(null));
	}
	
	@Test
	void acosWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.acos(null));
	}
	
	@Test
	void atanWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.atan(null));
	}
	
	@Test
	void radiansWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.radians(null));
	}
	
	@Test
	void degreesWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.degrees(null));
	}
	
	@Test
	void atan2WithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.atan2(null, INT));
	}
	
	@Test
	void atan2WithNullXThrows() {
		assertThrows(NullPointerException.class, () -> Sql.atan2(INT, null));
	}
	
	@Test
	void roundWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.round((SqlExpression<Integer>) null));
	}
	
	@Test
	void roundPrecisionWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.round((SqlExpression<Integer>) null, INT2));
	}
	
	@Test
	void roundPrecisionAllowsNullPrecision() {
		SqlExpression<Integer> result = Sql.round(INT, (SqlExpression<? extends Number>) null);
		assertNull(((SqlRoundFunction<?>) result).precision());
	}
	
	@Test
	void roundNumberWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.round((SqlExpression<Integer>) null, 2));
	}
	
	@Test
	void roundNumberWithNullPrecisionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.round(INT, (Number) null));
	}
	
	@Test
	void bitwiseAndWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(null, INT2));
	}
	
	@Test
	void bitwiseAndWithNullOtherThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(INT, (SqlExpression<Integer>) null));
	}
	
	@Test
	void bitwiseAndValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(null, 3));
	}
	
	@Test
	void bitwiseAndValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(INT, (Integer) null));
	}
	
	@Test
	void bitwiseAndTypedWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(null, INT2, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseAndTypedWithNullOtherThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(INT, (SqlExpression<? extends Number>) null, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseAndTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(INT, INT2, null));
	}
	
	@Test
	void bitwiseAndNumberTypedWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(null, 3, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseAndNumberTypedWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(INT, (Number) null, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseAndNumberTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseAnd(INT, 3, null));
	}
	
	@Test
	void bitwiseOrWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(null, INT2));
	}
	
	@Test
	void bitwiseOrWithNullOtherThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(INT, (SqlExpression<Integer>) null));
	}
	
	@Test
	void bitwiseOrValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(null, 3));
	}
	
	@Test
	void bitwiseOrValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(INT, (Integer) null));
	}
	
	@Test
	void bitwiseOrTypedWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(null, INT2, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseOrTypedWithNullOtherThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(INT, (SqlExpression<? extends Number>) null, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseOrTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(INT, INT2, null));
	}
	
	@Test
	void bitwiseOrNumberTypedWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(null, 3, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseOrNumberTypedWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(INT, (Number) null, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseOrNumberTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseOr(INT, 3, null));
	}
	
	@Test
	void bitwiseXorWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(null, INT2));
	}
	
	@Test
	void bitwiseXorWithNullOtherThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(INT, (SqlExpression<Integer>) null));
	}
	
	@Test
	void bitwiseXorValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(null, 3));
	}
	
	@Test
	void bitwiseXorValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(INT, (Integer) null));
	}
	
	@Test
	void bitwiseXorTypedWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(null, INT2, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseXorTypedWithNullOtherThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(INT, (SqlExpression<? extends Number>) null, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseXorTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(INT, INT2, null));
	}
	
	@Test
	void bitwiseXorNumberTypedWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(null, 3, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseXorNumberTypedWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(INT, (Number) null, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseXorNumberTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseXor(INT, 3, null));
	}
	
	@Test
	void bitwiseNotWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseNot((SqlExpression<Integer>) null));
	}
	
	@Test
	void bitwiseNotTypedWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseNot(null, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseNotTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.bitwiseNot(INT, null));
	}
	
	// string conditions
	@Test
	void startsWithWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.startsWith(null, STR2));
	}
	
	@Test
	void startsWithWithNullArgThrows() {
		assertThrows(NullPointerException.class, () -> Sql.startsWith(STR, (SqlExpression<String>) null));
	}
	
	@Test
	void startsWithStringWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.startsWith(null, "x"));
	}
	
	@Test
	void startsWithStringWithNullStringThrows() {
		assertThrows(NullPointerException.class, () -> Sql.startsWith(STR, (String) null));
	}
	
	@Test
	void containsWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.contains(null, STR2));
	}
	
	@Test
	void containsWithNullArgThrows() {
		assertThrows(NullPointerException.class, () -> Sql.contains(STR, (SqlExpression<String>) null));
	}
	
	@Test
	void containsStringWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.contains(null, "x"));
	}
	
	@Test
	void containsStringWithNullStringThrows() {
		assertThrows(NullPointerException.class, () -> Sql.contains(STR, (String) null));
	}
	
	@Test
	void endsWithWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.endsWith(null, STR2));
	}
	
	@Test
	void endsWithWithNullArgThrows() {
		assertThrows(NullPointerException.class, () -> Sql.endsWith(STR, (SqlExpression<String>) null));
	}
	
	@Test
	void endsWithStringWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.endsWith(null, "x"));
	}
	
	@Test
	void endsWithStringWithNullStringThrows() {
		assertThrows(NullPointerException.class, () -> Sql.endsWith(STR, (String) null));
	}
	
	@Test
	void likeWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.like(null, STR2));
	}
	
	@Test
	void likeWithNullArgThrows() {
		assertThrows(NullPointerException.class, () -> Sql.like(STR, (SqlExpression<String>) null));
	}
	
	@Test
	void likeStringWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.like(null, "x"));
	}
	
	@Test
	void likeStringWithNullStringThrows() {
		assertThrows(NullPointerException.class, () -> Sql.like(STR, (String) null));
	}
	
	@Test
	void lowerWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lower(null));
	}
	
	@Test
	void upperWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.upper(null));
	}
	
	@Test
	void trimWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.trim(null));
	}
	
	@Test
	void leftTrimWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.leftTrim(null));
	}
	
	@Test
	void rightTrimWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.rightTrim(null));
	}
	
	@Test
	void trimCharsWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.trimChars(null, STR2));
	}
	
	@Test
	void trimCharsWithNullCharactersThrows() {
		assertThrows(NullPointerException.class, () -> Sql.trimChars(STR, (SqlExpression<String>) null));
	}
	
	@Test
	void trimCharsStringWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.trimChars(null, "x"));
	}
	
	@Test
	void trimCharsStringWithNullCharactersThrows() {
		assertThrows(NullPointerException.class, () -> Sql.trimChars(STR, (String) null));
	}
	
	@Test
	void lengthWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.length(null));
	}
	
	@Test
	void lengthTypedWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.length(null, SqlTypes.LONG));
	}
	
	@Test
	void lengthTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.length(STR, (SqlType<Long>) null));
	}
	
	@Test
	void substringWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.substring(null, INT, INT2));
	}
	
	@Test
	void substringWithNullStartThrows() {
		assertThrows(NullPointerException.class, () -> Sql.substring(STR, null, INT2));
	}
	
	@Test
	void substringAllowsNullLength() {
		SqlExpression<String> result = Sql.substring(STR, INT, null);
		assertNull(((SqlSubstringFunction<?>) result).length());
	}
	
	@Test
	void substringIntWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.substring(null, 1, 2));
	}
	
	// concat
	@Test
	void concatWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> Sql.concat((SqlExpression<String>[]) null));
	}
	
	@Test
	void concatWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.concat(STR, null));
	}
	
	@Test
	void concatEmptyThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.concat(new SqlExpression[0]));
	}
	
	@Test
	void concatStringsWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> Sql.concat((String[]) null));
	}
	
	@Test
	void concatStringsWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.concat("a", null));
	}
	
	@Test
	void concatStringsEmptyThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.concat(new String[0]));
	}
	
	@Test
	void concatWithSeparatorWithNullSeparatorThrows() {
		assertThrows(NullPointerException.class, () -> Sql.concatWithSeparator(null, STR));
	}
	
	@Test
	void concatWithSeparatorWithEmptySeparatorThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.concatWithSeparator("", STR));
	}
	
	@Test
	void concatWithSeparatorWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.concatWithSeparator(",", STR, null));
	}
	
	@Test
	void concatWithSeparatorEmptyThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.concatWithSeparator(",", new SqlExpression[0]));
	}
	
	@Test
	void concatDistinctWithSeparatorWithNullSeparatorThrows() {
		assertThrows(NullPointerException.class, () -> Sql.concatDistinctWithSeparator(null, STR));
	}
	
	@Test
	void concatDistinctWithSeparatorWithEmptySeparatorThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.concatDistinctWithSeparator("", STR));
	}
	
	@Test
	void concatDistinctWithSeparatorWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.concatDistinctWithSeparator(",", STR, null));
	}
	
	@Test
	void concatDistinctWithSeparatorEmptyThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.concatDistinctWithSeparator(",", new SqlExpression[0]));
	}
	
	@Test
	void concatOrderedWithSeparatorWithNullSeparatorThrows() {
		assertThrows(NullPointerException.class, () -> Sql.concatOrderedWithSeparator(null, STR));
	}
	
	@Test
	void concatOrderedWithSeparatorWithEmptySeparatorThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.concatOrderedWithSeparator("", STR));
	}
	
	@Test
	void concatOrderedWithSeparatorWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.concatOrderedWithSeparator(",", STR, null));
	}
	
	@Test
	void concatOrderedWithSeparatorEmptyThrows() {
		assertThrows(IllegalArgumentException.class, () -> Sql.concatOrderedWithSeparator(",", new SqlExpression[0]));
	}
	
	@Test
	void replaceWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.replace(null, STR, STR2));
	}
	
	@Test
	void replaceWithNullSearchThrows() {
		assertThrows(NullPointerException.class, () -> Sql.replace(STR, null, STR2));
	}
	
	@Test
	void replaceWithNullReplacementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.replace(STR, STR2, null));
	}
	
	@Test
	void replaceStringsWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.replace(null, "a", "b"));
	}
	
	@Test
	void replaceStringsWithNullSearchThrows() {
		assertThrows(NullPointerException.class, () -> Sql.replace(STR, null, "b"));
	}
	
	@Test
	void replaceStringsWithNullReplacementThrows() {
		assertThrows(NullPointerException.class, () -> Sql.replace(STR, "a", null));
	}
	
	@Test
	void positionWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.position(null, STR2, SqlTypes.INTEGER));
	}
	
	@Test
	void positionWithNullSubstringThrows() {
		assertThrows(NullPointerException.class, () -> Sql.position(STR, null, SqlTypes.INTEGER));
	}
	
	@Test
	void positionWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.position(STR, STR2, (SqlType<Integer>) null));
	}
	
	@Test
	void leftWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.left(null, INT));
	}
	
	@Test
	void leftWithNullNThrows() {
		assertThrows(NullPointerException.class, () -> Sql.left(STR, null));
	}
	
	@Test
	void leftIntWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.left(null, 3));
	}
	
	@Test
	void rightWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.right(null, INT));
	}
	
	@Test
	void rightWithNullNThrows() {
		assertThrows(NullPointerException.class, () -> Sql.right(STR, null));
	}
	
	@Test
	void rightIntWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.right(null, 3));
	}
	
	@Test
	void leftPadWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.leftPad(null, INT, STR2));
	}
	
	@Test
	void leftPadWithNullLengthThrows() {
		assertThrows(NullPointerException.class, () -> Sql.leftPad(STR, null, STR2));
	}
	
	@Test
	void leftPadWithNullFillThrows() {
		assertThrows(NullPointerException.class, () -> Sql.leftPad(STR, INT, null));
	}
	
	@Test
	void leftPadIntWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.leftPad(null, 3, "x"));
	}
	
	@Test
	void leftPadIntWithNullFillThrows() {
		assertThrows(NullPointerException.class, () -> Sql.leftPad(STR, 3, null));
	}
	
	@Test
	void rightPadWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.rightPad(null, INT, STR2));
	}
	
	@Test
	void rightPadWithNullLengthThrows() {
		assertThrows(NullPointerException.class, () -> Sql.rightPad(STR, null, STR2));
	}
	
	@Test
	void rightPadWithNullFillThrows() {
		assertThrows(NullPointerException.class, () -> Sql.rightPad(STR, INT, null));
	}
	
	@Test
	void rightPadIntWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.rightPad(null, 3, "x"));
	}
	
	@Test
	void rightPadIntWithNullFillThrows() {
		assertThrows(NullPointerException.class, () -> Sql.rightPad(STR, 3, null));
	}
	
	@Test
	void hexWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.hex(null));
	}
	
	@Test
	void hexTypedWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.hex(null, STRING_TYPE));
	}
	
	@Test
	void hexTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.hex(STR, null));
	}
	
	@Test
	void unhexWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.unhex(null));
	}
	
	@Test
	void unhexTypedWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.unhex(null, STRING_TYPE));
	}
	
	@Test
	void unhexTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.unhex(STR, (SqlType<String>) null));
	}
	
	@Test
	void withinLastWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.withinLast(null, DUR));
	}
	
	@Test
	void withinLastWithNullArgThrows() {
		assertThrows(NullPointerException.class, () -> Sql.withinLast(TS, (SqlExpression<Duration>) null));
	}
	
	@Test
	void withinLastValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.withinLast(null, Duration.ofDays(1)));
	}
	
	@Test
	void withinLastValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.withinLast(TS, (Duration) null));
	}
	
	@Test
	void withinNextWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.withinNext(null, DUR));
	}
	
	@Test
	void withinNextWithNullArgThrows() {
		assertThrows(NullPointerException.class, () -> Sql.withinNext(TS, (SqlExpression<Duration>) null));
	}
	
	@Test
	void withinNextValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.withinNext(null, Duration.ofDays(1)));
	}
	
	@Test
	void withinNextValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.withinNext(TS, (Duration) null));
	}
	
	@Test
	void beforeWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.before(null, TS));
	}
	
	@Test
	void beforeWithNullArgThrows() {
		assertThrows(NullPointerException.class, () -> Sql.before(TS, (SqlExpression<? extends Temporal>) null));
	}
	
	@Test
	void beforeValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.before(null, Instant.EPOCH));
	}
	
	@Test
	void beforeValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.before(TS, (Temporal) null));
	}
	
	@Test
	void afterWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.after(null, TS));
	}
	
	@Test
	void afterWithNullArgThrows() {
		assertThrows(NullPointerException.class, () -> Sql.after(TS, (SqlExpression<? extends Temporal>) null));
	}
	
	@Test
	void afterValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.after(null, Instant.EPOCH));
	}
	
	@Test
	void afterValueWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> Sql.after(TS, (Temporal) null));
	}
	
	@Test
	void nowTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.now(null));
	}
	
	@Test
	void currentDateTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.currentDate(null));
	}
	
	@Test
	void currentTimeTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.currentTime(null));
	}
	
	@Test
	void currentTimestampTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.currentTimestamp(null));
	}
	
	@Test
	void fromEpochWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.fromEpoch(null, INSTANT_TYPE));
	}
	
	@Test
	void fromEpochWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.fromEpoch(INT, (SqlType<Instant>) null));
	}
	
	@Test
	void makeDateWithNullYearThrows() {
		assertThrows(NullPointerException.class, () -> Sql.makeDate(null, INT, INT, SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void makeDateWithNullMonthThrows() {
		assertThrows(NullPointerException.class, () -> Sql.makeDate(INT, null, INT, SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void makeDateWithNullDayThrows() {
		assertThrows(NullPointerException.class, () -> Sql.makeDate(INT, INT, null, SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void makeDateWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.makeDate(INT, INT, INT, (SqlType<LocalDate>) null));
	}
	
	@Test
	void makeDateIntWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.makeDate(2020, 1, 1, (SqlType<LocalDate>) null));
	}
	
	@Test
	void makeTimeWithNullHourThrows() {
		assertThrows(NullPointerException.class, () -> Sql.makeTime(null, INT, INT, LOCAL_TIME_TYPE));
	}
	
	@Test
	void makeTimeWithNullMinuteThrows() {
		assertThrows(NullPointerException.class, () -> Sql.makeTime(INT, null, INT, LOCAL_TIME_TYPE));
	}
	
	@Test
	void makeTimeWithNullSecondThrows() {
		assertThrows(NullPointerException.class, () -> Sql.makeTime(INT, INT, null, LOCAL_TIME_TYPE));
	}
	
	@Test
	void makeTimeWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.makeTime(INT, INT, INT, (SqlType<LocalTime>) null));
	}
	
	@Test
	void makeTimeIntWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.makeTime(1, 2, 3, (SqlType<LocalTime>) null));
	}
	
	@Test
	void extractWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.extract(null, SqlTemporalPart.DAY));
	}
	
	@Test
	void extractWithNullPartThrows() {
		assertThrows(NullPointerException.class, () -> Sql.extract(TS, null));
	}
	
	@Test
	void extractTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.extract(TS, SqlTemporalPart.DAY, (SqlType<Integer>) null));
	}
	
	@Test
	void temporalTruncateWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.truncate(null, SqlTemporalPart.DAY, INSTANT_TYPE));
	}
	
	@Test
	void temporalTruncateWithNullPartThrows() {
		assertThrows(NullPointerException.class, () -> Sql.truncate(TS, null, INSTANT_TYPE));
	}
	
	@Test
	void temporalTruncateWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.truncate(TS, SqlTemporalPart.DAY, null));
	}
	
	@Test
	void temporalAddWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.add(null, SqlTemporalPart.DAY, INT, INSTANT_TYPE));
	}
	
	@Test
	void temporalAddWithNullPartThrows() {
		assertThrows(NullPointerException.class, () -> Sql.add(TS, null, INT, INSTANT_TYPE));
	}
	
	@Test
	void temporalAddWithNullAmountThrows() {
		assertThrows(NullPointerException.class, () -> Sql.add(TS, SqlTemporalPart.DAY, null, INSTANT_TYPE));
	}
	
	@Test
	void temporalAddWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.add(TS, SqlTemporalPart.DAY, INT, null));
	}
	
	@Test
	void temporalAddIntWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.add(null, SqlTemporalPart.DAY, 1, INSTANT_TYPE));
	}
	
	@Test
	void temporalAddIntWithNullPartThrows() {
		assertThrows(NullPointerException.class, () -> Sql.add(TS, null, 1, INSTANT_TYPE));
	}
	
	@Test
	void temporalAddIntWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.add(TS, SqlTemporalPart.DAY, 1, null));
	}
	
	@Test
	void temporalSubtractWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.subtract(null, SqlTemporalPart.DAY, INT, INSTANT_TYPE));
	}
	
	@Test
	void temporalSubtractWithNullPartThrows() {
		assertThrows(NullPointerException.class, () -> Sql.subtract(TS, null, INT, INSTANT_TYPE));
	}
	
	@Test
	void temporalSubtractWithNullAmountThrows() {
		assertThrows(NullPointerException.class, () -> Sql.subtract(TS, SqlTemporalPart.DAY, null, INSTANT_TYPE));
	}
	
	@Test
	void temporalSubtractWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.subtract(TS, SqlTemporalPart.DAY, INT, null));
	}
	
	@Test
	void temporalSubtractIntWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.subtract(null, SqlTemporalPart.DAY, 1, INSTANT_TYPE));
	}
	
	@Test
	void temporalSubtractIntWithNullPartThrows() {
		assertThrows(NullPointerException.class, () -> Sql.subtract(TS, null, 1, INSTANT_TYPE));
	}
	
	@Test
	void temporalSubtractIntWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.subtract(TS, SqlTemporalPart.DAY, 1, null));
	}
	
	@Test
	void toEpochWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.toEpoch(null, SqlTypes.LONG));
	}
	
	@Test
	void toEpochWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.toEpoch(TS, (SqlType<Long>) null));
	}
	
	@Test
	void toDateWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.toDate(null, SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void toDateWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.toDate(TS, (SqlType<LocalDate>) null));
	}
	
	@Test
	void toTimeWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.toTime(null, LOCAL_TIME_TYPE));
	}
	
	@Test
	void toTimeWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.toTime(TS, (SqlType<LocalTime>) null));
	}
	
	@Test
	void overWithNullAggregateThrows() {
		assertThrows(NullPointerException.class, () -> Sql.over(null, OVER));
	}
	
	@Test
	void overWithNullClauseThrows() {
		assertThrows(NullPointerException.class, () -> Sql.over(aggregate(), null));
	}
	
	@Test
	void rowNumberWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.rowNumber(null));
	}
	
	@Test
	void rowNumberTypedWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.rowNumber(null, SqlTypes.INTEGER));
	}
	
	@Test
	void rowNumberTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.rowNumber(OVER, (SqlType<Integer>) null));
	}
	
	@Test
	void rankWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.rank(null));
	}
	
	@Test
	void rankTypedWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.rank(null, SqlTypes.INTEGER));
	}
	
	@Test
	void rankTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.rank(OVER, (SqlType<Integer>) null));
	}
	
	@Test
	void denseRankWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.denseRank(null));
	}
	
	@Test
	void denseRankTypedWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.denseRank(null, SqlTypes.INTEGER));
	}
	
	@Test
	void denseRankTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.denseRank(OVER, (SqlType<Integer>) null));
	}
	
	@Test
	void percentRankWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.percentRank(null));
	}
	
	@Test
	void percentRankTypedWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.percentRank(null, SqlTypes.DOUBLE));
	}
	
	@Test
	void percentRankTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.percentRank(OVER, (SqlType<Double>) null));
	}
	
	@Test
	void cumulativeDistributionWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.cumulativeDistribution(null));
	}
	
	@Test
	void cumulativeDistributionTypedWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.cumulativeDistribution(null, SqlTypes.DOUBLE));
	}
	
	@Test
	void cumulativeDistributionTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.cumulativeDistribution(OVER, (SqlType<Double>) null));
	}
	
	@Test
	void tileBucketWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.tileBucket(4, null));
	}
	
	@Test
	void tileBucketTypedWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.tileBucket(4, null, SqlTypes.LONG));
	}
	
	@Test
	void tileBucketTypedWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.tileBucket(4, OVER, (SqlType<Long>) null));
	}
	
	@Test
	void lagWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lag((SqlExpression<Integer>) null, OVER));
	}
	
	@Test
	void lagWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lag(INT, null));
	}
	
	@Test
	void lagOffsetWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lag((SqlExpression<Integer>) null, 1, OVER));
	}
	
	@Test
	void lagOffsetWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lag(INT, 1, null));
	}
	
	@Test
	void lagFullWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lag(null, 1, 9, SqlTypes.INTEGER, OVER));
	}
	
	@Test
	void lagFullWithNullDefaultThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lag(INT, 1, null, SqlTypes.INTEGER, OVER));
	}
	
	@Test
	void lagFullWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lag(INT, 1, 9, null, OVER));
	}
	
	@Test
	void lagFullWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lag(INT, 1, 9, SqlTypes.INTEGER, null));
	}
	
	@Test
	void leadWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lead((SqlExpression<Integer>) null, OVER));
	}
	
	@Test
	void leadWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lead(INT, null));
	}
	
	@Test
	void leadOffsetWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lead((SqlExpression<Integer>) null, 1, OVER));
	}
	
	@Test
	void leadOffsetWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lead(INT, 1, null));
	}
	
	@Test
	void leadFullWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lead(null, 1, 9, SqlTypes.INTEGER, OVER));
	}
	
	@Test
	void leadFullWithNullDefaultThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lead(INT, 1, null, SqlTypes.INTEGER, OVER));
	}
	
	@Test
	void leadFullWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lead(INT, 1, 9, null, OVER));
	}
	
	@Test
	void leadFullWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lead(INT, 1, 9, SqlTypes.INTEGER, null));
	}
	
	@Test
	void firstValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.firstValue(null, OVER));
	}
	
	@Test
	void firstValueWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.firstValue(INT, null));
	}
	
	@Test
	void lastValueWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lastValue(null, OVER));
	}
	
	@Test
	void lastValueWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.lastValue(INT, null));
	}
	
	@Test
	void valueAtWithNullExpressionThrows() {
		assertThrows(NullPointerException.class, () -> Sql.valueAt(null, 1, OVER));
	}
	
	@Test
	void valueAtWithNullOverThrows() {
		assertThrows(NullPointerException.class, () -> Sql.valueAt(INT, 1, null));
	}
	
	@Test
	void countWithoutDistinct() {
		SqlExpression<Long> result = Sql.count(INT, false);
		assertInstanceOf(SqlCountFunction.class, result);
		assertEquals(Long.class, result.type().javaType());
	}
	
	@Test
	void countWithDistinct() {
		assertInstanceOf(SqlCountDistinctFunction.class, Sql.count(INT, true));
	}
	
	@Test
	void countWithoutDistinctAllowsNullExpression() {
		SqlExpression<Long> result = assertDoesNotThrow(() -> Sql.count(null, false));
		assertInstanceOf(SqlCountFunction.class, result);
	}
	
	@Test
	void greaterThanIsStrict() {
		assertFalse(((SqlGreaterThanCondition) Sql.greaterThan(INT, INT2)).equalTo());
	}
	
	@Test
	void greaterThanOrEqualToIsInclusive() {
		assertTrue(((SqlGreaterThanCondition) Sql.greaterThanOrEqualTo(INT, INT2)).equalTo());
	}
	
	@Test
	void lessThanIsStrict() {
		assertFalse(((SqlLessThanCondition) Sql.lessThan(INT, INT2)).equalTo());
	}
	
	@Test
	void lessThanOrEqualToIsInclusive() {
		assertTrue(((SqlLessThanCondition) Sql.lessThanOrEqualTo(INT, INT2)).equalTo());
	}
	
	@Test
	void roundWithoutPrecisionHasNoPrecision() {
		assertNull(((SqlRoundFunction<?>) Sql.round(INT)).precision());
	}
	
	@Test
	void roundWithPrecisionSetsPrecision() {
		assertNotNull(((SqlRoundFunction<?>) Sql.round(INT, INT2)).precision());
	}
	
	@Test
	void concatHasNoSeparator() {
		SqlConcatFunction<?> result = (SqlConcatFunction<?>) Sql.concat(STR, STR2);
		assertTrue(result.separator().isEmpty());
		assertFalse(result.distinct());
		assertFalse(result.ordered());
	}
	
	@Test
	void concatWithSeparatorSetsSeparator() {
		SqlConcatFunction<?> result = (SqlConcatFunction<?>) Sql.concatWithSeparator(",", STR);
		assertTrue(result.separator().isPresent());
		assertFalse(result.distinct());
		assertFalse(result.ordered());
	}
	
	@Test
	void concatDistinctWithSeparatorSetsDistinct() {
		SqlConcatFunction<?> result = (SqlConcatFunction<?>) Sql.concatDistinctWithSeparator(",", STR);
		assertTrue(result.distinct());
		assertFalse(result.ordered());
	}
	
	@Test
	void concatOrderedWithSeparatorSetsOrdered() {
		SqlConcatFunction<?> result = (SqlConcatFunction<?>) Sql.concatOrderedWithSeparator(",", STR);
		assertFalse(result.distinct());
		assertTrue(result.ordered());
	}
	
	@Test
	void lagWithoutOffsetLeavesOffsetNull() {
		SqlLagFunction<?> result = (SqlLagFunction<?>) Sql.lag(INT, OVER);
		assertNull(result.offset());
		assertNull(result.defaultValue());
	}
	
	@Test
	void lagWithOffsetSetsOffset() {
		SqlLagFunction<?> result = (SqlLagFunction<?>) Sql.lag(INT, 2, OVER);
		assertNotNull(result.offset());
		assertNull(result.defaultValue());
	}
	
	@Test
	void lagWithOffsetAndDefaultSetsAll() {
		SqlLagFunction<?> result = (SqlLagFunction<?>) Sql.lag(INT, 2, 9, SqlTypes.INTEGER, OVER);
		assertNotNull(result.offset());
		assertNotNull(result.defaultValue());
	}
	
	@Test
	void leadWithoutOffsetLeavesOffsetNull() {
		SqlLeadFunction<?> result = (SqlLeadFunction<?>) Sql.lead(INT, OVER);
		assertNull(result.offset());
		assertNull(result.defaultValue());
	}
	
	@Test
	void leadWithOffsetSetsOffset() {
		SqlLeadFunction<?> result = (SqlLeadFunction<?>) Sql.lead(INT, 2, OVER);
		assertNotNull(result.offset());
		assertNull(result.defaultValue());
	}
	
	@Test
	void leadWithOffsetAndDefaultSetsAll() {
		SqlLeadFunction<?> result = (SqlLeadFunction<?>) Sql.lead(INT, 2, 9, SqlTypes.INTEGER, OVER);
		assertNotNull(result.offset());
		assertNotNull(result.defaultValue());
	}
	
	@Test
	void rowNumberDefaultsToLong() {
		assertEquals(Long.class, Sql.rowNumber(OVER).type().javaType());
	}
	
	@Test
	void rowNumberUsesGivenType() {
		assertEquals(Integer.class, Sql.rowNumber(OVER, SqlTypes.INTEGER).type().javaType());
	}
	
	@Test
	void rankDefaultsToLong() {
		assertEquals(Long.class, Sql.rank(OVER).type().javaType());
	}
	
	@Test
	void rankUsesGivenType() {
		assertEquals(Integer.class, Sql.rank(OVER, SqlTypes.INTEGER).type().javaType());
	}
	
	@Test
	void denseRankDefaultsToLong() {
		assertEquals(Long.class, Sql.denseRank(OVER).type().javaType());
	}
	
	@Test
	void denseRankUsesGivenType() {
		assertEquals(Integer.class, Sql.denseRank(OVER, SqlTypes.INTEGER).type().javaType());
	}
	
	@Test
	void percentRankDefaultsToDouble() {
		assertEquals(Double.class, Sql.percentRank(OVER).type().javaType());
	}
	
	@Test
	void percentRankUsesGivenType() {
		assertEquals(Integer.class, Sql.percentRank(OVER, SqlTypes.INTEGER).type().javaType());
	}
	
	@Test
	void cumulativeDistributionDefaultsToDouble() {
		assertEquals(Double.class, Sql.cumulativeDistribution(OVER).type().javaType());
	}
	
	@Test
	void cumulativeDistributionUsesGivenType() {
		assertEquals(Integer.class, Sql.cumulativeDistribution(OVER, SqlTypes.INTEGER).type().javaType());
	}
	
	@Test
	void tileBucketDefaultsToLong() {
		assertEquals(Long.class, Sql.tileBucket(4, OVER).type().javaType());
	}
	
	@Test
	void tileBucketUsesGivenType() {
		assertEquals(Integer.class, Sql.tileBucket(4, OVER, SqlTypes.INTEGER).type().javaType());
	}
	
	@Test
	void lengthDefaultsToInteger() {
		assertEquals(Integer.class, Sql.length(STR).type().javaType());
	}
	
	@Test
	void lengthUsesGivenType() {
		assertEquals(Long.class, Sql.length(STR, SqlTypes.LONG).type().javaType());
	}
	
	@Test
	void extractDefaultsToInteger() {
		assertEquals(Integer.class, Sql.extract(TS, SqlTemporalPart.DAY).type().javaType());
	}
	
	@Test
	void extractUsesGivenType() {
		assertEquals(Long.class, Sql.extract(TS, SqlTemporalPart.DAY, SqlTypes.LONG).type().javaType());
	}
	
	@Test
	void unhexDefaultsToLargeBytes() {
		assertEquals(byte[].class, Sql.unhex(STR).type().javaType());
	}
	
	@Test
	void unhexUsesGivenType() {
		assertEquals(String.class, Sql.unhex(STR, STRING_TYPE).type().javaType());
	}
	
	@Test
	void nowDefaultsToInstant() {
		assertEquals(Instant.class, Sql.now().type().javaType());
	}
	
	@Test
	void nowUsesGivenType() {
		assertEquals(LocalDate.class, Sql.now(SqlTypes.LOCAL_DATE).type().javaType());
	}
	
	@Test
	void currentDateDefaultsToLocalDate() {
		assertEquals(LocalDate.class, Sql.currentDate().type().javaType());
	}
	
	@Test
	void currentDateUsesGivenType() {
		assertEquals(Instant.class, Sql.currentDate(INSTANT_TYPE).type().javaType());
	}
	
	@Test
	void currentTimeDefaultsToLocalTime() {
		assertEquals(LocalTime.class, Sql.currentTime().type().javaType());
	}
	
	@Test
	void currentTimeUsesGivenType() {
		assertEquals(Instant.class, Sql.currentTime(INSTANT_TYPE).type().javaType());
	}
	
	@Test
	void currentTimestampDefaultsToInstant() {
		assertEquals(Instant.class, Sql.currentTimestamp().type().javaType());
	}
	
	@Test
	void currentTimestampUsesGivenType() {
		assertEquals(LocalDate.class, Sql.currentTimestamp(SqlTypes.LOCAL_DATE).type().javaType());
	}
	
	@Test
	void inExpressionsSingleOption() {
		assertEquals(1, ((SqlInListCondition) Sql.in(INT, INT2)).options().size());
	}
	
	@Test
	void inExpressionsMultipleOptions() {
		assertEquals(3, ((SqlInListCondition) Sql.in(INT, INT2, INT, INT2)).options().size());
	}
	
	@Test
	void coalesceSingleExpression() {
		assertEquals(1, ((SqlCoalesceFunction<?>) Sql.coalesce(INT)).expressions().size());
	}
	
	@Test
	void coalesceMultipleExpressions() {
		assertEquals(3, ((SqlCoalesceFunction<?>) Sql.coalesce(INT, INT2, INT)).expressions().size());
	}
	
	@Test
	void concatSingleValue() {
		assertEquals(1, ((SqlConcatFunction<?>) Sql.concat(STR)).expressions().size());
	}
	
	@Test
	void concatMultipleValues() {
		assertEquals(3, ((SqlConcatFunction<?>) Sql.concat(STR, STR2, STR)).expressions().size());
	}
	
	@Test
	void greatestWithoutExtraOthers() {
		assertEquals(2, ((SqlGreatestFunction<?>) Sql.greatest(INT, INT2)).expressions().size());
	}
	
	@Test
	void greatestWithExtraOthers() {
		assertEquals(4, ((SqlGreatestFunction<?>) Sql.greatest(INT, INT2, INT, INT2)).expressions().size());
	}
	
	@Test
	void leastWithoutExtraOthers() {
		assertEquals(2, ((SqlLeastFunction<?>) Sql.least(INT, INT2)).expressions().size());
	}
	
	@Test
	void leastWithExtraOthers() {
		assertEquals(4, ((SqlLeastFunction<?>) Sql.least(INT, INT2, INT, INT2)).expressions().size());
	}
	
	@Test
	void ofUnsafeWithoutArgs() {
		assertInstanceOf(SqlUnsafeFunction.class, Sql.ofUnsafe("F", SqlTypes.INTEGER));
	}
	
	@Test
	void ofUnsafeWithArgs() {
		assertInstanceOf(SqlUnsafeFunction.class, Sql.ofUnsafe("F", SqlTypes.INTEGER, INT, INT2));
	}
	
	@Test
	void ofInfersIntegerType() throws SqlTypeNotFoundException {
		assertEquals(Integer.class, Sql.of(5).type().javaType());
	}
	
	@Test
	void ofWithExplicitType() {
		assertSame(SqlTypes.INTEGER, Sql.of(5, SqlTypes.INTEGER).type());
	}
	
	@Test
	void equalToBuildsCondition() {
		assertInstanceOf(SqlEqualToCondition.class, Sql.equalTo(INT, INT2));
	}
	
	@Test
	void equalToValueWrapsExpression() {
		assertEquals(9, valueOf(((SqlEqualToCondition) Sql.equalTo(INT, 9)).second()));
	}
	
	@Test
	void isDistinctFromBuildsCondition() {
		assertInstanceOf(SqlIsDistinctFromCondition.class, Sql.isDistinctFrom(INT, INT2));
	}
	
	@Test
	void isDistinctFromValue() {
		assertInstanceOf(SqlIsDistinctFromCondition.class, Sql.isDistinctFrom(INT, 9));
	}
	
	@Test
	void greaterThanBuildsCondition() {
		assertInstanceOf(SqlGreaterThanCondition.class, Sql.greaterThan(INT, INT2));
	}
	
	@Test
	void greaterThanValue() {
		assertInstanceOf(SqlGreaterThanCondition.class, Sql.greaterThan(INT, 9));
	}
	
	@Test
	void greaterThanOrEqualToValue() {
		assertInstanceOf(SqlGreaterThanCondition.class, Sql.greaterThanOrEqualTo(INT, 9));
	}
	
	@Test
	void lessThanBuildsCondition() {
		assertInstanceOf(SqlLessThanCondition.class, Sql.lessThan(INT, INT2));
	}
	
	@Test
	void lessThanValue() {
		assertInstanceOf(SqlLessThanCondition.class, Sql.lessThan(INT, 9));
	}
	
	@Test
	void lessThanOrEqualToValue() {
		assertInstanceOf(SqlLessThanCondition.class, Sql.lessThanOrEqualTo(INT, 9));
	}
	
	@Test
	void betweenBuildsCondition() {
		assertInstanceOf(SqlBetweenCondition.class, Sql.between(INT, INT2, INT));
	}
	
	@Test
	void betweenValues() {
		assertInstanceOf(SqlBetweenCondition.class, Sql.between(INT, 1, 9));
	}
	
	@Test
	void equalsIgnoreCaseBuildsCondition() {
		assertInstanceOf(SqlEqualsIgnoreCaseCondition.class, Sql.equalsIgnoreCase(STR, STR2));
	}
	
	@Test
	void equalsIgnoreCaseString() {
		assertInstanceOf(SqlEqualsIgnoreCaseCondition.class, Sql.equalsIgnoreCase(STR, "b"));
	}
	
	@Test
	void isNullBuildsCondition() {
		assertInstanceOf(SqlIsNullCondition.class, Sql.isNull(INT));
	}
	
	@Test
	void inExpressionsBuildsCondition() {
		assertInstanceOf(SqlInListCondition.class, Sql.in(INT, INT2));
	}
	
	@Test
	void inValuesWrapsEachValue() {
		assertEquals(2, ((SqlInListCondition) Sql.in(INT, 1, 2)).options().size());
	}
	
	@Test
	void inSubqueryBuildsCondition() {
		assertInstanceOf(SqlInQueryCondition.class, Sql.in(INT, intSelectQuery()));
	}
	
	@Test
	void castBuildsFunction() {
		assertInstanceOf(SqlCastFunction.class, Sql.cast(INT, STRING_TYPE));
	}
	
	@Test
	void nullIfBuildsFunction() {
		assertInstanceOf(SqlNullIfFunction.class, Sql.nullIf(INT, INT2));
	}
	
	@Test
	void nullIfValue() {
		assertInstanceOf(SqlNullIfFunction.class, Sql.nullIf(INT, 9));
	}
	
	@Test
	void caseWhenBuildsFunction() {
		assertInstanceOf(SqlCaseWhenFunction.class, Sql.caseWhen(Sql.equalTo(INT, INT2), STR, STR2));
	}
	
	@Test
	void caseWhenValuesWrapBoth() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlCaseWhenFunction.class, Sql.caseWhen(Sql.equalTo(INT, INT2), "a", "b"));
	}
	
	@Test
	void ofUnsafeBuildsFunction() {
		assertInstanceOf(SqlUnsafeFunction.class, Sql.ofUnsafe("F", SqlTypes.INTEGER, INT));
	}
	
	@Test
	void minBuildsFunction() {
		SqlExpression<Integer> result = Sql.min(INT);
		assertInstanceOf(SqlMinFunction.class, result);
		assertEquals(Integer.class, result.type().javaType());
	}
	
	@Test
	void maxBuildsFunction() {
		SqlExpression<Integer> result = Sql.max(INT);
		assertInstanceOf(SqlMaxFunction.class, result);
		assertEquals(Integer.class, result.type().javaType());
	}
	
	@Test
	void isPositiveBuildsCondition() {
		assertInstanceOf(SqlIsPositiveCondition.class, Sql.isPositive(INT));
	}
	
	@Test
	void isNegativeBuildsCondition() {
		assertInstanceOf(SqlIsNegativeCondition.class, Sql.isNegative(INT));
	}
	
	@Test
	void isZeroBuildsCondition() {
		assertInstanceOf(SqlIsZeroCondition.class, Sql.isZero(INT));
	}
	
	@Test
	void modEqualsBuildsCondition() {
		assertInstanceOf(SqlModEqualsCondition.class, Sql.modEquals(INT, INT2, INT));
	}
	
	@Test
	void modEqualsValues() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlModEqualsCondition.class, Sql.modEquals(INT, 2, 1));
	}
	
	@Test
	void randomBuildsDoubleFunction() {
		assertEquals(Double.class, Sql.random().type().javaType());
	}
	
	@Test
	void piBuildsDoubleFunction() {
		assertEquals(Double.class, Sql.pi().type().javaType());
	}
	
	@Test
	void addBuildsFunction() {
		assertInstanceOf(SqlNumericAddFunction.class, Sql.add(INT, INT2));
	}
	
	@Test
	void addNumber() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlNumericAddFunction.class, Sql.add(INT, 3));
	}
	
	@Test
	void subtractBuildsFunction() {
		assertInstanceOf(SqlNumericSubtractFunction.class, Sql.subtract(INT, INT2));
	}
	
	@Test
	void subtractNumber() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlNumericSubtractFunction.class, Sql.subtract(INT, 3));
	}
	
	@Test
	void multiplyBuildsFunction() {
		assertInstanceOf(SqlNumericMultiplyFunction.class, Sql.multiply(INT, INT2));
	}
	
	@Test
	void multiplyNumber() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlNumericMultiplyFunction.class, Sql.multiply(INT, 3));
	}
	
	@Test
	void divideBuildsFunction() {
		assertInstanceOf(SqlNumericDivideFunction.class, Sql.divide(INT, INT2));
	}
	
	@Test
	void divideNumber() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlNumericDivideFunction.class, Sql.divide(INT, 3));
	}
	
	@Test
	void modBuildsFunction() {
		assertInstanceOf(SqlModFunction.class, Sql.mod(INT, INT2));
	}
	
	@Test
	void modNumber() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlModFunction.class, Sql.mod(INT, 3));
	}
	
	@Test
	void powBuildsFunction() {
		assertInstanceOf(SqlPowFunction.class, Sql.pow(INT, INT2));
	}
	
	@Test
	void powNumber() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlPowFunction.class, Sql.pow(INT, 3));
	}
	
	@Test
	void negateBuildsFunction() {
		assertInstanceOf(SqlNegateFunction.class, Sql.negate(INT));
	}
	
	@Test
	void sumBuildsFunction() {
		assertInstanceOf(SqlSumFunction.class, Sql.sum(INT));
	}
	
	@Test
	void averageBuildsFunction() {
		assertInstanceOf(SqlAverageFunction.class, Sql.average(INT));
	}
	
	@Test
	void absBuildsFunction() {
		assertInstanceOf(SqlAbsFunction.class, Sql.abs(INT));
	}
	
	@Test
	void ceilBuildsFunction() {
		assertInstanceOf(SqlCeilFunction.class, Sql.ceil(INT));
	}
	
	@Test
	void floorBuildsFunction() {
		assertInstanceOf(SqlFloorFunction.class, Sql.floor(INT));
	}
	
	@Test
	void truncateNumericBuildsFunction() {
		assertInstanceOf(SqlNumericTruncateFunction.class, Sql.truncate(INT));
	}
	
	@Test
	void sqrtBuildsDoubleFunction() {
		SqlExpression<Double> result = Sql.sqrt(INT);
		assertInstanceOf(SqlSqrtFunction.class, result);
		assertEquals(Double.class, result.type().javaType());
	}
	
	@Test
	void signBuildsIntegerFunction() {
		SqlExpression<Integer> result = Sql.sign(INT);
		assertInstanceOf(SqlSignFunction.class, result);
		assertEquals(Integer.class, result.type().javaType());
	}
	
	@Test
	void expBuildsDoubleFunction() {
		assertEquals(Double.class, Sql.exp(INT).type().javaType());
	}
	
	@Test
	void log2UsesBaseTwo() {
		assertEquals(2, valueOf(((SqlLogFunction) Sql.log2(INT)).base()));
	}
	
	@Test
	void lnUsesBaseE() {
		assertEquals(Math.E, valueOf(((SqlLogFunction) Sql.ln(INT)).base()));
	}
	
	@Test
	void log10UsesBaseTen() {
		assertEquals(10, valueOf(((SqlLogFunction) Sql.log10(INT)).base()));
	}
	
	@Test
	void sinBuildsDoubleFunction() {
		assertEquals(Double.class, Sql.sin(INT).type().javaType());
	}
	
	@Test
	void cosBuildsDoubleFunction() {
		assertEquals(Double.class, Sql.cos(INT).type().javaType());
	}
	
	@Test
	void tanBuildsDoubleFunction() {
		assertEquals(Double.class, Sql.tan(INT).type().javaType());
	}
	
	@Test
	void asinBuildsDoubleFunction() {
		assertEquals(Double.class, Sql.asin(INT).type().javaType());
	}
	
	@Test
	void acosBuildsDoubleFunction() {
		assertEquals(Double.class, Sql.acos(INT).type().javaType());
	}
	
	@Test
	void atanBuildsDoubleFunction() {
		assertEquals(Double.class, Sql.atan(INT).type().javaType());
	}
	
	@Test
	void radiansBuildsDoubleFunction() {
		assertEquals(Double.class, Sql.radians(INT).type().javaType());
	}
	
	@Test
	void degreesBuildsDoubleFunction() {
		assertEquals(Double.class, Sql.degrees(INT).type().javaType());
	}
	
	@Test
	void atan2BuildsDoubleFunction() {
		SqlExpression<Double> result = Sql.atan2(INT, INT2);
		assertInstanceOf(SqlAtan2Function.class, result);
		assertEquals(Double.class, result.type().javaType());
	}
	
	@Test
	void roundBuildsFunction() {
		assertInstanceOf(SqlRoundFunction.class, Sql.round(INT));
	}
	
	@Test
	void roundNumber() throws SqlTypeNotFoundException {
		assertNotNull(((SqlRoundFunction<?>) Sql.round(INT, 2)).precision());
	}
	
	@Test
	void bitwiseAndBuildsFunction() {
		assertInstanceOf(SqlBitwiseAndFunction.class, Sql.bitwiseAnd(INT, INT2));
	}
	
	@Test
	void bitwiseAndValue() {
		assertInstanceOf(SqlBitwiseAndFunction.class, Sql.bitwiseAnd(INT, 3));
	}
	
	@Test
	void bitwiseAndTyped() {
		assertInstanceOf(SqlBitwiseAndFunction.class, Sql.bitwiseAnd(INT, INT2, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseAndNumberTyped() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlBitwiseAndFunction.class, Sql.bitwiseAnd(INT, 3, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseOrBuildsFunction() {
		assertInstanceOf(SqlBitwiseOrFunction.class, Sql.bitwiseOr(INT, INT2));
	}
	
	@Test
	void bitwiseOrValue() {
		assertInstanceOf(SqlBitwiseOrFunction.class, Sql.bitwiseOr(INT, 3));
	}
	
	@Test
	void bitwiseOrTyped() {
		assertInstanceOf(SqlBitwiseOrFunction.class, Sql.bitwiseOr(INT, INT2, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseOrNumberTyped() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlBitwiseOrFunction.class, Sql.bitwiseOr(INT, 3, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseXorBuildsFunction() {
		assertInstanceOf(SqlBitwiseXorFunction.class, Sql.bitwiseXor(INT, INT2));
	}
	
	@Test
	void bitwiseXorValue() {
		assertInstanceOf(SqlBitwiseXorFunction.class, Sql.bitwiseXor(INT, 3));
	}
	
	@Test
	void bitwiseXorTyped() {
		assertInstanceOf(SqlBitwiseXorFunction.class, Sql.bitwiseXor(INT, INT2, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseXorNumberTyped() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlBitwiseXorFunction.class, Sql.bitwiseXor(INT, 3, SqlTypes.INTEGER));
	}
	
	@Test
	void bitwiseNotBuildsFunction() {
		assertInstanceOf(SqlBitwiseNotFunction.class, Sql.bitwiseNot(INT));
	}
	
	@Test
	void bitwiseNotTyped() {
		assertInstanceOf(SqlBitwiseNotFunction.class, Sql.bitwiseNot(INT, SqlTypes.INTEGER));
	}
	
	@Test
	void startsWithBuildsCondition() {
		assertInstanceOf(SqlStartsWithCondition.class, Sql.startsWith(STR, STR2));
	}
	
	@Test
	void startsWithString() {
		assertInstanceOf(SqlStartsWithCondition.class, Sql.startsWith(STR, "x"));
	}
	
	@Test
	void containsBuildsCondition() {
		assertInstanceOf(SqlContainsCondition.class, Sql.contains(STR, STR2));
	}
	
	@Test
	void containsString() {
		assertInstanceOf(SqlContainsCondition.class, Sql.contains(STR, "x"));
	}
	
	@Test
	void endsWithBuildsCondition() {
		assertInstanceOf(SqlEndsWithCondition.class, Sql.endsWith(STR, STR2));
	}
	
	@Test
	void endsWithString() {
		assertInstanceOf(SqlEndsWithCondition.class, Sql.endsWith(STR, "x"));
	}
	
	@Test
	void likeBuildsCondition() {
		assertInstanceOf(SqlLikeCondition.class, Sql.like(STR, STR2));
	}
	
	@Test
	void likeString() {
		assertInstanceOf(SqlLikeCondition.class, Sql.like(STR, "x"));
	}
	
	@Test
	void lowerBuildsFunction() {
		assertInstanceOf(SqlLowerFunction.class, Sql.lower(STR));
	}
	
	@Test
	void upperBuildsFunction() {
		assertInstanceOf(SqlUpperFunction.class, Sql.upper(STR));
	}
	
	@Test
	void trimBuildsFunction() {
		assertInstanceOf(SqlTrimFunction.class, Sql.trim(STR));
	}
	
	@Test
	void leftTrimBuildsFunction() {
		assertInstanceOf(SqlLeftTrimFunction.class, Sql.leftTrim(STR));
	}
	
	@Test
	void rightTrimBuildsFunction() {
		assertInstanceOf(SqlRightTrimFunction.class, Sql.rightTrim(STR));
	}
	
	@Test
	void trimCharsBuildsFunction() {
		assertInstanceOf(SqlTrimCharsFunction.class, Sql.trimChars(STR, STR2));
	}
	
	@Test
	void trimCharsString() {
		assertInstanceOf(SqlTrimCharsFunction.class, Sql.trimChars(STR, "x"));
	}
	
	@Test
	void lengthBuildsIntegerFunction() {
		SqlExpression<Integer> result = Sql.length(STR);
		assertInstanceOf(SqlLengthFunction.class, result);
		assertEquals(Integer.class, result.type().javaType());
	}
	
	@Test
	void substringBuildsFunction() {
		assertInstanceOf(SqlSubstringFunction.class, Sql.substring(STR, INT, INT2));
	}
	
	@Test
	void substringInts() {
		assertInstanceOf(SqlSubstringFunction.class, Sql.substring(STR, 1, 2));
	}
	
	@Test
	void replaceBuildsFunction() {
		assertInstanceOf(SqlReplaceFunction.class, Sql.replace(STR, STR2, STR));
	}
	
	@Test
	void replaceStrings() {
		assertInstanceOf(SqlReplaceFunction.class, Sql.replace(STR, "a", "b"));
	}
	
	@Test
	void positionBuildsFunction() {
		SqlPositionFunction<?> result = (SqlPositionFunction<?>) Sql.position(STR, STR2, SqlTypes.INTEGER);
		assertSame(STR, result.expression());
		assertSame(STR2, result.substring());
	}
	
	@Test
	void leftBuildsFunction() {
		assertInstanceOf(SqlLeftFunction.class, Sql.left(STR, INT));
	}
	
	@Test
	void leftInt() {
		assertInstanceOf(SqlLeftFunction.class, Sql.left(STR, 3));
	}
	
	@Test
	void rightBuildsFunction() {
		assertInstanceOf(SqlRightFunction.class, Sql.right(STR, INT));
	}
	
	@Test
	void rightInt() {
		assertInstanceOf(SqlRightFunction.class, Sql.right(STR, 3));
	}
	
	@Test
	void leftPadBuildsFunction() {
		assertInstanceOf(SqlLeftPadFunction.class, Sql.leftPad(STR, INT, STR2));
	}
	
	@Test
	void leftPadInt() {
		assertInstanceOf(SqlLeftPadFunction.class, Sql.leftPad(STR, 5, "x"));
	}
	
	@Test
	void rightPadBuildsFunction() {
		assertInstanceOf(SqlRightPadFunction.class, Sql.rightPad(STR, INT, STR2));
	}
	
	@Test
	void rightPadInt() {
		assertInstanceOf(SqlRightPadFunction.class, Sql.rightPad(STR, 5, "x"));
	}
	
	@Test
	void hexBuildsFunction() {
		assertInstanceOf(SqlHexFunction.class, Sql.hex(STR));
	}
	
	@Test
	void hexTyped() {
		assertInstanceOf(SqlHexFunction.class, Sql.hex(STR, STRING_TYPE));
	}
	
	@Test
	void unhexBuildsFunction() {
		assertInstanceOf(SqlUnhexFunction.class, Sql.unhex(STR));
	}
	
	@Test
	void withinLastBuildsCondition() {
		assertInstanceOf(SqlWithinLastCondition.class, Sql.withinLast(TS, DUR));
	}
	
	@Test
	void withinLastValue() {
		assertInstanceOf(SqlWithinLastCondition.class, Sql.withinLast(TS, Duration.ofDays(1)));
	}
	
	@Test
	void withinNextBuildsCondition() {
		assertInstanceOf(SqlWithinNextCondition.class, Sql.withinNext(TS, DUR));
	}
	
	@Test
	void withinNextValue() {
		assertInstanceOf(SqlWithinNextCondition.class, Sql.withinNext(TS, Duration.ofDays(1)));
	}
	
	@Test
	void beforeBuildsCondition() {
		assertInstanceOf(SqlBeforeCondition.class, Sql.before(TS, TS));
	}
	
	@Test
	void beforeValue() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlBeforeCondition.class, Sql.before(TS, Instant.EPOCH));
	}
	
	@Test
	void afterBuildsCondition() {
		assertInstanceOf(SqlAfterCondition.class, Sql.after(TS, TS));
	}
	
	@Test
	void afterValue() throws SqlTypeNotFoundException {
		assertInstanceOf(SqlAfterCondition.class, Sql.after(TS, Instant.EPOCH));
	}
	
	@Test
	void fromEpochBuildsFunction() {
		assertInstanceOf(SqlFromEpochFunction.class, Sql.fromEpoch(INT, INSTANT_TYPE));
	}
	
	@Test
	void makeDateBuildsFunction() {
		assertInstanceOf(SqlMakeDateFunction.class, Sql.makeDate(INT, INT, INT, SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void makeDateInts() {
		assertInstanceOf(SqlMakeDateFunction.class, Sql.makeDate(2020, 1, 1, SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void makeTimeBuildsFunction() {
		assertInstanceOf(SqlMakeTimeFunction.class, Sql.makeTime(INT, INT, INT, LOCAL_TIME_TYPE));
	}
	
	@Test
	void makeTimeInts() {
		assertInstanceOf(SqlMakeTimeFunction.class, Sql.makeTime(1, 2, 3, LOCAL_TIME_TYPE));
	}
	
	@Test
	void extractBuildsFunction() {
		assertInstanceOf(SqlExtractFunction.class, Sql.extract(TS, SqlTemporalPart.DAY));
	}
	
	@Test
	void temporalTruncateBuildsFunction() {
		assertInstanceOf(SqlTemporalTruncateFunction.class, Sql.truncate(TS, SqlTemporalPart.DAY, INSTANT_TYPE));
	}
	
	@Test
	void temporalAddBuildsFunction() {
		assertInstanceOf(SqlTemporalAddFunction.class, Sql.add(TS, SqlTemporalPart.DAY, INT, INSTANT_TYPE));
	}
	
	@Test
	void temporalAddInt() {
		assertInstanceOf(SqlTemporalAddFunction.class, Sql.add(TS, SqlTemporalPart.DAY, 1, INSTANT_TYPE));
	}
	
	@Test
	void temporalSubtractBuildsFunction() {
		assertInstanceOf(SqlTemporalSubtractFunction.class, Sql.subtract(TS, SqlTemporalPart.DAY, INT, INSTANT_TYPE));
	}
	
	@Test
	void temporalSubtractInt() {
		assertInstanceOf(SqlTemporalSubtractFunction.class, Sql.subtract(TS, SqlTemporalPart.DAY, 1, INSTANT_TYPE));
	}
	
	@Test
	void toEpochBuildsFunction() {
		assertInstanceOf(SqlToEpochFunction.class, Sql.toEpoch(TS, SqlTypes.LONG));
	}
	
	@Test
	void toDateBuildsFunction() {
		assertInstanceOf(SqlToDateFunction.class, Sql.toDate(TS, SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void toTimeBuildsFunction() {
		assertInstanceOf(SqlToTimeFunction.class, Sql.toTime(TS, LOCAL_TIME_TYPE));
	}
	
	@Test
	void overBuildsWindowedAggregate() {
		assertInstanceOf(SqlWindowedAggregate.class, Sql.over(aggregate(), OVER));
	}
	
	@Test
	void firstValueBuildsFunction() {
		assertInstanceOf(SqlFirstValueFunction.class, Sql.firstValue(INT, OVER));
	}
	
	@Test
	void lastValueBuildsFunction() {
		assertInstanceOf(SqlLastValueFunction.class, Sql.lastValue(INT, OVER));
	}
	
	@Test
	void valueAtBuildsFunction() {
		assertInstanceOf(SqlValueAtFunction.class, Sql.valueAt(INT, 1, OVER));
	}
	
	@Test
	void nestedFunctionComposition() {
		SqlExpression<String> result = Sql.upper(Sql.trim(Sql.lower(STR)));
		assertInstanceOf(SqlUpperFunction.class, result);
		assertEquals(String.class, result.type().javaType());
	}
	
	@Test
	void caseWhenWrappingConditionAndExpressions() {
		SqlCaseWhenFunction<?> result = (SqlCaseWhenFunction<?>) Sql.caseWhen(Sql.equalTo(INT, 5), STR, STR2);
		assertEquals(1, result.branches().size());
		assertSame(STR2, result.elseValue());
	}
	
	@Test
	void coalesceChainPreservesType() throws SqlTypeNotFoundException {
		SqlCoalesceFunction<?> result = (SqlCoalesceFunction<?>) Sql.coalesce(INT, INT2, Sql.add(INT, 1));
		assertEquals(Integer.class, result.type().javaType());
		assertEquals(3, result.expressions().size());
	}
	
	@Test
	void inSubqueryComposition() {
		assertInstanceOf(SqlInQueryCondition.class, Sql.in(INT, intSelectQuery()));
	}
	
	@Test
	void windowedAggregateOverComposition() {
		SqlAggregateFunction<Integer> aggregate = aggregate();
		SqlWindowedAggregate<?> result = (SqlWindowedAggregate<?>) Sql.over(aggregate, OVER);
		assertSame(aggregate, result.aggregate());
		assertSame(OVER, result.over());
	}
	
	@Test
	void greatestAndLeastWithManyOthers() {
		SqlGreatestFunction<?> greatest = (SqlGreatestFunction<?>) Sql.greatest(INT, INT2, INT, INT2);
		assertEquals(4, greatest.expressions().size());
		assertSame(INT, greatest.expressions().get(0));
		assertSame(INT2, greatest.expressions().get(1));
		assertEquals(4, ((SqlLeastFunction<?>) Sql.least(INT, INT2, INT, INT2)).expressions().size());
	}
	
	@Test
	void concatDistinctOrderedFlagsAreIndependent() {
		SqlConcatFunction<?> plain = (SqlConcatFunction<?>) Sql.concatWithSeparator(",", STR);
		SqlConcatFunction<?> distinct = (SqlConcatFunction<?>) Sql.concatDistinctWithSeparator(",", STR);
		SqlConcatFunction<?> ordered = (SqlConcatFunction<?>) Sql.concatOrderedWithSeparator(",", STR);
		assertFalse(plain.distinct() || plain.ordered());
		assertTrue(distinct.distinct() && !distinct.ordered());
		assertTrue(!ordered.distinct() && ordered.ordered());
	}
	
	@Test
	void bitwiseOperatorsProduceDistinctTypes() {
		assertInstanceOf(SqlBitwiseAndFunction.class, Sql.bitwiseAnd(INT, INT2));
		assertInstanceOf(SqlBitwiseOrFunction.class, Sql.bitwiseOr(INT, INT2));
		assertInstanceOf(SqlBitwiseXorFunction.class, Sql.bitwiseXor(INT, INT2));
	}
	
	@Test
	void temporalAddThenExtractRoundTrip() {
		SqlExpression<Integer> result = Sql.extract(Sql.add(TS, SqlTemporalPart.DAY, 1, INSTANT_TYPE), SqlTemporalPart.DAY);
		assertEquals(Integer.class, result.type().javaType());
	}
}
