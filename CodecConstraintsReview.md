### 1. CodecLengthConstraint
**Applies to:** String, Arrays

**Methods:**
- `minLength(int)` - Minimum length validation
- `maxLength(int)` - Maximum length validation
- `exactLength(int)` - Exact length validation
- `lengthBetween(int min, int max)` - Range validation (inclusive)
- `lengthIn(Set<Integer>)` - Length must be one of specified values
- `empty()` - Must be empty (length = 0)
- `notEmpty()` - Must not be empty (length >= 1)

---

### 2. CodecSizeConstraint
**Applies to:** Collections (List, Set), Maps

**Methods:**
- `minSize(int)` - Minimum number of elements
- `maxSize(int)` - Maximum number of elements
- `exactSize(int)` - Exact number of elements
- `sizeBetween(int min, int max)` - Range validation (inclusive)
- `sizeIn(Set<Integer>)` - Size must be one of specified values
- `empty()` - Must be empty (size = 0)
- `notEmpty()` - Must not be empty (size >= 1)
- `singleton()` - Exactly one element (size = 1)
- `pair()` - Exactly two elements (size = 2)

---

### 3. CodecTemporalConstraint
**Applies to:** LocalDate, LocalDateTime, LocalTime, Instant, ZonedDateTime, OffsetDateTime, OffsetTime, Year, Month, DayOfWeek

**Comparison Constraints:**
- `after(T)` - Must be after specified time (exclusive)
- `afterOrEqual(T)` - Must be after or equal to specified time
- `before(T)` - Must be before specified time (exclusive)
- `beforeOrEqual(T)` - Must be before or equal to specified time
- `between(T, T)` - Must be within time range (inclusive)
- `betweenExclusive(T, T)` - Must be within time range (exclusive)
- `equalTo(T)` - Must be equal to specified time
- `notEqualTo(T)` - Must not be equal to specified time

**Relative Time Constraints:**
- `past()` - Must be in the past (before now)
- `pastOrPresent()` - Must be in the past or now
- `future()` - Must be in the future (after now)
- `futureOrPresent()` - Must be in the future or now
- `today()` - Same day as today [NEW]
- `thisMonth()` - Same month as this month [NEW]
- `thisYear()` - Same year as this year [NEW]
- `withinLast(Duration/Period)` - Within last X time (e.g., within last 7 days, within last 2 hours)
- `withinNext(Duration/Period)` - Within next X time (e.g., within next 30 days, within next 1 hour)

**Temporal Component Matching:**
- `sameDay(T)` - Same calendar day as specified temporal
- `sameMonth(T)` - Same month as specified temporal
- `sameYear(T)` - Same year as specified temporal
- `day(DayOfWeek)` - Must be specific day of week (MONDAY, TUESDAY, etc.)
- `dayIn(Set<DayOfWeek>)` - Must be one of specified days of week
- `month(Month)` - Must be specific month (JANUARY, FEBRUARY, etc.)
- `monthIn(Set<Month>)` - Must be one of specified months

**Calendar Constraints:**
- `weekday()` - Alias for businessDay()
- `weekend()` - Saturday or Sunday
- `dayOfMonth(int)` - Specific day of month (1-31, validated)
- `dayOfMonthIn(Set<Integer>)` - One of specified days of month
- `quarter(int)` - Specific quarter (1-4, validated)
- `quarterIn(Set<Integer>)` - One of specified quarters

---

### 4. CodecNumericConstraint
**Applies to:** Byte, Short, Integer, Long, Float, Double, BigInteger, BigDecimal

**Range Constraints:**
- `greaterThan(T)` - Greater than value (exclusive >)
- `greaterThanOrEqual(T)` - Greater than or equal to value (inclusive >=)
- `lessThan(T)` - Less than value (exclusive <)
- `lessThanOrEqual(T)` - Less than or equal to value (inclusive <=)
- `between(T, T)` - Within range (inclusive, both bounds)
- `betweenExclusive(T, T)` - Within range (exclusive, both bounds)
- `equalTo(T)` - Equal to value
- `notEqualTo(T)` - Not equal to value

**Sign Constraints:**
- `positive()` - Must be > 0
- `negative()` - Must be < 0
- `nonNegative()` - Must be >= 0
- `nonPositive()` - Must be <= 0
- `zero()` - Must be == 0
- `nonZero()` - Must be != 0

