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
 * Test class for {@link IpFormatOptionsBuilder}.<br>
 *
 * @author Luis-St
 */
class IpFormatOptionsBuilderTest {
	
	@Test
	void builderDefaultValues() {
		IpFormatOptions options = IpFormatOptions.builder().build();
		
		assertFalse(options.uppercase());
		assertFalse(options.expandZeros());
		assertFalse(options.padHextets());
		assertTrue(options.ipv4Mapped());
		assertTrue(options.includeZoneId());
		assertFalse(options.includePrefixLength());
	}
	
	@Test
	void uppercaseTrue() {
		IpFormatOptions options = IpFormatOptions.builder().uppercase(true).build();
		assertTrue(options.uppercase());
	}
	
	@Test
	void uppercaseFalse() {
		IpFormatOptions options = IpFormatOptions.builder().uppercase(false).build();
		assertFalse(options.uppercase());
	}
	
	@Test
	void expandZerosTrue() {
		IpFormatOptions options = IpFormatOptions.builder().expandZeros(true).build();
		assertTrue(options.expandZeros());
	}
	
	@Test
	void expandZerosFalse() {
		IpFormatOptions options = IpFormatOptions.builder().expandZeros(false).build();
		assertFalse(options.expandZeros());
	}
	
	@Test
	void padHextetsTrue() {
		IpFormatOptions options = IpFormatOptions.builder().padHextets(true).build();
		assertTrue(options.padHextets());
	}
	
	@Test
	void padHextetsFalse() {
		IpFormatOptions options = IpFormatOptions.builder().padHextets(false).build();
		assertFalse(options.padHextets());
	}
	
	@Test
	void ipv4MappedTrue() {
		IpFormatOptions options = IpFormatOptions.builder().ipv4Mapped(true).build();
		assertTrue(options.ipv4Mapped());
	}
	
	@Test
	void ipv4MappedFalse() {
		IpFormatOptions options = IpFormatOptions.builder().ipv4Mapped(false).build();
		assertFalse(options.ipv4Mapped());
	}
	
	@Test
	void includeZoneIdTrue() {
		IpFormatOptions options = IpFormatOptions.builder().includeZoneId(true).build();
		assertTrue(options.includeZoneId());
	}
	
	@Test
	void includeZoneIdFalse() {
		IpFormatOptions options = IpFormatOptions.builder().includeZoneId(false).build();
		assertFalse(options.includeZoneId());
	}
	
	@Test
	void includePrefixLengthTrue() {
		IpFormatOptions options = IpFormatOptions.builder().includePrefixLength(true).build();
		assertTrue(options.includePrefixLength());
	}
	
	@Test
	void includePrefixLengthFalse() {
		IpFormatOptions options = IpFormatOptions.builder().includePrefixLength(false).build();
		assertFalse(options.includePrefixLength());
	}
	
	@Test
	void builderSetsAllValues() {
		IpFormatOptions options = IpFormatOptions.builder()
			.uppercase(true)
			.expandZeros(true)
			.padHextets(true)
			.ipv4Mapped(false)
			.includeZoneId(false)
			.includePrefixLength(true)
			.build();
		
		assertTrue(options.uppercase());
		assertTrue(options.expandZeros());
		assertTrue(options.padHextets());
		assertFalse(options.ipv4Mapped());
		assertFalse(options.includeZoneId());
		assertTrue(options.includePrefixLength());
	}
	
	@Test
	void methodChainingConsistency() {
		IpFormatOptionsBuilder builder = IpFormatOptions.builder();
		assertSame(builder, builder.uppercase(true));
		assertSame(builder, builder.expandZeros(true));
		assertSame(builder, builder.padHextets(true));
		assertSame(builder, builder.ipv4Mapped(true));
		assertSame(builder, builder.includeZoneId(true));
		assertSame(builder, builder.includePrefixLength(true));
	}
	
	@Test
	void builderReuseAfterBuild() {
		IpFormatOptionsBuilder builder = IpFormatOptions.builder()
			.uppercase(true);
		
		IpFormatOptions first = builder.build();
		assertTrue(first.uppercase());
		
		builder.uppercase(false);
		IpFormatOptions second = builder.build();
		assertFalse(second.uppercase());
		
		assertTrue(first.uppercase());
	}
	
	@Test
	void builderMultipleBuilds() {
		IpFormatOptionsBuilder builder = IpFormatOptions.builder();
		
		IpFormatOptions options1 = builder.build();
		IpFormatOptions options2 = builder.build();
		
		assertEquals(options1, options2);
		assertNotSame(options1, options2);
	}
	
	@Test
	void builderOverwriteValues() {
		IpFormatOptions options = IpFormatOptions.builder()
			.uppercase(true)
			.uppercase(false)
			.build();
		
		assertFalse(options.uppercase());
	}
	
	@Test
	void buildMatchesDefaultConstant() {
		IpFormatOptions fromBuilder = IpFormatOptions.builder().build();
		IpFormatOptions defaultOptions = IpFormatOptions.DEFAULT;
		
		assertEquals(defaultOptions.uppercase(), fromBuilder.uppercase());
		assertEquals(defaultOptions.expandZeros(), fromBuilder.expandZeros());
		assertEquals(defaultOptions.padHextets(), fromBuilder.padHextets());
		assertEquals(defaultOptions.ipv4Mapped(), fromBuilder.ipv4Mapped());
		assertEquals(defaultOptions.includeZoneId(), fromBuilder.includeZoneId());
		assertEquals(defaultOptions.includePrefixLength(), fromBuilder.includePrefixLength());
	}
	
	@Test
	void buildExpandedEquivalent() {
		IpFormatOptions fromBuilder = IpFormatOptions.builder()
			.uppercase(false)
			.expandZeros(true)
			.padHextets(true)
			.ipv4Mapped(false)
			.includeZoneId(true)
			.includePrefixLength(false)
			.build();
		
		assertEquals(IpFormatOptions.EXPANDED, fromBuilder);
	}
	
	@Test
	void buildCompactEquivalent() {
		IpFormatOptions fromBuilder = IpFormatOptions.builder()
			.uppercase(false)
			.expandZeros(false)
			.padHextets(false)
			.ipv4Mapped(true)
			.includeZoneId(false)
			.includePrefixLength(false)
			.build();
		
		assertEquals(IpFormatOptions.COMPACT, fromBuilder);
	}
}
