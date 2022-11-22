package net.luis.utils.collection;

import java.util.NavigableMap;
import java.util.Random;

import com.google.common.collect.Maps;

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
			throw new IllegalArgumentException("The weight must be larger that 0 but it is " + weight);
		}
		this.total += weight;
		this.map.put(this.total, value);
	}
	
	public T next() {
		int value = (int) (this.rng.nextDouble() * this.total);
		return this.map.higherEntry(value).getValue();
	}
	
	public boolean isEmpty() {
		return this.map.isEmpty();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof WeightCollection<?> collection) {
			if (!this.map.equals(collection.map)) {
				return false;
			} else if (!this.rng.equals(collection.rng)) {
				return false;
			} else {
				return this.total == collection.total;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.map.toString();
	}
	
}
