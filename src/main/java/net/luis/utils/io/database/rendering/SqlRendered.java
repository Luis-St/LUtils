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

package net.luis.utils.io.database.rendering;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlRendered {
	
	static @NonNull SqlRendered of(@NonNull String sql) {
		Objects.requireNonNull(sql, "Sql statement must not be null");
		
		return new SimpleSqlRendered(List.of(sql), List.of());
	}
	
	@NonNull
	@Unmodifiable
	List<String> statements();
	
	@NonNull
	@Unmodifiable
	List<Object> parameters();
	
	@NonNull String sql();
}
