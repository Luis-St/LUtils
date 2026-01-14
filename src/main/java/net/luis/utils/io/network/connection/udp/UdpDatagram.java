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

package net.luis.utils.io.network.connection.udp;

import net.luis.utils.io.network.IpEndpoint;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a UDP datagram with its source/destination endpoint and payload.<br>
 * For received datagrams, the endpoint represents the source address.<br>
 * For outgoing datagrams, the endpoint represents the destination address.
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * // Receiving a datagram
 * UdpDatagram received = client.receive();
 * System.out.println("From: " + received.endpoint());
 * System.out.println("Data: " + new String(received.data()));
 *
 * // Creating a datagram to send
 * UdpDatagram toSend = new UdpDatagram(targetEndpoint, "Hello".getBytes());
 * client.send(toSend);
 * }</pre>
 *
 * @see UdpClient
 * @see UdpServer
 *
 * @author Luis-St
 *
 * @param endpoint The remote endpoint (source for received, destination for sending)
 * @param data The payload data
 */
public record UdpDatagram(
	@NonNull IpEndpoint endpoint,
	byte @NonNull [] data
) {

	/**
	 * Constructs a new UDP datagram.<br>
	 *
	 * @param endpoint The remote endpoint
	 * @param data The payload data
	 * @throws NullPointerException If endpoint or data is null
	 */
	public UdpDatagram {
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		Objects.requireNonNull(data, "Data must not be null");
	}

	/**
	 * Returns the length of the payload data.<br>
	 *
	 * @return The number of bytes in the payload
	 */
	public int length() {
		return this.data.length;
	}

	/**
	 * Returns a copy of the payload data.<br>
	 * Modifications to the returned array will not affect this datagram.
	 *
	 * @return A copy of the payload data
	 */
	public byte @NonNull [] dataCopy() {
		return this.data.clone();
	}
}
