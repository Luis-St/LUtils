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

package net.luis.utils.io.database;

import net.luis.utils.io.database.audit.SqlTimestampSource;
import org.jspecify.annotations.NonNull;

import java.time.Clock;
import java.util.concurrent.Executor;

/**
 * Interface representing a SQL database configuration builder.<br>
 *
 * @author Luis-St
 */
public interface SqlDatabaseConfig {

	static @NonNull SqlDatabaseConfig builder() {
		throw new UnsupportedOperationException();
	}

	@NonNull SqlDatabaseConfig asyncExecutor(@NonNull Executor executor);

	@NonNull SqlDatabaseConfig asyncConnectionPoolSize(int size);

	@NonNull SqlDatabaseConfig auditTimestampSource(@NonNull SqlTimestampSource source);

	@NonNull SqlDatabaseConfig auditClock(@NonNull Clock clock);

	@NonNull SqlDatabaseConfig build();
}
