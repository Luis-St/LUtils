package net.luis.utils.collection;

import com.google.common.collect.Maps;
import net.luis.utils.util.Equals;
import org.jetbrains.annotations.NotNull;

import java.util.NavigableMap;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Luis-st
 *
 */

public class WeightCollection<T> {
	
	private final NavigableMap<Integer, T> map;
	private final Random rng;
	private int total = 0;
	
	public WeightCollection() {
		this(new Random());
	}
	
	public WeightCollection(Random rng) {
		this.map = Maps.newTreeMap();
		this.rng = rng;
	}
	
	public void add(int weight, T value) {
		if (0 >= weight) {
			throw new IllegalArgumentException("The weight must be greater than 0, but it is " + weight);
		}
		this.total += weight;
		this.map.put(this.total, Objects.requireNonNull(value));
	}
	
	public @NotNull T next() {
		if (this.isEmpty()) {
			throw new RuntimeException("The collection is empty, there is no next value");
		}
		int value = (int) (this.rng.nextDouble() * this.total);
		return this.map.higherEntry(value).getValue();
	}
	
	public boolean isEmpty() {
		return this.map.isEmpty();
	}
	
	@Override
	public String toString() {
		return this.map.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		return Equals.equals(this, object, "rng");
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.map, this.rng, this.total);
	}
	
}
