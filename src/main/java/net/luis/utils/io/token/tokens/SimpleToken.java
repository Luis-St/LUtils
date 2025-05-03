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

package net.luis.utils.io.token.tokens;

import net.luis.utils.io.token.TokenCategory;
import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.definition.TokenDefinition;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record SimpleToken(@NotNull TokenDefinition definition, @NotNull String value, @NotNull TokenPosition position) implements Token {
	
	public SimpleToken {
		Objects.requireNonNull(definition, "Token definition must not be null");
		Objects.requireNonNull(value, "Token value must not be null");
		Objects.requireNonNull(position, "Token position must not be null");
	}
	
	@Override
	public @NotNull String value() {
		return this.value;
	}
	
	@Override
	public @NotNull TokenPosition startPosition() {
		return this.position;
	}
	
	@Override
	public @NotNull TokenPosition endPosition() {
		return this.position;
	}
	
	public @NotNull Optional<TokenCategory> category() {
		return this.definition.category();
	}
	
	public boolean isCategory(@Nullable TokenCategory category) {
		return this.definition.isCategory(category);
	}
	
	@Override
	public @NotNull String toString() {
		return "SimpleToken[definition=" + this.definition + ",value=" + this.value.replace("\t", "\\t").replace("\n", "\\n") + ",position=" + this.position + "]";
	}
}
