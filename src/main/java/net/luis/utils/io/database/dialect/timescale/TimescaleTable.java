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
	
	void createHypertable(@NonNull SqlColumn<?> timeColumn);
	
	void createHypertable(@NonNull SqlColumn<?> timeColumn, @NonNull Duration chunkInterval);
	
	void setChunkInterval(@NonNull Duration interval);
	
	void enableCompression();
	
	void addCompressionPolicy(@NonNull Duration compressAfter);
	
	void addRetentionPolicy(@NonNull Duration dropAfter);
	
	void addContinuousAggregatePolicy(@NonNull Duration startOffset, @NonNull Duration endOffset, @NonNull Duration scheduleInterval);
}
