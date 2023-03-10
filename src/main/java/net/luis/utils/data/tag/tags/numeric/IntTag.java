package net.luis.utils.data.tag.tags.numeric;

import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.visitor.TagVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Luis-st
 *
 */

public class IntTag extends NumericTag {
	
	public static final TagType<IntTag> TYPE = new TagType<>() {
		@Override
		public @NotNull IntTag load(@NotNull DataInput input) throws LoadTagException {
			try {
				return valueOf(input.readInt());
			} catch (IOException e) {
				throw new LoadTagException(e);
			}
		}
		
		@Override
		public @NotNull String getName() {
			return "int_tag";
		}
		
		@Override
		public @NotNull String getVisitorName() {
			return "IntTag";
		}
		
		@Override
		public boolean isValue() {
			return true;
		}
	};
	
	private final int data;
	
	private IntTag(int data) {
		this.data = data;
	}
	
	public static @NotNull IntTag valueOf(int data) {
		return new IntTag(data);
	}
	
	@Override
	public void save(@NotNull DataOutput output) throws SaveTagException {
		try {
			output.writeInt(this.data);
		} catch (IOException e) {
			throw new SaveTagException(e);
		}
	}
	
	@Override
	public byte getId() {
		return INT_TAG;
	}
	
	@Override
	public @NotNull TagType<IntTag> getType() {
		return TYPE;
	}
	
	@Override
	public @NotNull IntTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(@NotNull TagVisitor visitor) {
		visitor.visitInt(this);
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
		return this.data;
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
		return this.data;
	}
	
	@Override
	public @NotNull Number getAsNumber() {
		return this.data;
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof IntTag intTag)) return false;
		
		return this.data == intTag.data;
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(this.data);
	}
	
}
