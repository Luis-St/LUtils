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

import net.luis.utils.io.network.connection.exception.*;
import net.luis.utils.io.network.connection.ssl.SslClientConfig;
import net.luis.utils.io.network.connection.ssl.SslClientConfigBuilder;
import net.luis.utils.io.network.mail.message.*;
import org.junit.jupiter.api.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SmtpClient}.<br>
 * <p>
 *     The protocol logic is exercised over real loopback sockets using a scripted fake SMTP server
 *     ({@link FakeSmtpServer}) instead of mocks, consistent with the "test actual behavior" guideline.<br>
 *     TLS dependent tests use the bundled self-signed test keystore and are tagged {@code network}/{@code tls}.
 * </p>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class SmtpClientTest {
	
	private static final String HOST = "127.0.0.1";
	private static SSLContext sslContext;
	
	@BeforeAll
	static void setUp() throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		try (InputStream stream = SmtpClientTest.class.getResourceAsStream("/ssl/keystore.p12")) {
			assertNotNull(stream, "Test keystore /ssl/keystore.p12 not found on the classpath");
			keyStore.load(stream, "changeit".toCharArray());
		}
		
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, "changeit".toCharArray());
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(keyStore);
		
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
		sslContext = context;
	}
	
	//region Helper methods
	private static SslClientConfigBuilder tlsBuilder() {
		return SslClientConfig.builder().connectTimeout(Duration.ofSeconds(5));
	}
	
	private static SmtpClientConfig plaintextConfig(SmtpAuth auth) {
		return plaintextBuilder(auth).build();
	}
	
	private static SmtpClientConfigBuilder plaintextBuilder(SmtpAuth auth) {
		return SmtpClientConfig.builder().security(SmtpSecurity.PLAINTEXT).auth(auth).ehloHostname("client.test").tlsConfig(tlsBuilder().build());
	}
	
	private static SmtpClientConfig startTlsConfig(SmtpAuth auth) {
		return SmtpClientConfig.builder().security(SmtpSecurity.STARTTLS).auth(auth).ehloHostname("client.test")
			.tlsConfig(tlsBuilder().sslContext(sslContext).verifyHostname(false).build()).build();
	}
	
	private static SmtpClientConfig implicitTlsConfig(SmtpAuth auth, SslClientConfigBuilder tls) {
		return SmtpClientConfig.builder().security(SmtpSecurity.IMPLICIT_TLS).auth(auth).ehloHostname("client.test").tlsConfig(tls.build()).build();
	}
	
	private static MailMessage simpleMessage() {
		return messageWithBody("Hello Bob");
	}
	
	private static MailMessage messageWithBody(String body) {
		return MailMessage.builder().from(Mailbox.parse("alice@example.com")).to(Mailbox.parse("bob@example.com"))
			.subject("Hi").content(TextContent.of(body)).build();
	}
	
	private static MailMessage multiRecipientMessage() {
		return MailMessage.builder().from(Mailbox.parse("alice@example.com")).to(Mailbox.parse("bob@example.com"))
			.cc(Mailbox.parse("carol@example.com")).bcc(Mailbox.parse("dave@example.com"))
			.subject("Hi").content(TextContent.of("Hello all")).build();
	}
	//endregion
	
	private static void handshakeNone(Session session) throws Exception {
		session.send("220 smtp.test ESMTP ready");
		session.readLine();
		session.send("250 smtp.test");
	}
	
	private static String acceptTransaction(Session session, int rcptCount) throws Exception {
		session.readLine();
		session.send("250 OK");
		for (int i = 0; i < rcptCount; i++) {
			session.readLine();
			session.send("250 OK");
		}
		session.readLine();
		session.send("354 Start mail input; end with <CRLF>.<CRLF>");
		String body = session.readDataRaw();
		session.send("250 OK: queued");
		return body;
	}
	
	private static void startTlsHandshake(Session session, String capabilityLine) throws Exception {
		session.send("220 smtp.test ready");
		session.readLine();
		session.send("250-smtp.test");
		session.send("250 " + capabilityLine);
		session.readLine();
		session.send("220 Ready to start TLS");
		session.startTls(sslContext);
		session.readLine();
		session.send("250 smtp.test");
	}
	
	@Test
	void constructDefault() {
		SmtpClient client = new SmtpClient();
		assertNotNull(client);
		assertFalse(client.isConnected());
	}
	
	@Test
	void constructWithConfig() {
		SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
		assertNotNull(client);
		assertFalse(client.isConnected());
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new SmtpClient(null));
	}
	
	@Test
	void connectWithNullHost() {
		SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
		assertThrows(NullPointerException.class, () -> client.connect(null, 587));
	}
	
	@Test
	void sendWithNullMessage() {
		SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
		assertThrows(NullPointerException.class, () -> client.send(null));
	}
	
	@Test
	void sendWhenNotConnected() {
		SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(simpleMessage()));
		assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
	}
	
	@Test
	void closeOnUnconnectedClientThrows() {
		SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
		assertThrows(NullPointerException.class, client::close);
	}
	
	@Test
	void connectRefusedMapsToConnectionRefused() throws Exception {
		int port;
		try (ServerSocket probe = new ServerSocket()) {
			probe.bind(new InetSocketAddress(HOST, 0));
			port = probe.getLocalPort();
		}
		
		SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(HOST, port));
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, exception.errorType());
	}
	
	@Test
	void connectWhenAlreadyConnected() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(HOST, server.port()));
				assertEquals(NetworkErrorType.ALREADY_CONNECTED, exception.errorType());
			}
		}
	}
	
	@Test
	void connectRejectsBadGreeting() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> session.send("554 Service unavailable"))) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				assertThrows(SmtpException.class, () -> client.connect(HOST, server.port()));
			}
		}
	}
	
	@Test
	void connectStartTlsNotAdvertisedThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			SmtpClientConfig config = SmtpClientConfig.builder().security(SmtpSecurity.STARTTLS).auth(new SmtpAuth.None())
				.ehloHostname("client.test").tlsConfig(tlsBuilder().sslContext(sslContext).verifyHostname(false).build()).build();
			
			try (SmtpClient client = new SmtpClient(config)) {
				SmtpException exception = assertThrows(SmtpException.class, () -> client.connect(HOST, server.port()));
				assertTrue(exception.getMessage().contains("does not advertise STARTTLS"));
				assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
			}
		}
	}
	
	@Test
	void authPlainRejectedThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("535 Authentication failed");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.Plain("user@example.com", "secret".toCharArray())))) {
				SmtpException exception = assertThrows(SmtpException.class, () -> client.connect(HOST, server.port()));
				assertEquals(NetworkErrorType.AUTHENTICATION_FAILED, exception.errorType());
			}
		}
	}
	
	@Test
	void authLoginRejectedThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("535 Authentication rejected");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.Login("user@example.com", "secret".toCharArray())))) {
				SmtpException exception = assertThrows(SmtpException.class, () -> client.connect(HOST, server.port()));
				assertEquals(NetworkErrorType.AUTHENTICATION_FAILED, exception.errorType());
			}
		}
	}
	
	@Test
	void authOAuthRejectedThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("535 Token rejected");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.OAuth("user@example.com", "token".toCharArray())))) {
				SmtpException exception = assertThrows(SmtpException.class, () -> client.connect(HOST, server.port()));
				assertEquals(NetworkErrorType.AUTHENTICATION_FAILED, exception.errorType());
			}
		}
	}
	
	@Test
	void ehloAndHeloBothRejectedThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			session.send("220 smtp.test ready");
			session.readLine();
			session.send("502 Command not implemented");
			session.readLine();
			session.send("504 Command parameter not implemented");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				assertThrows(SmtpException.class, () -> client.connect(HOST, server.port()));
			}
		}
	}
	
	@Test
	void sendMailFromRejectedThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("550 Mailbox unavailable");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertThrows(SmtpException.class, () -> client.send(simpleMessage()));
			}
		}
	}
	
	@Test
	void sendRcptRejectedThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("250 OK");
			session.readLine();
			session.send("550 No such user");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertThrows(SmtpException.class, () -> client.send(simpleMessage()));
			}
		}
	}
	
	@Test
	void sendDataRejectedThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("250 OK");
			session.readLine();
			session.send("250 OK");
			session.readLine();
			session.send("503 Bad sequence");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertThrows(SmtpException.class, () -> client.send(simpleMessage()));
			}
		}
	}
	
	@Test
	void sendFinalDotRejectedThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("250 OK");
			session.readLine();
			session.send("250 OK");
			session.readLine();
			session.send("354 Start mail input");
			session.readDataRaw();
			session.send("554 Transaction failed");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertThrows(SmtpException.class, () -> client.send(simpleMessage()));
			}
		}
	}
	
	@Test
	void readReplyConnectionClosedThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, _ -> {})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(HOST, server.port()));
				assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
			}
		}
	}
	
	@Test
	void readReplyShortLineThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> session.send("OK"))) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(HOST, server.port()));
				assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
			}
		}
	}
	
	@Test
	void readReplyNonNumericCodeThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> session.send("XYZ ready"))) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(HOST, server.port()));
				assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
			}
		}
	}
	
	@Test
	void readReplyTimeoutThrows() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, _ -> Thread.sleep(1500))) {
			SmtpClientConfig config = plaintextBuilder(new SmtpAuth.None())
				.tlsConfig(tlsBuilder().readTimeout(Duration.ofMillis(300)).build()).build();
			
			try (SmtpClient client = new SmtpClient(config)) {
				NetworkTimeoutException exception = assertThrows(NetworkTimeoutException.class, () -> client.connect(HOST, server.port()));
				assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
			}
		}
	}
	
	@Test
	void readReplyIoErrorMapsToIoError() throws Exception {
		AtomicReference<NetworkErrorType> errorRef = new AtomicReference<>();
		try (FakeSmtpServer server = new FakeSmtpServer(false, Session::abort)) {
			SmtpClientConfig config = plaintextBuilder(new SmtpAuth.None()).onError((type, _, _) -> errorRef.set(type)).build();
			
			try (SmtpClient client = new SmtpClient(config)) {
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(HOST, server.port()));
				assertEquals(NetworkErrorType.IO_ERROR, exception.errorType());
				assertEquals(NetworkErrorType.IO_ERROR, errorRef.get());
			}
		}
	}
	
	@Test
	void sendWriteLineIoErrorMapsToIoError() throws Exception {
		AtomicReference<NetworkErrorType> errorRef = new AtomicReference<>();
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.abort();
		})) {
			SmtpClientConfig config = plaintextBuilder(new SmtpAuth.None()).onError((type, _, _) -> errorRef.set(type)).build();
			
			try (SmtpClient client = new SmtpClient(config)) {
				client.connect(HOST, server.port());
				Thread.sleep(200);
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(simpleMessage()));
				assertEquals(NetworkErrorType.IO_ERROR, exception.errorType());
				assertEquals(NetworkErrorType.IO_ERROR, errorRef.get());
			}
		}
	}
	
	@Test
	void sendWriteDataIoErrorMapsToIoError() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("250 OK");
			session.readLine();
			session.send("250 OK");
			session.readLine();
			session.send("354 Start mail input");
			session.abort();
		})) {
			SmtpClientConfig config = plaintextBuilder(new SmtpAuth.None()).build();
			
			try (SmtpClient client = new SmtpClient(config)) {
				client.connect(HOST, server.port());
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(simpleMessage()));
				assertEquals(NetworkErrorType.IO_ERROR, exception.errorType());
			}
		}
	}
	
	@Test
	void connectPlaintextUsesPlainSocket() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	@Tag("network")
	@Tag("tls")
	void connectImplicitTlsUsesSslSocket() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(true, SmtpClientTest::handshakeNone)) {
			try (SmtpClient client = new SmtpClient(implicitTlsConfig(new SmtpAuth.None(), tlsBuilder().sslContext(sslContext).verifyHostname(false)))) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	@Tag("network")
	@Tag("tls")
	void connectImplicitTlsWithDefaultTlsParamsSkipsOptionalConfig() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(true, SmtpClientTest::handshakeNone)) {
			SmtpClientConfig config = implicitTlsConfig(new SmtpAuth.None(), tlsBuilder().sslContext(sslContext).verifyHostname(false));
			try (SmtpClient client = new SmtpClient(config)) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	@Tag("network")
	@Tag("tls")
	void connectImplicitTlsAppliesConfiguredProtocolsCiphersAndHostnameVerification() throws Exception {
		AtomicReference<String> protocol = new AtomicReference<>();
		try (FakeSmtpServer server = new FakeSmtpServer(true, session -> {
			session.send("220 smtp.test ready");
			protocol.set(session.tlsProtocol());
			session.readLine();
			session.send("250 smtp.test");
		})) {
			SslClientConfig tls = tlsBuilder().sslContext(sslContext).verifyHostname(true)
				.enabledProtocols(List.of("TLSv1.2")).enabledCipherSuites(List.of("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256")).build();
			SmtpClientConfig config = SmtpClientConfig.builder().security(SmtpSecurity.IMPLICIT_TLS).auth(new SmtpAuth.None())
				.ehloHostname("client.test").tlsConfig(tls).build();
			
			try (SmtpClient client = new SmtpClient(config)) {
				client.connect("localhost", server.port());
				assertTrue(client.isConnected());
				assertEquals("TLSv1.2", protocol.get());
			}
		}
	}
	
	@Test
	void connectAppliesReadTimeoutWhenNonZero() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			session.send("220 smtp.test ready");
			session.readLine();
			Thread.sleep(800);
		})) {
			SmtpClientConfig config = plaintextBuilder(new SmtpAuth.None())
				.tlsConfig(tlsBuilder().readTimeout(Duration.ofMillis(300)).build()).build();
			
			try (SmtpClient client = new SmtpClient(config)) {
				NetworkTimeoutException exception = assertThrows(NetworkTimeoutException.class, () -> client.connect(HOST, server.port()));
				assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
			}
		}
	}
	
	@Test
	void connectSkipsReadTimeoutWhenZero() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			session.send("220 smtp.test ready");
			session.readLine();
			Thread.sleep(400);
			session.send("250 smtp.test");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				assertDoesNotThrow(() -> client.connect(HOST, server.port()));
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	@Tag("network")
	@Tag("tls")
	void connectStartTlsUpgradesConnection() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> startTlsHandshake(session, "STARTTLS"))) {
			try (SmtpClient client = new SmtpClient(startTlsConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	@Tag("network")
	void connectSocketTimeoutMapped() {
		SmtpClientConfig config = plaintextBuilder(new SmtpAuth.None())
			.tlsConfig(tlsBuilder().connectTimeout(Duration.ofMillis(300)).build()).build();
		
		try (SmtpClient client = new SmtpClient(config)) {
			NetworkTimeoutException exception = assertThrows(NetworkTimeoutException.class, () -> client.connect("10.255.255.1", 587));
			assertEquals(NetworkErrorType.CONNECTION_TIMEOUT, exception.errorType());
		}
	}
	
	@Test
	@Tag("network")
	@Tag("tls")
	void connectSslHandshakeMapped() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(true, session -> session.send("220 smtp.test ready"))) {
			// A client that does not trust the self-signed server certificate must fail the handshake.
			SmtpClientConfig config = implicitTlsConfig(new SmtpAuth.None(), tlsBuilder().verifyHostname(false));
			try (SmtpClient client = new SmtpClient(config)) {
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(HOST, server.port()));
				assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			}
		}
	}
	
	@Test
	void connectRethrowsNetworkConnectionException() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> session.send("554 Service unavailable"))) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				// A bad greeting throws an SmtpException inside the try; the passthrough branch must not re-wrap it as CONNECTION_FAILED.
				SmtpException exception = assertThrows(SmtpException.class, () -> client.connect(HOST, server.port()));
				assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
			}
		}
	}
	
	@Test
	@Tag("network")
	@Tag("tls")
	void hasCapabilityMatchesStartTlsWithParams() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> startTlsHandshake(session, "STARTTLS SOMEPARAM"))) {
			try (SmtpClient client = new SmtpClient(startTlsConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	@Tag("network")
	@Tag("tls")
	void hasCapabilityCaseInsensitive() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> startTlsHandshake(session, "starttls"))) {
			try (SmtpClient client = new SmtpClient(startTlsConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	void ehloFallsBackToHelo() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			session.send("220 smtp.test ready");
			session.readLine();
			session.send("502 EHLO not supported");
			session.readLine();
			session.send("250 smtp.test");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
				assertTrue(server.received().stream().anyMatch(line -> line.startsWith("HELO ")));
			}
		}
	}
	
	@Test
	void resolveEhloUsesConfiguredHostname() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertTrue(server.received().contains("EHLO client.test"));
			}
		}
	}
	
	@Test
	void resolveEhloResolvesLocalHostWhenUnset() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			SmtpClientConfig config = plaintextBuilder(new SmtpAuth.None()).ehloHostname(null).build();
			try (SmtpClient client = new SmtpClient(config)) {
				client.connect(HOST, server.port());
				String ehlo = server.received().stream().filter(line -> line.startsWith("EHLO ")).findFirst().orElseThrow();
				assertFalse(ehlo.substring(5).isBlank());
			}
		}
	}
	
	@Test
	void authenticateNoneSkipsExchange() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
				assertTrue(server.received().stream().noneMatch(line -> line.startsWith("AUTH")));
			}
		}
	}
	
	@Test
	void authenticatePlainSendsToken() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("235 Authentication successful");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.Plain("user@example.com", "secret".toCharArray())))) {
				client.connect(HOST, server.port());
				
				String command = server.received().stream().filter(line -> line.startsWith("AUTH PLAIN ")).findFirst().orElseThrow();
				byte[] decoded = Base64.getDecoder().decode(command.substring("AUTH PLAIN ".length()));
				assertEquals(" user@example.com secret", new String(decoded, StandardCharsets.UTF_8));
			}
		}
	}
	
	@Test
	void authenticateLoginSendsUserThenPass() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("334 VXNlcm5hbWU6");
			session.readLine();
			session.send("334 UGFzc3dvcmQ6");
			session.readLine();
			session.send("235 Authentication successful");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.Login("user@example.com", "secret".toCharArray())))) {
				client.connect(HOST, server.port());
				
				assertTrue(server.received().contains("AUTH LOGIN"));
				String user = Base64.getEncoder().encodeToString("user@example.com".getBytes(StandardCharsets.UTF_8));
				String pass = Base64.getEncoder().encodeToString("secret".getBytes(StandardCharsets.UTF_8));
				assertTrue(server.received().contains(user));
				assertTrue(server.received().contains(pass));
			}
		}
	}
	
	@Test
	void authenticateOAuthSendsBearer() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("235 Authentication successful");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.OAuth("user@example.com", "token123".toCharArray())))) {
				client.connect(HOST, server.port());
				
				String command = server.received().stream().filter(line -> line.startsWith("AUTH XOAUTH2 ")).findFirst().orElseThrow();
				byte[] decoded = Base64.getDecoder().decode(command.substring("AUTH XOAUTH2 ".length()));
				assertEquals("user=user@example.comauth=Bearer token123", new String(decoded, StandardCharsets.UTF_8));
			}
		}
	}
	
	@Test
	void sendWithSingleRecipient() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			acceptTransaction(session, 1);
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				client.send(simpleMessage());
				assertEquals(1, server.received().stream().filter(line -> line.startsWith("RCPT TO")).count());
			}
		}
	}
	
	@Test
	void sendWithMultipleRecipients() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			acceptTransaction(session, 3);
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				client.send(multiRecipientMessage());
				assertEquals(3, server.received().stream().filter(line -> line.startsWith("RCPT TO")).count());
			}
		}
	}
	
	@Test
	void sendRcptAccepts251() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("250 OK");
			session.readLine();
			session.send("251 User not local; will forward");
			session.readLine();
			session.send("354 Start mail input");
			session.readDataRaw();
			session.send("250 Queued");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertDoesNotThrow(() -> client.send(simpleMessage()));
			}
		}
	}
	
	@Test
	void readReplyParsesMultilineReply() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			session.send("220 smtp.test ready");
			session.readLine();
			session.send("250-smtp.test");
			session.send("250-PIPELINING");
			session.send("250 8BITMIME");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	void readReplyLineExactlyThreeChars() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			session.send("220");
			session.readLine();
			session.send("250 smtp.test");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				assertDoesNotThrow(() -> client.connect(HOST, server.port()));
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	void readReplyLineFourChars() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			session.send("220-");
			session.send("220 x");
			session.readLine();
			session.send("250 smtp.test");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				assertDoesNotThrow(() -> client.connect(HOST, server.port()));
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	void sendDotStuffsLeadingDotLines() throws Exception {
		AtomicReference<String> body = new AtomicReference<>();
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			body.set(acceptTransaction(session, 1));
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				client.send(messageWithBody("Line1\n.hidden\nLine3"));
				server.awaitScript();
				assertTrue(body.get().contains("..hidden"));
			}
		}
	}
	
	@Test
	void sendDoesNotStuffNonLeadingDots() throws Exception {
		AtomicReference<String> body = new AtomicReference<>();
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			body.set(acceptTransaction(session, 1));
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				client.send(messageWithBody("aXbXc".replace("X", ".")));
				server.awaitScript();
				assertTrue(body.get().contains("a.b.c"));
				assertFalse(body.get().contains("a..b"));
			}
		}
	}
	
	@Test
	void sendNoExtraCrlfWhenBodyEndsWithCrlf() throws Exception {
		AtomicReference<String> body = new AtomicReference<>();
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			body.set(acceptTransaction(session, 1));
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				client.send(simpleMessage());
				server.awaitScript();
				// The serialized message already ends with CRLF, so no blank line is inserted before the dot terminator.
				assertTrue(body.get().endsWith("\r\n.\r\n"));
				assertFalse(body.get().endsWith("\r\n\r\n.\r\n"));
			}
		}
	}
	
	@Test
	void isConnectedFalseBeforeConnect() {
		SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
		assertFalse(client.isConnected());
	}
	
	@Test
	void isConnectedTrueWhenConnectedAndOpen() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	void isConnectedFalseAfterClose() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
			client.connect(HOST, server.port());
			client.close();
			assertFalse(client.isConnected());
		}
	}
	
	@Test
	void closeSendsQuitWhenConnected() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("221 Bye");
		})) {
			SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
			client.connect(HOST, server.port());
			client.close();
			
			assertFalse(client.isConnected());
			assertTrue(server.received().contains("QUIT"));
		}
	}
	
	@Test
	void closeIgnoresQuitFailure() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.abort();
		})) {
			SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
			client.connect(HOST, server.port());
			Thread.sleep(200);
			
			assertDoesNotThrow(client::close);
			assertFalse(client.isConnected());
		}
	}
	
	@Test
	void closeIdempotentWhenAlreadyClosed() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
			client.connect(HOST, server.port());
			client.close();
			
			assertDoesNotThrow(client::close);
			assertFalse(client.isConnected());
		}
	}
	
	@Test
	void connectPlaintextNoAuthSucceeds() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	void sendSimpleTextMessage() throws Exception {
		AtomicReference<String> body = new AtomicReference<>();
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			body.set(acceptTransaction(session, 1));
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertDoesNotThrow(() -> client.send(simpleMessage()));
				server.awaitScript();
				
				assertTrue(server.received().stream().anyMatch(line -> line.startsWith("MAIL FROM")));
				assertEquals(1, server.received().stream().filter(line -> line.startsWith("RCPT TO")).count());
				assertTrue(server.received().contains("DATA"));
				assertTrue(body.get().endsWith("\r\n.\r\n"));
			}
		}
	}
	
	@Test
	void connectThenIsConnectedTrue() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
				assertTrue(server.received().stream().anyMatch(line -> line.startsWith("EHLO")));
			}
		}
	}
	
	@Test
	void closeAfterConnectClearsState() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
			client.connect(HOST, server.port());
			client.close();
			assertFalse(client.isConnected());
		}
	}
	
	@Test
	void reconnectAfterCloseSucceeds() throws Exception {
		try (FakeSmtpServer first = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
			SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()));
			client.connect(HOST, first.port());
			client.close();
			
			try (FakeSmtpServer second = new FakeSmtpServer(false, SmtpClientTest::handshakeNone)) {
				assertDoesNotThrow(() -> client.connect(HOST, second.port()));
				assertTrue(client.isConnected());
				client.close();
			}
		}
	}
	
	@Test
	void sendMessageWithUtf8Body() throws Exception {
		AtomicReference<String> body = new AtomicReference<>();
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			body.set(acceptTransaction(session, 1));
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				client.send(messageWithBody("Grüße"));
				server.awaitScript();
				// The transport encodes with the configured charset; the non-ASCII text is quoted-printable encoded by the serializer.
				assertTrue(body.get().contains("=C3="));
			}
		}
	}
	
	@Test
	void fullPlainAuthTransaction() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			session.send("220 smtp.test ready");
			session.readLine();
			session.send("250-smtp.test");
			session.send("250 AUTH PLAIN LOGIN");
			session.readLine();
			session.send("235 Authentication successful");
			acceptTransaction(session, 3);
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.Plain("user@example.com", "secret".toCharArray())))) {
				client.connect(HOST, server.port());
				assertDoesNotThrow(() -> client.send(multiRecipientMessage()));
				assertEquals(3, server.received().stream().filter(line -> line.startsWith("RCPT TO")).count());
			}
		}
	}
	
	@Test
	void fullLoginAuthTransaction() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("334 VXNlcm5hbWU6");
			session.readLine();
			session.send("334 UGFzc3dvcmQ6");
			session.readLine();
			session.send("235 Authentication successful");
			acceptTransaction(session, 1);
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.Login("user@example.com", "secret".toCharArray())))) {
				client.connect(HOST, server.port());
				assertDoesNotThrow(() -> client.send(simpleMessage()));
				
				String pass = Base64.getEncoder().encodeToString("secret".getBytes(StandardCharsets.UTF_8));
				assertTrue(server.received().contains(pass));
			}
		}
	}
	
	@Test
	void fullOAuthTransaction() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("235 Authentication successful");
			acceptTransaction(session, 1);
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.OAuth("user@example.com", "token123".toCharArray())))) {
				client.connect(HOST, server.port());
				assertDoesNotThrow(() -> client.send(simpleMessage()));
				assertTrue(server.received().stream().anyMatch(line -> line.startsWith("AUTH XOAUTH2 ")));
			}
		}
	}
	
	@Test
	@Tag("network")
	@Tag("tls")
	void startTlsUpgradeThenSend() throws Exception {
		AtomicReference<String> body = new AtomicReference<>();
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			startTlsHandshake(session, "STARTTLS");
			body.set(acceptTransaction(session, 1));
		})) {
			try (SmtpClient client = new SmtpClient(startTlsConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertDoesNotThrow(() -> client.send(simpleMessage()));
				server.awaitScript();
				assertTrue(body.get().endsWith("\r\n.\r\n"));
			}
		}
	}
	
	@Test
	@Tag("network")
	@Tag("tls")
	void implicitTlsConnectThenSend() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(true, session -> {
			handshakeNone(session);
			acceptTransaction(session, 1);
		})) {
			try (SmtpClient client = new SmtpClient(implicitTlsConfig(new SmtpAuth.None(), tlsBuilder().sslContext(sslContext).verifyHostname(false)))) {
				client.connect(HOST, server.port());
				assertDoesNotThrow(() -> client.send(simpleMessage()));
				assertTrue(server.received().stream().anyMatch(line -> line.startsWith("MAIL FROM")));
			}
		}
	}
	
	@Test
	void reuseClientForMultipleMessages() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			acceptTransaction(session, 1);
			acceptTransaction(session, 1);
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				client.send(simpleMessage());
				assertTrue(client.isConnected());
				client.send(simpleMessage());
				
				assertEquals(2, server.received().stream().filter(line -> line.startsWith("MAIL FROM")).count());
			}
		}
	}
	
	@Test
	void sendMultipleRecipientsIncludingBcc() throws Exception {
		AtomicReference<String> body = new AtomicReference<>();
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			body.set(acceptTransaction(session, 3));
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				client.send(multiRecipientMessage());
				server.awaitScript();
				
				assertTrue(server.received().contains("RCPT TO:<dave@example.com>"));
				assertFalse(body.get().contains("dave@example.com"));
			}
		}
	}
	
	@Test
	@Tag("network")
	@Tag("tls")
	void multilineEhloCapabilitiesDriveStartTls() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			session.send("220 smtp.test ready");
			session.readLine();
			session.send("250-smtp.test");
			session.send("250-PIPELINING");
			session.send("250-SIZE 10240000");
			session.send("250 STARTTLS");
			session.readLine();
			session.send("220 Ready to start TLS");
			session.startTls(sslContext);
			session.readLine();
			session.send("250 smtp.test");
		})) {
			try (SmtpClient client = new SmtpClient(startTlsConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertTrue(client.isConnected());
			}
		}
	}
	
	@Test
	void sendBodyWithLeadingDotLinesDotStuffed() throws Exception {
		AtomicReference<String> body = new AtomicReference<>();
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			body.set(acceptTransaction(session, 1));
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				client.send(messageWithBody(".first\n.second\nmid.dle"));
				server.awaitScript();
				
				assertTrue(body.get().contains("..first"));
				assertTrue(body.get().contains("..second"));
				assertTrue(body.get().contains("mid.dle"));
				assertTrue(body.get().endsWith("\r\n.\r\n"));
			}
		}
	}
	
	@Test
	void abortedTransactionSurfacesServerRejection() throws Exception {
		try (FakeSmtpServer server = new FakeSmtpServer(false, session -> {
			handshakeNone(session);
			session.readLine();
			session.send("250 OK");
			session.readLine();
			session.send("550 No such user");
		})) {
			try (SmtpClient client = new SmtpClient(plaintextConfig(new SmtpAuth.None()))) {
				client.connect(HOST, server.port());
				assertThrows(SmtpException.class, () -> client.send(simpleMessage()));
				assertTrue(client.isConnected());
			}
		}
	}
	
	//region Loopback SMTP server harness
	@FunctionalInterface
	private interface ServerScript {
		
		void run(Session session) throws Exception;
	}
	
	private static final class Session {
		
		private final List<String> received;
		private Socket socket;
		private InputStream in;
		private OutputStream out;
		
		private Session(Socket socket, List<String> received) throws IOException {
			this.socket = socket;
			this.in = socket.getInputStream();
			this.out = socket.getOutputStream();
			this.received = received;
		}
		
		private String readLine() throws IOException {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int read;
			while ((read = this.in.read()) != -1) {
				if (read == '\n') {
					break;
				}
				if (read == '\r') {
					continue;
				}
				buffer.write(read);
			}
			if (read == -1 && buffer.size() == 0) {
				return null;
			}
			String line = buffer.toString(StandardCharsets.ISO_8859_1);
			this.received.add(line);
			return line;
		}
		
		private String readDataRaw() throws IOException {
			StringBuilder builder = new StringBuilder();
			int read;
			while ((read = this.in.read()) != -1) {
				builder.append((char) (read & 0xFF));
				int length = builder.length();
				if (length >= 5 && "\r\n.\r\n".equals(builder.substring(length - 5))) {
					break;
				}
			}
			String raw = builder.toString();
			this.received.add(raw);
			return raw;
		}
		
		private void send(String line) throws IOException {
			this.out.write((line + "\r\n").getBytes(StandardCharsets.ISO_8859_1));
			this.out.flush();
		}
		
		private void startTls(SSLContext context) throws IOException {
			SSLSocket sslSocket = (SSLSocket) context.getSocketFactory().createSocket(this.socket, null, this.socket.getPort(), true);
			sslSocket.setUseClientMode(false);
			sslSocket.startHandshake();
			this.socket = sslSocket;
			this.in = sslSocket.getInputStream();
			this.out = sslSocket.getOutputStream();
		}
		
		private String tlsProtocol() {
			return ((SSLSocket) this.socket).getSession().getProtocol();
		}
		
		private void abort() throws Exception {
			this.socket.setSoLinger(true, 0);
			Thread.sleep(100);
			this.socket.close();
		}
	}
	
	private static final class FakeSmtpServer implements AutoCloseable {
		
		private final ServerSocket serverSocket;
		private final int port;
		private final Thread thread;
		private final List<String> received = Collections.synchronizedList(new ArrayList<>());
		private volatile Socket clientSocket;
		
		private FakeSmtpServer(boolean implicitTls, ServerScript script) throws Exception {
			if (implicitTls) {
				SSLServerSocket socket = (SSLServerSocket) sslContext.getServerSocketFactory().createServerSocket();
				socket.bind(new InetSocketAddress(HOST, 0));
				this.serverSocket = socket;
			} else {
				this.serverSocket = new ServerSocket();
				this.serverSocket.bind(new InetSocketAddress(HOST, 0));
			}
			this.port = this.serverSocket.getLocalPort();
			this.thread = new Thread(() -> {
				Socket accepted = null;
				try {
					accepted = this.serverSocket.accept();
					this.clientSocket = accepted;
					Session session = new Session(accepted, this.received);
					script.run(session);
				} catch (Exception ignored) {
				} finally {
					if (accepted != null) {
						try {
							accepted.close();
						} catch (IOException ignored) {}
					}
				}
			});
			this.thread.setDaemon(true);
			this.thread.start();
		}
		
		private int port() {
			return this.port;
		}
		
		private List<String> received() {
			return this.received;
		}
		
		// Waits for the scripted handler to finish, so state it publishes after the final reply (e.g. the captured DATA body) is visible to assertions.
		private void awaitScript() throws InterruptedException {
			this.thread.join(TimeUnit.SECONDS.toMillis(5));
		}
		
		@Override
		public void close() throws Exception {
			this.thread.interrupt();
			try {
				this.serverSocket.close();
			} catch (IOException ignored) {}
			Socket socket = this.clientSocket;
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException ignored) {}
			}
		}
	}
	//endregion
}
