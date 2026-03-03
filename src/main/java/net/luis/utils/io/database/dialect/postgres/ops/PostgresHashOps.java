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

package net.luis.utils.io.database.dialect.postgres.ops;

import net.luis.utils.io.database.function.SqlExpression;
import org.jspecify.annotations.NonNull;

/**
 * Interface providing PostgreSQL-specific hashing operations for column expressions.<br>
 * <p>
 *     These operations generate SQL expressions for computing hash values of column data.<br>
 *     The {@link #md5()} function is built-in to PostgreSQL.<br>
 *     The SHA variants ({@link #sha1()}, {@link #sha224()}, {@link #sha256()}, {@link #sha384()}, {@link #sha512()})
 *     require the {@code pgcrypto} extension to be enabled.
 * </p>
 *
 * @author Luis-St
 */
public interface PostgresHashOps {
	
	/**
	 * Computes the MD5 hash of the column.<br>
	 * Generates SQL: {@code md5(column)}.<br>
	 *
	 * @return The MD5 hash expression
	 */
	@NonNull SqlExpression<String> md5();
	
	/**
	 * Computes the SHA-1 hash of the column.<br>
	 * Generates SQL: {@code encode(digest(column, 'sha1'), 'hex')}.<br>
	 * <p>
	 *     Requires the {@code pgcrypto} extension.
	 * </p>
	 *
	 * @return The SHA-1 hash expression
	 */
	@NonNull SqlExpression<String> sha1();
	
	/**
	 * Computes the SHA-224 hash of the column.<br>
	 * Generates SQL: {@code encode(digest(column, 'sha224'), 'hex')}.<br>
	 * <p>
	 *     Requires the {@code pgcrypto} extension.
	 * </p>
	 *
	 * @return The SHA-224 hash expression
	 */
	@NonNull SqlExpression<String> sha224();
	
	/**
	 * Computes the SHA-256 hash of the column.<br>
	 * Generates SQL: {@code encode(digest(column, 'sha256'), 'hex')}.<br>
	 * <p>
	 *     Requires the {@code pgcrypto} extension.
	 * </p>
	 *
	 * @return The SHA-256 hash expression
	 */
	@NonNull SqlExpression<String> sha256();
	
	/**
	 * Computes the SHA-384 hash of the column.<br>
	 * Generates SQL: {@code encode(digest(column, 'sha384'), 'hex')}.<br>
	 * <p>
	 *     Requires the {@code pgcrypto} extension.
	 * </p>
	 *
	 * @return The SHA-384 hash expression
	 */
	@NonNull SqlExpression<String> sha384();
	
	/**
	 * Computes the SHA-512 hash of the column.<br>
	 * Generates SQL: {@code encode(digest(column, 'sha512'), 'hex')}.<br>
	 * <p>
	 *     Requires the {@code pgcrypto} extension.
	 * </p>
	 *
	 * @return The SHA-512 hash expression
	 */
	@NonNull SqlExpression<String> sha512();
}
