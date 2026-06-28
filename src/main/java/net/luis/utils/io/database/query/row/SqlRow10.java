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

package net.luis.utils.io.database.query.row;

import org.jspecify.annotations.NonNull;

/**
 * Represents a row of 10 typed columns returned from a sql query.<br>
 * Each column value is accessed positionally through its ordinal accessor, where the n-th column is bound to the n-th accessor method.<br>
 *
 * @author Luis-St
 *
 * @param <T1> The type of the first column
 * @param <T2> The type of the second column
 * @param <T3> The type of the third column
 * @param <T4> The type of the fourth column
 * @param <T5> The type of the fifth column
 * @param <T6> The type of the sixth column
 * @param <T7> The type of the seventh column
 * @param <T8> The type of the eighth column
 * @param <T9> The type of the ninth column
 * @param <T10> The type of the tenth column
 */
public interface SqlRow10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {
	
	/**
	 * Returns the value of the first column of this row.<br>
	 * @return The value of the first column
	 */
	@NonNull T1 first();
	
	/**
	 * Returns the value of the second column of this row.<br>
	 * @return The value of the second column
	 */
	@NonNull T2 second();
	
	/**
	 * Returns the value of the third column of this row.<br>
	 * @return The value of the third column
	 */
	@NonNull T3 third();
	
	/**
	 * Returns the value of the fourth column of this row.<br>
	 * @return The value of the fourth column
	 */
	@NonNull T4 fourth();
	
	/**
	 * Returns the value of the fifth column of this row.<br>
	 * @return The value of the fifth column
	 */
	@NonNull T5 fifth();
	
	/**
	 * Returns the value of the sixth column of this row.<br>
	 * @return The value of the sixth column
	 */
	@NonNull T6 sixth();
	
	/**
	 * Returns the value of the seventh column of this row.<br>
	 * @return The value of the seventh column
	 */
	@NonNull T7 seventh();
	
	/**
	 * Returns the value of the eighth column of this row.<br>
	 * @return The value of the eighth column
	 */
	@NonNull T8 eighth();
	
	/**
	 * Returns the value of the ninth column of this row.<br>
	 * @return The value of the ninth column
	 */
	@NonNull T9 ninth();
	
	/**
	 * Returns the value of the tenth column of this row.<br>
	 * @return The value of the tenth column
	 */
	@NonNull T10 tenth();
}
