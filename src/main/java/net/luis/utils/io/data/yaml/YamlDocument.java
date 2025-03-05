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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.luis.utils.io.data.yaml.exception.YamlAnchorResolutionException;
import net.luis.utils.lang.StringUtils;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.BiConsumer;

/**
 *
 * @author Luis-St
 *
 */

public class YamlDocument {
	
	private final Map<String, YamlStruct> structs = Maps.newLinkedHashMap();
	
	//region Query operations
	public int size() {
		return this.structs.size();
	}
	
	public boolean isEmpty() {
		return this.structs.isEmpty();
	}
	
	public boolean containsKey(@Nullable String key) {
		return this.structs.containsKey(key);
	}
	
	public boolean containsStruct(@Nullable YamlStruct struct) {
		return this.structs.containsValue(struct);
	}
	
	public @NotNull @Unmodifiable Set<String> keySet() {
		return Collections.unmodifiableSet(this.structs.keySet());
	}
	
	public @NotNull @Unmodifiable Collection<YamlNode> structs() {
		return Collections.unmodifiableCollection(this.structs.values());
	}
	
	public @NotNull Set<Map.Entry<String, YamlStruct>> entrySet() {
		return this.structs.entrySet();
	}
	
	public void forEach(@NotNull BiConsumer<? super String, YamlStruct> action) {
		this.structs.forEach(Objects.requireNonNull(action, "Action must not be null"));
	}
	
	public @Nullable YamlStruct get(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.structs.get(key);
	}
	//endregion
	
	//region Modification operations
	public void add(@NotNull YamlStruct struct) {
		Objects.requireNonNull(struct, "yaml struct must not be null");
		this.structs.put(struct.getKey(), struct);
	}
	
	public void remove(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		this.structs.remove(key);
	}
	//endregion
	
	public void resolveAnchors() {
		Map<String, YamlNode> anchors = this.collectAnchors();
		Set<String> keys = Sets.newLinkedHashSet(this.structs.keySet());
		for (String key : keys) {
			YamlStruct struct = this.structs.get(key);
			if (struct.isAnchorDefined()) {
				YamlNode node = anchors.get(struct.getAnchorReference());
				if (node == null) {
					throw new YamlAnchorResolutionException("Anchor reference '" + struct.getAnchorReference() + "' of struct '" + key + "' not found");
				}
				this.structs.replace(key, new YamlStruct(struct.getKey(), node));
			}
		}
		for (YamlStruct struct : this.structs.values()) {
			if (struct.isAnchorDefined()) {
				throw new YamlAnchorResolutionException("Anchor reference '" + struct.getAnchorReference() + "' not resolved");
			}
			this.resolveNode(anchors, struct.getNode());
		}
	}
	
	//region Collect anchors
	private @NotNull Map<String, YamlNode> collectAnchors() {
		Map<String, YamlNode> anchors = Maps.newHashMap();
		for (YamlStruct struct : this.structs.values()) {
			if (!struct.hasAnchor()) {
				this.mergeAnchors(anchors, this.collectAnchors(struct));
			}
		}
		return anchors;
	}
	
	private @NotNull Map<String, YamlNode> collectAnchors(@NotNull YamlStruct struct) {
		Objects.requireNonNull(struct, "yaml struct must not be null");
		Map<String, YamlNode> anchors = Maps.newHashMap();
		if (struct.hasAnchor()) {
			anchors.put(struct.getAnchor(), struct);
		}
		YamlNode node = struct.getNode();
		if (node instanceof YamlMapping mapping) {
			this.mergeAnchors(anchors, this.collectAnchors(mapping.nodes()));
		} else if (node instanceof YamlSequence sequence) {
			this.mergeAnchors(anchors, this.collectAnchors(sequence));
		}
		return anchors;
	}
	
	private @NotNull Map<String, YamlNode> collectAnchors(@NotNull Iterable<? extends YamlNode> nodes) {
		Objects.requireNonNull(nodes, "Yaml nodes must not be null");
		Map<String, YamlNode> anchors = Maps.newHashMap();
		for (YamlNode node : nodes) {
			if (node.hasAnchor()) {
				anchors.put(node.getAnchor(), node);
			}
			if (node instanceof YamlMapping childMapping) {
				this.mergeAnchors(anchors, this.collectAnchors(childMapping.nodes()));
			} else if (node instanceof YamlSequence childSequence) {
				this.mergeAnchors(anchors, this.collectAnchors(childSequence));
			}
		}
		return anchors;
	}
	
