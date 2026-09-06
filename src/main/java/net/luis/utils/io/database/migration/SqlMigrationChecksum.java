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

package net.luis.utils.io.database.migration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.operation.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Computes a checksum over the structure of a list of {@link SqlMigrationOperation}s.<br>
 * <p>
 *     The checksum answers whether the body of a migration has been edited since it was applied. It therefore must not
 *     depend on anything that can change without the migration itself being edited. Migration operations hold live
 *     {@link SqlTable} and {@link SqlColumn} references, so their names and types are read from the schema definitions as
 *     they exist today, which changes whenever a table or column is renamed or retyped by a later migration.
 * </p>
 * <p>
 *     To stay stable across such changes every table, column and type is replaced by a token derived from the order in
 *     which it is first encountered while the operations are walked. Everything the author wrote literally, such as
 *     constraint names, index names, audit column names, flags and default values, is kept verbatim. Adding, removing or
 *     reordering operations, changing the number of columns of a table or breaking up the way tables, columns and types
 *     are shared between operations therefore changes the checksum, while a pure rename or retype of a referenced schema
 *     object does not.
 * </p>
 *
 * @author Luis-St
 */
class SqlMigrationChecksum {
	
	/**
	 * The prefix that marks a checksum as being computed over the structure of the operations.
	 */
	private static final String FORMAT = "s1:";
	/**
	 * The number of hex characters of the digest that are kept, chosen so that the prefixed checksum still fits into the
	 * 64 characters the migration store reserves for it.
	 */
	private static final int DIGEST_LENGTH = 61;
	
	/**
	 * The dialect used to render the conditions carried by the operations.
	 */
	private final SqlDialect dialect;
	/**
	 * The assigned table tokens mapped by the qualified name of the table.
	 */
	private final Map<String, String> tableTokens = Maps.newLinkedHashMap();
	/**
	 * The assigned column tokens mapped by the table token and the name of the column.
	 */
	private final Map<String, String> columnTokens = Maps.newLinkedHashMap();
	/**
	 * The assigned type tokens mapped by the type itself.
	 */
	private final Map<SqlType<?>, String> typeTokens = Maps.newLinkedHashMap();
	/**
	 * The assigned tokens mapped by the raw identifier they replace, used to rewrite rendered conditions.
	 */
	private final Map<String, String> identifierTokens = Maps.newLinkedHashMap();
	/**
	 * The conditions encountered while walking the operations, rendered after the walk has completed.
	 */
	private final List<SqlCondition> conditions = Lists.newArrayList();
	/**
	 * The canonical representation of the operations that is hashed.
	 */
	private final StringBuilder content = new StringBuilder();
	
