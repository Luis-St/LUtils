/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.database.function.functions.numeric.bitwise;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.SqlNumericFunction;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents the SQL bitwise {@code XOR} function.<br>
 *
 * @author Luis-St
 *
 * @param firstOperand The first operand expression
 * @param secondOperand The second operand expression
 * @param type The sql type of the result
 *
 * @param <T> The numeric type of the result
 */

public record SqlBitwiseXorFunction<T extends Number>(
	@NonNull SqlExpression<T> firstOperand,
	@NonNull SqlExpression<? extends Number> secondOperand,
	@NonNull SqlType<T> type
) implements SqlNumericFunction<T> {
	
	/**
	 * Constructs a new bitwise XOR function inferring the sql type from the operand expressions.<br>
	 *
	 * @param firstOperand The first operand expression
	 * @param secondOperand The second operand expression
	 * @throws NullPointerException If the first or second operand is null
	 * @throws IllegalArgumentException If the operands have different types
	 */
	public SqlBitwiseXorFunction(@NonNull SqlExpression<T> firstOperand, @NonNull SqlExpression<T> secondOperand) {
		Objects.requireNonNull(firstOperand, "Sql first operand expression must not be null");
		Objects.requireNonNull(secondOperand, "Sql second operand expression must not be null");
		
		if (!firstOperand.type().equals(secondOperand.type())) {
			throw new IllegalArgumentException("Sql first and second operand expressions must have the same type or the type must be specified");
		}
		
		this(firstOperand, secondOperand, firstOperand.type());
	}
	
	/**
	 * Constructs a new bitwise XOR function with the given operands and sql type.<br>
	 * @throws NullPointerException If the first operand, second operand or type is null
	 */
	public SqlBitwiseXorFunction {
		Objects.requireNonNull(firstOperand, "Sql first operand expression must not be null");
		Objects.requireNonNull(secondOperand, "Sql second operand expression must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
	}
}
