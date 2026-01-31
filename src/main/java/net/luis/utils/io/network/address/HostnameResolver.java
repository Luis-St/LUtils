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

import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

/**
 * Utility class for DNS hostname resolution.<br>
 * This class provides methods for both forward resolution (hostname to IP address) and reverse resolution (IP address to hostname).<br>
 * <p>
 *     All resolution methods are available in both synchronous and asynchronous variants.<br>
 *     The synchronous methods block until the DNS operation completes or times out.<br>
 *     The asynchronous methods return {@link CompletableFuture} instances that complete when the DNS operation finishes.
 * </p>
 * <p>
 *     <b>Warning:</b> DNS resolution is a network operation that can take several seconds,<br>
 *     especially for reverse lookups that may have no PTR record.<br>
 *     Consider using the async variants or methods with timeout parameters in production code.
 * </p>
 * <pre>{@code
 * // Forward resolution
 * List<IpAddress<?>> addresses = HostnameResolver.resolveAll("example.com");
 *
 * // Reverse resolution
 * Ipv4Address ip = IpAddresses.parseIpv4("8.8.8.8");
 * Optional<String> hostname = HostnameResolver.reverseResolve(ip);
 * // Result: Optional[dns.google]
 *
 * // Async resolution with timeout
 * CompletableFuture<Optional<String>> future = HostnameResolver.reverseResolveAsync(ip);
 * Optional<String> result = future.orTimeout(5, TimeUnit.SECONDS).join();
 * }</pre>
 *
 * @author Luis-St
 */
public final class HostnameResolver {
	
	/**
	 * Shared executor for async DNS operations.<br>
	 * Uses daemon threads to avoid blocking JVM shutdown.<br>
	 * <p>
	 *     This field is non-final to allow replacement of the executor if needed.<br>
	 *     If you replace it, ensure the new executor uses daemon threads to prevent blocking shutdown.
	 * </p>
	 */
	public static ExecutorService dnsExecutor = Executors.newCachedThreadPool(r -> {
		Thread t = new Thread(r, "dns-resolver");
		t.setDaemon(true);
		return t;
	});
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private HostnameResolver() {}
	
	/**
	 * Resolves all IP addresses associated with the given hostname.<br>
	 * This method performs a DNS lookup and returns all A (IPv4) and AAAA (IPv6) records for the specified hostname.<br>
	 * <p>
	 *     The returned list may contain both IPv4 and IPv6 addresses, depending
	 *     on what records exist for the hostname.
	 * </p>
	 *
	 * @param hostname The hostname to resolve
	 * @return A list of all IP addresses associated with the hostname, or an empty list if resolution fails
	 * @throws NullPointerException If hostname is null
	 */
	public static @NonNull List<IpAddress<?>> resolveAll(@NonNull String hostname) {
		Objects.requireNonNull(hostname, "Hostname must not be null");
		
		try {
			InetAddress[] addresses = InetAddress.getAllByName(hostname);
			List<IpAddress<?>> result = new ArrayList<>();
			for (InetAddress address : addresses) {
				
				if (address instanceof Inet4Address inet4) {
					result.add(Ipv4Address.from(inet4));
				} else if (address instanceof Inet6Address inet6) {
					result.add(Ipv6Address.from(inet6));
				}
			}
			return result;
		} catch (UnknownHostException e) {
			return List.of();
		}
	}
	
	/**
	 * Resolves all IPv4 addresses associated with the given hostname.<br>
	 * This method performs a DNS lookup and returns only the A (IPv4) records for the specified hostname.<br>
	 *
	 * @param hostname The hostname to resolve
	 * @return A list of all IPv4 addresses associated with the hostname, or an empty list if resolution fails
	 * @throws NullPointerException If hostname is null
	 */
	public static @NonNull List<Ipv4Address> resolveAllIpv4(@NonNull String hostname) {
		Objects.requireNonNull(hostname, "Hostname must not be null");
		
		try {
			InetAddress[] addresses = InetAddress.getAllByName(hostname);
			List<Ipv4Address> result = new ArrayList<>();
			
			for (InetAddress address : addresses) {
				if (address instanceof Inet4Address inet4) {
					result.add(Ipv4Address.from(inet4));
				}
			}
			return result;
		} catch (UnknownHostException e) {
			return List.of();
		}
	}
	
	/**
	 * Resolves all IPv6 addresses associated with the given hostname.<br>
	 * This method performs a DNS lookup and returns only the AAAA (IPv6) records for the specified hostname.<br>
	 *
	 * @param hostname The hostname to resolve
	 * @return A list of all IPv6 addresses associated with the hostname, or an empty list if resolution fails
	 * @throws NullPointerException If hostname is null
	 */
	public static @NonNull List<Ipv6Address> resolveAllIpv6(@NonNull String hostname) {
		Objects.requireNonNull(hostname, "Hostname must not be null");
		
		try {
			InetAddress[] addresses = InetAddress.getAllByName(hostname);
			List<Ipv6Address> result = new ArrayList<>();
			
			for (InetAddress address : addresses) {
				if (address instanceof Inet6Address inet6) {
					result.add(Ipv6Address.from(inet6));
				}
			}
			return result;
		} catch (UnknownHostException e) {
			return List.of();
		}
	}
	
