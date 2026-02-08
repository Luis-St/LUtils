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

import net.luis.utils.io.database.dialect.postgres.SqlPostgresColumn;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.time.Instant;

/**
 * Interface representing a TimescaleDB-specific column.<br>
 * TimescaleDB is built on PostgreSQL, so this extends {@link SqlPostgresColumn}.<br>
 *
 * @param <T> The type of the column value
 * @author Luis-St
 */
public interface SqlTimescaleColumn<T> extends SqlPostgresColumn<T> {

	@NonNull SqlExpression<?> timeBucket(@NonNull String interval);

	@NonNull SqlExpression<?> timeBucketGapfill(@NonNull String interval, @NonNull Instant start, @NonNull Instant end);

	@NonNull SqlExpression<?> first(@NonNull SqlColumn<?> timeColumn);

	@NonNull SqlExpression<?> last(@NonNull SqlColumn<?> timeColumn);

	@NonNull SqlExpression<?> locf();
}
