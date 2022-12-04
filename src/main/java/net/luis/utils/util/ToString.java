package net.luis.utils.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;

/**
 *
 * @author Luis-st
 *
 */

public class ToString {
	
	private final Object object;
	private final boolean includeSuperClassFields;
	private final List<String> excludeFields;
	
	private ToString(Object object, boolean includeSuperClassFields, String... excludeFields) {
		this.object = object;
		this.includeSuperClassFields = includeSuperClassFields;
		this.excludeFields = Lists.newArrayList(excludeFields);
	}
	
	@NotNull
	public static String toString(Object object) {
		return new ToString(object, false).toString();
	}
	
	@NotNull
	public static String toString(Object object, boolean includeSuperClassFields) {
		return new ToString(object, includeSuperClassFields).toString();
	}
	
	@NotNull
	public static String toString(Object object, String... excludeFields) {
		return new ToString(object, false, excludeFields).toString();
	}
	
	@NotNull
	public static String toString(Object object, boolean includeSuperClassFields, String... excludeFields) {
		return new ToString(object, includeSuperClassFields, excludeFields).toString();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.object.getClass().getSimpleName());
		List<Field> fields = this.getFields();
		if (fields.size() > 0) {
			builder.append("{");
			for (int i = 0; i < fields.size(); i++) {
				String name = fields.get(i).getName();
				if (!this.excludeFields.contains(name)) {
					if (i != 0) {
						builder.append(",");
					}
					builder.append(name).append("=").append(ReflectionHelper.get(fields.get(i), this.object).toString());
				}
			}
			builder.append("}");
		}
		return builder.toString();
	}
	
	@NotNull
	private <T> List<Field> getFields() {
		List<List<Field>> allFields = new ArrayList<>();
		Class<?> clazz = this.object.getClass();
		if (!this.includeSuperClassFields) {
			allFields.add(Arrays.asList(clazz.getDeclaredFields()));
		} else {
			while (clazz.getSuperclass() != null) {
				allFields.add(Arrays.asList(clazz.getDeclaredFields()));
				clazz = clazz.getSuperclass();
			}
		}
		List<Field> fields = new ArrayList<>();
		for (List<Field> list : Utils.reverseList(allFields)) {
			fields.addAll(list);
		}
		return fields;
	}
	
}
