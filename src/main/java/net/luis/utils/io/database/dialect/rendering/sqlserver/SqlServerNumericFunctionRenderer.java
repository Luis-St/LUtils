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

package net.luis.utils.io.database.dialect.rendering.sqlserver;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.rendering.base.function.SqlNumericFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.numeric.SqlNumericTruncateFunction;
import net.luis.utils.io.database.function.functions.numeric.SqlRandomFunction;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public class SqlServerNumericFunctionRenderer extends SqlNumericFunctionRenderer {
	
	public SqlServerNumericFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderRandom(@NonNull SqlRandomFunction function) throws SqlException {
		return SqlRendered.of("RAND()");
	}
	
	@Override
	protected @NonNull SqlRendered renderTruncate(@NonNull SqlNumericTruncateFunction<?> function) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("ROUND").openingBracket();
		renderer.rendered(function.expression().toSql(this.dialect)).comma();
		renderer.rendered(new SqlValueExpression<>(0, SqlTypes.INTEGER).toSql(this.dialect)).comma();
		renderer.rendered(new SqlValueExpression<>(1, SqlTypes.INTEGER).toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
}
