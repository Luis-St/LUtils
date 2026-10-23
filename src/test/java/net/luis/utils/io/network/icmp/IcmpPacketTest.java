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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IcmpPacket}.<br>
 *
 * @author Luis-St
 */
class IcmpPacketTest {
	
	@Test
	void constructWithValidValues() {
		byte[] payload = { 1, 2, 3 };
		IcmpPacket packet = new IcmpPacket(Ipv4Address.LOOPBACK, 8, 0, 0x12340001, payload);
		assertEquals(Ipv4Address.LOOPBACK, packet.address());
		assertEquals(8, packet.type());
		assertEquals(0, packet.code());
		assertEquals(0x12340001, packet.restOfHeader());
		assertSame(payload, packet.payload());
	}
	
	@Test
	void constructWithNullAddress() {
		assertThrows(NullPointerException.class, () -> new IcmpPacket(null, 8, 0, 0, new byte[0]));
	}
	
	@Test
	void constructWithNullPayload() {
		assertThrows(NullPointerException.class, () -> new IcmpPacket(Ipv4Address.LOOPBACK, 8, 0, 0, null));
	}
	
	@Test
	void constructWithNegativeType() {
		assertThrows(IllegalArgumentException.class, () -> new IcmpPacket(Ipv4Address.LOOPBACK, -1, 0, 0, new byte[0]));
	}
	
	@Test
	void constructWithTypeAboveMax() {
		assertThrows(IllegalArgumentException.class, () -> new IcmpPacket(Ipv4Address.LOOPBACK, 256, 0, 0, new byte[0]));
	}
	
	@Test
	void constructWithNegativeCode() {
		assertThrows(IllegalArgumentException.class, () -> new IcmpPacket(Ipv4Address.LOOPBACK, 8, -1, 0, new byte[0]));
	}
	
	@Test
	void constructWithCodeAboveMax() {
		assertThrows(IllegalArgumentException.class, () -> new IcmpPacket(Ipv4Address.LOOPBACK, 8, 256, 0, new byte[0]));
	}
	
	@Test
	void echoRequestWithNullAddress() {
		assertThrows(NullPointerException.class, () -> IcmpPacket.echoRequest(null, 1, 1, new byte[0]));
	}
	
