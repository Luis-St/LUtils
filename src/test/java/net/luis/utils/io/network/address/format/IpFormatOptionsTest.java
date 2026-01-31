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
 * Test class for {@link IpFormatOptions}.<br>
 *
 * @author Luis-St
 */
class IpFormatOptionsTest {
	
	@Test
	void defaultHasCorrectValues() {
		IpFormatOptions options = IpFormatOptions.DEFAULT;
		
		assertFalse(options.uppercase());
		assertFalse(options.expandZeros());
		assertFalse(options.padHextets());
		assertTrue(options.ipv4Mapped());
		assertTrue(options.includeZoneId());
		assertFalse(options.includePrefixLength());
	}
	
	@Test
	void expandedHasCorrectValues() {
		IpFormatOptions options = IpFormatOptions.EXPANDED;
		
		assertFalse(options.uppercase());
		assertTrue(options.expandZeros());
		assertTrue(options.padHextets());
		assertFalse(options.ipv4Mapped());
		assertTrue(options.includeZoneId());
		assertFalse(options.includePrefixLength());
	}
	
	@Test
	void compactHasCorrectValues() {
		IpFormatOptions options = IpFormatOptions.COMPACT;
		
		assertFalse(options.uppercase());
		assertFalse(options.expandZeros());
		assertFalse(options.padHextets());
		assertTrue(options.ipv4Mapped());
		assertFalse(options.includeZoneId());
		assertFalse(options.includePrefixLength());
	}
	
	@Test
	void constructWithAllTrue() {
		IpFormatOptions options = new IpFormatOptions(true, true, true, true, true, true);
		
		assertTrue(options.uppercase());
		assertTrue(options.expandZeros());
		assertTrue(options.padHextets());
		assertTrue(options.ipv4Mapped());
		assertTrue(options.includeZoneId());
		assertTrue(options.includePrefixLength());
	}
	
	@Test
	void constructWithAllFalse() {
		IpFormatOptions options = new IpFormatOptions(false, false, false, false, false, false);
		
		assertFalse(options.uppercase());
		assertFalse(options.expandZeros());
		assertFalse(options.padHextets());
		assertFalse(options.ipv4Mapped());
		assertFalse(options.includeZoneId());
		assertFalse(options.includePrefixLength());
	}
	
	@Test
	void builderReturnsNonNull() {
		IpFormatOptionsBuilder builder = IpFormatOptions.builder();
		assertNotNull(builder);
	}
	
	@Test
	void builderCreatesDefaultValues() {
		IpFormatOptions options = IpFormatOptions.builder().build();
		
		assertFalse(options.uppercase());
		assertFalse(options.expandZeros());
		assertFalse(options.padHextets());
		assertTrue(options.ipv4Mapped());
		assertTrue(options.includeZoneId());
		assertFalse(options.includePrefixLength());
	}
	
	@Test
	void recordEquals() {
		IpFormatOptions options1 = new IpFormatOptions(true, false, true, false, true, false);
		IpFormatOptions options2 = new IpFormatOptions(true, false, true, false, true, false);
		IpFormatOptions options3 = new IpFormatOptions(false, false, true, false, true, false);
		
		assertEquals(options1, options2);
		assertNotEquals(options1, options3);
	}
	
	@Test
	void recordHashCode() {
		IpFormatOptions options1 = new IpFormatOptions(true, false, true, false, true, false);
		IpFormatOptions options2 = new IpFormatOptions(true, false, true, false, true, false);
		
		assertEquals(options1.hashCode(), options2.hashCode());
	}
	
	@Test
	void defaultNotEqualsExpanded() {
		assertNotEquals(IpFormatOptions.DEFAULT, IpFormatOptions.EXPANDED);
	}
	
	@Test
	void defaultNotEqualsCompact() {
		assertNotEquals(IpFormatOptions.DEFAULT, IpFormatOptions.COMPACT);
	}
	
	@Test
	void expandedNotEqualsCompact() {
		assertNotEquals(IpFormatOptions.EXPANDED, IpFormatOptions.COMPACT);
	}
	
	@Test
	void staticConstantsAreNotNull() {
		assertNotNull(IpFormatOptions.DEFAULT);
		assertNotNull(IpFormatOptions.EXPANDED);
		assertNotNull(IpFormatOptions.COMPACT);
	}
}
