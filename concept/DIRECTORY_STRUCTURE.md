# IP Address Library - Directory Structure

## Package Naming Note

The existing library uses `net.luis.utils.*` as the root package. The specified root `net.luis.io.network.address.*` differs from this pattern.

**Options:**
1. `net.luis.io.network.address.*` - As specified (separate from existing utils)
2. `net.luis.utils.io.network.address.*` - Consistent with existing library structure

This document uses the specified `net.luis.io.network.address.*` structure.

---

## Design Approach

This library uses a **simplified type hierarchy**:

- **Base interfaces** are used only where polymorphism is needed (`IpAddress`, `IpNetwork`, `IpRange`, `IpSet`)
- **Concrete types** are implemented directly as records or final classes (no separate `*Impl` files)
- **Sealed types** restrict which classes can implement the base interfaces

```java
// Base interface - sealed to restrict implementations
public sealed interface IpAddress<T extends IpAddress<T>>
    permits Ipv4Address, Ipv6Address {
    // common methods
}

// Concrete type - record (no separate Impl class)
public record Ipv4Address(int value) implements IpAddress<Ipv4Address> {
    // all methods implemented directly here
}

public record Ipv6Address(long highBits, long lowBits, String zoneId)
    implements IpAddress<Ipv6Address> {
    // all methods implemented directly here
}
```

**Benefits:**
- Fewer files, less indirection
- Records provide immutability, equals/hashCode, toString automatically
- Sealed types ensure type safety
- Single source of truth for each type

---

## Directory Structure

```
src/main/java/net/luis/io/network/address/
├── IpAddress.java                    # Sealed base interface for IPv4/IPv6 polymorphism
├── IpAddresses.java                  # Factory and utility class (main entry point)
├── AddressType.java                  # Address type classification enum
│
├── ipv4/                             # IPv4-specific types
│   ├── Ipv4Address.java              # Record: record Ipv4Address(int value)
│   ├── Ipv4Network.java              # Record implementing IpNetwork
│   ├── Ipv4Range.java                # Record implementing IpRange
│   ├── Ipv4Set.java                  # Final class implementing IpSet
│   └── Ipv4SubnetMask.java           # Record for subnet masks
│
├── ipv6/                             # IPv6-specific types
│   ├── Ipv6Address.java              # Record: record Ipv6Address(long highBits, long lowBits, String zoneId)
│   ├── Ipv6Network.java              # Record implementing IpNetwork
│   ├── Ipv6Range.java                # Record implementing IpRange
│   ├── Ipv6Set.java                  # Final class implementing IpSet
│   ├── Ipv6MulticastScope.java       # Enum for multicast scopes
│   └── TeredoInfo.java               # Record for Teredo address info
│
├── mac/                              # MAC address types
│   ├── MacAddress.java               # Record: record MacAddress(long value)
│   └── MacAddresses.java             # Factory and utility class
│
├── common/                           # Shared base interfaces (for polymorphism only)
│   ├── IpNetwork.java                # Sealed interface permits Ipv4Network, Ipv6Network
│   ├── IpRange.java                  # Sealed interface permits Ipv4Range, Ipv6Range
│   └── IpSet.java                    # Sealed interface permits Ipv4Set, Ipv6Set
│
├── format/                           # Parsing and formatting options
│   ├── ParseOptions.java             # Record with builder
│   └── FormatOptions.java            # Record with builder
│
├── exception/                        # Exception types
│   └── IpParseException.java         # Parse exception with error types
│
└── resolver/                         # Hostname resolution (optional feature)
    └── HostnameResolver.java         # Async hostname resolution utilities
```

---

## Type Hierarchy

```
IpAddress<T> (sealed interface)
├── Ipv4Address (record)
└── Ipv6Address (record)

IpNetwork<A, N> (sealed interface)
├── Ipv4Network (record)
└── Ipv6Network (record)

IpRange<A, R, N> (sealed interface)
├── Ipv4Range (record)
└── Ipv6Range (record)

IpSet<A, R, N, S> (sealed interface)
├── Ipv4Set (final class)
└── Ipv6Set (final class)

MacAddress (record) - standalone, no base interface needed
```

