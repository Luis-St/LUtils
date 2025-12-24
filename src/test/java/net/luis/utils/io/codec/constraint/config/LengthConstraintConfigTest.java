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

package net.luis.utils.io.codec.constraint.config;

import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LengthConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class LengthConstraintConfigTest {

	@Test
	void constructor() {
		assertDoesNotThrow(() -> new LengthConstraintConfig(OptionalInt.empty(), OptionalInt.empty()));
		assertDoesNotThrow(() -> new LengthConstraintConfig(OptionalInt.of(0), OptionalInt.empty()));
		assertDoesNotThrow(() -> new LengthConstraintConfig(OptionalInt.empty(), OptionalInt.of(10)));
		assertDoesNotThrow(() -> new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.of(10)));
		assertDoesNotThrow(() -> new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.of(5)));
	}

	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new LengthConstraintConfig(null, OptionalInt.empty()));
		assertThrows(NullPointerException.class, () -> new LengthConstraintConfig(OptionalInt.empty(), null));
		assertThrows(NullPointerException.class, () -> new LengthConstraintConfig(null, null));
	}

	@Test
	void constructorNegativeMinLength() {
		assertThrows(IllegalArgumentException.class, () -> new LengthConstraintConfig(OptionalInt.of(-1), OptionalInt.empty()));
		assertThrows(IllegalArgumentException.class, () -> new LengthConstraintConfig(OptionalInt.of(-5), OptionalInt.of(10)));
	}

	@Test
	void constructorNegativeMaxLength() {
		assertThrows(IllegalArgumentException.class, () -> new LengthConstraintConfig(OptionalInt.empty(), OptionalInt.of(-1)));
		assertThrows(IllegalArgumentException.class, () -> new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.of(-10)));
	}

	@Test
	void constructorMaxLessThanMin() {
		assertThrows(IllegalArgumentException.class, () -> new LengthConstraintConfig(OptionalInt.of(10), OptionalInt.of(5)));
		assertThrows(IllegalArgumentException.class, () -> new LengthConstraintConfig(OptionalInt.of(100), OptionalInt.of(1)));
	}

	@Test
	void unconstrainedConstant() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.minLength().isEmpty());
		assertTrue(config.maxLength().isEmpty());
	}

	@Test
	void withMinLength() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withMinLength(5);

		assertTrue(config.minLength().isPresent());
		assertEquals(5, config.minLength().getAsInt());
		assertTrue(config.maxLength().isEmpty());
	}

	@Test
	void withMinLengthPreservesMaxLength() {
		LengthConstraintConfig initial = new LengthConstraintConfig(OptionalInt.empty(), OptionalInt.of(20));
		LengthConstraintConfig config = initial.withMinLength(10);

		assertTrue(config.minLength().isPresent());
		assertEquals(10, config.minLength().getAsInt());
		assertTrue(config.maxLength().isPresent());
		assertEquals(20, config.maxLength().getAsInt());
	}

	@Test
	void withMinLengthNegative() {
		assertThrows(IllegalArgumentException.class, () -> LengthConstraintConfig.UNCONSTRAINED.withMinLength(-1));
	}

	@Test
	void withMinLengthExceedsMaxLength() {
		LengthConstraintConfig config = new LengthConstraintConfig(OptionalInt.empty(), OptionalInt.of(5));
		assertThrows(IllegalArgumentException.class, () -> config.withMinLength(10));
	}

	@Test
	void withMaxLength() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withMaxLength(10);

		assertTrue(config.minLength().isEmpty());
		assertTrue(config.maxLength().isPresent());
		assertEquals(10, config.maxLength().getAsInt());
	}

	@Test
	void withMaxLengthPreservesMinLength() {
		LengthConstraintConfig initial = new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.empty());
		LengthConstraintConfig config = initial.withMaxLength(15);

		assertTrue(config.minLength().isPresent());
		assertEquals(5, config.minLength().getAsInt());
		assertTrue(config.maxLength().isPresent());
		assertEquals(15, config.maxLength().getAsInt());
	}

	@Test
	void withMaxLengthNegative() {
		assertThrows(IllegalArgumentException.class, () -> LengthConstraintConfig.UNCONSTRAINED.withMaxLength(-1));
	}

	@Test
	void withMaxLengthLessThanMinLength() {
		LengthConstraintConfig config = new LengthConstraintConfig(OptionalInt.of(10), OptionalInt.empty());
		assertThrows(IllegalArgumentException.class, () -> config.withMaxLength(5));
	}

	@Test
	void withLength() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withLength(5, 10);

		assertTrue(config.minLength().isPresent());
		assertEquals(5, config.minLength().getAsInt());
		assertTrue(config.maxLength().isPresent());
		assertEquals(10, config.maxLength().getAsInt());
	}

	@Test
	void withLengthReplacesExistingConstraints() {
		LengthConstraintConfig initial = new LengthConstraintConfig(OptionalInt.of(1), OptionalInt.of(100));
		LengthConstraintConfig config = initial.withLength(10, 20);

		assertTrue(config.minLength().isPresent());
		assertEquals(10, config.minLength().getAsInt());
		assertTrue(config.maxLength().isPresent());
		assertEquals(20, config.maxLength().getAsInt());
	}

	@Test
	void withLengthNegativeMinLength() {
		assertThrows(IllegalArgumentException.class, () -> LengthConstraintConfig.UNCONSTRAINED.withLength(-1, 10));
	}

	@Test
	void withLengthNegativeMaxLength() {
		assertThrows(IllegalArgumentException.class, () -> LengthConstraintConfig.UNCONSTRAINED.withLength(5, -1));
	}

	@Test
	void withLengthMaxLessThanMin() {
		assertThrows(IllegalArgumentException.class, () -> LengthConstraintConfig.UNCONSTRAINED.withLength(10, 5));
	}

	@Test
	void matchesUnconstrained() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED;

		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(100).isSuccess());
		assertTrue(config.matches(Integer.MAX_VALUE).isSuccess());
	}

	@Test
	void matchesMinLengthOnly() {
		LengthConstraintConfig config = new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.empty());

		Result<Void> resultBelow = config.matches(4);
		assertTrue(resultBelow.isError());
		assertTrue(resultBelow.errorOrThrow().contains("Violated minimum length constraint"));
		assertTrue(resultBelow.errorOrThrow().contains("4 < 5"));

		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(6).isSuccess());
		assertTrue(config.matches(100).isSuccess());
	}

	@Test
	void matchesMaxLengthOnly() {
		LengthConstraintConfig config = new LengthConstraintConfig(OptionalInt.empty(), OptionalInt.of(10));

		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(10).isSuccess());

		Result<Void> resultAbove = config.matches(11);
		assertTrue(resultAbove.isError());
		assertTrue(resultAbove.errorOrThrow().contains("Violated maximum length constraint"));
		assertTrue(resultAbove.errorOrThrow().contains("11 > 10"));
	}

	@Test
	void matchesBothMinAndMaxLength() {
		LengthConstraintConfig config = new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.of(10));

		Result<Void> resultBelowMin = config.matches(4);
		assertTrue(resultBelowMin.isError());
		assertTrue(resultBelowMin.errorOrThrow().contains("Violated minimum length constraint"));

		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(7).isSuccess());
		assertTrue(config.matches(10).isSuccess());

		Result<Void> resultAboveMax = config.matches(11);
		assertTrue(resultAboveMax.isError());
		assertTrue(resultAboveMax.errorOrThrow().contains("Violated maximum length constraint"));
	}

	@Test
	void matchesExactLength() {
		LengthConstraintConfig config = new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.of(5));

		Result<Void> resultBelow = config.matches(4);
		assertTrue(resultBelow.isError());

		assertTrue(config.matches(5).isSuccess());

		Result<Void> resultAbove = config.matches(6);
		assertTrue(resultAbove.isError());
	}

	@Test
	void matchesZeroLength() {
		LengthConstraintConfig config = new LengthConstraintConfig(OptionalInt.of(0), OptionalInt.of(0));

		assertTrue(config.matches(0).isSuccess());

		Result<Void> resultAbove = config.matches(1);
		assertTrue(resultAbove.isError());
	}

	@Test
	void toStringUnconstrained() {
		String str = LengthConstraintConfig.UNCONSTRAINED.toString();
		assertEquals("LengthConstraintConfig[unconstrained]", str);
	}

	@Test
	void toStringMinLengthOnly() {
		LengthConstraintConfig config = new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.empty());
		String str = config.toString();
		assertEquals("LengthConstraintConfig[minLength=5]", str);
	}

	@Test
	void toStringMaxLengthOnly() {
		LengthConstraintConfig config = new LengthConstraintConfig(OptionalInt.empty(), OptionalInt.of(10));
		String str = config.toString();
		assertEquals("LengthConstraintConfig[maxLength=10]", str);
	}

	@Test
	void toStringBothMinAndMaxLength() {
		LengthConstraintConfig config = new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.of(10));
		String str = config.toString();
		assertEquals("LengthConstraintConfig[minLength=5,maxLength=10]", str);
	}

	@Test
	void toStringExactLength() {
		LengthConstraintConfig config = new LengthConstraintConfig(OptionalInt.of(7), OptionalInt.of(7));
		String str = config.toString();
		assertEquals("LengthConstraintConfig[length=7]", str);
	}

	@Test
	void equality() {
		LengthConstraintConfig config1 = new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.of(10));
		LengthConstraintConfig config2 = new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.of(10));
		LengthConstraintConfig config3 = new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.of(15));

		assertEquals(config1, config2);
		assertNotEquals(config1, config3);
	}

	@Test
	void hashCodeConsistency() {
		LengthConstraintConfig config1 = new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.of(10));
		LengthConstraintConfig config2 = new LengthConstraintConfig(OptionalInt.of(5), OptionalInt.of(10));

		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
