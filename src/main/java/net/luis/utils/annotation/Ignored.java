package net.luis.utils.annotation;

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
@Target({
	ElementType.PARAMETER, ElementType.TYPE_PARAMETER, ElementType.FIELD
})
public @interface Ignored {
	
}
