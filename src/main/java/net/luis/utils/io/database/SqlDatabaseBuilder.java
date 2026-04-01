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

package net.luis.utils.io.database;

import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlDatabaseBuilder {
	
	private final DataSource dataSource;
	
	public SqlDatabaseBuilder(@NonNull DataSource dataSource) {
		this.dataSource = Objects.requireNonNull(dataSource, "Data source must not be null");
	}
	
	public @NonNull SqlDatabase build() {
		return new SqlDatabase(this.dataSource);
	}
}
