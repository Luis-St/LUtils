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
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link HostnameResolver}.<br>
 *
 * @author Luis-St
 */
class HostnameResolverTest {
	
	@Test
	void resolveAllWithNullThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.resolveAll(null));
	}
	
	@Test
	void resolveAllWithLocalhost() {
		List<IpAddress<?>> addresses = HostnameResolver.resolveAll("localhost");
		assertNotNull(addresses);
		assertFalse(addresses.isEmpty());
	}
	
	@Test
	void resolveAllWithInvalidHostnameReturnsEmpty() {
		List<IpAddress<?>> addresses = HostnameResolver.resolveAll("this.hostname.definitely.does.not.exist.invalid");
		assertNotNull(addresses);
		assertTrue(addresses.isEmpty());
	}
	
	@Test
	void resolveAllIpv4WithNullThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.resolveAllIpv4(null));
	}
	
	@Test
	void resolveAllIpv4WithLocalhost() {
		List<Ipv4Address> addresses = HostnameResolver.resolveAllIpv4("localhost");
		assertNotNull(addresses);
	}
	
	@Test
	void resolveAllIpv4WithInvalidHostnameReturnsEmpty() {
		List<Ipv4Address> addresses = HostnameResolver.resolveAllIpv4("this.hostname.definitely.does.not.exist.invalid");
		assertNotNull(addresses);
		assertTrue(addresses.isEmpty());
	}
	
	@Test
	void resolveAllIpv6WithNullThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.resolveAllIpv6(null));
	}
	
	@Test
	void resolveAllIpv6WithInvalidHostnameReturnsEmpty() {
		List<Ipv6Address> addresses = HostnameResolver.resolveAllIpv6("this.hostname.definitely.does.not.exist.invalid");
		assertNotNull(addresses);
		assertTrue(addresses.isEmpty());
	}
	
	@Test
	void resolveIpv4WithNullThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.resolveIpv4(null));
	}
	
	@Test
	void resolveIpv4WithInvalidHostnameReturnsEmpty() {
		Optional<Ipv4Address> address = HostnameResolver.resolveIpv4("this.hostname.definitely.does.not.exist.invalid");
		assertNotNull(address);
		assertTrue(address.isEmpty());
	}
	
	@Test
	void resolveIpv6WithNullThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.resolveIpv6(null));
	}
	
	@Test
	void resolveIpv6WithInvalidHostnameReturnsEmpty() {
		Optional<Ipv6Address> address = HostnameResolver.resolveIpv6("this.hostname.definitely.does.not.exist.invalid");
		assertNotNull(address);
		assertTrue(address.isEmpty());
	}
	
	@Test
	void resolveWithNullThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.resolve(null));
	}
	
	@Test
	void resolveWithLocalhost() {
		Optional<IpAddress<?>> address = HostnameResolver.resolve("localhost");
		assertNotNull(address);
		assertTrue(address.isPresent());
	}
	
	@Test
	void resolveWithInvalidHostnameReturnsEmpty() {
		Optional<IpAddress<?>> address = HostnameResolver.resolve("this.hostname.definitely.does.not.exist.invalid");
		assertNotNull(address);
		assertTrue(address.isEmpty());
	}
	
	@Test
	void resolveAllAsyncWithNullThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.resolveAllAsync(null));
	}
	
	@Test
	void resolveAllAsyncReturnsCompletableFuture() {
		CompletableFuture<List<IpAddress<?>>> future = HostnameResolver.resolveAllAsync("localhost");
		assertNotNull(future);
		List<IpAddress<?>> result = future.join();
		assertNotNull(result);
	}
	
	@Test
	void resolveAsyncWithNullThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.resolveAsync(null));
	}
	
	@Test
	void resolveAsyncReturnsCompletableFuture() {
		CompletableFuture<Optional<IpAddress<?>>> future = HostnameResolver.resolveAsync("localhost");
		assertNotNull(future);
		Optional<IpAddress<?>> result = future.join();
		assertNotNull(result);
	}
	
	@Test
	void reverseResolveWithNullAddressThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.reverseResolve((IpAddress<?>) null));
	}
	
	@Test
	void reverseResolveWithLoopbackAddress() {
		Optional<String> hostname = HostnameResolver.reverseResolve(Ipv4Address.LOOPBACK);
		assertNotNull(hostname);
	}
	
	@Test
	void reverseResolveWithTimeoutNullAddressThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.reverseResolve(null, Duration.ofSeconds(1)));
	}
	
	@Test
	void reverseResolveWithTimeoutNullDurationThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.reverseResolve(Ipv4Address.LOOPBACK, null));
	}
	
	@Test
	void reverseResolveWithTimeout() {
		Optional<String> hostname = HostnameResolver.reverseResolve(Ipv4Address.LOOPBACK, Duration.ofSeconds(5));
		assertNotNull(hostname);
	}
	
	@Test
	void reverseResolveAsyncWithNullThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.reverseResolveAsync(null));
	}
	
	@Test
	void reverseResolveAsyncReturnsCompletableFuture() {
		CompletableFuture<Optional<String>> future = HostnameResolver.reverseResolveAsync(Ipv4Address.LOOPBACK);
		assertNotNull(future);
	}
	
	@Test
	void reverseResolveWithNullBytesThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.reverseResolve((byte[]) null));
	}
	
	@Test
	void reverseResolveWithInvalidByteLengthThrows() {
		assertThrows(IllegalArgumentException.class, () -> HostnameResolver.reverseResolve(new byte[0]));
		assertThrows(IllegalArgumentException.class, () -> HostnameResolver.reverseResolve(new byte[3]));
		assertThrows(IllegalArgumentException.class, () -> HostnameResolver.reverseResolve(new byte[5]));
		assertThrows(IllegalArgumentException.class, () -> HostnameResolver.reverseResolve(new byte[15]));
		assertThrows(IllegalArgumentException.class, () -> HostnameResolver.reverseResolve(new byte[17]));
	}
	
	@Test
	void reverseResolveWithValidIpv4Bytes() {
		byte[] loopback = { 127, 0, 0, 1 };
		Optional<String> hostname = HostnameResolver.reverseResolve(loopback);
		assertNotNull(hostname);
	}
	
	@Test
	void reverseResolveWithValidIpv6Bytes() {
		byte[] loopback = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 };
		Optional<String> hostname = HostnameResolver.reverseResolve(loopback);
		assertNotNull(hostname);
	}
	
	@Test
	void isResolvableWithNullThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.isResolvable(null));
	}
	
	@Test
	void isResolvableWithLocalhost() {
		assertTrue(HostnameResolver.isResolvable("localhost"));
	}
	
	@Test
	void isResolvableWithInvalidHostname() {
		assertFalse(HostnameResolver.isResolvable("this.hostname.definitely.does.not.exist.invalid"));
	}
	
	@Test
	void getCanonicalHostnameWithNullThrows() {
		assertThrows(NullPointerException.class, () -> HostnameResolver.getCanonicalHostname(null));
	}
	
	@Test
	void getCanonicalHostnameWithLoopback() {
		Optional<String> canonical = HostnameResolver.getCanonicalHostname(Ipv4Address.LOOPBACK);
		assertNotNull(canonical);
		assertTrue(canonical.isPresent());
	}
	
	@Test
	void dnsExecutorIsNotNull() {
		assertNotNull(HostnameResolver.dnsExecutor);
	}
	
	@Test
	void resolveReturnsIpv4OrIpv6() {
		Optional<IpAddress<?>> address = HostnameResolver.resolve("localhost");
		IpAddress<?> ip = address.get();
		assertTrue(ip instanceof Ipv4Address || ip instanceof Ipv6Address);
	}
	
	@Test
	void resolveAllContainsOnlyValidTypes() {
		List<IpAddress<?>> addresses = HostnameResolver.resolveAll("localhost");
		for (IpAddress<?> address : addresses) {
			assertTrue(address instanceof Ipv4Address || address instanceof Ipv6Address);
		}
	}
}
