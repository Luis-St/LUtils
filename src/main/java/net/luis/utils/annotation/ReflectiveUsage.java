package net.luis.utils.annotation;

import java.lang.annotation.*;

/**
 * This annotation is used to indicate that the annotated element is used by reflection.<br>
 * When modifying the annotated element, this may cause an unexpected behavior or an error.<br>
 * Accessing but not modifying the annotated element is safe.<br>
 *
 * @author Luis-St
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReflectiveUsage {}
