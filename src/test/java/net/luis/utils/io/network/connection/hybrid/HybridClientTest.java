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
import net.luis.utils.io.network.connection.Connection;
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.ssl.*;
import net.luis.utils.io.network.connection.tcp.*;
import net.luis.utils.io.network.connection.udp.UdpClientConfig;
import net.luis.utils.io.network.connection.udp.UdpDatagram;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link HybridClient}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class HybridClientTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	
	private static SSLContext serverContext;
	private static SSLContext clientContext;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverContext = SslTestContext.serverContext();
		clientContext = SslTestContext.clientContext();
	}
	
	private static void echo(@NonNull Connection connection, byte @NonNull [] data) {
		try {
			connection.send(data);
		} catch (NetworkConnectionException _) {}
	}
	
	private static @NonNull TcpServerConfig tcpEchoConfig() {
		return TcpServerConfig.builder().onMessage((server, connection, data) -> echo(connection, data)).build();
	}
	
	private static @NonNull SslServerConfig sslEchoConfig() {
		return SslServerConfig.builder(serverContext).onMessage((server, connection, data) -> echo(connection, data)).build();
	}
	
	private static @NonNull TcpClientConfig tcpClientConfig() {
		return TcpClientConfig.builder().readTimeout(Duration.ofSeconds(5)).build();
	}
	
	private static @NonNull SslClientConfig sslClientConfig() {
		return SslClientConfig.builder().sslContext(clientContext).readTimeout(Duration.ofSeconds(5)).build();
	}
	
	private static @NonNull UdpClientConfig udpClientConfig() {
		return UdpClientConfig.builder().receiveTimeout(Duration.ofSeconds(5)).build();
	}
	
	private static @NonNull IpEndpoint loopback(int port) {
		return new IpEndpoint(Ipv4Address.LOOPBACK, port);
	}
	
	private static byte @NonNull [] filled(int length, byte value) {
		byte[] data = new byte[length];
		java.util.Arrays.fill(data, value);
		return data;
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
	
	private static @NonNull Thread startUdpEchoPeer(@NonNull DatagramSocket peer) {
		Thread thread = new Thread(() -> {
			try {
				DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
				peer.receive(packet);
				peer.send(new DatagramPacket(packet.getData(), packet.getLength(), packet.getSocketAddress()));
			} catch (Exception _) {}
		});
		thread.setDaemon(true);
		thread.start();
		return thread;
	}
	
	@Test
	void createUdpReturnsInactiveClient() {
		try (HybridClient<UdpDatagram> client = HybridClient.createUdp(UdpClientConfig.DEFAULT)) {
			assertNotNull(client);
			assertFalse(client.isActive());
			assertTrue(client.localEndpoint().isEmpty());
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void createTcpReturnsInactiveClient() {
		try (HybridClient<byte[]> client = HybridClient.createTcp(TcpClientConfig.DEFAULT)) {
			assertNotNull(client);
			assertFalse(client.isActive());
			assertTrue(client.localEndpoint().isEmpty());
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void createSslReturnsInactiveClient() {
		try (HybridClient<byte[]> client = HybridClient.createSsl(SslClientConfig.builder().sslContext(clientContext).build())) {
			assertNotNull(client);
			assertFalse(client.isActive());
			assertTrue(client.localEndpoint().isEmpty());
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void createUdpWithNullConfig() {
		assertThrows(NullPointerException.class, () -> HybridClient.createUdp(null));
	}
	
	@Test
	void createTcpWithNullConfig() {
		assertThrows(NullPointerException.class, () -> HybridClient.createTcp(null));
	}
	
	@Test
	void createSslWithNullConfig() {
		assertThrows(NullPointerException.class, () -> HybridClient.createSsl(null));
	}
	
	@Test
	void connectTcpWithNullEndpoint() {
		try (HybridClient<byte[]> client = HybridClient.createTcp(TcpClientConfig.DEFAULT)) {
			assertThrows(NullPointerException.class, () -> client.connect(null));
		}
	}
	
	@Test
	void connectSslWithNullEndpoint() {
		try (HybridClient<byte[]> client = HybridClient.createSsl(sslClientConfig())) {
			assertThrows(NullPointerException.class, () -> client.connect(null));
		}
	}
	
	@Test
	void connectUdpWithNullEndpoint() {
		try (HybridClient<UdpDatagram> client = HybridClient.createUdp(UdpClientConfig.DEFAULT)) {
			assertThrows(NullPointerException.class, () -> client.connect(null));
		}
	}
	
	@Test
	void connectTcpToClosedPortThrows() throws Exception {
		try (Socket placeholder = new Socket(); HybridClient<byte[]> client = HybridClient.createTcp(tcpClientConfig())) {
			placeholder.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
			
			assertThrows(NetworkConnectionException.class, () -> client.connect(loopback(placeholder.getLocalPort())));
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void connectSslToNonTlsPeerThrows() throws Exception {
		try (ServerSocket peer = new ServerSocket(0, 0, InetAddress.getLoopbackAddress())) {
			Thread acceptor = new Thread(() -> {
				try (Socket accepted = peer.accept()) {
					accepted.getOutputStream().write("NOT-TLS\n".getBytes());
					accepted.getOutputStream().flush();
					Thread.sleep(5000);
				} catch (Exception _) {}
			});
			acceptor.setDaemon(true);
			acceptor.start();
			
			try (HybridClient<byte[]> client = HybridClient.createSsl(sslClientConfig())) {
				assertThrows(NetworkConnectionException.class, () -> client.connect(loopback(peer.getLocalPort())));
			}
			acceptor.interrupt();
		}
	}
	
	@Test
	void connectUdpToAddressInUseThrows() throws Exception {
		try (DatagramSocket occupied = new DatagramSocket(0, InetAddress.getLoopbackAddress());
			 HybridClient<UdpDatagram> client = HybridClient.createUdp(UdpClientConfig.builder().reuseAddress(false).build())) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(loopback(occupied.getLocalPort())));
			assertEquals(NetworkErrorType.ADDRESS_IN_USE, exception.errorType());
		}
	}
	
	@Test
	void connectTcpTwiceThrows() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, tcpEchoConfig());
			 HybridClient<byte[]> client = HybridClient.createTcp(tcpClientConfig())) {
			client.connect(server.boundEndpoint());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(server.boundEndpoint()));
			assertEquals(NetworkErrorType.ALREADY_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void connectUdpTwiceThrows() throws Exception {
		try (HybridClient<UdpDatagram> client = HybridClient.createUdp(UdpClientConfig.DEFAULT)) {
			client.connect(EPHEMERAL);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(EPHEMERAL));
			assertEquals(NetworkErrorType.ALREADY_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendTcpBeforeConnectThrows() {
		try (HybridClient<byte[]> client = HybridClient.createTcp(TcpClientConfig.DEFAULT)) {
			assertThrows(NetworkConnectionException.class, () -> client.send("data".getBytes()));
		}
	}
	
	@Test
	void receiveTcpBeforeConnectThrows() {
		try (HybridClient<byte[]> client = HybridClient.createTcp(TcpClientConfig.DEFAULT)) {
			assertThrows(NetworkConnectionException.class, client::receive);
		}
	}
	
	@Test
	void receiveSslBeforeConnectThrows() {
		try (HybridClient<byte[]> client = HybridClient.createSsl(sslClientConfig())) {
			assertThrows(NetworkConnectionException.class, client::receive);
		}
	}
	
	@Test
	void receiveUdpBeforeConnectThrows() {
		try (HybridClient<UdpDatagram> client = HybridClient.createUdp(UdpClientConfig.DEFAULT)) {
			assertThrows(NetworkConnectionException.class, client::receive);
		}
	}
	
	@Test
	void sendTcpWithNullData() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, tcpEchoConfig());
			 HybridClient<byte[]> client = HybridClient.createTcp(tcpClientConfig())) {
			client.connect(server.boundEndpoint());
			
			assertThrows(NullPointerException.class, () -> client.send(null));
		}
	}
	
	@Test
	void sendUdpWithNullDatagram() throws Exception {
		try (HybridClient<UdpDatagram> client = HybridClient.createUdp(UdpClientConfig.DEFAULT)) {
			client.connect(EPHEMERAL);
			
			assertThrows(NullPointerException.class, () -> client.send(null));
		}
	}
	
	@Test
	void receiveWithZeroMaxBytesThrows() throws Exception {
		try (HybridClient<UdpDatagram> client = HybridClient.createUdp(UdpClientConfig.DEFAULT)) {
			client.connect(EPHEMERAL);
			
			assertThrows(IllegalArgumentException.class, () -> client.receive(0));
		}
	}
	
	@Test
	void receiveWithNegativeMaxBytesThrows() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, tcpEchoConfig());
			 HybridClient<byte[]> client = HybridClient.createTcp(tcpClientConfig())) {
			client.connect(server.boundEndpoint());
			
			assertThrows(IllegalArgumentException.class, () -> client.receive(-1));
		}
	}
	
	@Test
	void connectTcpConnectsToServer() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, tcpEchoConfig());
			 HybridClient<byte[]> client = HybridClient.createTcp(tcpClientConfig())) {
			client.connect(server.boundEndpoint());
			
			assertTrue(client.isActive());
			assertEquals(server.boundEndpoint().port(), client.remoteEndpoint().orElseThrow().port());
			assertTrue(client.localEndpoint().isPresent());
		}
	}
	
	@Test
	void connectSslPerformsHandshake() throws Exception {
		CountDownLatch handshake = new CountDownLatch(1);
		SslServerConfig config = SslServerConfig.builder(serverContext).onClientConnect((connection, local, remote, timestamp) -> handshake.countDown()).build();
		
		try (SslServer server = SslServer.startOn(EPHEMERAL, config);
			 HybridClient<byte[]> client = HybridClient.createSsl(sslClientConfig())) {
			client.connect(server.boundEndpoint());
			
			assertTrue(client.isActive());
			assertTrue(handshake.await(5, TimeUnit.SECONDS));
			assertEquals(server.boundEndpoint().port(), client.remoteEndpoint().orElseThrow().port());
		}
	}
	
	@Test
	void connectUdpBindsLocalEndpoint() throws Exception {
		try (HybridClient<UdpDatagram> client = HybridClient.createUdp(UdpClientConfig.DEFAULT)) {
			client.connect(EPHEMERAL);
			
			assertTrue(client.isActive());
			assertNotEquals(0, client.localEndpoint().orElseThrow().port());
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void connectUdpBindsGivenPort() throws Exception {
		int port;
		try (DatagramSocket probe = new DatagramSocket(0, InetAddress.getLoopbackAddress())) {
			port = probe.getLocalPort();
		}
		
		try (HybridClient<UdpDatagram> client = HybridClient.createUdp(UdpClientConfig.DEFAULT)) {
			client.connect(loopback(port));
			
			IpEndpoint local = (IpEndpoint) client.localEndpoint().orElseThrow();
			assertEquals(port, local.port());
		}
	}
	
	@Test
	void isActiveFalseAfterClose() throws Exception {
		try (TcpServer tcpServer = TcpServer.startOn(EPHEMERAL, tcpEchoConfig());
			 SslServer sslServer = SslServer.startOn(EPHEMERAL, sslEchoConfig())) {
			HybridClient<byte[]> tcp = HybridClient.createTcp(tcpClientConfig());
			tcp.connect(tcpServer.boundEndpoint());
			HybridClient<byte[]> ssl = HybridClient.createSsl(sslClientConfig());
			ssl.connect(sslServer.boundEndpoint());
			HybridClient<UdpDatagram> udp = HybridClient.createUdp(UdpClientConfig.DEFAULT);
			udp.connect(EPHEMERAL);
			
			tcp.close();
			ssl.close();
			udp.close();
			assertFalse(tcp.isActive());
			assertFalse(ssl.isActive());
			assertFalse(udp.isActive());
		}
	}
	
	@Test
	void closeBeforeConnectIsNoOp() {
		HybridClient<UdpDatagram> udp = HybridClient.createUdp(UdpClientConfig.DEFAULT);
		HybridClient<byte[]> tcp = HybridClient.createTcp(TcpClientConfig.DEFAULT);
		HybridClient<byte[]> ssl = HybridClient.createSsl(sslClientConfig());
		
		assertDoesNotThrow(udp::close);
		assertDoesNotThrow(tcp::close);
		assertDoesNotThrow(ssl::close);
	}
	
	@Test
	void closeIsIdempotent() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, tcpEchoConfig())) {
			HybridClient<byte[]> client = HybridClient.createTcp(tcpClientConfig());
			client.connect(server.boundEndpoint());
			
			assertDoesNotThrow(client::close);
			assertDoesNotThrow(client::close);
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void tcpSendAndReceiveRoundTrip() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, tcpEchoConfig());
			 HybridClient<byte[]> client = HybridClient.createTcp(tcpClientConfig())) {
			client.connect(server.boundEndpoint());
			
			client.send("Hello, TCP!".getBytes());
			assertArrayEquals("Hello, TCP!".getBytes(), client.receive());
		}
	}
	
	@Test
	void sslSendAndReceiveRoundTrip() throws Exception {
		try (SslServer server = SslServer.startOn(EPHEMERAL, sslEchoConfig());
			 HybridClient<byte[]> client = HybridClient.createSsl(sslClientConfig())) {
			client.connect(server.boundEndpoint());
			
			client.send("Hello, TLS!".getBytes());
			assertArrayEquals("Hello, TLS!".getBytes(), client.receive());
		}
	}
	
	@Test
	void udpSendAndReceiveRoundTrip() throws Exception {
		try (DatagramSocket peer = new DatagramSocket(0, InetAddress.getLoopbackAddress());
			 HybridClient<UdpDatagram> client = HybridClient.createUdp(udpClientConfig())) {
			startUdpEchoPeer(peer);
			client.connect(EPHEMERAL);
			
			client.send(new UdpDatagram(loopback(peer.getLocalPort()), "Hello, UDP!".getBytes()));
			UdpDatagram echoed = client.receive();
			assertArrayEquals("Hello, UDP!".getBytes(), echoed.data());
			assertEquals(peer.getLocalPort(), echoed.endpoint().port());
		}
	}
	
	@Test
	void tcpReceiveWithMaxBytes() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, tcpEchoConfig());
			 HybridClient<byte[]> client = HybridClient.createTcp(tcpClientConfig())) {
			client.connect(server.boundEndpoint());
			
			byte[] data = filled(10, (byte) 7);
			client.send(data);
			assertArrayEquals(data, client.receive(10));
		}
	}
	
	@Test
	void implementsNetworkClient() {
		try (HybridClient<UdpDatagram> udp = HybridClient.createUdp(UdpClientConfig.DEFAULT);
			 HybridClient<byte[]> tcp = HybridClient.createTcp(TcpClientConfig.DEFAULT);
			 HybridClient<byte[]> ssl = HybridClient.createSsl(sslClientConfig())) {
			assertInstanceOf(NetworkClient.class, udp);
			assertInstanceOf(NetworkClient.class, tcp);
			assertInstanceOf(NetworkClient.class, ssl);
		}
	}
	
	@Test
	void closeClosesConnectionToServer() throws Exception {
		CountDownLatch disconnected = new CountDownLatch(1);
		TcpServerConfig config = TcpServerConfig.builder().onClientDisconnect((connection, local, remote, timestamp) -> disconnected.countDown()).build();
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, config)) {
			HybridClient<byte[]> client = HybridClient.createTcp(tcpClientConfig());
			client.connect(server.boundEndpoint());
			
			client.close();
			assertTrue(disconnected.await(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void multipleRoundTripsWithVaryingSizes() throws Exception {
		try (TcpServer tcpServer = TcpServer.startOn(EPHEMERAL, tcpEchoConfig());
			 SslServer sslServer = SslServer.startOn(EPHEMERAL, sslEchoConfig());
			 HybridClient<byte[]> tcp = HybridClient.createTcp(tcpClientConfig());
			 HybridClient<byte[]> ssl = HybridClient.createSsl(sslClientConfig())) {
			tcp.connect(tcpServer.boundEndpoint());
			ssl.connect(sslServer.boundEndpoint());
			
			for (int size : new int[] { 1, 100, 4096 }) {
				byte[] data = filled(size, (byte) size);
				tcp.send(data);
				ssl.send(data);
				assertArrayEquals(data, tcp.receive());
				assertArrayEquals(data, ssl.receive());
			}
		}
	}
	
	@Test
	void usableThroughNetworkClientInterface() throws Exception {
		try (SslServer server = SslServer.startOn(EPHEMERAL, sslEchoConfig())) {
			HybridClient<byte[]> hybrid = HybridClient.createSsl(sslClientConfig());
			hybrid.connect(server.boundEndpoint());
			
			NetworkClient<byte[]> client = hybrid;
			assertTrue(client.isActive());
			client.send("interface".getBytes());
			assertArrayEquals("interface".getBytes(), client.receive());
			client.close();
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void unframedTcpClientRoundTrip() throws Exception {
		TcpServerConfig serverConfig = TcpServerConfig.builder().framing(false).onMessage((server, connection, data) -> echo(connection, data)).build();
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, serverConfig);
			 HybridClient<byte[]> client = HybridClient.createTcp(TcpClientConfig.builder().framing(false).readTimeout(Duration.ofSeconds(5)).build())) {
			client.connect(server.boundEndpoint());
			
			byte[] data = "unframed".getBytes();
			client.send(data);
			assertArrayEquals(data, readUntil(client, data.length));
		}
	}
}
