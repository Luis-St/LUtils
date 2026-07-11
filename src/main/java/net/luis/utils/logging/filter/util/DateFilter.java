package net.luis.utils.logging.filter.util;

import net.luis.utils.logging.filter.LogFilter;

/**
 *
 * @author Luis-St
 *
 */

// Idea: Create a filter that let log messages pass through in a date range, where the start and end date are configurable parameters. Log messages outside the date range are filtered out.
public abstract class DateFilter implements LogFilter {}
