/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.data.json;

import net.luis.utils.annotation.type.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a json null value.<br>
 * This class is a singleton.<br>
 *
 * @author Luis-St
 */
@Singleton
public class JsonNull implements JsonElement {
	
	/**
	 * The singleton instance of {@link JsonNull}.<br>
	 * This instance is immutable and can be used for all null values.<br>
	 */
	public static final JsonNull INSTANCE = new JsonNull();
	
	/**
	 * Constructs a new {@link JsonNull}.<br>
	 * Should not be used, use {@link #INSTANCE} instead.<br>
	 */
	private JsonNull() {}
	
	//region Object overrides
	
	@Override
	public String toString() {
		return this.toString(JsonConfig.DEFAULT);
	}
	
	@Override
	public @NotNull String toString(@Nullable JsonConfig config) {
		return "null";
	}
	//endregion
}
