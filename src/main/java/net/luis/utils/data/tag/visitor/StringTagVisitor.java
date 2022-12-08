package net.luis.utils.data.tag.visitor;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.data.tag.tags.CryptStringTag;
import net.luis.utils.data.tag.tags.EndTag;
import net.luis.utils.data.tag.tags.StringTag;
import net.luis.utils.data.tag.tags.collection.ListTag;
import net.luis.utils.data.tag.tags.collection.array.ByteArrayTag;
import net.luis.utils.data.tag.tags.collection.array.IntArrayTag;
import net.luis.utils.data.tag.tags.collection.array.LongArrayTag;
import net.luis.utils.data.tag.tags.numeric.ByteTag;
import net.luis.utils.data.tag.tags.numeric.DoubleTag;
import net.luis.utils.data.tag.tags.numeric.FloatTag;
import net.luis.utils.data.tag.tags.numeric.IntTag;
import net.luis.utils.data.tag.tags.numeric.LongTag;
import net.luis.utils.data.tag.tags.numeric.ShortTag;

public class StringTagVisitor implements TagVisitor {
	
	private final StringBuilder builder = new StringBuilder();
	
	public String visit(Tag tag) {
		tag.accept(this);
		return this.builder.toString();
	}
	
	@Override
	public void visitByte(ByteTag tag) {
		this.builder.append(tag.getAsNumber()).append("b");
	}
	
	@Override
	public void visitShort(ShortTag tag) {
		this.builder.append(tag.getAsNumber()).append("s");
	}
	
	@Override
	public void visitInt(IntTag tag) {
		this.builder.append(tag.getAsNumber());
	}
	
	@Override
	public void visitLong(LongTag tag) {
		this.builder.append(tag.getAsNumber()).append("l");
	}
	
	@Override
	public void visitFloat(FloatTag tag) {
		this.builder.append(tag.getAsNumber()).append("f");
	}
	
	@Override
	public void visitDouble(DoubleTag tag) {
		this.builder.append(tag.getAsNumber()).append("d");
	}
	
	@Override
	public void visitString(StringTag tag) {
		this.builder.append(tag.getAsString());
	}
	
	@Override
	public void visitCryptString(CryptStringTag tag) {
		this.builder.append(tag.getAsString());
	}
	
	@Override
	public void visitByteArray(ByteArrayTag tag) {
		this.builder.append("[B;");
		byte[] data = tag.getAsByteArray();
		for (int i = 0; i < data.length; ++i) {
			if (i != 0) {
				this.builder.append(',');
			}
			this.builder.append(data[i]);
		}
		this.builder.append(']');
	}
	
	@Override
	public void visitIntArray(IntArrayTag tag) {
		this.builder.append("[I;");
		int[] data = tag.getAsIntArray();
		for (int i = 0; i < data.length; ++i) {
			if (i != 0) {
				this.builder.append(',');
			}
			this.builder.append(data[i]);
		}
		this.builder.append(']');
	}
	
	@Override
	public void visitLongArray(LongArrayTag tag) {
		this.builder.append("[L;");
		long[] data = tag.getAsLongArray();
		for (int i = 0; i < data.length; ++i) {
			if (i != 0) {
				this.builder.append(',');
			}
			this.builder.append(data[i]);
		}
		this.builder.append(']');
	}
	
	@Override
	public void visitList(ListTag tag) {
		this.builder.append("[");
		for (int i = 0; i < tag.size(); i++) {
			if (i != 0) {
				this.builder.append(",");
			}
			this.builder.append(new StringTagVisitor().visit(tag.get(i)));
		}
		this.builder.append("]");
	}
	
	@Override
	public void visitCompound(CompoundTag tag) {
		this.builder.append("{");
		List<String> keys = Lists.newArrayList(tag.getAllKeys());
		Collections.sort(keys);
		for (String key : keys) {
			if (this.builder.length() != 1) {
				this.builder.append(',');
			}
			this.builder.append(key).append(":").append(new StringTagVisitor().visit(tag.get(key)));
		}
		this.builder.append("}");
	}
	
	@Override
	public void visitEnd(EndTag tag) {
		this.builder.append("END");
	}
	
}
