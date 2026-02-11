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

package net.luis.utils.io.database.dialect.sqlserver;

import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL Server-specific table.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface SqlServerTable<T> extends SqlTable<T> {
	
	/**
	 * Sets the filegroup for this table.<br>
	 * Generates SQL: {@code ON filegroup}.<br>
	 * <p>
	 *     A filegroup is a logical storage unit in SQL Server that maps to one or more<br>
	 *     physical files on disk. Placing tables on specific filegroups can improve performance and manageability.
	 * </p>
	 *
	 * @param filegroup The name of the target filegroup
	 */
	void setFilegroup(@NonNull String filegroup);
	
	/**
	 * Creates a clustered index on the specified columns.<br>
	 * Generates SQL: {@code CREATE CLUSTERED INDEX ... ON table (column1, column2, ...)}.<br>
	 * <p>
	 *     A clustered index determines the physical order of data in a table.<br>
	 *     Each table can have only one clustered index. Choosing the right columns<br>
	 *     for the clustered index is critical for query performance.
	 * </p>
	 *
	 * @param columns The columns to include in the clustered index
	 */
	void setClusteredIndex(@NonNull SqlColumn<?>... columns);
	
	/**
	 * Enables change tracking on this table.<br>
	 * Generates SQL: {@code ALTER TABLE ... ENABLE CHANGE_TRACKING}.
	 * @see <a href="https://learn.microsoft.com/en-us/sql/relational-databases/track-changes/about-change-tracking">Change Tracking</a>
	 */
	void enableChangeTracking();
	
	/**
	 * Enables system-versioned temporal table support with the specified history table.<br>
	 * Generates SQL: {@code ALTER TABLE ... SET (SYSTEM_VERSIONING = ON (HISTORY_TABLE = historyTable))}.<br>
	 * <p>
	 *     Temporal tables automatically track the full history of data changes by maintaining<br>
	 *     a paired history table. This allows querying data as it existed at any point in time.
	 * </p>
	 *
	 * @param historyTable The name of the history table to store row versions
	 */
	void enableTemporalTable(@NonNull String historyTable);
	
	/**
	 * Applies the {@code WITH (NOLOCK)} table hint to queries on this table.<br>
	 * Generates SQL: {@code SELECT ... FROM table WITH (NOLOCK)}.<br>
	 * <p>
	 *     The {@code NOLOCK} hint allows reading uncommitted data without acquiring shared locks.<br>
	 *     This can improve read performance but may result in dirty reads.
	 * </p>
	 */
	void withNoLock();
	
	/**
	 * Creates a columnstore index on the specified columns.<br>
	 * Generates SQL: {@code CREATE NONCLUSTERED COLUMNSTORE INDEX name ON table (column1, column2, ...)}.<br>
	 * <p>
	 *     Columnstore indexes store and query data in a columnar format, providing significant<br>
	 *     compression and performance improvements for analytical queries over large datasets.
	 * </p>
	 *
	 * @param name The name of the columnstore index
	 * @param columns The columns to include in the columnstore index
	 */
	void addColumnStoreIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns);
}
