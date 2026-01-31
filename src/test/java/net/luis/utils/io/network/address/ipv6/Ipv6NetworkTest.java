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
 * Test class for {@link Ipv6Network}.<br>
 *
 * @author Luis-St
 */
class Ipv6NetworkTest {
	
	@Test
	void constructWithNullAddress() {
		assertThrows(NullPointerException.class, () -> new Ipv6Network(null, 64));
	}
	
	@Test
	void constructWithInvalidPrefix() {
		Ipv6Address address = new Ipv6Address(0x20010DB800000000L, 0L);
		assertThrows(IllegalArgumentException.class, () -> new Ipv6Network(address, -1));
		assertThrows(IllegalArgumentException.class, () -> new Ipv6Network(address, 129));
	}
	
	@Test
	void constructValid() {
		Ipv6Address address = new Ipv6Address(0x20010DB800000000L, 0L);
		Ipv6Network network = new Ipv6Network(address, 32);
		
		assertEquals(address, network.networkAddress());
		assertEquals(32, network.prefixLength());
	}
	
	@Test
	void ofWithNullAddress() {
		assertThrows(NullPointerException.class, () -> Ipv6Network.of(null, 64));
	}
	
	@Test
	void ofWithInvalidPrefix() {
		Ipv6Address address = new Ipv6Address(0x20010DB800000000L, 1L);
		assertThrows(IllegalArgumentException.class, () -> Ipv6Network.of(address, -1));
		assertThrows(IllegalArgumentException.class, () -> Ipv6Network.of(address, 129));
	}
	
	@Test
	void ofCreatesCanonicalNetwork() {
		Ipv6Address address = new Ipv6Address(0x20010DB800000001L, 0x0000000000000001L);
		Ipv6Network network = Ipv6Network.of(address, 32);
		
		assertEquals(new Ipv6Address(0x20010DB800000000L, 0L), network.networkAddress());
	}
	
