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

package net.luis.utils.io.data.toml;

import net.luis.utils.annotation.type.Singleton;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a TOML null value.<br>
 * Note: TOML specification does not have a null type, but this is provided
 * for API consistency with other data formats. When serialized, null values
 * are typically represented as empty strings or omitted.<br>
 * This class is a singleton.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class TomlNull implements TomlElement {
	
	/**
	 * The singleton instance of {@link TomlNull}.<br>
	 * This instance is immutable and can be used for all null values.<br>
	 */
	public static final TomlNull INSTANCE = new TomlNull();
	
	/**
	 * Constructs a new TOML null.<br>
	 * Should not be used, use {@link #INSTANCE} instead.<br>
	 */
	private TomlNull() {}
	
	@Override
	public String toString() {
		return this.toString(TomlConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull TomlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return "";
	}
}
