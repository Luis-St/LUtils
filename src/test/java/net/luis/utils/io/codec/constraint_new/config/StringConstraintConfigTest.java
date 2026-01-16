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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Test class for {@link StringConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class StringConstraintConfigTest {

	@Test
	void unconstrained() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.minLength().isEmpty());
		assertTrue(config.maxLength().isEmpty());
		assertTrue(config.matches("any string").isSuccess());
	}

	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new StringConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new StringConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new StringConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNegativeMinLength() {
		assertThrows(IllegalArgumentException.class, () -> StringConstraintConfig.UNCONSTRAINED.withMinLength(-1));
	}

	@Test
	void constructWithNegativeMaxLength() {
		assertThrows(IllegalArgumentException.class, () -> StringConstraintConfig.UNCONSTRAINED.withMaxLength(-1));
	}

	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> StringConstraintConfig.UNCONSTRAINED.withLengthBetween(10, 5));
	}

	@Test
	void constructWithEqualMinMaxExclusiveBound() {
		assertThrows(IllegalArgumentException.class, () -> new StringConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.of(Pair.of(5, false)), Optional.of(Pair.of(5, true)),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithBlankAndNotBlank() {
		assertThrows(IllegalArgumentException.class, () -> StringConstraintConfig.UNCONSTRAINED.withBlank().withNotBlank());
	}

	@Test
	void constructWithUpperCaseAndLowerCase() {
		assertThrows(IllegalArgumentException.class, () -> StringConstraintConfig.UNCONSTRAINED.withUpperCase().withLowerCase());
	}

	@Test
	void constructWithBlankAndMinLengthGreaterZero() {
		assertThrows(IllegalArgumentException.class, () -> StringConstraintConfig.UNCONSTRAINED.withMinLength(1).withBlank());
	}

	@Test
	void constructWithNotBlankAndMaxLengthZero() {
		assertThrows(IllegalArgumentException.class, () -> StringConstraintConfig.UNCONSTRAINED.withMaxLength(0).withNotBlank());
	}

	@Test
	void withEqualTo() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("test");
		assertTrue(config.equalTo().isPresent());
		assertEquals("test", config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}

	@Test
	void withNotEqualTo() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNotEqualTo("test");
		assertTrue(config.equalTo().isPresent());
		assertEquals("test", config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}

	@Test
	void withIn() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withIn(List.of("a", "b", "c"));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of("a", "b", "c"), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}

	@Test
	void withNotIn() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNotIn(List.of("x", "y"));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of("x", "y"), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}

	@Test
	void withMinLength() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withMinLength(5);
		assertTrue(config.minLength().isPresent());
		assertEquals(5, config.minLength().get().getFirst());
		assertTrue(config.minLength().get().getSecond());
	}

	@Test
	void withMaxLength() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withMaxLength(10);
		assertTrue(config.maxLength().isPresent());
		assertEquals(10, config.maxLength().get().getFirst());
		assertTrue(config.maxLength().get().getSecond());
	}

	@Test
	void withExactLength() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withExactLength(7);
		assertTrue(config.minLength().isPresent());
		assertTrue(config.maxLength().isPresent());
		assertEquals(7, config.minLength().get().getFirst());
		assertEquals(7, config.maxLength().get().getFirst());
	}

	@Test
	void withLengthBetween() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withLengthBetween(3, 8);
		assertTrue(config.minLength().isPresent());
		assertTrue(config.maxLength().isPresent());
		assertEquals(3, config.minLength().get().getFirst());
		assertEquals(8, config.maxLength().get().getFirst());
	}

	@Test
	void withStartsWith() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("pre");
		assertTrue(config.startsWith().isPresent());
		assertEquals("pre", config.startsWith().get().getFirst());
		assertFalse(config.startsWith().get().getSecond());
	}

	@Test
	void withNotStartsWith() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNotStartsWith("pre");
		assertTrue(config.startsWith().isPresent());
		assertEquals("pre", config.startsWith().get().getFirst());
		assertTrue(config.startsWith().get().getSecond());
	}

	@Test
	void withContains() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("mid");
		assertTrue(config.contains().isPresent());
		assertEquals("mid", config.contains().get().getFirst());
		assertFalse(config.contains().get().getSecond());
	}

	@Test
	void withNotContains() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNotContains("mid");
		assertTrue(config.contains().isPresent());
		assertEquals("mid", config.contains().get().getFirst());
		assertTrue(config.contains().get().getSecond());
	}

	@Test
	void withEndsWith() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith("suf");
		assertTrue(config.endsWith().isPresent());
		assertEquals("suf", config.endsWith().get().getFirst());
		assertFalse(config.endsWith().get().getSecond());
	}

	@Test
	void withMatchesString() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withMatches("[a-z]+");
		assertTrue(config.matches().isPresent());
		assertEquals("[a-z]+", config.matches().get().getFirst().pattern());
		assertFalse(config.matches().get().getSecond());
	}

	@Test
	void withMatchesPattern() {
		Pattern pattern = Pattern.compile("[0-9]+");
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withMatches(pattern);
		assertTrue(config.matches().isPresent());
		assertSame(pattern, config.matches().get().getFirst());
		assertFalse(config.matches().get().getSecond());
	}

	@Test
	void matchesWithEqualTo() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("hello");
		assertTrue(config.matches("hello").isSuccess());
		assertTrue(config.matches("world").isError());
	}

	@Test
	void matchesWithNotEqualTo() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNotEqualTo("forbidden");
		assertTrue(config.matches("allowed").isSuccess());
		assertTrue(config.matches("forbidden").isError());
	}

	@Test
	void matchesWithIn() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withIn(List.of("a", "b", "c"));
		assertTrue(config.matches("a").isSuccess());
		assertTrue(config.matches("b").isSuccess());
		assertTrue(config.matches("d").isError());
	}

	@Test
	void matchesWithLength() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withLengthBetween(2, 5);
		assertTrue(config.matches("ab").isSuccess());
		assertTrue(config.matches("abcde").isSuccess());
		assertTrue(config.matches("a").isError());
		assertTrue(config.matches("abcdef").isError());
	}

	@Test
	void matchesWithStartsWith() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("pre");
		assertTrue(config.matches("prefix").isSuccess());
		assertTrue(config.matches("postfix").isError());
	}

	@Test
	void matchesWithContains() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("mid");
		assertTrue(config.matches("amid").isSuccess());
		assertTrue(config.matches("test").isError());
	}

	@Test
	void matchesWithEndsWith() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith("suf");
		assertTrue(config.matches("testsuf").isSuccess());
		assertTrue(config.matches("suffix").isError());
	}

	@Test
	void matchesWithPattern() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withMatches("[a-z]+");
		assertTrue(config.matches("abc").isSuccess());
		assertTrue(config.matches("ABC").isError());
		assertTrue(config.matches("123").isError());
	}

	@Test
	void matchesWithTrimmed() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withTrimmed();
		assertTrue(config.matches("trimmed").isSuccess());
		assertTrue(config.matches(" spaces ").isError());
	}

	@Test
	void matchesWithBlank() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withBlank();
		assertTrue(config.matches("").isSuccess());
		assertTrue(config.matches("   ").isSuccess());
		assertTrue(config.matches("text").isError());
	}

	@Test
	void matchesWithNotBlank() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNotBlank();
		assertTrue(config.matches("text").isSuccess());
		assertTrue(config.matches("").isError());
		assertTrue(config.matches("   ").isError());
	}

	@Test
	void matchesWithUpperCase() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withUpperCase();
		assertTrue(config.matches("HELLO").isSuccess());
		assertTrue(config.matches("Hello").isError());
		assertTrue(config.matches("hello").isError());
	}

	@Test
	void matchesWithLowerCase() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withLowerCase();
		assertTrue(config.matches("hello").isSuccess());
		assertTrue(config.matches("Hello").isError());
		assertTrue(config.matches("HELLO").isError());
	}

	@Test
	void matchesWithNumeric() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNumeric();
		assertTrue(config.matches("12345").isSuccess());
		assertTrue(config.matches("123a5").isError());
	}

	@Test
	void matchesWithAlphabetic() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		assertTrue(config.matches("hello").isSuccess());
		assertTrue(config.matches("hello1").isError());
	}

	@Test
	void matchesWithAlphanumeric() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphanumeric();
		assertTrue(config.matches("hello123").isSuccess());
		assertTrue(config.matches("hello-123").isError());
	}

	@Test
	void matchesWithAscii() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAscii();
		assertTrue(config.matches("hello").isSuccess());
		assertTrue(config.matches("hello\u00E9").isError());
	}

	@Test
	void matchesWithMultipleConstraints() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED
			.withMinLength(3)
			.withMaxLength(10)
			.withStartsWith("a")
			.withNotBlank();

		assertTrue(config.matches("abc").isSuccess());
		assertTrue(config.matches("ab").isError());
		assertTrue(config.matches("bcd").isError());
	}

	@Test
	void matchesWithNullValue() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
