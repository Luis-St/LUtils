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

package net.luis.utils.io.network.connection.event;

import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Handler for network errors.<br>
 * This functional interface is used to handle errors that occur during network operations without throwing exceptions.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * ErrorEventHandler onError = (errorType, message, cause) -> {
 *     System.err.println("Error [" + errorType + "]: " + message);
 *     if (cause != null) {
 *         cause.printStackTrace();
 *     }
 * };
 *
 * TcpServerConfig config = TcpServerConfig.builder()
 *     .onError(onError)
 *     .build();
 * }</pre>
 *
 * @see NetworkErrorType
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface ErrorEventHandler {
	
	/**
	 * Called when a network error occurs.<br>
	 *
	 * @param errorType The type/category of the error
	 * @param message A human-readable error message
	 * @param cause The underlying exception, if any
	 */
	void handle(@NonNull NetworkErrorType errorType, @NonNull String message, @Nullable Throwable cause);
}
