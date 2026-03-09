package net.luis.utils.io.database.condition;

import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlOrderable {
	
	@NonNull SqlOrderable ascending();
	
	@NonNull SqlOrderable descending();
	
	@NonNull SqlOrderable nullsFirst();
	
	@NonNull SqlOrderable nullsLast();
}
