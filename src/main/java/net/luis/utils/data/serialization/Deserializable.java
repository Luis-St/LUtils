package net.luis.utils.data.serialization;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 *
 * @author Luis-st
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Deserializable {
	
	@NotNull Type type() default Type.CONSTRUCTOR;
	
	@NotNull String methodName() default "";
	
	enum Type {
		
		CONSTRUCTOR(), METHOD(), STATIC_METHOD()
		
	}
	
}
