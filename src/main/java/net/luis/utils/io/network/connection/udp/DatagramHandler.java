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

import org.jspecify.annotations.NonNull;

/**
 * Handles a single datagram received by a {@link UdpServer}.<br>
 * <p>
 *     The handler is called on a thread of the executor of the server, so several datagrams can be handled at the same time.<br>
 *     Replies are sent through the peer of the server, an exception thrown by the handler is reported to the error handler of the server.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * DatagramHandler echo = (server, datagram) -> server.peer().send(datagram);
 * }</pre>
 *
 * @see UdpServer
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface DatagramHandler {
	
	/**
	 * Handles a datagram received by the given server.<br>
	 *
	 * @param server The server that received the datagram
	 * @param datagram The received datagram, whose endpoint is the source
	 * @throws Exception If handling the datagram fails
	 */
	void handle(@NonNull UdpServer server, @NonNull UdpDatagram datagram) throws Exception;
}
