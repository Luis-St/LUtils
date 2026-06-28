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

package net.luis.utils.io.database.migration;

/**
 * Represents the state of a migration as tracked by the migration framework.<br>
 *
 * @author Luis-St
 */
public enum SqlMigrationStatus {
	
	/**
	 * The migration is known but has not been applied yet.<br>
	 */
	PENDING,
	/**
	 * The migration has been applied to the database.<br>
	 */
	APPLIED,
	/**
	 * The migration has been applied and then rolled back.<br>
	 */
	ROLLED_BACK
}
