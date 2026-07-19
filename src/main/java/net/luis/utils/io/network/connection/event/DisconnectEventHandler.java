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

package net.luis.utils.io.network.connection.event;

import org.jspecify.annotations.NonNull;

/**
 * Handler for connection termination events.<br>
 * This functional interface is used to handle disconnection events for both TCP/SSL clients and servers.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * DisconnectEventHandler onDisconnect = event -> {
 *     System.out.println("Disconnected from " + event.remoteEndpoint());
 * };
 *
 * TcpClientConfig config = TcpClientConfig.builder()
 *     .onDisconnect(onDisconnect)
 *     .build();
 * }</pre>
 *
 * @see DisconnectEvent
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface DisconnectEventHandler {
	
	/**
	 * Called when a disconnect event occurs.<br>
	 * @param event The disconnect event context
	 */
	void handle(@NonNull DisconnectEvent event);
}
