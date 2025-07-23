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

import net.luis.utils.io.codec.Codec;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

import static net.luis.utils.io.codec.mapping.CodecAutoMapping.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("unchecked")
class CodecArrayHelper {
	
	static <C> @NotNull Codec<C> getOrCreateArrayCodec(@NotNull Class<?> clazz, @NotNull Function<Class<?>, Codec<Object>> baseCodecGetter) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(baseCodecGetter, "Base codec getter must not be null");
		
		if (!clazz.isArray()) {
			throw new IllegalArgumentException("Provided class is not an array type: " + clazz.getName());
		}
		
		int arrayDimension = 1;
		Class<?> componentType = clazz.getComponentType();
		while (componentType.isArray()) {
			arrayDimension++;
			componentType = componentType.getComponentType();
		}
		
		if (componentType.isPrimitive()) {
			return getOrCreatePrimitiveArrayCodec(componentType, arrayDimension);
		}
		return createObjectArrayCodec(baseCodecGetter.apply(componentType), componentType, arrayDimension);
	}
	
	private static <C> @NotNull Codec<C> createObjectArrayCodec(@NotNull Codec<Object> componentCodec, @NotNull Class<?> componentType, int arrayDimension) {
		Objects.requireNonNull(componentCodec, "Component codec must not be null");
		Objects.requireNonNull(componentType, "Component type must not be null");
		
		if (componentType.isPrimitive()) {
			throw new IllegalArgumentException("Component type must not be a primitive type: " + componentType.getName());
		}
		
		if (arrayDimension == 1) {
			Codec<C> codec = (Codec<C>) componentCodec.list().xmap(Arrays::asList, list -> {
				if (list.isEmpty()) {
					return ArrayUtils.EMPTY_OBJECT_ARRAY;
				}
				
				Object[] array = (Object[]) Array.newInstance(componentType, list.size());
				for (int i = 0; i < list.size(); i++) {
					array[i] = list.get(i);
				}
				return array;
			});
			
			return Codec.of(codec, codec, ClassUtils.primitiveToWrapper(componentType).getSimpleName() + "ArrayCodec");
		}
		
		Codec<?> codec = componentCodec;
		for (int i = 0; i < arrayDimension; i++) {
			codec = codec.list();
		}
		
		return createMultidimensionalArrayCodec(codec, componentType, arrayDimension);
	}
	
	private static <C> @NotNull Codec<C> getOrCreatePrimitiveArrayCodec(@NotNull Class<?> componentType, int arrayDimension) {
		Objects.requireNonNull(componentType, "Component type must not be null");
		
		if (arrayDimension == 1) {
			return (Codec<C>) CODEC_LOOKUP.get(componentType.arrayType());
		}
		
		Codec<?> codec = CODEC_LOOKUP.get(ClassUtils.primitiveToWrapper(componentType));
		for (int i = 0; i < arrayDimension; i++) {
			codec = codec.list();
		}
		
		return createMultidimensionalArrayCodec(codec, componentType, arrayDimension);
	}
	
	private static <C> @NotNull Codec<C> createMultidimensionalArrayCodec(@NotNull Codec<?> rawListCodec, @NotNull Class<?> componentType, int arrayDimension) {
		Objects.requireNonNull(rawListCodec, "Raw list codec must not be null");
		Objects.requireNonNull(componentType, "Component type must not be null");
		
		Codec<C> codec = (Codec<C>) rawListCodec.xmap(CodecArrayHelper::arrayToTypedList, list -> listToTypedArray((List<?>) list, componentType, arrayDimension));
		return Codec.of(codec, codec, ClassUtils.primitiveToWrapper(componentType).getSimpleName() + arrayDimension + "DArrayCodec");
	}
	
	private static <T> @NotNull T arrayToTypedList(@NotNull Object array) {
		Objects.requireNonNull(array, "Array must not be null");
		return (T) arrayToList(array);
	}
	
	private static @NotNull Object arrayToList(@NotNull Object array) {
		Objects.requireNonNull(array, "Array must not be null");
		
		int length = Array.getLength(array);
		List<Object> list = new ArrayList<>(length);
		Class<?> componentType = array.getClass().getComponentType();
		
		for (int i = 0; i < length; i++) {
			Object element = Array.get(array, i);
			
			if (componentType.isArray()) {
				list.add(arrayToList(element));
			} else {
				list.add(element);
			}
		}
		
		return list;
	}
	
	public static @NotNull Object listToTypedArray(@NotNull List<?> list, @NotNull Class<?> componentType, int arrayDimension) {
		Objects.requireNonNull(list, "List must not be null");
		Objects.requireNonNull(componentType, "Component type must not be null");
		
		if (list.isEmpty()) {
			int[] dimensions = new int[arrayDimension];
			Arrays.fill(dimensions, 0);
			return Array.newInstance(componentType, dimensions);
		}
		
		Object array = createJaggedArray(list, componentType);
		fillJaggedArray(array, list);
		return array;
	}
	
	private static @NotNull Object createJaggedArray(@NotNull List<?> list, @NotNull Class<?> componentType) {
		Objects.requireNonNull(list, "List must not be null");
		Objects.requireNonNull(componentType, "Component type must not be null");
		
		if (list.isEmpty()) {
			return Array.newInstance(componentType, 0);
		}
		
		Object firstElement = list.getFirst();
		if (!(firstElement instanceof List)) {
			return Array.newInstance(componentType, list.size());
		}
		
		Class<?> subArrayType = getArrayType(componentType, getDepth(firstElement));
		Object[] outerArray = (Object[]) Array.newInstance(subArrayType, list.size());
		
		for (int i = 0; i < list.size(); i++) {
			Object element = list.get(i);
			if (element instanceof List) {
				outerArray[i] = createJaggedArray((List<?>) element, componentType);
			}
		}
		
		return outerArray;
	}
	
	private static int getDepth(@NotNull Object element) {
		Objects.requireNonNull(element, "Element must not be null");
		
		if (!(element instanceof List<?> list)) {
			return 0;
		}
		
		if (list.isEmpty()) {
			return 1;
		}
		
		return 1 + getDepth(list.getFirst());
	}
	
	private static void fillJaggedArray(@NotNull Object array, @NotNull List<?> list) {
		Objects.requireNonNull(array, "Array must not be null");
		Objects.requireNonNull(list, "List must not be null");
		
		for (int i = 0; i < list.size(); i++) {
			Object element = list.get(i);
			
			if (element instanceof List) {
				Object subArray = Array.get(array, i);
				fillJaggedArray(subArray, (List<?>) element);
			} else {
				Array.set(array, i, element);
			}
		}
	}
	
	private static @NotNull Class<?> getArrayType(@NotNull Class<?> componentType, int depth) {
		Objects.requireNonNull(componentType, "Component type must not be null");
		
		Class<?> result = componentType;
		for (int i = 0; i < depth; i++) {
			result = Array.newInstance(result, 0).getClass();
		}
		return result;
	}
}
