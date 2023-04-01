package net.luis.utils.collection;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.NavigableMap;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Luis-St
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
	
	public void add(int weight, @NotNull T value) {
		if (0 >= weight) {
			throw new IllegalArgumentException("The weight must be greater than 0, but it is " + weight);
		}
		this.total += weight;
		this.map.put(this.total, Objects.requireNonNull(value));
	}
	
	public T next() {
		if (this.isEmpty()) {
			throw new RuntimeException("The collection is empty, there is no next value");
		}
		return this.map.higherEntry((int) (this.rng.nextDouble() * this.total)).getValue();
	}
	
	public boolean isEmpty() {
		return this.map.isEmpty();
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return this.map.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof WeightCollection<?> that)) return false;
		
		if (this.total != that.total) return false;
		if (!this.map.equals(that.map)) return false;
		return this.rng.equals(that.rng);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.map, this.rng, this.total);
	}
	//endregion
	
}
