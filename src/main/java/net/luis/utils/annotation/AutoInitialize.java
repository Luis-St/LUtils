package net.luis.utils.annotation;

import java.lang.annotation.*;

/**
 * Types that are annotated with this annotation are considered to be auto initialized.<br>
 *
 * @author Luis-St
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoInitialize {}
