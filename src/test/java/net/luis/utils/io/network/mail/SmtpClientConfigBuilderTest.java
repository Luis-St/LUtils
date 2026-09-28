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
 * Test class for {@link SmtpClientConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class SmtpClientConfigBuilderTest {
	
	private static final ErrorEventHandler NO_OP = (connection, errorType, message, cause) -> {};
	
	@Test
	void constructDefaultBuilder() {
		SmtpClientConfig config = SmtpClientConfig.builder().build();
		
		assertEquals(SmtpSecurity.STARTTLS, config.security());
		assertInstanceOf(SmtpAuth.None.class, config.auth());
		assertNull(config.ehloHostname());
		assertSame(SslClientConfig.DEFAULT, config.tlsConfig());
		assertEquals(StandardCharsets.UTF_8, config.defaultCharset());
		assertEquals(8192, config.bufferSize());
		assertNull(config.onError());
	}
	
	@Test
	void securityWithNull() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertThrows(NullPointerException.class, () -> builder.security(null));
	}
	
	@Test
	void authWithNull() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertThrows(NullPointerException.class, () -> builder.auth(null));
	}
	
	@Test
	void tlsConfigWithNull() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertThrows(NullPointerException.class, () -> builder.tlsConfig(null));
	}
	
	@Test
	void defaultCharsetWithNull() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertThrows(NullPointerException.class, () -> builder.defaultCharset(null));
	}
	
	@Test
	void buildWithZeroBufferSizeThrows() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder().bufferSize(0);
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithNegativeBufferSizeThrows() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder().bufferSize(-1);
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithWhitespaceEhloHostnameThrows() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder().ehloHostname("host name");
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithControlCharEhloHostnameThrows() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder().ehloHostname("host\tname");
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void securityReturnsSameBuilder() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertSame(builder, builder.security(SmtpSecurity.IMPLICIT_TLS));
	}
	
	@Test
	void authReturnsSameBuilder() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertSame(builder, builder.auth(new SmtpAuth.None()));
	}
	
	@Test
	void tlsConfigReturnsSameBuilder() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertSame(builder, builder.tlsConfig(SslClientConfig.DEFAULT));
	}
	
	@Test
	void defaultCharsetReturnsSameBuilder() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertSame(builder, builder.defaultCharset(StandardCharsets.US_ASCII));
	}
	
	@Test
	void ehloHostnameReturnsSameBuilderWithValue() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertSame(builder, builder.ehloHostname("client.example.com"));
		assertEquals("client.example.com", builder.build().ehloHostname());
	}
	
	@Test
	void ehloHostnameWithNull() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertSame(builder, builder.ehloHostname(null));
		assertNull(builder.build().ehloHostname());
	}
	
	@Test
	void bufferSizeReturnsSameBuilder() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertSame(builder, builder.bufferSize(4096));
	}
	
	@Test
	void onErrorReturnsSameBuilderWithHandler() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertSame(builder, builder.onError(NO_OP));
		assertSame(NO_OP, builder.build().onError());
	}
	
	@Test
	void onErrorWithNull() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		
		assertSame(builder, builder.onError(null));
		assertNull(builder.build().onError());
	}
	
	@Test
	void buildWithMinimumBufferSize() {
		SmtpClientConfig config = SmtpClientConfig.builder().bufferSize(1).build();
		
		assertEquals(1, config.bufferSize());
	}
	
	@Test
	void buildWithNonNullEhloHostnameNoWhitespace() {
		SmtpClientConfig config = SmtpClientConfig.builder().ehloHostname("smtp.example.com").build();
		
		assertEquals("smtp.example.com", config.ehloHostname());
	}
	
	@Test
	void buildWithEmptyEhloHostname() {
		SmtpClientConfig config = SmtpClientConfig.builder().ehloHostname("").build();
		
		assertEquals("", config.ehloHostname());
	}
	
	@Test
	void securitySetsValue() {
		SmtpClientConfig config = SmtpClientConfig.builder().security(SmtpSecurity.IMPLICIT_TLS).build();
		
		assertEquals(SmtpSecurity.IMPLICIT_TLS, config.security());
	}
	
	@Test
	void authSetsValue() {
		SmtpAuth.Login auth = new SmtpAuth.Login("user@example.com", "pw".toCharArray());
		SmtpClientConfig config = SmtpClientConfig.builder().auth(auth).build();
		
		assertSame(auth, config.auth());
	}
	
	@Test
	void defaultCharsetSetsValue() {
		SmtpClientConfig config = SmtpClientConfig.builder().defaultCharset(StandardCharsets.ISO_8859_1).build();
		
		assertEquals(StandardCharsets.ISO_8859_1, config.defaultCharset());
	}
	
	@Test
	void bufferSizeSetsValue() {
		SmtpClientConfig config = SmtpClientConfig.builder().bufferSize(16384).build();
		
		assertEquals(16384, config.bufferSize());
	}
	
	@Test
	void methodChainingConsistency() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder();
		SmtpClientConfigBuilder chained = builder
			.security(SmtpSecurity.IMPLICIT_TLS)
			.auth(new SmtpAuth.None())
			.ehloHostname("smtp.example.com")
			.tlsConfig(SslClientConfig.DEFAULT)
			.defaultCharset(StandardCharsets.US_ASCII)
			.bufferSize(4096)
			.onError(NO_OP);
		
		assertSame(builder, chained);
		SmtpClientConfig config = chained.build();
		assertEquals(SmtpSecurity.IMPLICIT_TLS, config.security());
		assertEquals(4096, config.bufferSize());
	}
	
	@Test
	void buildAppliesAllConfiguredValues() {
		SmtpAuth.Login auth = new SmtpAuth.Login("user@example.com", "pw".toCharArray());
		SmtpClientConfig config = SmtpClientConfig.builder()
			.security(SmtpSecurity.IMPLICIT_TLS)
			.auth(auth)
			.ehloHostname("client.example.com")
			.tlsConfig(SslClientConfig.DEFAULT)
			.defaultCharset(StandardCharsets.ISO_8859_1)
			.bufferSize(2048)
			.onError(NO_OP)
			.build();
		
		assertEquals(SmtpSecurity.IMPLICIT_TLS, config.security());
		assertSame(auth, config.auth());
		assertEquals("client.example.com", config.ehloHostname());
		assertSame(SslClientConfig.DEFAULT, config.tlsConfig());
		assertEquals(StandardCharsets.ISO_8859_1, config.defaultCharset());
		assertEquals(2048, config.bufferSize());
		assertSame(NO_OP, config.onError());
	}
	
	@Test
	void builderReuseAfterBuild() {
		SmtpClientConfigBuilder builder = SmtpClientConfig.builder().bufferSize(2048);
		
		SmtpClientConfig first = builder.build();
		assertEquals(2048, first.bufferSize());
		
		builder.bufferSize(4096);
		SmtpClientConfig second = builder.build();
		assertEquals(4096, second.bufferSize());
		assertEquals(2048, first.bufferSize());
	}
	
	@Test
	void setterLastValueWins() {
		SmtpClientConfig config = SmtpClientConfig.builder()
			.security(SmtpSecurity.IMPLICIT_TLS)
			.security(SmtpSecurity.STARTTLS)
			.build();
		
		assertEquals(SmtpSecurity.STARTTLS, config.security());
	}
}
