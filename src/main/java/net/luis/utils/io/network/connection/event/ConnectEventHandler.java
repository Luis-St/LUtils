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
 * Handler for connection establishment events.<br>
 * This functional interface is used to handle connection events for both TCP/SSL clients and servers.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * ConnectEventHandler onConnect = event -> {
 *     System.out.println("Connected to " + event.remoteEndpoint());
 * };
 *
 * TcpClientConfig config = TcpClientConfig.builder()
 *     .onConnect(onConnect)
 *     .build();
 * }</pre>
 *
 * @see ConnectEvent
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface ConnectEventHandler {
	
	/**
	 * Called when a connect event occurs.<br>
	 * @param event The connect event context
	 */
	void handle(@NonNull ConnectEvent event);
}
