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

package net.luis.utils.io.codec.constraint_new.core;

import org.jspecify.annotations.NonNull;

/**
 * Enumeration of network port ranges as defined by IANA.<br>
 * <p>
 *     Network ports are divided into three ranges based on their intended use and registration status.<br>
 *     These ranges help categorize ports for different types of network services.
 * </p>
 *
 * @author Luis-St
 *
 */

public enum PortRange {
	
	/**
	 * System port range (0-1023).<br>
	 * <p>
	 *     Also known as well-known ports, these are assigned by IANA for specific services.<br>
	 *     Typically require administrative privileges to bind on most operating systems.
	 * </p>
	 */
	SYSTEM,
	
	/**
	 * Registered port range (1024-49151).<br>
	 * <p>
	 *     Also known as user ports, these can be registered with IANA for specific services.<br>
	 *     Generally available for use by user applications without special privileges.
	 * </p>
	 */
	REGISTERED,
	
	/**
	 * Dynamic port range (49152-65535).<br>
	 * <p>
	 *     Also known as private or ephemeral ports, these are used for temporary connections.<br>
	 *     Typically assigned automatically by the operating system for client-side connections.
	 * </p>
	 */
	DYNAMIC;
	
	/**
	 * Gets the port range for a given port number.<br>
	 * Determines which port range the specified port number belongs to.<br>
	 *
	 * @param port The port number to evaluate
	 * @return The corresponding port range
	 * @throws IllegalArgumentException if the port number is not in the range 0-65535
	 */
	public static @NonNull PortRange fromPort(int port) {
		if (port >= 0 && port <= 1023) {
			return SYSTEM;
		} else if (port >= 1024 && port <= 49151) {
			return REGISTERED;
		} else if (port >= 49152 && port <= 65535) {
			return DYNAMIC;
		} else {
			throw new IllegalArgumentException("Port must be in the range 0-65535");
		}
	}
}
