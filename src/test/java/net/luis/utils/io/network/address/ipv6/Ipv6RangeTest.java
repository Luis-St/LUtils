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

package net.luis.utils.io.network.address.ipv6;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Ipv6Range}.<br>
 *
 * @author Luis-St
 */
class Ipv6RangeTest {
	
	@Test
	void constructWithNullStart() {
		Ipv6Address end = new Ipv6Address(0L, 100L);
		assertThrows(NullPointerException.class, () -> new Ipv6Range(null, end));
	}
	
	@Test
	void constructWithNullEnd() {
		Ipv6Address start = new Ipv6Address(0L, 1L);
		assertThrows(NullPointerException.class, () -> new Ipv6Range(start, null));
	}
	
	@Test
	void constructWithStartGreaterThanEnd() {
		Ipv6Address start = new Ipv6Address(0L, 100L);
		Ipv6Address end = new Ipv6Address(0L, 1L);
		assertThrows(IllegalArgumentException.class, () -> new Ipv6Range(start, end));
	}
	
	@Test
	void constructStripsZoneId() {
		Ipv6Address start = new Ipv6Address(0L, 1L, "eth0");
		Ipv6Address end = new Ipv6Address(0L, 100L, "eth1");
		Ipv6Range range = new Ipv6Range(start, end);
		
		assertNull(range.start().zoneId());
		assertNull(range.end().zoneId());
	}
	
	@Test
	void constructValid() {
		Ipv6Address start = new Ipv6Address(0L, 1L);
		Ipv6Address end = new Ipv6Address(0L, 100L);
		Ipv6Range range = new Ipv6Range(start, end);
		
		assertEquals(start, range.start());
		assertEquals(end, range.end());
	}
	
	@Test
	void ofWithNullStart() {
		Ipv6Address end = new Ipv6Address(0L, 100L);
		assertThrows(NullPointerException.class, () -> Ipv6Range.of(null, end));
	}
	
	@Test
	void ofWithNullEnd() {
		Ipv6Address start = new Ipv6Address(0L, 1L);
		assertThrows(NullPointerException.class, () -> Ipv6Range.of(start, null));
	}
	
