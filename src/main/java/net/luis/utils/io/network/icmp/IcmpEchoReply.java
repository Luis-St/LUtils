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

import java.time.Duration;
import java.util.Objects;

/**
 * The outcome of a successful {@link IcmpSocket#ping(IpAddress, int, byte[]) ping}.<br>
 *
 * @see IcmpSocket
 *
 * @author Luis-St
 *
 * @param packet The echo reply that answered the request
 * @param roundTripTime The time between sending the request and receiving the reply
 */
public record IcmpEchoReply(
	@NonNull IcmpPacket packet,
	@NonNull Duration roundTripTime
) {
	
	/**
	 * Constructs a new echo reply.<br>
	 *
	 * @param packet The echo reply packet
	 * @param roundTripTime The round trip time
	 * @throws NullPointerException If the packet or round trip time is null
	 */
	public IcmpEchoReply {
		Objects.requireNonNull(packet, "Packet must not be null");
		Objects.requireNonNull(roundTripTime, "Round trip time must not be null");
	}
}
