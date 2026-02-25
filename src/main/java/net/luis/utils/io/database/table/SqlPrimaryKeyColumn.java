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

package net.luis.utils.io.database.table;

/**
 * Marker interface for a primary key column.<br>
 * <p>
 *     Columns implementing this interface are recognized by query builders
 *     for identity-based operations such as {@code findById()} and upsert logic.<br>
 * </p>
 *
 * @see SqlColumn
 *
 * @author Luis-St
 *
 * @param <T> The entity type (for table-level type safety)
 * @param <V> The primary key value type (e.g., {@link Long}, {@link java.util.UUID})
 */
public interface SqlPrimaryKeyColumn<T, V> extends SqlColumn<V> {}

