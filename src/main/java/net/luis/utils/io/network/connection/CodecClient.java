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

package net.luis.utils.io.network.connection;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.BinaryTypeProvider;
import net.luis.utils.io.data.binary.*;
import net.luis.utils.io.data.binary.exception.BinarySyntaxException;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * A network client that sends and receives typed values over a connection.<br>
 * <p>
 *     Every value is encoded by the codec into the binary format of {@link BinaryTypeProvider}
 *     and sent as a single message, which is delimited by the framing of the underlying connection.<br>
 *     Received messages are decoded back into values by the same codec.
 * </p>
 * <p>
 *     A codec client is created by {@link Connection#map(Codec)} and shares the underlying connection.<br>
 *     Closing the codec client closes the connection.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * try (CodecClient<Message> client = TcpConnection.connectTo(endpoint).map(Message.CODEC)) {
 *     client.send(new Message("Hello"));
 *     Optional<Message> response = client.receive();
 * }
 * }</pre>
 *
 * @see Connection#map(Codec)
 *
 * @author Luis-St
 *
 * @param <T> The type of the values
 */
public final class CodecClient<T> implements NetworkClient<T> {
	
	/**
	 * The connection the encoded values are sent over.<br>
	 */
	private final Connection connection;
	/**
	 * The codec used to encode and decode the values.<br>
	 */
	private final Codec<T> codec;
	
	/**
	 * Constructs a new codec client over the given connection.<br>
	 *
	 * @param connection The connection the encoded values are sent over
	 * @param codec The codec used to encode and decode the values
	 * @throws NullPointerException If the connection or the codec is null
	 */
	CodecClient(@NonNull Connection connection, @NonNull Codec<T> codec) {
		this.connection = Objects.requireNonNull(connection, "Connection must not be null");
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
	}
	
	/**
	 * Returns the connection the encoded values are sent over.<br>
	 * @return The underlying connection
	 */
	public @NonNull Connection connection() {
		return this.connection;
	}
	
	/**
	 * Returns the codec used to encode and decode the values.<br>
	 * @return The codec
	 */
	public @NonNull Codec<T> codec() {
		return this.codec;
	}
	
	@Override
	public boolean isActive() {
		return this.connection.isActive();
	}
	
	@Override
	public @NonNull Optional<IpEndpoint> localEndpoint() {
		return this.connection.localEndpoint();
	}
	
	@Override
	public @NonNull Optional<IpEndpoint> remoteEndpoint() {
		return this.connection.remoteEndpoint();
	}
	
	/**
	 * Encodes the given value and sends it as a single message.<br>
	 *
	 * @param value The value to send
	 * @throws NullPointerException If the value is null
	 * @throws NetworkConnectionException If the value cannot be encoded or sending fails
	 */
	@Override
	public void send(@NonNull T value) throws NetworkConnectionException {
		Objects.requireNonNull(value, "Value must not be null");
		
		BinaryElement element;
		try {
			element = this.codec.encode(BinaryTypeProvider.INSTANCE, value);
		} catch (EncoderException e) {
			throw new NetworkConnectionException("Failed to encode value", e, NetworkErrorType.PROTOCOL_ERROR, this.connection.remoteEndpoint().orElse(null));
		}
		if (element == null) {
			throw new NetworkConnectionException("Codec encoded the value to null", NetworkErrorType.PROTOCOL_ERROR, this.connection.remoteEndpoint().orElse(null));
		}
		this.connection.send(BinaryWriter.toByteArray(element));
	}
	
	/**
	 * Receives the next message and decodes it into a value (blocking).<br>
	 *
	 * @return The received value, or an empty optional if the communication has ended
	 * @throws NetworkConnectionException If receiving fails or the message cannot be decoded
	 */
	@Override
	public @NonNull Optional<T> receive() throws NetworkConnectionException {
		Optional<byte[]> message = this.connection.receive();
		if (message.isEmpty()) {
			return Optional.empty();
		}
		
		T value;
		try {
			value = this.codec.decode(BinaryTypeProvider.INSTANCE, BinaryReader.fromByteArray(message.get()));
		} catch (DecoderException | BinarySyntaxException e) {
			throw new NetworkConnectionException("Failed to decode message", e, NetworkErrorType.PROTOCOL_ERROR, this.connection.remoteEndpoint().orElse(null));
		}
		if (value == null) {
			throw new NetworkConnectionException("Codec decoded the message to null", NetworkErrorType.PROTOCOL_ERROR, this.connection.remoteEndpoint().orElse(null));
		}
		return Optional.of(value);
	}
	
	@Override
	public void close() {
		this.connection.close();
	}
}
