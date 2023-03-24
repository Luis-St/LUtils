package net.luis.utils.util;

import com.google.common.collect.Lists;
import net.luis.utils.util.reflection.ReflectionHelper;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public class ToString {
	
	private final Object object;
	private final FieldHandler fieldHandler;
	private final List<String> excludeFields;
	
	private ToString(@NotNull Object object, @NotNull FieldHandler fieldHandler, @NotNull String... excludeFields) {
		this.object = object;
		this.fieldHandler = fieldHandler;
		this.excludeFields = Lists.newArrayList(excludeFields);
	}
	
	public static @NotNull String toString(@NotNull Object object, @NotNull String... excludeFields) {
		return new ToString(object, FieldHandler.EXCLUDE_JAVA, excludeFields).toString();
	}
	
	public static @NotNull String toString(@NotNull Object object, @NotNull FieldHandler fieldHandler, @NotNull String... excludeFields) {
		return new ToString(object, fieldHandler, excludeFields).toString();
	}
	
	@Override
	public @NotNull String toString() {
		StringBuilder builder = new StringBuilder(this.getClassName(this.object.getClass()));
		List<Field> fields = this.getFields();
		if (fields.size() > 0) {
			builder.append("{");
			ReflectionHelper.enableExceptionThrowing();
			for (Field field : fields) {
				builder.append(field.getName()).append("=");
				Object object = ReflectionHelper.get(field, this.object);
				Class<?> clazz = ClassUtils.primitiveToWrapper(field.getType());
				if (object == null) {
					if (clazz.isArray()) {
						builder.append("[]");
					} else if (clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class) {
						builder.append(0);
					} else if (clazz == Float.class || clazz == Double.class) {
						builder.append(0.0);
					} else if (clazz == Boolean.class) {
						builder.append(false);
					} else {
						builder.append("null");
					}
				} else if (clazz.isArray()) {
					builder.append(Arrays.toString((Object[]) object));
				} else {
					builder.append(object);
				}
				builder.append(",");
			}
			ReflectionHelper.disableExceptionThrowing();
			builder.replace(builder.lastIndexOf(","), builder.length(), "").append("}");
		}
		return builder.toString();
	}
	
	private @NotNull String getClassName(@NotNull Class<?> clazz) {
		return clazz.getName().replace(clazz.getPackageName() + ".", "").replace("$", ".");
	}
	
	private <T> @NotNull List<Field> getFields() {
		if (this.fieldHandler == FieldHandler.NO) {
			return Lists.newArrayList();
		}
		List<List<Field>> fields = Lists.newArrayList();
		Class<?> clazz = this.object.getClass();
		if (this.fieldHandler == FieldHandler.EXCLUDE) {
			fields.add(Arrays.asList(clazz.getDeclaredFields()));
		} else {
			while (clazz.getSuperclass() != null) {
				if (this.fieldHandler == FieldHandler.EXCLUDE_JAVA && clazz.getPackageName().startsWith("java")) {
					break;
				}
				fields.add(Arrays.asList(clazz.getDeclaredFields()));
				clazz = clazz.getSuperclass();
			}
		}
		return Utils.reverseList(fields.stream().flatMap((list) -> Utils.reverseList(list).stream()).filter((field) -> !Modifier.isStatic(field.getModifiers())).filter((field) -> !this.excludeFields.contains(field.getName())).collect(Collectors.toList()));
	}
	
	public enum FieldHandler {
		
		NO, INCLUDE, EXCLUDE, EXCLUDE_JAVA
		
	}
	
}
