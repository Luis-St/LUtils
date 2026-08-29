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
	void isUpgradedFalseInitially() {
		try (TcpClient client = new TcpClient()) {
			assertFalse(client.isUpgraded());
			assertFalse(client.isActive());
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
}
