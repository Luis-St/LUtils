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

import net.luis.utils.io.network.address.IpAddress;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents an ICMP message together with its source or destination address.<br>
 * For received packets, the address is the source address.<br>
 * For outgoing packets, the address is the destination address.<br>
 * <p>
 *     The checksum is not part of this record.<br>
 *     It is computed by the socket when the packet is sent and verified by the operating system when it is received.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IcmpPacket request = IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, 0x1234, 1, "ping".getBytes());
 * socket.send(request);
 *
 * IcmpPacket reply = socket.receive();
 * System.out.println("Type " + reply.type() + " from " + reply.address());
 * }</pre>
 *
 * @see IcmpSocket
 *
 * @author Luis-St
 *
 * @param address The remote address (source for received, destination for sending)
 * @param type The message type
 * @param code The message code
 * @param restOfHeader The four bytes following the checksum, which hold the identifier and sequence number of echo messages
 * @param payload The data following the header
 */
public record IcmpPacket(
	@NonNull IpAddress<?> address,
	int type,
	int code,
	int restOfHeader,
	byte @NonNull [] payload
) {
	
	/**
	 * The size of the ICMP header in bytes.<br>
	 */
	public static final int HEADER_SIZE = 8;
	
	/**
	 * Constructs a new ICMP packet.<br>
	 *
	 * @param address The remote address
	 * @param type The message type
	 * @param code The message code
	 * @param restOfHeader The four bytes following the checksum
	 * @param payload The data following the header
	 * @throws NullPointerException If the address or payload is null
	 * @throws IllegalArgumentException If the type or code is not between 0 and 255
	 */
	public IcmpPacket {
		Objects.requireNonNull(address, "Address must not be null");
		Objects.requireNonNull(payload, "Payload must not be null");
		
		if (type < 0 || type > 255) {
			throw new IllegalArgumentException("Type must be between 0 and 255: " + type);
		}
		if (code < 0 || code > 255) {
			throw new IllegalArgumentException("Code must be between 0 and 255: " + code);
		}
	}
	
	/**
	 * Creates an echo request for the given address.<br>
	 * The message type is chosen by the IP version of the address.<br>
	 *
	 * @param address The destination address
	 * @param identifier The identifier of the echo request
	 * @param sequenceNumber The sequence number of the echo request
	 * @param payload The data the reply is expected to echo
	 * @return The echo request
	 * @throws NullPointerException If the address or payload is null
	 * @throws IllegalArgumentException If the identifier or sequence number is not between 0 and 65535
	 */
	public static @NonNull IcmpPacket echoRequest(@NonNull IpAddress<?> address, int identifier, int sequenceNumber, byte @NonNull [] payload) {
		Objects.requireNonNull(address, "Address must not be null");
		if (identifier < 0 || identifier > 0xFFFF) {
			throw new IllegalArgumentException("Identifier must be between 0 and 65535: " + identifier);
		}
		if (sequenceNumber < 0 || sequenceNumber > 0xFFFF) {
			throw new IllegalArgumentException("Sequence number must be between 0 and 65535: " + sequenceNumber);
		}
		return new IcmpPacket(address, IcmpVersion.of(address).echoRequestType(), 0, (identifier << 16) | sequenceNumber, payload);
	}
	
	/**
	 * Returns the ICMP version of this packet derived from its address.<br>
	 * @return The ICMP version
	 */
	public @NonNull IcmpVersion version() {
		return IcmpVersion.of(this.address);
	}
	
	/**
	 * Returns the identifier of an echo message, which is the upper half of the rest of the header.<br>
	 * @return The identifier
	 */
	public int identifier() {
		return this.restOfHeader >>> 16;
	}
	
	/**
	 * Returns the sequence number of an echo message, which is the lower half of the rest of the header.<br>
	 * @return The sequence number
	 */
	public int sequenceNumber() {
		return this.restOfHeader & 0xFFFF;
	}
	
	/**
	 * Checks whether this packet is an echo request.<br>
	 * @return True if the type is the echo request type of the packet version
	 */
	public boolean isEchoRequest() {
		return this.code == 0 && this.type == this.version().echoRequestType();
	}
	
	/**
	 * Checks whether this packet is an echo reply.<br>
	 * @return True if the type is the echo reply type of the packet version
	 */
	public boolean isEchoReply() {
		return this.code == 0 && this.type == this.version().echoReplyType();
	}
	
	/**
	 * Returns the total length of the message including the header.<br>
	 * @return The number of bytes of the message on the wire
	 */
	public int length() {
		return HEADER_SIZE + this.payload.length;
	}
	
	/**
	 * Returns a copy of the payload.<br>
	 * Modifications to the returned array will not affect this packet.<br>
	 *
	 * @return A copy of the payload
	 */
	public byte @NonNull [] payloadCopy() {
		return this.payload.clone();
	}
}
