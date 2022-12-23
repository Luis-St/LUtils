package net.luis.utils.data.tag.tags.numeric;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.visitor.TagVisitor;
import net.luis.utils.util.Equals;

public class FloatTag extends NumericTag {
	
	public static final TagType<FloatTag> TYPE = new TagType<FloatTag>() {
		@Override
		public FloatTag load(DataInput input) throws LoadTagException {
			try {
				return valueOf(input.readFloat());
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
			return "FloatTag";
		}
		
		@Override
		public boolean isValue() {
			return true;
		}
	};
	
	private final float data;
	
	private FloatTag(float data) {
		this.data = data;
	}
	
	public static FloatTag valueOf(float data) {
		return new FloatTag(data);
	}
	
	@Override
	public void save(DataOutput output) throws SaveTagException {
		try {
			output.writeFloat(this.data);
		} catch (IOException e) {
			throw new SaveTagException(e);
		}
	}
	
	@Override
	public byte getId() {
		return FLOAT_TAG;
	}
	
	@Override
	public TagType<FloatTag> getType() {
		return TYPE;
	}
	
	@Override
	public FloatTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitFloat(this);
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
		return Float.hashCode(this.data);
	}
	
}
