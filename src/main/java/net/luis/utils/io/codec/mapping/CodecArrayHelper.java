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

import net.luis.utils.io.codec.Codec;
import org.apache.commons.lang3.ClassUtils;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

import static net.luis.utils.io.codec.mapping.CodecAutoMapping.*;

/**
 * A utility class for creating and managing codecs for array types.<br>
 * This class provides methods to automatically generate codecs for both primitive and object arrays of any dimension.<br>
 * It handles the conversion between arrays and lists for codec operations, supporting both regular and jagged (irregular) arrays.<br>
 * <br>
 * The class supports:
 * <ul>
 *     <li>Single-dimensional arrays of primitives and objects</li>
 *     <li>Multidimensional arrays of any depth</li>
 *     <li>Jagged arrays (arrays with sub-arrays of different lengths)</li>
 *     <li>Automatic conversion between array and list representations</li>
 * </ul>
 * <br>
 * This is a package-private helper class used internally by {@link CodecAutoMapping} to handle array codec creation.<br>
 *
 * @see Codec
 * @see CodecAutoMapping
 *
 * @author Luis-St
 */
@SuppressWarnings("unchecked")
final class CodecArrayHelper {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private CodecArrayHelper() {}
	
	/**
	 * Creates or retrieves a codec for the specified array class.<br>
	 * This is the main entry point for array codec creation. It analyzes the array type and delegates to the appropriate
	 * specialized method based on whether the array contains primitives or objects.<br>
	 * <br>
	 * The method automatically detects:
	 * <ul>
	 *     <li>The component type of the array</li>
	 *     <li>The number of array dimensions</li>
	 *     <li>Whether the component type is primitive or object-based</li>
	 * </ul>
	 *
	 * @param clazz The array class for which to create a codec
	 * @param baseCodecGetter A function that provides codecs for component types
	 * @param <C> The type of the array
	 * @return A codec capable of encoding and decoding the specified array type
	 * @throws NullPointerException If the class or base codec getter is null
	 * @throws IllegalArgumentException If the provided class is not an array type
	 * @see #createObjectArrayCodec(Codec, Class, int)
	 * @see #getOrCreatePrimitiveArrayCodec(Class, int)
	 */
	static <C> @NonNull Codec<C> getOrCreateArrayCodec(@NonNull Class<?> clazz, @NonNull Function<Class<?>, Codec<Object>> baseCodecGetter) {
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
	
	/**
	 * Creates a codec for object arrays with the specified component type and dimensions.<br>
	 * This method handles the creation of codecs for arrays containing object references (non-primitive types).<br>
	 * For single-dimensional arrays, it creates a direct list-to-array mapping codec.<br>
	 * For multidimensional arrays, it delegates to the multidimensional array codec creation method.<br>
	 *
	 * @param componentCodec The codec to use for individual array elements
	 * @param componentType The component type of the array elements
	 * @param arrayDimension The number of dimensions in the array (1 for simple arrays, 2+ for multidimensional)
	 * @param <C> The type of the array
	 * @return A codec for the specified object array type
	 * @throws NullPointerException If the component codec or component type is null
	 * @throws IllegalArgumentException If the component type is a primitive type
	 * @see #createMultidimensionalArrayCodec(Codec, Class, int)
	 */
	private static <C> @NonNull Codec<C> createObjectArrayCodec(@NonNull Codec<Object> componentCodec, @NonNull Class<?> componentType, int arrayDimension) {
		Objects.requireNonNull(componentCodec, "Component codec must not be null");
		Objects.requireNonNull(componentType, "Component type must not be null");
		
		if (componentType.isPrimitive()) {
			throw new IllegalArgumentException("Component type must not be a primitive type: " + componentType.getName());
		}
		
		if (arrayDimension == 1) {
			Codec<C> codec = (Codec<C>) componentCodec.list().xmap(Arrays::asList, (List<Object> list) -> {
				if (list.isEmpty()) {
					// Return an empty array of the correct type, cast to Object[] to avoid type issues (needed for some reason)
					return (Object[]) Array.newInstance(componentType, 0);
				}
				
				Object[] array = (Object[]) Array.newInstance(componentType, list.size());
				for (int i = 0; i < list.size(); i++) {
					array[i] = list.get(i);
				}
				return array;
			});
			
			return Codec.of((Class<C>) componentType, codec, codec, ClassUtils.primitiveToWrapper(componentType).getSimpleName() + "ArrayCodec");
		}
		
		Codec<?> codec = componentCodec;
		for (int i = 0; i < arrayDimension; i++) {
			codec = codec.list();
		}
		return createMultidimensionalArrayCodec(codec, componentType, arrayDimension);
	}
	
	/**
	 * Creates or retrieves a codec for primitive arrays with the specified component type and dimensions.<br>
	 * This method handles arrays of primitive types (boolean, byte, short, int, long, float, double, char).<br>
	 * For single-dimensional primitive arrays, it retrieves pre-existing codecs from the codec lookup table.<br>
	 * For multidimensional primitive arrays, it creates a nested list structure and converts it appropriately.<br>
	 *
	 * @param componentType The primitive component type of the array
	 * @param arrayDimension The number of dimensions in the array
	 * @param <C> The type of the array
	 * @return A codec for the specified primitive array type
	 * @throws NullPointerException If the component type is null
	 * @see #createMultidimensionalArrayCodec(Codec, Class, int)
	 * @see CodecAutoMapping#CODEC_LOOKUP
	 */
	private static <C> @NonNull Codec<C> getOrCreatePrimitiveArrayCodec(@NonNull Class<?> componentType, int arrayDimension) {
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
	
	/**
	 * Creates a codec for multidimensional arrays by mapping between nested list structures and array representations.<br>
	 * This method handles the complex conversion between the list-based representation used by codecs internally
	 * and the array-based representation expected by Java code.<br>
	 * <br>
	 * The codec performs bidirectional conversion:
	 * <ul>
	 *     <li>From arrays to nested lists for encoding</li>
	 *     <li>From nested lists to arrays for decoding</li>
	 * </ul>
	 *
	 * @param rawListCodec The codec that handles the nested list representation
	 * @param componentType The base component type of the array elements
	 * @param arrayDimension The number of dimensions in the array
	 * @param <C> The type of the array
	 * @return A codec that can handle multidimensional arrays
	 * @throws NullPointerException If the raw list codec or component type is null
	 * @see #arrayToTypedList(Object)
	 * @see #listToTypedArray(List, Class, int)
	 */
	private static <C> @NonNull Codec<C> createMultidimensionalArrayCodec(@NonNull Codec<?> rawListCodec, @NonNull Class<?> componentType, int arrayDimension) {
		Objects.requireNonNull(rawListCodec, "Raw list codec must not be null");
		Objects.requireNonNull(componentType, "Component type must not be null");
		
		Codec<C> codec = rawListCodec.xmap(CodecArrayHelper::arrayToTypedList, list -> listToTypedArray((List<?>) list, componentType, arrayDimension));
		return Codec.of((Class<C>) componentType, codec, codec, ClassUtils.primitiveToWrapper(componentType).getSimpleName() + arrayDimension + "DArrayCodec");
	}
	
	/**
	 * Converts an array to a typed list representation with proper generic type casting.<br>
	 * This is a type-safe wrapper around {@link #arrayToList(Object)} that ensures proper generic type handling.<br>
	 *
	 * @param array The array to convert
	 * @param <T> The expected return type
	 * @return The array converted to a nested list structure
	 * @throws NullPointerException If the array is null
	 * @see #arrayToList(Object)
	 */
	public static <T> @NonNull T arrayToTypedList(@NonNull Object array) {
		Objects.requireNonNull(array, "Array must not be null");
		return (T) arrayToList(array);
	}
	
	/**
	 * Recursively converts an array of any dimension to a nested list structure.<br>
	 * This method handles arrays of any depth by recursively processing sub-arrays and converting them to lists.<br>
	 * Each level of array nesting becomes a level of list nesting in the result.<br>
	 * <br>
	 * For example:
	 * <ul>
	 *     <li>A 1D array [1, 2, 3] becomes a list [1, 2, 3]</li>
	 *     <li>A 2D array [[1, 2], [3, 4]] becomes a nested list [[1, 2], [3, 4]]</li>
	 * </ul>
	 *
	 * @param array The array to convert (can be multidimensional)
	 * @return A nested list structure representing the array
	 * @throws NullPointerException If the array is null
	 */
	private static @NonNull Object arrayToList(@NonNull Object array) {
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
	
	/**
	 * Converts a nested list structure back to a typed array with the specified component type and dimensions.<br>
	 * This method is the inverse of {@link #arrayToList(Object)} and handles the conversion from the list-based
	 * representation used internally by codecs back to the array representation expected by application code.<br>
	 * <br>
	 * The method supports jagged arrays (arrays where sub-arrays have different lengths) by creating and filling
	 * appropriately sized sub-arrays for each level of nesting.<br>
	 *
	 * @param list The nested list structure to convert
	 * @param componentType The base component type for the array elements
	 * @param arrayDimension The number of dimensions in the target array
	 * @param <T> The expected return type
	 * @return An array representation of the list structure
	 * @throws NullPointerException If the list or component type is null
	 * @see #createJaggedArray(List, Class)
	 * @see #fillJaggedArray(Object, List)
	 */
	public static <T> @NonNull T listToTypedArray(@NonNull List<?> list, @NonNull Class<?> componentType, int arrayDimension) {
		Objects.requireNonNull(list, "List must not be null");
		Objects.requireNonNull(componentType, "Component type must not be null");
		
		if (list.isEmpty()) {
			int[] dimensions = new int[arrayDimension];
			Arrays.fill(dimensions, 0);
			return (T) Array.newInstance(componentType, dimensions);
		}
		
		Object array = createJaggedArray(list, componentType);
		fillJaggedArray(array, list);
		return (T) array;
	}
	
	/**
	 * Creates a jagged array structure that matches the shape of the provided list.<br>
	 * This method recursively analyzes the nested list structure to determine the appropriate array dimensions
	 * and creates an array hierarchy that can accommodate all the data, even if sub-lists have different lengths.<br>
	 * <br>
	 * A jagged array is an array of arrays where the sub-arrays can have different lengths, unlike rectangular
	 * multidimensional arrays where all dimensions are fixed.<br>
	 *
	 * @param list The nested list structure to analyze
	 * @param componentType The base component type for the array elements
	 * @return An appropriately shaped but unfilled array structure
	 * @throws NullPointerException If the list or component type is null
	 * @see #getDepth(Object)
	 * @see #getArrayType(Class, int)
	 */
	private static @NonNull Object createJaggedArray(@NonNull List<?> list, @NonNull Class<?> componentType) {
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
	
	/**
	 * Calculates the depth of nesting in a list or list element.<br>
	 * This method recursively determines how many levels of list nesting exist, which is used to determine
	 * the appropriate array type for jagged array creation.<br>
	 * <br>
	 * For example:
	 * <ul>
	 *     <li>A simple object has depth 0</li>
	 *     <li>A list containing objects has depth 1</li>
	 *     <li>A list containing lists of objects has depth 2</li>
	 * </ul>
	 *
	 * @param element The element to analyze for nesting depth
	 * @return The depth of list nesting (0 for non-lists, 1+ for nested lists)
	 * @throws NullPointerException If the element is null
	 */
	private static int getDepth(@NonNull Object element) {
		Objects.requireNonNull(element, "Element must not be null");
		
		if (!(element instanceof List<?> list)) {
			return 0;
		}
		
		if (list.isEmpty()) {
			return 1;
		}
		
		return 1 + getDepth(list.getFirst());
	}
	
	/**
	 * Recursively fills a jagged array structure with data from a corresponding nested list.<br>
	 * This method traverses both the array structure and the list structure simultaneously, copying data
	 * from the list into the appropriate array positions. It handles arbitrary levels of nesting and
	 * supports jagged structures where sub-arrays have different lengths.<br>
	 *
	 * @param array The array structure to fill (created by {@link #createJaggedArray(List, Class)})
	 * @param list The nested list containing the data to copy
	 * @throws NullPointerException If the array or list is null
	 * @see #createJaggedArray(List, Class)
	 */
	private static void fillJaggedArray(@NonNull Object array, @NonNull List<?> list) {
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
	
	/**
	 * Gets the array type for a given component type and nesting depth.<br>
	 * This method dynamically constructs the appropriate array class for a given base component type
	 * and the specified number of array dimensions. It uses reflection to create array instances
	 * and extract their class types.<br>
	 * <br>
	 * For example:
	 * <ul>
	 *     <li>getArrayType(int.class, 1) returns int[].class</li>
	 *     <li>getArrayType(String.class, 2) returns String[][].class</li>
	 * </ul>
	 *
	 * @param componentType The base component type of the array
	 * @param depth The number of array dimensions to create
	 * @return The class representing the array type with the specified dimensions
	 * @throws NullPointerException If the component type is null
	 */
	private static @NonNull Class<?> getArrayType(@NonNull Class<?> componentType, int depth) {
		Objects.requireNonNull(componentType, "Component type must not be null");
		
		Class<?> result = componentType;
		for (int i = 0; i < depth; i++) {
			result = Array.newInstance(result, 0).getClass();
		}
		return result;
	}
}
