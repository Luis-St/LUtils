package net.luis.utils.data.tag.tags.collection;

import com.google.common.collect.Lists;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.TagTypes;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.data.tag.tags.collection.array.IntArrayTag;
import net.luis.utils.data.tag.tags.collection.array.LongArrayTag;
import net.luis.utils.data.tag.tags.numeric.DoubleTag;
import net.luis.utils.data.tag.tags.numeric.IntTag;
import net.luis.utils.data.tag.tags.numeric.LongTag;
import net.luis.utils.data.tag.visitor.TagVisitor;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class ListTag extends CollectionTag<Tag> {
	
	//region Type
	public static final TagType<ListTag> TYPE = new TagType<>() {
		@Override
		public @NotNull ListTag load(@NotNull DataInput input) throws LoadTagException {
			try {
				byte type = input.readByte();
				int size = input.readInt();
				if (type == 0 && size > 0) {
					throw new LoadTagException("Missing type on ListTag");
				} else {
					TagType<?> tagType = TagTypes.getType(type);
					List<Tag> data = Lists.newArrayListWithCapacity(size);
					for (int i = 0; i < size; ++i) {
						data.add(tagType.load(input));
					}
					return new ListTag(data, type);
				}
			} catch (IOException e) {
				throw new LoadTagException(e);
			}
		}
		
		@Override
		public @NotNull String getName() {
			return "data_tag";
		}
		
		@Override
		public @NotNull String getVisitorName() {
			return "ListTag";
		}
		
		@Override
		public boolean isValue() {
			return true;
		}
	};
	//endregion
	
	private final List<Tag> data;
	private byte type;
	
	public ListTag() {
		this(Lists.newArrayList(), (byte) 0);
	}
	
	private ListTag(List<Tag> data, byte type) {
		this.data = data;
		this.type = type;
	}
	
	@Override
	public void save(@NotNull DataOutput output) throws SaveTagException {
		try {
			if (this.data.isEmpty()) {
				this.type = 0;
			} else {
				this.type = this.data.get(0).getId();
			}
			output.writeByte(this.type);
			output.writeInt(this.data.size());
			for (Tag tag : this.data) {
				tag.save(output);
			}
		} catch (IOException e) {
			throw new SaveTagException(e);
		}
	}
	
	@Override
	public byte getId() {
		return LIST_TAG;
	}
	
	@Override
	public @NotNull TagType<ListTag> getType() {
		return TYPE;
	}
	
	@Override
	public @NotNull ListTag copy() {
		List<Tag> copy = Lists.newArrayList();
		for (Tag tag : this.data) {
			copy.add(tag.copy());
		}
		return new ListTag(copy, this.type);
	}
	
	@Override
	public void accept(@NotNull TagVisitor visitor) {
		visitor.visitList(this);
	}
	
	@Override
	public Tag remove(int index) {
		Tag tag = this.data.remove(index);
		this.updateTypeAfterRemove();
		return tag;
	}
	
	@Override
	public boolean isEmpty() {
		return this.data.isEmpty();
	}
	
	//region Getters
	@Override
	public Tag get(int index) {
		return this.data.get(index);
	}
	
	public int getInt(int index) {
		if (this.data.size() > index && index >= 0) {
			Tag tag = this.data.get(index);
			if (tag.getId() == INT_TAG) {
				return ((IntTag) tag).getAsInt();
			}
		}
		return 0;
	}
	
	public long getLong(int index) {
		if (this.data.size() > index && index >= 0) {
			Tag tag = this.data.get(index);
			if (tag.getId() == LONG_TAG) {
				return ((LongTag) tag).getAsLong();
			}
		}
		return 0;
	}
	
	public double getDouble(int index) {
		if (this.data.size() > index && index >= 0) {
			Tag tag = this.data.get(index);
			if (tag.getId() == DOUBLE_TAG) {
				return ((DoubleTag) tag).getAsDouble();
			}
		}
		return 0;
	}
	
	public @NotNull String getString(int index) {
		if (this.data.size() > index && index >= 0) {
			Tag tag = this.data.get(index);
			if (tag.getId() == STRING_TAG) {
				return tag.getAsString();
			}
		}
		return "";
	}
	
	public int[] getIntArray(int index) {
		if (this.data.size() > index && index >= 0) {
			Tag tag = this.data.get(index);
			if (tag.getId() == INT_ARRAY_TAG) {
				return ((IntArrayTag) tag).getAsIntArray();
			}
		}
		return new int[0];
	}
	
	public long[] getLongArray(int index) {
		if (this.data.size() > index && index >= 0) {
			Tag tag = this.data.get(index);
			if (tag.getId() == LONG_ARRAY_TAG) {
				return ((LongArrayTag) tag).getAsLongArray();
			}
		}
		return new long[0];
	}
	
	public @NotNull ListTag getList(int index) {
		if (this.data.size() > index && index >= 0) {
			Tag tag = this.data.get(index);
			if (tag.getId() == LIST_TAG) {
				return (ListTag) tag;
			}
		}
		return new ListTag();
	}
	
	public @NotNull CompoundTag getCompoundTag(int index) {
		if (this.data.size() > index && index >= 0) {
			Tag tag = this.data.get(index);
			if (tag.getId() == COMPOUND_TAG) {
				return (CompoundTag) tag;
			}
		}
		return new CompoundTag();
	}
	//endregion
	
	@Override
	public int size() {
		return this.data.size();
	}
	
	//region Setters
	@Override
	public Tag set(int index, @NotNull Tag tag) {
		Tag oldTag = this.get(index);
		if (!this.setTag(index, tag)) {
			LOGGER.warn("Try to add tag of type {} to data of {}", tag.getId(), this.type);
		}
		return oldTag;
	}
	
	@Override
	public boolean setTag(int index, @NotNull Tag tag) {
		if (this.updateType(tag)) {
			this.data.set(index, tag);
			return true;
		}
		return false;
	}
	//endregion
	
	//region Adders
	@Override
	public boolean addTag(int index, @NotNull Tag tag) {
		if (this.updateType(tag)) {
			this.data.add(index, tag);
			return true;
		}
		return false;
	}
	
	@Override
	public void add(int index, @NotNull Tag tag) {
		if (!this.addTag(index, tag)) {
			LOGGER.warn("Try to add tag of type {} to data of {}", tag.getId(), this.type);
		}
	}
	//endregion
	
	//region Internal helper methods
	private boolean updateType(@NotNull Tag tag) {
		if (tag.getId() == 0) {
			return false;
		} else if (this.type == 0) {
			this.type = tag.getId();
			return true;
		} else {
			return this.type == tag.getId();
		}
	}
	
	private void updateTypeAfterRemove() {
		if (this.data.isEmpty()) {
			this.type = 0;
		}
	}
	//endregion
	
	@Override
	public void clear() {
		this.data.clear();
		this.type = 0;
	}
	
	@Override
	public byte getElementType() {
		return this.type;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ListTag tags)) return false;
		if (!super.equals(o)) return false;
		
		if (this.type != tags.type) return false;
		return this.data.equals(tags.data);
	}
	
	@Override
	public int hashCode() {
		return this.data.hashCode();
	}
	//endregion
	
}
