# Common Hashing Functions Across Dialects

Cryptographic hash and checksum functions shared across PostgreSQL, MySQL, and MariaDB.
These functions compute fixed-size hash values from column data, commonly used for data integrity verification, deduplication, and non-reversible storage of sensitive values.

SQLite has no built-in hashing functions. All hash operations in SQLite require loadable extensions (e.g., the `crypto` extension or custom user-defined functions).
PostgreSQL has built-in `MD5` and SHA-2 functions but requires the `pgcrypto` extension for SHA-1.

All hash functions below return lowercase hexadecimal strings unless otherwise noted.

---

## Message Digest Hashes

### MD5

Computes the MD5 hash of the column value, returning a 32-character hex string (128-bit digest).

| Dialect    | Method Name | Generated SQL   |
|------------|-------------|-----------------|
| PostgreSQL | `md5()`     | `MD5(column)`   |
| MySQL      | `md5()`     | `MD5(column)`   |
| MariaDB    | `md5()`     | `MD5(column)`   |

Note: All three dialects have `MD5` as a built-in function with identical syntax. PostgreSQL's `MD5` accepts `text` or `bytea` input; when given `bytea`, it returns the hex digest of the raw bytes. MySQL and MariaDB's `MD5` treats the input as a binary string. MD5 produces 128-bit digests and is considered cryptographically broken; do not use it for security-sensitive purposes such as password storage. It remains useful for checksums and non-security deduplication.

### SHA-1

Computes the SHA-1 hash of the column value, returning a 40-character hex string (160-bit digest).

| Dialect    | Method Name | Generated SQL                          |
|------------|-------------|----------------------------------------|
| PostgreSQL | `sha1()`    | `ENCODE(DIGEST(column, 'sha1'), 'hex')` |
| MySQL      | `sha1()`    | `SHA1(column)`                         |
| MariaDB    | `sha1()`    | `SHA1(column)`                         |

Note: MySQL and MariaDB have a built-in `SHA1()` function (also aliased as `SHA()`). PostgreSQL does not have a built-in SHA-1 function; it requires the `pgcrypto` extension (`CREATE EXTENSION IF NOT EXISTS pgcrypto`). The `DIGEST(data, type)` function returns `bytea`, so `ENCODE(..., 'hex')` is needed to produce a hex string. SHA-1 is considered cryptographically weak; prefer SHA-256 or SHA-512 for new applications.

---

## SHA-2 Family

The SHA-2 family provides stronger cryptographic hashes in four standard output sizes. PostgreSQL 11+ has built-in SHA-2 functions that operate on `bytea` input and return `bytea` output, requiring `ENCODE(..., 'hex')` for hex string results. MySQL and MariaDB unify all SHA-2 variants under the `SHA2(column, bitLength)` function.

### SHA-224

Computes the SHA-224 hash of the column value, returning a 56-character hex string (224-bit digest).

| Dialect    | Method Name | Generated SQL                                  |
|------------|-------------|-------------------------------------------------|
| PostgreSQL | `sha224()`  | `ENCODE(SHA224(column::bytea), 'hex')`          |
| MySQL      | `sha224()`  | `SHA2(column, 224)`                             |
| MariaDB    | `sha224()`  | `SHA2(column, 224)`                             |

### SHA-256

Computes the SHA-256 hash of the column value, returning a 64-character hex string (256-bit digest).

| Dialect    | Method Name | Generated SQL                                  |
|------------|-------------|-------------------------------------------------|
| PostgreSQL | `sha256()`  | `ENCODE(SHA256(column::bytea), 'hex')`          |
| MySQL      | `sha256()`  | `SHA2(column, 256)`                             |
| MariaDB    | `sha256()`  | `SHA2(column, 256)`                             |

Note: SHA-256 is the most widely used variant. MySQL and MariaDB also accept `SHA2(column, 0)` as an alias for `SHA2(column, 256)`.

### SHA-384

Computes the SHA-384 hash of the column value, returning a 96-character hex string (384-bit digest).

| Dialect    | Method Name | Generated SQL                                  |
|------------|-------------|-------------------------------------------------|
| PostgreSQL | `sha384()`  | `ENCODE(SHA384(column::bytea), 'hex')`          |
| MySQL      | `sha384()`  | `SHA2(column, 384)`                             |
| MariaDB    | `sha384()`  | `SHA2(column, 384)`                             |

### SHA-512

Computes the SHA-512 hash of the column value, returning a 128-character hex string (512-bit digest).

| Dialect    | Method Name | Generated SQL                                  |
|------------|-------------|-------------------------------------------------|
| PostgreSQL | `sha512()`  | `ENCODE(SHA512(column::bytea), 'hex')`          |
| MySQL      | `sha512()`  | `SHA2(column, 512)`                             |
| MariaDB    | `sha512()`  | `SHA2(column, 512)`                             |

Note: SHA-512 provides the highest security margin in the SHA-2 family. On 64-bit systems, SHA-512 can be faster than SHA-256 due to its use of 64-bit arithmetic.

---

## Checksum

### CRC32

Computes the CRC-32 checksum of the column value, returning an unsigned 32-bit integer.

| Dialect    | Method Name | Generated SQL      |
|------------|-------------|--------------------|
| MySQL      | `crc32()`   | `CRC32(column)`    |
| MariaDB    | `crc32()`   | `CRC32(column)`    |

