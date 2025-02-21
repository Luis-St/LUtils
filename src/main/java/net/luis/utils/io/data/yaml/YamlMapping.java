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

import com.google.common.collect.*;
import net.luis.utils.io.data.yaml.exception.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.BiConsumer;

/**
 *
 * @author Luis-St
 *
 */

public class YamlMapping extends AbstractYamlNode {
	
	private final Map<String, YamlNode> nodes = Maps.newLinkedHashMap();
	private final Set<String> anchors = Sets.newLinkedHashSet();
	
	public YamlMapping() {}
	
	public YamlMapping(@NotNull List<YamlStruct> structs) {
		Objects.requireNonNull(structs, "yaml structs must not be null");
		structs.forEach(this::add);
	}
	
	public YamlMapping(@NotNull Map<String, ? extends YamlNode> nodes) {
		Objects.requireNonNull(nodes, "Yaml nodes must not be null");
		nodes.forEach(this::add);
	}
	
	//region Anchor operations
	private void addAnchor(@NotNull String anchor) {
		Objects.requireNonNull(anchor, "Anchor must not be null");
		YamlHelper.validateYamlAnchor(anchor);
		this.anchors.add(anchor);
	}
	
	public void addAnchor(String @NotNull ... anchor) {
		Objects.requireNonNull(anchor, "Anchor must not be null");
		this.addAnchor(Arrays.asList(anchor));
	}
	
	public void addAnchor(@NotNull List<String> anchors) {
		Objects.requireNonNull(anchors, "Anchors must not be null");
		anchors.forEach(this::addAnchor);
	}
	
	public void removeAnchor(@Nullable String anchor) {
		this.anchors.remove(anchor);
	}
	
	public void clearAnchors() {
		this.anchors.clear();
	}
	
	public @NotNull @Unmodifiable Set<String> getAnchors() {
		return Set.copyOf(this.anchors);
	}
	//endregion
	
	//region Query operations
	public int size() {
		return this.nodes.size();
	}
	
	public boolean isEmpty() {
		return this.nodes.isEmpty();
	}
	
	public boolean containsKey(@Nullable String key) {
		return this.nodes.containsKey(key);
	}
	
	public boolean containsNode(@Nullable YamlNode node) {
		return this.nodes.containsValue(node);
	}
	
	public @NotNull Set<String> keySet() {
		return this.nodes.keySet();
	}
	
	public @NotNull @Unmodifiable Collection<YamlNode> nodes() {
		return Collections.unmodifiableCollection(this.nodes.values());
	}
	
	public @NotNull Set<Map.Entry<String, YamlNode>> entrySet() {
		return this.nodes.entrySet();
	}
	
	public void forEach(@NotNull BiConsumer<? super String, ? super YamlNode> action) {
		this.nodes.forEach(Objects.requireNonNull(action, "Action must not be null"));
	}
	//endregion
	
	//region Add operations
	public @Nullable YamlNode add(@NotNull String key, @Nullable YamlNode node) {
		Objects.requireNonNull(key, "Key must not be null");
		YamlHelper.validateYamlKey(key);
		return this.nodes.put(key, node == null ? YamlNull.INSTANCE : node);
	}
	
	public @Nullable YamlNode add(@NotNull YamlStruct struct) {
		Objects.requireNonNull(struct, "Struct must not be null");
		return this.add(struct.getKey(), struct.getNode());
	}
	
	public @Nullable YamlNode add(@NotNull String key, @Nullable String value) {
		return this.add(key, value == null ? null : new YamlScalar(value));
	}
	
	public @Nullable YamlNode add(@NotNull String key, boolean value) {
		return this.add(key, new YamlScalar(value));
	}
	
	public @Nullable YamlNode add(@NotNull String key, @Nullable Number value) {
		return this.add(key, value == null ? null : new YamlScalar(value));
	}
	
	public @Nullable YamlNode add(@NotNull String key, byte value) {
		return this.add(key, new YamlScalar(value));
	}
	
	public @Nullable YamlNode add(@NotNull String key, short value) {
		return this.add(key, new YamlScalar(value));
	}
	
	public @Nullable YamlNode add(@NotNull String key, int value) {
		return this.add(key, new YamlScalar(value));
	}
	
	public @Nullable YamlNode add(@NotNull String key, long value) {
		return this.add(key, new YamlScalar(value));
	}
	
	public @Nullable YamlNode add(@NotNull String key, float value) {
		return this.add(key, new YamlScalar(value));
	}
	
	public @Nullable YamlNode add(@NotNull String key, double value) {
		return this.add(key, new YamlScalar(value));
	}
	
	public void addAll(@NotNull YamlMapping mapping) {
		this.nodes.putAll(Objects.requireNonNull(mapping, "Yaml mapping must not be null").nodes);
	}
	
	public void addAll(YamlStruct @NotNull ... structs) {
		Objects.requireNonNull(structs, "yaml structs must not be null");
		this.addAll(Arrays.asList(structs));
	}
	
	public void addAll(@NotNull List<YamlStruct> structs) {
		Objects.requireNonNull(structs, "yaml structs must not be null");
		structs.forEach(this::add);
	}
	
	public void addAll(@NotNull Map<String, ? extends YamlNode> nodes) {
		Objects.requireNonNull(nodes, "Yaml nodes must not be null");
		nodes.forEach(this::add);
	}
	//endregion
	
	//region Remove operations
	public @Nullable YamlNode remove(@Nullable String key) {
		return this.nodes.remove(key);
	}
	
	public void clear() {
		this.nodes.clear();
	}
	//endregion
	
