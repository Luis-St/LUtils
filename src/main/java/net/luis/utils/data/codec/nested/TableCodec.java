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

package net.luis.utils.data.codec.nested;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.luis.utils.data.codec.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class TableCodec<R, C, V> implements NestedCodec<Table<R, C, V>> {
	
	private final Codec<R> rowCodec;
	private final Codec<C> columnCodec;
	private final Codec<V> valueCodec;
	
	public TableCodec(@NotNull Codec<R> rowCodec, @NotNull Codec<C> columnCodec, @NotNull Codec<V> valueCodec) {
		this.rowCodec = Objects.requireNonNull(rowCodec, "Row codec must not be null");
		this.columnCodec = Objects.requireNonNull(columnCodec, "Column codec must not be null");
		this.valueCodec = Objects.requireNonNull(valueCodec, "Value codec must not be null");
	}
	
	@Override
	public <X> @NotNull ParserCache<X> isValid(@Nullable String value) {
		return null;
	}
	
	@Override
	public @NotNull <X> Table<R, C, V> decode(@NotNull ParserCache<X> cache) {
		return null;
	}
	
	@Override
	public @NotNull String encode(@NotNull Table<R, C, V> table) {
		Objects.requireNonNull(table, "Table must not be null");
		StringBuilder builder = new StringBuilder("{");
		for (R row : table.rowKeySet()) {
			builder.append(this.rowCodec.encode(row)).append("={");
			for (Map.Entry<C, V> columnEntry : table.row(row).entrySet()) {
				builder.append(this.columnCodec.encode(columnEntry.getKey())).append("=");
				builder.append(this.valueCodec.encode(columnEntry.getValue())).append(",");
			}
			builder.deleteCharAt(builder.length() - 1).append("},");
		}
		builder.deleteCharAt(builder.length() - 1).append("}");
		return builder.toString();
	}
	
	@Override
	public @NotNull Table<R, C, V> getDefaultValue() {
		return HashBasedTable.create();
	}
	
	@Override
	public boolean isNested() {
		return true;
	}
}
