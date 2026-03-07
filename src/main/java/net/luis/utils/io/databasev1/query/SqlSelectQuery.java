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

package net.luis.utils.io.databasev1.query;

/**
 * Interface representing a SQL select query for entities.<br>
 * Extends {@link SqlSelectQueryBase} with pessimistic row-level locking via {@link SqlLockableQuery}.<br>
 *
 * @see SqlSelectQueryBase
 * @see SqlLockableQuery
 * @see SqlSelectProjectionQuery
 *
 * @author Luis-St
 *
 * @param <T> The type of the result entity
 */
public interface SqlSelectQuery<T> extends SqlSelectQueryBase<T, SqlSelectQuery<T>>, SqlLockableQuery<SqlSelectQuery<T>> {}

