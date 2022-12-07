package net.luis.utils.data.tag.tags.numeric;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.visitor.TagVisitor;

public class LongTag extends NumericTag {
	
	public static final TagType<LongTag> TYPE = new TagType<LongTag>() {
		@Override
		public LongTag load(DataInput input) throws LoadTagException {
			try {
				return valueOf(input.readLong());
			} catch (IOException e) {
				throw new LoadTagException(e);
			}
		}
		
		@Override
		public String getName() {
			return "long_tag";
		}
		
		@Override
		public String getVisitorName() {
			return "LongTag";
		}
		
		@Override
		public boolean isValue() {
			return true;
		}
	};
	
	private final long data;
	
	private LongTag(long data) {
		this.data = data;
	}
	
	public static LongTag valueOf(long data) {
		return new LongTag(data);
	}
	
	@Override
	public void save(DataOutput output) throws SaveTagException {
		try {
			output.writeLong(this.data);
		} catch (IOException e) {
			throw new SaveTagException(e);
		}
	}
	
	@Override
	public byte getId() {
		return LONG_TAG;
	}
	
	@Override
	public TagType<LongTag> getType() {
		return TYPE;
	}
	
	@Override
	public LongTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitLong(this);
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
		} else if (object instanceof LongTag tag) {
			return this.getAsLong() == tag.getAsLong();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(this.data);
	}
	
}
