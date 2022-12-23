package net.luis.utils.data.tag.tags.numeric;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.visitor.TagVisitor;
import net.luis.utils.util.Equals;

public class DoubleTag extends NumericTag {
	
	public static final TagType<DoubleTag> TYPE = new TagType<DoubleTag>() {
		@Override
		public DoubleTag load(DataInput input) throws LoadTagException {
			try {
				return valueOf(input.readDouble());
			} catch (IOException e) {
				throw new LoadTagException(e);
			}
		}
		
		@Override
		public String getName() {
			return "float_tag";
		}
		
		@Override
		public String getVisitorName() {
			return "DoubleTag";
		}
		
		@Override
		public boolean isValue() {
			return true;
		}
	};
	
	private final double data;
	
	private DoubleTag(double data) {
		this.data = data;
	}
	
	public static DoubleTag valueOf(double data) {
		return new DoubleTag(data);
	}
	
	@Override
	public void save(DataOutput output) throws SaveTagException {
		try {
			output.writeDouble(this.data);
		} catch (IOException e) {
			throw new SaveTagException(e);
		}
	}
	
	@Override
	public byte getId() {
		return DOUBLE_TAG;
	}
	
	@Override
	public TagType<DoubleTag> getType() {
		return TYPE;
	}
	
	@Override
	public DoubleTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitDouble(this);
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
		return Equals.equals(this, object);
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode(this.data);
	}
	
}
