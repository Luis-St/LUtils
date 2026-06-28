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
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.util.Pair;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A fluent builder for assembling sql statements out of individual fragments and bound parameters.<br>
 * <p>
 *     Each builder method appends a single fragment, such as a keyword, a literal or a parameter placeholder, to an
 *     internal list of statement tokens and returns this renderer so that calls can be chained.<br>
 *     Bound values are collected separately as typed parameters and rendered as {@code ?} placeholders within the
 *     statement tokens to keep them safe from sql injection.
 * </p>
 * <p>
 *     Once the statement has been fully assembled, {@link #toSql()} produces an immutable {@link SqlRendered}
 *     snapshot holding the collected tokens and parameters.
 * </p>
 *
 * @see SqlRendered
 * @see SqlRenderable
 *
 * @author Luis-St
 */
@SuppressWarnings("NewMethodNamingConvention")
public class SqlRenderer {
	
	/**
	 * The ordered list of statement tokens that have been appended to this renderer.<br>
	 */
	private final List<String> statements = Lists.newArrayList();
	/**
	 * The ordered list of bound parameters, each pairing the parameter's sql type with its value.<br>
	 */
	private final List<Pair<SqlType<?>, Object>> parameters = Lists.newArrayList();
	
	/**
	 * Constructs a new sql renderer with the given statement tokens and parameters.<br>
	 * The given lists are copied into the internal lists, so later modifications to them have no effect.<br>
	 *
	 * @param statements The initial statement tokens to copy into this renderer
	 * @param parameters The initial parameters to copy into this renderer
	 * @throws NullPointerException If the statements or parameters list is null
	 */
	public SqlRenderer(@NonNull List<String> statements, @NonNull List<Pair<SqlType<?>, Object>> parameters) {
		this.statements.addAll(Objects.requireNonNull(statements, "Sql statements must not be null"));
		this.parameters.addAll(Objects.requireNonNull(parameters, "Sql parameters must not be null"));
	}
	
	/**
	 * Creates a new empty sql renderer with no statement tokens and no parameters.<br>
	 * @return A new empty sql renderer
	 */
	public static @NonNull SqlRenderer empty() {
		return new SqlRenderer(Lists.newArrayList(), Lists.newArrayList());
	}
	
	/**
	 * Returns an immutable copy of the statement tokens appended to this renderer so far.<br>
	 * @return The statement tokens
	 */
	public @NonNull @Unmodifiable List<String> getStatements() {
		return List.copyOf(this.statements);
	}
	
	/**
	 * Returns an immutable copy of the parameters bound to this renderer so far.<br>
	 * @return The bound parameters as type-value pairs
	 */
	public @NonNull @Unmodifiable List<Pair<SqlType<?>, Object>> getParameters() {
		return List.copyOf(this.parameters);
	}
	
	/**
	 * Appends the given literal as a single statement token exactly as provided.<br>
	 *
	 * @param literal The literal to append
	 * @return This renderer for method chaining
	 * @throws NullPointerException If the literal is null
	 */
	public @NonNull SqlRenderer literal(@NonNull String literal) {
		Objects.requireNonNull(literal, "Sql literal must not be null");
		
		this.statements.add(literal);
		return this;
	}
	
	/**
	 * Appends the given literal wrapped in the given quote characters as a single statement token.<br>
	 * Any occurrence of the quote within the literal is replaced with the given escaped quote before wrapping.<br>
	 *
	 * @param quote The quote to wrap the literal in
	 * @param escapedQuote The replacement used for occurrences of the quote inside the literal
	 * @param literal The literal to quote and append
	 * @return This renderer for method chaining
	 * @throws NullPointerException If the quote, escaped quote or literal is null
	 */
	public @NonNull SqlRenderer quotedLiteral(@NonNull String quote, @NonNull String escapedQuote, @NonNull String literal) {
		Objects.requireNonNull(quote, "Sql quote must not be null");
		Objects.requireNonNull(escapedQuote, "Sql escaped quote must not be null");
		Objects.requireNonNull(literal, "Sql literal must not be null");
		
		this.statements.add(quote + literal.replace(quote, escapedQuote) + quote);
		return this;
	}
	
	/**
	 * Appends the given keyword as a single statement token.<br>
	 * The keyword must consist only of uppercase letters and underscores.<br>
	 *
	 * @param keyword The keyword to append
	 * @return This renderer for method chaining
	 * @throws NullPointerException If the keyword is null
	 * @throws IllegalArgumentException If the keyword is blank or contains a character that is neither uppercase nor an underscore
	 */
	public @NonNull SqlRenderer keyword(@NonNull String keyword) {
		Objects.requireNonNull(keyword, "Sql keyword must not be null");
		if (keyword.isBlank()) {
			throw new IllegalArgumentException("Sql keyword must not be blank");
		}
		for (int i = 0; i < keyword.length(); i++) {
			char c = keyword.charAt(i);
			
			if (!Character.isUpperCase(c) && c != '_') {
				throw new IllegalArgumentException("Sql keyword must be uppercase, character '" + c + "' at index " + i + " is not uppercase: " + keyword);
			}
		}
		
		this.statements.add(keyword);
		return this;
	}
	
	/**
	 * Appends a {@code ?} placeholder token and binds the given typed value as a parameter.<br>
	 * The placeholder keeps the value out of the statement text to guard against sql injection.<br>
	 *
	 * @param <T> The java type of the bound value
	 * @param type The sql type of the value
	 * @param value The value to bind, may be {@code null}
	 * @return This renderer for method chaining
	 * @throws NullPointerException If the type is null
	 */
	public <T> @NonNull SqlRenderer parameter(@NonNull SqlType<T> type, @Nullable T value) {
		Objects.requireNonNull(type, "Sql type must not be null");
		
		this.statements.add("?");
		this.parameters.add(Pair.of(type, value));
		return this;
	}
	
	/**
	 * Appends all statement tokens and parameters of the given rendered fragment to this renderer.<br>
	 *
	 * @param rendered The rendered fragment to append
	 * @return This renderer for method chaining
	 * @throws NullPointerException If the rendered fragment is null
	 */
	public @NonNull SqlRenderer rendered(@NonNull SqlRendered rendered) {
		Objects.requireNonNull(rendered, "Sql rendered must not be null");
		
		this.statements.addAll(rendered.statements());
		this.parameters.addAll(rendered.parameters());
		return this;
	}
	
	/**
	 * Appends an opening bracket {@code (} token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer openingBracket() {
		this.statements.add("(");
		return this;
	}
	
	/**
	 * Appends a closing bracket {@code )} token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer closingBracket() {
		this.statements.add(")");
		return this;
	}
	
	/**
	 * Appends a comma {@code ,} token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer comma() {
		this.statements.add(",");
		return this;
	}
	
	/**
	 * Appends the {@code TRUE} boolean literal token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer true_() {
		this.statements.add("TRUE");
		return this;
	}
	
	/**
	 * Appends the {@code FALSE} boolean literal token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer false_() {
		this.statements.add("FALSE");
		return this;
	}
	
	/**
	 * Appends the {@code NULL} literal token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer null_() {
		this.statements.add("NULL");
		return this;
	}
	
	/**
	 * Appends the {@code SELECT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer select() {
		this.statements.add("SELECT");
		return this;
	}
	
	/**
	 * Appends the {@code DISTINCT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer distinct() {
		this.statements.add("DISTINCT");
		return this;
	}
	
	/**
	 * Appends the {@code FROM} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer from() {
		this.statements.add("FROM");
		return this;
	}
	
	/**
	 * Appends the {@code WHERE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer where() {
		this.statements.add("WHERE");
		return this;
	}
	
	/**
	 * Appends the {@code INSERT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer insert() {
		this.statements.add("INSERT");
		return this;
	}
	
	/**
	 * Appends the {@code INTO} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer into() {
		this.statements.add("INTO");
		return this;
	}
	
	/**
	 * Appends the {@code VALUES} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer values() {
		this.statements.add("VALUES");
		return this;
	}
	
	/**
	 * Appends the {@code UPDATE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer update() {
		this.statements.add("UPDATE");
		return this;
	}
	
	/**
	 * Appends the {@code SET} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer set() {
		this.statements.add("SET");
		return this;
	}
	
	/**
	 * Appends the {@code DELETE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer delete() {
		this.statements.add("DELETE");
		return this;
	}
	
	/**
	 * Appends the {@code TRUNCATE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer truncate() {
		this.statements.add("TRUNCATE");
		return this;
	}
	
	/**
	 * Appends the {@code JOIN} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer join() {
		this.statements.add("JOIN");
		return this;
	}
	
	/**
	 * Appends the {@code LEFT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer left() {
		this.statements.add("LEFT");
		return this;
	}
	
	/**
	 * Appends the {@code RIGHT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer right() {
		this.statements.add("RIGHT");
		return this;
	}
	
	/**
	 * Appends the {@code FULL} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer full() {
		this.statements.add("FULL");
		return this;
	}
	
	/**
	 * Appends the {@code CROSS} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer cross() {
		this.statements.add("CROSS");
		return this;
	}
	
	/**
	 * Appends the {@code INNER} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer inner() {
		this.statements.add("INNER");
		return this;
	}
	
	/**
	 * Appends the {@code OUTER} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer outer() {
		this.statements.add("OUTER");
		return this;
	}
	
	/**
	 * Appends the {@code ON} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer on() {
		this.statements.add("ON");
		return this;
	}
	
	/**
	 * Appends the {@code BY} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer by() {
		this.statements.add("BY");
		return this;
	}
	
	/**
	 * Appends the {@code GROUP BY} keyword tokens to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer groupBy() {
		this.statements.add("GROUP");
		this.by();
		return this;
	}
	
	/**
	 * Appends the {@code ORDER BY} keyword tokens to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer orderBy() {
		this.statements.add("ORDER");
		this.by();
		return this;
	}
	
	/**
	 * Appends the {@code HAVING} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer having() {
		this.statements.add("HAVING");
		return this;
	}
	
	/**
	 * Appends the {@code ASC} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer asc() {
		this.statements.add("ASC");
		return this;
	}
	
	/**
	 * Appends the {@code DESC} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer desc() {
		this.statements.add("DESC");
		return this;
	}
	
	/**
	 * Appends the {@code NULLS} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer nulls() {
		this.statements.add("NULLS");
		return this;
	}
	
	/**
	 * Appends the {@code LIMIT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer limit() {
		this.statements.add("LIMIT");
		return this;
	}
	
	/**
	 * Appends the {@code OFFSET} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer offset() {
		this.statements.add("OFFSET");
		return this;
	}
	
	/**
	 * Appends the {@code FETCH} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer fetch() {
		this.statements.add("FETCH");
		return this;
	}
	
	/**
	 * Appends the {@code FIRST} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer first() {
		this.statements.add("FIRST");
		return this;
	}
	
	/**
	 * Appends the {@code NEXT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer next() {
		this.statements.add("NEXT");
		return this;
	}
	
	/**
	 * Appends the {@code LAST} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer last() {
		this.statements.add("LAST");
		return this;
	}
	
	/**
	 * Appends the {@code ROWS} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer rows() {
		this.statements.add("ROWS");
		return this;
	}
	
	/**
	 * Appends the {@code ONLY} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer only() {
		this.statements.add("ONLY");
		return this;
	}
	
	/**
	 * Appends the {@code WITH} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer with() {
		this.statements.add("WITH");
		return this;
	}
	
	/**
	 * Appends the {@code RECURSIVE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer recursive() {
		this.statements.add("RECURSIVE");
		return this;
	}
	
	/**
	 * Appends the {@code AND} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer and() {
		this.statements.add("AND");
		return this;
	}
	
	/**
	 * Appends the {@code OR} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer or() {
		this.statements.add("OR");
		return this;
	}
	
	/**
	 * Appends the {@code NOT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer not() {
		this.statements.add("NOT");
		return this;
	}
	
	/**
	 * Appends the {@code IS} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer is() {
		this.statements.add("IS");
		return this;
	}
	
	/**
	 * Appends the {@code IN} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer in() {
		this.statements.add("IN");
		return this;
	}
	
	/**
	 * Appends the {@code EXISTS} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer exists() {
		this.statements.add("EXISTS");
		return this;
	}
	
	/**
	 * Appends the {@code BETWEEN} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer between() {
		this.statements.add("BETWEEN");
		return this;
	}
	
	/**
	 * Appends the {@code LIKE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer like() {
		this.statements.add("LIKE");
		return this;
	}
	
	/**
	 * Appends the {@code ANY} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer any() {
		this.statements.add("ANY");
		return this;
	}
	
	/**
	 * Appends the {@code CASE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer case_() {
		this.statements.add("CASE");
		return this;
	}
	
	/**
	 * Appends the {@code WHEN} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer when() {
		this.statements.add("WHEN");
		return this;
	}
	
	/**
	 * Appends the {@code THEN} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer then() {
		this.statements.add("THEN");
		return this;
	}
	
	/**
	 * Appends the {@code ELSE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer else_() {
		this.statements.add("ELSE");
		return this;
	}
	
	/**
	 * Appends the {@code END} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer end() {
		this.statements.add("END");
		return this;
	}
	
	/**
	 * Appends the {@code IF} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer if_() {
		this.statements.add("IF");
		return this;
	}
	
	/**
	 * Appends the {@code CAST} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer cast() {
		this.statements.add("CAST");
		return this;
	}
	
	/**
	 * Appends the {@code AS} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer as() {
		this.statements.add("AS");
		return this;
	}
	
	/**
	 * Appends the {@code CREATE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer create() {
		this.statements.add("CREATE");
		return this;
	}
	
	/**
	 * Appends the {@code DROP} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer drop() {
		this.statements.add("DROP");
		return this;
	}
	
	/**
	 * Appends the {@code ALTER} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer alter() {
		this.statements.add("ALTER");
		return this;
	}
	
	/**
	 * Appends the {@code MODIFY} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer modify() {
		this.statements.add("MODIFY");
		return this;
	}
	
	/**
	 * Appends the {@code DATABASE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer database() {
		this.statements.add("DATABASE");
		return this;
	}
	
	/**
	 * Appends the {@code SCHEMA} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer schema() {
		this.statements.add("SCHEMA");
		return this;
	}
	
	/**
	 * Appends the {@code TABLE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer table() {
		this.statements.add("TABLE");
		return this;
	}
	
	/**
	 * Appends the {@code COLUMN} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer column() {
		this.statements.add("COLUMN");
		return this;
	}
	
	/**
	 * Appends the {@code TYPE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer type() {
		this.statements.add("TYPE");
		return this;
	}
	
	/**
	 * Appends the {@code ADD} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer add() {
		this.statements.add("ADD");
		return this;
	}
	
	/**
	 * Appends the {@code RENAME} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer rename() {
		this.statements.add("RENAME");
		return this;
	}
	
	/**
	 * Appends the {@code TO} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer to() {
		this.statements.add("TO");
		return this;
	}
	
	/**
	 * Appends the {@code INDEX} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer index() {
		this.statements.add("INDEX");
		return this;
	}
	
	/**
	 * Appends the {@code USING} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer using() {
		this.statements.add("USING");
		return this;
	}
	
	/**
	 * Appends the {@code PRIMARY} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer primary() {
		this.statements.add("PRIMARY");
		return this;
	}
	
	/**
	 * Appends the {@code FOREIGN} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer foreign() {
		this.statements.add("FOREIGN");
		return this;
	}
	
	/**
	 * Appends the {@code KEY} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer key() {
		this.statements.add("KEY");
		return this;
	}
	
	/**
	 * Appends the {@code UNIQUE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer unique() {
		this.statements.add("UNIQUE");
		return this;
	}
	
	/**
	 * Appends the {@code CHECK} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer check() {
		this.statements.add("CHECK");
		return this;
	}
	
	/**
	 * Appends the {@code CONSTRAINT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer constraint() {
		this.statements.add("CONSTRAINT");
		return this;
	}
	
	/**
	 * Appends the {@code REFERENCES} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer references() {
		this.statements.add("REFERENCES");
		return this;
	}
	
	/**
	 * Appends the {@code DEFAULT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer default_() {
		this.statements.add("DEFAULT");
		return this;
	}
	
	/**
	 * Appends the {@code NO ACTION} keyword tokens to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer noAction() {
		this.statements.add("NO ACTION");
		return this;
	}
	
	/**
	 * Appends the {@code RESTRICT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer restrict() {
		this.statements.add("RESTRICT");
		return this;
	}
	
	/**
	 * Appends the {@code CASCADE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer cascade() {
		this.statements.add("CASCADE");
		return this;
	}
	
	/**
	 * Appends the {@code SET NULL} referential action keyword tokens to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer setNull() {
		this.set();
		this.null_();
		return this;
	}
	
	/**
	 * Appends the {@code SET DEFAULT} referential action keyword tokens to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer setDefault() {
		this.set();
		this.default_();
		return this;
	}
	
	/**
	 * Appends the {@code OVER} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer over() {
		this.statements.add("OVER");
		return this;
	}
	
	/**
	 * Appends the {@code PARTITION} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer partition() {
		this.statements.add("PARTITION");
		return this;
	}
	
	/**
	 * Appends the {@code RANGE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer range() {
		this.statements.add("RANGE");
		return this;
	}
	
	/**
	 * Appends the {@code GROUPS} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer groups() {
		this.statements.add("GROUPS");
		return this;
	}
	
	/**
	 * Appends the {@code UNBOUNDED} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer unbounded() {
		this.statements.add("UNBOUNDED");
		return this;
	}
	
	/**
	 * Appends the {@code PRECEDING} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer preceding() {
		this.statements.add("PRECEDING");
		return this;
	}
	
	/**
	 * Appends the {@code FOLLOWING} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer following() {
		this.statements.add("FOLLOWING");
		return this;
	}
	
	/**
	 * Appends the {@code CURRENT ROW} keyword tokens to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer currentRow() {
		this.statements.add("CURRENT ROW");
		return this;
	}
	
	/**
	 * Appends the {@code FOR} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer for_() {
		this.statements.add("FOR");
		return this;
	}
	
	/**
	 * Appends the {@code SHARE} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer share() {
		this.statements.add("SHARE");
		return this;
	}
	
	/**
	 * Appends the {@code NOWAIT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer nowait() {
		this.statements.add("NOWAIT");
		return this;
	}
	
	/**
	 * Appends the {@code WAIT} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer wait_() {
		this.statements.add("WAIT");
		return this;
	}
	
	/**
	 * Appends the {@code SKIP} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer skip() {
		this.statements.add("SKIP");
		return this;
	}
	
	/**
	 * Appends the {@code LOCKED} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer locked() {
		this.statements.add("LOCKED");
		return this;
	}
	
	/**
	 * Appends the {@code RETURNING} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer returning() {
		this.statements.add("RETURNING");
		return this;
	}
	
	/**
	 * Appends the {@code LATERAL} keyword token to the statement.<br>
	 * @return This renderer for method chaining
	 */
	public @NonNull SqlRenderer lateral() {
		this.statements.add("LATERAL");
		return this;
	}
	
	/**
	 * Produces an immutable snapshot of the statement tokens and parameters accumulated by this renderer.<br>
	 * @return A new rendered sql containing the current statement tokens and parameters
	 */
	public @NonNull SqlRendered toSql() {
		return new SqlRendered(this.statements, this.parameters);
	}
}
