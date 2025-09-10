/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.token.rule.rules;

import com.google.common.collect.Maps;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

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
	 * Map of captured tokens by their keys.<br>
	 */
	protected final Map<String, Token> capturedTokens = Maps.newHashMap();
	
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
	public static @NotNull TokenRuleContext empty() {
		return new TokenRuleContext();
	}
	
	/**
	 * Defines a token rule with the given key.<br>
	 * If a rule with the same key already exists, it will be overwritten.<br>
	 *
	 * @param key The key to associate with the token rule
	 * @param rule The token rule to define
	 * @throws NullPointerException If the key or rule is null
	 */
	public void defineRule(@NotNull String key, @NotNull TokenRule rule) {
		Objects.requireNonNull(key, "Rule key must not be null");
		Objects.requireNonNull(rule, "Token rule must not be null");
		this.definedRules.put(key, rule);
	}
	
	/**
	 * Retrieves a defined token rule by its key.<br>
	 * If no rule is defined for the given key, an exception is thrown.<br>
	 *
	 * @param key The key of the token rule to retrieve
	 * @return The token rule associated with the given key
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If no rule is defined for the given key
	 */
	public @NotNull TokenRule getRuleReference(@NotNull String key) {
		Objects.requireNonNull(key, "Rule key must not be null");
		
		TokenRule rule = this.definedRules.get(key);
		if (rule == null) {
			throw new IllegalArgumentException("No rule defined for key: " + key);
		}
		return rule;
	}
	
	/**
	 * Captures a token with the given key.<br>
	 * If a token with the same key already exists, it will be overwritten.<br>
	 *
	 * @param key The key to associate with the captured token
	 * @param token The token to capture
	 * @throws NullPointerException If the key or token is null
	 */
	public void captureToken(@NotNull String key, @NotNull Token token) {
		Objects.requireNonNull(key, "Token key must not be null");
		Objects.requireNonNull(token, "Token must not be null");
		this.capturedTokens.put(key, token);
	}
	
	/**
	 * Retrieves a captured token by its key.<br>
	 * If no token is captured for the given key, an exception is thrown.<br>
	 *
	 * @param key The key of the captured token to retrieve
	 * @return The token associated with the given key
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If no token is captured for the given key
	 */
	public @NotNull Token getCapturedToken(@NotNull String key) {
		Objects.requireNonNull(key, "Token key must not be null");
		
		Token token = this.capturedTokens.get(key);
		if (token == null) {
			throw new IllegalArgumentException("No token captured for key: " + key);
		}
		return token;
	}
}