**Integer-Specific (for Byte, Short, Integer, Long, BigInteger):**
- `even()` - Must be even (divisible by 2)
- `odd()` - Must be odd (not divisible by 2)
- `divisibleBy(long)` - Divisible by specified number
- `notDivisibleBy(long)` - Not divisible by specified number
- `powerOf(int base)` - Power of specified base (base must be > 1, validated)

**Decimal-Specific (for Float, Double, BigDecimal):**
- `precision(int)` - Total number of significant digits (BigDecimal)
- `minPrecision(int)` - Minimum number of significant digits (BigDecimal)
- `maxPrecision(int)` - Maximum number of significant digits (BigDecimal)
- `scale(int)` - Exact number of decimal places
- `minScale(int)` - Minimum number of decimal places
- `maxScale(int)` - Maximum number of decimal places
- `scaleBetween(int min, int max)` - Scale within range
- `finite()` - Not infinity (Float/Double)
- `notNaN()` - Not NaN (Float/Double)
- `integral()` - No fractional part (e.g., 5 or 5.0 valid, 5.1 invalid)

**Common Value Constraints:**
- `percentage()` - Between 0 and 100 (inclusive)
- `normalized()` - Between 0.0 and 1.0 (inclusive)

**Number Format Constraints:**
- `binaryFormat()` - Valid binary number (only 0s and 1s)
- `octalFormat()` - Valid octal number (digits 0-7)
- `decimalFormat()` - Valid decimal number (0-9)
- `hexFormat()` - Valid hexadecimal number (0-9, A-F, a-f)
- `inBase(int)` - Valid representation in specified base (2-36, validated)

---

### 5. CodecPatternConstraint
**Applies to:** String

**Pattern Matching:**
- `matches(Pattern)` - Match compiled pattern
- `matchesRegex(String)` - Match regex string (compiled internally)
- `notMatches(Pattern)` - Must not match compiled pattern
- `notMatchesRegex(String)` - Must not match regex string

**Common Patterns:**
- `numeric()` - Only digits
- `alphabetic()` - Letters only
- `alphanumeric()` - Letters and digits only
- `hexadecimal()` - Valid hexadecimal string (0-9, A-F, a-f)
- `base64()` - Valid base64 string
- `email()` - Basic email pattern
- `uuid()` - UUID format pattern
- `ipv4Address()` - IPv4 address format
- `ipv6Address()` - IPv6 address format

**Number Format Constraints:**
- `binaryFormat()` - Valid binary string representation (for String to Number parsing)
- `octalFormat()` - Valid octal string representation
- `decimalFormat()` - Valid decimal string representation
- `hexFormat()` - Valid hexadecimal string representation
- `inBase(int)` - Valid representation in specified base (2-36, validated)

**String Content:**
- `startsWith(String)` - Starts with prefix
- `notStartsWith(String)` - Does not start with prefix
- `startsWithAny(String...)` - Starts with any of the prefixes
- `endsWith(String)` - Ends with suffix
- `notEndsWith(String)` - Does not end with suffix
- `endsWithAny(String...)` - Ends with any of the suffixes
- `contains(String)` - Contains substring
- `notContains(String)` - Does not contain substring
- `containsAny(String...)` - Contains any of the substrings
- `containsAll(String...)` - Contains all substrings
- `containsNone(String...)` - Contains none of the substrings

**Case Constraints:**
- `isCase(CaseType)` - Specific case type (UPPER_CASE, LOWER_CASE, TITLE_CASE, CAMEL_CASE, PASCAL_CASE, SNAKE_CASE, KEBAB_CASE) (chainable)
- `IsNotCase(CaseType)` - Not specific case type (chainable)

**Character Constraints:**
- `noWhitespace()` - No whitespace characters
- `trimmed()` - No leading/trailing whitespace
- `blank()` - Whitespace-only string (or empty)
- `notBlank()` - Not whitespace-only (contains non-whitespace)
- `singleLine()` - No line breaks
- `ascii()` - Only ASCII characters (0-127)
- `latin1()` - Only Latin-1 characters (0-255)

---

### 6. CodecDurationConstraint
**Applies to:** Duration, Period

