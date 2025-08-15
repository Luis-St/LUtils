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

package net.luis.utils.io.token.rule.actions.transformers;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.actions.TokenAction;
import net.luis.utils.io.token.rule.actions.core.TokenDefinitionProvider;
import net.luis.utils.io.token.tokens.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Token action that splits tokens based on a regular expression pattern.<br>
 * Each token's value is split using the pattern, creating multiple tokens from each original token.<br>
 * Empty splits are ignored. The token definition for split parts is provided by a TokenDefinitionProvider.<br>
 * If the original token is positioned, the split tokens will have their positions calculated accordingly.<br>
 *
 * @author Luis-St
 *
 * @param splitPattern The pattern to use for splitting token values
 * @param definitionProvider The provider for token definitions of split parts
 */
public record SplitTokenAction(
	@NotNull Pattern splitPattern,
	@NotNull TokenDefinitionProvider definitionProvider
) implements TokenAction {
	
	/**
	 * Constructs a new split token action with the given split pattern and definition provider.<br>
	 *
	 * @param splitPattern The pattern to use for splitting token values
	 * @param definitionProvider The provider for token definitions of split parts
	 * @throws NullPointerException If the split pattern or definition provider is null
	 */
	public SplitTokenAction {
		Objects.requireNonNull(splitPattern, "Split pattern must not be null");
		Objects.requireNonNull(definitionProvider, "Definition provider must not be null");
	}
	
	/**
	 * Constructs a new split token action with the given split pattern string and definition provider.<br>
	 *
	 * @param splitPattern The pattern string to use for splitting token values
	 * @param definitionProvider The provider for token definitions of split parts
	 * @throws NullPointerException If the split pattern or definition provider is null
	 */
	public SplitTokenAction(@NotNull String splitPattern, @NotNull TokenDefinitionProvider definitionProvider) {
		this(Pattern.compile(Objects.requireNonNull(splitPattern, "Split pattern must not be null")), definitionProvider);
	}
	
	/**
	 * Constructs a new split token action with the given split pattern and a default definition provider.<br>
	 * The default provider accepts any string value.<br>
	 *
	 * @param splitPattern The pattern to use for splitting token values
	 * @throws NullPointerException If the split pattern is null
	 */
	public SplitTokenAction(@NotNull Pattern splitPattern) {
		this(splitPattern, TokenDefinitionProvider.acceptAll());
	}
	
	/**
	 * Constructs a new split token action with the given split pattern string and a default definition provider.<br>
	 * The default provider accepts any string value.<br>
	 *
	 * @param splitPattern The pattern string to use for splitting token values
	 * @throws NullPointerException If the split pattern is null
	 */
	public SplitTokenAction(@NotNull String splitPattern) {
		this(Pattern.compile(Objects.requireNonNull(splitPattern, "Split pattern must not be null")), TokenDefinitionProvider.acceptAll());
	}
	
	@Override
	public @NotNull @Unmodifiable List<Token> apply(@NotNull TokenRuleMatch match) {
		Objects.requireNonNull(match, "Token rule match must not be null");
		
		List<Token> result = Lists.newArrayList();
		
		for (Token token : match.matchedTokens()) {
			String originalValue = token.value();
			String[] parts = this.splitPattern.split(originalValue);
			
			int currentOffset = 0;
			boolean isPositioned = token.position().isPositioned();
			
			for (String part : parts) {
				if (!part.isEmpty()) {
					TokenDefinition partDefinition = this.definitionProvider.provide(part);
					TokenPosition startPos;
					
					if (isPositioned) {
						int partIndex = originalValue.indexOf(part, currentOffset);
						if (partIndex == -1) {
							partIndex = currentOffset;
						}
						
						int startChar = token.position().character() + partIndex;
						int startCharInLine = token.position().characterInLine() + partIndex;
						startPos = new TokenPosition(token.position().line(), startCharInLine, startChar);
						
						currentOffset = partIndex + part.length();
					} else {
						startPos = TokenPosition.UNPOSITIONED;
					}
					
					Token splitToken = switch (token) {
						case AnnotatedToken at -> new AnnotatedToken(
							new SimpleToken(partDefinition, part, startPos), at.metadata()
						);
						case IndexedToken it -> new IndexedToken(
							new SimpleToken(partDefinition, part, startPos), it.index()
						);
						default -> new SimpleToken(partDefinition, part, startPos);
					};
					
					result.add(splitToken);
				}
			}
		}
		
		return List.copyOf(result);
	}
}
