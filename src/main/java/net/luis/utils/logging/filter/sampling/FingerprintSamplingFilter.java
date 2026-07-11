package net.luis.utils.logging.filter.sampling;

import net.luis.utils.logging.filter.LogFilter;

/**
 *
 * @author Luis-St
 *
 */

// Idea: Create a filter that let 1 of n log messages pass through, where n is a configurable parameter (probability). Log messages are grouped by a fingerprint of the message(part), exception, and/or context, and each group has its own sampling rate.
public abstract class FingerprintSamplingFilter implements LogFilter {}
