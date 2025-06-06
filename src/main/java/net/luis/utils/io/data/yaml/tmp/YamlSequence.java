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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.luis.utils.io.data.yaml.*;
import net.luis.utils.io.data.yaml.exception.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class YamlSequence extends AbstractYamlNode implements Iterable<YamlNode> {
	
	private final List<YamlNode> nodes = Lists.newLinkedList();
	private final Set<String> anchors = Sets.newLinkedHashSet();
	
	public YamlSequence() {}
	
	public YamlSequence(@NotNull List<? extends YamlNode> nodes) {
		Objects.requireNonNull(this.nodes, "Yaml nodes must not be null");
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
	
	public boolean contains(@Nullable YamlNode node) {
		return this.nodes.contains(node);
	}
	
	@Override
	public @NotNull Iterator<YamlNode> iterator() {
		return this.nodes.iterator();
	}
	
	public @NotNull @Unmodifiable Collection<YamlNode> nodes() {
		return Collections.unmodifiableCollection(this.nodes);
	}
	
	public @NotNull @Unmodifiable List<YamlNode> getNodes() {
		return List.copyOf(this.nodes);
	}
	//endregion
	
	//region Set operations
	public @NotNull YamlNode set(int index, @Nullable YamlNode node) {
		if (0 > index) {
			throw new YamlSequenceIndexOutOfBoundsException(index);
		}
		if (index >= this.size()) {
			throw new YamlSequenceIndexOutOfBoundsException(index, this.size());
		}
		return this.nodes.set(index, node == null ? YamlNull.INSTANCE : node);
	}
	
	public @NotNull YamlNode set(int index, @Nullable String value) {
		return this.set(index, value == null ? null : new YamlScalar(value));
	}
	
	public @NotNull YamlNode set(int index, boolean value) {
		return this.set(index, new YamlScalar(value));
	}
	
	public @NotNull YamlNode set(int index, @Nullable Number value) {
		return this.set(index, value == null ? null : new YamlScalar(value));
	}
	
	public @NotNull YamlNode set(int index, byte value) {
		return this.set(index, new YamlScalar(value));
	}
	
	public @NotNull YamlNode set(int index, short value) {
		return this.set(index, new YamlScalar(value));
	}
	
	public @NotNull YamlNode set(int index, int value) {
		return this.set(index, new YamlScalar(value));
	}
	
	public @NotNull YamlNode set(int index, long value) {
		return this.set(index, new YamlScalar(value));
	}
	
	public @NotNull YamlNode set(int index, float value) {
		return this.set(index, new YamlScalar(value));
	}
	
	public @NotNull YamlNode set(int index, double value) {
		return this.set(index, new YamlScalar(value));
	}
	//endregion
	
	//region Add operations
	public void add(@Nullable YamlNode node) {
		this.nodes.add(node == null ? YamlNull.INSTANCE : node);
	}
	
	public void add(@Nullable String value) {
		this.add(value == null ? null : new YamlScalar(value));
	}
	
	public void add(boolean value) {
		this.add(new YamlScalar(value));
	}
	
	public void add(@Nullable Number value) {
		this.add(value == null ? null : new YamlScalar(value));
	}
	
	public void add(byte value) {
		this.add(new YamlScalar(value));
	}
	
	public void add(short value) {
		this.add(new YamlScalar(value));
	}
	
	public void add(int value) {
		this.add(new YamlScalar(value));
	}
	
	public void add(long value) {
		this.add(new YamlScalar(value));
	}
	
	public void add(float value) {
		this.add(new YamlScalar(value));
	}
	
	public void add(double value) {
		this.add(new YamlScalar(value));
	}
	
	public void addAll(@NotNull YamlSequence array) {
		this.addAll(Objects.requireNonNull(array, "Yaml sequence must not be null").nodes);
	}
	
	public void addAll(YamlNode @NotNull ... elements) {
		Objects.requireNonNull(elements, "Yaml nodes must not be null");
		this.addAll(Arrays.asList(elements));
	}
	
	public void addAll(@NotNull List<? extends YamlNode> elements) {
		Objects.requireNonNull(elements, "Yaml nodes must not be null");
		elements.forEach(this::add);
	}
	//endregion
	
	//region Remove operations
	public @NotNull YamlNode remove(int index) {
		if (0 > index) {
			throw new YamlSequenceIndexOutOfBoundsException(index);
		}
		if (index >= this.size()) {
			throw new YamlSequenceIndexOutOfBoundsException(index, this.size());
		}
		return this.nodes.remove(index);
	}
	
	public boolean remove(@Nullable YamlNode node) {
		return this.nodes.remove(node);
	}
	
	public void clear() {
		this.nodes.clear();
	}
	//endregion
	
	//region Get operations
	public @NotNull YamlNode get(int index) {
		if (0 > index) {
			throw new YamlSequenceIndexOutOfBoundsException(index);
		}
		if (index >= this.size()) {
			throw new YamlSequenceIndexOutOfBoundsException(index, this.size());
		}
		return this.nodes.get(index);
	}
	
	public @NotNull YamlMapping getAsYamlMapping(int index) {
		YamlNode node = this.get(index);
		if (node instanceof YamlMapping mapping) {
			return mapping;
		}
		return node.getAsYamlMapping(); // throws YamlTypeException
	}
	
	public @NotNull YamlSequence getAsYamlSequence(int index) {
		YamlNode node = this.get(index);
		if (node instanceof YamlSequence sequence) {
			return sequence;
		}
		return node.getAsYamlSequence(); // throws YamlTypeException
	}
	
	public @NotNull YamlScalar getAsYamlScalar(int index) {
		YamlNode node = this.get(index);
		if (node instanceof YamlScalar scalar) {
			return scalar;
		}
		return node.getAsYamlScalar(); // throws YamlTypeException
	}
	
	public @NotNull String getAsString(int index) {
		return this.getAsYamlScalar(index).getAsString();
	}
	
	public boolean getAsBoolean(int index) {
		return this.getAsYamlScalar(index).getAsBoolean();
	}
	
	public @NotNull Number getAsNumber(int index) {
		return this.getAsYamlScalar(index).getAsNumber();
	}
	
	public byte getAsByte(int index) {
		return this.getAsYamlScalar(index).getAsByte();
	}
	
	public short getAsShort(int index) {
		return this.getAsYamlScalar(index).getAsShort();
	}
	
	public int getAsInteger(int index) {
		return this.getAsYamlScalar(index).getAsInteger();
	}
	
	public long getAsLong(int index) {
		return this.getAsYamlScalar(index).getAsLong();
	}
	
	public float getAsFloat(int index) {
		return this.getAsYamlScalar(index).getAsFloat();
	}
	
	public double getAsDouble(int index) {
		return this.getAsYamlScalar(index).getAsDouble();
	}
	
	public @NotNull YamlStruct getAsYamlStruct(int index) {
		YamlNode node = this.get(index);
		if (node instanceof YamlStruct struct) {
			return struct;
		}
		return node.getAsYamlStruct(); // throws YamlTypeException
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof YamlSequence yamlNodes)) return false;
		if (!super.equals(o)) return false;
		
		if (!this.nodes.equals(yamlNodes.nodes)) return false;
		return this.anchors.equals(yamlNodes.anchors);
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
		for (String anchor : this.anchors) {
			builder.append("- ").append("*").append(anchor).append(System.lineSeparator());
		}
		for (YamlNode node : this.nodes) {
			String value = node.toString(config).replace(System.lineSeparator(), System.lineSeparator() + config.indent());
			if (node instanceof YamlSequence) {
				builder.append("- ").append(System.lineSeparator()).append(config.indent());
			} else {
				builder.append("- ");
			}
			builder.append(value).append(System.lineSeparator());
		}
		return this.getBaseString(config) + builder.toString().stripTrailing();
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
