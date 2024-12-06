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
 * Implementation of the Extended Euclidean Algorithm (EEA).<br>
 * The EEA is an extension of the Euclidean Algorithm and is used to find the greatest common divisor (GCD) of two integers.<br>
 *
 * @author Luis-St
 */
public class ExtendedEuclideanAlgorithm {
	
	/**
	 * The first coefficients of the EEA.<br>
	 */
	private final List<BigInteger> firstCoefficients = new ArrayList<>();
	/**
	 * The second coefficients of the EEA.<br>
	 */
	private final List<BigInteger> secondCoefficients = new ArrayList<>();
	/**
	 * The initial value of the EEA.<br>
	 */
	private final BigInteger initialValue;
	/**
	 * The initial divisor of the EEA.<br>
	 */
	private final BigInteger initialDivisor;
	/**
	 * The current value of the EEA.<br>
	 */
	private BigInteger value;
	/**
	 * The current divisor of the EEA.<br>
	 */
	private BigInteger divisor;
	/**
	 * The current remainder of the EEA.<br>
	 */
	private BigInteger remainder;
	/**
	 * The current quotient of the EEA.<br>
	 */
	private BigInteger quotient;
	/**
	 * The number of steps the EEA has executed.<br>
	 */
	private int step;
	
	/**
	 * Constructs a new EEA with the given value and divisor.<br>
	 * @param value The value of the EEA
	 * @param divisor The divisor of the EEA
	 * @throws IllegalArgumentException If the value or divisor is 0
	 */
	public ExtendedEuclideanAlgorithm(int value, int divisor) {
		this(BigInteger.valueOf(value), BigInteger.valueOf(divisor));
	}
	
	/**
	 * Constructs a new EEA with the given value and divisor.<br>
	 * @param value The value of the EEA
	 * @param divisor The divisor of the EEA
	 * @throws IllegalArgumentException If the value or divisor is 0
	 */
	public ExtendedEuclideanAlgorithm(long value, long divisor) {
		this(BigInteger.valueOf(value), BigInteger.valueOf(divisor));
	}
	
	/**
	 * Constructs a new EEA with the given value and divisor.<br>
	 * @param value The value of the EEA
	 * @param divisor The divisor of the EEA
	 * @throws IllegalArgumentException If the value or divisor is 0
	 */
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
	
	/**
	 * Calculates the greatest common divisor (GCD) of the given value and divisor using the EEA.<br>
	 * @param value The value to calculate the GCD for
	 * @param divisor The divisor to calculate the GCD for
	 * @return The GCD of the given value and divisor
	 * @throws IllegalArgumentException If the value or divisor is 0
	 */
	public static int gcd(int value, int divisor) {
		return gcd(BigInteger.valueOf(value), BigInteger.valueOf(divisor)).intValueExact();
	}
	
	/**
	 * Calculates the greatest common divisor (GCD) of the given value and divisor using the EEA.<br>
	 * @param value The value to calculate the GCD for
	 * @param divisor The divisor to calculate the GCD for
	 * @return The GCD of the given value and divisor
	 * @throws IllegalArgumentException If the value or divisor is 0
	 */
	public static long gcd(long value, long divisor) {
		return gcd(BigInteger.valueOf(value), BigInteger.valueOf(divisor)).longValueExact();
	}
	
	/**
	 * Calculates the greatest common divisor (GCD) of the given value and divisor using the EEA.<br>
	 * @param value The value to calculate the GCD for
	 * @param divisor The divisor to calculate the GCD for
	 * @return The GCD of the given value and divisor
	 * @throws IllegalArgumentException If the value or divisor is 0
	 */
	public static @NotNull BigInteger gcd(@NotNull BigInteger value, @NotNull BigInteger divisor) {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(value, divisor);
		eea.execute();
		return eea.getDivisor();
	}
	
	//region Getter
	
	/**
	 * Checks if the EEA is complete.<br>
	 * @return True, if the EEA is complete, otherwise false
	 */
	public boolean isComplete() {
		return this.remainder.equals(ZERO);
	}
	
