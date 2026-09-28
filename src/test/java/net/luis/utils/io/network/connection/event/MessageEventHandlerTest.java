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


package net.luis.utils.io.network.connection.event;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.tcp.*;
import net.luis.utils.io.network.connection.udp.UdpDatagram;
import net.luis.utils.io.network.connection.udp.UdpServer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MessageEventHandler}.<br>
 *
 * @author Luis-St
 */
class MessageEventHandlerTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	private static final IpEndpoint SOURCE = new IpEndpoint(Ipv4Address.LOOPBACK, 9999);
	
	@Test
	void handleReceivesServerContextAndData() {
		AtomicReference<UdpServer> capturedServer = new AtomicReference<>();
		AtomicReference<UdpDatagram> capturedContext = new AtomicReference<>();
		AtomicReference<byte[]> capturedData = new AtomicReference<>();
		UdpDatagram datagram = new UdpDatagram(SOURCE, "payload".getBytes());
		
		MessageEventHandler<UdpServer, UdpDatagram> handler = (server, context, data) -> {
			capturedServer.set(server);
			capturedContext.set(context);
			capturedData.set(data);
		};
		
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			handler.handle(server, datagram, datagram.data());
			
			assertSame(server, capturedServer.get());
			assertSame(datagram, capturedContext.get());
			assertArrayEquals("payload".getBytes(), capturedData.get());
		}
	}
	
	@Test
	void handleReceivesEmptyData() {
		AtomicReference<byte[]> capturedData = new AtomicReference<>();
		UdpDatagram datagram = new UdpDatagram(SOURCE, new byte[0]);
		
		MessageEventHandler<UdpServer, UdpDatagram> handler = (server, context, data) -> capturedData.set(data);
		
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			assertDoesNotThrow(() -> handler.handle(server, datagram, new byte[0]));
			
			assertEquals(0, capturedData.get().length);
		}
	}
	
	@Test
	void handleReceivesDataArrayByReference() {
		AtomicReference<byte[]> capturedData = new AtomicReference<>();
		byte[] data = { 1, 2, 3 };
		UdpDatagram datagram = new UdpDatagram(SOURCE, data);
		
		MessageEventHandler<UdpServer, UdpDatagram> handler = (server, context, payload) -> capturedData.set(payload);
		
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			handler.handle(server, datagram, data);
			data[0] = 42;
			
			assertSame(data, capturedData.get());
			assertEquals(42, capturedData.get()[0]);
		}
	}
	
	@Test
	void handleWithUdpParameterisation() {
		AtomicReference<IpEndpoint> capturedEndpoint = new AtomicReference<>();
		UdpDatagram datagram = new UdpDatagram(SOURCE, "ping".getBytes());
		
		MessageEventHandler<UdpServer, UdpDatagram> handler = (server, context, data) -> capturedEndpoint.set(context.endpoint());
		
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			handler.handle(server, datagram, datagram.data());
			
			assertEquals(SOURCE, capturedEndpoint.get());
			assertEquals(datagram.endpoint(), capturedEndpoint.get());
		}
	}
	
	@Test
	void handleWithTcpParameterisationCompiles() {
		MessageEventHandler<TcpServer, TcpConnection> handler = (server, connection, data) -> {};
		
		TcpServerConfig config = TcpServerConfig.builder().onMessage(handler).build();
		
		assertNotNull(config.onMessage());
		assertSame(handler, config.onMessage());
	}
	
	@Test
	void handleInvokedMultipleTimesAccumulates() {
		AtomicInteger callCount = new AtomicInteger(0);
		AtomicReference<byte[]> capturedData = new AtomicReference<>();
		UdpDatagram datagram = new UdpDatagram(SOURCE, new byte[0]);
		
		MessageEventHandler<UdpServer, UdpDatagram> handler = (server, context, data) -> {
			callCount.incrementAndGet();
			capturedData.set(data);
		};
		
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			handler.handle(server, datagram, "first".getBytes());
			handler.handle(server, datagram, "second".getBytes());
			handler.handle(server, datagram, "third".getBytes());
			
			assertEquals(3, callCount.get());
			assertArrayEquals("third".getBytes(), capturedData.get());
		}
	}
	
	@Test
	void handlerExceptionPropagatesToCaller() {
		UdpDatagram datagram = new UdpDatagram(SOURCE, new byte[1]);
		
		MessageEventHandler<UdpServer, UdpDatagram> handler = (server, context, data) -> {
			throw new RuntimeException("handler failed");
		};
		
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			RuntimeException exception = assertThrows(RuntimeException.class, () -> handler.handle(server, datagram, new byte[1]));
			assertEquals("handler failed", exception.getMessage());
		}
	}
}
