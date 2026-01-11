# IP Address Library - Implementation Tasks

Based on the concept analysis, this document contains all identified improvements, suggestions, and tasks to complete the implementation.

---

## Improvements

### 1. Add `ParseOptions` Integration in Factory Methods
**Status:** Approved
**Priority:** High

**Task:** Add overloaded parsing methods in `IpAddresses.java` that accept `ParseOptions`:

```java
static Ipv4Address parseIpv4(String address, ParseOptions options);
static Optional<Ipv4Address> tryParseIpv4(String address, ParseOptions options);
static Ipv6Address parseIpv6(String address, ParseOptions options);
static Optional<Ipv6Address> tryParseIpv6(String address, ParseOptions options);
static IpAddress<?> parse(String address, ParseOptions options);
static Optional<? extends IpAddress<?>> tryParse(String address, ParseOptions options);
```

**Files to modify:**
- `IpAddresses.java`

---

### 2. Add Zone ID Support to `Ipv6Address`
**Status:** Approved
**Priority:** Medium

**Task:** Add zone identifier support to the `Ipv6Address` interface. Zone IDs are used for link-local addresses to specify the network interface (e.g., `fe80::1%eth0`).

Add the following methods to `Ipv6Address.java`:

```java
/**
 * Returns the zone identifier if present.
 * Zone IDs are typically interface names (e.g., "eth0") or interface indices.
 *
 * @return the zone ID, or empty if not present
 */
Optional<String> zoneId();

/**
 * Returns a new address with the specified zone identifier.
 *
 * @param zoneId the zone identifier to attach
 * @return a new address with the zone ID
 */
Ipv6Address withZoneId(String zoneId);

/**
 * Returns this address without the zone identifier.
 *
 * @return a new address without zone ID, or this if no zone ID present
 */
Ipv6Address withoutZoneId();
```

**Files to modify:**
- `Ipv6Address.java`

---

### 3. Change `distanceTo()` to Return `BigInteger`
**Status:** Approved
**Priority:** High

**Task:** Update the `distanceTo()` method signature in `IpAddress.java` to use `BigInteger` instead of `Long`. This is necessary because IPv6 address space is 128 bits and the distance between addresses can exceed `Long.MAX_VALUE`.

Change from:
```java
Optional<Long> distanceTo(T other);
```

To:
```java
/**
 * Calculates the distance from this address to another.
 * Returns a positive value if other is greater, negative if smaller.
 *
 * @param other the other address
 * @return the distance as a BigInteger
 */
BigInteger distanceTo(T other);
```

**Note:** Remove the `Optional` wrapper since `BigInteger` can represent any distance. Consider if `Optional` is still needed for null safety.

**Files to modify:**
- `IpAddress.java`

**Import to add:**
- `java.math.BigInteger`

---

### 4. Add Iteration Safety Documentation (No Code Change)
**Status:** Approved (documentation only)
**Priority:** Low

**Task:** Add Javadoc warnings to `IpNetwork.iterator()` and `IpRange.iterator()` about potential memory/performance issues when iterating large networks.

Update the Javadoc in `IpNetwork.java`:

```java
/**
 * Returns an iterator over all addresses in this network.
 * <p>
 * <b>Warning:</b> For large networks (e.g., /8 for IPv4, /64 for IPv6),
 * iterating over all addresses may consume significant memory and time.
 * Consider using {@link #addressStream()} with a limit, or check {@link #size()}
 * before iterating. For example, a /24 network contains 256 addresses,
 * while a /8 contains over 16 million.
 */
@Override
Iterator<A> iterator();
```

Similar warning for `IpRange.java`:

```java
/**
 * Returns an iterator over all addresses in this range.
 * <p>
 * <b>Warning:</b> For large ranges, iterating over all addresses may consume
 * significant memory and time. Check {@link #size()} before iterating or use
 * {@link #addressStream()} with a limit.
 */
@Override
Iterator<A> iterator();
```

**Files to modify:**
- `IpNetwork.java`
- `IpRange.java`

---

### 5. Convert `IpAddresses` from Interface to Final Class
**Status:** Approved
**Priority:** Medium