	/**
	 * Returns the initial value of the EEA.<br>
	 * @return The initial value
	 */
	public @NotNull BigInteger getInitialValue() {
		return this.initialValue;
	}
	
	/**
	 * Returns the initial divisor of the EEA.<br>
	 * @return The initial divisor
	 */
	public @NotNull BigInteger getInitialDivisor() {
		return this.initialDivisor;
	}
	
	/**
	 * Returns the current value of the EEA.<br>
	 * @return The current value
	 */
	public @NotNull BigInteger getValue() {
		return this.value;
	}
	
	/**
	 * Returns the current divisor of the EEA.<br>
	 * @return The current divisor
	 */
	public @NotNull BigInteger getDivisor() {
		return this.divisor;
	}
	
	/**
	 * Returns the current remainder of the EEA.<br>
	 * @return The current remainder
	 */
	public @NotNull BigInteger getRemainder() {
		return this.remainder;
	}
	
	/**
	 * Returns the current quotient of the EEA.<br>
	 * @return The current quotient
	 */
	public @NotNull BigInteger getQuotient() {
		return this.quotient;
	}
	
	/**
	 * Returns the number of steps the EEA has executed.<br>
	 * @return The number of steps
	 */
	public int getStep() {
		return this.step;
	}
	
	/**
	 * Returns the first coefficient of the EEA.<br>
	 * @return The first coefficient
	 */
	public @NotNull BigInteger getFirstCoefficient() {
		return this.getFirstCoefficient(1);
	}
	
	/**
	 * Returns the second coefficient of the EEA.<br>
	 * @return The second coefficient
	 */
	public @NotNull BigInteger getSecondCoefficient() {
		return this.getSecondCoefficient(1);
	}
	//endregion
	
	/**
	 * Executes the EEA for a single step.<br>
	 */
	public void execute() {
		this.execute(1);
	}
	
	/**
	 * Executes the EEA for the given number of steps.<br>
	 * If the EEA is already completed the method will return immediately.<br>
	 * @param steps The number of steps to execute
	 */
	public void execute(int steps) {
		if (this.isComplete() || steps <= 0) {
			return;
		}
		for (int i = 0; i < steps; i++) {
			this.executeStep();
		}
	}
	
	/**
	 * Executes the EEA until it is complete.<br>
	 */
	public void executeUntilComplete() {
		while (!this.isComplete()) {
			this.executeStep();
		}
	}
	
	//region Helper methods
	
	/**
	 * Gets the first coefficient of the EEA.<br>
	 * The number of steps is subtracted from the size of the first coefficients list to get the correct coefficient.<br>
	 * @param step The number of steps to go back
	 * @return The first coefficient of the EEA
	 */
	private @NotNull BigInteger getFirstCoefficient(int step) {
		return this.firstCoefficients.get(this.firstCoefficients.size() - Math.abs(step));
	}
	
	/**
	 * Gets the second coefficient of the EEA.<br>
	 * The number of steps is subtracted from the size of the second coefficients list to get the correct coefficient.<br>
	 * @param step The number of steps to go back
	 * @return The second coefficient of the EEA
	 */
	private @NotNull BigInteger getSecondCoefficient(int step) {
		return this.secondCoefficients.get(this.secondCoefficients.size() - Math.abs(step));
	}
	
	/**
	 * Executes a single step of the EEA.<br>
	 * The values of the EEA are recalculated.<br>
	 * @see #update(BigInteger, BigInteger, BigInteger, BigInteger)
	 */
	private void executeStep() {
		BigInteger base = this.divisor;
		BigInteger divisor = this.remainder;
		BigInteger remainder = base.mod(divisor);
		BigInteger quotient = base.divide(divisor);
		this.update(base, divisor, remainder, quotient);
	}
	
	/**
	 * Updates the values of the EEA.<br>
	 * The first and second coefficients are calculated and added to their respective lists.<br>
	 * The value, divisor, remainder, quotient and step are updated.<br>
	 * @param value The new value
	 * @param divisor The new divisor
	 * @param remainder The new remainder
	 * @param quotient The new quotient
	 */
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
