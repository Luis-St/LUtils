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

package net.luis.utils.io.database.query;

import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 *
 * @author Luis-St
 *
 */

public class SqlAlias implements Supplier<String> {
	
	private final String alias;
	
	SqlAlias(@NonNull String alias) {
		this.alias = Objects.requireNonNull(alias, "Alias must not be null");
	}
	
	public static @NonNull SqlAlias of(@NonNull String alias) {
		return new SqlAlias(alias);
	}
	
	@Override
	public @NonNull String get() {
		return this.alias;
	}
	
	//region Object overrides
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SqlAlias sqlAlias)) return false;
		
		return this.alias.equals(sqlAlias.alias);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.alias);
	}
	
	@Override
	public String toString() {
		return this.alias;
	}
	//endregion
}
