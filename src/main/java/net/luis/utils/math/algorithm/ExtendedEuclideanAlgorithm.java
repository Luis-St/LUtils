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

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.*;

import static java.math.BigInteger.*;

/**
 *
 * @author Luis-St
 *
 */

public class ExtendedEuclideanAlgorithm {
	
	private final List<BigInteger> firstCoefficients = new ArrayList<>();
	private final List<BigInteger> secondCoefficients = new ArrayList<>();
	private final BigInteger initialValue;
	private final BigInteger initialDivisor;
	private BigInteger value;
	private BigInteger divisor;
	private BigInteger remainder;
	private BigInteger quotient;
	private int step;
	
	public ExtendedEuclideanAlgorithm(int value, int divisor) {
		this(BigInteger.valueOf(value), BigInteger.valueOf(divisor));
	}
	
	public ExtendedEuclideanAlgorithm(long value, long divisor) {
		this(BigInteger.valueOf(value), BigInteger.valueOf(divisor));
	}
	
	public ExtendedEuclideanAlgorithm(@NotNull BigInteger value, @NotNull BigInteger divisor) {
		//region Validation
		if (value.equals(ZERO)) {
			throw new IllegalArgumentException("The value must not be 0");
		}
		if (divisor.equals(ZERO)) {
			throw new IllegalArgumentException("The divisor must not be 0");
		}
		//endregion
		this.initialValue = value;
		this.initialDivisor = divisor;
		this.value = value;
		this.divisor = divisor;
		this.remainder = value.mod(divisor);
		this.quotient = value.divide(divisor);
		this.firstCoefficients.addAll(List.of(ONE, ZERO));
		this.secondCoefficients.addAll(List.of(ZERO, ONE));
	}
	
	public static int gcd(int value, int divisor) {
		return gcd(BigInteger.valueOf(value), BigInteger.valueOf(divisor)).intValueExact();
	}
	
	public static long gcd(long value, long divisor) {
		return gcd(BigInteger.valueOf(value), BigInteger.valueOf(divisor)).longValueExact();
	}
	
	public static @NotNull BigInteger gcd(@NotNull BigInteger value, @NotNull BigInteger divisor) {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(value, divisor);
		eea.execute();
		return eea.getDivisor();
	}
	
	//region Getter
	public boolean isComplete() {
		return this.remainder.equals(ZERO);
	}
	
	public @NotNull BigInteger getInitialValue() {
		return this.initialValue;
	}
	
	public @NotNull BigInteger getInitialDivisor() {
		return this.initialDivisor;
	}
	
	public @NotNull BigInteger getValue() {
		return this.value;
	}
	
	public @NotNull BigInteger getDivisor() {
		return this.divisor;
	}
	
	public @NotNull BigInteger getRemainder() {
		return this.remainder;
	}
	
	public @NotNull BigInteger getQuotient() {
		return this.quotient;
	}
	
	public int getStep() {
		return this.step;
	}
	
	public @NotNull BigInteger getFirstCoefficient() {
		return this.getFirstCoefficient(1);
	}
	
	public @NotNull BigInteger getSecondCoefficient() {
		return this.getSecondCoefficient(1);
	}
	//endregion
	
	public void execute() {
		this.execute(1);
	}
	
	public void execute(int steps) {
		if (this.isComplete() || steps <= 0) {
			return;
		}
		for (int i = 0; i < steps; i++) {
			this.executeStep();
		}
	}
	
	public void executeUntilComplete() {
		while (!this.isComplete()) {
			this.executeStep();
		}
	}
	
	//region Helper methods
	private @NotNull BigInteger getFirstCoefficient(int step) {
		return this.firstCoefficients.get(this.firstCoefficients.size() - Math.abs(step));
	}
	
	private @NotNull BigInteger getSecondCoefficient(int step) {
		return this.secondCoefficients.get(this.secondCoefficients.size() - Math.abs(step));
	}
	
	private void executeStep() {
		BigInteger base = this.divisor;
		BigInteger divisor = this.remainder;
		BigInteger remainder = base.mod(divisor);
		BigInteger quotient = base.divide(divisor);
		this.update(base, divisor, remainder, quotient);
	}
	
	private void update(@NotNull BigInteger value, @NotNull BigInteger divisor, @NotNull BigInteger remainder, @NotNull BigInteger quotient) {
		BigInteger first = this.getFirstCoefficient(-2).subtract(this.quotient.multiply(this.getFirstCoefficient(-1)));
		BigInteger second = this.getSecondCoefficient(-2).subtract(this.quotient.multiply(this.getSecondCoefficient(-1)));
		this.firstCoefficients.add(first);
		this.secondCoefficients.add(second);
		
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
		
		if (!Objects.equals(this.initialValue, that.initialValue)) return false;
		if (!Objects.equals(this.initialDivisor, that.initialDivisor)) return false;
		if (!Objects.equals(this.value, that.value)) return false;
		if (!Objects.equals(this.divisor, that.divisor)) return false;
		if (!Objects.equals(this.remainder, that.remainder)) return false;
		if (!Objects.equals(this.quotient, that.quotient)) return false;
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
		return "EEA(" + this.initialValue + ", " + this.initialDivisor + ") = (" + this.value + "v, " + this.divisor + "d, " + this.remainder + "r, " + this.quotient + "q, " + this.getFirstCoefficient() + "fc, " + this.getSecondCoefficient() + "sc)";
	}
	//endregion
}
