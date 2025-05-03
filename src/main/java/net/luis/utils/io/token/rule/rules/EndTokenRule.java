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

import net.luis.utils.annotation.type.Singleton;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

@Singleton
public final class EndTokenRule implements TokenRule {
	
	public static final EndTokenRule INSTANCE = new EndTokenRule();
	
	private EndTokenRule() {}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		if (startIndex >= tokens.size()) {
			return new TokenRuleMatch(startIndex, startIndex, Collections.emptyList(), this);
		}
		return null;
	}
}
