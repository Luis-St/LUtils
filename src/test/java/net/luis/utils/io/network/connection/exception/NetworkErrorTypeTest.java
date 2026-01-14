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

package net.luis.utils.io.network.connection.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link NetworkErrorType}.<br>
 *
 * @author Luis-St
 */
class NetworkErrorTypeTest {

	@Test
	void valuesCount() {
		assertEquals(14, NetworkErrorType.values().length);
	}

	@Test
	void valueOf() {
		assertEquals(NetworkErrorType.CONNECTION_FAILED, NetworkErrorType.valueOf("CONNECTION_FAILED"));
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, NetworkErrorType.valueOf("CONNECTION_REFUSED"));
		assertEquals(NetworkErrorType.CONNECTION_TIMEOUT, NetworkErrorType.valueOf("CONNECTION_TIMEOUT"));
		assertEquals(NetworkErrorType.READ_TIMEOUT, NetworkErrorType.valueOf("READ_TIMEOUT"));
		assertEquals(NetworkErrorType.WRITE_TIMEOUT, NetworkErrorType.valueOf("WRITE_TIMEOUT"));
		assertEquals(NetworkErrorType.CONNECTION_RESET, NetworkErrorType.valueOf("CONNECTION_RESET"));
		assertEquals(NetworkErrorType.NETWORK_UNREACHABLE, NetworkErrorType.valueOf("NETWORK_UNREACHABLE"));
		assertEquals(NetworkErrorType.HOST_UNREACHABLE, NetworkErrorType.valueOf("HOST_UNREACHABLE"));
		assertEquals(NetworkErrorType.ADDRESS_IN_USE, NetworkErrorType.valueOf("ADDRESS_IN_USE"));
		assertEquals(NetworkErrorType.SOCKET_CLOSED, NetworkErrorType.valueOf("SOCKET_CLOSED"));
		assertEquals(NetworkErrorType.NOT_CONNECTED, NetworkErrorType.valueOf("NOT_CONNECTED"));
		assertEquals(NetworkErrorType.ALREADY_CONNECTED, NetworkErrorType.valueOf("ALREADY_CONNECTED"));
		assertEquals(NetworkErrorType.IO_ERROR, NetworkErrorType.valueOf("IO_ERROR"));
		assertEquals(NetworkErrorType.UNKNOWN, NetworkErrorType.valueOf("UNKNOWN"));
	}

	@Test
	void valueOfInvalidThrows() {
		assertThrows(IllegalArgumentException.class, () -> NetworkErrorType.valueOf("INVALID"));
	}
}
