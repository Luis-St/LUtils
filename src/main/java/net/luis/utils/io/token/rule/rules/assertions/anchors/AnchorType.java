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

package net.luis.utils.io.token.rule.rules.assertions.anchors;

/**
 * Enumeration for different types of anchors in token matching.<br>
 * Defines whether an anchor should match at line boundaries or document boundaries.<br>
 *
 * @author Luis-St
 */
public enum AnchorType {
	
	/**
	 * Match at line boundaries (start/end of line).<br>
	 */
	LINE,
	
	/**
	 * Match at document boundaries (start/end of the entire token list).<br>
	 */
	DOCUMENT
}
