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

import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TeredoInfo}.<br>
 *
 * @author Luis-St
 */
class TeredoInfoTest {
	
	@Test
	void constructWithNullServerThrows() {
		assertThrows(NullPointerException.class, () -> new TeredoInfo(null, new Ipv4Address(0), 80, 0, false));
	}
	
	@Test
	void constructWithNullClientThrows() {
		assertThrows(NullPointerException.class, () -> new TeredoInfo(new Ipv4Address(0), null, 80, 0, false));
	}
	
	@Test
	void constructWithNegativePortThrows() {
		Ipv4Address server = new Ipv4Address(0);
		Ipv4Address client = new Ipv4Address(0);
		assertThrows(IllegalArgumentException.class, () -> new TeredoInfo(server, client, -1, 0, false));
	}
	
	@Test
	void constructWithPortTooHighThrows() {
		Ipv4Address server = new Ipv4Address(0);
		Ipv4Address client = new Ipv4Address(0);
		assertThrows(IllegalArgumentException.class, () -> new TeredoInfo(server, client, 65536, 0, false));
	}
	
	@Test
	void constructWithValidParameters() {
		Ipv4Address server = Ipv4Address.fromOctets(65, 54, 227, 120);
		Ipv4Address client = Ipv4Address.fromOctets(192, 0, 2, 45);
		TeredoInfo info = new TeredoInfo(server, client, 40000, 0x8000, true);
		
		assertEquals(server, info.server());
		assertEquals(client, info.client());
		assertEquals(40000, info.port());
		assertEquals(0x8000, info.flags());
		assertTrue(info.coneNat());
	}
	
	@Test
	void constructWithMinPort() {
		Ipv4Address server = new Ipv4Address(0);
		Ipv4Address client = new Ipv4Address(0);
		TeredoInfo info = new TeredoInfo(server, client, 0, 0, false);
		assertEquals(0, info.port());
	}
	
	@Test
	void constructWithMaxPort() {
		Ipv4Address server = new Ipv4Address(0);
		Ipv4Address client = new Ipv4Address(0);
		TeredoInfo info = new TeredoInfo(server, client, 65535, 0, false);
		assertEquals(65535, info.port());
	}
	
	@Test
	void teredoPrefixReturnsCorrectNetwork() {
		Ipv6Network prefix = TeredoInfo.teredoPrefix();
		assertNotNull(prefix);
		assertEquals(32, prefix.prefixLength());
		assertEquals(0x2001000000000000L, prefix.networkAddress().highBits());
		assertEquals(0L, prefix.networkAddress().lowBits());
	}
	
	@Test
	void isTeredoWithNullThrows() {
		assertThrows(NullPointerException.class, () -> TeredoInfo.isTeredo(null));
	}
	
	@Test
	void isTeredoReturnsTrueForTeredoAddress() {
		// Teredo prefix is 2001:0000::/32
		Ipv6Address teredo = new Ipv6Address(0x2001000041360000L, 0L);
		assertTrue(TeredoInfo.isTeredo(teredo));
	}
	
	@Test
	void isTeredoReturnsFalseForNonTeredoAddress() {
		Ipv6Address nonTeredo = new Ipv6Address(0x2001db8000000000L, 0L);
		assertFalse(TeredoInfo.isTeredo(nonTeredo));
	}
	
	@Test
	void isTeredoReturnsFalseForLoopback() {
		assertFalse(TeredoInfo.isTeredo(Ipv6Address.LOOPBACK));
	}
	
	@Test
	void isTeredoReturnsFalseForUnspecified() {
		assertFalse(TeredoInfo.isTeredo(Ipv6Address.UNSPECIFIED));
	}
	
	@Test
	void fromWithNullThrows() {
		assertThrows(NullPointerException.class, () -> TeredoInfo.from(null));
	}
	
