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

import net.luis.utils.io.network.*;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NetworkConnectionException}.<br>
 *
 * @author Luis-St
 */
class NetworkConnectionExceptionTest {
	
	@Test
	void constructNoArgs() {
		NetworkConnectionException exception = new NetworkConnectionException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertEquals(NetworkErrorType.UNKNOWN, exception.errorType());
		assertNull(exception.endpoint());
	}
	
	@Test
	void constructWithMessage() {
		NetworkConnectionException exception = new NetworkConnectionException("Test message");
		
		assertEquals("Test message", exception.getMessage());
		assertNull(exception.getCause());
		assertEquals(NetworkErrorType.UNKNOWN, exception.errorType());
		assertNull(exception.endpoint());
	}
	
	@Test
	void constructWithMessageAndCause() {
		Throwable cause = new RuntimeException("Cause");
		NetworkConnectionException exception = new NetworkConnectionException("Test message", cause);
		
		assertEquals("Test message", exception.getMessage());
		assertEquals(cause, exception.getCause());
		assertEquals(NetworkErrorType.UNKNOWN, exception.errorType());
		assertNull(exception.endpoint());
	}
	
	@Test
	void constructWithCause() {
		Throwable cause = new RuntimeException("Cause");
		NetworkConnectionException exception = new NetworkConnectionException(cause);
		
		assertEquals(cause, exception.getCause());
		assertEquals(NetworkErrorType.UNKNOWN, exception.errorType());
		assertNull(exception.endpoint());
	}
	
