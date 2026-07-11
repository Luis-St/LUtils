package net.luis.utils.logging.filter.level;

import net.luis.utils.logging.filter.LogFilter;

/**
 *
 * @author Luis-St
 *
 */

// Idea: Create a filter that filters log messages based on a dynamic threshold (e.g., only log messages with a level equal to or higher than a specified threshold that can change at runtime, threshold set via context)
public abstract class DynamicThresholdFilter implements LogFilter {}
