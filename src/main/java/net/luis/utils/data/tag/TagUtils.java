package net.luis.utils.data.tag;

import com.google.common.collect.Lists;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.data.tag.tags.collection.ListTag;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 *
 * @author Luis-st
 *
 */

public class TagUtils {
	
	public static @NotNull CompoundTag writeUUID(@NotNull UUID uuid) {
		CompoundTag tag = new CompoundTag();
		tag.putLong("mostBits", uuid.getMostSignificantBits());
		tag.putLong("leastBits", uuid.getLeastSignificantBits());
		return tag;
	}
	
	public static @NotNull UUID readUUID(@NotNull CompoundTag tag) {
		if (tag.contains("mostBits") && tag.contains("leastBits")) {
			return new UUID(tag.getLong("mostBits"), tag.getLong("leastBits"));
		}
		return Utils.EMPTY_UUID;
	}
	
	public static <E, T extends Tag> @NotNull ListTag writeList(@NotNull List<E> list, @NotNull Function<E, T> function) {
		ListTag listTag = new ListTag();
		for (E element : list) {
			listTag.add(function.apply(element));
		}
		return listTag;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Tag, E> @NotNull List<E> readList(@NotNull ListTag listTag, @NotNull Function<T, E> function) {
		List<E> list = Lists.newArrayList();
		for (Tag tag : listTag) {
			list.add(function.apply((T) tag));
		}
		return list;
	}
	
}
