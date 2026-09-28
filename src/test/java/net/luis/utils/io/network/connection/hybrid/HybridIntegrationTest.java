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

package net.luis.utils.io.network.connection.hybrid;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.ssl.*;
import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import net.luis.utils.io.network.connection.tcp.TcpServerConfig;
import net.luis.utils.io.network.connection.udp.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for hybrid client and server communication.<br>
 * Tests UDP, TCP and SSL/TLS clients against a hybrid server, alone and on a shared port.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class HybridIntegrationTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	private static final Duration DETECTION = Duration.ofMillis(300);
	
	private static SSLContext serverContext;
	private static SSLContext clientContext;
	private static SSLContext trustOnlyClientContext;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverContext = SslTestContext.serverContext();
		clientContext = SslTestContext.clientContext();
		trustOnlyClientContext = SslTestContext.trustOnlyClientContext();
	}
	
	private static @NonNull IpEndpoint loopback(@NonNull HybridServer server) {
		return new IpEndpoint(Ipv4Address.LOOPBACK, server.boundEndpoint().port());
	}
	
	private static byte @NonNull [] filled(int length, byte value) {
		byte[] data = new byte[length];
		Arrays.fill(data, value);
		return data;
	}
	
	private static void send(@NonNull Connection connection, byte @NonNull [] data) {
		try {
			connection.send(data);
		} catch (NetworkConnectionException _) {}
	}
	
	private static byte @NonNull [] prefixed(@NonNull String prefix, byte @NonNull [] data) {
		byte[] prefixBytes = prefix.getBytes(StandardCharsets.UTF_8);
		byte[] result = Arrays.copyOf(prefixBytes, prefixBytes.length + data.length);
		System.arraycopy(data, 0, result, prefixBytes.length, data.length);
		return result;
	}
	
	private static @NonNull ErrorEventHandler recorder(@NonNull List<NetworkErrorType> errors) {
		return (connection, errorType, message, cause) -> errors.add(errorType);
	}
	
	private static @NonNull UdpServerConfig udpEchoConfig() {
		return UdpServerConfig.builder().onMessage((server, datagram, data) -> {
			try {
				server.send(datagram.endpoint(), data);
			} catch (NetworkConnectionException _) {}
		}).build();
	}
	
	private static @NonNull TcpServerConfig tcpEchoConfig() {
		return TcpServerConfig.builder().onMessage((server, connection, data) -> send(connection, data)).build();
	}
	
	private static @NonNull SslServerConfig sslEchoConfig() {
		return SslServerConfig.builder(serverContext).onMessage((server, connection, data) -> send(connection, data)).build();
	}
	
	private static @NonNull UdpClientConfig udpClientConfig() {
		return UdpClientConfig.builder().receiveTimeout(Duration.ofSeconds(5)).build();
	}
	
	private static @NonNull TcpClientConfig tcpClientConfig() {
		return TcpClientConfig.builder().readTimeout(Duration.ofSeconds(5)).build();
	}
	
	private static @NonNull SslClientConfigBuilder sslClientConfig(@NonNull SSLContext context) {
		return SslClientConfig.builder().sslContext(context).readTimeout(Duration.ofSeconds(5));
	}
	
	private static @NonNull HybridClient<byte[]> connectTcp(@NonNull HybridServer server) throws Exception {
		HybridClient<byte[]> client = HybridClient.createTcp(tcpClientConfig());
		client.connect(loopback(server));
		return client;
	}
	
	private static @NonNull HybridClient<byte[]> connectSsl(@NonNull HybridServer server) throws Exception {
		HybridClient<byte[]> client = HybridClient.createSsl(sslClientConfig(clientContext).build());
		client.connect(loopback(server));
		return client;
	}
	
	private static @NonNull HybridClient<UdpDatagram> bindUdp() throws Exception {
		HybridClient<UdpDatagram> client = HybridClient.createUdp(udpClientConfig());
		client.connect(EPHEMERAL);
		return client;
	}
	
	private static byte @NonNull [] roundTrip(@NonNull HybridClient<byte[]> client, byte @NonNull [] data) throws Exception {
		client.send(data);
		return client.receive();
	}
	
	private static byte @NonNull [] roundTrip(@NonNull HybridClient<UdpDatagram> client, @NonNull HybridServer server, byte @NonNull [] data) throws Exception {
		client.send(new UdpDatagram(loopback(server), data));
		return client.receive().data();
	}
	
	private static byte @NonNull [] readUntil(@NonNull HybridClient<byte[]> client, int expected) throws Exception {
		ByteArrayOutputStream reassembled = new ByteArrayOutputStream();
		while (reassembled.size() < expected) {
			byte[] chunk = client.receive();
			if (chunk.length == 0) {
				break;
			}
			reassembled.writeBytes(chunk);
		}
		return reassembled.toByteArray();
	}
	
	private static void assertDisconnected(@NonNull HybridClient<byte[]> client) {
		try {
			assertEquals(0, client.receive().length);
		} catch (IOException _) {}
	}
	
	private static void awaitCondition(@NonNull BooleanSupplier condition) throws Exception {
		for (int attempt = 0; attempt < 400 && !condition.getAsBoolean(); attempt++) {
			Thread.sleep(25);
		}
		assertTrue(condition.getAsBoolean());
	}
	
	@Test
	void serverStartAndStop() {
		try (HybridServer server = HybridServer.create(EPHEMERAL, udpEchoConfig(), tcpEchoConfig(), sslEchoConfig())) {
			assertFalse(server.isRunning());
			
			server.start();
			assertTrue(server.isRunning());
			assertNotEquals(0, server.boundEndpoint().port());
			
			server.stop();
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void serverImplementsNetworkServer() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, tcpEchoConfig())) {
			assertInstanceOf(NetworkServer.class, server);
		}
	}
	
	@Test
	void clientImplementsNetworkClient() {
		try (HybridClient<UdpDatagram> udp = HybridClient.createUdp(udpClientConfig());
			 HybridClient<byte[]> tcp = HybridClient.createTcp(tcpClientConfig());
			 HybridClient<byte[]> ssl = HybridClient.createSsl(sslClientConfig(clientContext).build())) {
			assertInstanceOf(NetworkClient.class, udp);
			assertInstanceOf(NetworkClient.class, tcp);
			assertInstanceOf(NetworkClient.class, ssl);
		}
	}
	
	@Test
	void clientNotActiveInitially() {
		try (HybridClient<UdpDatagram> udp = HybridClient.createUdp(udpClientConfig());
			 HybridClient<byte[]> tcp = HybridClient.createTcp(tcpClientConfig());
			 HybridClient<byte[]> ssl = HybridClient.createSsl(sslClientConfig(clientContext).build())) {
			for (NetworkClient<?> client : List.of(udp, tcp, ssl)) {
				assertFalse(client.isActive());
				assertTrue(client.localEndpoint().isEmpty());
				assertTrue(client.remoteEndpoint().isEmpty());
			}
		}
	}
	
	@Test
	void serverBoundEndpointBeforeStartReturnsBindEndpoint() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, tcpEchoConfig())) {
			assertEquals(EPHEMERAL, server.boundEndpoint());
		}
	}
	
	@Test
	void serverBoundEndpointAfterStartReturnsActualPort() {
		try (HybridServer server = HybridServer.create(EPHEMERAL, udpEchoConfig(), tcpEchoConfig(), sslEchoConfig())) {
			server.start();
			assertNotEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void udpServerEchoesUdpClient() throws Exception {
		try (HybridServer server = HybridServer.createUdp(EPHEMERAL, udpEchoConfig())) {
			server.start();
			
			try (HybridClient<UdpDatagram> client = bindUdp()) {
				client.send(new UdpDatagram(loopback(server), "udp".getBytes()));
				UdpDatagram echoed = client.receive();
				assertArrayEquals("udp".getBytes(), echoed.data());
				assertEquals(server.boundEndpoint().port(), echoed.endpoint().port());
			}
		}
	}
	
	@Test
	void tcpServerEchoesTcpClient() throws Exception {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, tcpEchoConfig())) {
			server.start();
			
			try (HybridClient<byte[]> client = connectTcp(server)) {
				assertArrayEquals("tcp".getBytes(), roundTrip(client, "tcp".getBytes()));
			}
		}
	}
	
	@Test
	void sslServerEchoesSslClient() throws Exception {
		AtomicInteger connects = new AtomicInteger();
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onClientConnect((connection, local, remote, timestamp) -> connects.incrementAndGet())
			.onMessage((server, connection, data) -> send(connection, data))
			.build();
		
		try (HybridServer server = HybridServer.createSsl(EPHEMERAL, config)) {
			server.start();
			
			try (HybridClient<byte[]> client = connectSsl(server)) {
				assertArrayEquals("ssl".getBytes(), roundTrip(client, "ssl".getBytes()));
			}
			assertEquals(1, connects.get());
		}
	}
	
	@Test
	void tcpClientAgainstSslOnlyServerFailsHandshakeOnServer() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		SslServerConfig config = SslServerConfig.builder(serverContext).onError(recorder(errors)).build();
		
		try (HybridServer server = HybridServer.createSsl(EPHEMERAL, config)) {
			server.start();
			
			try (HybridClient<byte[]> client = connectTcp(server)) {
				client.send("plain".getBytes());
				awaitCondition(() -> !errors.isEmpty());
				assertDisconnected(client);
			}
		}
	}
	
	@Test
	void sslClientAgainstTcpOnlyServerFailsHandshake() throws Exception {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, tcpEchoConfig())) {
			server.start();
			
			try (HybridClient<byte[]> client = HybridClient.createSsl(sslClientConfig(clientContext).build())) {
				assertThrows(NetworkConnectionException.class, () -> client.connect(loopback(server)));
			}
		}
	}
	
	@Test
	void tcpAndSslClientsShareOnePort() throws Exception {
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onMessage((server, connection, data) -> send(connection, prefixed("tcp:", data))).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).onMessage((server, connection, data) -> send(connection, prefixed("ssl:", data))).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpConfig, sslConfig, DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> tcp = connectTcp(server); HybridClient<byte[]> ssl = connectSsl(server)) {
				assertArrayEquals("tcp:hello".getBytes(), roundTrip(tcp, "hello".getBytes()));
				assertArrayEquals("ssl:hello".getBytes(), roundTrip(ssl, "hello".getBytes()));
			}
		}
	}
	
	@Test
	void allThreeProtocolsShareOnePort() throws Exception {
		try (HybridServer server = HybridServer.create(EPHEMERAL, udpEchoConfig(), tcpEchoConfig(), sslEchoConfig(), DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> tcp = connectTcp(server); HybridClient<byte[]> ssl = connectSsl(server); HybridClient<UdpDatagram> udp = bindUdp()) {
				assertArrayEquals("tcp".getBytes(), roundTrip(tcp, "tcp".getBytes()));
				assertArrayEquals("ssl".getBytes(), roundTrip(ssl, "ssl".getBytes()));
				assertArrayEquals("udp".getBytes(), roundTrip(udp, server, "udp".getBytes()));
				assertEquals(server.boundEndpoint().port(), tcp.remoteEndpoint().orElseThrow().port());
				assertEquals(server.boundEndpoint().port(), ssl.remoteEndpoint().orElseThrow().port());
			}
		}
	}
	
	@Test
	void tcpClientMessageStartingWith0x16IsServedAsTcp() throws Exception {
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpEchoConfig(), sslEchoConfig(), DETECTION)) {
			server.start();
			
			byte[] payload = { 0x16, 0x03, 0x03, 0x00 };
			try (HybridClient<byte[]> client = connectTcp(server)) {
				assertArrayEquals(payload, roundTrip(client, payload));
			}
		}
	}
	
	@Test
	void sslClientHandshakeReachesSslConnectHandler() throws Exception {
		AtomicInteger tcpConnects = new AtomicInteger();
		CountDownLatch sslConnected = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onClientConnect((connection, local, remote, timestamp) -> tcpConnects.incrementAndGet()).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).onClientConnect((connection, local, remote, timestamp) -> sslConnected.countDown()).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpConfig, sslConfig, DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> ignored = connectSsl(server)) {
				assertTrue(sslConnected.await(5, TimeUnit.SECONDS));
			}
			assertEquals(0, tcpConnects.get());
		}
	}
	
	@Test
	void tcpClientReachesTcpConnectHandler() throws Exception {
		CountDownLatch tcpConnected = new CountDownLatch(1);
		AtomicInteger sslConnects = new AtomicInteger();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onClientConnect((connection, local, remote, timestamp) -> tcpConnected.countDown()).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).onClientConnect((connection, local, remote, timestamp) -> sslConnects.incrementAndGet()).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpConfig, sslConfig, DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> client = connectTcp(server)) {
				client.send("hello".getBytes());
				assertTrue(tcpConnected.await(5, TimeUnit.SECONDS));
			}
			assertEquals(0, sslConnects.get());
		}
	}
	
	@Test
	void serverSpeaksFirstOverTcpAfterDetectionTimeout() throws Exception {
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onConnection((server, connection) -> connection.send("welcome".getBytes())).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpConfig, sslEchoConfig(), DETECTION)) {
			server.start();
			
			long start = System.nanoTime();
			try (HybridClient<byte[]> client = connectTcp(server)) {
				assertArrayEquals("welcome".getBytes(), client.receive());
				assertTrue(System.nanoTime() - start >= DETECTION.toNanos());
			}
		}
	}
	
	@Test
	void sslConnectionHandlerOnSharedPort() throws Exception {
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).framing(false).onConnection((server, connection) -> {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
			String line = reader.readLine();
			connection.getOutputStream().write((line + "\n").getBytes(StandardCharsets.UTF_8));
			connection.getOutputStream().flush();
		}).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpEchoConfig(), sslConfig, DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> client = HybridClient.createSsl(sslClientConfig(clientContext).framing(false).build())) {
				client.connect(loopback(server));
				client.send("line\n".getBytes());
				assertArrayEquals("line\n".getBytes(), readUntil(client, 5));
			}
		}
	}
	
	@Test
	void sslBroadcastOnSharedPortReachesSslClients() throws Exception {
		CountDownLatch tcpReceived = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onMessage((server, connection, data) -> tcpReceived.countDown()).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpConfig, SslServerConfig.builder(serverContext).build(), DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> first = connectSsl(server); HybridClient<byte[]> second = connectSsl(server);
				 HybridClient<byte[]> tcp = HybridClient.createTcp(TcpClientConfig.builder().readTimeout(Duration.ofMillis(500)).build())) {
				tcp.connect(loopback(server));
				tcp.send("hello".getBytes());
				assertTrue(tcpReceived.await(5, TimeUnit.SECONDS));
				SslServer sslServer = server.sslServer().orElseThrow();
				awaitCondition(() -> sslServer.getClientCount() == 2);
				
				sslServer.broadcast("broadcast".getBytes());
				assertArrayEquals("broadcast".getBytes(), first.receive());
				assertArrayEquals("broadcast".getBytes(), second.receive());
				assertThrows(NetworkConnectionException.class, tcp::receive);
			}
		}
	}
	
	@Test
	void clientDisconnectFiresProtocolSpecificHandler() throws Exception {
		CountDownLatch tcpDisconnected = new CountDownLatch(1);
		CountDownLatch sslDisconnected = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder()
			.onMessage((server, connection, data) -> send(connection, data))
			.onClientDisconnect((connection, local, remote, timestamp) -> tcpDisconnected.countDown())
			.build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext)
			.onMessage((server, connection, data) -> send(connection, data))
			.onClientDisconnect((connection, local, remote, timestamp) -> sslDisconnected.countDown())
			.build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpConfig, sslConfig, DETECTION)) {
			server.start();
			HybridClient<byte[]> tcp = connectTcp(server);
			HybridClient<byte[]> ssl = connectSsl(server);
			roundTrip(tcp, "tcp".getBytes());
			roundTrip(ssl, "ssl".getBytes());
			
			tcp.close();
			assertTrue(tcpDisconnected.await(5, TimeUnit.SECONDS));
			assertEquals(1, sslDisconnected.getCount());
			
			ssl.close();
			assertTrue(sslDisconnected.await(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void mutualTlsOnSharedPort() throws Exception {
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).clientAuth(SslClientAuth.REQUIRED).onMessage((server, connection, data) -> send(connection, data)).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpEchoConfig(), sslConfig, DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> client = connectSsl(server)) {
				assertArrayEquals("mtls".getBytes(), roundTrip(client, "mtls".getBytes()));
			}
		}
	}
	
	@Test
	void mutualTlsRejectsClientWithoutCertificateOnSharedPort() throws Exception {
		List<NetworkErrorType> sslErrors = new CopyOnWriteArrayList<>();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).clientAuth(SslClientAuth.REQUIRED).onError(recorder(sslErrors)).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpEchoConfig(), sslConfig, DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> client = HybridClient.createSsl(sslClientConfig(trustOnlyClientContext).build())) {
				assertThrows(NetworkConnectionException.class, () -> {
					client.connect(loopback(server));
					client.send("rejected".getBytes());
					client.receive();
				});
			}
			awaitCondition(() -> !sslErrors.isEmpty());
			
			try (HybridClient<byte[]> tcp = connectTcp(server)) {
				assertArrayEquals("still".getBytes(), roundTrip(tcp, "still".getBytes()));
			}
		}
	}
	
	@Test
	void serverMessageHandlerErrorOnSharedPortReported() throws Exception {
		List<Connection> failedConnections = new CopyOnWriteArrayList<>();
		AtomicInteger calls = new AtomicInteger();
		TcpServerConfig tcpConfig = TcpServerConfig.builder()
			.onMessage((server, connection, data) -> {
				calls.incrementAndGet();
				throw new IllegalStateException("Handler fails");
			})
			.onError((connection, errorType, message, cause) -> {
				assertEquals(NetworkErrorType.IO_ERROR, errorType);
				failedConnections.add(connection);
			})
			.build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpConfig, sslEchoConfig(), DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> client = connectTcp(server)) {
				client.send("fail".getBytes());
				awaitCondition(() -> failedConnections.size() == 1);
			}
			assertNotNull(failedConnections.getFirst());
			
			try (HybridClient<byte[]> client = connectTcp(server)) {
				client.send("again".getBytes());
				awaitCondition(() -> calls.get() == 2);
			}
		}
	}
	
	@Test
	void startWithUdpPortInUseReportsAndStops() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		UdpServerConfig udpConfig = UdpServerConfig.builder().onError(recorder(errors)).build();
		
		try (DatagramSocket occupied = new DatagramSocket(0, InetAddress.getLoopbackAddress())) {
			IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, occupied.getLocalPort());
			try (HybridServer server = HybridServer.create(endpoint, udpConfig, tcpEchoConfig(), sslEchoConfig())) {
				server.start();
				
				assertFalse(server.isRunning());
				assertEquals(List.of(NetworkErrorType.ADDRESS_IN_USE), errors);
				assertFalse(server.tcpServer().orElseThrow().isRunning());
			}
		}
	}
	
	@Test
	void stopDisconnectsClientsOfEveryProtocol() throws Exception {
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpEchoConfig(), sslEchoConfig(), DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> tcp = connectTcp(server); HybridClient<byte[]> ssl = connectSsl(server)) {
				roundTrip(tcp, "tcp".getBytes());
				roundTrip(ssl, "ssl".getBytes());
				
				server.stop();
				assertDisconnected(tcp);
				assertDisconnected(ssl);
			}
		}
	}
	
	@Test
	void clientConnectToStoppedServerThrows() throws Exception {
		IpEndpoint endpoint;
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpEchoConfig(), sslEchoConfig())) {
			server.start();
			endpoint = loopback(server);
		}
		
		try (Socket placeholder = new Socket();
			 HybridClient<byte[]> tcp = HybridClient.createTcp(tcpClientConfig());
			 HybridClient<byte[]> ssl = HybridClient.createSsl(sslClientConfig(clientContext).build())) {
			try {
				placeholder.bind(endpoint.toInetSocketAddress());
			} catch (BindException e) {
				Assumptions.abort("Port " + endpoint.port() + " was taken by another test after the server stopped");
			}
			
			assertThrows(NetworkConnectionException.class, () -> tcp.connect(endpoint));
			assertThrows(NetworkConnectionException.class, () -> ssl.connect(endpoint));
		}
	}
	
	@Test
	void sendAndReceiveLargeMessageViaEcho() throws Exception {
		int size = 64 * 1024;
		TcpServerConfig tcpConfig = TcpServerConfig.builder().clientBufferSize(2 * size).onMessage((server, connection, data) -> send(connection, data)).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).clientBufferSize(2 * size).onMessage((server, connection, data) -> send(connection, data)).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpConfig, sslConfig, DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> tcp = HybridClient.createTcp(TcpClientConfig.builder().bufferSize(2 * size).readTimeout(Duration.ofSeconds(5)).build());
				 HybridClient<byte[]> ssl = HybridClient.createSsl(sslClientConfig(clientContext).bufferSize(2 * size).build())) {
				tcp.connect(loopback(server));
				ssl.connect(loopback(server));
				
				byte[] payload = filled(size, (byte) 0x5A);
				assertArrayEquals(payload, roundTrip(tcp, payload));
				assertArrayEquals(payload, roundTrip(ssl, payload));
			}
		}
	}
	
	@Test
	void multipleRoundTripsPerProtocol() throws Exception {
		try (HybridServer server = HybridServer.create(EPHEMERAL, udpEchoConfig(), tcpEchoConfig(), sslEchoConfig(), DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> tcp = connectTcp(server); HybridClient<byte[]> ssl = connectSsl(server); HybridClient<UdpDatagram> udp = bindUdp()) {
				for (int i = 0; i < 10; i++) {
					byte[] data = filled(i * 100 + 1, (byte) i);
					assertArrayEquals(data, roundTrip(tcp, data));
					assertArrayEquals(data, roundTrip(ssl, data));
					assertArrayEquals(data, roundTrip(udp, server, data));
				}
			}
		}
	}
	
	@Test
	void multipleClientsOfMixedProtocolsSimultaneously() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		UdpServerConfig udpConfig = UdpServerConfig.builder().onError(recorder(errors)).onMessage((server, datagram, data) -> {
			try {
				server.send(datagram.endpoint(), data);
			} catch (NetworkConnectionException _) {}
		}).build();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onError(recorder(errors)).onMessage((server, connection, data) -> send(connection, data)).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).onError(recorder(errors)).onMessage((server, connection, data) -> send(connection, data)).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, udpConfig, tcpConfig, sslConfig, DETECTION);
			 ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
			server.start();
			
			CountDownLatch start = new CountDownLatch(1);
			List<Future<Boolean>> results = new ArrayList<>();
			for (int i = 0; i < 4; i++) {
				byte[] data = ("client-" + i).getBytes();
				results.add(executor.submit(() -> {
					start.await();
					try (HybridClient<byte[]> client = connectTcp(server)) {
						return Arrays.equals(data, roundTrip(client, data));
					}
				}));
				results.add(executor.submit(() -> {
					start.await();
					try (HybridClient<byte[]> client = connectSsl(server)) {
						return Arrays.equals(data, roundTrip(client, data));
					}
				}));
				results.add(executor.submit(() -> {
					start.await();
					try (HybridClient<UdpDatagram> client = bindUdp()) {
						return Arrays.equals(data, roundTrip(client, server, data));
					}
				}));
			}
			
			start.countDown();
			for (Future<Boolean> result : results) {
				assertTrue(result.get(10, TimeUnit.SECONDS));
			}
			assertTrue(errors.isEmpty());
		}
	}
	
	@Test
	void unframedTcpAndSslOnSharedPort() throws Exception {
		TcpServerConfig tcpConfig = TcpServerConfig.builder().framing(false).onMessage((server, connection, data) -> send(connection, data)).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).framing(false).onMessage((server, connection, data) -> send(connection, data)).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpConfig, sslConfig, DETECTION)) {
			server.start();
			
			try (HybridClient<byte[]> tcp = HybridClient.createTcp(TcpClientConfig.builder().framing(false).readTimeout(Duration.ofSeconds(5)).build());
				 HybridClient<byte[]> ssl = HybridClient.createSsl(sslClientConfig(clientContext).framing(false).build())) {
				tcp.connect(loopback(server));
				ssl.connect(loopback(server));
				
				tcp.send("plain".getBytes());
				ssl.send("secure".getBytes());
				assertArrayEquals("plain".getBytes(), readUntil(tcp, 5));
				assertArrayEquals("secure".getBytes(), readUntil(ssl, 6));
			}
		}
	}
	
	@Test
	void restartedServerAcceptsNewClients() throws Exception {
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpEchoConfig(), sslEchoConfig(), DETECTION)) {
			server.start();
			server.stop();
			server.start();
			assertTrue(server.isRunning());
			
			try (HybridClient<byte[]> tcp = connectTcp(server); HybridClient<byte[]> ssl = connectSsl(server)) {
				assertArrayEquals("tcp".getBytes(), roundTrip(tcp, "tcp".getBytes()));
				assertArrayEquals("ssl".getBytes(), roundTrip(ssl, "ssl".getBytes()));
			}
		}
	}
}
