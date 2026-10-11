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

package net.luis.utils.io.network.connection.hybrid;

import net.luis.utils.io.network.Endpoint;
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.ssl.SslClient;
import net.luis.utils.io.network.connection.ssl.SslClientConfig;
import net.luis.utils.io.network.connection.tcp.TcpClient;
import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import net.luis.utils.io.network.connection.udp.*;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * A client that talks UDP, TCP or SSL/TLS through one common type.<br>
 * This class wraps a {@link UdpClient}, a {@link TcpClient} or an {@link SslClient} and delegates every operation to it.<br>
 * <p>
 *     The protocol is chosen once through the factory method that creates the client.<br>
 *     Code that works with a hybrid client stays the same for every protocol, only the message type differs.<br>
 *     The stream based clients send and receive {@code byte[]}, while the datagram based client sends and receives {@link UdpDatagram}.
 * </p>
 * <p>
 *     A hybrid client is the counterpart of a {@link HybridServer}, which serves all three protocols on a single endpoint.<br>
 *     A TCP client and an SSL/TLS client are both connected to the port of the server.<br>
 *     The server tells them apart by the first byte they send.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpEndpoint server = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
 *
 * try (HybridClient<byte[]> client = HybridClient.createSsl(SslClientConfig.builder().build())) {
 *     client.connect(server);
 *     client.send("Hello, Server!".getBytes());
 *     byte[] response = client.receive();
 * }
 * }</pre>
 *
 * @see HybridServer
 * @see UdpClient
 * @see TcpClient
 * @see SslClient
 *
 * @author Luis-St
 *
 * @param <M> The type of message this client sends and receives, which is {@code byte[]} for the stream based clients and {@link UdpDatagram} for the datagram based client
 */
public final class HybridClient<M> implements NetworkClient<M> {
	
	/**
	 * The wrapped client every operation is delegated to.<br>
	 */
	private final NetworkClient<M> client;
	
	/**
	 * Constructs a new hybrid client wrapping the given client.<br>
	 *
	 * @param client The client to delegate to
	 * @throws NullPointerException If client is null
	 */
	private HybridClient(@NonNull NetworkClient<M> client) {
		this.client = Objects.requireNonNull(client, "Network client must not be null");
	}
	
	/**
	 * Creates a new hybrid client that sends and receives UDP datagrams.<br>
	 * The client is not bound yet, {@link #connect(Endpoint)} binds it to a local endpoint.<br>
	 *
	 * @param config The configuration of the UDP client
	 * @return The created client
	 * @throws NullPointerException If config is null
	 */
	public static @NonNull HybridClient<UdpDatagram> createUdp(@NonNull UdpClientConfig config) {
		return new HybridClient<>(new UdpClient(config));
	}
	
	/**
	 * Creates a new hybrid client that talks plain TCP.<br>
	 * The client is not connected yet, {@link #connect(Endpoint)} connects it to a server.<br>
	 *
	 * @param config The configuration of the TCP client
	 * @return The created client
	 * @throws NullPointerException If config is null
	 */
	public static @NonNull HybridClient<byte[]> createTcp(@NonNull TcpClientConfig config) {
		return new HybridClient<>(new TcpClient(config));
	}
	
	/**
	 * Creates a new hybrid client that talks SSL/TLS.<br>
	 * The client is not connected yet, {@link #connect(Endpoint)} connects it to a server and performs the TLS handshake.<br>
	 *
	 * @param config The configuration of the SSL client
	 * @return The created client
	 * @throws NullPointerException If config is null
	 */
	public static @NonNull HybridClient<byte[]> createSsl(@NonNull SslClientConfig config) {
		return new HybridClient<>(new SslClient(config));
	}
	
	/**
	 * Activates the wrapped client with the given endpoint.<br>
	 * <p>
	 *     A TCP client connects to the given remote endpoint, and an SSL/TLS client additionally performs the TLS handshake.<br>
	 *     A UDP client is connectionless, so it is bound to the given endpoint as its local endpoint instead.<br>
	 *     The destination of a datagram is taken from the datagram itself when it is sent.
	 * </p>
	 *
	 * @param endpoint The remote endpoint to connect to, or the local endpoint to bind to for UDP
	 * @throws NullPointerException If endpoint is null
	 * @throws NetworkConnectionException If connecting, binding, or the TLS handshake fails
	 * @throws IllegalStateException If the wrapped client is of an unsupported type
	 */
	public void connect(@NonNull Endpoint endpoint) throws NetworkConnectionException {
		switch (this.client) {
			case TcpClient tcp -> tcp.connect(endpoint);
			case SslClient ssl -> ssl.connect(endpoint);
			case UdpClient udp -> udp.bind(endpoint);
			default -> throw new IllegalStateException("Unsupported client type: " + this.client.getClass().getName());
		}
	}
	
	@Override
	public boolean isActive() {
		return this.client.isActive();
	}
	
	@Override
	public @NonNull Optional<? extends Endpoint> localEndpoint() {
		return this.client.localEndpoint();
	}
	
	@Override
	public @NonNull Optional<? extends Endpoint> remoteEndpoint() {
		return this.client.remoteEndpoint();
	}
	
	@Override
	public void send(@NonNull M message) throws NetworkConnectionException {
		this.client.send(message);
	}
	
	@Override
	public @NonNull M receive() throws NetworkConnectionException {
		return this.client.receive();
	}
	
	@Override
	public @NonNull M receive(int maxBytes) throws NetworkConnectionException {
		return this.client.receive(maxBytes);
	}
	
	@Override
	public void close() {
		this.client.close();
	}
}
