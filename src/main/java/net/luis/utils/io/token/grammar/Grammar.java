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

package net.luis.utils.io.token.grammar;

import net.luis.utils.io.token.TokenRuleEngine;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a grammar definition that can parse and process tokens according to defined rules.<br>
 * <p>
 *     A Grammar consists of a collection of named rules that are applied to token sequences using a {@link TokenRuleEngine}.<br>
 *     The grammar maintains a context for rule resolution and provides methods to parse token lists according to the defined rules.
 * </p>
 *
 * @author Luis-St
 */
public class Grammar {
	
	/**
	 * The predefined token rule context for this grammar.<br>
	 */
	private final TokenRuleContext context;
	/**
	 * The map of named grammar rules.<br>
	 */
	private final List<GrammarRule> rules;
	
	/**
	 * Constructs a new grammar with the specified context and rules.<br>
	 *
	 * @param context The token rule context for rule resolution
	 * @param rules The map of named grammar rules
	 * @throws NullPointerException If context or rules is null
	 */
	Grammar(@NonNull TokenRuleContext context, @NonNull List<GrammarRule> rules) {
		this.context = Objects.requireNonNull(context, "Context must not be null");
		Objects.requireNonNull(rules, "Rules must not be null");
		this.rules = List.copyOf(rules);
	}
	
	/**
	 * Creates a new grammar using a builder pattern.<br>
	 * This method provides a convenient way to construct a grammar by accepting a consumer that configures a {@link GrammarBuilder}.<br>
	 *
	 * @param builderFunction The function that configures the grammar builder
	 * @return A new Grammar instance
	 * @throws NullPointerException If builder function is null
	 */
	public static @NonNull Grammar builder(@NonNull Consumer<GrammarBuilder> builderFunction) {
		Objects.requireNonNull(builderFunction, "Builder function must not be null");
		GrammarBuilder builder = new GrammarBuilder();
		builderFunction.accept(builder);
		return builder.build();
	}
	
	/**
	 * Returns the token rule context associated with this grammar.<br>
	 * @return The token rule context
	 */
	public @NonNull TokenRuleContext getContext() {
		return this.context;
	}
	
	/**
	 * Returns an unmodifiable set of all rule added to this grammar.<br>
	 * @return An unmodifiable set of rules
	 */
	public @NonNull List<GrammarRule> getRules() {
		return Collections.unmodifiableList(this.rules);
	}
	
	/**
	 * Parses a list of tokens according to the rules defined in this grammar.<br>
	 * This method creates a {@link TokenRuleEngine}, registers all grammar ruleswith their associated actions, and processes the input tokens.
	 *
	 * @param tokens The list of tokens to parse
	 * @return The processed list of tokens after applying all grammar rules
	 * @throws NullPointerException If the list of tokens is null
	 * @see TokenRuleEngine
	 */
	public @NonNull List<Token> parse(@NonNull List<Token> tokens) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		
		TokenRuleEngine engine = new TokenRuleEngine(this.context);
		this.rules.forEach(rule -> engine.addRule(rule.rule(), rule.action()));
		
		return engine.process(tokens);
	}
}
