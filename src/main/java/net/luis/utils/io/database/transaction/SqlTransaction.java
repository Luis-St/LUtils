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

package net.luis.utils.io.database.transaction;

import net.luis.utils.io.database.SqlIsolationLevel;
import org.jspecify.annotations.NonNull;

import java.time.Duration;

/**
 * Interface representing a SQL transaction.<br>
 *
 * @author Luis-St
 */
public interface SqlTransaction {

	static boolean isInTransaction() {
		throw new UnsupportedOperationException();
	}

	static @NonNull SqlTransaction current() {
		throw new UnsupportedOperationException();
	}

	static void requireActive() {
		throw new UnsupportedOperationException();
	}

	void commit();

	void rollback();

	void rollbackTo(@NonNull SqlSavepoint savepoint);

	@NonNull SqlSavepoint savepoint(@NonNull String name);

	@NonNull SqlTransaction readOnly();

	@NonNull SqlTransaction timeout(@NonNull Duration timeout);

	@NonNull SqlTransaction isolation(@NonNull SqlIsolationLevel level);
}
