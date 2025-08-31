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

import net.luis.utils.exception.NotInitializedException;
import net.luis.utils.function.FunctionUtils;
import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A token rule that supports lazy initialization of its underlying rule.<br>
 * This is particularly useful for creating recursive or mutually dependent token rules
 * where the actual rule definition needs to be deferred until after construction.<br>
 * <br>
 * <b>Usage example:</b><br>
 * <pre>{@code
 * // Create a lazy container for the rule
 * Lazy<TokenRule> lazy = new Lazy<>();
 * LazyTokenRule lazyTokenRule = new LazyTokenRule(lazy);
 *
 * // Use lazyTokenRule in other rule definitions
 * TokenRule recursiveRule = TokenRules.sequence(
 *     TokenRules.pattern("\\("),
 *     lazyTokenRule,
 *     TokenRules.pattern("\\)")
 * );
 *
 * // Initialize the lazy rule with the actual definition
 * lazy.set(TokenRules.any(
 *     TokenRules.pattern("\\w+"),
 *     recursiveRule
 * ));
 * }</pre>
 *
 * @author Luis-St
 */
public class LazyTokenRule implements TokenRule {
	
	/**
	 * The supplier for the lazily-initialized token rule.<br>
	 */
	private final Supplier<TokenRule> lazyTokenRule;
	
	/**
	 * Constructs a new lazy token rule with the given lazy initialization container.<br>
	 * The lazy container must be initialized before this rule can successfully match tokens.<br>
	 *
	 * @param lazyTokenRule The lazy initialization container for the token rule
	 * @throws NullPointerException If the lazy token rule is null
	 */
	public LazyTokenRule(@NotNull Lazy<TokenRule> lazyTokenRule) {
		this.lazyTokenRule = Objects.requireNonNull(lazyTokenRule, "The lazy token rule must not be null");
	}
	
	/**
	 * Constructs a new lazy token rule with the given supplier.<br>
	 * The supplier is memoized to ensure the rule is only created once.<br>
	 *
	 * @param lazyTokenRule The supplier for the token rule
	 * @throws NullPointerException If the supplier is null
	 */
	private LazyTokenRule(@NotNull Supplier<TokenRule> lazyTokenRule) {
		Objects.requireNonNull(lazyTokenRule, "The lazy token rule supplier must not be null");
		this.lazyTokenRule = FunctionUtils.memorize(lazyTokenRule);
	}
	
	/**
	 * Gets the supplier for the lazily-initialized token rule.<br>
	 * If the rule has not been initialized yet, calling {@link Supplier#get()} will throw a {@link NotInitializedException}.<br>
	 *
	 * @return The supplier for the lazily-initialized token rule
	 */
	public @NotNull Supplier<TokenRule> lazyTokenRule() {
		return this.lazyTokenRule;
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
		TokenRule tokenRule = null;
		try {
			tokenRule = this.lazyTokenRule.get();
		} catch (NotInitializedException ignored) {}
		
		if (tokenRule != null) {
			return tokenRule.match(stream);
		}
		return null;
	}
	
	@Override
	public @NotNull TokenRule not() {
		return new LazyTokenRule(() -> this.lazyTokenRule.get().not()) {
			
			@Override
			public @NotNull TokenRule not() {
				return LazyTokenRule.this; // Negating the not() method returns the original rule, preventing double negation and nesting of classes
			}
		};
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LazyTokenRule that)) return false;
		
		return this.lazyTokenRule.equals(that.lazyTokenRule);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.lazyTokenRule);
	}
	
	@Override
	public String toString() {
		return "LazyTokenRule[lazyTokenRule=" + this.lazyTokenRule + ']';
	}
	//endregion
}
