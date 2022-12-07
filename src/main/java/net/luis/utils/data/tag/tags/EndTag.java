package net.luis.utils.data.tag.tags;

import java.io.DataInput;
import java.io.DataOutput;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.visitor.TagVisitor;

public class EndTag implements Tag {
	
	public static final TagType<EndTag> TYPE = new TagType<EndTag>() {
		@Override
		public EndTag load(DataInput input) throws LoadTagException {
			return INSTANCE;
		}
		
		@Override
		public String getName() {
			return "end_tag";
		}
		
		@Override
		public String getVisitorName() {
			return "EndTag";
		}
	};
	public static final EndTag INSTANCE = new EndTag();
	
	private EndTag() {
		
	}
	
	@Override
	public void save(DataOutput outpt) throws SaveTagException {
		throw new SaveTagException("Cannot save a tag of the type" + this.getType().getVisitorName());
	}
	
	@Override
	public byte getId() {
		return END_TAG;
	}
	
	@Override
	public TagType<EndTag> getType() {
		return TYPE;
	}
	
	@Override
	public EndTag copy() {
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
