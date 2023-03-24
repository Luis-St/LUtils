package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Luis-St
 *
 */

public class Chance {
	
	private final Random rng = new Random();
	private final double chance;
	
	private Chance(double chance) {
		this.chance = chance;
	}
	
	public static @NotNull Chance of(double chance) {
		return new Chance(chance);
	}
	
	public void setSeed(long seed) {
		this.rng.setSeed(seed);
	}
	
	public boolean isTrue() {
		return this.chance >= 1.0;
	}
	
	public boolean isFalse() {
		return 0.0 >= this.chance;
	}
	
	public boolean result() {
		if (this.isTrue()) {
			return true;
		} else if (this.isFalse()) {
			return false;
		} else {
			return this.chance > this.rng.nextDouble();
		}
	}
	
	@Override
	public @NotNull String toString() {
		return ToString.toString(this, "rng");
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof Chance that)) return false;
		
		if (Double.compare(that.chance, this.chance) != 0) return false;
		return this.rng.equals(that.rng);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.rng, this.chance);
	}
	
}
