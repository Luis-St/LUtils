package net.luis.utils.logging.filter.context;

import net.luis.utils.logging.filter.LogFilter;

/**
 *
 * @author Luis-St
 *
 */

// Idea: Create a filter that filters log messages based on the marker (any, none, specific (start with, end with, contains, regex, hierarchy))
public abstract class MarkerFilter implements LogFilter {}
