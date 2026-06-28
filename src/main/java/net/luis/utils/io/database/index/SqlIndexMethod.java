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

package net.luis.utils.io.database.index;

/**
 * Represents the method (access structure) used by a database index.<br>
 * Not every method is supported by every dialect.<br>
 *
 * @author Luis-St
 */
public enum SqlIndexMethod {
	
	/**
	 * A balanced-tree index, the default method supported by most databases.<br>
	 */
	BTREE,
	/**
	 * A hash index optimized for equality lookups.<br>
	 */
	HASH,
	/**
	 * A generalized inverted index, used for composite values such as arrays or full-text data.<br>
	 */
	GIN,
	/**
	 * A generalized search tree index, used for geometric and other custom data types.<br>
	 */
	GIST,
	/**
	 * A block range index, storing summaries over ranges of physically adjacent rows.<br>
	 */
	BRIN,
	/**
	 * A space-partitioned generalized search tree index.<br>
	 */
	SPGIST,
	/**
	 * A clustered index that determines the physical storage order of the rows.<br>
	 */
	CLUSTERED,
	/**
	 * A non-clustered index stored separately from the row data.<br>
	 */
	NONCLUSTERED,
	/**
	 * A columnstore index that stores data column-wise for analytical workloads.<br>
	 */
	COLUMNSTORE,
	/**
	 * A bitmap index, efficient for low-cardinality columns.<br>
	 */
	BITMAP
}
