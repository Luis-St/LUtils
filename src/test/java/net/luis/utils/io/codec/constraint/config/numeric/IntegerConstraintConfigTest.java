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

package net.luis.utils.io.codec.constraint.config.numeric;

import net.luis.utils.math.NumberType;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IntegerConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class IntegerConstraintConfigTest {

	@Test
	void constructor() {
		assertDoesNotThrow(() -> new IntegerConstraintConfig<>(Optional.empty(), Optional.empty(), OptionalLong.empty(), OptionalInt.empty()));
		assertDoesNotThrow(() -> new IntegerConstraintConfig<>(Optional.empty(), Optional.of(true), OptionalLong.empty(), OptionalInt.empty()));
		assertDoesNotThrow(() -> new IntegerConstraintConfig<>(Optional.empty(), Optional.of(false), OptionalLong.empty(), OptionalInt.empty()));
		assertDoesNotThrow(() -> new IntegerConstraintConfig<>(Optional.empty(), Optional.empty(), OptionalLong.of(5), OptionalInt.empty()));
		assertDoesNotThrow(() -> new IntegerConstraintConfig<>(Optional.empty(), Optional.empty(), OptionalLong.empty(), OptionalInt.of(2)));
	}

	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<>(null, Optional.empty(), OptionalLong.empty(), OptionalInt.empty()));
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<>(Optional.empty(), null, OptionalLong.empty(), OptionalInt.empty()));
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<>(Optional.empty(), Optional.empty(), null, OptionalInt.empty()));
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<>(Optional.empty(), Optional.empty(), OptionalLong.empty(), null));
	}

	@Test
	void constructorZeroDivisor() {
		assertThrows(IllegalArgumentException.class, () -> new IntegerConstraintConfig<>(Optional.empty(), Optional.empty(), OptionalLong.of(0), OptionalInt.empty()));
	}

	@Test
	void constructorInvalidPowerBase() {
		assertThrows(IllegalArgumentException.class, () -> new IntegerConstraintConfig<>(Optional.empty(), Optional.empty(), OptionalLong.empty(), OptionalInt.of(1)));
		assertThrows(IllegalArgumentException.class, () -> new IntegerConstraintConfig<>(Optional.empty(), Optional.empty(), OptionalLong.empty(), OptionalInt.of(0)));
		assertThrows(IllegalArgumentException.class, () -> new IntegerConstraintConfig<>(Optional.empty(), Optional.empty(), OptionalLong.empty(), OptionalInt.of(-1)));
	}

	@Test
	void unconstrainedMethod() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.unconstrained();
		assertNotNull(config);
		assertTrue(config.numericConfig().isEmpty());
		assertTrue(config.even().isEmpty());
		assertTrue(config.divisor().isEmpty());
		assertTrue(config.powerBase().isEmpty());
	}

	@Test
	void isUnconstrained() {
		assertTrue(IntegerConstraintConfig.unconstrained().isUnconstrained());
		assertFalse(IntegerConstraintConfig.<Integer>unconstrained().withEven().isUnconstrained());
	}

	@Test
	void withNumericConfig() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNumericConfig(c -> c.withMin(0, true));

		assertTrue(config.numericConfig().isPresent());
		assertTrue(config.numericConfig().get().min().isPresent());
		assertEquals(0, config.numericConfig().get().min().get().getFirst());
	}

	@Test
	void withEven() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withEven();

		assertTrue(config.even().isPresent());
		assertTrue(config.even().get());
	}

	@Test
	void withOdd() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withOdd();

		assertTrue(config.even().isPresent());
		assertFalse(config.even().get());
	}

	@Test
	void withDivisor() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withDivisor(5);

		assertTrue(config.divisor().isPresent());
		assertEquals(5, config.divisor().getAsLong());
	}

	@Test
	void withPowerBase() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPowerBase(2);

		assertTrue(config.powerBase().isPresent());
		assertEquals(2, config.powerBase().getAsInt());
	}

	@Test
	void matchesUnconstrained() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.unconstrained();

		assertTrue(config.matches(NumberType.INTEGER, 0).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 1).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 100).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, -50).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, Integer.MAX_VALUE).isSuccess());
	}

	@Test
	void matchesEven() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withEven();

		assertTrue(config.matches(NumberType.INTEGER, 0).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 2).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, -4).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 100).isSuccess());

		Result<Void> resultOdd = config.matches(NumberType.INTEGER, 1);
		assertTrue(resultOdd.isError());
		assertTrue(resultOdd.errorOrThrow().contains("Violated even constraint"));

		Result<Void> resultOdd2 = config.matches(NumberType.INTEGER, -3);
		assertTrue(resultOdd2.isError());
	}

	@Test
	void matchesOdd() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withOdd();

		assertTrue(config.matches(NumberType.INTEGER, 1).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 3).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, -5).isSuccess());

		Result<Void> resultEven = config.matches(NumberType.INTEGER, 0);
		assertTrue(resultEven.isError());
		assertTrue(resultEven.errorOrThrow().contains("Violated odd constraint"));

		Result<Void> resultEven2 = config.matches(NumberType.INTEGER, 2);
		assertTrue(resultEven2.isError());
	}

	@Test
	void matchesDivisor() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withDivisor(5);

		assertTrue(config.matches(NumberType.INTEGER, 0).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 5).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 10).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, -15).isSuccess());

		Result<Void> resultNotDivisible = config.matches(NumberType.INTEGER, 3);
		assertTrue(resultNotDivisible.isError());
		assertTrue(resultNotDivisible.errorOrThrow().contains("Violated divisibility constraint"));

		Result<Void> resultNotDivisible2 = config.matches(NumberType.INTEGER, 7);
		assertTrue(resultNotDivisible2.isError());
	}

	@Test
	void matchesPowerOfTwo() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPowerBase(2);

		assertTrue(config.matches(NumberType.INTEGER, 1).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 2).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 4).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 8).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 16).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 1024).isSuccess());

		Result<Void> resultNotPower = config.matches(NumberType.INTEGER, 3);
		assertTrue(resultNotPower.isError());
		assertTrue(resultNotPower.errorOrThrow().contains("Violated power-of-2 constraint"));

		Result<Void> resultNotPower2 = config.matches(NumberType.INTEGER, 5);
		assertTrue(resultNotPower2.isError());

		Result<Void> resultNotPower3 = config.matches(NumberType.INTEGER, 100);
		assertTrue(resultNotPower3.isError());
	}

	@Test
	void matchesPowerOfThree() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPowerBase(3);

		assertTrue(config.matches(NumberType.INTEGER, 1).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 3).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 9).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 27).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 81).isSuccess());

		Result<Void> resultNotPower = config.matches(NumberType.INTEGER, 2);
		assertTrue(resultNotPower.isError());
		assertTrue(resultNotPower.errorOrThrow().contains("Violated power-of-3 constraint"));

		Result<Void> resultNotPower2 = config.matches(NumberType.INTEGER, 10);
		assertTrue(resultNotPower2.isError());
	}

	@Test
	void matchesPowerBaseRejectsNegative() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPowerBase(2);

		Result<Void> resultNegative = config.matches(NumberType.INTEGER, -1);
		assertTrue(resultNegative.isError());

		Result<Void> resultNegative2 = config.matches(NumberType.INTEGER, -2);
		assertTrue(resultNegative2.isError());
	}

	@Test
	void matchesPowerBaseAcceptsZero() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPowerBase(2);

		Result<Void> resultZero = config.matches(NumberType.INTEGER, 0);
		assertTrue(resultZero.isError());
	}

	@Test
	void matchesWithNumericConfig() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNumericConfig(c -> c.withRange(0, 100, true));

		Result<Void> resultBelow = config.matches(NumberType.INTEGER, -1);
		assertTrue(resultBelow.isError());

		assertTrue(config.matches(NumberType.INTEGER, 0).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 50).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 100).isSuccess());

		Result<Void> resultAbove = config.matches(NumberType.INTEGER, 101);
		assertTrue(resultAbove.isError());
	}

	@Test
	void matchesMultipleConstraints() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained()
			.withNumericConfig(c -> c.withRange(0, 100, true))
			.withEven()
			.withDivisor(10);

		assertTrue(config.matches(NumberType.INTEGER, 0).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 10).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 20).isSuccess());
		assertTrue(config.matches(NumberType.INTEGER, 100).isSuccess());

		Result<Void> resultOdd = config.matches(NumberType.INTEGER, 15);
		assertTrue(resultOdd.isError());

		Result<Void> resultNotDivisible = config.matches(NumberType.INTEGER, 22);
		assertTrue(resultNotDivisible.isError());

		Result<Void> resultOutOfRange = config.matches(NumberType.INTEGER, 110);
		assertTrue(resultOutOfRange.isError());
	}

	@Test
	void matchesInvalidTypeThrows() {
		IntegerConstraintConfig<Double> config = IntegerConstraintConfig.unconstrained();
		assertThrows(IllegalArgumentException.class, () -> config.matches(NumberType.DOUBLE, 5.5));
	}

	@Test
	void toStringUnconstrained() {
		String str = IntegerConstraintConfig.unconstrained().toString();
		assertEquals("IntegerConstraintConfig[unconstrained]", str);
	}

	@Test
	void toStringWithConstraints() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withEven().withDivisor(5).withPowerBase(2);
		String str = config.toString();
		assertTrue(str.contains("IntegerConstraintConfig["));
		assertTrue(str.contains("even"));
		assertTrue(str.contains("divisibleBy=5"));
		assertTrue(str.contains("powerOf=2"));
	}

	@Test
	void equality() {
		IntegerConstraintConfig<Integer> config1 = IntegerConstraintConfig.<Integer>unconstrained().withEven();
		IntegerConstraintConfig<Integer> config2 = IntegerConstraintConfig.<Integer>unconstrained().withEven();
		IntegerConstraintConfig<Integer> config3 = IntegerConstraintConfig.<Integer>unconstrained().withOdd();

		assertEquals(config1, config2);
		assertNotEquals(config1, config3);
	}

	@Test
	void hashCodeConsistency() {
		IntegerConstraintConfig<Integer> config1 = IntegerConstraintConfig.<Integer>unconstrained().withEven();
		IntegerConstraintConfig<Integer> config2 = IntegerConstraintConfig.<Integer>unconstrained().withEven();

		assertEquals(config1.hashCode(), config2.hashCode());
	}

	// BigInteger support tests

	@Test
	void matchesBigIntegerUnconstrained() {
		IntegerConstraintConfig<BigInteger> config = IntegerConstraintConfig.unconstrained();

		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.ZERO).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.ONE).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(100)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(-50)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, new BigInteger("999999999999999999999999999")).isSuccess());
	}

	@Test
	void matchesBigIntegerEven() {
		IntegerConstraintConfig<BigInteger> config = IntegerConstraintConfig.<BigInteger>unconstrained().withEven();

		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.ZERO).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(2)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(-4)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, new BigInteger("1000000000000000000000000000")).isSuccess());

		Result<Void> resultOdd = config.matches(NumberType.BIG_INTEGER, BigInteger.ONE);
		assertTrue(resultOdd.isError());
		assertTrue(resultOdd.errorOrThrow().contains("Violated even constraint"));

		Result<Void> resultOdd2 = config.matches(NumberType.BIG_INTEGER, new BigInteger("999999999999999999999999999"));
		assertTrue(resultOdd2.isError());
	}

	@Test
	void matchesBigIntegerOdd() {
		IntegerConstraintConfig<BigInteger> config = IntegerConstraintConfig.<BigInteger>unconstrained().withOdd();

		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.ONE).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(3)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(-5)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, new BigInteger("999999999999999999999999999")).isSuccess());

		Result<Void> resultEven = config.matches(NumberType.BIG_INTEGER, BigInteger.ZERO);
		assertTrue(resultEven.isError());
		assertTrue(resultEven.errorOrThrow().contains("Violated odd constraint"));

		Result<Void> resultEven2 = config.matches(NumberType.BIG_INTEGER, new BigInteger("1000000000000000000000000000"));
		assertTrue(resultEven2.isError());
	}

	@Test
	void matchesBigIntegerDivisor() {
		IntegerConstraintConfig<BigInteger> config = IntegerConstraintConfig.<BigInteger>unconstrained().withDivisor(5);

		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.ZERO).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(5)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(10)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(-15)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, new BigInteger("5000000000000000000000000000")).isSuccess());

		Result<Void> resultNotDivisible = config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(3));
		assertTrue(resultNotDivisible.isError());
		assertTrue(resultNotDivisible.errorOrThrow().contains("Violated divisibility constraint"));

		Result<Void> resultNotDivisible2 = config.matches(NumberType.BIG_INTEGER, new BigInteger("999999999999999999999999997"));
		assertTrue(resultNotDivisible2.isError());
	}

	@Test
	void matchesBigIntegerPowerOfTwo() {
		IntegerConstraintConfig<BigInteger> config = IntegerConstraintConfig.<BigInteger>unconstrained().withPowerBase(2);

		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.ONE).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(2)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(4)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(8)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(16)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(1024)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, new BigInteger("1267650600228229401496703205376")).isSuccess()); // 2^100

		Result<Void> resultNotPower = config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(3));
		assertTrue(resultNotPower.isError());
		assertTrue(resultNotPower.errorOrThrow().contains("Violated power-of-2 constraint"));

		Result<Void> resultNotPower2 = config.matches(NumberType.BIG_INTEGER, new BigInteger("999999999999999999999999999"));
		assertTrue(resultNotPower2.isError());
	}

	@Test
	void matchesBigIntegerPowerOfThree() {
		IntegerConstraintConfig<BigInteger> config = IntegerConstraintConfig.<BigInteger>unconstrained().withPowerBase(3);

		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.ONE).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(3)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(9)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(27)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(81)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, new BigInteger("515377520732011331036461129765621272702107522001")).isSuccess()); // 3^100

		Result<Void> resultNotPower = config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(2));
		assertTrue(resultNotPower.isError());
		assertTrue(resultNotPower.errorOrThrow().contains("Violated power-of-3 constraint"));

		Result<Void> resultNotPower2 = config.matches(NumberType.BIG_INTEGER, new BigInteger("999999999999999999999999999"));
		assertTrue(resultNotPower2.isError());
	}

	@Test
	void matchesBigIntegerPowerBaseRejectsNegative() {
		IntegerConstraintConfig<BigInteger> config = IntegerConstraintConfig.<BigInteger>unconstrained().withPowerBase(2);

		Result<Void> resultNegative = config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(-1));
		assertTrue(resultNegative.isError());

		Result<Void> resultNegative2 = config.matches(NumberType.BIG_INTEGER, new BigInteger("-1000000000000000000000000000"));
		assertTrue(resultNegative2.isError());
	}

	@Test
	void matchesBigIntegerPowerBaseRejectsZero() {
		IntegerConstraintConfig<BigInteger> config = IntegerConstraintConfig.<BigInteger>unconstrained().withPowerBase(2);

		Result<Void> resultZero = config.matches(NumberType.BIG_INTEGER, BigInteger.ZERO);
		assertTrue(resultZero.isError());
	}

	@Test
	void matchesBigIntegerWithNumericConfig() {
		IntegerConstraintConfig<BigInteger> config = IntegerConstraintConfig.<BigInteger>unconstrained()
			.withNumericConfig(c -> c.withRange(BigInteger.ZERO, new BigInteger("1000000000000000000000000000"), true));

		Result<Void> resultBelow = config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(-1));
		assertTrue(resultBelow.isError());

		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.ZERO).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, new BigInteger("500000000000000000000000000")).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, new BigInteger("1000000000000000000000000000")).isSuccess());

		Result<Void> resultAbove = config.matches(NumberType.BIG_INTEGER, new BigInteger("1000000000000000000000000001"));
		assertTrue(resultAbove.isError());
	}

	@Test
	void matchesBigIntegerMultipleConstraints() {
		IntegerConstraintConfig<BigInteger> config = IntegerConstraintConfig.<BigInteger>unconstrained()
			.withNumericConfig(c -> c.withRange(BigInteger.ZERO, new BigInteger("1000000000000000000000000000"), true))
			.withEven()
			.withDivisor(10);

		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.ZERO).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(10)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(20)).isSuccess());
		assertTrue(config.matches(NumberType.BIG_INTEGER, new BigInteger("1000000000000000000000000000")).isSuccess());

		Result<Void> resultOdd = config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(15));
		assertTrue(resultOdd.isError());

		Result<Void> resultNotDivisible = config.matches(NumberType.BIG_INTEGER, BigInteger.valueOf(22));
		assertTrue(resultNotDivisible.isError());

		Result<Void> resultOutOfRange = config.matches(NumberType.BIG_INTEGER, new BigInteger("2000000000000000000000000000"));
		assertTrue(resultOutOfRange.isError());
	}

	@Test
	void matchesBigIntegerVeryLargeValues() {
		IntegerConstraintConfig<BigInteger> config = IntegerConstraintConfig.<BigInteger>unconstrained().withEven();

		// Test with value larger than Long.MAX_VALUE
		BigInteger veryLarge = new BigInteger("999999999999999999999999999999999999999999999999998");
		assertTrue(config.matches(NumberType.BIG_INTEGER, veryLarge).isSuccess());

		BigInteger veryLargeOdd = new BigInteger("999999999999999999999999999999999999999999999999999");
		Result<Void> resultOdd = config.matches(NumberType.BIG_INTEGER, veryLargeOdd);
		assertTrue(resultOdd.isError());
		assertTrue(resultOdd.errorOrThrow().contains("Violated even constraint"));
	}
}
