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

package net.luis.utils.io.database.query;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.SqlOrderable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Interface representing a SQL select query with projection.<br>
 *
 * @param <T> The type of the projection result
 * @author Luis-St
 */
public interface SqlSelectProjectionQuery<T> {

	@NonNull SqlSelectProjectionQuery<T> where(@NonNull SqlCondition condition);

	@NonNull SqlSelectProjectionQuery<T> orderBy(SqlOrderable @NonNull ... orderables);

	@NonNull SqlSelectProjectionQuery<T> limit(int limit);

	@NonNull SqlSelectProjectionQuery<T> offset(long offset);

	@NonNull List<T> fetch();

	@NonNull Optional<T> fetchFirst();

	@NonNull T fetchOne();

	long count();

	@NonNull Stream<T> stream();

	@NonNull String toSql();
}
