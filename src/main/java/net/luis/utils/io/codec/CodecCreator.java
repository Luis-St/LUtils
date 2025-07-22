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

package net.luis.utils.io.codec;

import net.luis.utils.exception.ReflectionException;
import net.luis.utils.io.codec.function.CodecGroupingFunction;
import net.luis.utils.util.Result;
import net.luis.utils.util.unsafe.reflection.ReflectionHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;

/**
 * A utility class to create a {@link Codec} from a list of {@link ConfiguredCodec} instances and a {@link CodecGroupingFunction}.<br>
 * This class is used to group multiple codecs together and create an object from the decoded components.<br>
 * It uses reflection to invoke the create method of the provided function with the decoded components.<br>
 * This class is primarily used by the {@link CodecBuilder} to create complex codecs from up to sixteen components.<br>
 *
 * @see CodecBuilder
 * @see CodecGroupingFunction
 *
 * @author Luis-St
 *
 * @param <O> The type of the object that the codecs will create
 * @param <F> The type of the codec grouping function that will be used to create the object
 */
public class CodecCreator<O, F extends CodecGroupingFunction> {
	
	/**
	 * The list of codecs that will be used to decode the components of the object.<br>
	 * Each codec is responsible for decoding a specific component of the final object.<br>^
	 */
	private final List<ConfiguredCodec<?, O>> codecs;
	
	/**
	 * Constructs a new codec creator using the given list of codecs.<br>
	 * This constructor is intended for internal use only and should not be used directly.<br>
	 *
	 * @param codecs The list of codecs to use for decoding the components of the object
	 * @throws NullPointerException If the codecs list is null
	 */
	@ApiStatus.Internal
	public CodecCreator(@NotNull List<ConfiguredCodec<?, O>> codecs) {
		this.codecs = Objects.requireNonNull(codecs, "Codecs list must not be null");
	}
	
	/**
	 * Creates a new {@link Codec} using the provided codec grouping function.<br>
	 * This method will invoke the create method of the function with the decoded components.<br>
	 * The function must have a method named 'create' that accepts a list of decoded components.<br>
	 * <p>
	 *     A decoded component is an object that has been processed by the corresponding codec in the codecs list.<br>
	 *     The order of the components in the list must match the order of the codecs in the codecs list.
	 * </p>
	 *
	 * @param function The codec grouping function that will be used to create the object
	 * @return A new {@link Codec}
	 * @throws NullPointerException If the function is null
	 */
	@SuppressWarnings("unchecked")
	public @NotNull Codec<O> create(@NotNull F function) {
		Objects.requireNonNull(function, "Function must not be null");
		
		return new CodecGroup<>(this.codecs, components -> {
			try {
				Optional<Object> optional = this.invokeCreateMethod(function, components);
				
				if (optional.isEmpty()) {
					return (Result<O>) Result.error("Unable to create object with function '" + function + "' and decoded components: " + components);
				}
				
				return Result.success((O) optional.orElseThrow());
			} catch (ReflectionException e) {
				return (Result<O>) Result.error("Unable to create object with function '" + function + "' and decoded components " + components + ": " + e.getMessage());
			} finally {
				System.setProperty("reflection.exceptions.throw", "false");
			}
		});
	}
	
	/**
	 * Invokes the create method of the provided function with the decoded components.<br>
	 * This method uses reflection to find the create method and invoke it with the components.<br>
	 * The create method must accept a list of objects as parameters, where each object is a decoded component.<br>
	 * The method will return an {@link Optional} containing the created object, or empty if the invocation fails.<br>
	 *
	 * @param function The codec grouping function that contains the create method to invoke
	 * @param components The list of decoded components to pass to the create method
	 * @return An {@link Optional} containing the created object, or empty if the invocation fails
	 * @throws NullPointerException If the function or components list is null
	 * @throws ReflectionException If the create method cannot be found or invoked successfully
	 * @see ReflectionHelper
	 */
	private @NotNull Optional<Object> invokeCreateMethod(@NotNull F function, @NotNull List<Object> components) {
		Objects.requireNonNull(function, "Function must not be null");
		Objects.requireNonNull(components, "Components list must not be null");
		
		System.setProperty("reflection.exceptions.throw", "true");
		Class<?>[] parameterTypes = new Class<?>[this.codecs.size()];
		Arrays.fill(parameterTypes, Object.class);
		
		Method method = ReflectionHelper.getMethod(function.getClass(), "create", parameterTypes).orElseThrow();
		Optional<Object> optional = ReflectionHelper.invoke(method, function, components.toArray());
		
		System.setProperty("reflection.exceptions.throw", "false");
		return optional;
	}
}
