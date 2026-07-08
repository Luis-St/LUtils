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

import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.exception.*;
import net.luis.utils.io.network.connection.ssl.SslClientConfig;
import net.luis.utils.io.network.mail.message.MailMessage;
import net.luis.utils.io.network.mail.message.MailRecipient;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * A blocking, RFC 5321 compliant SMTP submission client built on the SSL/TLS transport stack.<br>
 * The client manages its own {@link Socket}/{@link SSLSocket} rather than wrapping an existing SSL client,<br>
 * because the {@code STARTTLS} flow requires upgrading a live plaintext socket to TLS.<br>
 * <br>
 * It still reuses the {@link SslClientConfig} carried by its {@link SmtpClientConfig} for all TLS<br>
 * parameters (SSL context, protocols, cipher suites, timeouts, and hostname verification).<br>
 * <p>
 *     A connection follows the standard submission flow: read the greeting, {@code EHLO},<br>
 *     optionally upgrade via {@code STARTTLS}, and authenticate.<br>
 *     Each {@link #send(MailMessage)} call runs one mail transaction and the client may be reused for multiple messages on one connection.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SmtpClientConfig config = SmtpClientConfig.builder()
 *     .security(SmtpSecurity.STARTTLS)
 *     .auth(new SmtpAuth.Login("user@example.com", password))
 *     .build();
 *
 * try (SmtpClient client = new SmtpClient(config)) {
 *     client.connect("smtp.example.com", 587);
 *     client.send(message);
 * }
 * }</pre>
 *
 * @see SmtpClientConfig
 * @see SmtpSecurity
 * @see SmtpAuth
 *
 * @author Luis-St
 */
public final class SmtpClient implements AutoCloseable {
	
	/**
	 * The configuration for this client.<br>
	 */
	private final @NonNull SmtpClientConfig config;
	/**
	 * The underlying socket, which is upgraded to an {@link SSLSocket} for TLS.<br>
	 */
	private volatile Socket socket;
	/**
	 * The buffered reader over the socket input, used to read replies as ASCII lines.<br>
	 */
	private volatile BufferedReader reader;
	/**
	 * The buffered output stream over the socket, used to write commands and message data.<br>
	 */
	private volatile OutputStream output;
	/**
	 * The host this client is connected to.<br>
	 */
	private volatile String host = "";
	/**
	 * The port this client is connected to.<br>
	 */
	private volatile int port;
	/**
	 * Whether this client is currently connected.<br>
	 */
	private volatile boolean connected;
	
