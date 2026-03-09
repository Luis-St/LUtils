package net.luis.utils.io.database.query.crud;

import net.luis.utils.io.database.SqlException;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.query.SqlQuery;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class SqlInsertQuery<T> implements SqlQuery<T> {
	
	public int execute() throws SqlException {
		return 0;
	}
	
	public @NonNull List<T> returning() throws SqlException {
		return null;
	}
	
	public @NonNull List<T> fetchInserted() throws SqlException {
		return null;
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) {
		return null;
	}
}