	/**
	 * Resolves the first IPv4 address associated with the given hostname.<br>
	 * This method performs a DNS lookup and returns the first A (IPv4) record for the specified hostname.<br>
	 *
	 * @param hostname The hostname to resolve
	 * @return An {@link Optional} containing the first IPv4 address, or empty if resolution fails or no IPv4 address exists
	 * @throws NullPointerException If hostname is null
	 */
	public static @NonNull Optional<Ipv4Address> resolveIpv4(@NonNull String hostname) {
		List<Ipv4Address> addresses = resolveAllIpv4(hostname);
		return addresses.isEmpty() ? Optional.empty() : Optional.of(addresses.getFirst());
	}
	
	/**
	 * Resolves the first IPv6 address associated with the given hostname.<br>
	 * This method performs a DNS lookup and returns the first AAAA (IPv6) record for the specified hostname.<br>
	 *
	 * @param hostname The hostname to resolve
	 * @return An {@link Optional} containing the first IPv6 address, or empty if resolution fails or no IPv6 address exists
	 * @throws NullPointerException If hostname is null
	 */
	public static @NonNull Optional<Ipv6Address> resolveIpv6(@NonNull String hostname) {
		List<Ipv6Address> addresses = resolveAllIpv6(hostname);
		return addresses.isEmpty() ? Optional.empty() : Optional.of(addresses.getFirst());
	}
	
