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

package net.luis.utils.io.data.yaml.tmp;

import net.luis.utils.io.data.yaml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public abstract class AbstractYamlNode implements YamlNode {
	
	private String anchor;
	
	@Override
	public boolean hasAnchor() {
		return this.anchor != null;
	}
	
	@Override
	public @Nullable String getAnchor() {
		return this.anchor;
	}
	
	@Override
	public void setAnchor(@Nullable String anchor) {
		this.anchor = anchor;
		if (this.anchor != null) {
			YamlHelper.validateYamlAnchor(this.anchor);
		}
	}
	
	protected @NotNull String getBaseString(@NotNull YamlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return this.hasAnchor() ? "&" + this.anchor : "";
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AbstractYamlNode that)) return false;
		
		return Objects.equals(this.anchor, that.anchor);
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
	//endregion
}
