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

package net.luis.utils.io.token.lexer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.lexer.stream.CharStream;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * A lexer that scans an input string into a flat list of tokens based on a regular grammar.<br>
 * <p>
 *     A lexer is configured with an alphabet (the set of allowed characters) and an ordered list of {@link LexerRule}s.<br>
 *     Each rule pairs a regular {@link net.luis.utils.io.token.lexer.rules.CharRule} pattern with the token kind it produces.
 * </p>
 * <p>
 *     The lexer is the regular-grammar counterpart of the context-free {@link net.luis.utils.io.token.parser.Parser}.<br>
 *     Both stages share the same form: an alphabet of allowed symbols plus an ordered set of rules over a stream produce an output.<br>
 *     The lexer operates over characters and emits a flat list of tokens, while the parser operates over tokens and emits an abstract syntax tree.
 * </p>
 * <p>
 *     At each position the lexer applies maximal munch: the longest matching rule wins, with ties broken by declaration order.<br>
 *     This replaces the previous reader based on token definitions and a separate classifier.
 * </p>
 *
 * @author Luis-St
 *
 * @see LexerBuilder
 * @see LexerRule
 */
public class Lexer {
	
	/**
	 * The set of characters that are allowed in the input.
	 */
	private final Set<Character> allowedChars;
	/**
	 * The ordered list of lexer rules.
	 */
	private final List<LexerRule> rules;
	
	/**
	 * Constructs a new lexer with the given alphabet and rules.<br>
	 *
	 * @param allowedChars The set of allowed characters
	 * @param rules The ordered list of lexer rules
	 * @throws NullPointerException If the allowed characters or rules are null
	 * @throws IllegalStateException If the allowed characters or rules are empty
	 */
	Lexer(@NonNull Set<Character> allowedChars, @NonNull List<LexerRule> rules) {
		Objects.requireNonNull(allowedChars, "Allowed characters must not be null");
		Objects.requireNonNull(rules, "Rules must not be null");
		
		if (allowedChars.isEmpty()) {
			throw new IllegalStateException("Allowed characters must not be empty");
		}
		if (rules.isEmpty()) {
			throw new IllegalStateException("Lexer rules must not be empty");
		}
		
		this.allowedChars = Sets.newHashSet(allowedChars);
		this.rules = List.copyOf(rules);
	}
	
	/**
	 * Creates a new lexer using a builder pattern.<br>
	 * This method provides a convenient way to construct a lexer by accepting a consumer that configures a {@link LexerBuilder}.<br>
	 *
	 * @param builderFunction The function that configures the lexer builder
	 * @return A new lexer instance
	 * @throws NullPointerException If the builder function is null
	 */
	public static @NonNull Lexer builder(@NonNull Consumer<LexerBuilder> builderFunction) {
		Objects.requireNonNull(builderFunction, "Builder function must not be null");
		
		LexerBuilder builder = new LexerBuilder();
		builderFunction.accept(builder);
		return builder.build();
	}
	
	/**
	 * Returns an unmodifiable set of the allowed characters of this lexer.<br>
	 * @return An unmodifiable set of allowed characters
	 */
	public @NonNull @Unmodifiable Set<Character> getAllowedChars() {
		return Collections.unmodifiableSet(this.allowedChars);
	}
	
	/**
	 * Returns an unmodifiable list of the rules of this lexer.<br>
	 * @return An unmodifiable list of rules
	 */
	public @NonNull @Unmodifiable List<LexerRule> getRules() {
		return Collections.unmodifiableList(this.rules);
	}
	
	/**
	 * Tokenizes the given input string into a flat list of tokens.<br>
	 * <p>
	 *     At each position the lexer validates that the current character is part of the alphabet, then applies maximal munch
	 *     to find the longest matching rule (ties broken by declaration order).<br>
	 *     The matched token is emitted (typed and optionally shadowed) and the stream is advanced past it.
	 * </p>
	 *
	 * @param input The input string to tokenize
	 * @return An unmodifiable list of tokens read from the input string
	 * @throws NullPointerException If the input string is null
	 * @throws IllegalStateException If an undefined character is encountered or no rule matches at a position
	 */
	public @NonNull @Unmodifiable List<Token> tokenize(@NonNull String input) {
		Objects.requireNonNull(input, "Input string must not be null");
		List<Token> tokens = Lists.newArrayList();
		
		CharStream stream = CharStream.createMutable(input);
		while (stream.hasMore()) {
			int startIndex = stream.getCurrentIndex();
			char current = stream.getCurrentChar();
			if (!this.allowedChars.contains(current)) {
				throw this.undefinedCharacter(stream, current);
			}
			
			LexerRule bestRule = null;
			CharRuleMatch bestMatch = null;
			for (LexerRule rule : this.rules) {
				CharRuleMatch match = rule.pattern().match(stream.copyWithIndex(startIndex));
				if (match == null || match.endIndex() <= match.startIndex()) {
					continue;
				}
				if (bestMatch == null || match.endIndex() - match.startIndex() > bestMatch.endIndex() - bestMatch.startIndex()) {
					bestRule = rule;
					bestMatch = match;
				}
			}
			
			if (bestRule == null) {
				TokenPosition position = stream.currentPosition();
				throw new IllegalStateException("No matching rule at line " + position.line() + ", character " + position.characterInLine() + ": '" + current + "'");
			}
			
			for (int i = bestMatch.startIndex(); i < bestMatch.endIndex(); i++) {
				char c = input.charAt(i);
				if (!this.allowedChars.contains(c)) {
					throw this.undefinedCharacter(stream.copyWithIndex(i), c);
				}
			}
			
			tokens.add(bestRule.createToken(bestMatch.matched(), stream.currentPosition()));
			stream.advanceTo(bestMatch.endIndex());
		}
		return List.copyOf(tokens);
	}
	
	/**
	 * Creates an illegal state exception for an undefined character at the current position of the given stream.<br>
	 *
	 * @param stream The character stream positioned at the undefined character
	 * @param c The undefined character
	 * @return The created illegal state exception
	 */
	private @NonNull IllegalStateException undefinedCharacter(@NonNull CharStream stream, char c) {
		TokenPosition position = stream.currentPosition();
		return new IllegalStateException("Undefined character at line " + position.line() + ", character " + position.characterInLine() + ": '" + c + "'");
	}
}
