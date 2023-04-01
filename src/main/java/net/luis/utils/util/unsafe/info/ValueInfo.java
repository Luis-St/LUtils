package net.luis.utils.util.unsafe.info;

import net.luis.utils.util.unsafe.Nullability;

/**
 *
 * @author Luis-St
 *
 */

public record ValueInfo(Object value, String name, Nullability nullability) {
}
