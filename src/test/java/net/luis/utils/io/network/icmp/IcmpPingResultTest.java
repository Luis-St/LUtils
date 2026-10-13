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

package net.luis.utils.io.network.icmp;

import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IcmpPingResult}.<br>
 *
 * @author Luis-St
 */
class IcmpPingResultTest {
	
	private static final IcmpEchoReply REPLY = new IcmpEchoReply(new IcmpPacket(Ipv4Address.LOOPBACK, 0, 0, 0x00010001, new byte[0]), Duration.ofMillis(12));
	
	@Test
	void constructReplied() {
		IcmpPingResult.Replied result = new IcmpPingResult.Replied(1, REPLY);
		assertEquals(1, result.sequenceNumber());
		assertSame(REPLY, result.reply());
	}
	
	@Test
	void constructLost() {
		assertEquals(2, new IcmpPingResult.Lost(2).sequenceNumber());
	}
	
	@Test
	void constructFailed() {
		IcmpPingResult.Failed result = new IcmpPingResult.Failed(3, "Network is unreachable");
		assertEquals(3, result.sequenceNumber());
		assertEquals("Network is unreachable", result.message());
	}
	
	@Test
	void constructRepliedWithNullReply() {
		assertThrows(NullPointerException.class, () -> new IcmpPingResult.Replied(1, null));
	}
	
	@Test
	void constructFailedWithNullMessage() {
		assertThrows(NullPointerException.class, () -> new IcmpPingResult.Failed(1, null));
	}
	
	@Test
	void roundTripTimeOfRepliedIsPresent() {
		assertEquals(Optional.of(Duration.ofMillis(12)), new IcmpPingResult.Replied(1, REPLY).roundTripTime());
	}
	
	@Test
	void roundTripTimeOfLostIsEmpty() {
		assertEquals(Optional.empty(), new IcmpPingResult.Lost(1).roundTripTime());
	}
	
	@Test
	void roundTripTimeOfFailedIsEmpty() {
		assertEquals(Optional.empty(), new IcmpPingResult.Failed(1, "error").roundTripTime());
	}
	
	@Test
	void constructFailedWithEmptyMessage() {
		assertEquals("", new IcmpPingResult.Failed(1, "").message());
	}
	
	@Test
	void equalResultsOfSameVariant() {
		assertEquals(new IcmpPingResult.Lost(1), new IcmpPingResult.Lost(1));
		assertEquals(new IcmpPingResult.Lost(1).hashCode(), new IcmpPingResult.Lost(1).hashCode());
		assertNotEquals(new IcmpPingResult.Lost(1), new IcmpPingResult.Lost(2));
		assertEquals(new IcmpPingResult.Failed(1, "x"), new IcmpPingResult.Failed(1, "x"));
		assertEquals(new IcmpPingResult.Failed(1, "x").hashCode(), new IcmpPingResult.Failed(1, "x").hashCode());
	}
	
	@Test
	void resultsOfDifferentVariantsAreNotEqual() {
		assertNotEquals(new IcmpPingResult.Lost(1), new IcmpPingResult.Failed(1, "x"));
	}
	
	@Test
	void sealedHierarchyPermitsThreeVariants() {
		assertTrue(IcmpPingResult.class.isSealed());
		Set<Class<?>> permitted = Set.of(IcmpPingResult.class.getPermittedSubclasses());
		assertEquals(Set.of(IcmpPingResult.Replied.class, IcmpPingResult.Lost.class, IcmpPingResult.Failed.class), permitted);
	}
	
	@Test
	void switchOverAllVariants() {
		List<IcmpPingResult> results = List.of(new IcmpPingResult.Replied(1, REPLY), new IcmpPingResult.Lost(2), new IcmpPingResult.Failed(3, "x"));
		List<String> labels = new ArrayList<>();
		for (IcmpPingResult result : results) {
			labels.add(switch (result) {
				case IcmpPingResult.Replied replied -> "replied";
				case IcmpPingResult.Lost lost -> "lost";
				case IcmpPingResult.Failed failed -> "failed";
			});
		}
		assertEquals(List.of("replied", "lost", "failed"), labels);
		assertTrue(results.get(0).roundTripTime().isPresent());
		assertTrue(results.get(1).roundTripTime().isEmpty());
		assertTrue(results.get(2).roundTripTime().isEmpty());
	}
}