---

## Test Directory Structure

```
src/test/java/net/luis/io/network/address/
├── IpAddressesTest.java              # Factory method tests
├── AddressTypeTest.java
│
├── ipv4/
│   ├── Ipv4AddressTest.java
│   ├── Ipv4NetworkTest.java
│   ├── Ipv4RangeTest.java
│   ├── Ipv4SetTest.java
│   └── Ipv4SubnetMaskTest.java
│
├── ipv6/
│   ├── Ipv6AddressTest.java
│   ├── Ipv6NetworkTest.java
│   ├── Ipv6RangeTest.java
│   ├── Ipv6SetTest.java
│   ├── Ipv6MulticastScopeTest.java
│   └── TeredoInfoTest.java
│
├── mac/
│   ├── MacAddressTest.java
│   └── MacAddressesTest.java
│
├── common/
│   ├── IpNetworkPolymorphismTest.java  # Tests for polymorphic behavior
│   ├── IpRangePolymorphismTest.java
│   └── IpSetPolymorphismTest.java
│
├── format/
│   ├── ParseOptionsTest.java
│   └── FormatOptionsTest.java
│
├── exception/
│   └── IpParseExceptionTest.java
│
├── resolver/
│   └── HostnameResolverTest.java
│
└── integration/
    ├── Ipv4Ipv6ConversionTest.java
    ├── MacEui64IntegrationTest.java
    └── InetAddressInteropTest.java
```

---

## Package Descriptions

| Package | Description |
|---------|-------------|
| `address` | Root package with main entry points (`IpAddresses`, `IpAddress`, `AddressType`) |
| `address.ipv4` | IPv4 concrete types: `Ipv4Address`, `Ipv4Network`, `Ipv4Range`, `Ipv4Set`, `Ipv4SubnetMask` |
| `address.ipv6` | IPv6 concrete types: `Ipv6Address`, `Ipv6Network`, `Ipv6Range`, `Ipv6Set`, `Ipv6MulticastScope`, `TeredoInfo` |
| `address.mac` | MAC address: `MacAddress` record and `MacAddresses` factory |
| `address.common` | Sealed base interfaces for polymorphism (`IpNetwork`, `IpRange`, `IpSet`) |
| `address.format` | Configuration records: `ParseOptions`, `FormatOptions` |
| `address.exception` | Exception types: `IpParseException` |
| `address.resolver` | Hostname resolution utilities |

---

## File to Package Mapping (from concept/)

| Concept File | Target Location | Implementation |
|--------------|-----------------|----------------|
| `IpAddress.java` | `address/IpAddress.java` | Sealed interface |
| `IpAddresses.java` | `address/IpAddresses.java` | Final utility class |
| `AddressType.java` | `address/AddressType.java` | Enum |
| `Ipv4Address.java` | `address/ipv4/Ipv4Address.java` | Record |
| `Ipv4Network.java` | `address/ipv4/Ipv4Network.java` | Record |
| `Ipv4Range.java` | `address/ipv4/Ipv4Range.java` | Record |
| `Ipv4Set.java` | `address/ipv4/Ipv4Set.java` | Final class |
| `Ipv4SubnetMask.java` | `address/ipv4/Ipv4SubnetMask.java` | Record |
| `Ipv6Address.java` | `address/ipv6/Ipv6Address.java` | Record |
| `Ipv6Network.java` | `address/ipv6/Ipv6Network.java` | Record |
| `Ipv6Range.java` | `address/ipv6/Ipv6Range.java` | Record |
| `Ipv6Set.java` | `address/ipv6/Ipv6Set.java` | Final class |
| `Ipv6MulticastScope.java` | `address/ipv6/Ipv6MulticastScope.java` | Enum |
| `TeredoInfo.java` | `address/ipv6/TeredoInfo.java` | Record |
| `IpNetwork.java` | `address/common/IpNetwork.java` | Sealed interface |
| `IpRange.java` | `address/common/IpRange.java` | Sealed interface |
| `IpSet.java` | `address/common/IpSet.java` | Sealed interface |
| `ParseOptions.java` | `address/format/ParseOptions.java` | Record with builder |
| `FormatOptions.java` | `address/format/FormatOptions.java` | Record with builder |
| `IpParseException.java` | `address/exception/IpParseException.java` | Class extends IllegalArgumentException |
| `MacAddress.java` (new) | `address/mac/MacAddress.java` | Record |
| `MacAddresses.java` (new) | `address/mac/MacAddresses.java` | Final utility class |

