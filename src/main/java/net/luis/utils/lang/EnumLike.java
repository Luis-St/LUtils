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

package net.luis.utils.lang;

import com.google.common.collect.Lists;
import net.luis.utils.annotation.ReflectiveUsage;
import net.luis.utils.util.unsafe.reflection.ReflectionHelper;
import net.luis.utils.util.unsafe.reflection.ReflectionUtils;
import org.jetbrains.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * An interface that provides utility methods for enum-like classes.<br>
 * <p>
 *     An enum-like class is a class with constants like an enum but with a public constructor.<br>
 *     The enum-like class contains predefined constants of its own type.<br>
 *     An enum-like class is a class that contains constants of its own type.<br>
 * </p>
 * <p>
 *     It is anticipated that constants are added at runtime.<br>
 *     The constants are not required to be known at compile time.<br>
 * </p>
 * <p>
 *     Class Implementation example, see below:<br>
 *     The implementation can also be a record.<br>
 * </p>
 * <pre>{@code
 * public class Example implements EnumLike<Example> {
 *
 *   // Required field, must be exactly declared like this
 *   @ReflectiveUsage
 *   private static final List<Example> VALUES = Lists.newLinkedList();
 *
 *   public static final Example EXAMPLE_1 = new Example("Example 1");
 *   public static final Example EXAMPLE_2 = new Example("Example 2");
 *   public static final Example EXAMPLE_3 = new Example("Example 3");
 *
 *   private final String name;
 *
 *   public Example(String name) {
 *     this.name = name;
 *     VALUES.add(this);
 *   }
 *
 *   public @NotNull String name() {
 *     return this.name;
 *   }
 *
 *  // other methods
 * }
 * }</pre>
 * <p>
 *     The 'VALUES' field is required and must be exactly declared like above.<br>
 *     All instances of the enum-like class must be added to the 'VALUES' list in the constructor or a factory method.<br>
 * </p>
 *
 * @author Luis-St
 */
@SuppressWarnings({ "unchecked", "SuspiciousMethodCalls" })
public interface EnumLike<T extends EnumLike<T>> extends Comparable<T> {
	
	/**
	 * Checks if the given field is a constant.<br>
	 * A constant is a static final field.<br>
	 * @param field The field to check
	 * @return True, if the given field is a constant, otherwise false
	 * @throws NullPointerException If the given field is null
	 */
	private static boolean isConstant(@NotNull Field field) {
		Objects.requireNonNull(field, "Field must not be null");
		return Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers());
	}
	
	/**
	 * Gets all predefined constants of the given enum-like class.<br>
	 * A constant is a static final field of the given enum-like class.<br>
	 * @param enumType The enum-like class
	 * @return An unmodifiable list of all predefined constants declared in the given enum-like class
	 * @param <T> The type of the enum-like class
	 * @throws NullPointerException If the given enum-like class is null
	 */
	static <T extends EnumLike<T>> @NotNull @Unmodifiable List<EnumConstant<T>> getPredefinedConstants(@NotNull Class<T> enumType) {
		Objects.requireNonNull(enumType, "Enum type must not be null");
		List<EnumConstant<T>> values = Lists.newArrayList();
		List<Field> constants = ReflectionUtils.getFieldsForType(enumType, enumType);
		for (Field field : constants) {
			if (isConstant(field)) {
				T constant = (T) ReflectionHelper.get(field, null);
				values.add(new EnumConstant<>(field.getName(), constant.ordinal(), constant));
			}
		}
		return List.copyOf(values);
	}
	
	/**
	 * Gets the values all enum-like values of the given enum-like class.<br>
	 * <p>
	 *     The values are gathered from the 'VALUES' field in the given enum-like class.<br>
	 *     The 'VALUES' field is required and must be exactly declared like this:
	 * </p>
	 * <pre>{@code
	 * private static final List<Example> VALUES = Lists.newLinkedList();
	 * }</pre>
	 * @param enumType The enum-like class
	 * @return An unmodifiable list with the values of all constants declared in the given enum-like class
	 * @param <T> The type of the enum-like class
	 * @throws NullPointerException If the given enum-like class is null
	 * @throws ClassCastException If the 'VALUES' field in the given enum-like class is not a list of the given enum-like class
	 * @throws IllegalStateException If no 'VALUES' field in the given enum-like class exists
	 */
	static <T extends EnumLike<T>> @NotNull @Unmodifiable List<T> values(@NotNull Class<T> enumType) {
		Objects.requireNonNull(enumType, "Enum type must not be null");
		Predicate<Field> isConstant = field -> field.isAnnotationPresent(ReflectiveUsage.class) && Modifier.isPrivate(field.getModifiers()) && isConstant(field);
		if (ReflectionHelper.hasField(enumType, "VALUES", isConstant)) {
			return List.copyOf((List<T>) ReflectionHelper.get(enumType, "VALUES", null));
		}
		throw new IllegalStateException("No 'VALUES' field in " + enumType.getSimpleName() + " found");
	}
	
	/**
	 * Returns the constant of the given enum-like class with the specified name.<br>
	 * If the given name is null or no constant with the given name (case-insensitive) exists, an exception is thrown.<br>
	 * @param enumType The enum-like class
	 * @param name The name of the constant
	 * @return The constant with the given name
	 * @param <T> The type of the enum-like class
	 * @throws NullPointerException If the given enum-like class is null
	 * @throws IllegalStateException If no constant with the given name exists
	 */
	static <T extends EnumLike<T>> T valueOf(@NotNull Class<T> enumType, @Nullable String name) {
		return values(enumType).stream().filter(constant -> constant.name().equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new IllegalStateException("No constant '" + name + "' in " + enumType.getSimpleName() + " found"));
	}
	
	/**
	 * @return The name of the constant
	 */
	@NotNull String name();
	
	/**
	 * @return The ordinal of the constant
	 * @throws IllegalStateException If the constant was not correctly defined
	 */
	default int ordinal() {
		int index = values((Class<T>) this.getClass()).indexOf(this);
		if (index != -1) {
			return index;
		}
		throw new IllegalStateException("Constant was not correctly defined");
	}
	
	@Override
	default int compareTo(@NotNull T object) {
		Objects.requireNonNull(object, "Object to compare must not be null");
		return Integer.compare(this.ordinal(), object.ordinal());
	}
}
