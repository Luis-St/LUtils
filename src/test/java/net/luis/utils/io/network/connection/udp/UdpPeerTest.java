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

import net.luis.utils.io.network.HostEndpoint;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UdpPeer}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class UdpPeerTest {
	
	private static final IpEndpoint LOOPBACK = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	private static final UdpPeerConfig RECEIVING = UdpPeerConfig.builder().receiveTimeout(Duration.ofSeconds(5)).build();
	private static final UdpPeerConfig SHORT_TIMEOUT = UdpPeerConfig.builder().receiveTimeout(Duration.ofMillis(300)).build();
	
	private static UdpPeer peer() throws NetworkConnectionException {
		return UdpPeer.bindTo(LOOPBACK, RECEIVING);
	}
	
	private static UdpPeer peer(UdpPeerConfig config) throws NetworkConnectionException {
		return UdpPeer.bindTo(LOOPBACK, config);
	}
	
	private static IpEndpoint endpoint(UdpPeer peer) {
		return peer.localEndpoint().orElseThrow();
	}
	
	private static byte[] bytes(String value) {
		return value.getBytes(StandardCharsets.UTF_8);
	}
	
	private static String string(byte[] value) {
		return new String(value, StandardCharsets.UTF_8);
	}
	
	private static void awaitBlocked(Thread thread) throws InterruptedException {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
		while (thread.getState() != Thread.State.WAITING) {
			assertTrue(System.nanoTime() < deadline, "Thread did not block: " + thread.getState());
			Thread.sleep(10);
		}
	}
	
	@Test
	void bindToLoopback() throws Exception {
		try (UdpPeer peer = UdpPeer.bindTo(LOOPBACK)) {
			assertTrue(peer.isActive());
			assertFalse(peer.isConnected());
			assertEquals(Ipv4Address.LOOPBACK, endpoint(peer).address());
			assertTrue(endpoint(peer).port() > 0);
			assertSame(UdpPeerConfig.DEFAULT, peer.config());
		}
	}
	
	@Test
	void bindToWithConfig() throws Exception {
		try (UdpPeer peer = UdpPeer.bindTo(LOOPBACK, RECEIVING)) {
			assertSame(RECEIVING, peer.config());
		}
	}
	
	@Test
	void ephemeralBindsToWildcard() throws Exception {
		try (UdpPeer peer = UdpPeer.ephemeral()) {
			assertTrue(peer.isActive());
			assertTrue(endpoint(peer).port() > 0);
			assertTrue(endpoint(peer).toInetSocketAddress().getAddress().isAnyLocalAddress());
		}
	}
	
	@Test
	void ephemeralWithConfig() throws Exception {
		try (UdpPeer peer = UdpPeer.ephemeral(RECEIVING)) {
			assertSame(RECEIVING, peer.config());
		}
	}
	
	@Test
	void bindToWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> UdpPeer.bindTo(null));
		assertThrows(NullPointerException.class, () -> UdpPeer.bindTo(null, UdpPeerConfig.DEFAULT));
	}
	
	@Test
	void bindToWithNullConfig() {
		assertThrows(NullPointerException.class, () -> UdpPeer.bindTo(LOOPBACK, null));
	}
	
	@Test
	void ephemeralWithNullConfig() {
		assertThrows(NullPointerException.class, () -> UdpPeer.ephemeral(null));
	}
	
	@Test
	void bindToUnresolvedHost() {
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> UdpPeer.bindTo(new HostEndpoint("does-not-exist.invalid", 0)));
		
		assertEquals(NetworkErrorType.HOST_UNREACHABLE, exception.errorType());
	}
	
	@Test
	void bindToPortInUse() throws Exception {
		try (UdpPeer first = peer()) {
			IpEndpoint occupied = endpoint(first);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> UdpPeer.bindTo(occupied));
			assertEquals(NetworkErrorType.ADDRESS_IN_USE, exception.errorType());
			assertEquals(occupied, exception.endpoint());
		}
	}
	
	@Test
	void connectWithNull() throws Exception {
		try (UdpPeer peer = peer()) {
			assertThrows(NullPointerException.class, () -> peer.connect(null));
		}
	}
	
	@Test
	void connectClosedPeer() throws Exception {
		try (UdpPeer peer = peer(); UdpPeer other = peer()) {
			peer.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> peer.connect(endpoint(other)));
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		}
	}
	
	@Test
	void connectUnresolvedHost() throws Exception {
		try (UdpPeer peer = peer()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> peer.connect(new HostEndpoint("does-not-exist.invalid", 9)));
			
			assertEquals(NetworkErrorType.HOST_UNREACHABLE, exception.errorType());
			assertInstanceOf(UnknownHostException.class, exception.getCause());
		}
	}
	
	@Test
	void sendDatagramWithNull() throws Exception {
		try (UdpPeer peer = peer()) {
			assertThrows(NullPointerException.class, () -> peer.send((UdpDatagram) null));
		}
	}
	
	@Test
	void sendBytesWithNull() throws Exception {
		try (UdpPeer peer = peer()) {
			assertThrows(NullPointerException.class, () -> peer.send((byte[]) null));
		}
	}
	
	@Test
	void sendBytesWhenNotConnected() throws Exception {
		try (UdpPeer peer = peer()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> peer.send(bytes("data")));
			
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendToEndpointWithNullArguments() throws Exception {
		try (UdpPeer peer = peer()) {
			assertThrows(NullPointerException.class, () -> peer.send(null, bytes("data")));
			assertThrows(NullPointerException.class, () -> peer.send(endpoint(peer), null));
		}
	}
	
	@Test
	void sendTooLargeDatagram() throws Exception {
		try (UdpPeer peer = peer(UdpPeerConfig.builder().maxDatagramSize(16).build()); UdpPeer receiver = peer()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> peer.send(endpoint(receiver), new byte[17]));
			
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void sendTooLargeDatagramOnClosedPeer() throws Exception {
		try (UdpPeer peer = peer(UdpPeerConfig.builder().maxDatagramSize(16).build()); UdpPeer receiver = peer()) {
			peer.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> peer.send(endpoint(receiver), new byte[17]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void sendOnClosedPeer() throws Exception {
		try (UdpPeer peer = peer(); UdpPeer receiver = peer()) {
			peer.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> peer.send(endpoint(receiver), bytes("data")));
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		}
	}
	
	@Test
	void sendToUnresolvedHost() throws Exception {
		try (UdpPeer peer = peer()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> peer.send(new HostEndpoint("does-not-exist.invalid", 9), bytes("data")));
			
			assertEquals(NetworkErrorType.HOST_UNREACHABLE, exception.errorType());
		}
	}
	
	@Test
	void sendToOtherEndpointWhileConnected() throws Exception {
		try (UdpPeer peer = peer(); UdpPeer target = peer(); UdpPeer other = peer(SHORT_TIMEOUT)) {
			peer.connect(endpoint(target));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> peer.send(endpoint(other), bytes("data")));
			assertEquals(NetworkErrorType.ALREADY_CONNECTED, exception.errorType());
			assertThrows(NetworkTimeoutException.class, other::receive);
		}
	}
	
	@Test
	void receiveTimesOut() throws Exception {
		try (UdpPeer peer = peer(UdpPeerConfig.builder().receiveTimeout(Duration.ofMillis(200)).build())) {
			NetworkTimeoutException exception = assertThrows(NetworkTimeoutException.class, peer::receive);
			
			assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
			assertEquals(Duration.ofMillis(200), exception.timeout());
		}
	}
	
	@Test
	void bindToHostEndpoint() throws Exception {
		try (UdpPeer peer = UdpPeer.bindTo(new HostEndpoint("localhost", 0))) {
			assertTrue(peer.isActive());
			assertTrue(endpoint(peer).port() > 0);
		}
	}
	
	@Test
	void bindToWithReuseAddressAllowsSecondBind() throws Exception {
		UdpPeerConfig config = UdpPeerConfig.builder().reuseAddress(true).build();
		try (UdpPeer first = UdpPeer.bindTo(LOOPBACK, config); UdpPeer second = UdpPeer.bindTo(endpoint(first), config)) {
			assertEquals(endpoint(first).port(), endpoint(second).port());
		}
	}
	
	@Test
	void connectSetsRemoteEndpoint() throws Exception {
		try (UdpPeer peer = peer(); UdpPeer other = peer()) {
			peer.connect(endpoint(other));
			
			assertTrue(peer.isConnected());
			assertEquals(Optional.of(endpoint(other)), peer.remoteEndpoint());
		}
	}
	
	@Test
	void sendBytesWhenConnected() throws Exception {
		try (UdpPeer peer = peer(); UdpPeer other = peer()) {
			peer.connect(endpoint(other));
			
			peer.send(bytes("connected"));
			UdpDatagram datagram = other.receive().orElseThrow();
			
			assertEquals("connected", string(datagram.data()));
			assertEquals(endpoint(peer), datagram.endpoint());
		}
	}
	
	@Test
	void sendToSameEndpointWhileConnected() throws Exception {
		try (UdpPeer peer = peer(); UdpPeer other = peer()) {
			peer.connect(endpoint(other));
			
			peer.send(endpoint(other), bytes("same"));
			
			assertEquals("same", string(other.receive().orElseThrow().data()));
		}
	}
	
	@Test
	void connectedPeerIgnoresOtherSources() throws Exception {
		try (UdpPeer peer = peer(); UdpPeer target = peer(); UdpPeer stranger = peer()) {
			peer.connect(endpoint(target));
			
			stranger.send(endpoint(peer), bytes("stranger"));
			target.send(endpoint(peer), bytes("target"));
			UdpDatagram datagram = peer.receive().orElseThrow();
			
			assertEquals("target", string(datagram.data()));
			assertEquals(endpoint(target), datagram.endpoint());
		}
	}
	
	@Test
	void disconnectClearsTarget() throws Exception {
		try (UdpPeer peer = peer(); UdpPeer target = peer(); UdpPeer other = peer()) {
			peer.connect(endpoint(target));
			
			peer.disconnect();
			
			assertFalse(peer.isConnected());
			assertTrue(peer.remoteEndpoint().isEmpty());
			peer.send(endpoint(other), bytes("free"));
			assertEquals("free", string(other.receive().orElseThrow().data()));
		}
	}
	
	@Test
	void disconnectWhenNotConnected() throws Exception {
		try (UdpPeer peer = peer()) {
			assertDoesNotThrow(peer::disconnect);
			assertFalse(peer.isConnected());
		}
	}
	
	@Test
	void isActiveFalseAfterClose() throws Exception {
		UdpPeer peer = peer();
		assertTrue(peer.isActive());
		
		peer.close();
		
		assertFalse(peer.isActive());
	}
	
	@Test
	void localEndpointEmptyAfterClose() throws Exception {
		UdpPeer peer = peer();
		assertTrue(peer.localEndpoint().isPresent());
		
		peer.close();
		
		assertTrue(peer.localEndpoint().isEmpty());
	}
	
	@Test
	void remoteEndpointEmptyWhenNotConnected() throws Exception {
		try (UdpPeer peer = peer()) {
			assertTrue(peer.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void receiveOnClosedPeerReturnsEmpty() throws Exception {
		UdpPeer peer = peer();
		peer.close();
		
		assertTrue(peer.receive().isEmpty());
	}
	
	@Test
	void receiveClosedWhileWaitingReturnsEmpty() throws Exception {
		UdpPeer peer = UdpPeer.bindTo(LOOPBACK);
		FutureTask<Optional<UdpDatagram>> result = new FutureTask<>(peer::receive);
		Thread receiver = Thread.ofVirtual().start(result);
		
		awaitBlocked(receiver);
		peer.close();
		
		assertTrue(result.get(5, TimeUnit.SECONDS).isEmpty());
	}
	
	@Test
	void closeIsIdempotent() throws Exception {
		UdpPeer peer = peer();
		
		assertDoesNotThrow(peer::close);
		assertDoesNotThrow(peer::close);
		assertFalse(peer.isActive());
	}
	
	@Test
	void sendDatagramDelegatesToEndpoint() throws Exception {
		try (UdpPeer sender = peer(); UdpPeer receiver = peer()) {
			sender.send(new UdpDatagram(endpoint(receiver), bytes("datagram")));
			UdpDatagram datagram = receiver.receive().orElseThrow();
			
			assertEquals("datagram", string(datagram.data()));
			assertEquals(endpoint(sender).port(), datagram.endpoint().port());
		}
	}
	
	@Test
	void receiveEmptyDatagram() throws Exception {
		try (UdpPeer sender = peer(); UdpPeer receiver = peer()) {
			sender.send(endpoint(receiver), new byte[0]);
			
			assertEquals(0, receiver.receive().orElseThrow().length());
		}
	}
	
	@Test
	void sendDatagramAtMaxSize() throws Exception {
		try (UdpPeer sender = peer(UdpPeerConfig.builder().maxDatagramSize(16).build()); UdpPeer receiver = peer()) {
			byte[] data = new byte[16];
			Arrays.fill(data, (byte) 3);
			
			sender.send(endpoint(receiver), data);
			
			assertArrayEquals(data, receiver.receive().orElseThrow().data());
		}
	}
	
	@Test
	void receiveTruncatesLargeDatagram() throws Exception {
		UdpPeerConfig receiverConfig = UdpPeerConfig.builder().maxDatagramSize(8).receiveTimeout(Duration.ofSeconds(5)).build();
		try (UdpPeer sender = peer(UdpPeerConfig.builder().maxDatagramSize(100).build()); UdpPeer receiver = peer(receiverConfig)) {
			byte[] data = new byte[20];
			for (int i = 0; i < data.length; i++) {
				data[i] = (byte) i;
			}
			
			sender.send(endpoint(receiver), data);
			UdpDatagram datagram = receiver.receive().orElseThrow();
			
			assertEquals(8, datagram.length());
			assertArrayEquals(Arrays.copyOf(data, 8), datagram.data());
		}
	}
	
	@Test
	void receiveBufferIsNotShared() throws Exception {
		try (UdpPeer sender = peer(); UdpPeer receiver = peer()) {
			sender.send(endpoint(receiver), bytes("0123456789"));
			byte[] first = receiver.receive().orElseThrow().data();
			
			sender.send(endpoint(receiver), bytes("abc"));
			byte[] second = receiver.receive().orElseThrow().data();
			
			assertEquals(3, second.length);
			assertEquals("abc", string(second));
			assertEquals("0123456789", string(first));
		}
	}
	
	@Test
	void concurrentReceivesGetDistinctDatagrams() throws Exception {
		UdpPeerConfig config = UdpPeerConfig.builder().receiveTimeout(Duration.ofSeconds(1)).build();
		List<String> received = new CopyOnWriteArrayList<>();
		try (UdpPeer sender = peer(); UdpPeer receiver = peer(config); ExecutorService executor = Executors.newFixedThreadPool(4)) {
			List<Future<?>> futures = new ArrayList<>();
			for (int thread = 0; thread < 4; thread++) {
				futures.add(executor.submit(() -> {
					try {
						Optional<UdpDatagram> datagram;
						while ((datagram = receiver.receive()).isPresent()) {
							received.add(string(datagram.get().data()));
						}
					} catch (NetworkTimeoutException _) {}
					return null;
				}));
			}
			
			for (int i = 0; i < 100; i++) {
				sender.send(endpoint(receiver), bytes(String.valueOf(i)));
			}
			for (Future<?> future : futures) {
				future.get(10, TimeUnit.SECONDS);
			}
		}
		
		assertEquals(100, received.size());
		assertEquals(100, new HashSet<>(received).size());
	}
	
	@Test
	void requestResponseBetweenTwoPeers() throws Exception {
		try (UdpPeer client = peer(); UdpPeer server = peer()) {
			client.send(endpoint(server), bytes("request"));
			UdpDatagram request = server.receive().orElseThrow();
			
			server.send(request.endpoint(), bytes("response"));
			UdpDatagram response = client.receive().orElseThrow();
			
			assertEquals("request", string(request.data()));
			assertEquals("response", string(response.data()));
			assertEquals(endpoint(server), response.endpoint());
		}
	}
	
	@Test
	void broadcastAllowedOnlyWithOption() throws Exception {
		IpEndpoint broadcast = new IpEndpoint(Ipv4Address.fromOctets(127, 255, 255, 255), 9);
		try (UdpPeer denied = UdpPeer.ephemeral(); UdpPeer allowed = UdpPeer.ephemeral(UdpPeerConfig.builder().broadcast(true).build())) {
			assertThrows(NetworkConnectionException.class, () -> denied.send(broadcast, bytes("broadcast")));
			assertDoesNotThrow(() -> allowed.send(broadcast, bytes("broadcast")));
		}
	}
}