**Task:** Change `IpAddresses` from an interface with static methods to a final utility class. This is the standard Java convention for utility classes.

Change from:
```java
public interface IpAddresses {
```

To:
```java
public final class IpAddresses {

    private IpAddresses() {
        // Prevent instantiation
    }

    // ... all existing static methods remain the same
}
```

**Files to modify:**
- `IpAddresses.java`

---

### 6. Add Well-Known Network Constants
**Status:** Approved
**Priority:** Medium

**Task:** Add factory methods for commonly used network constants to `IpAddresses.java`:

```java
// ==================== Well-Known Networks ====================

/**
 * Returns the IPv4 loopback network (127.0.0.0/8).
 */
static Ipv4Network ipv4LoopbackNetwork() {
    throw new UnsupportedOperationException("Not implemented");
}

/**
 * Returns the IPv4 link-local network (169.254.0.0/16).
 */
static Ipv4Network ipv4LinkLocalNetwork() {
    throw new UnsupportedOperationException("Not implemented");
}

/**
 * Returns the IPv4 private network 10.0.0.0/8.
 */
static Ipv4Network ipv4PrivateClassA() {
    throw new UnsupportedOperationException("Not implemented");
}

/**
 * Returns the IPv4 private network 172.16.0.0/12.
 */
static Ipv4Network ipv4PrivateClassB() {
    throw new UnsupportedOperationException("Not implemented");
}

/**
 * Returns the IPv4 private network 192.168.0.0/16.
 */
static Ipv4Network ipv4PrivateClassC() {
    throw new UnsupportedOperationException("Not implemented");
}

/**
 * Returns the IPv4 shared address space (100.64.0.0/10, RFC 6598).
 */
static Ipv4Network ipv4SharedAddressSpace() {
    throw new UnsupportedOperationException("Not implemented");
}

/**
 * Returns the IPv6 loopback network (::1/128).
 */
static Ipv6Network ipv6LoopbackNetwork() {
    throw new UnsupportedOperationException("Not implemented");
}

/**
 * Returns the IPv6 link-local network (fe80::/10).
 */
static Ipv6Network ipv6LinkLocalNetwork() {
    throw new UnsupportedOperationException("Not implemented");
}

/**
 * Returns the IPv6 unique local address network (fc00::/7).
 */
static Ipv6Network ipv6UniqueLocalNetwork() {
    throw new UnsupportedOperationException("Not implemented");
}

/**
 * Returns the IPv6 documentation network (2001:db8::/32).
 */
static Ipv6Network ipv6DocumentationNetwork() {
    throw new UnsupportedOperationException("Not implemented");
}

/**
 * Returns the IPv6 multicast network (ff00::/8).
 */
static Ipv6Network ipv6MulticastNetwork() {
    throw new UnsupportedOperationException("Not implemented");
}
```

**Files to modify:**
- `IpAddresses.java`

---

### 7. Add Builder Pattern for Addresses
**Status:** Approved
**Priority:** Low

**Task:** Add builder classes for constructing addresses step-by-step.

Add to `Ipv4Address.java`:

```java
/**
 * Creates a new builder for constructing an IPv4 address.
 */
static Builder builder() {
    throw new UnsupportedOperationException("Not implemented");
}

/**
 * Builder for constructing IPv4 addresses.
 */
interface Builder {
    /**
     * Sets the octet at the specified index.
     *
     * @param index the octet index (0-3)
     * @param value the octet value (0-255)
     * @return this builder
     * @throws IllegalArgumentException if index or value is out of range
     */
    Builder octet(int index, int value);

    /**
     * Sets all four octets.
     *
     * @param a first octet
     * @param b second octet
     * @param c third octet
     * @param d fourth octet
     * @return this builder
     */
    Builder octets(int a, int b, int c, int d);

    /**
     * Builds the IPv4 address.
     *
     * @return the constructed address
     * @throws IllegalStateException if not all octets have been set
     */
    Ipv4Address build();
}
```

Add to `Ipv6Address.java`:

