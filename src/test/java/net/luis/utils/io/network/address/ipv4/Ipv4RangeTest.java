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

package net.luis.utils.io.network.address.ipv4;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Ipv4Range}.<br>
 *
 * @author Luis-St
 */
class Ipv4RangeTest {
	
	@Test
	void constructWithNullStart() {
		Ipv4Address end = Ipv4Address.fromOctets(192, 168, 1, 100);
		assertThrows(NullPointerException.class, () -> new Ipv4Range(null, end));
	}
	
	@Test
	void constructWithNullEnd() {
		Ipv4Address start = Ipv4Address.fromOctets(192, 168, 1, 1);
		assertThrows(NullPointerException.class, () -> new Ipv4Range(start, null));
	}
	
	@Test
	void constructWithStartGreaterThanEnd() {
		Ipv4Address start = Ipv4Address.fromOctets(192, 168, 1, 100);
		Ipv4Address end = Ipv4Address.fromOctets(192, 168, 1, 1);
		assertThrows(IllegalArgumentException.class, () -> new Ipv4Range(start, end));
	}
	
	@Test
	void constructValid() {
		Ipv4Address start = Ipv4Address.fromOctets(192, 168, 1, 1);
		Ipv4Address end = Ipv4Address.fromOctets(192, 168, 1, 100);
		Ipv4Range range = new Ipv4Range(start, end);
		
		assertEquals(start, range.start());
		assertEquals(end, range.end());
	}
	
	@Test
	void ofWithNullStart() {
		Ipv4Address end = Ipv4Address.fromOctets(192, 168, 1, 100);
		assertThrows(NullPointerException.class, () -> Ipv4Range.of(null, end));
	}
	
	@Test
	void ofWithNullEnd() {
		Ipv4Address start = Ipv4Address.fromOctets(192, 168, 1, 1);
		assertThrows(NullPointerException.class, () -> Ipv4Range.of(start, null));
	}
	
	@Test
	void ofValid() {
		Ipv4Address start = Ipv4Address.fromOctets(192, 168, 1, 1);
		Ipv4Address end = Ipv4Address.fromOctets(192, 168, 1, 100);
		Ipv4Range range = Ipv4Range.of(start, end);
		
		assertEquals(start, range.start());
		assertEquals(end, range.end());
	}
	
