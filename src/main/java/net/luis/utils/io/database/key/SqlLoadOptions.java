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

package net.luis.utils.io.database.key;

import org.jspecify.annotations.NonNull;

import java.time.Duration;

/**
 * Interface representing SQL load options for foreign keys.<br>
 *
 * @author Luis-St
 */
public interface SqlLoadOptions {
	
	/**
	 * Creates a new load options builder.<br>
	 * @return The new builder
	 */
	static @NonNull SqlLoadOptions builder() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Acquires a row-level lock on the loaded entity.<br>
	 * Generates SQL: {@code FOR UPDATE}.<br>
	 *
	 * @return This load options builder
	 */
	@NonNull SqlLoadOptions forUpdate();
	
	/**
	 * Sets the timeout for the load operation.<br>
	 *
	 * @param timeout The maximum duration to wait
	 * @return This load options builder
	 */
	@NonNull SqlLoadOptions timeout(@NonNull Duration timeout);
	
	/**
	 * Builds the load options.<br>
	 * @return The built load options
	 */
	@NonNull SqlLoadOptions build();
}
