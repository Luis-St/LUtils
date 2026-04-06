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
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlRenderer {
	
	private final List<String> statements = Lists.newArrayList();
	private final List<Object> parameters = Lists.newArrayList();
	
	public SqlRenderer() {}
	
	public SqlRenderer(@NonNull List<String> statements, @NonNull List<Object> parameters) {
		this.statements.addAll(Objects.requireNonNull(statements, "Sql statements must not be null"));
		this.parameters.addAll(Objects.requireNonNull(parameters, "Sql parameters must not be null"));
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
	
	public @NonNull SqlRenderer cast() {
		this.statements.add("CAST");
		return this;
	}
	
	public @NonNull SqlRenderer as() {
		this.statements.add("AS");
		return this;
	}
	
	/*public @NonNull SqlRenderer case() {
		this.statements.add("CASE");
		return this;
	}*/
	
	public @NonNull SqlRenderer when() {
		this.statements.add("WHEN");
		return this;
	}
	
	public @NonNull SqlRenderer then() {
		this.statements.add("THEN");
		return this;
	}
	
	/*public @NonNull SqlRenderer else() {
		this.statements.add("ELSE");
		return this;
	}*/
	
	public @NonNull SqlRenderer end() {
		this.statements.add("END");
		return this;
	}
	
	/*public @NonNull SqlRenderer if() {
		this.statements.add("IF");
		return this;
	}*/
	
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
	
	public @NonNull SqlRenderer select() {
		this.statements.add("SELECT");
		return this;
	}
	
	public @NonNull SqlRenderer insert() {
		this.statements.add("INSERT");
		return this;
	}
	
	public @NonNull SqlRenderer update() {
		this.statements.add("UPDATE");
		return this;
	}
	
	public @NonNull SqlRenderer delete() {
		this.statements.add("DELETE");
		return this;
	}
	
	public @NonNull SqlRenderer join() {
		this.statements.add("JOIN");
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
	
	public @NonNull SqlRenderer from() {
		this.statements.add("FROM");
		return this;
	}
	
	public @NonNull SqlRenderer where() {
		this.statements.add("WHERE");
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
	
	public @NonNull SqlRenderer exists() {
		this.statements.add("EXISTS");
		return this;
	}
	
	public @NonNull SqlRenderer table() {
		this.statements.add("TABLE");
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
	
	public @NonNull SqlRenderer index() {
		this.statements.add("INDEX");
		return this;
	}
	
	public @NonNull SqlRenderer using() {
		this.statements.add("USING");
		return this;
	}
	
	public @NonNull SqlRenderer values() {
		this.statements.add("VALUES");
		return this;
	}
	
	public @NonNull SqlRenderer set() {
		this.statements.add("SET");
		return this;
	}
	
	/*public @NonNull SqlRenderer default() {
		this.statements.add("DEFAULT");
		return this;
	}*/
	
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
	/*	this.null();*/
		return this;
	}
	
	public @NonNull SqlRenderer setDefault() {
		this.set();
	/*	this.default();*/
		return this;
	}
	
	
}
