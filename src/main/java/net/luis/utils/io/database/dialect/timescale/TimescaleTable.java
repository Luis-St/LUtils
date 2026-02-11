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

package net.luis.utils.io.database.dialect.timescale;

import net.luis.utils.io.database.dialect.postgres.PostgresTable;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.time.Duration;

/**
 * Interface representing a TimescaleDB-specific table.<br>
 * TimescaleDB is built on PostgreSQL, so this extends {@link PostgresTable}.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface TimescaleTable<T> extends PostgresTable<T> {
	
	/**
	 * Converts this table into a TimescaleDB hypertable partitioned by the specified time column.<br>
	 * Generates SQL: {@code SELECT create_hypertable('table', 'timeColumn')}.
	 *
	 * @param timeColumn The column to use as the time dimension for partitioning
	 */
	void createHypertable(@NonNull SqlColumn<?> timeColumn);
	
	/**
	 * Converts this table into a TimescaleDB hypertable with a custom chunk interval.<br>
	 * Generates SQL: {@code SELECT create_hypertable('table', 'timeColumn', chunk_time_interval => 'interval')}.<br>
	 * <p>
	 *     The chunk interval determines the size of each time-based partition. Smaller intervals<br>
	 *     create more chunks but allow finer-grained data management and better cache utilization.
	 * </p>
	 *
	 * @param timeColumn The column to use as the time dimension for partitioning
	 * @param chunkInterval The time interval for each chunk
	 */
	void createHypertable(@NonNull SqlColumn<?> timeColumn, @NonNull Duration chunkInterval);
	
	/**
	 * Changes the chunk time interval for this hypertable.<br>
	 * Generates SQL: {@code SELECT set_chunk_time_interval('table', 'interval')}.
	 *
	 * @param interval The new chunk time interval
	 */
	void setChunkInterval(@NonNull Duration interval);
	
	/**
	 * Enables native compression on this hypertable.<br>
	 * Generates SQL: {@code ALTER TABLE ... SET (timescaledb.compress)}.
	 */
	void enableCompression();
	
	/**
	 * Adds an automatic compression policy to this hypertable.<br>
	 * Generates SQL: {@code SELECT add_compression_policy('table', 'compressAfter')}.<br>
	 * <p>
	 *     The policy automatically compresses chunks that are older than the specified duration.<br>
	 *     Compression must be enabled on the hypertable before adding a compression policy.
	 * </p>
	 *
	 * @param compressAfter The age after which chunks are automatically compressed
	 */
	void addCompressionPolicy(@NonNull Duration compressAfter);
	
	/**
	 * Adds a data retention policy to this hypertable.<br>
	 * Generates SQL: {@code SELECT add_retention_policy('table', 'dropAfter')}.<br>
	 * <p>
	 *     The policy automatically drops chunks that are older than the specified duration,<br>
	 *     providing automated data lifecycle management for time-series data.
	 * </p>
	 *
	 * @param dropAfter The age after which chunks are automatically dropped
	 */
	void addRetentionPolicy(@NonNull Duration dropAfter);
	
	/**
	 * Adds a refresh policy for a continuous aggregate on this hypertable.<br>
	 * Generates SQL: {@code SELECT add_continuous_aggregate_policy('table', 'startOffset', 'endOffset', 'scheduleInterval')}.<br>
	 * <p>
	 *     Continuous aggregates are materialized views that are automatically refreshed<br>
	 *     by a background worker. The policy defines the window of data to refresh<br>
	 *     and how frequently the refresh occurs.
	 * </p>
	 *
	 * @param startOffset The start of the refresh window relative to the current time
	 * @param endOffset The end of the refresh window relative to the current time
	 * @param scheduleInterval The interval between policy executions
	 */
	void addContinuousAggregatePolicy(@NonNull Duration startOffset, @NonNull Duration endOffset, @NonNull Duration scheduleInterval);
}
