package net.luis.utils.data.tag.tags.collection.array;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.tags.collection.CollectionTag;
import net.luis.utils.data.tag.tags.numeric.LongTag;
import net.luis.utils.data.tag.tags.numeric.NumericTag;
import net.luis.utils.data.tag.visitor.TagVisitor;

public class LongArrayTag extends CollectionTag<LongTag> {
	
	public static final TagType<LongArrayTag> TYPE = new TagType<LongArrayTag>() {
		@Override
		public LongArrayTag load(DataInput input) throws IOException {
			int length = input.readInt();
			long[] data = new long[length];
			for (int i = 0; i < length; i++) {
				data[i] = input.readLong();
			}
			return new LongArrayTag(data);
		}
		
		@Override
		public String getName() {
			return "int_array_tag";
		}
		
		@Override
		public String getVisitorName() {
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
	
	private static long[] toArray(List<Long> list) {
		long[] data = new long[list.size()];
		for (int i = 0; i < list.size(); i++) {
			Long integer = list.get(i);
			data[i] = integer == null ? 0 : integer;
		}
		return data;
	}
	
	@Override
	public void save(DataOutput output) throws IOException {
		output.writeInt(this.data.length);
		for (int i = 0; i < this.data.length; i++) {
			output.writeLong(this.data[i]);
		}
	}
	
	@Override
	public byte getId() {
		return LONG_ARRAY_TAG;
	}
	
	@Override
	public TagType<LongArrayTag> getType() {
		return TYPE;
	}
	
	@Override
	public LongArrayTag copy() {
		long[] data = new long[this.data.length];
		System.arraycopy(this.data, 0, data, 0, this.data.length);
		return new LongArrayTag(data);
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitLongArray(this);
	}
	
	public long[] getAsLongArray() {
		return this.data;
	}
	
	@Override
	public LongTag set(int index, LongTag tag) {
		long i = this.data[index];
		this.data[index] = tag.getAsByte();
		return LongTag.valueOf(i);
	}
	
	@Override
	public void add(int index, LongTag tag) {
		this.data = ArrayUtils.add(this.data, index, tag.getAsByte());
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
			this.data[index] = numericTag.getAsByte();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean addTag(int index, Tag tag) {
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
	public LongTag get(int index) {
		return LongTag.valueOf(this.data[index]);
	}
	
	@Override
	public int size() {
		return this.data.length;
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return object instanceof LongArrayTag tag && Arrays.equals(this.data, tag.data);
		}
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(this.data);
	}
	
}