	@Test
	void fromNonTeredoReturnsEmpty() {
		Ipv6Address nonTeredo = new Ipv6Address(0x2001db8000000000L, 1L);
		Optional<TeredoInfo> result = TeredoInfo.from(nonTeredo);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void fromTeredoAddressReturnsInfo() {
		// Teredo address: 2001:0000:4136:e378:8000:63bf:3fff:fdd2
		// Server: 65.54.227.120 (0x4136e378)
		// Flags: 0x8000 (cone NAT)
		// Port: 0x63bf XOR 0xFFFF = 40000 (0x9c40)
		// Client: 0x3ffffdd2 XOR 0xFFFFFFFF = 192.0.2.45 (0xc000022d)
		long highBits = 0x20010000_4136e378L;
		long lowBits = 0x800063bf_3ffffdd2L;
		Ipv6Address teredo = new Ipv6Address(highBits, lowBits);
		
		Optional<TeredoInfo> result = TeredoInfo.from(teredo);
		assertTrue(result.isPresent());
		
		TeredoInfo info = result.get();
		assertEquals(Ipv4Address.fromOctets(65, 54, 227, 120), info.server());
		assertEquals(40000, info.port());
		assertEquals(0x8000, info.flags());
		assertTrue(info.coneNat());
	}
	
	@Test
	void fromTeredoWithoutConeNat() {
		// Create Teredo address without cone NAT flag
		long highBits = 0x20010000_4136e378L;
		long lowBits = 0x000063bf_3ffffdd2L; // flags = 0x0000 (no cone NAT)
		Ipv6Address teredo = new Ipv6Address(highBits, lowBits);
		
		Optional<TeredoInfo> result = TeredoInfo.from(teredo);
		assertTrue(result.isPresent());
		assertFalse(result.get().coneNat());
	}
	
	@Test
	void toIpv6AddressRoundTrip() {
		Ipv4Address server = Ipv4Address.fromOctets(65, 54, 227, 120);
		Ipv4Address client = Ipv4Address.fromOctets(192, 0, 2, 45);
		TeredoInfo original = new TeredoInfo(server, client, 40000, 0x8000, true);
		
		Ipv6Address ipv6 = original.toIpv6Address();
		assertTrue(TeredoInfo.isTeredo(ipv6));
		
		Optional<TeredoInfo> parsed = TeredoInfo.from(ipv6);
		assertTrue(parsed.isPresent());
		
		TeredoInfo reconstructed = parsed.get();
		assertEquals(original.server(), reconstructed.server());
		assertEquals(original.client(), reconstructed.client());
		assertEquals(original.port(), reconstructed.port());
		assertEquals(original.flags(), reconstructed.flags());
		assertEquals(original.coneNat(), reconstructed.coneNat());
	}
	
	@Test
	void toIpv6AddressProducesValidTeredoAddress() {
		Ipv4Address server = Ipv4Address.fromOctets(10, 20, 30, 40);
		Ipv4Address client = Ipv4Address.fromOctets(192, 168, 1, 1);
		TeredoInfo info = new TeredoInfo(server, client, 12345, 0, false);
		
		Ipv6Address ipv6 = info.toIpv6Address();
		assertTrue(TeredoInfo.isTeredo(ipv6));
		
		// Verify the high bits contain the Teredo prefix and server address
		assertEquals(0x20010000L, ipv6.highBits() >>> 32);
	}
	
	@Test
	void coneNatFlagConstant() {
		assertEquals(0x8000, TeredoInfo.CONE_NAT_FLAG);
	}
	
	@Test
	void recordEquals() {
		Ipv4Address server = Ipv4Address.fromOctets(10, 20, 30, 40);
		Ipv4Address client = Ipv4Address.fromOctets(192, 168, 1, 1);
		
		TeredoInfo info1 = new TeredoInfo(server, client, 80, 0, false);
		TeredoInfo info2 = new TeredoInfo(server, client, 80, 0, false);
		TeredoInfo info3 = new TeredoInfo(server, client, 81, 0, false);
		
		assertEquals(info1, info2);
		assertNotEquals(info1, info3);
	}
	
	@Test
	void recordHashCode() {
		Ipv4Address server = Ipv4Address.fromOctets(10, 20, 30, 40);
		Ipv4Address client = Ipv4Address.fromOctets(192, 168, 1, 1);
		
		TeredoInfo info1 = new TeredoInfo(server, client, 80, 0, false);
		TeredoInfo info2 = new TeredoInfo(server, client, 80, 0, false);
		
		assertEquals(info1.hashCode(), info2.hashCode());
	}
	
	@Test
	void portObfuscationCorrect() {
		// Test that port XOR 0xFFFF produces the correct obfuscated value
		Ipv4Address server = new Ipv4Address(0);
		Ipv4Address client = new Ipv4Address(0);
		TeredoInfo info = new TeredoInfo(server, client, 40000, 0, false);
		
		Ipv6Address ipv6 = info.toIpv6Address();
		// Extract the obfuscated port from bits 32-47 of lowBits
		int obfuscatedPort = (int) ((ipv6.lowBits() >>> 32) & 0xFFFF);
		assertEquals(40000 ^ 0xFFFF, obfuscatedPort);
	}
}
