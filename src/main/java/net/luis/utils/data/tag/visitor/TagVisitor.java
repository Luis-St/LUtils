package net.luis.utils.data.tag.visitor;

import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.data.tag.tags.EndTag;
import net.luis.utils.data.tag.tags.StringTag;
import net.luis.utils.data.tag.tags.collection.ListTag;
import net.luis.utils.data.tag.tags.collection.array.IntArrayTag;
import net.luis.utils.data.tag.tags.collection.array.LongArrayTag;
import net.luis.utils.data.tag.tags.numeric.DoubleTag;
import net.luis.utils.data.tag.tags.numeric.FloatTag;
import net.luis.utils.data.tag.tags.numeric.IntTag;
import net.luis.utils.data.tag.tags.numeric.LongTag;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public interface TagVisitor {
	
	void visitInt(@NotNull IntTag tag);
	
	void visitLong(@NotNull LongTag tag);
	
	@Deprecated
	void visitFloat(@NotNull FloatTag tag);
	
	void visitDouble(@NotNull DoubleTag tag);
	
	void visitString(@NotNull StringTag tag);
	
	void visitIntArray(@NotNull IntArrayTag tag);
	
	void visitLongArray(@NotNull LongArrayTag tag);
	
	void visitList(@NotNull ListTag tag);
	
	void visitCompound(@NotNull CompoundTag tag);
	
	void visitEnd(@NotNull EndTag tag);
	
}
