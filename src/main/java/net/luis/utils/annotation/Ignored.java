package net.luis.utils.annotation;

import java.lang.annotation.*;

/**
 * Annotations to indicate that a parameter or local variable is ignored.
 *
 * @see Ignored.Always
 * @see Ignored.Maybe
 * @see Ignored.Never
 *
 * @author Luis-St
 */
public class Ignored {
	
	/**
	 * Indicates that the parameter or local variable is always ignored.<br>
	 * Passing a null value to a parameter or local variable that is annotated with this annotation<br>
	 * will be safe and will not cause any errors.<br>
	 *
	 * @author Luis-St
	 */
	@Inherited
	@Documented
	@Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Always {}
	
	/**
	 * Indicates that the parameter or local variable is maybe ignored.<br>
	 * The reason for this could be that there is a rare case where the parameter or local variable<br>
	 * is used or that passing a null value to the parameter or local variable will cause an error.<br>
	 *
	 * @author Luis-St
	 */
	@Inherited
	@Documented
	@Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Maybe {}
	
	/**
	 * Indicates that a parameter is never ignored.<br>
	 * This is the default behavior of any parameter in java.<br>
	 * The annotation is only used to reset the behavior of a parameter that is<br>
	 * annotated with {@link Ignored.Always} or {@link Ignored.Maybe}.<br>
	 * An example would be an overridden method where the superclass ignores a parameter.<br>
	 *
	 * @author Luis-St
	 */
	@Inherited
	@Documented
	@Target({ElementType.PARAMETER})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Never {}
}
