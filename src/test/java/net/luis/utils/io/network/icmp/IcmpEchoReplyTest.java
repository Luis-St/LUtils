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
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IcmpEchoReply}.<br>
 *
 * @author Luis-St
 */
class IcmpEchoReplyTest {
	
	private static final IcmpPacket REPLY_PACKET = new IcmpPacket(Ipv4Address.LOOPBACK, 0, 0, 0x00010001, new byte[4]);
	
	@Test
	void constructWithValidValues() {
		IcmpEchoReply reply = new IcmpEchoReply(REPLY_PACKET, Duration.ofMillis(5));
		assertSame(REPLY_PACKET, reply.packet());
		assertEquals(Duration.ofMillis(5), reply.roundTripTime());
	}
	
	@Test
	void constructWithNullPacket() {
		assertThrows(NullPointerException.class, () -> new IcmpEchoReply(null, Duration.ZERO));
	}
	
	@Test
	void constructWithNullRoundTripTime() {
		assertThrows(NullPointerException.class, () -> new IcmpEchoReply(REPLY_PACKET, null));
	}
	
	@Test
	void constructWithZeroRoundTripTime() {
		IcmpEchoReply reply = assertDoesNotThrow(() -> new IcmpEchoReply(REPLY_PACKET, Duration.ZERO));
		assertTrue(reply.roundTripTime().isZero());
	}
	
	@Test
	void equalRepliesWithSamePacket() {
		IcmpEchoReply first = new IcmpEchoReply(REPLY_PACKET, Duration.ofMillis(3));
		IcmpEchoReply second = new IcmpEchoReply(REPLY_PACKET, Duration.ofMillis(3));
		IcmpEchoReply other = new IcmpEchoReply(REPLY_PACKET, Duration.ofMillis(4));
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, other);
	}
	
	@Test
	void constructWithIpv6Packet() {
		IcmpPacket packet = new IcmpPacket(Ipv6Address.LOOPBACK, 129, 0, 0x12340007, new byte[] { 1, 2 });
		IcmpEchoReply reply = new IcmpEchoReply(packet, Duration.ofNanos(1));
		assertTrue(reply.packet().isEchoReply());
		assertEquals(7, reply.packet().sequenceNumber());
		assertEquals(Duration.ofNanos(1), reply.roundTripTime());
	}
}
