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

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;

import java.time.Duration;

/**
 * Test class for {@link NetworkTimeoutException}.<br>
 *
 * @author Luis-St
 */
class NetworkTimeoutExceptionTest {

	@Test
	void constructWithErrorTypeAndTimeout() {
		Duration timeout = Duration.ofSeconds(30);
		NetworkTimeoutException exception = new NetworkTimeoutException(NetworkErrorType.CONNECTION_TIMEOUT, timeout);

		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertEquals(NetworkErrorType.CONNECTION_TIMEOUT, exception.errorType());
		assertEquals(timeout, exception.timeout());
		assertNull(exception.endpoint());
	}

	@Test
	void constructWithErrorTypeTimeoutAndEndpoint() {
		Duration timeout = Duration.ofSeconds(10);
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		NetworkTimeoutException exception = new NetworkTimeoutException(NetworkErrorType.READ_TIMEOUT, timeout, endpoint);

		assertNull(exception.getMessage());
		assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
		assertEquals(timeout, exception.timeout());
		assertEquals(endpoint, exception.endpoint());
	}

	@Test
	void constructWithMessageErrorTypeAndTimeout() {
		Duration timeout = Duration.ofMinutes(1);
		NetworkTimeoutException exception = new NetworkTimeoutException("Read timed out", NetworkErrorType.READ_TIMEOUT, timeout);

		assertEquals("Read timed out", exception.getMessage());
		assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
		assertEquals(timeout, exception.timeout());
		assertNull(exception.endpoint());
	}

	@Test
	void constructWithMessageErrorTypeTimeoutAndEndpoint() {
		Duration timeout = Duration.ofMillis(5000);
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 443);
		NetworkTimeoutException exception = new NetworkTimeoutException("Connection timed out", NetworkErrorType.CONNECTION_TIMEOUT, timeout, endpoint);

		assertEquals("Connection timed out", exception.getMessage());
		assertEquals(NetworkErrorType.CONNECTION_TIMEOUT, exception.errorType());
		assertEquals(timeout, exception.timeout());
		assertEquals(endpoint, exception.endpoint());
	}

	@Test
	void constructFullArgs() {
		Duration timeout = Duration.ofSeconds(15);
		Throwable cause = new RuntimeException("Timeout cause");
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 9000);
		NetworkTimeoutException exception = new NetworkTimeoutException("Write timed out", cause, NetworkErrorType.WRITE_TIMEOUT, timeout, endpoint);

		assertEquals("Write timed out", exception.getMessage());
		assertEquals(cause, exception.getCause());
		assertEquals(NetworkErrorType.WRITE_TIMEOUT, exception.errorType());
		assertEquals(timeout, exception.timeout());
		assertEquals(endpoint, exception.endpoint());
	}

	@Test
	void constructWithNullTimeoutThrows() {
		assertThrows(NullPointerException.class, () -> new NetworkTimeoutException(NetworkErrorType.CONNECTION_TIMEOUT, null));
		assertThrows(NullPointerException.class, () -> new NetworkTimeoutException(NetworkErrorType.READ_TIMEOUT, null, null));
		assertThrows(NullPointerException.class, () -> new NetworkTimeoutException("msg", NetworkErrorType.READ_TIMEOUT, null));
		assertThrows(NullPointerException.class, () -> new NetworkTimeoutException("msg", NetworkErrorType.READ_TIMEOUT, null, null));
		assertThrows(NullPointerException.class, () -> new NetworkTimeoutException("msg", null, NetworkErrorType.READ_TIMEOUT, null, null));
	}

	@Test
	void nullErrorTypeDefaultsToUnknown() {
		Duration timeout = Duration.ofSeconds(5);

		NetworkTimeoutException e1 = new NetworkTimeoutException(null, timeout);
		NetworkTimeoutException e2 = new NetworkTimeoutException(null, timeout, null);
		NetworkTimeoutException e3 = new NetworkTimeoutException("msg", null, timeout);
		NetworkTimeoutException e4 = new NetworkTimeoutException("msg", null, timeout, null);
		NetworkTimeoutException e5 = new NetworkTimeoutException("msg", new RuntimeException(), null, timeout, null);

		assertEquals(NetworkErrorType.UNKNOWN, e1.errorType());
		assertEquals(NetworkErrorType.UNKNOWN, e2.errorType());
		assertEquals(NetworkErrorType.UNKNOWN, e3.errorType());
		assertEquals(NetworkErrorType.UNKNOWN, e4.errorType());
		assertEquals(NetworkErrorType.UNKNOWN, e5.errorType());
	}

	@Test
	void extendsNetworkConnectionException() {
		NetworkTimeoutException exception = new NetworkTimeoutException(NetworkErrorType.READ_TIMEOUT, Duration.ofSeconds(1));
		assertInstanceOf(NetworkConnectionException.class, exception);
	}
}
