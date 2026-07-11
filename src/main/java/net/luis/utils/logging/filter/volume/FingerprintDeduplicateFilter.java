package net.luis.utils.logging.filter.volume;

import net.luis.utils.logging.filter.LogFilter;

/**
 *
 * @author Luis-St
 *
 */

// Idea: Create a filter that only accepts log messages that are not duplicates of previous messages based of on a fingerprint of the message(part), exception, and/or context within a certain time frame, and rejects the rest (allow x duplicates, then reject)
public abstract class FingerprintDeduplicateFilter implements LogFilter {}
