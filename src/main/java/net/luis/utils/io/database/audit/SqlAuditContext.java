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
 * Class for managing SQL audit context.<br>
 *
 * @author Luis-St
 */
public class SqlAuditContext {

	public static void setCurrentUser(@NonNull String user) {
		throw new UnsupportedOperationException();
	}

	public static @Nullable String getCurrentUser() {
		throw new UnsupportedOperationException();
	}

	public static void clear() {
		throw new UnsupportedOperationException();
	}

	public static <T> T withUser(@NonNull String user, @NonNull Supplier<T> action) {
		throw new UnsupportedOperationException();
	}
}
