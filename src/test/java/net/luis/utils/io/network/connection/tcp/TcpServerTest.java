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

package net.luis.utils.io.network.connection.tcp;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.ConnectionHandler;
import net.luis.utils.io.network.connection.Framing;
import net.luis.utils.io.network.connection.exception.*;
import net.luis.utils.io.network.connection.ssl.*;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TcpServer}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TcpServerTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	private static final ConnectionHandler<TcpServer, TcpConnection> NO_OP = (server, connection) -> {};
	
	private static byte[] bytes(String value) {
		return value.getBytes(StandardCharsets.UTF_8);
	}
	
	private static String string(byte[] value) {
		return new String(value, StandardCharsets.UTF_8);
	}
	
	@Test
	void constructWithDefaultConfig() {
		try (TcpServer server = new TcpServer(EPHEMERAL, NO_OP)) {
			assertSame(TcpServerConfig.DEFAULT, server.config());
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void constructWithConfig() {
		TcpServerConfig config = TcpServerConfig.builder().backlog(1).build();
		
		try (TcpServer server = new TcpServer(EPHEMERAL, config, NO_OP)) {
			assertSame(config, server.config());
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void constructWithNullBindEndpoint() {
		assertThrows(NullPointerException.class, () -> new TcpServer(null, NO_OP));
		assertThrows(NullPointerException.class, () -> new TcpServer(null, TcpServerConfig.DEFAULT, NO_OP));
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new TcpServer(EPHEMERAL, null, NO_OP));
	}
	
	@Test
	void constructWithNullHandler() {
		assertThrows(NullPointerException.class, () -> new TcpServer(EPHEMERAL, null));
		assertThrows(NullPointerException.class, () -> new TcpServer(EPHEMERAL, TcpServerConfig.DEFAULT, null));
	}
	
	@Test
	void startOnWithNullArguments() {
		assertThrows(NullPointerException.class, () -> TcpServer.startOn(null, NO_OP));
		assertThrows(NullPointerException.class, () -> TcpServer.startOn(EPHEMERAL, null));
		assertThrows(NullPointerException.class, () -> TcpServer.startOn(null, TcpServerConfig.DEFAULT, NO_OP));
		assertThrows(NullPointerException.class, () -> TcpServer.startOn(EPHEMERAL, null, NO_OP));
		assertThrows(NullPointerException.class, () -> TcpServer.startOn(EPHEMERAL, TcpServerConfig.DEFAULT, null));
	}
	
	@Test
	void startOnPortInUse() throws Exception {
		try (TcpServer first = TcpServer.startOn(EPHEMERAL, NO_OP)) {
			IpEndpoint occupied = first.boundEndpoint();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> TcpServer.startOn(occupied, NO_OP));
			assertEquals(NetworkErrorType.ADDRESS_IN_USE, exception.errorType());
		}
	}
	
	@Test
	void startOnReturnsRunningServer() throws Exception {
		try (TcpServer first = TcpServer.startOn(EPHEMERAL, NO_OP); TcpServer second = TcpServer.startOn(EPHEMERAL, TcpServerConfig.DEFAULT, NO_OP)) {
			assertTrue(first.isRunning());
			assertTrue(first.boundEndpoint().port() > 0);
			assertTrue(second.isRunning());
			assertTrue(second.boundEndpoint().port() > 0);
		}
	}
	
	@Test
	void handlerReceivesThisServer() throws Exception {
		CompletableFuture<TcpServer> received = new CompletableFuture<>();
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, (s, connection) -> received.complete(s)); TcpConnection _ = TcpConnection.connectTo(server.boundEndpoint())) {
			assertSame(server, received.get(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void openAppliesFramingAndMaxMessageSize() throws Exception {
		TcpServerConfig config = TcpServerConfig.builder().framing(Framing.lines()).maxMessageSize(16).build();
		CompletableFuture<TcpConnection> received = new CompletableFuture<>();
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, config, (s, connection) -> received.complete(connection)); TcpConnection _ = TcpConnection.connectTo(server.boundEndpoint())) {
			TcpConnection connection = received.get(5, TimeUnit.SECONDS);
			
			assertEquals(Framing.lines(), connection.framing());
			assertEquals(16, connection.maxMessageSize());
		}
	}
	
	@Test
	void openAppliesClientReadTimeout() throws Exception {
		TcpServerConfig config = TcpServerConfig.builder().clientReadTimeout(Duration.ofMillis(200)).build();
		CompletableFuture<Throwable> failure = new CompletableFuture<>();
		ConnectionHandler<TcpServer, TcpConnection> handler = (s, connection) -> {
			try {
				connection.receive();
				failure.complete(null);
			} catch (NetworkConnectionException e) {
				failure.complete(e);
			}
		};
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, config, handler); TcpConnection _ = TcpConnection.connectTo(server.boundEndpoint())) {
			NetworkTimeoutException exception = assertInstanceOf(NetworkTimeoutException.class, failure.get(5, TimeUnit.SECONDS));
			assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
		}
	}
	
	@Test
	void openWithoutClientReadTimeout() throws Exception {
		CompletableFuture<Optional<Duration>> received = new CompletableFuture<>();
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, (s, connection) -> received.complete(connection.readTimeout())); TcpConnection _ = TcpConnection.connectTo(server.boundEndpoint())) {
			assertTrue(received.get(5, TimeUnit.SECONDS).isEmpty());
		}
	}
	
	@Test
	void echoServerWithDefaultConfig() throws Exception {
		ConnectionHandler<TcpServer, TcpConnection> echo = ConnectionHandler.perMessage((s, connection, data) -> connection.send(data));
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, echo); TcpConnection client = TcpConnection.connectTo(server.boundEndpoint())) {
			client.send(bytes("one"));
			client.send(bytes("two"));
			client.send(bytes("three"));
			
			assertEquals("one", string(client.receive().orElseThrow()));
			assertEquals("two", string(client.receive().orElseThrow()));
			assertEquals("three", string(client.receive().orElseThrow()));
		}
	}
	
	@Test
	@Tag("tls")
	void serverSideStartTlsUpgrade() throws Exception {
		SslServerConfig serverConfig = SslServerConfig.builder(SslTestContext.serverContext()).build();
		SslUpgradeConfig clientConfig = SslUpgradeConfig.builder().sslContext(SslTestContext.clientContext()).build();
		ConnectionHandler<TcpServer, TcpConnection> handler = (s, connection) -> {
			if ("STARTTLS".equals(string(connection.receive().orElseThrow()))) {
				connection.send(bytes("OK"));
				try (SslConnection secured = connection.upgrade(serverConfig)) {
					secured.send(secured.receive().orElseThrow());
				}
			}
		};
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, handler); TcpConnection client = TcpConnection.connectTo(server.boundEndpoint())) {
			client.send(bytes("STARTTLS"));
			assertEquals("OK", string(client.receive().orElseThrow()));
			
			try (SslConnection secured = client.upgrade(clientConfig)) {
				secured.send(bytes("secret"));
				
				assertEquals("secret", string(secured.receive().orElseThrow()));
				assertTrue(client.isUpgraded());
			}
		}
	}
}
