package net.luis.utils.annotation;

import java.lang.annotation.*;

/**
 * Types that are annotated with this annotation are considered to be used internally.<br>
 * They are not intended to be used by the user, because this may cause an unexpected behavior.<br>
 *
 * @author Luis-St
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface Internal {}
