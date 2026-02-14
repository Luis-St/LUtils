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
 * Marker interface for a version column used in optimistic locking.<br>
 * <p>
 *     Extends {@link SqlColumn} so it can be used in all column contexts (conditions, increments, etc.).<br>
 *     Query builders detect this type via {@code instanceof} to apply automatic version checking.
 * </p>
 *
 * @see SqlColumn
 * @see SqlVersioned
 *
 * @author Luis-St
 *
 * @param <T> The entity type (for table-level type safety)
 * @param <V> The version value type (e.g., {@link Long} or {@link Integer})
 */
public interface SqlVersionColumn<T, V> extends SqlColumn<V> {}
