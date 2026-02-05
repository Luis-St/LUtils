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

package net.luis.utils.io.codec.constraint.config.collection;

import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PrimitiveArrayConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class PrimitiveArrayConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new PrimitiveArrayConstraintConfig<int[]>(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Arrays::equals, arr -> arr.length
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new PrimitiveArrayConstraintConfig<int[]>(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Arrays::equals, arr -> arr.length
		));
	}
	
	@Test
	void constructWithNullLength() {
		assertThrows(NullPointerException.class, () -> new PrimitiveArrayConstraintConfig<int[]>(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Arrays::equals, arr -> arr.length
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new PrimitiveArrayConstraintConfig<int[]>(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Arrays::equals, arr -> arr.length
		));
	}
	
	@Test
	void constructWithNullEqualityFunction() {
		assertThrows(NullPointerException.class, () -> new PrimitiveArrayConstraintConfig<int[]>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, arr -> arr.length
		));
	}
	
	@Test
	void constructWithNullLengthExtractor() {
		assertThrows(NullPointerException.class, () -> new PrimitiveArrayConstraintConfig<int[]>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Arrays::equals, null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new PrimitiveArrayConstraintConfig<int[]>(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Arrays::equals, arr -> arr.length
		));
	}
	
	@Test
	void constructWithNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> PrimitiveArrayConstraintConfig.intArray().withMinLength(-1));
	}
	
	@Test
	void constructWithNegativeMax() {
		assertThrows(IllegalArgumentException.class, () -> PrimitiveArrayConstraintConfig.intArray().withMaxLength(-1));
	}
	
	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> PrimitiveArrayConstraintConfig.intArray().withLengthBetween(10, 5));
	}
	
	@Test
	void intArray() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(new int[] { 1, 2, 3 }).isSuccess());
	}

	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(PrimitiveArrayConstraintConfig.intArray().isUnconstrained());
	}

	@Test
	void isUnconstrainedWithConstraint() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withMinLength(1);
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void longArray() {
		PrimitiveArrayConstraintConfig<long[]> config = PrimitiveArrayConstraintConfig.longArray();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(new long[] { 1L, 2L, 3L }).isSuccess());
	}
	
	@Test
	void doubleArray() {
		PrimitiveArrayConstraintConfig<double[]> config = PrimitiveArrayConstraintConfig.doubleArray();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(new double[] { 1.0, 2.0, 3.0 }).isSuccess());
	}
	
	@Test
	void floatArray() {
		PrimitiveArrayConstraintConfig<float[]> config = PrimitiveArrayConstraintConfig.floatArray();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(new float[] { 1.0f, 2.0f, 3.0f }).isSuccess());
	}
	
	@Test
	void shortArray() {
		PrimitiveArrayConstraintConfig<short[]> config = PrimitiveArrayConstraintConfig.shortArray();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(new short[] { 1, 2, 3 }).isSuccess());
	}
	
	@Test
	void byteArray() {
		PrimitiveArrayConstraintConfig<byte[]> config = PrimitiveArrayConstraintConfig.byteArray();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(new byte[] { 1, 2, 3 }).isSuccess());
	}
	
	@Test
	void booleanArray() {
		PrimitiveArrayConstraintConfig<boolean[]> config = PrimitiveArrayConstraintConfig.booleanArray();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(new boolean[] { true, false, true }).isSuccess());
	}
	
	@Test
	void charArray() {
		PrimitiveArrayConstraintConfig<char[]> config = PrimitiveArrayConstraintConfig.charArray();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(new char[] { 'a', 'b', 'c' }).isSuccess());
	}
	
	@Test
	void withEqualTo() {
		int[] array = new int[] { 1, 2, 3 };
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withEqualTo(array);
		assertTrue(config.equalTo().isPresent());
		assertArrayEquals(array, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> PrimitiveArrayConstraintConfig.intArray().withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		int[] array = new int[] { 1, 2, 3 };
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withNotEqualTo(array);
		assertTrue(config.equalTo().isPresent());
		assertArrayEquals(array, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> PrimitiveArrayConstraintConfig.intArray().withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		List<int[]> arrays = List.of(new int[] { 1 }, new int[] { 2 });
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withIn(arrays);
		assertTrue(config.in().isPresent());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> PrimitiveArrayConstraintConfig.intArray().withIn(null));
	}
	
	@Test
	void withNotIn() {
		List<int[]> arrays = List.of(new int[] { 1 }, new int[] { 2 });
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withNotIn(arrays);
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> PrimitiveArrayConstraintConfig.intArray().withNotIn(null));
	}
	
	@Test
	void withMinLength() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withMinLength(5);
		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertEquals(5, config.length().get().min().get().getFirst());
		assertTrue(config.length().get().min().get().getSecond());
	}
	
	@Test
	void withMaxLength() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withMaxLength(10);
		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(10, config.length().get().max().get().getFirst());
		assertTrue(config.length().get().max().get().getSecond());
	}
	
	@Test
	void withExactLength() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withExactLength(7);
		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(7, config.length().get().min().get().getFirst());
		assertEquals(7, config.length().get().max().get().getFirst());
	}
	
	@Test
	void withLengthBetween() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withLengthBetween(3, 8);
		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(3, config.length().get().min().get().getFirst());
		assertEquals(8, config.length().get().max().get().getFirst());
	}
	
	@Test
	void withLength() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(2).withMaxLength(10);
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withLength(lengthConfig);
		assertTrue(config.length().isPresent());
		assertEquals(lengthConfig, config.length().get());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(2, config.length().get().min().get().getFirst());
		assertEquals(10, config.length().get().max().get().getFirst());
	}
	
	@Test
	void withLengthNull() {
		assertThrows(NullPointerException.class, () -> PrimitiveArrayConstraintConfig.intArray().withLength(null));
	}
	
	@Test
	void withCustom() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withCustom(arr -> arr.length > 0 ? Result.success() : Result.error("Array must not be empty"));
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> PrimitiveArrayConstraintConfig.intArray().withCustom(null));
	}
	
	@Test
	void matchesWithEqualTo() {
		int[] array = new int[] { 1, 2, 3 };
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withEqualTo(array);
		assertTrue(config.matches(new int[] { 1, 2, 3 }).isSuccess());
		assertTrue(config.matches(new int[] { 4, 5, 6 }).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		int[] array = new int[] { 1, 2, 3 };
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withNotEqualTo(array);
		assertTrue(config.matches(new int[] { 4, 5, 6 }).isSuccess());
		assertTrue(config.matches(new int[] { 1, 2, 3 }).isError());
	}
	
	@Test
	void matchesWithMinLength() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withMinLength(2);
		assertTrue(config.matches(new int[] { 1, 2 }).isSuccess());
		assertTrue(config.matches(new int[] { 1 }).isError());
	}
	
	@Test
	void matchesWithMaxLength() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withMaxLength(2);
		assertTrue(config.matches(new int[] { 1, 2 }).isSuccess());
		assertTrue(config.matches(new int[] { 1, 2, 3 }).isError());
	}
	
	@Test
	void matchesWithExactLength() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withExactLength(2);
		assertTrue(config.matches(new int[] { 1, 2 }).isSuccess());
		assertTrue(config.matches(new int[] { 1 }).isError());
		assertTrue(config.matches(new int[] { 1, 2, 3 }).isError());
	}
	
	@Test
	void matchesWithLength() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(1).withMaxLength(3);
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withLength(lengthConfig);
		assertTrue(config.matches(new int[] { 1 }).isSuccess());
		assertTrue(config.matches(new int[] { 1, 2, 3 }).isSuccess());
		assertTrue(config.matches(new int[] {}).isError());
		assertTrue(config.matches(new int[] { 1, 2, 3, 4 }).isError());
	}
	
	@Test
	void matchesWithMultipleConstraints() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray()
			.withMinLength(1)
			.withMaxLength(3);
		
		assertTrue(config.matches(new int[] { 1, 2 }).isSuccess());
		assertTrue(config.matches(new int[] {}).isError());
		assertTrue(config.matches(new int[] { 1, 2, 3, 4 }).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray();
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
	
	@Test
	void matchesWithCustomConstraint() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray()
			.withCustom(arr -> Arrays.stream(arr).allMatch(i -> i > 0) ? Result.success() : Result.error("All elements must be positive"));
		
		assertTrue(config.matches(new int[] { 1, 2, 3 }).isSuccess());
		assertTrue(config.matches(new int[] { 1, -2, 3 }).isError());
	}
	
	@Test
	void intArrayEqualityFunction() {
		PrimitiveArrayConstraintConfig<int[]> config = PrimitiveArrayConstraintConfig.intArray().withEqualTo(new int[] { 1, 2, 3 });
		assertTrue(config.matches(new int[] { 1, 2, 3 }).isSuccess());
		assertTrue(config.matches(new int[] { 1, 2 }).isError());
		assertTrue(config.matches(new int[] { 1, 2, 3, 4 }).isError());
	}
	
	@Test
	void booleanArrayEqualityFunction() {
		PrimitiveArrayConstraintConfig<boolean[]> config = PrimitiveArrayConstraintConfig.booleanArray().withEqualTo(new boolean[] { true, false });
		assertTrue(config.matches(new boolean[] { true, false }).isSuccess());
		assertTrue(config.matches(new boolean[] { false, true }).isError());
		assertTrue(config.matches(new boolean[] { true }).isError());
	}
	
	@Test
	void doubleArrayEqualityFunction() {
		PrimitiveArrayConstraintConfig<double[]> config = PrimitiveArrayConstraintConfig.doubleArray().withEqualTo(new double[] { 1.0, 2.0, 3.0 });
		assertTrue(config.matches(new double[] { 1.0, 2.0, 3.0 }).isSuccess());
		assertTrue(config.matches(new double[] { 1.0, 2.0 }).isError());
	}
	
	@Test
	void doubleArrayEqualityFunctionWithNaN() {
		PrimitiveArrayConstraintConfig<double[]> config = PrimitiveArrayConstraintConfig.doubleArray().withEqualTo(new double[] { Double.NaN });
		assertTrue(config.matches(new double[] { Double.NaN }).isSuccess());
	}
}
