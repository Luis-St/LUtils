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

import net.luis.utils.io.databasev1.renderer.SqlRenderer;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface for SQL elements that can be rendered to a dialect-specific SQL string.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface SqlRenderable {
	
	/**
	 * Generates the SQL representation of this element using the given renderer.<br>
	 *
	 * @param renderer The SQL renderer to generate SQL with
	 * @return The rendered SQL string
	 */
	@NonNull String toSql(@NonNull SqlRenderer renderer);
	
	/**
	 * Returns the bound parameter values for this element in the order they appear in the rendered SQL.<br>
	 * Elements that do not bind values (e.g. static conditions like {@code IS NULL}) return an empty list.<br>
	 *
	 * @return An immutable list of bound parameter values
	 */
	default @NonNull List<Object> getParameters() {
		return List.of();
	}
	
	/**
	 * Generates the SQL representation along with bound parameter values.<br>
	 *
	 * @param renderer The SQL renderer to generate SQL with
	 * @return A {@link SqlRendered} record containing the SQL string and bound parameters
	 */
	default @NonNull SqlRendered toRendered(@NonNull SqlRenderer renderer) {
		return new SqlRendered(this.toSql(renderer), this.getParameters());
	}
}
