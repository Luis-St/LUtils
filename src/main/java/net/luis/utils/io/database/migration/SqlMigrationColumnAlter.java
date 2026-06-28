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

package net.luis.utils.io.database.migration;

import com.google.common.collect.Lists;
import net.luis.utils.io.database.migration.operation.*;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Fluent column-alteration builder used to describe changes to an existing column during a migration.<br>
 * It accumulates {@link SqlColumnAlteration}s such as type, nullability and default value changes.<br>
 *
 * @author Luis-St
 *
 * @param <C> The value type of the column
 */

public class SqlMigrationColumnAlter<C> {
	
	/**
	 * The accumulated column alterations.
	 */
	private final List<SqlColumnAlteration> alterations = Lists.newArrayList();
	
	/**
	 * Constructs a new sql migration column alter builder.<br>
	 */
	SqlMigrationColumnAlter() {}
	
	/**
	 * Adds an alteration that changes the type of the column.<br>
	 *
	 * @param type The new sql type of the column
	 * @return This builder
	 */
	public @NonNull SqlMigrationColumnAlter<C> setType(@NonNull SqlType<C> type) {
		this.alterations.add(new SqlSetTypeAlteration(type));
		return this;
	}
	
	/**
	 * Adds an alteration that changes the nullability of the column.<br>
	 *
	 * @param nullable Whether the column should be nullable
	 * @return This builder
	 */
	public @NonNull SqlMigrationColumnAlter<C> setNullable(boolean nullable) {
		this.alterations.add(new SqlSetNullableAlteration(nullable));
		return this;
	}
	
	/**
	 * Adds an alteration that sets the default value of the column.<br>
	 *
	 * @param value The new default value
	 * @return This builder
	 */
	public @NonNull SqlMigrationColumnAlter<C> setDefault(@NonNull C value) {
		this.alterations.add(new SqlSetDefaultAlteration(value));
		return this;
	}
	
	/**
	 * Adds an alteration that drops the default value of the column.<br>
	 * @return This builder
	 */
	public @NonNull SqlMigrationColumnAlter<C> dropDefault() {
		this.alterations.add(new SqlDropDefaultAlteration());
		return this;
	}
	
	/**
	 * Returns the alterations accumulated by this builder.<br>
	 * @return An immutable copy of the column alterations
	 */
	@NonNull List<SqlColumnAlteration> getAlterations() {
		return List.copyOf(this.alterations);
	}
}
