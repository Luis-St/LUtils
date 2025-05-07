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

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface TokenRule {
	
	@Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex);
	
	default @NotNull TokenRule optional() {
		return TokenRules.optional(this);
	}
	
	default @NotNull TokenRule repeatAtLeast(int min) {
		return TokenRules.repeatAtLeast(this, min);
	}
	
	default @NotNull TokenRule repeatExactly(int occurrences) {
		return TokenRules.repeatExactly(this, occurrences);
	}
	
	default @NotNull TokenRule repeatAtMost(int max) {
		return TokenRules.repeatAtMost(this, max);
	}
	
	default @NotNull TokenRule repeatInfinitely() {
		return TokenRules.repeatInfinitely(this);
	}
	
	default @NotNull TokenRule repeatBetween(int min, int max) {
		return TokenRules.repeatBetween(this, min, max);
	}
}