	@Test
	void parseNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Range.parse(null));
	}
	
	@Test
	void parseInvalid() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4Range.parse("invalid"));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Range.parse("192.168.1.1"));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Range.parse("192.168.1.100-192.168.1.1"));
	}
	
	@Test
	void parseValid() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), range.start());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 100), range.end());
	}
	
	@Test
	void tryParseNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Range.tryParse(null));
	}
	
	@Test
	void tryParseInvalid() {
		assertTrue(Ipv4Range.tryParse("invalid").isEmpty());
		assertTrue(Ipv4Range.tryParse("192.168.1.1").isEmpty());
		assertTrue(Ipv4Range.tryParse("192.168.1.100-192.168.1.1").isEmpty());
	}
	
	@Test
	void tryParseValid() {
		assertTrue(Ipv4Range.tryParse("192.168.1.1-192.168.1.100").isPresent());
	}
	
	@Test
	void singleWithNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Range.single(null));
	}
	
	@Test
	void single() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		Ipv4Range range = Ipv4Range.single(address);
		
		assertEquals(address, range.start());
		assertEquals(address, range.end());
		assertTrue(range.isSingleAddress());
	}
	
	@Test
	void fromNetworkNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Range.fromNetwork(null));
	}
	
	@Test
	void fromNetwork() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		Ipv4Range range = Ipv4Range.fromNetwork(network);
		
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 0), range.start());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 255), range.end());
	}
	
	@Test
	void size() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		assertEquals(BigInteger.valueOf(100), range.size());
	}
	
	@Test
	void sizeSingleAddress() {
		Ipv4Range range = Ipv4Range.single(Ipv4Address.LOOPBACK);
		assertEquals(BigInteger.ONE, range.size());
	}
	
	@Test
	void containsAddressNull() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		assertThrows(NullPointerException.class, () -> range.containsAddress(null));
	}
	
	@Test
	void containsAddress() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		
		assertTrue(range.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 1)));
		assertTrue(range.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 50)));
		assertTrue(range.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 100)));
		assertFalse(range.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 0)));
		assertFalse(range.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 101)));
	}
	
	@Test
	void containsRangeNull() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		assertThrows(NullPointerException.class, () -> range.containsRange(null));
	}
	
	@Test
	void containsRange() {
		Ipv4Range outer = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range inner = Ipv4Range.parse("192.168.1.10-192.168.1.50");
		Ipv4Range overlapping = Ipv4Range.parse("192.168.1.50-192.168.1.150");
		
		assertTrue(outer.containsRange(inner));
		assertFalse(inner.containsRange(outer));
		assertFalse(outer.containsRange(overlapping));
	}
	
	@Test
	void containsRangeSelf() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		assertTrue(range.containsRange(range));
	}
	
	@Test
	void overlapsNull() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		assertThrows(NullPointerException.class, () -> range.overlaps(null));
	}
	
	@Test
	void overlaps() {
		Ipv4Range range1 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range2 = Ipv4Range.parse("192.168.1.50-192.168.1.150");
		Ipv4Range range3 = Ipv4Range.parse("192.168.2.1-192.168.2.100");
		
		assertTrue(range1.overlaps(range2));
		assertTrue(range2.overlaps(range1));
		assertFalse(range1.overlaps(range3));
	}
	
	@Test
	void overlapsAdjacent() {
		Ipv4Range range1 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range2 = Ipv4Range.parse("192.168.1.101-192.168.1.200");
		
		assertFalse(range1.overlaps(range2));
	}
	
	@Test
	void intersectionNull() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		assertThrows(NullPointerException.class, () -> range.intersection(null));
	}
	
	@Test
	void intersectionNoOverlap() {
		Ipv4Range range1 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range2 = Ipv4Range.parse("192.168.2.1-192.168.2.100");
		
		assertTrue(range1.intersection(range2).isEmpty());
	}
	
	@Test
	void intersection() {
		Ipv4Range range1 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range2 = Ipv4Range.parse("192.168.1.50-192.168.1.150");
		
		assertTrue(range1.intersection(range2).isPresent());
		Ipv4Range inter = range1.intersection(range2).get();
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 50), inter.start());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 100), inter.end());
	}
	
	@Test
	void differenceNull() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		assertThrows(NullPointerException.class, () -> range.difference(null));
	}
	
	@Test
	void differenceNoOverlap() {
		Ipv4Range range1 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range2 = Ipv4Range.parse("192.168.2.1-192.168.2.100");
		
		List<Ipv4Range> diff = range1.difference(range2);
		assertEquals(1, diff.size());
		assertEquals(range1, diff.get(0));
	}
	
	@Test
	void differenceFullyContained() {
		Ipv4Range outer = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range inner = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		
		List<Ipv4Range> diff = inner.difference(outer);
		assertEquals(0, diff.size());
	}
	
	@Test
	void differencePartialOverlap() {
		Ipv4Range range1 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range2 = Ipv4Range.parse("192.168.1.50-192.168.1.150");
		
		List<Ipv4Range> diff = range1.difference(range2);
		assertEquals(1, diff.size());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), diff.get(0).start());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 49), diff.get(0).end());
	}
	
	@Test
	void differenceMiddle() {
		Ipv4Range outer = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range middle = Ipv4Range.parse("192.168.1.25-192.168.1.75");
		
		List<Ipv4Range> diff = outer.difference(middle);
		assertEquals(2, diff.size());
	}
	
	@Test
	void mergeNull() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		assertThrows(NullPointerException.class, () -> range.merge(null));
	}
	
	@Test
	void mergeOverlapping() {
		Ipv4Range range1 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range2 = Ipv4Range.parse("192.168.1.50-192.168.1.150");
		
		assertTrue(range1.merge(range2).isPresent());
		Ipv4Range merged = range1.merge(range2).get();
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), merged.start());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 150), merged.end());
	}
	
	@Test
	void mergeAdjacent() {
		Ipv4Range range1 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range2 = Ipv4Range.parse("192.168.1.101-192.168.1.200");
		
		assertTrue(range1.merge(range2).isPresent());
		Ipv4Range merged = range1.merge(range2).get();
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), merged.start());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 200), merged.end());
	}
	
	@Test
	void mergeDisjoint() {
		Ipv4Range range1 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range2 = Ipv4Range.parse("192.168.1.102-192.168.1.200");
		
		assertTrue(range1.merge(range2).isEmpty());
	}
	
	@Test
	void iterator() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.3");
		Iterator<Ipv4Address> iterator = range.iterator();
		
		assertTrue(iterator.hasNext());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), iterator.next());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 2), iterator.next());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 3), iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	void iteratorExhaustedThrows() {
		Ipv4Range range = Ipv4Range.single(Ipv4Address.LOOPBACK);
		Iterator<Ipv4Address> iterator = range.iterator();
		iterator.next();
		
		assertThrows(NoSuchElementException.class, iterator::next);
	}
	
	@Test
	void addressStream() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.10");
		assertEquals(10, range.addressStream().count());
	}
	
	@Test
	void toCidrNetworksSingleNetwork() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.0-192.168.1.255");
		List<Ipv4Network> networks = range.toCidrNetworks();
		
		assertEquals(1, networks.size());
		assertEquals("192.168.1.0/24", networks.get(0).toString());
	}
	
	@Test
	void toCidrNetworksMultipleNetworks() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.0-192.168.1.10");
		List<Ipv4Network> networks = range.toCidrNetworks();
		
		assertTrue(networks.size() > 1);
	}
	
	@Test
	void isSingleAddress() {
		Ipv4Range single = Ipv4Range.single(Ipv4Address.LOOPBACK);
		Ipv4Range multi = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		
		assertTrue(single.isSingleAddress());
		assertFalse(multi.isSingleAddress());
	}
	
	@Test
	void isNetwork() {
		Ipv4Range networkRange = Ipv4Range.parse("192.168.1.0-192.168.1.255");
		Ipv4Range nonNetworkRange = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		
		assertTrue(networkRange.isNetwork());
		assertFalse(nonNetworkRange.isNetwork());
	}
	
	@Test
	void toNetwork() {
		Ipv4Range networkRange = Ipv4Range.parse("192.168.1.0-192.168.1.255");
		Ipv4Range nonNetworkRange = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		
		assertTrue(networkRange.toNetwork().isPresent());
		assertEquals("192.168.1.0/24", networkRange.toNetwork().get().toString());
		assertTrue(nonNetworkRange.toNetwork().isEmpty());
	}
	
	@Test
	void compareTo() {
		Ipv4Range range1 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range2 = Ipv4Range.parse("192.168.1.1-192.168.1.200");
		Ipv4Range range3 = Ipv4Range.parse("192.168.2.1-192.168.2.100");
		
		assertTrue(range1.compareTo(range2) < 0);
		assertTrue(range1.compareTo(range3) < 0);
		assertEquals(0, range1.compareTo(Ipv4Range.parse("192.168.1.1-192.168.1.100")));
	}
	
	@Test
	void toStringFormat() {
		Ipv4Range range = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		assertEquals("192.168.1.1-192.168.1.100", range.toString());
	}
	
	@Test
	void recordEquals() {
		Ipv4Range range1 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range2 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range3 = Ipv4Range.parse("192.168.1.1-192.168.1.200");
		
		assertEquals(range1, range2);
		assertNotEquals(range1, range3);
	}
	
	@Test
	void recordHashCode() {
		Ipv4Range range1 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		Ipv4Range range2 = Ipv4Range.parse("192.168.1.1-192.168.1.100");
		
		assertEquals(range1.hashCode(), range2.hashCode());
	}
}
