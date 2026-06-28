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

package net.luis.utils.io.database.integration.reflection;

import net.luis.utils.io.database.Sql;
import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.aggregate.SqlCountFunction;
import net.luis.utils.io.database.function.functions.aggregate.SqlSumFunction;
import net.luis.utils.io.database.function.functions.generic.SqlCaseWhenFunction;
import net.luis.utils.io.database.function.functions.numeric.SqlLogFunction;
import net.luis.utils.io.database.function.functions.string.SqlSubstringFunction;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.integration.reflection.SqlOperatorCase.Determinism;
import net.luis.utils.io.database.integration.reflection.SqlOperatorCase.Kind;
import net.luis.utils.io.database.rendering.SqlRenderable;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.io.database.util.SqlCaseWhenBranch;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static net.luis.utils.io.database.integration.reflection.SqlEngineFixture.*;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlOperatorRegistry {
	
	private static final SqlWindowClause WIN = SqlWindowClause.partitionBy(ACTIVE).orderBy(AGE.ascending());
	
	private SqlOperatorRegistry() {}
	
	public static @NonNull List<SqlOperatorCase> cases() {
		List<SqlOperatorCase> out = new ArrayList<>();
		comparisonConditions(out);
		numericConditions(out);
		stringConditions(out);
		temporalConditions(out);
		stringFunctions(out);
		numericFunctions(out);
		bitwiseFunctions(out);
		trigonometricFunctions(out);
		temporalFunctions(out);
		aggregateFunctions(out);
		genericFunctions(out);
		windowFunctions(out);
		return out;
	}
	
	private static void comparisonConditions(@NonNull List<SqlOperatorCase> out) {
		out.add(cond("equalTo(5,5)", "SqlEqualToCondition", Sql.equalTo(i(5), 5), SEED_ROWS));
		out.add(cond("greaterThan(5,3)", "SqlGreaterThanCondition", Sql.greaterThan(i(5), 3), SEED_ROWS));
		out.add(cond("greaterThanOrEqualTo(5,5)", "SqlGreaterThanCondition", Sql.greaterThanOrEqualTo(i(5), 5), SEED_ROWS));
		out.add(cond("lessThan(3,5)", "SqlLessThanCondition", Sql.lessThan(i(3), 5), SEED_ROWS));
		out.add(cond("lessThanOrEqualTo(5,5)", "SqlLessThanCondition", Sql.lessThanOrEqualTo(i(5), 5), SEED_ROWS));
		out.add(cond("between(5,1,10)", "SqlBetweenCondition", Sql.between(i(5), 1, 10), SEED_ROWS));
		out.add(cond("inList(2 in {1,2,3})", "SqlInListCondition", Sql.in(i(2), 1, 2, 3), SEED_ROWS));
		out.add(cond("isNull(5)", "SqlIsNullCondition", Sql.isNull(i(5)), 0L));
		out.add(new SqlOperatorCase("inQuery(id in select id)", leaf("SqlInQueryCondition"), Kind.CONDITION, null, Determinism.DETERMINISTIC,
			db -> {
				try {
					return Sql.in(ID, db.from(REFL).select(ID));
				} catch (net.luis.utils.io.database.exception.SqlException e) {
					throw new RuntimeException("Failed to build sub-query for inQuery case", e);
				}
			}, SEED_ROWS));
		out.add(condFeature("isDistinctFrom(1,2)", "SqlIsDistinctFromCondition", Sql.isDistinctFrom(i(1), 2), SEED_ROWS, SqlFeature.IS_DISTINCT_FROM));
	}
	
	private static void numericConditions(@NonNull List<SqlOperatorCase> out) {
		out.add(cond("isPositive(5)", "SqlIsPositiveCondition", Sql.isPositive(i(5)), SEED_ROWS));
		out.add(cond("isNegative(-5)", "SqlIsNegativeCondition", Sql.isNegative(i(-5)), SEED_ROWS));
		out.add(cond("isZero(0)", "SqlIsZeroCondition", Sql.isZero(i(0)), SEED_ROWS));
		out.add(cond("modEquals(10,3,1)", "SqlModEqualsCondition", Sql.modEquals(i(10), i(3), i(1)), SEED_ROWS));
	}
	
	private static void stringConditions(@NonNull List<SqlOperatorCase> out) {
		out.add(cond("contains('Alice','lic')", "SqlContainsCondition", Sql.contains(str("Alice"), "lic"), SEED_ROWS));
		out.add(cond("startsWith('Alice','Al')", "SqlStartsWithCondition", Sql.startsWith(str("Alice"), "Al"), SEED_ROWS));
		out.add(cond("endsWith('Alice','ce')", "SqlEndsWithCondition", Sql.endsWith(str("Alice"), "ce"), SEED_ROWS));
		out.add(cond("like('Alice','Al%')", "SqlLikeCondition", Sql.like(str("Alice"), "Al%"), SEED_ROWS));
		out.add(cond("equalsIgnoreCase('Alice','alice')", "SqlEqualsIgnoreCaseCondition", Sql.equalsIgnoreCase(str("Alice"), "alice"), SEED_ROWS));
	}
	
	private static void temporalConditions(@NonNull List<SqlOperatorCase> out) {
		out.add(cond("after(2024-06-01, 2024-01-01)", "SqlAfterCondition", Sql.after(date(LocalDate.of(2024, 6, 1)), date(LocalDate.of(2024, 1, 1))), SEED_ROWS));
		out.add(cond("before(2024-01-01, 2024-06-01)", "SqlBeforeCondition", Sql.before(date(LocalDate.of(2024, 1, 1)), date(LocalDate.of(2024, 6, 1))), SEED_ROWS));
		out.add(cond("withinLast(2000-01-01, PT1H)", "SqlWithinLastCondition", Sql.withinLast(ldt(LocalDateTime.of(2000, 1, 1, 0, 0, 0)), Duration.ofHours(1)), 0L));
		out.add(cond("withinNext(2000-01-01, PT1H)", "SqlWithinNextCondition", Sql.withinNext(ldt(LocalDateTime.of(2000, 1, 1, 0, 0, 0)), Duration.ofHours(1)), 0L));
	}
	
	private static void stringFunctions(@NonNull List<SqlOperatorCase> out) {
		out.add(scalar("upper('Ada')", "SqlUpperFunction", Sql.upper(str("Ada")), "ADA"));
		out.add(scalar("lower('Ada')", "SqlLowerFunction", Sql.lower(str("Ada")), "ada"));
		out.add(scalar("trim(' Ada ')", "SqlTrimFunction", Sql.trim(str(" Ada ")), "Ada"));
		out.add(scalar("leftTrim(' Ada')", "SqlLeftTrimFunction", Sql.leftTrim(str(" Ada")), "Ada"));
		out.add(scalar("rightTrim('Ada ')", "SqlRightTrimFunction", Sql.rightTrim(str("Ada ")), "Ada"));
		out.add(scalar("trimChars('xxAdaxx','x')", "SqlTrimCharsFunction", Sql.trimChars(str("xxAdaxx"), "x"), "Ada"));
		out.add(scalar("length('Ada')", "SqlLengthFunction", Sql.length(str("Ada")), 3));
		out.add(scalar("substring('Ada',1,2)", "SqlSubstringFunction", Sql.substring(str("Ada"), 1, 2), "Ad"));
		out.add(scalar("substring('Ada',2,null)", "SqlSubstringFunction", new SqlSubstringFunction<>(str("Ada"), i(2), null), "da"));
		out.add(scalar("concat('Ab','cd')", "SqlConcatFunction", Sql.concat(str("Ab"), str("cd")), "Abcd"));
		out.add(scalar("concatWithSeparator('-','a','b')", "SqlConcatFunction", Sql.concatWithSeparator("-", str("a"), str("b")), "a-b"));
		out.add(scalar("replace('Ada','d','X')", "SqlReplaceFunction", Sql.replace(str("Ada"), "d", "X"), "AXa"));
		out.add(scalar("left('Ada',2)", "SqlLeftFunction", Sql.left(str("Ada"), 2), "Ad"));
		out.add(scalar("right('Ada',2)", "SqlRightFunction", Sql.right(str("Ada"), 2), "da"));
		out.add(scalar("leftPad('Ad',4,'*')", "SqlLeftPadFunction", Sql.leftPad(str("Ad"), 4, "*"), "**Ad"));
		out.add(scalar("rightPad('Ad',4,'*')", "SqlRightPadFunction", Sql.rightPad(str("Ad"), 4, "*"), "Ad**"));
		out.add(scalar("position('Ada','d')", "SqlPositionFunction", Sql.position(str("Ada"), str("d"), SqlTypes.INTEGER), 2));
		out.add(scalarNoOracle("hex('A')", "SqlHexFunction", Sql.hex(str("A")), Determinism.DETERMINISTIC));
		out.add(scalarNoOracle("unhex('41')", "SqlUnhexFunction", Sql.unhex(str("41")), Determinism.DETERMINISTIC));
	}
	
	private static void numericFunctions(@NonNull List<SqlOperatorCase> out) {
		out.add(scalar("abs(-5)", "SqlAbsFunction", Sql.abs(i(-5)), 5));
		out.add(scalar("ceil(1.2)", "SqlCeilFunction", Sql.ceil(dbl(1.2)), 2));
		out.add(scalar("floor(1.8)", "SqlFloorFunction", Sql.floor(dbl(1.8)), 1));
		out.add(scalar("round(2.345)", "SqlRoundFunction", Sql.round(dbl(2.345)), 2));
		out.add(scalar("round(2.345,2)", "SqlRoundFunction", Sql.round(dbl(2.345), i(2)), new java.math.BigDecimal("2.35")));
		out.add(scalar("numericTruncate(2.789)", "SqlNumericTruncateFunction", Sql.truncate(dbl(2.789)), 2));
		out.add(scalar("mod(10,3)", "SqlModFunction", Sql.mod(i(10), i(3)), 1));
		out.add(scalar("add(5,3)", "SqlNumericAddFunction", Sql.add(i(5), i(3)), 8));
		out.add(scalar("subtract(5,3)", "SqlNumericSubtractFunction", Sql.subtract(i(5), i(3)), 2));
		out.add(scalar("multiply(5,3)", "SqlNumericMultiplyFunction", Sql.multiply(i(5), i(3)), 15));
		out.add(scalar("divide(10,2)", "SqlNumericDivideFunction", Sql.divide(i(10), i(2)), 5));
		out.add(scalar("negate(5)", "SqlNegateFunction", Sql.negate(i(5)), -5));
		out.add(scalar("sign(-5)", "SqlSignFunction", Sql.sign(i(-5)), -1));
		out.add(scalarF("sqrt(9)", "SqlSqrtFunction", Sql.sqrt(i(9)), 3.0));
		out.add(scalarF("pow(2,3)", "SqlPowFunction", Sql.pow(i(2), i(3)), 8.0));
		out.add(scalarF("exp(1)", "SqlExpFunction", Sql.exp(i(1)), Math.E));
		out.add(scalarF("log10(100)", "SqlLogFunction", Sql.log10(i(100)), 2.0));
		out.add(scalarF("ln(natural,e)", "SqlLogFunction", new SqlLogFunction(dbl(Math.E), null), 1.0));
		out.add(scalarF("radians(180)", "SqlRadiansFunction", Sql.radians(i(180)), Math.PI));
		out.add(scalarF("degrees(PI)", "SqlDegreesFunction", Sql.degrees(dbl(Math.PI)), 180.0));
		out.add(scalarF("pi()", "SqlPiFunction", Sql.pi(), Math.PI));
		out.add(new SqlOperatorCase("random()", leaf("SqlRandomFunction"), Kind.SCALAR, null, Determinism.NON_DETERMINISTIC, db -> Sql.random(), null));
	}
	
	private static void bitwiseFunctions(@NonNull List<SqlOperatorCase> out) {
		out.add(scalar("bitwiseAnd(6,3)", "SqlBitwiseAndFunction", Sql.bitwiseAnd(i(6), 3), 2));
		out.add(scalar("bitwiseOr(6,1)", "SqlBitwiseOrFunction", Sql.bitwiseOr(i(6), 1), 7));
		out.add(scalar("bitwiseXor(6,3)", "SqlBitwiseXorFunction", Sql.bitwiseXor(i(6), 3), 5));
		out.add(scalar("bitwiseNot(5)", "SqlBitwiseNotFunction", Sql.bitwiseNot(i(5)), -6));
	}
	
	private static void trigonometricFunctions(@NonNull List<SqlOperatorCase> out) {
		out.add(scalarF("sin(0)", "SqlSinFunction", Sql.sin(dbl(0.0)), 0.0));
		out.add(scalarF("cos(0)", "SqlCosFunction", Sql.cos(dbl(0.0)), 1.0));
		out.add(scalarF("tan(0)", "SqlTanFunction", Sql.tan(dbl(0.0)), 0.0));
		out.add(scalarF("asin(0)", "SqlAsinFunction", Sql.asin(dbl(0.0)), 0.0));
		out.add(scalarF("acos(1)", "SqlAcosFunction", Sql.acos(dbl(1.0)), 0.0));
		out.add(scalarF("atan(0)", "SqlAtanFunction", Sql.atan(dbl(0.0)), 0.0));
		out.add(scalarF("atan2(0,1)", "SqlAtan2Function", Sql.atan2(dbl(0.0), dbl(1.0)), 0.0));
	}
	
	private static void temporalFunctions(@NonNull List<SqlOperatorCase> out) {
		SqlExpression<LocalDate> date = date(LocalDate.of(2024, 3, 15));
		SqlExpression<LocalDateTime> ts = ldt(LocalDateTime.of(2024, 3, 15, 10, 30, 0));
		
		out.add(scalar("extract(YEAR, 2024-03-15)", "SqlExtractFunction", Sql.extract(date, SqlTemporalPart.YEAR), 2024));
		out.add(scalar("extract(MONTH, 2024-03-15)", "SqlExtractFunction", Sql.extract(date, SqlTemporalPart.MONTH), 3));
		out.add(scalar("extract(DAY, 2024-03-15)", "SqlExtractFunction", Sql.extract(date, SqlTemporalPart.DAY), 15));
		out.add(scalar("extract(QUARTER, 2024-03-15)", "SqlExtractFunction", Sql.extract(date, SqlTemporalPart.QUARTER), 1));
		out.add(scalarNoOracle("extract(WEEK, 2024-03-15)", "SqlExtractFunction", Sql.extract(date, SqlTemporalPart.WEEK), Determinism.DETERMINISTIC));
		out.add(scalarNoOracle("extract(DAY_OF_WEEK, 2024-03-15)", "SqlExtractFunction", Sql.extract(date, SqlTemporalPart.DAY_OF_WEEK), Determinism.DETERMINISTIC));
		out.add(scalarNoOracle("extract(DAY_OF_YEAR, 2024-03-15)", "SqlExtractFunction", Sql.extract(date, SqlTemporalPart.DAY_OF_YEAR), Determinism.DETERMINISTIC));
		
		out.add(scalarNoOracle("temporalTruncate(WEEK)", "SqlTemporalTruncateFunction", Sql.truncate(date, SqlTemporalPart.WEEK, SqlTypes.LOCAL_DATE), Determinism.DETERMINISTIC));
		out.add(scalarNoOracle("temporalTruncate(MONTH)", "SqlTemporalTruncateFunction", Sql.truncate(date, SqlTemporalPart.MONTH, SqlTypes.LOCAL_DATE), Determinism.DETERMINISTIC));
		out.add(scalarNoOracle("temporalTruncate(DAY)", "SqlTemporalTruncateFunction", Sql.truncate(date, SqlTemporalPart.DAY, SqlTypes.LOCAL_DATE), Determinism.DETERMINISTIC));
		
		out.add(scalar("temporalAdd(2024-01-01 + 5 DAY)", "SqlTemporalAddFunction", Sql.add(date(LocalDate.of(2024, 1, 1)), SqlTemporalPart.DAY, 5, SqlTypes.LOCAL_DATE), LocalDate.of(2024, 1, 6)));
		out.add(scalar("temporalSubtract(2024-01-10 - 5 DAY)", "SqlTemporalSubtractFunction", Sql.subtract(date(LocalDate.of(2024, 1, 10)), SqlTemporalPart.DAY, 5, SqlTypes.LOCAL_DATE), LocalDate.of(2024, 1, 5)));
		out.add(scalar("makeDate(2024,3,15)", "SqlMakeDateFunction", Sql.makeDate(2024, 3, 15, SqlTypes.LOCAL_DATE), LocalDate.of(2024, 3, 15)));
		out.add(scalarNoOracle("makeTime(10,30,0)", "SqlMakeTimeFunction", Sql.makeTime(10, 30, 0, SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(0))), Determinism.DETERMINISTIC));
		out.add(scalarNoOracle("fromEpoch(0)", "SqlFromEpochFunction", Sql.fromEpoch(lng(0L), SqlTypes.INSTANT.configure(SqlParameter.fractional(0))), Determinism.DETERMINISTIC));
		out.add(scalarNoOracle("toEpoch(2024-01-01)", "SqlToEpochFunction", Sql.toEpoch(ldt(LocalDateTime.of(2024, 1, 1, 0, 0, 0)), SqlTypes.LONG), Determinism.DETERMINISTIC));
		out.add(scalarNoOracle("toDate(2024-03-15 10:00)", "SqlToDateFunction", Sql.toDate(ts, SqlTypes.LOCAL_DATE), Determinism.DETERMINISTIC));
		out.add(scalarNoOracle("toTime(2024-03-15 10:30)", "SqlToTimeFunction", Sql.toTime(ts, SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(0))), Determinism.DETERMINISTIC));
		
		out.add(nonDeterministic("now()", "SqlNowFunction", db -> Sql.now()));
		out.add(nonDeterministic("currentDate()", "SqlCurrentDateFunction", db -> Sql.currentDate()));
		out.add(nonDeterministic("currentTime()", "SqlCurrentTimeFunction", db -> Sql.currentTime()));
		out.add(nonDeterministic("currentTimestamp()", "SqlCurrentTimestampFunction", db -> Sql.currentTimestamp()));
	}
	
	private static void aggregateFunctions(@NonNull List<SqlOperatorCase> out) {
		out.add(agg("count(*)", "SqlCountFunction", new SqlCountFunction(null), SEED_ROWS));
		out.add(agg("count(id)", "SqlCountFunction", Sql.count(ID, false), SEED_ROWS));
		out.add(agg("countDistinct(active)", "SqlCountDistinctFunction", Sql.count(ACTIVE, true), 2L));
		out.add(agg("max(age)", "SqlMaxFunction", Sql.max(AGE), 40));
		out.add(agg("min(age)", "SqlMinFunction", Sql.min(AGE), 25));
		out.add(agg("sum(age)", "SqlSumFunction", Sql.sum(AGE), 95));
		out.add(aggF("average(age)", "SqlAverageFunction", Sql.average(AGE)));
	}
	
	private static void genericFunctions(@NonNull List<SqlOperatorCase> out) {
		out.add(scalar("caseWhen(true->'yes')", "SqlCaseWhenFunction", Sql.caseWhen(Sql.equalTo(i(1), 1), str("yes"), str("no")), "yes"));
		out.add(scalar("caseWhen(noElse, true->'yes')", "SqlCaseWhenFunction", new SqlCaseWhenFunction<>(List.of(new SqlCaseWhenBranch<>(Sql.equalTo(i(1), 1), str("yes"))), null), "yes"));
		out.add(scalar("cast('5' as INTEGER)", "SqlCastFunction", Sql.cast(str("5"), SqlTypes.INTEGER), 5));
		out.add(scalar("coalesce(5,7)", "SqlCoalesceFunction", Sql.coalesce(i(5), i(7)), 5));
		out.add(scalar("nullIf(5,7)", "SqlNullIfFunction", Sql.nullIf(i(5), 7), 5));
		out.add(scalarNoOracle("nullIf(5,5)", "SqlNullIfFunction", Sql.nullIf(i(5), 5), Determinism.DETERMINISTIC));
		out.add(scalar("greatest(1,5,3)", "SqlGreatestFunction", Sql.greatest(i(1), i(5), i(3)), 5));
		out.add(scalar("least(1,5,3)", "SqlLeastFunction", Sql.least(i(1), i(5), i(3)), 1));
	}
	
	private static void windowFunctions(@NonNull List<SqlOperatorCase> out) {
		out.add(window("rowNumber()", "SqlRowNumberFunction", Sql.rowNumber(WIN), Determinism.DETERMINISTIC));
		out.add(window("rank()", "SqlRankFunction", Sql.rank(WIN), Determinism.DETERMINISTIC));
		out.add(window("denseRank()", "SqlDenseRankFunction", Sql.denseRank(WIN), Determinism.DETERMINISTIC));
		out.add(window("percentRank()", "SqlPercentRankFunction", Sql.percentRank(WIN), Determinism.FLOAT));
		out.add(window("cumulativeDistribution()", "SqlCumulativeDistributionFunction", Sql.cumulativeDistribution(WIN), Determinism.FLOAT));
		out.add(window("tileBucket(2)", "SqlTileBucketFunction", Sql.tileBucket(2, WIN), Determinism.DETERMINISTIC));
		out.add(window("lag(name)", "SqlLagFunction", Sql.lag(NAME, WIN), Determinism.DETERMINISTIC));
		out.add(window("lag(name,1)", "SqlLagFunction", Sql.lag(NAME, 1, WIN), Determinism.DETERMINISTIC));
		out.add(window("lead(name)", "SqlLeadFunction", Sql.lead(NAME, WIN), Determinism.DETERMINISTIC));
		out.add(window("firstValue(name)", "SqlFirstValueFunction", Sql.firstValue(NAME, WIN), Determinism.DETERMINISTIC));
		out.add(window("lastValue(name)", "SqlLastValueFunction", Sql.lastValue(NAME, WIN), Determinism.DETERMINISTIC));
		out.add(window("valueAt(name,1)", "SqlValueAtFunction", Sql.valueAt(NAME, 1, WIN), Determinism.DETERMINISTIC));
		out.add(window("over(sum(age))", "SqlWindowedAggregate", Sql.over(new SqlSumFunction<>(AGE), WIN), Determinism.DETERMINISTIC));
	}
	
	private static SqlOperatorCase scalar(@NonNull String label, @NonNull String cls, @NonNull SqlExpression<?> expr, @NonNull Object oracle) {
		return new SqlOperatorCase(label, leaf(cls), Kind.SCALAR, null, Determinism.DETERMINISTIC, db -> expr, oracle);
	}
	
	private static SqlOperatorCase scalarF(@NonNull String label, @NonNull String cls, @NonNull SqlExpression<?> expr, double oracle) {
		return new SqlOperatorCase(label, leaf(cls), Kind.SCALAR, null, Determinism.FLOAT, db -> expr, oracle);
	}
	
	private static SqlOperatorCase scalarNoOracle(@NonNull String label, @NonNull String cls, @NonNull SqlExpression<?> expr, @NonNull Determinism determinism) {
		return new SqlOperatorCase(label, leaf(cls), Kind.SCALAR, null, determinism, db -> expr, null);
	}
	
	private static SqlOperatorCase nonDeterministic(@NonNull String label, @NonNull String cls, @NonNull Function<SqlDatabase, SqlRenderable> builder) {
		return new SqlOperatorCase(label, leaf(cls), Kind.SCALAR, null, Determinism.NON_DETERMINISTIC, builder, null);
	}
	
	private static SqlOperatorCase agg(@NonNull String label, @NonNull String cls, @NonNull SqlExpression<?> expr, @NonNull Object oracle) {
		return new SqlOperatorCase(label, leaf(cls), Kind.AGGREGATE, null, Determinism.DETERMINISTIC, db -> expr, oracle);
	}
	
	private static SqlOperatorCase aggF(@NonNull String label, @NonNull String cls, @NonNull SqlExpression<?> expr) {
		return new SqlOperatorCase(label, leaf(cls), Kind.AGGREGATE, null, Determinism.FLOAT, db -> expr, null);
	}
	
	private static SqlOperatorCase window(@NonNull String label, @NonNull String cls, @NonNull SqlExpression<?> expr, @NonNull Determinism determinism) {
		return new SqlOperatorCase(label, leaf(cls), Kind.WINDOW, SqlFeature.WINDOW_FUNCTIONS, determinism, db -> expr, null);
	}
	
	private static SqlOperatorCase cond(@NonNull String label, @NonNull String cls, @NonNull SqlCondition condition, long expectedCount) {
		return new SqlOperatorCase(label, leaf(cls), Kind.CONDITION, null, Determinism.DETERMINISTIC, db -> condition, expectedCount);
	}
	
	private static SqlOperatorCase condFeature(@NonNull String label, @NonNull String cls, @NonNull SqlCondition condition, long expectedCount, @Nullable SqlFeature feature) {
		return new SqlOperatorCase(label, leaf(cls), Kind.CONDITION, feature, Determinism.DETERMINISTIC, db -> condition, expectedCount);
	}
	
	private static @NonNull Class<?> leaf(@NonNull String simpleName) {
		return SqlOperatorDiscovery.leafBySimpleName(simpleName);
	}
	
	private static @NonNull SqlExpression<Integer> i(int value) {
		return Sql.of(value, SqlTypes.INTEGER);
	}
	
	private static @NonNull SqlExpression<Long> lng(long value) {
		return Sql.of(value, SqlTypes.LONG);
	}
	
	private static @NonNull SqlExpression<Double> dbl(double value) {
		return Sql.of(value, SqlTypes.DOUBLE);
	}
	
	private static @NonNull SqlExpression<String> str(@NonNull String value) {
		return Sql.of(value, SqlTypes.STRING.configure(SqlParameter.length(255)));
	}
	
	private static @NonNull SqlExpression<LocalDate> date(@NonNull LocalDate value) {
		return Sql.of(value, SqlTypes.LOCAL_DATE);
	}
	
	private static @NonNull SqlExpression<LocalDateTime> ldt(@NonNull LocalDateTime value) {
		return Sql.of(value, SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(0)));
	}
}
