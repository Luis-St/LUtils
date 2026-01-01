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

package net.luis.utils.io.token.context;

import com.google.common.collect.Maps;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Context which is passed to token rules when they are processed.<br>
 * The context allows to store and retrieve additional information during rule processing.<br>
 * <p>
 *     In the default implementation, the context allows to define and retrieve named token rules and captured tokens.<br>
 *     This can be useful for implementing complex parsing logic where certain rules or tokens need to be referenced multiple times.
 * </p>
 * The context can be extended to include additional functionality as needed.<br>
 *
 * @author Luis-St
 */
public class TokenRuleContext {
	
	/**
	 * Map of defined token rules by their keys.<br>
	 */
	protected final Map<String, TokenRule> definedRules = Maps.newHashMap();
	/**
	 * Map of captured token lists by their keys.<br>
	 */
	protected final Map<String, List<Token>> capturedTokens = Maps.newHashMap();
	
	/**
	 * Constructs an empty token rule context.<br>
	 * This constructor is protected to allow subclassing.<br>
	 * @see #empty()
	 */
	protected TokenRuleContext() {}
	
	/**
	 * Factory method to create an empty token rule context.<br>
	 * This method provides a convenient way to create a new instance of the context.<br>
	 *
	 * @return A new instance of an empty token rule context
	 */
	public static @NonNull TokenRuleContext empty() {
		return new TokenRuleContext();
	}
	
	/**
	 * Defines a token rule with the given key.<br>
	 * If a key already exists, it will be overwritten.<br>
	 *
	 * @param key The key to associate with the token rule
	 * @param rule The token rule to define
	 * @throws NullPointerException If the key or rule is null
	 */
	public void defineRule(@NonNull String key, @NonNull TokenRule rule) {
		Objects.requireNonNull(key, "Rule key must not be null");
		Objects.requireNonNull(rule, "Token rule must not be null");
		this.definedRules.put(key, rule);
	}
	
	/**
	 * Retrieves a defined token rule by its key.<br>
	 *
	 * @param key The key of the token rule to retrieve
	 * @return The token rule associated with the given key or null if no rule is defined for the key
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable TokenRule getRuleReference(@NonNull String key) {
		Objects.requireNonNull(key, "Rule key must not be null");
		return this.definedRules.get(key);
	}
	
	/**
	 * Captures the given list of tokens with the specified key.<br>
	 * If a key already exists, it will be overwritten.<br>
	 *
	 * @param key The key to associate with the captured token
	 * @param tokens The list of tokens to capture
	 * @throws NullPointerException If the key or token is null
	 */
	public void captureTokens(@NonNull String key, @NonNull List<Token> tokens) {
		Objects.requireNonNull(key, "Token key must not be null");
		Objects.requireNonNull(tokens, "List of tokens must not be null");
		this.capturedTokens.put(key, List.copyOf(tokens));
	}
	
	/**
	 * Retrieves captured tokens by their key.<br>
	 *
	 * @param key The key of the captured token to retrieve
	 * @return The token associated with the given key or null if no token is captured for the key
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable List<Token> getCapturedTokens(@NonNull String key) {
		Objects.requireNonNull(key, "Capture key must not be null");
		
		return this.capturedTokens.get(key);
	}
}
