/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.collection.util;

import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A simple implementation of the {@link Table.Cell} interface.<br>
 *
 * @author Luis-St
 *
 * @param getRowKey The row key of the cell
 * @param getColumnKey The column key of the cell
 * @param getValue The value of the cell
 * @param <R> The type of the row key
 * @param <C> The type of the column key
 * @param <V> The type of the value
 */
public record SimpleCell<R, C, V>(@NotNull R getRowKey, @NotNull C getColumnKey, @Nullable V getValue) implements Table.Cell<R, C, V> {
	
	/**
	 * Constructs a new {@link SimpleCell} with the specified row key, column key and value.<br>
	 * @param getRowKey The row key of the cell
	 * @param getColumnKey The column key of the cell
	 * @param getValue The value of the cell
	 * @throws NullPointerException if the row key or column key is null
	 */
	public SimpleCell {
		Objects.requireNonNull(getRowKey, "Row key must not be null");
		Objects.requireNonNull(getColumnKey, "Column key must not be null");
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SimpleCell<?, ?, ?> that)) return false;
		
		if (!this.getRowKey.equals(that.getRowKey)) return false;
		if (!this.getColumnKey.equals(that.getColumnKey)) return false;
		return Objects.equals(this.getValue, that.getValue);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.getRowKey, this.getColumnKey, this.getValue);
	}
	
	@Override
	public String toString() {
		return "(" + this.getRowKey + "," + this.getColumnKey + ")=" + this.getValue;
	}
	//endregion
}