```java
/**
 * Creates a new builder for constructing an IPv6 address.
 */
static Builder builder() {
    throw new UnsupportedOperationException("Not implemented");
}

/**
 * Builder for constructing IPv6 addresses.
 */
interface Builder {
    /**
     * Sets the hextet at the specified index.
     *
     * @param index the hextet index (0-7)
     * @param value the hextet value (0-65535)
     * @return this builder
     */
    Builder hextet(int index, int value);

    /**
     * Sets the high 64 bits.
     *
     * @param highBits the high 64 bits
     * @return this builder
     */
    Builder highBits(long highBits);

    /**
     * Sets the low 64 bits.
     *
     * @param lowBits the low 64 bits
     * @return this builder
     */
    Builder lowBits(long lowBits);

    /**
     * Sets the zone identifier.
     *
     * @param zoneId the zone identifier
     * @return this builder
     */
    Builder zoneId(String zoneId);

    /**
     * Builds the IPv6 address.
     *
     * @return the constructed address
     */
    Ipv6Address build();
}
```

**Files to modify:**
- `Ipv4Address.java`
- `Ipv6Address.java`

---

### 8. Add MAC Address Support
**Status:** Approved
**Priority:** Medium

**Task:** Create a complete MAC address handling subsystem. MAC addresses are closely related to IPv6 through EUI-64, and the library already references `isEui64()`.

#### Create new file `MacAddress.java`:

```java
package net.luis.ip;

import java.util.Optional;

/**
 * Represents a MAC (Media Access Control) address (EUI-48).
 * MAC addresses are 48-bit identifiers assigned to network interfaces.
 * Implementations must be immutable.
 */
public interface MacAddress extends Comparable<MacAddress> {

    /**
     * The number of bits in a MAC address.
     */
    int BIT_LENGTH = 48;

    /**
     * The number of octets in a MAC address.
     */
    int OCTET_COUNT = 6;

    /**
     * Returns the MAC address as raw bytes in network byte order.
     */
    byte[] toBytes();

    /**
     * Returns the MAC address as a 48-bit value stored in a long.
     */
    long toLong();

    /**
     * Returns the octet at the specified index (0-5, where 0 is the most significant).
     *
     * @param index the octet index (0-5)
     * @return the octet value (0-255)
     * @throws IndexOutOfBoundsException if index is out of range
     */
    int getOctet(int index);

    /**
     * Returns a new address with the specified octet changed.
     *
     * @param index the octet index (0-5)
     * @param value the new octet value (0-255)
     * @return a new address with the modified octet
     * @throws IllegalArgumentException if value is out of range
     */
    MacAddress withOctet(int index, int value);

    /**
     * Returns the OUI (Organizationally Unique Identifier) bytes.
     * The OUI is the first 3 octets, assigned to vendors by IEEE.
     */
    byte[] getOui();

    /**
     * Returns the OUI as a colon-separated string (e.g., "00:1A:2B").
     */
    String getOuiString();

    /**
     * Checks if this is a unicast address (I/G bit = 0).
     */
    boolean isUnicast();

    /**
     * Checks if this is a multicast address (I/G bit = 1).
     */
    boolean isMulticast();

    /**
     * Checks if this is a universally administered address (U/L bit = 0).
     */
    boolean isUniversal();

    /**
     * Checks if this is a locally administered address (U/L bit = 1).
     */
    boolean isLocal();

    /**
     * Checks if this is the broadcast address (FF:FF:FF:FF:FF:FF).
     */
    boolean isBroadcast();

    /**
     * Returns the colon-delimited string representation (e.g., "00:1A:2B:3C:4D:5E").
     */
    String toColonString();

    /**
     * Returns the dash-delimited string representation (e.g., "00-1A-2B-3C-4D-5E").
     */
    String toDashString();

    /**
     * Returns the Cisco-style dotted string representation (e.g., "001A.2B3C.4D5E").
     */
    String toCiscoString();

    /**
     * Returns the bare hexadecimal string representation (e.g., "001A2B3C4D5E").
     */
    String toBareString();

    /**
     * Returns the canonical string representation (colon-delimited, lowercase).
     */
    @Override
    String toString();

    /**
     * Converts this MAC address to a Modified EUI-64 identifier.
     * The algorithm:
     * 1. Split the MAC into two 24-bit halves
     * 2. Insert FFFE in the middle
     * 3. Flip the U/L bit (7th bit)
     *
     * @return the 64-bit EUI-64 identifier as bytes
     */
    byte[] toModifiedEui64();

    /**
     * Generates an IPv6 link-local address from this MAC using Modified EUI-64.
     * Format: fe80::xxxx:xxff:fexx:xxxx
     *
     * @return the link-local IPv6 address
     */
    Ipv6Address toLinkLocalIpv6();

    /**
     * Generates an IPv6 address by combining a /64 prefix with this MAC's EUI-64.
     *
     * @param prefix the /64 network prefix
     * @return the full IPv6 address
     * @throws IllegalArgumentException if prefix is not /64
     */
    Ipv6Address toIpv6(Ipv6Network prefix);

    /**
     * Returns a new address with the U/L bit set (locally administered).
     */
    MacAddress toLocallyAdministered();

    /**
     * Returns a new address with the U/L bit cleared (universally administered).
     */
    MacAddress toUniversallyAdministered();
}
```

