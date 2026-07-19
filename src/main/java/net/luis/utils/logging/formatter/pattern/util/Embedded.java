package net.luis.utils.logging.formatter.pattern.util;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 *
 * @author Luis-St
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.RECORD_COMPONENT)
// ToDo: Move to root util package
public @interface Embedded {
	
	@NotNull String namespace() default "";
	
	boolean namespaceRequired() default false;
}