**Range Constraints:**
- `greaterThan(T)` - Greater than specified duration (exclusive)
- `greaterThanOrEqual(T)` - Greater than or equal to specified duration (inclusive) 
- `lessThan(T)` - Less than specified duration (exclusive)
- `lessThanOrEqual(T)` - Less than or equal to specified duration (inclusive)
- `between(T, T)` - Within duration range (inclusive)
- `betweenExclusive(T, T)` - Within duration range (exclusive)
- `exactDuration(T)` - Exact duration match

**Sign Constraints:**
- `positive()` - Positive duration (> 0)
- `negative()` - Negative duration (< 0)
- `nonNegative()` - Non-negative duration (>= 0)
- `nonPositive()` - Non-positive duration (<= 0)
- `zero()` - Zero duration
- `nonZero()` - Not zero duration

**Unit-Based Constraints:**
- `unitMax(ChronoUnit unit, long value)` - Maximum value in specified unit
  - Duration: DAYS, HOURS, MINUTES, SECONDS, MILLIS, NANOS, etc.
  - Period: YEARS, MONTHS, DAYS
  - (chainable)
- `unitMin(ChronoUnit unit, long value)` - Minimum value in specified unit
  - Same units as above
  - (chainable)
- `unitExact(ChronoUnit unit, long value)` - Exact value in specified unit
  - Same units as above
  - (chainable)
- `unitBetween(ChronoUnit unit, long min, long max)` - Value range in specified unit
  - Same units as above
  - (chainable)

**Common Duration Helpers:**
- `withinDay()` - Duration <= 24 hours
- `withinWeek()` - Duration <= 7 days
- `normalized()` - Duration/Period in normalized form

**NOTE:** Duration (time-based) and Period (date-based) are fundamentally different. Consider splitting into separate constraints in future.

---

### 7. CodecMapConstraint
**Applies to:** Map

**Key Constraints:**
- `requireKey(K)` - Must contain this key
- `requiredKeys(K...)` - Must contain these keys (varargs)
- `requiredKeys(Set<K>)` - Must contain these keys
- `forbidKey(K)` - Must not contain this key
- `forbiddenKeys(K...)` - Must not contain these keys (varargs)
- `forbiddenKeys(Set<K>)` - Must not contain these keys
- `allowOnlyKeys(K...)` - Only these keys allowed (varargs)
- `allowOnlyKeys(Set<K>)` - Only these keys allowed

**Key Pattern Constraints (for String keys):**
- `keysMatch(Pattern)` - All keys match pattern

---

### 8. CodecNetworkConstraint
**Applies to:** InetAddress, InetSocketAddress

**IP Constraints (InetAddress):**
- `ipv4()` - Only IPv4 addresses
- `ipv6()` - Only IPv6 addresses
- `ipVersion(int)` - Specific IP version (4 or 6)
- `publicIP()` - Not private/loopback/link-local
- `privateIP()` - Private IP range
- `notLoopback()` - Not loopback address
- `notLinkLocal()` - Not link-local address
- `multicast()` - Multicast address
- `global()` - Global unicast addresses
- `inSubnet(String)` - Within specified subnet (CIDR notation)
- `inAnySubnet(Set<String>)` - Within any of specified subnets (CIDR notation)
- `notInSubnet(String)` - Not within specified subnet
- `ipInRange(InetAddress, InetAddress)` - IP within range

**Hostname Constraints (InetSocketAddress):**
- `hasHostname()` - Has hostname (not just IP)
- `hostname(String)` - Specific hostname
- `hostnameMatches(Pattern)` - Hostname pattern

**Port Constraints (InetSocketAddress):**
- `port(int)` - Specific port number
- `notPort(int)` - Not specific port number
- `portIn(Set<Integer>)` - Port in specified set
- `portInRange(int, int)` - Port within range
- `wellKnownPort()` - 0-1023
- `registeredPort()` - 1024-49151
- `dynamicPort()` - 49152-65535

**NOTE:** Reachability/DNS constraints (reachable(), resolvable()) intentionally omitted as they require network I/O and can be slow/unreliable.

---

### 9. CodecResourceIdentifiersConstraint
**Applies to:** URI
**Note:** Remove URL Codec

**Scheme Constraints:**
- `scheme(String)` - Specific scheme required (e.g., "http", "https", "ftp")
- `notScheme(String)` - Exclude specific scheme
- `schemeIn(Set<String>)` - Scheme must be in set
- `schemeNotIn(Set<String>)` - Scheme must not be in set
- `schemeMatches(Pattern)` - Scheme matches regex pattern
- `secure()` - Uses secure scheme (https, ftps, wss, etc.)

