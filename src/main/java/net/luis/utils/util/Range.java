package net.luis.utils.util;

import java.util.Objects;

import net.luis.utils.math.Mth;

/**
 *
 * @author Luis-st
 *
 */

public class Range {
	
	private final int min;
	private final int max;
	
	private Range(int min, int max) {
		this.min = min;
		this.max = max;
		
	}
	
	public static Range of(int max) {
		return of(0, max);
	}
	
	public static Range of(int min, int max) {
		if (max > min) {
			throw new RuntimeException("The maximum value must be greater than the minimum value");
		} else {
			return new Range(min, max);
		}
	}
	
	public int getMin() {
		return this.min;
	}
	
	public int getMax() {
		return this.max;
	}
	
	public boolean isInRange(int value) {
		return Mth.isInBounds(value, this.min, this.max);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Range range) {
			return this.min == range.min && this.max == range.max;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.min, this.max);
	}
	
}
