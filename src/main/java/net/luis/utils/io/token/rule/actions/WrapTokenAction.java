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
import net.luis.utils.io.token.rule.Match;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record WrapTokenAction(@NotNull String prefix, @NotNull String suffix) implements TokenAction {
	
	public WrapTokenAction {
		Objects.requireNonNull(prefix, "Prefix must not be null");
		Objects.requireNonNull(suffix, "Suffix must not be null");
	}
	
	@Override
	public @NotNull @Unmodifiable List<String> apply(@NotNull Match match) {
		Objects.requireNonNull(match, "Match must not be null");
		
		List<String> tokens = match.matchedTokens();
		if (tokens.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<String> result = Lists.newArrayListWithExpectedSize(tokens.size());
		result.add(this.prefix + tokens.getFirst());
		
		for (int i = 1; i < tokens.size() - 1; i++) {
			result.add(tokens.get(i));
		}
		
		if (tokens.size() > 1) {
			result.add(tokens.getLast() + this.suffix);
		} else {
			String firstToken = result.removeFirst();
			result.add(firstToken + this.suffix);
		}
		return result;
	}
}