---

## Example Implementations

### Ipv4Address (Record)

```java
package net.luis.io.network.address.ipv4;

import net.luis.io.network.address.IpAddress;
import net.luis.io.network.address.AddressType;
import java.math.BigInteger;
import java.util.Optional;

public record Ipv4Address(int value) implements IpAddress<Ipv4Address> {

    public static final int BIT_LENGTH = 32;
    public static final int OCTET_COUNT = 4;

    // Compact constructor for validation
    public Ipv4Address {
        // value is stored as signed int, but represents unsigned 32-bit
    }

    @Override
    public int version() {
        return 4;
    }

    @Override
    public int bitLength() {
        return BIT_LENGTH;
    }

    public long toUnsignedLong() {
        return Integer.toUnsignedLong(value);
    }

    @Override
    public byte[] toBytes() {
        return new byte[] {
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value
        };
    }

    public int getOctet(int index) {
        return switch (index) {
            case 0 -> (value >>> 24) & 0xFF;
            case 1 -> (value >>> 16) & 0xFF;
            case 2 -> (value >>> 8) & 0xFF;
            case 3 -> value & 0xFF;
            default -> throw new IndexOutOfBoundsException(index);
        };
    }

    @Override
    public boolean isLoopback() {
        return (value >>> 24) == 127;  // 127.0.0.0/8
    }

    @Override
    public boolean isPrivate() {
        int first = (value >>> 24) & 0xFF;
        int second = (value >>> 16) & 0xFF;
        return first == 10 ||                           // 10.0.0.0/8
               (first == 172 && (second >= 16 && second <= 31)) ||  // 172.16.0.0/12
               (first == 192 && second == 168);         // 192.168.0.0/16
    }

    // ... other methods

    @Override
    public String toString() {
        return getOctet(0) + "." + getOctet(1) + "." + getOctet(2) + "." + getOctet(3);
    }

    @Override
    public int compareTo(Ipv4Address other) {
        return Integer.compareUnsigned(this.value, other.value);
    }
}
```

### Ipv6Address (Record)

```java
package net.luis.io.network.address.ipv6;

import net.luis.io.network.address.IpAddress;

public record Ipv6Address(long highBits, long lowBits, String zoneId)
    implements IpAddress<Ipv6Address> {

    public static final int BIT_LENGTH = 128;
    public static final int HEXTET_COUNT = 8;

    // Constructor without zone ID
    public Ipv6Address(long highBits, long lowBits) {
        this(highBits, lowBits, null);
    }

    @Override
    public int version() {
        return 6;
    }

    @Override
    public int bitLength() {
        return BIT_LENGTH;
    }

    public Optional<String> zoneId() {
        return Optional.ofNullable(zoneId);
    }

    public Ipv6Address withZoneId(String zoneId) {
        return new Ipv6Address(highBits, lowBits, zoneId);
    }

    public Ipv6Address withoutZoneId() {
        return zoneId == null ? this : new Ipv6Address(highBits, lowBits, null);
    }

    @Override
    public boolean isLoopback() {
        return highBits == 0 && lowBits == 1;  // ::1
    }

    @Override
    public boolean isLinkLocal() {
        return (highBits >>> 54) == 0x3FA;  // fe80::/10
    }

    // ... other methods
}
```

