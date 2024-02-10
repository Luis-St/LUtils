package net.luis.utils.logging;

import com.google.common.collect.Iterables;
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
	private static final Level[] CONSOLE_LEVELS = new Level[] {Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL};
	/**
	 * Supported file logging levels.<br>
	 */
	private static final Level[] FILE_LEVELS = new Level[] {Level.DEBUG, Level.INFO, Level.ERROR};
	
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
		return new Iterator<Level>() {
			private int index = 0;
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
