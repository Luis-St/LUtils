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

package net.luis.utils.io.codec.constraint_new.builder;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.StringConstraintConfig;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class StringConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		StringConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(StringConstraintConfig.UNCONSTRAINED, config);
	}
	
	@Test
	void constructWithInitialConfig() {
		StringConstraintConfig initialConfig = StringConstraintConfig.UNCONSTRAINED.withNotBlank();
		StringConstraintBuilder builder = new StringConstraintBuilder(initialConfig);
		StringConstraintConfig config = builder.build();

		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.notBlank().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new StringConstraintBuilder(null));
	}
	
	@Test
	void equalToReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.equalTo("test"));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.notEqualTo("test"));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.in(List.of("a", "b", "c")));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.notIn(List.of("x", "y", "z")));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void equalToIgnoreCaseReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.equalToIgnoreCase("TEST"));
		assertTrue(builder.build().equalToIgnoreCase().isPresent());
	}
	
	@Test
	void equalToIgnoreCaseWithNullValue() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalToIgnoreCase(null));
	}
	
	@Test
	void notEqualToIgnoreCaseReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.notEqualToIgnoreCase("TEST"));
		assertTrue(builder.build().equalToIgnoreCase().isPresent());
	}
	
	@Test
	void notEqualToIgnoreCaseWithNullValue() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualToIgnoreCase(null));
	}
	
	@Test
	void inIgnoreCaseReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.inIgnoreCase(List.of("A", "B", "C")));
		assertTrue(builder.build().inIgnoreCase().isPresent());
	}
	
	@Test
	void inIgnoreCaseWithNullValues() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.inIgnoreCase(null));
	}
	
	@Test
	void notInIgnoreCaseReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.notInIgnoreCase(List.of("X", "Y", "Z")));
		assertTrue(builder.build().inIgnoreCase().isPresent());
	}
	
	@Test
	void notInIgnoreCaseWithNullValues() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notInIgnoreCase(null));
	}
	
	@Test
	void customReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		Constraint<String> constraint = value -> Result.success(null);
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraint() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}
	
	@Test
	void lowerCaseReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.lowerCase());
		assertTrue(builder.build().lowerCase().isPresent());
	}
	
	@Test
	void upperCaseReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.upperCase());
		assertTrue(builder.build().upperCase().isPresent());
	}

	@Test
	void lengthReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.length(l -> l.minLength(3).maxLength(10)));

		StringConstraintConfig config = builder.build();
		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertEquals(3, config.length().get().min().get().getFirst());
		assertEquals(10, config.length().get().max().get().getFirst());
	}

	@Test
	void lengthWithNullBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.length(null));
	}
	
	@Test
	void startsWithReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.startsWith("prefix"));
		assertTrue(builder.build().startsWith().isPresent());
	}
	
	@Test
	void startsWithWithNullPrefix() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.startsWith(null));
	}
	
	@Test
	void notStartsWithReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.notStartsWith("prefix"));
		assertTrue(builder.build().startsWith().isPresent());
	}
	
	@Test
	void notStartsWithWithNullPrefix() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notStartsWith(null));
	}
	
	@Test
	void startsWithAnyReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.startsWithAny(List.of("pre", "start")));
		assertTrue(builder.build().startsWithAny().isPresent());
	}
	
	@Test
	void startsWithAnyWithNullPrefixes() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.startsWithAny(null));
	}
	
	@Test
	void startsWithNoneReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.startsWithNone(List.of("pre", "start")));
		assertTrue(builder.build().startsWithAny().isPresent());
	}
	
	@Test
	void startsWithNoneWithNullPrefixes() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.startsWithNone(null));
	}
	
	@Test
	void containsReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.contains("sub"));
		assertTrue(builder.build().contains().isPresent());
	}
	
	@Test
	void containsWithNullSubstring() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.contains(null));
	}
	
	@Test
	void notContainsReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.notContains("forbidden"));
		assertTrue(builder.build().contains().isPresent());
	}
	
	@Test
	void notContainsWithNullSubstring() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notContains(null));
	}
	
	@Test
	void containsAnyReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.containsAny(List.of("one", "two")));
		assertTrue(builder.build().containsAny().isPresent());
	}
	
	@Test
	void containsAnyWithNullSubstrings() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.containsAny(null));
	}
	
	@Test
	void containsNoneReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.containsNone(List.of("bad", "words")));
		assertTrue(builder.build().containsAny().isPresent());
	}
	
	@Test
	void containsNoneWithNullSubstrings() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.containsNone(null));
	}
	
	@Test
	void containsAllReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.containsAll(List.of("must", "have")));
		assertTrue(builder.build().containsAll().isPresent());
	}
	
	@Test
	void containsAllWithNullSubstrings() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.containsAll(null));
	}
	
	@Test
	void containsOnlyReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.containsOnly(List.of("a", "b", "c")));
		assertTrue(builder.build().containsOnly().isPresent());
	}
	
	@Test
	void containsOnlyWithNullSubstrings() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.containsOnly(null));
	}
	
	@Test
	void endsWithReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.endsWith("suffix"));
		assertTrue(builder.build().endsWith().isPresent());
	}
	
	@Test
	void endsWithWithNullSuffix() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.endsWith(null));
	}
	
	@Test
	void notEndsWithReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.notEndsWith("suffix"));
		assertTrue(builder.build().endsWith().isPresent());
	}
	
	@Test
	void notEndsWithWithNullSuffix() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEndsWith(null));
	}
	
	@Test
	void endsWithAnyReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.endsWithAny(List.of(".txt", ".xml")));
		assertTrue(builder.build().endsWithAny().isPresent());
	}
	
	@Test
	void endsWithAnyWithNullSuffixes() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.endsWithAny(null));
	}
	
	@Test
	void endsWithNoneReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.endsWithNone(List.of(".exe", ".bat")));
		assertTrue(builder.build().endsWithAny().isPresent());
	}
	
	@Test
	void endsWithNoneWithNullSuffixes() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.endsWithNone(null));
	}
	
	@Test
	void matchesStringReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.matches("[a-z]+"));
		assertTrue(builder.build().matches().isPresent());
	}
	
	@Test
	void matchesStringWithNullRegex() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.matches((String) null));
	}
	
	@Test
	void notMatchesStringReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.notMatches("[0-9]+"));
		assertTrue(builder.build().matches().isPresent());
	}
	
	@Test
	void notMatchesStringWithNullRegex() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notMatches((String) null));
	}
	
	@Test
	void matchesPatternReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.matches(Pattern.compile("[a-z]+")));
		assertTrue(builder.build().matches().isPresent());
	}
	
	@Test
	void matchesPatternWithNullPattern() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.matches((Pattern) null));
	}
	
	@Test
	void notMatchesPatternReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.notMatches(Pattern.compile("[0-9]+")));
		assertTrue(builder.build().matches().isPresent());
	}
	
	@Test
	void notMatchesPatternWithNullPattern() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notMatches((Pattern) null));
	}
	
	@Test
	void trimmedReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.trimmed());
		assertTrue(builder.build().trimmed().isPresent());
	}
	
	@Test
	void blankReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.blank());
		assertTrue(builder.build().blank().isPresent());
	}
	
	@Test
	void notBlankReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.notBlank());
		assertTrue(builder.build().notBlank().isPresent());
	}
	
	@Test
	void numericReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.numeric());
		assertTrue(builder.build().numeric().isPresent());
	}
	
	@Test
	void alphabeticReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.alphabetic());
		assertTrue(builder.build().alphabetic().isPresent());
	}
	
	@Test
	void alphanumericReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.alphanumeric());
		assertTrue(builder.build().alphanumeric().isPresent());
	}
	
	@Test
	void asciiReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.ascii());
		assertTrue(builder.build().ascii().isPresent());
	}
	
	@Test
	void latin1ReturnsBuilder() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		assertSame(builder, builder.latin1());
		assertTrue(builder.build().latin1().isPresent());
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		StringConstraintBuilder builder = new StringConstraintBuilder();
		builder.length(b -> b.minLength(5).maxLength(100)).startsWith("test");

		StringConstraintConfig config = builder.build();

		assertNotNull(config);
		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertTrue(config.startsWith().isPresent());
	}

	@Test
	void methodChainingWorks() {
		StringConstraintBuilder builder = new StringConstraintBuilder();

		StringConstraintConfig config = builder
			.length(b -> b.minLength(5).maxLength(100))
			.notBlank()
			.startsWith("test")
			.endsWith(".txt")
			.build();

		assertNotNull(config);
		assertTrue(config.length().isPresent());
		assertTrue(config.length().get().min().isPresent());
		assertTrue(config.length().get().max().isPresent());
		assertTrue(config.notBlank().isPresent());
		assertTrue(config.startsWith().isPresent());
		assertTrue(config.endsWith().isPresent());
	}
}
