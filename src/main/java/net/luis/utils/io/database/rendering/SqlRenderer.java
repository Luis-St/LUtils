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

package net.luis.utils.io.database.rendering;

import com.google.common.collect.Lists;
import net.luis.utils.io.database.dialect.SqlDialect;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("NewMethodNamingConvention")
public class SqlRenderer {
	
	private final List<String> statements = Lists.newArrayList();
	private final List<Object> parameters = Lists.newArrayList();
	
	public SqlRenderer(@NonNull List<String> statements, @NonNull List<Object> parameters) {
		this.statements.addAll(Objects.requireNonNull(statements, "Sql statements must not be null"));
		this.parameters.addAll(Objects.requireNonNull(parameters, "Sql parameters must not be null"));
	}
	
	public static @NonNull SqlRenderer empty() {
		return new SqlRenderer(Lists.newArrayList(), Lists.newArrayList());
	}
	
	public @NonNull @Unmodifiable List<String> getStatements() {
		return List.copyOf(this.statements);
	}
	
	public @NonNull @Unmodifiable List<Object> getParameters() {
		return List.copyOf(this.parameters);
	}
	
	public @NonNull SqlRenderer literal(@NonNull String literal) {
		Objects.requireNonNull(literal, "Sql literal must not be null");
		
		this.statements.add(literal);
		return this;
	}
	
	public @NonNull SqlRenderer quotedLiteral(@NonNull String quote, @NonNull String escapedQuote, @NonNull String literal) {
		Objects.requireNonNull(quote, "Sql quote must not be null");
		Objects.requireNonNull(escapedQuote, "Sql escaped quote must not be null");
		Objects.requireNonNull(literal, "Sql literal must not be null");
		
		this.statements.add(quote + literal.replace(quote, escapedQuote) + quote);
		return this;
	}
	
	public @NonNull SqlRenderer keyword(@NonNull String keyword) {
		Objects.requireNonNull(keyword, "Sql keyword must not be null");
		if (keyword.isBlank()) {
			throw new IllegalArgumentException("Sql keyword must not be blank");
		}
		for (int i = 0; i < keyword.length(); i++) {
			if (!Character.isUpperCase(keyword.charAt(i))) {
				throw new IllegalArgumentException("Sql keyword must be uppercase, character '" + keyword.charAt(i) + "' at index " + i + " is not uppercase: " + keyword);
			}
		}
		
		this.statements.add(keyword);
		return this;
	}
	
	public @NonNull SqlRenderer parameter(@NonNull Object parameter) {
		Objects.requireNonNull(parameter, "Sql parameter must not be null");
		
		this.statements.add("?");
		this.parameters.add(parameter);
		return this;
	}
	
	public @NonNull SqlRenderer rendered(@NonNull SqlRendered rendered) {
		Objects.requireNonNull(rendered, "Sql rendered must not be null");
		
		this.statements.addAll(rendered.statements());
		this.parameters.addAll(rendered.parameters());
		return this;
	}
	
	public @NonNull SqlRenderer openingBracket() {
		this.statements.add("(");
		return this;
	}
	
	public @NonNull SqlRenderer closingBracket() {
		this.statements.add(")");
		return this;
	}
	
	public @NonNull SqlRenderer comma() {
		this.statements.add(",");
		return this;
	}
	
	public @NonNull SqlRenderer true_() {
		this.statements.add("TRUE");
		return this;
	}
	
	public @NonNull SqlRenderer false_() {
		this.statements.add("FALSE");
		return this;
	}
	
	public @NonNull SqlRenderer unknown() {
		this.statements.add("UNKNOWN");
		return this;
	}
	
	public @NonNull SqlRenderer null_() {
		this.statements.add("NULL");
		return this;
	}
	
	public @NonNull SqlRenderer select() {
		this.statements.add("SELECT");
		return this;
	}
	
