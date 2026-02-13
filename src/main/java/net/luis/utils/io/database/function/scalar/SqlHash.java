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

package net.luis.utils.io.database.function.scalar;

import net.luis.utils.io.database.function.SqlExpression;
import org.jspecify.annotations.NonNull;

/**
 * Static utility class for SQL hashing functions.<br>
 *
 * @author Luis-St
 */
public class SqlHash {
	
	/**
	 * Computes the MD5 hash of the given expression.<br>
	 * Generates SQL: {@code MD5(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The expression to hash
	 * @return The MD5 hash expression
	 */
	public static @NonNull SqlExpression<String> md5(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Computes the SHA-1 hash of the given expression.<br>
	 * Generates SQL: {@code SHA1(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The expression to hash
	 * @return The SHA-1 hash expression
	 */
	public static @NonNull SqlExpression<String> sha1(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Computes the SHA-224 hash of the given expression.<br>
	 * Generates SQL: {@code SHA224(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The expression to hash
	 * @return The SHA-224 hash expression
	 */
	public static @NonNull SqlExpression<String> sha224(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Computes the SHA-256 hash of the given expression.<br>
	 * Generates SQL: {@code SHA256(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The expression to hash
	 * @return The SHA-256 hash expression
	 */
	public static @NonNull SqlExpression<String> sha256(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Computes the SHA-384 hash of the given expression.<br>
	 * Generates SQL: {@code SHA384(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The expression to hash
	 * @return The SHA-384 hash expression
	 */
	public static @NonNull SqlExpression<String> sha384(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Computes the SHA-512 hash of the given expression.<br>
	 * Generates SQL: {@code SHA512(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The expression to hash
	 * @return The SHA-512 hash expression
	 */
	public static @NonNull SqlExpression<String> sha512(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Computes the CRC32 checksum of the given expression.<br>
	 * Generates SQL: {@code CRC32(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The expression to compute the checksum for
	 * @return The CRC32 checksum expression
	 */
	public static @NonNull SqlExpression<Long> crc32(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
}
