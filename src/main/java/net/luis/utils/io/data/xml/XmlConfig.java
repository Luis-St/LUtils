/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.data.xml;

import net.luis.utils.io.data.config.ReadOnly;
import net.luis.utils.io.data.config.WriteOnly;
import net.luis.utils.util.ErrorAction;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Luis-St
 *
 */

public record XmlConfig(
	@WriteOnly boolean prettyPrint,
	@WriteOnly("prettyPrint") @NotNull String indent,
	@ReadOnly boolean allowAttributes,
	@WriteOnly boolean simplifyValues,
	@NotNull Charset charset,
	@NotNull ErrorAction errorAction
) {
	
	public static final XmlConfig DEFAULT = new XmlConfig(true, "\t", true, true, StandardCharsets.UTF_8, ErrorAction.THROW);
}
