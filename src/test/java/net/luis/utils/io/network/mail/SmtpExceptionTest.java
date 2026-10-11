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

package net.luis.utils.io.network.mail;

import net.luis.utils.io.network.HostEndpoint;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SmtpException}.<br>
 *
 * @author Luis-St
 */
class SmtpExceptionTest {
	
	private static SmtpReply reply(int code) {
		return new SmtpReply(code, List.of("OK"));
	}
	
	private static IpEndpoint endpoint() {
		return new IpEndpoint(Ipv4Address.LOOPBACK, 587);
	}
	
	@Test
	void constructWithMessageAndReply() {
		SmtpReply reply = reply(250);
		SmtpException exception = new SmtpException("message", reply);
		
		assertEquals("message", exception.getMessage());
		assertSame(reply, exception.reply());
		assertEquals(250, exception.replyCode());
		assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
		assertNull(exception.endpoint());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessageReplyAndEndpoint() {
		SmtpReply reply = reply(250);
		IpEndpoint endpoint = endpoint();
		SmtpException exception = new SmtpException("message", reply, endpoint);
		
		assertEquals("message", exception.getMessage());
		assertSame(reply, exception.reply());
		assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
		assertEquals(endpoint, exception.endpoint());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessageReplyErrorTypeAndEndpoint() {
		SmtpReply reply = reply(535);
		IpEndpoint endpoint = endpoint();
		SmtpException exception = new SmtpException("message", reply, NetworkErrorType.AUTHENTICATION_FAILED, endpoint);
		
		assertEquals("message", exception.getMessage());
		assertSame(reply, exception.reply());
		assertEquals(NetworkErrorType.AUTHENTICATION_FAILED, exception.errorType());
		assertEquals(endpoint, exception.endpoint());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithAllDetails() {
		SmtpReply reply = reply(451);
		IpEndpoint endpoint = endpoint();
		Throwable cause = new RuntimeException("root");
		SmtpException exception = new SmtpException("message", cause, reply, NetworkErrorType.AUTHENTICATION_FAILED, endpoint);
		
		assertEquals("message", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertSame(reply, exception.reply());
		assertEquals(NetworkErrorType.AUTHENTICATION_FAILED, exception.errorType());
		assertEquals(endpoint, exception.endpoint());
	}
	
	@Test
	void constructWithNullMessageAndReply() {
		SmtpReply reply = reply(250);
		SmtpException exception = new SmtpException(null, reply);
		
		assertNull(exception.getMessage());
		assertSame(reply, exception.reply());
		assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
	}
	
	@Test
	void constructWithNullReply() {
		assertThrows(NullPointerException.class, () -> new SmtpException("m", null));
	}
	
	@Test
	void constructWithNullReplyAndEndpoint() {
		IpEndpoint endpoint = endpoint();
		assertThrows(NullPointerException.class, () -> new SmtpException("m", null, endpoint));
	}
	
	@Test
	void constructWithNullReplyErrorTypeEndpoint() {
		IpEndpoint endpoint = endpoint();
		assertThrows(NullPointerException.class, () -> new SmtpException("m", null, NetworkErrorType.PROTOCOL_ERROR, endpoint));
	}
	
	@Test
	void constructWithNullReplyAllDetails() {
		IpEndpoint endpoint = endpoint();
		Throwable cause = new RuntimeException("root");
		assertThrows(NullPointerException.class, () -> new SmtpException("m", cause, null, NetworkErrorType.PROTOCOL_ERROR, endpoint));
	}
	
	@Test
	void constructWithNullCause() {
		SmtpReply reply = reply(250);
		SmtpException exception = new SmtpException("m", null, reply, NetworkErrorType.PROTOCOL_ERROR, null);
		
		assertNull(exception.getCause());
		assertSame(reply, exception.reply());
	}
	
	@Test
	void constructWithNullEndpoint() {
		SmtpReply reply = reply(250);
		SmtpException exception = new SmtpException("m", reply, null);
		
		assertNull(exception.endpoint());
		assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
	}
	
	@Test
	void constructWithNonNullReplyStoresReply() {
		SmtpReply reply = reply(250);
		SmtpException exception = new SmtpException("m", reply);
		
		assertSame(reply, exception.reply());
		assertEquals(250, exception.replyCode());
	}
	
	@Test
	void constructWithNullErrorTypeDefaultsToUnknown() {
		SmtpReply reply = reply(250);
		SmtpException exception = new SmtpException("m", reply, null, endpoint());
		
		assertEquals(NetworkErrorType.UNKNOWN, exception.errorType());
	}
	
	@Test
	void constructWithNullErrorTypeAllDetailsDefaultsToUnknown() {
		SmtpReply reply = reply(250);
		Throwable cause = new RuntimeException("root");
		SmtpException exception = new SmtpException("m", cause, reply, null, endpoint());
		
		assertEquals(NetworkErrorType.UNKNOWN, exception.errorType());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithNonNullErrorTypeUsesGivenType() {
		SmtpReply reply = reply(250);
		SmtpException exception = new SmtpException("m", reply, NetworkErrorType.CONNECTION_TIMEOUT, endpoint());
		
		assertEquals(NetworkErrorType.CONNECTION_TIMEOUT, exception.errorType());
	}
	
	@Test
	void replyCodeReturnsReplyCode() {
		SmtpReply reply = new SmtpReply(451, List.of("try later"));
		SmtpException exception = new SmtpException("m", reply);
		
		assertEquals(451, exception.replyCode());
	}
	
	@Test
	void replyReturnsStoredReply() {
		SmtpReply reply = reply(250);
		SmtpException exception = new SmtpException("m", reply);
		
		assertSame(reply, exception.reply());
		assertEquals(reply.code(), exception.reply().code());
	}
	
	@Test
	void replyCodeReflectsBoundaryCodes() {
		SmtpException min = new SmtpException("m", reply(SmtpReply.MIN_CODE));
		SmtpException max = new SmtpException("m", reply(SmtpReply.MAX_CODE));
		
		assertEquals(100, min.replyCode());
		assertEquals(599, max.replyCode());
	}
	
	@Test
	void getMessageReturnsProvidedMessage() {
		SmtpException exception = new SmtpException("Server rejected", reply(250));
		
		assertEquals("Server rejected", exception.getMessage());
	}
	
	@Test
	void constructWithChainedCausePreservesCause() {
		Throwable cause = new IOException("io", new IllegalStateException("deep"));
		SmtpException exception = new SmtpException("m", cause, reply(250), NetworkErrorType.PROTOCOL_ERROR, null);
		
		assertSame(cause, exception.getCause());
		assertInstanceOf(IllegalStateException.class, exception.getCause().getCause());
	}
	
	@Test
	void throwAndCatchAsSmtpException() {
		SmtpReply reply = reply(550);
		
		SmtpException caught = assertThrows(SmtpException.class, () -> {
			throw new SmtpException("rejected", reply);
		});
		assertEquals(550, caught.replyCode());
		assertSame(reply, caught.reply());
	}
	
	@Test
	void throwAndCatchAsNetworkConnectionException() {
		SmtpException exception = new SmtpException("m", reply(250));
		
		assertInstanceOf(NetworkConnectionException.class, exception);
		assertInstanceOf(IOException.class, exception);
		NetworkConnectionException supertype = exception;
		assertEquals(NetworkErrorType.PROTOCOL_ERROR, supertype.errorType());
	}
	
	@Test
	void catchDistinguishesReplyCodeInHandler() {
		SmtpReply reply = reply(535);
		
		SmtpException caught = assertThrows(SmtpException.class, () -> {
			throw new SmtpException("auth failed", reply, NetworkErrorType.AUTHENTICATION_FAILED, endpoint());
		});
		assertEquals(535, caught.replyCode());
		assertEquals(NetworkErrorType.AUTHENTICATION_FAILED, caught.errorType());
	}
	
	@Test
	void constructWithHostEndpointAndNullReply() {
		HostEndpoint endpoint = new HostEndpoint("smtp.example.com", 587);
		assertThrows(NullPointerException.class, () -> new SmtpException("message", null, endpoint));
	}
	
	@Test
	void constructWithMessageReplyAndHostEndpoint() {
		SmtpReply reply = reply(503);
		HostEndpoint endpoint = new HostEndpoint("smtp.example.com", 587);
		SmtpException exception = new SmtpException("Bad sequence", reply, endpoint);
		
		assertEquals("Bad sequence", exception.getMessage());
		assertSame(reply, exception.reply());
		assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
		assertSame(endpoint, exception.endpoint());
	}
	
	@Test
	void constructWithErrorTypeAndHostEndpoint() {
		SmtpReply reply = reply(535);
		HostEndpoint endpoint = new HostEndpoint("smtp.example.com", 587);
		SmtpException exception = new SmtpException("Auth failed", reply, NetworkErrorType.AUTHENTICATION_FAILED, endpoint);
		
		assertEquals(NetworkErrorType.AUTHENTICATION_FAILED, exception.errorType());
		assertSame(endpoint, exception.endpoint());
	}
	
	@Test
	void constructWithCauseAndHostEndpoint() {
		SmtpReply reply = reply(421);
		HostEndpoint endpoint = new HostEndpoint("smtp.example.com", 587);
		RuntimeException cause = new RuntimeException("cause");
		SmtpException exception = new SmtpException("Service unavailable", cause, reply, NetworkErrorType.IO_ERROR, endpoint);
		
		assertEquals("Service unavailable", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertSame(reply, exception.reply());
		assertEquals(NetworkErrorType.IO_ERROR, exception.errorType());
		assertSame(endpoint, exception.endpoint());
	}
	
	@Test
	void smtpExceptionIsCatchableAsConnectionExceptionWithNamedEndpoint() {
		HostEndpoint endpoint = new HostEndpoint("smtp.example.com", 587);
		
		NetworkConnectionException caught = assertThrows(NetworkConnectionException.class, () -> {
			throw new SmtpException("Bad sequence", reply(503), endpoint);
		});
		assertInstanceOf(SmtpException.class, caught);
		assertEquals("smtp.example.com:587", String.valueOf(caught.endpoint()));
	}
	
	@Test
	void endpointRetainsConcreteTypeForPatternMatching() {
		SmtpException exception = new SmtpException("Bad sequence", reply(503), new HostEndpoint("smtp.example.com", 587));
		
		String host = switch (exception.endpoint()) {
			case HostEndpoint hostEndpoint -> hostEndpoint.hostname();
			case IpEndpoint ipEndpoint -> ipEndpoint.address().toString();
			case null -> "";
		};
		assertEquals("smtp.example.com", host);
	}
}
