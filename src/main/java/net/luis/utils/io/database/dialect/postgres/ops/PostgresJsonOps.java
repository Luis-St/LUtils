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

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.function.SqlExpression;
import org.jspecify.annotations.NonNull;

/**
 * Interface providing PostgreSQL-specific JSON/JSONB operations for column conditions.<br>
 * <p>
 *     These operations generate SQL expressions and conditions for PostgreSQL JSON and JSONB types,
 *     including field access, key existence checks, and text extraction.<br>
 * </p>
 *
 * @author Luis-St
 */
public interface PostgresJsonOps {

	/**
	 * Accesses a JSON field by key and returns it as an expression.<br>
	 * Generates SQL: {@code column -> 'key'}.<br>
	 *
	 * @param key The JSON field key
	 * @return An expression representing the JSON field value
	 */
	@NonNull SqlExpression<?> get(@NonNull String key);

	/**
	 * Creates a condition that checks if the JSON column contains the given key.<br>
	 * Generates SQL: {@code column ? 'key'}.<br>
	 *
	 * @param key The key to check for
	 * @return The has-key condition
	 */
	@NonNull SqlCondition hasKey(@NonNull String key);

	/**
	 * Accesses a JSON field by key and returns its value as text.<br>
	 * Generates SQL: {@code column ->> 'key'}.<br>
	 *
	 * @param key The JSON field key
	 * @return A condition representing the JSON field value as text
	 */
	@NonNull SqlCondition getAsText(@NonNull String key);
}
