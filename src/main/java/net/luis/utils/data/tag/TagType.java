package net.luis.utils.data.tag;

import java.io.DataInput;
import java.io.IOException;

import net.luis.utils.data.tag.tags.EndTag;

public interface TagType<T extends Tag> {
	
	T load(DataInput input) throws IOException;
	
	String getName();
	
	String getVisitorName();
	
	default boolean isValue() {
		return false;
	}
	
	static TagType<EndTag> createInvalid(int id) {
		return new TagType<EndTag>() {
			@Override
			public EndTag load(DataInput input) throws IOException {
				throw new IOException("Invalid tag id: " + id);
			}
			
			@Override
			public String getName() {
				return "INVALID[" + id + "]";
			}
			
			@Override
			public String getVisitorName() {
				return "UNKNOWN_" + id;
			}
		};
	}
	
}
