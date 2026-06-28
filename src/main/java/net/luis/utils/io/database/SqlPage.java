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

package net.luis.utils.io.database;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Represents a single page of results obtained from a paginated sql query.<br>
 * A page carries the elements it contains together with the metadata required to navigate between pages.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the elements contained in the page
 */
public record SqlPage<T>(
	@NonNull @Unmodifiable List<T> content,
	int page,
	int pageSize,
	boolean hasNext,
	boolean hasPrevious
) {
	
	/**
	 * Constructs a new sql page with the given content and pagination metadata.<br>
	 * The given content is copied into an unmodifiable list.<br>
	 *
	 * @param content The elements contained in this page
	 * @param page The zero-based index of this page
	 * @param pageSize The maximum number of elements per page
	 * @param hasNext Whether a following page exists
	 * @param hasPrevious Whether a preceding page exists
	 * @throws NullPointerException If the content is null
	 * @throws IllegalArgumentException If the page index is negative or the page size is not positive
	 */
	public SqlPage {
		Objects.requireNonNull(content, "Sql page content must not be null");
		
		if (page < 0) {
			throw new IllegalArgumentException("Sql page index must be non-negative");
		}
		if (pageSize <= 0) {
			throw new IllegalArgumentException("Sql page size must be positive");
		}
		
		content = List.copyOf(content);
	}
}
