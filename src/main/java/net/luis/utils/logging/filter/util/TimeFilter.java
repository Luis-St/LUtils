package net.luis.utils.logging.filter.util;

import net.luis.utils.logging.filter.LogFilter;

/**
 *
 * @author Luis-St
 *
 */

// Idea: Create a filter that let log messages pass through in a time range, where the start and end time are configurable parameters. Log messages outside the time range are filtered out.
public abstract class TimeFilter implements LogFilter {}
