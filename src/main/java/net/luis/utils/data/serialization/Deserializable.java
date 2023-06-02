package net.luis.utils.data.serialization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Luis-st
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Deserializable {
	
	Type type() default Type.CONSTRUCTOR;
	
	String methodName() default "";
	
	public static enum Type {
		
		CONSTRUCTOR(), METHOD(), STATIC_METHOD();
	
	}
	
}