	public @NonNull SqlRenderer distinct() {
		this.statements.add("DISTINCT");
		return this;
	}
	
	public @NonNull SqlRenderer all() {
		this.statements.add("ALL");
		return this;
	}
	
	public @NonNull SqlRenderer from() {
		this.statements.add("FROM");
		return this;
	}
	
	public @NonNull SqlRenderer where() {
		this.statements.add("WHERE");
		return this;
	}
	
	public @NonNull SqlRenderer insert() {
		this.statements.add("INSERT");
		return this;
	}
	
	public @NonNull SqlRenderer into() {
		this.statements.add("INTO");
		return this;
	}
	
	public @NonNull SqlRenderer values() {
		this.statements.add("VALUES");
		return this;
	}
	
	public @NonNull SqlRenderer update() {
		this.statements.add("UPDATE");
		return this;
	}
	
	public @NonNull SqlRenderer set() {
		this.statements.add("SET");
		return this;
	}
	
	public @NonNull SqlRenderer delete() {
		this.statements.add("DELETE");
		return this;
	}
	
	public @NonNull SqlRenderer truncate() {
		this.statements.add("TRUNCATE");
		return this;
	}
	
	public @NonNull SqlRenderer join() {
		this.statements.add("JOIN");
		return this;
	}
	
	public @NonNull SqlRenderer natural() {
		this.statements.add("NATURAL");
		return this;
	}
	
	public @NonNull SqlRenderer left() {
		this.statements.add("LEFT");
		return this;
	}
	
	public @NonNull SqlRenderer right() {
		this.statements.add("RIGHT");
		return this;
	}
	
	public @NonNull SqlRenderer full() {
		this.statements.add("FULL");
		return this;
	}
	
	public @NonNull SqlRenderer cross() {
		this.statements.add("CROSS");
		return this;
	}
	
	public @NonNull SqlRenderer inner() {
		this.statements.add("INNER");
		return this;
	}
	
	public @NonNull SqlRenderer outer() {
		this.statements.add("OUTER");
		return this;
	}
	
	public @NonNull SqlRenderer on() {
		this.statements.add("ON");
		return this;
	}
	
	public @NonNull SqlRenderer groupBy() {
		this.statements.add("GROUP BY");
		return this;
	}
	
	public @NonNull SqlRenderer orderBy() {
		this.statements.add("ORDER BY");
		return this;
	}
	
	public @NonNull SqlRenderer having() {
		this.statements.add("HAVING");
		return this;
	}
	
	public @NonNull SqlRenderer asc() {
		this.statements.add("ASC");
		return this;
	}
	
	public @NonNull SqlRenderer desc() {
		this.statements.add("DESC");
		return this;
	}
	
	public @NonNull SqlRenderer nulls() {
		this.statements.add("NULLS");
		return this;
	}
	
	public @NonNull SqlRenderer limit() {
		this.statements.add("LIMIT");
		return this;
	}
	
	public @NonNull SqlRenderer offset() {
		this.statements.add("OFFSET");
		return this;
	}
	
	public @NonNull SqlRenderer fetch() {
		this.statements.add("FETCH");
		return this;
	}
	
	public @NonNull SqlRenderer first() {
		this.statements.add("FIRST");
		return this;
	}
	
	public @NonNull SqlRenderer next() {
		this.statements.add("NEXT");
		return this;
	}
	
	public @NonNull SqlRenderer rows() {
		this.statements.add("ROWS");
		return this;
	}
	
	public @NonNull SqlRenderer only() {
		this.statements.add("ONLY");
		return this;
	}
	
	public @NonNull SqlRenderer with() {
		this.statements.add("WITH");
		return this;
	}
	
	public @NonNull SqlRenderer recursive() {
		this.statements.add("RECURSIVE");
		return this;
	}
	
	public @NonNull SqlRenderer and() {
		this.statements.add("AND");
		return this;
	}
	
