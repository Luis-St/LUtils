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

package net.luis.utils.io.database.integration.reflection;

import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.rendering.SqlRenderable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * One reflection-discovered operator instantiated with deterministic literal (or fixed-column)
 * operands, ready to be rendered and executed across every dialect.<br>
 * <p>
 *     The {@link #builder} produces the concrete {@link SqlRenderable} (a condition or an
 *     expression) given the engine's {@link SqlDatabase}; the database is only needed by cases that
 *     embed a sub-query, every other case ignores it. {@link #operatorClass} ties the case back to
 *     the discovered leaf class so the coverage guard can prove every operator is represented.
 * </p>
 *
 * @author Luis-St
 */
public record SqlOperatorCase(
	@NonNull String label,
	@NonNull Class<?> operatorClass,
	@NonNull Kind kind,
	@Nullable SqlFeature requiredFeature,
	@NonNull Determinism determinism,
	@NonNull Function<SqlDatabase, SqlRenderable> builder,
	@Nullable Object oracle
) {
	
	public SqlOperatorCase {
		Objects.requireNonNull(label, "Label must not be null");
		Objects.requireNonNull(operatorClass, "Operator class must not be null");
		Objects.requireNonNull(kind, "Kind must not be null");
		Objects.requireNonNull(determinism, "Determinism must not be null");
		Objects.requireNonNull(builder, "Builder must not be null");
	}
	
	public @NonNull SqlRenderable build(@NonNull SqlDatabase database) {
		return Objects.requireNonNull(this.builder.apply(database), "Builder produced a null renderable for " + this.label);
	}
	
	public boolean hasOracle() {
		return this.oracle != null;
	}
	
	@Override
	public @NonNull String toString() {
		return this.label;
	}
	
	public enum Kind {
		SCALAR,
		AGGREGATE,
		WINDOW,
		CONDITION
	}
	
	public enum Determinism {
		DETERMINISTIC,
		FLOAT,
		NON_DETERMINISTIC
	}
}
