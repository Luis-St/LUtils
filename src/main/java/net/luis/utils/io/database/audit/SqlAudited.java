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

package net.luis.utils.io.database.audit;

import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record SqlAudited<T>(
	@NonNull T entity,
	@NonNull SqlAuditMetadata audit
) {
	
	public SqlAudited {
		Objects.requireNonNull(entity, "Audited entity must not be null");
		Objects.requireNonNull(audit, "Sql audit metadata must not be null");
	}
	
	public static <T> @NonNull SqlAudited<T> of(@NonNull T entity, @NonNull SqlAuditMetadata audit) {
		return new SqlAudited<>(entity, audit);
	}
	
	public @NonNull OptionalLong version() {
		return this.audit.version();
	}
	
	public @NonNull Optional<LocalDateTime> createdAt() {
		return this.audit.createdAt();
	}
	
	public @NonNull Optional<String> createdBy() {
		return this.audit.createdBy();
	}
	
	public @NonNull Optional<LocalDateTime> updatedAt() {
		return this.audit.updatedAt();
	}
	
	public @NonNull Optional<String> updatedBy() {
		return this.audit.updatedBy();
	}
}
