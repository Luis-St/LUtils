package net.luis.utils.data.tag;

import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.data.tag.tags.EndTag;
import net.luis.utils.data.tag.tags.StringTag;
import net.luis.utils.data.tag.tags.collection.ListTag;
import net.luis.utils.data.tag.tags.collection.array.IntArrayTag;
import net.luis.utils.data.tag.tags.collection.array.LongArrayTag;
import net.luis.utils.data.tag.tags.numeric.*;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class TagTypes {
	
	private static final TagType<?>[] TYPES = new TagType<?>[] {
			EndTag.TYPE, IntTag.TYPE, LongTag.TYPE, FloatTag.TYPE, DoubleTag.TYPE, StringTag.TYPE, IntArrayTag.TYPE, LongArrayTag.TYPE, ListTag.TYPE, CompoundTag.TYPE
	};
	
	public static @NotNull TagType<?> getType(int id) {
		return id >= 0 && id < TYPES.length ? TYPES[id] : TagType.createInvalid(id);
	}
	
}
