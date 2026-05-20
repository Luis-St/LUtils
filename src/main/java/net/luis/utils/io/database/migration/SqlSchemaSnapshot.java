package net.luis.utils.io.database.migration;

import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */
public record SqlSchemaSnapshot(
	@NonNull List<SqlSchemaColumnInfo> columns,
	@NonNull Map<String, List<SqlCheckConstraintInfo>> checkConstraints
) {
	
	public SqlSchemaSnapshot {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		Objects.requireNonNull(checkConstraints, "Sql check constraints must not be null");
	}
}
