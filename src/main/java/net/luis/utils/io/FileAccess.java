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

package net.luis.utils.io;

import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class to manage file access in a thread-safe way.<br>
 * This class manages the access to a file from multiple threads.<br>
 * <p>
 *     To use this class, create an instance of it with the file you want to access.<br>
 *     Share the instance with the threads that need to access the file.<br>
 * </p>
 * <p>
 *     To ensure that only one thread accesses the file at a time,<br>
 *     use the {@link FileAccess#access(FailableConsumer)} or {@link FileAccess#access(FailableFunction)} methods.<br>
 *     These methods will lock the file for the calling thread and unlock it when the action is done.<br>
 * </p>
 * <p>
 *     Each file can only be accessed by one instance of this class at a time.<br>
 *     If you try to access a file that is already accessed by another instance of this class, an exception will be thrown.<br>
 * </p>
 *
 * @author Luis-St
 */
public class FileAccess {
	
	/**
	 * Map of all files that are currently accessed by an instance of this class.<br>
	 * Weak references are used to ensure that the map does not prevent the garbage collection of the files.<br>
	 */
	private static final Map<String, Boolean> ACCESSED_FILES = Collections.synchronizedMap(new WeakHashMap<>());
	
	/**
	 * Lock to ensure that one thread only accesses the file at a time.<br>
	 */
	private final ReentrantLock lock = new ReentrantLock();
	/**
	 * File that is managed by this instance of the class.<br>
	 */
	private final Path file;
	
	/**
	 * Constructs a new file access instance for the given file.<br>
	 * @param file The file to access
	 * @throws NullPointerException If the file is null
	 * @throws IllegalStateException If the file is already accessed by another instance of this class
	 */
	public FileAccess(@NotNull Path file) {
		this.file = Objects.requireNonNull(file, "File must not be null");
		if (ACCESSED_FILES.containsKey(file.toString())) {
			throw new IllegalStateException("File already accessed: " + file);
		}
		ACCESSED_FILES.put(file.toString(), true);
	}
	
	/**
	 * @return The file that is accessed by this instance of the class
	 */
	public @NotNull Path getFile() {
		return this.file;
	}
	
	/**
	 * Checks if the file can be accessed.<br>
	 * @return True if the file can be accessed, false if not
	 */
	public boolean canAccess() {
		return !this.lock.isLocked();
	}
	
	/**
	 * Access the file with the given action.<br>
	 * @param action The action to perform on the file
	 * @throws UncheckedIOException If the action throws an IOException
	 */
	public void access(@NotNull FailableConsumer<Path, IOException> action) {
		this.lock.lock();
		try {
			action.accept(this.file);
		} catch (IOException e) {
			throw new UncheckedIOException("File access failed: " + this.file, e);
		} finally {
			this.lock.unlock();
		}
	}
	
	/**
	 * Access the file with the given action and return the result.<br>
	 * @param action The action to perform on the file
	 * @return The result of the action
	 * @param <T> The type of the result
	 * @throws UncheckedIOException If the action throws an IOException
	 */
	public <T> @NotNull T access(@NotNull FailableFunction<Path, T, IOException> action) {
		this.lock.lock();
		try {
			return action.apply(this.file);
		} catch (IOException e) {
			throw new UncheckedIOException("File access failed: " + this.file, e);
		} finally {
			this.lock.unlock();
		}
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof FileAccess that)) return false;
		
		if (!this.lock.equals(that.lock)) return false;
		return this.file.equals(that.file);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.lock, this.file);
	}
	
	@Override
	public String toString() {
		return "FileAccess " + this.file;
	}
	//endregion
}
