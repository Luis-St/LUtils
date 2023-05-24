package net.luis.utils.data.tag.tags.collection.array;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.tags.collection.CollectionTag;
import net.luis.utils.data.tag.tags.numeric.LongTag;
import net.luis.utils.data.tag.tags.numeric.NumericTag;
import net.luis.utils.data.tag.visitor.TagVisitor;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class LongArrayTag extends CollectionTag<LongTag> {
	
	//region Type
	public static final TagType<LongArrayTag> TYPE = new TagType<>() {
		@Override
		public @NotNull LongArrayTag load(@NotNull DataInput input) throws LoadTagException {
			try {
				int length = input.readInt();
				long[] data = new long[length];
				for (int i = 0; i < length; i++) {
					data[i] = input.readLong();
				}
				return new LongArrayTag(data);
			} catch (IOException e) {
				throw new LoadTagException(e);
			}
		}
		
		@Override
		public @NotNull String getName() {
			return "int_array_tag";
		}
		
		@Override
		public @NotNull String getVisitorName() {
			return "IntArrayTag";
		}
	};
	//endregion
	
	private long[] data;
	
	public LongArrayTag() {
		this(new long[0]);
	}
	
	public LongArrayTag(List<Long> data) {
		this(toArray(data));
	}
	
	public LongArrayTag(long[] data) {
		this.data = data;
	}
	
	private static long[] toArray(@NotNull List<Long> list) {
		long[] data = new long[list.size()];
		for (int i = 0; i < list.size(); i++) {
			Long integer = list.get(i);
			data[i] = integer == null ? 0 : integer;
		}
		return data;
	}
	
	@Override
	public void save(@NotNull DataOutput output) throws SaveTagException {
		try {
			output.writeInt(this.data.length);
			for (long datum : this.data) {
				output.writeLong(datum);
			}
		} catch (IOException e) {
			throw new SaveTagException(e);
		}
	}
	
	@Override
	public byte getId() {
		return LONG_ARRAY_TAG;
	}
	
	@Override
	public @NotNull TagType<LongArrayTag> getType() {
		return TYPE;
	}
	
	@Override
	public @NotNull LongArrayTag copy() {
		long[] data = new long[this.data.length];
		System.arraycopy(this.data, 0, data, 0, this.data.length);
		return new LongArrayTag(data);
	}
	
	@Override
	public void accept(@NotNull TagVisitor visitor) {
		visitor.visitLongArray(this);
	}
	
	public long[] getAsLongArray() {
		return this.data;
	}
	
	@Override
	public LongTag set(int index, @NotNull LongTag tag) {
		long i = this.data[index];
		this.data[index] = tag.getAsLong();
		return LongTag.valueOf(i);
	}
	
	@Override
	public void add(int index, @NotNull LongTag tag) {
		this.data = ArrayUtils.add(this.data, index, tag.getAsLong());
	}
	
	@Override
	public LongTag remove(int index) {
		long i = this.data[index];
		this.data = ArrayUtils.remove(this.data, index);
		return LongTag.valueOf(i);
	}
	
	@Override
	public boolean setTag(int index, Tag tag) {
		if (tag instanceof NumericTag numericTag) {
			this.data[index] = numericTag.getAsLong();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean addTag(int index, Tag tag) {
		if (tag instanceof NumericTag numericTag) {
			this.data = ArrayUtils.add(this.data, index, numericTag.getAsLong());
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public byte getElementType() {
		return LONG_TAG;
	}
	
	@Override
	public LongTag get(int index) {
		return LongTag.valueOf(this.data[index]);
	}
	
	@Override
	public int size() {
		return this.data.length;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LongArrayTag longTags)) return false;
		if (!super.equals(o)) return false;
		
		return Arrays.equals(this.data, longTags.data);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(this.data);
	}
	//endregion
}