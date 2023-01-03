package net.luis.utils.data.tag.tags.collection.array;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.tags.collection.CollectionTag;
import net.luis.utils.data.tag.tags.numeric.ByteTag;
import net.luis.utils.data.tag.tags.numeric.NumericTag;
import net.luis.utils.data.tag.visitor.TagVisitor;
import net.luis.utils.util.Equals;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ByteArrayTag extends CollectionTag<ByteTag> {
	
	public static final TagType<ByteArrayTag> TYPE = new TagType<>() {
		@Override
		public @NotNull ByteArrayTag load(DataInput input) throws LoadTagException {
			try {
				int length = input.readInt();
				byte[] data = new byte[length];
				for (int i = 0; i < length; i++) {
					data[i] = input.readByte();
				}
				return new ByteArrayTag(data);
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
	
	private byte[] data;
	
	public ByteArrayTag() {
		this(new byte[0]);
	}
	
	public ByteArrayTag(List<Byte> data) {
		this(toArray(data));
	}
	
	public ByteArrayTag(byte[] data) {
		this.data = data;
	}
	
	private static byte[] toArray(List<Byte> list) {
		byte[] data = new byte[list.size()];
		for (int i = 0; i < list.size(); i++) {
			Byte integer = list.get(i);
			data[i] = integer == null ? 0 : integer;
		}
		return data;
	}
	
	@Override
	public void save(DataOutput output) throws SaveTagException {
		try {
			output.writeInt(this.data.length);
			for (int i = 0; i < this.data.length; i++) {
				output.writeByte(this.data[i]);
			}
		} catch (IOException e) {
			throw new SaveTagException(e);
		}
	}
	
	@Override
	public byte getId() {
		return BYTE_ARRAY_TAG;
	}
	
	@Override
	public @NotNull TagType<ByteArrayTag> getType() {
		return TYPE;
	}
	
	@Override
	public @NotNull ByteArrayTag copy() {
		byte[] data = new byte[this.data.length];
		System.arraycopy(this.data, 0, data, 0, this.data.length);
		return new ByteArrayTag(data);
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitByteArray(this);
	}
	
	public byte[] getAsByteArray() {
		return this.data;
	}
	
	@Override
	public @NotNull ByteTag set(int index, ByteTag tag) {
		byte i = this.data[index];
		this.data[index] = tag.getAsByte();
		return ByteTag.valueOf(i);
	}
	
	@Override
	public void add(int index, ByteTag tag) {
		this.data = ArrayUtils.add(this.data, index, tag.getAsByte());
	}
	
	@Override
	public ByteTag remove(int index) {
		byte i = this.data[index];
		this.data = ArrayUtils.remove(this.data, index);
		return ByteTag.valueOf(i);
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
		return BYTE_TAG;
	}
	
	@Override
	public ByteTag get(int index) {
		return ByteTag.valueOf(this.data[index]);
	}
	
	@Override
	public int size() {
		return this.data.length;
	}
	
	@Override
	public boolean equals(Object object) {
		return Equals.equals(this, object);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(this.data);
	}
	
}