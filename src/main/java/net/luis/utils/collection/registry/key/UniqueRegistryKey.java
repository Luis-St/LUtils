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

package net.luis.utils.collection.registry.key;

import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * A registry key with a unique id of type {@link UUID}.<br>
 *
 * @author Luis-St
 */
public final class UniqueRegistryKey implements RegistryKey<UUID> {
	
	/**
	 * The default unique registry key with an empty UUID ({@link Utils#EMPTY_UUID}).<br>
	 */
	public static final UniqueRegistryKey DEFAULT = of(Utils.EMPTY_UUID);
	
	/**
	 * The unique id of the registry key.<br>
	 */
	private final UUID key;
	
	/**
	 * Constructs a new unique registry key with the given unique id.<br>
	 * @param key The unique id of the registry key
	 * @throws NullPointerException If the given key is null
	 */
	private UniqueRegistryKey(@NotNull UUID key) {
		this.key = Objects.requireNonNull(key, "Key must not be null");
	}
	
	//region Static factory methods
	
	/**
	 * Creates a new unique registry key with a random unique id.<br>
	 * @return The created unique registry key
	 */
	public static @NotNull UniqueRegistryKey create() {
		return new UniqueRegistryKey(UUID.randomUUID());
	}
	
	/**
	 * Creates a new unique registry key with the given unique id.<br>
	 * @param uniqueId The unique id of the registry key
	 * @return The created unique registry key
	 * @throws NullPointerException If the given unique id is null
	 */
	public static @NotNull UniqueRegistryKey of(@NotNull UUID uniqueId) {
		return new UniqueRegistryKey(uniqueId);
	}
	//endregion
	
	@Override
	public @NotNull UUID getKey() {
		return this.key;
	}
	
	@Override
	public int compareTo(@NotNull RegistryKey<UUID> otherKey) {
		return this.key.compareTo(otherKey.getKey());
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof UniqueRegistryKey that)) return false;
		
		return this.key.equals(that.key);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key);
	}
	
	@Override
	public String toString() {
		return this.key.toString();
	}
	//endregion
}
