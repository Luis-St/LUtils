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

package net.luis.utils.io.data.toon;

import net.luis.utils.annotation.type.Singleton;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a toon null value.<br>
 * <p>
 *     In TOON, null is represented as the unquoted token {@code null}.<br>
 *     This class is a singleton.
 * </p>
 *
 * @author Luis-St
 */
@Singleton
public final class ToonNull implements ToonElement {
	
	/**
	 * The singleton instance of {@link ToonNull}.<br>
	 * This instance is immutable and can be used for all null values.<br>
	 */
	public static final ToonNull INSTANCE = new ToonNull();
	
	/**
	 * Constructs a new toon null.<br>
	 * Should not be used, use {@link #INSTANCE} instead.<br>
	 */
	private ToonNull() {}
	
	//region Object overrides
	
	@Override
	public String toString() {
		return this.toString(ToonConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull ToonConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return "null";
	}
	//endregion
}