#### Create new file `MacAddresses.java`:

```java
package net.luis.ip;

import java.util.Optional;

/**
 * Factory and utility methods for MAC addresses.
 */
public final class MacAddresses {

    private MacAddresses() {}

    /**
     * Parses a MAC address from a string.
     * Accepts formats: colon-delimited, dash-delimited, Cisco dot notation, bare hex.
     *
     * @param address the address string
     * @return the parsed address
     * @throws IllegalArgumentException if the string is not a valid MAC address
     */
    public static MacAddress parse(String address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Attempts to parse a MAC address from a string.
     *
     * @param address the address string
     * @return the parsed address, or empty if invalid
     */
    public static Optional<MacAddress> tryParse(String address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates a MAC address from 6 bytes.
     *
     * @param bytes the address bytes (must be exactly 6 bytes)
     * @return the address
     * @throws IllegalArgumentException if bytes length is not 6
     */
    public static MacAddress of(byte[] bytes) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates a MAC address from a 48-bit value.
     *
     * @param value the address as an unsigned 48-bit value (stored in long)
     * @return the address
     */
    public static MacAddress of(long value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates a MAC address from 6 octets.
     *
     * @param o0 first octet (0-255)
     * @param o1 second octet
     * @param o2 third octet
     * @param o3 fourth octet
     * @param o4 fifth octet
     * @param o5 sixth octet
     * @return the address
     * @throws IllegalArgumentException if any octet is out of range
     */
    public static MacAddress of(int o0, int o1, int o2, int o3, int o4, int o5) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Checks if a string is a valid MAC address.
     *
     * @param address the string to check
     * @return true if valid
     */
    public static boolean isValid(String address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the broadcast MAC address (FF:FF:FF:FF:FF:FF).
     */
    public static MacAddress broadcast() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Extracts the embedded MAC address from an IPv6 address that uses Modified EUI-64.
     * This reverses the EUI-64 transformation:
     * 1. Extract the last 64 bits
     * 2. Verify FFFE is in the middle (bytes 3-4)
     * 3. Remove FFFE and flip the U/L bit
     *
     * @param address the IPv6 address
     * @return the extracted MAC address, or empty if not an EUI-64 address
     */
    public static Optional<MacAddress> fromEui64(Ipv6Address address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Generates a random locally-administered unicast MAC address.
     * Useful for testing and virtualization.
     *
     * @return a random MAC address
     */
    public static MacAddress random() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
```

#### Update `Ipv6Address.java`:

Add the following method:

```java
/**
 * Extracts the embedded MAC address if this address uses Modified EUI-64 format.
 * This checks for the FFFE marker in the interface identifier and reverses
 * the EUI-64 transformation.
 *
 * @return the embedded MAC address, or empty if not an EUI-64 address
 */
Optional<MacAddress> extractMacAddress();
```

**Files to create:**
- `MacAddress.java`
- `MacAddresses.java`

