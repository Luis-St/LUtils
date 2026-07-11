package net.luis.utils.logging.filter.structural;

import net.luis.utils.logging.LogEvent;

/**
 *
 * @author Luis-St
 *
 */
 
// Idea: Create a filter that negates the result of another filter. If the other filter accepts a log message, this filter will reject it, and vice versa.
public abstract class NegatingFilter implements LogEvent {}
