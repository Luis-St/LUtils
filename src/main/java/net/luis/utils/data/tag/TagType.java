package net.luis.utils.data.tag;

import net.luis.utils.data.tag.exception.InvalidTagException;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.tags.EndTag;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;

/**
 *
 * @author Luis-st
 *
 */

public interface TagType<T extends Tag> {
	
	static @NotNull TagType<EndTag> createInvalid(int id) {
		return new TagType<>() {
			@Override
			public @NotNull EndTag load(@NotNull DataInput input) throws LoadTagException {
				throw new InvalidTagException(id);
			}
			
			@Override
			public @NotNull String getName() {
				return "INVALID[" + id + "]";
			}
			
			@Override
			public @NotNull String getVisitorName() {
				return "INVALID_" + id;
			}
		};
	}
	
	@NotNull
	T load(@NotNull DataInput input) throws LoadTagException;
	
	@NotNull
	String getName();
	
	@NotNull
	String getVisitorName();
	
	default boolean isValue() {
		return false;
	}
	
}