### MacAddress (Record)

```java
package net.luis.io.network.address.mac;

public record MacAddress(long value) implements Comparable<MacAddress> {

    public static final int BIT_LENGTH = 48;
    public static final int OCTET_COUNT = 6;

    // Compact constructor - mask to 48 bits
    public MacAddress {
        value = value & 0xFFFF_FFFF_FFFFL;
    }

    public byte[] toBytes() {
        return new byte[] {
            (byte) (value >>> 40),
            (byte) (value >>> 32),
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value
        };
    }

    public int getOctet(int index) {
        if (index < 0 || index >= OCTET_COUNT) {
            throw new IndexOutOfBoundsException(index);
        }
        return (int) ((value >>> ((5 - index) * 8)) & 0xFF);
    }

    public boolean isUnicast() {
        return (value & (1L << 40)) == 0;  // I/G bit = 0
    }

    public boolean isMulticast() {
        return !isUnicast();
    }

    public boolean isBroadcast() {
        return value == 0xFFFF_FFFF_FFFFL;
    }

    @Override
    public String toString() {
        return toColonString();
    }

    public String toColonString() {
        return String.format("%02x:%02x:%02x:%02x:%02x:%02x",
            getOctet(0), getOctet(1), getOctet(2),
            getOctet(3), getOctet(4), getOctet(5));
    }

    @Override
    public int compareTo(MacAddress other) {
        return Long.compareUnsigned(this.value, other.value);
    }
}
```

### IpAddress (Sealed Interface)

```java
package net.luis.io.network.address;

import net.luis.io.network.address.ipv4.Ipv4Address;
import net.luis.io.network.address.ipv6.Ipv6Address;
import java.math.BigInteger;
import java.util.Optional;

public sealed interface IpAddress<T extends IpAddress<T>>
    extends Comparable<T>
    permits Ipv4Address, Ipv6Address {

    int version();
    int bitLength();
    byte[] toBytes();

    boolean getBit(int index);
    T withBit(int index, boolean value);

    Optional<T> next();
    Optional<T> previous();
    BigInteger distanceTo(T other);

    boolean isUnspecified();
    boolean isLoopback();
    boolean isMulticast();
    boolean isLinkLocal();
    boolean isPrivate();
    boolean isDocumentation();
    boolean isGlobalUnicast();

    AddressType getAddressType();
    String toReverseDnsName();

    T minValue();
    T maxValue();
}
```

---

## Import Examples

```java
// Main entry points
import net.luis.io.network.address.IpAddresses;
import net.luis.io.network.address.IpAddress;
import net.luis.io.network.address.AddressType;

// IPv4 types (records)
import net.luis.io.network.address.ipv4.Ipv4Address;
import net.luis.io.network.address.ipv4.Ipv4Network;
import net.luis.io.network.address.ipv4.Ipv4Range;
import net.luis.io.network.address.ipv4.Ipv4Set;
import net.luis.io.network.address.ipv4.Ipv4SubnetMask;

// IPv6 types (records)
import net.luis.io.network.address.ipv6.Ipv6Address;
import net.luis.io.network.address.ipv6.Ipv6Network;
import net.luis.io.network.address.ipv6.Ipv6MulticastScope;
import net.luis.io.network.address.ipv6.TeredoInfo;

// MAC address (record)
import net.luis.io.network.address.mac.MacAddress;
import net.luis.io.network.address.mac.MacAddresses;

// Base interfaces (for polymorphism)
import net.luis.io.network.address.common.IpNetwork;
import net.luis.io.network.address.common.IpRange;
import net.luis.io.network.address.common.IpSet;

// Formatting and parsing (records)
import net.luis.io.network.address.format.ParseOptions;
import net.luis.io.network.address.format.FormatOptions;

// Exceptions
import net.luis.io.network.address.exception.IpParseException;

// Hostname resolution
import net.luis.io.network.address.resolver.HostnameResolver;
```

---

## Usage Examples

