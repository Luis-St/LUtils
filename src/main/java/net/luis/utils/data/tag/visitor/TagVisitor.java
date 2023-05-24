package net.luis.utils.data.tag.visitor;

import net.luis.utils.annotation.Ignored.Always;
import net.luis.utils.data.tag.tags.*;
import net.luis.utils.data.tag.tags.collection.ListTag;
import net.luis.utils.data.tag.tags.collection.array.IntArrayTag;
import net.luis.utils.data.tag.tags.collection.array.LongArrayTag;
import net.luis.utils.data.tag.tags.numeric.*;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public interface TagVisitor {
	
	void visitInt(@NotNull IntTag tag);
	
	void visitLong(@NotNull LongTag tag);
	
	void visitDouble(@NotNull DoubleTag tag);
	
	void visitString(@NotNull StringTag tag);
	
	void visitIntArray(@NotNull IntArrayTag tag);
	
	void visitLongArray(@NotNull LongArrayTag tag);
	
	void visitList(@NotNull ListTag tag);
	
	void visitCompound(@NotNull CompoundTag tag);
	
	void visitEnd(@Always EndTag tag);
}
