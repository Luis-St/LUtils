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
 * Represents an absent value of a binary struct.<br>
 * <p>
 *     An absent value is used for fields of a struct which have not been set.<br>
 *     It is the binary representation of an optional value which is empty.
 * </p>
 * <p>
 *     An absent value must not be confused with a {@link BinaryNull null value}.<br>
 *     A null value is a value which is explicitly set to null, an absent value is a value which is not present at all.
 * </p>
 * This class is a singleton.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class BinaryAbsent implements BinaryElement {
	
	/**
	 * The singleton instance of {@link BinaryAbsent}.<br>
	 * This instance is immutable and can be used for all absent values.<br>
	 */
	public static final BinaryAbsent INSTANCE = new BinaryAbsent();
	
	/**
	 * Constructs a new binary absent value.<br>
	 * Should not be used, use {@link #INSTANCE} instead.<br>
	 */
	private BinaryAbsent() {}
	
	@Override
	public @NonNull BinaryType getType() {
		return BinaryType.ABSENT;
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return "absent";
	}
	//endregion
}
