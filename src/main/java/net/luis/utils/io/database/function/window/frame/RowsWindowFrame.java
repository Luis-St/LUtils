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

package net.luis.utils.io.database.function.window.frame;

import net.luis.utils.io.database.function.window.SqlFrameBound;
import net.luis.utils.io.database.function.window.SqlWindowFrame;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record RowsWindowFrame(
	@NonNull SqlFrameBound start,
	@NonNull SqlFrameBound end
) implements SqlWindowFrame {
	
	public RowsWindowFrame {
		Objects.requireNonNull(start, "Sql start frame bound must not be null");
		Objects.requireNonNull(end, "Sql end frame bound must not be null");
	}
}
