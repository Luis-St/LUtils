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

package net.luis.utils.io.network.address;

import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Base interface for internet protocol addresses.<br>
 * This sealed interface represents both IPv4 and IPv6 addresses and provides common operations for address manipulation, classification, and conversion.<br>
 * <p>
 *     This interface is sealed and permits only {@link Ipv4Address} and {@link Ipv6Address}
 *     as implementations, ensuring type safety when working with IP addresses polymorphically.
 * </p>
 * <p>
 *     All implementations are immutable. Methods that would modify an address return a new instance instead.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpAddress<?> address = IpAddresses.parse("192.168.1.1");
 *
 * if (address.isPrivate()) {
 *     System.out.println("Private address");
 * }
 *
 * // Pattern matching
 * switch (address) {
 *     case Ipv4Address ipv4 -> System.out.println("IPv4: " + ipv4);
 *     case Ipv6Address ipv6 -> System.out.println("IPv6: " + ipv6);
 * }
 * }</pre>
 * 
 * @author Luis-St
 * 
 * @param <T> The concrete type of the IP address (self-referential for type safety)
 */
public sealed interface IpAddress<T extends IpAddress<T>> extends Comparable<T> permits Ipv4Address, Ipv6Address {

	/**
	 * Returns the IP version number of this address.<br>
	 * This method returns {@code 4} for IPv4 addresses and {@code 6} for IPv6 addresses.<br>
	 * <p>
	 *     The version number corresponds to the internet Protocol version as defined
	 *     in the IP header and is useful for distinguishing between address types
	 *     when working with the generic ip address interface.
	 * </p>
	 *
	 * @return The IP version number ({@code 4} for IPv4, {@code 6} for IPv6)
	 */
	int version();

	/**
	 * Returns the bit length of this IP address.<br>
	 * This method returns {@code 32} for IPv4 addresses and {@code 128} for IPv6 addresses.<br>
	 * <p>
	 *     The bit length represents the total number of bits used to represent the address.<br>
	 *     This value is useful for bit manipulation operations and for calculating the total address space of the IP version.
	 * </p>
	 *
	 * @return The bit length of the address ({@code 32} for IPv4, {@code 128} for IPv6)
	 */
	int bitLength();

	/**
	 * Returns the byte representation of this IP address in network byte order (big-endian).<br>
	 * The returned array contains the raw bytes of the address, with the most significant byte at index 0.<br>
	 * <p>
	 *     For IPv4 addresses, this returns a 4-byte array.<br>
	 *     For IPv6 addresses, this returns a 16-byte array.
	 * </p>
	 * <p>
	 *     The returned array is a copy; modifications to it will not affect this address.
	 * </p>
	 * <p>
	 *     Example:
	 * </p>
	 * <pre>{@code
	 * Ipv4Address addr = Ipv4Address.of(192, 168, 1, 1);
	 * byte[] bytes = addr.toBytes();
	 * // bytes = [0xC0, 0xA8, 0x01, 0x01] (192, 168, 1, 1)
	 * }</pre>
	 *
	 * @return A new byte array containing the address in network byte order
	 */
	byte @NonNull [] toBytes();

	/**
	 * Returns the unsigned integer representation of this IP address as a {@link BigInteger}.<br>
	 * The returned value is always non-negative and represents the address as a single unsigned integer value.<br>
	 * <p>
	 *     For IPv4 addresses, the value ranges from {@code 0} to {@code 2^32 - 1}.<br>
	 *     For IPv6 addresses, the value ranges from {@code 0} to {@code 2^128 - 1}.
	 * </p>
	 * <p>
	 *     This representation is useful for arithmetic operations on addresses,<br>
	 *     such as calculating distances or iterating through address ranges.
	 * </p>
	 * <p>
	 *     Example:
	 * </p>
	 * <pre>{@code
	 * Ipv4Address addr = Ipv4Address.of(192, 168, 1, 1);
	 * BigInteger value = addr.toBigInteger();
	 * // value = 3232235777 (0xC0A80101)
	 * }</pre>
	 *
	 * @return The address as an unsigned {@link BigInteger}
	 */
	@NonNull BigInteger toBigInteger();

