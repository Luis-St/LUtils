package net.luis.utils.data.tag.tags.numeric;

import net.luis.utils.data.tag.Tag;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public abstract class NumericTag implements Tag {
	
	//region Getters
	public abstract int getAsInt();
	
	public abstract long getAsLong();
	
	public abstract double getAsDouble();
	
	public abstract @NotNull Number getAsNumber();
	//endregion
	
	@Override
	public String toString() {
		return this.getAsString();
	}
	
}