	public @NonNull SqlRenderer or() {
		this.statements.add("OR");
		return this;
	}
	
	public @NonNull SqlRenderer not() {
		this.statements.add("NOT");
		return this;
	}
	
	public @NonNull SqlRenderer is() {
		this.statements.add("IS");
		return this;
	}
	
	public @NonNull SqlRenderer in() {
		this.statements.add("IN");
		return this;
	}
	
	public @NonNull SqlRenderer exists() {
		this.statements.add("EXISTS");
		return this;
	}
	
	public @NonNull SqlRenderer between() {
		this.statements.add("BETWEEN");
		return this;
	}
	
	public @NonNull SqlRenderer like() {
		this.statements.add("LIKE");
		return this;
	}
	
	public @NonNull SqlRenderer escape() {
		this.statements.add("ESCAPE");
		return this;
	}
	
	public @NonNull SqlRenderer any() {
		this.statements.add("ANY");
		return this;
	}
	
	public @NonNull SqlRenderer some() {
		this.statements.add("SOME");
		return this;
	}
	
	public @NonNull SqlRenderer case_() {
		this.statements.add("CASE");
		return this;
	}
	
	public @NonNull SqlRenderer when() {
		this.statements.add("WHEN");
		return this;
	}
	
	public @NonNull SqlRenderer then() {
		this.statements.add("THEN");
		return this;
	}
	
	public @NonNull SqlRenderer else_() {
		this.statements.add("ELSE");
		return this;
	}
	
	public @NonNull SqlRenderer end() {
		this.statements.add("END");
		return this;
	}
	
	public @NonNull SqlRenderer if_() {
		this.statements.add("IF");
		return this;
	}
	
	public @NonNull SqlRenderer cast() {
		this.statements.add("CAST");
		return this;
	}
	
	public @NonNull SqlRenderer as() {
		this.statements.add("AS");
		return this;
	}
	
	public @NonNull SqlRenderer create() {
		this.statements.add("CREATE");
		return this;
	}
	
	public @NonNull SqlRenderer drop() {
		this.statements.add("DROP");
		return this;
	}
	
	public @NonNull SqlRenderer alter() {
		this.statements.add("ALTER");
		return this;
	}
	
	public @NonNull SqlRenderer table() {
		this.statements.add("TABLE");
		return this;
	}
	
	public @NonNull SqlRenderer column() {
		this.statements.add("COLUMN");
		return this;
	}
	
	public @NonNull SqlRenderer add() {
		this.statements.add("ADD");
		return this;
	}
	
	public @NonNull SqlRenderer rename() {
		this.statements.add("RENAME");
		return this;
	}
	
	public @NonNull SqlRenderer to() {
		this.statements.add("TO");
		return this;
	}
	
	public @NonNull SqlRenderer index() {
		this.statements.add("INDEX");
		return this;
	}
	
	public @NonNull SqlRenderer using() {
		this.statements.add("USING");
		return this;
	}
	
	public @NonNull SqlRenderer primary() {
		this.statements.add("PRIMARY");
		return this;
	}
	
	public @NonNull SqlRenderer foreign() {
		this.statements.add("FOREIGN");
		return this;
	}
	
	public @NonNull SqlRenderer key() {
		this.statements.add("KEY");
		return this;
	}
	
	public @NonNull SqlRenderer unique() {
		this.statements.add("UNIQUE");
		return this;
	}
	
	public @NonNull SqlRenderer check() {
		this.statements.add("CHECK");
		return this;
	}
	
	public @NonNull SqlRenderer constraint() {
		this.statements.add("CONSTRAINT");
		return this;
	}
	
	public @NonNull SqlRenderer references() {
		this.statements.add("REFERENCES");
		return this;
	}
	
	public @NonNull SqlRenderer default_() {
		this.statements.add("DEFAULT");
		return this;
	}
	
	public @NonNull SqlRenderer noAction() {
		this.statements.add("NO ACTION");
		return this;
	}
	
