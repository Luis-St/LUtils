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
 * Represents a sql query that can be extended with join clauses.<br>
 * It provides methods to add inner, left, right, full and cross joins to another {@link SqlTable table}.<br>
 *
 * @author Luis-St
 *
 * @param <E> The type of the elements returned by the query
 */
public interface SqlJoinableQuery<E> extends SqlQuery<E> {
	
	/**
	 * Adds an inner join with the given table using the given join condition.<br>
	 *
	 * @param table The table to join with
	 * @param on The condition the joined rows must match
	 * @return A new joinable query with the inner join applied
	 * @throws NullPointerException If the table or condition is null
	 */
	@NonNull SqlJoinableQuery<E> innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	/**
	 * Adds a left outer join with the given table using the given join condition.<br>
	 *
	 * @param table The table to join with
	 * @param on The condition the joined rows must match
	 * @return A new joinable query with the left join applied
	 * @throws NullPointerException If the table or condition is null
	 */
	@NonNull SqlJoinableQuery<E> leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	/**
	 * Adds a right outer join with the given table using the given join condition.<br>
	 *
	 * @param table The table to join with
	 * @param on The condition the joined rows must match
	 * @return A new joinable query with the right join applied
	 * @throws NullPointerException If the table or condition is null
	 */
	@NonNull SqlJoinableQuery<E> rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	/**
	 * Adds a full outer join with the given table using the given join condition.<br>
	 *
	 * @param table The table to join with
	 * @param on The condition the joined rows must match
	 * @return A new joinable query with the full join applied
	 * @throws NullPointerException If the table or condition is null
	 */
	@NonNull SqlJoinableQuery<E> fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	/**
	 * Adds a cross join with the given table.<br>
	 *
	 * @param table The table to join with
	 * @return A new joinable query with the cross join applied
	 * @throws NullPointerException If the table is null
	 */
	@NonNull SqlJoinableQuery<E> crossJoin(@NonNull SqlTable<?> table);
}
