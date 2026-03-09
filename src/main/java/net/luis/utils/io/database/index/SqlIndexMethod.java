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
 *
 * @author Luis-St
 *
 */

public enum SqlIndexMethod {
	
	/**
	 * B-tree index method, commonly used for general-purpose indexing.<br>
	 */
	BTREE,
	/**
	 * Hash index method, optimized for equality comparisons but not suitable for range queries.<br>
	 */
	HASH,
	/**
	 * GIN (Generalized Inverted Index) method, designed for indexing composite values such as arrays and full-text search data.<br>
	 */
	GIN,
	/**
	 * GIST (Generalized Search Tree) method, used for indexing complex data types like geometric data and full-text search data.<br>
	 */
	GIST,
	/**
	 * BRIN (Block Range INdex) method,<br>
	 * optimized for indexing large tables where the indexed columns have a natural ordering, such as timestamps or sequential IDs.<br>
	 */
	BRIN,
	/**
	 * SP-GiST (Space-Partitioned Generalized Search Tree) method,<br>
	 * designed for indexing data that can be partitioned into non-overlapping regions, such as spatial data or text search data.<br>
	 */
	SPGIST,
	/**
	 * CLUSTERED index method, where the physical order of the rows in the table is the same as the order of the index.<br>
	 * This can improve performance for certain types of queries but may require more maintenance when inserting or updating data.<br>
	 */
	CLUSTERED,
	/**
	 * NONCLUSTERED index method, where the physical order of the rows in the table is independent of the order of the index.<br>
	 * This allows for more flexibility in maintaining the index but may result in slower performance for certain types of queries compared to clustered indexes.<br>
	 */
	NONCLUSTERED,
	/**
	 * COLUMNSTORE index method, designed for analytical workloads and large datasets, where data is stored in a columnar format rather than row-based format.<br>
	 * This can significantly improve query performance for certain types of queries, especially those that involve aggregations and filtering on large datasets, but may not be suitable for all types of workloads.<br>
	 */
	COLUMNSTORE,
	/**
	 * BITMAP index method, used for indexing columns with low cardinality (i.e., columns that have a small number of distinct values).<br>
	 * Bitmap indexes can be very efficient for certain types of queries, especially those that involve filtering on low-cardinality columns, but may not be suitable for all types of workloads.<br>
	 */
	BITMAP
}