**Component Constraints:**
- `has(URIComponent)` - Component must be present (e.g., HOST, PORT, PATH, QUERY, FRAGMENT, USER_INFO) (chainable)
- `lacks(URIComponent)` - Component must be absent (e.g., HOST, PORT, PATH, QUERY, FRAGMENT, USER_INFO) (chainable)
- `equals(URIComponent, String)` - Component equals specific value
  - Supported components: HOST, PATH, FRAGMENT, USER_INFO
- `anyOf(URIComponent, Set<String>)` - Component is in set of values
  - Supported components: HOST, PATH, FRAGMENT, USER_INFO
- `noneOf(URIComponent, Set<String>)` - Component is not in set of values
  - Supported components: HOST, PATH, FRAGMENT, USER_INFO
- `startsWith(URIComponent, String)` - Component starts with prefix
  - Supported components: HOST, PATH, FRAGMENT, USER_INFO
- `endsWith(URIComponent, String)` - Component ends with suffix
  - Supported components: HOST, PATH, FRAGMENT, USER_INFO
- `matches(URIComponent, Pattern)` - Component matches regex pattern
  - Supported components: HOST, PATH, FRAGMENT, USER_INFO
- `contains(URIComponent, String)` - Component contains substring
  - Supported components: HOST, PATH, FRAGMENT, USER_INFO

**Host Specific Constraints:**
- `ipAddress()` - Host is IP address (not domain name)
- `domainName()` - Host is domain name (not IP address)
- `localhost()` - Must be localhost
- `rootDomain()` - Host is root domain (e.g., "example.com" but not "sub.example.com")
- `subdomainOf(String)` - Host is subdomain of given domain (e.g., subdomainOf("example.com") matches "sub.example.com", "api.example.com")

**Port Constraints:**
- `port(int)` - Specific port number
- `portIn(Set<Integer>)` - Port in set
- `portInRange(int, int)` - Port within range
- `wellKnownPort()` - Port 0-1023
- `registeredPort()` - Port 1024-49151
- `dynamicPort()` - Port 49152-65535

**Path Constraints:** [NEW section]
- `path(String)` - Specific path
- `pathMatches(Pattern)` - Path matches regex pattern
- `pathNotMatches(Pattern)` - Path does not match regex pattern
- `pathStartsWith(String)` - Path starts with prefix
- `pathNotStartsWith(String)` - Path does not start with prefix
- `pathEndsWith(String)` - Path ends with suffix
- `pathNotEndsWith(String)` - Path does not end with suffix
- `containsPathSegment(String)` - Contains specific path segment
- `notContainsPathSegment(String)` - Does not contain specific path segment
- `pathSegments(int)` - Specific number of path segments
- `minPathSegments(int)` - Minimum number of path segments
- `maxPathSegments(int)` - Maximum number of path segments
- `pathSegmentsInRange(int, int)` - Number of path segments within range
- `pathSegmentAt(int, String)` - Specific segment at index
- `pathSegmentAtMatches(int, Pattern)` - Segment at index matches pattern

**Query Constraints:**
- `hasQueryParameter(String)` - Has query parameter (any value) (chainable)
- `hasAnyQueryParameter(Set<String>)` - Has any query parameter from set
- `queryParameter(String key, String value)` - Specific key-value pair
- `queryParameterIn(String key, Set<String>)` - Parameter value in set
- `queryParameterMatches(String key, Pattern)` - Parameter value pattern

**URI Type Constraints:**
- `absolute()` - Absolute URI (has scheme)
- `relative()` - Relative URI (no scheme)
- `opaque()` - Opaque URI (no authority component)
- `hierarchical()` - Hierarchical URI (has authority)

---

### 10. CodecPathConstraint
**Applies to:** Path
**Note:** Remove File codec

**Path Type Constraints:**
- `absolute()` - Must be absolute path (e.g., starts with "/" or "C:\\")
- `relative()` - Must be relative path (e.g., does not start with "/" or "C:\\")
- `normalized()` - No "." or ".." components
- `canonical()` - In canonical form (resolved, normalized, absolute)

