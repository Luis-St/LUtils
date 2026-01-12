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
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Ipv6Set}.<br>
 *
 * @author Luis-St
 */
class Ipv6SetTest {
	
	@Test
	void empty() {
		Ipv6Set set = Ipv6Set.empty();
		
		assertTrue(set.isEmpty());
		assertEquals(BigInteger.ZERO, set.size());
	}
	
	@Test
	void ofAddressesNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Set.of((Ipv6Address[]) null));
	}
	
	@Test
	void ofAddressesContainsNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Set.of(Ipv6Address.LOOPBACK, null));
	}
	
	@Test
	void ofAddressesEmpty() {
		Ipv6Set set = Ipv6Set.of(new Ipv6Address[0]);
		assertTrue(set.isEmpty());
	}
	
	@Test
	void ofAddresses() {
		Ipv6Set set = Ipv6Set.of(
			new Ipv6Address(0L, 1L),
			new Ipv6Address(0L, 3L),
			new Ipv6Address(0L, 2L)
		);
		
		assertFalse(set.isEmpty());
		assertEquals(BigInteger.valueOf(3), set.size());
	}
	
	@Test
	void ofAddressesStripsZoneId() {
		Ipv6Set set = Ipv6Set.of(new Ipv6Address(0L, 1L, "eth0"));
		assertTrue(set.containsAddress(new Ipv6Address(0L, 1L)));
	}
	
	@Test
	void ofNetworksNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Set.of((Ipv6Network[]) null));
	}
	
	@Test
	void ofNetworksContainsNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Set.of(Ipv6Network.parse("::/126"), null));
	}
	
	@Test
	void ofNetworksEmpty() {
		Ipv6Set set = Ipv6Set.of(new Ipv6Network[0]);
		assertTrue(set.isEmpty());
	}
	
	@Test
	void ofNetworks() {
		Ipv6Set set = Ipv6Set.of(
			Ipv6Network.parse("::/126"),
			Ipv6Network.parse("::100/126")
		);
		
		assertFalse(set.isEmpty());
	}
	
	@Test
	void ofRangesNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Set.of((Ipv6Range[]) null));
	}
	
	@Test
	void ofRangesContainsNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Set.of(Ipv6Range.single(Ipv6Address.LOOPBACK), null));
	}
	
	@Test
	void ofRangesEmpty() {
		Ipv6Set set = Ipv6Set.of(new Ipv6Range[0]);
		assertTrue(set.isEmpty());
	}
	
	@Test
	void ofRanges() {
		Ipv6Set set = Ipv6Set.of(
			Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L)),
			Ipv6Range.of(new Ipv6Address(0L, 200L), new Ipv6Address(0L, 300L))
		);
		
		assertFalse(set.isEmpty());
	}
	
	@Test
	void all() {
		Ipv6Set set = Ipv6Set.all();
		assertFalse(set.isEmpty());
	}
	
	@Test
	void isEmpty() {
		assertTrue(Ipv6Set.empty().isEmpty());
		assertFalse(Ipv6Set.of(Ipv6Address.LOOPBACK).isEmpty());
	}
	
	@Test
	void size() {
		assertEquals(BigInteger.ZERO, Ipv6Set.empty().size());
		assertEquals(BigInteger.ONE, Ipv6Set.of(Ipv6Address.LOOPBACK).size());
		assertEquals(BigInteger.valueOf(4), Ipv6Set.of(Ipv6Network.parse("::/126")).size());
	}
	
	@Test
	void containsAddressNull() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/126"));
		assertThrows(NullPointerException.class, () -> set.containsAddress(null));
	}
	
	@Test
	void containsAddress() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/126"));
		
		assertTrue(set.containsAddress(new Ipv6Address(0L, 0L)));
		assertTrue(set.containsAddress(new Ipv6Address(0L, 3L)));
		assertFalse(set.containsAddress(new Ipv6Address(0L, 4L)));
	}
	
	@Test
	void containsAddressStripsZoneId() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/126"));
		assertTrue(set.containsAddress(new Ipv6Address(0L, 1L, "eth0")));
	}
	
	@Test
	void containsNetworkNull() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/64"));
		assertThrows(NullPointerException.class, () -> set.containsNetwork(null));
	}
	
	@Test
	void containsNetwork() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/64"));
		
		assertTrue(set.containsNetwork(Ipv6Network.parse("::/126")));
		// 1::/64 has highBits != 0, so it's outside ::/64 (which requires highBits == 0)
		assertFalse(set.containsNetwork(Ipv6Network.parse("1::/64")));
	}
	
	@Test
	void containsRangeNull() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/126"));
		assertThrows(NullPointerException.class, () -> set.containsRange(null));
	}
	
	@Test
	void containsRange() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/126"));
		
		assertTrue(set.containsRange(Ipv6Range.of(new Ipv6Address(0L, 0L), new Ipv6Address(0L, 2L))));
		assertFalse(set.containsRange(Ipv6Range.of(new Ipv6Address(0L, 0L), new Ipv6Address(0L, 10L))));
	}
	
	@Test
	void containsAllNull() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/64"));
		assertThrows(NullPointerException.class, () -> set.containsAll(null));
	}
	
	@Test
	void containsAll() {
		Ipv6Set parent = Ipv6Set.of(Ipv6Network.parse("::/64"));
		Ipv6Set child = Ipv6Set.of(Ipv6Network.parse("::/126"));
		Ipv6Set other = Ipv6Set.of(Ipv6Network.parse("2001:db8::/32"));
		
		assertTrue(parent.containsAll(child));
		assertFalse(child.containsAll(parent));
		assertFalse(parent.containsAll(other));
	}
	
	@Test
	void unionNull() {
		Ipv6Set set = Ipv6Set.of(Ipv6Address.LOOPBACK);
		assertThrows(NullPointerException.class, () -> set.union(null));
	}
	
	@Test
	void unionWithEmpty() {
		Ipv6Set set = Ipv6Set.of(Ipv6Address.LOOPBACK);
		Ipv6Set result = set.union(Ipv6Set.empty());
		
		assertEquals(set.size(), result.size());
	}
	
	@Test
	void union() {
		Ipv6Set set1 = Ipv6Set.of(Ipv6Network.parse("::/126"));
		Ipv6Set set2 = Ipv6Set.of(Ipv6Network.parse("2001:db8::/126"));
		
		Ipv6Set result = set1.union(set2);
		assertTrue(result.containsNetwork(Ipv6Network.parse("::/126")));
		assertTrue(result.containsNetwork(Ipv6Network.parse("2001:db8::/126")));
	}
	
	@Test
	void intersectionNull() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/126"));
		assertThrows(NullPointerException.class, () -> set.intersection(null));
	}
	
	@Test
	void intersectionWithEmpty() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/126"));
		Ipv6Set result = set.intersection(Ipv6Set.empty());
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void intersectionNoOverlap() {
		Ipv6Set set1 = Ipv6Set.of(Ipv6Network.parse("::/126"));
		Ipv6Set set2 = Ipv6Set.of(Ipv6Network.parse("2001:db8::/126"));
		
		Ipv6Set result = set1.intersection(set2);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void intersection() {
		Ipv6Set set1 = Ipv6Set.of(Ipv6Network.parse("::/64"));
		Ipv6Set set2 = Ipv6Set.of(Ipv6Network.parse("::/126"));
		
		Ipv6Set result = set1.intersection(set2);
		assertEquals(BigInteger.valueOf(4), result.size());
	}
	
	@Test
	void differenceNull() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/126"));
		assertThrows(NullPointerException.class, () -> set.difference(null));
	}
	
	@Test
	void differenceWithEmpty() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/126"));
		Ipv6Set result = set.difference(Ipv6Set.empty());
		
		assertEquals(set.size(), result.size());
	}
	
	@Test
	void difference() {
		Ipv6Set set1 = Ipv6Set.of(Ipv6Range.of(new Ipv6Address(0L, 0L), new Ipv6Address(0L, 100L)));
		Ipv6Set set2 = Ipv6Set.of(Ipv6Range.of(new Ipv6Address(0L, 50L), new Ipv6Address(0L, 60L)));
		
		Ipv6Set result = set1.difference(set2);
		assertTrue(result.containsAddress(new Ipv6Address(0L, 0L)));
		assertFalse(result.containsAddress(new Ipv6Address(0L, 55L)));
		assertTrue(result.containsAddress(new Ipv6Address(0L, 100L)));
	}
	
	@Test
	void symmetricDifferenceNull() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/126"));
		assertThrows(NullPointerException.class, () -> set.symmetricDifference(null));
	}
	
	@Test
	void symmetricDifference() {
		Ipv6Set set1 = Ipv6Set.of(Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L)));
		Ipv6Set set2 = Ipv6Set.of(Ipv6Range.of(new Ipv6Address(0L, 50L), new Ipv6Address(0L, 150L)));
		
		Ipv6Set result = set1.symmetricDifference(set2);
		assertTrue(result.containsAddress(new Ipv6Address(0L, 1L)));
		assertTrue(result.containsAddress(new Ipv6Address(0L, 150L)));
		assertFalse(result.containsAddress(new Ipv6Address(0L, 75L)));
	}
	
	@Test
	void complementOfEmpty() {
		Ipv6Set set = Ipv6Set.empty();
		Ipv6Set result = set.complement();
		
		assertEquals(Ipv6Set.all().size(), result.size());
	}
	
	@Test
	void complement() {
		Ipv6Set set = Ipv6Set.of(Ipv6Address.LOOPBACK);
		Ipv6Set result = set.complement();
		
		assertFalse(result.containsAddress(Ipv6Address.LOOPBACK));
		assertTrue(result.containsAddress(Ipv6Address.UNSPECIFIED));
	}
	
	@Test
	void addAddressNull() {
		Ipv6Set set = Ipv6Set.empty();
		assertThrows(NullPointerException.class, () -> set.addAddress(null));
	}
	
	@Test
	void addAddress() {
		Ipv6Set set = Ipv6Set.empty();
		Ipv6Set result = set.addAddress(Ipv6Address.LOOPBACK);
		
		assertTrue(result.containsAddress(Ipv6Address.LOOPBACK));
	}
	
	@Test
	void addNetworkNull() {
		Ipv6Set set = Ipv6Set.empty();
		assertThrows(NullPointerException.class, () -> set.addNetwork(null));
	}
	
	@Test
	void addNetwork() {
		Ipv6Set set = Ipv6Set.empty();
		Ipv6Set result = set.addNetwork(Ipv6Network.parse("::/126"));
		
		assertTrue(result.containsNetwork(Ipv6Network.parse("::/126")));
	}
	
	@Test
	void addRangeNull() {
		Ipv6Set set = Ipv6Set.empty();
		assertThrows(NullPointerException.class, () -> set.addRange(null));
	}
	
	@Test
	void addRange() {
		Ipv6Set set = Ipv6Set.empty();
		Ipv6Set result = set.addRange(Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L)));
		
		assertTrue(result.containsRange(Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L))));
	}
	
	@Test
	void removeAddressNull() {
		Ipv6Set set = Ipv6Set.of(Ipv6Address.LOOPBACK);
		assertThrows(NullPointerException.class, () -> set.removeAddress(null));
	}
	
	@Test
	void removeAddress() {
		Ipv6Set set = Ipv6Set.of(Ipv6Address.LOOPBACK, Ipv6Address.UNSPECIFIED);
		Ipv6Set result = set.removeAddress(Ipv6Address.LOOPBACK);
		
		assertFalse(result.containsAddress(Ipv6Address.LOOPBACK));
		assertTrue(result.containsAddress(Ipv6Address.UNSPECIFIED));
	}
	
	@Test
	void removeNetworkNull() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/126"));
		assertThrows(NullPointerException.class, () -> set.removeNetwork(null));
	}
	
	@Test
	void removeNetwork() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/64"));
		Ipv6Set result = set.removeNetwork(Ipv6Network.parse("::/126"));
		
		assertFalse(result.containsAddress(new Ipv6Address(0L, 0L)));
		assertTrue(result.containsAddress(new Ipv6Address(0L, 100L)));
	}
	
	@Test
	void removeRangeNull() {
		Ipv6Set set = Ipv6Set.of(Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L)));
		assertThrows(NullPointerException.class, () -> set.removeRange(null));
	}
	
	@Test
	void removeRange() {
		Ipv6Set set = Ipv6Set.of(Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L)));
		Ipv6Set result = set.removeRange(Ipv6Range.of(new Ipv6Address(0L, 20L), new Ipv6Address(0L, 30L)));
		
		assertFalse(result.containsAddress(new Ipv6Address(0L, 25L)));
		assertTrue(result.containsAddress(new Ipv6Address(0L, 10L)));
		assertTrue(result.containsAddress(new Ipv6Address(0L, 50L)));
	}
	
	@Test
	void toRanges() {
		Ipv6Set set = Ipv6Set.of(
			Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L)),
			Ipv6Range.of(new Ipv6Address(0L, 200L), new Ipv6Address(0L, 300L))
		);
		
		List<Ipv6Range> ranges = set.toRanges();
		assertEquals(2, ranges.size());
	}
	
	@Test
	void toNetworks() {
		Ipv6Set set = Ipv6Set.of(Ipv6Network.parse("::/126"));
		List<Ipv6Network> networks = set.toNetworks();
		
		assertFalse(networks.isEmpty());
	}
	
	@Test
	void toExactNetworks() {
		Ipv6Set set = Ipv6Set.of(Ipv6Range.of(new Ipv6Address(0L, 0L), new Ipv6Address(0L, 3L)));
		List<Ipv6Network> networks = set.toExactNetworks();
		
		assertEquals(1, networks.size());
		assertEquals(126, networks.get(0).prefixLength());
	}
	
	@Test
	void rangeIterator() {
		Ipv6Set set = Ipv6Set.of(
			Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L)),
			Ipv6Range.of(new Ipv6Address(0L, 200L), new Ipv6Address(0L, 300L))
		);
		
		Iterator<Ipv6Range> iterator = set.rangeIterator();
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		assertEquals(2, count);
	}
	
	@Test
	void rangeStream() {
		Ipv6Set set = Ipv6Set.of(
			Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 100L)),
			Ipv6Range.of(new Ipv6Address(0L, 200L), new Ipv6Address(0L, 300L))
		);
		
		assertEquals(2, set.rangeStream().count());
	}
	
	@Test
	void equals() {
		Ipv6Set set1 = Ipv6Set.of(Ipv6Network.parse("::/126"));
		Ipv6Set set2 = Ipv6Set.of(Ipv6Network.parse("::/126"));
		Ipv6Set set3 = Ipv6Set.of(Ipv6Network.parse("2001:db8::/126"));
		
		assertEquals(set1, set2);
		assertNotEquals(set1, set3);
	}
	
	@Test
	void hashCodeConsistent() {
		Ipv6Set set1 = Ipv6Set.of(Ipv6Network.parse("::/126"));
		Ipv6Set set2 = Ipv6Set.of(Ipv6Network.parse("::/126"));
		
		assertEquals(set1.hashCode(), set2.hashCode());
	}
	
	@Test
	void toStringEmpty() {
		assertEquals("Ipv6Set[]", Ipv6Set.empty().toString());
	}
	
	@Test
	void toStringNonEmpty() {
		Ipv6Set set = Ipv6Set.of(Ipv6Address.LOOPBACK);
		String str = set.toString();
		
		assertTrue(str.startsWith("Ipv6Set["));
		assertTrue(str.endsWith("]"));
	}
	
	@Test
	void normalizeMergesAdjacent() {
		Ipv6Set set = Ipv6Set.of(
			Ipv6Range.of(new Ipv6Address(0L, 1L), new Ipv6Address(0L, 50L)),
			Ipv6Range.of(new Ipv6Address(0L, 51L), new Ipv6Address(0L, 100L))
		);
		
		List<Ipv6Range> ranges = set.toRanges();
		assertEquals(1, ranges.size());
	}
}
