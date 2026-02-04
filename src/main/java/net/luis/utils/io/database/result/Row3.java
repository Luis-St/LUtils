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

package net.luis.utils.io.database.result;

import org.jspecify.annotations.NonNull;

/**
 * Interface representing a row with three columns.<br>
 *
 * @param <A> The type of the first column
 * @param <B> The type of the second column
 * @param <C> The type of the third column
 * @author Luis-St
 */
public interface Row3<A, B, C> {

	@NonNull A first();

	@NonNull B second();

	@NonNull C third();
}
