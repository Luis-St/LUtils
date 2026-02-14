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

package net.luis.utils.io.database.audit;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Interface for managing SQL audit context.<br>
 * Provides methods to track the current user performing database operations for auditing purposes.<br>
 *
 * @author Luis-St
 */
public interface SqlAuditContext {
	
	/**
	 * Returns the current audit user.<br>
	 * @return The current audit user or {@code null} if not set
	 */
	@Nullable String getCurrentUser();
	
	/**
	 * Sets the current audit user.<br>
	 * @param user The audit user to set
	 */
	void setCurrentUser(@NonNull String user);
	
	/**
	 * Clears the current audit context.<br>
	 */
	void clear();
	
	/**
	 * Executes the given action with the specified audit user set.<br>
	 * The audit context is restored to its previous state after the action completes.<br>
	 *
	 * @param user The audit user to set
	 * @param action The action to execute
	 * @param <T> The return type of the action
	 * @return The result of the action
	 */
	default <T> T withUser(@NonNull String user, @NonNull Supplier<T> action) {
		String previous = this.getCurrentUser();
		
		this.setCurrentUser(user);
		
		try {
			return action.get();
		} finally {
			if (previous != null) {
				this.setCurrentUser(previous);
			} else {
				this.clear();
			}
		}
	}
}
