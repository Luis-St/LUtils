package net.luis.utils.data.tag.tags;

import net.luis.utils.data.tag.Tag;
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

public class StringTag implements Tag {
	
	public static final StringTag EMPTY = new StringTag("");
	public static final TagType<StringTag> TYPE = new TagType<>() {
		@Override
		public @NotNull StringTag load(@NotNull DataInput input) throws LoadTagException {
			try {
				int length = input.readInt();
				if (length == 0) {
					return StringTag.EMPTY;
				} else {
					char[] array = new char[length];
					for (int i = 0; i < length; i++) {
						array[i] = (char) (input.readInt() - length * 2);
					}
					return StringTag.valueOf(new String(array));
				}
			} catch (IOException e) {
				throw new LoadTagException(e);
			}
		}
		
		@Override
		public @NotNull String getName() {
			return "string_tag";
		}
		
		@Override
		public @NotNull String getVisitorName() {
			return "StringTag";
		}
	};
	private final String data;
	
	private StringTag(String data) {
		this.data = data;
	}
	
	public static @NotNull StringTag valueOf(String data) {
		if (data == null) {
			return EMPTY;
		}
		return data.isEmpty() ? EMPTY : new StringTag(data);
	}
	
	@Override
	public void save(@NotNull DataOutput output) throws SaveTagException {
		try {
			int[] array = this.data.chars().toArray();
			output.writeInt(array.length);
			for (int i : array) {
				output.writeInt(i + array.length * 2);
			}
		} catch (IOException e) {
			throw new SaveTagException(e);
		}
	}
	
	@Override
	public byte getId() {
		return STRING_TAG;
	}
	
	@Override
	public @NotNull TagType<StringTag> getType() {
		return TYPE;
	}
	
	@Override
	public @NotNull StringTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(@NotNull TagVisitor visitor) {
		visitor.visitString(this);
	}
	
	@Override
	public @NotNull String getAsString() {
		return this.data;
	}
	
	@Override
	public @NotNull String toString() {
		return Tag.super.getAsString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StringTag stringTag)) return false;
		
		return this.data.equals(stringTag.data);
	}
	
	@Override
	public int hashCode() {
		return this.data.hashCode();
	}
	
}
