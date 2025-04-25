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

import net.luis.utils.io.token.rule.Match;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record TransformTokenAction(@NotNull TokenTransformer transformer) implements TokenAction {
	
	public TransformTokenAction {
		Objects.requireNonNull(transformer, "Transformer must not be null");
	}
	
	@Override
	public @NotNull @Unmodifiable List<String> apply(@NotNull Match match) {
		Objects.requireNonNull(match, "Match must not be null");
		return this.transformer.transform(match.matchedTokens());
	}
}
