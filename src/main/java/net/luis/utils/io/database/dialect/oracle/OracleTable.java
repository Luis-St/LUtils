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

package net.luis.utils.io.database.dialect.oracle;

import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing an Oracle-specific table.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface OracleTable<T> extends SqlTable<T> {
	
	/**
	 * Assigns this table to the specified tablespace.<br>
	 * Generates SQL: {@code TABLESPACE tablespace}.<br>
	 * <p>
	 *     A tablespace is a logical storage unit in Oracle that groups related data files.<br>
	 *     Moving tables to specific tablespaces allows fine-grained control over storage allocation and I/O distribution.
	 * </p>
	 *
	 * @param tablespace The name of the target tablespace
	 */
	void setTablespace(@NonNull String tablespace);
	
	/**
	 * Enables row movement on this table.<br>
	 * Generates SQL: {@code ALTER TABLE ... ENABLE ROW MOVEMENT}.<br>
	 * <p>
	 *     Row movement allows Oracle to move rows between partitions when the partition key column is updated.<br>
	 *     It is also required for certain operations such as flashback table and shrink space.
	 * </p>
	 */
	void enableRowMovement();
	
	/**
	 * Configures range partitioning on this table using the specified column.<br>
	 * Generates SQL: {@code PARTITION BY RANGE (column)}.<br>
	 * <p>
	 *     Range partitioning divides the table into partitions based on ranges of values in the specified column.<br>
	 *     This is commonly used for date-based or numeric ranges.
	 * </p>
	 *
	 * @param column The column to partition by
	 */
	void partitionByRange(@NonNull SqlColumn<?> column);
	
	/**
	 * Configures list partitioning on this table using the specified column.<br>
	 * Generates SQL: {@code PARTITION BY LIST (column)}.<br>
	 * <p>
	 *     List partitioning divides the table into partitions based on discrete lists of values in the specified column.<br>
	 *     This is useful for columns with a known set of distinct values.
	 * </p>
	 *
	 * @param column The column to partition by
	 */
	void partitionByList(@NonNull SqlColumn<?> column);
	
	/**
	 * Configures hash partitioning on this table using the specified column.<br>
	 * Generates SQL: {@code PARTITION BY HASH (column) PARTITIONS n}.<br>
	 * <p>
	 *     Hash partitioning distributes rows evenly across a specified number of partitions<br>
	 *     using a hash function on the partition key column. This provides uniform data<br>
	 *     distribution when there is no natural range or list partitioning strategy.
	 * </p>
	 *
	 * @param column The column to partition by
	 * @param partitions The number of hash partitions to create
	 */
	void partitionByHash(@NonNull SqlColumn<?> column, int partitions);
	
	/**
	 * Enables flashback on this table.<br>
	 * Generates SQL: {@code ALTER TABLE ... FLASHBACK ARCHIVE}.<br>
	 * <p>
	 *     Flashback allows querying past versions of data and restoring the table to a previous point in time.<br>
	 *     Row movement must be enabled before using flashback.
	 * </p>
	 */
	void enableFlashback();
	
	/**
	 * Sets the compression mode for this table.<br>
	 * Generates SQL: {@code ALTER TABLE ... COMPRESS ...} or {@code ALTER TABLE ... NOCOMPRESS}.<br>
	 * <p>
	 *     Table compression reduces storage requirements and can improve query performance by reducing I/O.<br>
	 *     Different compression levels offer trade-offs between storage savings and CPU overhead.
	 * </p>
	 *
	 * @param compression The compression mode to apply
	 */
	void setCompression(@NonNull OracleCompression compression);
	
	/**
	 * Creates a materialized view based on the specified query.<br>
	 * Generates SQL: {@code CREATE MATERIALIZED VIEW name AS query}.<br>
	 * <p>
	 *     A materialized view stores the result of a query physically, unlike a regular view.<br>
	 *     It can be refreshed periodically to reflect changes in the underlying tables, providing faster access to precomputed results.
	 * </p>
	 *
	 * @param name The name of the materialized view
	 * @param query The SQL query defining the materialized view
	 */
	void createMaterializedView(@NonNull String name, @NonNull String query);
}
