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

package net.luis.utils.io.database.function.window;

import net.luis.utils.io.database.expression.orderable.SqlOrderable;
import net.luis.utils.io.database.rendering.SqlRenderable;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlWindowClause extends SqlRenderable {
	
	static @NonNull SqlWindowClause of() {
		return null;
	}
	
	static @NonNull SqlWindowClause partitionBy(SqlColumn<?, ?> @NonNull ... columns) {
		return null;
	}
	
	@NonNull SqlWindowClause orderBy(SqlOrderable<?> @NonNull ... orderables);
	
	@NonNull SqlWindowClause frame(@NonNull SqlWindowFrame frame);
	
	@NonNull SqlWindowClause rows(@NonNull SqlFrameBound start, @NonNull SqlFrameBound end);
	
	@NonNull SqlWindowClause range(@NonNull SqlFrameBound start, @NonNull SqlFrameBound end);
	
	@NonNull SqlWindowClause groups(@NonNull SqlFrameBound start, @NonNull SqlFrameBound end);
}
