package net.luis.utils.data.tag.tags;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.visitor.TagVisitor;

public class StringTag implements Tag {
	
	public static final TagType<StringTag> TYPE = new TagType<StringTag>() {
		@Override
		public StringTag load(DataInput input) throws LoadTagException {
			try {
				return StringTag.valueOf(input.readUTF());
			} catch (IOException e) {
				throw new LoadTagException(e);
			}
		}
		
		@Override
		public String getName() {
			return "string_tag";
		}
		
		@Override
		public String getVisitorName() {
			return "StringTag";
		}
	};
	public static final StringTag EMPTY = new StringTag("");
	
	private final String data;
	
	private StringTag(String data) {
		this.data = data;
	}
	
	public static StringTag valueOf(String data) {
		if (data == null) {
			return EMPTY;
		}
		return data.isEmpty() ? EMPTY : new StringTag(data);
	}
	
	@Override
	public void save(DataOutput output) throws SaveTagException {
		try {
			output.writeUTF(this.data);
		} catch (IOException e) {
			throw new SaveTagException(e);
		}
	}
	
	@Override
	public byte getId() {
		return STRING_TAG;
	}
	
	@Override
	public TagType<StringTag> getType() {
		return TYPE;
	}
	
	@Override
	public StringTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitString(this);
	}
	
	@Override
	public String getAsString() {
		return this.data;
	}
	
	@Override
	public String toString() {
		return Tag.super.getAsString();
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return object instanceof StringTag tag && Objects.equals(this.data, tag.data);
		}
	}
	
	@Override
	public int hashCode() {
		return this.data.hashCode();
	}
	
}
