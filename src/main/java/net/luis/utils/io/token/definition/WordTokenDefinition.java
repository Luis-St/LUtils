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

import net.luis.utils.annotation.type.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Token definition for a word.<br>
 * This token definition matches a string that is not empty.<br>
 * It should be used for tokens that cannot be defined by a single character or string.<br>
 * An example would be identifiers in programming languages.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class WordTokenDefinition implements TokenDefinition {
	
	/**
	 * Singleton instance of the word token definition.<br>
	 */
	public static final WordTokenDefinition INSTANCE = new WordTokenDefinition();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private WordTokenDefinition() {}
	
	@Override
	public boolean matches(@NotNull String word) {
		Objects.requireNonNull(word, "Word must not be null");
		return !word.isEmpty();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof WordTokenDefinition that)) return false;
		
		return this == INSTANCE;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(WordTokenDefinition.class);
	}
	
	@Override
	public String toString() {
		return "WORD";
	}
	//endregion
}
