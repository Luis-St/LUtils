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

/**
 * Enumeration of operating system platforms for path validation.<br>
 * <p>
 *     Different platforms have different rules for file paths including path separators,
 *     reserved characters, and maximum path lengths.<br>
 *     This enum is used to validate paths against platform-specific rules.
 * </p>
 *
 * @author Luis-St
 *
 */

public enum Platform {
	
	/**
	 * Microsoft Windows platform.<br>
	 * <p>
	 *     Uses backslash (\) as path separator, has reserved characters and names,
	 *     and typically has a maximum path length of 260 characters (unless long paths are enabled).
	 * </p>
	 */
	WINDOWS,
	
	/**
	 * Apple macOS platform.<br>
	 * <p>
	 *     Uses forward slash (/) as path separator and is based on BSD Unix.<br>
	 *     Case-insensitive by default on HFS+ and APFS file systems.
	 * </p>
	 */
	MAC,
	
	/**
	 * Linux platform.<br>
	 * <p>
	 *     Uses forward slash (/) as path separator.<br>
	 *     Case-sensitive file system and follows POSIX standards.
	 * </p>
	 */
	LINUX,
	
	/**
	 * Generic POSIX-compliant platform.<br>
	 * <p>
	 *     Represents any POSIX-compliant operating system.<br>
	 *     Uses forward slash (/) as path separator and follows POSIX path conventions.
	 * </p>
	 */
	POSIX
}
