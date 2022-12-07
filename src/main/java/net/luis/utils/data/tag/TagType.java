package net.luis.utils.data.tag;

import java.io.DataInput;

import net.luis.utils.data.tag.exception.InvalidTagException;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.tags.EndTag;

public interface TagType<T extends Tag> {
	
	T load(DataInput input) throws LoadTagException;
	
	String getName();
	
	String getVisitorName();
	
	default boolean isValue() {
		return false;
	}
	
	static TagType<EndTag> createInvalid(int id) {
		return new TagType<EndTag>() {
			@Override
			public EndTag load(DataInput input) throws LoadTagException {
				throw new InvalidTagException(id);
			}
			
			@Override
			public String getName() {
				return "INVALID[" + id + "]";
			}
			
			@Override
			public String getVisitorName() {
				return "INVALID_" + id;
			}
		};
	}
	
}
