package net.luis.utils.data.tag.tags.numeric;

import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.visitor.TagVisitor;
import net.luis.utils.util.Equals;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Luis-st
 *
 */

public class ByteTag extends NumericTag {
	
	public static final TagType<ByteTag> TYPE = new TagType<>() {
		@Override
		public @NotNull ByteTag load(DataInput input) throws LoadTagException {
			try {
				return valueOf(input.readByte());
			} catch (IOException e) {
				throw new LoadTagException(e);
			}
		}
		
		@Override
		public @NotNull String getName() {
			return "byte_tag";
		}
		
		@Override
		public @NotNull String getVisitorName() {
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
	public void save(DataOutput output) throws SaveTagException {
		try {
			output.writeByte(this.data);
		} catch (IOException e) {
			throw new SaveTagException(e);
		}
	}
	
	@Override
	public byte getId() {
		return BYTE_TAG;
	}
	
	@Override
	public @NotNull TagType<ByteTag> getType() {
		return TYPE;
	}
	
	@Override
	public @NotNull ByteTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitByte(this);
	}
	
	@Override
	public byte getAsByte() {
		return this.data;
	}
	
	@Override
	public short getAsShort() {
		return this.data;
	}
	
	@Override
	public int getAsInt() {
		return this.data;
	}
	
	@Override
	public long getAsLong() {
		return this.data;
	}
	
	@Override
	public float getAsFloat() {
		return this.data;
	}
	
	@Override
	public double getAsDouble() {
		return this.data;
	}
	
	@Override
	public @NotNull Number getAsNumber() {
		return this.data;
	}
	
	@Override
	public boolean equals(Object object) {
		return Equals.equals(this, object);
	}
	
	@Override
	public int hashCode() {
		return Byte.hashCode(this.data);
	}
	
}