	@Test
	void constructWithErrorType() {
		NetworkConnectionException exception = new NetworkConnectionException(NetworkErrorType.CONNECTION_REFUSED);
		
		assertNull(exception.getMessage());
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, exception.errorType());
		assertNull(exception.endpoint());
	}
	
	@Test
	void constructWithNullErrorTypeDefaultsToUnknown() {
		NetworkConnectionException exception = new NetworkConnectionException((NetworkErrorType) null);
		
		assertEquals(NetworkErrorType.UNKNOWN, exception.errorType());
	}
	
	@Test
	void constructWithErrorTypeAndEndpoint() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		NetworkConnectionException exception = new NetworkConnectionException(NetworkErrorType.CONNECTION_REFUSED, endpoint);
		
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, exception.errorType());
		assertEquals(endpoint, exception.endpoint());
	}
	
	@Test
	void constructWithMessageAndErrorType() {
		NetworkConnectionException exception = new NetworkConnectionException("Connection failed", NetworkErrorType.CONNECTION_FAILED);
		
		assertEquals("Connection failed", exception.getMessage());
		assertEquals(NetworkErrorType.CONNECTION_FAILED, exception.errorType());
		assertNull(exception.endpoint());
	}
	
	@Test
	void constructWithMessageErrorTypeAndEndpoint() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 9000);
		NetworkConnectionException exception = new NetworkConnectionException("Connection failed", NetworkErrorType.CONNECTION_FAILED, endpoint);
		
		assertEquals("Connection failed", exception.getMessage());
		assertEquals(NetworkErrorType.CONNECTION_FAILED, exception.errorType());
		assertEquals(endpoint, exception.endpoint());
	}
	
	@Test
	void constructWithMessageCauseAndErrorType() {
		Throwable cause = new RuntimeException("Cause");
		NetworkConnectionException exception = new NetworkConnectionException("Connection reset", cause, NetworkErrorType.CONNECTION_RESET);
		
		assertEquals("Connection reset", exception.getMessage());
		assertEquals(cause, exception.getCause());
		assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
		assertNull(exception.endpoint());
	}
	
	@Test
	void constructFullArgs() {
		Throwable cause = new RuntimeException("Cause");
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 443);
		NetworkConnectionException exception = new NetworkConnectionException("Host unreachable", cause, NetworkErrorType.HOST_UNREACHABLE, endpoint);
		
		assertEquals("Host unreachable", exception.getMessage());
		assertEquals(cause, exception.getCause());
		assertEquals(NetworkErrorType.HOST_UNREACHABLE, exception.errorType());
		assertEquals(endpoint, exception.endpoint());
	}
	
	@Test
	void nullErrorTypeDefaultsToUnknownInAllConstructors() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 80);
		
		NetworkConnectionException e1 = new NetworkConnectionException((NetworkErrorType) null);
		NetworkConnectionException e2 = new NetworkConnectionException(null, endpoint);
		NetworkConnectionException e3 = new NetworkConnectionException("msg", (NetworkErrorType) null);
		NetworkConnectionException e4 = new NetworkConnectionException("msg", null, (IpEndpoint) null);
		NetworkConnectionException e5 = new NetworkConnectionException("msg", new RuntimeException(), null);
		NetworkConnectionException e6 = new NetworkConnectionException("msg", new RuntimeException(), null, endpoint);
		
		assertEquals(NetworkErrorType.UNKNOWN, e1.errorType());
		assertEquals(NetworkErrorType.UNKNOWN, e2.errorType());
		assertEquals(NetworkErrorType.UNKNOWN, e3.errorType());
		assertEquals(NetworkErrorType.UNKNOWN, e4.errorType());
		assertEquals(NetworkErrorType.UNKNOWN, e5.errorType());
		assertEquals(NetworkErrorType.UNKNOWN, e6.errorType());
	}
	
	@Test
	void extendsIOException() {
		NetworkConnectionException exception = new NetworkConnectionException();
		assertInstanceOf(IOException.class, exception);
	}
	
	@Test
	void constructWithErrorTypeAndHostEndpoint() {
		HostEndpoint endpoint = new HostEndpoint("example.com", 443);
		
		NetworkConnectionException exception = new NetworkConnectionException(NetworkErrorType.CONNECTION_REFUSED, endpoint);
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, exception.errorType());
		assertSame(endpoint, exception.endpoint());
	}
	
	@Test
	void constructWithMessageAndHostEndpoint() {
		HostEndpoint endpoint = new HostEndpoint("example.com", 443);
		
		NetworkConnectionException exception = new NetworkConnectionException("Failed", NetworkErrorType.HOST_UNREACHABLE, endpoint);
		assertEquals("Failed", exception.getMessage());
		assertEquals(NetworkErrorType.HOST_UNREACHABLE, exception.errorType());
		assertSame(endpoint, exception.endpoint());
	}
	
	@Test
	void constructWithMessageCauseAndHostEndpoint() {
		HostEndpoint endpoint = new HostEndpoint("example.com", 443);
		RuntimeException cause = new RuntimeException("cause");
		
		NetworkConnectionException exception = new NetworkConnectionException("Failed", cause, NetworkErrorType.IO_ERROR, endpoint);
		assertEquals("Failed", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals(NetworkErrorType.IO_ERROR, exception.errorType());
		assertSame(endpoint, exception.endpoint());
	}
	
	@Test
	void endpointAccessorReturnsEndpointSupertype() {
		Endpoint fromHost = new NetworkConnectionException(NetworkErrorType.IO_ERROR, new HostEndpoint("example.com", 443)).endpoint();
		Endpoint fromIp = new NetworkConnectionException(NetworkErrorType.IO_ERROR, new IpEndpoint(Ipv4Address.LOOPBACK, 8080)).endpoint();
		
		assertNotNull(fromHost);
		assertNotNull(fromIp);
		assertEquals(443, fromHost.port());
		assertEquals(8080, fromIp.port());
	}
	
	@Test
	void endpointRetainsConcreteTypeForPatternMatching() {
		NetworkConnectionException hostException = new NetworkConnectionException(NetworkErrorType.IO_ERROR, new HostEndpoint("example.com", 443));
		NetworkConnectionException ipException = new NetworkConnectionException(NetworkErrorType.IO_ERROR, new IpEndpoint(Ipv4Address.LOOPBACK, 8080));
		
		assertEquals("example.com", hostPartOf(hostException));
		assertEquals("127.0.0.1", hostPartOf(ipException));
	}
	
	@Test
	void exceptionsWithDifferentEndpointVariantsAreDistinguishable() {
		Endpoint host = new NetworkConnectionException(NetworkErrorType.IO_ERROR, new HostEndpoint("127.0.0.1", 8080)).endpoint();
		Endpoint ip = new NetworkConnectionException(NetworkErrorType.IO_ERROR, new IpEndpoint(Ipv4Address.LOOPBACK, 8080)).endpoint();
		
		assertEquals("127.0.0.1:8080", String.valueOf(host));
		assertEquals("127.0.0.1:8080", String.valueOf(ip));
		assertNotEquals(host, ip);
	}
	
	private static String hostPartOf(NetworkConnectionException exception) {
		return switch (exception.endpoint()) {
			case HostEndpoint hostEndpoint -> hostEndpoint.hostname();
			case IpEndpoint ipEndpoint -> ipEndpoint.address().toString();
			case null -> "";
		};
	}
}