**Files to modify:**
- `Ipv6Address.java`

---

## Suggestions

### 1. Add `java.net.InetAddress` Conversion with Port Support
**Status:** Approved
**Priority:** High

**Task:** Add conversion methods to/from Java's built-in `InetAddress` and `InetSocketAddress` classes.

Add to `Ipv4Address.java`:

```java
/**
 * Converts this address to a {@link java.net.Inet4Address}.
 *
 * @return the equivalent Inet4Address
 */
java.net.Inet4Address toInetAddress();

/**
 * Creates an {@link java.net.InetSocketAddress} with the given port.
 *
 * @param port the port number (0-65535)
 * @return the socket address
 * @throws IllegalArgumentException if port is out of range
 */
java.net.InetSocketAddress toSocketAddress(int port);

/**
 * Creates an IPv4 address from an Inet4Address.
 *
 * @param address the Inet4Address
 * @return the equivalent Ipv4Address
 */
static Ipv4Address from(java.net.Inet4Address address);
```

Add to `Ipv6Address.java`:

```java
/**
 * Converts this address to a {@link java.net.Inet6Address}.
 *
 * @return the equivalent Inet6Address
 */
java.net.Inet6Address toInetAddress();

/**
 * Creates an {@link java.net.InetSocketAddress} with the given port.
 *
 * @param port the port number (0-65535)
 * @return the socket address
 * @throws IllegalArgumentException if port is out of range
 */
java.net.InetSocketAddress toSocketAddress(int port);

/**
 * Creates an IPv6 address from an Inet6Address.
 *
 * @param address the Inet6Address
 * @return the equivalent Ipv6Address
 */
static Ipv6Address from(java.net.Inet6Address address);
```

Add to `IpAddresses.java`:

```java
/**
 * Creates an IP address from a {@link java.net.InetAddress}.
 *
 * @param address the InetAddress
 * @return the equivalent IpAddress (either Ipv4Address or Ipv6Address)
 */
static IpAddress<?> from(java.net.InetAddress address);
```

**Files to modify:**
- `Ipv4Address.java`
- `Ipv6Address.java`
- `IpAddresses.java`

---

### 2. Add Serialization Support
**Status:** Rejected
**Reason:** User preference - serialization will not be included in the library.

---

### 3. Add Hostname Resolution
**Status:** Approved
**Priority:** Low

**Task:** Add hostname resolution (reverse DNS lookup) using Java's standard library. This can be implemented without external dependencies using `InetAddress.getCanonicalHostName()`.

Add to `IpAddress.java`:

```java
/**
 * Performs a reverse DNS lookup to get the hostname for this IP address.
 * <p>
 * <b>Warning:</b> This is a blocking network operation that can take
 * several seconds. For non-blocking usage, use {@link #resolveHostnameAsync()}.
 * Reverse DNS lookups are not cached by the JVM, so repeated calls will
 * hit the DNS server each time.
 *
 * @return the hostname, or empty if no PTR record exists or lookup fails
 */
Optional<String> resolveHostname();

/**
 * Performs a reverse DNS lookup asynchronously.
 * <p>
 * Uses a dedicated thread pool for DNS operations to avoid blocking
 * the calling thread.
 *
 * @return a future that completes with the hostname, or empty if lookup fails
 */
CompletableFuture<Optional<String>> resolveHostnameAsync();

/**
 * Performs a reverse DNS lookup with a timeout.
 * <p>
 * If the lookup does not complete within the specified timeout,
 * an empty Optional is returned.
 *
 * @param timeout maximum time to wait
 * @return the hostname, or empty if lookup fails or times out
 */
Optional<String> resolveHostname(java.time.Duration timeout);
```

**Implementation notes:**
- Use `InetAddress.getCanonicalHostName()` internally
- Compare result with IP string to detect failed lookups (method returns IP if PTR lookup fails)
- Use a dedicated `ExecutorService` with daemon threads for async operations
- Consider adding application-level caching as a future enhancement

**Imports to add:**
- `java.util.concurrent.CompletableFuture`
- `java.time.Duration`

**Files to modify:**
- `IpAddress.java`

