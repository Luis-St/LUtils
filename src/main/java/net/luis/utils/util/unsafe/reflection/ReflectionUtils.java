package net.luis.utils.util.unsafe.reflection;

import com.google.common.collect.Lists;
import net.luis.utils.util.Pair;
import net.luis.utils.util.Utils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ArrayUtils.*;

/**
 * Utility class for reflection-related operations.<br>
 *
 *
 * @author Luis-St
 */
public class ReflectionUtils {
	
	/**
	 * Gets the raw name of the given method.<br>
	 * The raw name is the name of the method without prefixes like "get", "set", "is" or "has".<br>
	 * @param method The method to get the raw name of
	 * @param prefixes Additional prefixes to remove
	 * @return The raw name of the given method or {@link Method#getName()} as fallback
	 * @throws NullPointerException If the given method is null
	 */
	public static @NotNull String getRawName(@NotNull Method method, String @Nullable ... prefixes) {
		String name = Objects.requireNonNull(method, "Method must not be null").getName();
		if (name.startsWith("get")) {
			return name.substring(3);
		} else if (name.startsWith("set")) {
			return name.substring(3);
		} else if (name.startsWith("is")) {
			return name.substring(2);
		} else if (name.startsWith("has")) {
			return name.substring(3);
		}
		for (String prefix : nullToEmpty(prefixes)) {
			if (name.startsWith(prefix)) {
				return name.substring(prefix.length());
			}
		}
		return name;
	}
	
	/**
	 * Gets all methods of the given class for the given name (case-sensitive).<br>
	 * If the given name is null, all methods of the given class will be returned.<br>
	 * The methods will be returned in the order of their parameter count.<br>
	 * @param clazz The class to get the methods of
	 * @param name The name of the methods to get
	 * @return A list of all methods from the given class for the given name
	 * @throws NullPointerException If the given class is null
	 */
	public static @NotNull List<Method> getMethodsForName(@NotNull Class<?> clazz, @Nullable String name) {
		Objects.requireNonNull(clazz, "Class must not be null");
		return Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.getName().equals(name) || name == null)
			.sorted(Comparator.comparingInt(Method::getParameterCount)).collect(Collectors.toList());
	}
	
	/**
	 * Finds the parameters in the correct order for the given executable.<br>
	 * The parameters will be searched by their type, and if the type is ambiguous, by their name.<br>
	 * <p>
	 *     In the case the compiler has not included the parameter names,<br>
	 *     and the types are ambiguous, an exception will be thrown.<br>
	 *     If the given executable has no parameters, an empty array will be returned.<br>
	 * </p>
	 * @param executable The executable to find the parameters for
	 * @param values The possible values for the parameters
	 * @return An array of the found parameters in the correct order
	 * @throws NullPointerException If the given executable is null
	 * @throws IllegalArgumentException If the type of the parameter is ambiguous and the name is not present
	 * @throws IllegalArgumentException If no value for a parameter could be found
	 * @see #findParameter(Parameter, List)
	 */
	public static Object @NotNull [] findParameters(@NotNull Executable executable, Object @Nullable ... values) {
		Objects.requireNonNull(executable, "Executable must not be null");
		return findParameters(executable, Utils.mapList(Lists.newArrayList(nullToEmpty(values)), value -> {
			String name = value.getClass().getSimpleName();
			return Pair.of(value, StringUtils.uncapitalize(name));
		}));
	}
	
	/**
	 * Finds the parameters in the correct order for the given executable.<br>
	 * The parameters will be searched by their type, and if the type is ambiguous, by their name.<br>
	 * <p>
	 *     In the case the compiler has not included the parameter names,<br>
	 *     and the types are ambiguous, an exception will be thrown.<br>
	 *     If the given executable has no parameters, an empty array will be returned.<br>
	 * </p>
	 * @param executable The executable to find the parameters for
	 * @param values The possible values for the parameters
	 * @return An array of the found parameters in the correct order
	 * @throws NullPointerException If the given executable is null
	 * @throws IllegalArgumentException If the type of the parameter is ambiguous and the name is not present
	 * @throws IllegalArgumentException If no value for a parameter could be found
	 * @see #findParameter(Parameter, List)
	 */
	public static Object @NotNull [] findParameters(@NotNull Executable executable, @Nullable List<Pair<Object, String>> values) {
		Parameter[] parameters = Objects.requireNonNull(executable, "Executable must not be null").getParameters();
		Object[] arguments = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			arguments[i] = findParameter(parameters[i], values == null ? Lists.newArrayList() : values);
		}
		return arguments;
	}
	
	//region Internal
	
	/**
	 * Finds the value for the given parameter.<br>
	 * The value will be searched by its type, and if the type is ambiguous, by its name.<br>
	 * An exception will be thrown if no value for the parameter could be found.<br>
	 * @param parameter The parameter to find the value for
	 * @param values The possible values for the parameter
	 * @return The value found for the given parameter
	 * @throws NullPointerException If the given parameter or values are null
	 * @throws IllegalArgumentException If the type of the parameter is ambiguous and the name is not present
	 * @throws IllegalStateException If no value for the parameter could be found
	 */
	private static @NotNull Object findParameter(@NotNull Parameter parameter, @NotNull List<Pair<Object, String>> values) {
		Objects.requireNonNull(parameter, "Parameter must not be null");
		Objects.requireNonNull(values, "Values must not be null");
		Executable executable = parameter.getDeclaringExecutable();
		for (Pair<Object, String> value : values) {
			Object object = value.getFirst();
			boolean noDuplicates = !Utils.hasDuplicates(object, Utils.mapList(Lists.newArrayList(values), Pair::getFirst));
			if (parameter.getType().isInstance(object) || ClassUtils.primitiveToWrapper(parameter.getType()).isInstance(object)) {
				if (noDuplicates) {
					return object;
				} else if (!parameter.isNamePresent()) {
					throw new IllegalArgumentException("The parameter '" + parameter.getName() + "' of method '" + executable.getDeclaringClass().getSimpleName() + "#" + executable.getName() + "' is ambiguous");
				} else if (parameter.getName().equalsIgnoreCase(value.getSecond())) {
					return object;
				}
			}
		}
		throw new IllegalStateException("No value for parameter '" + parameter.getName() + "' of method '" + executable.getDeclaringClass().getSimpleName() + "#" + executable.getName() + "' found");
	}
	//endregion
}
