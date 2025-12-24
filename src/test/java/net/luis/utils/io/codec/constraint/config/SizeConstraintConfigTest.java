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
 * Test class for {@link SizeConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class SizeConstraintConfigTest {

	@Test
	void constructor() {
		assertDoesNotThrow(() -> new SizeConstraintConfig(OptionalInt.empty(), OptionalInt.empty()));
		assertDoesNotThrow(() -> new SizeConstraintConfig(OptionalInt.of(0), OptionalInt.empty()));
		assertDoesNotThrow(() -> new SizeConstraintConfig(OptionalInt.empty(), OptionalInt.of(10)));
		assertDoesNotThrow(() -> new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.of(10)));
		assertDoesNotThrow(() -> new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.of(5)));
	}

	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new SizeConstraintConfig(null, OptionalInt.empty()));
		assertThrows(NullPointerException.class, () -> new SizeConstraintConfig(OptionalInt.empty(), null));
		assertThrows(NullPointerException.class, () -> new SizeConstraintConfig(null, null));
	}

	@Test
	void constructorNegativeMinSize() {
		assertThrows(IllegalArgumentException.class, () -> new SizeConstraintConfig(OptionalInt.of(-1), OptionalInt.empty()));
		assertThrows(IllegalArgumentException.class, () -> new SizeConstraintConfig(OptionalInt.of(-5), OptionalInt.of(10)));
	}

	@Test
	void constructorNegativeMaxSize() {
		assertThrows(IllegalArgumentException.class, () -> new SizeConstraintConfig(OptionalInt.empty(), OptionalInt.of(-1)));
		assertThrows(IllegalArgumentException.class, () -> new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.of(-10)));
	}

	@Test
	void constructorMaxLessThanMin() {
		assertThrows(IllegalArgumentException.class, () -> new SizeConstraintConfig(OptionalInt.of(10), OptionalInt.of(5)));
		assertThrows(IllegalArgumentException.class, () -> new SizeConstraintConfig(OptionalInt.of(100), OptionalInt.of(1)));
	}

	@Test
	void unconstrainedConstant() {
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.minSize().isEmpty());
		assertTrue(config.maxSize().isEmpty());
	}

	@Test
	void withMinSize() {
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED.withMinSize(5);

		assertTrue(config.minSize().isPresent());
		assertEquals(5, config.minSize().getAsInt());
		assertTrue(config.maxSize().isEmpty());
	}

	@Test
	void withMinSizePreservesMaxSize() {
		SizeConstraintConfig initial = new SizeConstraintConfig(OptionalInt.empty(), OptionalInt.of(20));
		SizeConstraintConfig config = initial.withMinSize(10);

		assertTrue(config.minSize().isPresent());
		assertEquals(10, config.minSize().getAsInt());
		assertTrue(config.maxSize().isPresent());
		assertEquals(20, config.maxSize().getAsInt());
	}

	@Test
	void withMinSizeNegative() {
		assertThrows(IllegalArgumentException.class, () -> SizeConstraintConfig.UNCONSTRAINED.withMinSize(-1));
	}

	@Test
	void withMinSizeExceedsMaxSize() {
		SizeConstraintConfig config = new SizeConstraintConfig(OptionalInt.empty(), OptionalInt.of(5));
		assertThrows(IllegalArgumentException.class, () -> config.withMinSize(10));
	}

	@Test
	void withMaxSize() {
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED.withMaxSize(10);

		assertTrue(config.minSize().isEmpty());
		assertTrue(config.maxSize().isPresent());
		assertEquals(10, config.maxSize().getAsInt());
	}

	@Test
	void withMaxSizePreservesMinSize() {
		SizeConstraintConfig initial = new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.empty());
		SizeConstraintConfig config = initial.withMaxSize(15);

		assertTrue(config.minSize().isPresent());
		assertEquals(5, config.minSize().getAsInt());
		assertTrue(config.maxSize().isPresent());
		assertEquals(15, config.maxSize().getAsInt());
	}

	@Test
	void withMaxSizeNegative() {
		assertThrows(IllegalArgumentException.class, () -> SizeConstraintConfig.UNCONSTRAINED.withMaxSize(-1));
	}

	@Test
	void withMaxSizeLessThanMinSize() {
		SizeConstraintConfig config = new SizeConstraintConfig(OptionalInt.of(10), OptionalInt.empty());
		assertThrows(IllegalArgumentException.class, () -> config.withMaxSize(5));
	}

	@Test
	void withSize() {
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED.withSize(5, 10);

		assertTrue(config.minSize().isPresent());
		assertEquals(5, config.minSize().getAsInt());
		assertTrue(config.maxSize().isPresent());
		assertEquals(10, config.maxSize().getAsInt());
	}

	@Test
	void withSizeReplacesExistingConstraints() {
		SizeConstraintConfig initial = new SizeConstraintConfig(OptionalInt.of(1), OptionalInt.of(100));
		SizeConstraintConfig config = initial.withSize(10, 20);

		assertTrue(config.minSize().isPresent());
		assertEquals(10, config.minSize().getAsInt());
		assertTrue(config.maxSize().isPresent());
		assertEquals(20, config.maxSize().getAsInt());
	}

	@Test
	void withSizeNegativeMinSize() {
		assertThrows(IllegalArgumentException.class, () -> SizeConstraintConfig.UNCONSTRAINED.withSize(-1, 10));
	}

	@Test
	void withSizeNegativeMaxSize() {
		assertThrows(IllegalArgumentException.class, () -> SizeConstraintConfig.UNCONSTRAINED.withSize(5, -1));
	}

	@Test
	void withSizeMaxLessThanMin() {
		assertThrows(IllegalArgumentException.class, () -> SizeConstraintConfig.UNCONSTRAINED.withSize(10, 5));
	}

	@Test
	void matchesUnconstrained() {
		SizeConstraintConfig config = SizeConstraintConfig.UNCONSTRAINED;

		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(100).isSuccess());
		assertTrue(config.matches(Integer.MAX_VALUE).isSuccess());
	}

	@Test
	void matchesMinSizeOnly() {
		SizeConstraintConfig config = new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.empty());

		Result<Void> resultBelow = config.matches(4);
		assertTrue(resultBelow.isError());
		assertTrue(resultBelow.errorOrThrow().contains("Violated minimum size constraint"));
		assertTrue(resultBelow.errorOrThrow().contains("4 < 5"));

		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(6).isSuccess());
		assertTrue(config.matches(100).isSuccess());
	}

	@Test
	void matchesMaxSizeOnly() {
		SizeConstraintConfig config = new SizeConstraintConfig(OptionalInt.empty(), OptionalInt.of(10));

		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(10).isSuccess());

		Result<Void> resultAbove = config.matches(11);
		assertTrue(resultAbove.isError());
		assertTrue(resultAbove.errorOrThrow().contains("Violated maximum size constraint"));
		assertTrue(resultAbove.errorOrThrow().contains("11 > 10"));
	}

	@Test
	void matchesBothMinAndMaxSize() {
		SizeConstraintConfig config = new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.of(10));

		Result<Void> resultBelowMin = config.matches(4);
		assertTrue(resultBelowMin.isError());
		assertTrue(resultBelowMin.errorOrThrow().contains("Violated minimum size constraint"));

		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(7).isSuccess());
		assertTrue(config.matches(10).isSuccess());

		Result<Void> resultAboveMax = config.matches(11);
		assertTrue(resultAboveMax.isError());
		assertTrue(resultAboveMax.errorOrThrow().contains("Violated maximum size constraint"));
	}

	@Test
	void matchesExactSize() {
		SizeConstraintConfig config = new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.of(5));

		Result<Void> resultBelow = config.matches(4);
		assertTrue(resultBelow.isError());

		assertTrue(config.matches(5).isSuccess());

		Result<Void> resultAbove = config.matches(6);
		assertTrue(resultAbove.isError());
	}

	@Test
	void matchesZeroSize() {
		SizeConstraintConfig config = new SizeConstraintConfig(OptionalInt.of(0), OptionalInt.of(0));

		assertTrue(config.matches(0).isSuccess());

		Result<Void> resultAbove = config.matches(1);
		assertTrue(resultAbove.isError());
	}

	@Test
	void toStringUnconstrained() {
		String str = SizeConstraintConfig.UNCONSTRAINED.toString();
		assertEquals("SizeConstraintConfig[unconstrained]", str);
	}

	@Test
	void toStringMinSizeOnly() {
		SizeConstraintConfig config = new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.empty());
		String str = config.toString();
		assertEquals("SizeConstraintConfig[minSize=5]", str);
	}

	@Test
	void toStringMaxSizeOnly() {
		SizeConstraintConfig config = new SizeConstraintConfig(OptionalInt.empty(), OptionalInt.of(10));
		String str = config.toString();
		assertEquals("SizeConstraintConfig[maxSize=10]", str);
	}

	@Test
	void toStringBothMinAndMaxSize() {
		SizeConstraintConfig config = new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.of(10));
		String str = config.toString();
		assertEquals("SizeConstraintConfig[minSize=5,maxSize=10]", str);
	}

	@Test
	void toStringExactSize() {
		SizeConstraintConfig config = new SizeConstraintConfig(OptionalInt.of(7), OptionalInt.of(7));
		String str = config.toString();
		assertEquals("SizeConstraintConfig[size=7]", str);
	}

	@Test
	void equality() {
		SizeConstraintConfig config1 = new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.of(10));
		SizeConstraintConfig config2 = new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.of(10));
		SizeConstraintConfig config3 = new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.of(15));

		assertEquals(config1, config2);
		assertNotEquals(config1, config3);
	}

	@Test
	void hashCodeConsistency() {
		SizeConstraintConfig config1 = new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.of(10));
		SizeConstraintConfig config2 = new SizeConstraintConfig(OptionalInt.of(5), OptionalInt.of(10));

		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
