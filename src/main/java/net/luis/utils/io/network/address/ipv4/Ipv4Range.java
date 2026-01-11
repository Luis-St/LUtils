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

package net.luis.utils.io.network.address.ipv4;

import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.io.network.address.IpRange;
import net.luis.utils.io.network.address.ipv6.Ipv6Range;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents a contiguous range of IPv4 addresses from a start to an end address.<br>
 * Unlike {@link Ipv4Network}, a range does not need to align to CIDR boundaries.<br>
 * <p>
 *     Ranges are useful for representing arbitrary address spans such as DHCP pools,<br>
 *     allocation blocks, or firewall rules that don't fit neatly into CIDR notation.
 * </p>
 * <p>
 *     <b>Warning:</b> For large ranges, iterating over all addresses may consume significant memory and time.<br>
 *     Check {@link #size()} before iterating or use {@link #addressStream()} with a limit.
 * </p>
 * <pre>{@code
 * // Create a range
 * Ipv4Range range = Ipv4Range.of(
 *     IpAddresses.parseIpv4("192.168.1.100"),
 *     IpAddresses.parseIpv4("192.168.1.200")
 * );
 *
 * // Check containment
 * if (range.contains(IpAddresses.parseIpv4("192.168.1.150"))) {
 *     System.out.println("Address is in range");
 * }
 *
 * // Convert to CIDR networks for firewall rules
 * List<Ipv4Network> networks = range.toCidrNetworks();
 * // Result: [192.168.1.100/30, 192.168.1.104/29, ...]
 * }</pre>
 *
 * @param start The first address in the range (inclusive)
 * @param end The last address in the range (inclusive)
 *
 * @see IpRange
 * @see Ipv6Range
 *
 * @author Luis-St
 */
public record Ipv4Range(@NonNull Ipv4Address start, @NonNull Ipv4Address end) implements IpRange<Ipv4Address, Ipv4Range, Ipv4Network> {

	/**
	 * Constructs a new IPv4 range with the given start and end addresses.<br>
	 *
	 * @param start The first address in the range (inclusive)
	 * @param end The last address in the range (inclusive)
	 * @throws NullPointerException If start or end is null
	 * @throws IllegalArgumentException If start is greater than end
	 */
	public Ipv4Range {
		Objects.requireNonNull(start, "Start address must not be null");
		Objects.requireNonNull(end, "End address must not be null");
		if (start.compareTo(end) > 0) {
			throw new IllegalArgumentException("Start address must not be greater than end address");
		}
	}

	/**
	 * Creates a new IPv4 range from the given start and end addresses.<br>
	 *
	 * @param start The first address in the range (inclusive)
	 * @param end The last address in the range (inclusive)
	 * @return A new IPv4 range
	 * @throws NullPointerException If start or end is null
	 * @throws IllegalArgumentException If start is greater than end
	 */
	public static @NonNull Ipv4Range of(@NonNull Ipv4Address start, @NonNull Ipv4Address end) {
		return new Ipv4Range(start, end);
	}

	/**
	 * Parses an IPv4 range from a string in the format "start-end".<br>
	 * For example: "192.168.1.10-192.168.1.50"<br>
	 *
	 * @param range The range string to parse
	 * @return A new IPv4 range parsed from the string
	 * @throws NullPointerException If range is null
	 * @throws IllegalArgumentException If the range string is invalid
	 */
	public static @NonNull Ipv4Range parse(@NonNull String range) {
		Objects.requireNonNull(range, "Range must not be null");
		return tryParse(range).orElseThrow(() -> new IllegalArgumentException("Invalid range notation: " + range));
	}

	/**
	 * Attempts to parse an IPv4 range from a string in the format "start-end".<br>
	 * For example: "192.168.1.10-192.168.1.50"<br>
	 *
	 * @param range The range string to parse
	 * @return An optional containing the parsed range, or empty if parsing fails
	 * @throws NullPointerException If range is null
	 */
	public static @NonNull Optional<Ipv4Range> tryParse(@NonNull String range) {
		Objects.requireNonNull(range, "Range must not be null");
		
		int dashIndex = range.indexOf('-');
		if (dashIndex == -1) {
			return Optional.empty();
		}
		
		String startPart = range.substring(0, dashIndex);
		String endPart = range.substring(dashIndex + 1);
		try {
			Optional<Ipv4Address> startAddress = IpAddresses.tryParseIpv4(startPart);
			Optional<Ipv4Address> endAddress = IpAddresses.tryParseIpv4(endPart);
			if (startAddress.isEmpty() || endAddress.isEmpty()) {
				return Optional.empty();
			}
			
			
			Ipv4Address start = startAddress.get();
			Ipv4Address end = endAddress.get();
			if (start.compareTo(end) > 0) {
				return Optional.empty();
			}
			return Optional.of(new Ipv4Range(start, end));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Creates a single-address range containing only the specified address.<br>
	 *
	 * @param address The address for the single-address range
	 * @return A new IPv4 range containing only the specified address
	 * @throws NullPointerException If address is null
	 */
	public static @NonNull Ipv4Range single(@NonNull Ipv4Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		return new Ipv4Range(address, address);
	}

	/**
	 * Creates a range from a network, covering all addresses in the network.<br>
	 *
	 * @param network The network to create a range from
	 * @return A new IPv4 range covering all addresses in the network
	 * @throws NullPointerException If network is null
	 */
	public static @NonNull Ipv4Range fromNetwork(@NonNull Ipv4Network network) {
		Objects.requireNonNull(network, "Network must not be null");
		return new Ipv4Range(network.networkAddress(), network.broadcastAddress());
	}

	@Override
	public @NonNull BigInteger size() {
		return this.end.toBigInteger().subtract(this.start.toBigInteger()).add(BigInteger.ONE);
	}

	@Override
	public boolean containsAddress(@NonNull Ipv4Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		return address.compareTo(this.start) >= 0 && address.compareTo(this.end) <= 0;
	}

	@Override
	public boolean containsRange(@NonNull Ipv4Range other) {
		Objects.requireNonNull(other, "Other range must not be null");
		return other.start.compareTo(this.start) >= 0 && other.end.compareTo(this.end) <= 0;
	}

	@Override
	public boolean overlaps(@NonNull Ipv4Range other) {
		Objects.requireNonNull(other, "Other range must not be null");
		return this.start.compareTo(other.end) <= 0 && this.end.compareTo(other.start) >= 0;
	}

	@Override
	public @NonNull Optional<Ipv4Range> intersection(@NonNull Ipv4Range other) {
		Objects.requireNonNull(other, "Other range must not be null");
		if (!this.overlaps(other)) {
			return Optional.empty();
		}
		
		Ipv4Address intersectionStart = this.start.compareTo(other.start) >= 0 ? this.start : other.start;
		Ipv4Address intersectionEnd = this.end.compareTo(other.end) <= 0 ? this.end : other.end;
		return Optional.of(new Ipv4Range(intersectionStart, intersectionEnd));
	}

	@Override
	public @NonNull List<Ipv4Range> difference(@NonNull Ipv4Range other) {
		Objects.requireNonNull(other, "Other range must not be null");
		if (!this.overlaps(other)) {
			return List.of(this);
		}

		if (other.containsRange(this)) {
			return List.of();
		}

		List<Ipv4Range> result = new ArrayList<>(2);

		if (this.start.compareTo(other.start) < 0) {
			Ipv4Address beforeEnd = other.start.previous().orElse(other.start);
			if (this.start.compareTo(beforeEnd) <= 0) {
				result.add(new Ipv4Range(this.start, beforeEnd));
			}
		}

		if (this.end.compareTo(other.end) > 0) {
			Ipv4Address afterStart = other.end.next().orElse(other.end);
			if (afterStart.compareTo(this.end) <= 0) {
				result.add(new Ipv4Range(afterStart, this.end));
			}
		}
		return result;
	}

	@Override
	public @NonNull Optional<Ipv4Range> merge(@NonNull Ipv4Range other) {
		Objects.requireNonNull(other, "Other range must not be null");

		boolean overlapping = this.overlaps(other);
		boolean adjacent = false;
		if (!overlapping) {
			Optional<Ipv4Address> thisEndNext = this.end.next();
			Optional<Ipv4Address> otherEndNext = other.end.next();
			adjacent = (thisEndNext.isPresent() && thisEndNext.get().equals(other.start)) || (otherEndNext.isPresent() && otherEndNext.get().equals(this.start));
		}

		if (!overlapping && !adjacent) {
			return Optional.empty();
		}

		Ipv4Address mergedStart = this.start.compareTo(other.start) <= 0 ? this.start : other.start;
		Ipv4Address mergedEnd = this.end.compareTo(other.end) >= 0 ? this.end : other.end;
		return Optional.of(new Ipv4Range(mergedStart, mergedEnd));
	}

	@Override
	public @NonNull Iterator<Ipv4Address> iterator() {
		return new Ipv4RangeIterator(this.start, this.end);
	}

	@Override
	public @NonNull Stream<Ipv4Address> addressStream() {
		Spliterator<Ipv4Address> spliterator = Spliterators.spliterator(
			this.iterator(),
			this.size().longValueExact(),
			Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.IMMUTABLE
		);
		return StreamSupport.stream(spliterator, false);
	}

	@Override
	public @NonNull List<Ipv4Network> toCidrNetworks() {
		List<Ipv4Network> networks = new ArrayList<>();
		Ipv4Address current = this.start;

		while (current.compareTo(this.end) <= 0) {
			int maxPrefixLength = findMaxPrefixLength(current, this.end);
			Ipv4Network network = new Ipv4Network(current, maxPrefixLength);
			networks.add(network);

			Ipv4Address broadcastAddr = network.broadcastAddress();
			Optional<Ipv4Address> nextAddr = broadcastAddr.next();
			if (nextAddr.isEmpty()) {
				return networks;
			}
			current = nextAddr.get();
		}
		return networks;
	}

	/**
	 * Finds the maximum prefix length (smallest network) that starts at the given address and does not extend beyond the end address.<br>
	 *
	 * @param start The start address of the potential network
	 * @param end The maximum end address
	 * @return The maximum prefix length
	 */
	private static int findMaxPrefixLength(@NonNull Ipv4Address start, @NonNull Ipv4Address end) {
		Objects.requireNonNull(start, "Start address must not be null");
		Objects.requireNonNull(end, "End address must not be null");
		
		int startValue = start.value();
		long endValue = end.toUnsignedLong();

		int prefixLength = 32;
		while (prefixLength > 0) {
			int hostBits = 32 - prefixLength;
			int mask = -(1 << hostBits);

			if ((startValue & mask) != startValue) {
				prefixLength++;
				break;
			}

			long broadcastValue = Integer.toUnsignedLong(startValue | ((1 << hostBits) - 1));
			if (broadcastValue > endValue) {
				prefixLength++;
				break;
			}
			prefixLength--;
		}
		return Math.max(0, prefixLength);
	}

	@Override
	public boolean isSingleAddress() {
		return this.start.equals(this.end);
	}

	@Override
	public boolean isNetwork() {
		return this.toNetwork().isPresent();
	}

	@Override
	public @NonNull Optional<Ipv4Network> toNetwork() {
		for (int prefixLength = 0; prefixLength <= 32; prefixLength++) {
			Ipv4Network network = Ipv4Network.of(this.start, prefixLength);
			
			if (network.networkAddress().equals(this.start) &&
				network.broadcastAddress().equals(this.end)) {
				return Optional.of(network);
			}
		}
		return Optional.empty();
	}

	@Override
	public int compareTo(@NonNull Ipv4Range other) {
		int startCompare = this.start.compareTo(other.start);
		if (startCompare != 0) {
			return startCompare;
		}
		return this.end.compareTo(other.end);
	}
	
	@Override
	public @NonNull String toString() {
		return this.start.toString() + "-" + this.end.toString();
	}

	//region Inner classes

	/**
	 * Iterator implementation for iterating over addresses in an IPv4 range.<br>
	 *
	 * @author Luis-St
	 */
	private static final class Ipv4RangeIterator implements Iterator<Ipv4Address> {
		
		/**
		 * The end address of the range (inclusive).<br>
		 */
		private final Ipv4Address endAddress;
		/**
		 * The current address in the iteration.<br>
		 */
		private Ipv4Address currentAddress;
		/**
		 * Indicates if there are more addresses to iterate.<br>
		 */
		private boolean hasNext;

		/**
		 * Creates a new iterator from the start address to the end address (inclusive).<br>
		 *
		 * @param startAddress The first address to iterate
		 * @param endAddress The last address to iterate (inclusive)
		 * @throws NullPointerException If startAddress or endAddress is null
		 */
		Ipv4RangeIterator(@NonNull Ipv4Address startAddress, @NonNull Ipv4Address endAddress) {
			this.currentAddress = Objects.requireNonNull(startAddress, "Start address must not be null");
			this.endAddress = Objects.requireNonNull(endAddress, "End address must not be null");
			this.hasNext = startAddress.compareTo(endAddress) <= 0;
		}

		@Override
		public boolean hasNext() {
			return this.hasNext;
		}

		@Override
		public @NonNull Ipv4Address next() {
			if (!this.hasNext) {
				throw new NoSuchElementException("No more addresses in range");
			}
			Ipv4Address result = this.currentAddress;
			if (this.currentAddress.equals(this.endAddress)) {
				this.hasNext = false;
			} else {
				this.currentAddress = this.currentAddress.next().orElseThrow();
			}
			return result;
		}
	}
	//endregion
}
