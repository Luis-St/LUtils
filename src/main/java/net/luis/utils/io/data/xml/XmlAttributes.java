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

import com.google.common.collect.Maps;
import net.luis.utils.io.data.json.*;
import net.luis.utils.io.data.xml.exception.NoSuchXmlAttributeException;
import net.luis.utils.util.ValueParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class XmlAttributes {
	
	private final Map<String, XmlAttribute> attributes = Maps.newLinkedHashMap();
	
	public XmlAttributes() {}
	
	public XmlAttributes(@NotNull Map<String, XmlAttribute> attributes) {
		this.attributes.putAll(Objects.requireNonNull(attributes, "Attributes must not be null"));
	}
	
	//region Query operations
	public int size() {
		return this.attributes.size();
	}
	
	public boolean isEmpty() {
		return this.attributes.isEmpty();
	}
	
	public boolean containsKey(@Nullable String key) {
		return this.attributes.containsKey(key);
	}
	
	public boolean containsValue(@Nullable XmlAttribute attribute) {
		return this.attributes.containsValue(attribute);
	}
	
	public @NotNull Set<String> keySet() {
		return this.attributes.keySet();
	}
	
	public @NotNull Collection<XmlAttribute> values() {
		return this.attributes.values();
	}
	
	public @NotNull Set<Map.Entry<String, XmlAttribute>> entrySet() {
		return this.attributes.entrySet();
	}
	//endregion
	
	//region Add operations
	public @Nullable XmlAttribute add(@Nullable XmlAttribute attribute) {
		Objects.requireNonNull(attribute, "Attribute must not be null");
		return this.attributes.put(attribute.getKey(), attribute);
	}
	
	public @Nullable XmlAttribute add(@NotNull String key, @Nullable String value) {
		return this.add(new XmlAttribute(key, value));
	}
	
	public @Nullable XmlAttribute add(@NotNull String key, boolean value) {
		return this.add(new XmlAttribute(key, value));
	}
	
	public @Nullable XmlAttribute add(@NotNull String key, @Nullable Number value) {
		return this.add(new XmlAttribute(key, value));
	}
	
	public @Nullable XmlAttribute add(@NotNull String key, byte value) {
		return this.add(new XmlAttribute(key, value));
	}
	
	public @Nullable XmlAttribute add(@NotNull String key, short value) {
		return this.add(new XmlAttribute(key, value));
	}
	
	public @Nullable XmlAttribute add(@NotNull String key, int value) {
		return this.add(new XmlAttribute(key, value));
	}
	
	public @Nullable XmlAttribute add(@NotNull String key, long value) {
		return this.add(new XmlAttribute(key, value));
	}
	
	public @Nullable XmlAttribute add(@NotNull String key, float value) {
		return this.add(new XmlAttribute(key, value));
	}
	
	public @Nullable XmlAttribute add(@NotNull String key, double value) {
		return this.add(new XmlAttribute(key, value));
	}
	//endregion
	
	//region Remove operations
	public @Nullable XmlAttribute remove(@Nullable String key) {
		return this.attributes.remove(key);
	}
	
	public void clear() {
		this.attributes.clear();
	}
	//endregion
	
	//region Replace operations
	public @Nullable XmlAttribute replace(@NotNull String key, @NotNull XmlAttribute newAttribute) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(newAttribute, "New attribute must not be null");
		return this.attributes.replace(key, newAttribute);
	}
	
	public boolean replace(@NotNull String key, @NotNull XmlAttribute oldAttribute, @NotNull XmlAttribute newAttribute) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(oldAttribute, "Old attribute must not be null");
		Objects.requireNonNull(newAttribute, "New attribute must not be null");
		return this.attributes.replace(key, oldAttribute, newAttribute);
	}
	//endregion
	
	//region Get operations
	public @Nullable XmlAttribute get(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.attributes.get(key);
	}
	
	public @NotNull String getAsString(@NotNull String key) {
		XmlAttribute attribute = this.get(key);
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for key '" + key + "' but found none");
		}
		return attribute.getAsString();
	}
	
	public boolean getAsBoolean(@NotNull String key) {
		XmlAttribute attribute = this.get(key);
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for key '" + key + "' but found none");
		}
		return attribute.getAsBoolean();
	}
	
	public @NotNull Number getAsNumber(@NotNull String key) {
		XmlAttribute attribute = this.get(key);
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for key '" + key + "' but found none");
		}
		return attribute.getAsNumber();
	}
	
	public byte getAsByte(@NotNull String key) {
		XmlAttribute attribute = this.get(key);
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for key '" + key + "' but found none");
		}
		return attribute.getAsByte();
	}
	
	public short getAsShort(@NotNull String key) {
		XmlAttribute attribute = this.get(key);
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for key '" + key + "' but found none");
		}
		return attribute.getAsShort();
	}
	
	public int getAsInteger(@NotNull String key) {
		XmlAttribute attribute = this.get(key);
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for key '" + key + "' but found none");
		}
		return attribute.getAsInteger();
	}
	
	public long getAsLong(@NotNull String key) {
		XmlAttribute attribute = this.get(key);
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for key '" + key + "' but found none");
		}
		return attribute.getAsLong();
	}
	
	public float getAsFloat(@NotNull String key) {
		XmlAttribute attribute = this.get(key);
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for key '" + key + "' but found none");
		}
		return attribute.getAsFloat();
	}
	
	public double getAsDouble(@NotNull String key) {
		XmlAttribute attribute = this.get(key);
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for key '" + key + "' but found none");
		}
		return attribute.getAsDouble();
	}
	
	public <T> @NotNull T getAs(@NotNull String key, @NotNull ValueParser<String, T> parser) {
		XmlAttribute attribute = this.get(key);
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for key '" + key + "' but found none");
		}
		return attribute.getAs(parser);
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof XmlAttributes that)) return false;
		
		return this.attributes.equals(that.attributes);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.attributes);
	}
	
	@Override
	public String toString() {
		return this.toString(XmlConfig.DEFAULT);
	}
	
	public @NotNull String toString(@NotNull XmlConfig config) {
		StringBuilder builder = new StringBuilder();
		this.attributes.forEach((key, value) -> builder.append(value.toString(config)).append(" "));
		return builder.toString().strip();
	}
	//endregion
}
