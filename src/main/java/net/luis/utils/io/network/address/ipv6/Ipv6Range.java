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

import net.luis.utils.io.network.address.IpRange;
import net.luis.utils.io.network.address.ipv4.Ipv4Range;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents a contiguous range of IPv6 addresses from a start to an end address.<br>
 * Unlike {@link Ipv6Network}, a range does not need to align to CIDR boundaries.<br>
 * <p>
 *     <b>Warning:</b> IPv6 ranges can be astronomically large.<br>
 *     A range spanning even a small portion of the IPv6 address space can contain more addresses than all atoms in the observable universe.<br>
 *     Always check {@link #size()} and avoid iterating over large ranges.
 * </p>
 * <pre>{@code
 * Ipv6Range range = Ipv6Range.of(
 *     IpAddresses.parseIpv6("2001:db8::1"),
 *     IpAddresses.parseIpv6("2001:db8::100")
 * );
 *
 * if (range.isSingleAddress()) {
 *     System.out.println("Single address");
 * }
 *
 * List<Ipv6Network> networks = range.toCidrNetworks();
 * }</pre>
 *
 * @see IpRange
 * @see Ipv4Range
 *
 * @author Luis-St
 *
 * @param start The first address in the range (inclusive, without zone ID)
 * @param end The last address in the range (inclusive, without zone ID)
 */
public record Ipv6Range(@NonNull Ipv6Address start, @NonNull Ipv6Address end) implements IpRange<Ipv6Address, Ipv6Range, Ipv6Network> {
	
	/**
	 * Constructs a new IPv6 range with the given start and end addresses.<br>
	 * Zone IDs are stripped from both addresses as they are not relevant for range comparisons.
	 *
	 * @param start The first address in the range (inclusive)
	 * @param end The last address in the range (inclusive)
	 * @throws NullPointerException If start or end is null
	 * @throws IllegalArgumentException If start is greater than end
	 */
	public Ipv6Range {
		Objects.requireNonNull(start, "Start address must not be null");
		Objects.requireNonNull(end, "End address must not be null");
		
		start = start.withoutZoneId();
		end = end.withoutZoneId();
		if (start.compareTo(end) > 0) {
			throw new IllegalArgumentException("Start address must not be greater than end address: " + start + " > " + end);
		}
	}
	
	/**
	 * Creates a new IPv6 range from the given start and end addresses.<br>
	 * Zone IDs are stripped from both addresses.<br>
	 *
	 * @param start The first address in the range (inclusive)
	 * @param end The last address in the range (inclusive)
	 * @return A new IPv6 range
	 * @throws NullPointerException If start or end is null
	 * @throws IllegalArgumentException If start is greater than end
	 */
	public static @NonNull Ipv6Range of(@NonNull Ipv6Address start, @NonNull Ipv6Address end) {
		return new Ipv6Range(start, end);
	}
	
	/**
	 * Creates a single-address range containing only the given address.<br>
	 * The zone ID is stripped from the address.<br>
	 *
	 * @param address The single address to include in the range
	 * @return A new IPv6 range containing only the specified address
	 * @throws NullPointerException If address is null
	 */
	public static @NonNull Ipv6Range single(@NonNull Ipv6Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		Ipv6Address stripped = address.withoutZoneId();
		return new Ipv6Range(stripped, stripped);
	}
	
	/**
	 * Creates a range from a network, covering all addresses in the network.<br>
	 *
	 * @param network The network to create a range from
	 * @return A new IPv6 range covering all addresses in the network
	 * @throws NullPointerException If network is null
	 */
	public static @NonNull Ipv6Range fromNetwork(@NonNull Ipv6Network network) {
		Objects.requireNonNull(network, "Network must not be null");
		return new Ipv6Range(network.networkAddress(), network.lastAddress());
	}
	
	@Override
	public @NonNull BigInteger size() {
		return this.end.toBigInteger().subtract(this.start.toBigInteger()).add(BigInteger.ONE);
	}
	
	@Override
	public boolean containsAddress(@NonNull Ipv6Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		Ipv6Address stripped = address.withoutZoneId();
		return stripped.compareTo(this.start) >= 0 && stripped.compareTo(this.end) <= 0;
	}
	
	@Override
	public boolean containsRange(@NonNull Ipv6Range other) {
		Objects.requireNonNull(other, "Other range must not be null");
		return this.start.compareTo(other.start) <= 0 && this.end.compareTo(other.end) >= 0;
	}
	
	@Override
	public boolean overlaps(@NonNull Ipv6Range other) {
		Objects.requireNonNull(other, "Other range must not be null");
		return this.start.compareTo(other.end) <= 0 && this.end.compareTo(other.start) >= 0;
	}
	
	@Override
	public @NonNull Optional<Ipv6Range> intersection(@NonNull Ipv6Range other) {
		Objects.requireNonNull(other, "Other range must not be null");
		if (!this.overlaps(other)) {
			return Optional.empty();
		}
		
		Ipv6Address newStart = this.start.compareTo(other.start) >= 0 ? this.start : other.start;
		Ipv6Address newEnd = this.end.compareTo(other.end) <= 0 ? this.end : other.end;
		return Optional.of(new Ipv6Range(newStart, newEnd));
	}
	
	@Override
	public @NonNull List<Ipv6Range> difference(@NonNull Ipv6Range other) {
		Objects.requireNonNull(other, "Other range must not be null");
		if (!this.overlaps(other)) {
			return List.of(this);
		}
		
		List<Ipv6Range> result = new ArrayList<>(2);
		if (this.start.compareTo(other.start) < 0) {
			Ipv6Address beforeEnd = other.start.previous().orElse(other.start);
			if (this.start.compareTo(beforeEnd) <= 0) {
				result.add(new Ipv6Range(this.start, beforeEnd));
			}
		}
		
		if (this.end.compareTo(other.end) > 0) {
			Ipv6Address afterStart = other.end.next().orElse(other.end);
			if (afterStart.compareTo(this.end) <= 0) {
				result.add(new Ipv6Range(afterStart, this.end));
			}
		}
		return result;
	}
	
	@Override
	public @NonNull Optional<Ipv6Range> merge(@NonNull Ipv6Range other) {
		Objects.requireNonNull(other, "Other range must not be null");
		
		boolean adjacent = false;
		if (!this.overlaps(other)) {
			Optional<Ipv6Address> nextAfterThis = this.end.next();
			Optional<Ipv6Address> nextAfterOther = other.end.next();
			adjacent = (nextAfterThis.isPresent() && nextAfterThis.get().equals(other.start)) || (nextAfterOther.isPresent() && nextAfterOther.get().equals(this.start));
			
			if (!adjacent) {
				return Optional.empty();
			}
		}
		
		Ipv6Address newStart = this.start.compareTo(other.start) <= 0 ? this.start : other.start;
		Ipv6Address newEnd = this.end.compareTo(other.end) >= 0 ? this.end : other.end;
		return Optional.of(new Ipv6Range(newStart, newEnd));
	}
	
	@Override
	public @NonNull Iterator<Ipv6Address> iterator() {
		return new Ipv6RangeIterator(this.start, this.end);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NonNull Stream<Ipv6Address> addressStream() {
		BigInteger size = this.size();
		long estimatedSize = size.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0 ? Long.MAX_VALUE : size.longValueExact();
		
		Spliterator<Ipv6Address> spliterator = Spliterators.spliterator(
			this.iterator(),
			estimatedSize,
			Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.IMMUTABLE
		);
		return StreamSupport.stream(spliterator, false);
	}
	
	@Override
	public @NonNull List<Ipv6Network> toCidrNetworks() {
		List<Ipv6Network> networks = new ArrayList<>();
		BigInteger currentValue = this.start.toBigInteger();
		BigInteger endValue = this.end.toBigInteger();
		
		while (currentValue.compareTo(endValue) <= 0) {
			int prefixLength = findOptimalPrefixLength(currentValue, endValue);
			Ipv6Address networkAddr = bigIntegerToIpv6(currentValue);
			networks.add(Ipv6Network.of(networkAddr, prefixLength));
			
			BigInteger blockSize = BigInteger.ONE.shiftLeft(128 - prefixLength);
			currentValue = currentValue.add(blockSize);
		}
		return networks;
	}
	
	/**
	 * Finds the optimal prefix length for a CIDR block starting at the given value.<br>
	 *
	 * @param startValue The starting address value
	 * @param endValue The ending address value (inclusive)
	 * @return The optimal prefix length
	 * @throws NullPointerException If start value or end value is null
	 */
	private static int findOptimalPrefixLength(@NonNull BigInteger startValue, @NonNull BigInteger endValue) {
		Objects.requireNonNull(startValue, "Start value must not be null");
		Objects.requireNonNull(endValue, "End value must not be null");
		BigInteger remaining = endValue.subtract(startValue).add(BigInteger.ONE);
		
		// Iterate from largest block (prefix 0) to smallest (prefix 128)
		// Return the first (largest) aligned block that fits within remaining
		for (int prefixLength = 0; prefixLength <= 128; prefixLength++) {
			BigInteger blockSize = BigInteger.ONE.shiftLeft(128 - prefixLength);
			
			// Skip if start address is not aligned to this block size
			if (!startValue.mod(blockSize).equals(BigInteger.ZERO)) {
				continue;
			}
			
			// Return this prefix if the block fits within remaining addresses
			if (blockSize.compareTo(remaining) <= 0) {
				return prefixLength;
			}
		}
		return 128;
	}
	
	/**
	 * Converts a BigInteger to an IPv6 address.<br>
	 *
	 * @param value The BigInteger value
	 * @return The corresponding IPv6 address
	 * @throws NullPointerException If value is null
	 */
	private static @NonNull Ipv6Address bigIntegerToIpv6(@NonNull BigInteger value) {
		Objects.requireNonNull(value, "Value must not be null");
		byte[] bytes = value.toByteArray();
		byte[] addressBytes = new byte[16];
		
		if (bytes.length <= 16) {
			System.arraycopy(bytes, 0, addressBytes, 16 - bytes.length, bytes.length);
		} else {
			System.arraycopy(bytes, bytes.length - 16, addressBytes, 0, 16);
		}
		
		long highBits = 0;
		for (int i = 0; i < 8; i++) {
			highBits = (highBits << 8) | (addressBytes[i] & 0xFFL);
		}
		long lowBits = 0;
		for (int i = 8; i < 16; i++) {
			lowBits = (lowBits << 8) | (addressBytes[i] & 0xFFL);
		}
		return new Ipv6Address(highBits, lowBits);
	}
	
	@Override
	public boolean isSingleAddress() {
		return this.start.equals(this.end);
	}
	
	@Override
	public boolean isNetwork() {
		BigInteger startValue = this.start.toBigInteger();
		BigInteger endValue = this.end.toBigInteger();
		BigInteger size = this.size();
		
		if (size.bitCount() != 1) {
			return false;
		}
		
		int hostBits = size.bitLength() - 1;
		int prefixLength = 128 - hostBits;
		
		if (prefixLength < 0 || prefixLength > 128) {
			return false;
		}
		
		BigInteger blockSize = BigInteger.ONE.shiftLeft(hostBits);
		if (!startValue.mod(blockSize).equals(BigInteger.ZERO)) {
			return false;
		}
		
		BigInteger expectedEnd = startValue.add(blockSize).subtract(BigInteger.ONE);
		return endValue.equals(expectedEnd);
	}
	
	@Override
	public @NonNull Optional<Ipv6Network> toNetwork() {
		if (!this.isNetwork()) {
			return Optional.empty();
		}
		
		BigInteger size = this.size();
		int hostBits = size.bitLength() - 1;
		int prefixLength = 128 - hostBits;
		return Optional.of(Ipv6Network.of(this.start, prefixLength));
	}
	
	@Override
	public int compareTo(@NonNull Ipv6Range other) {
		int startCompare = this.start.compareTo(other.start);
		if (startCompare != 0) {
			return startCompare;
		}
		return this.end.compareTo(other.end);
	}
	
	@Override
	public @NonNull String toString() {
		if (this.isSingleAddress()) {
			return this.start.toString();
		}
		return this.start + "-" + this.end;
	}
	
	//region Inner classes
	
	/**
	 * Iterator implementation for iterating over addresses in an IPv6 range.<br>
	 *
	 * @author Luis-St
	 */
	private static final class Ipv6RangeIterator implements Iterator<Ipv6Address> {
		
		/**
		 * The end address of the range (inclusive).<br>
		 */
		private final Ipv6Address endAddress;
		/**
		 * The current address in the iteration.<br>
		 */
		private Ipv6Address currentAddress;
		/**
		 * Indicates if there are more addresses to iterate.<br>
		 */
		private boolean hasNext;
		
		/**
		 * Creates a new iterator from the start address to the end address (inclusive).
		 *
		 * @param startAddress The first address to iterate
		 * @param endAddress The last address to iterate (inclusive)
		 * @throws NullPointerException If start address or end address is null
		 */
		Ipv6RangeIterator(@NonNull Ipv6Address startAddress, @NonNull Ipv6Address endAddress) {
			this.currentAddress = Objects.requireNonNull(startAddress, "Start address must not be null");
			this.endAddress = Objects.requireNonNull(endAddress, "End address must not be null");
			this.hasNext = startAddress.compareTo(endAddress) <= 0;
		}
		
		@Override
		public boolean hasNext() {
			return this.hasNext;
		}
		
		@Override
		public @NonNull Ipv6Address next() {
			if (!this.hasNext) {
				throw new NoSuchElementException("No more addresses in range");
			}
			
			Ipv6Address result = this.currentAddress;
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
