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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharacterConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class CharacterConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new CharacterConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new CharacterConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new CharacterConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> CharacterConstraintConfig.UNCONSTRAINED.withBetweenOrEqual('z', 'a'));
	}
	
	@Test
	void constructWithEqualMinMaxExclusiveBound() {
		assertThrows(IllegalArgumentException.class, () -> CharacterConstraintConfig.UNCONSTRAINED.withBetween('a', 'a'));
	}
	
	@Test
	void constructWithUpperCaseAndLowerCase() {
		assertThrows(IllegalArgumentException.class, () -> CharacterConstraintConfig.UNCONSTRAINED.withUpperCase().withLowerCase());
	}
	
	@Test
	void unconstrained() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.letter().isEmpty());
		assertTrue(config.digit().isEmpty());
		assertTrue(config.alphanumeric().isEmpty());
		assertTrue(config.whitespace().isEmpty());
		assertTrue(config.punctuation().isEmpty());
		assertTrue(config.symbol().isEmpty());
		assertTrue(config.control().isEmpty());
		assertTrue(config.upperCase().isEmpty());
		assertTrue(config.lowerCase().isEmpty());
		assertTrue(config.ascii().isEmpty());
		assertTrue(config.latin1().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertDoesNotThrow(() -> config.validate('a'));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(CharacterConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLetter();
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withEqualTo('x');
		assertTrue(config.equalTo().isPresent());
		assertEquals('x', config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withNotEqualTo('x');
		assertTrue(config.equalTo().isPresent());
		assertEquals('x', config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withIn(List.of('a', 'b', 'c'));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of('a', 'b', 'c'), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> CharacterConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withNotIn(List.of('x', 'y'));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of('x', 'y'), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> CharacterConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withGreaterThan() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withGreaterThan('a');
		assertTrue(config.min().isPresent());
		assertEquals('a', config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
	}
	
	@Test
	void withGreaterThanOrEqual() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual('a');
		assertTrue(config.min().isPresent());
		assertEquals('a', config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
	}
	
	@Test
	void withLessThan() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLessThan('z');
		assertTrue(config.max().isPresent());
		assertEquals('z', config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withLessThanOrEqual() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLessThanOrEqual('z');
		assertTrue(config.max().isPresent());
		assertEquals('z', config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withBetween() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withBetween('a', 'z');
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals('a', config.min().get().getFirst());
		assertEquals('z', config.max().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqual() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withBetweenOrEqual('a', 'z');
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals('a', config.min().get().getFirst());
		assertEquals('z', config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withLetter() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLetter();
		assertTrue(config.letter().isPresent());
	}
	
	@Test
	void withDigit() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withDigit();
		assertTrue(config.digit().isPresent());
	}
	
	@Test
	void withAlphanumeric() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withAlphanumeric();
		assertTrue(config.alphanumeric().isPresent());
	}
	
	@Test
	void withWhitespace() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withWhitespace();
		assertTrue(config.whitespace().isPresent());
	}
	
	@Test
	void withPunctuation() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withPunctuation();
		assertTrue(config.punctuation().isPresent());
	}
	
	@Test
	void withSymbol() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withSymbol();
		assertTrue(config.symbol().isPresent());
	}
	
	@Test
	void withControl() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withControl();
		assertTrue(config.control().isPresent());
	}
	
	@Test
	void withUpperCase() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withUpperCase();
		assertTrue(config.upperCase().isPresent());
	}
	
	@Test
	void withLowerCase() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLowerCase();
		assertTrue(config.lowerCase().isPresent());
	}
	
	@Test
	void withAscii() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withAscii();
		assertTrue(config.ascii().isPresent());
	}
	
	@Test
	void withLatin1() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLatin1();
		assertTrue(config.latin1().isPresent());
	}
	
	@Test
	void withCustom() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withCustom(c -> {
			if (!Character.isLetter(c)) throw new ConstraintViolateException("Must be letter");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> CharacterConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void validateWithEqualTo() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withEqualTo('a');
		assertDoesNotThrow(() -> config.validate('a'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('b'));
	}
	
	@Test
	void validateWithNotEqualTo() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withNotEqualTo('a');
		assertDoesNotThrow(() -> config.validate('b'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('a'));
	}
	
	@Test
	void validateWithIn() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withIn(List.of('a', 'b', 'c'));
		assertDoesNotThrow(() -> config.validate('a'));
		assertDoesNotThrow(() -> config.validate('b'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('d'));
	}
	
	@Test
	void validateWithNotIn() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withNotIn(List.of('x', 'y'));
		assertDoesNotThrow(() -> config.validate('a'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('x'));
	}
	
	@Test
	void validateWithGreaterThan() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withGreaterThan('m');
		assertDoesNotThrow(() -> config.validate('n'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('m'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('a'));
	}
	
	@Test
	void validateWithGreaterThanOrEqual() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual('m');
		assertDoesNotThrow(() -> config.validate('m'));
		assertDoesNotThrow(() -> config.validate('n'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('l'));
	}
	
	@Test
	void validateWithLessThan() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLessThan('m');
		assertDoesNotThrow(() -> config.validate('l'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('m'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('z'));
	}
	
	@Test
	void validateWithLessThanOrEqual() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLessThanOrEqual('m');
		assertDoesNotThrow(() -> config.validate('m'));
		assertDoesNotThrow(() -> config.validate('l'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('n'));
	}
	
	@Test
	void validateWithBetween() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withBetween('a', 'z');
		assertDoesNotThrow(() -> config.validate('m'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('a'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('z'));
	}
	
	@Test
	void validateWithBetweenOrEqual() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withBetweenOrEqual('a', 'z');
		assertDoesNotThrow(() -> config.validate('a'));
		assertDoesNotThrow(() -> config.validate('z'));
		assertDoesNotThrow(() -> config.validate('m'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('A'));
	}
	
	@Test
	void validateWithLetter() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLetter();
		assertDoesNotThrow(() -> config.validate('a'));
		assertDoesNotThrow(() -> config.validate('Z'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('1'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('!'));
	}
	
	@Test
	void validateWithDigit() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withDigit();
		assertDoesNotThrow(() -> config.validate('0'));
		assertDoesNotThrow(() -> config.validate('9'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('a'));
	}
	
	@Test
	void validateWithAlphanumeric() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withAlphanumeric();
		assertDoesNotThrow(() -> config.validate('a'));
		assertDoesNotThrow(() -> config.validate('5'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('!'));
	}
	
	@Test
	void validateWithWhitespace() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withWhitespace();
		assertDoesNotThrow(() -> config.validate(' '));
		assertDoesNotThrow(() -> config.validate('\t'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('a'));
	}
	
	@Test
	void validateWithPunctuation() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withPunctuation();
		assertDoesNotThrow(() -> config.validate('.'));
		assertDoesNotThrow(() -> config.validate(','));
		assertThrows(ConstraintViolateException.class, () -> config.validate('a'));
	}
	
	@Test
	void validateWithSymbol() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withSymbol();
		assertDoesNotThrow(() -> config.validate('+'));
		assertDoesNotThrow(() -> config.validate('$'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('a'));
	}
	
	@Test
	void validateWithControl() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withControl();
		assertDoesNotThrow(() -> config.validate('\u0000'));
		assertDoesNotThrow(() -> config.validate('\u001F'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('a'));
	}
	
	@Test
	void validateWithUpperCase() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withUpperCase();
		assertDoesNotThrow(() -> config.validate('A'));
		assertDoesNotThrow(() -> config.validate('Z'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('a'));
	}
	
	@Test
	void validateWithLowerCase() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLowerCase();
		assertDoesNotThrow(() -> config.validate('a'));
		assertDoesNotThrow(() -> config.validate('z'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('A'));
	}
	
	@Test
	void validateWithAscii() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withAscii();
		assertDoesNotThrow(() -> config.validate('a'));
		assertDoesNotThrow(() -> config.validate('\u007F'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('\u0080'));
	}
	
	@Test
	void validateWithLatin1() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLatin1();
		assertDoesNotThrow(() -> config.validate('a'));
		assertDoesNotThrow(() -> config.validate('\u00FF'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('\u0100'));
	}
	
	@Test
	void validateWithMultipleConstraints() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED
			.withLetter()
			.withLowerCase()
			.withAscii();
		
		assertDoesNotThrow(() -> config.validate('a'));
		assertDoesNotThrow(() -> config.validate('z'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('A'));
		assertThrows(ConstraintViolateException.class, () -> config.validate('1'));
	}
	
	@Test
	void validateWithNullValue() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