	@Test
	void echoRequestWithNullPayload() {
		assertThrows(NullPointerException.class, () -> IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, 1, 1, null));
	}
	
	@Test
	void echoRequestWithNegativeIdentifier() {
		assertThrows(IllegalArgumentException.class, () -> IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, -1, 1, new byte[0]));
	}
	
	@Test
	void echoRequestWithIdentifierAboveMax() {
		assertThrows(IllegalArgumentException.class, () -> IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, 65536, 1, new byte[0]));
	}
	
	@Test
	void echoRequestWithNegativeSequenceNumber() {
		assertThrows(IllegalArgumentException.class, () -> IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, 1, -1, new byte[0]));
	}
	
	@Test
	void echoRequestWithSequenceNumberAboveMax() {
		assertThrows(IllegalArgumentException.class, () -> IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, 1, 65536, new byte[0]));
	}
	
	@Test
	void constructWithTypeAndCodeBoundaries() {
		assertDoesNotThrow(() -> new IcmpPacket(Ipv4Address.LOOPBACK, 0, 0, 0, new byte[0]));
		assertDoesNotThrow(() -> new IcmpPacket(Ipv4Address.LOOPBACK, 255, 255, 0, new byte[0]));
	}
	
	@Test
	void echoRequestWithIdentifierAndSequenceBoundaries() {
		IcmpPacket lower = IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, 0, 0, new byte[0]);
		IcmpPacket upper = IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, 0xFFFF, 0xFFFF, new byte[0]);
		assertEquals(0, lower.restOfHeader());
		assertEquals(65535, upper.identifier());
		assertEquals(65535, upper.sequenceNumber());
	}
	
	@Test
	void echoRequestForIpv4Address() {
		IcmpPacket packet = IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, 1, 1, new byte[0]);
		assertEquals(8, packet.type());
		assertEquals(0, packet.code());
		assertTrue(packet.isEchoRequest());
	}
	
	@Test
	void echoRequestForIpv6Address() {
		IcmpPacket packet = IcmpPacket.echoRequest(Ipv6Address.LOOPBACK, 1, 1, new byte[0]);
		assertEquals(128, packet.type());
		assertEquals(0, packet.code());
		assertTrue(packet.isEchoRequest());
	}
	
	@Test
	void isEchoRequestWithNonZeroCode() {
		assertFalse(new IcmpPacket(Ipv4Address.LOOPBACK, 8, 1, 0, new byte[0]).isEchoRequest());
	}
	
	@Test
	void isEchoRequestWithOtherType() {
		assertFalse(new IcmpPacket(Ipv4Address.LOOPBACK, 0, 0, 0, new byte[0]).isEchoRequest());
	}
	
	@Test
	void isEchoReplyForIpv4() {
		IcmpPacket packet = new IcmpPacket(Ipv4Address.LOOPBACK, 0, 0, 0, new byte[0]);
		assertTrue(packet.isEchoReply());
		assertFalse(packet.isEchoRequest());
	}
	
	@Test
	void isEchoReplyForIpv6() {
		assertTrue(new IcmpPacket(Ipv6Address.LOOPBACK, 129, 0, 0, new byte[0]).isEchoReply());
	}
	
	@Test
	void isEchoReplyWithNonZeroCode() {
		assertFalse(new IcmpPacket(Ipv4Address.LOOPBACK, 0, 3, 0, new byte[0]).isEchoReply());
	}
	
	@Test
	void isEchoReplyWithOtherType() {
		assertFalse(new IcmpPacket(Ipv4Address.LOOPBACK, 3, 0, 0, new byte[0]).isEchoReply());
	}
	
	@Test
	void headerSizeIsEight() {
		assertEquals(8, IcmpPacket.HEADER_SIZE);
	}
	
	@Test
	void versionFollowsAddress() {
		assertEquals(IcmpVersion.ICMP_V4, new IcmpPacket(Ipv4Address.LOOPBACK, 0, 0, 0, new byte[0]).version());
		assertEquals(IcmpVersion.ICMP_V6, new IcmpPacket(Ipv6Address.LOOPBACK, 0, 0, 0, new byte[0]).version());
	}
	
	@Test
	void identifierAndSequenceNumberFromRestOfHeader() {
		IcmpPacket packet = new IcmpPacket(Ipv4Address.LOOPBACK, 0, 0, 0x12345678, new byte[0]);
		assertEquals(0x1234, packet.identifier());
		assertEquals(0x5678, packet.sequenceNumber());
	}
	
	@Test
	void lengthWithEmptyPayload() {
		assertEquals(8, new IcmpPacket(Ipv4Address.LOOPBACK, 0, 0, 0, new byte[0]).length());
	}
	
	@Test
	void lengthWithPayload() {
		assertEquals(64, new IcmpPacket(Ipv4Address.LOOPBACK, 0, 0, 0, new byte[56]).length());
	}
	
	@Test
	void identifierWithNegativeRestOfHeader() {
		IcmpPacket packet = new IcmpPacket(Ipv4Address.LOOPBACK, 0, 0, 0xFFFF0001, new byte[0]);
		assertEquals(0xFFFF, packet.identifier());
		assertEquals(1, packet.sequenceNumber());
	}
	
	@Test
	void echoRequestPacksIdentifierAndSequenceNumber() {
		IcmpPacket packet = IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, 0xABCD, 42, new byte[0]);
		assertEquals(0xABCD, packet.identifier());
		assertEquals(42, packet.sequenceNumber());
		assertEquals(0xABCD002A, packet.restOfHeader());
	}
	
	@Test
	void payloadCopyIsIndependent() {
		IcmpPacket packet = new IcmpPacket(Ipv4Address.LOOPBACK, 0, 0, 0, new byte[] { 1, 2, 3 });
		byte[] copy = packet.payloadCopy();
		assertArrayEquals(packet.payload(), copy);
		assertNotSame(packet.payload(), copy);
		
		copy[0] = 9;
		assertEquals(1, packet.payload()[0]);
	}
	
	@Test
	void echoRequestKeepsPayloadContent() {
		byte[] payload = { 5, 6, 7, 8, 9 };
		IcmpPacket packet = IcmpPacket.echoRequest(Ipv6Address.LOOPBACK, 7, 3, payload);
		assertArrayEquals(payload, packet.payloadCopy());
		assertEquals(8 + payload.length, packet.length());
	}
	
	@Test
	void constructWithIpv4TypeOnIpv6Address() {
		IcmpPacket packet = new IcmpPacket(Ipv6Address.LOOPBACK, 8, 0, 0, new byte[0]);
		assertFalse(packet.isEchoRequest());
		assertFalse(packet.isEchoReply());
	}
}
