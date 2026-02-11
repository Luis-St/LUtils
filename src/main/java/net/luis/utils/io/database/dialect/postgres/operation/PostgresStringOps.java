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

package net.luis.utils.io.database.dialect.postgres.operation;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.operation.SqlStringOps;
import org.jspecify.annotations.NonNull;

/**
 * Interface providing PostgreSQL-specific string column operations.<br>
 *
 * @author Luis-St
 */
public interface PostgresStringOps extends SqlStringOps {
	
	/**
	 * Creates a case-insensitive pattern matching condition.<br>
	 * Generates SQL: {@code column ILIKE pattern}.<br>
	 *
	 * @param pattern The pattern to match against
	 * @return The case-insensitive like condition
	 */
	@NonNull SqlCondition ilike(@NonNull String pattern);
	
	/**
	 * Creates a condition using the SQL {@code SIMILAR TO} operator.<br>
	 * Generates SQL: {@code column SIMILAR TO pattern}.<br>
	 * <p>
	 *     The {@code SIMILAR TO} operator combines SQL {@code LIKE} syntax with POSIX regular expression syntax.<br>
	 *     It supports patterns like {@code %}, {@code _}, {@code |}, {@code *}, and {@code +}.<br>
	 * </p>
	 *
	 * @param pattern The SIMILAR TO pattern to match against
	 * @return The SIMILAR TO condition
	 */
	@NonNull SqlCondition similarTo(@NonNull String pattern);
	
	/**
	 * Creates a condition using a POSIX regular expression match.<br>
	 * Generates SQL: {@code column ~ pattern}.<br>
	 * <p>
	 *     POSIX regular expressions provide more powerful pattern matching than {@code LIKE} or {@code SIMILAR TO}.<br>
	 *     The {@code ~} operator performs a case-sensitive match against the given regex pattern.<br>
	 * </p>
	 *
	 * @param pattern The POSIX regular expression pattern
	 * @return The POSIX regex condition
	 */
	@NonNull SqlCondition posixRegex(@NonNull String pattern);
}
