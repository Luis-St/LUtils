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

import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * The result of a single echo request sent by an {@link IcmpPing}.<br>
 *
 * @see IcmpPing
 * @see IcmpPingSession
 *
 * @author Luis-St
 */
public sealed interface IcmpPingResult {
	
	/**
	 * Returns the sequence number of the echo request.<br>
	 * @return The sequence number
	 */
	int sequenceNumber();
	
	/**
	 * Returns the round trip time of the echo request.<br>
	 * @return The round trip time, or an empty optional if the request was not answered
	 */
	default @NonNull Optional<Duration> roundTripTime() {
		return Optional.empty();
	}
	
	/**
	 * The echo request was answered.<br>
	 *
	 * @author Luis-St
	 *
	 * @param sequenceNumber The sequence number of the echo request
	 * @param reply The echo reply and the round trip time
	 */
	record Replied(int sequenceNumber, @NonNull IcmpEchoReply reply) implements IcmpPingResult {
		
		/**
		 * Constructs a new answered result.<br>
		 *
		 * @param sequenceNumber The sequence number of the echo request
		 * @param reply The echo reply
		 * @throws NullPointerException If the reply is null
		 */
		public Replied {
			Objects.requireNonNull(reply, "Reply must not be null");
		}
		
		@Override
		public @NonNull Optional<Duration> roundTripTime() {
			return Optional.of(this.reply.roundTripTime());
		}
	}
	
	/**
	 * The echo request was sent but no reply arrived within the timeout.<br>
	 *
	 * @author Luis-St
	 *
	 * @param sequenceNumber The sequence number of the echo request
	 */
	record Lost(int sequenceNumber) implements IcmpPingResult {}
	
	/**
	 * The echo request could not be sent or its reply could not be received, for example because the network is unreachable.<br>
	 *
	 * @author Luis-St
	 *
	 * @param sequenceNumber The sequence number of the echo request
	 * @param message The message describing the failure
	 */
	record Failed(int sequenceNumber, @NonNull String message) implements IcmpPingResult {
		
		/**
		 * Constructs a new failed result.<br>
		 *
		 * @param sequenceNumber The sequence number of the echo request
		 * @param message The message describing the failure
		 * @throws NullPointerException If the message is null
		 */
		public Failed {
			Objects.requireNonNull(message, "Message must not be null");
		}
	}
}
