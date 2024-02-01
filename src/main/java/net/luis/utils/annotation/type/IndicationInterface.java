package net.luis.utils.annotation.type;

import java.lang.annotation.*;

/**
 * Interfaces that are annotated with this annotation do not have any methods or fields.<br>
 * The interface is only used to indication purpose only.<br>
 * <p>
 *     Any logic related to the interface is done in other classes<br>
 *     via an instance of check of the interface.<br>
 * </p>
 *
 * @author Luis-St
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IndicationInterface {}
