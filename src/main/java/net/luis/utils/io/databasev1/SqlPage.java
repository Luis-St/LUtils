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

package net.luis.utils.io.databasev1;

import net.luis.utils.io.databasev1.exception.SqlException;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing a page of SQL query results.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the content
 */
public interface SqlPage<T> {
	
	/**
	 * Returns the content of this page.<br>
	 * @return The list of elements on this page
	 */
	@NonNull List<T> content();
	
	/**
	 * Returns the total number of elements across all pages.<br>
	 * @return The total element count
	 */
	long totalElements();
	
	/**
	 * Returns the total number of pages.<br>
	 * @return The total page count
	 */
	int totalPages();
	
	/**
	 * Returns the current page number.<br>
	 * @return The current page number
	 */
	int currentPage();
	
	/**
	 * Checks if there is a next page.<br>
	 * @return Whether a next page exists
	 */
	boolean hasNext();
	
	/**
	 * Checks if there is a previous page.<br>
	 * @return Whether a previous page exists
	 */
	boolean hasPrevious();
	
	/**
	 * Returns the number of elements per page as configured when this page was fetched.<br>
	 * @return The page size
	 */
	int pageSize();
	
	/**
	 * Fetches the next page of results.<br>
	 * Executes a SQL query with adjusted {@code LIMIT} and {@code OFFSET} clauses.<br>
	 * Callers must check {@link #hasNext()} before calling this method.<br>
	 *
	 * @return The next page of results
	 * @throws java.util.NoSuchElementException If there is no next page (i.e. {@link #hasNext()} returns false)
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull SqlPage<T> fetchNext() throws SqlException;
	
	/**
	 * Fetches the page {@code pages} positions ahead of the current page.<br>
	 * Executes a SQL query with {@code LIMIT} and {@code OFFSET} adjusted by {@code pages * pageSize()}.<br>
	 * Callers must ensure the target page exists (i.e., {@code currentPage() + pages < totalPages()}).<br>
	 *
	 * @param pages The number of pages to advance (must be &gt; 0)
	 * @return The page at the requested offset from the current page
	 * @throws java.util.NoSuchElementException If the resulting page is beyond the last page
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull SqlPage<T> fetchNext(int pages) throws SqlException;
	
	/**
	 * Fetches the previous page of results.<br>
	 * Executes a SQL query with adjusted {@code LIMIT} and {@code OFFSET} clauses.<br>
	 * Callers must check {@link #hasPrevious()} before calling this method.<br>
	 *
	 * @return The previous page of results
	 * @throws java.util.NoSuchElementException If there is no previous page (i.e. {@link #hasPrevious()} returns false)
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull SqlPage<T> fetchPrevious() throws SqlException;
	
	/**
	 * Fetches the page {@code pages} positions before the current page.<br>
	 * Executes a SQL query with {@code LIMIT} and {@code OFFSET} adjusted by {@code pages * pageSize()}.<br>
	 * Callers must ensure the target page exists (i.e., {@code currentPage() - pages >= 0}).<br>
	 *
	 * @param pages The number of pages to go back (must be &gt; 0)
	 * @return The page at the requested offset before the current page
	 * @throws java.util.NoSuchElementException If the resulting page is before the first page
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull SqlPage<T> fetchPrevious(int pages) throws SqlException;
}
