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
 *
 * @author Luis-St
 *
 */

public class SqlMigrationColumnAlter<C> {
	
	private final List<SqlColumnAlteration> alterations = Lists.newArrayList();
	
	SqlMigrationColumnAlter() {}
	
	public @NonNull SqlMigrationColumnAlter<C> setType(@NonNull SqlType<C> type) {
		this.alterations.add(new SetTypeAlteration(type));
		return this;
	}
	
	public @NonNull SqlMigrationColumnAlter<C> setNullable(boolean nullable) {
		this.alterations.add(new SetNullableAlteration(nullable));
		return this;
	}
	
	public @NonNull SqlMigrationColumnAlter<C> setDefault(@NonNull C value) {
		this.alterations.add(new SetDefaultAlteration(value));
		return this;
	}
	
	public @NonNull SqlMigrationColumnAlter<C> dropDefault() {
		this.alterations.add(new DropDefaultAlteration());
		return this;
	}
	
	@NonNull List<SqlColumnAlteration> getAlterations() {
		return List.copyOf(this.alterations);
	}
}
