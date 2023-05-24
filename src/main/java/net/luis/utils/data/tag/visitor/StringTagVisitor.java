package net.luis.utils.data.tag.visitor;

import com.google.common.collect.Lists;
import net.luis.utils.annotation.Ignored;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.data.tag.tags.EndTag;
import net.luis.utils.data.tag.tags.StringTag;
import net.luis.utils.data.tag.tags.collection.ListTag;
import net.luis.utils.data.tag.tags.collection.array.IntArrayTag;
import net.luis.utils.data.tag.tags.collection.array.LongArrayTag;
import net.luis.utils.data.tag.tags.numeric.DoubleTag;
import net.luis.utils.data.tag.tags.numeric.IntTag;
import net.luis.utils.data.tag.tags.numeric.LongTag;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class StringTagVisitor implements TagVisitor {
	
	private final StringBuilder builder = new StringBuilder();
	
	public String visit(@NotNull Tag tag) {
		tag.accept(this);
		return this.builder.toString();
	}
	
	@Override
	public void visitInt(@NotNull IntTag tag) {
		this.builder.append(tag.getAsNumber());
	}
	
	@Override
	public void visitLong(@NotNull LongTag tag) {
		this.builder.append(tag.getAsNumber()).append("L");
	}
	
	@Override
	public void visitDouble(@NotNull DoubleTag tag) {
		this.builder.append(tag.getAsNumber()).append("D");
	}
	
	@Override
	public void visitString(@NotNull StringTag tag) {
		this.builder.append(tag.getAsString());
	}
	
	@Override
	public void visitIntArray(@NotNull IntArrayTag tag) {
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
	public void visitLongArray(@NotNull LongArrayTag tag) {
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
	public void visitList(@NotNull ListTag tag) {
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
	public void visitCompound(@NotNull CompoundTag tag) {
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
	public void visitEnd(@Ignored.Always EndTag tag) {
		this.builder.append("END");
	}
	
}