	/**
	 * Constructs a new checksum computation for the given dialect.<br>
	 *
	 * @param dialect The dialect used to render the conditions carried by the operations
	 * @throws NullPointerException If the dialect is null
	 */
	private SqlMigrationChecksum(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Computes the structural checksum of the given migration operations.<br>
	 *
	 * @param operations The migration operations to compute the checksum for
	 * @param dialect The dialect used to render the conditions carried by the operations
	 * @return The prefixed and hex encoded SHA-256 checksum over the canonical representation of the operations
	 * @throws NullPointerException If the operations or dialect is null
	 * @throws IllegalStateException If the SHA-256 algorithm is not available
	 * @throws SqlException If a condition carried by an operation could not be rendered
	 */
	static @NonNull String compute(@NonNull List<SqlMigrationOperation> operations, @NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(operations, "Sql migration operations must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
		return FORMAT + new SqlMigrationChecksum(dialect).digest(operations);
	}
	
	/**
	 * Checks whether the given recorded checksum can be compared against a freshly computed one.<br>
	 * A checksum written by an earlier version of the library was computed over the rendered sql rather than over the
	 * structure of the operations, so it carries no prefix and must be treated as unknown instead of as a mismatch.<br>
	 *
	 * @param checksum The recorded checksum to check
	 * @return True if the recorded checksum is comparable, false otherwise
	 * @throws NullPointerException If the checksum is null
	 */
	static boolean isComparable(@NonNull String checksum) {
		Objects.requireNonNull(checksum, "Sql migration checksum must not be null");
		return checksum.startsWith(FORMAT);
	}
	
	/**
	 * Converts the given value into a stable string representation for the canonical representation.<br>
	 * A {@code null} value is rendered as {@code "null"}, byte arrays are hex encoded and other arrays are rendered
	 * element-wise, all remaining values use their {@link String#valueOf(Object)} representation.<br>
	 *
	 * @param value The value to convert
	 * @return The stable string representation of the value
	 */
	private static @NonNull String stableValue(@Nullable Object value) {
		return switch (value) {
			case null -> "null";
			case byte[] bytes -> HexFormat.of().formatHex(bytes);
			default -> value.getClass().isArray() ? arrayToString(value) : String.valueOf(value);
		};
	}
	
	/**
	 * Converts the given array into a stable string representation.<br>
	 * Each element is converted via {@link #stableValue(Object)} and the elements are joined with commas inside
	 * square brackets.<br>
	 *
	 * @param array The array to convert
	 * @return The stable string representation of the array
	 * @throws NullPointerException If the array is null
	 */
	private static @NonNull String arrayToString(@NonNull Object array) {
		Objects.requireNonNull(array, "Array must not be null");
		
		int length = Array.getLength(array);
		StringBuilder builder = new StringBuilder("[");
		for (int i = 0; i < length; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			
			builder.append(stableValue(Array.get(array, i)));
		}
		return builder.append(']').toString();
	}
	
	/**
	 * Walks the given operations, builds their canonical representation and hashes it.<br>
	 *
	 * @param operations The migration operations to digest
	 * @return The hex encoded SHA-256 checksum over the canonical representation, truncated to {@link #DIGEST_LENGTH} characters
	 * @throws IllegalStateException If the SHA-256 algorithm is not available
	 * @throws SqlException If a condition carried by an operation could not be rendered
	 */
	private @NonNull String digest(@NonNull List<SqlMigrationOperation> operations) throws SqlException {
		for (SqlMigrationOperation operation : operations) {
			this.appendOperation(operation);
		}
		this.appendConditions();
		
		try {
			byte[] hash = MessageDigest.getInstance("SHA-256").digest(this.content.toString().getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hash).substring(0, DIGEST_LENGTH);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 algorithm is not available", e);
		}
	}
	
	/**
	 * Appends the canonical representation of the given operation to the content.<br>
	 *
	 * @param operation The operation to append
	 * @throws NullPointerException If the operation is null
	 */
	private void appendOperation(@NonNull SqlMigrationOperation operation) {
		Objects.requireNonNull(operation, "Sql migration operation must not be null");
		
		switch (operation) {
			case SqlCreateTableOperation op -> {
				this.content.append("createTable ").append(this.table(op.table()));
				for (SqlColumnDefinition definition : op.columns()) {
					this.content.append(" column ").append(this.column(definition.column())).append(':').append(this.type(definition.type()));
					this.appendOptions(definition.options());
				}
				this.content.append(" primaryKey");
				for (SqlColumn<?, ?> column : op.primaryKeyColumns()) {
					this.content.append(' ').append(this.column(column));
				}
			}
			case SqlDropTableOperation op -> this.content.append("dropTable ").append(this.table(op.table()));
			case SqlRenameTableOperation op -> this.content.append("renameTable ").append(this.table(op.from())).append(' ').append(this.table(op.to()));
			case SqlAddColumnOperation op -> {
				this.content.append("addColumn ").append(this.column(op.column())).append(':').append(this.type(op.type()));
				this.appendOptions(op.options());
			}
			case SqlDropColumnOperation op -> this.content.append("dropColumn ").append(this.column(op.column()));
			case SqlRenameColumnOperation op -> this.content.append("renameColumn ").append(this.column(op.from())).append(' ').append(this.column(op.to()));
			case SqlAlterColumnOperation op -> {
				this.content.append("alterColumn ").append(this.column(op.column()));
				for (SqlColumnAlteration alteration : op.alterations()) {
					this.content.append(' ');
					switch (alteration) {
						case SqlSetTypeAlteration set -> this.content.append("setType:").append(this.type(set.type()));
						case SqlSetNullableAlteration set -> this.content.append("setNullable:").append(set.nullable());
						case SqlSetDefaultAlteration set -> this.content.append("setDefault:").append(stableValue(set.value()));
						case SqlDropDefaultAlteration _ -> this.content.append("dropDefault");
					}
				}
			}
			case SqlCreateIndexOperation op -> {
				this.content.append("createIndex ").append(this.table(op.table())).append(' ').append(op.index().name())
					.append(" unique:").append(op.index().unique()).append(" method:").append(op.index().method().name());
				for (SqlColumn<?, ?> column : op.index().columns()) {
					this.content.append(' ').append(this.column(column));
				}
				this.appendCondition(op.index().whereCondition());
			}
			case SqlDropIndexOperation op -> this.content.append("dropIndex ").append(this.nullableTable(op.table())).append(' ').append(op.index());
			case SqlRenameIndexOperation op -> this.content.append("renameIndex ").append(this.nullableTable(op.table())).append(' ').append(op.from()).append(' ').append(op.to());
			case SqlAddUniqueConstraintOperation op -> this.appendConstraint("addUniqueConstraint", op.table(), op.name(), op.columns());
			case SqlAddForeignKeyOperation op -> {
				this.appendConstraint("addForeignKey", op.table(), op.name(), op.columns());
				this.content.append(" references ").append(this.table(op.referencedTable()));
				for (SqlColumn<?, ?> column : op.referencedColumns()) {
					this.content.append(' ').append(this.column(column));
				}
				this.content.append(" onDelete:").append(op.onDelete().name()).append(" onUpdate:").append(op.onUpdate().name());
			}
			case SqlAddCheckConstraintOperation op -> {
				this.content.append("addCheckConstraint ").append(this.table(op.table())).append(' ').append(op.name());
				this.appendCondition(op.condition());
			}
			case SqlAddCompositePrimaryKeyOperation op -> this.appendConstraint("addCompositePrimaryKey", op.table(), op.name(), op.columns());
			case SqlDropConstraintOperation op -> this.content.append("dropConstraint ").append(this.table(op.table())).append(' ').append(op.name());
			case SqlEnableAuditingOperation op -> {
				this.content.append("enableAuditing ").append(this.table(op.table()));
				this.appendAuditConfig(op.config());
			}
			case SqlDisableAuditingOperation op -> {
				this.content.append("disableAuditing ").append(this.table(op.table()));
				this.appendAuditConfig(op.config());
			}
			case SqlExecuteDataOperation op -> this.content.append("executeData ").append(this.table(op.table()));
		}
		this.content.append('\n');
	}
	
	/**
	 * Appends the canonical representation of a constraint operation with the given kind, table, name and columns.<br>
	 *
	 * @param kind The kind of the constraint operation
	 * @param table The table the constraint is added to
	 * @param name The name of the constraint as written by the migration author
	 * @param columns The columns covered by the constraint
	 */
	private void appendConstraint(@NonNull String kind, @NonNull SqlTable<?> table, @NonNull String name, @NonNull List<SqlColumn<?, ?>> columns) {
		this.content.append(kind).append(' ').append(this.table(table)).append(' ').append(name);
		for (SqlColumn<?, ?> column : columns) {
			this.content.append(' ').append(this.column(column));
		}
	}
	
	/**
	 * Appends the canonical representation of the given column options to the content.<br>
	 *
	 * @param options The column options to append
	 * @throws NullPointerException If the options are null
	 */
	private void appendOptions(@NonNull SqlColumnOptions options) {
		Objects.requireNonNull(options, "Sql column options must not be null");
		
		this.content.append(" notNull:").append(options.notNull())
			.append(" unique:").append(options.unique())
			.append(" autoIncrement:").append(options.autoIncrement())
			.append(" default:").append(options.defaultValue().map(SqlMigrationChecksum::stableValue).orElse("none"))
			.append(" references:").append(this.nullableTable(options.referencesTable()));
		this.appendCondition(options.check());
	}
	
	/**
	 * Appends the canonical representation of the given audit config to the content.<br>
	 * The clock of the config is not part of the representation because it does not affect the emitted schema.<br>
	 *
	 * @param config The audit config to append
	 * @throws NullPointerException If the config is null
	 */
	private void appendAuditConfig(@NonNull SqlAuditConfig config) {
		Objects.requireNonNull(config, "Sql audit config must not be null");
		
		this.content.append(" version:").append(config.versionColumn())
			.append(" createdAt:").append(config.createdAtColumn())
			.append(" createdBy:").append(config.createdByColumn())
			.append(" updatedAt:").append(config.updatedAtColumn())
			.append(" updatedBy:").append(config.updatedByColumn())
			.append(" versionType:").append(this.type(config.versionType()))
			.append(" timestampType:").append(this.type(config.timestampType()))
			.append(" userType:").append(this.type(config.userType()))
			.append(" valueSource:").append(config.valueSource().name());
	}
	
	/**
	 * Appends a reference to the given condition to the content and remembers the condition for later rendering.<br>
	 * The condition itself is rendered only after every operation has been walked, so that the rendered sql can be
	 * rewritten with the complete set of assigned tokens.<br>
	 *
	 * @param condition The condition to reference or {@code null} if there is none
	 */
	private void appendCondition(@Nullable SqlCondition condition) {
		if (condition == null) {
			this.content.append(" check:none");
			return;
		}
		
		this.content.append(" check:#").append(this.conditions.size());
		this.conditions.add(condition);
	}
	
	/**
	 * Appends the canonical representation of every remembered condition to the content.<br>
	 * Each condition is rendered with the dialect and every identifier in the rendered sql is replaced by the token that
	 * was assigned to it while the operations were walked.<br>
	 *
	 * @throws SqlException If a condition could not be rendered
	 */
	private void appendConditions() throws SqlException {
		for (int i = 0; i < this.conditions.size(); i++) {
			SqlRendered rendered = this.dialect.renderCheckCondition(this.conditions.get(i));
			
			this.content.append('#').append(i).append(' ').append(this.replaceIdentifiers(rendered.sql()));
			for (Pair<SqlType<?>, Object> parameter : rendered.parameters()) {
				this.content.append(' ').append(this.type(parameter.getFirst())).append('=').append(stableValue(parameter.getSecond()));
			}
			this.content.append('\n');
		}
	}
	
	/**
	 * Replaces every known identifier in the given sql with the token that was assigned to it.<br>
	 * The identifiers are replaced longest first so that an identifier which is a prefix of another one does not corrupt
	 * the replacement.<br>
	 *
	 * @param sql The rendered sql to rewrite
	 * @return The rewritten sql with every known identifier replaced by its token
	 */
	private @NonNull String replaceIdentifiers(@NonNull String sql) {
		List<String> identifiers = Lists.newArrayList(this.identifierTokens.keySet());
		identifiers.sort(Comparator.comparingInt(String::length).reversed().thenComparing(Comparator.naturalOrder()));
		
		String result = sql;
		for (String identifier : identifiers) {
			String replacement = Matcher.quoteReplacement('<' + this.identifierTokens.get(identifier) + '>');
			result = result.replaceAll("\\b" + Pattern.quote(identifier) + "\\b", replacement);
		}
		return result;
	}
	
	/**
	 * Returns the token assigned to the given table, assigning a new one if the table has not been seen before.<br>
	 *
	 * @param table The table to return the token for
	 * @return The token assigned to the table
	 * @throws NullPointerException If the table is null
	 */
	private @NonNull String table(@NonNull SqlTable<?> table) {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		String key = table.schema() + '.' + table.name();
		String token = this.tableTokens.get(key);
		if (token == null) {
			token = "t" + this.tableTokens.size();
			this.tableTokens.put(key, token);
		}
		
		this.identifierTokens.putIfAbsent(table.name(), token);
		return token;
	}
	
	/**
	 * Returns the token assigned to the given table or {@code "none"} if the table is null.<br>
	 *
	 * @param table The table to return the token for
	 * @return The token assigned to the table or {@code "none"}
	 */
	private @NonNull String nullableTable(@Nullable SqlTable<?> table) {
		return table == null ? "none" : this.table(table);
	}
	
	/**
	 * Returns the token assigned to the given column, assigning a new one if the column has not been seen before.<br>
	 * The returned token is qualified with the token of the table owning the column.<br>
	 *
	 * @param column The column to return the token for
	 * @return The qualified token assigned to the column
	 * @throws NullPointerException If the column is null
	 */
	private @NonNull String column(@NonNull SqlColumn<?, ?> column) {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		String tableToken = this.table(column.owningTable());
		String key = tableToken + '.' + column.name();
		String token = this.columnTokens.get(key);
		if (token == null) {
			token = "c" + this.columnTokens.size();
			this.columnTokens.put(key, token);
		}
		
		this.identifierTokens.putIfAbsent(column.name(), token);
		return tableToken + '.' + token;
	}
	
	/**
	 * Returns the token assigned to the given type, assigning a new one if the type has not been seen before.<br>
	 *
	 * @param type The type to return the token for
	 * @return The token assigned to the type
	 * @throws NullPointerException If the type is null
	 */
	private @NonNull String type(@NonNull SqlType<?> type) {
		Objects.requireNonNull(type, "Sql type must not be null");
		
		String token = this.typeTokens.get(type);
		if (token == null) {
			token = "y" + this.typeTokens.size();
			this.typeTokens.put(type, token);
		}
		return token;
	}
}
