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

package net.luis.utils.io.data.binary;

import net.luis.utils.annotation.type.Singleton;
import org.jspecify.annotations.NonNull;

/**
 * Represents a binary null value.<br>
 * This class is a singleton.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class BinaryNull implements BinaryElement {
	
	/**
	 * The singleton instance of {@link BinaryNull}.<br>
	 * This instance is immutable and can be used for all null values.<br>
	 */
	public static final BinaryNull INSTANCE = new BinaryNull();
	
	/**
	 * Constructs a new binary null.<br>
	 * Should not be used, use {@link #INSTANCE} instead.<br>
	 */
	private BinaryNull() {}
	
	@Override
	public @NonNull BinaryType getType() {
		return BinaryType.NULL;
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return "null";
	}
	//endregion
}
