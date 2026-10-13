/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.provider.TypeProvider;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Set;

/**
 * A reference to a single field of a data structure.<br>
 * The reference is passed to a {@link TypeProvider} to identify the field which should be read or written.<br>
 * <p>
 *     A field is identified by its name and optionally by a set of aliases.<br>
 *     Providers for named data structures like json or xml use the name and the aliases to address the field.<br>
 *     Providers for structures without field names like the binary format use the index instead.
 * </p>
 * <p>
 *     The index is the position of the field in the surrounding {@link CodecGroup}.<br>
 *     It is {@code -1} as long as the field is not part of a codec group.<br>
 *     The index is assigned by the codec group itself, it must not be set manually.
 * </p>
 *
 * @see FieldCodec
 * @see TypeProvider
 *
 * @author Luis-St
 *
 * @param name The name of the field
 * @param aliases The aliases of the field
 * @param index The index of the field in the surrounding codec group or {@code -1} if the field is not indexed
 */
public record FieldRef(@NonNull String name, @NonNull @Unmodifiable Set<String> aliases, int index) {
	
	/**
	 * The index which is used for fields that are not part of a codec group.<br>
	 */
	public static final int NO_INDEX = -1;
	
	/**
	 * Constructs a new field reference.<br>
	 *
	 * @param name The name of the field
	 * @param aliases The aliases of the field
	 * @param index The index of the field or {@code -1} if the field is not indexed
	 * @throws NullPointerException If the name or the aliases are null
	 * @throws IllegalArgumentException If the name is empty or the index is lower than {@code -1}
	 */
	public FieldRef {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(aliases, "Aliases must not be null");
		
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Name must not be empty");
		}
		if (index < NO_INDEX) {
			throw new IllegalArgumentException("Index must not be lower than " + NO_INDEX + ", but was " + index);
		}
		
		aliases = Set.copyOf(aliases);
	}
	
	/**
	 * Constructs a new field reference without any aliases and without an index.<br>
	 *
	 * @param name The name of the field
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is empty
	 */
	public FieldRef(@NonNull String name) {
		this(name, Set.of(), NO_INDEX);
	}
	
	/**
	 * Constructs a new field reference without an index.<br>
	 *
	 * @param name The name of the field
	 * @param aliases The aliases of the field
	 * @throws NullPointerException If the name or the aliases are null
	 * @throws IllegalArgumentException If the name is empty
	 */
	public FieldRef(@NonNull String name, @NonNull Set<String> aliases) {
		this(name, aliases, NO_INDEX);
	}
	
	/**
	 * Checks whether this field reference has an index assigned.<br>
	 * @return True if the index is not {@code -1}, false otherwise
	 */
	public boolean isIndexed() {
		return this.index != NO_INDEX;
	}
	
	/**
	 * Creates a copy of this field reference with the given index.<br>
	 *
	 * @param index The index to assign
	 * @return The copy with the given index
	 * @throws IllegalArgumentException If the index is lower than {@code -1}
	 */
	public @NonNull FieldRef withIndex(int index) {
		return new FieldRef(this.name, this.aliases, index);
	}
}
