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

package net.luis.utils.io.token.definition;

import net.luis.utils.io.token.rule.rules.TokenRule;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface TokenDefinition {
	
	TokenDefinition WORD = WordTokenDefinition.INSTANCE;
	
	//region Factory methods
	static @NotNull TokenDefinition of(char token) {
		return new CharTokenDefinition(token);
	}
	
	static @NotNull TokenDefinition of(@NotNull String token, boolean equalsIgnoreCase) {
		Objects.requireNonNull(token, "Token must not be null");
		return new StringTokenDefinition(token, equalsIgnoreCase);
	}
	//endregion
	
	boolean matches(@NotNull String word);
	
	//region Rule creation
	default @NotNull TokenRule rule() {
		return TokenRule.single(this);
	}
	
	default @NotNull TokenRule optional() {
		return TokenRule.optional(this.rule());
	}
	
	default @NotNull TokenRule atLeast(int min) {
		return TokenRule.atLeast(this.rule(), min);
	}
	
	default @NotNull TokenRule exactly(int occurrences) {
		return TokenRule.exactly(this.rule(), occurrences);
	}
	
	default @NotNull TokenRule atMost(int max) {
		return TokenRule.atMost(this.rule(), max);
	}
	
	default @NotNull TokenRule between(int min, int max) {
		return TokenRule.between(this.rule(), min, max);
	}
	
	default @NotNull TokenRule followedBy(@NotNull TokenDefinition definition) {
		Objects.requireNonNull(definition, "Token definition must not be null");
		return TokenRule.sequence(this.rule(), definition.rule());
	}
	
	default @NotNull TokenRule followedBy(TokenDefinition @NotNull ... definitions) {
		Objects.requireNonNull(definitions, "Token definition array must not be null");
		List<TokenRule> rules = Stream.of(definitions).map(TokenDefinition::rule).collect(Collectors.toList());
		rules.addFirst(this.rule());
		return TokenRule.sequence(rules);
	}
	
	default @NotNull TokenRule or(@NotNull TokenDefinition definition) {
		Objects.requireNonNull(definition, "Token definition must not be null");
		return TokenRule.any(this.rule(), definition.rule());
	}
	
	default @NotNull TokenRule or(TokenDefinition @NotNull ... definitions) {
		Objects.requireNonNull(definitions, "Token definition array must not be null");
		Set<TokenRule> rules = Stream.of(definitions).map(TokenDefinition::rule).collect(Collectors.toSet());
		rules.add(this.rule());
		return TokenRule.any(rules);
	}
	
	default @NotNull TokenRule asStart(@NotNull TokenDefinition endDefinition) {
		Objects.requireNonNull(endDefinition, "End token definition must not be null");
		return TokenRule.boundary(this.rule(), endDefinition.rule());
	}
	
	default @NotNull TokenRule asStart(@NotNull TokenDefinition betweenDefinition, @NotNull TokenDefinition endDefinition) {
		Objects.requireNonNull(betweenDefinition, "Between token definition must not be null");
		Objects.requireNonNull(endDefinition, "End token definition must not be null");
		return TokenRule.boundary(this.rule(), betweenDefinition.rule(), endDefinition.rule());
	}
	
	default @NotNull TokenRule asEnd(@NotNull TokenDefinition startDefinition) {
		Objects.requireNonNull(startDefinition, "Start token definition must not be null");
		return TokenRule.boundary(startDefinition.rule(), this.rule());
	}
	
	default @NotNull TokenRule asEnd(@NotNull TokenDefinition startDefinition, @NotNull TokenDefinition betweenDefinition) {
		Objects.requireNonNull(startDefinition, "Start token definition must not be null");
		Objects.requireNonNull(betweenDefinition, "Between token definition must not be null");
		return TokenRule.boundary(startDefinition.rule(), betweenDefinition.rule(), this.rule());
	}
	//endregion
}
