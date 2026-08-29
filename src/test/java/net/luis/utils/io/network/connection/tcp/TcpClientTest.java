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
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.ssl.SslUpgradeConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TcpClient}.<br>
 *
 * @author Luis-St
 */
class TcpClientTest {
	
	private static final IpEndpoint ENDPOINT = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
	private static final TcpClientConfig SMALL_BUFFER_CONFIG = TcpClientConfig.builder().bufferSize(16).build();
	
	@Test
	void constructWithDefaultConfig() {
		try (TcpClient client = new TcpClient()) {
			assertFalse(client.isActive());
			assertFalse(client.isUpgraded());
			assertTrue(client.localEndpoint().isEmpty());
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void constructWithCustomConfig() {
		try (TcpClient client = new TcpClient(SMALL_BUFFER_CONFIG)) {
			assertFalse(client.isActive());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[17]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new TcpClient(null));
	}
	
	@Test
	void connectToWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> TcpClient.connectTo(null));
	}
	
	@Test
	void connectToWithNullEndpointAndConfig() {
		assertThrows(NullPointerException.class, () -> TcpClient.connectTo(null, TcpClientConfig.DEFAULT));
	}
	
	@Test
	void connectToWithNullConfig() {
		assertThrows(NullPointerException.class, () -> TcpClient.connectTo(ENDPOINT, null));
	}
	
	@Test
	void connectWithNullEndpoint() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(NullPointerException.class, () -> client.connect(null));
		}
	}
	
	@Test
	void sendWithNullData() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null));
		}
	}
	
	@Test
	void upgradeWithNullConfigThrows() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(NullPointerException.class, () -> client.upgrade(null));
		}
	}
	
	@Test
	void upgradeWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.upgrade(SslUpgradeConfig.DEFAULT));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void upgradeWithDefaultsWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::upgrade);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void upgradeAfterCloseWithoutConnectThrows() {
		TcpClient client = new TcpClient();
		assertDoesNotThrow(client::close);
		
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.upgrade(SslUpgradeConfig.DEFAULT));
		assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		assertFalse(client.isUpgraded());
	}
	
	@Test
	void sendWithoutConnectThrowsNotConnected() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[1]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendDataExceedingBufferSizeThrowsBeforeConnectionCheck() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8193]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			assertNotEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendDataEqualToBufferSizePassesSizeCheck() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8192]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendEmptyDataPassesSizeCheck() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[0]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendDataExceedingConfiguredBufferSize() {
		try (TcpClient client = new TcpClient(SMALL_BUFFER_CONFIG)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[17]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			
			NetworkConnectionException accepted = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[16]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, accepted.errorType());
		}
	}
	
	@Test
	void receiveWithZeroMaxBytesThrows() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(0));
		}
	}
	
	@Test
	void receiveWithNegativeMaxBytesThrows() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(-1));
		}
	}
	
	@Test
	void receiveWithMinimumMaxBytesReachesConnectionCheck() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.receive(1));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void receiveWithoutConnectThrowsNotConnected() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void getInputStreamWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getInputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void getOutputStreamWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getOutputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void isActiveFalseWithoutConnect() {
		try (TcpClient client = new TcpClient()) {
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void isUpgradedFalseInitially() {
		try (TcpClient client = new TcpClient()) {
			assertFalse(client.isUpgraded());
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void localEndpointEmptyWithoutConnect() {
		try (TcpClient client = new TcpClient()) {
			assertTrue(client.localEndpoint().isEmpty());
		}
	}
	
	@Test
	void remoteEndpointEmptyWithoutConnect() {
		try (TcpClient client = new TcpClient()) {
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void closeWithoutConnectDoesNothing() {
		TcpClient client = new TcpClient();
		
		assertDoesNotThrow(client::close);
		assertFalse(client.isActive());
	}
	
	@Test
	void closeIsIdempotentWithoutConnect() {
		TcpClient client = new TcpClient();
		
		assertDoesNotThrow(client::close);
		assertDoesNotThrow(client::close);
		assertDoesNotThrow(client::close);
		assertFalse(client.isActive());
	}
	
	@Test
	void operationsAfterCloseStillReportNotConnected() {
		TcpClient client = new TcpClient();
		client.close();
		
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, () -> client.send(new byte[1])).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::receive).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::getInputStream).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::getOutputStream).errorType());
	}
	
	@Test
	void implementsNetworkClient() {
		try (TcpClient client = new TcpClient()) {
			assertInstanceOf(NetworkClient.class, client);
		}
	}
	
	@Test
	void guardPrecedenceConsistencyOnUnconnectedClient() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null));
			
			NetworkConnectionException tooLarge = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8193]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, tooLarge.errorType());
			
			NetworkConnectionException notConnected = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8192]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, notConnected.errorType());
		}
	}
	
	@Test
	void receiveGuardPrecedenceConsistencyOnUnconnectedClient() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(0));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.receive(1));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
}
