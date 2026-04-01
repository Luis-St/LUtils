package net.luis.utils.io.database.type;

import net.luis.utils.function.throwable.ThrowableTriFunction;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.database.type.value.SqlValue;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public record SqlCodec<C>(
	@NonNull String name,
	@NonNull ThrowableTriFunction<SqlTypeProvider, SqlValue, Function<String, DecoderException>, C, DecoderException> getter
) implements Codec<C> {
	
	public SqlCodec {
		Objects.requireNonNull(name, "Codec name must not be null");
		Objects.requireNonNull(getter, "Codec getter function must not be null");
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable C value) throws EncoderException {
		throw new EncoderException("Sql " + this.name + " codec is read-only and does not support encoding");
	}
	
	@Override
	public <R> @NonNull C decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as " + this.name);
		}
		
		if (provider instanceof SqlTypeProvider sqlProvider) {
			return this.getter.apply(sqlProvider, (SqlValue) value, DecoderException::new);
		}
		throw new DecoderException(StringUtils.capitalize(this.name) + " codec requires SqlTypeProvider, got " + provider.getClass().getSimpleName());
	}
}
