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
import net.luis.utils.io.network.connection.tcp.*;
import net.luis.utils.io.network.connection.udp.UdpServerConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link HybridServer}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class HybridServerTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	private static final Duration DETECTION = Duration.ofMillis(500);
	
	private static SSLContext serverContext;
	private static SSLContext clientContext;
	private static SSLContext trustOnlyClientContext;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverContext = SslTestContext.serverContext();
		clientContext = SslTestContext.clientContext();
		trustOnlyClientContext = SslTestContext.trustOnlyClientContext();
	}
	
	private static @NonNull SslServerConfig sslConfig() {
		return SslServerConfig.builder(serverContext).build();
	}
	
	private static void echo(@NonNull Connection connection, byte @NonNull [] data) {
		try {
			connection.send(data);
		} catch (NetworkConnectionException _) {}
	}
	
	private static @NonNull ErrorEventHandler recorder(@NonNull List<NetworkErrorType> errors) {
		return (connection, errorType, message, cause) -> errors.add(errorType);
	}
	
	private static @NonNull HybridServer shared(@NonNull TcpServerConfig tcpConfig, @NonNull SslServerConfig sslConfig) {
		return HybridServer.create(EPHEMERAL, null, tcpConfig, sslConfig, DETECTION);
	}
	
	private static @NonNull IpEndpoint loopback(int port) {
		return new IpEndpoint(Ipv4Address.LOOPBACK, port);
	}
	
	private static @NonNull Socket connectPlain(@NonNull HybridServer server) throws Exception {
		Socket socket = new Socket("127.0.0.1", server.boundEndpoint().port());
		socket.setSoTimeout(5000);
		return socket;
	}
	
	private static @NonNull SSLSocket connectTls(@NonNull SSLContext context, @NonNull HybridServer server) throws Exception {
		SSLSocket socket = (SSLSocket) context.getSocketFactory().createSocket("127.0.0.1", server.boundEndpoint().port());
		socket.setSoTimeout(5000);
		socket.startHandshake();
		return socket;
	}
	
	private static void writeFrame(@NonNull Socket socket, byte @NonNull [] data) throws Exception {
		NetworkUtils.writeFrame(socket.getOutputStream(), data);
	}
	
	private static byte @Nullable [] readFrame(@NonNull Socket socket) throws Exception {
		return NetworkUtils.readFrame(socket.getInputStream(), 65536);
	}
	
	private static void assertClosedByServer(@NonNull Socket socket) {
		try {
			assertEquals(-1, socket.getInputStream().read());
		} catch (IOException _) {}
	}
	
	private static void awaitCondition(@NonNull BooleanSupplier condition) throws Exception {
		for (int attempt = 0; attempt < 400 && !condition.getAsBoolean(); attempt++) {
			Thread.sleep(25);
		}
		assertTrue(condition.getAsBoolean());
	}
	
	@Test
	void createUdpReturnsStoppedServer() {
		try (HybridServer server = HybridServer.createUdp(EPHEMERAL, UdpServerConfig.DEFAULT)) {
			assertFalse(server.isRunning());
			assertEquals(EPHEMERAL, server.boundEndpoint());
			assertTrue(server.udpServer().isEmpty());
			assertTrue(server.tcpServer().isEmpty());
			assertTrue(server.sslServer().isEmpty());
		}
	}
	
	@Test
	void createTcpReturnsStoppedServer() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT)) {
			assertFalse(server.isRunning());
			assertEquals(EPHEMERAL, server.boundEndpoint());
			assertTrue(server.udpServer().isEmpty());
			assertTrue(server.tcpServer().isEmpty());
			assertTrue(server.sslServer().isEmpty());
		}
	}
	
	@Test
	void createSslReturnsStoppedServer() {
		try (HybridServer server = HybridServer.createSsl(EPHEMERAL, sslConfig())) {
			assertFalse(server.isRunning());
			assertEquals(EPHEMERAL, server.boundEndpoint());
			assertTrue(server.udpServer().isEmpty());
			assertTrue(server.tcpServer().isEmpty());
			assertTrue(server.sslServer().isEmpty());
		}
	}
	
	@Test
	void createWithAllConfigs() {
		try (HybridServer server = HybridServer.create(EPHEMERAL, UdpServerConfig.DEFAULT, TcpServerConfig.DEFAULT, sslConfig())) {
			assertFalse(server.isRunning());
			assertEquals(EPHEMERAL, server.boundEndpoint());
		}
	}
	
	@Test
	void createWithDetectionTimeout() {
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, TcpServerConfig.DEFAULT, sslConfig(), Duration.ofMillis(200))) {
			assertNotNull(server);
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void createUdpWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> HybridServer.createUdp(null, UdpServerConfig.DEFAULT));
	}
	
	@Test
	void createUdpWithNullConfig() {
		assertThrows(NullPointerException.class, () -> HybridServer.createUdp(EPHEMERAL, null));
	}
	
	@Test
	void createTcpWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> HybridServer.createTcp(null, TcpServerConfig.DEFAULT));
	}
	
	@Test
	void createTcpWithNullConfig() {
		assertThrows(NullPointerException.class, () -> HybridServer.createTcp(EPHEMERAL, null));
	}
	
	@Test
	void createSslWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> HybridServer.createSsl(null, sslConfig()));
	}
	
	@Test
	void createSslWithNullConfig() {
		assertThrows(NullPointerException.class, () -> HybridServer.createSsl(EPHEMERAL, null));
	}
	
	@Test
	void createWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> HybridServer.create(null, null, TcpServerConfig.DEFAULT, null));
	}
	
	@Test
	void createWithNullDetectionTimeout() {
		assertThrows(NullPointerException.class, () -> HybridServer.create(EPHEMERAL, null, TcpServerConfig.DEFAULT, sslConfig(), null));
	}
	
	@Test
	void createWithoutAnyConfig() {
		assertThrows(IllegalArgumentException.class, () -> HybridServer.create(EPHEMERAL, null, null, null));
	}
	
	@Test
	void createWithZeroDetectionTimeout() {
		assertThrows(IllegalArgumentException.class, () -> HybridServer.create(EPHEMERAL, null, TcpServerConfig.DEFAULT, null, Duration.ZERO));
	}
	
	@Test
	void createWithNegativeDetectionTimeout() {
		assertThrows(IllegalArgumentException.class, () -> HybridServer.create(EPHEMERAL, null, TcpServerConfig.DEFAULT, null, Duration.ofMillis(-1)));
	}
	
	@Test
	void createWithMinimalPositiveDetectionTimeout() {
		assertDoesNotThrow(() -> HybridServer.create(EPHEMERAL, null, TcpServerConfig.DEFAULT, null, Duration.ofNanos(1)).close());
	}
	
	@Test
	void startOnTcpPortInUseStopsServer() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		try (ServerSocket occupied = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
			 HybridServer server = HybridServer.createTcp(loopback(occupied.getLocalPort()), TcpServerConfig.builder().onError(recorder(errors)).build())) {
			server.start();
			
			assertFalse(server.isRunning());
			assertEquals(List.of(NetworkErrorType.ADDRESS_IN_USE), errors);
		}
	}
	
	@Test
	void startOnSslPortInUseStopsServer() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		try (ServerSocket occupied = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
			 HybridServer server = HybridServer.createSsl(loopback(occupied.getLocalPort()), SslServerConfig.builder(serverContext).onError(recorder(errors)).build())) {
			server.start();
			
			assertFalse(server.isRunning());
			assertEquals(List.of(NetworkErrorType.ADDRESS_IN_USE), errors);
		}
	}
	
	@Test
	void startOnSharedPortInUseStopsServer() throws Exception {
		List<NetworkErrorType> tcpErrors = new CopyOnWriteArrayList<>();
		List<NetworkErrorType> sslErrors = new CopyOnWriteArrayList<>();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onError(recorder(tcpErrors)).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).onError(recorder(sslErrors)).build();
		
		try (ServerSocket occupied = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
			 HybridServer server = HybridServer.create(loopback(occupied.getLocalPort()), null, tcpConfig, sslConfig)) {
			server.start();
			
			assertFalse(server.isRunning());
			assertEquals(List.of(NetworkErrorType.ADDRESS_IN_USE), tcpErrors);
			assertTrue(sslErrors.isEmpty());
		}
	}
	
	@Test
	void startOnUdpPortInUseStopsStreamServers() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		UdpServerConfig udpConfig = UdpServerConfig.builder().onError(recorder(errors)).build();
		
		try (DatagramSocket occupied = new DatagramSocket(0, InetAddress.getLoopbackAddress());
			 HybridServer server = HybridServer.create(loopback(occupied.getLocalPort()), udpConfig, TcpServerConfig.DEFAULT, null)) {
			server.start();
			
			assertFalse(server.isRunning());
			assertFalse(server.tcpServer().orElseThrow().isRunning());
			assertFalse(server.udpServer().orElseThrow().isRunning());
			assertEquals(List.of(NetworkErrorType.ADDRESS_IN_USE), errors);
		}
	}
	
	@Test
	void startUdpOnlyOnPortInUseStopsServer() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		try (DatagramSocket occupied = new DatagramSocket(0, InetAddress.getLoopbackAddress());
			 HybridServer server = HybridServer.createUdp(loopback(occupied.getLocalPort()), UdpServerConfig.builder().onError(recorder(errors)).build())) {
			server.start();
			
			assertFalse(server.isRunning());
			assertEquals(List.of(NetworkErrorType.ADDRESS_IN_USE), errors);
		}
	}
	
	@Test
	void startTwiceIsNoOp() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT)) {
			server.start();
			TcpServer first = server.tcpServer().orElseThrow();
			
			assertDoesNotThrow(server::start);
			assertSame(first, server.tcpServer().orElseThrow());
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void stopBeforeStartIsNoOp() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT)) {
			assertDoesNotThrow(server::stop);
			assertFalse(server.isRunning());
			assertTrue(server.tcpServer().isEmpty());
		}
	}
	
	@Test
	void stopTwiceIsNoOp() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT)) {
			server.start();
			
			assertDoesNotThrow(server::stop);
			assertDoesNotThrow(server::stop);
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void stopStopsEveryServer() throws Exception {
		try (HybridServer server = HybridServer.create(EPHEMERAL, UdpServerConfig.DEFAULT, TcpServerConfig.DEFAULT, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "data".getBytes());
				TcpServer tcpServer = server.tcpServer().orElseThrow();
				awaitCondition(() -> tcpServer.getClientCount() == 1);
				
				server.stop();
				assertFalse(server.isRunning());
				assertFalse(server.udpServer().orElseThrow().isRunning());
				assertFalse(tcpServer.isRunning());
				assertClosedByServer(socket);
			}
		}
	}
	
	@Test
	void stopTcpOnlySkipsMissingServers() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT)) {
			server.start();
			
			assertDoesNotThrow(server::stop);
			assertFalse(server.tcpServer().orElseThrow().isRunning());
		}
	}
	
	@Test
	void closeDelegatesToStop() {
		HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT);
		server.start();
		
		server.close();
		assertFalse(server.isRunning());
		assertFalse(server.tcpServer().orElseThrow().isRunning());
	}
	
	@Test
	void closeBeforeStartIsNoOp() {
		HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT);
		assertDoesNotThrow(server::close);
	}
	
	@Test
	void restartAfterStop() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT)) {
			server.start();
			TcpServer first = server.tcpServer().orElseThrow();
			server.stop();
			
			server.start();
			assertTrue(server.isRunning());
			assertNotSame(first, server.tcpServer().orElseThrow());
		}
	}
	
	@Test
	void isRunningFalseBeforeStart() {
		try (HybridServer server = HybridServer.create(EPHEMERAL, UdpServerConfig.DEFAULT, TcpServerConfig.DEFAULT, sslConfig())) {
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void isRunningTrueForUdpOnly() {
		try (HybridServer server = HybridServer.createUdp(EPHEMERAL, UdpServerConfig.DEFAULT)) {
			server.start();
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void isRunningFalseWhenUdpServerStoppedExternally() {
		try (HybridServer server = HybridServer.create(EPHEMERAL, UdpServerConfig.DEFAULT, TcpServerConfig.DEFAULT, null)) {
			server.start();
			assertTrue(server.isRunning());
			
			server.udpServer().orElseThrow().stop();
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void isRunningTrueForTcpOnly() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT)) {
			server.start();
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void isRunningFalseWhenTcpServerStoppedExternally() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT)) {
			server.start();
			
			server.tcpServer().orElseThrow().stop();
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void isRunningTrueForSslOnly() {
		try (HybridServer server = HybridServer.createSsl(EPHEMERAL, sslConfig())) {
			server.start();
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void isRunningFalseWhenSslServerStoppedExternally() {
		try (HybridServer server = HybridServer.createSsl(EPHEMERAL, sslConfig())) {
			server.start();
			
			server.sslServer().orElseThrow().stop();
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void isRunningOnSharedPortIgnoresSslServer() {
		try (HybridServer server = shared(TcpServerConfig.DEFAULT, sslConfig())) {
			server.start();
			
			assertTrue(server.isRunning());
			assertFalse(server.sslServer().orElseThrow().isRunning());
		}
	}
	
	@Test
	void gettersAfterStartUdpOnly() {
		try (HybridServer server = HybridServer.createUdp(EPHEMERAL, UdpServerConfig.DEFAULT)) {
			server.start();
			
			assertTrue(server.udpServer().isPresent());
			assertTrue(server.tcpServer().isEmpty());
			assertTrue(server.sslServer().isEmpty());
		}
	}
	
	@Test
	void gettersAfterStartTcpOnly() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT)) {
			server.start();
			
			assertTrue(server.udpServer().isEmpty());
			assertTrue(server.tcpServer().isPresent());
			assertTrue(server.sslServer().isEmpty());
		}
	}
	
	@Test
	void gettersAfterStartSslOnly() {
		try (HybridServer server = HybridServer.createSsl(EPHEMERAL, sslConfig())) {
			server.start();
			
			assertTrue(server.udpServer().isEmpty());
			assertTrue(server.tcpServer().isEmpty());
			assertTrue(server.sslServer().orElseThrow().isRunning());
		}
	}
	
	@Test
	void gettersAfterStartShared() {
		try (HybridServer server = shared(TcpServerConfig.DEFAULT, sslConfig())) {
			server.start();
			
			assertTrue(server.udpServer().isEmpty());
			assertTrue(server.tcpServer().orElseThrow().isRunning());
			assertFalse(server.sslServer().orElseThrow().isRunning());
		}
	}
	
	@Test
	void boundEndpointBeforeStartReturnsBindEndpoint() {
		try (HybridServer server = HybridServer.create(EPHEMERAL, UdpServerConfig.DEFAULT, TcpServerConfig.DEFAULT, sslConfig())) {
			assertEquals(EPHEMERAL, server.boundEndpoint());
			assertEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void boundEndpointTcpOnlyReturnsTcpPort() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT)) {
			server.start();
			
			assertNotEquals(0, server.boundEndpoint().port());
			assertEquals(server.tcpServer().orElseThrow().boundEndpoint().port(), server.boundEndpoint().port());
		}
	}
	
	@Test
	void boundEndpointSslOnlyReturnsSslPort() {
		try (HybridServer server = HybridServer.createSsl(EPHEMERAL, sslConfig())) {
			server.start();
			
			assertNotEquals(0, server.boundEndpoint().port());
			assertEquals(server.sslServer().orElseThrow().boundEndpoint().port(), server.boundEndpoint().port());
		}
	}
	
	@Test
	void boundEndpointUdpOnlyReturnsUdpPort() {
		try (HybridServer server = HybridServer.createUdp(EPHEMERAL, UdpServerConfig.DEFAULT)) {
			server.start();
			
			assertNotEquals(0, server.boundEndpoint().port());
			assertEquals(server.udpServer().orElseThrow().boundEndpoint().port(), server.boundEndpoint().port());
		}
	}
	
	@Test
	void udpSharesEphemeralPortWithTcp() {
		try (HybridServer server = HybridServer.create(EPHEMERAL, UdpServerConfig.DEFAULT, TcpServerConfig.DEFAULT, null)) {
			server.start();
			
			assertTrue(server.isRunning());
			assertEquals(server.tcpServer().orElseThrow().boundEndpoint().port(), server.udpServer().orElseThrow().boundEndpoint().port());
		}
	}
	
	@Test
	void udpSharesEphemeralPortWithSsl() {
		try (HybridServer server = HybridServer.create(EPHEMERAL, UdpServerConfig.DEFAULT, null, sslConfig())) {
			server.start();
			
			assertTrue(server.isRunning());
			assertEquals(server.sslServer().orElseThrow().boundEndpoint().port(), server.udpServer().orElseThrow().boundEndpoint().port());
		}
	}
	
	@Test
	void boundEndpointAfterStopKeepsLastServerPort() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT)) {
			server.start();
			int port = server.boundEndpoint().port();
			
			server.stop();
			assertEquals(port, server.boundEndpoint().port());
		}
	}
	
	@Test
	void sharedPortServesTlsClientBySslServer() throws Exception {
		AtomicInteger tcpMessages = new AtomicInteger();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onMessage((server, connection, data) -> tcpMessages.incrementAndGet()).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).onMessage((server, connection, data) -> echo(connection, data)).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig)) {
			server.start();
			
			try (SSLSocket socket = connectTls(clientContext, server)) {
				writeFrame(socket, "tls".getBytes());
				assertArrayEquals("tls".getBytes(), readFrame(socket));
			}
			assertEquals(0, tcpMessages.get());
		}
	}
	
	@Test
	void sharedPortServesPlainClientAsTcp() throws Exception {
		CountDownLatch received = new CountDownLatch(1);
		AtomicReference<byte[]> payload = new AtomicReference<>();
		AtomicInteger sslConnects = new AtomicInteger();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onMessage((server, connection, data) -> {
			payload.set(data);
			received.countDown();
		}).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).onClientConnect((connection, local, remote, timestamp) -> sslConnects.incrementAndGet()).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig)) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "plain".getBytes());
				assertTrue(received.await(5, TimeUnit.SECONDS));
			}
			assertArrayEquals("plain".getBytes(), payload.get());
			assertEquals(0, sslConnects.get());
		}
	}
	
	@Test
	void sharedPortServesSilentClientAsTcpAfterTimeout() throws Exception {
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onConnection((server, connection) -> connection.send("hello".getBytes())).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			long start = System.nanoTime();
			try (Socket socket = connectPlain(server)) {
				assertArrayEquals("hello".getBytes(), readFrame(socket));
				long elapsed = System.nanoTime() - start;
				assertTrue(elapsed >= DETECTION.toNanos(), "Greeting arrived after " + Duration.ofNanos(elapsed).toMillis() + " ms");
			}
		}
	}
	
	@Test
	void sharedPortClosedBeforeFirstByteIsServedAsPlain() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		AtomicInteger connects = new AtomicInteger();
		CountDownLatch disconnected = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder()
			.onClientConnect((connection, local, remote, timestamp) -> connects.incrementAndGet())
			.onClientDisconnect((connection, local, remote, timestamp) -> disconnected.countDown())
			.onError(recorder(errors))
			.build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).onError(recorder(errors)).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig)) {
			server.start();
			
			connectPlain(server).close();
			assertTrue(disconnected.await(5, TimeUnit.SECONDS));
			assertEquals(1, connects.get());
			assertTrue(errors.isEmpty());
		}
	}
	
	@Test
	void sharedPortPeekDoesNotConsumeFirstByte() throws Exception {
		CountDownLatch received = new CountDownLatch(1);
		AtomicReference<byte[]> payload = new AtomicReference<>();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onConnection((server, connection) -> {
			payload.set(connection.getInputStream().readNBytes(3));
			received.countDown();
		}).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				socket.getOutputStream().write(new byte[] { 0x41, 0x42, 0x43 });
				socket.getOutputStream().flush();
				assertTrue(received.await(5, TimeUnit.SECONDS));
			}
			assertArrayEquals(new byte[] { 0x41, 0x42, 0x43 }, payload.get());
		}
	}
	
	@Test
	void sharedPortFramedMessageStartingWith0x16IsPlain() throws Exception {
		CountDownLatch received = new CountDownLatch(1);
		AtomicReference<byte[]> payload = new AtomicReference<>();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onMessage((server, connection, data) -> {
			payload.set(data);
			received.countDown();
		}).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, new byte[] { 0x16, 0x03, 0x01 });
				assertTrue(received.await(5, TimeUnit.SECONDS));
			}
			assertArrayEquals(new byte[] { 0x16, 0x03, 0x01 }, payload.get());
		}
	}
	
	@Test
	void sharedPortUnframedMessageStartingWith0x16IsTreatedAsTls() throws Exception {
		AtomicInteger tcpMessages = new AtomicInteger();
		List<NetworkErrorType> sslErrors = new CopyOnWriteArrayList<>();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().framing(false).onMessage((server, connection, data) -> tcpMessages.incrementAndGet()).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).framing(false).onError(recorder(sslErrors)).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig)) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				socket.getOutputStream().write(new byte[] { 0x16, 0x03, 0x03, 0x00, 0x04, 0x01, 0x00, 0x00, 0x00 });
				socket.getOutputStream().flush();
				socket.shutdownOutput();
				awaitCondition(() -> !sslErrors.isEmpty());
			}
			assertEquals(0, tcpMessages.get());
		}
	}
	
	@Test
	void sharedPortTlsHandshakeFailureReportedToSslErrorHandler() throws Exception {
		List<NetworkErrorType> tcpErrors = new CopyOnWriteArrayList<>();
		List<NetworkErrorType> sslErrors = new CopyOnWriteArrayList<>();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onError(recorder(tcpErrors)).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).clientAuth(SslClientAuth.REQUIRED).onError(recorder(sslErrors)).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig)) {
			server.start();
			
			try (SSLSocket socket = (SSLSocket) trustOnlyClientContext.getSocketFactory().createSocket("127.0.0.1", server.boundEndpoint().port())) {
				socket.setSoTimeout(5000);
				assertThrows(IOException.class, () -> {
					socket.startHandshake();
					socket.getInputStream().read();
				});
			}
			awaitCondition(() -> !sslErrors.isEmpty());
			assertTrue(tcpErrors.isEmpty());
		}
	}
	
	@Test
	void sharedPortTlsClientUsesSslReadTimeout() throws Exception {
		CountDownLatch disconnected = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder().clientReadTimeout(Duration.ZERO).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext)
			.clientReadTimeout(Duration.ofMillis(300))
			.onClientDisconnect((connection, local, remote, timestamp) -> disconnected.countDown())
			.build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig)) {
			server.start();
			
			try (SSLSocket ignored = connectTls(clientContext, server)) {
				assertTrue(disconnected.await(5, TimeUnit.SECONDS));
			}
		}
	}
	
	@Test
	void servePlainWithoutConnectHandler() throws Exception {
		CountDownLatch received = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onMessage((server, connection, data) -> received.countDown()).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "data".getBytes());
				assertTrue(received.await(5, TimeUnit.SECONDS));
			}
		}
	}
	
	@Test
	void servePlainCallsConnectHandlerAfterDetection() throws Exception {
		CountDownLatch connected = new CountDownLatch(1);
		AtomicInteger connects = new AtomicInteger();
		AtomicReference<Connection> connectionRef = new AtomicReference<>();
		AtomicReference<IpEndpoint> localRef = new AtomicReference<>();
		AtomicReference<IpEndpoint> remoteRef = new AtomicReference<>();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onClientConnect((connection, local, remote, timestamp) -> {
			connectionRef.set(connection);
			localRef.set((IpEndpoint) local);
			remoteRef.set((IpEndpoint) remote);
			connects.incrementAndGet();
			connected.countDown();
		}).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "data".getBytes());
				assertTrue(connected.await(5, TimeUnit.SECONDS));
				
				assertEquals(1, connects.get());
				assertInstanceOf(TcpConnection.class, connectionRef.get());
				assertEquals(server.boundEndpoint().port(), localRef.get().port());
				assertEquals(socket.getLocalPort(), remoteRef.get().port());
			}
		}
	}
	
	@Test
	void servePlainTlsClientDoesNotFireTcpConnectHandler() throws Exception {
		AtomicInteger tcpEvents = new AtomicInteger();
		CountDownLatch sslDisconnected = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder()
			.onClientConnect((connection, local, remote, timestamp) -> tcpEvents.incrementAndGet())
			.onClientDisconnect((connection, local, remote, timestamp) -> tcpEvents.incrementAndGet())
			.build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext)
			.onMessage((server, connection, data) -> echo(connection, data))
			.onClientDisconnect((connection, local, remote, timestamp) -> sslDisconnected.countDown())
			.build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig)) {
			server.start();
			
			try (SSLSocket socket = connectTls(clientContext, server)) {
				writeFrame(socket, "tls".getBytes());
				assertArrayEquals("tls".getBytes(), readFrame(socket));
			}
			assertTrue(sslDisconnected.await(5, TimeUnit.SECONDS));
			assertEquals(0, tcpEvents.get());
		}
	}
	
	@Test
	void servePlainWithConnectionHandler() throws Exception {
		AtomicInteger calls = new AtomicInteger();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onConnection((server, connection) -> {
			calls.incrementAndGet();
			connection.send(connection.receive());
		}).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "handler".getBytes());
				assertArrayEquals("handler".getBytes(), readFrame(socket));
				assertClosedByServer(socket);
			}
			assertEquals(1, calls.get());
		}
	}
	
	@Test
	void servePlainReceiveLoopDispatchesMessages() throws Exception {
		CountDownLatch received = new CountDownLatch(3);
		List<String> messages = new CopyOnWriteArrayList<>();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onMessage((server, connection, data) -> {
			messages.add(new String(data));
			received.countDown();
		}).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "one".getBytes());
				writeFrame(socket, "two".getBytes());
				writeFrame(socket, "three".getBytes());
				assertTrue(received.await(5, TimeUnit.SECONDS));
			}
			assertEquals(List.of("one", "two", "three"), messages);
		}
	}
	
	@Test
	void servePlainWithoutMessageHandler() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		CountDownLatch disconnected = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder()
			.onClientDisconnect((connection, local, remote, timestamp) -> disconnected.countDown())
			.onError(recorder(errors))
			.build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "ignored".getBytes());
			}
			assertTrue(disconnected.await(5, TimeUnit.SECONDS));
			assertTrue(errors.isEmpty());
		}
	}
	
	@Test
	void servePlainMessageHandlerErrorReportedAndLoopContinues() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		CountDownLatch second = new CountDownLatch(1);
		AtomicInteger calls = new AtomicInteger();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onError(recorder(errors)).onMessage((server, connection, data) -> {
			if (calls.incrementAndGet() == 1) {
				throw new IllegalStateException("First message fails");
			}
			second.countDown();
		}).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "first".getBytes());
				writeFrame(socket, "second".getBytes());
				assertTrue(second.await(5, TimeUnit.SECONDS));
			}
			assertEquals(List.of(NetworkErrorType.IO_ERROR), errors);
		}
	}
	
	@Test
	void servePlainEmptyReceiveEndsLoop() throws Exception {
		AtomicInteger disconnects = new AtomicInteger();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onClientDisconnect((connection, local, remote, timestamp) -> disconnects.incrementAndGet()).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "data".getBytes());
			}
			awaitCondition(() -> disconnects.get() == 1);
			Thread.sleep(200);
			assertEquals(1, disconnects.get());
		}
	}
	
	@Test
	void servePlainWithoutDisconnectHandler() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		CountDownLatch received = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onError(recorder(errors)).onMessage((server, connection, data) -> received.countDown()).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "data".getBytes());
				assertTrue(received.await(5, TimeUnit.SECONDS));
			}
			TcpServer tcpServer = server.tcpServer().orElseThrow();
			awaitCondition(() -> tcpServer.getClientCount() == 0);
			assertTrue(errors.isEmpty());
		}
	}
	
	@Test
	void servePlainDisconnectHandlerSkippedWhenConnectionClosed() throws Exception {
		AtomicInteger disconnects = new AtomicInteger();
		TcpServerConfig tcpConfig = TcpServerConfig.builder()
			.onConnection((server, connection) -> connection.close())
			.onClientDisconnect((connection, local, remote, timestamp) -> disconnects.incrementAndGet())
			.build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "data".getBytes());
				assertClosedByServer(socket);
			}
			TcpServer tcpServer = server.tcpServer().orElseThrow();
			awaitCondition(() -> tcpServer.getClientCount() == 0);
			assertEquals(0, disconnects.get());
		}
	}
	
	@Test
	void servePlainDisconnectHandlerExceptionIsSwallowed() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		CountDownLatch disconnected = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onError(recorder(errors)).onClientDisconnect((connection, local, remote, timestamp) -> {
			disconnected.countDown();
			throw new IllegalStateException("Disconnect handler fails");
		}).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "data".getBytes());
			}
			assertTrue(disconnected.await(5, TimeUnit.SECONDS));
			TcpServer tcpServer = server.tcpServer().orElseThrow();
			awaitCondition(() -> tcpServer.getClientCount() == 0);
			assertTrue(errors.isEmpty());
		}
	}
	
	@Test
	void servePlainLoopEndsWhenServerStops() throws Exception {
		CountDownLatch received = new CountDownLatch(1);
		AtomicInteger disconnects = new AtomicInteger();
		TcpServerConfig tcpConfig = TcpServerConfig.builder()
			.onMessage((server, connection, data) -> received.countDown())
			.onClientDisconnect((connection, local, remote, timestamp) -> disconnects.incrementAndGet())
			.build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "data".getBytes());
				assertTrue(received.await(5, TimeUnit.SECONDS));
				
				server.stop();
				assertClosedByServer(socket);
			}
			assertTrue(disconnects.get() <= 1);
		}
	}
	
	@Test
	void servePlainConnectionHandlerFailureReportedToTcpErrorHandler() throws Exception {
		List<NetworkErrorType> errors = new CopyOnWriteArrayList<>();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onError(recorder(errors)).onConnection((server, connection) -> {
			throw new IllegalStateException("Connection handler fails");
		}).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				writeFrame(socket, "data".getBytes());
				awaitCondition(() -> !errors.isEmpty());
			}
			assertEquals(List.of(NetworkErrorType.IO_ERROR), errors);
		}
	}
	
	@Test
	void stopDuringDetectionEndsPendingConnection() throws Exception {
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, TcpServerConfig.DEFAULT, sslConfig(), Duration.ofSeconds(10))) {
			server.start();
			
			try (Socket socket = connectPlain(server)) {
				TcpServer tcpServer = server.tcpServer().orElseThrow();
				awaitCondition(() -> tcpServer.getClientCount() == 1);
				
				long start = System.nanoTime();
				server.stop();
				assertClosedByServer(socket);
				assertTrue(System.nanoTime() - start < TimeUnit.SECONDS.toNanos(8));
			}
		}
	}
	
	@Test
	void implementsNetworkServer() {
		try (HybridServer server = HybridServer.createTcp(EPHEMERAL, TcpServerConfig.DEFAULT)) {
			assertInstanceOf(NetworkServer.class, server);
		}
	}
	
	@Test
	void defaultDetectionTimeoutIsOneSecond() {
		assertEquals(Duration.ofSeconds(1), HybridServer.DEFAULT_DETECTION_TIMEOUT);
	}
	
	@Test
	void udpOnlyServerEchoesDatagram() throws Exception {
		UdpServerConfig config = UdpServerConfig.builder().onMessage((server, datagram, data) -> {
			try {
				server.send(datagram.endpoint(), data);
			} catch (NetworkConnectionException _) {}
		}).build();
		
		try (HybridServer server = HybridServer.createUdp(EPHEMERAL, config);
			 DatagramSocket client = new DatagramSocket(0, InetAddress.getLoopbackAddress())) {
			server.start();
			client.setSoTimeout(5000);
			
			byte[] data = "udp".getBytes();
			client.send(new DatagramPacket(data, data.length, InetAddress.getLoopbackAddress(), server.boundEndpoint().port()));
			DatagramPacket response = new DatagramPacket(new byte[64], 64);
			client.receive(response);
			assertArrayEquals(data, Arrays.copyOf(response.getData(), response.getLength()));
		}
	}
	
	@Test
	void tcpOnlyServerDoesNotDetectProtocol() throws Exception {
		CountDownLatch connected = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onClientConnect((connection, local, remote, timestamp) -> connected.countDown()).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, null, tcpConfig, null, Duration.ofSeconds(10))) {
			server.start();
			
			try (Socket ignored = connectPlain(server)) {
				assertTrue(connected.await(2, TimeUnit.SECONDS));
			}
		}
	}
	
	@Test
	void sslOnlyServerServesTlsClient() throws Exception {
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).onMessage((server, connection, data) -> echo(connection, data)).build();
		
		try (HybridServer server = HybridServer.createSsl(EPHEMERAL, sslConfig)) {
			server.start();
			
			try (SSLSocket socket = connectTls(clientContext, server)) {
				writeFrame(socket, "tls".getBytes());
				assertArrayEquals("tls".getBytes(), readFrame(socket));
			}
			assertTrue(server.tcpServer().isEmpty());
		}
	}
	
	@Test
	void sharedPortPlainAndTlsClientsConcurrently() throws Exception {
		Set<String> plainMessages = ConcurrentHashMap.newKeySet();
		Set<String> tlsMessages = ConcurrentHashMap.newKeySet();
		CountDownLatch received = new CountDownLatch(10);
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onMessage((server, connection, data) -> {
			plainMessages.add(new String(data));
			received.countDown();
		}).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).onMessage((server, connection, data) -> {
			tlsMessages.add(new String(data));
			received.countDown();
		}).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig)) {
			server.start();
			
			CountDownLatch done = new CountDownLatch(1);
			List<Thread> clients = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				String plain = "plain-" + i;
				String tls = "tls-" + i;
				clients.add(Thread.ofVirtual().start(() -> {
					try (Socket socket = connectPlain(server)) {
						writeFrame(socket, plain.getBytes());
						done.await();
					} catch (Exception _) {}
				}));
				clients.add(Thread.ofVirtual().start(() -> {
					try (SSLSocket socket = connectTls(clientContext, server)) {
						writeFrame(socket, tls.getBytes());
						done.await();
					} catch (Exception _) {}
				}));
			}
			
			assertTrue(received.await(10, TimeUnit.SECONDS));
			done.countDown();
			for (Thread client : clients) {
				client.join(5000);
			}
			assertEquals(Set.of("plain-0", "plain-1", "plain-2", "plain-3", "plain-4"), plainMessages);
			assertEquals(Set.of("tls-0", "tls-1", "tls-2", "tls-3", "tls-4"), tlsMessages);
		}
	}
	
	@Test
	void sharedPortClientCounts() throws Exception {
		CountDownLatch plainReceived = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onMessage((server, connection, data) -> plainReceived.countDown()).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket plain = connectPlain(server); SSLSocket tls = connectTls(clientContext, server)) {
				writeFrame(plain, "plain".getBytes());
				assertTrue(plainReceived.await(5, TimeUnit.SECONDS));
				SslServer sslServer = server.sslServer().orElseThrow();
				awaitCondition(() -> sslServer.getClientCount() == 1);
				
				assertEquals(1, sslServer.getClientCount());
				assertEquals(1, server.tcpServer().orElseThrow().getClientCount());
			}
		}
	}
	
	@Test
	void sharedPortSslBroadcastReachesOnlyTlsClients() throws Exception {
		CountDownLatch plainReceived = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onMessage((server, connection, data) -> plainReceived.countDown()).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig())) {
			server.start();
			
			try (Socket plain = connectPlain(server); SSLSocket tls = connectTls(clientContext, server)) {
				writeFrame(plain, "plain".getBytes());
				assertTrue(plainReceived.await(5, TimeUnit.SECONDS));
				SslServer sslServer = server.sslServer().orElseThrow();
				awaitCondition(() -> sslServer.getClientCount() == 1);
				
				sslServer.broadcast("broadcast".getBytes());
				assertArrayEquals("broadcast".getBytes(), readFrame(tls));
				plain.setSoTimeout(500);
				assertThrows(SocketTimeoutException.class, () -> plain.getInputStream().read());
			}
		}
	}
	
	@Test
	void stopClosesServedTlsConnections() throws Exception {
		try (HybridServer server = shared(TcpServerConfig.DEFAULT, sslConfig())) {
			server.start();
			
			try (SSLSocket tls = connectTls(clientContext, server)) {
				SslServer sslServer = server.sslServer().orElseThrow();
				awaitCondition(() -> sslServer.getClientCount() == 1);
				
				server.stop();
				assertClosedByServer(tls);
				assertEquals(0, sslServer.getClientCount());
			}
		}
	}
	
	@Test
	void allProtocolsOnOneEphemeralPort() throws Exception {
		UdpServerConfig udpConfig = UdpServerConfig.builder().onMessage((server, datagram, data) -> {
			try {
				server.send(datagram.endpoint(), data);
			} catch (NetworkConnectionException _) {}
		}).build();
		TcpServerConfig tcpConfig = TcpServerConfig.builder().onMessage((server, connection, data) -> echo(connection, data)).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).onMessage((server, connection, data) -> echo(connection, data)).build();
		
		try (HybridServer server = HybridServer.create(EPHEMERAL, udpConfig, tcpConfig, sslConfig, DETECTION)) {
			server.start();
			int port = server.boundEndpoint().port();
			
			try (Socket plain = connectPlain(server); SSLSocket tls = connectTls(clientContext, server);
				 DatagramSocket udp = new DatagramSocket(0, InetAddress.getLoopbackAddress())) {
				writeFrame(plain, "plain".getBytes());
				assertArrayEquals("plain".getBytes(), readFrame(plain));
				writeFrame(tls, "tls".getBytes());
				assertArrayEquals("tls".getBytes(), readFrame(tls));
				
				udp.setSoTimeout(5000);
				udp.send(new DatagramPacket("udp".getBytes(), 3, InetAddress.getLoopbackAddress(), port));
				DatagramPacket response = new DatagramPacket(new byte[64], 64);
				udp.receive(response);
				assertEquals("udp", new String(response.getData(), 0, response.getLength()));
			}
		}
	}
	
	@Test
	void sharedBacklogUsesLargerBacklog() throws Exception {
		CountDownLatch release = new CountDownLatch(1);
		TcpServerConfig tcpConfig = TcpServerConfig.builder().backlog(1).onConnection((server, connection) -> release.await()).build();
		SslServerConfig sslConfig = SslServerConfig.builder(serverContext).backlog(50).build();
		
		try (HybridServer server = shared(tcpConfig, sslConfig)) {
			server.start();
			
			List<Socket> sockets = new ArrayList<>();
			try {
				for (int i = 0; i < 20; i++) {
					sockets.add(connectPlain(server));
				}
				assertEquals(20, sockets.size());
				assertTrue(sockets.stream().allMatch(Socket::isConnected));
			} finally {
				release.countDown();
				for (Socket socket : sockets) {
					socket.close();
				}
			}
		}
	}
}
