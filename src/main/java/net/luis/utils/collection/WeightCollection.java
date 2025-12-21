/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.collection;

import com.google.common.collect.Maps;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * A collection which returns a random element based on the weight of the elements.<br>
 * The higher the weight, the higher the chance that the element is returned.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the elements
 */
@SuppressWarnings("UsingRandomNextDoubleForRandomInteger")
public class WeightCollection<T> {
	
	/**
	 * The internal map.
	 */
	private final NavigableMap<Integer, T> map;
	/**
	 * Map to track individual weights of elements.
	 */
	private final Map<T, Integer> weights;
	/**
	 * The random number generator.
	 */
	private final Random rng;
	/**
	 * The total weight of the collection.
	 */
	private int total;
	
	/**
	 * Constructs a new empty weight collection.<br>
	 */
	public WeightCollection() {
		this(new Random());
	}
	
	/**
	 * Constructs a new empty weight collection with the given random number generator.<br>
	 *
	 * @param rng The random number generator
	 * @throws NullPointerException If the random number generator is null
	 */
	public WeightCollection(@NonNull Random rng) {
		this.map = Maps.newTreeMap();
		this.weights = Maps.newHashMap();
		this.rng = Objects.requireNonNull(rng, "Random must not be null");
	}
	
	/**
	 * Adds the given value with the given weight to the collection.<br>
	 *
	 * @param weight The weight of the value
	 * @param value The value to add
	 * @throws NullPointerException If the value is null
	 * @throws IllegalArgumentException If the weight is less than or equal to
	 */
	public void add(int weight, @NonNull T value) {
		Objects.requireNonNull(value, "Value must not be null");
		if (0 >= weight) {
			throw new IllegalArgumentException("The weight must be greater than 0, but it is " + weight);
		}
		this.total += weight;
		this.map.put(this.total, value);
		this.weights.put(value, weight);
	}
	
	/**
	 * Removes the given value from the collection.<br>
	 *
	 * @param value The value to remove
	 * @return True, if the value was removed, otherwise false
	 */
	public boolean remove(@Nullable T value) {
		if (value == null || !this.weights.containsKey(value)) {
			return false;
		}
		
		this.weights.remove(value);
		
		this.map.clear();
		this.total = 0;
		
		for (Map.Entry<T, Integer> entry : this.weights.entrySet()) {
			this.total += entry.getValue();
			this.map.put(this.total, entry.getKey());
		}
		
		return true;
	}
	
	/**
	 * Checks if the collection contains the given value.<br>
	 *
	 * @param value The value to check
	 * @return True, if the collection contains the value, otherwise false
	 */
	public boolean contains(@Nullable T value) {
		return this.map.containsValue(value);
	}
	
	/**
	 * Removes all values from the collection.<br>
	 */
	public void clear() {
		this.map.clear();
		this.weights.clear();
		this.total = 0;
	}
	
	/**
	 * Gets an element from the collection based on the weight of the elements.<br>
	 * Elements with a higher weight have a higher chance to be returned.<br>
	 *
	 * @return An weighted random element
	 */
	public T next() {
		if (this.isEmpty()) {
			throw new IllegalStateException("The collection is empty, there is no next value");
		}
		return this.map.higherEntry((int) (this.rng.nextDouble() * this.total)).getValue();
	}
	
	/**
	 * Checks if the collection is empty.<br>
	 * @return True, if the collection is empty, otherwise false
	 */
	public boolean isEmpty() {
		return this.map.isEmpty();
	}
	
	/**
	 * Returns the number of elements in the collection.<br>
	 * @return The size of the collection
	 */
	public int getSize() {
		return this.map.size();
	}
	
	/**
	 * Returns the total weight of the collection.<br>
	 * @return The total weight
	 */
	public int getTotal() {
		return this.total;
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
		return this.weights.equals(that.weights);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.map, this.weights);
	}
	//endregion
}
