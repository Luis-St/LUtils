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

package net.luis.utils.io.network.address.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpParseOptionsBuilder}.<br>
 *
 * @author Luis-St
 */
class IpParseOptionsBuilderTest {
	
	@Test
	void builderDefaultValues() {
		IpParseOptions options = IpParseOptions.builder().build();
		
		assertFalse(options.allowLeadingZeros());
		assertFalse(options.allowOctalNotation());
		assertFalse(options.allowDecimalNotation());
		assertTrue(options.allowMixedCase());
		assertTrue(options.allowEmptySegments());
		assertTrue(options.allowMixedNotation());
		assertTrue(options.allowZoneId());
		assertFalse(options.allowPortSuffix());
		assertFalse(options.normalize());
	}
	
	@Test
	void allowLeadingZerosTrue() {
		IpParseOptions options = IpParseOptions.builder().allowLeadingZeros(true).build();
		assertTrue(options.allowLeadingZeros());
	}
	
	@Test
	void allowLeadingZerosFalse() {
		IpParseOptions options = IpParseOptions.builder().allowLeadingZeros(false).build();
		assertFalse(options.allowLeadingZeros());
	}
	
	@Test
	void allowOctalNotationTrue() {
		IpParseOptions options = IpParseOptions.builder().allowOctalNotation(true).build();
		assertTrue(options.allowOctalNotation());
	}
	
	@Test
	void allowOctalNotationFalse() {
		IpParseOptions options = IpParseOptions.builder().allowOctalNotation(false).build();
		assertFalse(options.allowOctalNotation());
	}
	
	@Test
	void allowDecimalNotationTrue() {
		IpParseOptions options = IpParseOptions.builder().allowDecimalNotation(true).build();
		assertTrue(options.allowDecimalNotation());
	}
	
	@Test
	void allowDecimalNotationFalse() {
		IpParseOptions options = IpParseOptions.builder().allowDecimalNotation(false).build();
		assertFalse(options.allowDecimalNotation());
	}
	
	@Test
	void allowMixedCaseTrue() {
		IpParseOptions options = IpParseOptions.builder().allowMixedCase(true).build();
		assertTrue(options.allowMixedCase());
	}
	
	@Test
	void allowMixedCaseFalse() {
		IpParseOptions options = IpParseOptions.builder().allowMixedCase(false).build();
		assertFalse(options.allowMixedCase());
	}
	
	@Test
	void allowEmptySegmentsTrue() {
		IpParseOptions options = IpParseOptions.builder().allowEmptySegments(true).build();
		assertTrue(options.allowEmptySegments());
	}
	
	@Test
	void allowEmptySegmentsFalse() {
		IpParseOptions options = IpParseOptions.builder().allowEmptySegments(false).build();
		assertFalse(options.allowEmptySegments());
	}
	
	@Test
	void allowMixedNotationTrue() {
		IpParseOptions options = IpParseOptions.builder().allowMixedNotation(true).build();
		assertTrue(options.allowMixedNotation());
	}
	
	@Test
	void allowMixedNotationFalse() {
		IpParseOptions options = IpParseOptions.builder().allowMixedNotation(false).build();
		assertFalse(options.allowMixedNotation());
	}
	
	@Test
	void allowZoneIdTrue() {
		IpParseOptions options = IpParseOptions.builder().allowZoneId(true).build();
		assertTrue(options.allowZoneId());
	}
	
	@Test
	void allowZoneIdFalse() {
		IpParseOptions options = IpParseOptions.builder().allowZoneId(false).build();
		assertFalse(options.allowZoneId());
	}
	
	@Test
	void allowPortSuffixTrue() {
		IpParseOptions options = IpParseOptions.builder().allowPortSuffix(true).build();
		assertTrue(options.allowPortSuffix());
	}
	
	@Test
	void allowPortSuffixFalse() {
		IpParseOptions options = IpParseOptions.builder().allowPortSuffix(false).build();
		assertFalse(options.allowPortSuffix());
	}
	
	@Test
	void normalizeTrue() {
		IpParseOptions options = IpParseOptions.builder().normalize(true).build();
		assertTrue(options.normalize());
	}
	
	@Test
	void normalizeFalse() {
		IpParseOptions options = IpParseOptions.builder().normalize(false).build();
		assertFalse(options.normalize());
	}
	
	@Test
	void builderSetsAllValues() {
		IpParseOptions options = IpParseOptions.builder()
			.allowLeadingZeros(true)
			.allowOctalNotation(false)
			.allowDecimalNotation(true)
			.allowMixedCase(false)
			.allowEmptySegments(false)
			.allowMixedNotation(false)
			.allowZoneId(false)
			.allowPortSuffix(true)
			.normalize(true)
			.build();
		
		assertTrue(options.allowLeadingZeros());
		assertFalse(options.allowOctalNotation());
		assertTrue(options.allowDecimalNotation());
		assertFalse(options.allowMixedCase());
		assertFalse(options.allowEmptySegments());
		assertFalse(options.allowMixedNotation());
		assertFalse(options.allowZoneId());
		assertTrue(options.allowPortSuffix());
		assertTrue(options.normalize());
	}
	
