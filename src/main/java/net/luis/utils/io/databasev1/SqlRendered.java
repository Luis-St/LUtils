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

package net.luis.utils.io.databasev1;

import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Record bundling a rendered SQL string together with its bound parameters.<br>
 * Used to represent a fully rendered SQL statement that is ready for execution, including all parameter values for prepared statements.<br>
 *
 * @author Luis-St
 *
 * @param sql The rendered SQL string
 * @param parameters The list of bound parameter values
 */
public record SqlRendered(@NonNull String sql, @NonNull List<Object> parameters) {}
