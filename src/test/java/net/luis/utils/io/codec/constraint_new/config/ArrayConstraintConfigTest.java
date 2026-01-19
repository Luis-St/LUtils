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

package net.luis.utils.io.codec.constraint_new.config;

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ArrayConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class ArrayConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new ArrayConstraintConfig<String>(
			null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new ArrayConstraintConfig<String>(
			Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullLength() {
		assertThrows(NullPointerException.class, () -> new ArrayConstraintConfig<String>(
			Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new ArrayConstraintConfig<String>(
			Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new ArrayConstraintConfig<String>(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> ArrayConstraintConfig.<String>unconstrained().withMinLength(-1));
	}
	
	@Test
	void constructWithNegativeMax() {
		assertThrows(IllegalArgumentException.class, () -> ArrayConstraintConfig.<String>unconstrained().withMaxLength(-1));
	}
	
	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> ArrayConstraintConfig.<String>unconstrained().withLengthBetween(10, 5));
	}
	
	@Test
	void unconstrained() {
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.unconstrained();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(new String[] { "a", "b" }).isSuccess());
	}
	
	@Test
	void withEqualTo() {
		String[] array = new String[] { "a", "b" };
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withEqualTo(array);
		assertTrue(config.equalTo().isPresent());
		assertArrayEquals(array, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> ArrayConstraintConfig.<String>unconstrained().withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		String[] array = new String[] { "a", "b" };
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withNotEqualTo(array);
		assertTrue(config.equalTo().isPresent());
		assertArrayEquals(array, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> ArrayConstraintConfig.<String>unconstrained().withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		List<String[]> arrays = List.of(new String[] { "a" }, new String[] { "b" });
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withIn(arrays);
		assertTrue(config.in().isPresent());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> ArrayConstraintConfig.<String>unconstrained().withIn(null));
	}
	
	@Test
	void withNotIn() {
		List<String[]> arrays = List.of(new String[] { "a" }, new String[] { "b" });
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withNotIn(arrays);
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> ArrayConstraintConfig.<String>unconstrained().withNotIn(null));
	}
	
	@Test
	void withMinLength() {
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withMinLength(5);
		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertEquals(5, config.length().get().min().get().getFirst());
		assertTrue(config.length().get().min().get().getSecond());
	}
	
	@Test
	void withMaxLength() {
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withMaxLength(10);
		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(10, config.length().get().max().get().getFirst());
		assertTrue(config.length().get().max().get().getSecond());
	}
	
	@Test
	void withExactLength() {
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withExactLength(7);
		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(7, config.length().get().min().get().getFirst());
		assertEquals(7, config.length().get().max().get().getFirst());
	}
	
	@Test
	void withLengthBetween() {
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withLengthBetween(3, 8);
		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(3, config.length().get().min().get().getFirst());
		assertEquals(8, config.length().get().max().get().getFirst());
	}
	
	@Test
	void withLength() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(2).withMaxLength(10);
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withLength(lengthConfig);
		assertTrue(config.length().isPresent());
		assertEquals(lengthConfig, config.length().get());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(2, config.length().get().min().get().getFirst());
		assertEquals(10, config.length().get().max().get().getFirst());
	}
	
	@Test
	void withLengthNull() {
		assertThrows(NullPointerException.class, () -> ArrayConstraintConfig.<String>unconstrained().withLength(null));
	}
	
	@Test
	void withCustom() {
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withCustom(arr -> arr.length > 0 ? Result.success() : Result.error("Array must not be empty"));
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> ArrayConstraintConfig.<String>unconstrained().withCustom(null));
	}
	
	@Test
	void matchesWithEqualTo() {
		String[] array = new String[] { "a", "b" };
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withEqualTo(array);
		assertTrue(config.matches(new String[] { "a", "b" }).isSuccess());
		assertTrue(config.matches(new String[] { "c", "d" }).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		String[] array = new String[] { "a", "b" };
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withNotEqualTo(array);
		assertTrue(config.matches(new String[] { "c", "d" }).isSuccess());
		assertTrue(config.matches(new String[] { "a", "b" }).isError());
	}
	
	@Test
	void matchesWithMinLength() {
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withMinLength(2);
		assertTrue(config.matches(new String[] { "a", "b" }).isSuccess());
		assertTrue(config.matches(new String[] { "a" }).isError());
	}
	
	@Test
	void matchesWithMaxLength() {
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withMaxLength(2);
		assertTrue(config.matches(new String[] { "a", "b" }).isSuccess());
		assertTrue(config.matches(new String[] { "a", "b", "c" }).isError());
	}
	
	@Test
	void matchesWithExactLength() {
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withExactLength(2);
		assertTrue(config.matches(new String[] { "a", "b" }).isSuccess());
		assertTrue(config.matches(new String[] { "a" }).isError());
		assertTrue(config.matches(new String[] { "a", "b", "c" }).isError());
	}
	
	@Test
	void matchesWithLength() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(1).withMaxLength(3);
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained().withLength(lengthConfig);
		assertTrue(config.matches(new String[] { "a" }).isSuccess());
		assertTrue(config.matches(new String[] { "a", "b", "c" }).isSuccess());
		assertTrue(config.matches(new String[] {}).isError());
		assertTrue(config.matches(new String[] { "a", "b", "c", "d" }).isError());
	}
	
	@Test
	void matchesWithMultipleConstraints() {
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.<String>unconstrained()
			.withMinLength(1)
			.withMaxLength(3);
		
		assertTrue(config.matches(new String[] { "a", "b" }).isSuccess());
		assertTrue(config.matches(new String[] {}).isError());
		assertTrue(config.matches(new String[] { "a", "b", "c", "d" }).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		ArrayConstraintConfig<String> config = ArrayConstraintConfig.unconstrained();
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