---

### 4. Consider Records for Implementations
**Status:** Approved
**Priority:** Low (implementation detail)

**Task:** When implementing the interfaces, use Java records where appropriate for immutable value types.

Example implementation for `Ipv4Address`:
```java
record Ipv4AddressImpl(int value) implements Ipv4Address {
    // value stored as unsigned int using the full 32 bits
    // Use Integer.toUnsignedLong() for toInt() method
}
```

Example implementation for `Ipv6Address`:
```java
record Ipv6AddressImpl(long highBits, long lowBits, String zoneId) implements Ipv6Address {
    // Compact constructor for validation
    public Ipv6AddressImpl {
        // zoneId can be null
    }
}
```

**Note:** This is an implementation detail, not an API change. The public interfaces remain the same.

---

### 5. Add Prefix Validation in Networks
**Status:** Approved
**Priority:** Medium

**Task:** Add a method to check if a network address is in canonical form (no host bits set).

Add to `IpNetwork.java`:

```java
/**
 * Checks if this network is in canonical form.
 * A network is canonical if its network address has no host bits set
 * (i.e., applying the prefix mask results in the same address).
 * <p>
 * For example, 192.168.1.0/24 is canonical, but 192.168.1.100/24 is not
 * (the canonical form would be 192.168.1.0/24).
 *
 * @return true if the network address has no host bits set
 */
boolean isCanonical();

/**
 * Returns the canonical form of this network.
 * If this network is already canonical, returns this.
 * Otherwise, returns a new network with host bits cleared.
 *
 * @return the canonical network
 */
N toCanonical();
```

**Files to modify:**
- `IpNetwork.java`

---

## Minor Issues (Resolved)

### 1. Add Missing Multicast Scopes to `Ipv6MulticastScope`
**Status:** Approved
**Priority:** Low

**Task:** Add explicit enum values for all scope values 0x0-0xF instead of relying on UNKNOWN.

Update `Ipv6MulticastScope.java`:

```java
public enum Ipv6MulticastScope {

    RESERVED_0(0x0),
    INTERFACE_LOCAL(0x1),
    LINK_LOCAL(0x2),
    REALM_LOCAL(0x3),
    ADMIN_LOCAL(0x4),
    SITE_LOCAL(0x5),
    UNASSIGNED_6(0x6),      // Add
    UNASSIGNED_7(0x7),      // Add
    ORGANIZATION_LOCAL(0x8),
    UNASSIGNED_9(0x9),      // Add
    UNASSIGNED_A(0xA),      // Add
    UNASSIGNED_B(0xB),      // Add
    UNASSIGNED_C(0xC),      // Add
    UNASSIGNED_D(0xD),      // Add
    GLOBAL(0xE),
    RESERVED_F(0xF),
    UNKNOWN(-1);

    // ... rest unchanged
}
```

**Files to modify:**
- `Ipv6MulticastScope.java`

---

### 2. Change `TeredoInfo.TEREDO_PREFIX` to `Ipv6Network`
**Status:** Approved
**Priority:** Low

**Task:** Change the prefix constant from String to the actual network type.

Update `TeredoInfo.java`:

```java
// Change from:
public static final String TEREDO_PREFIX = "2001:0000::/32";

// To:
/**
 * The Teredo prefix network (2001:0000::/32).
 * Note: This is initialized lazily to avoid circular dependencies.
 */
public static Ipv6Network teredoPrefix() {
    return IpAddresses.parseIpv6Network("2001:0000::/32");
}
```

**Note:** Using a method instead of a constant to avoid circular initialization issues.

**Files to modify:**
- `TeredoInfo.java`

---

### 3. Clarify `Ipv4SubnetMask.isWildcard()` Semantics
**Status:** Approved
**Priority:** Low

**Task:** Either remove the confusing method or clarify its purpose with better documentation.

**Option A - Remove the method:**
Remove `isWildcard()` from `Ipv4SubnetMask.java` as the distinction should be clear from context (subnet mask vs wildcard mask are separate objects).

