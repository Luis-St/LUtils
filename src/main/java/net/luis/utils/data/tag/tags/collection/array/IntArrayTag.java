package net.luis.utils.data.tag.tags.collection.array;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.tags.collection.CollectionTag;
import net.luis.utils.data.tag.tags.numeric.IntTag;
import net.luis.utils.data.tag.tags.numeric.NumericTag;
import net.luis.utils.data.tag.visitor.TagVisitor;

public class IntArrayTag extends CollectionTag<IntTag> {
	
	public static final TagType<IntArrayTag> TYPE = new TagType<IntArrayTag>() {
		@Override
		public IntArrayTag load(DataInput input) throws LoadTagException {
			try {
				int length = input.readInt();
				int[] data = new int[length];
				for (int i = 0; i < length; i++) {
					data[i] = input.readInt();
				}
				return new IntArrayTag(data);
			} catch (IOException e) {
				throw new LoadTagException(e);
			}
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
	
	private int[] data;
	
	public IntArrayTag() {
		this(new int[0]);
	}
	
	public IntArrayTag(List<Integer> data) {
		this(toArray(data));
	}
	
	public IntArrayTag(int[] data) {
		this.data = data;
	}
	
	private static int[] toArray(List<Integer> list) {
		int[] data = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			Integer integer = list.get(i);
			data[i] = integer == null ? 0 : integer;
		}
		return data;
	}
	
	@Override
	public void save(DataOutput output) throws SaveTagException {
		try {
			output.writeInt(this.data.length);
			for (int i = 0; i < this.data.length; i++) {
				output.writeInt(this.data[i]);
			}
		} catch (IOException e) {
			throw new SaveTagException(e);
		}
	}
	
	@Override
	public byte getId() {
		return INT_ARRAY_TAG;
	}
	
	@Override
	public TagType<IntArrayTag> getType() {
		return TYPE;
	}
	
	@Override
	public IntArrayTag copy() {
		int[] data = new int[this.data.length];
		System.arraycopy(this.data, 0, data, 0, this.data.length);
		return new IntArrayTag(data);
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitIntArray(this);
	}
	
	public int[] getAsIntArray() {
		return this.data;
	}
	
	@Override
	public IntTag set(int index, IntTag tag) {
		int i = this.data[index];
		this.data[index] = tag.getAsInt();
		return IntTag.valueOf(i);
	}
	
	@Override
	public void add(int index, IntTag tag) {
		this.data = ArrayUtils.add(this.data, index, tag.getAsInt());
	}
	
	@Override
	public IntTag remove(int index) {
		int i = this.data[index];
		this.data = ArrayUtils.remove(this.data, index);
		return IntTag.valueOf(i);
	}
	
	@Override
	public boolean setTag(int index, Tag tag) {
		if (tag instanceof NumericTag numericTag) {
			this.data[index] = numericTag.getAsInt();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean addTag(int index, Tag tag) {
		if (tag instanceof NumericTag numericTag) {
			this.data = ArrayUtils.add(this.data, index, numericTag.getAsInt());
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public byte getElementType() {
		return INT_TAG;
	}
	
	@Override
	public IntTag get(int index) {
		return IntTag.valueOf(this.data[index]);
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
			return object instanceof IntArrayTag tag && Arrays.equals(this.data, tag.data);
		}
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(this.data);
	}
	
}