**Structure Constraints:**
- `depth(int)` - Specific depth level (number of path components)
- `depthBetween(int, int)` - Depth within range
- `maxDepth(int)` - Maximum depth level
- `minDepth(int)` - Minimum depth level
- `root(String)` - Specific root path (e.g., "/", "C:\\", "/dev/")
- `equals(PathComponent, String)` - Specific path component equals value
  - PathComponent: ROOT, PARENT_PATH, FULL_PATH, FILENAME, EXTENSION
- `anyOf(PathComponent, Set<String>)` - Any of the specified values for a path component
- `noneOf(PathComponent, Set<String>)` - None of the specified values for a path component
- `startsWith(PathComponent, String)` - Path component starts with prefix
- `endsWith(PathComponent, String)` - Path component ends with suffix
- `matches(PathComponent, Pattern)` - Regex match for a path component
- `contains(PathComponent, String)` - Path component contains substring

**Filename Constraints:**
- `filename(String)` - Specific filename
- `filenameMatches(Pattern)` - Filename pattern
- `filenameStartsWith(String)` - Filename prefix
- `filenameEndsWith(String)` - Filename suffix (excluding extension)

**Extension Constraints:**
- `extension(String)` - Specific extension (e.g., "txt", "java")
- `extensionIn(Set<String>)` - Extension in set
- `extensionMatches(Pattern)` - Extension pattern
- `hasExtension()` - Has any extension
- `noExtension()` - No file extension

**Path Matching:** [NEW section]
- `glob(String)` - Path matches glob pattern (e.g., "*.txt", "**/test/*.java")
- `pathStartsWith(Path)` - Path starts with another path
- `pathEndsWith(Path)` - Path ends with another path
- `ancestorOf(Path)` - This path is ancestor of given path (e.g "/a/b" is ancestor of "/a/b/c/d")
- `descendantOf(Path)` - This path is descendant of given path (e.g "/a/b/c/d" is descendant of "/a/b")

**Platform Constraints:**
- `validFor(Platform)` - Valid for platform (Platform: CURRENT, UNIX, LINUX, WINDOWS, MAC)
- `portable()` - Valid for all platforms (no platform-specific chars)
- `separators(Platform)` - Uses specific path separators (e.g., Platform.WINDOWS uses "\\", Platform.UNIX uses "/")

**Special Path Constraints:**
- `hidden()` - Path represents hidden file (starts with "." on Unix, hidden attribute on Windows)
- `temporary()` - In system temp directory

**Length Constraints:**
- Note: CodecLengthConstraint methods (minLength, maxLength, etc.) also apply to Path for total path length validation

**NOTE:** Filesystem I/O constraints (exists(), readable(), writable(), directory(), regularFile()) intentionally omitted as they require filesystem access.

---

### 11. CodecCharConstraint
**Applies to:** Character

**Character Type:**
- `charType(CharacterType)` - Specific character type
  - CharacterType: LETTER, DIGIT, ALPHANUMERIC, PUNCTUATION (e.g., '.', ','), SYMBOL (e.g., '@', '#'), WHITESPACE, CONTROL (e.g., '\n', '\t')
- `notCharType(CharacterType)` - Not specific character type

**Character Type Helpers:**
- `letter()` - Is a letter
- `digit()` - Is a digit
- `alphanumeric()` - Is alphanumeric
- `whitespace()` - Is whitespace
- `punctuation()` - Is punctuation
- `symbol()` - Is a symbol
- `control()` - Is a control character

**Case:**
- `isCase(CaseType)` - Specific case
  - Supported cases: UPPER_CASE, LOWER_CASE
- `upperCase()` - Is uppercase letter
- `lowerCase()` - Is lowercase letter

**Range:**
- `inRange(char, char)` - Within character range (inclusive)
- `between(char, char)` - Alias for inRange
- `ascii()` - ASCII character (0-127)
- `extendedAscii()` - Extended ASCII (0-255)
- `nonAscii()` - Non-ASCII Unicode character (> 127)

**Specific Characters:**
- `oneOf(char...)` - One of specified characters (varargs)
- `oneOf(Set<Character>)` - One of specified characters
- `notOneOf(char...)` - Not one of specified characters (varargs)
- `notOneOf(Set<Character>)` - Not one of specified characters

**Special Character Helpers:**
- `newline()` - Is newline character ('\n')
- `tab()` - Is tab character ('\t')
- `space()` - Is space character (' ')