	@Test
	void singleWithNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Range.single(null));
	}
	
	@Test
	void single() {
		Ipv6Address address = new Ipv6Address(0L, 1L);
		Ipv6Range range = Ipv6Range.single(address);
		
		assertEquals(address, range.start());
		assertEquals(address, range.end());
		assertTrue(range.isSingleAddress());
	}
	
	@Test
	void singleStripsZoneId() {
		Ipv6Address address = new Ipv6Address(0L, 1L, "eth0");
		Ipv6Range range = Ipv6Range.single(address);
		
		assertNull(range.start().zoneId());
	}
	
	@Test
	void fromNetworkNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Range.fromNetwork(null));
	}
	
	@Test
	void fromNetwork() {
		Ipv6Network network = Ipv6Network.parse("::/126");
		Ipv6Range range = Ipv6Range.fromNetwork(network);
		
		assertEquals(network.networkAddress(), range.start());
		assertEquals(network.lastAddress(), range.end());
	}
	
	@Test
	void size() {
		Ipv6Address start = new Ipv6Address(0L, 1L);
		Ipv6Address end = new Ipv6Address(0L, 100L);
		Ipv6Range range = Ipv6Range.of(start, end);
		
		assertEquals(BigInteger.valueOf(100), range.size());
	}
	
	@Test
	void sizeSingleAddress() {
		Ipv6Range range = Ipv6Range.single(Ipv6Address.LOOPBACK);
		assertEquals(BigInteger.ONE, range.size());
	}
	
	@Test
	void containsAddressNull() {
		Ipv6Range range = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		assertThrows(NullPointerException.class, () -> range.containsAddress(null));
	}
	
	@Test
	void containsAddress() {
		Ipv6Range range = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		
		assertTrue(range.containsAddress(new Ipv6Address(0L, 1L)));
		assertTrue(range.containsAddress(new Ipv6Address(0L, 50L)));
		assertTrue(range.containsAddress(new Ipv6Address(0L, 100L)));
		assertFalse(range.containsAddress(new Ipv6Address(0L, 0L)));
		assertFalse(range.containsAddress(new Ipv6Address(0L, 101L)));
	}
	
	@Test
	void containsAddressStripsZoneId() {
		Ipv6Range range = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Address withZone = new Ipv6Address(0L, 50L, "eth0");
		
		assertTrue(range.containsAddress(withZone));
	}
	
	@Test
	void containsRangeNull() {
		Ipv6Range range = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		assertThrows(NullPointerException.class, () -> range.containsRange(null));
	}
	
	@Test
	void containsRange() {
		Ipv6Range outer = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range inner = Ipv6Range.of(new Ipv6Address(0L, 10L), new Ipv6Address(0L, 50L));
		Ipv6Range overlapping = Ipv6Range.of(new Ipv6Address(0L, 50L), new Ipv6Address(0L, 150L));
		
		assertTrue(outer.containsRange(inner));
		assertFalse(inner.containsRange(outer));
		assertFalse(outer.containsRange(overlapping));
	}
	
	@Test
	void overlapsNull() {
		Ipv6Range range = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		assertThrows(NullPointerException.class, () -> range.overlaps(null));
	}
	
	@Test
	void overlaps() {
		Ipv6Range range1 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range range2 = Ipv6Range.of(new Ipv6Address(0L, 50L), new Ipv6Address(0L, 150L));
		Ipv6Range range3 = Ipv6Range.of(new Ipv6Address(0L, 200L), new Ipv6Address(0L, 300L));
		
		assertTrue(range1.overlaps(range2));
		assertTrue(range2.overlaps(range1));
		assertFalse(range1.overlaps(range3));
	}
	
	@Test
	void intersectionNull() {
		Ipv6Range range = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		assertThrows(NullPointerException.class, () -> range.intersection(null));
	}
	
	@Test
	void intersectionNoOverlap() {
		Ipv6Range range1 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range range2 = Ipv6Range.of(new Ipv6Address(0L, 200L), new Ipv6Address(0L, 300L));
		
		assertTrue(range1.intersection(range2).isEmpty());
	}
	
	@Test
	void intersection() {
		Ipv6Range range1 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range range2 = Ipv6Range.of(new Ipv6Address(0L, 50L), new Ipv6Address(0L, 150L));
		
		assertTrue(range1.intersection(range2).isPresent());
		Ipv6Range inter = range1.intersection(range2).get();
		assertEquals(new Ipv6Address(0L, 50L), inter.start());
		assertEquals(new Ipv6Address(0L, 100L), inter.end());
	}
	
	@Test
	void differenceNull() {
		Ipv6Range range = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		assertThrows(NullPointerException.class, () -> range.difference(null));
	}
	
	@Test
	void differenceNoOverlap() {
		Ipv6Range range1 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range range2 = Ipv6Range.of(new Ipv6Address(0L, 200L), new Ipv6Address(0L, 300L));
		
		List<Ipv6Range> diff = range1.difference(range2);
		assertEquals(1, diff.size());
		assertEquals(range1, diff.get(0));
	}
	
	@Test
	void differencePartialOverlap() {
		Ipv6Range range1 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range range2 = Ipv6Range.of(new Ipv6Address(0L, 50L), new Ipv6Address(0L, 150L));
		
		List<Ipv6Range> diff = range1.difference(range2);
		assertEquals(1, diff.size());
		assertEquals(new Ipv6Address(0L, 1L), diff.get(0).start());
		assertEquals(new Ipv6Address(0L, 49L), diff.get(0).end());
	}
	
	@Test
	void mergeNull() {
		Ipv6Range range = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		assertThrows(NullPointerException.class, () -> range.merge(null));
	}
	
	@Test
	void mergeOverlapping() {
		Ipv6Range range1 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range range2 = Ipv6Range.of(new Ipv6Address(0L, 50L), new Ipv6Address(0L, 150L));
		
		assertTrue(range1.merge(range2).isPresent());
		Ipv6Range merged = range1.merge(range2).get();
		assertEquals(new Ipv6Address(0L, 1L), merged.start());
		assertEquals(new Ipv6Address(0L, 150L), merged.end());
	}
	
	@Test
	void mergeAdjacent() {
		Ipv6Range range1 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range range2 = Ipv6Range.of(new Ipv6Address(0L, 101L), new Ipv6Address(0L, 200L));
		
		assertTrue(range1.merge(range2).isPresent());
	}
	
	@Test
	void mergeDisjoint() {
		Ipv6Range range1 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range range2 = Ipv6Range.of(new Ipv6Address(0L, 102L), new Ipv6Address(0L, 200L));
		
		assertTrue(range1.merge(range2).isEmpty());
	}
	
	@Test
	void iterator() {
		Ipv6Range range = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 3L));
		Iterator<Ipv6Address> iterator = range.iterator();
		
		assertTrue(iterator.hasNext());
		assertEquals(new Ipv6Address(0L, 1L), iterator.next());
		assertEquals(new Ipv6Address(0L, 2L), iterator.next());
		assertEquals(new Ipv6Address(0L, 3L), iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	void iteratorExhaustedThrows() {
		Ipv6Range range = Ipv6Range.single(Ipv6Address.LOOPBACK);
		Iterator<Ipv6Address> iterator = range.iterator();
		iterator.next();
		
		assertThrows(NoSuchElementException.class, iterator::next);
	}
	
	@Test
	void addressStream() {
		Ipv6Range range = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 10L));
		assertEquals(10, range.addressStream().count());
	}
	
	@Test
	void toCidrNetworksSingleNetwork() {
		Ipv6Range range = Ipv6Range.of(new Ipv6Address(0L, 0L), new Ipv6Address(0L, 3L));
		List<Ipv6Network> networks = range.toCidrNetworks();
		
		assertEquals(1, networks.size());
		assertEquals(126, networks.get(0).prefixLength());
	}
	
	@Test
	void isSingleAddress() {
		Ipv6Range single = Ipv6Range.single(Ipv6Address.LOOPBACK);
		Ipv6Range multi = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		
		assertTrue(single.isSingleAddress());
		assertFalse(multi.isSingleAddress());
	}
	
	@Test
	void isNetwork() {
		Ipv6Range networkRange = Ipv6Range.of(new Ipv6Address(0L, 0L), new Ipv6Address(0L, 3L));
		Ipv6Range nonNetworkRange = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		
		assertTrue(networkRange.isNetwork());
		assertFalse(nonNetworkRange.isNetwork());
	}
	
	@Test
	void toNetwork() {
		Ipv6Range networkRange = Ipv6Range.of(new Ipv6Address(0L, 0L), new Ipv6Address(0L, 3L));
		Ipv6Range nonNetworkRange = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		
		assertTrue(networkRange.toNetwork().isPresent());
		assertEquals(126, networkRange.toNetwork().get().prefixLength());
		assertTrue(nonNetworkRange.toNetwork().isEmpty());
	}
	
	@Test
	void compareTo() {
		Ipv6Range range1 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range range2 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 200L));
		Ipv6Range range3 = Ipv6Range.of(new Ipv6Address(0L, 2L), new Ipv6Address(0L, 100L));
		
		assertTrue(range1.compareTo(range2) < 0);
		assertTrue(range1.compareTo(range3) < 0);
	}
	
	@Test
	void toStringSingleAddress() {
		Ipv6Range range = Ipv6Range.single(Ipv6Address.LOOPBACK);
		assertFalse(range.toString().contains("-"));
	}
	
	@Test
	void toStringMultipleAddresses() {
		Ipv6Range range = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		assertTrue(range.toString().contains("-"));
	}
	
	@Test
	void recordEquals() {
		Ipv6Range range1 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range range2 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range range3 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 200L));
		
		assertEquals(range1, range2);
		assertNotEquals(range1, range3);
	}
	
	@Test
	void recordHashCode() {
		Ipv6Range range1 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		Ipv6Range range2 = Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L));
		
		assertEquals(range1.hashCode(), range2.hashCode());
	}
}
