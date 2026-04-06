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

package net.luis.utils.io.database.type.parameter;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlPrecisionParameter implements SqlParameter {
	
	private final int precision;
	private final int scale;
	
	SqlPrecisionParameter(int precision, int scale) {
		if (precision < 0) {
			throw new IllegalArgumentException("Precision must be non-negative");
		}
		if (scale < 0) {
			throw new IllegalArgumentException("Scale must be non-negative");
		}
		this.precision = precision;
		this.scale = scale;
	}
	
	public int precision() {
		return this.precision;
	}
	
	public int scale() {
		return this.scale;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SqlPrecisionParameter that)) return false;
		
		if (this.precision != that.precision) return false;
		return this.scale == that.scale;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.precision, this.scale);
	}
	
	@Override
	public @NonNull String toString() {
		return "SqlPrecisionParameter[precision=" + this.precision + ", scale=" + this.scale + "]";
	}
	//endregion
}
