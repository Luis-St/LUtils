package net.luis.utils.annotation;

import java.lang.annotation.*;

/**
 *
 * @author Luis-St
 *
 */


public class Ignored {
	
	@Documented
	@Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Always { }
	
	@Documented
	@Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Maybe { }
	
	@Documented
	@Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Never { }
}
