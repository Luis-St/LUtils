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

package net.luis.utils.util;

import com.google.common.collect.Lists;
import net.luis.utils.annotation.ReflectiveUsage;
import net.luis.utils.lang.EnumLike;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * A record that represents a priority.<br>
 * <p>
 *     The priority can be used to determine the order of elements in a list.<br>
 *     The priority is an integer value that can be compared to other priorities.<br>
 * </p>
 * <p>
 *     Higher priorities are considered to be more important than lower priorities.<br>
 *     A higher priority has a higher value than a lower priority.<br>
 * </p>
 * @param name The name of the priority
 * @param priority The priority value
 */
public record Priority(@NotNull String name, int priority) implements EnumLike<Priority> {
	
	/**
	 * The list of all priority values.<br>
	 * Required for the {@link EnumLike} interface.<br>
	 */
	@ReflectiveUsage
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private static final List<Priority> VALUES = Lists.newLinkedList();
	
	/**
	 * The lowest priority with the value of {@link Integer#MIN_VALUE}.<br>
	 */
	public static final Priority LOWEST = new Priority("lowest", Integer.MIN_VALUE);
	/**
	 * A low priority with the value of {@link Integer#MIN_VALUE} divided by 2.<br>
	 */
	public static final Priority LOW = new Priority("low", Integer.MIN_VALUE / 2);
	/**
	 * A normal priority with the value of {@code 0}.<br>
	 */
	public static final Priority NORMAL = new Priority("normal", 0);
	/**
	 * A high priority with the value of {@link Integer#MAX_VALUE} divided by 2.<br>
	 */
	public static final Priority HIGH = new Priority("high", Integer.MAX_VALUE / 2);
	/**
	 * The highest priority with the value of {@link Integer#MAX_VALUE}.<br>
	 */
	public static final Priority HIGHEST = new Priority("highest", Integer.MAX_VALUE);
	
	/**
	 * Constructs a new priority with the given name and priority value.<br>
	 * @param name The name of the priority
	 * @param priority The priority value
	 * @throws NullPointerException If the name is null
	 */
	public Priority {
		Objects.requireNonNull(name, "Name must not be null");
		VALUES.add(this);
	}
	
	@Override
	public int compareTo(@NotNull Priority object) {
		return Integer.compare(this.priority, object.priority);
	}
}
