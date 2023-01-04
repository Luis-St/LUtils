package net.luis.utils.data.tag.tags;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.visitor.TagVisitor;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;

/**
 *
 * @author Luis-st
 *
 */

public class EndTag implements Tag {
	
	public static final EndTag INSTANCE = new EndTag();
	public static final TagType<EndTag> TYPE = new TagType<>() {
		@Override
		public @NotNull EndTag load(DataInput input) throws LoadTagException {
			return INSTANCE;
		}
		
		@Override
		public @NotNull String getName() {
			return "end_tag";
		}
		
		@Override
		public @NotNull String getVisitorName() {
			return "EndTag";
		}
	};
	
	private EndTag() {
		
	}
	
	@Override
	public void save(DataOutput output) throws SaveTagException {
		throw new SaveTagException("Cannot save a tag of the type" + this.getType().getVisitorName());
	}
	
	@Override
	public byte getId() {
		return END_TAG;
	}
	
	@Override
	public @NotNull TagType<EndTag> getType() {
		return TYPE;
	}
	
	@Override
	public @NotNull EndTag copy() {
		return this;
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitEnd(this);
	}
	
	@Override
	public String toString() {
		return this.getAsString();
	}
	
}
