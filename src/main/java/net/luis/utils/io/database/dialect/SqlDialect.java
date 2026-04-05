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

package net.luis.utils.io.database.dialect;

import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedFunctionException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedTypeException;
import net.luis.utils.io.database.function.SqlFunctionType;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlDialect {
	
	@NonNull String name();
	
	boolean isTypeSupported(@NonNull SqlType<?> type);
	
	@NonNull String getTypeName(@NonNull SqlType<?> type) throws SqlDialectUnsupportedTypeException;
	
	boolean isFunctionSupported(@NonNull SqlFunctionType type);
	
	@NonNull SqlRendered renderFunction(@NonNull SqlFunctionType type, @NonNull List<SqlRendered> arguments) throws SqlDialectUnsupportedFunctionException;
}
