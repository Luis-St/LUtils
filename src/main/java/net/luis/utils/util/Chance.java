package net.luis.utils.util;

import net.luis.utils.math.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

/**
 * A class that represents a chance of something happening.<br>
 * The chance is a double between 0.0 and 1.0, where 0.0 is 0% and 1.0 is 100%.<br>
 *
 * @author Luis-St
 */
public class Chance {
	
	/**
	 * The random number generator used to generate the chance.<br>
	 */
	private final Random rng = new Random();
	/**
	 * The chance value of something happens.<br>
	 */
	private final double chance;
	
	/**
	 * Constructs a new {@link Chance} with the given chance.<br>
	 * The chance will be clamped between 0.0 and 1.0.<br>
	 * @param chance The chance value as a double
	 */
	private Chance(double chance) {
		this.chance = Mth.clamp(chance, 0.0, 1.0);
	}
	
	/**
	 * Creates a new {@link Chance} with the given chance.<br>
	 * The chance will be clamped between 0.0 and 1.0.<br>
	 * @param chance The chance as a double
	 * @return The created chance
	 */
	public static @NotNull Chance of(double chance) {
		return new Chance(chance);
	}
	
	/**
	 * Sets the seed of the random number generator.<br>
	 * @param seed The seed
	 */
	public void setSeed(long seed) {
		this.rng.setSeed(seed);
	}
	
	/**
	 * @return If the chance is always true, the chance is 1.0 or above
	 */
	public boolean isTrue() {
		return this.chance >= 1.0;
	}
	
	/**
	 * @return If the chance is always false, the chance is 0.0 or below
	 */
	public boolean isFalse() {
		return 0.0 >= this.chance;
	}
	
	/**
	 * Gets the result of the chance.<br>
	 * <p>
	 *     If {@link #isTrue()} this will always return true.<br>
	 *     If {@link #isFalse()} this will always return false.<br>
	 *     Otherwise, true if the random number generator returns a number below the chance.<br>
	 * </p>
	 * @return The result of the chance
	 */
	public boolean result() {
		if (this.isTrue()) {
			return true;
		} else if (this.isFalse()) {
			return false;
		} else {
			return this.chance > this.rng.nextDouble();
		}
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Chance that)) return false;
		
		if (Double.compare(that.chance, this.chance) != 0) return false;
		return this.rng.equals(that.rng);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.rng, this.chance);
	}
	
	@Override
	public String toString() {
		return "Chance{chance=" + this.chance + "}";
	}
	//endregion
}
