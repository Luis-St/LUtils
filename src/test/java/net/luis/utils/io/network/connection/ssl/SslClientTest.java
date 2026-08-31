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
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.Test;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslClient}.<br>
 *
 * @author Luis-St
 */
class SslClientTest {
	
	private static final IpEndpoint ENDPOINT = new IpEndpoint(Ipv4Address.LOOPBACK, 8443);
	private static final SslClientConfig SMALL_BUFFER_CONFIG = SslClientConfig.builder().bufferSize(16).build();
	
	@Test
	void constructWithDefaultConfig() {
		try (SslClient client = new SslClient()) {
			assertFalse(client.isActive());
			assertTrue(client.localEndpoint().isEmpty());
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void constructWithCustomConfig() {
		try (SslClient client = new SslClient(SMALL_BUFFER_CONFIG)) {
			assertFalse(client.isActive());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[17]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new SslClient(null));
	}
	
	@Test
	void connectToWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> SslClient.connectTo(null));
	}
	
	@Test
	void connectToWithNullEndpointAndConfig() {
		assertThrows(NullPointerException.class, () -> SslClient.connectTo(null, SslClientConfig.DEFAULT));
	}
	
	@Test
	void connectToWithNullConfig() {
		assertThrows(NullPointerException.class, () -> SslClient.connectTo(ENDPOINT, null));
	}
	
	@Test
	void connectWithNullEndpoint() {
		try (SslClient client = new SslClient()) {
			assertThrows(NullPointerException.class, () -> client.connect(null));
		}
	}
	
	@Test
	void sendWithNullData() {
		try (SslClient client = new SslClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null));
		}
	}
	
	@Test
	void upgradeWithNullSocketThrows() {
		assertThrows(NullPointerException.class, () -> SslClient.upgrade(null, ENDPOINT, SslClientConfig.DEFAULT));
	}
	
	@Test
	void upgradeWithNullEndpointThrows() throws Exception {
		try (Socket socket = new Socket()) {
			assertThrows(NullPointerException.class, () -> SslClient.upgrade(socket, null, SslClientConfig.DEFAULT));
		}
	}
	
	@Test
	void upgradeWithNullConfigThrows() throws Exception {
		try (Socket socket = new Socket()) {
			assertThrows(NullPointerException.class, () -> SslClient.upgrade(socket, ENDPOINT, null));
		}
	}
	
	@Test
	void upgradeUnconnectedSocketThrows() throws Exception {
		try (Socket socket = new Socket()) {
			assertFalse(socket.isConnected());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, ENDPOINT, SslClientConfig.DEFAULT));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
			assertEquals(ENDPOINT, exception.endpoint());
		}
	}
	
	@Test
	void upgradeClosedSocketThrows() throws Exception {
		Socket socket = new Socket();
		socket.close();
		assertTrue(socket.isClosed());
		
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, ENDPOINT, SslClientConfig.DEFAULT));
		assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
	}
	
	@Test
	void getSessionWithoutConnectThrows() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getSession);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendWithoutConnectThrowsNotConnected() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[1]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendDataExceedingBufferSizeThrowsBeforeConnectionCheck() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8193]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			assertNotEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendDataEqualToBufferSizePassesSizeCheck() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8192]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendEmptyDataPassesSizeCheck() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[0]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendDataExceedingConfiguredBufferSize() {
		try (SslClient client = new SslClient(SMALL_BUFFER_CONFIG)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[17]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			
			NetworkConnectionException accepted = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[16]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, accepted.errorType());
		}
	}
	
	@Test
	void receiveWithZeroMaxBytesThrows() {
		try (SslClient client = new SslClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(0));
		}
	}
	
	@Test
	void receiveWithNegativeMaxBytesThrows() {
		try (SslClient client = new SslClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(-1));
		}
	}
	
	@Test
	void receiveWithMinimumMaxBytesReachesConnectionCheck() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.receive(1));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void receiveWithoutConnectThrowsNotConnected() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void getInputStreamWithoutConnectThrows() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getInputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void getOutputStreamWithoutConnectThrows() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getOutputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void isActiveFalseWithoutConnect() {
		try (SslClient client = new SslClient()) {
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void localEndpointEmptyWithoutConnect() {
		try (SslClient client = new SslClient()) {
			assertTrue(client.localEndpoint().isEmpty());
		}
	}
	
	@Test
	void remoteEndpointEmptyWithoutConnect() {
		try (SslClient client = new SslClient()) {
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void closeWithoutConnectDoesNothing() {
		SslClient client = new SslClient();
		
		assertDoesNotThrow(client::close);
		assertFalse(client.isActive());
	}
	
	@Test
	void closeIsIdempotentWithoutConnect() {
		SslClient client = new SslClient();
		
		assertDoesNotThrow(client::close);
		assertDoesNotThrow(client::close);
		assertDoesNotThrow(client::close);
		assertFalse(client.isActive());
	}
	
	@Test
	void operationsAfterCloseStillReportNotConnected() {
		SslClient client = new SslClient();
		client.close();
		
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, () -> client.send(new byte[1])).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::receive).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::getSession).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::getInputStream).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::getOutputStream).errorType());
	}
	
	@Test
	void implementsNetworkClient() {
		try (SslClient client = new SslClient()) {
			assertInstanceOf(NetworkClient.class, client);
		}
	}
	
	@Test
	void upgradeWithUnboundSocketThrowsWithEndpointAttached() throws Exception {
		try (Socket socket = new Socket()) {
			assertFalse(socket.isBound());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, ENDPOINT, SslClientConfig.DEFAULT));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
			assertEquals(ENDPOINT, exception.endpoint());
		}
	}
	
	@Test
	void guardPrecedenceConsistencyOnUnconnectedClient() {
		try (SslClient client = new SslClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null));
			
			NetworkConnectionException tooLarge = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8193]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, tooLarge.errorType());
			
			NetworkConnectionException notConnected = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8192]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, notConnected.errorType());
		}
	}
	
	@Test
	void upgradeGuardPrecedenceOnClosedSocket() throws Exception {
		Socket socket = new Socket();
		socket.close();
		
		assertThrows(NullPointerException.class, () -> SslClient.upgrade(socket, null, SslClientConfig.DEFAULT));
		
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, ENDPOINT, SslClientConfig.DEFAULT));
		assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
	}
}
