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

import net.luis.utils.io.network.connection.Framing;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TcpClientConfig}.<br>
 *
 * @author Luis-St
 */
class TcpClientConfigTest {
	
	@Test
	void constructWithAllValues() {
		TcpClientConfig config = new TcpClientConfig(Duration.ofSeconds(5), Duration.ofSeconds(10), 1024, Framing.lines(), false, false);
		
		assertEquals(Duration.ofSeconds(5), config.connectTimeout());
		assertEquals(Duration.ofSeconds(10), config.readTimeout());
		assertEquals(1024, config.maxMessageSize());
		assertEquals(Framing.lines(), config.framing());
		assertFalse(config.tcpNoDelay());
		assertFalse(config.keepAlive());
	}
	
	@Test
	void constructWithNullReadTimeout() {
		TcpClientConfig config = assertDoesNotThrow(() -> new TcpClientConfig(Duration.ofSeconds(5), null, 1024, Framing.lines(), true, true));
		
		assertNull(config.readTimeout());
	}
	
	@Test
	void constructWithNullConnectTimeout() {
		assertThrows(NullPointerException.class, () -> new TcpClientConfig(null, null, 1024, Framing.lines(), true, true));
	}
	
	@Test
	void constructWithNullFraming() {
		assertThrows(NullPointerException.class, () -> new TcpClientConfig(Duration.ofSeconds(5), null, 1024, null, true, true));
	}
	
	@Test
	void constructWithInvalidConnectTimeout() {
		assertThrows(IllegalArgumentException.class, () -> new TcpClientConfig(Duration.ZERO, null, 1024, Framing.lines(), true, true));
		assertThrows(IllegalArgumentException.class, () -> new TcpClientConfig(Duration.ofSeconds(-1), null, 1024, Framing.lines(), true, true));
	}
	
	@Test
	void constructWithInvalidReadTimeout() {
		assertThrows(IllegalArgumentException.class, () -> new TcpClientConfig(Duration.ofSeconds(5), Duration.ZERO, 1024, Framing.lines(), true, true));
		assertThrows(IllegalArgumentException.class, () -> new TcpClientConfig(Duration.ofSeconds(5), Duration.ofSeconds(-1), 1024, Framing.lines(), true, true));
	}
	
	@Test
	void constructWithInvalidMaxMessageSize() {
		assertThrows(IllegalArgumentException.class, () -> new TcpClientConfig(Duration.ofSeconds(5), null, 0, Framing.lines(), true, true));
		assertThrows(IllegalArgumentException.class, () -> new TcpClientConfig(Duration.ofSeconds(5), null, Integer.MIN_VALUE, Framing.lines(), true, true));
	}
	
	@Test
	void withFramingNull() {
		assertThrows(NullPointerException.class, () -> TcpClientConfig.DEFAULT.withFraming(null));
	}
	
	@Test
	void withMaxMessageSizeInvalid() {
		assertThrows(IllegalArgumentException.class, () -> TcpClientConfig.DEFAULT.withMaxMessageSize(0));
	}
	
	@Test
	void constructWithMaxMessageSizeOne() {
		TcpClientConfig config = assertDoesNotThrow(() -> new TcpClientConfig(Duration.ofSeconds(5), null, 1, Framing.lines(), true, true));
		
		assertEquals(1, config.maxMessageSize());
	}
	
	@Test
	void constructWithSmallestTimeouts() {
		assertDoesNotThrow(() -> new TcpClientConfig(Duration.ofNanos(1), Duration.ofNanos(1), 1024, Framing.lines(), true, true));
	}
	
	@Test
	void defaultValues() {
		TcpClientConfig config = TcpClientConfig.DEFAULT;
		
		assertEquals(Duration.ofSeconds(30), config.connectTimeout());
		assertNull(config.readTimeout());
		assertEquals(8192, config.maxMessageSize());
		assertEquals(Framing.lengthPrefixed(), config.framing());
		assertTrue(config.tcpNoDelay());
		assertTrue(config.keepAlive());
	}
	
	@Test
	void builderReturnsNewInstanceEachCall() {
		assertNotSame(TcpClientConfig.builder(), TcpClientConfig.builder());
	}
	
	@Test
	void builderWithoutChangesBuildsDefault() {
		assertEquals(TcpClientConfig.DEFAULT, TcpClientConfig.builder().build());
	}
	
	@Test
	void withFramingChangesOnlyFraming() {
		TcpClientConfig original = TcpClientConfig.DEFAULT;
		TcpClientConfig changed = original.withFraming(Framing.lines());
		
		assertEquals(Framing.lines(), changed.framing());
		assertEquals(original.connectTimeout(), changed.connectTimeout());
		assertEquals(original.readTimeout(), changed.readTimeout());
		assertEquals(original.maxMessageSize(), changed.maxMessageSize());
		assertEquals(original.tcpNoDelay(), changed.tcpNoDelay());
		assertEquals(original.keepAlive(), changed.keepAlive());
		assertEquals(Framing.lengthPrefixed(), original.framing());
	}
	
	@Test
	void withMaxMessageSizeChangesOnlySize() {
		TcpClientConfig original = TcpClientConfig.DEFAULT;
		TcpClientConfig changed = original.withMaxMessageSize(1024);
		
		assertEquals(1024, changed.maxMessageSize());
		assertEquals(original.connectTimeout(), changed.connectTimeout());
		assertEquals(original.readTimeout(), changed.readTimeout());
		assertEquals(original.framing(), changed.framing());
		assertEquals(original.tcpNoDelay(), changed.tcpNoDelay());
		assertEquals(original.keepAlive(), changed.keepAlive());
		assertEquals(8192, original.maxMessageSize());
	}
	
	@Test
	void withMethodsChain() {
		TcpClientConfig changed = TcpClientConfig.DEFAULT.withFraming(Framing.lines()).withMaxMessageSize(512);
		
		assertEquals(Framing.lines(), changed.framing());
		assertEquals(512, changed.maxMessageSize());
		assertEquals(Framing.lengthPrefixed(), TcpClientConfig.DEFAULT.framing());
		assertEquals(8192, TcpClientConfig.DEFAULT.maxMessageSize());
	}
	
	@Test
	void equalityAndHashCode() {
		TcpClientConfig first = new TcpClientConfig(Duration.ofSeconds(5), null, 1024, Framing.delimited((byte) 0), true, true);
		TcpClientConfig second = new TcpClientConfig(Duration.ofSeconds(5), null, 1024, Framing.delimited((byte) 0), true, true);
		TcpClientConfig otherSize = new TcpClientConfig(Duration.ofSeconds(5), null, 2048, Framing.delimited((byte) 0), true, true);
		TcpClientConfig otherDelimiter = new TcpClientConfig(Duration.ofSeconds(5), null, 1024, Framing.delimited((byte) 1), true, true);
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, otherSize);
		assertNotEquals(first, otherDelimiter);
	}
}
