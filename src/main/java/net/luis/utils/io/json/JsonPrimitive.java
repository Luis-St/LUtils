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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 *
 * @author Luis-St
 *
 */

public class JsonPrimitive implements JsonElement {
	
	public @UnknownNullability String getAsString() {
		return null;
	}
	
	public boolean getAsBoolean() {
		return false;
	}
	
	public @UnknownNullability Number getAsNumber() {
		return null;
	}
	
	public byte getAsByte() {
		return 0;
	}
	
	public short getAsShort() {
		return 0;
	}
	
	public int getAsInteger() {
		return 0;
	}
	
	public long getAsLong() {
		return 0;
	}
	
	public float getAsFloat() {
		return 0;
	}
	
	public double getAsDouble() {
		return 0;
	}
	
	@Override
	public @NotNull String toString(@NotNull JsonConfig config) {
		return "";
	}
}
