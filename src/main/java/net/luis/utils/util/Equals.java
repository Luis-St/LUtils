package net.luis.utils.util;

import com.google.common.collect.Lists;
import net.luis.utils.util.reflection.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-st
 *
 */

public class Equals {
	
	private final Object object;
	private final List<String> excludeFields;
	
	private Equals(Object object, String... excludeFields) {
		this.object = object;
		this.excludeFields = Lists.newArrayList(excludeFields);
	}
	
	public static boolean equals(Object object, Object objectToCheck, String... excludeFields) {
		return new Equals(object, excludeFields).equals(objectToCheck);
	}
	
	@Override
	public boolean equals(Object object) {
		if (this.object == object) {
			return true;
		} else if (this.object.getClass() == object.getClass()) {
			List<Field> fields = this.getFields();
			if (fields.size() > 0) {
				ReflectionHelper.enableExceptionThrowing();
				for (Field field : fields) {
					if (field.getType().isArray()) {
						if (!Objects.deepEquals(ReflectionHelper.get(field, this.object), ReflectionHelper.get(field, object))) {
							return false;
						}
					} else {
						if (!Objects.equals(ReflectionHelper.get(field, this.object), ReflectionHelper.get(field, object))) {
							return false;
						}
					}
				}
				ReflectionHelper.disableExceptionThrowing();
			}
			return true;
		}
		return false;
	}
	
	private <T> List<Field> getFields() {
		return Lists.newArrayList(this.object.getClass().getDeclaredFields()).stream().filter((field) -> !Modifier.isStatic(field.getModifiers())).filter((field) -> !this.excludeFields.contains(field.getName())).collect(Collectors.toList());
	}
	
}
