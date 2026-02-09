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

/**
 * Interface representing a SQL select query with column projection.<br>
 * <p>
 *     This interface is used when selecting specific columns rather than full entities,
 *     returning results as {@code Row2}, {@code Row3}, etc. or single column values.<br>
 * </p>
 * <p>
 *     All common query functionality is inherited from {@link SqlSelectQueryBase}.<br>
 *     Unlike {@link SqlSelectQuery}, this interface does not support row-level locking
 *     as projection queries are typically read-only operations.<br>
 * </p>
 *
 * @param <T> The type of the projection result (e.g., Row2, Row3, or single column type)
 * @author Luis-St
 *
 * @see SqlSelectQueryBase
 * @see SqlSelectQuery
 */
public interface SqlSelectProjectionQuery<T> extends SqlSelectQueryBase<T, SqlSelectProjectionQuery<T>> {

}
