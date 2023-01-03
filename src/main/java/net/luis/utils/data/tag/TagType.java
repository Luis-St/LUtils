package net.luis.utils.data.tag;

import net.luis.utils.data.tag.exception.InvalidTagException;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.tags.EndTag;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;

public interface TagType<T extends Tag> {
	
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
	
	@NotNull
	T load(DataInput input) throws LoadTagException;
	
	@NotNull
	String getName();
	
	@NotNull
	String getVisitorName();
	
	default boolean isValue() {
		return false;
	}
	
}