	//region Replace operations
	public @Nullable YamlNode replace(@NotNull String key, @Nullable YamlNode newNode) {
		Objects.requireNonNull(key, "Key must not be null");
		YamlHelper.validateYamlKey(key);
		if (newNode instanceof YamlStruct struct) {
			key = struct.getKey();
			newNode = struct.getNode();
		}
		return this.nodes.replace(key, newNode == null ? YamlNull.INSTANCE : newNode);
	}
	
	public boolean replace(@NotNull String key, @NotNull YamlNode oldNode, @Nullable YamlNode newNode) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(oldNode, "Old node must not be null");
		YamlHelper.validateYamlKey(key);
		if (newNode instanceof YamlStruct struct) {
			key = struct.getKey();
			newNode = struct.getNode();
		}
		return this.nodes.replace(key, oldNode, newNode == null ? YamlNull.INSTANCE : newNode);
	}
	//endregion
	
	//region Get operations
	public @Nullable YamlNode get(@NotNull String key) {
		return this.nodes.get(Objects.requireNonNull(key, "Key must not be null"));
	}
	
	public @NotNull YamlMapping getAsYamlMapping(@NotNull String key) {
		YamlNode node = this.get(key);
		if (node == null) {
			throw new NoSuchYamlNodeException("Expected yaml mapping for key '" + key + "', but found none");
		}
		if (node instanceof YamlMapping mapping) {
			return mapping;
		}
		return node.getAsYamlMapping(); // throws YamlTypeException
	}
	
	public @NotNull YamlSequence getAsYamlSequence(@NotNull String key) {
		YamlNode node = this.get(key);
		if (node == null) {
			throw new NoSuchYamlNodeException("Expected yaml sequence for key '" + key + "', but found none");
		}
		if (node instanceof YamlSequence sequence) {
			return sequence;
		}
		return node.getAsYamlSequence(); // throws YamlTypeException
	}
	
	public @NotNull YamlScalar getAsYamlScalar(@NotNull String key) {
		YamlNode node = this.get(key);
		if (node == null) {
			throw new NoSuchYamlNodeException("Expected yaml scalar for key '" + key + "', but found none");
		}
		if (node instanceof YamlScalar scalar) {
			return scalar;
		}
		return node.getAsYamlScalar(); // throws YamlTypeException
	}
	
	public @NotNull String getAsString(@NotNull String key) {
		return this.getAsYamlScalar(key).getAsString();
	}
	
	public boolean getAsBoolean(@NotNull String key) {
		return this.getAsYamlScalar(key).getAsBoolean();
	}
	
	public @NotNull Number getAsNumber(@NotNull String key) {
		return this.getAsYamlScalar(key).getAsNumber();
	}
	
	public byte getAsByte(@NotNull String key) {
		return this.getAsYamlScalar(key).getAsByte();
	}
	
	public short getAsShort(@NotNull String key) {
		return this.getAsYamlScalar(key).getAsShort();
	}
	
	public int getAsInteger(@NotNull String key) {
		return this.getAsYamlScalar(key).getAsInteger();
	}
	
	public long getAsLong(@NotNull String key) {
		return this.getAsYamlScalar(key).getAsLong();
	}
	
	public float getAsFloat(@NotNull String key) {
		return this.getAsYamlScalar(key).getAsFloat();
	}
	
	public double getAsDouble(@NotNull String key) {
		return this.getAsYamlScalar(key).getAsDouble();
	}
	
	public @NotNull YamlStruct getAsYamlStruct(@NotNull String key) {
		YamlNode node = this.get(key);
		if (node == null) {
			throw new NoSuchYamlNodeException("Expected yaml struct for key '" + key + "', but found none");
		}
		if (node instanceof YamlStruct struct) {
			return struct;
		}
		return node.getAsYamlStruct(); // throws YamlTypeException
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof YamlMapping that)) return false;
		if (!super.equals(o)) return false;
		
		if (!this.nodes.equals(that.nodes)) return false;
		return this.anchors.equals(that.anchors);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.nodes, this.anchors);
	}
	
	@Override
	public String toString() {
		return this.toString(YamlConfig.DEFAULT);
	}
	
	@Override
	public @NotNull String toString(@NotNull YamlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		StringBuilder builder = new StringBuilder();
		if (!this.anchors.isEmpty()) {
			builder.append(this.getAnchorString(config, Lists.newLinkedList(this.anchors))).append(System.lineSeparator());
		}
		for (Map.Entry<String, YamlNode> entry : this.nodes.entrySet()) {
			YamlNode node = entry.getValue();
			builder.append(entry.getKey()).append(":");
			if ((node instanceof YamlSequence || node instanceof YamlMapping) && !node.hasAnchor()) {
				builder.append(System.lineSeparator()).append(config.indent());
			} else {
				builder.append(" ");
			}
			String value = node.toString(config).replace(System.lineSeparator(), System.lineSeparator() + config.indent());
			builder.append(value).append(System.lineSeparator());
			
		}
		return this.getBaseString(config) + builder.toString().stripTrailing();
	}
	
	private @NotNull String getAnchorString(@NotNull YamlConfig config, @NotNull List<String> anchors) {
		Objects.requireNonNull(config, "Config must not be null");
		if (anchors.size() == 1) {
			return "<<: *" + anchors.getFirst();
		}
		StringBuilder builder = new StringBuilder("<<: ");
		builder.append(System.lineSeparator());
		for (String anchor : anchors) {
			builder.append(config.indent()).append("- *").append(anchor).append(System.lineSeparator());
		}
		return builder.toString().stripTrailing();
	}
	
	@Override
	protected @NotNull String getBaseString(@NotNull YamlConfig config) {
		String base = super.getBaseString(config);
		if (base.isEmpty()) {
			return "";
		}
		return base + System.lineSeparator();
	}
	//endregion
}
