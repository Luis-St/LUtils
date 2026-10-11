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

package net.luis.utils.io.network.tls;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * The severity of a TLS alert.<br>
 * <p>
 *     TLS 1.3 ignores the level of every alert except {@code close_notify} and {@code user_canceled},<br>
 *     and treats all other alerts as fatal regardless of the level they were sent with.
 * </p>
 *
 * @see TlsAlert
 *
 * @author Luis-St
 */
public enum TlsAlertLevel {
	
	/**
	 * An alert the connection survives.<br>
	 */
	WARNING(1),
	/**
	 * An alert that terminates the connection immediately.<br>
	 */
	FATAL(2);
	
	/**
	 * The single byte level code as it appears on the wire.<br>
	 */
	private final int code;
	
	/**
	 * Constructs a new alert level constant.<br>
	 * @param code The single byte level code
	 */
	TlsAlertLevel(int code) {
		this.code = code;
	}
	
	/**
	 * Returns the alert level with the given single byte code.<br>
	 *
	 * @param code The single byte level code
	 * @return An optional containing the matching level, or an empty optional if the code is unknown
	 */
	public static @NonNull Optional<TlsAlertLevel> byCode(int code) {
		for (TlsAlertLevel level : values()) {
			if (level.code == code) {
				return Optional.of(level);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Returns the single byte level code as it appears on the wire.<br>
	 * @return The level code
	 */
	public int code() {
		return this.code;
	}
}
