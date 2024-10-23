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

package net.luis.utils.collection.registry.key;

import net.luis.utils.collection.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Class that represents a key in a {@link Registry}.<br>
 * The key is used to access the items in the registry.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the key
 */
public interface RegistryKey<T> extends Comparable<RegistryKey<T>> {
	
	/**
	 * Returns the element associated with the key.<br>
	 * @return The element of the registry key
	 */
	@NotNull T getKey();
	
	@Override
	int compareTo(@NotNull RegistryKey<T> otherKey);
}
