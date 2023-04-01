package net.luis.utils.data.tag.tags.collection;

import net.luis.utils.data.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractList;

/**
 *
 * @author Luis-St
 *
 */

public abstract class CollectionTag<T extends Tag> extends AbstractList<T> implements Tag {
	
	public abstract T remove(int index);
	
	//region Setters
	public abstract T set(int index, @NotNull T tag);
	
	public abstract boolean setTag(int index, Tag tag);
	//endregion
	
	//region Setters
	public abstract void add(int index, @NotNull T tag);
	
	public abstract boolean addTag(int index, Tag tag);
	//endregion
	
	public abstract byte getElementType();
	
	@Override
	public String toString() {
		return this.getAsString();
	}
	
}
