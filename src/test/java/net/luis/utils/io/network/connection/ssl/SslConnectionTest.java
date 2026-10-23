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

import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.network.HostEndpoint;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.exception.*;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslConnection}.<br>
 *
 * @author Luis-St
 */
@Tag("tls")
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class SslConnectionTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	private static final ConnectionHandler<SslServer, SslConnection> ECHO = ConnectionHandler.perMessage((server, connection, data) -> connection.send(data));
	private static SslServerConfig serverConfig;
	private static SslClientConfig clientConfig;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverConfig = SslServerConfig.builder(SslTestContext.serverContext()).build();
		clientConfig = SslClientConfig.builder().sslContext(SslTestContext.clientContext()).build();
	}
	
	private static SslServer echoServer(SslServerConfig config) throws NetworkConnectionException {
		return SslServer.startOn(EPHEMERAL, config, ECHO);
	}
	
	private static SslClientConfigBuilder clientBuilder() throws Exception {
		return SslClientConfig.builder().sslContext(SslTestContext.clientContext());
	}
	
	private static byte[] bytes(String value) {
		return value.getBytes(StandardCharsets.UTF_8);
	}
	
	private static String string(byte[] value) {
		return new String(value, StandardCharsets.UTF_8);
	}
	
	@Test
	void constructWithLayeredSocket() throws Exception {
		try (SslServer server = echoServer(serverConfig); Socket plain = new Socket("127.0.0.1", server.boundEndpoint().port())) {
			SSLSocket socket = SslSockets.layerClient(plain, server.boundEndpoint(), clientConfig.tls());
			
			try (SslConnection connection = new SslConnection(socket, Framing.lengthPrefixed(), 1024, null)) {
				assertTrue(connection.isActive());
				assertTrue(connection.getSession().isValid());
			}
		}
	}
	
	@Test
	void constructWithNullSocket() {
		assertThrows(NullPointerException.class, () -> new SslConnection(null, Framing.lengthPrefixed(), 1024, null));
	}
	
	@Test
	void constructWithNullFraming() throws Exception {
		try (SslServer server = echoServer(serverConfig); Socket plain = new Socket("127.0.0.1", server.boundEndpoint().port())) {
			SSLSocket socket = SslSockets.layerClient(plain, server.boundEndpoint(), clientConfig.tls());
			
			assertThrows(NullPointerException.class, () -> new SslConnection(socket, null, 1024, null));
			socket.close();
		}
	}
	
	@Test
	void connectToWithConfig() throws Exception {
		SslServerConfig config = SslServerConfig.builder(SslTestContext.serverContext()).framing(Framing.lines()).maxMessageSize(128).build();
		SslClientConfig client = clientBuilder().framing(Framing.lines()).maxMessageSize(128).readTimeout(Duration.ofSeconds(5)).build();
		
		try (SslServer server = echoServer(config); SslConnection connection = SslConnection.connectTo(server.boundEndpoint(), client)) {
			assertTrue(connection.isActive());
			assertEquals(Framing.lines(), connection.framing());
			assertEquals(128, connection.maxMessageSize());
			assertEquals(Optional.of(Duration.ofSeconds(5)), connection.readTimeout());
		}
	}
	
	@Test
	void connectToWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> SslConnection.connectTo(null));
		assertThrows(NullPointerException.class, () -> SslConnection.connectTo(null, clientConfig));
	}
	
	@Test
	void connectToWithNullConfig() {
		assertThrows(NullPointerException.class, () -> SslConnection.connectTo(new IpEndpoint(Ipv4Address.LOOPBACK, 443), null));
	}
	
	@Test
	void connectToRefused() throws Exception {
		try (Socket unlistened = new Socket()) {
			unlistened.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslConnection.connectTo(new IpEndpoint(Ipv4Address.LOOPBACK, unlistened.getLocalPort()), clientConfig));
			assertEquals(NetworkErrorType.CONNECTION_REFUSED, exception.errorType());
		}
	}
	
	@Test
	void connectToWithDefaultConfigRejectsSelfSignedCertificate() throws Exception {
		try (SslServer server = echoServer(serverConfig)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslConnection.connectTo(server.boundEndpoint()));
			
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
		}
	}
	
	@Test
	void connectToHandshakeFailureClosesSocket() throws Exception {
		SslClientConfig config = clientBuilder().readTimeout(Duration.ofMillis(300)).build();
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress())) {
			CompletableFuture<Boolean> endOfStream = new CompletableFuture<>();
			Thread.ofPlatform().daemon().start(() -> {
				try (Socket accepted = serverSocket.accept()) {
					accepted.getInputStream().readAllBytes();
					endOfStream.complete(true);
				} catch (IOException e) {
					endOfStream.complete(false);
				}
			});
			
			NetworkTimeoutException exception = assertThrows(NetworkTimeoutException.class, () -> SslConnection.connectTo(new IpEndpoint(Ipv4Address.LOOPBACK, serverSocket.getLocalPort()), config));
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			assertTrue(endOfStream.get(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void connectToWithHostnameMismatch() throws Exception {
		IpEndpoint bind = new IpEndpoint(Ipv4Address.fromOctets(127, 0, 0, 2), 0);
		SslClientConfig config = clientBuilder().verifyHostname(true).build();
		
		try (SslServer server = SslServer.startOn(bind, serverConfig, ECHO)) {
			HostEndpoint endpoint = new HostEndpoint("127.0.0.2", server.boundEndpoint().port());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslConnection.connectTo(endpoint, config));
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
		}
	}
	
	@Test
	void getSessionReturnsNegotiatedSession() throws Exception {
		try (SslServer server = echoServer(serverConfig); SslConnection connection = SslConnection.connectTo(server.boundEndpoint(), clientConfig)) {
			X509Certificate certificate = (X509Certificate) connection.getSession().getPeerCertificates()[0];
			
			assertTrue(List.of("TLSv1.3", "TLSv1.2").contains(connection.getSession().getProtocol()));
			assertFalse(connection.getSession().getCipherSuite().isEmpty());
			assertEquals("CN=localhost", certificate.getSubjectX500Principal().getName());
		}
	}
	
	@Test
	void connectToWithHostEndpoint() throws Exception {
		SslClientConfig config = clientBuilder().verifyHostname(true).build();
		
		try (SslServer server = echoServer(serverConfig); SslConnection connection = SslConnection.connectTo(new HostEndpoint("localhost", server.boundEndpoint().port()), config)) {
			assertTrue(connection.isActive());
		}
	}
	
	@Test
	void connectToWithIpEndpointAndHostnameVerification() throws Exception {
		SslClientConfig config = clientBuilder().verifyHostname(true).build();
		
		try (SslServer server = echoServer(serverConfig); SslConnection connection = SslConnection.connectTo(new IpEndpoint(Ipv4Address.LOOPBACK, server.boundEndpoint().port()), config)) {
			assertTrue(connection.isActive());
		}
	}
	
	@Test
	void sendAndReceiveOverTls() throws Exception {
		try (SslServer server = echoServer(serverConfig); SslConnection connection = SslConnection.connectTo(server.boundEndpoint(), clientConfig)) {
			connection.send(bytes("hello"));
			
			assertEquals("hello", string(connection.receive().orElseThrow()));
		}
	}
	
	@Test
	void connectToAppliesFraming() throws Exception {
		SslServerConfig config = SslServerConfig.builder(SslTestContext.serverContext()).framing(Framing.lines()).build();
		SslClientConfig client = clientBuilder().framing(Framing.lines()).build();
		
		try (SslServer server = echoServer(config); SslConnection connection = SslConnection.connectTo(server.boundEndpoint(), client)) {
			connection.send(bytes("a line"));
			
			assertEquals("a line", string(connection.receive().orElseThrow()));
		}
	}
	
	@Test
	void connectToAppliesEnabledProtocols() throws Exception {
		SslClientConfig config = clientBuilder().enabledProtocols(List.of(TlsProtocol.TLS_V1_2)).build();
		
		try (SslServer server = echoServer(serverConfig); SslConnection connection = SslConnection.connectTo(server.boundEndpoint(), config)) {
			assertEquals("TLSv1.2", connection.getSession().getProtocol());
		}
	}
	
	@Test
	void mapCodecOverTls() throws Exception {
		try (SslServer server = echoServer(serverConfig); SslConnection connection = SslConnection.connectTo(server.boundEndpoint(), clientConfig)) {
			CodecClient<String> client = connection.map(Codecs.STRING);
			
			for (String value : List.of("first", "second", "third")) {
				client.send(value);
				assertEquals(value, client.receive().orElseThrow());
			}
		}
	}
	
	@Test
	void largeMessagesOverTls() throws Exception {
		int maxSize = 1024 * 1024;
		SslServerConfig config = SslServerConfig.builder(SslTestContext.serverContext()).maxMessageSize(maxSize).build();
		SslClientConfig client = clientBuilder().maxMessageSize(maxSize).build();
		byte[] payload = new byte[512 * 1024];
		new Random(7).nextBytes(payload);
		
		try (SslServer server = echoServer(config); SslConnection connection = SslConnection.connectTo(server.boundEndpoint(), client)) {
			connection.send(payload);
			
			assertArrayEquals(payload, connection.receive().orElseThrow());
		}
	}
	
	@Test
	void mutualTlsWithClientCertificate() throws Exception {
		SslServerConfig config = SslServerConfig.builder(SslTestContext.serverContext()).clientAuth(SslClientAuth.REQUIRED).build();
		
		try (SslServer server = echoServer(config); SslConnection connection = SslConnection.connectTo(server.boundEndpoint(), clientConfig)) {
			connection.send(bytes("mutual"));
			
			assertEquals("mutual", string(connection.receive().orElseThrow()));
			assertNotNull(connection.getSession().getLocalCertificates());
		}
	}
}
