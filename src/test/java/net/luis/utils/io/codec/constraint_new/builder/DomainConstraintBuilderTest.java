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

import static org.junit.jupiter.api.Assertions.*;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.network.DomainConstraintConfig;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Test class for {@link DomainConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class DomainConstraintBuilderTest {

	@Test
	void constructEmpty() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		DomainConstraintConfig config = builder.build();

		assertNotNull(config);
		assertEquals(DomainConstraintConfig.UNCONSTRAINED, config);
	}

	@Test
	void constructWithInitialConfig() {
		DomainConstraintConfig initialConfig = DomainConstraintConfig.UNCONSTRAINED.withMinLength(5);
		DomainConstraintBuilder builder = new DomainConstraintBuilder(initialConfig);
		DomainConstraintConfig config = builder.build();

		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.minLength().isPresent());
	}

	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new DomainConstraintBuilder(null));
	}

	@Test
	void equalToReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.equalTo("example.com"));
		assertTrue(builder.build().equalTo().isPresent());
	}

	@Test
	void equalToWithNullValue() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}

	@Test
	void notEqualToReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.notEqualTo("example.com"));
		assertTrue(builder.build().equalTo().isPresent());
	}

	@Test
	void notEqualToWithNullValue() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}

	@Test
	void inReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.in(List.of("example.com", "test.org")));
		assertTrue(builder.build().in().isPresent());
	}

	@Test
	void inWithNullValues() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}

	@Test
	void notInReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.notIn(List.of("example.com", "test.org")));
		assertTrue(builder.build().in().isPresent());
	}

	@Test
	void notInWithNullValues() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}

	@Test
	void customReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		Constraint<String> constraint = value -> Result.success(null);
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}

	@Test
	void customWithNullConstraint() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}

	@Test
	void minLengthReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.minLength(5));
		assertTrue(builder.build().minLength().isPresent());
	}

	@Test
	void maxLengthReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.maxLength(253));
		assertTrue(builder.build().maxLength().isPresent());
	}

	@Test
	void exactLengthReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.exactLength(11));

		DomainConstraintConfig config = builder.build();
		assertTrue(config.minLength().isPresent());
		assertTrue(config.maxLength().isPresent());
	}

	@Test
	void lengthBetweenReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.lengthBetween(5, 253));

		DomainConstraintConfig config = builder.build();
		assertTrue(config.minLength().isPresent());
		assertTrue(config.maxLength().isPresent());
	}

	@Test
	void startsWithReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.startsWith("www."));
		assertTrue(builder.build().startsWith().isPresent());
	}

	@Test
	void startsWithWithNullPrefix() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.startsWith(null));
	}

	@Test
	void notStartsWithReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.notStartsWith("www."));
		assertTrue(builder.build().startsWith().isPresent());
	}

	@Test
	void notStartsWithWithNullPrefix() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notStartsWith(null));
	}

	@Test
	void startsWithAnyReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.startsWithAny(List.of("www.", "api.")));
		assertTrue(builder.build().startsWithAny().isPresent());
	}

	@Test
	void startsWithAnyWithNullPrefixes() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.startsWithAny(null));
	}

	@Test
	void startsWithNoneReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.startsWithNone(List.of("www.", "api.")));
		assertTrue(builder.build().startsWithAny().isPresent());
	}

	@Test
	void startsWithNoneWithNullPrefixes() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.startsWithNone(null));
	}

	@Test
	void containsReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.contains("example"));
		assertTrue(builder.build().contains().isPresent());
	}

	@Test
	void containsWithNullSubstring() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.contains(null));
	}

	@Test
	void notContainsReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.notContains("example"));
		assertTrue(builder.build().contains().isPresent());
	}

	@Test
	void notContainsWithNullSubstring() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notContains(null));
	}

	@Test
	void containsAnyReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.containsAny(List.of("example", "test")));
		assertTrue(builder.build().containsAny().isPresent());
	}

	@Test
	void containsAnyWithNullSubstrings() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.containsAny(null));
	}

	@Test
	void containsNoneReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.containsNone(List.of("example", "test")));
		assertTrue(builder.build().containsAny().isPresent());
	}

	@Test
	void containsNoneWithNullSubstrings() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.containsNone(null));
	}

	@Test
	void containsAllReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.containsAll(List.of("example", ".")));
		assertTrue(builder.build().containsAll().isPresent());
	}

	@Test
	void containsAllWithNullSubstrings() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.containsAll(null));
	}

	@Test
	void containsOnlyReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.containsOnly(List.of("a", "b", ".")));
		assertTrue(builder.build().containsOnly().isPresent());
	}

	@Test
	void containsOnlyWithNullSubstrings() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.containsOnly(null));
	}

	@Test
	void endsWithReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.endsWith(".com"));
		assertTrue(builder.build().endsWith().isPresent());
	}

	@Test
	void endsWithWithNullSuffix() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.endsWith(null));
	}

	@Test
	void notEndsWithReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.notEndsWith(".com"));
		assertTrue(builder.build().endsWith().isPresent());
	}

	@Test
	void notEndsWithWithNullSuffix() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEndsWith(null));
	}

	@Test
	void endsWithAnyReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.endsWithAny(List.of(".com", ".org")));
		assertTrue(builder.build().endsWithAny().isPresent());
	}

	@Test
	void endsWithAnyWithNullSuffixes() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.endsWithAny(null));
	}

	@Test
	void endsWithNoneReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.endsWithNone(List.of(".com", ".org")));
		assertTrue(builder.build().endsWithAny().isPresent());
	}

	@Test
	void endsWithNoneWithNullSuffixes() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.endsWithNone(null));
	}

	@Test
	void matchesStringReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.matches("^[a-z]+\\.[a-z]+$"));
		assertTrue(builder.build().matches().isPresent());
	}

	@Test
	void matchesStringWithNullRegex() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.matches((String) null));
	}

	@Test
	void notMatchesStringReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.notMatches("^[0-9]+$"));
		assertTrue(builder.build().matches().isPresent());
	}

	@Test
	void notMatchesStringWithNullRegex() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notMatches((String) null));
	}

	@Test
	void matchesPatternReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.matches(Pattern.compile("^[a-z]+\\.[a-z]+$")));
		assertTrue(builder.build().matches().isPresent());
	}

	@Test
	void matchesPatternWithNullPattern() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.matches((Pattern) null));
	}

	@Test
	void notMatchesPatternReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.notMatches(Pattern.compile("^[0-9]+$")));
		assertTrue(builder.build().matches().isPresent());
	}

	@Test
	void notMatchesPatternWithNullPattern() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notMatches((Pattern) null));
	}

	@Test
	void rootDomainReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.rootDomain());
		assertTrue(builder.build().rootDomain().isPresent());
	}

	@Test
	void subDomainReturnsBuilder() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		assertSame(builder, builder.subDomain());
		assertTrue(builder.build().subDomain().isPresent());
	}

	@Test
	void buildReturnsCorrectConfig() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();
		builder.minLength(5).maxLength(253).endsWith(".com");

		DomainConstraintConfig config = builder.build();

		assertNotNull(config);
		assertTrue(config.minLength().isPresent());
		assertTrue(config.maxLength().isPresent());
		assertTrue(config.endsWith().isPresent());
	}

	@Test
	void methodChainingWorks() {
		DomainConstraintBuilder builder = new DomainConstraintBuilder();

		DomainConstraintConfig config = builder
			.minLength(5)
			.maxLength(253)
			.endsWith(".com")
			.rootDomain()
			.build();

		assertNotNull(config);
		assertTrue(config.minLength().isPresent());
		assertTrue(config.maxLength().isPresent());
		assertTrue(config.endsWith().isPresent());
		assertTrue(config.rootDomain().isPresent());
	}
}
