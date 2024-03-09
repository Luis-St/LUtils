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

package net.luis.utils.logging;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The possible logging types.<br>
 * Implements {@link Iterable} to allow iterating over the allowed levels.<br>
 */
public enum LoggingType implements Iterable<Level> {
	/**
	 * The logging will be printed to the console.<br>
	 */
	CONSOLE,
	/**
	 * The logging will be printed to a file.<br>
	 */
	FILE;
	
	/**
	 * Supported console logging levels.<br>
	 */
	private static final Level[] CONSOLE_LEVELS = { Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL };
	/**
	 * Supported file logging levels.<br>
	 */
	private static final Level[] FILE_LEVELS = { Level.DEBUG, Level.INFO, Level.ERROR };
	
	/**
	 * Gets the allowed levels for this logging type.
	 * @return An array of allowed levels
	 * @throws IllegalStateException If the logging type is unknown
	 */
	public Level @NotNull [] getAllowedLevels() {
		if (this == CONSOLE) {
			return CONSOLE_LEVELS;
		} else if (this == FILE) {
			return FILE_LEVELS;
		}
		throw new IllegalStateException("Unknown logging type: " + this);
	}
	
	@Override
	public @NotNull Iterator<Level> iterator() {
		return this.createIterator(this.getAllowedLevels());
	}
	
	/**
	 * Creates an iterator for the given levels.
	 * @param levels The levels
	 * @return The created iterator
	 */
	private @NotNull Iterator<Level> createIterator(Level @NotNull [] levels) {
		return new Iterator<>() {
			private int index;
			
			@Override
			public boolean hasNext() {
				return this.index < levels.length;
			}
			
			@Override
			public @NotNull Level next() {
				if (!this.hasNext()) {
					throw new NoSuchElementException("No more elements left in the iterator");
				}
				return levels[this.index++];
			}
		};
	}
	
	@Override
	public @NotNull String toString() {
		return this.name().toLowerCase();
	}
}