	public @NonNull SqlRenderer restrict() {
		this.statements.add("RESTRICT");
		return this;
	}
	
	public @NonNull SqlRenderer cascade() {
		this.statements.add("CASCADE");
		return this;
	}
	
	public @NonNull SqlRenderer setNull() {
		this.set();
		this.null_();
		return this;
	}
	
	public @NonNull SqlRenderer setDefault() {
		this.set();
		this.default_();
		return this;
	}
	
	public @NonNull SqlRenderer union() {
		this.statements.add("UNION");
		return this;
	}
	
	public @NonNull SqlRenderer intersect() {
		this.statements.add("INTERSECT");
		return this;
	}
	
	public @NonNull SqlRenderer except() {
		this.statements.add("EXCEPT");
		return this;
	}
	
	public @NonNull SqlRenderer over() {
		this.statements.add("OVER");
		return this;
	}
	
	public @NonNull SqlRenderer partition() {
		this.statements.add("PARTITION");
		return this;
	}
	
	public @NonNull SqlRenderer window() {
		this.statements.add("WINDOW");
		return this;
	}
	
	public @NonNull SqlRenderer range() {
		this.statements.add("RANGE");
		return this;
	}
	
	public @NonNull SqlRenderer groups() {
		this.statements.add("GROUPS");
		return this;
	}
	
	public @NonNull SqlRenderer unbounded() {
		this.statements.add("UNBOUNDED");
		return this;
	}
	
	public @NonNull SqlRenderer preceding() {
		this.statements.add("PRECEDING");
		return this;
	}
	
	public @NonNull SqlRenderer following() {
		this.statements.add("FOLLOWING");
		return this;
	}
	
	public @NonNull SqlRenderer currentRow() {
		this.statements.add("CURRENT ROW");
		return this;
	}
	
	public @NonNull SqlRenderer exclude() {
		this.statements.add("EXCLUDE");
		return this;
	}
	
	public @NonNull SqlRenderer ties() {
		this.statements.add("TIES");
		return this;
	}
	
	public @NonNull SqlRenderer filter() {
		this.statements.add("FILTER");
		return this;
	}
	
	public @NonNull SqlRenderer for_() {
		this.statements.add("FOR");
		return this;
	}
	
	public @NonNull SqlRenderer share() {
		this.statements.add("SHARE");
		return this;
	}
	
	public @NonNull SqlRenderer nowait() {
		this.statements.add("NOWAIT");
		return this;
	}
	
	public @NonNull SqlRenderer wait_() {
		this.statements.add("WAIT");
		return this;
	}
	
	public @NonNull SqlRenderer skip() {
		this.statements.add("SKIP");
		return this;
	}
	
	public @NonNull SqlRenderer locked() {
		this.statements.add("LOCKED");
		return this;
	}
	
	public @NonNull SqlRenderer returning() {
		this.statements.add("RETURNING");
		return this;
	}
	
	public @NonNull SqlRenderer lateral() {
		this.statements.add("LATERAL");
		return this;
	}
	
	public @NonNull SqlRenderer begin() {
		this.statements.add("BEGIN");
		return this;
	}
	
	public @NonNull SqlRenderer commit() {
		this.statements.add("COMMIT");
		return this;
	}
	
	public @NonNull SqlRenderer rollback() {
		this.statements.add("ROLLBACK");
		return this;
	}
	
	public @NonNull SqlRenderer savepoint() {
		this.statements.add("SAVEPOINT");
		return this;
	}
	
	public @NonNull SqlRenderer start() {
		this.statements.add("START");
		return this;
	}
	
	public @NonNull SqlRenderer transaction() {
		this.statements.add("TRANSACTION");
		return this;
	}
	
	public @NonNull SqlRendered toSql() {
		return new SimpleSqlRendered(this.statements, this.parameters);
	}
}
