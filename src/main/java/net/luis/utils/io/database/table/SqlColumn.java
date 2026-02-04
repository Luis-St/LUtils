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

package net.luis.utils.io.database.table;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.SqlOrderable;
import net.luis.utils.io.database.query.SqlSelectQuery;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.List;

/**
 * Interface representing a SQL column.<br>
 *
 * @param <T> The type of the column value
 * @author Luis-St
 */
public interface SqlColumn<T> extends SqlOrderable {

	// Equality
	@NonNull SqlCondition equalTo(@NonNull T value);

	@NonNull SqlCondition notEqualTo(@NonNull T value);

	@NonNull SqlCondition equalToIgnoreCase(@NonNull String value);

	// Null
	@NonNull SqlCondition isNull();

	@NonNull SqlCondition isNotNull();

	// Comparison
	@NonNull SqlCondition greaterThan(@NonNull T value);

	@NonNull SqlCondition greaterThanOrEqualTo(@NonNull T value);

	@NonNull SqlCondition lessThan(@NonNull T value);

	@NonNull SqlCondition lessThanOrEqualTo(@NonNull T value);

	@NonNull SqlCondition between(@NonNull T start, @NonNull T end);

	@NonNull SqlCondition withinLast(@NonNull Duration duration);

	// String patterns
	@NonNull SqlCondition like(@NonNull String pattern);

	@NonNull SqlCondition startsWith(@NonNull String prefix);

	@NonNull SqlCondition contains(@NonNull String substring);

	@NonNull SqlCondition endsWith(@NonNull String suffix);

	@NonNull SqlCondition lengthGreaterThan(int length);

	// Collection
	@SuppressWarnings("unchecked")
	@NonNull SqlCondition in(@NonNull T... values);

	@NonNull SqlCondition in(@NonNull List<T> values);

	@NonNull SqlCondition in(@NonNull SqlSelectQuery<?> subquery);

	@SuppressWarnings("unchecked")
	@NonNull SqlCondition notIn(@NonNull T... values);

	// Ordering (SqlOrderable)
	@Override
	@NonNull SqlColumn<T> asc();

	@Override
	@NonNull SqlColumn<T> desc();

	@Override
	@NonNull SqlColumn<T> nullsFirst();

	@Override
	@NonNull SqlColumn<T> nullsLast();
}