	/**
	 * Returns the value of the bit at the specified index.<br>
	 * Bit index {@code 0} represents the most significant bit (leftmost), and the last index represents the least significant bit (rightmost).<br>
	 * <p>
	 *     For IPv4 addresses, valid indices are {@code 0} to {@code 31}.<br>
	 *     For IPv6 addresses, valid indices are {@code 0} to {@code 127}.
	 * </p>
	 * <p>
	 *     Example:
	 * </p>
	 * <pre>{@code
	 * Ipv4Address addr = Ipv4Address.of(192, 168, 1, 1);
	 * // 192 = 0b11000000
	 * boolean bit0 = addr.getBit(0);  // true (most significant bit of 192)
	 * boolean bit1 = addr.getBit(1);  // true
	 * boolean bit2 = addr.getBit(2);  // false
	 * }</pre>
	 *
	 * @param index The bit index, where {@code 0} is the most significant bit
	 * @return {@code true} if the bit is set, {@code false} otherwise
	 * @throws IndexOutOfBoundsException If the index is negative or greater than or equal to {@link #bitLength()}
	 */
	boolean getBit(int index);

	/**
	 * Returns a new address with the bit at the specified index changed to the given value.<br>
	 * This method does not modify the current address; it returns a new instance with the specified bit set or cleared.<br>
	 * <p>
	 *     Bit index {@code 0} represents the most significant bit (leftmost), and the last index represents the least significant bit (rightmost).
	 * </p>
	 * <p>
	 *     For IPv4 addresses, valid indices are {@code 0} to {@code 31}.<br>
	 *     For IPv6 addresses, valid indices are {@code 0} to {@code 127}.
	 * </p>
	 * <p>
	 *     Example:
	 * </p>
	 * <pre>{@code
	 * Ipv4Address addr = Ipv4Address.of(192, 168, 1, 1);
	 * Ipv4Address modified = addr.withBit(31, true);  // Set least significant bit
	 * // modified = 192.168.1.1 (already set) or 192.168.1.1 -> no change if already true
	 * }</pre>
	 *
	 * @param index The bit index, where {@code 0} is the most significant bit
	 * @param value The new value for the bit ({@code true} to set, {@code false} to clear)
	 * @return A new address with the bit at the specified index changed
	 * @throws IndexOutOfBoundsException If the index is negative or greater than or equal to {@link #bitLength()}
	 */
	@NonNull T withBit(int index, boolean value);

	/**
	 * Returns the next sequential IP address, if one exists.<br>
	 * This method returns the address that is numerically one greater than this address.<br>
	 * If this address is the maximum possible value for its type, an empty {@link Optional} is returned.<br>
	 * <p>
	 *     For IPv4, the maximum address is {@code 255.255.255.255}.<br>
	 *     For IPv6, the maximum address is {@code ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff}.
	 * </p>
	 * <p>
	 *     Example:
	 * </p>
	 * <pre>{@code
	 * Ipv4Address addr = Ipv4Address.of(192, 168, 1, 1);
	 * Optional<Ipv4Address> next = addr.next();
	 * // next.get() = 192.168.1.2
	 *
	 * Ipv4Address max = Ipv4Address.of(255, 255, 255, 255);
	 * Optional<Ipv4Address> beyondMax = max.next();
	 * // beyondMax.isEmpty() = true
	 * }</pre>
	 *
	 * @return An {@link Optional} containing the next address, or empty if at maximum
	 */
	@NonNull Optional<T> next();

