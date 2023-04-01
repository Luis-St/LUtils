package net.luis.utils.data.serialization;

import net.luis.utils.data.serialization.Deserializable.Type;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.exception.TagException;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.util.unsafe.reflection.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Luis-St
 */

public class SerializationUtils {
	
	private static final Logger LOGGER = LogManager.getLogger(SerializationUtils.class);
	
	public static <T extends Serializable> @NotNull T deserialize(@NotNull Class<T> clazz, @NotNull Path path) {
		try {
			Tag tag = Tag.load(path);
			if (tag instanceof CompoundTag compoundTag) {
				return deserialize(clazz, compoundTag);
			} else {
				throw new TagException("The type of the loaded tag (" + tag.getType().getVisitorName() + ") does not match the expected type (" + CompoundTag.TYPE.getVisitorName() + ")");
			}
		} catch (Exception e) {
			throw new RuntimeException("An error occurred while deserializing an object of type " + clazz.getSimpleName() + " from file " + path, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> @NotNull T deserialize(@NotNull Class<T> clazz, @NotNull CompoundTag tag) {
		if (!clazz.isAnnotationPresent(Deserializable.class)) {
			throw new RuntimeException("An object of type " + clazz.getSimpleName() + " cannot be deserialized because the type is not marked as deserializable");
		}
		try {
			Deserializable deserializable = clazz.getAnnotation(Deserializable.class);
			if (deserializable.type() == Type.CONSTRUCTOR) {
				if (ReflectionHelper.hasConstructor(clazz, CompoundTag.class)) {
					return Objects.requireNonNull(ReflectionHelper.newInstance(clazz, tag));
				}
				throw new RuntimeException("The deserialization type of the type " + clazz.getSimpleName() + " was set to constructor, but a valid constructor could not be found");
			} else if (deserializable.type() == Type.METHOD) {
				if (ReflectionHelper.hasConstructor(clazz)) {
					T object = Objects.requireNonNull(ReflectionHelper.newInstance(clazz));
					if (ReflectionHelper.hasMethod(clazz, deserializable.methodName(), CompoundTag.class)) {
						Method method = ReflectionHelper.getMethod(clazz, deserializable.methodName(), CompoundTag.class);
						if (!Modifier.isStatic(method.getModifiers())) {
							ReflectionHelper.invoke(method, object, tag);
							return object;
						}
						throw new RuntimeException("The method " + deserializable.methodName() + " to be used for deserialization is static, but a non-static method was expected");
					}
					throw new RuntimeException("The deserialization type of type " + clazz.getSimpleName() + " was set to method, but no valid method was found");
				}
				throw new RuntimeException("The deserialization type of type " + clazz.getSimpleName() + " was set to method, but no valid constructor was found to create the class");
			} else if (deserializable.type() == Type.STATIC_METHOD) {
				if (ReflectionHelper.hasMethod(clazz, deserializable.methodName(), CompoundTag.class)) {
					Method method = ReflectionHelper.getMethod(clazz, deserializable.methodName(), CompoundTag.class);
					if (Modifier.isStatic(method.getModifiers())) {
						return (T) Objects.requireNonNull(ReflectionHelper.invoke(method, null, tag));
					}
					throw new RuntimeException("The method " + deserializable.methodName() + " to be used for deserialization is non-static, but a static method was expected");
				}
				throw new RuntimeException("The deserialization type of type " + clazz.getSimpleName() + " was set to method, but no valid method was found");
			} else {
				throw new RuntimeException("The deserialization type of type " + clazz.getSimpleName() + " must not be null");
			}
		} catch (Exception e) {
			throw new RuntimeException("An error occurred while deserializing an object of type " + clazz.getSimpleName() + " from a tag of type " + tag.getType().getVisitorName(), e);
		}
	}
	
}
