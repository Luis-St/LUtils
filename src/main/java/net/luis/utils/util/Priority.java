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
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;

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
 * <p>
 *     The default priorities {@link #LOWEST}, {@link #LOW}, {@link #NORMAL}, {@link #HIGH}, and {@link #HIGHEST}<br>
 *     are using integer values to represent the priority.<br>
 *     This allows the creation of priorities below the lowest priority and above the highest priority.<br>
 * </p>
 * @param name The name of the priority
 * @param priority The priority value
 */
public record Priority(@NotNull String name, long priority) implements EnumLike<Priority> {
	
	//region Internal
	
	/**
	 * The list of all priority values.<br>
	 * Required for the {@link EnumLike} interface.<br>
	 */
	@ReflectiveUsage
	private static final List<Priority> VALUES = Lists.newLinkedList();
	//endregion
	
	/**
	 * The lowest priority with the value of {@link Integer#MIN_VALUE}.<br>
	 * <p>
	 *     The lowest priority does not use {@link Long#MIN_VALUE}<br>
	 *     to allow the creation of priorities below the lowest priority.<br>
	 * </p>
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
	 * <p>
	 *     The highest priority does not use {@link Long#MAX_VALUE}<br>
	 *     to allow the creation of priorities above the highest priority.<br>
	 * </p>
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
	
	//region Static helper methods
	
	/**
	 * Creates a new priority below the given priority.<br>
	 * @param priority The target priority
	 * @return The created priority
	 */
	public static @NotNull Priority createBelow(@NotNull Priority priority) {
		Objects.requireNonNull(priority, "Priority must not be null");
		return new Priority(priority.name() + "-1", priority.priority() - 1);
	}
	
	/**
	 * Creates a new priority above the given priority.<br>
	 * @param priority The target priority
	 * @return The created priority
	 */
	public static @NotNull Priority createAbove(@NotNull Priority priority) {
		Objects.requireNonNull(priority, "Priority must not be null");
		return new Priority(priority.name() + "+1", priority.priority() + 1);
	}
	
	/**
	 * Creates a new priority between the two given priorities.<br>
	 * @param first The first priority
	 * @param second The second priority
	 * @return The created priority
	 */
	public static @NotNull Priority createBetween(@NotNull Priority first, @NotNull Priority second) {
		Objects.requireNonNull(first, "First priority must not be null");
		Objects.requireNonNull(second, "Second priority must not be null");
		return new Priority(first.name() + "-" + second.name(), (first.priority() + second.priority()) / 2);
	}
	
	/**
	 * Gets the lowest priority from the given priorities.<br>
	 * If the list of priorities is null or empty, the nearest priority to {@link Long#MIN_VALUE} is returned.<br>
	 * @param priorities The list of priorities
	 * @return The lowest priority
	 */
	public static @NotNull Priority lowest(Priority @Nullable ... priorities) {
		if (priorities == null || priorities.length == 0) {
			return getNearest(Long.MIN_VALUE);
		}
		return Stream.of(priorities).min(Priority::compareTo).orElseThrow();
	}
	
	/**
	 * Gets the highest priority from the given priorities.<br>
	 * If the list of priorities is null or empty, the nearest priority to {@link Long#MAX_VALUE} is returned.<br>
	 * @param priorities The list of priorities
	 * @return The highest priority
	 */
	public static @NotNull Priority highest(Priority @Nullable ... priorities) {
		if (priorities == null || priorities.length == 0) {
			return getNearest(Long.MAX_VALUE);
		}
		return Stream.of(priorities).max(Priority::compareTo).orElseThrow();
	}
	
	/**
	 * Returns the priority with the nearest value to the given priority.<br>
	 * @param priority The target priority
	 * @return The nearest priority
	 */
	public static @NotNull Priority getNearest(long priority) {
		BigInteger target = BigInteger.valueOf(priority);
		return Priority.VALUES.stream().reduce((p1, p2) -> {
			BigInteger diff1 = BigInteger.valueOf(p1.priority()).subtract(target).abs();
			BigInteger diff2 = BigInteger.valueOf(p2.priority()).subtract(target).abs();
			return diff1.compareTo(diff2) < 0 ? p1 : p2;
		}).orElseThrow();
	}
	//endregion
	
	@Override
	public int compareTo(@NotNull Priority object) {
		return Long.compare(this.priority, object.priority);
	}
}
