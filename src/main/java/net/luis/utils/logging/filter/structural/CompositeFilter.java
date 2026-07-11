package net.luis.utils.logging.filter.structural;

import net.luis.utils.logging.filter.LogFilter;

/**
 *
 * @author Luis-St
 *
 */

// Idea: Create a filter that combines multiple filters with logical 'AND' or 'OR' operations. The composite filter will accept a log message if all of its child filters accept it (AND) or if at least one of its child filters accepts it (OR).
public abstract class CompositeFilter implements LogFilter {}