```java
// Parse and work with IPv4
Ipv4Address ip = IpAddresses.parseIpv4("192.168.1.1");
Ipv4Network network = IpAddresses.parseIpv4Network("192.168.1.0/24");
boolean contains = network.contains(ip);

// Access record components directly
int rawValue = ip.value();  // Record component accessor
long unsigned = ip.toUnsignedLong();

// Parse and work with IPv6
Ipv6Address ipv6 = IpAddresses.parseIpv6("2001:db8::1");
Ipv6Address linkLocal = IpAddresses.parseIpv6("fe80::1%eth0");
String zone = linkLocal.zoneId().orElse("none");

// Access record components
long high = ipv6.highBits();
long low = ipv6.lowBits();

// MAC to IPv6 conversion
MacAddress mac = MacAddresses.parse("00:1A:2B:3C:4D:5E");
Ipv6Address generated = mac.toLinkLocalIpv6();

// Polymorphism via sealed interface
IpAddress<?> address = IpAddresses.parse("192.168.1.1");
if (address instanceof Ipv4Address ipv4) {
    System.out.println("IPv4: " + ipv4.value());
} else if (address instanceof Ipv6Address ipv6) {
    System.out.println("IPv6: " + ipv6.highBits() + ":" + ipv6.lowBits());
}

// Convert to java.net types
InetAddress inet = ip.toInetAddress();
InetSocketAddress socket = ip.toSocketAddress(8080);

// Set operations for firewall rules
Ipv4Set allowed = Ipv4Set.of(
    IpAddresses.parseIpv4Network("10.0.0.0/8"),
    IpAddresses.parseIpv4Network("192.168.0.0/16")
);
Ipv4Set blocked = Ipv4Set.of(
    IpAddresses.parseIpv4Network("10.0.0.0/24")
);
Ipv4Set effective = allowed.difference(blocked);
```

---

## Module Info Update

If using Java modules, add to `module-info.java`:

```java
module net.luis.utils {
    // ... existing exports ...

    // IP Address system
    exports net.luis.io.network.address;
    exports net.luis.io.network.address.ipv4;
    exports net.luis.io.network.address.ipv6;
    exports net.luis.io.network.address.mac;
    exports net.luis.io.network.address.common;
    exports net.luis.io.network.address.format;
    exports net.luis.io.network.address.exception;
    exports net.luis.io.network.address.resolver;
}
```

---

## Alternative: Flat Structure

If you prefer fewer packages, here's a flatter alternative:

```
src/main/java/net/luis/io/network/address/
├── IpAddress.java                    # Sealed interface
├── IpAddresses.java                  # Factory
├── AddressType.java                  # Enum
├── Ipv4Address.java                  # Record
├── Ipv4Network.java                  # Record
├── Ipv4Range.java                    # Record
├── Ipv4Set.java                      # Final class
├── Ipv4SubnetMask.java               # Record
├── Ipv6Address.java                  # Record
├── Ipv6Network.java                  # Record
├── Ipv6Range.java                    # Record
├── Ipv6Set.java                      # Final class
├── Ipv6MulticastScope.java           # Enum
├── TeredoInfo.java                   # Record
├── IpNetwork.java                    # Sealed interface
├── IpRange.java                      # Sealed interface
├── IpSet.java                        # Sealed interface
├── MacAddress.java                   # Record
├── MacAddresses.java                 # Factory
├── ParseOptions.java                 # Record
├── FormatOptions.java                # Record
├── IpParseException.java             # Exception class
└── HostnameResolver.java             # Utility class
```

**Pros:** Simpler imports, everything in one place, 22 files total
**Cons:** Large package, less organization

---

## Recommendation

Use the **hierarchical structure** because:
1. Matches the organization style of the existing library (`io.data.json`, `io.data.yaml`, etc.)
2. Clear separation between IPv4, IPv6, and MAC concerns
3. Records and sealed types eliminate the need for separate implementation files
4. Each subpackage has a focused, cohesive set of types
5. Easier to navigate as the library grows
