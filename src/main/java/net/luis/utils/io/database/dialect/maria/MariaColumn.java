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

package net.luis.utils.io.database.dialect.maria;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.mysql.MysqlColumn;
import net.luis.utils.io.database.function.SqlExpression;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a MariaDB-specific column.<br>
 * Extends MySQL column with MariaDB additions.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the column value
 */
public interface MariaColumn<T> extends MysqlColumn<T> {
	
	@NonNull SqlCondition ilike(@NonNull String pattern);
	
	@NonNull SqlExpression<?> jsonValue(@NonNull String path);
	
	@NonNull SqlExpression<?> nextSequenceValue(@NonNull String sequence);
}
