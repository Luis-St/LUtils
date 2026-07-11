package net.luis.utils.logging.filter.volume;

import net.luis.utils.logging.filter.LogFilter;

/**
 *
 * @author Luis-St
 *
 */

// Idea: Create a filter that only accepts a certain number of log messages within a given time frame, and rejects the rest (config set using context)
public abstract class BurstKeyFilter  implements LogFilter {}
