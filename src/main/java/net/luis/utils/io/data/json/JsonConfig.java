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

package net.luis.utils.io.data.json;

import net.luis.utils.io.data.config.WriteOnly;
import net.luis.utils.util.ErrorAction;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record JsonConfig(
	@WriteOnly @NotNull String indent,
	@NotNull Charset charset,
	@NotNull ErrorAction errorAction
) {
	
	public static final JsonConfig DEFAULT = new JsonConfig("\t", StandardCharsets.UTF_8, ErrorAction.THROW);
	
	public JsonConfig {
		Objects.requireNonNull(charset, "Charset must not be null");
		Objects.requireNonNull(errorAction, "Error action must not be null");
	}
}
