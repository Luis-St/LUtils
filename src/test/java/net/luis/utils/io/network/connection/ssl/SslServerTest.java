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

package net.luis.utils.io.network.connection.ssl;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslServer}.<br>
 *
 * @author Luis-St
 */
@Tag("tls")
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class SslServerTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	private static final ConnectionHandler<SslServer, SslConnection> NO_OP = (server, connection) -> {};
	private static final ConnectionHandler<SslServer, SslConnection> BLOCKING = (server, connection) -> connection.receive();
	private static SslServerConfig serverConfig;
	private static SslClientConfig clientConfig;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverConfig = SslServerConfig.builder(SslTestContext.serverContext()).build();
		clientConfig = SslClientConfig.builder().sslContext(SslTestContext.clientContext()).build();
	}
	
	private static SslServerConfigBuilder serverBuilder() throws Exception {
		return SslServerConfig.builder(SslTestContext.serverContext());
	}
	
	private static SslConnection connect(SslServer server) throws NetworkConnectionException {
		return SslConnection.connectTo(server.boundEndpoint(), clientConfig);
	}
	
	private static boolean awaitConnections(SslServer server, int expected) throws InterruptedException {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
		while (System.nanoTime() < deadline && server.connections().size() != expected) {
			Thread.sleep(10);
		}
		return server.connections().size() == expected;
	}
	
	private static byte[] bytes(String value) {
		return value.getBytes(StandardCharsets.UTF_8);
	}
	
	private static String string(byte[] value) {
		return new String(value, StandardCharsets.UTF_8);
	}
	
	@Test
	void constructWithConfig() {
		try (SslServer server = new SslServer(EPHEMERAL, serverConfig, NO_OP)) {
			assertSame(serverConfig, server.config());
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void constructWithNullConfig() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new SslServer(EPHEMERAL, null, NO_OP));
		
		assertEquals("Config must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNullBindEndpoint() {
		assertThrows(NullPointerException.class, () -> new SslServer(null, serverConfig, NO_OP));
	}
	
	@Test
	void constructWithNullHandler() {
		assertThrows(NullPointerException.class, () -> new SslServer(EPHEMERAL, serverConfig, null));
	}
	
	@Test
	void startOnWithNullArguments() {
		assertThrows(NullPointerException.class, () -> SslServer.startOn(null, serverConfig, NO_OP));
		assertThrows(NullPointerException.class, () -> SslServer.startOn(EPHEMERAL, null, NO_OP));
		assertThrows(NullPointerException.class, () -> SslServer.startOn(EPHEMERAL, serverConfig, null));
	}
	
	@Test
	void startOnPortInUse() throws Exception {
		try (SslServer first = SslServer.startOn(EPHEMERAL, serverConfig, NO_OP)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslServer.startOn(first.boundEndpoint(), serverConfig, NO_OP));
			
			assertEquals(NetworkErrorType.ADDRESS_IN_USE, exception.errorType());
		}
	}
	
	@Test
	void failedHandshakeIsReportedAndHandlerNotCalled() throws Exception {
		CompletableFuture<Throwable> error = new CompletableFuture<>();
		CompletableFuture<Optional<NetworkClient<?>>> errorClient = new CompletableFuture<>();
		AtomicInteger handled = new AtomicInteger();
		SslServerConfig config = serverBuilder().onError((client, throwable) -> {
			errorClient.complete(Optional.ofNullable(client));
			error.complete(throwable);
		}).build();
		
		try (SslServer server = SslServer.startOn(EPHEMERAL, config, (s, connection) -> handled.incrementAndGet());
			 Socket plain = new Socket("127.0.0.1", server.boundEndpoint().port())) {
			plain.getOutputStream().write(bytes("GARBAGE PLAINTEXT THAT IS NOT A CLIENT HELLO\r\n"));
			plain.getOutputStream().flush();
			
			NetworkConnectionException exception = assertInstanceOf(NetworkConnectionException.class, error.get(5, TimeUnit.SECONDS));
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			assertTrue(errorClient.get(5, TimeUnit.SECONDS).isEmpty());
			assertEquals(0, handled.get());
		}
	}
	
	@Test
	void requiredClientAuthRejectsClientWithoutCertificate() throws Exception {
		CompletableFuture<Throwable> error = new CompletableFuture<>();
		AtomicInteger handled = new AtomicInteger();
		SslServerConfig config = serverBuilder().clientAuth(SslClientAuth.REQUIRED).onError((client, throwable) -> error.complete(throwable)).build();
		SslClientConfig trustOnly = SslClientConfig.builder().sslContext(SslTestContext.trustOnlyClientContext()).build();
		
		try (SslServer server = SslServer.startOn(EPHEMERAL, config, (s, connection) -> handled.incrementAndGet())) {
			boolean clientFailed;
			try (SslConnection connection = SslConnection.connectTo(server.boundEndpoint(), trustOnly)) {
				clientFailed = connection.receive().isEmpty();
			} catch (NetworkConnectionException e) {
				clientFailed = true;
			}
			
			assertTrue(clientFailed);
			NetworkConnectionException exception = assertInstanceOf(NetworkConnectionException.class, error.get(5, TimeUnit.SECONDS));
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			assertEquals(0, handled.get());
		}
	}
	
	@Test
	void startOnReturnsRunningServer() throws Exception {
		try (SslServer server = SslServer.startOn(EPHEMERAL, serverConfig, NO_OP)) {
			assertTrue(server.isRunning());
			assertTrue(server.boundEndpoint().port() > 0);
		}
	}
	
	@Test
	void handlerReceivesThisServer() throws Exception {
		CompletableFuture<SslServer> received = new CompletableFuture<>();
		
		try (SslServer server = SslServer.startOn(EPHEMERAL, serverConfig, (s, connection) -> received.complete(s)); SslConnection _ = connect(server)) {
			assertSame(server, received.get(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void handlerReceivesCompletedHandshake() throws Exception {
		CompletableFuture<Boolean> valid = new CompletableFuture<>();
		
		try (SslServer server = SslServer.startOn(EPHEMERAL, serverConfig, (s, connection) -> valid.complete(connection.getSession().isValid())); SslConnection _ = connect(server)) {
			assertTrue(valid.get(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void requestedClientAuthAcceptsClientWithoutCertificate() throws Exception {
		SslServerConfig config = serverBuilder().clientAuth(SslClientAuth.REQUESTED).build();
		SslClientConfig trustOnly = SslClientConfig.builder().sslContext(SslTestContext.trustOnlyClientContext()).build();
		CompletableFuture<Throwable> peerCertificates = new CompletableFuture<>();
		ConnectionHandler<SslServer, SslConnection> handler = (s, connection) -> {
			try {
				connection.getSession().getPeerCertificates();
				peerCertificates.complete(null);
			} catch (SSLPeerUnverifiedException e) {
				peerCertificates.complete(e);
			}
		};
		
		try (SslServer server = SslServer.startOn(EPHEMERAL, config, handler); SslConnection _ = SslConnection.connectTo(server.boundEndpoint(), trustOnly)) {
			assertInstanceOf(SSLPeerUnverifiedException.class, peerCertificates.get(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void requiredClientAuthWithCertificate() throws Exception {
		SslServerConfig config = serverBuilder().clientAuth(SslClientAuth.REQUIRED).build();
		CompletableFuture<Integer> certificates = new CompletableFuture<>();
		
		try (SslServer server = SslServer.startOn(EPHEMERAL, config, (s, connection) -> certificates.complete(connection.getSession().getPeerCertificates().length));
			 SslConnection _ = connect(server)) {
			assertTrue(certificates.get(5, TimeUnit.SECONDS) > 0);
		}
	}
	
	@Test
	void openAppliesTransportSettings() throws Exception {
		SslServerConfig config = serverBuilder().framing(Framing.lines()).maxMessageSize(64).clientReadTimeout(Duration.ofSeconds(2)).build();
		CompletableFuture<SslConnection> received = new CompletableFuture<>();
		
		try (SslServer server = SslServer.startOn(EPHEMERAL, config, (s, connection) -> received.complete(connection)); SslConnection _ = connect(server)) {
			SslConnection connection = received.get(5, TimeUnit.SECONDS);
			
			assertEquals(Framing.lines(), connection.framing());
			assertEquals(64, connection.maxMessageSize());
			assertEquals(Optional.of(Duration.ofSeconds(2)), connection.readTimeout());
		}
	}
	
	@Test
	void openAppliesEnabledProtocols() throws Exception {
		SslServerConfig config = serverBuilder().enabledProtocols(List.of(TlsProtocol.TLS_V1_2)).build();
		
		try (SslServer server = SslServer.startOn(EPHEMERAL, config, BLOCKING); SslConnection connection = connect(server)) {
			assertEquals("TLSv1.2", connection.getSession().getProtocol());
		}
	}
	
	@Test
	void echoOverTls() throws Exception {
		ConnectionHandler<SslServer, SslConnection> echo = ConnectionHandler.perMessage((s, connection, data) -> connection.send(data));
		
		try (SslServer server = SslServer.startOn(EPHEMERAL, serverConfig, echo); SslConnection connection = connect(server)) {
			connection.send(bytes("echo"));
			
			assertEquals("echo", string(connection.receive().orElseThrow()));
		}
	}
	
	@Test
	void slowHandshakeDoesNotBlockOtherClients() throws Exception {
		ConnectionHandler<SslServer, SslConnection> echo = ConnectionHandler.perMessage((s, connection, data) -> connection.send(data));
		
		try (SslServer server = SslServer.startOn(EPHEMERAL, serverConfig, echo); Socket silent = new Socket("127.0.0.1", server.boundEndpoint().port())) {
			try (SslConnection connection = connect(server)) {
				connection.send(bytes("served"));
				
				assertEquals("served", string(connection.receive().orElseThrow()));
				assertFalse(silent.isClosed());
			}
		}
	}
	
	@Test
	void broadcastToTlsConnections() throws Exception {
		try (SslServer server = SslServer.startOn(EPHEMERAL, serverConfig, BLOCKING);
			 SslConnection first = connect(server); SslConnection second = connect(server); SslConnection third = connect(server)) {
			assertTrue(awaitConnections(server, 3));
			
			server.broadcast(bytes("broadcast"));
			
			assertEquals("broadcast", string(first.receive().orElseThrow()));
			assertEquals("broadcast", string(second.receive().orElseThrow()));
			assertEquals("broadcast", string(third.receive().orElseThrow()));
		}
	}
}
