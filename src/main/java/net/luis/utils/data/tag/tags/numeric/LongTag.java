package net.luis.utils.data.tag.tags.numeric;

import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.visitor.TagVisitor;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Luis-st
 *
 */

public class LongTag extends NumericTag {
	
	public static final TagType<LongTag> TYPE = new TagType<>() {
		@Override
		public @NotNull LongTag load(@NotNull DataInput input) throws LoadTagException {
			try {
				return valueOf(input.readLong());
			} catch (IOException e) {
				throw new LoadTagException(e);
			}
		}
		
		@Override
		public @NotNull String getName() {
			return "long_tag";
		}
		
		@Override
		public @NotNull String getVisitorName() {
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
	
	public static @NotNull LongTag valueOf(long data) {
		return new LongTag(data);
	}
	
	@Override
	public void save(@NotNull DataOutput output) throws SaveTagException {
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
	public @NotNull TagType<LongTag> getType() {
		return TYPE;
	}
	
	@Override
	public @NotNull LongTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(@NotNull TagVisitor visitor) {
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
		return this.data;
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
	public @NotNull Number getAsNumber() {
		return this.data;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LongTag longTag)) return false;
		
		return this.data == longTag.data;
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(this.data);
	}
	
}
