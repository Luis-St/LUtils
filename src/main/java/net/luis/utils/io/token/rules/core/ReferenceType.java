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

package net.luis.utils.io.token.rules.core;

import net.luis.utils.io.token.rules.reference.ReferenceTokenRule;

/**
 * The type of reference used in a {@link ReferenceTokenRule}.<br>
 * It determines whether a rule or a list of tokens is referenced.<br>
 *
 * @author Luis-St
 */
public enum ReferenceType {
	
	/**
	 * Indicates that a token rule is referenced.<br>
	 */
	RULE,
	/**
	 * Indicates that a list of tokens is referenced.<br>
	 */
	TOKENS,
	/**
	 * Indicates that either a token rule or a list of tokens can be referenced.<br>
	 * The reference type is determined dynamically at runtime based on the context.<br>
	 * <p>
	 *     If the context contains a token rule and a list of tokens under the specified key, or neither, the {@link ReferenceTokenRule} will not match any tokens.<br>
	 *     If the context contains only a token rule under the specified key, the {@link ReferenceTokenRule} will behave as if the reference type is {@link #RULE}.<br>
	 *     If the context contains only a list of tokens under the specified key, the {@link ReferenceTokenRule} will behave as if the reference type is {@link #TOKENS}.
	 * </p>
	 */
	DYNAMIC;
}
