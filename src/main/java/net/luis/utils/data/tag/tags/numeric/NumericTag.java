package net.luis.utils.data.tag.tags.numeric;

import net.luis.utils.data.tag.Tag;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public abstract class NumericTag implements Tag {
	
	public abstract byte getAsByte();
	
	public abstract short getAsShort();
	
	public abstract int getAsInt();
	
	public abstract long getAsLong();
	
	public abstract float getAsFloat();
	
	public abstract double getAsDouble();
	
	public abstract @NotNull Number getAsNumber();
	
	@Override
	public @NotNull String toString() {
		return this.getAsString();
	}
	
}
