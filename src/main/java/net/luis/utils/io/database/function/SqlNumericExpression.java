package net.luis.utils.io.database.function;

import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlNumericExpression<T> extends SqlOrderableExpression<T> {
	
	static @NonNull SqlNumericExpression<Double> random() {
		return null;
	}
	
	static @NonNull SqlNumericExpression<Double> pi() {
		return null;
	}
	
	@NonNull SqlNumericExpression<T> negate();
	
	@NonNull SqlNumericExpression<T> sum();
	
	@NonNull SqlNumericExpression<T> average();
	
	@NonNull SqlNumericExpression<T> abs();
	
	@NonNull SqlNumericExpression<T> round();
	
	@NonNull SqlNumericExpression<T> round(int precision);
	
	@NonNull SqlNumericExpression<T> ceil();
	
	@NonNull SqlNumericExpression<T> floor();
	
	@NonNull SqlNumericExpression<T> truncate();
	
	@NonNull SqlNumericExpression<T> mod(@NonNull Number divisor);
	
	@NonNull SqlNumericExpression<T> pow(@NonNull Number exponent);
	
	@NonNull SqlNumericExpression<T> sqrt();
	
	@NonNull SqlNumericExpression<T> sign();
	
	@NonNull SqlNumericExpression<T> exp();
	
	@NonNull SqlNumericExpression<T> log2();
	
	@NonNull SqlNumericExpression<T> ln();
	
	@NonNull SqlNumericExpression<T> log10();
	
	@NonNull SqlNumericExpression<T> sin();
	
	@NonNull SqlNumericExpression<T> cos();
	
	@NonNull SqlNumericExpression<T> tan();
	
	@NonNull SqlNumericExpression<T> asin();
	
	@NonNull SqlNumericExpression<T> acos();
	
	@NonNull SqlNumericExpression<T> atan();
	
	@NonNull SqlNumericExpression<T> atan2(@NonNull Number x);
	
	@NonNull SqlNumericExpression<T> radians();
	
	@NonNull SqlNumericExpression<T> degrees();
	
	@NonNull SqlNumericExpression<T> bitwiseAnd(@NonNull Number other);
	
	@NonNull SqlNumericExpression<T> bitwiseOr(@NonNull Number other);
	
	@NonNull SqlNumericExpression<T> bitwiseXor(@NonNull Number other);
	
	@NonNull SqlNumericExpression<T> bitwiseNot();
}
