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
 * Test class for {@link IpParseOptions}.<br>
 *
 * @author Luis-St
 */
class IpParseOptionsTest {
	
	@Test
	void defaultHasCorrectValues() {
		IpParseOptions options = IpParseOptions.DEFAULT;
		
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
	void strictHasCorrectValues() {
		IpParseOptions options = IpParseOptions.STRICT;
		
		assertFalse(options.allowLeadingZeros());
		assertFalse(options.allowOctalNotation());
		assertFalse(options.allowDecimalNotation());
		assertFalse(options.allowMixedCase());
		assertFalse(options.allowEmptySegments());
		assertFalse(options.allowMixedNotation());
		assertFalse(options.allowZoneId());
		assertFalse(options.allowPortSuffix());
		assertTrue(options.normalize());
	}
	
	@Test
	void lenientHasCorrectValues() {
		IpParseOptions options = IpParseOptions.LENIENT;
		
		assertTrue(options.allowLeadingZeros());
		assertFalse(options.allowOctalNotation()); // Can't have both leading zeros and octal
		assertTrue(options.allowDecimalNotation());
		assertTrue(options.allowMixedCase());
		assertTrue(options.allowEmptySegments());
		assertTrue(options.allowMixedNotation());
		assertTrue(options.allowZoneId());
		assertTrue(options.allowPortSuffix());
		assertTrue(options.normalize());
	}
	
	@Test
	void constructWithBothLeadingZerosAndOctalThrows() {
		assertThrows(IllegalArgumentException.class, () ->
			new IpParseOptions(true, true, false, false, false, false, false, false, false)
		);
	}
	
	@Test
	void constructWithLeadingZerosOnlySucceeds() {
		IpParseOptions options = new IpParseOptions(true, false, false, false, false, false, false, false, false);
		assertTrue(options.allowLeadingZeros());
		assertFalse(options.allowOctalNotation());
	}
	
	@Test
	void constructWithOctalOnlySucceeds() {
		IpParseOptions options = new IpParseOptions(false, true, false, false, false, false, false, false, false);
		assertFalse(options.allowLeadingZeros());
		assertTrue(options.allowOctalNotation());
	}
	
	@Test
	void builderCreatesDefaultValues() {
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
	void builderWithBothLeadingZerosAndOctalThrows() {
		assertThrows(IllegalArgumentException.class, () ->
			IpParseOptions.builder()
				.allowLeadingZeros(true)
				.allowOctalNotation(true)
				.build()
		);
	}
	
	@Test
	void recordEquals() {
		IpParseOptions options1 = IpParseOptions.builder().allowLeadingZeros(true).build();
		IpParseOptions options2 = IpParseOptions.builder().allowLeadingZeros(true).build();
		IpParseOptions options3 = IpParseOptions.builder().allowOctalNotation(true).build();
		
		assertEquals(options1, options2);
		assertNotEquals(options1, options3);
	}
	
	@Test
	void recordHashCode() {
		IpParseOptions options1 = IpParseOptions.builder().allowLeadingZeros(true).build();
		IpParseOptions options2 = IpParseOptions.builder().allowLeadingZeros(true).build();
		
		assertEquals(options1.hashCode(), options2.hashCode());
	}
	
	@Test
	void builderMethodChaining() {
		IpParseOptionsBuilder builder = IpParseOptions.builder();
		assertSame(builder, builder.allowLeadingZeros(true));
		assertSame(builder, builder.allowDecimalNotation(true));
		assertSame(builder, builder.allowMixedCase(false));
	}
}
