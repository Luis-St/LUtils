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

package net.luis.utils.io.token.rules.reference;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.rules.TokenRules;
import net.luis.utils.io.token.rules.core.ReferenceType;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Token rule that references another token rule or a list of tokens by a specified key.<br>
 * The referenced rule or tokens are looked up in the context using the key.<br>
 * The passed reference type determines whether a rule or a list of tokens is referenced.<br>
 *
 * @see ReferenceType
 *
 * @author Luis-St
 *
 * @param key The key used to look up the referenced rule or tokens in the context
 * @param type The type of reference (rule or tokens)
 */
public record ReferenceTokenRule(
	@NonNull String key,
	@NonNull ReferenceType type
) implements TokenRule {
	
	/**
	 * Creates a new reference token rule using the given key and reference type.<br>
	 *
	 * @param key The key used to look up the referenced rule or tokens in the context
	 * @param type The type of reference (rule, tokens or dynamic)
	 * @throws NullPointerException If the key or reference type is null
	 * @throws IllegalArgumentException If the key is empty
	 */
	public ReferenceTokenRule {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(type, "Reference type must not be null");
		
		if (key.isEmpty()) {
			throw new IllegalArgumentException("Key must not be empty");
		}
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NonNull TokenStream stream, @NonNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		
		TokenStream workingStream = stream.copyWithOffset(0);
		TokenRuleMatch match = switch (this.type) {
			case RULE -> this.matchReferencedRule(workingStream, ctx);
			case TOKENS -> this.matchReferencedTokens(workingStream, ctx);
			case DYNAMIC -> {
				TokenRule referencedRule = ctx.getRuleReference(this.key);
				List<Token> capturedTokens = ctx.getCapturedTokens(this.key);
				if (referencedRule != null && capturedTokens != null) {
					yield null;
				} else if (referencedRule != null) {
					yield this.matchReferencedRule(workingStream, ctx);
				} else if (capturedTokens != null) {
					yield this.matchReferencedTokens(workingStream, ctx);
				} else {
					yield null;
				}
			}
		};
		
		if (match != null) {
			stream.advanceTo(workingStream);
			return match;
		}
		return null;
	}
	
	/**
	 * Matches the referenced rule from the context using the specified key.<br>
	 * If the rule is found, it attempts to match it against the provided token stream and context.<br>
	 * If the match is successful, it returns the resulting match, otherwise it returns null.<br>
	 *
	 * @param stream The token stream to match against
	 * @param ctx The token rule context containing the referenced rules
	 * @return The resulting token rule match if successful, otherwise null
	 * @throws NullPointerException If the token stream or context is null
	 */
	private @Nullable TokenRuleMatch matchReferencedRule(@NonNull TokenStream stream, @NonNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		
		TokenRule tokenRule = ctx.getRuleReference(this.key);
		if (tokenRule != null) {
			return tokenRule.match(stream, ctx);
		}
		return null;
	}
	
	/**
	 * Matches the referenced tokens from the context using the specified key.<br>
	 * If the tokens are found, it creates a sequence rule from them and attempts to match it against the provided token stream and context.<br>
	 * If the match is successful, it returns the resulting match, otherwise it returns null.<br>
	 *
	 * @param stream The token stream to match against
	 * @param ctx The token rule context containing the referenced tokens
	 * @return The resulting token rule match if successful, otherwise null
	 * @throws NullPointerException If the token stream or context is null
	 */
	private @Nullable TokenRuleMatch matchReferencedTokens(@NonNull TokenStream stream, @NonNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		if (!stream.hasMoreTokens()) {
			return null;
		}
		
		List<Token> tokens = ctx.getCapturedTokens(this.key);
		if (tokens != null && !tokens.isEmpty()) {
			if (tokens.size() == 1) {
				return TokenRules.value(tokens.getFirst().value(), false).match(stream, ctx);
			}
			
			
			TokenRule rule = TokenRules.sequence(
				tokens.stream().map(token -> TokenRules.value(token.value(), false)).toList()
			);
			return rule.match(stream, ctx);
		}
		return null;
	}
}
