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

import net.luis.utils.io.data.yaml.exception.YamlTypeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class YamlStruct extends AbstractYamlNode implements YamlNode {
	
	private final String key;
	private final YamlNode node;
	private final boolean anchorDefined;
	
	public YamlStruct(@NotNull String key, @NotNull String anchor) {
		this.key = Objects.requireNonNull(key, "Key must not be null");
		YamlHelper.validateYamlKey(this.key);
		this.node = new YamlScalar(Objects.requireNonNull(anchor, "Anchor must not be null"));
		this.anchorDefined = true;
	}
	
	public YamlStruct(@NotNull String key, @NotNull YamlNode node) {
		this.key = Objects.requireNonNull(key, "Key must not be null");
		YamlHelper.validateYamlKey(this.key);
		this.node = Objects.requireNonNull(node, "Node must not be null");
		this.anchorDefined = false;
	}
	
	@Override
	public boolean hasAnchor() {
		return !this.anchorDefined && this.node.hasAnchor();
	}
	
	@Override
	public @Nullable String getAnchor() {
		return this.anchorDefined ? null : this.node.getAnchor();
	}
	
	@Override
	public void setAnchor(@Nullable String anchor) {
		if (this.anchorDefined) {
			throw new YamlTypeException("Defining an anchor on a node where the value is defined by an anchor is not allowed");
		}
		this.node.setAnchor(anchor);
	}
	
	public @NotNull String getKey() {
		return this.key;
	}
	
	public boolean isAnchorDefined() {
		return this.anchorDefined;
	}
	
	public @NotNull YamlNode getNode() {
		if (this.anchorDefined) {
			throw new YamlTypeException("Struct does only contain an anchor");
		}
		return this.node;
	}
	
	public @NotNull String getAnchorReference() {
		if (!this.anchorDefined) {
			throw new YamlTypeException("Struct is not defined by an anchor");
		}
		return this.node.getAsYamlScalar().getAsString();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof YamlStruct that)) return false;
		
		if (this.anchorDefined != that.anchorDefined) return false;
		if (!this.key.equals(that.key)) return false;
		return this.node.equals(that.node);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key, this.node, this.anchorDefined);
	}
	
	@Override
	public String toString() {
		return this.toString(YamlConfig.DEFAULT);
	}
	
	@Override
	public @NotNull String toString(@NotNull YamlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		String base = this.key + ":";
		if ((this.node instanceof YamlSequence || this.node instanceof YamlMapping) && !this.node.hasAnchor()) {
			base += System.lineSeparator() + config.indent();
		} else {
			base += " ";
		}
		String node = this.node.toString(config);
		if (this.anchorDefined) {
			YamlScalar scalar = this.node.getAsYamlScalar();
			if (scalar.hasAnchor()) {
				base += "&" + scalar.getAnchor() + " ";
			}
			return base + "*" + scalar.getAsString();
		}
		return base + this.node.toString(config).replace(System.lineSeparator(), System.lineSeparator() + config.indent()).stripTrailing();
	}
	//endregion
}