	/**
	 * Resolves the first IP address (of any type) associated with the given hostname.<br>
	 * This method performs a DNS lookup and returns the first address found, which may be either IPv4 or IPv6.<br>
	 *
	 * @param hostname The hostname to resolve
	 * @return An {@link Optional} containing the first IP address, or empty if resolution fails
	 * @throws NullPointerException If hostname is null
	 */
	public static @NonNull Optional<IpAddress<?>> resolve(@NonNull String hostname) {
		Objects.requireNonNull(hostname, "Hostname must not be null");
		
		try {
			InetAddress address = InetAddress.getByName(hostname);
			
			if (address instanceof Inet4Address inet4) {
				return Optional.of(Ipv4Address.from(inet4));
			} else if (address instanceof Inet6Address inet6) {
				return Optional.of(Ipv6Address.from(inet6));
			}
			return Optional.empty();
		} catch (UnknownHostException e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Asynchronously resolves all IP addresses associated with the given hostname.<br>
	 * This method returns a {@link CompletableFuture} that will complete with all A (IPv4) and AAAA (IPv6) records for the specified hostname.<br>
	 *
	 * @param hostname The hostname to resolve
	 * @return A {@link CompletableFuture} that will complete with the list of resolved addresses
	 * @throws NullPointerException If hostname is null
	 */
	public static @NonNull CompletableFuture<List<IpAddress<?>>> resolveAllAsync(@NonNull String hostname) {
		Objects.requireNonNull(hostname, "Hostname must not be null");
		return CompletableFuture.supplyAsync(() -> resolveAll(hostname), dnsExecutor);
	}
	
	/**
	 * Asynchronously resolves the first IP address (of any type) associated with the given hostname.<br>
	 * This method returns a {@link CompletableFuture} that will complete with the first address found, which may be either IPv4 or IPv6.<br>
	 *
	 * @param hostname The hostname to resolve
	 * @return A {@link CompletableFuture} that will complete with the resolved address
	 * @throws NullPointerException If hostname is null
	 */
	public static @NonNull CompletableFuture<Optional<IpAddress<?>>> resolveAsync(@NonNull String hostname) {
		Objects.requireNonNull(hostname, "Hostname must not be null");
		return CompletableFuture.supplyAsync(() -> resolve(hostname), dnsExecutor);
	}
	
	/**
	 * Performs a reverse DNS lookup for the given IP address.<br>
	 * This method queries the DNS system to find the PTR record associated with the IP address and returns the corresponding hostname.<br>
	 * <p>
	 *     <b>Warning:</b> This method performs a blocking network operation and may take several seconds to complete,
	 *     depending on network conditions and DNS server responsiveness.
	 * </p>
	 *
	 * @param address The IP address to resolve
	 * @return An {@link Optional} containing the resolved hostname, or empty if resolution fails or no PTR record exists
	 * @throws NullPointerException If address is null
	 */
	public static @NonNull Optional<String> reverseResolve(@NonNull IpAddress<?> address) {
		Objects.requireNonNull(address, "Address must not be null");
		
		try {
			InetAddress inetAddress = address.toInetAddress();
			String hostname = inetAddress.getCanonicalHostName();
			
			if (hostname.equals(inetAddress.getHostAddress())) {
				return Optional.empty();
			}
			return Optional.of(hostname);
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Performs a reverse DNS lookup for the given IP address with a timeout.<br>
	 * This method queries the DNS system to find the PTR record associated with the IP address, but will abort and return empty if the lookup takes longer
	 * than the specified duration.<br>
	 *
	 * @param address The IP address to resolve
	 * @param timeout The maximum duration to wait for the DNS lookup to complete
	 * @return An {@link Optional} containing the resolved hostname, or empty if resolution fails, times out, or no PTR record exists
	 * @throws NullPointerException If address or timeout is null
	 */
	public static @NonNull Optional<String> reverseResolve(@NonNull IpAddress<?> address, @NonNull Duration timeout) {
		Objects.requireNonNull(address, "Address must not be null");
		Objects.requireNonNull(timeout, "Timeout must not be null");
		try {
			return reverseResolveAsync(address).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Asynchronously performs a reverse DNS lookup for the given IP address.<br>
	 * This method returns a {@link CompletableFuture} that will complete with the resolved hostname when the DNS lookup finishes.<br>
	 *
	 * @param address The IP address to resolve
	 * @return A {@link CompletableFuture} that will complete with the resolved hostname
	 * @throws NullPointerException If address is null
	 */
	public static @NonNull CompletableFuture<Optional<String>> reverseResolveAsync(@NonNull IpAddress<?> address) {
		Objects.requireNonNull(address, "Address must not be null");
		return CompletableFuture.supplyAsync(() -> reverseResolve(address), dnsExecutor);
	}
	
	/**
	 * Performs a reverse DNS lookup for the given IP address bytes.<br>
	 * This method accepts raw IP address bytes (4 bytes for IPv4, 16 bytes for IPv6) and performs a reverse DNS lookup.<br>
	 *
	 * @param addressBytes The IP address bytes in network byte order
	 * @return An {@link Optional} containing the resolved hostname, or empty if resolution fails or no PTR record exists
	 * @throws NullPointerException If addressBytes is null
	 * @throws IllegalArgumentException If addressBytes has an invalid length (must be 4 or 16)
	 */
	public static @NonNull Optional<String> reverseResolve(byte @NonNull [] addressBytes) {
		Objects.requireNonNull(addressBytes, "Address bytes must not be null");
		if (addressBytes.length != 4 && addressBytes.length != 16) {
			throw new IllegalArgumentException("Address bytes must have length 4 (IPv4) or 16 (IPv6), got: " + addressBytes.length);
		}
		try {
			InetAddress inetAddress = InetAddress.getByAddress(addressBytes);
			String hostname = inetAddress.getCanonicalHostName();
			// If the hostname equals the IP address string, no PTR record was found
			if (hostname.equals(inetAddress.getHostAddress())) {
				return Optional.empty();
			}
			return Optional.of(hostname);
		} catch (UnknownHostException e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Checks if the given hostname is resolvable to at least one IP address.<br>
	 * This method performs a DNS lookup and returns whether any A or AAAA records exist for the hostname.<br>
	 *
	 * @param hostname The hostname to check
	 * @return {@code true} if the hostname resolves to at least one IP address, {@code false} otherwise
	 * @throws NullPointerException If hostname is null
	 */
	public static boolean isResolvable(@NonNull String hostname) {
		Objects.requireNonNull(hostname, "Hostname must not be null");
		try {
			InetAddress.getByName(hostname);
			return true;
		} catch (UnknownHostException e) {
			return false;
		}
	}
	
	/**
	 * Gets the canonical hostname for the given IP address.<br>
	 * The canonical hostname is the fully qualified domain name (FQDN) associated with the IP address, as determined by a reverse DNS lookup.<br>
	 * <p>
	 *     Unlike {@link #reverseResolve(IpAddress)}, this method returns the raw result
	 *     from the DNS system, which may be the IP address string itself if no PTR record exists.
	 * </p>
	 *
	 * @param address The IP address to look up
	 * @return An {@link Optional} containing the canonical hostname, or empty if the lookup fails
	 * @throws NullPointerException If address is null
	 */
	public static @NonNull Optional<String> getCanonicalHostname(@NonNull IpAddress<?> address) {
		Objects.requireNonNull(address, "Address must not be null");
		try {
			InetAddress inetAddress = address.toInetAddress();
			String canonical = inetAddress.getCanonicalHostName();
			
			return Optional.of(canonical);
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Shuts down the DNS executor service.<br>
	 * This method should be called during application shutdown to cleanly terminate the background thread pool used for async DNS operations.<br>
	 * <p>
	 *     <b>Note:</b> After calling this method, async resolution methods may fail or behave unpredictably.<br>
	 *     This method is intended for use during application shutdown only.
	 * </p>
	 */
	public static void shutdown() {
		dnsExecutor.shutdown();
	}
}