**Option B - Clarify documentation (recommended):**
```java
/**
 * Checks if this mask object represents a wildcard mask rather than a subnet mask.
 * <p>
 * Wildcard masks are the bitwise inverse of subnet masks and are used in
 * access control lists (ACLs). For example, 0.0.0.255 is the wildcard mask
 * corresponding to subnet mask 255.255.255.0.
 * <p>
 * This property is informational and depends on how the mask was created.
 * Use {@link #toWildcard()} to convert between representations.
 *
 * @return true if this mask was created as a wildcard mask
 */
boolean isWildcard();
```

**Files to modify:**
- `Ipv4SubnetMask.java`

---

### 4. Move `ParseOptionsImpl` to Proper Location
**Status:** Approved
**Priority:** Low

**Task:** Move the `ParseOptionsImpl` record to be a private nested class inside the `ParseOptions` interface, or move it to a separate package-private file.

**Option A - Nested class (recommended):**

```java
public interface ParseOptions {
    // ... interface methods ...

    class Builder {
        // ... builder methods ...

        public ParseOptions build() {
            return new Impl(
                allowLeadingZeros, allowOctalNotation, allowDecimalNotation,
                allowMixedCase, allowEmptySegments, allowMixedNotation,
                allowZoneId, allowPortSuffix, normalize
            );
        }
    }

    /**
     * Default implementation of ParseOptions.
     */
    record Impl(
        boolean allowLeadingZeros,
        boolean allowOctalNotation,
        boolean allowDecimalNotation,
        boolean allowMixedCase,
        boolean allowEmptySegments,
        boolean allowMixedNotation,
        boolean allowZoneId,
        boolean allowPortSuffix,
        boolean normalize
    ) implements ParseOptions {}
}
```

Do the same for `FormatOptionsImpl` in `FormatOptions.java`.

**Files to modify:**
- `ParseOptions.java`
- `FormatOptions.java`

---

## Research Findings

### MAC Address Support - Rationale

**Decision: Include MAC address support in the library.**

#### Why MAC Addresses Belong in This Library

1. **EUI-64 Integration Already Exists**: The `Ipv6Address` interface already has `isEui64()`, which detects addresses generated from MAC addresses. This method is incomplete without the ability to actually work with MAC addresses.

2. **Technical Relationship is Fundamental**:
   - **ARP (IPv4)**: Maps IPv4 addresses to MAC addresses on local networks
   - **NDP (IPv6)**: Replaces ARP for IPv6, uses MAC for neighbor discovery
   - **SLAAC**: IPv6 Stateless Address Autoconfiguration generates addresses from MAC
   - **EUI-64**: Algorithm that converts 48-bit MAC to 64-bit IPv6 interface identifier:
     1. Split MAC into two 24-bit halves
     2. Insert `FFFE` in the middle (creating 64 bits)
     3. Flip the 7th bit (U/L bit) to indicate global/local scope
   - Example: MAC `00:1A:2B:3C:4D:5E` becomes Interface ID `021A:2BFF:FE3C:4D5E`

3. **Common Use Cases**:
   - **IPv6 Address Generation**: Generate link-local (`fe80::`) or global addresses from MAC
   - **Network Device Discovery**: Correlate IP and MAC for DHCP, inventory, ACLs
   - **Wake-on-LAN**: Requires MAC for magic packet, IP for discovery
   - **Security/Forensics**: Track devices, detect spoofing, analyze network flows
   - **Extracting MAC from IPv6**: Reverse EUI-64 to get original MAC

4. **Precedent**: The most comprehensive Java IP library (seancfoley/IPAddress) includes MAC addresses and states they "allow most library functionality to be independent of address type or version, whether IPv4, IPv6 or MAC."

5. **Your Library Already Has Related Features**:
   - `Ipv6Address.isEui64()` - detects EUI-64 addresses
   - `Ipv6Address.toSolicitedNodeMulticast()` - used in NDP (which bridges MAC/IPv6)

#### Features to Implement

| Category | Features |
|----------|----------|
| **Essential** | Parsing (colon, dash, Cisco dot, bare hex), formatting, byte array conversion, EUI-64 conversion, link-local generation, type classification (unicast/multicast/universal/local) |
| **Advanced** | OUI extraction, MAC extraction from IPv6, random generation for testing |
| **Avoid** | Bundled OUI vendor database (adds size, becomes stale), ARP table access (OS-specific) |

