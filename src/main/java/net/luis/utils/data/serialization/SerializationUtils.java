package net.luis.utils.data.serialization;

import net.luis.utils.data.serialization.Deserializable.Type;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.exception.TagException;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.util.reflection.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class SerializationUtils {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static <T extends Serializable> @NotNull T deserialize(@NotNull Class<T> clazz, @NotNull Path path) {
		try {
			Tag tag = Tag.load(path);
			if (tag instanceof CompoundTag compoundTag) {
				return deserialize(clazz, compoundTag);
			} else {
				LOGGER.error("The type of the loaded tag ({}) does not correspond to the expected type ({})", tag.getType().getVisitorName(), CompoundTag.TYPE.getVisitorName());
				throw new TagException();
			}
		} catch (Exception e) {
			LOGGER.error("Deserialization of an object of type {} from tag in file {} failed", clazz.getSimpleName(), path);
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> @NotNull T deserialize(@NotNull Class<T> clazz, @NotNull CompoundTag tag) {
		if (!clazz.isAnnotationPresent(Deserializable.class)) {
			LOGGER.error("An object of type {} cannot be deserialized because the type is not marked as deserializable", clazz.getSimpleName());
			throw new RuntimeException();
		}
		try {
			Deserializable deserializable = clazz.getAnnotationsByType(Deserializable.class)[0];
			if (deserializable.type() == Type.CONSTRUCTOR) {
				if (ReflectionHelper.hasConstructor(clazz, CompoundTag.class)) {
					return Objects.requireNonNull(ReflectionHelper.newInstance(clazz, tag));
				}
				LOGGER.error("The deserialization type of type {} was set to constructor, but no valid constructor was found", clazz.getSimpleName());
				throw new RuntimeException();
			} else if (deserializable.type() == Type.METHOD) {
				if (ReflectionHelper.hasConstructor(clazz)) {
					T object = Objects.requireNonNull(ReflectionHelper.newInstance(clazz));
					if (ReflectionHelper.hasMethod(clazz, deserializable.methodName(), CompoundTag.class)) {
						Method method = ReflectionHelper.getMethod(clazz, deserializable.methodName(), CompoundTag.class);
						if (!Modifier.isStatic(method.getModifiers())) {
							ReflectionHelper.invoke(method, object, tag);
							return object;
						}
						LOGGER.error("The method {} to be used for deserialization is static, but a non-static method was expected", deserializable.methodName());
						throw new RuntimeException();
					}
					LOGGER.error("The deserialization type of type {} was set to method, but no valid method was found", clazz.getSimpleName());
					throw new RuntimeException();
				}
				LOGGER.error("The deserialization type of type {} was set to method, but no valid constructor was found to create the class", clazz.getSimpleName());
				throw new RuntimeException();
			} else if (deserializable.type() == Type.STATIC_METHOD) {
				if (ReflectionHelper.hasMethod(clazz, deserializable.methodName(), CompoundTag.class)) {
					Method method = ReflectionHelper.getMethod(clazz, deserializable.methodName(), CompoundTag.class);
					if (Modifier.isStatic(method.getModifiers())) {
						return (T) Objects.requireNonNull(ReflectionHelper.invoke(method, null, tag));
					}
					LOGGER.error("The method {} to be used for deserialization is non-static, but a static method was expected", deserializable.methodName());
					throw new RuntimeException();
				}
				LOGGER.error("The deserialization type of type {} was set to method, but no valid method was found", clazz.getSimpleName());
				throw new RuntimeException();
			} else {
				LOGGER.error("The deserialization type of type {} must not be null", clazz.getSimpleName());
				throw new RuntimeException();
			}
		} catch (Exception e) {
			LOGGER.error("Failed to deserialize an object of type {} from a tag of type {}", clazz.getSimpleName(), tag.getType().getVisitorName());
			throw new RuntimeException(e);
		}
	}
	
}
