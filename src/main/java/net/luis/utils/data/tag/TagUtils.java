package net.luis.utils.data.tag;

import com.google.common.collect.Lists;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.data.tag.tags.collection.ListTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class TagUtils {
	
	public static @NotNull CompoundTag writeUUID(UUID uuid) {
		CompoundTag tag = new CompoundTag();
		tag.putLong("mostBits", uuid.getMostSignificantBits());
		tag.putLong("leastBits", uuid.getLeastSignificantBits());
		return tag;
	}
	
	public static @NotNull UUID readUUID(CompoundTag tag) {
		return new UUID(tag.getLong("mostBits"), tag.getLong("leastBits"));
	}
	
	public static <E, T extends Tag> @NotNull ListTag writeList(List<E> list, Function<E, T> function) {
		ListTag listTag = new ListTag();
		for (E element : list) {
			listTag.add(function.apply(element));
		}
		return listTag;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Tag, E> @NotNull List<E> readList(ListTag listTag, Function<T, E> function) {
		List<E> list = Lists.newArrayList();
		for (Tag tag : listTag) {
			list.add(function.apply((T) tag));
		}
		return list;
	}
	
}
