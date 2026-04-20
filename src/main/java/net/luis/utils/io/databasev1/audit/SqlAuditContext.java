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

/*package net.luis.utils.io.databasev1.audit;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

*//**
 * Immutable interface representing a SQL audit context.<br>
 * <p>
 *     Provides the identity of the user performing database operations for auditing purposes.<br>
 *     Instances are immutable and thread-safe. Use {@link #withUser(String)} to derive a new context with a different user,<br>
 *     or {@link #empty()} for an anonymous context.
 * </p>
 *
 * @author Luis-St
 *//*
public interface SqlAuditContext {
	
	*//**
	 * Creates an empty audit context with no user set.<br>
	 * @return An empty audit context
	 *//*
	static @NonNull SqlAuditContext empty() {
		throw new UnsupportedOperationException();
	}
	
	*//**
	 * Creates an audit context for the specified user.<br>
	 *
	 * @param user The audit user
	 * @return An audit context with the given user
	 *//*
	static @NonNull SqlAuditContext of(@NonNull String user) {
		throw new UnsupportedOperationException();
	}
	
	*//**
	 * Returns the current audit user.<br>
	 * @return The current audit user or {@code null} if not set
	 *//*
	@Nullable String getUser();
	
	*//**
	 * Returns a new audit context with the specified user.<br>
	 *
	 * @param user The audit user
	 * @return A new audit context with the given user
	 *//*
	@NonNull SqlAuditContext withUser(@NonNull String user);
}*/