	/**
	 * Returns the previous sequential IP address, if one exists.<br>
	 * This method returns the address that is numerically one less than this address.<br>
	 * If this address is the minimum possible value (all zeros), an empty {@link Optional} is returned.<br>
	 * <p>
	 *     For IPv4, the minimum address is {@code 0.0.0.0}.<br>
	 *     For IPv6, the minimum address is {@code ::} (all zeros).
	 * </p>
	 * <p>
	 *     Example:
	 * </p>
	 * <pre>{@code
	 * Ipv4Address addr = Ipv4Address.of(192, 168, 1, 1);
	 * Optional<Ipv4Address> prev = addr.previous();
	 * // prev.get() = 192.168.1.0
	 *
	 * Ipv4Address min = Ipv4Address.of(0, 0, 0, 0);
	 * Optional<Ipv4Address> beforeMin = min.previous();
	 * // beforeMin.isEmpty() = true
	 * }</pre>
	 *
	 * @return An {@link Optional} containing the previous address, or empty if at minimum
	 */
	@NonNull Optional<T> previous();

	/**
	 * Calculates the signed distance from this address to another address.<br>
	 * The distance is positive if the other address is greater than this address,
	 * negative if the other address is less than this address, and zero if they are equal.<br>
	 * <p>
	 *     The distance is calculated as {@code other.toBigInteger() - this.toBigInteger()}.
	 * </p>
	 * <p>
	 *     Example:
	 * </p>
	 * <pre>{@code
	 * Ipv4Address addr1 = Ipv4Address.of(192, 168, 1, 1);
	 * Ipv4Address addr2 = Ipv4Address.of(192, 168, 1, 10);
	 * BigInteger dist = addr1.distanceTo(addr2);
	 * // dist = 9 (addr2 is 9 addresses after addr1)
	 *
	 * BigInteger reverseDist = addr2.distanceTo(addr1);
	 * // reverseDist = -9 (addr1 is 9 addresses before addr2)
	 * }</pre>
	 *
	 * @param other The target address to calculate the distance to
	 * @return The signed distance as a {@link BigInteger}
	 * @throws NullPointerException If other is null
	 */
	@NonNull BigInteger distanceTo(@NonNull T other);

	/**
	 * Checks if this is the unspecified (all-zeros) address.<br>
	 * The unspecified address indicates the absence of an address and is typically
	 * used as a source address before an interface has been assigned an address.<br>
	 * <p>
	 *     For IPv4, this is {@code 0.0.0.0}.<br>
	 *     For IPv6, this is {@code ::} (all zeros).
	 * </p>
	 *
	 * @return {@code true} if this is the unspecified address, {@code false} otherwise
	 * @see <a href="https://tools.ietf.org/html/rfc5735">RFC 5735 - Special Use IPv4 Addresses</a>
	 * @see <a href="https://tools.ietf.org/html/rfc4291">RFC 4291 - IP Version 6 Addressing Architecture</a>
	 */
	boolean isUnspecified();

	/**
	 * Checks if this is a loopback address.<br>
	 * Loopback addresses are used by a host to send packets to itself.<br>
	 * <p>
	 *     For IPv4, this includes any address in the {@code 127.0.0.0/8} range (commonly {@code 127.0.0.1}).<br>
	 *     For IPv6, this is {@code ::1}.
	 * </p>
	 *
	 * @return {@code true} if this is a loopback address, {@code false} otherwise
	 * @see <a href="https://tools.ietf.org/html/rfc5735">RFC 5735 - Special Use IPv4 Addresses</a>
	 * @see <a href="https://tools.ietf.org/html/rfc4291">RFC 4291 - IP Version 6 Addressing Architecture</a>
	 */
	boolean isLoopback();

	/**
	 * Checks if this is a multicast address.<br>
	 * Multicast addresses are used to deliver packets to multiple destinations simultaneously.<br>
	 * <p>
	 *     For IPv4, this includes addresses in the {@code 224.0.0.0/4} range (224.x.x.x through 239.x.x.x).<br>
	 *     For IPv6, this includes addresses in the {@code ff00::/8} range.
	 * </p>
	 *
	 * @return {@code true} if this is a multicast address, {@code false} otherwise
	 * @see <a href="https://tools.ietf.org/html/rfc5771">RFC 5771 - IANA Guidelines for IPv4 Multicast Address Assignments</a>
	 * @see <a href="https://tools.ietf.org/html/rfc4291">RFC 4291 - IP Version 6 Addressing Architecture</a>
	 */
	boolean isMulticast();

