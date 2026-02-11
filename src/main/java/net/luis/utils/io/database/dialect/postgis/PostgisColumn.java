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

package net.luis.utils.io.database.dialect.postgis;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.postgres.PostgresColumn;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a PostGIS-specific column with spatial operations.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the column value
 */
public interface PostgisColumn<T> extends PostgresColumn<T> {
	
	/**
	 * Creates a condition that checks if this geometry contains the given geometry.<br>
	 * Generates SQL: {@code ST_Contains(column, geometry)}.<br>
	 *
	 * @param geometry The geometry to check containment for
	 * @return The spatial contains condition
	 */
	@NonNull SqlCondition stContains(@NonNull T geometry);
	
	/**
	 * Creates a condition that checks if this geometry is within the given geometry.<br>
	 * Generates SQL: {@code ST_Within(column, geometry)}.<br>
	 *
	 * @param geometry The geometry to check against
	 * @return The spatial within condition
	 */
	@NonNull SqlCondition stWithin(@NonNull T geometry);
	
	/**
	 * Creates a condition that checks if this geometry intersects the given geometry.<br>
	 * Generates SQL: {@code ST_Intersects(column, geometry)}.<br>
	 *
	 * @param geometry The geometry to check intersection with
	 * @return The spatial intersects condition
	 */
	@NonNull SqlCondition stIntersects(@NonNull T geometry);
	
	/**
	 * Creates an expression that calculates the distance between this geometry and another column.<br>
	 * Generates SQL: {@code ST_Distance(column, other)}.<br>
	 *
	 * @param other The other geometry column to measure distance to
	 * @return The spatial distance expression
	 */
	@NonNull SqlExpression<?> stDistance(@NonNull SqlColumn<?> other);
	
	/**
	 * Creates an expression that returns a geometry buffered by the given radius.<br>
	 * Generates SQL: {@code ST_Buffer(column, radius)}.<br>
	 *
	 * @param radius The buffer radius
	 * @return The buffered geometry expression
	 */
	@NonNull SqlExpression<?> stBuffer(double radius);
	
	/**
	 * Creates an expression that calculates the area of this geometry.<br>
	 * Generates SQL: {@code ST_Area(column)}.<br>
	 * @return The spatial area expression
	 */
	@NonNull SqlExpression<?> stArea();
	
	/**
	 * Creates an expression that returns the centroid of this geometry.<br>
	 * Generates SQL: {@code ST_Centroid(column)}.<br>
	 * @return The spatial centroid expression
	 */
	@NonNull SqlExpression<?> stCentroid();
	
	/**
	 * Creates an expression that transforms this geometry to a different spatial reference system.<br>
	 * Generates SQL: {@code ST_Transform(column, srid)}.<br>
	 *
	 * @param srid The target spatial reference system identifier
	 * @return The transformed geometry expression
	 */
	@NonNull SqlExpression<?> stTransform(int srid);
	
	/**
	 * Creates a condition that checks if this geometry is within a given distance of another column.<br>
	 * Generates SQL: {@code ST_DWithin(column, other, distance)}.<br>
	 *
	 * @param other The other geometry column to measure distance from
	 * @param distance The maximum distance threshold
	 * @return The spatial distance-within condition
	 */
	@NonNull SqlCondition stDWithin(@NonNull SqlColumn<?> other, double distance);
}
