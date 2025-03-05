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

package net.luis.utils.io.data.yaml;

import net.luis.utils.io.data.config.ReadOnly;
import net.luis.utils.io.data.config.WriteOnly;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Luis-St
 *
 */

public record YamlConfig(
	@ReadOnly boolean strict,
	@WriteOnly boolean useNullLiteral,
	@WriteOnly boolean multilineStringsToBlockScalars,
	@WriteOnly char blockScalarStyle,
	@WriteOnly boolean quoteSingleLineStrings,
	@WriteOnly char quoteCharacter,
	@WriteOnly @NotNull String indent,
	@NotNull Charset charset
) {
	
	public static final YamlConfig DEFAULT = new YamlConfig(true, true, true, '|', false, '\'', "  ", StandardCharsets.UTF_8);
}