**Unicode Categories:**
- `unicodeCategory(Character.UnicodeBlock)` - Matches specific Unicode block
- `unicodeCategoryIn(Set<Character.UnicodeBlock>)` - Unicode block in set

---

### 12. CodecUUIDConstraint
**Applies to:** UUID

**Version Constraints:**
- `version(int)` - Specific UUID version (1-8, validated)
- `versionIn(Set<Integer>)` - Version in set
- `version1()` - Time-based UUID
- `version4()` - Random UUID
- `version5()` - Name-based (SHA-1) UUID
- `version7()` - Unix Epoch time-based UUID

**Variant Constraints:**
- `variant(UUIDVariant)` - Specific UUID variant
  - UUIDVariant: NCS, RFC_4122, MICROSOFT, RESERVED

**Special UUID Constraints:**
- `nil()` - All zeros UUID (nil UUID)
- `notNil()` - Not all zeros
- `max()` - All ones UUID (ffffffff-ffff-ffff-ffff-ffffffffffff)

---

### 13. CodecLocaleConstraint
**Applies to:** Locale

**Language Constraints:**
- `hasLanguage()` - Has language component
- `language(String)` - Specific language code (ISO 639)
- `languageIn(Set<String>)` - Language in set
- `notLanguage(String)` - Not specific language
- `languageNotIn(Set<String>)` - Language not in set

**Country Constraints:**
- `hasCountry()` - Has country component
- `country(String)` - Specific country code (ISO 3166)
- `countryIn(Set<String>)` - Country in set
- `notCountry(String)` - Not specific country
- `countryNotIn(Set<String>)` - Country not in set

---

### 14. CodecZoneIdConstraint
**Applies to:** ZoneId

**Zone Type Constraints:**
- `normalized()` - Normalized zone ID
- `regionBased()` - Region-based (e.g., "Europe/Paris")
- `offsetBased()` - Offset-based (e.g., "+02:00")
- `utc()` - Must be UTC
- `systemDefault()` - System default zone

**Zone Set Constraints:**
- `zoneIn(Set<ZoneId>)` - Zone in set
- `available()` - Zone ID is in ZoneId.getAvailableZoneIds()

**Region Constraints:**
- `region(String)` - Specific region (e.g., "Europe", "America")
- `regionIn(Set<String>)` - Region in set
- `regionMatches(Pattern)` - Region pattern

---

### 15. CodecZoneOffsetConstraint
**Applies to:** ZoneOffset

**Comparison Constraints:**
- `between(ZoneOffset, ZoneOffset)` - Offset within range (inclusive)
- `greaterThan(ZoneOffset)` - More positive offset
- `lessThan(ZoneOffset)` - More negative offset

**Sign Constraints:**
- `utc()` - UTC offset (00:00)
- `nonZero()` - Not UTC
- `positive()` - Positive offset (east of UTC)
- `negative()` - Negative offset (west of UTC)

**Hour-Based Constraints:**
- `hoursMin(int)` - Minimum offset in hours (>= value)
- `hoursMax(int)` - Maximum offset in hours (<= value)
- `hoursBetween(int, int)` - Offset hours within range

**NOTE:** ZoneId and ZoneOffset are now separate constraint interfaces (14 and 15) as they apply to different types with different concerns.

---

## IMPLEMENTATION NOTES

### Constraint Composition
Constraints are composed using **AND** logic by default. When multiple constraints are applied to a codec, all must pass.

For **OR** logic, use composition methods (to be defined in base `CodecConstraint` interface):
```java
anyOf(constraint1, constraint2, ...)  // At least one must pass
allOf(constraint1, constraint2, ...)  // All must pass (explicit AND)
not(constraint)                        // Negation
```

### Error Messages
Constraint violations should return detailed error information following this format:
```
Constraint '{constraintName}' failed: {actualValue} {reason}
```

Examples:
- `Constraint 'minLength' failed: 'abc' (length 3) is less than minimum 5`
- `Constraint 'between' failed: 150 is not within range [0, 100]`
- `Constraint 'email' failed: 'notanemail' does not match email pattern`

### Parameter Validation
All constraint parameters must be validated at constraint creation time:
- Ranges: min <= max
- Values: within valid bounds (e.g., quarter 1-4, version 1-8, base 2-36)
- Throw `IllegalArgumentException` with clear message on invalid parameters