	@Test
	void methodChainingConsistency() {
		IpParseOptionsBuilder builder = IpParseOptions.builder();
		assertSame(builder, builder.allowLeadingZeros(true));
		assertSame(builder, builder.allowOctalNotation(false));
		assertSame(builder, builder.allowDecimalNotation(true));
		assertSame(builder, builder.allowMixedCase(true));
		assertSame(builder, builder.allowEmptySegments(true));
		assertSame(builder, builder.allowMixedNotation(true));
		assertSame(builder, builder.allowZoneId(true));
		assertSame(builder, builder.allowPortSuffix(true));
		assertSame(builder, builder.normalize(true));
	}
	
	@Test
	void builderReuseAfterBuild() {
		IpParseOptionsBuilder builder = IpParseOptions.builder()
			.allowLeadingZeros(true);
		
		IpParseOptions first = builder.build();
		assertTrue(first.allowLeadingZeros());
		
		builder.allowLeadingZeros(false);
		IpParseOptions second = builder.build();
		assertFalse(second.allowLeadingZeros());
		
		// First should still have true
		assertTrue(first.allowLeadingZeros());
	}
	
	@Test
	void builderMultipleBuilds() {
		IpParseOptionsBuilder builder = IpParseOptions.builder();
		
		IpParseOptions options1 = builder.build();
		IpParseOptions options2 = builder.build();
		
		assertEquals(options1, options2);
		assertNotSame(options1, options2);
	}
	
	@Test
	void builderOverwriteValues() {
		IpParseOptions options = IpParseOptions.builder()
			.allowLeadingZeros(true)
			.allowLeadingZeros(false)
			.build();
		
		assertFalse(options.allowLeadingZeros());
	}
	
	@Test
	void buildMatchesDefaultConstant() {
		IpParseOptions fromBuilder = IpParseOptions.builder().build();
		IpParseOptions defaultOptions = IpParseOptions.DEFAULT;
		
		assertEquals(defaultOptions.allowLeadingZeros(), fromBuilder.allowLeadingZeros());
		assertEquals(defaultOptions.allowOctalNotation(), fromBuilder.allowOctalNotation());
		assertEquals(defaultOptions.allowDecimalNotation(), fromBuilder.allowDecimalNotation());
		assertEquals(defaultOptions.allowMixedCase(), fromBuilder.allowMixedCase());
		assertEquals(defaultOptions.allowEmptySegments(), fromBuilder.allowEmptySegments());
		assertEquals(defaultOptions.allowMixedNotation(), fromBuilder.allowMixedNotation());
		assertEquals(defaultOptions.allowZoneId(), fromBuilder.allowZoneId());
		assertEquals(defaultOptions.allowPortSuffix(), fromBuilder.allowPortSuffix());
		assertEquals(defaultOptions.normalize(), fromBuilder.normalize());
	}
	
	@Test
	void buildStrictEquivalent() {
		IpParseOptions fromBuilder = IpParseOptions.builder()
			.allowLeadingZeros(false)
			.allowOctalNotation(false)
			.allowDecimalNotation(false)
			.allowMixedCase(false)
			.allowEmptySegments(false)
			.allowMixedNotation(false)
			.allowZoneId(false)
			.allowPortSuffix(false)
			.normalize(true)
			.build();
		
		assertEquals(IpParseOptions.STRICT, fromBuilder);
	}
	
	@Test
	void buildLenientEquivalent() {
		IpParseOptions fromBuilder = IpParseOptions.builder()
			.allowLeadingZeros(true)
			.allowOctalNotation(false)
			.allowDecimalNotation(true)
			.allowMixedCase(true)
			.allowEmptySegments(true)
			.allowMixedNotation(true)
			.allowZoneId(true)
			.allowPortSuffix(true)
			.normalize(true)
			.build();
		
		assertEquals(IpParseOptions.LENIENT, fromBuilder);
	}
	
	@Test
	void builderWithBothLeadingZerosAndOctalThrows() {
		assertThrows(IllegalArgumentException.class, () ->
			IpParseOptions.builder()
				.allowLeadingZeros(true)
				.allowOctalNotation(true)
				.build()
		);
	}
	
	@Test
	void builderCanSetLeadingZerosAfterOctal() {
		// Setting leading zeros after octal, but then setting octal to false should work
		IpParseOptions options = IpParseOptions.builder()
			.allowOctalNotation(true)
			.allowOctalNotation(false)
			.allowLeadingZeros(true)
			.build();
		
		assertTrue(options.allowLeadingZeros());
		assertFalse(options.allowOctalNotation());
	}
	
	@Test
	void builderCanSetOctalAfterLeadingZeros() {
		// Setting octal after leading zeros, but then setting leading zeros to false should work
		IpParseOptions options = IpParseOptions.builder()
			.allowLeadingZeros(true)
			.allowLeadingZeros(false)
			.allowOctalNotation(true)
			.build();
		
		assertFalse(options.allowLeadingZeros());
		assertTrue(options.allowOctalNotation());
	}
}
