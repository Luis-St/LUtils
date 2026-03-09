package net.luis.utils.io.database.function;

import net.luis.utils.io.databasev1.function.SqlExpression;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlStringExpression extends SqlOrderableExpression<String> {
	
	@NonNull SqlStringExpression lower();
	
	@NonNull SqlStringExpression upper();
	
	@NonNull SqlStringExpression trim();
	
	@NonNull SqlStringExpression leftTrim();
	
	@NonNull SqlStringExpression rightTrim();
	
	@NonNull SqlStringExpression trimChars(@NonNull String characters);
	
	@NonNull SqlNumericExpression<Integer> length();
	
	@NonNull SqlStringExpression substring(int start, int length);
	
	@NonNull SqlStringExpression concat(SqlExpression<?> @NonNull ... values);
	
	@NonNull SqlStringExpression concatWithSeparator(@NonNull String separator, SqlExpression<?> @NonNull ... values);
	
	@NonNull SqlStringExpression concatDistinctWithSeparator(@NonNull String separator, SqlExpression<?> @NonNull ... values);
	
	@NonNull SqlStringExpression concatOrderedWithSeparator(@NonNull String separator, SqlExpression<?> @NonNull ... values);
	
	@NonNull SqlStringExpression replace(@NonNull String search, @NonNull String replacement);
	
	@NonNull SqlNumericExpression<Integer> position(@NonNull String substring);
	
	@NonNull SqlStringExpression left(int n);
	
	@NonNull SqlStringExpression right(int n);
	
	@NonNull SqlStringExpression leftPad(int length, @NonNull String fill);
	
	@NonNull SqlStringExpression rightPad(int length, @NonNull String fill);
	
	@NonNull SqlStringExpression hex();
	
	@NonNull SqlStringExpression unhex();
}
