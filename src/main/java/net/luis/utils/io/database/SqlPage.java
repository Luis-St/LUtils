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
 *
 * @author Luis-St
 *
 */

public record SqlPage<T>(
	@NonNull @Unmodifiable List<T> content,
	int page,
	int pageSize,
	long totalElements
) {
	
	public SqlPage {
		Objects.requireNonNull(content, "Sql page content must not be null");
		
		if (content.isEmpty()) {
			throw new IllegalArgumentException("Sql page content must not be empty");
		}
		if (page < 0) {
			throw new IllegalArgumentException("Sql page index must be non-negative");
		}
		if (pageSize <= 0) {
			throw new IllegalArgumentException("Sql page size must be positive");
		}
		if (totalElements < 0) {
			throw new IllegalArgumentException("Total elements must be non-negative");
		}
		
		content = List.copyOf(content);
	}
	
	public int totalPages() {
		return (int) Math.ceil((double) this.totalElements / this.pageSize);
	}
	
	public boolean hasNext() {
		return this.page < this.totalPages() - 1;
	}
	
	public boolean hasPrevious() {
		return this.page > 0;
	}
}
