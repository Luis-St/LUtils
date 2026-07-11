package net.luis.utils.logging.filter.sampling;

import net.luis.utils.logging.filter.LogFilter;

/**
 *
 * @author Luis-St
 *
 */

// Idea: Create a filter that let 1 of n log messages pass through, where n is a configurable parameter (probability).
public abstract class SamplingFilter implements LogFilter {}
