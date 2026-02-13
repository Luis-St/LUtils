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

package net.luis.utils.io.database.function.window;

import net.luis.utils.io.database.SqlRenderable;
import net.luis.utils.io.database.condition.SqlOrderable;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a full SQL window specification for use in {@code OVER(...)} clauses.<br>
 * Supports partition, ordering, and frame specifications with a fluent chaining API.<br>
 * <p>
 * Use static factory methods as entry points, then chain instance methods to build the specification:
 * <pre>{@code
 * SqlWindowClause.partitionBy(col).orderBy(col.desc()).rows(start, end)
 * }</pre>
 *
 * @author Luis-St
 */
public interface SqlWindowClause extends SqlRenderable {
	
	/**
	 * Creates an empty window clause.<br>
	 * Generates SQL: {@code OVER()}.<br>
	 *
	 * @return An empty window clause
	 */
	static @NonNull SqlWindowClause of() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a window clause with the given partition columns.<br>
	 * Generates SQL: {@code PARTITION BY col1, col2, ...}.<br>
	 *
	 * @param columns The columns to partition by
	 * @return A window clause with partition specification
	 */
	static @NonNull SqlWindowClause partitionBy(SqlColumn<?> @NonNull ... columns) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Adds ordering to this window clause.<br>
	 * Generates SQL: {@code ORDER BY col1 ASC, col2 DESC, ...}.<br>
	 *
	 * @param orderables The orderable expressions
	 * @return This window clause with order specification added
	 */
	@NonNull SqlWindowClause orderBy(SqlOrderable @NonNull ... orderables);
	
	/**
	 * Sets the frame specification for this window clause.<br>
	 *
	 * @param frame The window frame specification
	 * @return This window clause with frame specification added
	 */
	@NonNull SqlWindowClause frame(@NonNull SqlWindowFrame frame);
	
	/**
	 * Sets a row-based frame specification for this window clause.<br>
	 * Shortcut for {@code frame(SqlWindowFrame.rows(start, end))}.<br>
	 * Generates SQL: {@code ROWS BETWEEN start AND end}.<br>
	 *
	 * @param start The start bound of the frame
	 * @param end The end bound of the frame
	 * @return This window clause with rows frame specification added
	 */
	@NonNull SqlWindowClause rows(@NonNull SqlFrameBound start, @NonNull SqlFrameBound end);
	
	/**
	 * Sets a range-based frame specification for this window clause.<br>
	 * Shortcut for {@code frame(SqlWindowFrame.range(start, end))}.<br>
	 * Generates SQL: {@code RANGE BETWEEN start AND end}.<br>
	 *
	 * @param start The start bound of the frame
	 * @param end The end bound of the frame
	 * @return This window clause with range frame specification added
	 */
	@NonNull SqlWindowClause range(@NonNull SqlFrameBound start, @NonNull SqlFrameBound end);
	
	/**
	 * Sets a groups-based frame specification for this window clause.<br>
	 * Shortcut for {@code frame(SqlWindowFrame.groups(start, end))}.<br>
	 * Generates SQL: {@code GROUPS BETWEEN start AND end}.<br>
	 *
	 * @param start The start bound of the frame
	 * @param end The end bound of the frame
	 * @return This window clause with groups frame specification added
	 */
	@NonNull SqlWindowClause groups(@NonNull SqlFrameBound start, @NonNull SqlFrameBound end);
}
