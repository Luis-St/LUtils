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

package net.luis.utils.io.database.dialect.postgres;

import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a PostgreSQL-specific table.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface PostgresTable<T> extends SqlTable<T> {
	
	@Override
	@NonNull PostgresSelectQuery<T> select();
	
	/**
	 * Sets whether this table should be unlogged.<br>
	 * Generates SQL: {@code ALTER TABLE ... SET UNLOGGED} or {@code ALTER TABLE ... SET LOGGED}.<br>
	 * <p>
	 *     Unlogged tables are not written to the write-ahead log, which makes them considerably faster<br>
	 *     but not crash-safe. Data in unlogged tables is automatically truncated after a crash or unclean shutdown.<br>
	 * </p>
	 *
	 * @param unlogged Whether the table should be unlogged
	 */
	void setUnlogged(boolean unlogged);
	
	/**
	 * Moves this table to the specified tablespace.<br>
	 * Generates SQL: {@code ALTER TABLE ... SET TABLESPACE tablespace}.<br>
	 * <p>
	 *     A tablespace allows control over the disk layout of a database.<br>
	 *     Tables can be moved between tablespaces after creation.<br>
	 * </p>
	 *
	 * @param tablespace The name of the target tablespace
	 */
	void setTablespace(@NonNull String tablespace);
	
	/**
	 * Configures range partitioning on this table using the specified columns.<br>
	 * Generates SQL: {@code PARTITION BY RANGE (column1, column2, ...)}.<br>
	 * <p>
	 *     Range partitioning divides the table into partitions based on ranges of values<br>
	 *     in the specified columns. This is commonly used for date or numeric ranges.<br>
	 * </p>
	 *
	 * @param columns The columns to partition by
	 */
	void partitionByRange(SqlColumn<?> @NonNull ... columns);
	
	/**
	 * Configures list partitioning on this table using the specified column.<br>
	 * Generates SQL: {@code PARTITION BY LIST (column)}.<br>
	 * <p>
	 *     List partitioning divides the table into partitions based on explicit lists<br>
	 *     of values in the specified column. Each partition holds rows matching a specific set of values.<br>
	 * </p>
	 *
	 * @param column The column to partition by
	 */
	void partitionByList(@NonNull SqlColumn<?> column);
	
	/**
	 * Configures hash partitioning on this table using the specified columns.<br>
	 * Generates SQL: {@code PARTITION BY HASH (column1, column2, ...)}.<br>
	 * <p>
	 *     Hash partitioning divides the table into partitions based on a hash of the specified columns.<br>
	 *     This distributes data evenly across partitions and is useful for load balancing.<br>
	 * </p>
	 *
	 * @param columns The columns to partition by
	 */
	void partitionByHash(SqlColumn<?> @NonNull ... columns);
	
	/**
	 * Enables row-level security on this table.<br>
	 * Generates SQL: {@code ALTER TABLE ... ENABLE ROW LEVEL SECURITY}.<br>
	 * <p>
	 *     Row-level security restricts which rows a user can access based on policies.<br>
	 *     After enabling, policies must be created to define access rules.<br>
	 * </p>
	 */
	void enableRowLevelSecurity();
}
