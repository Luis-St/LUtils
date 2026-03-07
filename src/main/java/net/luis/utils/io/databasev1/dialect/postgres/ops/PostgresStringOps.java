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

package net.luis.utils.io.databasev1.dialect.postgres.ops;

import net.luis.utils.io.databasev1.condition.SqlCondition;
import net.luis.utils.io.databasev1.table.ops.SqlStringOps;
import org.jspecify.annotations.NonNull;

/**
 * Interface providing PostgreSQL-specific string operations for column conditions.<br>
 * Extends {@link SqlStringOps} with additional operations that are only available in PostgreSQL,
 * such as case-insensitive pattern matching, similar-to patterns, and POSIX regular expressions.<br>
 *
 * @see SqlStringOps
 *
 * @author Luis-St
 */
public interface PostgresStringOps extends SqlStringOps {
	
	/**
	 * Creates a condition using a case-insensitive {@code ILIKE} pattern.<br>
	 * Generates SQL: {@code column ILIKE pattern}.<br>
	 *
	 * @param pattern The ilike pattern
	 * @return The ilike condition
	 */
	@NonNull SqlCondition ilike(@NonNull String pattern);
	
	/**
	 * Creates a condition using a PostgreSQL {@code SIMILAR TO} regular expression pattern.<br>
	 * Generates SQL: {@code column SIMILAR TO pattern}.<br>
	 *
	 * @param pattern The similar-to pattern
	 * @return The similar-to condition
	 */
	@NonNull SqlCondition similarTo(@NonNull String pattern);
	
	/**
	 * Creates a condition using a POSIX regular expression match.<br>
	 * Generates SQL: {@code column ~ regex}.<br>
	 *
	 * @param regex The POSIX regular expression
	 * @return The regex match condition
	 */
	@NonNull SqlCondition posixRegex(@NonNull String regex);
}
