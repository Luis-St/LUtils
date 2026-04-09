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

package net.luis.utils.io.database.dialect;

import com.google.common.collect.Lists;
import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.exception.dialect.*;
import net.luis.utils.io.database.function.SqlDefaultFunctionType;
import net.luis.utils.io.database.function.SqlFunctionType;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.query.SqlSetOperation;
import net.luis.utils.io.database.rendering.*;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.type.*;
import net.luis.utils.io.database.type.parameter.*;
import org.jspecify.annotations.NonNull;

import java.sql.Types;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public abstract class AbstractSqlDialect implements SqlDialect {
	
	@Override
	public boolean isTypeSupported(@NonNull SqlType<?> type) {
		Objects.requireNonNull(type, "Type must not be null");
		return !(this.resolveBaseType(type) instanceof SqlArrayType<?>);
	}
	
	@Override
	public @NonNull String getTypeName(@NonNull SqlType<?> type) throws SqlDialectUnsupportedTypeException {
		Objects.requireNonNull(type, "Type must not be null");
		if (!this.isTypeSupported(type)) {
			throw new SqlDialectUnsupportedTypeException("Type " + type + " is not supported by dialect " + this.name());
		}
		
		SqlType<?> resolved = this.resolveBaseType(type);
		if (resolved instanceof SqlScalarType<?> scalar) {
			return this.getScalarTypeName(scalar.jdbcType());
		} else if (resolved instanceof ParameterizedSqlType<?, ?> parameterized) {
			return this.getParameterizedTypeName(parameterized.jdbcType(), parameterized.parameter());
		}
		throw new SqlDialectUnsupportedTypeException("Unknown type structure: " + type);
	}
	
	protected @NonNull SqlType<?> resolveBaseType(@NonNull SqlType<?> type) {
		if (type instanceof MappedSqlType<?, ?> mapped) {
			return this.resolveBaseType(mapped.sourceType());
		}
		return type;
	}
	
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedTypeException {
		return switch (jdbcType) {
			case Types.BOOLEAN -> "BOOLEAN";
			case Types.TINYINT -> "TINYINT";
			case Types.SMALLINT -> "SMALLINT";
			case Types.INTEGER -> "INTEGER";
			case Types.BIGINT -> "BIGINT";
			case Types.REAL -> "REAL";
			case Types.FLOAT -> "FLOAT";
			case Types.DOUBLE -> "DOUBLE PRECISION";
			case Types.LONGVARCHAR -> "TEXT";
			case Types.LONGNVARCHAR -> "NCLOB";
			case Types.CLOB -> "CLOB";
			case Types.NCLOB -> "NCLOB";
			case Types.LONGVARBINARY -> "BLOB";
			case Types.BLOB -> "BLOB";
			case Types.DATE -> "DATE";
			default -> throw new SqlDialectUnsupportedTypeException("Unsupported scalar JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedTypeException {
		return switch (parameter) {
			case SqlLengthParameter length -> switch (jdbcType) {
				case Types.CHAR -> "CHAR(" + length.length() + ")";
				case Types.VARCHAR -> "VARCHAR(" + length.length() + ")";
				case Types.NCHAR -> "NCHAR(" + length.length() + ")";
				case Types.NVARCHAR -> "NVARCHAR(" + length.length() + ")";
				case Types.BINARY -> "BINARY(" + length.length() + ")";
				case Types.VARBINARY -> "VARBINARY(" + length.length() + ")";
				default -> throw new SqlDialectUnsupportedTypeException("Unsupported length-parameterized JDBC type: " + jdbcType + " in dialect " + this.name());
			};
			case SqlPrecisionParameter precision -> switch (jdbcType) {
				case Types.NUMERIC -> "NUMERIC(" + precision.precision() + ", " + precision.scale() + ")";
				case Types.DECIMAL -> "DECIMAL(" + precision.precision() + ", " + precision.scale() + ")";
				default -> throw new SqlDialectUnsupportedTypeException("Unsupported precision-parameterized JDBC type: " + jdbcType + " in dialect " + this.name());
			};
			case SqlFractionalParameter fractional -> switch (jdbcType) {
				case Types.TIME -> "TIME(" + fractional.digits() + ")";
				case Types.TIMESTAMP -> "TIMESTAMP(" + fractional.digits() + ")";
				case Types.TIME_WITH_TIMEZONE -> "TIME(" + fractional.digits() + ") WITH TIME ZONE";
				case Types.TIMESTAMP_WITH_TIMEZONE -> "TIMESTAMP(" + fractional.digits() + ") WITH TIME ZONE";
				default -> throw new SqlDialectUnsupportedTypeException("Unsupported fractional-parameterized JDBC type: " + jdbcType + " in dialect " + this.name());
			};
			default -> throw new SqlDialectUnsupportedTypeException("Unsupported parameter type: " + parameter.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	@Override
	public boolean isFunctionSupported(@NonNull SqlFunctionType type) {
		Objects.requireNonNull(type, "Function type must not be null");
		return type instanceof SqlDefaultFunctionType;
	}
	
	@Override
	public @NonNull SqlRendered renderFunction(@NonNull SqlFunctionType type, @NonNull List<SqlRendered> arguments) throws SqlDialectUnsupportedFunctionException {
		Objects.requireNonNull(type, "Function type must not be null");
		Objects.requireNonNull(arguments, "Arguments must not be null");
		if (!this.isFunctionSupported(type)) {
			throw new SqlDialectUnsupportedFunctionException("Function " + type + " is not supported by dialect " + this.name());
		}
		
		if (type instanceof SqlDefaultFunctionType defaultType) {
			return this.renderDefaultFunction(defaultType, arguments);
		}
		throw new SqlDialectUnsupportedFunctionException("Unknown function type: " + type + " in dialect " + this.name());
	}
	
	protected @NonNull SqlRendered renderDefaultFunction(@NonNull SqlDefaultFunctionType type, @NonNull List<SqlRendered> arguments) throws SqlDialectUnsupportedFunctionException {
		return switch (type) {
			// Comparison operators
			case EQUAL_TO -> this.renderInfix("=", arguments);
			case GREATER_THAN -> this.renderInfix(">", arguments);
			case GREATER_THAN_OR_EQUAL_TO -> this.renderInfix(">=", arguments);
			case LESS_THAN -> this.renderInfix("<", arguments);
			case LESS_THAN_OR_EQUAL_TO -> this.renderInfix("<=", arguments);
			case IS_DISTINCT_FROM -> this.renderInfix("IS DISTINCT FROM", arguments);
			case IS_NULL -> SimpleSqlRendered.of(arguments.getFirst().sql() + " IS NULL", arguments.getFirst().parameters());
			case BETWEEN -> SimpleSqlRendered.of(
				arguments.get(0).sql() + " BETWEEN " + arguments.get(1).sql() + " AND " + arguments.get(2).sql(),
				this.collectParameters(arguments)
			);
			case IN_LIST -> {
				String placeholders = arguments.subList(1, arguments.size()).stream().map(SqlRendered::sql).collect(Collectors.joining(", "));
				yield SimpleSqlRendered.of(arguments.getFirst().sql() + " IN (" + placeholders + ")", this.collectParameters(arguments));
			}
			case IN_SUBQUERY -> SimpleSqlRendered.of(
				arguments.get(0).sql() + " IN (" + arguments.get(1).sql() + ")",
				this.collectParameters(arguments)
			);
			
			// Aggregate functions
			case COUNT -> arguments.isEmpty() ? SimpleSqlRendered.of("COUNT(*)") : this.renderFunctionCall("COUNT", arguments);
			case COUNT_DISTINCT -> SimpleSqlRendered.of("COUNT(DISTINCT " + arguments.getFirst().sql() + ")", arguments.getFirst().parameters());
			case SUM -> this.renderFunctionCall("SUM", arguments);
			case AVERAGE -> this.renderFunctionCall("AVG", arguments);
			case MIN -> this.renderFunctionCall("MIN", arguments);
			case MAX -> this.renderFunctionCall("MAX", arguments);
			
			// Logic functions
			case COALESCE -> this.renderFunctionCall("COALESCE", arguments);
			case NULLIF -> this.renderFunctionCall("NULLIF", arguments);
			case GREATEST -> this.renderFunctionCall("GREATEST", arguments);
			case LEAST -> this.renderFunctionCall("LEAST", arguments);
			case CAST -> SimpleSqlRendered.of(
				"CAST(" + arguments.get(0).sql() + " AS " + arguments.get(1).sql() + ")",
				this.collectParameters(arguments)
			);
			case CASE_WHEN -> this.renderCaseWhen(arguments);
			case UNSAFE -> arguments.getFirst();
			
			// Window functions
			case ROW_NUMBER -> SimpleSqlRendered.of("ROW_NUMBER()");
			case RANK -> SimpleSqlRendered.of("RANK()");
			case DENSE_RANK -> SimpleSqlRendered.of("DENSE_RANK()");
			case TILE_BUCKET -> this.renderFunctionCall("NTILE", arguments);
			case LAG -> this.renderFunctionCall("LAG", arguments);
			case LEAD -> this.renderFunctionCall("LEAD", arguments);
			case PERCENT_RANK -> SimpleSqlRendered.of("PERCENT_RANK()");
			case CUMULATIVE_DISTRIBUTION -> SimpleSqlRendered.of("CUME_DIST()");
			case FIRST_VALUE -> this.renderFunctionCall("FIRST_VALUE", arguments);
			case LAST_VALUE -> this.renderFunctionCall("LAST_VALUE", arguments);
			case VALUE_AT -> this.renderFunctionCall("NTH_VALUE", arguments);
			
			// Numeric functions
			case IS_POSITIVE -> SimpleSqlRendered.of(arguments.getFirst().sql() + " > 0", arguments.getFirst().parameters());
			case IS_NEGATIVE -> SimpleSqlRendered.of(arguments.getFirst().sql() + " < 0", arguments.getFirst().parameters());
			case IS_ZERO -> SimpleSqlRendered.of(arguments.getFirst().sql() + " = 0", arguments.getFirst().parameters());
			case MOD_EQUALS -> SimpleSqlRendered.of(
				"MOD(" + arguments.get(0).sql() + ", " + arguments.get(1).sql() + ") = " + arguments.get(2).sql(),
				this.collectParameters(arguments)
			);
			case RANDOM -> SimpleSqlRendered.of("RANDOM()");
			case PI -> SimpleSqlRendered.of("PI()");
			case NEGATE -> SimpleSqlRendered.of("-(" + arguments.getFirst().sql() + ")", arguments.getFirst().parameters());
			case ABS -> this.renderFunctionCall("ABS", arguments);
			case ROUND -> this.renderFunctionCall("ROUND", arguments);
			case CEIL -> this.renderFunctionCall("CEIL", arguments);
			case FLOOR -> this.renderFunctionCall("FLOOR", arguments);
			case TRUNCATE -> this.renderFunctionCall("TRUNCATE", arguments);
			case MOD -> this.renderFunctionCall("MOD", arguments);
			case POW -> this.renderFunctionCall("POWER", arguments);
			case SQRT -> this.renderFunctionCall("SQRT", arguments);
			case SIGN -> this.renderFunctionCall("SIGN", arguments);
			case EXP -> this.renderFunctionCall("EXP", arguments);
			case LOG2 -> SimpleSqlRendered.of("LOG(2, " + arguments.getFirst().sql() + ")", arguments.getFirst().parameters());
			case LN -> this.renderFunctionCall("LN", arguments);
			case LOG10 -> SimpleSqlRendered.of("LOG(10, " + arguments.getFirst().sql() + ")", arguments.getFirst().parameters());
			case SIN -> this.renderFunctionCall("SIN", arguments);
			case COS -> this.renderFunctionCall("COS", arguments);
			case TAN -> this.renderFunctionCall("TAN", arguments);
			case ASIN -> this.renderFunctionCall("ASIN", arguments);
			case ACOS -> this.renderFunctionCall("ACOS", arguments);
			case ATAN -> this.renderFunctionCall("ATAN", arguments);
			case ATAN2 -> this.renderFunctionCall("ATAN2", arguments);
			case RADIANS -> this.renderFunctionCall("RADIANS", arguments);
			case DEGREES -> this.renderFunctionCall("DEGREES", arguments);
			case BITWISE_AND -> this.renderInfix("&", arguments);
			case BITWISE_OR -> this.renderInfix("|", arguments);
			case BITWISE_XOR -> this.renderInfix("^", arguments);
			case BITWISE_NOT -> SimpleSqlRendered.of("~(" + arguments.getFirst().sql() + ")", arguments.getFirst().parameters());
			
			// String functions
			case STARTS_WITH -> SimpleSqlRendered.of(
				arguments.get(0).sql() + " LIKE " + arguments.get(1).sql() + " || '%'",
				this.collectParameters(arguments)
			);
			case CONTAINS -> SimpleSqlRendered.of(
				arguments.get(0).sql() + " LIKE '%' || " + arguments.get(1).sql() + " || '%'",
				this.collectParameters(arguments)
			);
			case ENDS_WITH -> SimpleSqlRendered.of(
				arguments.get(0).sql() + " LIKE '%' || " + arguments.get(1).sql(),
				this.collectParameters(arguments)
			);
			case LIKE -> this.renderInfix("LIKE", arguments);
			case EQUALS_IGNORE_CASE -> SimpleSqlRendered.of(
				"UPPER(" + arguments.get(0).sql() + ") = UPPER(" + arguments.get(1).sql() + ")",
				this.collectParameters(arguments)
			);
			case LOWER -> this.renderFunctionCall("LOWER", arguments);
			case UPPER -> this.renderFunctionCall("UPPER", arguments);
			case TRIM -> this.renderFunctionCall("TRIM", arguments);
			case LEFT_TRIM -> this.renderFunctionCall("LTRIM", arguments);
			case RIGHT_TRIM -> this.renderFunctionCall("RTRIM", arguments);
			case TRIM_CHARS -> SimpleSqlRendered.of(
				"TRIM(" + arguments.get(1).sql() + " FROM " + arguments.get(0).sql() + ")",
				this.collectParameters(arguments)
			);
			case LENGTH -> this.renderFunctionCall("CHAR_LENGTH", arguments);
			case SUBSTRING -> {
				if (arguments.size() == 3) {
					yield SimpleSqlRendered.of(
						"SUBSTRING(" + arguments.get(0).sql() + " FROM " + arguments.get(1).sql() + " FOR " + arguments.get(2).sql() + ")",
						this.collectParameters(arguments)
					);
				}
				yield SimpleSqlRendered.of(
					"SUBSTRING(" + arguments.get(0).sql() + " FROM " + arguments.get(1).sql() + ")",
					this.collectParameters(arguments)
				);
			}
			case CONCAT -> {
				String sql = arguments.stream().map(SqlRendered::sql).collect(Collectors.joining(" || "));
				yield SimpleSqlRendered.of(sql, this.collectParameters(arguments));
			}
			case CONCAT_WITH_SEPARATOR -> {
				String sep = arguments.getFirst().sql();
				String exprs = arguments.subList(1, arguments.size()).stream().map(SqlRendered::sql).collect(Collectors.joining(" || " + sep + " || "));
				yield SimpleSqlRendered.of(exprs, this.collectParameters(arguments));
			}
			case CONCAT_DISTINCT_WITH_SEPARATOR -> this.renderFunctionCall("STRING_AGG", List.of(arguments.get(1), arguments.get(0)));
			case CONCAT_ORDERED_WITH_SEPARATOR -> this.renderFunctionCall("STRING_AGG", List.of(arguments.get(1), arguments.get(0)));
			case REPLACE -> this.renderFunctionCall("REPLACE", arguments);
			case POSITION -> SimpleSqlRendered.of(
				"POSITION(" + arguments.get(0).sql() + " IN " + arguments.get(1).sql() + ")",
				this.collectParameters(arguments)
			);
			case LEFT -> this.renderFunctionCall("LEFT", arguments);
			case RIGHT -> this.renderFunctionCall("RIGHT", arguments);
			case LEFT_PAD -> this.renderFunctionCall("LPAD", arguments);
			case RIGHT_PAD -> this.renderFunctionCall("RPAD", arguments);
			case HEX -> this.renderFunctionCall("HEX", arguments);
			case UNHEX -> this.renderFunctionCall("UNHEX", arguments);
			
			// Temporal functions
			case NOW -> SimpleSqlRendered.of("CURRENT_TIMESTAMP");
			case CURRENT_DATE -> SimpleSqlRendered.of("CURRENT_DATE");
			case CURRENT_TIME -> SimpleSqlRendered.of("CURRENT_TIME");
			case CURRENT_TIMESTAMP -> SimpleSqlRendered.of("CURRENT_TIMESTAMP");
			case WITHIN_LAST -> SimpleSqlRendered.of(
				arguments.get(0).sql() + " >= CURRENT_TIMESTAMP - " + arguments.get(1).sql(),
				this.collectParameters(arguments)
			);
			case WITHIN_NEXT -> SimpleSqlRendered.of(
				arguments.get(0).sql() + " <= CURRENT_TIMESTAMP + " + arguments.get(1).sql(),
				this.collectParameters(arguments)
			);
			case BEFORE -> this.renderInfix("<", arguments);
			case AFTER -> this.renderInfix(">", arguments);
			case FROM_EPOCH -> this.renderFunctionCall("FROM_UNIXTIME", arguments);
			case MAKE_DATE -> this.renderFunctionCall("MAKE_DATE", arguments);
			case MAKE_TIME -> this.renderFunctionCall("MAKE_TIME", arguments);
			case EXTRACT -> SimpleSqlRendered.of(
				"EXTRACT(" + arguments.get(0).sql() + " FROM " + arguments.get(1).sql() + ")",
				this.collectParameters(arguments)
			);
			case TRUNCATE_TEMPORAL -> this.renderFunctionCall("DATE_TRUNC", arguments);
			case ADD_TEMPORAL -> this.renderInfix("+", arguments);
			case SUBTRACT_TEMPORAL -> this.renderInfix("-", arguments);
			case TO_EPOCH -> SimpleSqlRendered.of(
				"EXTRACT(EPOCH FROM " + arguments.getFirst().sql() + ")",
				arguments.getFirst().parameters()
			);
			case TO_DATE -> SimpleSqlRendered.of(
				"CAST(" + arguments.getFirst().sql() + " AS DATE)",
				arguments.getFirst().parameters()
			);
			case TO_TIME -> SimpleSqlRendered.of(
				"CAST(" + arguments.getFirst().sql() + " AS TIME)",
				arguments.getFirst().parameters()
			);
		};
	}
	
	protected @NonNull SqlRendered renderInfix(@NonNull String operator, @NonNull List<SqlRendered> arguments) {
		if (arguments.size() != 2) {
			throw new IllegalArgumentException("Infix operator requires exactly 2 arguments");
		}
		
		return SimpleSqlRendered.of(
			arguments.get(0).sql() + " " + operator + " " + arguments.get(1).sql(),
			this.collectParameters(arguments)
		);
	}
	
	protected @NonNull SqlRendered renderFunctionCall(@NonNull String functionName, @NonNull List<SqlRendered> arguments) {
		String argsSql = arguments.stream().map(SqlRendered::sql).collect(Collectors.joining(", "));
		return SimpleSqlRendered.of(functionName + "(" + argsSql + ")", this.collectParameters(arguments));
	}
	
	protected @NonNull SqlRendered renderCaseWhen(@NonNull List<SqlRendered> arguments) {
		SqlRenderer renderer = new SqlRenderer();
		renderer.case_();
		
		int i = 0;
		while (i + 1 < arguments.size()) {
			renderer.when().rendered(arguments.get(i));
			renderer.then().rendered(arguments.get(i + 1));
			i += 2;
		}
		
		if (i < arguments.size()) {
			renderer.else_().rendered(arguments.get(i));
		}
		renderer.end();
		return renderer.toSql(this);
	}
	
	protected @NonNull List<Object> collectParameters(@NonNull List<SqlRendered> arguments) {
		List<Object> parameters = Lists.newArrayList();
		for (SqlRendered argument : arguments) {
			parameters.addAll(argument.parameters());
		}
		return parameters;
	}
	
	@Override
	public boolean isFeatureSupported(@NonNull SqlFeature feature) {
		Objects.requireNonNull(feature, "Feature must not be null");
		return false;
	}
	
	@Override
	public boolean isIndexMethodSupported(@NonNull SqlIndexMethod method) {
		Objects.requireNonNull(method, "Index method must not be null");
		return method == SqlIndexMethod.BTREE;
	}
	
	@Override
	public @NonNull String getIndexMethodName(@NonNull SqlIndexMethod method) throws SqlDialectUnsupportedIndexMethodException {
		Objects.requireNonNull(method, "Index method must not be null");
		if (!this.isIndexMethodSupported(method)) {
			throw new SqlDialectUnsupportedIndexMethodException("Index method " + method + " is not supported by dialect " + this.name());
		}
		return method.name();
	}
	
	@Override
	public @NonNull String quoteIdentifier(@NonNull String identifier) {
		Objects.requireNonNull(identifier, "Identifier must not be null");
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}
	
	@Override
	public @NonNull String renderQualifiedName(@NonNull String schema, @NonNull String name) {
		Objects.requireNonNull(schema, "Schema must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		return this.quoteIdentifier(schema) + "." + this.quoteIdentifier(name);
	}
	
	@Override
	public @NonNull SqlRendered renderCreateTable(@NonNull SqlTable<?> table, boolean ifNotExists) {
		Objects.requireNonNull(table, "Table must not be null");
		SqlRenderer renderer = new SqlRenderer();
		renderer.create().table();
		if (ifNotExists) {
			renderer.if_().not().exists();
		}
		
		renderer.literal(this.quoteIdentifier(table.getName()));
		renderer.openingBracket();
		
		boolean hasCompositeKey = table.getCompositePrimaryKey().isPresent();
		List<? extends SqlColumn<?, ?>> columns = table.getColumns();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.rendered(this.renderColumnForTable(columns.get(i), hasCompositeKey));
		}
		
		SqlRendered tableConstraints = this.renderTableConstraints(table);
		if (!tableConstraints.sql().isEmpty()) {
			renderer.comma().rendered(tableConstraints);
		}
		
		renderer.closingBracket();
		return renderer.toSql(this);
	}
	
	@Override
	public @NonNull SqlRendered renderDropTable(@NonNull SqlTable<?> table, boolean ifExists) {
		Objects.requireNonNull(table, "Table must not be null");
		SqlRenderer renderer = new SqlRenderer();
		renderer.drop().table();
		if (ifExists) {
			renderer.if_().exists();
		}
		renderer.literal(this.quoteIdentifier(table.getName()));
		return renderer.toSql(this);
	}
	
	@Override
	public @NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table) {
		Objects.requireNonNull(table, "Table must not be null");
		SqlRenderer renderer = new SqlRenderer();
		renderer.truncate().table().literal(this.quoteIdentifier(table.getName()));
		return renderer.toSql(this);
	}
	
	@Override
	public @NonNull SqlRendered renderColumnDefinition(@NonNull SqlColumn<?, ?> column) {
		Objects.requireNonNull(column, "Column must not be null");
		return this.renderColumnForTable(column, false);
	}
	
	protected void renderForeignKey(@NonNull SqlRenderer renderer, @NonNull SqlForeignKey<?, ?> fk) {
		Objects.requireNonNull(fk, "Foreign key must not be null");
		
		renderer.literal(this.quoteIdentifier(fk.getReferencedTable().getName()));
		renderer.openingBracket();
		List<? extends SqlColumn<?, ?>> referencedColumns = fk.getReferencedColumns();
		for (int i = 0; i < referencedColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(this.quoteIdentifier(referencedColumns.get(i).getName()));
		}
		renderer.closingBracket();
		
		if (fk.getOnUpdate() != SqlReferentialAction.NO_ACTION) {
			renderer.on().update();
			this.renderReferentialAction(renderer, fk.getOnUpdate());
		}
		if (fk.getOnDelete() != SqlReferentialAction.NO_ACTION) {
			renderer.on().delete();
			this.renderReferentialAction(renderer, fk.getOnDelete());
		}
	}
	
	protected @NonNull SqlRendered renderColumnForTable(@NonNull SqlColumn<?, ?> column, boolean skipPrimaryKey) {
		SqlRenderer renderer = new SqlRenderer();
		renderer.literal(this.quoteIdentifier(column.getName()));
		
		try {
			renderer.literal(this.getTypeName(column.getType()));
		} catch (SqlDialectUnsupportedTypeException e) {
			throw new IllegalArgumentException("Column type is not supported by dialect " + this.name(), e);
		}
		
		if (!column.isNullable()) {
			renderer.not().null_();
		}
		if (column.getDefaultValue().isPresent()) {
			renderer.default_().parameter(column.getDefaultValue().get());
		}
		if (column.isAutoIncrement()) {
			this.renderAutoIncrement(renderer, column);
		}
		if (column.isPrimaryKey() && !skipPrimaryKey) {
			renderer.primary().key();
		}
		if (column.isUnique()) {
			renderer.unique();
		}
		
		if (column.getForeignKey().isPresent()) {
			renderer.references();
			this.renderForeignKey(renderer, column.getForeignKey().get());
		}
		
		for (SqlCondition check : column.getChecks()) {
			renderer.check().openingBracket().rendered(check.toSql(this)).closingBracket();
		}
		
		return renderer.toSql(this);
	}
	
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) {
		renderer.keyword("GENERATED").keyword("ALWAYS").as().keyword("IDENTITY");
	}
	
	protected void renderReferentialAction(@NonNull SqlRenderer renderer, @NonNull SqlReferentialAction action) {
		switch (action) {
			case NO_ACTION -> renderer.noAction();
			case RESTRICT -> renderer.restrict();
			case CASCADE -> renderer.cascade();
			case SET_NULL -> renderer.setNull();
			case SET_DEFAULT -> renderer.setDefault();
		}
	}
	
	protected @NonNull SqlRendered renderTableConstraints(@NonNull SqlTable<?> table) {
		SqlRenderer renderer = new SqlRenderer();
		boolean first = true;
		
		if (table.getCompositePrimaryKey().isPresent()) {
			SqlCompositePrimaryKey<?> pk = table.getCompositePrimaryKey().get();
			renderer.primary().key().openingBracket();
			List<? extends SqlColumn<?, ?>> pkColumns = pk.columns();
			for (int i = 0; i < pkColumns.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				renderer.literal(this.quoteIdentifier(pkColumns.get(i).getName()));
			}
			renderer.closingBracket();
			first = false;
		}
		
		for (SqlForeignKey<?, ?> fk : table.getForeignKeys()) {
			if (!first) {
				renderer.comma();
			}
			renderer.foreign().key().openingBracket();
			List<? extends SqlColumn<?, ?>> referencingColumns = fk.getReferencingColumns();
			for (int i = 0; i < referencingColumns.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				renderer.literal(this.quoteIdentifier(referencingColumns.get(i).getName()));
			}
			renderer.closingBracket().references();
			this.renderForeignKey(renderer, fk);
			first = false;
		}
		
		for (List<? extends SqlColumn<?, ?>> uniqueColumns : table.getUniqueConstraints()) {
			if (!first) {
				renderer.comma();
			}
			renderer.unique().openingBracket();
			for (int i = 0; i < uniqueColumns.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				renderer.literal(this.quoteIdentifier(uniqueColumns.get(i).getName()));
			}
			renderer.closingBracket();
			first = false;
		}
		
		for (SqlCondition check : table.getCheckConstraints()) {
			if (!first) {
				renderer.comma();
			}
			renderer.check().openingBracket().rendered(check.toSql(this)).closingBracket();
			first = false;
		}
		
		return renderer.toSql(this);
	}
	
	@Override
	public @NonNull SqlRendered renderCreateIndex(@NonNull SqlIndex index) {
		Objects.requireNonNull(index, "Index must not be null");
		SqlRenderer renderer = new SqlRenderer();
		renderer.create();
		if (index.unique()) {
			renderer.unique();
		}
		
		renderer.index().literal(this.quoteIdentifier(index.name()));
		renderer.on().literal(this.quoteIdentifier(index.columns().getFirst().getOwningTable().getName()));
		
		try {
			renderer.using().literal(this.getIndexMethodName(index.method()));
		} catch (SqlDialectUnsupportedIndexMethodException e) {
			throw new IllegalArgumentException("Index method is not supported by dialect " + this.name(), e);
		}
		
		renderer.openingBracket();
		List<? extends SqlColumn<?, ?>> columns = index.columns();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(this.quoteIdentifier(columns.get(i).getName()));
		}
		renderer.closingBracket();
		
		if (index.whereCondition() != null) {
			renderer.where().rendered(index.whereCondition().toSql(this));
		}
		return renderer.toSql(this);
	}
	
	@Override
	public @NonNull SqlRendered renderDropIndex(@NonNull SqlIndex index) {
		Objects.requireNonNull(index, "Index must not be null");
		SqlRenderer renderer = new SqlRenderer();
		renderer.drop().index().literal(this.quoteIdentifier(index.name()));
		return renderer.toSql(this);
	}
	
	@Override
	public @NonNull SqlRendered renderLimitOffset(long limit, long offset) {
		if (limit < 0) {
			throw new IllegalArgumentException("Limit must be non-negative");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		
		SqlRenderer renderer = new SqlRenderer();
		if (limit > 0) {
			renderer.limit().literal(String.valueOf(limit));
		}
		if (offset > 0) {
			renderer.offset().literal(String.valueOf(offset));
		}
		return renderer.toSql(this);
	}
	
	@Override
	public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlDialectUnsupportedFeatureException {
		throw new SqlDialectUnsupportedFeatureException("RETURNING clause is not supported by dialect " + this.name());
	}
	
	@Override
	public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlDialectUnsupportedFeatureException {
		Objects.requireNonNull(mode, "Lock mode must not be null");
		SqlRenderer renderer = new SqlRenderer();
		
		switch (mode) {
			case FOR_UPDATE -> renderer.for_().update();
			case FOR_SHARE -> renderer.for_().share();
		}
		if (skipLocked) {
			renderer.skip().locked();
		}
		if (noWait) {
			renderer.nowait();
		}
		return renderer.toSql(this);
	}
	
	@Override
	public @NonNull String renderSetOperation(@NonNull SqlSetOperation operation) {
		Objects.requireNonNull(operation, "Set operation must not be null");
		return switch (operation) {
			case UNION -> "UNION";
			case UNION_ALL -> "UNION ALL";
			case INTERSECT -> "INTERSECT";
			case EXCEPT -> "EXCEPT";
		};
	}
	
	@Override
	public @NonNull String renderLateralJoin() throws SqlDialectUnsupportedFeatureException {
		throw new SqlDialectUnsupportedFeatureException("LATERAL join is not supported by dialect " + this.name());
	}
	
	@Override
	public @NonNull String renderBooleanLiteral(boolean value) {
		return value ? "TRUE" : "FALSE";
	}
}
