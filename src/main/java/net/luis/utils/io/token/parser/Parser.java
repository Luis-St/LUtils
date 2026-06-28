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

package net.luis.utils.io.token.parser;

import net.luis.utils.io.token.TokenRuleEngine;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.tokens.TokenGroup;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a parser definition that builds an abstract syntax tree from a flat list of tokens.<br>
 * <p>
 *     A parser consists of a collection of named rules that are applied to token sequences using a {@link TokenRuleEngine}.<br>
 *     Each named rule produces a labeled {@link TokenGroup}; nesting these groups forms the abstract syntax tree.
 * </p>
 * <p>
 *     The parser is the context-free counterpart of the regular {@link net.luis.utils.io.token.lexer.Lexer}.<br>
 *     Both stages share the same form: an alphabet of allowed symbols plus an ordered set of rules over a stream produce an output.<br>
 *     The lexer operates over characters and emits a flat list of tokens (its alphabet being the allowed characters),
 *     while the parser operates over tokens and emits an abstract syntax tree (its alphabet being the token kinds).
 * </p>
 *
 * @author Luis-St
 *
 * @see ParserBuilder
 * @see ParserRule
 */
public class Parser {
	
	/**
	 * The label used for the synthetic root node when the parsed result is not already a single token group.
	 */
	private static final String ROOT_LABEL = "root";
	
	/**
	 * The predefined token rule context for this parser.
	 */
	private final TokenRuleContext context;
	/**
	 * The list of named parser rules.
	 */
	private final List<ParserRule> rules;
	
	/**
	 * Constructs a new parser with the specified context and rules.<br>
	 *
	 * @param context The token rule context for rule resolution
	 * @param rules The list of named parser rules
	 * @throws NullPointerException If the context or rules is null
	 */
	Parser(@NonNull TokenRuleContext context, @NonNull List<ParserRule> rules) {
		this.context = Objects.requireNonNull(context, "Context must not be null");
		this.rules = List.copyOf(Objects.requireNonNull(rules, "Rules must not be null"));
	}
	
	/**
	 * Creates a new parser using a builder pattern.<br>
	 * This method provides a convenient way to construct a parser by accepting a consumer that configures a {@link ParserBuilder}.<br>
	 *
	 * @param builderFunction The function that configures the parser builder
	 * @return A new parser instance
	 * @throws NullPointerException If the builder function is null
	 */
	public static @NonNull Parser builder(@NonNull Consumer<ParserBuilder> builderFunction) {
		Objects.requireNonNull(builderFunction, "Builder function must not be null");
		
		ParserBuilder builder = new ParserBuilder();
		builderFunction.accept(builder);
		return builder.build();
	}
	
	/**
	 * Returns the token rule context associated with this parser.<br>
	 * @return The token rule context
	 */
	public @NonNull TokenRuleContext getContext() {
		return this.context;
	}
	
	/**
	 * Returns an unmodifiable list of all rules added to this parser.<br>
	 * @return An unmodifiable list of rules
	 */
	public @NonNull List<ParserRule> getRules() {
		return Collections.unmodifiableList(this.rules);
	}
	
	/**
	 * Parses a list of tokens into an abstract syntax tree according to the rules defined in this parser.<br>
	 * <p>
	 *     This method creates a {@link TokenRuleEngine}, registers all parser rules with their associated actions and processes the input tokens.<br>
	 *     If the resulting token list is already a single {@link TokenGroup}, it is returned directly as the root.<br>
	 *     Otherwise, the resulting tokens are wrapped under a synthetic root group.
	 * </p>
	 *
	 * @param tokens The list of tokens to parse
	 * @return The root token group of the produced abstract syntax tree
	 * @throws NullPointerException If the list of tokens is null
	 * @throws IllegalArgumentException If the list of tokens is empty
	 * @see TokenRuleEngine
	 */
	public @NonNull TokenGroup parse(@NonNull List<Token> tokens) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (tokens.isEmpty()) {
			throw new IllegalArgumentException("Tokens must not be empty");
		}
		
		TokenRuleEngine engine = new TokenRuleEngine(this.context);
		this.rules.forEach(rule -> engine.addRule(rule.rule(), rule.action()));
		
		List<Token> result = engine.process(tokens);
		if (result.size() == 1 && result.getFirst() instanceof TokenGroup group) {
			return group;
		}
		return new TokenGroup(ROOT_LABEL, result);
	}
}
