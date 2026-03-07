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

package net.luis.utils.io.databasev1.audit;

import org.jspecify.annotations.NonNull;

/**
 * Pluggable sink for audit entries produced by INSERT, UPDATE, and DELETE operations.<br>
 * Register via {@link net.luis.utils.io.databasev1.table.SqlTable#setAuditLog(SqlAuditLog)}.<br>
 * Called by the framework after each successful mutation, after entity listeners fire.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the audited entity
 */
public interface SqlAuditLog<T> {
	
	/**
	 * Records an audit entry for a completed database operation.<br>
	 *
	 * @param entry The audit entry describing what happened
	 */
	void record(@NonNull SqlAuditEntry<T> entry);
}
