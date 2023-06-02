package net.luis.utils.annotation;

import java.lang.annotation.*;

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
