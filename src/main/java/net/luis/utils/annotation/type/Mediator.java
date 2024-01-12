package net.luis.utils.annotation.type;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Types that are annotated with this annotation are considered to be mediators<br>
 * between two or more types. They are used to facilitate communication.<br>
 * The mediator handles a many-to-many relationship between those types.<br>
 *
 * @author Luis-St
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mediator {
	
	/**
	 * @return The types that are mediated by this class.<br>
	 */
	@NotNull Class<?>[] value() default {};
}
