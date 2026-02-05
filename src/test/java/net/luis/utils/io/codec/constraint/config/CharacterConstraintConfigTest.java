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

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
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
		assertTrue(config.matches('a').isSuccess());
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
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withCustom(c -> Character.isLetter(c) ? Result.success() : Result.error("Must be letter"));
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> CharacterConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesWithEqualTo() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withEqualTo('a');
		assertTrue(config.matches('a').isSuccess());
		assertTrue(config.matches('b').isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withNotEqualTo('a');
		assertTrue(config.matches('b').isSuccess());
		assertTrue(config.matches('a').isError());
	}
	
	@Test
	void matchesWithIn() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withIn(List.of('a', 'b', 'c'));
		assertTrue(config.matches('a').isSuccess());
		assertTrue(config.matches('b').isSuccess());
		assertTrue(config.matches('d').isError());
	}
	
	@Test
	void matchesWithNotIn() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withNotIn(List.of('x', 'y'));
		assertTrue(config.matches('a').isSuccess());
		assertTrue(config.matches('x').isError());
	}
	
	@Test
	void matchesWithGreaterThan() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withGreaterThan('m');
		assertTrue(config.matches('n').isSuccess());
		assertTrue(config.matches('m').isError());
		assertTrue(config.matches('a').isError());
	}
	
	@Test
	void matchesWithGreaterThanOrEqual() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual('m');
		assertTrue(config.matches('m').isSuccess());
		assertTrue(config.matches('n').isSuccess());
		assertTrue(config.matches('l').isError());
	}
	
	@Test
	void matchesWithLessThan() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLessThan('m');
		assertTrue(config.matches('l').isSuccess());
		assertTrue(config.matches('m').isError());
		assertTrue(config.matches('z').isError());
	}
	
	@Test
	void matchesWithLessThanOrEqual() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLessThanOrEqual('m');
		assertTrue(config.matches('m').isSuccess());
		assertTrue(config.matches('l').isSuccess());
		assertTrue(config.matches('n').isError());
	}
	
	@Test
	void matchesWithBetween() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withBetween('a', 'z');
		assertTrue(config.matches('m').isSuccess());
		assertTrue(config.matches('a').isError());
		assertTrue(config.matches('z').isError());
	}
	
	@Test
	void matchesWithBetweenOrEqual() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withBetweenOrEqual('a', 'z');
		assertTrue(config.matches('a').isSuccess());
		assertTrue(config.matches('z').isSuccess());
		assertTrue(config.matches('m').isSuccess());
		assertTrue(config.matches('A').isError());
	}
	
	@Test
	void matchesWithLetter() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLetter();
		assertTrue(config.matches('a').isSuccess());
		assertTrue(config.matches('Z').isSuccess());
		assertTrue(config.matches('1').isError());
		assertTrue(config.matches('!').isError());
	}
	
	@Test
	void matchesWithDigit() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withDigit();
		assertTrue(config.matches('0').isSuccess());
		assertTrue(config.matches('9').isSuccess());
		assertTrue(config.matches('a').isError());
	}
	
	@Test
	void matchesWithAlphanumeric() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withAlphanumeric();
		assertTrue(config.matches('a').isSuccess());
		assertTrue(config.matches('5').isSuccess());
		assertTrue(config.matches('!').isError());
	}
	
	@Test
	void matchesWithWhitespace() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withWhitespace();
		assertTrue(config.matches(' ').isSuccess());
		assertTrue(config.matches('\t').isSuccess());
		assertTrue(config.matches('a').isError());
	}
	
	@Test
	void matchesWithPunctuation() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withPunctuation();
		assertTrue(config.matches('.').isSuccess());
		assertTrue(config.matches(',').isSuccess());
		assertTrue(config.matches('a').isError());
	}
	
	@Test
	void matchesWithSymbol() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withSymbol();
		assertTrue(config.matches('+').isSuccess());
		assertTrue(config.matches('$').isSuccess());
		assertTrue(config.matches('a').isError());
	}
	
	@Test
	void matchesWithControl() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withControl();
		assertTrue(config.matches('\u0000').isSuccess());
		assertTrue(config.matches('\u001F').isSuccess());
		assertTrue(config.matches('a').isError());
	}
	
	@Test
	void matchesWithUpperCase() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withUpperCase();
		assertTrue(config.matches('A').isSuccess());
		assertTrue(config.matches('Z').isSuccess());
		assertTrue(config.matches('a').isError());
	}
	
	@Test
	void matchesWithLowerCase() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLowerCase();
		assertTrue(config.matches('a').isSuccess());
		assertTrue(config.matches('z').isSuccess());
		assertTrue(config.matches('A').isError());
	}
	
	@Test
	void matchesWithAscii() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withAscii();
		assertTrue(config.matches('a').isSuccess());
		assertTrue(config.matches('\u007F').isSuccess());
		assertTrue(config.matches('\u0080').isError());
	}
	
	@Test
	void matchesWithLatin1() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED.withLatin1();
		assertTrue(config.matches('a').isSuccess());
		assertTrue(config.matches('\u00FF').isSuccess());
		assertTrue(config.matches('\u0100').isError());
	}
	
	@Test
	void matchesWithMultipleConstraints() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED
			.withLetter()
			.withLowerCase()
			.withAscii();
		
		assertTrue(config.matches('a').isSuccess());
		assertTrue(config.matches('z').isSuccess());
		assertTrue(config.matches('A').isError());
		assertTrue(config.matches('1').isError());
	}
	
	@Test
	void matchesWithNullValue() {
		CharacterConstraintConfig config = CharacterConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
