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

package net.luis.utils.io.data.yaml;

import net.luis.utils.annotation.type.Singleton;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents a yaml null value.<br>
 * This class is a singleton.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class YamlNull implements YamlElement {
	
	/**
	 * The singleton instance of {@link YamlNull}.<br>
	 * This instance is immutable and can be used for all null values.<br>
	 */
	public static final YamlNull INSTANCE = new YamlNull();
	
	/**
	 * Constructs a new yaml null.<br>
	 * Should not be used, use {@link #INSTANCE} instead.<br>
	 */
	private YamlNull() {}
	
	//region Object overrides
	
	@Override
	public String toString() {
		return this.toString(YamlConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@Nullable YamlConfig config) {
		YamlConfig cfg = config == null ? YamlConfig.DEFAULT : config;
		return switch (cfg.nullStyle()) {
			case NULL -> "null";
			case TILDE -> "~";
			case EMPTY -> "";
		};
	}
	//endregion
}
