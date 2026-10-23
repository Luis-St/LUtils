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

package net.luis.utils.io.network.connection;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Receives the errors a server cannot throw to a caller.<br>
 * <p>
 *     A server runs its handlers on its own threads, so their failures have no caller to propagate to.<br>
 *     This includes exceptions thrown by a handler, failed TLS handshakes, failed broadcasts and failures while accepting or receiving.<br>
 *     Every other operation reports its failure only by throwing an exception.
 * </p>
 * <p>
 *     If a server has no error handler, the error is passed to the uncaught exception handler of the thread it occurred on.
 * </p>
 *
 * @see NetworkServer
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface ErrorHandler {
	
	/**
	 * Handles an error that occurred in a server.<br>
	 *
	 * @param client The client the error occurred on, or null if it is not related to a client
	 * @param error The error that occurred
	 */
	void handle(@Nullable NetworkClient<?> client, @NonNull Throwable error);
}
