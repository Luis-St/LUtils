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

package net.luis.utils.io.token.rules.core;

/**
 * An enum representing the match mode for lookahead and lookbehind token rules.<br>
 *
 * @author Luis-St
 */
public enum LookMatchMode {
	
	/**
	 * Match mode that matches when the inner rule matches.<br>
	 */
	POSITIVE,
	/**
	 * Match mode that matches when the inner rule does not match.<br>
	 */
	NEGATIVE;
	
	/**
	 * Determines if the rule should match based on the provided rule match result.<br>
	 * If this mode is {@link #POSITIVE}, it returns true if the rule matches.<br>
	 * If this mode is {@link #NEGATIVE}, it returns true if the rule does not match.<br>
	 *
	 * @param ruleMatches The result of the inner rule match
	 * @return True if the rule should match, false otherwise
	 */
	public boolean shouldMatch(boolean ruleMatches) {
		return (this == POSITIVE) == ruleMatches;
	}
}
