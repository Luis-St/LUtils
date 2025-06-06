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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class YamlStruct {
	
	private final String key;
	private final @Nullable String anchorDefinition;
	private final YamlNode node;
	
	public YamlStruct(@NotNull String key, @NotNull YamlNode node) {
		this.key = Objects.requireNonNull(key, "Key must not be null");
		YamlHelper.validateYamlKey(this.key);
		this.anchorDefinition = null;
		this.node = Objects.requireNonNull(node, "Node must not be null");
	}
	
	public YamlStruct(@NotNull String key, @NotNull String anchorDefinition, @NotNull YamlNode node) {
		this.key = Objects.requireNonNull(key, "Key must not be null");
		YamlHelper.validateYamlKey(this.key);
		this.anchorDefinition = Objects.requireNonNull(anchorDefinition, "Anchor definition must not be null");
		YamlHelper.validateYamlAnchor(this.anchorDefinition);
		this.node = Objects.requireNonNull(node, "Node must not be null");
	}
	
	public @NotNull String getKey() {
		return this.key;
	}
	
	public boolean hasAnchorDefinition() {
		return this.anchorDefinition != null;
	}
	
	public @Nullable String getAnchorDefinition() {
		return this.anchorDefinition;
	}
	
	public @NotNull YamlNode getNode() {
		return this.node;
	}
}