	private void mergeAnchors(@NotNull Map<String, YamlNode> anchors, @NotNull Map<String, YamlNode> newCollected) {
		Objects.requireNonNull(anchors, "Anchors must not be null");
		Objects.requireNonNull(newCollected, "New collected anchors must not be null");
		for (Map.Entry<String, YamlNode> entry : newCollected.entrySet()) {
			if (anchors.containsKey(entry.getKey())) {
				throw new YamlAnchorResolutionException("Anchor '" + entry.getKey() + "' already defined");
			}
			if (entry.getValue().isYamlStruct()) {
				anchors.put(entry.getKey(), entry.getValue().getAsYamlStruct().getNode());
			} else {
				anchors.put(entry.getKey(), entry.getValue());
			}
		}
	}
	//endregion
	
	//region Resolve nodes
	private void resolveNode(@NotNull Map<String, YamlNode> anchors, @NotNull YamlNode node) {
		if (node instanceof YamlSequence sequence) {
			this.resolveSequence(anchors, sequence);
		} else if (node instanceof YamlMapping mapping) {
			this.resolveMapping(anchors, mapping);
		}
	}
	
	private void resolveSequence(@NotNull Map<String, YamlNode> anchors, @NotNull YamlSequence sequence) {
		for (String anchor : sequence.getAnchors()) {
			if (!anchors.containsKey(anchor)) {
				throw new YamlAnchorResolutionException("Anchor reference '" + anchor + "' of sequence not found");
			}
			sequence.add(anchors.get(anchor));
		}
		for (YamlNode node : sequence.nodes()) {
			this.resolveNode(anchors, node);
		}
		sequence.clearAnchors();
	}
	
	private void resolveMapping(@NotNull Map<String, YamlNode> anchors, @NotNull YamlMapping mapping) {
		for (String anchor : mapping.getAnchors()) {
			if (!anchors.containsKey(anchor)) {
				throw new YamlAnchorResolutionException("Anchor reference '" + anchor + "' of mapping not found");
			}
			if (!(anchors.get(anchor) instanceof YamlMapping baseMapping)) {
				throw new YamlAnchorResolutionException("Anchor '" + anchor + "' in mapping references a non-mapping node");
			}
			this.mergeMapping(anchor, mapping, baseMapping);
		}
		for (YamlNode node : mapping.nodes()) {
			this.resolveNode(anchors, node);
		}
		mapping.clearAnchors();
	}
	
	private void mergeMapping(@NotNull String anchor, @NotNull YamlMapping override, @NotNull YamlMapping base) {
		for (String key : base.keySet()) {
			if (!override.containsKey(key)) {
				override.add(key, base.get(key));
				continue;
			}
			YamlNode overrideNode = Objects.requireNonNull(override.get(key), "Override node must not be null");
			YamlNode baseNode = Objects.requireNonNull(base.get(key), "Base node must not be null");
			if (baseNode.getClass() != overrideNode.getClass()) {
				String baseType = StringUtils.getReadableString(baseNode.getClass().getSimpleName(), Character::isUpperCase);
				String overrideType = StringUtils.getReadableString(overrideNode.getClass().getSimpleName(), Character::isUpperCase);
				throw new YamlAnchorResolutionException("Override node does '" + overrideType +  "' not have the same type as the base node '" + baseType + "' defined by anchor '" + anchor + "' in mapping with key '" + key + "'");
			}
			if (baseNode.isYamlScalar()) {
				continue; // Scalars are not mergeable, use the override value
			} else if (baseNode.isYamlSequence()) {
				this.mergeSequence(overrideNode.getAsYamlSequence(), baseNode.getAsYamlSequence());
			} else if (baseNode.isYamlMapping()) {
				this.mergeMapping(anchor, overrideNode.getAsYamlMapping(), baseNode.getAsYamlMapping());
			}
		}
	}
	
	private void mergeSequence(@NotNull YamlSequence override, @NotNull YamlSequence base) {
		for (YamlNode node : base.nodes()) {
			if (!override.contains(node)) {
				override.add(node);
			}
		}
	}
	//endregion
}
