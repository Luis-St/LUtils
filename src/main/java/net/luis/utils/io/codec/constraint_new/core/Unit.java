package net.luis.utils.io.codec.constraint_new.core;

/**
 * A singleton class representing a unit value.<br>
 * Used by the constraint configs to represent the presence of a constraint without any associated data.<br>
 *
 * @author Luis-St
 */
public final class Unit {
	
	/**
	 * The singleton instance of the unit value.<br>
	 */
	public static final Unit INSTANCE = new Unit();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private Unit() {}
	
	@Override
	public String toString() {
		return "Unit";
	}
}
