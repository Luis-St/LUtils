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
	private final Random random;
	private int total = 0;
	
	public WeightCollection() {
		this(new Random());
	}
	
	public WeightCollection(Random random) {
		this.map = Maps.newTreeMap();
		this.random = random;
	}
	
	public void add(int weight, T value) {
		if (0 >= weight) {
			throw new IllegalArgumentException("The weight must be larger that 0 but it is " + weight);
		}
		this.total += weight;
		this.map.put(this.total, value);
	}
	
	public T next() {
		int value = (int) (this.random.nextDouble() * this.total);
		return this.map.higherEntry(value).getValue();
	}
	
	public boolean isEmpty() {
		return this.map.isEmpty();
	}
	
}
