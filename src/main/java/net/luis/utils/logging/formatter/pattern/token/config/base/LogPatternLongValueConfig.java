package net.luis.utils.logging.formatter.pattern.token.config.base;

import net.luis.utils.io.codec.*;
import net.luis.utils.logging.formatter.LogTokenFormatHelper;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.util.Casing;
import net.luis.utils.math.Radix;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternLongValueConfig(
	@NonNull Optional<Radix> radix,
	@NonNull Optional<Casing> casing,
	@NonNull Optional<LogPatternPaddingConfig> padding
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternLongValueConfig> CODEC = CodecBuilder.of(
		Codecs.enumLowerName(Radix.class).optional().optionalFieldOf("radix", Optional.empty(), LogPatternLongValueConfig::radix),
		Codecs.enumLowerName(Casing.class).optional().optionalFieldOf("casing", Optional.empty(), LogPatternLongValueConfig::casing),
		LogPatternPaddingConfig.CODEC.optional().optionalFieldOf("padding", Optional.empty(), LogPatternLongValueConfig::padding)
	).create(LogPatternLongValueConfig::new);
	
	public LogPatternLongValueConfig {
		Objects.requireNonNull(radix, "Radix must not be null");
		Objects.requireNonNull(casing, "Casing must not be null");
		Objects.requireNonNull(padding, "Padding config must not be null");
	}
	
	public @NonNull String format(long value) {
		String result = switch (this.radix.orElse(Radix.DECIMAL)) {
			case BINARY -> Long.toBinaryString(value);
			case OCTAL -> Long.toOctalString(value);
			case DECIMAL -> Long.toString(value);
			case HEXADECIMAL -> Long.toHexString(value);
		};
		return LogTokenFormatHelper.format(result, this.casing, this.padding);
	}
}
