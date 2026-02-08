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

package net.luis.utils.io.codec.constraint.config;

import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class StringConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new StringConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new StringConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new StringConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithEqualMinMaxExclusiveBound() {
		assertThrows(IllegalArgumentException.class, () -> new LengthConstraintConfig(
			Optional.empty(), Optional.empty(),
			Optional.of(Pair.of(5, false)), Optional.of(Pair.of(5, true)),
			Optional.empty()
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
		assertThrows(IllegalArgumentException.class, () -> StringConstraintConfig.UNCONSTRAINED.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(1)).withBlank());
	}
	
	@Test
	void constructWithNotBlankAndMaxLengthZero() {
		assertThrows(IllegalArgumentException.class, () -> StringConstraintConfig.UNCONSTRAINED.withLength(LengthConstraintConfig.UNCONSTRAINED.withMaxLength(0)).withNotBlank());
	}
	
	@Test
	void unconstrained() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.length().isEmpty());
		assertDoesNotThrow(() -> config.validate("any string"));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(StringConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("test");
		assertFalse(config.isUnconstrained());
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
	void withLength() {
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(2).withMaxLength(10);
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withLength(lengthConfig);
		assertTrue(config.length().isPresent());
		assertEquals(lengthConfig, config.length().get());
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
	void validateWithEqualTo() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEqualTo("hello");
		assertDoesNotThrow(() -> config.validate("hello"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("world"));
	}
	
	@Test
	void validateWithNotEqualTo() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNotEqualTo("forbidden");
		assertDoesNotThrow(() -> config.validate("allowed"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("forbidden"));
	}
	
	@Test
	void validateWithIn() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withIn(List.of("a", "b", "c"));
		assertDoesNotThrow(() -> config.validate("a"));
		assertDoesNotThrow(() -> config.validate("b"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("d"));
	}
	
	@Test
	void validateWithLength() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(2).withMaxLength(5));
		assertDoesNotThrow(() -> config.validate("ab"));
		assertDoesNotThrow(() -> config.validate("abcde"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("a"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("abcdef"));
	}
	
	@Test
	void validateWithStartsWith() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withStartsWith("pre");
		assertDoesNotThrow(() -> config.validate("prefix"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("postfix"));
	}
	
	@Test
	void validateWithContains() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withContains("mid");
		assertDoesNotThrow(() -> config.validate("amid"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("test"));
	}
	
	@Test
	void validateWithEndsWith() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withEndsWith("suf");
		assertDoesNotThrow(() -> config.validate("testsuf"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("suffix"));
	}
	
	@Test
	void validateWithPattern() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withMatches("[a-z]+");
		assertDoesNotThrow(() -> config.validate("abc"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("ABC"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("123"));
	}
	
	@Test
	void validateWithTrimmed() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withTrimmed();
		assertDoesNotThrow(() -> config.validate("trimmed"));
		assertThrows(ConstraintViolateException.class, () -> config.validate(" spaces "));
	}
	
	@Test
	void validateWithBlank() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withBlank();
		assertDoesNotThrow(() -> config.validate(""));
		assertDoesNotThrow(() -> config.validate("   "));
		assertThrows(ConstraintViolateException.class, () -> config.validate("text"));
	}
	
	@Test
	void validateWithNotBlank() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNotBlank();
		assertDoesNotThrow(() -> config.validate("text"));
		assertThrows(ConstraintViolateException.class, () -> config.validate(""));
		assertThrows(ConstraintViolateException.class, () -> config.validate("   "));
	}
	
	@Test
	void validateWithUpperCase() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withUpperCase();
		assertDoesNotThrow(() -> config.validate("HELLO"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("Hello"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("hello"));
	}
	
	@Test
	void validateWithLowerCase() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withLowerCase();
		assertDoesNotThrow(() -> config.validate("hello"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("Hello"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("HELLO"));
	}
	
	@Test
	void validateWithNumeric() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withNumeric();
		assertDoesNotThrow(() -> config.validate("12345"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("123a5"));
	}
	
	@Test
	void validateWithAlphabetic() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphabetic();
		assertDoesNotThrow(() -> config.validate("hello"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("hello1"));
	}
	
	@Test
	void validateWithAlphanumeric() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAlphanumeric();
		assertDoesNotThrow(() -> config.validate("hello123"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("hello-123"));
	}
	
	@Test
	void validateWithAscii() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED.withAscii();
		assertDoesNotThrow(() -> config.validate("hello"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("hello\u00E9"));
	}
	
	@Test
	void validateWithMultipleConstraints() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED
			.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(3).withMaxLength(10))
			.withStartsWith("a")
			.withNotBlank();
		
		assertDoesNotThrow(() -> config.validate("abc"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("ab"));
		assertThrows(ConstraintViolateException.class, () -> config.validate("bcd"));
	}
	
	@Test
	void validateWithNullValue() {
		StringConstraintConfig config = StringConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
