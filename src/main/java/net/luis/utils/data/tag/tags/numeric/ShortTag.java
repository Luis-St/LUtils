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

public class ShortTag extends NumericTag {
	
	public static final TagType<ShortTag> TYPE = new TagType<>() {
		@Override
		public @NotNull ShortTag load(DataInput input) throws LoadTagException {
			try {
				return valueOf(input.readShort());
			} catch (IOException e) {
				throw new LoadTagException(e);
			}
		}
		
		@Override
		public @NotNull String getName() {
			return "short_tag";
		}
		
		@Override
		public @NotNull String getVisitorName() {
			return "ShortTag";
		}
		
		@Override
		public boolean isValue() {
			return true;
		}
	};
	
	private final short data;
	
	private ShortTag(short data) {
		this.data = data;
	}
	
	public static ShortTag valueOf(short data) {
		return new ShortTag(data);
	}
	
	@Override
	public void save(DataOutput output) throws SaveTagException {
		try {
			output.writeShort(this.data);
		} catch (IOException e) {
			throw new SaveTagException(e);
		}
	}
	
	@Override
	public byte getId() {
		return SHORT_TAG;
	}
	
	@Override
	public @NotNull TagType<ShortTag> getType() {
		return TYPE;
	}
	
	@Override
	public @NotNull ShortTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitShort(this);
	}
	
	@Override
	public byte getAsByte() {
		return (byte) this.data;
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
		return Short.hashCode(this.data);
	}
	
}
