/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.token.actions.core;

/**
 * Enum representing the grouping mode for grouping token actions.<br>
 *
 * @author Luis-St
 */
public enum GroupingMode {
	
	/**
	 * Groups only the matched tokens.<br>
	 * This mode groups only the tokens that were explicitly matched by the rule.<br>
	 */
	MATCHED,
	
	/**
	 * Groups all tokens including shadow tokens.<br>
	 * This mode groups all tokens in the range from the start index to the end index,<br>
	 * including any tokens that were skipped by the rule (such as shadow tokens).<br>
	 */
	ALL
}
