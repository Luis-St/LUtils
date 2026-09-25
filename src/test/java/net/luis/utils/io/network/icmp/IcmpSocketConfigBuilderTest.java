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

package net.luis.utils.io.network.icmp;

import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IcmpSocketConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class IcmpSocketConfigBuilderTest {
	
	private static final ErrorEventHandler HANDLER = (connection, type, message, cause) -> {};
	
	@Test
	void constructWithDefaults() {
		IcmpSocketConfig config = IcmpSocketConfig.builder().build();
		assertEquals(IcmpSocketType.DATAGRAM, config.socketType());
		assertEquals(Duration.ZERO, config.receiveTimeout());
		assertEquals(65535, config.bufferSize());
		assertEquals(0, config.timeToLive());
		assertNull(config.onError());
		assertEquals(IcmpSocketConfig.DEFAULT, config);
	}
	
	@Test
	void socketTypeWithNull() {
		assertThrows(NullPointerException.class, () -> IcmpSocketConfig.builder().socketType(null));
	}
	
	@Test
	void receiveTimeoutWithNull() {
		assertThrows(NullPointerException.class, () -> IcmpSocketConfig.builder().receiveTimeout(null));
	}
	
	@Test
	void buildWithNegativeReceiveTimeout() {
		IcmpSocketConfigBuilder builder = IcmpSocketConfig.builder();
		assertDoesNotThrow(() -> builder.receiveTimeout(Duration.ofSeconds(-1)));
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithBufferSizeBelowHeaderSize() {
		IcmpSocketConfigBuilder builder = IcmpSocketConfig.builder().bufferSize(7);
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithTimeToLiveOutOfRange() {
		assertThrows(IllegalArgumentException.class, () -> IcmpSocketConfig.builder().timeToLive(-1).build());
		assertThrows(IllegalArgumentException.class, () -> IcmpSocketConfig.builder().timeToLive(256).build());
	}
	
	@Test
	void onErrorWithNullDisablesHandler() {
		IcmpSocketConfig config = IcmpSocketConfig.builder().onError(HANDLER).onError(null).build();
		assertNull(config.onError());
	}
	
	@Test
	void timeToLiveAtBoundaries() {
		assertEquals(0, IcmpSocketConfig.builder().timeToLive(0).build().timeToLive());
		assertEquals(255, IcmpSocketConfig.builder().timeToLive(255).build().timeToLive());
	}
	
	@Test
	void socketTypeSetsValue() {
		IcmpSocketConfigBuilder builder = IcmpSocketConfig.builder();
		assertSame(builder, builder.socketType(IcmpSocketType.RAW));
		assertEquals(IcmpSocketType.RAW, builder.build().socketType());
	}
	
	@Test
	void receiveTimeoutSetsValue() {
		IcmpSocketConfigBuilder builder = IcmpSocketConfig.builder();
		assertSame(builder, builder.receiveTimeout(Duration.ofMillis(250)));
		assertEquals(Duration.ofMillis(250), builder.build().receiveTimeout());
	}
	
	@Test
	void bufferSizeSetsValue() {
		IcmpSocketConfigBuilder builder = IcmpSocketConfig.builder();
		assertSame(builder, builder.bufferSize(1024));
		assertEquals(1024, builder.build().bufferSize());
	}
	
	@Test
	void timeToLiveSetsValue() {
		IcmpSocketConfigBuilder builder = IcmpSocketConfig.builder();
		assertSame(builder, builder.timeToLive(64));
		assertEquals(64, builder.build().timeToLive());
	}
	
	@Test
	void onErrorSetsValue() {
		IcmpSocketConfigBuilder builder = IcmpSocketConfig.builder();
		assertSame(builder, builder.onError(HANDLER));
		assertSame(HANDLER, builder.build().onError());
	}
	
	@Test
	void methodChainingConsistency() {
		IcmpSocketConfigBuilder builder = IcmpSocketConfig.builder();
		assertSame(builder, builder.socketType(IcmpSocketType.RAW));
		assertSame(builder, builder.receiveTimeout(Duration.ofSeconds(2)));
		assertSame(builder, builder.bufferSize(2048));
		assertSame(builder, builder.timeToLive(32));
		assertSame(builder, builder.onError(HANDLER));
		assertEquals(new IcmpSocketConfig(IcmpSocketType.RAW, Duration.ofSeconds(2), 2048, 32, HANDLER), builder.build());
	}
	
	@Test
	void setterMultipleTimesKeepsLastValue() {
		assertEquals(20, IcmpSocketConfig.builder().timeToLive(10).timeToLive(20).build().timeToLive());
	}
	
	@Test
	void builderReuseAfterBuild() {
		IcmpSocketConfigBuilder builder = IcmpSocketConfig.builder().bufferSize(1024);
		IcmpSocketConfig first = builder.build();
		assertEquals(1024, first.bufferSize());
		
		IcmpSocketConfig second = builder.bufferSize(4096).build();
		assertEquals(4096, second.bufferSize());
		assertNotEquals(first, second);
		assertEquals(1024, first.bufferSize());
	}
}
