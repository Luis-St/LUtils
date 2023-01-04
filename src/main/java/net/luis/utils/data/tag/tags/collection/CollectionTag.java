package net.luis.utils.data.tag.tags.collection;

import net.luis.utils.data.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;

/**
 *
 * @author Luis-st
 *
 */

public abstract class CollectionTag<T extends Tag> extends AbstractList<T> implements Tag {
	
	
	public abstract @NotNull T set(int index, T tag);
	
	public abstract void add(int index, T tag);
	
	public abstract @Nullable T remove(int index);
	
	public abstract boolean setTag(int index, Tag tag);
	
	public abstract boolean addTag(int index, Tag tag);
	
	public abstract byte getElementType();
	
	@Override
	public String toString() {
		return this.getAsString();
	}
	
}
