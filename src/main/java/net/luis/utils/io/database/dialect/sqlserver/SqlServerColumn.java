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

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL Server-specific column.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the column value
 */
public interface SqlServerColumn<T> extends SqlColumn<T> {
	
	@NonNull SqlCondition freeText(@NonNull String searchTerms);
	
	@NonNull SqlCondition containsText(@NonNull String searchTerms);
	
	@NonNull SqlExpression<?> jsonValue(@NonNull String path);
	
	@NonNull SqlCondition isJsonPath(@NonNull String path);
	
	@NonNull SqlExpression<?> isNull(@NonNull T defaultValue);
	
	@NonNull SqlExpression<?> tryConvert(@NonNull Class<?> targetType);
	
	@NonNull SqlExpression<?> formatDate(@NonNull String format);
	
	@NonNull SqlExpression<?> stringAgg(@NonNull String separator);
}
