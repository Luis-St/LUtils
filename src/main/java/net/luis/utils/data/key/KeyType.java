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

package net.luis.utils.data.key;

import com.google.common.collect.Lists;
import net.luis.utils.annotation.ReflectiveUsage;
import net.luis.utils.lang.EnumLike;
import net.luis.utils.util.Priority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author Luis-St
 *
 */

public class KeyType<K extends Key> implements EnumLike<KeyType<?>> {
	
	@ReflectiveUsage
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private static final List<KeyType<?>> VALUES = Lists.newLinkedList();
	
	public static final KeyType<StringKey> STRING = new KeyType<>("string", Priority.LOW, StringKey::isValid, StringKey::new);
	public static final KeyType<NamespaceKey> NAMESPACE = new KeyType<>("namespace", Priority.NORMAL, NamespaceKey::isValid, NamespaceKey::new);
	public static final KeyType<PatternKey> PATTERN = new KeyType<>("pattern", Priority.HIGH, PatternKey::isValid, PatternKey::new);
	public static final KeyType<UniqueKey> UNIQUE = new KeyType<>("unique", Priority.HIGH, UniqueKey::isValid, UniqueKey::new);
	
	private final String name;
	private final Priority priority;
	private final Predicate<String> validator;
	private final Function<String, K> factory;
	
	public KeyType(@NotNull String name, @NotNull Priority priority, @NotNull Predicate<String> validator, @NotNull Function<String, K> factory) {
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.priority = Objects.requireNonNull(priority, "Priority must not be null");
		this.validator = Objects.requireNonNull(validator, "Validator must not be null");
		this.factory = Objects.requireNonNull(factory, "Factory must not be null");
		VALUES.add(this);
	}
	
	public @NotNull String name() {
		return this.name;
	}
	
	public @NotNull Priority getPriority() {
		return this.priority;
	}
	
	public boolean isValid(@Nullable String key) {
		return this.validator.test(key);
	}
	
	public @NotNull K createKey(@NotNull String key) {
		return this.factory.apply(key);
	}
	
	@Override
	public int compareTo(@NotNull KeyType<?> object) {
		return this.priority.compareTo(object.priority);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KeyType<?> keyType)) return false;
		
		if (!this.name.equals(keyType.name)) return false;
		if (!this.priority.equals(keyType.priority)) return false;
		if (!this.validator.equals(keyType.validator)) return false;
		return this.factory.equals(keyType.factory);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.priority, this.validator, this.factory);
	}
	
	@Override
	public String toString() {
		return "KeyType: " + this.name + " (" + this.priority + ")";
	}
	//endregion
}
