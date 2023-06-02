package net.luis.utils.data.serialization;

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
	
	Type type() default Type.CONSTRUCTOR;
	
	String methodName() default "";
	
	enum Type {
		
		CONSTRUCTOR(), METHOD(), STATIC_METHOD()
		
	}
	
}
