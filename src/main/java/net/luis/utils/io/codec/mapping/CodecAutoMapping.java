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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.codec.*;
import net.luis.utils.util.Either;
import net.luis.utils.util.Utils;
import net.luis.utils.util.unsafe.reflection.ReflectionHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A utility class for automatically creating codecs for classes using reflection.<br>
 * This class provides methods to generate codecs for classes without having to manually implement them.<br>
 * It supports record classes (preferred), regular classes, and enums.<br>
 *
 * @see Codec
 * @see CodecBuilder
 *
 * @author Luis-St
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CodecAutoMapping {
	
	/**
	 * An empty array of codec components used as a default return value when no components are found.<br>
	 * This avoids creating a new empty array each time no components are found.<br>
	 */
	private static final CodecComponent[] EMPTY_CODEC_COMPONENTS = new CodecComponent[0];
	
	/**
	 * A lookup map that associates Java classes with their corresponding codec implementations.<br>
	 * This map contains predefined codecs for commonly used Java types like primitives, strings, and date/time classes.<br>
	 * When creating an auto-mapped codec, this map is consulted first to find an existing codec for a given type.<br>
	 */
	private static final Map<Class<?>, Codec<?>> CODEC_LOOKUP = Utils.make(Maps.newHashMap(), map -> {
		map.put(Boolean.class, Codec.BOOLEAN);
		map.put(Byte.class, Codec.BYTE);
		map.put(Short.class, Codec.SHORT);
		map.put(Integer.class, Codec.INTEGER);
		map.put(Long.class, Codec.LONG);
		map.put(Float.class, Codec.FLOAT);
		map.put(Double.class, Codec.DOUBLE);
		map.put(String.class, Codec.STRING);
		map.put(Character.class, Codec.CHARACTER);
		map.put(UUID.class, Codec.UUID);
		map.put(LocalTime.class, Codec.LOCAL_TIME);
		map.put(LocalDate.class, Codec.LOCAL_DATE);
		map.put(LocalDateTime.class, Codec.LOCAL_DATE_TIME);
		map.put(ZonedDateTime.class, Codec.ZONED_DATE_TIME);
		map.put(Charset.class, Codec.CHARSET);
		map.put(File.class, Codec.FILE);
		map.put(Path.class, Codec.PATH);
		map.put(URI.class, Codec.URI);
		map.put(URL.class, Codec.URL);
	});
	
	/**
	 * Creates an automatically mapped codec for the given class.<br>
	 * The method analyzes the class structure and creates a codec that can encode and decode instances of the class.<br>
	 * For record classes, it uses the record components to create the codec.<br>
	 * For regular classes, it uses either annotated fields or non-static, final fields to create the codec.<br>
	 *
	 * @param clazz The class for which a codec should be created
	 * @param <O> The type of the class
	 * @return A codec for the given class
	 * @throws NullPointerException If the provided class is null
	 * @throws IllegalArgumentException If the class is an interface, annotation, primitive type, or has invalid structure
	 * @see CodecConstructor
	 * @see CodecField
	 * @see GenericInfo
	 */
	public static @NotNull <O> Codec<O> createAutoMappedCodec(@NotNull Class<O> clazz) {
		Objects.requireNonNull(clazz, "Class must not be null");
		if (clazz.isInterface() || clazz.isAnnotation() || clazz.isPrimitive()) {
			throw new IllegalArgumentException("Class must not be an interface, annotation or primitive type: " + clazz.getName());
		}
		
		System.setProperty("reflection.exceptions.throw", "true");
		Codec<O> codec = createCodec(clazz);
		System.setProperty("reflection.exceptions.throw", "false");
		return codec;
	}
	
	/**
	 * Creates a codec for the given class based on its structure.<br>
	 * This method handles different types of classes including enums and complex classes with fields.<br>
	 *
	 * @param clazz The class for which to create a codec
	 * @param <O> The type of the class
	 * @return A codec for the given class
	 * @throws IllegalArgumentException If the class has more than 16 components or an invalid structure
	 */
	private static <O> @NotNull Codec<O> createCodec(@NotNull Class<O> clazz) {
		if (clazz.isEnum()) {
			return Codec.dynamicEnum((Class<? extends Enum>) clazz);
		}
		
		CodecComponent[] components = getComponents(clazz);
		Constructor<O> constructor = getConstructor(clazz, components);
		if (components.length == 0) {
			O instance = ReflectionHelper.newInstance(constructor).orElseThrow();
			return Codec.unit(instance);
		}
		if (components.length > 16) {
			throw new IllegalArgumentException("Record class has too many components (max 16): " + clazz.getName());
		}
		
		ConfiguredCodec<?, O>[] configuredCodecs = Stream.of(components).map(CodecAutoMapping::createConfiguredCodec).toArray(ConfiguredCodec[]::new);
		return switch (components.length) {
			case 1 -> CodecBuilder.group(
				configuredCodecs[0]
			).create(arg0 -> createInstance(constructor, arg0));
			case 2 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1]
			).create((arg0, arg1) -> createInstance(constructor, arg0, arg1));
			case 3 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2]
			).create((arg0, arg1, arg2) -> createInstance(constructor, arg0, arg1, arg2));
			case 4 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3]
			).create((arg0, arg1, arg2, arg3) -> createInstance(constructor, arg0, arg1, arg2, arg3));
			case 5 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3], configuredCodecs[4]
			).create((arg0, arg1, arg2, arg3, arg4) -> createInstance(constructor, arg0, arg1, arg2, arg3, arg4));
			case 6 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3], configuredCodecs[4], configuredCodecs[5]
			).create((arg0, arg1, arg2, arg3, arg4, arg5) -> createInstance(constructor, arg0, arg1, arg2, arg3, arg4, arg5));
			case 7 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3], configuredCodecs[4], configuredCodecs[5], configuredCodecs[6]
			).create((arg0, arg1, arg2, arg3, arg4, arg5, arg6) -> createInstance(constructor, arg0, arg1, arg2, arg3, arg4, arg5, arg6));
			case 8 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3], configuredCodecs[4], configuredCodecs[5], configuredCodecs[6], configuredCodecs[7]
			).create((arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) -> createInstance(constructor, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7));
			case 9 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3], configuredCodecs[4], configuredCodecs[5], configuredCodecs[6], configuredCodecs[7],
				configuredCodecs[8]
			).create((arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) -> createInstance(constructor, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8));
			case 10 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3], configuredCodecs[4], configuredCodecs[5], configuredCodecs[6], configuredCodecs[7],
				configuredCodecs[8], configuredCodecs[9]
			).create((arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) -> createInstance(constructor, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9));
			case 11 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3], configuredCodecs[4], configuredCodecs[5], configuredCodecs[6], configuredCodecs[7],
				configuredCodecs[8], configuredCodecs[9], configuredCodecs[10]
			).create((arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) -> createInstance(
				constructor, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10
			));
			case 12 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3], configuredCodecs[4], configuredCodecs[5], configuredCodecs[6], configuredCodecs[7],
				configuredCodecs[8], configuredCodecs[9], configuredCodecs[10], configuredCodecs[11]
			).create((arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11) -> createInstance(
				constructor, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11
			));
			case 13 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3], configuredCodecs[4], configuredCodecs[5], configuredCodecs[6], configuredCodecs[7],
				configuredCodecs[8], configuredCodecs[9], configuredCodecs[10], configuredCodecs[11], configuredCodecs[12]
			).create((arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12) -> createInstance(
				constructor, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12
			));
			case 14 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3], configuredCodecs[4], configuredCodecs[5], configuredCodecs[6], configuredCodecs[7],
				configuredCodecs[8], configuredCodecs[9], configuredCodecs[10], configuredCodecs[11], configuredCodecs[12], configuredCodecs[13]
			).create((arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13) -> createInstance(
				constructor, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13
			));
			case 15 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3], configuredCodecs[4], configuredCodecs[5], configuredCodecs[6], configuredCodecs[7],
				configuredCodecs[8], configuredCodecs[9], configuredCodecs[10], configuredCodecs[11], configuredCodecs[12], configuredCodecs[13], configuredCodecs[14]
			).create((arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14) -> createInstance(
				constructor, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14
			));
			case 16 -> CodecBuilder.group(
				configuredCodecs[0], configuredCodecs[1], configuredCodecs[2], configuredCodecs[3], configuredCodecs[4], configuredCodecs[5], configuredCodecs[6], configuredCodecs[7],
				configuredCodecs[8], configuredCodecs[9], configuredCodecs[10], configuredCodecs[11], configuredCodecs[12], configuredCodecs[13], configuredCodecs[14], configuredCodecs[15]
			).create((arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15) -> createInstance(
				constructor, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15
			));
			default -> throw new IllegalArgumentException("Record class has too many components (max 16): " + clazz.getName());
		};
	}
	
	/**
	 * Gets the components (fields or record components) of a class that should be included in the codec.<br>
	 * For record classes, it returns all record components.<br>
	 * For regular classes, it returns either all fields annotated with {@link CodecField} or all non-static, final fields that are not transient.<br>
	 *
	 * @param clazz The class to get components from
	 * @return An array of codec components
	 * @throws NullPointerException If the provided class is null
	 * @see CodecField
	 */
	private static CodecComponent @NotNull [] getComponents(@NotNull Class<?> clazz) {
		Objects.requireNonNull(clazz, "Class must not be null");
		RecordComponent[] recordComponents = clazz.getRecordComponents();
		if (recordComponents != null) {
			return Arrays.stream(recordComponents).map(CodecComponent::new).toArray(CodecComponent[]::new);
		}
		
		List<Field> fields = Lists.newArrayList(clazz.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).toList();
		if (fields.isEmpty()) {
			return EMPTY_CODEC_COMPONENTS;
		}
		
		if (fields.stream().anyMatch(field -> field.isAnnotationPresent(CodecField.class))) {
			return fields.stream().filter(field -> field.isAnnotationPresent(CodecField.class)).map(CodecComponent::new).toArray(CodecComponent[]::new);
		}
		
		return fields.stream().filter(field -> Modifier.isFinal(field.getModifiers()) && !Modifier.isTransient(field.getModifiers()))
			.map(CodecComponent::new).toArray(CodecComponent[]::new);
	}
	
	/**
	 * Gets the constructor to use for creating instances of the class.<br>
	 * For record classes, it returns the canonical constructor that matches the record components.<br>
	 * For regular classes, it returns either the constructor annotated with {@link CodecConstructor} or the only constructor of the class.<br>
	 *
	 * @param clazz The class to get the constructor from
	 * @param components The components of the class
	 * @param <O> The type of the class
	 * @return The constructor to use
	 * @throws NullPointerException If the class or components are null
	 * @throws IllegalArgumentException If no suitable constructor is found or multiple constructors are annotated
	 * @see CodecConstructor
	 */
	private static <O> @NotNull Constructor<O> getConstructor(@NotNull Class<O> clazz, CodecComponent @NotNull [] components) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(components, "Components must not be null");
		
		if (clazz.isRecord()) {
			Class<?>[] parameterTypes = new Class<?>[components.length];
			for (int i = 0; i < components.length; i++) {
				parameterTypes[i] = components[i].getType();
			}
			
			return ReflectionHelper.getConstructor(clazz, parameterTypes).orElseThrow();
		}
		
		List<Constructor<?>> constructors = Lists.newArrayList(clazz.getDeclaredConstructors());
		if (constructors.size() == 1) {
			return validateClassConstructor(clazz, components, constructors);
		}
		
		constructors.removeIf(constructor -> !constructor.isAnnotationPresent(CodecConstructor.class));
		if (constructors.isEmpty()) {
			throw new IllegalArgumentException("No codec constructor found for class " + clazz.getName() + ", class must either have a single constructor or one constructor must be annotated with @CodecConstructor");
		} else if (constructors.size() > 1) {
			throw new IllegalArgumentException("Multiple codec constructors found for class " + clazz.getName() + ", only one constructor must be annotated with @CodecConstructor");
		}
		
		return validateClassConstructor(clazz, components, constructors);
	}
	
	/**
	 * Validates that the constructor matches the components of the class.<br>
	 * This ensures that the constructor has the correct number of parameters and that each parameter is assignable from the corresponding component type.<br>
	 *
	 * @param clazz The class being validated
	 * @param components The components of the class
	 * @param constructors The (singleton) list containing the constructor to validate
	 * @param <O> The type of the class
	 * @return The validated constructor
	 * @throws NullPointerException If the class, components, or constructors are null
	 * @throws IllegalArgumentException If the constructor doesn't match the components
	 */
	private static <O> @NotNull Constructor<O> validateClassConstructor(@NotNull Class<O> clazz, CodecComponent @NotNull [] components, @NotNull List<Constructor<?>> constructors) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(components, "Components must not be null");
		Objects.requireNonNull(constructors, "Constructors must not be null");
		
		Constructor<O> constructor = (Constructor<O>) constructors.getFirst();
		if (constructor.getParameterCount() != components.length) {
			throw new IllegalArgumentException("Codec constructor for class " + clazz.getName() + " must have " + components.length + " parameters, but has " + constructor.getParameterCount());
		}
		
		for (int i = 0; i < components.length; i++) {
			Class<?> parameterType = constructor.getParameterTypes()[i];
			if (!components[i].getType().isAssignableFrom(parameterType)) {
				throw new IllegalArgumentException("Codec constructor for class " + clazz.getName() + " has parameter " + i + " of type " + parameterType.getName() + ", but component " + components[i].getName() + " has type " + components[i].getType().getName());
			}
		}
		return constructor;
	}
	
	/**
	 * Creates a configured codec for the given component.<br>
	 * This method determines the codec to use for the component's type and configures it with the component's name and a getter function.<br>
	 *
	 * @param component The component to create a configured codec for
	 * @param <C> The type of the component
	 * @param <O> The type of the object containing the component
	 * @return A configured codec for the component
	 * @throws NullPointerException If the component is null
	 * @see GenericInfo
	 */
	private static <C, O> @NotNull ConfiguredCodec<C, O> createConfiguredCodec(@NotNull CodecComponent component) {
		Objects.requireNonNull(component, "Component must not be null");
		
		GenericInfo genericInfo = component.getAnnotation(GenericInfo.class);
		Codec<C> codec = getCodec(component.getType(), genericInfo == null ? null : genericInfo.value());
		String name = component.getName();
		
		Function<O, C> getter = o -> (C) component.accessValue(o).orElseThrow();
		return codec.configure(name, getter);
	}
	
	/**
	 * Gets a codec for the given class type, with optional generic type information.<br>
	 * This method handles various types including arrays, enums, collections, and primitive types.<br>
	 *
	 * @param clazz The class to get a codec for
	 * @param genericInfo Optional (nullable) generic type information for the class
	 * @param <C> The type of the class
	 * @return A codec for the given class
	 * @throws NullPointerException If the class is null
	 * @throws IllegalArgumentException If generic type information is missing or invalid
	 * @see GenericInfo
	 */
	private static <C> @NotNull Codec<C> getCodec(@NotNull Class<?> clazz, Class<?> @Nullable [] genericInfo) {
		Objects.requireNonNull(clazz, "Class must not be null");
		
		if (clazz.isArray()) {
			Class<?> componentType = clazz.getComponentType();
			return (Codec<C>) getCodec(componentType, genericInfo).list().xmap(Arrays::asList, list -> {
				if (list.isEmpty()) {
					return ArrayUtils.EMPTY_OBJECT_ARRAY;
				}
				Object[] array = (Object[]) Array.newInstance(componentType, list.size());
				for (int i = 0; i < list.size(); i++) {
					array[i] = list.get(i);
				}
				return array;
			});
		} else if (clazz.isEnum()) {
			return Codec.dynamicEnum((Class<? extends Enum>) clazz);
		}
		
		if (clazz.isAssignableFrom(Optional.class)) {
			if (genericInfo == null) {
				throw new IllegalArgumentException("Missing generic type information for Optional: " + clazz.getName());
			}
			
			return (Codec<C>) Codec.optional(getCodec(genericInfo[0], dropElements(genericInfo, 1)));
		} else if (clazz.isAssignableFrom(Either.class)) {
			if (genericInfo == null || genericInfo.length < 2) {
				throw new IllegalArgumentException("Missing generic type information for Either: " + clazz.getName());
			}
			
			Class<?>[] newGenericInfo = dropElements(genericInfo, 2);
			return (Codec<C>) Codec.either(
				getCodec(genericInfo[0], newGenericInfo),
				getCodec(genericInfo[1], newGenericInfo)
			);
		} else if (clazz.isAssignableFrom(Map.class)) {
			if (genericInfo == null || genericInfo.length < 2) {
				throw new IllegalArgumentException("Missing generic type information for Map: " + clazz.getName());
			}
			
			Class<?>[] newGenericInfo = dropElements(genericInfo, 2);
			Codec<?> keyType = getCodec(genericInfo[0], newGenericInfo);
			if (!(keyType instanceof KeyableCodec<?> keyableCodec)) {
				throw new IllegalArgumentException("Key type must be keyable: " + clazz.getName());
			}
			
			return (Codec<C>) Codec.map(
				keyableCodec, getCodec(genericInfo[1], newGenericInfo)
			);
		} else if (clazz.isAssignableFrom(List.class)) {
			if (genericInfo == null) {
				throw new IllegalArgumentException("Missing generic type information for List: " + clazz.getName());
			}
			
			return (Codec<C>) Codec.list(getCodec(genericInfo[0], dropElements(genericInfo, 1)));
		} else if (clazz.isAssignableFrom(Set.class)) {
			if (genericInfo == null) {
				throw new IllegalArgumentException("Missing generic type information for Set: " + clazz.getName());
			}
			
			return (Codec<C>) Codec.list(getCodec(genericInfo[0], dropElements(genericInfo, 1))).xmap(ArrayList::new, HashSet::new);
		}
		
		Codec<?> codec = CODEC_LOOKUP.get(ClassUtils.primitiveToWrapper(clazz));
		if (codec == null) {
			codec = createCodec(clazz);
		}
		return (Codec<C>) codec;
	}
	
	/**
	 * Drops a specified number of elements from the beginning of an array.<br>
	 * This is used to handle nested generic types by removing the processed type parameters.<br>
	 *
	 * @param genericInfo The array of generic type information
	 * @param count The number of elements to drop
	 * @return A new array with the specified elements dropped, or null if the resulting array is empty
	 */
	private static Class<?> @Nullable [] dropElements(Class<?> @Nullable [] genericInfo, int count) {
		if (genericInfo == null || count >= genericInfo.length) {
			return null;
		}
		
		Class<?>[] newGenericInfo = new Class<?>[genericInfo.length - count];
		System.arraycopy(genericInfo, count, newGenericInfo, 0, newGenericInfo.length);
		return newGenericInfo;
	}
	
	/**
	 * Creates a new instance of a class using the specified constructor and arguments.<br>
	 * This method uses reflection to instantiate the class.<br>
	 *
	 * @param constructor The constructor to use
	 * @param args The arguments to pass to the constructor
	 * @param <O> The type of the class
	 * @return A new instance of the class
	 * @throws NullPointerException If the constructor or args are null
	 */
	private static <O> @NotNull O createInstance(@NotNull Constructor<O> constructor, Object @NotNull ... args) {
		return ReflectionHelper.newInstance(constructor, args).orElseThrow();
	}
}
