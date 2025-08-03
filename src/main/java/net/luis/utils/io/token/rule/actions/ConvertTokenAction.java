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

package net.luis.utils.io.token.rule.actions;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

/**
 * Token action that converts tokens using a converter.<br>
 * The converter is applied to each token, allowing full control over token transformation.<br>
 *
 * @see TokenConverter
 *
 * @author Luis-St
 *
 * @param converter The converter to apply to tokens
 */
public record ConvertTokenAction(
	@NotNull TokenConverter converter
) implements TokenAction {
	
	/**
	 * Constructs a new convert token action with the given converter.<br>
	 *
	 * @param converter The converter to apply to tokens
	 * @throws NullPointerException If the converter is null
	 */
	public ConvertTokenAction {
		Objects.requireNonNull(converter, "Converter must not be null");
	}
	
	@Override
	public @NotNull @Unmodifiable List<Token> apply(@NotNull TokenRuleMatch match) {
		Objects.requireNonNull(match, "Token rule match must not be null");
		
		List<Token> result = Lists.newArrayListWithExpectedSize(match.matchedTokens().size());
		
		for (Token token : match.matchedTokens()) {
			result.add(this.converter.convert(token));
		}
		
		return List.copyOf(result);
	}
}