Note: Only MySQL and MariaDB have a built-in `CRC32()` function. PostgreSQL does not offer a built-in CRC32 (the `pg_crc32` extension or a custom function can provide it). CRC32 is not a cryptographic hash; it is a fast checksum intended for error detection, not security. It has a high collision rate for adversarial inputs. The return value is an unsigned 32-bit integer (0 to 4294967295).

---

## Key Differences & Limitations

### SQLite Limitations

SQLite has no built-in hashing functions. To use hash functions in SQLite, one of the following approaches is required:

| Approach                    | Description                                                           |
|-----------------------------|-----------------------------------------------------------------------|
| Loadable extension          | Compile and load a C extension that registers hash functions          |
| Application-side hashing    | Compute hashes in application code before inserting into the database |
| Custom SQL function         | Register hash functions via the application's SQLite API bindings     |

Most SQLite bindings (Python, Java, etc.) allow registering custom scalar functions at runtime. For example, in Java with JDBC, `org.sqlite.Function` can be used to register an `md5()` or `sha256()` function backed by `java.security.MessageDigest`.

### PostgreSQL Extension Requirements

| Function | Built-in Since | Extension Alternative                          |
|----------|----------------|------------------------------------------------|
| `MD5`    | 7.1+           | —                                              |
| `SHA1`   | —              | `pgcrypto` (`DIGEST(column, 'sha1')`)          |
| `SHA224` | 11 (2018)      | `pgcrypto` (`DIGEST(column, 'sha224')`)        |
| `SHA256` | 11 (2018)      | `pgcrypto` (`DIGEST(column, 'sha256')`)        |
| `SHA384` | 11 (2018)      | `pgcrypto` (`DIGEST(column, 'sha384')`)        |
| `SHA512` | 11 (2018)      | `pgcrypto` (`DIGEST(column, 'sha512')`)        |

The built-in SHA-2 functions (`SHA224`, `SHA256`, `SHA384`, `SHA512`) take `bytea` input, so text columns require an explicit cast: `column::bytea`. The `pgcrypto` extension's `DIGEST` function accepts both `text` and `bytea` directly.

For PostgreSQL versions before 11 that need SHA-2 hashing without `pgcrypto`, there is no built-in option.

### Return Type Differences

| Dialect    | MD5 Return Type    | SHA Return Type       | Notes                                        |
|------------|--------------------|-----------------------|----------------------------------------------|
| PostgreSQL | `text` (hex)       | `bytea` (raw bytes)   | Use `ENCODE(..., 'hex')` for hex string      |
| MySQL      | `varchar` (hex)    | `varchar` (hex)       | Returns hex string directly                  |
| MariaDB    | `varchar` (hex)    | `varchar` (hex)       | Returns hex string directly                  |

PostgreSQL's `MD5()` is an exception: it returns a hex string directly. All other PostgreSQL hash functions (`SHA224` through `SHA512`) return raw `bytea` and need `ENCODE(result, 'hex')` to produce a hex string. MySQL and MariaDB always return hex strings for all hash functions.

### Input Encoding

Hash functions are sensitive to the byte-level representation of the input:

- **PostgreSQL**: `MD5('text')` hashes the UTF-8 bytes of the text. `SHA256(column::bytea)` also uses UTF-8 byte representation when casting from text.
- **MySQL / MariaDB**: Hash functions use the connection character set encoding. Ensure consistent encoding (preferably `utf8mb4`) to produce consistent hashes across different client sessions.
- **General**: The same logical string can produce different hashes if encoded differently (e.g., UTF-8 vs Latin-1). Always ensure consistent character encoding when comparing hashes across systems.

### Null Handling

All dialects return `NULL` if the input column value is `NULL` for all hash functions. No special null handling is needed.

### Performance Considerations

Relative performance of hash algorithms (fastest to slowest):

| Algorithm | Output Size | Relative Speed | Use Case                           |
|-----------|-------------|----------------|------------------------------------|
| CRC32     | 32 bits     | Fastest        | Error detection, partitioning      |
| MD5       | 128 bits    | Fast           | Checksums, deduplication           |
| SHA-1     | 160 bits    | Moderate       | Legacy compatibility               |
| SHA-256   | 256 bits    | Moderate       | General-purpose security           |
| SHA-512   | 512 bits    | Moderate       | High-security, fast on 64-bit CPUs |

Note: For large datasets, consider computing hashes in application code or using generated/computed columns to avoid recalculating hashes on every query. MySQL and MariaDB support `GENERATED ALWAYS AS (SHA2(column, 256)) STORED` for persisted hash columns.

### Security Considerations

| Algorithm | Status                  | Recommendation                                                       |
|-----------|-------------------------|----------------------------------------------------------------------|
| CRC32     | Not cryptographic       | Never use for security; only for checksums and error detection        |
| MD5       | Cryptographically broken | Do not use for passwords or digital signatures; acceptable for checksums |
| SHA-1     | Cryptographically weak  | Deprecated for security use since 2017; prefer SHA-256+              |
| SHA-256   | Secure                  | Recommended for general-purpose cryptographic hashing                |
| SHA-512   | Secure                  | Recommended for applications requiring maximum security margin       |

For password storage, do not use any of these hash functions directly. Use dedicated password hashing algorithms (bcrypt, scrypt, Argon2) in application code instead. SQL-level hash functions lack salting and key stretching, making them unsuitable for password storage.