	@Test
	void parseNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Network.parse(null));
	}
	
	@Test
	void parseInvalid() {
		assertThrows(IllegalArgumentException.class, () -> Ipv6Network.parse("invalid"));
		assertThrows(IllegalArgumentException.class, () -> Ipv6Network.parse("2001:db8::1"));
		assertThrows(IllegalArgumentException.class, () -> Ipv6Network.parse("2001:db8::/129"));
	}
	
	@Test
	void parseValid() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		assertEquals(32, network.prefixLength());
	}
	
	@Test
	void tryParseNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Network.tryParse(null));
	}
	
	@Test
	void tryParseInvalid() {
		assertTrue(Ipv6Network.tryParse("invalid").isEmpty());
		assertTrue(Ipv6Network.tryParse("2001:db8::1").isEmpty());
		assertTrue(Ipv6Network.tryParse("2001:db8::/129").isEmpty());
	}
	
	@Test
	void tryParseValid() {
		assertTrue(Ipv6Network.tryParse("2001:db8::/32").isPresent());
	}
	
	@Test
	void broadcastAddressThrowsException() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		assertThrows(UnsupportedOperationException.class, network::broadcastAddress);
	}
	
	@Test
	void size() {
		Ipv6Network network64 = Ipv6Network.parse("2001:db8::/64");
		assertEquals(BigInteger.ONE.shiftLeft(64), network64.size());
		
		Ipv6Network network128 = Ipv6Network.parse("2001:db8::1/128");
		assertEquals(BigInteger.ONE, network128.size());
	}
	
	@Test
	void containsAddressNull() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		assertThrows(NullPointerException.class, () -> network.contains((Ipv6Address) null));
	}
	
	@Test
	void containsAddress() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		
		assertTrue(network.contains(new Ipv6Address(0x20010DB800000000L, 0L)));
		assertTrue(network.contains(new Ipv6Address(0x20010DB8FFFFFFFFL, -1L)));
		assertFalse(network.contains(new Ipv6Address(0x20010DB900000000L, 0L)));
	}
	
	@Test
	void containsNetworkNull() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		assertThrows(NullPointerException.class, () -> network.contains((Ipv6Network) null));
	}
	
	@Test
	void containsNetwork() {
		Ipv6Network parent = Ipv6Network.parse("2001:db8::/32");
		Ipv6Network child = Ipv6Network.parse("2001:db8:1::/48");
		
		assertTrue(parent.contains(child));
		assertFalse(child.contains(parent));
	}
	
	@Test
	void overlapsNull() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		assertThrows(NullPointerException.class, () -> network.overlaps(null));
	}
	
	@Test
	void overlaps() {
		Ipv6Network network1 = Ipv6Network.parse("2001:db8::/32");
		Ipv6Network network2 = Ipv6Network.parse("2001:db8:1::/48");
		Ipv6Network network3 = Ipv6Network.parse("2001:db9::/32");
		
		assertTrue(network1.overlaps(network2));
		assertFalse(network1.overlaps(network3));
	}
	
	@Test
	void splitSlash128ThrowsException() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::1/128");
		assertThrows(IllegalStateException.class, network::split);
	}
	
	@Test
	void split() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		List<Ipv6Network> subnets = network.split();
		
		assertEquals(2, subnets.size());
		assertEquals(33, subnets.get(0).prefixLength());
		assertEquals(33, subnets.get(1).prefixLength());
	}
	
	@Test
	void supernetSlash0ReturnsEmpty() {
		Ipv6Network network = Ipv6Network.parse("::/0");
		assertTrue(network.supernet().isEmpty());
	}
	
	@Test
	void supernet() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		assertTrue(network.supernet().isPresent());
		assertEquals(31, network.supernet().get().prefixLength());
	}
	
	@Test
	void subnetsInvalidPrefix() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		assertThrows(IllegalArgumentException.class, () -> network.subnets(32));
		assertThrows(IllegalArgumentException.class, () -> network.subnets(20));
		assertThrows(IllegalArgumentException.class, () -> network.subnets(129));
	}
	
	@Test
	void subnetsTooMany() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		assertThrows(IllegalArgumentException.class, () -> network.subnets(64));
	}
	
	@Test
	void subnets() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		List<Ipv6Network> subnets = network.subnets(34);
		
		assertEquals(4, subnets.size());
		for (Ipv6Network subnet : subnets) {
			assertEquals(34, subnet.prefixLength());
		}
	}
	
	@Test
	void iterator() {
		Ipv6Network network = Ipv6Network.parse("::1/126");
		Iterator<Ipv6Address> iterator = network.iterator();
		
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		assertEquals(4, count);
	}
	
	@Test
	void addressStream() {
		Ipv6Network network = Ipv6Network.parse("::1/126");
		assertEquals(4, network.addressStream().count());
	}
	
	@Test
	void hostIteratorThrowsException() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/64");
		assertThrows(UnsupportedOperationException.class, network::hostIterator);
	}
	
	@Test
	void hostStreamThrowsException() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/64");
		assertThrows(UnsupportedOperationException.class, network::hostStream);
	}
	
	@Test
	void hostCountThrowsException() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/64");
		assertThrows(UnsupportedOperationException.class, network::hostCount);
	}
	
	@Test
	void isCanonical() {
		Ipv6Address canonical = new Ipv6Address(0x20010DB800000000L, 0L);
		Ipv6Network network = new Ipv6Network(canonical, 32);
		assertTrue(network.isCanonical());
	}
	
	@Test
	void toCanonical() {
		Ipv6Address nonCanonical = new Ipv6Address(0x20010DB800000001L, 0L);
		Ipv6Network network = new Ipv6Network(nonCanonical, 32);
		Ipv6Network canonicalNetwork = network.toCanonical();
		
		assertTrue(canonicalNetwork.isCanonical());
	}
	
	@Test
	void toCidrNotation() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		assertTrue(network.toCidrNotation().endsWith("/32"));
	}
	
	@Test
	void toRange() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/126");
		Ipv6Range range = network.toRange();
		
		assertEquals(network.networkAddress(), range.start());
		assertEquals(network.lastAddress(), range.end());
	}
	
	@Test
	void lastAddress() {
		Ipv6Network network = Ipv6Network.parse("::/126");
		Ipv6Address last = network.lastAddress();
		
		assertEquals(3L, last.lowBits());
	}
	
	@Test
	void lastAddressSlash128() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::1/128");
		assertEquals(network.networkAddress(), network.lastAddress());
	}
	
	@Test
	void compareTo() {
		Ipv6Network net1 = Ipv6Network.parse("2001:db8::/32");
		Ipv6Network net2 = Ipv6Network.parse("2001:db9::/32");
		Ipv6Network net3 = Ipv6Network.parse("2001:db8::/48");
		
		assertTrue(net1.compareTo(net2) < 0);
		assertTrue(net1.compareTo(net3) < 0);
	}
	
	@Test
	void isStandardSubnet() {
		Ipv6Network standard = Ipv6Network.parse("2001:db8::/64");
		Ipv6Network nonStandard = Ipv6Network.parse("2001:db8::/32");
		
		assertTrue(standard.isStandardSubnet());
		assertFalse(nonStandard.isStandardSubnet());
	}
	
	@Test
	void isSingleHost() {
		Ipv6Network single = Ipv6Network.parse("2001:db8::1/128");
		Ipv6Network multi = Ipv6Network.parse("2001:db8::/64");
		
		assertTrue(single.isSingleHost());
		assertFalse(multi.isSingleHost());
	}
	
	@Test
	void toStringFormat() {
		Ipv6Network network = Ipv6Network.parse("2001:db8::/32");
		assertTrue(network.toString().contains("/32"));
	}
	
	@Test
	void recordEquals() {
		Ipv6Network net1 = Ipv6Network.parse("2001:db8::/32");
		Ipv6Network net2 = Ipv6Network.parse("2001:db8::/32");
		Ipv6Network net3 = Ipv6Network.parse("2001:db8::/48");
		
		assertEquals(net1, net2);
		assertNotEquals(net1, net3);
	}
	
	@Test
	void recordHashCode() {
		Ipv6Network net1 = Ipv6Network.parse("2001:db8::/32");
		Ipv6Network net2 = Ipv6Network.parse("2001:db8::/32");
		
		assertEquals(net1.hashCode(), net2.hashCode());
	}
}
