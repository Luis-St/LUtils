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

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class JsonArray implements JsonElement {
	
	private final List<JsonElement> elements = Lists.newLinkedList();
	
	public @NotNull JsonElement get(int index) {
		return null;
	}
	
	public @NotNull JsonObject getAsJsonObject(int index) {
		return null;
	}
	
	public @NotNull JsonArray getAsJsonArray(int index) {
		return null;
	}
	
	public @NotNull JsonPrimitive getJsonPrimitive(int index) {
		return null;
	}
	
	public @NotNull String getAsString(int index) {
		return null;
	}
	
	public boolean getAsBoolean(int index) {
		return false;
	}
	
	public @NotNull Number getAsNumber(int index) {
		return null;
	}
	
	public byte getAsByte(int index) {
		return 0;
	}
	
	public short getAsShort(int index) {
		return 0;
	}
	
	public int getAsInteger(int index) {
		return 0;
	}
	
	public long getAsLong(int index) {
		return 0;
	}
	
	public float getAsFloat(int index) {
		return 0;
	}
	
	public double getAsDouble(int index) {
		return 0;
	}
	
	@Override
	public @NotNull String toString(@NotNull JsonConfig config) {
		return "";
	}
}