### Null Handling
Constraints apply only to non-null values. For null checking, use:
- `nullable()` - Allows null values (wraps codec)
- `nonNull()` - Rejects null values (explicit constraint)

Composition: `codec.nonNull().minLength(5)` - first check non-null, then apply length

### Chainability
Most constraint methods are chainable, returning the constraint builder/interface to allow:
```java
codec.minLength(5)
     .maxLength(20)
     .alphanumeric()
     .notContains("admin")
```

### Performance Considerations
- **Pure constraints**: No I/O, deterministic, fast (most constraints)
- **Time-dependent constraints**: Depend on current time (`past()`, `future()`, `today()`)
  - "now" is evaluated at validation time, not constraint creation time
- **I/O constraints**: Intentionally omitted from this specification
  - Examples: `exists()`, `reachable()`, `resolvable()`
  - These require filesystem/network access and can be slow or unreliable
  - Consider separate constraint interfaces if needed

### Varargs Overloads
Most methods accepting `Set<T>` also provide varargs `T...` overload for convenience:
```java
requiredKeys("key1", "key2", "key3")  // varargs
requiredKeys(Set.of("key1", "key2", "key3"))  // Set
```

### Type Safety
Generic constraints maintain type safety through constraint interface generics:
```java
interface CodecConstraint<T, C extends Codec<T>, V> {
    @NotNull C applyConstraint(@NotNull V config);
}
```
Where:
- `T` = type being constrained (e.g., String, Integer, LocalDate)
- `C` = codec type (e.g., Codec<String>)
- `V` = constraint configuration type (e.g., CodecLengthConstraintConfig)

---

## MAJOR CHANGES FROM ORIGINAL

### Renamed Methods
1. **Consistency**: `lengthInRange` → `lengthBetween`, `sizeInRange` → `sizeBetween`
2. **Clarity**: `monthOfYear` → `month`, `longerThan` → `greaterThan`
3. **Avoid conflicts**: `is(CaseType)` → `caseIs(CaseType)`, `case(CaseType)` → `isCase(CaseType)`
4. **Overload resolution**: Duration unit methods `max/min/exact/between` → `unitMax/unitMin/unitExact/unitBetween`
5. **Standard terminology**: UUID `empty/notEmpty` → `nil/notNil`
6. **Network**: `anyPortOf` → `portIn`
7. **Query parameters**: `anyQueryParameterOf` → `hasAnyQueryParameter`

### Added Methods
- **Varargs overloads**: Most Set-based methods now have varargs alternatives
- **Common helpers**: `even()`, `odd()`, `percentage()`, `normalized()`, `pair()`, `weekday()`
- **Missing operations**: `notEqualTo`, `betweenExclusive`, `notStartsWith`, `notEndsWith`, etc.
- **Pattern helpers**: `email()`, `uuid()`, `ipv4Address()`, `ipv6Address()`, `numeric()`, `hexadecimal()`
- **Component constraints**: Extended for URI, Path with more operations
- **Locale extensions**: Script, variant, Unicode extensions
- **UUID completeness**: All versions (1-8), variant, timestamp/node constraints

### Moved Methods
- **Number format constraints**: Moved from CodecNumericConstraint to CodecPatternConstraint (they validate strings, not numbers)

### Fixed Issues
1. **Typo**: `ìnAnySubnet` → `inAnySubnet` (CodecNetworkConstraint:193)
2. **Logic error**: Fixed `subdomainOf` description (CodecResourceIdentifiersConstraint:229)
3. **Method overloading**: Renamed Duration constraint unit methods to avoid conflicts

### Structural Changes
1. **Split constraints**: Separated ZoneConstraint into CodecZoneIdConstraint (14) and CodecZoneOffsetConstraint (15)
2. **New sections**: Added helper methods, validation, matching, and specialized subsections
3. **Documentation**: Added implementation notes, performance considerations, composition semantics

### Philosophy Changes
1. **I/O constraints excluded**: Removed filesystem/network I/O operations to keep constraints fast and deterministic
2. **Time evaluation documented**: Clarified when "now" is evaluated for temporal constraints
3. **Varargs everywhere**: Improved developer experience with varargs overloads
4. **Helper methods**: Added aliases and shortcuts for common patterns
