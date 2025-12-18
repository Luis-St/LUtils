### 1. CodecLengthConstraint
**Applies to:** String, Arrays

**Methods:**
- `minLength(int)` - Minimum length validation
- `maxLength(int)` - Maximum length validation
- `exactLength(int)` - Exact length validation
- `lengthInRange(int, int)` - Range validation
- `empty()` - Must be empty (length = 0)
- `notEmpty()` - Must not be empty (length >= 1)

---

### 2. CodecSizeConstraint
**Applies to:** Collections (List, Set), Maps

**Methods:**
- `minSize(int)` - Minimum number of elements
- `maxSize(int)` - Maximum number of elements
- `exactSize(int)` - Exact number of elements
- `sizeInRange(int, int)` - Range validation
- `empty()` - Must be empty (size = 0)
- `notEmpty()` - Must not be empty (size >= 1)
- `singleton()` - Exactly one element (size = 1)

---

### 3. CodecTemporalConstraint
**Applies to:** LocalDate, LocalDateTime, LocalTime, Instant, ZonedDateTime, OffsetDateTime, OffsetTime, Year, Month, DayOfWeek

**Comparison Constraints:**
- `after(T)` - Must be after specified time (exclusive)
- `afterOrEqual(T)` - Must be after or equal to specified time
- `before(T)` - Must be before specified time (exclusive)
- `beforeOrEqual(T)` - Must be before or equal to specified time
- `between(T, T)` - Must be within time range (inclusive)
- `equalTo(T)` - Must be equal to specified time

**Relative Time Constraints:**
- `past()` - Must be in the past (before now)
- `pastOrPresent()` - Must be in the past or now
- `future()` - Must be in the future (after now)
- `futureOrPresent()` - Must be in the future or now
- `withinLast(Duration/Period)` - Within last X time (e.g., within last 7 days, within last 2 hours)
- `withinNext(Duration/Period)` - Within next X time (e.g., within next 30 days, within next 1 hour)

**Temporal Component Matching:**
- `sameDay(T)` - Same calendar day as specified temporal
- `sameMonth(T)` - Same month as specified temporal
- `sameYear(T)` - Same year as specified temporal
- `dayOfWeek(DayOfWeek)` - Must be specific day of week (MONDAY, TUESDAY, etc.)
- `dayOfWeekIn(Set<DayOfWeek>)` - Must be one of specified days of week
- `monthOfYear(Month)` - Must be specific month (JANUARY, FEBRUARY, etc.)
- `monthIn(Set<Month>)` - Must be one of specified months

**Calendar Constraints:**
- `businessDay()` - Monday through Friday (not weekend)
- `weekend()` - Saturday or Sunday
- `dayOfMonth(int)` - Specific day of month (1-31)
- `dayOfMonthIn(Set<Integer>)` - One of specified days of month
- `quarter(int)` - Specific quarter (1-4)

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
- `divisibleBy(long)` - Divisible by specified number
- `notDivisibleBy(long)` - Not divisible by specified number
- `powerOfTwo()` - Power of 2 (1, 2, 4, 8, 16, 32, ...)
- `powerOf(int)` - Power of specified base

**Decimal-Specific (for Float, Double, BigDecimal):**
- `precision(int)` - Total number of significant digits (BigDecimal)
- `minPrecision(int)` - Minimum number of significant digits (BigDecimal)
- `maxPrecision(int)` - Maximum number of significant digits (BigDecimal)
- `scale(int)` - Exact number of decimal places
- `minScale(int)` - Minimum number of decimal places
- `maxScale(int)` - Maximum number of decimal places
- `finite()` - Not infinity (Float/Double)
- `notNaN()` - Not NaN (Float/Double)
- `integral()` - No fractional part (e.g., 5 or 5.0 valid, 5.1 invalid)

**Number Format Constraints:**
- `binaryFormat()` - Valid binary string representation (for String to Number parsing)
- `octalFormat()` - Valid octal string representation
- `decimalFormat()` - Valid decimal string representation
- `hexFormat()` - Valid hexadecimal string representation
- `inBase(int)` - Valid representation in specified base (2-36)

---

### 5. CodecPatternConstraint
**Applies to:** String

**Pattern Matching:**
- `matches(Pattern)` - Match compiled pattern
- `notMatches(Pattern)` - Must not match compiled pattern

**Common Patterns:**
- `alphanumeric()` - Letters and digits only
- `alphabetic()` - Letters only
- `base64()` - Valid base64 string

**String Content:**
- `startsWith(String)` - Starts with prefix
- `endsWith(String)` - Ends with suffix
- `contains(String)` - Contains substring
- `notContains(String)` - Does not contain substring
- `containsAny(String...)` - Contains any of the substrings
- `containsAll(String...)` - Contains all substrings

**Case Constraints:**
- `is(CaseType)` - Specific case type (UPPER_CASE, LOWER_CASE, TITLE_CASE, CAMEL_CASE, PASCAL_CASE, SNAKE_CASE, KEBAB_CASE) (chainable)
- `not(CaseType)` - Not specific case type (chainable)

