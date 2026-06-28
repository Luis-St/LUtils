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

package net.luis.utils.io.database.type;

/**
 * Internal access token used to restrict the internal {@link SqlType} methods to callers inside this package.<br>
 * Passing the {@link #INSTANCE} proves that a call originates from within the type package.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings("InstantiationOfUtilityClass")
final class SqlTypeInternalAccess {
	
	/**
	 * The single shared instance used as the access token for internal type calls.
	 */
	static final SqlTypeInternalAccess INSTANCE = new SqlTypeInternalAccess();
	
	/**
	 * Constructs a new sql type internal access token.<br>
	 */
	SqlTypeInternalAccess() {}
}
