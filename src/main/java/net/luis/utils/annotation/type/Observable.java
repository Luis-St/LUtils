package net.luis.utils.annotation.type;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Types that are annotated with this annotation are observable.<br>
 * This means that there are methods that can be called to add and remove listeners.<br>
 * The annotated type might be associated with an event system.<br>
 *
 * @author Luis-St
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Observable {
	
	/**
	 * Returns the type of the observer that is used by this observable.<br>
	 * The type might be used to automatically register and unregister listeners.<br>
	 * If the type is {@code Object.class}, no type is specified.<br>
	 * @return The type of the observer
	 */
	@NotNull Class<?> value() default Object.class;
	
	/**
	 * Returns the methods that are used to add remove listeners.<br>
	 * The methods which are returned by this method must be public and non-static.<br>
	 * They must have exactly one parameter of the type that is returned by {@link #value()}.<br>
	 * The methods can include wildcard characters or regular expressions.<br>
	 * @return The methods that are used to add and remove listeners
	 */
	@NotNull String[] methods() default {};
}
