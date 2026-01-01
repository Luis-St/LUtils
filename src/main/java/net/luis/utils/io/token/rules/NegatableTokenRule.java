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

package net.luis.utils.io.token.rules;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * A functional interface representing a token rule that can be inverted.<br>
 * It extends the {@link TokenRule} interface and provides a method to match a token against the rule.<br>
 * The rule can be negated using the {@link #not()} method, which returns a new {@link TokenRule} that matches tokens not matching the original rule.<br>
 * <p>
 *     The rule only handles a single token at a time, and the matching logic is defined in the {@link #match(Token)} method.<br>
 *     The {@link #match(TokenStream, TokenRuleContext)} method is overridden to provide a default implementation that checks the current token in the stream against the rule.<br>
 *     If the token matches, it returns a {@link TokenRuleMatch} containing the matched token; otherwise, it returns null.
 * </p>
 * <p>
 *     The {@link #not()} method creates a new rule that negates the original rule's logic, allowing for flexible rule definitions.<br>
 *     If the original rule matches a token, the negated rule will not match it.<br>
 *     If the inverted rule is inverted again, it returns the original rule, allowing for a chain of negations.
 * </p>
 * <p>
 *     It's guaranteed that the {@link #match(TokenStream, TokenRuleContext)} method consumes a token from the stream if it returns a match.<br>
 *     The method will never return an empty non-consuming match.<br>
 *     This ensures that the token stream is always advanced when a match is found, preventing infinite loops in token processing.
 * </p>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface NegatableTokenRule extends TokenRule {
	
	/**
	 * Matches the given token against the logic of the rule.<br>
	 * This method should be implemented to define the specific matching logic of the rule.<br>
	 *
	 * @param token The token to match against the rule
	 * @return True if the token matches the rule, false otherwise
	 * @throws NullPointerException If the token is null
	 */
	boolean match(@NonNull Token token);
	
	@Override
	default @Nullable TokenRuleMatch match(@NonNull TokenStream stream, @NonNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		if (!stream.hasMoreTokens()) {
			return null;
		}
		
		int startIndex = stream.getCurrentIndex();
		Token token = stream.getCurrentToken();
		if (this.match(token)) {
			List<Token> matchedTokens = Collections.singletonList(token);
			return new TokenRuleMatch(startIndex, stream.advance(), matchedTokens, this);
		}
		return null;
	}
	
	@Override
	default @NonNull TokenRule not() {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NonNull TokenStream stream, @NonNull TokenRuleContext ctx) {
				Objects.requireNonNull(stream, "Token stream must not be null");
				Objects.requireNonNull(ctx, "Token rule context must not be null");
				if (!stream.hasMoreTokens()) {
					return null;
				}
				
				int startIndex = stream.getCurrentIndex();
				Token token = stream.getCurrentToken();
				
				if (!NegatableTokenRule.this.match(token)) {
					List<Token> matchedTokens = Collections.singletonList(token);
					return new TokenRuleMatch(startIndex, stream.advance(), matchedTokens, this);
				}
				return null;
			}
			
			@Override
			public @NonNull TokenRule not() {
				return NegatableTokenRule.this; // Negating the not() method returns the original rule, preventing double negation and nesting of classes
			}
		};
	}
}
