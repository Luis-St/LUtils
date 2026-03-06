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

package net.luis.utils.io.database.query;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

/**
 * Mixin interface for SQL query types that support {@code JOIN} clauses.<br>
 * Implemented by SELECT, UPDATE, and DELETE query builders to provide a uniform join API across all mutating and read query types.<br>
 *
 * @author Luis-St
 *
 * @param <Q> The self-referencing query type enabling fluent method chaining
 */
public interface SqlJoinable<Q> {
	
	/**
	 * Adds an {@code INNER JOIN} clause to the query.<br>
	 * Only rows that match the join condition in both tables are included.<br>
	 * Generates SQL: {@code INNER JOIN table ON condition}.<br>
	 *
	 * @param table The table to join
	 * @param on The join condition
	 * @return This query for method chaining
	 */
	@NonNull Q innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	/**
	 * Adds a {@code LEFT JOIN} clause to the query.<br>
	 * All rows from the primary table are included; unmatched rows from the joined table produce {@code NULL} values.<br>
	 * Generates SQL: {@code LEFT JOIN table ON condition}.<br>
	 *
	 * @param table The table to join
	 * @param on The join condition
	 * @return This query for method chaining
	 */
	@NonNull Q leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	/**
	 * Adds a {@code RIGHT JOIN} clause to the query.<br>
	 * All rows from the joined table are included; unmatched rows from the primary table produce {@code NULL} values.<br>
	 * Generates SQL: {@code RIGHT JOIN table ON condition}.<br>
	 *
	 * @param table The table to join
	 * @param on The join condition
	 * @return This query for method chaining
	 */
	@NonNull Q rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	/**
	 * Adds a {@code FULL JOIN} clause to the query.<br>
	 * All rows from both tables are included; unmatched rows on either side produce {@code NULL} values.<br>
	 * Generates SQL: {@code FULL JOIN table ON condition}.<br>
	 *
	 * @param table The table to join
	 * @param on The join condition
	 * @return This query for method chaining
	 */
	@NonNull Q fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	/**
	 * Adds a {@code CROSS JOIN} clause to the query.<br>
	 * Produces the Cartesian product of both tables (all row combinations).<br>
	 * Generates SQL: {@code CROSS JOIN table}.<br>
	 *
	 * @param table The table to cross join
	 * @return This query for method chaining
	 */
	@NonNull Q crossJoin(@NonNull SqlTable<?> table);
}
