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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Supplies the user that is recorded in the created-by and updated-by audit columns.<br>
 * The provider returns an empty optional if no current user is available.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface SqlAuditUserProvider extends Supplier<Optional<String>> {
	
	/**
	 * Creates a user provider that never supplies a user.<br>
	 * @return A user provider that always returns an empty optional
	 */
	static @NonNull SqlAuditUserProvider empty() {
		return Optional::empty;
	}
	
	/**
	 * Creates a user provider that always supplies the given user.<br>
	 *
	 * @param user The user to supply
	 * @return A user provider that always returns the given user
	 * @throws NullPointerException If the user is null
	 */
	static @NonNull SqlAuditUserProvider of(@NonNull String user) {
		Objects.requireNonNull(user, "Sql audit user must not be null");
		
		return () -> Optional.of(user);
	}
}
