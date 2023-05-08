package net.luis.utils.data.serialization;

import net.luis.utils.data.tag.tags.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public interface Serializable {
	
	@NotNull CompoundTag serialize();
}
