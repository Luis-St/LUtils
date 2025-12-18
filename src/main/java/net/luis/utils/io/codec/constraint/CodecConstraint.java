package net.luis.utils.io.codec.constraint;

import net.luis.utils.io.codec.Codec;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 * @param <T> The type being constrained
 * @param <C> The codec type
 * @param <V> The type of the constraint configuration
 */
@FunctionalInterface
public interface CodecConstraint<T, C extends Codec<T>, V>  {

	@NotNull C applyConstraint(@NotNull V config);
}
