package net.luis.utils.io.codec.constraint.temporal;

/**
 *
 * @author Luis-St
 *
 */

public enum WeekType {
	
	WEEK_OF_MONTH(4),
	WEEK_OF_YEAR(52);
	
	private final int last;
	
	WeekType(int last) {
		this.last = last;
	}
	
	public int getLast() {
		return this.last;
	}
}
