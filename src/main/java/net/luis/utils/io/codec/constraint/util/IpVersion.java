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

package net.luis.utils.io.codec.constraint.util;

import net.luis.utils.io.codec.constraint.builder.IpConstraintBuilder;
import net.luis.utils.io.codec.constraint.merged.io.IpConstraint;

/**
 * Enumeration representing Internet Protocol versions.<br>
 * <p>
 *     This enum defines the two IP address versions used in network communication:
 *     IPv4 (32-bit addresses) and IPv6 (128-bit addresses).
 * </p>
 * <p>
 *     Used by {@link IpConstraint} and {@link IpConstraintBuilder} to specify
 *     which IP version format should be validated against.
 * </p>
 *
 * @author Luis-St
 *
 * @see IpConstraint
 * @see IpConstraintBuilder
 */
public enum IpVersion {
	
	/**
	 * Internet Protocol version 4.<br>
	 * Addresses are 32-bit numeric values typically represented in dot-decimal notation (e.g., 192.168.1.1).
	 */
	IPV4,
	
	/**
	 * Internet Protocol version 6.<br>
	 * Addresses are 128-bit numeric values typically represented in colon-hexadecimal notation (e.g., 2001:0db8::1).
	 */
	IPV6
}