	/**
	 * Constructs a new SMTP client with default configuration.<br>
	 */
	public SmtpClient() {
		this(SmtpClientConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new SMTP client with the specified configuration.<br>
	 *
	 * @param config The client configuration
	 * @throws NullPointerException If config is null
	 */
	public SmtpClient(@NonNull SmtpClientConfig config) {
		this.config = Objects.requireNonNull(config, "Config must not be null");
	}
	
	/**
	 * Returns whether the given capability lines advertise the named capability.<br>
	 *
	 * @param capabilities The advertised capability lines
	 *
	 * @return True if the capability is advertised
	 */
	private static boolean hasCapability(@NonNull List<String> capabilities) {
		for (String line : capabilities) {
			String upper = line.toUpperCase(Locale.ROOT);
			if ("STARTTLS".equals(upper) || upper.startsWith("STARTTLS" + " ")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Encodes the given characters to bytes with the given charset without an intermediate string.<br>
	 *
	 * @param chars The characters to encode
	 * @param charset The charset to encode with
	 * @return The encoded bytes
	 */
	private static byte @NonNull [] toBytes(char @NonNull [] chars, @NonNull Charset charset) {
		ByteBuffer buffer = charset.encode(CharBuffer.wrap(chars));
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return bytes;
	}
	
	/**
	 * Concatenates two byte arrays into a new array.<br>
	 *
	 * @param prefix The prefix bytes
	 * @param suffix The suffix bytes
	 * @return The concatenated array
	 */
	private static byte @NonNull [] concat(byte @NonNull [] prefix, byte @NonNull [] suffix) {
		byte[] result = new byte[prefix.length + suffix.length];
		System.arraycopy(prefix, 0, result, 0, prefix.length);
		System.arraycopy(suffix, 0, result, prefix.length, suffix.length);
		return result;
	}
	
	//region Helper methods
	
	/**
	 * Applies SMTP dot-stuffing to the given message data.<br>
	 * Any line beginning with a dot has an extra dot prepended (RFC 5321 section 4.5.2).<br>
	 *
	 * @param data The message data
	 * @return The dot-stuffed data
	 */
	private static @NonNull String dotStuff(@NonNull String data) {
		StringBuilder sb = new StringBuilder(data.length() + 16);
		boolean lineStart = true;
		
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			
			if (lineStart && c == '.') {
				sb.append('.');
			}
			
			sb.append(c);
			lineStart = c == '\n';
		}
		return sb.toString();
	}
	
	/**
	 * Connects to the specified SMTP server and performs the greeting, EHLO, optional STARTTLS
	 * upgrade, and authentication handshake according to the configured security mode.<br>
	 *
	 * @param host The server host to connect to
	 * @param port The server port to connect to
	 * @throws NullPointerException If host is null
	 * @throws NetworkConnectionException If the connection, TLS handshake, or SMTP handshake fails
	 * @throws NetworkTimeoutException If the connection times out
	 * @throws SmtpException If the server returns an unexpected reply or authentication fails
	 */
	public void connect(@NonNull String host, int port) throws NetworkConnectionException {
		Objects.requireNonNull(host, "Host must not be null");
		if (this.connected) {
			throw new NetworkConnectionException("Client is already connected", NetworkErrorType.ALREADY_CONNECTED);
		}
		this.host = host;
		this.port = port;
		SslClientConfig tls = this.config.tlsConfig();
		
		try {
			Socket transport;
			if (this.config.security() == SmtpSecurity.IMPLICIT_TLS) {
				SSLSocket sslSocket = (SSLSocket) tls.resolveSslContext().getSocketFactory().createSocket();
				this.configureSslSocket(sslSocket);
				transport = sslSocket;
			} else {
				transport = new Socket();
			}
			
			this.socket = transport;
			transport.setTcpNoDelay(tls.tcpNoDelay());
			transport.setKeepAlive(tls.keepAlive());
			transport.connect(new InetSocketAddress(host, port), (int) tls.connectTimeout().toMillis());
			
			if (!tls.readTimeout().isZero()) {
				transport.setSoTimeout((int) tls.readTimeout().toMillis());
			}
			if (transport instanceof SSLSocket sslSocket) {
				sslSocket.startHandshake();
			}
			
			this.initStreams();
			this.connected = true;
			
			this.expect(this.readReply(), 220);
			List<String> capabilities = this.ehlo();
			
			if (this.config.security() == SmtpSecurity.STARTTLS) {
				if (!hasCapability(capabilities)) {
					SmtpReply reply = new SmtpReply(454, List.of("STARTTLS not advertised by server"));
					throw new SmtpException("Server does not advertise STARTTLS", reply, NetworkErrorType.PROTOCOL_ERROR, null);
				}
				
				this.sendCommand("STARTTLS");
				this.expect(this.readReply(), 220);
				this.upgradeToTls();
				capabilities = this.ehlo();
			}
			
			this.authenticate();
		} catch (NetworkConnectionException e) {
			throw e;
		} catch (SocketTimeoutException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.CONNECTION_TIMEOUT, "Connection timed out to " + host + ":" + port, e);
			throw new NetworkTimeoutException("Connection timed out to " + host + ":" + port, NetworkErrorType.CONNECTION_TIMEOUT, tls.connectTimeout());
		} catch (SSLHandshakeException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.HANDSHAKE_FAILED, "SSL handshake failed with " + host + ":" + port, e);
			throw new NetworkConnectionException("SSL handshake failed with " + host + ":" + port, e, NetworkErrorType.HANDSHAKE_FAILED);
		} catch (ConnectException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.CONNECTION_REFUSED, "Connection refused by " + host + ":" + port, e);
			throw new NetworkConnectionException("Connection refused by " + host + ":" + port, e, NetworkErrorType.CONNECTION_REFUSED);
		} catch (NoRouteToHostException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.HOST_UNREACHABLE, "Host unreachable: " + host + ":" + port, e);
			throw new NetworkConnectionException("Host unreachable: " + host + ":" + port, e, NetworkErrorType.HOST_UNREACHABLE);
		} catch (NoSuchAlgorithmException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.CONNECTION_FAILED, "Failed to initialize SSL context", e);
			throw new NetworkConnectionException("Failed to initialize SSL context", e, NetworkErrorType.CONNECTION_FAILED);
		} catch (IOException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.CONNECTION_FAILED, "Failed to connect to " + host + ":" + port, e);
			throw new NetworkConnectionException("Failed to connect to " + host + ":" + port, e, NetworkErrorType.CONNECTION_FAILED);
		}
	}
	
	/**
	 * Sends the given message as a single mail transaction.<br>
	 * Every recipient, including blind carbon-copy recipients, is submitted with {@code RCPT TO},
	 * and the serialized message is written with SMTP dot-stuffing applied and terminated by
	 * {@code CRLF.CRLF}.<br>
	 *
	 * @param message The message to send
	 * @throws NullPointerException If message is null
	 * @throws NetworkConnectionException If the client is not connected or an I/O error occurs
	 * @throws SmtpException If the server rejects any command in the transaction
	 */
	public void send(@NonNull MailMessage message) throws NetworkConnectionException {
		Objects.requireNonNull(message, "Message must not be null");
		this.ensureConnected();
		
		this.sendCommand("MAIL FROM:<" + message.from().address() + ">");
		this.expect(this.readReply(), 250);
		
		for (MailRecipient recipient : message.recipients()) {
			this.sendCommand("RCPT TO:<" + recipient.mailbox().address() + ">");
			this.expect(this.readReply(), 250, 251);
		}
		
		this.sendCommand("DATA");
		this.expect(this.readReply(), 354);
		
		this.writeData(message.toRfc5322());
		this.expect(this.readReply(), 250);
	}
	
	/**
	 * Returns whether this client is currently connected.<br>
	 * @return True if the client holds an open connection
	 */
	public boolean isConnected() {
		return this.connected && !this.socket.isClosed();
	}
	
	/**
	 * Sends a best-effort {@code QUIT} command and closes the underlying socket.<br>
	 */
	@Override
	public void close() {
		if (!this.socket.isClosed()) {
			if (this.connected) {
				try {
					this.sendCommand("QUIT");
					this.readReply();
				} catch (NetworkConnectionException _) {}
			}
			try {
				this.socket.close();
			} catch (IOException _) {}
		}
		this.connected = false;
	}
	
	/**
	 * Initializes the buffered reader and output stream from the current socket.<br>
	 * The reader decodes replies as ISO-8859-1 so any byte sequence maps cleanly to characters.<br>
	 *
	 * @throws IOException If the socket streams cannot be obtained
	 */
	private void initStreams() throws IOException {
		this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.ISO_8859_1));
		this.output = new BufferedOutputStream(this.socket.getOutputStream());
	}
	
	/**
	 * Applies the configured TLS protocols, cipher suites, and hostname verification to the socket.<br>
	 *
	 * @param sslSocket The SSL socket to configure
	 */
	private void configureSslSocket(@NonNull SSLSocket sslSocket) {
		SslClientConfig tls = this.config.tlsConfig();
		if (!tls.enabledProtocols().isEmpty()) {
			sslSocket.setEnabledProtocols(tls.enabledProtocols().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
		}
		if (!tls.enabledCipherSuites().isEmpty()) {
			sslSocket.setEnabledCipherSuites(tls.enabledCipherSuites().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
		}
		if (tls.verifyHostname()) {
			SSLParameters parameters = sslSocket.getSSLParameters();
			parameters.setEndpointIdentificationAlgorithm("HTTPS");
			sslSocket.setSSLParameters(parameters);
		}
	}
	
	/**
	 * Upgrades the current plaintext socket to TLS by layering an {@link SSLSocket} over it.<br>
	 *
	 * @throws IOException If the TLS handshake fails
	 * @throws NoSuchAlgorithmException If the SSL context cannot be resolved
	 */
	private void upgradeToTls() throws IOException, NoSuchAlgorithmException {
		SSLSocket sslSocket = (SSLSocket) this.config.tlsConfig().resolveSslContext().getSocketFactory().createSocket(this.socket, this.host, this.port, true);
		this.configureSslSocket(sslSocket);
		sslSocket.startHandshake();
		this.socket = sslSocket;
		this.initStreams();
	}
	
	/**
	 * Issues the {@code EHLO} command, falling back to {@code HELO} if the server rejects it.<br>
	 *
	 * @return The advertised capability lines, or an empty list when only HELO succeeded
	 * @throws NetworkConnectionException If reading or writing fails
	 * @throws SmtpException If both EHLO and HELO are rejected
	 */
	private @NonNull List<String> ehlo() throws NetworkConnectionException {
		String hostname = this.resolveEhloHostname();
		this.sendCommand("EHLO " + hostname);
		SmtpReply reply = this.readReply();
		
		if (reply.isPositiveCompletion()) {
			return reply.lines();
		}
		
		this.sendCommand("HELO " + hostname);
		this.expect(this.readReply(), 250);
		return List.of();
	}
	
	/**
	 * Resolves the hostname to announce in the EHLO command.<br>
	 * The configured hostname is used if set, otherwise the local canonical host name is resolved,
	 * falling back to {@code localhost}.<br>
	 *
	 * @return The EHLO hostname
	 */
	private @NonNull String resolveEhloHostname() {
		String configured = this.config.ehloHostname();
		if (configured != null && !configured.isBlank()) {
			return configured;
		}
		
		try {
			String canonical = InetAddress.getLocalHost().getCanonicalHostName();
			if (!canonical.isBlank()) {
				return canonical;
			}
		} catch (UnknownHostException _) {}
		return "localhost";
	}
	
	/**
	 * Runs the configured authentication mechanism, skipping the exchange for no authentication.<br>
	 *
	 * @throws NetworkConnectionException If reading or writing fails
	 * @throws SmtpException If authentication is rejected by the server
	 */
	private void authenticate() throws NetworkConnectionException {
		switch (this.config.auth()) {
			case SmtpAuth.None _ -> {}
			case SmtpAuth.Plain plain -> this.authPlain(plain);
			case SmtpAuth.Login login -> this.authLogin(login);
			case SmtpAuth.OAuth oauth -> this.authOAuth(oauth);
		}
	}
	
	/**
	 * Performs SASL {@code PLAIN} authentication.<br>
	 *
	 * @param auth The PLAIN authentication strategy
	 * @throws NetworkConnectionException If reading or writing fails
	 * @throws SmtpException If authentication is rejected by the server
	 */
	private void authPlain(SmtpAuth.@NonNull Plain auth) throws NetworkConnectionException {
		byte[] userBytes = auth.username().getBytes(this.config.defaultCharset());
		byte[] passwordBytes = toBytes(auth.password(), this.config.defaultCharset());
		byte[] token = new byte[1 + userBytes.length + 1 + passwordBytes.length];
		int pos = 0;
		token[pos++] = 0;
		System.arraycopy(userBytes, 0, token, pos, userBytes.length);
		pos += userBytes.length;
		token[pos++] = 0;
		System.arraycopy(passwordBytes, 0, token, pos, passwordBytes.length);
		byte[] command = concat("AUTH PLAIN ".getBytes(StandardCharsets.US_ASCII), Base64.getEncoder().encode(token));
		this.writeLine(command);
		this.expect(this.readReply(), NetworkErrorType.AUTHENTICATION_FAILED, 235);
	}
	
	/**
	 * Performs the legacy {@code AUTH LOGIN} exchange.<br>
	 *
	 * @param auth The LOGIN authentication strategy
	 * @throws NetworkConnectionException If reading or writing fails
	 * @throws SmtpException If authentication is rejected by the server
	 */
	private void authLogin(SmtpAuth.@NonNull Login auth) throws NetworkConnectionException {
		this.sendCommand("AUTH LOGIN");
		this.expect(this.readReply(), NetworkErrorType.AUTHENTICATION_FAILED, 334);
		this.writeLine(Base64.getEncoder().encode(auth.username().getBytes(this.config.defaultCharset())));
		this.expect(this.readReply(), NetworkErrorType.AUTHENTICATION_FAILED, 334);
		this.writeLine(Base64.getEncoder().encode(toBytes(auth.password(), this.config.defaultCharset())));
		this.expect(this.readReply(), NetworkErrorType.AUTHENTICATION_FAILED, 235);
	}
	
	/**
	 * Performs {@code XOAUTH2} bearer-token authentication.<br>
	 *
	 * @param auth The XOAUTH2 authentication strategy
	 * @throws NetworkConnectionException If reading or writing fails
	 * @throws SmtpException If authentication is rejected by the server
	 */
	private void authOAuth(SmtpAuth.@NonNull OAuth auth) throws NetworkConnectionException {
		byte[] head = ("user=" + auth.username() + "\u0001auth=Bearer ").getBytes(this.config.defaultCharset());
		byte[] tokenBytes = toBytes(auth.token(), this.config.defaultCharset());
		byte[] tail = "\u0001\u0001".getBytes(StandardCharsets.US_ASCII);
		byte[] payload = new byte[head.length + tokenBytes.length + tail.length];
		System.arraycopy(head, 0, payload, 0, head.length);
		System.arraycopy(tokenBytes, 0, payload, head.length, tokenBytes.length);
		System.arraycopy(tail, 0, payload, head.length + tokenBytes.length, tail.length);
		byte[] command = concat("AUTH XOAUTH2 ".getBytes(StandardCharsets.US_ASCII), Base64.getEncoder().encode(payload));
		this.writeLine(command);
		this.expect(this.readReply(), NetworkErrorType.AUTHENTICATION_FAILED, 235);
	}
	
	/**
	 * Reads a single, possibly multiline, SMTP reply from the server.<br>
	 *
	 * @return The parsed reply
	 * @throws NetworkConnectionException If the connection closes or an I/O error occurs
	 * @throws NetworkTimeoutException If the read times out
	 */
	private @NonNull SmtpReply readReply() throws NetworkConnectionException {
		try {
			List<String> lines = new ArrayList<>();
			int code = -1;
			while (true) {
				String line = this.reader.readLine();
				if (line == null) {
					throw new NetworkConnectionException("Connection closed while reading reply", NetworkErrorType.CONNECTION_RESET);
				}
				if (line.length() < 3) {
					throw new NetworkConnectionException("Malformed SMTP reply: " + line, NetworkErrorType.PROTOCOL_ERROR);
				}
				try {
					code = Integer.parseInt(line.substring(0, 3));
				} catch (NumberFormatException e) {
					throw new NetworkConnectionException("Malformed SMTP reply: " + line, e, NetworkErrorType.PROTOCOL_ERROR);
				}
				lines.add(line.length() > 4 ? line.substring(4) : "");
				char separator = line.length() > 3 ? line.charAt(3) : ' ';
				if (separator != '-') {
					break;
				}
			}
			return new SmtpReply(code, lines);
		} catch (SocketTimeoutException e) {
			throw new NetworkTimeoutException("Read timed out", NetworkErrorType.READ_TIMEOUT, this.config.tlsConfig().readTimeout());
		} catch (NetworkConnectionException e) {
			throw e;
		} catch (IOException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Failed to read reply", e);
			throw new NetworkConnectionException("Failed to read reply", e, NetworkErrorType.IO_ERROR);
		}
	}
	
	/**
	 * Sends an ASCII command line terminated by CRLF.<br>
	 *
	 * @param command The command to send, without the trailing CRLF
	 * @throws NetworkConnectionException If sending fails
	 */
	private void sendCommand(@NonNull String command) throws NetworkConnectionException {
		this.writeLine(command.getBytes(StandardCharsets.US_ASCII));
	}
	
	/**
	 * Writes the given bytes followed by CRLF and flushes the output.<br>
	 *
	 * @param line The bytes to write, without the trailing CRLF
	 * @throws NetworkConnectionException If writing fails
	 */
	private void writeLine(byte @NonNull [] line) throws NetworkConnectionException {
		try {
			this.output.write(line);
			this.output.write('\r');
			this.output.write('\n');
			this.output.flush();
		} catch (IOException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Failed to send command", e);
			throw new NetworkConnectionException("Failed to send command", e, NetworkErrorType.IO_ERROR);
		}
	}
	
	/**
	 * Writes the serialized message body during a {@code DATA} transaction.<br>
	 * Dot-stuffing is applied, and the body is terminated by a line containing a single dot.<br>
	 *
	 * @param data The serialized message
	 * @throws NetworkConnectionException If writing fails
	 */
	private void writeData(@NonNull String data) throws NetworkConnectionException {
		try {
			this.output.write(dotStuff(data).getBytes(this.config.defaultCharset()));
			if (!data.endsWith("\r\n")) {
				this.output.write('\r');
				this.output.write('\n');
			}
			this.output.write('.');
			this.output.write('\r');
			this.output.write('\n');
			this.output.flush();
		} catch (IOException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Failed to send message data", e);
			throw new NetworkConnectionException("Failed to send message data", e, NetworkErrorType.IO_ERROR);
		}
	}
	
	/**
	 * Verifies that the reply code matches one of the accepted codes, throwing otherwise.<br>
	 *
	 * @param reply The reply to check
	 * @param codes The accepted reply codes
	 * @throws SmtpException If the reply code is not accepted
	 */
	private void expect(@NonNull SmtpReply reply, int @NonNull ... codes) throws SmtpException {
		this.expect(reply, NetworkErrorType.PROTOCOL_ERROR, codes);
	}
	
	/**
	 * Verifies that the reply code matches one of the accepted codes, throwing otherwise.<br>
	 *
	 * @param reply The reply to check
	 * @param errorType The error type to attach to the thrown exception
	 * @param codes The accepted reply codes
	 * @throws SmtpException If the reply code is not accepted
	 */
	private void expect(@NonNull SmtpReply reply, @NonNull NetworkErrorType errorType, int @NonNull ... codes) throws SmtpException {
		for (int code : codes) {
			if (reply.code() == code) {
				return;
			}
		}
		String message = "Unexpected SMTP reply " + reply.code() + ": " + reply.message();
		SmtpException exception = new SmtpException(message, reply, errorType, null);
		NetworkUtils.handleError(this.config.onError(), errorType, message, exception);
		throw exception;
	}
	
	/**
	 * Ensures that the client is connected before performing operations.<br>
	 * @throws NetworkConnectionException If the client is not connected
	 */
	private void ensureConnected() throws NetworkConnectionException {
		if (!this.connected || this.socket.isClosed()) {
			throw new NetworkConnectionException("Client is not connected", NetworkErrorType.NOT_CONNECTED);
		}
	}
	//endregion
}
