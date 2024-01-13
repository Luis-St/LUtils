package net.luis.utils.annotation.type;

import java.lang.annotation.*;

/**
 * Types that are annotated with this annotation are considered as Facades.<br>
 * Facades are used to provide a simple interface to a complex subsystem.<br>
 * <br>
 * Facades can also be used to extend multiple classes.<br>
 * Therefor, the facade hides an instance of each class that should be extended<br>
 * and provides the methods of the extended classes.<br>
 * The logic of the methods provided by the facade may be different<br>
 * from the logic from the standalone classes.<br>
 * The facade can also keep methods internal or provide additional methods.<br>
 *
 * @author Luis-St
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Facade {}
