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

package net.luis.utils.io.codec.mapping;

import net.luis.utils.util.Either;
import net.luis.utils.util.unsafe.reflection.ReflectionHelper;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.Objects;
import java.util.Optional;

/**
 * A wrapper class that represents a component of a class which can be either a record component or a field.<br>
 * This class provides a unified interface to access properties of components regardless of their underlying type.<br>
 * It is used by the codec auto-mapping system to extract information about class components for codec creation.<br>
 *
 * @author Luis-St
 */
public class CodecComponent {
	
	/**
	 * The underlying component that this codec component wraps.<br>
	 * This can be either a record component or a field, represented as an Either type.<br>
	 * Using the Either type allows handling both types through a unified interface while preserving type safety.<br>
	 */
	private final Either<RecordComponent, Field> component;
	
	/**
	 * Constructs a new codec component from a record component.<br>
	 *
	 * @param component The record component to wrap
	 * @throws NullPointerException If the record component is null
	 */
	public CodecComponent(@NonNull RecordComponent component) {
		this.component = Either.left(Objects.requireNonNull(component, "Record component must not be null"));
	}
	
	/**
	 * Constructs a new codec component from a field.<br>
	 *
	 * @param component The field to wrap
	 * @throws NullPointerException If the field is null
	 */
	public CodecComponent(@NonNull Field component) {
		this.component = Either.right(Objects.requireNonNull(component, "Field must not be null"));
	}
	
	/**
	 * Gets the name of this component.<br>
	 * @return The name
	 */
	public @NonNull String getName() {
		return this.component.mapTo(
			RecordComponent::getName,
			Field::getName
		);
	}
	
	/**
	 * Gets the type of this component.<br>
	 * @return The type as a class
	 */
	public @NonNull Class<?> getType() {
		return this.component.mapTo(
			RecordComponent::getType,
			Field::getType
		);
	}
	
	/**
	 * Gets an annotation of the specified type from this component.<br>
	 *
	 * @param annotationClass The class of the annotation to get
	 * @param <T> The type of the annotation
	 * @return The annotation if present, or null if not
	 * @throws NullPointerException If the annotation class is null
	 */
	public <T extends Annotation> @UnknownNullability T getAnnotation(@NonNull Class<T> annotationClass) {
		Objects.requireNonNull(annotationClass, "Annotation class must not be null");
		return this.component.mapTo(
			component -> component.getAnnotation(annotationClass),
			field -> field.getAnnotation(annotationClass)
		);
	}
	
	/**
	 * Accesses the value of this component in the given instance.<br>
	 * For record components, this uses the accessor method.<br>
	 * For fields, this uses reflection to get the field value.<br>
	 *
	 * @param instance The instance to access the component value from
	 * @return An optional containing the value of the component, or an empty optional if access failed
	 * @throws NullPointerException If the instance is null
	 */
	public @NonNull Optional<Object> accessValue(@NonNull Object instance) {
		Objects.requireNonNull(instance, "Instance must not be null");
		return this.component.mapTo(
			component -> ReflectionHelper.invoke(component.getAccessor(), instance),
			field -> ReflectionHelper.get(field, instance)
		);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CodecComponent that)) return false;
		
		return this.component.equals(that.component);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.component);
	}
	//endregion
}
