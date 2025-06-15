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

package net.luis.utils.io.codec.mapping;

import net.luis.utils.util.Either;
import net.luis.utils.util.unsafe.reflection.ReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public class CodecComponent {
	
	private final Either<RecordComponent, Field> component;
	
	public CodecComponent(@NotNull RecordComponent component) {
		this.component = Either.left(Objects.requireNonNull(component, "Record component must not be null"));
	}
	
	public CodecComponent(@NotNull Field component) {
		this.component = Either.right(Objects.requireNonNull(component, "Field must not be null"));
	}
	
	public @NotNull String getName() {
		return this.component.mapTo(
			RecordComponent::getName,
			Field::getName
		);
	}
	
	public @NotNull Class<?> getType() {
		return this.component.mapTo(
			RecordComponent::getType,
			Field::getType
		);
	}
	
	public <T extends Annotation> @UnknownNullability T getAnnotation(@NotNull Class<T> annotationClass) {
		return this.component.mapTo(
			component -> component.getAnnotation(annotationClass),
			field -> field.getAnnotation(annotationClass)
		);
	}
	
	public @NotNull Optional<Object> accessValue(@NotNull Object instance) {
		return this.component.mapTo(
			component -> ReflectionHelper.invoke(component.getAccessor(), instance),
			field -> ReflectionHelper.get(field, instance)
		);
	}
}