	/**
	 * Checks if this is a link-local address.<br>
	 * Link-local addresses are used for communication within a single network segment and are not routable beyond the local link.<br>
	 * <p>
	 *     For IPv4, this includes addresses in the {@code 169.254.0.0/16} range (often auto-configured when DHCP fails).<br>
	 *     For IPv6, this includes addresses in the {@code fe80::/10} range.
	 * </p>
	 *
	 * @return {@code true} if this is a link-local address, {@code false} otherwise
	 * @see <a href="https://tools.ietf.org/html/rfc3927">RFC 3927 - Dynamic Configuration of IPv4 Link-Local Addresses</a>
	 * @see <a href="https://tools.ietf.org/html/rfc4291">RFC 4291 - IP Version 6 Addressing Architecture</a>
	 */
	boolean isLinkLocal();

	/**
	 * Checks if this is a private address.<br>
	 * Private addresses are reserved for use in private networks and are not globally routable on the internet.<br>
	 * <p>
	 *     For IPv4, this includes addresses defined in RFC 1918:
	 * </p>
	 * <ul>
	 *     <li>{@code 10.0.0.0/8} (10.x.x.x)</li>
	 *     <li>{@code 172.16.0.0/12} (172.16.x.x through 172.31.x.x)</li>
	 *     <li>{@code 192.168.0.0/16} (192.168.x.x)</li>
	 * </ul>
	 * <p>
	 *     For IPv6, this includes Unique Local Addresses (ULA) in the {@code fc00::/7} range.
	 * </p>
	 *
	 * @return {@code true} if this is a private address, {@code false} otherwise
	 * @see <a href="https://tools.ietf.org/html/rfc1918">RFC 1918 - Address Allocation for Private Internets</a>
	 * @see <a href="https://tools.ietf.org/html/rfc4193">RFC 4193 - Unique Local IPv6 Unicast Addresses</a>
	 */
	boolean isPrivate();

	/**
	 * Checks if this is a documentation address.<br>
	 * Documentation addresses are reserved for use in documentation and examples, and should not be used in production networks.<br>
	 * <p>
	 *     For IPv4, this includes the TEST-NET blocks:
	 * </p>
	 * <ul>
	 *     <li>{@code 192.0.2.0/24} (TEST-NET-1)</li>
	 *     <li>{@code 198.51.100.0/24} (TEST-NET-2)</li>
	 *     <li>{@code 203.0.113.0/24} (TEST-NET-3)</li>
	 * </ul>
	 * <p>
	 *     For IPv6, this includes the {@code 2001:db8::/32} range.
	 * </p>
	 *
	 * @return {@code true} if this is a documentation address, {@code false} otherwise
	 * @see <a href="https://tools.ietf.org/html/rfc5737">RFC 5737 - IPv4 Address Blocks Reserved for Documentation</a>
	 * @see <a href="https://tools.ietf.org/html/rfc3849">RFC 3849 - IPv6 Address Prefix Reserved for Documentation</a>
	 */
	boolean isDocumentation();

	/**
	 * Checks if this is a globally routable unicast address.<br>
	 * Global unicast addresses are addresses that can be routed on the public internet.<br>
	 * This excludes private, link-local, loopback, multicast, and other special-purpose addresses.<br>
	 * <p>
	 *     For IPv4, this includes addresses that are not in any reserved range
	 *     (not private, not loopback, not link-local, not multicast, etc.).<br>
	 *     For IPv6, this typically includes addresses in the {@code 2000::/3} range
	 *     that are not otherwise reserved.
	 * </p>
	 *
	 * @return {@code true} if this is a globally routable unicast address, {@code false} otherwise
	 */
	boolean isGlobalUnicast();

