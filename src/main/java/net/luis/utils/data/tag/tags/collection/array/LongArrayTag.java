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
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class LongArrayTag extends CollectionTag<LongTag> {
	
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
	public @NotNull LongTag set(int index, @NotNull LongTag tag) {
		long i = this.data[index];
		this.data[index] = tag.getAsByte();
		return LongTag.valueOf(i);
	}
	
	@Override
	public void add(int index, @NotNull LongTag tag) {
		this.data = ArrayUtils.add(this.data, index, tag.getAsByte());
	}
	
	@Override
	public @Nullable LongTag remove(int index) {
		long i = this.data[index];
		this.data = ArrayUtils.remove(this.data, index);
		return LongTag.valueOf(i);
	}
	
	@Override
	public boolean setTag(int index, @NotNull Tag tag) {
		if (tag instanceof NumericTag numericTag) {
			this.data[index] = numericTag.getAsByte();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean addTag(int index, @NotNull Tag tag) {
		if (tag instanceof NumericTag numericTag) {
			this.data = ArrayUtils.add(this.data, index, numericTag.getAsByte());
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
	public @NotNull LongTag get(int index) {
		return LongTag.valueOf(this.data[index]);
	}
	
	@Override
	public int size() {
		return this.data.length;
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof LongArrayTag longTags)) return false;
		if (!super.equals(o)) return false;
		
		return Arrays.equals(this.data, longTags.data);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(this.data);
	}
	
}