**Character Constraints:**
- `noWhitespace()` - No whitespace characters
- `trimmed()` - No leading/trailing whitespace
- `singleLine()` - No line breaks
- `ascii()` - Only ASCII characters

---

### 6. CodecDurationConstraint
**Applies to:** Duration, Period

**Range Constraints:**
- `longerThan(T)` - Longer than specified duration (exclusive)
- `longerThanOrEqual(T)` - Longer than or equal to specified duration (inclusive)
- `shorterThan(T)` - Shorter than specified duration (exclusive)
- `shorterThanOrEqual(T)` - Shorter than or equal to specified duration (inclusive)
- `between(T, T)` - Within duration range (inclusive)
- `exactDuration(T)` - Exact duration match

**Sign Constraints:**
- `positive()` - Positive duration (> 0)
- `negative()` - Negative duration (< 0)
- `nonNegative()` - Non-negative duration (>= 0)
- `nonPositive()` - Non-positive duration (<= 0)
- `zero()` - Zero duration
- `nonZero()` - Not zero duration

**Unit-Based Constraints (Duration):**
- `max(ChronoUnit unit, long value)` - Maximum value in specified unit (Duration: DAYS, HOURS, MINUTES, SECONDS, MILLIS, etc.) (Period: YEARS, MONTHS, DAYS) (chainable)
- `min(ChronoUnit unit, long value)` - Minimum value in specified unit (Duration: DAYS, HOURS, MINUTES, SECONDS, MILLIS, etc.) (Period: YEARS, MONTHS, DAYS) (chainable)
- `exact(ChronoUnit unit, long value)` - Exact value in specified unit (Duration: DAYS, HOURS, MINUTES, SECONDS, MILLIS, etc.) (Period: YEARS, MONTHS, DAYS) (chainable)
- `between(ChronoUnit unit, long min, long max)` - Value range in specified unit (Duration: DAYS, HOURS, MINUTES, SECONDS, MILLIS, etc.) (Period: YEARS, MONTHS, DAYS) (chainable)

---

### 7. CodecMapConstraint
**Applies to:** Map

**Key Constraints:**
- `requiredKeys(Set<K>)` - Must contain these keys
- `forbiddenKeys(Set<K>)` - Must not contain these keys
- `allowOnlyKeys(Set<K>)` - Only these keys allowed

---

### 8. CodecNetworkConstraint
**Applies to:** InetAddress, InetSocketAddress