	/**
	 * Returns the address type classification for this IP address.<br>
	 * This method provides a comprehensive classification of the address based on
	 * its special-purpose designation according to IANA and relevant RFCs.
	 * <p>
	 *     The returned {@link AddressType} indicates whether the address is unspecified,
	 *     loopback, private, link-local, multicast, documentation, global unicast, or other special types.
	 * </p>
	 * <p>
	 *     This method is useful for making routing and security decisions based on the type of address being used.
	 * </p>
	 *
	 * @return The {@link AddressType} classification of this address
	 * @see AddressType
	 */
	@NonNull AddressType getAddressType();

	/**
	 * Returns the reverse DNS (PTR) name for this IP address.<br>
	 * This is the domain name used for reverse DNS lookups to resolve an IP address back to a hostname.<br>
	 * <p>
	 *     For IPv4 addresses, this returns a name in the {@code in-addr.arpa} domain with the octets reversed.<br>
	 *     For example, {@code 192.168.1.1} becomes {@code 1.1.168.192.in-addr.arpa}.
	 * </p>
	 * <p>
	 *     For IPv6 addresses, this returns a name in the {@code ip6.arpa} domain with each nibble (4-bit group) reversed and separated by dots.<br>
	 *     For example,{@code 2001:db8::1} becomes {@code 1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.8.b.d.0.1.0.0.2.ip6.arpa}.
	 * </p>
	 *
	 * @return The reverse DNS name for this address
	 * @see <a href="https://tools.ietf.org/html/rfc1035">RFC 1035 - Domain Names</a>
	 * @see <a href="https://tools.ietf.org/html/rfc3596">RFC 3596 - DNS Extensions to Support IPv6</a>
	 */
	@NonNull String toReverseDnsName();

