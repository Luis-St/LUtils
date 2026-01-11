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

package net.luis.utils.io.network.address.ipv6;

import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Contains parsed information from a Teredo IPv6 address.<br>
 * Teredo (RFC 4380) is an IPv6 tunneling protocol that encapsulates IPv6 packets in IPv4 UDP datagrams.<br>
 * The Teredo address format encodes the server address, client address, port, and NAT type.<br>
 * <p>
 *     Teredo address format (2001:0000:ssss:ssss:ffff:pppp:cccc:cccc):
 * </p>
 * <ul>
 *     <li>2001:0000 - Fixed Teredo prefix</li>
 *     <li>ssss:ssss - Teredo server IPv4 address</li>
 *     <li>ffff - Flags (bit 0 is cone NAT indicator)</li>
 *     <li>pppp - Obfuscated UDP port (XOR 0xFFFF)</li>
 *     <li>cccc:cccc - Obfuscated client IPv4 (XOR 0xFFFFFFFF)</li>
 * </ul>
 * <pre>{@code
 * Ipv6Address teredo = IpAddresses.parseIpv6("2001:0:4136:e378:8000:63bf:3fff:fdd2");
 *
 * if (TeredoInfo.isTeredo(teredo)) {
 *     TeredoInfo info = TeredoInfo.from(teredo).orElseThrow();
 *     System.out.println("Server: " + info.server());
 *     System.out.println("Client: " + info.client());
 *     System.out.println("Port: " + info.port());
 *     System.out.println("Cone NAT: " + info.coneNat());
 * }
 * }</pre>
 *
 * @see Ipv6Address
 *
 * @author Luis-St
 *
 * @param server The Teredo server's IPv4 address
 * @param client The client's (obfuscated) IPv4 address behind NAT
 * @param port The (obfuscated) UDP port used for tunneling
 * @param flags The Teredo flags field
 * @param coneNat True if the client is behind a cone NAT
 */
public record TeredoInfo(
	@NonNull Ipv4Address server,
	@NonNull Ipv4Address client,
	int port,
	int flags,
	boolean coneNat
) {

	/**
	 * The fixed Teredo prefix high bits (2001:0000).<br>
	 */
	private static final long TEREDO_PREFIX_HIGH = 0x2001000000000000L;

	/**
	 * The cone NAT flag bit (bit 0 of the flags field).<br>
	 */
	public static final int CONE_NAT_FLAG = 0x8000;

	/**
	 * Constructs a new TeredoInfo with validated parameters.<br>
	 *
	 * @param server The Teredo server's IPv4 address
	 * @param client The client's IPv4 address behind NAT
	 * @param port The UDP port used for tunneling
	 * @param flags The Teredo flags field
	 * @param coneNat True if the client is behind a cone NAT
	 * @throws NullPointerException If server or client is null
	 * @throws IllegalArgumentException If port is out of valid range (0-65535)
	 */
	public TeredoInfo {
		Objects.requireNonNull(server, "Server address must not be null");
		Objects.requireNonNull(client, "Client address must not be null");
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("Port must be between 0 and 65535: " + port);
		}
	}

	/**
	 * Returns the Teredo prefix network (2001:0000::/32).<br>
	 * @return The Teredo prefix network
	 */
	public static @NonNull Ipv6Network teredoPrefix() {
		return new Ipv6Network(new Ipv6Address(TEREDO_PREFIX_HIGH, 0L), 32);
	}

	/**
	 * Parses Teredo information from an IPv6 address.<br>
	 *
	 * @param address The IPv6 address to parse
	 * @return An {@link Optional} containing the parsed TeredoInfo, or empty if not a Teredo address
	 * @throws NullPointerException If address is null
	 */
	public static @NonNull Optional<TeredoInfo> from(@NonNull Ipv6Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		if (!isTeredo(address)) {
			return Optional.empty();
		}

		int serverIpv4 = (int) address.highBits();
		int flags = (int) (address.lowBits() >>> 48);
		int port = (int) ((address.lowBits() >>> 32) & 0xFFFF) ^ 0xFFFF;
		int clientIpv4 = ~((int) address.lowBits());
		boolean isConeNat = (flags & CONE_NAT_FLAG) != 0;

		return Optional.of(new TeredoInfo(
			new Ipv4Address(serverIpv4),
			new Ipv4Address(clientIpv4),
			port,
			flags,
			isConeNat
		));
	}

	/**
	 * Checks if an IPv6 address is a Teredo address (2001:0000::/32).<br>
	 *
	 * @param address The IPv6 address to check
	 * @return {@code true} if the address is a Teredo address, {@code false} otherwise
	 * @throws NullPointerException If address is null
	 */
	public static boolean isTeredo(@NonNull Ipv6Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		return (address.highBits() >>> 32) == 0x20010000L;
	}

	/**
	 * Builds a Teredo IPv6 address from the components in this info.<br>
	 * @return The Teredo IPv6 address
	 */
	public @NonNull Ipv6Address toIpv6Address() {
		long highBits = TEREDO_PREFIX_HIGH | Integer.toUnsignedLong(this.server.value());

		long obfuscatedPort = (this.port ^ 0xFFFF) & 0xFFFFL;
		long obfuscatedClient = Integer.toUnsignedLong(~this.client.value());
		long lowBits = ((long) this.flags << 48) | (obfuscatedPort << 32) | obfuscatedClient;
		return new Ipv6Address(highBits, lowBits);
	}
}
