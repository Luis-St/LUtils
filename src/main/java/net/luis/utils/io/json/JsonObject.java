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

package net.luis.utils.io.json;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

/**
 *
 * @author Luis-St
 *
 */

public class JsonObject implements JsonElement {
	
	private final Map<String, JsonElement> elements = Maps.newLinkedHashMap();
	
	public @UnknownNullability JsonElement get(@NotNull String key) {
		return null;
	}
	
	public @UnknownNullability JsonObject getAsJsonObject(@NotNull String key) {
		return null;
	}
	
	public @UnknownNullability JsonArray getAsJsonArray(@NotNull String key) {
		return null;
	}
	
	public @UnknownNullability JsonPrimitive getJsonPrimitive(@NotNull String key) {
		return null;
	}
	
	public @UnknownNullability String getAsString(@NotNull String key) {
		return null;
	}
	
	public boolean getAsBoolean(@NotNull String key) {
		return false;
	}
	
	public @UnknownNullability Number getAsNumber(@NotNull String key) {
		return null;
	}
	
	public byte getAsByte(@NotNull String key) {
		return 0;
	}
	
	public short getAsShort(@NotNull String key) {
		return 0;
	}
	
	public int getAsInteger(@NotNull String key) {
		return 0;
	}
	
	public long getAsLong(@NotNull String key) {
		return 0;
	}

	public float getAsFloat(@NotNull String key) {
		return 0;
	}
	
	public double getAsDouble(@NotNull String key) {
		return 0;
	}
	
	@Override
	public @NotNull String toString(@NotNull JsonConfig config) {
		return "";
	}
}
