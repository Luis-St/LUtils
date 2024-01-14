package net.luis.utils.annotation.type;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Types that are annotated with this annotation will be instantiated only once.<br>
 * It might be possible to get the instance of the class by:
 * <ul>
 *     <li>Calling the static method {@code getInstance()}</li>
 *     <li>Accessing the public static field {@code INSTANCE}</li>
 * </ul>
 *
 * If an interface is annotated with this annotation,<br>
 * all classes that implement this interface will be instantiated only once.<br>
 *
 * @author Luis-St
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Singleton {
	
	/**
	 * @return The method to obtain the instance of the singleton class
	 */
	@NotNull Method value() default Method.UNDEFINED;
	
	/**
	 * The method to obtain the instance of the singleton class.
	 *
	 * @author Luis-St
	 */
	static enum Method {
		/**
		 * The instance of the singleton class can be obtained by calling the static method {@code getInstance()}.<br>
		 */
		METHOD,
		/**
		 * The instance of the singleton class can be obtained by accessing the public static field {@code INSTANCE}.<br>
		 */
		FIELD,
		/**
		 * The instance of the singleton class can be obtained by custom method.<br>
		 */
		CUSTOM,
		/**
		 * The instance of the singleton class can be not obtained.<br>
		 */
		NOT_POSSIBLE,
		/**
		 * The method to obtain the instance of the singleton class is undefined.<br>
		 */
		UNDEFINED;
	}
}
