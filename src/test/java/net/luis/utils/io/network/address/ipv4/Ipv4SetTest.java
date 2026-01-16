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
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Ipv4Set}.<br>
 *
 * @author Luis-St
 */
class Ipv4SetTest {
	
	@Test
	void empty() {
		Ipv4Set set = Ipv4Set.empty();
		
		assertTrue(set.isEmpty());
		assertEquals(BigInteger.ZERO, set.size());
	}
	
	@Test
	void ofAddressesNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Set.of((Ipv4Address[]) null));
	}
	
	@Test
	void ofAddressesContainsNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Set.of(Ipv4Address.LOOPBACK, null));
	}
	
	@Test
	void ofAddressesEmpty() {
		Ipv4Set set = Ipv4Set.of(new Ipv4Address[0]);
		assertTrue(set.isEmpty());
	}
	
	@Test
	void ofAddresses() {
		Ipv4Set set = Ipv4Set.of(
			Ipv4Address.fromOctets(192, 168, 1, 1),
			Ipv4Address.fromOctets(192, 168, 1, 3),
			Ipv4Address.fromOctets(192, 168, 1, 2)
		);
		
		assertFalse(set.isEmpty());
		assertEquals(BigInteger.valueOf(3), set.size());
	}
	
	@Test
	void ofNetworksNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Set.of((Ipv4Network[]) null));
	}
	
	@Test
	void ofNetworksContainsNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"), null));
	}
	
	@Test
	void ofNetworksEmpty() {
		Ipv4Set set = Ipv4Set.of(new Ipv4Network[0]);
		assertTrue(set.isEmpty());
	}
	
	@Test
	void ofNetworks() {
		Ipv4Set set = Ipv4Set.of(
			Ipv4Network.parse("192.168.1.0/24"),
			Ipv4Network.parse("10.0.0.0/8")
		);
		
		assertFalse(set.isEmpty());
	}
	
	@Test
	void ofRangesNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Set.of((Ipv4Range[]) null));
	}
	
	@Test
	void ofRangesContainsNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Set.of(Ipv4Range.parse("192.168.1.1-192.168.1.100"), null));
	}
	
	@Test
	void ofRangesEmpty() {
		Ipv4Set set = Ipv4Set.of(new Ipv4Range[0]);
		assertTrue(set.isEmpty());
	}
	
	@Test
	void ofRanges() {
		Ipv4Set set = Ipv4Set.of(
			Ipv4Range.parse("192.168.1.1-192.168.1.100"),
			Ipv4Range.parse("10.0.0.1-10.0.0.50")
		);
		
		assertFalse(set.isEmpty());
	}
	
	@Test
	void all() {
		Ipv4Set set = Ipv4Set.all();
		
		assertFalse(set.isEmpty());
		assertEquals(BigInteger.valueOf(4294967296L), set.size());
	}
	
	@Test
	void isEmpty() {
		assertTrue(Ipv4Set.empty().isEmpty());
		assertFalse(Ipv4Set.of(Ipv4Address.LOOPBACK).isEmpty());
	}
	
	@Test
	void size() {
		assertEquals(BigInteger.ZERO, Ipv4Set.empty().size());
		assertEquals(BigInteger.ONE, Ipv4Set.of(Ipv4Address.LOOPBACK).size());
		assertEquals(BigInteger.valueOf(256), Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24")).size());
	}
	
	@Test
	void containsAddressNull() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		assertThrows(NullPointerException.class, () -> set.containsAddress(null));
	}
	
	@Test
	void containsAddress() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		
		assertTrue(set.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 1)));
		assertTrue(set.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 255)));
		assertFalse(set.containsAddress(Ipv4Address.fromOctets(192, 168, 2, 1)));
	}
	
	@Test
	void containsNetworkNull() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.0.0/16"));
		assertThrows(NullPointerException.class, () -> set.containsNetwork(null));
	}
	
	@Test
	void containsNetwork() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.0.0/16"));
		
		assertTrue(set.containsNetwork(Ipv4Network.parse("192.168.1.0/24")));
		assertFalse(set.containsNetwork(Ipv4Network.parse("10.0.0.0/8")));
	}
	
	@Test
	void containsRangeNull() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		assertThrows(NullPointerException.class, () -> set.containsRange(null));
	}
	
	@Test
	void containsRange() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		
		assertTrue(set.containsRange(Ipv4Range.parse("192.168.1.10-192.168.1.20")));
		assertFalse(set.containsRange(Ipv4Range.parse("192.168.1.200-192.168.2.50")));
	}
	
	@Test
	void containsAllNull() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.0.0/16"));
		assertThrows(NullPointerException.class, () -> set.containsAll(null));
	}
	
	@Test
	void containsAll() {
		Ipv4Set parent = Ipv4Set.of(Ipv4Network.parse("192.168.0.0/16"));
		Ipv4Set child = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		Ipv4Set other = Ipv4Set.of(Ipv4Network.parse("10.0.0.0/8"));
		
		assertTrue(parent.containsAll(child));
		assertFalse(child.containsAll(parent));
		assertFalse(parent.containsAll(other));
	}
	
	@Test
	void unionNull() {
		Ipv4Set set = Ipv4Set.of(Ipv4Address.LOOPBACK);
		assertThrows(NullPointerException.class, () -> set.union(null));
	}
	
	@Test
	void unionWithEmpty() {
		Ipv4Set set = Ipv4Set.of(Ipv4Address.LOOPBACK);
		Ipv4Set result = set.union(Ipv4Set.empty());
		
		assertEquals(set.size(), result.size());
	}
	
	@Test
	void union() {
		Ipv4Set set1 = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		Ipv4Set set2 = Ipv4Set.of(Ipv4Network.parse("10.0.0.0/24"));
		
		Ipv4Set result = set1.union(set2);
		assertTrue(result.containsNetwork(Ipv4Network.parse("192.168.1.0/24")));
		assertTrue(result.containsNetwork(Ipv4Network.parse("10.0.0.0/24")));
	}
	
	@Test
	void intersectionNull() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		assertThrows(NullPointerException.class, () -> set.intersection(null));
	}
	
	@Test
	void intersectionWithEmpty() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		Ipv4Set result = set.intersection(Ipv4Set.empty());
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void intersectionNoOverlap() {
		Ipv4Set set1 = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		Ipv4Set set2 = Ipv4Set.of(Ipv4Network.parse("10.0.0.0/8"));
		
		Ipv4Set result = set1.intersection(set2);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void intersection() {
		Ipv4Set set1 = Ipv4Set.of(Ipv4Network.parse("192.168.0.0/16"));
		Ipv4Set set2 = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		
		Ipv4Set result = set1.intersection(set2);
		assertEquals(BigInteger.valueOf(256), result.size());
	}
	
	@Test
	void differenceNull() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		assertThrows(NullPointerException.class, () -> set.difference(null));
	}
	
	@Test
	void differenceWithEmpty() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		Ipv4Set result = set.difference(Ipv4Set.empty());
		
		assertEquals(set.size(), result.size());
	}
	
	@Test
	void difference() {
		Ipv4Set set1 = Ipv4Set.of(Ipv4Network.parse("192.168.0.0/16"));
		Ipv4Set set2 = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		
		Ipv4Set result = set1.difference(set2);
		assertTrue(result.containsAddress(Ipv4Address.fromOctets(192, 168, 0, 1)));
		assertFalse(result.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 1)));
	}
	
	@Test
	void symmetricDifferenceNull() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		assertThrows(NullPointerException.class, () -> set.symmetricDifference(null));
	}
	
	@Test
	void symmetricDifference() {
		Ipv4Set set1 = Ipv4Set.of(
			Ipv4Range.parse("192.168.1.1-192.168.1.100")
		);
		Ipv4Set set2 = Ipv4Set.of(
			Ipv4Range.parse("192.168.1.50-192.168.1.150")
		);
		
		Ipv4Set result = set1.symmetricDifference(set2);
		assertTrue(result.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 1)));
		assertTrue(result.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 150)));
		assertFalse(result.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 75)));
	}
	
	@Test
	void complementOfEmpty() {
		Ipv4Set set = Ipv4Set.empty();
		Ipv4Set result = set.complement();
		
		assertEquals(Ipv4Set.all().size(), result.size());
	}
	
	@Test
	void complement() {
		Ipv4Set set = Ipv4Set.of(Ipv4Address.LOOPBACK);
		Ipv4Set result = set.complement();
		
		assertFalse(result.containsAddress(Ipv4Address.LOOPBACK));
		assertTrue(result.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 1)));
	}
	
	@Test
	void addAddressNull() {
		Ipv4Set set = Ipv4Set.empty();
		assertThrows(NullPointerException.class, () -> set.addAddress(null));
	}
	
	@Test
	void addAddress() {
		Ipv4Set set = Ipv4Set.empty();
		Ipv4Set result = set.addAddress(Ipv4Address.LOOPBACK);
		
		assertTrue(result.containsAddress(Ipv4Address.LOOPBACK));
	}
	
	@Test
	void addNetworkNull() {
		Ipv4Set set = Ipv4Set.empty();
		assertThrows(NullPointerException.class, () -> set.addNetwork(null));
	}
	
	@Test
	void addNetwork() {
		Ipv4Set set = Ipv4Set.empty();
		Ipv4Set result = set.addNetwork(Ipv4Network.parse("192.168.1.0/24"));
		
		assertTrue(result.containsNetwork(Ipv4Network.parse("192.168.1.0/24")));
	}
	
	@Test
	void addRangeNull() {
		Ipv4Set set = Ipv4Set.empty();
		assertThrows(NullPointerException.class, () -> set.addRange(null));
	}
	
	@Test
	void addRange() {
		Ipv4Set set = Ipv4Set.empty();
		Ipv4Set result = set.addRange(Ipv4Range.parse("192.168.1.1-192.168.1.100"));
		
		assertTrue(result.containsRange(Ipv4Range.parse("192.168.1.1-192.168.1.100")));
	}
	
	@Test
	void removeAddressNull() {
		Ipv4Set set = Ipv4Set.of(Ipv4Address.LOOPBACK);
		assertThrows(NullPointerException.class, () -> set.removeAddress(null));
	}
	
	@Test
	void removeAddress() {
		Ipv4Set set = Ipv4Set.of(Ipv4Address.LOOPBACK, Ipv4Address.BROADCAST);
		Ipv4Set result = set.removeAddress(Ipv4Address.LOOPBACK);
		
		assertFalse(result.containsAddress(Ipv4Address.LOOPBACK));
		assertTrue(result.containsAddress(Ipv4Address.BROADCAST));
	}
	
	@Test
	void removeNetworkNull() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		assertThrows(NullPointerException.class, () -> set.removeNetwork(null));
	}
	
	@Test
	void removeNetwork() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.0.0/16"));
		Ipv4Set result = set.removeNetwork(Ipv4Network.parse("192.168.1.0/24"));
		
		assertFalse(result.containsNetwork(Ipv4Network.parse("192.168.1.0/24")));
		assertTrue(result.containsAddress(Ipv4Address.fromOctets(192, 168, 0, 1)));
	}
	
	@Test
	void removeRangeNull() {
		Ipv4Set set = Ipv4Set.of(Ipv4Range.parse("192.168.1.1-192.168.1.100"));
		assertThrows(NullPointerException.class, () -> set.removeRange(null));
	}
	
	@Test
	void removeRange() {
		Ipv4Set set = Ipv4Set.of(Ipv4Range.parse("192.168.1.1-192.168.1.100"));
		Ipv4Set result = set.removeRange(Ipv4Range.parse("192.168.1.20-192.168.1.30"));
		
		assertFalse(result.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 25)));
		assertTrue(result.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 10)));
		assertTrue(result.containsAddress(Ipv4Address.fromOctets(192, 168, 1, 50)));
	}
	
	@Test
	void toRanges() {
		Ipv4Set set = Ipv4Set.of(
			Ipv4Range.parse("192.168.1.1-192.168.1.100"),
			Ipv4Range.parse("10.0.0.1-10.0.0.50")
		);
		
		List<Ipv4Range> ranges = set.toRanges();
		assertEquals(2, ranges.size());
	}
	
	@Test
	void toNetworks() {
		Ipv4Set set = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		List<Ipv4Network> networks = set.toNetworks();
		
		assertFalse(networks.isEmpty());
	}
	
	@Test
	void toExactNetworks() {
		Ipv4Set set = Ipv4Set.of(Ipv4Range.parse("192.168.1.0-192.168.1.255"));
		List<Ipv4Network> networks = set.toExactNetworks();
		
		assertEquals(1, networks.size());
		assertEquals("192.168.1.0/24", networks.get(0).toString());
	}
	
	@Test
	void rangeIterator() {
		Ipv4Set set = Ipv4Set.of(
			Ipv4Range.parse("192.168.1.1-192.168.1.100"),
			Ipv4Range.parse("10.0.0.1-10.0.0.50")
		);
		
		Iterator<Ipv4Range> iterator = set.rangeIterator();
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		assertEquals(2, count);
	}
	
	@Test
	void rangeStream() {
		Ipv4Set set = Ipv4Set.of(
			Ipv4Range.parse("192.168.1.1-192.168.1.100"),
			Ipv4Range.parse("10.0.0.1-10.0.0.50")
		);
		
		assertEquals(2, set.rangeStream().count());
	}
	
	@Test
	void equals() {
		Ipv4Set set1 = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		Ipv4Set set2 = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		Ipv4Set set3 = Ipv4Set.of(Ipv4Network.parse("10.0.0.0/8"));
		
		assertEquals(set1, set2);
		assertNotEquals(set1, set3);
	}
	
	@Test
	void hashCodeConsistent() {
		Ipv4Set set1 = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		Ipv4Set set2 = Ipv4Set.of(Ipv4Network.parse("192.168.1.0/24"));
		
		assertEquals(set1.hashCode(), set2.hashCode());
	}
	
	@Test
	void toStringEmpty() {
		assertEquals("Ipv4Set[]", Ipv4Set.empty().toString());
	}
	
	@Test
	void toStringNonEmpty() {
		Ipv4Set set = Ipv4Set.of(Ipv4Address.LOOPBACK);
		String str = set.toString();
		
		assertTrue(str.startsWith("Ipv4Set["));
		assertTrue(str.endsWith("]"));
	}
	
	@Test
	void normalizeMergesAdjacent() {
		Ipv4Set set = Ipv4Set.of(
			Ipv4Range.parse("192.168.1.1-192.168.1.50"),
			Ipv4Range.parse("192.168.1.51-192.168.1.100")
		);
		
		List<Ipv4Range> ranges = set.toRanges();
		assertEquals(1, ranges.size());
		assertEquals("192.168.1.1-192.168.1.100", ranges.get(0).toString());
	}
	
	@Test
	void normalizeMergesOverlapping() {
		Ipv4Set set = Ipv4Set.of(
			Ipv4Range.parse("192.168.1.1-192.168.1.75"),
			Ipv4Range.parse("192.168.1.50-192.168.1.100")
		);
		
		List<Ipv4Range> ranges = set.toRanges();
		assertEquals(1, ranges.size());
		assertEquals("192.168.1.1-192.168.1.100", ranges.get(0).toString());
	}
}
