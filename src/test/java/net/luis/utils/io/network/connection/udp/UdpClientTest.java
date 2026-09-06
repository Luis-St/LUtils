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
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UdpClient}.<br>
 *
 * @author Luis-St
 */
class UdpClientTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	private static final IpEndpoint DESTINATION = new IpEndpoint(Ipv4Address.LOOPBACK, 9999);
	private static final HostEndpoint UNRESOLVABLE = new HostEndpoint("no-such-host.invalid", 9999);
	private static final UdpClientConfig SMALL_BUFFER_CONFIG = UdpClientConfig.builder().bufferSize(16).build();
	
	@Test
	void constructWithDefaultConfig() {
		try (UdpClient client = new UdpClient()) {
			assertFalse(client.isActive());
			assertTrue(client.localEndpoint().isEmpty());
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void constructWithCustomConfig() {
		try (UdpClient client = new UdpClient(SMALL_BUFFER_CONFIG)) {
			assertFalse(client.isActive());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(DESTINATION, new byte[17]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new UdpClient(null));
	}
	
	@Test
	void bindToWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> UdpClient.bindTo(null));
	}
	
	@Test
	void bindToWithNullEndpointAndConfig() {
		assertThrows(NullPointerException.class, () -> UdpClient.bindTo(null, UdpClientConfig.DEFAULT));
	}
	
	@Test
	void bindToWithNullConfig() {
		assertThrows(NullPointerException.class, () -> UdpClient.bindTo(EPHEMERAL, null));
	}
	
	@Test
	void bindWithNullEndpoint() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(NullPointerException.class, () -> client.bind(null));
		}
	}
	
	@Test
	void sendWithNullDestination() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null, new byte[1]));
		}
	}
	
	@Test
	void sendWithNullData() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(NullPointerException.class, () -> client.send(DESTINATION, null));
		}
	}
	
	@Test
	void sendDatagramWithNullDatagram() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null));
		}
	}
	
	@Test
	void receiveWithZeroMaxBytesThrows() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(0));
		}
	}
	
	@Test
	void receiveWithNegativeMaxBytesThrows() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(-1));
		}
	}
	
	@Test
	void bindToEphemeralPortSucceeds() {
		try (UdpClient client = new UdpClient()) {
			assertDoesNotThrow(() -> client.bind(EPHEMERAL));
			
			assertTrue(client.isActive());
			assertTrue(client.localEndpoint().isPresent());
			assertNotEquals(0, client.localEndpoint().orElseThrow().port());
		}
	}
	
	@Test
	void bindWhileAlreadyBoundThrows() throws Exception {
		try (UdpClient client = new UdpClient()) {
			client.bind(EPHEMERAL);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.bind(EPHEMERAL));
			assertEquals(NetworkErrorType.ALREADY_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void bindAfterCloseRebindsSuccessfully() throws Exception {
		try (UdpClient client = new UdpClient()) {
			client.bind(EPHEMERAL);
			client.close();
			assertFalse(client.isActive());
			
			assertDoesNotThrow(() -> client.bind(EPHEMERAL));
			assertTrue(client.isActive());
			assertTrue(client.localEndpoint().isPresent());
		}
	}
	
	@Test
	void bindToPortAlreadyInUseThrowsAddressInUse() throws Exception {
		AtomicReference<NetworkErrorType> reported = new AtomicReference<>();
		UdpClientConfig config = UdpClientConfig.builder().onError((conn, type, message, cause) -> reported.set(type)).build();
		
		try (UdpClient first = new UdpClient(); UdpClient second = new UdpClient(config)) {
			first.bind(EPHEMERAL);
			IpEndpoint occupied = first.localEndpoint().orElseThrow();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> second.bind(occupied));
			assertEquals(NetworkErrorType.ADDRESS_IN_USE, exception.errorType());
			assertEquals(NetworkErrorType.ADDRESS_IN_USE, reported.get());
		}
	}
	
	@Test
	void bindToFailureClosesClient() throws Exception {
		try (UdpClient first = new UdpClient()) {
			first.bind(EPHEMERAL);
			IpEndpoint occupied = first.localEndpoint().orElseThrow();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> UdpClient.bindTo(occupied));
			assertEquals(NetworkErrorType.ADDRESS_IN_USE, exception.errorType());
		}
	}
	
	@Test
	void isActiveFalseBeforeBind() {
		try (UdpClient client = new UdpClient()) {
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void isActiveFalseAfterClose() throws Exception {
		try (UdpClient client = new UdpClient()) {
			client.bind(EPHEMERAL);
			assertTrue(client.isActive());
			
			client.close();
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void localEndpointEmptyBeforeBind() {
		try (UdpClient client = new UdpClient()) {
			assertTrue(client.localEndpoint().isEmpty());
		}
	}
	
	@Test
	void localEndpointEmptyAfterClose() throws Exception {
		try (UdpClient client = new UdpClient()) {
			client.bind(EPHEMERAL);
			client.close();
			
			assertTrue(client.localEndpoint().isEmpty());
		}
	}
	
	@Test
	void localEndpointPresentAfterBind() throws Exception {
		try (UdpClient client = new UdpClient()) {
			client.bind(EPHEMERAL);
			
			IpEndpoint local = client.localEndpoint().orElseThrow();
			assertNotEquals(0, local.port());
			assertEquals(Ipv4Address.LOOPBACK, local.address());
		}
	}
	
	@Test
	void remoteEndpointAlwaysEmpty() throws Exception {
		try (UdpClient client = new UdpClient()) {
			assertTrue(client.remoteEndpoint().isEmpty());
			
			client.bind(EPHEMERAL);
			assertTrue(client.remoteEndpoint().isEmpty());
			
			client.close();
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void sendDataExceedingBufferSizeThrowsBeforeSocketCreation() {
		try (UdpClient client = new UdpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(DESTINATION, new byte[65536]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void sendDataExceedingConfiguredBufferSize() {
		try (UdpClient client = new UdpClient(SMALL_BUFFER_CONFIG)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(DESTINATION, new byte[17]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void sendToUnresolvedHostThrowsHostUnreachable() {
		AtomicInteger errorCount = new AtomicInteger(0);
		UdpClientConfig config = UdpClientConfig.builder().onError((conn, type, message, cause) -> errorCount.incrementAndGet()).build();
		
		try (UdpClient client = new UdpClient(config)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(UNRESOLVABLE, new byte[1]));
			assertEquals(NetworkErrorType.HOST_UNREACHABLE, exception.errorType());
			assertInstanceOf(UnknownHostException.class, exception.getCause());
			assertEquals(1, errorCount.get());
		}
	}
	
	@Test
	void receiveWithoutBindThrowsNotConnected() {
		try (UdpClient client = new UdpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void receiveAfterCloseThrowsNotConnected() throws Exception {
		try (UdpClient client = new UdpClient()) {
			client.bind(EPHEMERAL);
			client.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.receive(1));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void receiveOnCreatedButUnboundSocketThrowsNotConnected() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(NetworkConnectionException.class, () -> client.send(UNRESOLVABLE, new byte[1]));
			assertTrue(client.isActive());
			assertTrue(client.localEndpoint().isEmpty());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.receive(1));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void closeBeforeBindDoesNothing() {
		UdpClient client = new UdpClient();
		
		assertDoesNotThrow(client::close);
		assertFalse(client.isActive());
	}
	
	@Test
	void closeIsIdempotent() throws Exception {
		UdpClient client = new UdpClient();
		client.bind(EPHEMERAL);
		
		assertDoesNotThrow(client::close);
		assertDoesNotThrow(client::close);
		assertDoesNotThrow(client::close);
		assertFalse(client.isActive());
	}
	
	@Test
	void receiveWithMinimumMaxBytesReachesBoundCheck() {
		try (UdpClient client = new UdpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.receive(1));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void implementsNetworkClient() {
		try (UdpClient client = new UdpClient()) {
			assertInstanceOf(NetworkClient.class, client);
		}
	}
	
	@Test
	void bindToIpv6LoopbackEndpoint() {
		try (UdpClient client = new UdpClient()) {
			assertDoesNotThrow(() -> client.bind(new IpEndpoint(Ipv6Address.LOOPBACK, 0)));
			
			assertTrue(client.isActive());
			assertEquals(6, client.localEndpoint().orElseThrow().address().version());
		}
	}
	
	@Test
	void sendGuardPrecedenceOnUnboundClient() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null, new byte[1]));
			assertThrows(NullPointerException.class, () -> client.send(DESTINATION, null));
			
			NetworkConnectionException tooLarge = assertThrows(NetworkConnectionException.class, () -> client.send(DESTINATION, new byte[65536]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, tooLarge.errorType());
			
			NetworkConnectionException unreachable = assertThrows(NetworkConnectionException.class, () -> client.send(UNRESOLVABLE, new byte[1]));
			assertEquals(NetworkErrorType.HOST_UNREACHABLE, unreachable.errorType());
		}
	}
	
	@Test
	void sendDatagramDelegatesGuardsToEndpointOverload() {
		try (UdpClient client = new UdpClient()) {
			UdpDatagram datagram = new UdpDatagram(DESTINATION, new byte[65536]);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(datagram));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void bindCloseRebindCycleConsistency() throws Exception {
		try (UdpClient client = new UdpClient()) {
			client.bind(EPHEMERAL);
			assertTrue(client.isActive());
			assertTrue(client.localEndpoint().isPresent());
			
			client.close();
			assertFalse(client.isActive());
			
			assertDoesNotThrow(() -> client.bind(EPHEMERAL));
			assertTrue(client.isActive());
			assertTrue(client.localEndpoint().isPresent());
		}
	}
}
