package net.luis.utils.annotation.type;

import java.lang.annotation.*;

/**
 * Interfaces that are annotated with this annotation are<br>
 * are only used to indicate the class is a type of the interface.<br>
 * The interface does not have any methods or fields.<br>
 *
 * @author Luis-St
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IndicationInterface {}
