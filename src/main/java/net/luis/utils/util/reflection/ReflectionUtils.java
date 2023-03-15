package net.luis.utils.util.reflection;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Luis-st
 *
 */

public class ReflectionUtils {
	
	public static void invokeMethod(Method method, Object instance, Object... args) {
		if (hasDuplicates(args)) {
			throw new IllegalArgumentException("The arguments must not contain duplicates");
		}
		List<ParameterInfo> parameterInfos = Lists.newArrayList();
		for (Object arg : args) {
			parameterInfos.add(new ParameterInfo(arg, false, "", arg == null));
		}
		invokeMethod(method, instance, parameterInfos);
	}
	
	private static boolean hasDuplicates(Object... args) {
	    List<Class<?>> classes = Lists.newArrayList();
		for (Object arg : args) {
			if (classes.contains(arg.getClass())) {
				return true;
			}
			classes.add(arg.getClass());
		}
		return false;
	}
	
	public static void invokeMethod(Method method, Object instance, List<ParameterInfo> parameterInfos) {
		List<Object> parameters = Lists.newArrayList();
		for (Parameter parameter : method.getParameters()) {
			parameters.add(getParameter(parameter, parameterInfos));
		}
		if (parameters.size() != method.getParameters().length) {
			throw new IllegalArgumentException("The given parameters do not match the parameters of the method");
		}
		ReflectionHelper.invoke(method, instance, parameters.toArray());
	}
	
	private static ParameterInfo getParameter(Parameter parameter, List<ParameterInfo> parameterInfos) {
		for (ParameterInfo info : parameterInfos) {
			boolean duplicates = hasDuplicates(parameterInfos);
			if (info.type() == parameter.getType()) {
				if (!duplicates) {
					return checkNullability(info, parameter);
				} else if (info.hasName() && info.parameterName().equals(parameter.getName())) {
					return checkNullability(info, parameter);
				}
			}
		}
		for (ParameterInfo info : parameterInfos) {
			if (info.type() == parameter.getType()) {
				return info;
			}
		}
		throw new IllegalArgumentException("Could not find a parameter with the type " + parameter.getType().getName() + " in the given parameters");
	}
	
	private static ParameterInfo checkNullability(ParameterInfo info, Parameter parameter) {
		if (info.nullable() && !parameter.isAnnotationPresent(Nullable.class)) {
			throw new IllegalArgumentException("The parameter " + parameter.getName() + " of the method " + parameter.getDeclaringExecutable().getName() + " is not marked as nullable");
		}
		return info;
	}
	
	private static boolean hasDuplicates(List<ParameterInfo> parameterInfos) {
		List<Class<?>> classes = Lists.newArrayList();
		for (ParameterInfo info : parameterInfos) {
			if (classes.contains(info.type())) {
				return true;
			}
			classes.add(info.type());
		}
		return false;
	}
	
}
