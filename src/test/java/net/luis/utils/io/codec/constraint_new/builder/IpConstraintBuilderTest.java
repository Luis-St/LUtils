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
import net.luis.utils.io.codec.constraint_new.config.network.IpConstraintConfig;
import net.luis.utils.io.codec.constraint_new.core.IpAddressType;
import net.luis.utils.io.codec.constraint_new.core.IpVersion;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Test class for {@link IpConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class IpConstraintBuilderTest {

	@Test
	void constructEmpty() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		IpConstraintConfig config = builder.build();

		assertNotNull(config);
		assertEquals(IpConstraintConfig.UNCONSTRAINED, config);
	}

	@Test
	void constructWithInitialConfig() {
		IpConstraintConfig initialConfig = IpConstraintConfig.UNCONSTRAINED.withMinLength(7);
		IpConstraintBuilder builder = new IpConstraintBuilder(initialConfig);
		IpConstraintConfig config = builder.build();

		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.minLength().isPresent());
	}

	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new IpConstraintBuilder(null));
	}

	@Test
	void equalToReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.equalTo("192.168.1.1"));
		assertTrue(builder.build().equalTo().isPresent());
	}

	@Test
	void equalToWithNullValue() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}

	@Test
	void notEqualToReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.notEqualTo("192.168.1.1"));
		assertTrue(builder.build().equalTo().isPresent());
	}

	@Test
	void notEqualToWithNullValue() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}

	@Test
	void inReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.in(List.of("192.168.1.1", "10.0.0.1")));
		assertTrue(builder.build().in().isPresent());
	}

	@Test
	void inWithNullValues() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}

	@Test
	void notInReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.notIn(List.of("192.168.1.1", "10.0.0.1")));
		assertTrue(builder.build().in().isPresent());
	}

	@Test
	void notInWithNullValues() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}

	@Test
	void customReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		Constraint<String> constraint = value -> Result.success(null);
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}

	@Test
	void customWithNullConstraint() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}

	@Test
	void minLengthReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.minLength(7));
		assertTrue(builder.build().minLength().isPresent());
	}

	@Test
	void maxLengthReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.maxLength(15));
		assertTrue(builder.build().maxLength().isPresent());
	}

	@Test
	void exactLengthReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.exactLength(15));

		IpConstraintConfig config = builder.build();
		assertTrue(config.minLength().isPresent());
		assertTrue(config.maxLength().isPresent());
	}

	@Test
	void lengthBetweenReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.lengthBetween(7, 15));

		IpConstraintConfig config = builder.build();
		assertTrue(config.minLength().isPresent());
		assertTrue(config.maxLength().isPresent());
	}

	@Test
	void startsWithReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.startsWith("192."));
		assertTrue(builder.build().startsWith().isPresent());
	}

	@Test
	void startsWithWithNullPrefix() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.startsWith(null));
	}

	@Test
	void notStartsWithReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.notStartsWith("192."));
		assertTrue(builder.build().startsWith().isPresent());
	}

	@Test
	void notStartsWithWithNullPrefix() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notStartsWith(null));
	}

	@Test
	void startsWithAnyReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.startsWithAny(List.of("192.", "10.")));
		assertTrue(builder.build().startsWithAny().isPresent());
	}

	@Test
	void startsWithAnyWithNullPrefixes() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.startsWithAny(null));
	}

	@Test
	void startsWithNoneReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.startsWithNone(List.of("192.", "10.")));
		assertTrue(builder.build().startsWithAny().isPresent());
	}

	@Test
	void startsWithNoneWithNullPrefixes() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.startsWithNone(null));
	}

	@Test
	void containsReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.contains("168"));
		assertTrue(builder.build().contains().isPresent());
	}

	@Test
	void containsWithNullSubstring() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.contains(null));
	}

	@Test
	void notContainsReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.notContains("168"));
		assertTrue(builder.build().contains().isPresent());
	}

	@Test
	void notContainsWithNullSubstring() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notContains(null));
	}

	@Test
	void containsAnyReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.containsAny(List.of("168", "10")));
		assertTrue(builder.build().containsAny().isPresent());
	}

	@Test
	void containsAnyWithNullSubstrings() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.containsAny(null));
	}

	@Test
	void containsNoneReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.containsNone(List.of("168", "10")));
		assertTrue(builder.build().containsAny().isPresent());
	}

	@Test
	void containsNoneWithNullSubstrings() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.containsNone(null));
	}

	@Test
	void containsAllReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.containsAll(List.of("192", "168")));
		assertTrue(builder.build().containsAll().isPresent());
	}

	@Test
	void containsAllWithNullSubstrings() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.containsAll(null));
	}

	@Test
	void containsOnlyReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.containsOnly(List.of("0", "1", "2", ".")));
		assertTrue(builder.build().containsOnly().isPresent());
	}

	@Test
	void containsOnlyWithNullSubstrings() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.containsOnly(null));
	}

	@Test
	void endsWithReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.endsWith(".1"));
		assertTrue(builder.build().endsWith().isPresent());
	}

	@Test
	void endsWithWithNullSuffix() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.endsWith(null));
	}

	@Test
	void notEndsWithReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.notEndsWith(".1"));
		assertTrue(builder.build().endsWith().isPresent());
	}

	@Test
	void notEndsWithWithNullSuffix() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEndsWith(null));
	}

	@Test
	void endsWithAnyReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.endsWithAny(List.of(".1", ".255")));
		assertTrue(builder.build().endsWithAny().isPresent());
	}

	@Test
	void endsWithAnyWithNullSuffixes() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.endsWithAny(null));
	}

	@Test
	void endsWithNoneReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.endsWithNone(List.of(".1", ".255")));
		assertTrue(builder.build().endsWithAny().isPresent());
	}

	@Test
	void endsWithNoneWithNullSuffixes() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.endsWithNone(null));
	}

	@Test
	void matchesStringReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.matches("^192\\..*"));
		assertTrue(builder.build().matches().isPresent());
	}

	@Test
	void matchesStringWithNullRegex() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.matches((String) null));
	}

	@Test
	void notMatchesStringReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.notMatches("^10\\..*"));
		assertTrue(builder.build().matches().isPresent());
	}

	@Test
	void notMatchesStringWithNullRegex() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notMatches((String) null));
	}

	@Test
	void matchesPatternReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.matches(Pattern.compile("^192\\..*")));
		assertTrue(builder.build().matches().isPresent());
	}

	@Test
	void matchesPatternWithNullPattern() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.matches((Pattern) null));
	}

	@Test
	void notMatchesPatternReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.notMatches(Pattern.compile("^10\\..*")));
		assertTrue(builder.build().matches().isPresent());
	}

	@Test
	void notMatchesPatternWithNullPattern() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notMatches((Pattern) null));
	}

	@Test
	void ipVersionReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.ipVersion(b -> b.equalTo(IpVersion.IPV4)));
		assertTrue(builder.build().ipVersion().isPresent());
	}

	@Test
	void ipVersionWithNullBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.ipVersion(null));
	}

	@Test
	void ipTypeReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.ipType(b -> b.equalTo(IpAddressType.PRIVATE)));
		assertTrue(builder.build().ipType().isPresent());
	}

	@Test
	void ipTypeWithNullBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.ipType(null));
	}

	@Test
	void inAnySubnetReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.inAnySubnet(List.of("192.168.0.0/16", "10.0.0.0/8")));
		assertTrue(builder.build().inAnySubnet().isPresent());
	}

	@Test
	void inAnySubnetWithNullCidrs() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.inAnySubnet(null));
	}

	@Test
	void inNoSubnetReturnsBuilder() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertSame(builder, builder.inNoSubnet(List.of("192.168.0.0/16", "10.0.0.0/8")));
		assertTrue(builder.build().inAnySubnet().isPresent());
	}

	@Test
	void inNoSubnetWithNullCidrs() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.inNoSubnet(null));
	}

	@Test
	void buildReturnsCorrectConfig() {
		IpConstraintBuilder builder = new IpConstraintBuilder();
		builder.minLength(7).maxLength(15).startsWith("192.");

		IpConstraintConfig config = builder.build();

		assertNotNull(config);
		assertTrue(config.minLength().isPresent());
		assertTrue(config.maxLength().isPresent());
		assertTrue(config.startsWith().isPresent());
	}

	@Test
	void methodChainingWorks() {
		IpConstraintBuilder builder = new IpConstraintBuilder();

		IpConstraintConfig config = builder
			.minLength(7)
			.maxLength(15)
			.startsWith("192.")
			.ipVersion(b -> b.equalTo(IpVersion.IPV4))
			.build();

		assertNotNull(config);
		assertTrue(config.minLength().isPresent());
		assertTrue(config.maxLength().isPresent());
		assertTrue(config.startsWith().isPresent());
		assertTrue(config.ipVersion().isPresent());
	}
}
