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

import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import net.luis.utils.io.network.connection.ssl.SslClientConfig;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SmtpClientConfig}.<br>
 *
 * @author Luis-St
 */
class SmtpClientConfigTest {
	
	private static final ErrorEventHandler NO_OP = (connection, errorType, message, cause) -> {};
	
	@Test
	void constructWithValidArguments() {
		SmtpClientConfig config = new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "host.example.com", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null);
		
		assertEquals(SmtpSecurity.STARTTLS, config.security());
		assertInstanceOf(SmtpAuth.None.class, config.auth());
		assertEquals("host.example.com", config.ehloHostname());
		assertSame(SslClientConfig.DEFAULT, config.tlsConfig());
		assertEquals(StandardCharsets.UTF_8, config.defaultCharset());
		assertEquals(8192, config.bufferSize());
		assertNull(config.onError());
	}
	
	@Test
	void constructWithNullEhloHostname() {
		SmtpClientConfig config = assertDoesNotThrow(() -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
		
		assertNull(config.ehloHostname());
	}
	
	@Test
	void constructWithNullOnError() {
		SmtpClientConfig config = assertDoesNotThrow(() -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "host.example.com", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
		
		assertNull(config.onError());
	}
	
	@Test
	void constructWithNullSecurity() {
		assertThrows(NullPointerException.class, () -> new SmtpClientConfig(null, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
	}
	
	@Test
	void constructWithNullAuth() {
		assertThrows(NullPointerException.class, () -> new SmtpClientConfig(SmtpSecurity.STARTTLS, null, null, SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
	}
	
	@Test
	void constructWithNullTlsConfig() {
		assertThrows(NullPointerException.class, () -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), null, null, StandardCharsets.UTF_8, 8192, null));
	}
	
	@Test
	void constructWithNullDefaultCharset() {
		assertThrows(NullPointerException.class, () -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, null, 8192, null));
	}
	
	@Test
	void constructWithZeroBufferSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 0, null));
	}
	
	@Test
	void constructWithNegativeBufferSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, StandardCharsets.UTF_8, -1, null));
	}
	
	@Test
	void constructWithWhitespaceInEhloHostnameThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "host name", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
	}
	
	@Test
	void constructWithControlCharInEhloHostnameThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "host\tname", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
	}
	
	@Test
	void constructWithBufferSizeOneSucceeds() {
		SmtpClientConfig config = assertDoesNotThrow(() -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 1, null));
		
		assertEquals(1, config.bufferSize());
	}
	
	@Test
	void constructWithLargeBufferSizeSucceeds() {
		SmtpClientConfig config = assertDoesNotThrow(() -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, StandardCharsets.UTF_8, Integer.MAX_VALUE, null));
		
		assertEquals(Integer.MAX_VALUE, config.bufferSize());
	}
	
	@Test
	void constructWithNullEhloHostnameSkipsLoop() {
		SmtpClientConfig config = assertDoesNotThrow(() -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
		
		assertNull(config.ehloHostname());
	}
	
	@Test
	void constructWithEmptyEhloHostnameSucceeds() {
		SmtpClientConfig config = assertDoesNotThrow(() -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
		
		assertEquals("", config.ehloHostname());
	}
	
	@Test
	void constructWithValidEhloHostnameSucceeds() {
		SmtpClientConfig config = assertDoesNotThrow(() -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "smtp.example.com", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
		
		assertEquals("smtp.example.com", config.ehloHostname());
	}
	
	@Test
	void constructWithWhitespaceAtEndOfEhloHostnameThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "host ", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
	}
	
	@Test
	void constructWithBoundaryCharAboveSpaceSucceeds() {
		SmtpClientConfig config = assertDoesNotThrow(() -> new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "!", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
		
		assertEquals("!", config.ehloHostname());
	}
	
	@Test
	void defaultConstantHasExpectedValues() {
		SmtpClientConfig config = SmtpClientConfig.DEFAULT;
		
		assertEquals(SmtpSecurity.STARTTLS, config.security());
		assertInstanceOf(SmtpAuth.None.class, config.auth());
		assertNull(config.ehloHostname());
		assertSame(SslClientConfig.DEFAULT, config.tlsConfig());
		assertEquals(StandardCharsets.UTF_8, config.defaultCharset());
		assertEquals(8192, config.bufferSize());
		assertNull(config.onError());
	}
	
	@Test
	void builderReturnsNonNullNewInstance() {
		SmtpClientConfigBuilder first = SmtpClientConfig.builder();
		SmtpClientConfigBuilder second = SmtpClientConfig.builder();
		
		assertNotNull(first);
		assertNotNull(second);
		assertNotSame(first, second);
	}
	
	@Test
	void accessorsReturnSuppliedValues() {
		char[] password = "pw".toCharArray();
		SmtpAuth.Login auth = new SmtpAuth.Login("user", password);
		SmtpClientConfig config = new SmtpClientConfig(SmtpSecurity.IMPLICIT_TLS, auth, "smtp.example.com", SslClientConfig.DEFAULT, StandardCharsets.ISO_8859_1, 4096, NO_OP);
		
		assertEquals(SmtpSecurity.IMPLICIT_TLS, config.security());
		assertSame(auth, config.auth());
		assertEquals("smtp.example.com", config.ehloHostname());
		assertSame(SslClientConfig.DEFAULT, config.tlsConfig());
		assertEquals(StandardCharsets.ISO_8859_1, config.defaultCharset());
		assertEquals(4096, config.bufferSize());
		assertSame(NO_OP, config.onError());
	}
	
	@Test
	void constructWithEachSecurityMode() {
		for (SmtpSecurity security : SmtpSecurity.values()) {
			SmtpClientConfig config = new SmtpClientConfig(security, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null);
			assertEquals(security, config.security());
		}
	}
	
	@Test
	void equalConfigsAreEqualAndShareHashCode() {
		SmtpClientConfig first = new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "host.example.com", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null);
		SmtpClientConfig second = new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "host.example.com", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null);
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void configsDifferingInAnyFieldAreNotEqual() {
		SmtpClientConfig base = new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "host.example.com", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null);
		
		assertNotEquals(base, new SmtpClientConfig(SmtpSecurity.IMPLICIT_TLS, new SmtpAuth.None(), "host.example.com", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
		assertNotEquals(base, new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.Login("user", "pw".toCharArray()), "host.example.com", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
		assertNotEquals(base, new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "other.example.com", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null));
		assertNotEquals(base, new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "host.example.com", SslClientConfig.DEFAULT, StandardCharsets.ISO_8859_1, 8192, null));
		assertNotEquals(base, new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "host.example.com", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 4096, null));
		assertNotEquals(base, new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), "host.example.com", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, NO_OP));
	}
	
	@Test
	void toStringContainsFieldNames() {
		SmtpClientConfig config = new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.Login("user", "topsecret".toCharArray()), "host.example.com", SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null);
		String string = config.toString();
		
		assertTrue(string.contains("security"));
		assertTrue(string.contains("bufferSize"));
		assertFalse(string.contains("topsecret"));
	}
	
	@Test
	void configWithOnErrorHandlerStoresHandler() {
		SmtpClientConfig config = new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, NO_OP);
		
		assertSame(NO_OP, config.onError());
	}
	
	@Test
	void configWithNonUtf8CharsetStored() {
		SmtpClientConfig config = new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, StandardCharsets.ISO_8859_1, 8192, null);
		
		assertEquals(StandardCharsets.ISO_8859_1, config.defaultCharset());
	}
	
	@Test
	void configNotEqualToNullOrDifferentTypeAndEqualsSelf() {
		SmtpClientConfig config = new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null);
		
		assertNotEquals(null, config);
		assertNotEquals("not a config", config);
		assertEquals(config, config);
	}
}
