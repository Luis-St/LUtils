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

package net.luis.utils.io.network.file;

import com.google.common.collect.Maps;
import net.luis.utils.io.network.Endpoint;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.Connection;
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.context.ContextKey;
import net.luis.utils.io.network.connection.tcp.*;
import net.luis.utils.util.UUIDs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class FtpServer {
	
	private static final Logger LOGGER = LogManager.getLogger(FtpServer.class);
	private static final ContextKey<UUID> CONNECTION_ID_KEY = ContextKey.of("ftp_connection_id", UUID.class);
	
	private NetworkServer server;
	private final Map<UUID, FtpConnection> connections = Maps.newConcurrentMap();
	
	public FtpServer() {}
	
	public void start(@NonNull IpEndpoint endpoint, @NonNull FtpServerConfig config) {
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		Objects.requireNonNull(config, "Ftp config must not be null");
		
		TcpServerConfig tcpConfig = TcpServerConfig.builder()
			.backlog(config.maxConnections())
			.executorStrategy(config.executorStrategy())
			.onClientConnect(this::onConnect)
			.onClientDisconnect(this::onDisconnect)
			.build();
		
		this.server = TcpServer.startOn(endpoint, tcpConfig);
	}
	
	private void onConnect(@Nullable Connection connection, @NonNull Endpoint localEndpoint, @NonNull Endpoint remoteEndpoint, @NonNull Instant timestamp) {
		if (connection == null) {
			LOGGER.warn("Received null connection on connect event from {} to {} at {}", localEndpoint, remoteEndpoint, timestamp);
			return;
		}
		
		try {
			UUID connectionId = UUIDs.v4();
			connection.context().set(CONNECTION_ID_KEY, connectionId);
			this.connections.put(connectionId, new FtpConnection((TcpConnection) connection));
		} catch (Exception e) {
			LOGGER.error("Failed to create ftp connection for client {} at {}: {}", remoteEndpoint, timestamp, e.getMessage(), e);
		}
	}
	
	private void onDisconnect(@Nullable Connection connection, @NonNull Endpoint localEndpoint, @NonNull Endpoint remoteEndpoint, @NonNull Instant timestamp) {
		if (connection == null) {
			LOGGER.warn("Received null connection on disconnect event from {} to {} at {}", localEndpoint, remoteEndpoint, timestamp);
			return;
		}
		
		UUID connectionId = connection.context().get(CONNECTION_ID_KEY).orElse(null);
		if (connectionId != null) {
			this.connections.remove(connectionId);
		} else {
			LOGGER.warn("Connection {} from {} to {} at {} has no associated connection ID", connection, localEndpoint, remoteEndpoint, timestamp);
		}
	}
}
