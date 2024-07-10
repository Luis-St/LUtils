/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.math.algorithm;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class ExtendedEuclideanAlgorithm {
	
	private final List<Long> firstCoefficients = new ArrayList<>();
	private final List<Long> secondCoefficients = new ArrayList<>();
	private final long initialValue;
	private final long initialDivisor;
	private long value;
	private long divisor;
	private long remainder;
	private long quotient;
	private int step;
	
	public ExtendedEuclideanAlgorithm(long value, long divisor) {
		//region Validation
		if (value == 0) {
			throw new IllegalArgumentException("The value must not be 0");
		}
		if (divisor == 0) {
			throw new IllegalArgumentException("The divisor must not be 0");
		}
		//endregion
		this.initialValue = value;
		this.initialDivisor = divisor;
		this.value = value;
		this.divisor = divisor;
		this.remainder = value % divisor;
		this.quotient = value / divisor;
		this.firstCoefficients.addAll(List.of(1L, 0L));
		this.secondCoefficients.addAll(List.of(0L, 1L));
	}
	
	public static long gcd(long value, long divisor) {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(value, divisor);
		eea.execute();
		return eea.getDivisor();
	}
	
	//region Getter
	public boolean isComplete() {
		return this.remainder == 0;
	}
	
	public long getInitialValue() {
		return this.initialValue;
	}
	
	public long getInitialDivisor() {
		return this.initialDivisor;
	}
	
	public long getValue() {
		return this.value;
	}
	
	public long getDivisor() {
		return this.divisor;
	}
	
	public long getRemainder() {
		return this.remainder;
	}
	
	public long getQuotient() {
		return this.quotient;
	}
	
	public int getStep() {
		return this.step;
	}
	
	public long getFirstCoefficient() {
		return this.getFirstCoefficient(1);
	}
	
	public long getSecondCoefficient() {
		return this.getSecondCoefficient(1);
	}
	//endregion
	
	public void execute() {
		while (!this.isComplete()) {
			this.executeStep();
		}
	}
	
	public void execute(int steps) {
		for (int i = 0; i < steps; i++) {
			this.executeStep();
		}
	}
	
	//region Helper methods
	private long getFirstCoefficient(int step) {
		return this.firstCoefficients.get(this.firstCoefficients.size() - Math.abs(step));
	}
	
	private long getSecondCoefficient(int step) {
		return this.secondCoefficients.get(this.secondCoefficients.size() - Math.abs(step));
	}
	
	private void executeStep() {
		long base = this.divisor;
		long divisor = this.remainder;
		
		long remainder = base % divisor;
		long quotient = base / divisor;
		
		this.update(base, divisor, remainder, quotient);
	}
	
	private void update(long value, long divisor, long remainder, long quotient) {
		long s = this.getFirstCoefficient(-2) - (this.quotient * this.getFirstCoefficient(-1));
		long t = this.getSecondCoefficient(-2) - (this.quotient * this.getSecondCoefficient(-1));
		this.firstCoefficients.add(s);
		this.secondCoefficients.add(t);
		
		this.value = value;
		this.divisor = divisor;
		this.remainder = remainder;
		this.quotient = quotient;
		this.step++;
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof ExtendedEuclideanAlgorithm that)) return false;
		
		if (this.initialValue != that.initialValue) return false;
		if (this.initialDivisor != that.initialDivisor) return false;
		if (this.value != that.value) return false;
		if (this.divisor != that.divisor) return false;
		if (this.remainder != that.remainder) return false;
		if (this.quotient != that.quotient) return false;
		if (this.step != that.step) return false;
		if (!this.firstCoefficients.equals(that.firstCoefficients)) return false;
		return this.secondCoefficients.equals(that.secondCoefficients);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.initialValue, this.initialDivisor);
	}
	
	@Override
	public String toString() {
		return "EEA(" + this.initialValue + ", " + this.initialDivisor + ") = (" + this.value + "v, " + this.divisor + "d, " + this.remainder + "r, " + this.quotient + "q, " + this.getFirstCoefficient() + "s, " + this.getSecondCoefficient() + "t)";
	}
	//endregion
}
