package net.luis.utils.data.tag.tags.numeric;

import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.visitor.TagVisitor;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 *
 * @author Luis-St
 *
 */

public class DoubleTag extends NumericTag {
	
	//region Type
	public static final TagType<DoubleTag> TYPE = new TagType<>() {
		@Override
		public @NotNull DoubleTag load(@NotNull DataInput input) throws LoadTagException {
			try {
				return valueOf(input.readDouble());
			} catch (IOException e) {
				throw new LoadTagException(e);
			}
		}
		
		@Override
		public @NotNull String getName() {
			return "double_tag";
		}
		
		@Override
		public @NotNull String getVisitorName() {
			return "DoubleTag";
		}
	};
	//endregion
	
	private final double data;
	
	private DoubleTag(double data) {
		this.data = data;
	}
	
	public static @NotNull DoubleTag valueOf(double data) {
		return new DoubleTag(data);
	}
	
	@Override
	public void save(@NotNull DataOutput output) throws SaveTagException {
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
	public @NotNull TagType<DoubleTag> getType() {
		return TYPE;
	}
	
	@Override
	public @NotNull DoubleTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(@NotNull TagVisitor visitor) {
		visitor.visitDouble(this);
	}
	
	//region Getters
	@Override
	public int getAsInt() {
		return (int) this.data;
	}
	
	@Override
	public long getAsLong() {
		return (long) this.data;
	}
	
	@Override
	public double getAsDouble() {
		return this.data;
	}
	
	@Override
	public @NotNull Number getAsNumber() {
		return this.data;
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DoubleTag doubleTag)) return false;
		
		return Double.compare(doubleTag.data, this.data) == 0;
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode(this.data);
	}
	//endregion
}