	/**
	 * Performs a blocking reverse DNS lookup to resolve the hostname for this IP address.<br>
	 * This method queries the DNS system to find the PTR record associated with this IP address and returns the corresponding hostname.<br>
	 * <p>
	 *     <b>Warning:</b> This method performs a blocking network operation and may take several seconds to complete,<br>
	 *     depending on network conditions and DNS server responsiveness.<br>
	 *     Consider using {@link #resolveHostnameAsync()} for non-blocking resolution or {@link #resolveHostname(Duration)} to specify a timeout.
	 * </p>
	 * <p>
	 *     If the reverse DNS lookup fails or no PTR record exists for this address, an empty {@link Optional} is returned.
	 * </p>
	 *
	 * @return An {@link Optional} containing the resolved hostname, or empty if resolution fails
	 */
	default @NonNull Optional<String> resolveHostname() {
		try {
			InetAddress inetAddress = this.toInetAddress();
			String hostName = inetAddress.getCanonicalHostName();
			
			if (hostName.equals(inetAddress.getHostAddress())) {
				return Optional.empty();
			}
			return Optional.of(hostName);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Performs a non-blocking reverse DNS lookup to resolve the hostname for this IP address.<br>
	 * This method returns a {@link CompletableFuture} that will complete with the resolved hostname when the DNS lookup finishes.<br>
	 * <p>
	 *     This is the preferred method for hostname resolution when you want to avoid blocking the calling thread.<br>
	 *     The future can be composed with other async operations or awaited at a convenient time.
	 * </p>
	 * <p>
	 *     If the reverse DNS lookup fails or no PTR record exists for this address, the future completes with an empty {@link Optional}.
	 * </p>
	 * <p>
	 *     Example:
	 * </p>
	 * <pre>{@code
	 * IpAddress<?> address = IpAddresses.parse("8.8.8.8");
	 * address.resolveHostnameAsync()
	 *     .thenAccept(hostname -> hostname.ifPresent(System.out::println))
	 *     .exceptionally(e -> { e.printStackTrace(); return null; });
	 * }</pre>
	 *
	 * @return A {@link CompletableFuture} that will complete with the resolved hostname
	 */
	default @NonNull CompletableFuture<Optional<String>> resolveHostnameAsync() {
		return CompletableFuture.supplyAsync(this::resolveHostname);
	}

	/**
	 * Performs a blocking reverse DNS lookup with a specified timeout.<br>
	 * This method queries the DNS system to find the PTR record associated with this IP address,
	 * but will abort and return empty if the lookup takes longer than the specified duration.
	 * <p>
	 *     This method is useful when you need synchronous resolution but want to limit the maximum wait time to prevent excessive delays.
	 * </p>
	 * <p>
	 *     If the timeout expires, the reverse DNS lookup fails, or no PTR record exists for this address, an empty {@link Optional} is returned.
	 * </p>
	 * <p>
	 *     Example:
	 * </p>
	 * <pre>{@code
	 * IpAddress<?> address = IpAddresses.parse("8.8.8.8");
	 * Optional<String> hostname = address.resolveHostname(Duration.ofSeconds(5));
	 * hostname.ifPresent(name -> System.out.println("Hostname: " + name));
	 * }</pre>
	 *
	 * @param timeout The maximum duration to wait for the DNS lookup to complete
	 * @return An {@link Optional} containing the resolved hostname, or empty if resolution fails or times out
	 * @throws NullPointerException If timeout is null
	 */
	default @NonNull Optional<String> resolveHostname(@NonNull Duration timeout) {
		Objects.requireNonNull(timeout, "Timeout must not be null");
		
		try {
			return this.resolveHostnameAsync().get(timeout.toMillis(), TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Converts this IP address to a {@link java.net.InetAddress}.<br>
	 * This method provides interoperability with the standard Java networking API.<br>
	 * <p>
	 *     For IPv4 addresses, this returns an {@link java.net.Inet4Address}.<br>
	 *     For IPv6 addresses, this returns an {@link java.net.Inet6Address}.
	 * </p>
	 * <p>
	 *     The returned {@link InetAddress} can be used with standard Java networking classes such as {@link java.net.Socket} and {@link java.net.DatagramSocket}.
	 * </p>
	 *
	 * @return An {@link InetAddress} representing this IP address
	 */
	@NonNull InetAddress toInetAddress();

	/**
	 * Creates an {@link InetSocketAddress} from this IP address and the specified port.<br>
	 * This method provides a convenient way to create socket addresses for use with standard Java networking APIs.
	 * <p>
	 *     The returned socket address can be used with classes such as
	 *     {@link java.net.Socket#connect(java.net.SocketAddress)} and
	 *     {@link java.net.ServerSocket#bind(java.net.SocketAddress)}.
	 * </p>
	 * <p>
	 *     Example:
	 * </p>
	 * <pre>{@code
	 * IpAddress<?> address = IpAddresses.parse("192.168.1.1");
	 * InetSocketAddress socketAddr = address.toSocketAddress(8080);
	 * Socket socket = new Socket();
	 * socket.connect(socketAddr);
	 * }</pre>
	 *
	 * @param port The port number (must be between 0 and 65535 inclusive)
	 * @return An {@link InetSocketAddress} combining this IP address and the specified port
	 * @throws IllegalArgumentException If the port is outside the valid range (0-65535)
	 */
	@NonNull InetSocketAddress toSocketAddress(int port);

	/**
	 * Returns the minimum possible value for this IP address type.<br>
	 * This is the address with all bits set to zero.<br>
	 * <p>
	 *     For IPv4, this returns {@code 0.0.0.0}.<br>
	 *     For IPv6, this returns {@code ::} (all zeros).
	 * </p>
	 * <p>
	 *     This method is useful for range calculations and for obtaining the starting point of the address space.
	 * </p>
	 *
	 * @return The minimum address value for this IP address type
	 */
	@NonNull T minValue();

	/**
	 * Returns the maximum possible value for this IP address type.<br>
	 * This is the address with all bits set to one.<br>
	 * <p>
	 *     For IPv4, this returns {@code 255.255.255.255}.<br>
	 *     For IPv6, this returns {@code ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff}.
	 * </p>
	 * <p>
	 *     This method is useful for range calculations and for obtaining the ending point of the address space.
	 * </p>
	 *
	 * @return The maximum address value for this IP address type
	 */
	@NonNull T maxValue();
}
