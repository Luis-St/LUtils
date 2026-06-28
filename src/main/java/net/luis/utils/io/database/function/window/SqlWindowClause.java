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

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.orderable.SqlOrderable;
import net.luis.utils.io.database.rendering.SqlRenderable;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Represents the {@code OVER(...)} clause of a sql window function.<br>
 * A window clause is composed of an optional {@code PARTITION BY}, an optional {@code ORDER BY} and an optional window frame.<br>
 *
 * @author Luis-St
 *
 * @param partitions The columns the rows are partitioned by
 * @param orderings The orderings applied within each partition
 * @param frame The window frame restricting the rows considered, or {@code null} if no frame is set
 */
public record SqlWindowClause(
	@NonNull @Unmodifiable List<SqlColumn<?, ?>> partitions,
	@NonNull @Unmodifiable List<SqlOrderable<?>> orderings,
	@Nullable SqlWindowFrame frame
) implements SqlRenderable {
	
	/**
	 * Creates an empty window clause without any partitioning, ordering or frame.<br>
	 * @return A new empty window clause
	 */
	public static @NonNull SqlWindowClause of() {
		return new SqlWindowClause(List.of(), List.of(), null);
	}
	
	/**
	 * Creates a window clause partitioned by the given columns.<br>
	 *
	 * @param columns The columns to partition the rows by
	 * @return A new window clause partitioned by the given columns
	 * @throws NullPointerException If the columns array is null
	 */
	public static @NonNull SqlWindowClause partitionBy(SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Sql partition columns must not be null");
		return new SqlWindowClause(List.of(columns), List.of(), null);
	}
	
	/**
	 * Creates a copy of this window clause with the given orderings applied within each partition.<br>
	 *
	 * @param orderables The orderings to apply
	 * @return A new window clause with the given orderings
	 * @throws NullPointerException If the orderables array is null
	 */
	public @NonNull SqlWindowClause orderBy(SqlOrderable<?> @NonNull ... orderables) {
		Objects.requireNonNull(orderables, "Sql order by clauses must not be null");
		
		return new SqlWindowClause(this.partitions, List.of(orderables), this.frame);
	}
	
	/**
	 * Creates a copy of this window clause with the given window frame.<br>
	 *
	 * @param frame The window frame to apply
	 * @return A new window clause with the given frame
	 * @throws NullPointerException If the frame is null
	 */
	public @NonNull SqlWindowClause frame(@NonNull SqlWindowFrame frame) {
		Objects.requireNonNull(frame, "Sql window frame must not be null");
		return new SqlWindowClause(this.partitions, this.orderings, frame);
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return dialect.renderWindowClause(this);
	}
}