**IP Constraints (InetAddress):**
- `ipv4()` - Only IPv4 addresses
- `ipv6()` - Only IPv6 addresses
- `publicIP()` - Not private/loopback/link-local
- `privateIP()` - Private IP range
- `notLoopback()` - Not loopback address
- `notLinkLocal()` - Not link-local address
- `multicast()` - Multicast address
- `inSubnet(String)` - Within specified subnet (CIDR notation)
- ìnAnySubnet(Set<String>)` - Within any of specified subnets (CIDR notation)

**Port Constraints (InetSocketAddress):**
- `wellKnownPort()` - 0-1023
- `registeredPort()` - 1024-49151
- `dynamicPort()` - 49152-65535
- `anyPortOf(Set<Integer>)` - Port in specified set
- `portInRange(int, int)` - Port within range

---

### 9. CodecResourceIdentifiersConstraint
**Applies to:** URI
**Note:** Remove URL Codec

**Scheme Constraints:**
- `scheme(String)` - Specific scheme required (e.g., "http", "https", "ftp")
- `schemeIn(Set<String>)` - Scheme must be in set
- `schemeMatches(Pattern)` - Scheme matches regex pattern

**Component Constraints:**
- `has(URIComponent)` - Component must be present (e.g., HOST, PORT, PATH, QUERY, FRAGMENT, USER_INFO) (chainable)
- `lacks(URIComponent)` - Component must be absent (e.g., HOST, PORT, PATH, QUERY, FRAGMENT, USER_INFO) (chainable)
- `equals(URIComponent, String)` - Component equals specific value, supported components: HOST, PATH, FRAGMENT, USER_INFO
- `anyOf(URIComponent, Set<String>)` - Component is in set of values, supported components: HOST, PATH, FRAGMENT, USER_INFO
- `noneOf(URIComponent, Set<String>)` - Component is not in set of values, supported components: HOST, PATH, FRAGMENT, USER_INFO
- `startsWith(URIComponent, String)` - Component starts with prefix, supported components: HOST, FRAGMENT, USER_INFO
- `endsWith(URIComponent, String)` - Component ends with suffix, supported components: HOST, PORT, FRAGMENT, USER_INFO
- `matches(URIComponent, Pattern)` - Component matches regex pattern, supported components: HOST, PATH, FRAGMENT, USER_INFO
- `contains(URIComponent, String)` - Component contains substring, supported components: HOST, PATH, FRAGMENT, USER_INFO

**Host Specific Constraints:**
- `ipAddress()` - Host is IP address (not domain name)
- `domainName()` - Host is domain name (not IP address)
- `localhost()` - Must be localhost
- `rootDomain()` - Host is root domain (e.g., "example.com" but not "sub.example.com")
- `subdomainOf(String)` - Host is subdomain of given domain (e.g., "example.com" matches "sub.example.com")

**Port Constraints:**
- `port(int)` - Specific port number
- `portIn(Set<Integer>)` - Port in set
- `portInRange(int, int)` - Port within range
- `wellKnownPort()` - Port 0-1023
- `registeredPort()` - Port 1024-49151
- `dynamicPort()` - Port 49152-65535

**Query Constraints:**
- `hasQueryParameter(String)` - Has query parameter (any value) (chainable)
- `anyQueryParameterOf(Set<String>)` - Has query parameter in set

**URI Type Constraints:**
- `absolute()` - Absolute URI (has scheme)
- `relative()` - Relative URI (no scheme)
- `opaque()` - Opaque URI (no authority component)
- `hierarchical()` - Hierarchical URI (has authority)

---

### 10. CodecPathConstraint
**Applies to:** Path
**Note:** Remove File codec & Include/Extend length constraints

**Path Type Constraints:**
- `absolute()` - Must be absolute path (e.g., starts with "/" or "C:\\")
- `relative()` - Must be relative path (e.g., does not start with "/" or "C:\\")
- `normalized()` - No "." or ".." components
- `canonical()` - In canonical form (resolved, normalized, absolute)

**Structure Constraints:**
- `depth(int)` - Specific depth level (number of path components)
- `maxDepth(int)` - Maximum depth level
- `minDepth(int)` - Minimum depth level
- `root(String)` - Specific root path (e.g., "/", "C:\\", "/dev/")
- `equals(PathComponent, String)` - Specific path component equals value (PathComponent can be ROOT, PARENT_PATH, FULL_PATH, FILENAME, EXTENSION)
- `anyOf(PathComponent, Set<String>)` - Any of the specified values for a path component
- `noneOf(PathComponent, Set<String>)` - None of the specified values for a path component
- `startsWith(PathComponent, String)` - Path component starts with prefix
- `endsWith(PathComponent, String)` - Path component ends with suffix
- `matches(PathComponent, Pattern)` - Regex match for a path component
- `contains(PathComponent, String)` - Path component contains substring

**Extension Specific Constraints:**
- `noExtension()` - No file extension

**Platform Constraints:**
- `validFor(Platform)` - Valid Unix/Linux path (Platform.CURRENT, Platform.UNIX, Platform.LINUX, etc.)
- `portable()` - Valid for all platforms (no platform-specific chars)
- `separators(Platform)` - Uses specific path separators (e.g., Platform.WINDOWS uses "\\", Platform.UNIX uses "/")

---

### 11. CodecCharConstraint
**Applies to:** Character

**Character Type:**
- `is(CharacterType)` - Specific character type (e.g., LETTER, DIGIT, ALPHANUMERIC, PUNCTUATION such as '.', ',', SYMBOL such as '@', '#', WHITESPACE, CONTROL such as '\n', '\t')
- `not(CharacterType)` - Not specific character type

**Case:**
- `case(CaseType)` - Supported cases: UPPER_CASE, LOWER_CASE

**Range:**
- `inRange(char, char)` - Within character range
- `ascii()` - ASCII character (0-127)
- `extendedAscii()` - Extended ASCII (0-255)
- `unicode()` - Any unicode character

**Specific Characters:**
- `oneOf(Set<Character>)` - One of specified characters
- `notOneOf(Set<Character>)` - Not one of specified characters

---

### 12. CodecUUIDConstraint
**Applies to:** UUID

**Version Constraints:**
- `version(int)` - Specific UUID version (1-8)
- `versionIn(Set<Integer>)` - Version in set
- `version1()` - Time-based UUID
- `version4()` - Random UUID
- `version5()` - Name-based (SHA-1) UUID
- `version7()` - Unix Epoch time-based UUID

**Variant Constraints:**
- `empty()` - All zeros UUID
- `notEmpty()` - Not all zeros

---

### 13. CodecLocaleConstraint
**Applies to:** Locale

**Language Constraints:**
- `hasLanguage()` - Has language component
- `language(String)` - Specific language code
- `languageIn(Set<String>)` - Language in set

**Country Constraints:**
- `hasCountry()` - Has country component
- `country(String)` - Specific country code
- `countryIn(Set<String>)` - Country in set

---

### 14. CodecZoneConstraint
**Applies to:** ZoneId, ZoneOffset
**Priority:** LOW

**ZoneId Constraints:**
- `normalized()` - Normalized zone ID
- `regionBased()` - Region-based (e.g., "Europe/Paris")
- `offsetBased()` - Offset-based (e.g., "+02:00")
- `utc()` - Must be UTC
- `zoneIn(Set<ZoneId>)` - Zone in set

**ZoneOffset Constraints:**
- `inRange(ZoneOffset, ZoneOffset)` - Offset within range
- `utc()` - UTC offset (00:00)
- `positive()` - Positive offset
- `negative()` - Negative offset
- `min(int)` - Minimum offset in hours
- `max(int)` - Maximum offset in hours


