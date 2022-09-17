package net.luis.utils.data.tag.tags.numeric;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.visitor.TagVisitor;

public class ByteTag extends NumericTag {
	
	public static final TagType<ByteTag> TYPE = new TagType<ByteTag>() {
		@Override
		public ByteTag load(DataInput input) throws IOException {
			return valueOf(input.readByte());
		}
		
		@Override
		public String getName() {
			return "byte_tag";
		}
		
		@Override
		public String getVisitorName() {
			return "ByteTag";
		}
		
		@Override
		public boolean isValue() {
			return true;
		}
	};
	
	private final byte data;
	
	private ByteTag(byte data) {
		this.data = data;
	}
	
	public static ByteTag valueOf(byte data) {
		return new ByteTag(data);
	}
	
	public static ByteTag valueOf(boolean data) {
		if (data) {
			return new ByteTag((byte) 1);
		}
		return new ByteTag((byte) 0);
	}
	
	@Override
	public void save(DataOutput output) throws IOException {
		output.writeByte(this.data);
	}
	
	@Override
	public byte getId() {
		return BYTE_TAG;
	}
	
	@Override
	public TagType<ByteTag> getType() {
		return TYPE;
	}
	
	@Override
	public ByteTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitByte(this);
	}
	
	@Override
	public byte getAsByte() {
		return (byte) this.data;
	}
	
	@Override
	public short getAsShort() {
		return (short) this.data;
	}
	
	@Override
	public int getAsInt() {
		return (int) this.data;
	}
	
	@Override
	public long getAsLong() {
		return (long) this.data;
	}
	
	@Override
	public float getAsFloat() {
		return (float) this.data;
	}
	
	@Override
	public double getAsDouble() {
		return (double) this.data;
	}
	
	@Override
	public Number getAsNumber() {
		return this.data;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		} else if (object instanceof ByteTag tag) {
			return this.getAsByte() == tag.getAsByte();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Byte.hashCode(this.data);
	}
	
}
