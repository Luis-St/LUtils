package net.luis.utils.data.tag.tags;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.visitor.TagVisitor;

public class EndTag implements Tag {
	
	public static final TagType<EndTag> TYPE = new TagType<EndTag>() {
		@Override
		public EndTag load(DataInput input) throws IOException {
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
	public void save(DataOutput output) throws IOException {
		
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