---

### Hostname Resolution - Implementation Details

**Decision: Include hostname resolution using Java's standard library (no external dependencies).**

#### How It Works

Java's `InetAddress.getCanonicalHostName()` performs reverse DNS lookups:

```java
InetAddress inet = InetAddress.getByAddress(ipBytes);
String hostname = inet.getCanonicalHostName();
// Returns hostname, or IP string if lookup fails
```

**Failure Detection**: The method returns the IP address as a string when lookup fails (no exception thrown). Compare the result with the IP to detect failure.

#### Important Limitations

| Limitation | Impact | Mitigation |
|------------|--------|------------|
| **Blocking operation** | Can take 1-30+ seconds | Provide async variant with `CompletableFuture` |
| **No reverse lookup caching** | Repeated calls hit DNS server | Consider application-level caching (future enhancement) |
| **No timeout configuration** | Cannot limit wait time in JVM | Wrap with `CompletableFuture.orTimeout()` |
| **No failure exception** | Must compare result with IP | Check `hostname.equals(ipAddress.getHostAddress())` |
| **Platform-dependent** | Windows has 13+ second timeout for failures | Document behavior differences |

#### Recommended Implementation

```java
// Synchronous (blocking)
public Optional<String> resolveHostname() {
    try {
        InetAddress inet = InetAddress.getByAddress(toBytes());
        String hostname = inet.getCanonicalHostName();
        String ip = inet.getHostAddress();
        // getCanonicalHostName returns IP if lookup fails
        if (hostname.equals(ip)) {
            return Optional.empty();
        }
        return Optional.of(hostname);
    } catch (UnknownHostException e) {
        return Optional.empty();
    }
}

// Asynchronous (recommended for production use)
private static final ExecutorService DNS_EXECUTOR =
    Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "dns-resolver");
        t.setDaemon(true);
        return t;
    });

public CompletableFuture<Optional<String>> resolveHostnameAsync() {
    return CompletableFuture.supplyAsync(this::resolveHostname, DNS_EXECUTOR);
}

// With timeout
public Optional<String> resolveHostname(Duration timeout) {
    try {
        return resolveHostnameAsync()
            .get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    } catch (TimeoutException | InterruptedException | ExecutionException e) {
        return Optional.empty();
    }
}
```

#### Integration with Existing API

The library already has `toReverseDnsName()` which returns the PTR record format (e.g., `1.0.168.192.in-addr.arpa`). The hostname resolution complements this by actually querying DNS for the hostname.

#### No External Dependencies Required

- Uses only `java.net.InetAddress` from standard library
- Uses `java.util.concurrent.CompletableFuture` for async (Java 8+)
- Uses `java.time.Duration` for timeout (Java 8+)

---

## Summary

| Category | Count | Priority High | Priority Medium | Priority Low |
|----------|-------|---------------|-----------------|--------------|
| Improvements | 8 | 2 | 4 | 2 |
| Suggestions | 4 | 1 | 1 | 2 |
| Minor Issues | 4 | 0 | 0 | 4 |
| **Total** | **16** | **3** | **5** | **8** |

### High Priority Tasks
1. Add `ParseOptions` integration in factory methods
2. Change `distanceTo()` to return `BigInteger`
3. Add `java.net.InetAddress` conversion with port support

### Medium Priority Tasks
1. Add Zone ID support to `Ipv6Address`
2. Convert `IpAddresses` from interface to final class
3. Add well-known network constants
4. Add MAC address support
5. Add prefix validation in networks

### Low Priority Tasks
1. Add iteration safety documentation
2. Add builder pattern for addresses
3. Add hostname resolution
4. Consider records for implementations
5. Add missing multicast scopes
6. Change `TeredoInfo.TEREDO_PREFIX` to `Ipv6Network`
7. Clarify `Ipv4SubnetMask.isWildcard()` semantics
8. Move `ParseOptionsImpl` to proper location
