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
 * Test class for {@link Ipv4Network}.<br>
 *
 * @author Luis-St
 */
class Ipv4NetworkTest {
	
	@Test
	void constructWithNullAddress() {
		assertThrows(NullPointerException.class, () -> new Ipv4Network(null, 24));
	}
	
	@Test
	void constructWithInvalidPrefix() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 0);
		assertThrows(IllegalArgumentException.class, () -> new Ipv4Network(address, -1));
		assertThrows(IllegalArgumentException.class, () -> new Ipv4Network(address, 33));
	}
	
	@Test
	void constructValid() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 0);
		Ipv4Network network = new Ipv4Network(address, 24);
		
		assertEquals(address, network.networkAddress());
		assertEquals(24, network.prefixLength());
	}
	
	@Test
	void ofWithNullAddress() {
		assertThrows(NullPointerException.class, () -> Ipv4Network.of(null, 24));
	}
	
	@Test
	void ofWithInvalidPrefix() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 100);
		assertThrows(IllegalArgumentException.class, () -> Ipv4Network.of(address, -1));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Network.of(address, 33));
	}
	
	@Test
	void ofCreatesCanonicalNetwork() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 100);
		Ipv4Network network = Ipv4Network.of(address, 24);
		
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 0), network.networkAddress());
		assertEquals(24, network.prefixLength());
	}
	
	@Test
	void ofWithMask() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 100);
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		Ipv4Network network = Ipv4Network.of(address, mask);
		
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 0), network.networkAddress());
		assertEquals(24, network.prefixLength());
	}
	
	@Test
	void ofWithNullMask() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 100);
		assertThrows(NullPointerException.class, () -> Ipv4Network.of(address, null));
	}
	
	@Test
	void parseNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Network.parse(null));
	}
	
	@Test
	void parseInvalid() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4Network.parse("invalid"));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Network.parse("192.168.1.0"));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Network.parse("192.168.1.0/33"));
	}
	
	@Test
	void parseValid() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 0), network.networkAddress());
		assertEquals(24, network.prefixLength());
	}
	
	@Test
	void tryParseNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Network.tryParse(null));
	}
	
	@Test
	void tryParseInvalid() {
		assertTrue(Ipv4Network.tryParse("invalid").isEmpty());
		assertTrue(Ipv4Network.tryParse("192.168.1.0").isEmpty());
		assertTrue(Ipv4Network.tryParse("192.168.1.0/33").isEmpty());
	}
	
	@Test
	void tryParseValid() {
		assertTrue(Ipv4Network.tryParse("192.168.1.0/24").isPresent());
		assertEquals("192.168.1.0/24", Ipv4Network.tryParse("192.168.1.0/24").get().toString());
	}
	
	@Test
	void broadcastAddress() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 255), network.broadcastAddress());
	}
	
	@Test
	void broadcastAddressSlash32() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.1/32");
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), network.broadcastAddress());
	}
	
	@Test
	void sizeSlash24() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertEquals(BigInteger.valueOf(256), network.size());
	}
	
	@Test
	void sizeSlash32() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.1/32");
		assertEquals(BigInteger.ONE, network.size());
	}
	
	@Test
	void sizeSlash0() {
		Ipv4Network network = Ipv4Network.parse("0.0.0.0/0");
		assertEquals(BigInteger.valueOf(4294967296L), network.size());
	}
	
	@Test
	void containsAddressNull() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertThrows(NullPointerException.class, () -> network.contains((Ipv4Address) null));
	}
	
	@Test
	void containsAddress() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		
		assertTrue(network.contains(Ipv4Address.fromOctets(192, 168, 1, 0)));
		assertTrue(network.contains(Ipv4Address.fromOctets(192, 168, 1, 100)));
		assertTrue(network.contains(Ipv4Address.fromOctets(192, 168, 1, 255)));
		assertFalse(network.contains(Ipv4Address.fromOctets(192, 168, 2, 0)));
		assertFalse(network.contains(Ipv4Address.fromOctets(192, 168, 0, 255)));
	}
	
	@Test
	void containsNetworkNull() {
		Ipv4Network network = Ipv4Network.parse("192.168.0.0/16");
		assertThrows(NullPointerException.class, () -> network.contains((Ipv4Network) null));
	}
	
	@Test
	void containsNetwork() {
		Ipv4Network parent = Ipv4Network.parse("192.168.0.0/16");
		Ipv4Network child = Ipv4Network.parse("192.168.1.0/24");
		
		assertTrue(parent.contains(child));
		assertFalse(child.contains(parent));
	}
	
	@Test
	void containsNetworkSelf() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertTrue(network.contains(network));
	}
	
	@Test
	void overlapsNull() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertThrows(NullPointerException.class, () -> network.overlaps(null));
	}
	
	@Test
	void overlaps() {
		Ipv4Network network1 = Ipv4Network.parse("192.168.0.0/16");
		Ipv4Network network2 = Ipv4Network.parse("192.168.1.0/24");
		Ipv4Network network3 = Ipv4Network.parse("10.0.0.0/8");
		
		assertTrue(network1.overlaps(network2));
		assertTrue(network2.overlaps(network1));
		assertFalse(network1.overlaps(network3));
	}
	
	@Test
	void splitSlash32ThrowsException() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.1/32");
		assertThrows(IllegalStateException.class, network::split);
	}
	
	@Test
	void split() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		List<Ipv4Network> subnets = network.split();
		
		assertEquals(2, subnets.size());
		assertEquals("192.168.1.0/25", subnets.get(0).toString());
		assertEquals("192.168.1.128/25", subnets.get(1).toString());
	}
	
	@Test
	void supernetSlash0ReturnsEmpty() {
		Ipv4Network network = Ipv4Network.parse("0.0.0.0/0");
		assertTrue(network.supernet().isEmpty());
	}
	
	@Test
	void supernet() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertTrue(network.supernet().isPresent());
		assertEquals("192.168.0.0/23", network.supernet().get().toString());
	}
	
	@Test
	void subnetsInvalidPrefix() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertThrows(IllegalArgumentException.class, () -> network.subnets(24));
		assertThrows(IllegalArgumentException.class, () -> network.subnets(20));
		assertThrows(IllegalArgumentException.class, () -> network.subnets(33));
	}
	
	@Test
	void subnets() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		List<Ipv4Network> subnets = network.subnets(26);
		
		assertEquals(4, subnets.size());
		assertEquals("192.168.1.0/26", subnets.get(0).toString());
		assertEquals("192.168.1.64/26", subnets.get(1).toString());
		assertEquals("192.168.1.128/26", subnets.get(2).toString());
		assertEquals("192.168.1.192/26", subnets.get(3).toString());
	}
	
	@Test
	void iterator() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/30");
		Iterator<Ipv4Address> iterator = network.iterator();
		
		assertTrue(iterator.hasNext());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 0), iterator.next());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), iterator.next());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 2), iterator.next());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 3), iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	void addressStream() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/30");
		assertEquals(4, network.addressStream().count());
	}
	
	@Test
	void hostIteratorSlash24() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/30");
		Iterator<Ipv4Address> iterator = network.hostIterator();
		
		assertTrue(iterator.hasNext());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), iterator.next());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 2), iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	void hostIteratorSlash31() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/31");
		Iterator<Ipv4Address> iterator = network.hostIterator();
		
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 0), iterator.next());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	void hostStream() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/30");
		assertEquals(2, network.hostStream().count());
	}
	
	@Test
	void hostCountSlash24() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertEquals(BigInteger.valueOf(254), network.hostCount());
	}
	
	@Test
	void hostCountSlash31() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/31");
		assertEquals(BigInteger.valueOf(2), network.hostCount());
	}
	
	@Test
	void hostCountSlash32() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.1/32");
		assertEquals(BigInteger.ONE, network.hostCount());
	}
	
	@Test
	void isCanonical() {
		Ipv4Network canonical = new Ipv4Network(Ipv4Address.fromOctets(192, 168, 1, 0), 24);
		assertTrue(canonical.isCanonical());
	}
	
	@Test
	void toCanonical() {
		Ipv4Network nonCanonical = new Ipv4Network(Ipv4Address.fromOctets(192, 168, 1, 100), 24);
		Ipv4Network canonical = nonCanonical.toCanonical();
		
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 0), canonical.networkAddress());
		assertTrue(canonical.isCanonical());
	}
	
	@Test
	void toCidrNotation() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertEquals("192.168.1.0/24", network.toCidrNotation());
	}
	
	@Test
	void toRange() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		Ipv4Range range = network.toRange();
		
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 0), range.start());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 255), range.end());
	}
	
	@Test
	void compareTo() {
		Ipv4Network net1 = Ipv4Network.parse("10.0.0.0/8");
		Ipv4Network net2 = Ipv4Network.parse("192.168.0.0/16");
		Ipv4Network net3 = Ipv4Network.parse("192.168.0.0/24");
		
		assertTrue(net1.compareTo(net2) < 0);
		assertTrue(net2.compareTo(net1) > 0);
		assertTrue(net2.compareTo(net3) < 0);
	}
	
	@Test
	void subnetMask() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		Ipv4SubnetMask mask = network.subnetMask();
		assertEquals("255.255.255.0", mask.toString());
	}
	
	@Test
	void wildcardMask() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		Ipv4SubnetMask wildcard = network.wildcardMask();
		assertEquals("0.0.0.255", wildcard.toString());
	}
	
	@Test
	void firstHost() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertTrue(network.firstHost().isPresent());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), network.firstHost().get());
	}
	
	@Test
	void firstHostSlash31() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/31");
		assertTrue(network.firstHost().isPresent());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 0), network.firstHost().get());
	}
	
	@Test
	void firstHostSlash32() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.1/32");
		assertTrue(network.firstHost().isPresent());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), network.firstHost().get());
	}
	
	@Test
	void lastHost() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertTrue(network.lastHost().isPresent());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 254), network.lastHost().get());
	}
	
	@Test
	void lastHostSlash31() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/31");
		assertTrue(network.lastHost().isPresent());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), network.lastHost().get());
	}
	
	@Test
	void lastHostSlash32() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.1/32");
		assertTrue(network.lastHost().isPresent());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), network.lastHost().get());
	}
	
	@Test
	void toStringFormat() {
		Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
		assertEquals("192.168.1.0/24", network.toString());
	}
	
	@Test
	void recordEquals() {
		Ipv4Network net1 = Ipv4Network.parse("192.168.1.0/24");
		Ipv4Network net2 = Ipv4Network.parse("192.168.1.0/24");
		Ipv4Network net3 = Ipv4Network.parse("192.168.2.0/24");
		
		assertEquals(net1, net2);
		assertNotEquals(net1, net3);
	}
	
	@Test
	void recordHashCode() {
		Ipv4Network net1 = Ipv4Network.parse("192.168.1.0/24");
		Ipv4Network net2 = Ipv4Network.parse("192.168.1.0/24");
		
		assertEquals(net1.hashCode(), net2.hashCode());
	}
}
